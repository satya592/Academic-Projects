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
 
    NumericXTickLocator.java 
 */
package com.approximatrix.charting.coordsystem.ticklocator;

/** Extended class to locate and return a transformable vector of x,y pairs for
 * locating tick marks on an x axis (horizontal).
 * 
 * @author armstrong
 *
 */
public class NumericXTickLocator extends NumericTickLocator {

	/** Pass-through constructor to constructs a numeric tick locator 
	 * based on maximum and minimum values.
	 * 
	 * @param max the largest value to be plotted
	 * @param min the smallest value to be plotted
	 * @param numTicsDesired the approximate desired number of tick marks
	 * @param maxTics the most allowed ticks
	 * @param force_simple true to force the locator to create exactly numTicsDesired starting with min and ending with max
	 */
	public NumericXTickLocator(double max, double min, int numTicsDesired, int maxTics, boolean force_simple) {
		super(max, min, numTicsDesired, maxTics, force_simple);
	}

	/** Pass-through constructor to constructs a numeric tick locator 
	 * based on maximum and minimum values.  Allows one to specify using 
	 * a simple locator, spacing ticks equally between max and min
	 * values.  
	 * 
	 * @param max the largest value to be plotted
	 * @param min the smallest value to be plotted
	 * @param numTicsDesired the approximate desired number of tick marks
	 * @param maxTics the most allowed ticks
	 */
	public NumericXTickLocator(double max, double min, int numTicsDesired, int maxTics) {
		super(max, min, numTicsDesired, maxTics);
	}
	
	/** Returns an array of x,y pairs.  The x value contains an x tick
	 * location, while all the y values contain the passed value.
	 * 
	 * @param y the value to use for y in the x,y pairs
	 * @return a vector of x,y pairs ready for use in an AffineTransform
	 */
	public double[] getTickMarkLocationsAsPairs(double y) {
		double[] x = super.getTickMarkLocations();
		if(x == null) return null;
		double[] ret = new double[2*x.length];
		
		for(int i=0;i<ret.length;i=i+2) {
			ret[i] = x[i/2];
			ret[i+1] = y;
		}
		return ret;
	}
	
}
