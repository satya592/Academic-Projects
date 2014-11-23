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

    PieChartRenderer.java
    Created on 7. August 2001, 18:14
*/

package com.approximatrix.charting.render;

import java.awt.font.FontRenderContext;
import java.awt.font.TextLayout;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Arc2D;
import java.awt.geom.GeneralPath;
import java.awt.geom.Line2D;

import java.awt.geom.AffineTransform;

import java.awt.BasicStroke;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.RenderingHints;
import java.awt.Color;
import java.awt.Stroke;

import com.approximatrix.charting.coordsystem.CoordSystem;
import com.approximatrix.charting.coordsystem.CoordSystemUtilities;
import com.approximatrix.charting.coordsystem.ticklocator.NumericTickLocator;
import com.approximatrix.charting.coordsystem.ticklocator.NumericYTickLocator;
import com.approximatrix.charting.model.ChartDataModel;


/**
 * This renderer creates a Radar chart.
 * @author  tbee
 * @version 1.0
 */
public class RadarChartRenderer extends AbstractChartRenderer {

	private static final int LABEL_BUFFER = 3;
	
	private final static float[] DOT1 = {1.0f};
	
	/** The stroke to use for drawing grids */
    protected Stroke gridStroke = new BasicStroke(1.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10.0f, DOT1, 0.0f);
    
    /** The color of grid lines */
    protected Color gridColor = Color.lightGray;
	
    protected int estimated_grid_steps = 10;
    
    private boolean drawGrid = true;
    
    /** Creates new RadarChartRenderer
     * @param model the DataModel that should be rendered
     */
    public RadarChartRenderer(ChartDataModel model) {
        super(model);
    }
    
    /** Creates new RadarChartRenderer
     * @param cs the ClassicCoordSystem used to translate values into points
     * @param model the DataModel that should be rendered
     */
    public RadarChartRenderer(CoordSystem cs, ChartDataModel model) {
        super(cs, model);
    }

    /** Finally renders the Object in the Graphics object.
     * @param g the Graphics2D object in which to render
     */
    public boolean renderChart(Graphics2D g) {
    	// remember current anti aliasing rendering hint 
    	// then activate it
        Object rh = g.getRenderingHint(RenderingHints.KEY_ANTIALIASING);
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        // get the models
        ChartDataModel m = getChartDataModel();
        RowColorModel rcm = getRowColorModel();

		// get the available drawing space        
        double height = this.getBounds().getHeight();
        double width = this.getBounds().getWidth();

		// the number of dataset
        int lNumberOfRows = m.getDataSetNumber();
        
        // determine shortest dataset length, this is the number of axis that we need to draw
        int lNumberOfColumns = Integer.MAX_VALUE;        
        for(int i = 0; i < lNumberOfRows; i++) 
            lNumberOfColumns = Math.min(lNumberOfColumns, m.getDataSetLength(i));
        
        double[] maxvalues = new double[lNumberOfColumns];
        
        // get center
        double center_y = getBounds().getCenterY();
        double center_x = getBounds().getCenterX();

		// determine the radius
        double lRadius = Math.min(width * 0.9, height * 0.9) / 2;
        
        double[] ticks = null;
        double[] ym = this.getScaling();
        double lMaxValue = 0.0;
        if(ym != null) {
        	if(ym[CoordSystemUtilities.MAX] > 0.0) {        		
        		ticks = new NumericTickLocator(ym[CoordSystemUtilities.MAX], 0.0, estimated_grid_steps, this.coord.getMaximumYTicks()).getTickMarkLocations();
        		lMaxValue = 1.1*ym[CoordSystemUtilities.MAX];
        	}
        }

		// scan through the datasets
        for(int lRow = 0; lRow < lNumberOfRows && !this.getStopFlag(); lRow++) 
        {
			// scan through the values in the dataset
			GeneralPath filledPolygon = new GeneralPath(GeneralPath.WIND_EVEN_ODD, lNumberOfColumns);
			for (int lCol = 0; lCol < lNumberOfColumns && !this.getStopFlag(); lCol++)
			{
				// get the value
                double lValue = m.getValueAt(lRow, lCol).doubleValue();
                
                if(lMaxValue == 0.0) {
                    for(int row = 0; row < lNumberOfRows; row++)
                        lMaxValue = Math.max(lMaxValue, m.getValueAt(row, lCol).doubleValue() * 1.1);
                    
                    maxvalues[lCol] = lMaxValue;
                }
                
        		double lScaledValue = lValue / lMaxValue;
        		double lLineValue = lRadius * lScaledValue;

				// determine rotation: there are 2PI/noOfCols vertexes, this is vertex no lCol		
				// -1 : we want to rotate clockwise
				// + PI: rotate 180 degree to get the first column pointing up
				double lRotation = (-1 * (2 * Math.PI / lNumberOfColumns) * lCol) + Math.PI;

				// determine the end points
				double lX = center_x + (lLineValue * Math.sin(lRotation));
				double lY = center_y + (lLineValue * Math.cos(lRotation));

				// draw the line
				Line2D lLine = new Line2D.Double(center_x, center_y, lX, lY);
		        g.setColor(Color.black);
		        g.draw(lLine);

				// add to polygone
				if (lCol == 0) filledPolygon.moveTo((float)lX, (float)lY);
				else filledPolygon.lineTo((float)lX, (float)lY);		        
			}
			
			// draw the polygone
			filledPolygon.closePath();
			g.setPaint( rcm.getColor(lRow) );
			g.draw(filledPolygon);
        }
        
        double lRotation;
        double lX;
        double lY;
        TextLayout lLabel;

        // Cycle through tick marks
        if(ticks != null && lMaxValue > 0.0 && isDrawGrid()) {
        	Stroke shold = g.getStroke();
			Color chold = g.getColor();
			
			g.setStroke(gridStroke);
			g.setColor(gridColor);
			
        	for(int lRow = 0; lRow < ticks.length && !this.getStopFlag(); lRow++) 
        	{
        		GeneralPath filledPolygon = new GeneralPath(GeneralPath.WIND_EVEN_ODD, lNumberOfColumns);
        		
        		if(ticks[lRow] > lMaxValue || ticks[lRow] == 0.0)
        			continue;
        		
    			double lScaledValue = ticks[lRow] / lMaxValue;
        		double lLineValue = lRadius * lScaledValue;
        		//if(lLineValue > lMaxValue) 
        		//	continue;
        		
        		for (int lCol = 0; lCol < lNumberOfColumns && !this.getStopFlag(); lCol++) {

    				// determine rotation: there are 2PI/noOfCols vertexes, this is vertex no lCol		
    				// -1 : we want to rotate clockwise
    				// + PI: rotate 180 degree to get the first column pointing up
    				double rot = (-1 * (2 * Math.PI / lNumberOfColumns) * lCol) + Math.PI;

    				// determine the end points
    				double x = center_x + (lLineValue * Math.sin(rot));
    				double y = center_y + (lLineValue * Math.cos(rot));

    				// draw the line and label if it's column zero
    				if (lCol == 0) {
    					filledPolygon.moveTo((float)x, (float)y);
    					
    					TextLayout label = new TextLayout(coord.getYDecimalFormat().format(ticks[lRow]), coord.getTickFont(), 
    		                    new FontRenderContext(null, true, false));
    					
    					g.setStroke(shold);
    		        	g.setColor(Color.black);
    					
    		        	label.draw(g, (float)x+(float)LABEL_BUFFER, (float)y);
    					
    		        	g.setStroke(gridStroke);
    					g.setColor(gridColor);
    					
    				} else 
    					filledPolygon.lineTo((float)x, (float)y);
           		}
        		filledPolygon.closePath();
        		g.draw(filledPolygon);
        	}
        	
        	g.setStroke(shold);
        	g.setColor(chold);
        }
        
		// draw the lines
		for (int lCol = 0; lCol < lNumberOfColumns && !this.getStopFlag(); lCol++)
		{
			// determine rotation: there are 2PI/noOfCols vertexes, this is vertex no lCol		
			// -1 : we want to rotate clockwise
			// + PI: rotate 180 degree to get the first column pointing up
            // Math.PI ... - Math.PI
			lRotation = (-1 * (2 * Math.PI / lNumberOfColumns) * lCol) + Math.PI;

			// determine the end points
			lX = center_x + (lRadius * Math.sin(lRotation));
			lY = center_y + (lRadius * Math.cos(lRotation));

			// draw the line
			Line2D lLine = new Line2D.Double(center_x, center_y, lX, lY );
	        g.setColor(Color.black);
	        g.draw(lLine);

	        // draw the label 
	        lLabel = new TextLayout("" + model.getColumnValueAt(lCol), new Font("Courier", Font.BOLD, 9), new FontRenderContext(null, true, false));
	        g.setColor(Color.black);
            
            // Move the labels in the lower half circle down a bit, so the upper left corner touches the axis
            if ((lRotation <= Math.PI / 2) && (lRotation >= -Math.PI / 2))
                lY += lLabel.getBounds().getHeight();
            
            // Move the labels in the left half circle a bit left, so the upper right corner touches the axis
            if (lRotation <= 0)
                lX -= lLabel.getBounds().getWidth();
            
			lLabel.draw(g, (float)lX,  (float)lY);
		}

		// reset rendering hint        
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, rh);
        boolean completed = !this.getStopFlag();
        this.resetStopFlag();
        return completed;
    }    
    
    /** Returns the max and min values in the model
     * 
     * @return a two element array of the max and min values
     */ 
    private double[] getScaling() {
		try {
			return CoordSystemUtilities.SafeMaxMin(model.getChartDataModelConstraints(CoordSystem.FIRST_YAXIS).getMaximumY().doubleValue(), model.getChartDataModelConstraints(CoordSystem.FIRST_YAXIS).getMinimumY().doubleValue());
        } catch(NullPointerException npe) {
        	return null;
        }   
    }
    
    /** Enables or disables drawing a grid in the radar plot
     * 
     * @param value true to draw grid and tick labels, false to skip drawing
     */
    public void setDrawGrid(boolean value) {
    	drawGrid = value;
    }
    
    /** Returns whether the grid is being drawn on the radar plot
     * 
     * @return true if the grid and labels are drawn, false otherwise
     */
    public boolean isDrawGrid() {
    	return drawGrid;
    }
}
