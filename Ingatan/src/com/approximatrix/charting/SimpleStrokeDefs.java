/*
    OpenChart2 Java Charting Library and Toolkit
    Copyright (C) 2005-2009 Approximatrix, LLC
    Copyright (C) 2001  Sebastian M�ller
    http://www.approximatrix.com

    This library is free software; you can redistribute it and/or
    modify it under the terms of the GNU Lesser General Public
    License as published by the Free Software Foundation; either
    version 2.1 of the License, or (at your option) any later version.

    This library is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
    Lesser General Public License for more details.

    You should have received a copy of the GNU Lesser General Public
    License along with this library; if not, write to the Free Software
    Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA

 */

package com.approximatrix.charting;

import java.awt.BasicStroke;

/** Defines basic strokes for line drawing in a convenient manner.  Allows the user to
 * reference strokes simply by name.
 * 
 * @author armstrong
 * @version 1.0
 *
 */
public class SimpleStrokeDefs {

	/** Constants for line dash drawing */
	private static final float[][] dash = {null,{2.0f,2.0f},{7.0f,7.0f},{4.0f,4.0f},{4.0f,4.0f,2.0f,4.0f}};

	/** Number of line styles */
	private static final int NUMBER_OF_STYLES = 5;
	
	/** Solid line stroke */
	public static final BasicStroke SOLID =  new BasicStroke(1.0f, 
			BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10.0f, dash[0], 0.0f);
	
	/** Name for referencing solid line */
	private static final String SOLID_NAME = "Solid Line";
	
	/** Dot stroke */
	public static final BasicStroke DOT =  new BasicStroke(1.0f, 
			BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10.0f, dash[1], 0.0f);
	
	/** Name for referencing dotted line */
	private static final String DOT_NAME = "Dotted Line";
	
	/** Large dash stroke */
	public static final BasicStroke LARGE_DASH =  new BasicStroke(1.0f, 
			BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10.0f, dash[2], 0.0f);
	
	/** Name for referencing large dash line */
	private static final String LARGE_DASH_NAME = "Large-Dashed Line";
	
	/** Small dash stroke */ 
	public static final BasicStroke SMALL_DASH =  new BasicStroke(1.0f, 
			BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10.0f, dash[3], 0.0f);
	
	/** Name for referencing small dash line */
	private static final String SMALL_DASH_NAME = "Small-Dashed Line";
	
	/** Dash-Dot stroke */
	public static final BasicStroke DASH_DOT =  new BasicStroke(1.0f, 
			BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10.0f, dash[3], 0.0f);
	
	/** Name for referencing dash-dot line */
	private static final String DASH_DOT_NAME = "Dash-Dot Line";
	
	/** Returns the string associated with the stroke passed in
	 * 
	 * @param value a valid BasicStroke
	 * @return the string naming this stroke, or "Unknown"
	 */
	public static String getStrokeDescription(BasicStroke value) {
		if(value == SOLID) {
			return SOLID_NAME;
		} else if(value == DOT) {
			return DOT_NAME;
		} else if(value == LARGE_DASH) {
			return LARGE_DASH_NAME;
		} else if(value == SMALL_DASH) {
			return SMALL_DASH_NAME;
		} else if(value == DASH_DOT) {
			return DASH_DOT_NAME;
		} else {
			return "Unknown";
		}
	}
	
	/** Returns a BasicStroke based on the name passed into this function
	 * 
	 * @param description the name of the requested stroke
	 * @return the associated BasicStroke object, or a SOLID stroke if the name is not found
	 */
	public static BasicStroke getStroke(String description) {
		if(description == SOLID_NAME) {
			return SOLID;
		} else if(description == DOT_NAME) {
			return DOT;
		} else if(description == LARGE_DASH_NAME) {
			return LARGE_DASH;
		} else if(description == SMALL_DASH_NAME) {
			return SMALL_DASH;
		} else if(description == DASH_DOT_NAME) {
			return DASH_DOT;
		} else {
			return SOLID;
		}			
	}
	
	/** Returns the name of the default stroke
	 * 
	 * @return the name of the default stroke (which is solid)
	 */
	public static String getDefaultStrokeDescription() {
		return SOLID_NAME;
	}
	
	/** Returns an array containing the names of all the available strokes provided
	 * by this static class
	 * 
	 * @return an array of the styles defined in this class
	 */ 
	public static String[] getAvailableStyles() {
		String[] return_val = new String[NUMBER_OF_STYLES];
		return_val[0] = SOLID_NAME;
		return_val[1] = DOT_NAME;
		return_val[2] = LARGE_DASH_NAME;
		return_val[3] = SMALL_DASH_NAME;
		return_val[4] = DASH_DOT_NAME;
		return return_val;
	}
	
}
