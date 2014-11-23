/*
    OpenChart2 Java Charting Library and Toolkit
    Copyright (C) 2005-2009 Approximatrix, LLC
    Copyright (C) 2001  Sebastian Muller
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

    AbstractDotPlotDataModel.java
*/

package com.approximatrix.charting.model.threedimensional;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.awt.image.RasterFormatException;

import com.approximatrix.charting.coordsystem.CoordSystem;
import com.approximatrix.charting.model.AbstractChartDataModel;
import com.approximatrix.charting.model.ChartDataModelConstraints;
import com.approximatrix.charting.model.IntegratedImageModel;

/** Basis for models containing three dimensional scatter data (x,y,z) for use in
 * Dot Plots, where Z values are simply represented by a color scale.   This abstract
 * implementation provides some data model necessities, stores the "image" of the
 * data, and handles scanning for the largest observed x,y values.
 * 
 * @author armstrong
 */
public abstract class AbstractDotPlotDataModel extends AbstractChartDataModel implements IntegratedImageModel {

	/** Image of the data ready for rendering */
	protected BufferedImage dataImage = null;
	
	/** The smallest X value */
	protected double minX = 0;
	
	/** The largest X value */
	protected double maxX = 0;
	
	/** The smallest Y value */
	protected double minY = 0;
	
	/** The largest Y value */
	protected double maxY = 0;
	
	/** The data model constraints */
	protected ChartDataModelConstraints constraints = null;
	
	/** Default constructor that enables autoscaling immediately */
	protected AbstractDotPlotDataModel() {
		this.autoscale = true;
	}
	
	/** Returns the complete image of the data
	 * 
	 * @return the Image of the data, or null if it has not yet been created
	 */
	public BufferedImage getImage() {
		return dataImage;
	}
	
	/** Returns a copy of the data image for plotting.  If manual scaling is enabled,
	 * a subimage is sampled and returned.
	 * 
	 * @return the viewable image
	 */
	public Image getViewableImage() {
		if(dataImage == null)
			buildImage();
		
		if(this.autoscale)
			return dataImage;
		else {
			

			boolean flipx = false;
			boolean flipy = false;

			int pminx = this.getXIndex(minX);
			int pmaxx = this.getXIndex(maxX);

			int pminy = this.getYIndex(minY);
			int pmaxy = this.getYIndex(maxY);

			try {
				pminx = this.getXIndex(this.getManualMinimumX().doubleValue());
				pmaxx = this.getXIndex(this.getManualMaximumX().doubleValue());

				pminy = this.getYIndex(this.getManualMinimumY().doubleValue());
				pmaxy = this.getYIndex(this.getManualMaximumY().doubleValue());
				
			} catch(NullPointerException npe) {
				//This should only ever occur if manual scaling hasn't been set
			}

			// The y coordinates need to shift one up because the image is drawn
			// incorrectly on scaling otherwise.
			pminy++; pmaxy++;
			
			if(pmaxx < pminx) {
				flipx = true;
				pminx = dataImage.getWidth()-pminx;
				pmaxx = dataImage.getWidth()-pmaxx;
			}

			if(pminy < pmaxy) {
				flipy = true;
				pminy = dataImage.getHeight()-pminy;
				pmaxy = dataImage.getHeight()-pmaxy;
			}


			BufferedImage ret = dataImage;
			if(flipx) {
				AffineTransform tx = AffineTransform.getScaleInstance(-1, 1);
				tx.translate(-ret.getWidth(null),0);
				AffineTransformOp op = new AffineTransformOp(tx, AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
				ret = op.filter(ret, null);
			}
			if(flipy) {
				AffineTransform tx = AffineTransform.getScaleInstance(1, -1);
				tx.translate(0, -ret.getHeight(null));
				AffineTransformOp op = new AffineTransformOp(tx, AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
				ret = op.filter(ret, null);
			}
			
			try {
				ret = ret.getSubimage(pminx, pmaxy, Math.abs(pmaxx-pminx), Math.abs(pmaxy-pminy));
			} catch (RasterFormatException rfe) {
				int imw = Math.abs(pmaxx-pminx);
				int imh = Math.abs(pmaxy-pminy);
				imw = Math.max(imw, 1);
				imh = Math.max(imh, 1);
				
				BufferedImage rebuilt = new BufferedImage(imw,imh,dataImage.getType());
				Graphics g = rebuilt.getGraphics();

				try {
					int newx = Math.max(0,pminx);
					int newy = Math.max(0,pmaxy);
					
					int w = Math.abs(newx-Math.min(pmaxx, ret.getWidth()));
					int h = Math.abs(newy-Math.min(pminy, ret.getHeight()));
					
					Image sub = ret.getSubimage(newx, newy, w, h);
					g.drawImage(sub,Math.abs(Math.min(pminx,0)),Math.abs(Math.min(pmaxy,0)),null);
					ret = rebuilt;
				} catch (RasterFormatException rfepossibly) { 
					System.err.println("Error in AbstractDotPlotDataModel");
					rfepossibly.printStackTrace();
					ret = null;
				}
			}

			return ret;
		}
		
	}
	
	/** Conversion method to determine the data image X-index based
	 * on a double X value in data space
	 * 
	 * @param datavalue a data-space x value
	 * @return the equivalent image coordinate
	 */
	public int getXIndex(double datavalue) {
		//return getIndex(datavalue, minX, maxX, dataImage.getWidth());
		double[] values = new double[1];
		values[0] = datavalue;
		return getXIndices(values)[0];
	}
	
	/** Conversion method to determine the data image Y-index based
	 * on a double Y value in data space
	 * 
	 * @param datavalue a data-space y value
	 * @return the equivalent image coordinate
	 */
	public int getYIndex(double datavalue) {
		double[] values = new double[1];
		values[0] = datavalue;
		return getYIndices(values)[0];
	}
	
	/** Private conversion method to perform data-space to image-space conversions
	 * 
	 * @param datavalue the data space value
	 * @param minval the minimum data-space value along a dimension of interest
	 * @param maxval the maximum data-space value along a dimension of interest
	 * @param size the image size along a dimension of interest
	 * @return the equivalent image coordinate
	 */
	private int getIndex(double datavalue, double minval, double maxval, int size) {
		double range = maxval-minval;
		if(range==0.0) return 0;
		return (int)((datavalue/range)*(double)size);
	}
	
	/** Conversion method to determine the data image X-indices based
	 * on an array of double X values in data space
	 * 
	 * @param datavalue an array data-space x values
	 * @return the equivalent image coordinates
	 */ 
	public int[] getXIndices(double datavalue[]) {
		return getIndices(datavalue, minX, maxX, dataImage.getWidth());
	}
	
	/** Conversion method to determine the data image Y-indices based
	 * on an array of double Y values in data space
	 * 
	 * @param datavalue an array data-space y values
	 * @return the equivalent image coordinates
	 */ 
	public int[] getYIndices(double datavalue[]) {
		return getIndices(datavalue, maxY, minY, dataImage.getHeight());
	}
	
	/** Private conversion method to perform data-space to image-space conversions.
	 * All calculations are implemented here locally to avoid repetive math in
	 * calling getIndex().
	 * 
	 * @param datavalue the array data space values
	 * @param minval the minimum data-space value along a dimension of interest
	 * @param maxval the maximum data-space value along a dimension of interest
	 * @param size the image size along a dimension of interest
	 * @return an array of the equivalent image coordinates
	 */
	private int[] getIndices(double datavalue[], double minval, double maxval, int size) throws NullPointerException {
		if(datavalue == null) throw new NullPointerException();
		
		double range = maxval-minval;
		if(range==0.0) return null;
		double multiplier = ((double)size)/range;
		
		int[] value = new int[datavalue.length];
		for(int i=0; i<datavalue.length; i++) {
			double val = (datavalue[i]-minval)*multiplier;			
			if(val < 0.0)
				value[i] = (int)Math.ceil(val);
			else
				value[i] = (int)Math.floor(val);
			//value[i] = (int)((datavalue[i]-minval)*multiplier);
		}
		return value;
	}
	
	/** Scans through arrays of x and y data to determine the largest
	 * and smallest values encountered and stores them appropriately.
	 * 
	 * @param x the array of x data values
	 * @param y the array of y data values
	 */
	protected void scanRanges(double x[], double y[]) {
		maxX = Double.MIN_VALUE; minX = Double.MAX_VALUE;
		maxY= Double.MIN_VALUE; minY = Double.MAX_VALUE;
		
		for(int i = 0; i < x.length && i < y.length; i++) {
			maxX = Math.max(maxX, x[i]);
			minX = Math.min(minX, x[i]);
			maxY = Math.max(maxY, y[i]);
			minY = Math.min(minY, y[i]);
		}
	}
	
	/** Initializes the data image to a specified width and height.  Image is by
	 * default blank.
	 * 
	 * @param w the width of the data image
	 * @param h the height of the data image
	 */
	protected void initializeImage(int w, int h) {
		dataImage = new BufferedImage(w,h,BufferedImage.TYPE_INT_ARGB);
	}
	
	/** Constructs image from the data */
	protected abstract void buildImage();
	
	/** Assigns the chart data model constraints to this data model.
	 * 
	 */
	public void setChartDataModelConstraints(int axis, ChartDataModelConstraints constraints) {
		if(axis == CoordSystem.FIRST_YAXIS)
			this.constraints = constraints;
	}
	
	/** Returns the chart data model constraints */
	public ChartDataModelConstraints getChartDataModelConstraints(int axis) {
		if(axis == CoordSystem.FIRST_YAXIS)
			return this.constraints;
		
		return null;
	}
	
	/** Returns the largest x value in the data model.
	 * 
	 * @return the largest x value
	 */
	public double getModelMaximumX() {
		return maxX;
	}
	
	/** Returns the smallest x value in the data model.
	 * 
	 * @return the smallest x value
	 */
	public double getModelMinimumX() {
		return minX;
	}
	
	/** Returns the largest y value in the data model.
	 * 
	 * @return the largest y value
	 */
	public double getModelMaximumY() {
		return maxY;
	}
	
	/** Returns the smallest y value in the data model.
	 * 
	 * @return the smallest y value
	 */
	public double getModelMinimumY() {
		return minY;
	}
	
	/** Returns true because this model only uses numeric
	 * x-axis
	 * 
	 * @return true always
	 */
	public boolean isColumnNumeric() {
    	return true;
    }
}
