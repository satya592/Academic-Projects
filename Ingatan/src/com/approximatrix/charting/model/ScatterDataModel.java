/*
    OpenChart2 Java Charting Library and Toolkit
    Copyright (C) 2005-2007 Approximatrix, LLC
    Copyright (C) 2001  Sebastian Müller
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

import com.approximatrix.charting.model.*;

/** Implements an EditableChartDataModel that can be used for plotting
 * data that shares the same X-axis points.  Each series in the 
 * ScatterDataModel also retains the series plot properties, including
 * line style, color, and marker type.
 *
 * @author armstrong
 */
public class ScatterDataModel extends EditableChartDataModel {

	/** Arraylist containing a SeriesProperties classes, one for each
	 * data set */
	ArrayList data_properties = null;
	
	/** Creates a new ScatterDataModel with default series properties
	 * @param model an array containing one series along each row
	 * @param columns x-axis (independent) values corresponding to each column of model
	 * @param rows String array containing the name of each series
	 */
	public ScatterDataModel(double[][] model, double[] columns, String[] rows) {
		super(model,columns,rows);
		
		data_properties = new ArrayList();
		for(int i=0;i<rows.length;i++) {
			data_properties.add(new SeriesProperties());
		}				
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
			SeriesProperties this_series = (SeriesProperties)data_properties.get(i);
			if(on_or_off) {
				this_series.setMarkerOn();
			} else {
				this_series.setMarkerOff();
			}
		}
	}
	
	/** Set the series line, specified by name, to be either displayed or 
	 * hidden
	 * @param name line series name
	 * @param on_or_off true to display the line, false otherwise
	 */
	public void setSeriesLine(String name, boolean on_or_off) {
		int i;
		for(i=0;i<this.getDataSetNumber();i++) {
			if(name.compareTo(this.getDataSetName(i))==0) {
				break;
			}
		}
		if(i<this.getDataSetNumber()) {
			SeriesProperties this_series = (SeriesProperties)data_properties.get(i);
			if(on_or_off) {
				this_series.setLineOn();
			} else {
				this_series.setLineOff();
			}
		}
	}

	/** Returns whether the specified line series displays a marker
	 * @param set an integer specifying which series to inquire about
	 * @return true if marker is visible, false otherwise
	 */
	public boolean getSeriesMarker(int set) {
		SeriesProperties this_series = (SeriesProperties)data_properties.get(set);
		return this_series.getMarker();
	}
	
	/** Returns whether the specified line series displays a drawn line
	 * @param set an integer specifying which series to inquire about
	 * @return true if line is visible, false otherwise
	 */
	public boolean getSeriesLine(int set) {
		SeriesProperties this_series = (SeriesProperties)data_properties.get(set);
		return this_series.getLine();
	}
	
	/** Returns whether the specified line series displays a marker
	 * @param name the name of the series to inquire about
	 * @return true if marker is visible, false otherwise
	 */
	public boolean getSeriesMarker(String name) {
		int i;
		for(i=0;i<this.getDataSetNumber();i++) {
			if(name.compareTo(this.getDataSetName(i))==0) {
				break;
			}
		}
		if(i<this.getDataSetNumber()) {
			SeriesProperties this_series = (SeriesProperties)data_properties.get(i);
			return this_series.getMarker();
		} else {
			return false;
		}
	}
	
	/** Returns whether the specified line series displays a its line
	 * @param name the name of the series to inquire about
	 * @return true if line is visible, false otherwise
	 */
	public boolean getSeriesLine(String name) {
		int i;
		for(i=0;i<this.getDataSetNumber();i++) {
			if(name.compareTo(this.getDataSetName(i))==0) {
				break;
			}
		}
		if(i<this.getDataSetNumber()) {
			SeriesProperties this_series = (SeriesProperties)data_properties.get(i);
			return this_series.getLine();
		} else {
			return false;
		}
	}
	
	/** Simple private class that is used to store the drawing properties
	 * for a ScatterDataModel series.  All interfacing with the class
	 * objects is performed with ScatterDataModel.
	 *
	 * @author armstrong
	 */
	private class SeriesProperties {
		
		/** Whether a marker is shown when drawing the series */
		private boolean show_marker;
		
		/** Whether a line is shown when drawing the series */
		private boolean show_line;
		
		/** Initializes the properties object, setting no markers and
		 * line drawing
		 */
		public SeriesProperties() {
			show_marker = false;
			show_line = true;
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
	
}
