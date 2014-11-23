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

    ColorScaleDotPlotDataModel.java
*/

package com.approximatrix.charting.model.threedimensional;

import com.approximatrix.charting.render.colorscale.ColorScale;

/** Extension of ColorDotPlotModel that allows the assignment of
 * true numeric Z data to be input along with a ColorScale object
 * to determine how the Z data will be plotted.  The actual Z data
 * is maintained in the model.  All Z data is stored as double
 * values.
 * 
 * @author armstrong
 */
public class ColorScaleDotPlotDataModel extends ColorDotPlotDataModel {

	/** The array of z data */
	protected double[] z = null;
	
	/** The largest Z value */
	protected double maxZ = 1.0;
	
	/** The smallest Z value */
	protected double minZ = 0.0;
	
	/** Constructor that accepts data as three double arrays and a
	 * valid ColorScale object
	 * 
	 * @param x the x-axis data
	 * @param y the y-axis data
	 * @param z the z-axis data
	 * @param scale a ColorScale object to use when drawing the chart
	 */
	public ColorScaleDotPlotDataModel(double x[], double y[], double z[], ColorScale scale) {
		super(x,y,scale.getColors(z));
		this.z = z;
		scanRanges();
	}
	
	/** Constructor that accepts data as three integer arrays and a
	 * valid ColorScale object.  The integer data is converted into
	 * double values.
	 * 
	 * @param x the x-axis data
	 * @param y the y-axis data
	 * @param z the z-axis data
	 * @param scale a ColorScale object to use when drawing the chart
	 */
	public ColorScaleDotPlotDataModel(int x[], int y[], int z[], ColorScale scale) {
		super(x,y,scale.getColors(z));
		this.z = toDouble(z);
		scanRanges();
	}
	
	/** Constructor that accepts data as two integer arrays for x and
	 * y coordinates and an array of doubles for z data along with
	 * valid ColorScale object.  The integer data is converted into
	 * double values.
	 * 
	 * @param x the x-axis data
	 * @param y the y-axis data
	 * @param z the z-axis data
	 * @param scale a ColorScale object to use when drawing the chart
	 */
	public ColorScaleDotPlotDataModel(int x[], int y[], double z[], ColorScale scale) {
		super(x,y,scale.getColors(z));
		this.z = z;
		scanRanges();
	}
	
	/** Constructor that accepts data as two double arrays for x and
	 * y coordinates and an array of integers for z data along with
	 * valid ColorScale object.  The integer data is converted into
	 * double values.
	 * 
	 * @param x the x-axis data
	 * @param y the y-axis data
	 * @param z the z-axis data
	 * @param scale a ColorScale object to use when drawing the chart
	 */
	public ColorScaleDotPlotDataModel(double x[], double y[], int z[], ColorScale scale) {
		super(x,y,scale.getColors(z));
		this.z = toDouble(z);
		scanRanges();
	}
	
	/** Small method to convert integer arrays to double arrays.
	 * 
	 * @param z the integer data to convert
	 * @return a double array
	 */
	private double[] toDouble(int z[]) {
		if(z == null) return null;
		double[] ret = new double[z.length];
		for(int i=0;i<ret.length;i++)
			ret[i] = (double)z[i];
		return ret;
	}
	
	/** Override of the scanRanges() method to allow for scanning the
	 * Z axis data as well.
	 */
	protected void scanRanges() {
		super.scanRanges(this.x, this.y);
		for(int i=0;i<this.z.length;i++) {
			this.maxZ = Math.max(maxZ,this.z[i]);
			this.minZ = Math.min(minZ,this.z[i]);
		}
	}
	
}
