/*
	Sketch Elements: Chemistry molecular diagram drawing tool.
	
	(c) 2008 Dr. Alex M. Clark
	
	Released as GNUware, under the Gnu Public License (GPL)
	
	See www.gnu.org for details.
*/

package net.sf.sketchel.ds;


import java.awt.*;
import javax.swing.*;

/*
	Implementation of data user interface for string cells.
*/

public class DataUIString extends DataUI
{
	public DataUIString(DataSheet ds,int colNum,FontMetrics fm,DataPopupMaster master)
	{
		super(ds,colNum,fm,master);
	}
	
	// !! put in serious values
	public int minimumHeight() {return 10;}
	public int maximumHeight() {return 20;}
	public int preferredHeight() {return 15;}
	public int minimumWidth() {return 10;}
	public int maximumWidth() {return 200;}
	public int preferredWidth() {return 100;}
	
	public void draw(Graphics2D g,int rowNum,int w,int h,Color backgr)
	{
		if (ds.isNull(rowNum,colNum)) return;
		String str=ds.getString(rowNum,colNum);
		g.drawString(str,2,(h+fontMetrics.getAscent())/2);
	}
	
	public int editType() {return EDIT_INPLACE;}

	public JComponent beginEdit(int rowNum,char ch) 
	{
		String str=ds.isNull(rowNum,colNum) ? "" : ds.getString(rowNum,colNum);
		if (ch==8 && str.length()>0) str=str.substring(0,str.length()-1);
		else if (ch>=32 && ch<127) str+=ch;
		return new JTextField(str);
	}
	public boolean saveEdit(int rowNum,JComponent ed) 
	{
		JTextField txt=(JTextField)ed;
		if (!ds.isNull(rowNum,colNum) && ds.getString(rowNum,colNum).equals(txt.getText())) return false;
		ds.setString(rowNum,colNum,txt.getText());
		return true;
	}
}
