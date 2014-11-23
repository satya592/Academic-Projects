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
 
    BoxCoordSystem.java 
 */

package com.approximatrix.charting.coordsystem;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Stroke;
import java.awt.font.FontRenderContext;
import java.awt.font.TextLayout;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.text.DecimalFormat;

import com.approximatrix.charting.Axis;
import com.approximatrix.charting.coordsystem.ticklocator.NumericXTickLocator;
import com.approximatrix.charting.coordsystem.ticklocator.NumericYTickLocator;
import com.approximatrix.charting.coordsystem.ticklocator.ObjectXTickLocator;
import com.approximatrix.charting.event.ChartDataModelEvent;
import com.approximatrix.charting.event.ChartDataModelListener;
import com.approximatrix.charting.model.ChartDataModel;
import com.approximatrix.charting.model.ChartDataModelConstraints;

/** Coordinate system that renders as a box around the entire graph area.  Tick marks
 * are drawn on all sides of the box.  Axis labels are drawn centered on the X and Y axes.
 * 
 * 
 * @author armstrong
 */
public class BoxCoordSystem extends AbstractCoordSystem implements ChartDataModelListener {

    /** The default maximum number of ticks to allow the tick position
     * calculator to generate on a single axis.
     */
    private static final int DEFAULT_MAX_ACTUAL_TICKS = 40;
	
    /** Buffer between items on the bottom x axis */
	protected static final int BOTTOM_BUFFER = 5;
	
	/** Buffer between items on the left y axis */
	protected static final int LEFT_BUFFER = 5;
	
	/** Constant array of floats used to create the default grid
	 * line style.
	 */
	private final static float[] DOT1 = {1.0f};
	
	/** Flag to draw X-axis grid when the X axis contains an
	 * Object series
	 */
	protected static boolean DRAW_OBJECT_X_AXIS_GRID = false;
	
    /** Estimate of the number of x ticks to draw.  Used for
     * evaluating the automatically-generated tick locations.
     */
    private int estimated_x_ticks = 20;
    
    /** Estimates of the number of y ticks to draw.  Used for
     * evaluating the automatically-generated tick locations.
     */
    private int estimated_y_ticks = 20;
    
    /** The maximum number of x ticks to allow in the x direction
     */
    private int maximum_x_ticks = DEFAULT_MAX_ACTUAL_TICKS;
    
    /** The maximum number of y ticks to allow in the x direction
     */
    private int maximum_y_ticks = DEFAULT_MAX_ACTUAL_TICKS;
    
    /** The length of ticks in pixels */
    private int ticSize = 5;
	
	/** The ChartDataModel constraints of the first y-axis and the x-axis. */
    protected ChartDataModelConstraints constraints;
	/** The ChartDataModel constraints of the second y-axis and the x-axis. */
    protected ChartDataModelConstraints constraints2;
    
	/** The DataModel object. */
    protected ChartDataModel model;
    
    /** The stroke to use for drawing grids */
    protected Stroke gridStroke = new BasicStroke(1.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10.0f, DOT1, 0.0f);
    
    /** The color of grid lines */
    protected Color gridColor = Color.lightGray;

    /** Creates a new Box Coordinate System using the passed model.  Adds the coordinate
     * system to the model as a listener.
     * 
     * @param cdm the chart data model to associate the coordinate system with
     */
    public BoxCoordSystem(ChartDataModel cdm) {
        this.constraints = cdm.getChartDataModelConstraints(FIRST_YAXIS);
        this.constraints2 = cdm.getChartDataModelConstraints(SECOND_YAXIS);
        
        this.model = cdm;
        
        xaxis = new Axis(Axis.HORIZONTAL, constraints);
        yaxis = new Axis(Axis.VERTICAL, constraints);
		
		dfY = new DecimalFormat();
        dfX = new DecimalFormat();
        
        cdm.addChartDataModelListener(this);
        
        centerObjectLabelsBetweenTicks = !cdm.isColumnNumeric();
    }
    
	@Override
	protected int computeBottomMargin() {
		TextLayout layoutU = new TextLayout(getXAxisUnit(), getUnitFont(), 
                							new FontRenderContext(null, true, false));
		
		Rectangle2D ticks = new Rectangle();
		if(model.isColumnNumeric())
			ticks = new TextLayout("1.0", getTickFont(),new FontRenderContext(null, true, false)).getBounds();
		else {
			for(int i=0;i<model.getDataSetNumber();i++) {
				for(int j=0;j<model.getDataSetLength(i);j++) {
					Rectangle2D r = getTickFont().getStringBounds(model.getColumnValueAt(i, j).toString(), new FontRenderContext(null, true, false));
					if(r.getBounds().height > ticks.getBounds().height)
						ticks = r;
				}
			}
			
		}
		
		return (int)layoutU.getBounds().getHeight()+(int)ticks.getHeight()+3*BOTTOM_BUFFER;
	}

	@Override
	protected int computeLeftMargin() {
		FontRenderContext lefty = new FontRenderContext(null, true, false);
		TextLayout layoutU = new TextLayout(getYAxisUnit(), getUnitFont().deriveFont(AffineTransform.getRotateInstance(Math.PI/-2.0)), lefty);
		TextLayout layoutT = new TextLayout("1.0", getTickFont().deriveFont(AffineTransform.getRotateInstance(Math.PI/-2.0)), lefty);
		
		return (int)layoutU.getBounds().getWidth()+(int)layoutT.getBounds().getWidth()+3*LEFT_BUFFER;
	}

	@Override
	protected int computeRightMargin() {
		return 0;
	}

	@Override
	protected int computeTopMargin() {
		return 0;
	}

	@Override
	public void paintDefault(Graphics2D g) {
    	// If we shouldn't paint axes, then we shouldn't paint anything
    	// at all.
    	if(!shouldPaintAxes)
    		return;
    	
        g.setColor(Color.black);
        Rectangle crect = getInnerBounds();
        g.drawRect(crect.x, crect.y, crect.width, crect.height);
        
        // Draw the grids first
        if(shouldPaintGrid) {
        	//c.drawYAxisGrid(g);
        	//if(model.isColumnNumeric()) c.drawNumericalXAxisGrid(g);
        }

        g.setColor(Color.black);
        TextLayout layoutX = new TextLayout(getXAxisUnit(), getUnitFont(), 
                                           new FontRenderContext(null, true, false));
		layoutX.draw(g, (float)(crect.getCenterX()-layoutX.getBounds().getWidth()/2.0),  
						(float)(this.getBounds().getMinY() + this.getBounds().getHeight()-(float)BOTTOM_BUFFER)); // + (float)layoutX.getBounds().getHeight() ));
        
        
        // draw Y-Axis label right below the Arrow ?!
        g.setColor(Color.black);
        
        TextLayout layoutY = new TextLayout(getYAxisUnit(), getUnitFont().deriveFont(AffineTransform.getRotateInstance(Math.PI/-2.0)), 
        		new FontRenderContext(null, true, false));
        
        
        layoutY.draw(g, (float)LEFT_BUFFER + (float)layoutY.getBounds().getWidth()/2, 
        		     (float)crect.y + (float)(crect.getCenterY()+layoutY.getBounds().getWidth()/2.0));
        
        drawYAxisTicks(g);
        drawXAxisTicks(g);
        
        //c.drawYAxisTicks(g);
	}

	public void chartDataChanged(ChartDataModelEvent evt) {
		setTransforms();
	}

	public ChartDataModelConstraints getChartDataModelConstraints(int axis) {
		if(axis == CoordSystem.FIRST_YAXIS)
			return this.constraints;
		else if(axis == CoordSystem.SECOND_YAXIS)
			return this.constraints2;
		
		return null;
	}

	public int getMaximumXTicks() {
		return maximum_x_ticks;
	}

	public int getMaximumYTicks() {
		return maximum_y_ticks;
	}

	public void resetMaximumXTicks() {
		maximum_x_ticks = DEFAULT_MAX_ACTUAL_TICKS;
	}

	public void resetMaximumYTicks() {
		maximum_y_ticks = DEFAULT_MAX_ACTUAL_TICKS;
	}

	public void setMaximumXTicks(int value) {
		maximum_x_ticks = value;
	}

	public void setMaximumYTicks(int value) {
		maximum_y_ticks = value;
	}
	
	private void drawYAxisTicks(Graphics2D g) {
		double[] ym = null;
		
		try {
			ym = CoordSystemUtilities.SafeMaxMin(constraints.getMaximumY().doubleValue(), constraints.getMinimumY().doubleValue());
        } catch(NullPointerException npe) {
        	ym[0] = 0.0; ym[1] = 1.0;
        }
        
        double[] tics = new NumericYTickLocator(ym[CoordSystemUtilities.MAX],ym[CoordSystemUtilities.MIN],this.estimated_y_ticks,DEFAULT_MAX_ACTUAL_TICKS).getTickMarkLocationsAsPairs(0.0);
        
        if(tics == null) return;
        double[] dp = new double[tics.length];
        
        this.getTransform(CoordSystem.FIRST_YAXIS).transform(tics, 0, dp, 0, tics.length/2);
        
        g.setColor(Color.BLACK);
        int xright = this.getInnerBounds().x+this.getInnerBounds().width;
        int xleft = this.getInnerBounds().x;
        
        Font yfont = getTickFont().deriveFont(AffineTransform.getRotateInstance(Math.PI/-2.0));
        
        for(int i=1;i<tics.length;i=i+2) {
        	int y = (int)Math.round(dp[i]);
        	if(y > this.getInnerBounds().y+this.getInnerBounds().height || y < this.getInnerBounds().y)
        		continue;
        	g.drawLine(xright, y, xright-ticSize, y);
        	g.drawLine(xleft, y, xleft+ticSize, y);
        	
        	if(shouldPaintGrid)
        		drawSingleGridline(g,xleft+ticSize,y,xright-ticSize,y);
        	
        	TextLayout label = new TextLayout(super.getYDecimalFormat().format(tics[i]), yfont, 
                    new FontRenderContext(null, true, false));
        	
        	label.draw(g, xleft-LEFT_BUFFER, y+(int)(label.getBounds().getHeight()/2.0));
        }
	}
	
	private void drawXAxisTicks(Graphics2D g) {
		if(model.isColumnNumeric())
			drawNumericalXAxisTicks(g);
		else 
			drawObjectXAxisTicks(g);
	}
	
	private void drawNumericalXAxisTicks(Graphics2D g) {
		double[] xm = null;
		
		try {
        	xm = CoordSystemUtilities.SafeMaxMin(constraints.getMaximumX().doubleValue(), constraints.getMinimumX().doubleValue());
        } catch(NullPointerException npe) {
        	xm[0] = 0.0; xm[1] = 1.0;
        }
        
        double[] tics = new NumericXTickLocator(xm[CoordSystemUtilities.MAX],xm[CoordSystemUtilities.MIN],this.estimated_x_ticks,DEFAULT_MAX_ACTUAL_TICKS).getTickMarkLocationsAsPairs(0.0);
        
        if(tics == null) return;
        double[] dp = new double[tics.length];
        
        this.getTransform(CoordSystem.FIRST_YAXIS).transform(tics, 0, dp, 0, tics.length/2);
        
        g.setColor(Color.BLACK);
        int ybottom = this.getInnerBounds().y+this.getInnerBounds().height;
        int ytop = this.getInnerBounds().y;
        for(int i=0;i<tics.length;i=i+2) {
        	int x = (int)Math.round(dp[i]);
        	if(x > this.getInnerBounds().x+this.getInnerBounds().width || x < this.getInnerBounds().x)
        		continue;
        	g.drawLine(x, ytop, x, ytop+ticSize);
        	g.drawLine(x, ybottom, x, ybottom-ticSize);
        	
        	if(shouldPaintGrid)
        		drawSingleGridline(g,x,ytop+ticSize,x,ybottom-ticSize);
        	
        	TextLayout label = new TextLayout(super.getXDecimalFormat().format(tics[i]), getTickFont(), 
                    new FontRenderContext(null, true, false));
        	
        	if(this.isCenterLabelsBetweenTicks())
    			x = x + (int)((dp[i+2]-(float)x)/2.0f);
        	
        	label.draw(g, x-(int)(label.getBounds().getWidth()/2.0), ybottom+BOTTOM_BUFFER+(int)label.getBounds().getHeight());
        }
        
	}
	
	private void drawObjectXAxisTicks(Graphics2D g) {
		float[] tics = null;
		try {
			tics = new ObjectXTickLocator(constraints.getMinimumX().intValue(),constraints.getMaximumX().intValue(),this.estimated_x_ticks).getTickMarkLocationsAsPairs(0);
		} catch(NullPointerException npe) {
			int i = 0;
			while(model.getColumnValueAt(i) != null)
				i++;
			tics = new float[i*2];
			for(int j=0;j<i;j=j+2) {
				tics[j] = (float)(j/2);
				tics[j+1] = 0;
			}
		}
        
        if(tics == null) return;
        float[] dp = new float[tics.length];
        
        this.getTransform(CoordSystem.FIRST_YAXIS).transform(tics, 0, dp, 0, tics.length/2);
        
        g.setColor(Color.BLACK);
        int ybottom = this.getInnerBounds().y+this.getInnerBounds().height;
        int ytop = this.getInnerBounds().y;
        for(int i=0;i<tics.length;i=i+2) {
        	int x = (int)Math.round(dp[i]);
        	if(x > this.getInnerBounds().x+this.getInnerBounds().width || x < this.getInnerBounds().x)
        		continue;
        	g.drawLine(x, ytop, x, ytop+ticSize);
        	g.drawLine(x, ybottom, x, ybottom-ticSize);
        	
        	if(DRAW_OBJECT_X_AXIS_GRID && shouldPaintGrid)
        		drawSingleGridline(g,x,ytop+ticSize,x,ybottom-ticSize);
        	
        	try {
        		TextLayout label = new TextLayout(model.getColumnValueAt((int)tics[i]).toString(), getTickFont(), 
        				new FontRenderContext(null, true, false));
        	
        		if(this.isCenterLabelsBetweenTicks())
        			x = x + (int)((dp[i+2]-(float)x)/2.0f);
        		
        		label.draw(g, x-(int)(label.getBounds().getWidth()/2.0), ybottom+BOTTOM_BUFFER+(int)label.getBounds().getHeight());
        	} catch(NullPointerException npe) {
        		continue;
        	} catch(ArrayIndexOutOfBoundsException aiobe) {
        		continue;
        	}
        	
        }
        
	}
	
	private void drawSingleGridline(Graphics2D g, int x1, int y1, int x2, int y2) {
		Stroke original = g.getStroke();
		Color c = g.getColor();
		
		g.setStroke(gridStroke);
		g.setColor(gridColor);
		
		g.drawLine(x1, y1, x2, y2);
		
		g.setStroke(original);
		g.setColor(c);
	}
	
}
