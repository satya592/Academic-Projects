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
	Abstract bases class for the "data user-interface", which is a medium-weight class that is regularly instantiated to
	provide information and services pertaining to a particular column within a datasheet, and in some cases a specific cell.
	Duties include providinglayout information, rendering and editing.
*/

public abstract class DataUI
{
	protected DataSheet ds;
	protected int colNum;
	protected FontMetrics fontMetrics;
	protected DataPopupMaster master;

	public static final int EDIT_NONE=0; // no editing facilities are provided
	public static final int EDIT_INPLACE=1; // editing is done in-place, with a specially constructed component
	public static final int EDIT_POPUP=2; // editing is done with a popup frame which is managed by the parent sheet
	public static final int EDIT_DIRECT=3; // editing is done directly, without a component or a frame

	// constructor: holds general purpose data
	public DataUI(DataSheet ds,int colNum,FontMetrics fm,DataPopupMaster master)
	{
		this.ds=ds;
		this.colNum=colNum;
		this.fontMetrics=fm;
		this.master=master;
	}

	public DataSheet getDataSheet() {return ds;}
	public int getColNum() {return colNum;}
	
	// provide information about how big the cells should be; may or may not depend on the underlying data
	public abstract int minimumHeight();
	public abstract int maximumHeight();
	public abstract int preferredHeight();
	public abstract int minimumWidth();
	public abstract int maximumWidth();
	public abstract int preferredWidth();
	
	// if overridden to return >0, then on editing, the dimensions will be enlarged, if necessary, then snapped back when
	// editing is finished
	public int expansionWidth() {return 0;}
	public int expansionHeight() {return 0;}
	
	// if overridden to return true, then the enter or escape keys can be handled internally, i.e. they will not automatically
	// be translated into accept/reject; claimKeyboard() refers to any un-modified keystrokes
	public boolean claimEnter() {return false;}
	public boolean claimEscape() {return false;}
	public boolean claimKeyboard() {return false;}
		
	// render the cell onto the given graphics context, with the indicated colour; the graphics offset and clip will be set
	// to the cell's playground, so only width and height are given
	// Note: the parent should set the foreground color and the font, which may be used as-is
	public abstract void draw(Graphics2D g,int rowNum,int w,int h,Color backgr);
	
	// return true if editing is done directly within the cell; false if it needs to pop something up
	public abstract int editType();
	
	// methods called for in-place editing: beginEdit(..) responds to a possible keyboard invocation, and should return a 
	// component which will then be made to fill the cell, for its lifetime; if it is cancelled gracefully, then saveEdit(..)
	// will be called, providing the opportunity to read out its data and update the underlying datasheet; it should return
	// true if a change has been applied to the underlying datasheet; when the component is no longer needed, endEdit(..) is
	// called on it (this being a substitute for there being no decent destructor mechanism for components)
	public JComponent beginEdit(int rowNum,char ch) {return null;}
	public boolean saveEdit(int rowNum,JComponent ed) {return false;}
	public void endEdit(JComponent ed) {}
	
	// methods called for editing via a popup window: beginPopup(..) should return a window frame which contains the editing
	// functionality; the 'master' parameter allows this frame to inform the parent of changes, such as the user's request to
	// save the data content, or that the popup window has been closed; endPopup(..) is called as a finalisation, before the
	// popup goes to the abbatoir, and may ask it to save its data; savePopup(..) makes sure any changes have been saved; both
	// of these latter methods must ensure that the datasheet is uptodate, and return true iff anything changed
	public JFrame beginPopup(int rowNum) {return null;}
	public boolean savePopup(int rowNum,JFrame frame) {return false;}
	
	// !! keyTyped(...)
	// !! mouseEvent(...)
	
	// this is called for direct edit-types; the only information provided is that the user basically "did something" with the
	// cell; return true if this caused the underlying data to change, false if not
	public boolean directEdit(int rowNum) {return false;}
}
