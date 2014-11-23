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

    ColorDotPlotDataModel.java
*/

package com.approximatrix.charting.model.threedimensional;

import java.awt.Color;
import java.util.Arrays;
import java.util.TreeSet;

import com.approximatrix.charting.coordsystem.CoordSystem;
import com.approximatrix.charting.model.ChartDataModelConstraints;
import com.approximatrix.charting.model.DefaultChartDataModelConstraints;

/** A Dot Plot data model that stores Z-values directly as Color objects for plotting.  The class also
 * defines the data image as 500x500 by default.  If using this class, the data image size should
 * always be set appropriately.
 * 
 * @author armstrong
 */
public class ColorDotPlotDataModel extends AbstractDotPlotDataModel {
	
	/** The default data image width */
	private static final int DEFAULT_WIDTH = 500;
	
	/** The default data image height */
	private static final int DEFAULT_HEIGHT = 500;
	
	/** The actual data image width */
	private int w = DEFAULT_WIDTH;
	
	/** The actual data image height */
	private int h = DEFAULT_HEIGHT;
	
	/** The x data values */
	protected double[] x = null;
	
	/** The y data values */
	protected double[] y = null;
	
	/** The color values for plotting */
	protected Color[] c = null;
	
	/** Default constructor */
	public ColorDotPlotDataModel() {
		super();
	}
	
	/** Constructor that accepts coordinate data as (int,int,Color).  All int values
	 * passed in as arrays are converted and stored as double values.
	 * 
	 * @param x the array of x data points
	 * @param y the array of y data points
	 * @param color the array of Color objects representing z data
	 */
	public ColorDotPlotDataModel(int x[], int y[], Color color[]) {
		if(x == null || y == null || color == null)
			throw new NullPointerException();
	
		int size = Math.min(x.length, Math.min(y.length,color.length));
		this.x = new double[size]; this.y = new double[size]; this.c = color;
		for(int i=0;i<size;i++) {
			this.x[i] = (double)x[i];
			this.y[i] = (double)y[i];
		}
		
		initialize();
	}
	
	/** Constructor that accepts coordinate data as (double,double,Color).  
	 * 
	 * @param x the array of x data points
	 * @param y the array of y data points
	 * @param color the array of Color objects representing z data
	 */
	public ColorDotPlotDataModel(double x[], double y[], Color color[]) throws NullPointerException {
		if(x == null || y == null || color == null)
			throw new NullPointerException();
		
		this.x = x; this.y = y; this.c = color;
		
		initialize();
		//buildImage(x,y,color);
	}
	
	/** Initializes the model by scanning the data for the largest and smallest values, then
	 * assigning the appropriate constaints.
	 */
	private void initialize() {
		scanRanges(x,y);
		constraints = new DotPlotDataModelConstraints(this, CoordSystem.FIRST_YAXIS);	
	}
	
	/** Sets the resolution of the data image storing the pre-rendered data.  The
	 * values should reflect, ideally, the range of value so that each pixel represents
	 * one increment in the data.
	 * 
	 * @param w the width of the data image
	 * @param h the height of the data image
	 */
	public void setResolution(int w, int h) {
		if(w != this.w || h != this.h)
			super.dataImage = null;
		
		this.w = w;
		this.h = h;
		this.fireChartDataModelChangedEvent(this);
	}
	
	/** Constructs the data image based on the data.
	 * 
	 */
	protected void buildImage() {
		initializeImage(w,h);
		
		int limit = getDataSetLength(0);
		int[] ix = super.getXIndices(x);
		int[] iy = super.getYIndices(y);
		for(int i = 0; i < limit; i++) {
			try {
				dataImage.setRGB(ix[i],iy[i],c[i].getRGB());
			} catch(ArrayIndexOutOfBoundsException abe) {
				continue;
			}
		}
	}
	
	/** Returns the smallest X value */ 
	protected double getFirstColumnValue() {
		return minX;
	}

	/** Returns the largest X value */
	protected double getLastColumnValue() {
		return maxX;
	}

	/** Returns null as this model does not provide ordered values */
	protected TreeSet getOrderedValues(int axis) {
		return null;
	}

	/** Returns a specified X column value */
	public Object getColumnValueAt(int col) {
		return x[col];
	}
	
	/** Returns a specified X column value
	 * 
	 * @param set must be zero - this model only supports a single series
	 * @param the 
	 */
	public Object getColumnValueAt(int set, int col) {
		if(set != 0) throw new IndexOutOfBoundsException("Set 0 is the only valid data set");
		return x[col];
	}

	/** Returns the data set length.  
	 * @param set must be zero - this model only supports a single series
	 */
	public int getDataSetLength(int set) throws IndexOutOfBoundsException {
		if(set != 0) throw new IndexOutOfBoundsException("Set 0 is the only valid data set");
		return Math.min(x.length, Math.min(y.length, c.length));
	}

	/** This model will only ever have a single data set. */
	public int getDataSetNumber() {
		if(x == null || y == null || c == null)
			return 0;
		return 1;
	}

	/** Returns the Color value at a given index.  Note that this does not return the
	 * y value.
	 */ 
	public Number getValueAt(int set, int index) throws IndexOutOfBoundsException {
		if(set != 0) throw new IndexOutOfBoundsException("Set 0 is the only valid data set");
		return new Integer(c[index].getRGB());
	}

	/** Sets a Color at a given data-space coordinate
	 * 
	 * @param x the data space x coordinate
	 * @param y the data space y coordinate
	 * @param value the desired color
	 */ 
	public void setValueAt(int x, int y, Color value) {
		setValueAt((double)x, (double)y, value);
	}
	
	/** Sets a Color at a given data-space coordinate
	 * 
	 * @param x the data space x coordinate
	 * @param y the data space y coordinate
	 * @param value the desired color
	 */
	public void setValueAt(double x, double y, Color value) {
		int newi = getDataSetLength(0);

		double[] newx = new double[newi+1];
		double[] newy = new double[newi+1];
		Color[] newc = new Color[newi+1];
		newx[newi] = x;
		newy[newi] = y;
		newc[newi] = value;
		for(int i=0;i<newi;i++) {
			newx[i] = this.x[i];
			newy[i] = this.y[i];
			newc[i] = this.c[i];
		}
		this.x = newx;
		this.y = newy;
		this.c = newc;
		
		// Need a redraw
		dataImage = null;
		maxX = Math.max(x, maxX);
		maxY = Math.max(y, maxY);
		minX = Math.min(x, minX);
		minY = Math.min(y, minY);
	}

}
