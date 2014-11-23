/*
	Sketch Elements: Chemistry molecular diagram drawing tool.
	
	(c) 2008 Dr. Alex M. Clark
	
	Released as GNUware, under the Gnu Public License (GPL)
	
	See www.gnu.org for details.
*/

package net.sf.sketchel.ds;

import net.sf.sketchel.*;

import java.awt.*;
import java.util.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.table.*;

/*
	A dialog box which allows the columns of a table to be edited, and then calculates the effect of such edits.
*/

public class DialogEditColumns extends JDialog implements ActionListener
{
	private JButton bappend,bdelete,bmoveup,bmovedn,baccept,breject;
	private JTable table;
	private SchemaDataModel model;
	private DataSheet ds;

	private ArrayList<Integer> idx=new ArrayList<Integer>();
	private ArrayList<String> name=new ArrayList<String>();
	private ArrayList<Integer> type=new ArrayList<Integer>();
	private ArrayList<String> descr=new ArrayList<String>();
	
	private int[] resultOldPos=null,resultNewPos=null,resultType=null;
	private String[] resultName=null,resultDescr=null;
	
	private final String KEY_ESCAPE="*ESCAPE*";
	private final String KEY_ALTUP="*ALT-UP*";
	private final String KEY_ALTDOWN="*ALT-DOWN*";
	
	public DialogEditColumns(Frame Parent,DataSheet DS)
	{
		super(Parent,"Edit Column Layout",true);
		ds=DS;
		
		setLayout(new BorderLayout());
		
		for (int n=0;n<ds.numCols();n++)
		{
			idx.add(new Integer(n+1));
			name.add(ds.colName(n));
			type.add(new Integer(ds.colType(n)));
			descr.add(ds.colDescr(n));
		}
		model=new SchemaDataModel(idx,name,type,descr);
		table=new JTable(model);
			
		setColumnModel();
		
		JPanel buttons=new JPanel();
		buttons.setLayout(new FlowLayout(FlowLayout.RIGHT));
		buttons.add(bappend=Util.makeButton(this,"Append",KeyEvent.VK_P,"Add a new column"));
		buttons.add(bdelete=Util.makeButton(this,"Delete",KeyEvent.VK_D,"Remove selected column"));
		buttons.add(bmoveup=Util.makeButton(this,"Move Up",KeyEvent.VK_U,"Move selected column up"));
		buttons.add(bmovedn=Util.makeButton(this,"Move Down",KeyEvent.VK_W,"Move selected column down"));
		buttons.add(baccept=Util.makeButton(this,"Accept",KeyEvent.VK_A,"Close and apply changes"));
		buttons.add(breject=Util.makeButton(this,"Reject",0,"Cancel changes"));

		add(new JScrollPane(table),BorderLayout.CENTER);
		add(buttons,BorderLayout.SOUTH);
		
		getRootPane().setDefaultButton(baccept);
		
		ActionMap am=getRootPane().getActionMap();
		InputMap im=getRootPane().getInputMap(JRootPane.WHEN_IN_FOCUSED_WINDOW);

		im.put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE,0),KEY_ESCAPE);
		am.put(KEY_ESCAPE,new HotKeyAction(KEY_ESCAPE,this));
		
		im.put(KeyStroke.getKeyStroke(KeyEvent.VK_UP,KeyEvent.ALT_MASK),KEY_ALTUP);
		am.put(KEY_ALTUP,new HotKeyAction(KEY_ALTUP,this));
		
		im.put(KeyStroke.getKeyStroke(KeyEvent.VK_DOWN,KeyEvent.ALT_MASK),KEY_ALTDOWN);
		am.put(KEY_ALTDOWN,new HotKeyAction(KEY_ALTDOWN,this));
		
		pack();
	}

	private void setColumnModel()
	{
		JComboBox colTypes=new JComboBox();
		colTypes.addItem(DataSheet.typeName(DataSheet.COLTYPE_MOLECULE));
		colTypes.addItem(DataSheet.typeName(DataSheet.COLTYPE_STRING));
		colTypes.addItem(DataSheet.typeName(DataSheet.COLTYPE_REAL));
		colTypes.addItem(DataSheet.typeName(DataSheet.COLTYPE_INTEGER));
		colTypes.addItem(DataSheet.typeName(DataSheet.COLTYPE_BOOLEAN));
		
		// only add the "extend" type if there are already some present...
		for (int n=0;n<ds.numCols();n++) if (ds.colType(n)==DataSheet.COLTYPE_EXTEND)
		{
			colTypes.addItem(DataSheet.typeName(DataSheet.COLTYPE_EXTEND));
			break;
		}
		
		table.getColumnModel().getColumn(2).setCellEditor(new DefaultCellEditor(colTypes));
		table.setPreferredScrollableViewportSize(new Dimension(350,200));
		table.getColumnModel().getColumn(0).setMaxWidth(40);
		table.getColumnModel().getColumn(1).setMaxWidth(150);
		table.getColumnModel().getColumn(2).setMaxWidth(100);
	}

	private void actionAppend()
	{
		idx.add(0);
		name.add("");
		type.add(DataSheet.COLTYPE_STRING);
		descr.add("");
		model.fireTableStructureChanged();
		setColumnModel();
	}

	private void actionDelete()
	{
		int i=table.getSelectedRow();
		if (i<0) return;
		idx.remove(i);
		name.remove(i);
		type.remove(i);
		descr.remove(i);
		model.fireTableStructureChanged();
		setColumnModel();
	}
	
	private void actionMove(int Dir)
	{
		int i=table.getSelectedRow();
		if (i<0 || i+Dir<0 || i+Dir>=idx.size()) return;

		idx.add(i+Dir,idx.remove(i));
		name.add(i+Dir,name.remove(i));
		type.add(i+Dir,type.remove(i));
		descr.add(i+Dir,descr.remove(i));

		model.fireTableStructureChanged();
		setColumnModel();
		table.changeSelection(i+Dir,table.getSelectedColumn(),false,false);
	}

	private void actionAccept()
	{
		for (int n=0;n<idx.size();n++) if (name.get(n).length()==0)
		{
			JOptionPane.showMessageDialog(null,"All columns must be named.","Invalid",JOptionPane.ERROR_MESSAGE);
			return;
		}

		// count retained+new+deleted
		int nsz=ds.numCols();
		boolean[] retained=new boolean[ds.numCols()];
		for (int n=0;n<idx.size();n++) 
		{
			int i=idx.get(n).intValue();
			if (i==0) nsz++; else retained[i-1]=true;
		}
		
		// build up the indices: add the retained and new columns first, then the deleted columns
		int[] oldIdx=new int[nsz],newIdx=new int[nsz],newType=new int[nsz];
		String[] newName=new String[nsz],newDescr=new String[nsz];
		
		int pos=0;
		for (int n=0;n<idx.size();n++,pos++)
		{
			oldIdx[n]=idx.get(n).intValue()-1;
			newIdx[n]=pos;
			newName[n]=name.get(n);
			newType[n]=type.get(n).intValue();
			newDescr[n]=descr.get(n);
		}
		for (int n=0;n<retained.length;n++) if (!retained[n]) 
		{
			oldIdx[pos]=n;
			newIdx[pos]=-1;
			// (other fields left blank)
			pos++;
		}
		
		// make a string version of the to-do list
		String explan="";
		for (int n=0;n<oldIdx.length;n++)
		{
			if (oldIdx[n]<0 && newIdx[n]>=0)
			{
				explan+="New column: ["+newName[n]+"], type: "+DataSheet.typeName(newType[n])+"\n";
			}
			else if (oldIdx[n]>=0 && newIdx[n]<0)
			{
				explan+="Delete column: ["+ds.colName(oldIdx[n])+"]\n";
			}
			else if (oldIdx[n]>=0 && newIdx[n]>=0)
			{
				if (newName[n].compareTo(ds.colName(oldIdx[n]))!=0)
					explan+="Rename ["+ds.colName(oldIdx[n])+"] to ["+newName[n]+"]\n";
				if (newDescr[n].compareTo(ds.colDescr(oldIdx[n]))!=0)
					explan+="Change description for ["+newName[n]+"]\n";
				if (ds.colType(oldIdx[n])!=newType[n])
					explan+="Change type of ["+newName[n]+"] to: "+DataSheet.typeName(newType[n])+"\n";
				if (newIdx[n]!=oldIdx[n])
					explan+="Reorder ["+newName[n]+"] from #"+(oldIdx[n]+1)+" to #"+(newIdx[n]+1)+"\n";
			}
		}
		if (explan.length()==0) {setVisible(false); return;} // nothing to do, equivalent to reject
		
		String opt[]={"Yes","No"};
		if (JOptionPane.showOptionDialog(null,explan,"Apply Changes?",
					JOptionPane.YES_NO_OPTION,JOptionPane.YES_NO_OPTION,null,opt,opt[0])!=JOptionPane.YES_OPTION) return;

		resultOldPos=oldIdx;
		resultNewPos=newIdx;
		resultName=newName;
		resultType=newType;
		resultDescr=newDescr;
		setVisible(false);
	}
	
	private void actionReject()
	{
		setVisible(false);
	}

	// returns true if the datasheet has changed
	public boolean execute()
	{
		setVisible(true);
		return resultOldPos!=null;
	}

	public int[] resultOldPos() {return resultOldPos;}
	public int[] resultNewPos() {return resultNewPos;}
	public String[] resultName() {return resultName;}
	public int[] resultType() {return resultType;}
	public String[] resultDescr() {return resultDescr;}

	public void actionPerformed(ActionEvent e)
	{
		Object src=e.getSource();
		String cmd=e.getActionCommand();
	
		if (src==bappend) actionAppend();
		else if (src==bdelete) actionDelete();
		else if (src==bmoveup || cmd.equals(KEY_ALTUP)) actionMove(-1);
		else if (src==bmovedn || cmd.equals(KEY_ALTDOWN)) actionMove(1);
		else if (src==baccept) actionAccept();
		else if (src==breject || cmd.equals(KEY_ESCAPE)) actionReject();
	}
	
	// internal class for the underlying data
	
	class SchemaDataModel extends AbstractTableModel
	{
		ArrayList<Integer> idx;
		ArrayList<String> name;
		ArrayList<Integer> type;
		ArrayList<String> descr;
		
		public SchemaDataModel(ArrayList<Integer> Idx,ArrayList<String> Name,ArrayList<Integer> Type,ArrayList<String> Descr)
		{
			idx=Idx;
			name=Name;
			type=Type;
			descr=Descr;
		}
		
		public String getColumnName(int col) 
		{
			final String[] COLNAMES={"#","Name","Type","Description"};
			return COLNAMES[col];
		}
		public int getRowCount() {return idx.size();}
		public int getColumnCount() {return 4;}
		public Object getValueAt(int row,int col) 
		{
			if (col==0)
			{
				int i=idx.get(row).intValue();
				return i==0 ? "" : ""+i;
			}
			else if (col==1) return name.get(row);
			else if (col==2) return DataSheet.typeName(type.get(row).intValue());
			else if (col==3) return descr.get(row);
			return null;
		}
		public boolean isCellEditable(int row,int col) {return col>0;}
		public void setValueAt(Object value,int row,int col)
		{
			if (col==1) name.set(row,(String)value);
			else if (col==2)
			{
				String t=(String)value;
				if (t.compareTo(DataSheet.typeName(DataSheet.COLTYPE_MOLECULE))==0) type.set(row,DataSheet.COLTYPE_MOLECULE);
				else if (t.compareTo(DataSheet.typeName(DataSheet.COLTYPE_STRING))==0) type.set(row,DataSheet.COLTYPE_STRING);
				else if (t.compareTo(DataSheet.typeName(DataSheet.COLTYPE_REAL))==0) type.set(row,DataSheet.COLTYPE_REAL);
				else if (t.compareTo(DataSheet.typeName(DataSheet.COLTYPE_INTEGER))==0) type.set(row,DataSheet.COLTYPE_INTEGER);
				else if (t.compareTo(DataSheet.typeName(DataSheet.COLTYPE_BOOLEAN))==0) type.set(row,DataSheet.COLTYPE_BOOLEAN);
				else if (t.compareTo(DataSheet.typeName(DataSheet.COLTYPE_EXTEND))==0) type.set(row,DataSheet.COLTYPE_EXTEND);
			}
			else if (col==3) descr.set(row,(String)value);
			fireTableCellUpdated(row,col);
		}
	}
	
}
