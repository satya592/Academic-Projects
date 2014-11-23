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
 
    CoordSystem.java 
 */

package com.approximatrix.charting.coordsystem;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.text.DecimalFormat;

import com.approximatrix.charting.Axis;
import com.approximatrix.charting.event.ChartDataModelEvent;
import com.approximatrix.charting.model.ChartDataModelConstraints;
import com.approximatrix.charting.render.Renderer;

/** Interface defining a Coordinate System object that both provides
 * calculation functionality for determining data-space to graphics-space
 * transformation as well as actual rendering of axes and tick marks.
 * 
 * @author jarmstrong
 */
public interface CoordSystem extends Renderer {

	/** Sets whether a grid is painted.
	 * @param value whether the grid should be painted
	 */
	public abstract void setPaintGrid(boolean value);

	/** Returns whether a grid is painted.
	 */
	public abstract boolean getPaintGrid();

	/** Sets the x-axis.
	 * @param a the x-axis
	 */
	public abstract void setXAxis(Axis a);

	/** Returns the x axis.
	 * @return the x-axis
	 */
	public abstract Axis getXAxis();

	/** Sets the x-axis unit string.
	 * @param xtext the unit string
	 */
	public abstract void setXAxisUnit(String xunit);

	/** Gets the x-axis unit string.
	 * @return the label String
	 */
	public abstract String getXAxisUnit();

	/** Sets the y-axis unit string.
	 * @param ytext the unit string
	 */
	public abstract void setYAxisUnit(String yunit);

	/** Gets the y-axis label.
	 * @return the label String
	 */
	public abstract String getYAxisUnit();

	/** Sets the font for the axis tick labels.
	 * @param f the Font to be used
	 * 
	 * @deprecated deprecated in version 1.4 - use setTickFont()
	 */
	public abstract void setFont(Font f);

	/** Returns the font used for the axis tick labels.
	 * @return the Font object
	 * 
	 * @deprecated deprecated in version 1.4 - use getTickFont()
	 */
	public abstract Font getFont();

	/** Sets the font for the axis tick labels.
	 * @param f the Font to be used
	 */
	public abstract void setTickFont(Font f);

	/** Returns the font used for the axis tick labels.
	 * @return the Font object
	 */
	public abstract Font getTickFont();
	
	/** Sets the font for the axis unit labels.
	 * @param f the Font to be used
	 */
	public abstract void setUnitFont(Font f);

	/** Returns the font used for the axis unit labels.
	 * @return the Font object
	 */
	public abstract Font getUnitFont();
	
	/** Sets the left y-axis and computes the matrix transformation.
	 * @param a the left y-axis
	 */
	public abstract void setFirstYAxis(Axis a);

	/** Returns the first y-axis.
	 * @return the left y-axis
	 */
	public abstract Axis getFirstYAxis();

	/** Sets the second y-axis and computes the matrix transformation.
	 * @param a the right y-axis
	 */
	public abstract void setSecondYAxis(Axis a);

	/** Returns the second y-axis.
	 * @return the right y-axis
	 */
	public abstract Axis getSecondYAxis();

	/** Returns the inner margin, ie the bounds minus the margins.
	 * @return a Rectangle object defining the inner bounds.
	 */
	public abstract Rectangle getInnerBounds();

	/** Computes all margins, initializes the length of the Axis and
	 * calls <code>super.setBounds</code>. Additionally, it sets the
	 * default AffineTransforms for every y-axis.
	 * @param bounds <CODE>Rectangle</CODE> object defining the bounds
	 */
	public abstract void setBounds(Rectangle bounds);

	/** Returns the preferred size needed for the renderer.
	 * @return a Dimension with the minimum Integer values.
	 */
	public abstract Dimension getPreferredSize();

	/** Overrides the method to just call <code>paintDefault</code>.
	 * @param g the <CODE>Graphics2D</CODE> object to paint in
	 */
	public abstract void render(Graphics2D g);

	/** This method is called by the paint method to do the actual painting.
	 * The painting is supposed to start at point (0,0) and the size is
	 * always the same as the preferred size. The paint method performs
	 * the possible scaling.
	 * @param g the <CODE>Graphics2D</CODE> object to paint in
	 */
	public abstract void paintDefault(Graphics2D g);
	
	/** Returns the DecimalFormat used on the Yaxis */
	public abstract DecimalFormat getYDecimalFormat();

	/** Returns the DecimalFormat used on the Xaxis */
	public abstract DecimalFormat getXDecimalFormat();
	
	/** Returns the DecimalFormat used on the Yaxis */
	public abstract void setYDecimalFormat(DecimalFormat df);

	/** Returns the DecimalFormat used on the Xaxis */
	public abstract void setXDecimalFormat(DecimalFormat df);

	/** if true only the tick will be painted on the yaxis.  Alternately a 
	 * light grey line will paint across the background of the chart.
	 * @deprecated deprecated as of version 1.4 
	 */
	public abstract boolean isPaintOnlyTick();

	/** if true, labels will be painted for each axis
	 * 
	 * @return true if labels will be painted, false otherwise
	 */
	public abstract boolean isPaintLabels();

	/** if true, a grid will be painted on the chart
	 * 
	 * @return true if grid will be painted, false otherwise
	 */
	public abstract boolean isPaintGrid();
	
	/** Sets whether labels should be painted for the axes
	 * 
	 * @param label true to paint labels, false otherwise
	 */
	public abstract void setPaintLabels(boolean label);

	/** Returns whether axes will be painted.  If true, the axes, at minimum,
	 * will be painted.  If false, all ClassicCoordSystem painting is skipped regardless
	 * of other settings.
	 * 
	 * @return true if axes are to be painted, false otherwise
	 */
	public abstract boolean isPaintAxes();

	/** Sets whether the axes will be painted.  If set to false, the ClassicCoordSystem
	 * object will perform no painting whatsoever, regardless of other settings.
	 * 
	 * @param axes true to paint axes, false otherwise
	 */
	public abstract void setPaintAxes(boolean axes);

	/** Sets the maximum number of ticks along the X axis that will be allowed. 
	 * 
	 * @param value the maximum number of X ticks, or -1 to reset to default
	 */
	public abstract void setMaximumXTicks(int value);

	/** Sets the maximum number of ticks along the Y axis that will be allowed. 
	 * 
	 * @param value the maximum number of Y ticks, or -1 to reset to default
	 */
	public abstract void setMaximumYTicks(int value);

	/** Returns the maximum number of X ticks to draw on the X axis
	 * 
	 * @return the maximum number of X ticks to draw
	 */
	public abstract int getMaximumXTicks();

	/** Returns the maximum number of Y ticks to draw on the Y axis
	 * 
	 * @return the maximum number of Y ticks to draw
	 */
	public abstract int getMaximumYTicks();

	/**  Resets the maximum number of ticks along the X axis to the default value
	 */
	public abstract void resetMaximumXTicks();

	/**  Resets the maximum number of ticks along the X axis to the default value
	 */
	public abstract void resetMaximumYTicks();
	
	/** the axis binding constant for the first y-axis
	 */
	public static final int FIRST_YAXIS = 0;

	/** the axis binding constant for the second y-axis
	 */
	public static final int SECOND_YAXIS = 1;

	/** Sets the coordinate transformation for any y-coordinate.
	 * @param at the AffineTransform that transforms the coordinates into pixel
	 * space
	 * @axis defines for which y-axis the transform is computed
	 */
	public abstract void setTransform(AffineTransform at, int axis);

	/** Returns the currently defined AffineTransform for any y-axis.
	 * @param axis the y-axis to be used.
	 */
	public abstract AffineTransform getTransform(int axis);

	/** This method computes the default transform which transforms the
	 * user space coordinates of this coordinate system to the pixel
	 * space coordinates used in the Graphics object.
	 * All rendering in the CoordinateSystem and the ChartRenderers
	 * will rely on this transform.
	 * @param axis defines which y-axis to use.
	 */
	public abstract AffineTransform getDefaultTransform(int axis);

	/** Returns the used ChartDataModelConstraints. */
	public abstract ChartDataModelConstraints getChartDataModelConstraints(
			int axis);

	/** Listener to force the recomputation of all transforms on data changes
	 * @param evt the event that caused the data change
	 * @see com.approximatrix.charting.event.ChartDataModelListener#chartDataChanged(com.approximatrix.charting.event.ChartDataModelEvent)
	 */
	public abstract void chartDataChanged(ChartDataModelEvent evt);
	
	/** Forces the coordinate system to place the label centered between
	 * tick marks (for bar charts for example).
	 * 
	 * @param value true to center, false otherwise
	 */
	public abstract void setCenterLabelsBetweenTicks(boolean value);
	
	/** Returns true if labels are centered between ticks.
	 * 
	 * @return true if centered between ticks, false if centered on ticks
	 */ 
	public abstract boolean isCenterLabelsBetweenTicks();
}