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

    DefaultChartDataModelConstraints.java
    Created on 16. Sept. 2002
*/

package com.approximatrix.charting.model;

import java.util.TreeSet;

import com.approximatrix.charting.ChartUtilities;

/**
 * Implementing the ChartDataModelConstraints this class provides the default implementation
 * for the data model constraints. Alternative implementations could return the sum of all
 * column values to implement stacked bar charts e.g.
 * @author  smueller armstrong
 */
public class DefaultChartDataModelConstraints implements ChartDataModelConstraints {
    
    /** The model for which to calculate the constraints. */ 
    protected AbstractChartDataModel model;
    
    /** The axis to compute the constraints. */
    protected int axis;
    
    /** A flag which determines if column values should be manually scalable. */
    protected boolean allowManualColScale = true;
    
    /** Creates a new instance of DefaultChartDataModelConstraints */
    public DefaultChartDataModelConstraints(AbstractChartDataModel model, int axis) {
        this.model = model;
        this.axis = axis;
    }
    
    /** Creates a new instance of DefaultChartDataModelConstraints
     * @param model the AbstractDataModel for which constraints will be computed
     * @param axis the y-axis which will be considered
     * @param allowManualScale a flag which triggers if column values should 
     * be allowed to be scaled manually (default is yes)
     */
    public DefaultChartDataModelConstraints(AbstractChartDataModel model, int axis, boolean allowManualColScale) {
        this(model, axis);
        this.allowManualColScale = allowManualColScale;
    }
    
    /** Returns the maximum value of all datasets.  
     * @deprecated As of release 1.3, replaced by {@link #getMinimumY()}
     */
    public Number getMaximumValue() {
    	return getMaximumY();
    }
    
    /** Returns the maximum value of all datasets.  
     * 
     * @return the maximum value
     */
    public Number getMaximumY() {
        TreeSet ordered_values = (TreeSet)model.getOrderedValues(axis);
        
        if(ordered_values.size() == 0)
            return new Integer(1);
        else if(model.isManualScale()) {
            //System.out.println("** model.getManualMaximumValue() = "+model.getManualMaximumValue());
            return model.getManualMaximumY();
        }
        else if(model.isAutoScale()) {
            double min = ((Number)ordered_values.first()).doubleValue();
            double max = ((Number)ordered_values.last()).doubleValue();
            
            //System.out.println("** min = "+min+"  max = "+max);
            
            if(min / max > 0.95) {
                //System.out.println("** ChartUtilities.performAutoScale(min/2, 2 * max)[1]"+ChartUtilities.performAutoScale(min/2, 2 * max)[1]);
                return new Double(ChartUtilities.performAutoScale(min/2, 
                                                                  2 * max)[1]);
             }
            else {
                //System.out.println("** ChartUtilities.performAutoScale(min, max)[1]"+ChartUtilities.performAutoScale(min, max)[1]);
                return new Double(ChartUtilities.performAutoScale(min, 
                                                              max)[1]);
             }
        } else
            return (Number)ordered_values.last();
    }    

    /** Returns the minimum value of all datasets.  
     * @deprecated As of release 1.3, replaced by {@link #getMinimumY()}
     */
    public Number getMinimumValue() {
    	return getMinimumY();
    }
    
    /** Returns the minimum value of all datasets.  
     * 
     * @return the minimum value
     */
    public Number getMinimumY() {
        TreeSet ordered_values = (TreeSet)model.getOrderedValues(axis);

        if(ordered_values.size() == 0)
            return new Integer(0);
        else if(model.isManualScale()) {
            //System.out.println("** model.getManualMinimumValue() = "+model.getManualMinimumValue());
            return model.getManualMinimumY();
        }
        else if(model.isAutoScale()) {
            double min = ((Number)ordered_values.first()).doubleValue();
            double max = ((Number)ordered_values.last()).doubleValue();

            //System.out.println("** min = "+min+"  max = "+max);
            
            if(min / max > 0.95) {
                //System.out.println("** ChartUtilities.performAutoScale(min/2, 2 * max)[0]"+ChartUtilities.performAutoScale(min/2, 2 * max)[0]);
                return new Double(ChartUtilities.performAutoScale(min/2, 
                                                                  2 * max)[0]);
             }
            else {
                //System.out.println("** ChartUtilities.performAutoScale(min, max)[0]"+ChartUtilities.performAutoScale(min, max)[0]);
                return new Double(ChartUtilities.performAutoScale(min, 
                                                              max)[0]);
             }
        } else                
           return (Number)ordered_values.first();
    }

    /** Returns the minimum column value. 
     * @throws ArrayIndexOutOfBoundsException if the Model is empty
     * @deprecated As of release 1.3, replaced by {@link #getMinimumX()}
     */
    public double getMinimumColumnValue() {
    	Number x = getMinimumX();
    	if(x == null) 
    		return 0;
    	else
    		return x.doubleValue();
    }
    
    /** Returns the minimum xvalue of all datasets.  
     * 
     * @return the minimum x value
     */
    public Number getMinimumX() {
        if(model.isManualScale() && allowManualColScale) {
            return model.getManualMinimumX();
        }
        if(model.isAutoScale() && model.isColumnNumeric())
            return new Double(ChartUtilities.performAutoScale(model.getFirstColumnValue(),
                                                   model.getLastColumnValue())[0]);
        else
            return new Double(model.getFirstColumnValue());
    }

    /** Returns the maximum column value. 
     * @throws ArrayIndexOutOfBoundsException if the model is empty
     * @deprecated As of release 1.3, replaced by {@link #getMaximumX()}
     */
    public double getMaximumColumnValue() {
    	Number x = getMaximumX();
    	if(x == null) 
    		return 0;
    	else
    		return x.doubleValue();
    }
    
    /** Returns the maximum xvalue of all datasets.  
     * 
     * @return the maximum x value
     */
    public Number getMaximumX() {
        if(model.isManualScale() && allowManualColScale) {
            return model.getManualMaximumX();
        }
        if(model.isAutoScale() && model.isColumnNumeric())
            return new Double(ChartUtilities.performAutoScale(model.getFirstColumnValue(),
                                                   model.getLastColumnValue())[1]);
        else
            return new Double(model.getLastColumnValue());
    }
}