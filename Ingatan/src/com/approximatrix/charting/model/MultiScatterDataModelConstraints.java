/*
    OpenChart2 Java Charting Library and Toolkit
    Copyright (C) 2005-2007 Approximatrix, LLC
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

package com.approximatrix.charting.model;

/** Implements a ChartDataModelConstraint class specifically for the
 * MultiScatterDataModel class.  This implementation is used to interface
 * some of the easy scaling methods available in MultiScatterDataModel
 *
 * @author armstrong
 */
public class MultiScatterDataModelConstraints implements ChartDataModelConstraints {

	/** The internal pointer to the corresponding data model */
	MultiScatterDataModel model = null;
	
	/** Constructor requiring a data model to be specified
	 *
	 * @param model the MultiScatterDataModel to support
	 */
	public MultiScatterDataModelConstraints(MultiScatterDataModel model) {
		this.model = model;
	}
	
	/**
	 * @see com.approximatrix.charting.model.ChartDataModelConstraints#getMaximumX()
	 */
	public Number getMaximumX() {
		if(model.isAutoScale())
			return model.getMaxXValue();
		else
			return model.getManualMaximumX();
	}

	/**
	 * @see com.approximatrix.charting.model.ChartDataModelConstraints#getMaximumY()
	 */
	public Number getMaximumY() {
		if(model.isAutoScale())
			return new Double(model.getMaxYValue());
		else
			return model.getManualMaximumY();
	}

	/**
	 * @see com.approximatrix.charting.model.ChartDataModelConstraints#getMinimumX()
	 */
	public Number getMinimumX() {
		if(model.isAutoScale())
			return new Double(model.getMinXValue());
		else
			return model.getManualMinimumX();
	}

	/**
	 * @see com.approximatrix.charting.model.ChartDataModelConstraints#getMinimumY()
	 */
	public Number getMinimumY() {
		if(model.isAutoScale())
			return new Double(model.getMinYValue());
		else
			return model.getManualMinimumY();
	}

	/** Returns the minimum dependent value to display
	 *
	 * @return a Number which represents the minimum dependent value for display
	 * @deprecated As of release 1.3, replaced by {@link #getMinimumY()} 
	 */
	public Number getMinimumValue() {
		return getMinimumY();
	}

	/** Returns the maximum dependent value to display
	 *
	 * @return a Number which represents the maximum dependent value for display
	 * @deprecated As of release 1.3, replaced by {@link #getMaximumY()}
	 */
	public Number getMaximumValue() {
		return getMaximumY();
	}

	/** Returns the minimum independent value to display
	 *
	 * @return a double which represents the minimum independent value for display
	 * @deprecated As of release 1.3, replaced by {@link #getMinimumX()}
	 */
	public double getMinimumColumnValue() {
		if(getMinimumX() == null)
			return Double.NEGATIVE_INFINITY;
		else
			return getMinimumX().doubleValue();
	}

	/** Returns the maximum independent value to display
	 *
	 * @return a double which represents the maximum independent value for display
	 * @deprecated As of release 1.3, replaced by {@link #getMaximumX()}
	 */
	public double getMaximumColumnValue() {
		if(getMaximumX() == null)
			return Double.POSITIVE_INFINITY;
		else
			return getMaximumX().doubleValue();
	}

}
