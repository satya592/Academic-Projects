/*
	Sketch Elements: Chemistry molecular diagram drawing tool.
	
	(c) 2008 Dr. Alex M. Clark
	
	Released as GNUware, under the Gnu Public License (GPL)
	
	See www.gnu.org for details.
*/

package net.sf.sketchel.ds;

/*
	A data containing class for storing all of the information necessary to preserve the data and view state of a DataSheet
	in the context of its current SpreadSheet.
*/

public class CachedItem
{
	DataSheetHolder ds;

	// selection and layout info
	int curCol,curRow,selCol,selRow,selWidth,selHeight;
	int[] colWidth,rowHeight;
}
