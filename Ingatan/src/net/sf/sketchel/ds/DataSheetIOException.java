/*
	Sketch Elements: Chemistry molecular diagram drawing tool.
	
	(c) 2009 Dr. Alex M. Clark
	
	Released as GNUware, under the Gnu Public License (GPL)
	
	See www.gnu.org for details.
*/

package net.sf.sketchel.ds;

import java.io.IOException;

/*
	Exception used when datasheet reading or writing goes wrong, in such a way that the format is at fault, rather than the
	underlying IO transport mechanism. Extends IOException so that lazy calling code does not need to distinguish.
*/

public class DataSheetIOException extends IOException
{
	public DataSheetIOException(String message) {super(message);}
	public DataSheetIOException(String message,Throwable cause) {super(message,cause);}
}