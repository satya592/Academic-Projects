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
 
    ObjectXTickLocator.java 
 */
package com.approximatrix.charting.coordsystem.ticklocator;

/** Extended class to locate and return a transformable vector of x,y pairs for
 * locating tick marks on an x axis (horizontal).
 * 
 * @author armstrong
 *
 */
public class ObjectXTickLocator extends ObjectTickLocator {

	/** Pass-through constructor to construct the locator object based on 
	 * the first and last index on the X axis of objects to be displayed.  
	 * Constrains ticks to the maximum number.
	 * 
	 * @param first the smallest index to allow
	 * @param last the largest index to allow
	 * @param maxTics the maximum number of ticks to display
	 */
	public ObjectXTickLocator(int first, int last, int maxTics) {
		super(first, last, maxTics);
	}

	/** Pass-through constructor to construct the locator object based on 
	 * the first and last index on the X axis of objects to be displayed.  
	 * Constrains ticks to the maximum number.  Setting forceAll allows 
	 * all indices to be displayed.
	 * 
	 * @param first the smallest index to allow
	 * @param last the largest index to allow
	 * @param maxTics the maximum number of ticks to display
	 * @param forceAll true to display all indices
	 */
	public ObjectXTickLocator(int first, int last, int maxTics, boolean forceAll) {
		super(first, last, maxTics, forceAll);
	}

	/** Returns an array of x,y pairs.  The x value contains an x tick
	 * location, while all the y values contain the passed value.  Data is
	 * returned as floats for the AffineTransform, although only the integer
	 * is meaningful.
	 * 
	 * @param y the value to use for y in the x,y pairs
	 * @return a vector of x,y pairs ready for use in an AffineTransform
	 */
	public float[] getTickMarkLocationsAsPairs(int y) {
		int[] x = super.getTickMarkLocations();
		float[] ret = new float[x.length*2];
		for(int i=0;i<ret.length;i=i+2) {
			ret[i] = (float)x[i/2];
			ret[i+1] = (float)y;
		}
		return ret;
	}
}
