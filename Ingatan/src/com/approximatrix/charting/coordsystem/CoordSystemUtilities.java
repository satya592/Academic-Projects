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
 
    CoordSystemUtilities.java
 */

package com.approximatrix.charting.coordsystem;

import com.approximatrix.charting.model.ChartDataModel;

/** Static class used by Charts for defining and building the default
 * coordinate system throughout Openchart2.
 * 
 * @author armstrong
 *
 */ 
public class CoordSystemUtilities {

	/** Internal constant refering to the max value index of a 2-element constraint array */ 
	public static final int MAX = 0;
	
	/** Internal constant refering to the min value index of a 2-element constraint array */
	public static final int MIN = 1;
	
	/** Indicator for using the ClassicCoordSystem by default */
	public static final int CLASSIC_COORD_SYSTEM = 0;
	
	/** The actual indicator for coordinate system defaults */
	private static int DefaultCoordSystem = CLASSIC_COORD_SYSTEM;
	
	
	/** Returns the curret default coordinate system
	 * 
	 * @return the integer representing the default coordinate system
	 */
	public static int GetDefaultCoordSystem() {
		return DefaultCoordSystem;
	}
	
	/** Sets the default coordinate system to use.  Note that 
	 * no validity checking is performed.
	 * 
	 * @param i the identifier of the coordinate system style
	 */
	public static void SetDefaultCoordSystem(int i) {
		DefaultCoordSystem = i;
	}
	
	/** Constructs the default coordinate system
	 * 
	 * @param cdm the associated chart data model for the system
	 * @return a CoordSystem object
	 */
	public static CoordSystem BuildDefaultCoordSystem(ChartDataModel cdm) {
		switch (DefaultCoordSystem) {
			case CLASSIC_COORD_SYSTEM:
				return new ClassicCoordSystem(cdm);
			default:
				return new ClassicCoordSystem(cdm);
		}
	}
	
    /** Returns a safe estimate of min and max drawing values for
     * an axis.  Used to defend against special cases where max and
     * min might be equal or reversed.
     * 
     * @param max the original max value
     * @param min the originial min value
     * @return the new max and min values in an array, which can be indexed using the MAX and MIN constants in this class
     */
    public static double[] SafeMaxMin(double max, double min) {
    	double[] maxmin = new double[2];
    	if(max-min == 0.0) {
    		if(max == 0.0) {
    			max = 1.0;
    			min = -1.0;
    		} else {
    			max = 1.1*max;
    			min = 0.9*min;
    		}
    	}
    	if(max < min) {
    		double hold = max;
    		max = min;
    		min = hold;
    	}
    	maxmin[MAX] = max;
    	maxmin[MIN] = min;
    	return maxmin;
    }
}
