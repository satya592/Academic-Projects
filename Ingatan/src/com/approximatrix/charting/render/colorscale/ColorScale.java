/*
    OpenChart2 Java Charting Library and Toolkit
    Copyright (C) 2005-2008 Approximatrix, LLC
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

    ColorScale.java
*/

package com.approximatrix.charting.render.colorscale;

import java.awt.Color;

/** Color scale interface used for classes that convert data values into
 * a Color based on an implemented scaling algorithm
 * 
 * @author armstrong
 */
public interface ColorScale {
	
	/** Returns a color based on a double precision data value
	 * 
	 * @param value the data space value
	 * @return a valid Color object
	 */
	public Color getColor(double value);
	
	/** Returns a color based on an integer data value
	 * 
	 * @param value the data space value
	 * @return a valid Color object
	 */
	public Color getColor(int value);
	
	/** Returns an array of Colors based on an array of
	 * double precision data values
	 * 
	 * @param value an array of data space values
	 * @return an array of Colors
	 */
	public Color[] getColors(double value[]);
	
	/** Returns an array of Colors based on an array of
	 * integer data values
	 * 
	 * @param value an array of data space values
	 * @return an array of Colors
	 */
	public Color[] getColors(int value[]);
}
