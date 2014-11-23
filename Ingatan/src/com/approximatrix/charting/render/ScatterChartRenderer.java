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

package com.approximatrix.charting.render;

import java.awt.geom.Point2D;
import java.awt.geom.RectangularShape;
import java.awt.geom.AffineTransform;
import java.awt.Graphics2D;

import com.approximatrix.charting.coordsystem.CoordSystem;
import com.approximatrix.charting.model.ChartDataModel;
import com.approximatrix.charting.model.ScatterDataModel;
import com.approximatrix.charting.render.*;

/**
 * This renderer creates a scatter chart based on data contained in a ScatterDataModel.
 * The renderer will draw line and/or markers, depending on the settings contained within
 * each data set on the ScatterDataModel. 
 *  
 * @author  armstrong
 * @version 1.0
 */
public class ScatterChartRenderer extends AbstractChartRenderer {
	
	protected double shapeSize = 10.0;
	
	/** Creates new LineChartRenderer
	 * @param rcm the RowColorModel needed to determine the right colors
	 * @param cs the ClassicCoordSystem used to translate values into points
	 * @param model the DataModel that should be rendered
	 */
	public ScatterChartRenderer(CoordSystem cs, ChartDataModel model) {
		super(cs, model);
	}
	
	/** Finally renders the Object in the Graphics object.
	 * @param g the Graphics2D object in which to render
	 */
	public boolean renderChart(Graphics2D g) {
		ScatterDataModel m = (ScatterDataModel)getChartDataModel();
		RowColorModel rcm = getRowColorModel();
		AffineTransform yaxis1 = getTransform(CoordSystem.FIRST_YAXIS);
		
		int datasetcount = m.getDataSetNumber();		
		
		//System.out.println("** Render LineChart.-");
		for(int set = 0; set < datasetcount && !this.getStopFlag(); set++) {
			if(m.getSeriesLine(set)) {
				drawline(m,rcm,yaxis1,set,g);
			}
			if(m.getSeriesMarker(set)) {
				drawmarkers(m,rcm,yaxis1,set,g);
			}
			
		}
		boolean completed = !this.getStopFlag();
        this.resetStopFlag();
        return completed;
	}
	
	/** Performs the line drawing for a given data set if called
	 * 
	 * @param yaxis1 the transform to be used
	 * @param set the dataset to draw
	 * @param g the graphics to be painted to
	 */
	private void drawline(ScatterDataModel m, RowColorModel rcm, 
			              AffineTransform yaxis1, int set, Graphics2D g) {
		Point2D val;
		Point2D paint = null;
		Point2D oldpaint = null;
		boolean numericalcolumns = m.isColumnNumeric();
		float modelVal = 0f;
		
		for(int value = 0; value < m.getDataSetLength(set);  value++) {
			modelVal = m.getValueAt(set, value).floatValue();
			
			if(modelVal != modelVal || modelVal == Float.NEGATIVE_INFINITY || modelVal == Float.POSITIVE_INFINITY) {
				//System.out.print(".");
				oldpaint = null;
				continue;
			}
			
			if(numericalcolumns)
				val = new Point2D.Float(((Number)m.getColumnValueAt(set, value)).floatValue(),
						modelVal);
			else
				val = new Point2D.Float((float)value,
						modelVal);
			
			//System.out.println("** Rendered "+val);
			oldpaint = paint;
			
			if(yaxis1.transform(val, null) != null) {
				paint = yaxis1.transform(val, null);
				//System.out.println("** val = "+val+"  paint = "+paint);
			}
			else
				continue;
			
			g.setColor(rcm.getColor(set));
			
			if(oldpaint != null) {
				g.drawLine((int)oldpaint.getX(), (int)oldpaint.getY(),
						(int)paint.getX(), (int)paint.getY());
			}
		}
		
	}
	
	/** Performs the drawing point markers for a given data set if called
	 * 
	 * @param yaxis1 the transform to be used
	 * @param set the dataset to draw
	 * @param g the graphics to be painted to
	 */
	private void drawmarkers(ScatterDataModel m, RowColorModel rcm, 
            AffineTransform yaxis1, int set, Graphics2D g) {
		
        Point2D val;
        Point2D paint = new Point2D.Float(0f, 0f);
        boolean numericalcolumns = m.isColumnNumeric();
  
        float modelVal = 0f;
        
        RectangularShape shape;
		
        for(int value = 0; value < m.getDataSetLength(set);  value++) {
            modelVal = m.getValueAt(set, value).floatValue();
            
            // Catch modelVal == Not A Number
            if(modelVal != modelVal)
                continue;
            
            if(numericalcolumns)
                val = new Point2D.Float(((Number)m.getColumnValueAt(set, value)).floatValue(),
                                        modelVal);
            else
                val = new Point2D.Float((float)value,
                                        modelVal);
                
            
            yaxis1.transform(val, paint);
            if(paint == null)
                continue;
            
            g.setColor(rcm.getColor(set));
            
            shape = rcm.getShape(set);
            shape.setFrame(paint.getX() - shapeSize/2, paint.getY() - shapeSize/2, shapeSize, shapeSize);
            
			g.fill(shape);
        }
	}
	
}

