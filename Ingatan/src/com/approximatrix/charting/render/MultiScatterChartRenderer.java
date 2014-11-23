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

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.RectangularShape;

import java.util.ArrayList;

import com.approximatrix.charting.SimpleStrokeDefs;
import com.approximatrix.charting.coordsystem.CoordSystem;
import com.approximatrix.charting.model.ChartDataModel;
import com.approximatrix.charting.model.MultiScatterDataModel;

/**
 * This renderer creates a scatter chart based on data contained in a MultiScatterDataModel.
 * The renderer will draw line and/or markers, depending on the settings contained within
 * each data set on the MultiScatterDataModel.  This renderer also provides buffering of 
 * transformed coordinates, which in turn causes the refresh speed to be increased 
 * significantly.
 *  
 * @author  armstrong
 * @version 1.0
 */
public class MultiScatterChartRenderer extends AbstractChartRenderer {
	
    protected Rectangle bounds;
    
    protected CoordSystem coord;
    
    protected MultiScatterDataModel model;
  
    protected RowColorModel rcm;
    
	protected double shapeSize = 10.0;
	
	// For faster drawing, data is stored locally
	/** Buffered version of the coordinate transform used for painting */
	private AffineTransform last = null;
	
	/** ArrayList of transformed Point2D arrays for buffering the coordinate transform */
	private ArrayList transformed = null;
	
	/** Flag indicating whether coordinate transform buffering should be used */
	private boolean buffer_transform = false;
	
	/** Creates new LineChartRenderer
	 * @param rcm the RowColorModel needed to determine the right colors
	 * @param cs the ClassicCoordSystem used to translate values into points
	 * @param model the DataModel that should be rendered
	 */
	public MultiScatterChartRenderer(CoordSystem cs, MultiScatterDataModel model) {
		super(cs,model);
		this.coord = cs;
		this.model = model;
		
		// A dummy
		this.last = new AffineTransform();
	}
	
	/** Finally renders the Object in the Graphics object.
	 * @param g the Graphics2D object in which to render
	 */
	public boolean renderChart(Graphics2D g) {
		
		if(this.getStopFlag())
			return false;

		AffineTransform yaxis1 = getTransform(CoordSystem.FIRST_YAXIS);
		
		int datasetcount = model.getDataSetNumber();
		
		// Update the locally-stored transform information
		if(transformed == null && buffer_transform)
			transformed = new ArrayList(datasetcount);
		
		//System.out.println("** Render LineChart.-");
		for(int set = 0; set < datasetcount; set++) {
			
			// Stop check
			if(this.getStopFlag())
				break;
				
			
			if(transformed != null) {
				if(transformed.size() < datasetcount && buffer_transform) {
					transformed.add(new Point2D[model.getDataSetLength(set)]);
				}
			}
			
			if(model.getSeriesLine(set)  && !this.getStopFlag()) {
				drawline(yaxis1,set,g);
			}
			if(model.getSeriesMarker(set) && !this.getStopFlag()) {
				drawmarkers(yaxis1,set,g);
			}
			
		}
		
		// Only buffer the transform if enabled and the rendering was
		// not interrupted.
		if(buffer_transform && !this.getStopFlag()) {
    		if(!yaxis1.equals(last))
    			last = yaxis1;
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
	private void drawline(AffineTransform yaxis1, int set, Graphics2D g) {
		Point2D val;
		Point2D paint = null;
		Point2D hold = null;
		Point2D oldpaint = null;
		boolean numericalcolumns = model.isColumnNumeric();
		float modelVal = 0f;
		
		// The buffer for drawing
		Point2D buffer[] = null;

		if(buffer_transform)
			buffer = (Point2D[])transformed.get(set);
		
    	Stroke backupStroke = g.getStroke();
    	g.setStroke(SimpleStrokeDefs.getStroke(model.getSeriesLineStyle(set)));
    	g.setColor(rcm.getColor(set));
    	
    	// See if the transform needs to be recomputed
    	if(!yaxis1.equals(last) || !buffer_transform) {
    		
    		//System.out.println("*** Recomputing transform...");
    		
    		// Walk through, transforming all points
    		for(int value = 0; value < model.getDataSetLength(set);  value++) {
    			modelVal = model.getValueAt(set, value).floatValue();

    			if(modelVal != modelVal || modelVal == Float.NEGATIVE_INFINITY || modelVal == Float.POSITIVE_INFINITY) {
    				//System.out.print(".");
    				oldpaint = null;
    				continue;
    			}

    			if(numericalcolumns)
    				val = new Point2D.Float(((Number)model.getColumnValueAt(set, value)).floatValue(),
    						modelVal);
    			else
    				val = new Point2D.Float((float)value,
    						modelVal);

    			//System.out.println("** Rendered "+val);
    			oldpaint = paint;
    			
    			hold = yaxis1.transform(val, null);
    			if(hold != null) {
    				paint = hold;
    				hold = null;
    			} else
    				continue;

    			// Draw if buffering is completely disabled, otherwise, save the point
    			if(!buffer_transform) {
    				if(oldpaint != null) {
    					g.drawLine((int)oldpaint.getX(), (int)oldpaint.getY(),
    							(int)paint.getX(), (int)paint.getY());
    				}
    			} else {
    				buffer[value] = paint;
    			}

    		}
    	}
    	
    	// If buffered, do the drawing here
    	if(buffer_transform && !this.getStopFlag()) {
    		
    		//System.out.println("*** Drawing Buffer...");
    		for(int value = 1; value < model.getDataSetLength(set) && buffer_transform && !this.getStopFlag(); value++) {
    			
    			// Skip possible null values
    			if(buffer[value] == null || buffer[value-1] == null)
    				continue;
    			
    			g.drawLine((int)buffer[value-1].getX(), (int)buffer[value-1].getY(),
    					   (int)buffer[value].getX(),   (int)buffer[value].getY());
    		}
    	}
    	
		g.setStroke(backupStroke);
		
	}
	
	/** Performs the drawing point markers for a given data set if called
	 * 
	 * @param yaxis1 the transform to be used
	 * @param set the dataset to draw
	 * @param g the graphics to be painted to
	 */
	private void drawmarkers(AffineTransform yaxis1, int set, Graphics2D g) {
		
        Point2D val;
        Point2D paint = new Point2D.Float(0f, 0f);
        boolean numericalcolumns = model.isColumnNumeric();
  
        float modelVal = 0f;
        
        RectangularShape shape;
        
        // The buffer for drawing
		Point2D buffer[] = null;

		if(buffer_transform)
			buffer = (Point2D[])transformed.get(set);
        
        g.setColor(rcm.getColor(set));
		
        // See if the transform needs to be recomputed
        if(!yaxis1.equals(last) || !buffer_transform) {

        	for(int value = 0; value < model.getDataSetLength(set);  value++) {
        		modelVal = model.getValueAt(set, value).floatValue();

        		// Catch modelVal == Not A Number
        		if(Float.isNaN(modelVal))
        			continue;

        		if(numericalcolumns)
        			val = new Point2D.Float(((Number)model.getColumnValueAt(set, value)).floatValue(),
        					modelVal);
        		else
        			val = new Point2D.Float((float)value,
        					modelVal);


        		paint = yaxis1.transform(val, null);
    			if(paint == null) 
    				continue;

        		if(!buffer_transform) {
        			
        			shape = rcm.getShape(set);
        			shape.setFrame(paint.getX() - shapeSize/2, paint.getY() - shapeSize/2, shapeSize, shapeSize);

        			g.fill(shape);
        		} else {
        			buffer[value] = paint;
        		}
        		
        		if(this.getStopFlag())
    				break;
        	}
        }
        
        // If buffered, do the drawing here
    	if(buffer_transform && !this.getStopFlag()) {
    		
    		for(int value = 0; value < model.getDataSetLength(set) && buffer_transform; value++) {
    			
    			// Skip possible null values
    			if(buffer[value] == null)
    				continue;

    			shape = rcm.getShape(set);
    			shape.setFrame(buffer[value].getX() - shapeSize/2, buffer[value].getY() - shapeSize/2, shapeSize, shapeSize);

    			g.fill(shape);
    			
    			if(this.getStopFlag())
    				break;
    		}
    	}
        
	}
    
	/** Returns the renderer's prefered size */
	public Dimension getPreferredSize() {
		return new Dimension(Integer.MIN_VALUE, Integer.MIN_VALUE);
	}

	/** Sets the graphical bounds for this renderer */
	public void setBounds(Rectangle bounds) {
		this.bounds = bounds;
	}

	/** Returns the graphical bounds for this renderer */
	public Rectangle getBounds() {
		return bounds;
	}

	/** Sets the ChartDataModel whose DataSets are rendered.
     * @param model the ChartDataModel
     */
    public void setChartDataModel(ChartDataModel model) {
        this.model = (MultiScatterDataModel)model;
    }
    
    /** Returns the ChartDataModel whose DataSets are rendered.
     * @return a ChartDataModel which contains the Chart's data
     */
    public ChartDataModel getChartDataModel() {
        return model;
    } 
    
    /** Returns the current ClassicCoordSystem. */
    public CoordSystem getCoordSystem() {
        return coord;
    }
    
    /** Sets the ClassicCoordSystem which contains the AffineTransforms to
     * translate into pixel space.
     * @param cs the new ClassicCoordSystem 
     */
    public void setCoordSystem(CoordSystem cs) {
        coord = cs;
    }    
    
    /** Returns the currently defined AffineTransform for any y-axis.
     * @param axis the y-axis to be used.
     */
    public AffineTransform getTransform(int axis) {
        return getCoordSystem().getTransform(axis);
    }
    
    /** Sets a RowColorModel to define the correlation of row titles and colors used for the Legend.
     * @param rcm the RowColorModel
     */    
    public void setRowColorModel(RowColorModel rcm) {
        this.rcm = rcm;
    }
    
    /** Returns the RowColorModel currently in use.
     * @return a RowColorModel
     */    
    public RowColorModel getRowColorModel() {
        return rcm;
    }
	
    /** Clears all buffered transformation and drawing information
     * 
     */
    public void fireDataUpdate() {
    	last = null;
    	transformed = null;
    }
    
    /** Sets whether the renderer can buffer data internally for faster drawing
     * 
     * @param value true to allow drawing, false otherwise
     */
    public void setAllowBuffer(boolean value) {
    	buffer_transform = value;
    }
    
}
