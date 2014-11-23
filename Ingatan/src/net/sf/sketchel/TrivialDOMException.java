/*
	Sketch Elements: Chemistry molecular diagram drawing tool.
	
	(c) 2009 Dr. Alex M. Clark
	
	Released as GNUware, under the Gnu Public License (GPL)
	
	See www.gnu.org for details.
*/

package net.sf.sketchel;

import java.io.IOException;

/*
	Exception used when a TrivialDOM class failed, usually due to some formatting or I/O issue.
*/

public class TrivialDOMException extends IOException
{
	public TrivialDOMException(String message) {super(message);}
	public TrivialDOMException(String message,int line) {super(message+"@ line "+line);}
	public TrivialDOMException(String message,Throwable cause) {super(message,cause);}
}
