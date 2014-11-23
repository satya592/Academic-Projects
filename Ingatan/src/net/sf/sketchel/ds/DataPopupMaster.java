/*
	Sketch Elements: Chemistry molecular diagram drawing tool.
	
	(c) 2008 Dr. Alex M. Clark
	
	Released as GNUware, under the Gnu Public License (GPL)
	
	See www.gnu.org for details.
*/

package net.sf.sketchel.ds;

/*
	Must be implemented by a class which is the parent of a data editing popup window. This allows the popup window to inform
	its invoker when certain relevant actions are about to take place.
*/

public interface DataPopupMaster
{
	// this notification is sent just before the edited content is written to the datasheet
	void popupNotifySave(DataUI dui,int rowNum);
	
	// this notification is sent as the popup window is shutting down
	void popupNotifyClosed(DataUI dui,int rowNum);
	
	// for a DataUI implementation to ask its master to close it down; if apply is true, data will be saved
	void requestClose(DataUI dui,boolean apply);
}
