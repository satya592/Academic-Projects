/*
    OpenChart2 Java Charting Library and Toolkit
    Copyright (C) 2005-2008 Approximatrix, LLC
    Copyright (C) 2001  Sebastian Müller
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
 
    SimpleColorScale.java
 */

package com.approximatrix.charting.render.colorscale;

import java.awt.Color;

/** Implements a simple color scale based on explicit maximum and minimum
 * values specified.  Red at low end, green at the center, and blue at the
 * high end.
 * 
 * @author armstrong
 *
 */
public class SimpleColorScale extends AbstractColorScale {

	/** Returns this color whenever the max and min values are identical */
	protected static final Color FAILED_CALC = Color.BLACK;
	
	/** The lowest value in the scale range */
	private double minV = 0.0;
	
	/** The highest value in the scale range */
	private double maxV = 1.0;
	
	/** Default constructor */
	public SimpleColorScale() {
		
	}
	
	/** Constructor excepting min and max range values as doubles
	 * 
	 * @param min the lowest expected value
	 * @param max the highest expected value
	 */
	public SimpleColorScale(double min, double max) {
		minV = min;
		maxV = max;
	}
	
	/** Constructor excepting min and max range values as integers
	 * 
	 * @param min the lowest expected value
	 * @param max the highest expected value
	 */
	public SimpleColorScale(int min, int max) {
		this((double)min, (double)max);
	}
	
	/** Returns a color based on a double
	 * 
	 */
	public Color getColor(double value) {
		if((maxV-minV) == 0.0) return FAILED_CALC;
		double multiple = (value-minV)/(maxV-minV);
	
		int r = Math.max(0, (int)(511.0*(0.5-multiple)));
		int b = Math.max(0, (int)(511.0*(multiple-0.5)));
		int g = (int)(511.0*multiple - Math.max(0.0, 767.0*(multiple-0.5)));

		return new Color(Math.min(r,255),Math.min(g,255),Math.min(b,255));
	}

	/** Returns a color based on an integer
	 * 
	 */
	public Color getColor(int value) {
		return getColor((double)value);
	}

}
