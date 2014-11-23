/*
	Sketch Elements: Chemistry molecular diagram drawing tool.
	
	(c) 2009 Dr. Alex M. Clark
	
	Released as GNUware, under the Gnu Public License (GPL)
	
	See www.gnu.org for details.
*/

package net.sf.sketchel.ds;

import net.sf.sketchel.*;

import java.util.*;

/*
	Single-function class for the purpose of interpreting incoming data, typically from a text stream, e.g. the clipboard, and
	figuring out how to apply that to a current datasheet, which may be populated with data, and may have selected cells, which
	influences various effects such as column matching and unit extension. The available results consist of a new datasheet, and
	a list of the rows and columns which were affected in some way by the import, be that adding, moving or overwriting.
*/

public class ImportTable
{
	private DataSheetHolder ds;
	private boolean strictColumn=false;
	private int curRow=-1,curCol=-1;
	private int[] rowSel=new int[0],colSel=new int[0];
	private ArrayList<Integer> affRows=new ArrayList<Integer>(),affCols=new ArrayList<Integer>();

	// provides the original datasheet; note that the input datasheet will not be modified
	public ImportTable(DataSheetHolder ds)
	{
		this.ds=ds.clone();
		this.strictColumn=strictColumn;
	}
	
	// specifies the matrix of rows and columns which form the current selection; note that the row and column selection has the
	// meaning of the _whole_thing_, e.g. if the user is pasting a row into a selected row, it is desirable to replace that row
	// with the data; if the user merely has the cursor on the row, it is likely that it should in fact be an insert operation,
	// so the row should not be included in the selection
	public void setSelection(int curRow,int curCol,int[] rowSel,int[] colSel)
	{
		this.curRow=curRow;
		this.curCol=curCol;
		if (rowSel!=null) this.rowSel=rowSel;
		if (colSel!=null) this.colSel=colSel;
	}
	
	// when off, column-to-column mappings are just a hint; when on, it's more of a demand
	public void setStrictColumn(boolean strict) {strictColumn=strict;}
	
	public DataSheetHolder getResult() {return ds;}
	public int[] rowsAffected() 
		{int[] idx=new int[affRows.size()]; for (int n=0;n<affRows.size();n++) idx[n]=affRows.get(n); return idx;}
	public int[] colsAffected()
		{int[] idx=new int[affCols.size()]; for (int n=0;n<affCols.size();n++) idx[n]=affCols.get(n); return idx;}

/* !! original free-form version
	// performs the import, given that nothing is known about the incoming data except that it is text; attempts to convert
	// the data into a temporary datasheet, maybe using the current datasheet + selection as a hint, then pass that datasheet
	// off to the overloaded version of this function
	public void importData(String rawdata) throws IOException
	{
		if (rawdata==null || rawdata.length()==0) throw new IOException("Attempting to import blank data.");
	
		DataSheetHolder table=null;
		Molecule frag=null;
		
		if (DataSheetStream.examineIsXMLDS(new BufferedReader(new StringReader(rawdata))))
			table=DataSheetStream.readXML(new BufferedReader(new StringReader(rawdata)));
		else if (DataSheetStream.examineIsMDLSDF(new BufferedReader(new StringReader(rawdata))))
			table=DataSheetStream.readSDF(new BufferedReader(new StringReader(rawdata)));
		else if ((frag=tryToReadMolecule(rawdata))!=null)
		{	// it's a single molecule
			table=new DataSheetHolder();
			String colName=curCol<0 ? "molecule" : ds.colName(curCol);
			table.appendColumn(colName,DataSheet.COLTYPE_MOLECULE,"");
			table.appendRow();
			table.setMolecule(0,0,frag);
		}
		else if ((rowSel.length<=1 && colSel.length<=1 && rawdata.indexOf("\n")<0) ||
				 (rawdata.indexOf("\n")<0 && rawdata.indexOf("\t")<0))
		{	// single line of data being pasted into a single cell... 
			table=new DataSheetHolder();
			String colName=curCol<0 ? "import" : ds.colName(curCol);
			table.appendColumn(colName,DataSheet.COLTYPE_STRING,"");
			table.appendRow();
			table.setString(0,0,rawdata);
			
			if (curCol<0 || ds.colType(curCol)!=DataSheet.COLTYPE_STRING) table.upcastStringColumns();
		}
		else table=parseDelimitedText(rawdata);
				
		if (table==null) throw new IOException("Unrecognised incoming data format.");
		
		importData(table);
	}
*/

	// performs the import, assuming that the content is some free piece of text that for whatever reason could not be
	// parsed into a table or a molecule
	public void importData(String text) throws DataSheetIOException
	{
		if (curCol<0 || curCol>=ds.numCols() || curRow<0 || curRow>=ds.numRows()) return;
		try
		{
			if (ds.colType(curCol)==DataSheet.COLTYPE_STRING) ds.setString(curRow,curCol,text);
			else if (ds.colType(curCol)==DataSheet.COLTYPE_INTEGER) ds.setInteger(curRow,curCol,new Integer(text).intValue());
			else if (ds.colType(curCol)==DataSheet.COLTYPE_REAL) ds.setReal(curRow,curCol,new Double(text).doubleValue());
			else if (ds.colType(curCol)==DataSheet.COLTYPE_BOOLEAN) 
			{
				String bstr=text.trim().toLowerCase();
				if (bstr.equals("false")) ds.setBoolean(curRow,curCol,false);
				else if (bstr.equals("true")) ds.setBoolean(curRow,curCol,true);
			}
		}
		catch (NumberFormatException ex) {} // silent failure
	}
	
	// performs the import of a single molecule field
	public void importData(Molecule mol) throws DataSheetIOException
	{
		int c=-1;
		
		if (ds.colType(curCol)==DataSheet.COLTYPE_MOLECULE) c=curCol;
		for (int n=0;c<0 && n<colSel.length;n++) if (ds.colType(colSel[n])==DataSheet.COLTYPE_MOLECULE) {c=n; break;}
		if (c<0)
		{
			c=ds.findColByName("Molecule");
			if (c>=0 && ds.colType(c)!=DataSheet.COLTYPE_MOLECULE) c=-1;
		}
		if (c<0) c=ds.appendColumn("Molecule",DataSheet.COLTYPE_MOLECULE,"Molecular structure");
		
		int r=curRow;
		if (r<0 || r>ds.numRows()) r=ds.appendRow();
		ds.setMolecule(r,c,mol);
	}
	
	// performs the import, given that whatever incoming data is now in the form of a temporary datasheet
	public void importData(DataSheet table) throws DataSheetIOException
	{
		// first task: for each incoming column, make sure it matches a column in the datasheet, by whatever means necessary
		int[] matchCol=new int[table.numCols()];
		for (int n=0;n<table.numCols();n++) matchCol[n]=-1;
		
		// if column matching is strict, then line them up from the starting point
		if (strictColumn) for (int n=0;n<table.numCols() && n+curCol<ds.numCols();n++) matchCol[n]=n+curCol;
		
		// if incoming data is a unit, then try to hook it up with the current column, regardless of nomenclature, since the
		// column name is probably fabricated anyway
		if (curCol>=0 && matchCol[0]<0 && table.numCols()==1 && table.numRows()==1 && 
			compatibleColumns(table.colType(0),ds.colType(curCol))) 
		{
			matchCol[0]=curCol;
		}
			
		// any incoming column can be matched to an existing column if same name and compatible type
		if (!strictColumn) for (int n=0;n<matchCol.length;n++) if (matchCol[n]<0)
		{
			for (int i=0;i<ds.numCols();i++) 
				if (ds.colName(i).equals(table.colName(n)) && compatibleColumns(table.colType(n),ds.colType(i)))
					{matchCol[n]=i; break;}
		}
		
		// columns which are still unassigned get new columns made for them, with the name reassigned if necessary
		for (int n=0;n<matchCol.length;n++) if (matchCol[n]<0)
		{
			String colName=table.colName(n);
			boolean taken=false;
			for (int i=0;i<ds.numCols();i++) if (ds.colName(i).equals(colName)) {taken=true; break;}
			if (taken) colName=renameColumn(colName);
			matchCol[n]=ds.appendColumn(colName,table.colType(n),table.colDescr(n));
		}
		
		for (int n=0;n<matchCol.length;n++) affCols.add(matchCol[n]);
		
		// pasting of the row data: there are three possibilities...
		// (1) the incoming rows match the selection size, so overwrite 1:1
		// (2) there is one incoming row and multiple selected rows, so apply unit extension
		// (3) the selection is mismatched or nonexistent, so insert entries before current
		// (4) there isn't a current, so append them...
	
		if (table.numRows()==rowSel.length)
		{
			for (int n=0;n<table.numRows();n++)
			{
				transferRow(rowSel[n],table,n,matchCol);
				affRows.add(rowSel[n]);
			}
		}
		else if (table.numRows()==1 && rowSel.length>1)
		{
			for (int n=0;n<rowSel.length;n++)
			{
				transferRow(rowSel[n],table,0,matchCol);
				affRows.add(rowSel[n]);
			}
		}
		else if (curRow>=0)
		{
			for (int n=table.numRows()-1;n>=0;n--)
			{
				ds.insertRow(curRow);
				transferRow(curRow,table,n,matchCol);
			}
			for (int n=curRow;n<ds.numRows();n++) affRows.add(n);
		}
		else
		{
			for (int n=0;n<table.numRows();n++)
			{
				ds.appendRow();
				transferRow(ds.numRows()-1,table,n,matchCol);
				affRows.add(ds.numRows()-1);
			}
		}
	}
	
	// copies rows from a transient datasheet into the destination, from the given indices; the column index is a mapping for
	// each of the columns in the source
	private void transferRow(int destRow,DataSheet src,int srcRow,int[] colIdx)
	{
		for (int n=0;n<colIdx.length;n++)
		{
			int st=src.colType(n),dt=ds.colType(colIdx[n]);
			if (src.isNull(srcRow,n)) ds.setToNull(destRow,colIdx[n]);
			else if (st==DataSheet.COLTYPE_MOLECULE && dt==DataSheet.COLTYPE_MOLECULE) 
				ds.setMolecule(destRow,colIdx[n],src.getMolecule(srcRow,n));
			else if (st==DataSheet.COLTYPE_STRING && dt==DataSheet.COLTYPE_STRING)
				ds.setString(destRow,colIdx[n],src.getString(srcRow,n));
			else if (st==DataSheet.COLTYPE_INTEGER && dt==DataSheet.COLTYPE_STRING)
				ds.setString(destRow,colIdx[n],String.valueOf(src.getInteger(srcRow,n)));
			else if (st==DataSheet.COLTYPE_INTEGER && dt==DataSheet.COLTYPE_INTEGER)
				ds.setInteger(destRow,colIdx[n],src.getInteger(srcRow,n));
			else if (st==DataSheet.COLTYPE_INTEGER && dt==DataSheet.COLTYPE_REAL)
				ds.setReal(destRow,colIdx[n],src.getInteger(srcRow,n));
			else if (st==DataSheet.COLTYPE_REAL && dt==DataSheet.COLTYPE_STRING)
				ds.setString(destRow,colIdx[n],String.valueOf(src.getReal(srcRow,n)));
			else if (st==DataSheet.COLTYPE_REAL && dt==DataSheet.COLTYPE_REAL)
				ds.setReal(destRow,colIdx[n],src.getReal(srcRow,n));
			else if (st==DataSheet.COLTYPE_BOOLEAN && dt==DataSheet.COLTYPE_STRING)
				ds.setString(destRow,colIdx[n],String.valueOf(src.getBoolean(srcRow,n)));
			else if (st==DataSheet.COLTYPE_BOOLEAN && dt==DataSheet.COLTYPE_BOOLEAN)
				ds.setBoolean(destRow,colIdx[n],src.getBoolean(srcRow,n));
		}
	}
	
	// given that a column name clashes with one in the datasheet, find a new name for it which does not
	private String renameColumn(String colName)
	{
		while (colName.length()>0)
		{
			char ch=colName.charAt(colName.length()-1);
			if (ch>='0' && ch<='9') colName=colName.substring(0,colName.length()-1); else break;
		}
		for (int n=1;;n++)
		{
			String newName=colName+n;
			for (int i=0;i<ds.numCols();i++) if (ds.colName(i).equals(newName)) {newName=null; break;}
			if (newName!=null) return newName;
		}
	}
	
	// returns true if it is appropriate to paste one type of column into another; note that this is not symmetric
	public static boolean compatibleColumns(int newCol,int oldCol)
	{
		if (newCol==DataSheet.COLTYPE_MOLECULE) return oldCol==DataSheet.COLTYPE_MOLECULE;
		if (newCol==DataSheet.COLTYPE_STRING) return oldCol==DataSheet.COLTYPE_STRING;
		if (newCol==DataSheet.COLTYPE_INTEGER) return oldCol==DataSheet.COLTYPE_STRING || oldCol==DataSheet.COLTYPE_INTEGER ||
													  oldCol==DataSheet.COLTYPE_REAL;
		if (newCol==DataSheet.COLTYPE_REAL) return oldCol==DataSheet.COLTYPE_STRING || oldCol==DataSheet.COLTYPE_REAL;
		if (newCol==DataSheet.COLTYPE_BOOLEAN) return oldCol==DataSheet.COLTYPE_STRING || oldCol==DataSheet.COLTYPE_BOOLEAN;
		
		return false;
	}
	
	/* !! no longer used
	// slightly more polite reader of multi-molecule formats, which returns null instead of throwing an exception
	private Molecule tryToReadMolecule(String text)
	{
		try {return MoleculeReader.readUnknown(new BufferedReader(new StringReader(text)));}
		catch (IOException ex) {}
		return null;
	}*/
	
	// for a block of text, examines the possibility that it might be comma-delimited or tab-delimited text, where double quotes
	// may optionally encode strings
	private DataSheetHolder parseDelimitedText(String text)
	{
		String[] lines=text.split("\n");
		int numTabs=-1,numCommas=-1; // -1=unassigned thus far, -2=mismatching, cannot use
		for (int n=0;n<lines.length;n++)
		{
			if (lines[n].length()==0) continue;
			boolean inquotes=false;
			int tc=0,cc=0;
			for (int i=0;i<lines[n].length();i++)
			{
				if (lines[n].charAt(i)=='\t') tc++;
				else if (lines[n].charAt(i)=='"') inquotes=!inquotes;
				else if (lines[n].charAt(i)==',') cc+=(inquotes ? 0 : 1);
			}
			if (numTabs==-1) numTabs=tc; else if (numTabs!=tc) numTabs=-2;
			if (numCommas==-1) numCommas=cc; else if (numCommas!=cc) numCommas=-2;
			
			if (numTabs==-2 && numCommas==-2) break;
		}
		if (numTabs<0 && numCommas<0) return null; // both inconsistent
		if (numCommas>0 && numTabs<0) return null; // mixing commas with mismatched number of tabs is unusable
		// if it is consistently comma delimited, but the tab action is absent or inconsistent, then convert each of the
		// commas to tabs, if they are not within quotes
		if (numTabs<=0 && numCommas>0)
		{
			for (int n=0;n<lines.length;n++)
			{
				StringBuffer buff=new StringBuffer(lines[n]);
				boolean inquotes=false;
				for (int i=0;i<buff.length();i++) 
				{
					if (buff.charAt(i)=='"') inquotes=!inquotes;
					else if (buff.charAt(i)==',' && !inquotes) buff.setCharAt(i,'\t');
				}
				lines[n]=buff.toString();
			}
			numTabs=numCommas;
		}
		
		// now create a datasheet which has one string per tab
		int numCols=numTabs+1;
		DataSheetHolder table=new DataSheetHolder();
		for (int n=0;n<numCols;n++) table.appendColumn("text"+(n+1),DataSheet.COLTYPE_STRING,"Imported Column");
		for (int n=0;n<lines.length;n++)
		{
			if (lines[n].length()==0) continue;
			int r=table.appendRow();
			String[] cells=lines[n].split("\t");
			for (int c=0;c<numCols;c++) if (cells[c].length()>0) table.setString(r,c,cells[c]);
		}
		
		table.upcastStringColumns();
		return table;
	}
	
}