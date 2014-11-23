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
 
    ClassicCoordSystem.java
    Created on 26. Juni 2001, 22:49
 */

package com.approximatrix.charting.coordsystem;

import java.awt.geom.Line2D;
import java.awt.geom.AffineTransform;
import java.awt.Graphics2D;
import java.awt.Color;
import java.awt.font.FontRenderContext;
import java.awt.font.TextLayout;
import java.text.DecimalFormat;

import com.approximatrix.charting.Axis;
import com.approximatrix.charting.coordsystem.ClassicCoordSystemUtilities;
import com.approximatrix.charting.event.ChartDataModelEvent;
import com.approximatrix.charting.event.ChartDataModelListener;
import com.approximatrix.charting.model.ChartDataModel;
import com.approximatrix.charting.model.ChartDataModelConstraints;

/** This class defines a coordinate system. The ClassicCoordSystem class computes
 * an AffineTransform for each y-axis, which translates the user space
 * coordinates (ie. the data value coordinates) into pixel space coordinates.
 * These AffineTransform classes make the PixelToPointTranslator obsolete,
 * since it provides more flexibility. <code>getDefaultTransform</code> always
 * computes the default transformation, whereas you can set another
 * transformation via <code>setTransform</code>. This will be used to implement
 * zooming and panning in the Swing classes.<p>
 * All classes incl. this one, which render data will use the transformations
 * to translate the coordinates. The transformations are not set up on
 * instantiation of a ClassicCoordSystem, instead they're computed when setBounds
 * is called, because they need this information of course. Afterwards you
 * can set your own transformation or even better you can manipulate the
 * existing ones by pre- or postconcatenating another AffineTransform.
 * 
 */
public class ClassicCoordSystem extends AbstractCoordSystem implements ChartDataModelListener {
    
    /** FontRenderContext used througout the ClassicCoordSystem*/
    protected final FontRenderContext frc = new FontRenderContext(null, false, false);
    
    /** if true, the arrows will be drawn at the end of the axi*/
    protected boolean shouldDrawArrows = true;
    /** if true, the increment will be painted at each tick mark*/ 
    protected boolean shouldPaintAltTick = true;

    
	/** The minimal margin constant. */
    public final int MINIMALMARGIN = 20;
    
	/** The arrow length constant. */
    public final int ARROWLENGTH = 15;
    
    /** The ChartDataModel constraints of the first y-axis and the x-axis. */
    protected ChartDataModelConstraints constraints;
	/** The ChartDataModel constraints of the second y-axis and the x-axis. */
    protected ChartDataModelConstraints constraints2;
    
	/** The DataModel class. */
    protected ChartDataModel model;
    
	/** The utilities class, which contains all the rendering methods etc. */
    protected ClassicCoordSystemUtilities c;
    
    
    /** Creates a new ClassicCoordSystem using the given model constraints.
     * Also creates default linear x and y-axis. Note that the length
     * of the axis are set on the first call to
     * setBounds().
     * @param c the ChartDataModel needed to compute the DataConstraints.
     */
    public ClassicCoordSystem(ChartDataModel cdm) {
        this.constraints = cdm.getChartDataModelConstraints(FIRST_YAXIS);
        this.constraints2 = cdm.getChartDataModelConstraints(SECOND_YAXIS);
        
        this.model = cdm;
        
        xaxis = new Axis(Axis.HORIZONTAL, constraints);
        yaxis = new Axis(Axis.VERTICAL, constraints);
        
        c = new ClassicCoordSystemUtilities(this, constraints, constraints2, model);
		
		dfY = new DecimalFormat();
        dfX = new DecimalFormat();
        
        cdm.addChartDataModelListener(this);
        
        centerObjectLabelsBetweenTicks = !cdm.isColumnNumeric();
    }
    
    /** Creates a new ClassicCoordSystem using the given model constraints.
     * Also creates default linear x and y-axis. Note that the length
     * of the axis are set on the first call to
     * setBounds().
     * @param c the ChartDataModel needed to compute the DataConstraints.
     * @param xtext the x-axis unit
     * @param ytext the y-axis unit
     */
    public ClassicCoordSystem(ChartDataModel c, String xunit, String yunit) {
        this(c);

        setXAxisUnit(xunit);
        setYAxisUnit(yunit);
        
        c.addChartDataModelListener(this);
        centerObjectLabelsBetweenTicks = !c.isColumnNumeric();
    }
	
	/**
     * Create a new ClassicCoordSystem with alternate painting parameters.
     * @param c the ChartDataModel needed to compute the DataConstraints.
     * @param drawArrows if true the arrows will be drawn at the end of the axis
     * @param paintAltYTick if true the caption will paint on alternate ticks of the 
     * yaxis instead of on every one.
     * @param paintOnlyYTick if true the horizontal lightgray line will <i>not</i>
     * appear behind the chart at each yaxis tick mark.
     */
    public ClassicCoordSystem(ChartDataModel c, DecimalFormat yAxisFormat, 
            boolean drawArrows, boolean paintAltYTick, boolean paintOnlyYTick) {
        this(c);
        dfY = yAxisFormat;
        shouldDrawArrows = drawArrows;
        shouldPaintAltTick = paintAltYTick;
        shouldPaintOnlyTick = paintOnlyYTick;
        
        c.addChartDataModelListener(this);
        centerObjectLabelsBetweenTicks = !c.isColumnNumeric();
    }

    
    
    /** if true, the increment will be painted at each tick mark*/
	public boolean isPaintAltTick() {
		return shouldPaintAltTick;
	}
    
    /* (non-Javadoc)
	 * @see com.approximatrix.charting.RenderingCoordSystem#paintDefault(java.awt.Graphics2D)
	 */
    public void paintDefault(Graphics2D g) {
    	
    	// If we shouldn't paint axes, then we shouldn't paint anything
    	// at all.
    	if(!shouldPaintAxes)
    		return;
    	
        g.setColor(Color.black);
        
        Line2D x = c.getXAxisLine2D();
        Line2D y = c.getYAxisLine2D();
        
        g.draw(x);
        g.draw(y);
        
        // Draw the grids first
        if(shouldPaintGrid) {
        	c.drawYAxisGrid(g);
        	if(model.isColumnNumeric()) c.drawNumericalXAxisGrid(g);
        }
        
        // draw X-Axis Arrow
		if(shouldDrawArrows) {
			g.drawLine((int)x.getX2(), (int)x.getY2(), (int)x.getX2() + ARROWLENGTH, (int)x.getY2());
			g.fillPolygon(new int[] {(int)(x.getX2() + ARROWLENGTH / 3.0),
						(int)(x.getX2() + ARROWLENGTH / 3.0),
						(int)(x.getX2() + ARROWLENGTH)},
						new int[] {(int)x.getY2() - 3, (int)x.getY2() + 3, (int)x.getY2()},
						3);
		}
        
        // draw X-Axis label right below the Arrow ?!
        g.setColor(Color.black);
        TextLayout layoutX = new TextLayout(getXAxisUnit(), getUnitFont(), 
                                           new FontRenderContext(null, true, false));
		layoutX.draw(g, (float)x.getX2() + (float)ARROWLENGTH / 3,  (float)x.getY2() + (float)layoutX.getBounds().getHeight() + 5);
        
        // draw Y-Axis Arrow
		if(shouldDrawArrows) {
			g.drawLine((int)y.getX1(), (int)y.getY1(), (int)y.getX1(), (int)y.getY1() - ARROWLENGTH);
			g.fillPolygon(new int[] {(int)(y.getX1() - 3),
						(int)(y.getX1() + 3),
						(int)(y.getX1())},
						new int[] {(int)(y.getY1() - ARROWLENGTH / 3.0),
						(int)(y.getY1() - ARROWLENGTH / 3.0),
						(int)y.getY1() - ARROWLENGTH},
						3);
		}

        // draw Y-Axis label right below the Arrow ?!
        g.setColor(Color.black);
        TextLayout layoutY = new TextLayout(getYAxisUnit(), getUnitFont(), 
                                           new FontRenderContext(null, true, false));
        
        //leftbuffer
        layoutY.draw(g, (float)y.getX1()-6-(float)layoutY.getBounds().getWidth(), 
                        (float)y.getY1() - layoutX.getDescent() - 3);
        
        if(getSecondYAxis() != null) {
            Line2D y2 = c.getSecondYAxisLine2D();
            g.draw(y2);
        }
        
        if(model.isColumnNumeric())
            c.drawNumericalXAxisTicks(g);
        else
            c.drawXAxisTicks(g);
        
        c.drawYAxisTicks(g);

    }
	
	/** Returns the FontRenderContext used througout the ClassicCoordSystem*/
	public FontRenderContext getFontRenderContext() {
		return frc;
	}
	
    /* (non-Javadoc)
	 * @see com.approximatrix.charting.RenderingCoordSystem#getYDecimalFormat()
	 */
    public DecimalFormat getYDecimalFormat() {
		return dfY;
	}
	
    /* (non-Javadoc)
	 * @see com.approximatrix.charting.RenderingCoordSystem#getXDecimalFormat()
	 */
	public DecimalFormat getXDecimalFormat() {
		return dfX;
	}
    
    /** if true, the arrows will be drawn at the end of the axis*/
	public boolean isDrawArrows() {
		return shouldDrawArrows;
	}
    

    
    /* (non-Javadoc)
	 * @see com.approximatrix.charting.RenderingCoordSystem#getChartDataModelConstraints(int)
	 */
    public ChartDataModelConstraints getChartDataModelConstraints(int axis) {
        if(axis == FIRST_YAXIS)
            return constraints;
        else if(axis == SECOND_YAXIS)
            return constraints2;
        else
            return null;
    }
    
    /* (non-Javadoc)
	 * @see com.approximatrix.charting.RenderingCoordSystem#setMaximumXTicks(int)
	 */
    public void setMaximumXTicks(int value) {
    	if(c == null) return;
    	c.setMaximumXTicks(value);
        super.fireRenderChangeEvent();
    }
    
    /* (non-Javadoc)
	 * @see com.approximatrix.charting.RenderingCoordSystem#setMaximumYTicks(int)
	 */
    public void setMaximumYTicks(int value) {
    	if(c == null) return;
    	c.setMaximumYTicks(value);
        super.fireRenderChangeEvent();
    }
    
    /* (non-Javadoc)
	 * @see com.approximatrix.charting.RenderingCoordSystem#getMaximumXTicks()
	 */ 
    public int getMaximumXTicks() {
    	if(c==null) return 0;
    	return c.getMaximumXTicks();
    }
 
    /* (non-Javadoc)
	 * @see com.approximatrix.charting.RenderingCoordSystem#getMaximumYTicks()
	 */ 
    public int getMaximumYTicks() {
    	if(c==null) return 0;
    	return c.getMaximumYTicks();
    }
    
    /* (non-Javadoc)
	 * @see com.approximatrix.charting.RenderingCoordSystem#resetMaximumXTicks()
	 */
    public void resetMaximumXTicks() {
    	if(c == null) return;
    	c.setMaximumXTicks(-1);
    }
    
    /* (non-Javadoc)
	 * @see com.approximatrix.charting.RenderingCoordSystem#resetMaximumYTicks()
	 */
    public void resetMaximumYTicks() {
    	if(c == null) return;
    	c.setMaximumYTicks(-1);
    }

	/* (non-Javadoc)
	 * @see com.approximatrix.charting.RenderingCoordSystem#chartDataChanged(com.approximatrix.charting.event.ChartDataModelEvent)
	 */
	public void chartDataChanged(ChartDataModelEvent evt) {
		setTransforms();
	}

	protected int computeBottomMargin() {
		return c.computeBottomMargin();
	}

	protected int computeLeftMargin() {
		return c.computeLeftMargin();
	}

	protected int computeRightMargin() {
		return c.computeRightMargin();
	}

	protected int computeTopMargin() {
		return c.computeTopMargin();
	}

	@Override
	public void setCenterLabelsBetweenTicks(boolean value) {
		if(model.isColumnNumeric() && value)
			throw new IllegalArgumentException("ClassicCoordSystem does not support centering numeric X-axis labels.");
		super.setCenterLabelsBetweenTicks(value);
	}
}
