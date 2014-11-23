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
 
    ObjectTickLocator.java 
 */

package com.approximatrix.charting.coordsystem.ticklocator;

/** Class for calculating the position of tick marks along an axis which uses simple integer
 * values for positioning, like an axis plotting arbitrary Object labels.
 * 
 * @author armstrong
 */
public class ObjectTickLocator {

	private int first = 0;
	private int last = 1;
	private int maxTics = 20;
	private boolean forceAll = false;
	
	/** Constructs the locator object based on the first and last index of
	 * objects to be displayed.  Constrains ticks to the maximum number.
	 * 
	 * @param first the smallest index to allow
	 * @param last the largest index to allow
	 * @param maxTics the maximum number of ticks to display
	 */
	public ObjectTickLocator(int first, int last, int maxTics) {
		this(first,last,maxTics,false);
	}
	
	/** Constructs the locator object based on the first and last index of
	 * objects to be displayed.  Constrains ticks to the maximum number.
	 * Setting forceAll allows all indices to be displayed.
	 * 
	 * @param first the smallest index to allow
	 * @param last the largest index to allow
	 * @param maxTics the maximum number of ticks to display
	 * @param forceAll true to display all indices
	 */
	public ObjectTickLocator(int first, int last, int maxTics, boolean forceAll) {
		this.first = first;
		this.last = last;
		this.maxTics = maxTics;
		this.forceAll = forceAll;
		
		if(this.first > this.last) {
			int temp = this.last;
			this.last = this.first;
			this.first = temp;
		}

	}
	
	/** Returns an array of indices to display on the axes
	 * 
	 * @return an array of indices to display on the axis
	 */
	public int[] getTickMarkLocations() {
		int step = 1;
		int[] ret = null;
		do {
			ret = new int[(last-first)/step+1];
			for(int i=0;i<ret.length;i++)
				ret[i] = first+i*step;
			step = step * 2;
		} while(ret.length > maxTics && !forceAll);
		return ret;
	}
	
}
