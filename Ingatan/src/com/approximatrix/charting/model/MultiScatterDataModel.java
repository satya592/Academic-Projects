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

import java.util.ArrayList;

import javax.swing.event.EventListenerList;

import com.approximatrix.charting.SimpleStrokeDefs;
import com.approximatrix.charting.event.ChartDataModelEvent;
import com.approximatrix.charting.event.ChartDataModelListener;

/** Implements a ChartDataModel that is used for plotting true scatter plots
 * where each series can have a different set of independent points.  This
 * data model also stores series properties, similar to the ScatterDataModel.
 *
 * @author armstrong
 */
public class MultiScatterDataModel implements ChartDataModel  {

	/** Stores all DataSet objects in the model */
	private ArrayList<DataSet> data = null;
	
	/** Stores the names of all data sets */
	private ArrayList<String> names = null;
	
	/** Stores the properties of all data sets */
	private ArrayList<SeriesProperties> properties = null;
	
	/** The listener list. */
    protected EventListenerList listener = new EventListenerList();
    
    /** Flag defining the automatic scaling of max and min values. */
    protected boolean autoscale = true;

    /** Maximum and minimum manually-set column values to be displayed. */
    protected Double manual_max_x, manual_min_x;

    /** Maximum and minimum manually-set values to be displayed. */
    protected Double manual_max_y, manual_min_y;
    
    /** Buffers for the maximum values */
    private Double max_x, max_y, min_x, min_y;
    
    /** The constraints for the first y-axis.*/
    protected ChartDataModelConstraints constraints = null;

	/** A default constructor for initializing the internal ArrayLists */	
	public MultiScatterDataModel() {
		data = new ArrayList<DataSet>();
		names = new ArrayList<String>();
		properties = new ArrayList<SeriesProperties>();
		
		clearBuffers();
		
		constraints = new MultiScatterDataModelConstraints(this);
	}
	
	/** Adds a data set of an array of x values and an array of y values
	 * to the model. 
	 * @param x independent values
	 * @param y dependent values
	 * @param name data set name for legend
	 */
	public void addData(double[] x, double[] y, String name) {
		names.add(name);
		properties.add(new SeriesProperties());
		data.add(new DataSet(x,y));
		clearBuffers();
		this.fireChartDataModelChangedEvent(this);
	}
	
	/** Adds a data set of an array consisting of independent values in
	 * the first column and dependent values in the second column to 
	 * the model. 
	 * @param xy data values
	 * @param name data set name for legend
	 */
	public void addData(double[][] xy, String name) {
		names.add(name);
		properties.add(new SeriesProperties());
		data.add(new DataSet(xy));
		clearBuffers();
		this.fireChartDataModelChangedEvent(this);
	}
	
	/** Removes a series from the data model based on the series name
	 * 
	 * @param name name of the data set to remove
	 */
	public void removeData(String name) {
		for(int i=0;i<names.size();i++) {
			String this_name = (String)names.get(i);
			if(this_name.compareTo(name)==0) {
				removeData(i);
				break;
			}
		}
		clearBuffers();
		this.fireChartDataModelChangedEvent(this);
	}
	
	/** Updates the x,y values for a dataset specified by its name
	 * 
	 * @param name the name of the data set to update
	 * @param x the new independent values
	 * @param y the new dependent values
	 */
	public void updateData(String name, double[] x, double y[]) {
		for(int i=0;i<names.size();i++) {
			String this_name = (String)names.get(i);
			if(this_name.compareTo(name)==0) {
				data.set(i,new DataSet(x,y));
				break;
			}
		}
		clearBuffers();
		this.fireChartDataModelChangedEvent(this);
	}
	
	/** Updates the x,y values for a dataset specified by its name
	 * 
	 * @param name the name of the data set to update
	 * @param xy data values
	 */
	public void updateData(String name, double[][] xy) {
		for(int i=0;i<names.size();i++) {
			String this_name = (String)names.get(i);
			if(this_name.compareTo(name)==0) {
				data.set(i,new DataSet(xy));
				break;
			}
		}
		clearBuffers();
		this.fireChartDataModelChangedEvent(this);
	}
	
	/** Determines if a specified series exists in the model
	 * 
	 * @param name the series of interest
	 * @return true if the series exists, false otherwise
	 */
	public boolean isSeries(String name) {
		boolean return_val = false;
		for(int i=0;i<names.size();i++) {
			String this_name = (String)names.get(i);
			if(this_name.compareTo(name)==0) {
				return_val = true;
				break;
			}
		}
		return return_val;
	}
	
	/** Removes a series from the data model based on the passed index
	 * 
	 * @param i index of the series to remove
	 */
	public void removeData(int i) {
		if(i > names.size() || i > data.size()) return;
		
		names.remove(i);
		data.remove(i);
		if(i<properties.size()) properties.remove(i);
		//fireChartDataModelChangedEvent(this);
		clearBuffers();
		this.fireChartDataModelChangedEvent(this);
	}
	
	/** Returns the number of series in the model
	 * 
	 * @return Number of currently known data sets
	 */
	public int getNumberSeries() {
		return Math.min(names.size(),data.size());
	}
	
	/** Implements a complete data set model that holds and manages
	 * X and Y values separately
	 * @author armstrong
	 *
	 */
	private class DataSet {

		/** The data set's independent values */
		private ArrayList<Double> xvalues = null;
		
		/** The data set's dependent values */
		private ArrayList<Double> yvalues = null;
		
		/** Initializes internal ArrayLists without setting any data */
		public DataSet() {
			xvalues = new ArrayList<Double>();
			yvalues = new ArrayList<Double>();
		}
		
		/** Creates a new data set from two vectors containing x and y values
		 *
		 * @param x the independent values
		 * @param y the dependent values
		 */
		public DataSet(double[] x, double[] y) {
			this();
			this.setXValues(x);
			this.setYValues(y);
		}
		
		/** Creates a new data set from a single array containing both x and
		 * y values
		 *
		 * @param xy a 2 column array with the first column containing independent values, and the second containing dependent values
		 */
		public DataSet(double[][] xy) {
			this();
			this.setXYValues(xy);
		}
		
		/** Returns the number of entries in the data set
		 *
		 * @return the number of entries
		 */
		public int size() {
			return Math.min(xvalues.size(),yvalues.size());
		}
		
		/** Returns the specified independent value
		 *
		 * @param i the independent value to retrieve
		 * @return the independent value at i
		 */
		public double getX(int i) {
			if(i> xvalues.size()) return -1.0;
			
			return xvalues.get(i).doubleValue();
		}
		
		/** Returns the specified dependent value
		 *
		 * @param i the dependent value to retrieve
		 * @return the dependent value at i
		 */
		public double getY(int i) {
			if(i> yvalues.size()) return -1.0;
			
			return yvalues.get(i).doubleValue();
		}
		
		/** Sets the independent values for this data set
		 * 
		 * @param inputs a vector of independent values
		 */
		public void setXValues(double[] inputs) {
			for(int i=0;i<inputs.length;i++) {
				xvalues.add(new Double(inputs[i]));
			}
		}
		
		/** Sets the dependent values for this data set
		 * 
		 * @param inputs a vector of dependent values
		 */
		public void setYValues(double[] inputs) {
			for(int i=0;i<inputs.length;i++) {
				yvalues.add(new Double(inputs[i]));
			}
		}
		
		/** Sets the independent and dependent values simultaneously
		 *
		 * @param inputs an array containing independent values in the first column and dependent values in the second column
		 */
		public void setXYValues(double[][] inputs) {
			if(inputs[0].length < 2) return;
			
			for(int i=0;i<inputs.length;i++) {
				xvalues.add(new Double(inputs[i][0]));
				yvalues.add(new Double(inputs[i][1]));
			}
		}
		
		/** Sets a single independent value at a specified location
		 *
		 * @param value the value to set
		 * @param i the location to set
		 */
		public void setXValue(double value, int i) {
			if(i > xvalues.size()) return;
			
			xvalues.set(i,new Double(value));
		}
		
		/** Sets a single dependent value at a specified location
		 *
		 * @param value the value to set
		 * @param i the location to set
		 */
		public void setYValue(double value, int i) {
			if(i > yvalues.size()) return;
			
			yvalues.set(i,new Double(value));
		}
		
		/** Returns the largest independent value in the data set
		 *
		 * @return a double representing the largest indepedent value
		 */
		public double getMaxX() {
			double current = Double.NEGATIVE_INFINITY;
			
			for(int i=0;i<xvalues.size();i++) {
				if(xvalues.get(i).doubleValue() > current) current = xvalues.get(i).doubleValue();
			}
			
			return current;
		}
		
		/** Returns the largest dependent value in the data set
		 *
		 * @return a double representing the largest depedent value
		 */
		public double getMaxY() {
			double current = Double.NEGATIVE_INFINITY;
			
			for(int i=0;i<yvalues.size();i++) {
				if(yvalues.get(i).doubleValue() > current) current = yvalues.get(i).doubleValue();
			}
			
			return current;
		}
		
		/** Returns the smallest independent value in the data set
		 *
		 * @return a double representing the smallest indepedent value
		 */
		public double getMinX() {
			double current = Double.POSITIVE_INFINITY;
			
			for(int i=0;i<xvalues.size();i++) {
				if(xvalues.get(i).doubleValue() < current) current = xvalues.get(i).doubleValue();
			}
			
			return current;
		}
		
		/** Returns the smallest dependent value in the data set
		 *
		 * @return a double representing the smallest depedent value
		 */
		public double getMinY() {
			double current = Double.POSITIVE_INFINITY;
			
			for(int i=0;i<yvalues.size();i++) {
				if(yvalues.get(i).doubleValue() < current) current = yvalues.get(i).doubleValue();
			}
			
			return current;
		}
		
	}

	/** Stores all Series Properties, including marker and line preferences,
	 * for a data series (originally implemented in ScatterDataModel).
	 * @author armstrong
	 *
	 */
	private class SeriesProperties {
		
		/** Whether a marker is shown when drawing the series */
		private boolean show_marker;
		
		/** Whether a line is shown when drawing the series */
		private boolean show_line;
		
		/** String describing the line style to use */
		private String line_description;
		
		/** Initializes the properties object, setting no markers, line
		 * drawing, and the default line style
		 */
		public SeriesProperties() {
			show_marker = false;
			show_line = true;
			line_description = SimpleStrokeDefs.getDefaultStrokeDescription();
		}
		
		/** Sets the line style to the specified format
		 * 
		 * @param value the line style to use
		 */
		public void setLineStyle(String value) {
			line_description = value;
		}

		/** Returns a string describing the line style
		 *
		 * @return descrition of the line style
		 */
		public String getLineStyle() {
			return line_description;
		}
		
		/** Sets markers to be drawn */
		public void setMarkerOn() {
			show_marker = true;
		}
		
		/** Sets markers to be hidden */
		public void setMarkerOff() {
			show_marker = false;
		}
		
		/** Sets connecting line to be drawn */
		public void setLineOn() {
			show_line = true;
		}
		
		/** Sets connecting line to be hidden */
		public void setLineOff() {
			show_line = false;
		}
		
		/** Returns state of marker drawing
		 *
		 * @return true if markers are drawn, false otherwise
		 */
		public boolean getMarker() {
			return show_marker;
		}
		
		/** Returns state of line drawing
		 *
		 * @return true if line is drawn, false otherwise
		 */
		public boolean getLine() {
			return show_line;
		}
				
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
        listener.add(ChartDataModelListener.class, l);
    }
	
	/** Sets whether the data model is using autoscale for constraints or
	 * specified values
	 *
	 * @param b true to autoscale, false otherwise
	 */
    public void setAutoScale(boolean b) {
    	if(!b) {
    		this.setMaximumValueX(new Double(this.getMaxXValue()));
    		this.setMinimumValueX(new Double(this.getMinXValue()));
    		this.setMaximumValueY(new Double(this.getMaxYValue()));
    		this.setMinimumValueY(new Double(this.getMinYValue()));
    	}
    	autoscale = b;
    }
    
    /** Returns whether autoscaling is enabled
     *
     * @return true if autoscaling, false otherwise
     */
    public boolean isAutoScale() {
    	return autoscale;
    }

    /** Enables the manual axis scaling. Set the desired
     * maximum and minimum values using the setMaximum...Value
     * functions.
     *
     * @param b true to enable manual scaling, false otherwise
     * @deprecated As of release 1.4, only autoscale flag will be considered
     */
    public void setManualScale(boolean b) {
    	setAutoScale(!b);
    }

    /** Returns true if the manual axis scaling is enabled. This overrides
     * the enabled automatic axis scaling.
     *
     * @return true if manual scaling, false otherwise
     * @deprecated As of release 1.4, only autoscale flag will be considered
     */
    public boolean isManualScale() {
    	return !isAutoScale();
    }

    /** Sets the maximum independent (x-axis) value. Has no effect if
     * autoscaling is enabled
     *
     * @param d the maximum independent value
     * @deprecated As of release 1.3, replaced by {@link #setMaximumValueX()}
     */
    public void setMaximumColumnValue(double d) {
    	manual_max_x = new Double(d);
    }

    /** Sets the minimum independent (x-axis) value. Has no effect if
     * autoscaling is enabled
     *
     * @param d the minimum independent value
     * @deprecated As of release 1.3, replaced by {@link #setMinimumValueX()}
     */
    public void setMinimumColumnValue(double d) {
    	manual_min_x = new Double(d);
    }

    /** Sets the maximum dependent (y-axis) value. Has no effect if
     * autoscaling is enabled
     *
     * @param n a number of the maximum dependent value
     * @deprecated As of release 1.3, replaced by {@link #setMaximumValueY()}
     */
    public void setMaximumValue(Number n) {
    	manual_max_y = new Double(n.doubleValue());
    }

    /** Sets the minimum dependent (y-axis) value. Has no effect if
     * autoscaling is enabled
     *
     * @param n a number of the minimum dependent value
     * @deprecated As of release 1.3, replaced by {@link #setMinimumValueY()}
     */
    public void setMinimumValue(Number n) {
    	manual_min_y = new Double(n.doubleValue());
    }

	/** Returns the maximum manual scaling independent (x-axis) value
	 *
	 * @return the maximum manual scaling independent value
	 */
    public double getManualMaximumColumnValue() {
    	try {
    		return manual_max_x.doubleValue();
    	} catch(NullPointerException npe) {
    		return Double.NaN;
    	}
    }

	/** Returns the minimum manual scaling independent (x-axis) value
	 *
	 * @return the minimum manual scaling independent value
	 */
    public double getManualMinimumColumnValue() {
    	try {
    		return manual_min_x.doubleValue();
    	} catch(NullPointerException npe) {
    		return Double.NaN;
    	}
	}

    /** Returns the maximum manual scaling dependent (y-axis) value
	 *
	 * @return the maximum manual scaling dependent value
	 */
    public Number getManualMaximumValue() {
    	return manual_max_y;
    }

	/** Returns the minimum manual scaling dependent (y-axis) value
	 *
	 * @return the minimum manual scaling dependent value
	 */
    public Number getManualMinimumValue() {
		return manual_min_y;
    }
    
    /** Returns the minimum manual x-axis scale
     * 
     * @return the minimum x-axis value, or null if not assigned
     */
    public Number getManualMinimumX() {
    	return manual_min_x;
    }
    
    /** Returns the maximum manual x-axis scale
     * 
     * @return the maximum x-axis value, or null if not assigned
     */
    public Number getManualMaximumX() {
    	return manual_max_x;
    }
    
    /** Returns the minimum manual y-axis scale
     * 
     * @return the minimum y-axis value, or null if not assigned
     */
    public Number getManualMinimumY() {
    	return manual_min_y;
    }
    
    /** Returns the maximum manual y-axis scale
     * 
     * @return the maximum y-axis value, or null if not assigned
     */
    public Number getManualMaximumY() {
    	return manual_max_y;
    }
	
    /** Sets the maximum x-axis value. 
	 *
	 * @param n the new maximum x value
	 * @see com.approximatrix.charting.model.ChartDataModel#setMaximumValueX(java.lang.Number)
	 */
	public void setMaximumValueX(Number n) {
		if(n instanceof Double)
			manual_max_x = (Double)n;
		else if(n == null)
			manual_max_x = null;
		else
			manual_max_x = new Double(n.doubleValue());
	}

	/** Sets the maximum y-axis value. 
	 *
	 * @param n the new maximum y value
	 * @see com.approximatrix.charting.model.ChartDataModel#setMaximumValueY(java.lang.Number)
	 */
	public void setMaximumValueY(Number n) {
		if(n instanceof Double)
			manual_max_y = (Double)n;
		else if(n == null)
			manual_max_y = null;
		else
			manual_max_y = new Double(n.doubleValue());
	}

	/** Sets the minimum x-axis value. 
	 *
	 * @param n the new minimum x value
	 * @see com.approximatrix.charting.model.ChartDataModel#setMinimumValueX(java.lang.Number)
	 */
	public void setMinimumValueX(Number n) {
		if(n instanceof Double)
			manual_min_x = (Double)n;
		else if(n == null)
			manual_min_x = null;
		else
			manual_min_x = new Double(n.doubleValue());
	}

	/** Sets the minimum y-axis value. 
	 *
	 * @param n the new minimum y value
	 * @see com.approximatrix.charting.model.ChartDataModel#setMinimumValueY(java.lang.Number)
	 */
	public void setMinimumValueY(Number n) {
		if(n instanceof Double)
			manual_min_y = (Double)n;
		else if(n == null)
			manual_min_y = null;
		else
			manual_min_y = new Double(n.doubleValue());
	}

	/** Defines whether the column values are numeric,
     * that is, they can be casted to <code>Number</code>.
     * @return <CODE>true</CODE> if the column value can be
     * safely casted to Number type.
     */
    public boolean isColumnNumeric() {
    	return true;
    }
    
    /** Returns the length of a certain dataset. Note that for proper
     * rendering all datasets should have an equal length.
     * @param set the DataSet index
     * @return the int length of a DataSet
     */
    public int getDataSetLength(int set) {
    	if(set > data.size()) return -1;
    	
    	return ((DataSet)data.get(set)).size();
    }
    
    /** Returns the title of the DataSet used for rendering the Legend.
     * @param set the DataSet index
     * @return a String containing the Title.
     */
    public String getDataSetName(int set) {
    	if(set > names.size()) return null;
    	
    	return (String)names.get(set);
    }
    
    /** Returns the index of of the DataSet specified by the given DataSet name
     * @param series the name of the DataSet
     * @return the associated index or -1 if not found
     */
    public int getDataSetIndex(String series) {
    	for(int i=0;i<names.size();i++) {
    		if(((String)names.get(i)).compareTo(series)==0) 
    			return i;
    	}
    	
    	return -1;
    }
    
    /** Returns the Value in a specific dataset at a certain index.
     * @param set the DataSet index
     * @param index the value index in the DataSet
     * @return the value Object
     */
    public Number getValueAt(int set, int index) {
    	if(set > data.size()) return null;
    	
    	return new Double(((DataSet)data.get(set)).getY(index));
    }
    
    /** Sets the value in a specific dataset at the given index.
     * At this time, this function has no effect.
     * @param set the DataSet index
     * @param index the value index in the DataSet
     * @param value the value to be stored
     */
    public void setValueAt(int set, int index, Object value) {
    	if(set > data.size()) return;
    	//if(value.getClass()
    	//((DataSet)data.get(set)).setYValue(,index);
    	clearBuffers();
    }
    
    /** Returns the class of the columns.
     * @return the class of the column values. In case of numeric
     * columns this is always Number.class.
     */
    public Class getColumnClass() {
    	return Number.class;
    }

    /** Returns a specific column value.
     * @param col the column index
     * @return the column value. In case of numeric columns this is
     * always a Number object.
     */
    public Object getColumnValueAt(int col) {
    	return null;
    }
    
    /** Returns a specific column value.
     * @param set the data set index
     * @param col the column index
     * @return the column value. In case of numeric columns this is
     * always a Number object.
     */
    public Object getColumnValueAt(int set, int col) {
    	if(set > data.size()) return null;
    	
    	return new Double(((DataSet)data.get(set)).getX(col));
    }
    
    /** Returns the total amount of datasets.
     * @return the total amount of DataSets
     */
    public int getDataSetNumber() {
    	return getNumberSeries();
    }
    
    /** Returns the Axis binding of a specific DataSet.
     * @param set the DataSet index
     * @return an integer constant defining the Axis binding
     */
    public int getAxisBinding(int set) {
    	return 0;
    }
    
    /** Sets the Axis binding of a DataSet.
     * @param set the DataSet index
     * @param axis the Axis binding constant
     */
    public void setAxisBinding(int set, int axis) {
    	// No effect at this time
    }
    
    /** Returns a ChartDataModelConstraints object for the given
     * axis binding.
     * @param axis the Axis constant
     * @return a ChartDataModelConstraints object.
     */
    public ChartDataModelConstraints getChartDataModelConstraints(int axis) {
    	return constraints;
    }
    
    /** Sets the ChartDataModelConstraints object for the given
     * axis binding.
     * @param axis the Axis constant
     * @param constraints the ChartDataModelConstraints object
     * @return a ChartDataModelConstraints object.
     */
    public void setChartDataModelConstraints(int axis, ChartDataModelConstraints constraints) {
    	this.constraints = constraints;
    }
    
    /** Returns the maximum X value from all data sets
     * 
     * @return The maximum value of all X values
     */
    public double getMaxXValue() {
    	if(max_x == null) {
    		double return_val = Double.NEGATIVE_INFINITY;
    		for(int i=0;i<data.size();i++) {
    			double max_for_one = ((DataSet)data.get(i)).getMaxX();
    			if(max_for_one > return_val) return_val = max_for_one;
    		}
    		max_x = new Double(return_val);
    		checkConstraintSafety();
    	}
    	return max_x.doubleValue();
    }
    
    /** Returns the maximum Y value from all data sets
     * 
     * @return The maximum value of all Y values
     */
    public double getMaxYValue() {
    	if(max_y == null) {
    		double return_val = Double.NEGATIVE_INFINITY;
    		for(int i=0;i<data.size();i++) {
    			double max_for_one = ((DataSet)data.get(i)).getMaxY();
    			if(max_for_one > return_val) return_val = max_for_one;
    		}
    		max_y = new Double(return_val);
    		checkConstraintSafety();
    	}
    	return max_y.doubleValue();
    }
    
    /** Returns the minimum X value from all data sets
     * 
     * @return The minimum value of all X values
     */
    public double getMinXValue() {
    	if(min_x == null) {
    		double return_val = Double.POSITIVE_INFINITY;
    		for(int i=0;i<data.size();i++) {
    			double min_for_one = ((DataSet)data.get(i)).getMinX();
    			if(min_for_one < return_val) return_val = min_for_one;
    		}
    		min_x = new Double(return_val);
    		checkConstraintSafety();
    	}
    	return min_x.doubleValue();
    }
    
    /** Returns the minimum Y value from all data sets
     * 
     * @return The minimum value of all Y values
     */
    public double getMinYValue() {
    	if(min_y == null) {
    		double return_val = Double.POSITIVE_INFINITY;
    		for(int i=0;i<data.size();i++) {
    			double min_for_one = ((DataSet)data.get(i)).getMinY();
    			if(min_for_one < return_val) return_val = min_for_one;
    		}
    		min_y = new Double(return_val);
    		checkConstraintSafety();
    	}
    	return min_y.doubleValue();
    }
    
  	/** Sets the series marker, specified by name, to be either displayed or
	 * hidden
	 * @param name line series name
	 * @param on_or_off true to show markers, false otherwise
	 */
	public void setSeriesMarker(String name, boolean on_or_off) {
		int i;
		for(i=0;i<this.getDataSetNumber();i++) {
			if(name.compareTo(this.getDataSetName(i))==0) {
				break;
			}
		}
		if(i<this.getDataSetNumber()) {
			SeriesProperties this_series = (SeriesProperties)properties.get(i);
			if(on_or_off) {
				this_series.setMarkerOn();
			} else {
				this_series.setMarkerOff();
			}
		}
	}
	
	/** Returns the numeric index in the model of a series identified by name
	 * 
	 * @param name the name of the series
	 * @return the index, or -1 if not found
	 */
	public int getSeriesIndex(String name) {
		for(int i=0;i<this.getDataSetNumber();i++) {
			if(name.compareTo(this.getDataSetName(i))==0) {
				return i;
			}
		}
		return -1;
	}
	
	/** Set the series line, specified by name, to be either displayed or 
	 * hidden
	 * @param name line series name
	 * @param on_or_off true to display the line, false otherwise
	 */
	public void setSeriesLine(String name, boolean on_or_off) {
		int i = getSeriesIndex(name);
		
		if(i<this.getDataSetNumber() && i >= 0) {
			SeriesProperties this_series = (SeriesProperties)properties.get(i);
			if(on_or_off) {
				this_series.setLineOn();
			} else {
				this_series.setLineOff();
			}
		}
	}
	
	/** Determines whether the series should be drawn with markers
	 * 
	 * @param series the name of the desired series
	 * @return true if markers should be drawn
	 */
	public boolean getSeriesMarker(String series) {
		return getSeriesMarker(getDataSetIndex(series));
	}
	
	/** Determines whether the series should be drawn with markers
	 * 
	 * @param set the specified series
	 * @return true if markers should be drawn
	 */
	public boolean getSeriesMarker(int set) {
		if(set > properties.size() || set < 0) return false;
		SeriesProperties this_series = (SeriesProperties)properties.get(set);
		return this_series.getMarker();
	}
	
	/** Determines whether the series should be drawn with a line
	 * 
	 * @param series the name of the series
	 * @return true if a line should be drawn
	 */
	public boolean getSeriesLine(String series) {
		return getSeriesLine(getDataSetIndex(series));
	}
	
	/** Determines whether the series should be drawn with a line
	 * 
	 * @param set the specified series
	 * @return true if a line should be drawn
	 */
	public boolean getSeriesLine(int set) {
		if(set > properties.size() || set < 0) return false;
		SeriesProperties this_series = (SeriesProperties)properties.get(set);
		return this_series.getLine();
	}
	
	/** Returns the line style description
	 * 
	 * @param set the series of interest
	 * @return string containing the line style description for use with de.progra.charting.SimpleStrokeDefs
	 */
	public String getSeriesLineStyle(int set) {
		if(set > properties.size()) return null;
		SeriesProperties this_series = (SeriesProperties)properties.get(set);
		return this_series.getLineStyle();
	}
	
	/** Sets the line style description for the series of interest
	 * 
	 * @param value the description string
	 * @param set the series of interest
	 */ 
	public void setSeriesLineStyle(String value, int set) {
		if(set > properties.size()) return;
		SeriesProperties this_series = (SeriesProperties)properties.get(set);
		this_series.setLineStyle(value);
	}
	
	/** Sets the line style description for the series of interest
	 * 
	 * @param value the description string
	 * @param set the series of interest
	 */ 
	public void setSeriesLineStyle(String value, String name) {
		setSeriesLineStyle(value,this.getSeriesIndex(name));
	}
	
	/** Returns the current line style of a series
	 * 
	 * @param name the series name
	 * @return the current line style
	 */
	public String getSeriesLineStyle(String name) {
		return getSeriesLineStyle(this.getSeriesIndex(name));
	}

	/** Removes all data sets from the data model
	 */
	public void clearDataModel() {
		data.clear();
		names.clear();
		properties.clear();
		clearBuffers();
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
        clearBuffers();
    }
	
    /** Clears all the autoscale buffered max/min values
     * 
     */
    private void clearBuffers() {
    	max_x=null;
    	max_y=null;
    	min_x=null;
    	min_y=null;
    }
    
    /** Checks the values of min and max being passed out of the routine
     * for use by autoscaled graphs.
     *
     */
    private void checkConstraintSafety() {
    	if(this.getMaxXValue() <= this.getMinXValue()) {
    		if(min_x.compareTo(max_x) == 0) {
        		if(min_x.doubleValue() == 0) {
        			min_x = new Double (-1.0);
        			max_x = new Double (1.0);
        		} else {
        			min_x = new Double(0.9*min_x.doubleValue());
        			max_x = new Double(1.1*max_x.doubleValue());
        		}
        	}
        	if(min_x.compareTo(max_x) > 0) {
            	Double hold = min_x;
            	min_x = max_x;
            	max_x = hold;
            }
    	}

    	
    	if(this.getMaxYValue() <= this.getMinYValue()) {
    		if(min_y.compareTo(max_y) == 0) {
        		if(min_y.doubleValue() == 0) {
        			min_y = new Double (-1.0);
        			max_y = new Double (1.0);
        		} else {
        			min_y = new Double(0.9*min_y.doubleValue());
        			max_y = new Double(1.1*max_y.doubleValue());
        		}
        	}
        	if(min_y.compareTo(max_y) > 0) {
            	Double hold = min_y;
            	min_y = max_x;
            	max_y = hold;
            }
    	}
    }
}
