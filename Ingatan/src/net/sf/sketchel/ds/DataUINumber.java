/*
	Sketch Elements: Chemistry molecular diagram drawing tool.
	
	(c) 2008 Dr. Alex M. Clark
	
	Released as GNUware, under the Gnu Public License (GPL)
	
	See www.gnu.org for details.
*/

package net.sf.sketchel.ds;

import net.sf.sketchel.*;

import java.awt.*;
import javax.swing.*;

/*
	Implementation of data user interface for numeric cells.
*/

public class DataUINumber extends DataUI
{
	boolean isReal;

	public DataUINumber(DataSheet ds,int colNum,FontMetrics fm,DataPopupMaster master)
	{
		super(ds,colNum,fm,master);
		isReal=ds.colType(colNum)==DataSheet.COLTYPE_REAL;
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
		String str=dataAsString(rowNum);
		g.drawString(str,2,(h+fontMetrics.getAscent())/2);
	}
	
	public int editType() {return EDIT_INPLACE;}
	
	public JComponent beginEdit(int rowNum,char ch) 
	{
		String str=dataAsString(rowNum);
		if (ch==8 && str.length()>0) str=str.substring(0,str.length()-1);
		else if (ch>=32 && ch<127 && validateString(str+ch)) str+=ch;
		return new JTextField(str);
	}
	public boolean saveEdit(int rowNum,JComponent ed) 
	{
		String str=((JTextField)ed).getText();
		if (str.length()==0) 
		{
			if (!ds.isNull(rowNum,colNum)) {ds.setToNull(rowNum,colNum); return true;}
			return false;
		}
		
		double val=0;
		try
		{
			val=Double.valueOf(str);
		}
		catch (NumberFormatException ex) {return false;}
		
		if (isReal) 
		{
			if (ds.isNull(rowNum,colNum) || ds.getReal(rowNum,colNum)!=val) {ds.setReal(rowNum,colNum,val); return true;}
		}
		else 
		{
			int ival=Util.iround(val);
			if (ds.isNull(rowNum,colNum) || ds.getInteger(rowNum,colNum)!=ival) {ds.setInteger(rowNum,colNum,ival); return true;}
		}
		
		return false;
	}
	
	// internal methods
	
	private String dataAsString(int rowNum)
	{
		if (ds.isNull(rowNum,colNum)) return "";
		if (isReal) return new Double(ds.getReal(rowNum,colNum)).toString();
		else return new Integer(ds.getInteger(rowNum,colNum)).toString();
	}
	
	private boolean validateString(String str)
	{
		// !!
		return true;
	}
}
