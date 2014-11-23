/*
	Sketch Elements: Chemistry molecular diagram drawing tool.
	
	(c) 2008-2009 Dr. Alex M. Clark
	
	Released as GNUware, under the Gnu Public License (GPL)
	
	See www.gnu.org for details.
*/

package net.sf.sketchel.ds;

import net.sf.sketchel.*;
import javax.swing.*;


/*
	Used to allow a manager-type class to be informed when a datasheet-viewing widget changes its state in a relevant way,
	such as when the title may need to be updated, or the datasheet has gone into or out of editing mode, or needs a customised
	response to a user interaction.
*/

public interface StateListener
{
	public void replaceTitle();
	public void dataModified();
	public void notifyEditStart(EditInfo info);
	public void notifyEditStop(EditInfo info);
	
	public void populateRightMouseCell(JPopupMenu menu,int col,int row);
	public void populateRightMouseColumn(JPopupMenu menu,int col);
	public void populateRightMouseRow(JPopupMenu menu,int row);
	
	public static class EditInfo
	{
		public DataUI dui=null;
	}
}
