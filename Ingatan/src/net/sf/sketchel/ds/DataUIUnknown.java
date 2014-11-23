/*
	Sketch Elements: Chemistry molecular diagram drawing tool.
	
	(c) 2008 Dr. Alex M. Clark
	
	Released as GNUware, under the Gnu Public License (GPL)
	
	See www.gnu.org for details.
*/

package net.sf.sketchel.ds;


import java.awt.*;

/*
	A stub UI handler for data with a type which cannot be viewed or edited with default tools.
*/

public class DataUIUnknown extends DataUI
{
	public DataUIUnknown(DataSheet ds,int colNum,FontMetrics fm,DataPopupMaster master)
	{
		super(ds,colNum,fm,master);
	}
	
	// !! put in serious values
	public int minimumHeight() {return 10;}
	public int maximumHeight() {return 20;}
	public int preferredHeight() {return 15;}
	public int minimumWidth() {return 5;}
	public int maximumWidth() {return 100;}
	public int preferredWidth() {return 70;}
	
	public void draw(Graphics2D g,int rowNum,int w,int h,Color backgr)
	{
		String str="(unknown)";
		g.drawString(str,2,(h+fontMetrics.getAscent())/2);
	}
	
	public int editType() {return EDIT_NONE;}
}
