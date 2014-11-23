/*
	Sketch Elements: Chemistry molecular diagram drawing tool.
	
	(c) 2008 Dr. Alex M. Clark
	
	Released as GNUware, under the Gnu Public License (GPL)
	
	See www.gnu.org for details.
*/

package net.sf.sketchel.ds;


import java.awt.*;

/*
	Implementation of data user interface for boolean cells.
*/

public class DataUIBoolean extends DataUI
{
	public DataUIBoolean(DataSheet ds,int colNum,FontMetrics fm,DataPopupMaster master)
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
		String str=ds.isNull(rowNum,colNum) ? "" : ds.getBoolean(rowNum,colNum) ? "true" : "false";
		g.drawString(str,2,(h+fontMetrics.getAscent())/2);
	}
	
	public int editType() {return EDIT_DIRECT;}

	public boolean directEdit(int rowNum) 
	{
		boolean val=ds.isNull(rowNum,colNum) ? true : !ds.getBoolean(rowNum,colNum);
		ds.setBoolean(rowNum,colNum,val);
		return true;
	}
}
