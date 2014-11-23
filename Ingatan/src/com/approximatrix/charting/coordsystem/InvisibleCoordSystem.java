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
 
    InvisibleCoordSystem.java 
 */

package com.approximatrix.charting.coordsystem;

import java.awt.Graphics2D;

import com.approximatrix.charting.model.ChartDataModel;

/** Implements a complete calculating coordinate system via extending
 * ClassicCoordSystem, but overrides the drawing to hide the actual
 * coordinate system on viewable Graphics2D objects.  This coordinate
 * system is ideal for radar and pie charts.
 * 
 * @author jarmstrong
 */
public class InvisibleCoordSystem extends ClassicCoordSystem {

	/** Constructs an invisible coordinate system using the passed data model.
	 * Passes directly through to ClassicCoordSystem.
	 * 
	 * @param cdm the chart data model
	 */
	public InvisibleCoordSystem(ChartDataModel cdm) {
		super(cdm);
	}

	/** Overrides the ClassicCoordSystem painting to return immediately
	 * without actually painting anything.
	 */
	public void paintDefault(Graphics2D g) {
		return;
	}

}
