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

    DotPlotDataModelConstraints.java
*/

package com.approximatrix.charting.model.threedimensional;

import com.approximatrix.charting.model.ChartDataModelConstraints;

/** Constraints for use with dot plot models.  Uses some dot-plot-specific methods
 * to do its work.  
 * 
 * @author armstrong
 */
public class DotPlotDataModelConstraints implements ChartDataModelConstraints {

	/** A dot plot data model */
	private AbstractDotPlotDataModel model;
	
	/** Constructor that accepts a model and an axis.	
	 * 
	 * @param model a valid dot plot model
	 * @param axis the y axis associated with this model - currently ignored
	 */
	public DotPlotDataModelConstraints(AbstractDotPlotDataModel model, int axis) {
		this.model = model;
	}
	
	/** Returns the maximum X value
	 * @deprecated Deprecated as of version 1.3
	 */
	public double getMaximumColumnValue() {
		return getMaximumX().doubleValue();
	}

	/** Returns the maximum Y value
	 * @deprecated Deprecated as of version 1.3
	 */
	public Number getMaximumValue() {
		return getMaximumY();
	}

	/** Returns the largest X value.  Passes through to the model
	 * if autoscaling is enabled.
	 * 
	 */
	public Number getMaximumX() {
		if(model.isAutoScale())
			return new Double(model.getModelMaximumX());
		else
			return model.getManualMaximumX();
	}

	/** Returns the largest Y value.  Passes through to the model
	 * if autoscaling is enabled.
	 * 
	 */
	public Number getMaximumY() {
		if(model.isAutoScale())
			return new Double(model.getModelMaximumY());
		else
			return model.getManualMaximumY();
	}
	
	/** Returns the smallest X value.  Passes through to the model
	 * if autoscaling is enabled.
	 * 
	 * @deprecated Deprecated as of version 1.3
	 */
	public double getMinimumColumnValue() {
		return getMinimumX().doubleValue();
	}
	
	/** Returns the smallest Y value.  Passes through to the model
	 * if autoscaling is enabled.
	 * 
	 * @deprecated Deprecated as of version 1.3
	 */
	public Number getMinimumValue() {
		return getMinimumY();
	}

	/** Returns the smallest X value.  Passes through to the model
	 * if autoscaling is enabled.
	 * 
	 */
	public Number getMinimumX() {
		if(model.isAutoScale())
			return new Double(model.getModelMinimumX());
		else
			return model.getManualMinimumX();
	}

	/** Returns the smallest Y value.  Passes through to the model
	 * if autoscaling is enabled.
	 * 
	 */
	public Number getMinimumY() {
		if(model.isAutoScale())
			return new Double(model.getModelMinimumY());
		else
			return model.getManualMinimumY();
	}

}
