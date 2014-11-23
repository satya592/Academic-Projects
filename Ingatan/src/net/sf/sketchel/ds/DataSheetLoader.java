/*
	Sketch Elements: Chemistry molecular diagram drawing tool.
	
	(c) 2009 Dr. Alex M. Clark
	
	Released as GNUware, under the Gnu Public License (GPL)
	
	See www.gnu.org for details.
*/

package net.sf.sketchel.ds;

import net.sf.sketchel.*;

import java.io.*;

/*
	A special subclass of the DataSheet "holder", which requires an incoming stream of XML, in the SketchEl DataSheet format.
	The header information will be parsed by the constructor, but after that no further bytes will be read or parsed until the
	requests for row content come in. Once the input has been parsed to the end of file, this object behaves exactly like
	its superclass, and can be read/written as per usual.
*/

public class DataSheetLoader extends DataSheetHolder
{
	private TrivialDOMReader source; // null=finished reading
	private TrivialDOM.Node content=null;
	private int numRows=0,contentNext=0;

	// constructor: uses the TrivialDOMReader ("XML pull") to retrieve the header, and prepare the way for reading rows
	public DataSheetLoader(BufferedReader rdr) throws IOException
	{
		source=new TrivialDOMReader(rdr);
		
		int numRoot=0;
		boolean headOK=false;
		TrivialDOM.Node summary=null,header=null,extension=null;
		
		while (!source.isFinished() && content==null)
		{
			source.readBlock();
			if (source.document()==null) continue;
			else if (!headOK && !source.document().nodeName().equals("DataSheet"))
				throw new DataSheetIOException("Source does not start with <DataSheet> tag.");
			headOK=true;
			
			while (numRoot<source.document().numChildren())
			{
				if (source.document().childType(numRoot)==TrivialDOM.TYPE_NODE)
				{
					TrivialDOM.Node node=source.document().getChildNode(numRoot);
					if (node.nodeName().equals("Summary")) summary=node;
					else if (node.nodeName().equals("Header")) header=node;
					else if (node.nodeName().equals("Extension")) extension=node;
					else if (node.nodeName().equals("Content")) {content=node; break;}
					else throw new DataSheetIOException("Unexpected primary element: <"+node.nodeName()+">.");
				}
				numRoot++;
			}
		}
		
		if (header==null) throw new DataSheetIOException("DataSheet is missing the <Header> tag.");
		if (content==null) throw new DataSheetIOException("DataSheet is missing the <Content> tag.");

		// delegate parsing of the opening blocks
		DataSheetStream.parseXMLSummary(this,summary);
		DataSheetStream.parseXMLExtension(this,extension);
		DataSheetStream.parseXMLHeader(this,header);
		
		numRows=Util.safeInt(header.attribute("nrows"),0);
	}
	
	// ensure that as many as "maxRow" rows are read from the underlying source; note that any attempts to access data from rows
	// which have not been parsed within this method will result in failure; if this function does not throw an exception, then
	// it is safe to access any row in the range [0 .. maxRow-1]
	public void readUntil(int maxRow) throws IOException
	{
		if (rows.size()>=maxRow) return;
		if (maxRow>numRows) throw new DataSheetIOException("Attempting to read beyond end of file.");
		
		int nextRow=rows.size();
		while (rows.size()<maxRow)
		{
			if (!source.isFinished()) source.readBlock();
			while (contentNext<content.numChildren() && nextRow<maxRow && (rows.size()<nextRow || source.isFinished()))
			{
				if (content.childType(contentNext)==TrivialDOM.TYPE_NODE)
				{
					TrivialDOM.Node row=content.getChildNode(contentNext);
					if (!row.nodeName().equals("Row")) continue;
					
					int rid=Util.safeInt(row.attribute("id"),0);
					if (rid<1 || rid>numRows) throw new DataSheetIOException("Row@id out of range.");
					if (rid-1!=nextRow) throw new DataSheetIOException("Row@id out of order.");
					while (rows.size()<rid) appendRow();
		
					DataSheetStream.parseXMLRow(this,row,rid-1);
					nextRow++;
				}
				contentNext++;
			}
			if (source.isFinished()) {close(); break;}
		}
		
		if (rows.size()<maxRow) throw new DataSheetIOException("Unexpected end of file.");
	}
	
	// stop reading and mark as finished, whether it really is or not; now the datasheet is safe for arbitrary write operations
	public void close() 
	{
		if (source!=null) source.close(); 
		source=null;
	}
	
	public boolean canRead() {return true;}
	public boolean canWrite() {return source==null;}
	public boolean canRandomRead() {return source==null;}
	public boolean canRandomWrite() {return source==null;}
	public boolean knowRowCount() {return true;}
	public boolean knowColTypes() {return true;}
	public boolean hasNativeMolecules() {return true;}
	public int watermark() {return rows.size();}
	
	public int numRows() {return numRows;} // (not the same as # of rows read thus far)
}
