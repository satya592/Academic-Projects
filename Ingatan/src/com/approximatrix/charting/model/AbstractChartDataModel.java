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

    AbstractChartDataModel.java
    Created on 28. Juni 2001, 18:58
*/

package com.approximatrix.charting.model;

import javax.swing.event.EventListenerList;

import com.approximatrix.charting.coordsystem.CoordSystem;
import com.approximatrix.charting.event.ChartDataModelEvent;
import com.approximatrix.charting.event.ChartDataModelListener;

import java.util.TreeSet;

/**
 * This class implements the event-handling methods for a chart model.
 * @author  mueller armstrong
 */
public abstract class AbstractChartDataModel implements ChartDataModel {
    
    /** The listener list. */
    protected EventListenerList listener = new EventListenerList();

    /** Flag defining the automatic scaling of max and min values. */
    protected boolean autoscale = true;

    /** Maximum and minimum column values to be displayed. */
    protected double maxcolumn, mincolumn;

    /** Maximum and minimum values to be displayed. */
    protected Number maxvalue, minvalue;
    
    /** Creates new AbstractChartDataModel */
    public AbstractChartDataModel() {
    }

    /** Removes a ChartDataModelListener.
     * @param l the ChartDataListener
     */
    public void removeChartDataModelListener(ChartDataModelListener l) {
        listener.remove(ChartDataModelListener.class, l);
    }
    
    /** Adds a ChartDataModelListener.
     * @param l the ChartDataModelListener
     */
    public void addChartDataModelListener(ChartDataModelListener l) {
    
	   	/** Look for duplicates */
        Object[] ls = listener.getListenerList();
        boolean found = false;
        for(int i = ls.length-1; i >= 1; i-=2) {
        	if(ls[i] == l) {
        		found = true;
        		break;
        	}
        }
		if(!found)
	        listener.add(ChartDataModelListener.class, l);

        listener.add(ChartDataModelListener.class, l);
    }
    
    /** Determines if the column values are numeric.
     * @return <CODE>false</CODE> per default
     */
    public boolean isColumnNumeric() {
        return false;
    }    
    
    /** Provides an empty implementation for not-editable DataModels.
     * @param set the DataSet in which the value should be set
     * @param index the index in the DataSet where the value should be stored
     * @param value the value object
     */
    public void setValueAt(int set, int index, Object value) {
    }
    
    /** Returns the class of the column values.
     * @return <CODE>Object.class</CODE> per default
     */
    public Class getColumnClass() {
        return Object.class;
    }
    
    /** Promotes a new ChartDataModelEvent.
     * @param src the source object of the event.
     */
    public void fireChartDataModelChangedEvent(Object src) {
        ChartDataModelEvent e = new ChartDataModelEvent(src);
        Object[] ls = listener.getListenerList();
        for (int i = (ls.length - 2); i >= 0; i-=2) {
            if (ls[i] == ChartDataModelListener.class) {
                ((ChartDataModelListener)ls[i + 1]).chartDataChanged(e);
            }
        }
    }
    
    /** Returns the Axis Binding for a specific DataSet, ie the Axis on
     * which the DataSet should be plotted
     * @param set the DataSet whose Axis binding should be determined
     * @return <code>DataSet.FIRST_YAXIS</code> by default.
     */
    public int getAxisBinding(int set) {
        return CoordSystem.FIRST_YAXIS;
    }
    
    /** Provides a default empty implementation.
     * @param set the DataSet
     * @param axis the Axis binding
     */
    public void setAxisBinding(int set, int axis) {
    }    
    
    public void setAutoScale(boolean b) {
        autoscale = b;
    }
    
    public boolean isAutoScale() {
        return autoscale;
    }

    /** Enables the manual axis scaling. Set the desired
     * maximum and minimum values using the setMaximum...Value
     * functions.
     * 
     * @deprecated As of release 1.4, only autoscale flag will be considered
     */
    public void setManualScale(boolean b) {
    	autoscale = !b;
    }

    /** Returns true if the manual axis scaling is enabled. This overrides
     * the enabled automatic axis scaling.
     * 
     * @deprecated As of release 1.4, only autoscale flag will be considered
     */
    public boolean isManualScale() {
        return !autoscale;
    }

    /** Sets the maximum x-axis value.
     * @deprecated As of release 1.3, replaced by {@link #setMaximumValueX()} 
     */
    public void setMaximumColumnValue(double d) {
        maxcolumn = d;
    }

    /** Sets the minimum x-axis value.
     * @deprecated As of release 1.3, replaced by {@link #setMinimumValueX()}
     */
    public void setMinimumColumnValue(double d) {
        mincolumn = d;
    }

    /** Sets the maximum y-axis value.
     * @deprecated As of release 1.3, replaced by {@link #setMaximumValueY()} 
     */
    public void setMaximumValue(Number n) {
        maxvalue = n;
    }

    /** Sets the minimum y-axis value. 
     * @deprecated As of release 1.3, replaced by {@link #setMinimumValueY()} 
     */
    public void setMinimumValue(Number n) {
        minvalue = n;
    }

    /**
     * @deprecated As of release 1.3, replaced by {@link #getManualMaximumX()} 
     */
    public double getManualMaximumColumnValue() {
        return maxcolumn;
    }

    /**
     * @deprecated As of release 1.3, replaced by {@link #getManualMinimumX()} 
     */
	public double getManualMinimumColumnValue() {
        return mincolumn;
    }

	/**
     * @deprecated As of release 1.3, replaced by {@link #getManualMaximumY()} 
     */
    public Number getManualMaximumValue() {
        return maxvalue;
    }

    /**
     * @deprecated As of release 1.3, replaced by {@link #getManualMinimumY()} 
     */
	public Number getManualMinimumValue() {
        return minvalue;
    }
	
	
	/** Returns the maximum manual x-axis scale
	 * 
	 * @return the maximum x-axis value, or null if not assigned
	 * @see com.approximatrix.charting.model.ChartDataModel#getManualMaximumX()
	 */
	public Number getManualMaximumX() {
		return new Double(maxcolumn);
	}

	/** Returns the maximum manual y-axis scale
	 * 
	 * @return the maximum y-axis value, or null if not assigned
	 * @see com.approximatrix.charting.model.ChartDataModel#getManualMaximumY()
	 */
	public Number getManualMaximumY() {
		return maxvalue;
	}

	/** Returns the minimum manual x-axis scale
	 * 
	 * @return the minimum x-axis value, or null if not assigned
	 * @see com.approximatrix.charting.model.ChartDataModel#getManualMinumumX()
	 */
	public Number getManualMinimumX() {
		return new Double(mincolumn);
	}

	/** Returns the minimum manual y-axis scale
	 * 
	 * @return the minimum y-axis value, or null if not assigned
	 * @see com.approximatrix.charting.model.ChartDataModel#getManualMinumumY()
	 */
	public Number getManualMinimumY() {
		return minvalue;
	}

	/** Sets the maximum x-axis value. 
	 *
	 * @param n the new maximum x value
	 * @see com.approximatrix.charting.model.ChartDataModel#setMaximumValueX(java.lang.Number)
	 */
	public void setMaximumValueX(Number n) {
		if(n == null) return;
		maxcolumn = n.doubleValue();
	}

	/** Sets the maximum y-axis value. 
	 *
	 * @param n the new maximum y value
	 * @see com.approximatrix.charting.model.ChartDataModel#setMaximumValueY(java.lang.Number)
	 */
	public void setMaximumValueY(Number n) {
		maxvalue = n;
	}

	/** Sets the minimum x-axis value. 
	 *
	 * @param n the new minimum x value
	 * @see com.approximatrix.charting.model.ChartDataModel#setMinimumValueX(java.lang.Number)
	 */
	public void setMinimumValueX(Number n) {
		if(n == null) return;
		mincolumn = n.doubleValue();
	}

	/** Sets the minimum y-axis value. 
	 *
	 * @param n the new minimum y value
	 * @see com.approximatrix.charting.model.ChartDataModel#setMinimumValueY(java.lang.Number)
	 */
	public void setMinimumValueY(Number n) {
		minvalue = n;
	}

	/** Returns the title of the DataSet.
     * @param set the DataSet identifier
     * @return the the number of
     * the DataSet per default.
     */
    public String getDataSetName(int set) {
        return "Dataset "+set;
    }
    
    /** Compares this ChartDataModel with another object. 
     * @param o the object to compare with
     * @return true, if o is an AbstractChartDataModel, the number of
     * DataSets is equal and all DataSet names and column values are equal.
     */
    public boolean equals(Object o) {
        if(o == null)
            return false;
        try {
            AbstractChartDataModel model = (AbstractChartDataModel)o;
            
            if(getDataSetNumber() != model.getDataSetNumber()) {
                return false;
            }
            
            for(int i = 0; i < getDataSetNumber(); i++) {
                if(!getDataSetName(i).equals(model.getDataSetName(i))) {
                    return false;
                }
                
                for(int j = 0; j < getDataSetLength(j); j++) {
                    if(!getColumnValueAt(j).equals(model.getColumnValueAt(j))) {
                        return false;
                    }
                }
            }
        } catch(Exception e) { return false;}
        
        return true;
    }
    
    protected abstract TreeSet getOrderedValues(int axis);
    
    protected abstract double getFirstColumnValue();
    
    protected abstract double getLastColumnValue();
}
