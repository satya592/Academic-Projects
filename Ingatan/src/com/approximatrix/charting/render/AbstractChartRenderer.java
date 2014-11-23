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

    ChartRenderer.java
    Created on 26. Juni 2001, 22:52
*/

package com.approximatrix.charting.render;

import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.awt.Shape;
import java.awt.Graphics2D;
import java.awt.Dimension;
import javax.swing.event.EventListenerList;

import com.approximatrix.charting.model.ChartDataModel;

import com.approximatrix.charting.coordsystem.CoordSystem;
import com.approximatrix.charting.event.RenderChangeListener;
import com.approximatrix.charting.event.RenderChangeEvent;

/**
 * This class is the superclass for all the different ChartRenderer.
 * @author  mueller armstrong
 * @version 1.0
 */
public abstract class AbstractChartRenderer implements Renderer {

    protected Rectangle bounds;
    
    protected CoordSystem coord;
    
    protected ChartDataModel model;
  
    protected RowColorModel rcm;

	/** Flag indicating all rendering should be interrupted asap. */
	private volatile boolean stopFlag = false;
    
    /** The listener list. */
    protected EventListenerList listener = new EventListenerList();
    
    /** Creates new AbstractChartRenderer
     * @param model the DataModel that should be rendered
     */
    protected AbstractChartRenderer(ChartDataModel model) {
        this.model = model;
    }
    
    /** Creates new AbstractChartRenderer
     * @param cs the ClassicCoordSystem which contains the AffineTransforms to translate
     * into pixel space
     * @param rcm the RowColorModel that defines the correspondence between row titles and colors
     * @param model the DataModel that should be rendered
     */
    public AbstractChartRenderer(CoordSystem cs, 
                                 ChartDataModel model) {
        this(model);
        this.coord = cs;
    }

    /** Gets the bounds for this renderer.
     * @return the bounds of this renderer. If <code>setBounds</code> has not
     * been called before, the bounds computed from
     * <code>getPreferredSize</code> is returned.
     */
    public Rectangle getBounds() {
        return bounds;
    }
    
    /** Returns the preferred size needed for the renderer.
     * @return a non-null Dimension object
     */
    public Dimension getPreferredSize() {
        return new Dimension(Integer.MIN_VALUE, Integer.MIN_VALUE);
    }
    
    /** Calls <code>renderChart(g)</code> and crops the output to the desired
	 * bounds. This way you can manually set small maximum and minimum values 
	 * which automatically gets reflected in the ClassicCoordSystem but the ChartRenderer
	 * doesn't need to care.
     * @param g the Graphics2D object in which to render
     */
    public void render(Graphics2D g) {
		Rectangle bounds = getBounds();
		Shape clip = g.getClip();
		g.setClip((int)bounds.getX(), (int)bounds.getY(), (int)bounds.getWidth(), (int)bounds.getHeight());
		renderChart(g);
		g.setClip(clip);
	}
	
	/** Finally renders the chart in the clipped rectangle. */
	public abstract boolean renderChart(Graphics2D g);
    
    /** Sets the bounds the layout manager has assigned to
     * this renderer. Those, of course, have to be
     * considered in the rendering process.
     * @param bounds the new bounds for the renderer.
     */
    public void setBounds(Rectangle bounds) {
        if(!bounds.equals(this.bounds)) {
        	this.bounds = bounds;
        	this.fireRenderChangeEvent();
        }
    }
    
    /** Sets the ChartDataModel whose DataSets are rendered.
     * @param model the ChartDataModel
     */
    public void setChartDataModel(ChartDataModel model) {
        this.model = model;
   		this.fireRenderChangeEvent();
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
   		this.fireRenderChangeEvent();
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
   		this.fireRenderChangeEvent();
    }
    
    /** Returns the RowColorModel currently in use.
     * @return a RowColorModel
     */    
    public RowColorModel getRowColorModel() {
        return rcm;
    }
    
   /** Protected function that fires a render change event, notifying any
     * listeners that this renderable component needs to be redrawn.
     */
	public void fireRenderChangeEvent() {
		RenderChangeEvent rce = new RenderChangeEvent(this);
        Object[] ls = listener.getListenerList();
        for (int i = (ls.length - 2); i >= 0; i-=2) {
            if (ls[i] == RenderChangeListener.class) {
                ((RenderChangeListener)ls[i + 1]).renderUpdateRequested(rce);
            }
        }		
	}    
	
   /** Removes a RenderChangeListener.
     * @param l the RenderChangeListener
     */
    public void removeRenderChangeListener(RenderChangeListener l) {
        listener.remove(RenderChangeListener.class, l);
    }
    
    /** Adds a RenderChangeListener.
     * @param l the RenderChangeListener
     */
    public void addRenderChangeListener(RenderChangeListener l) {
    	/** Look for duplicates */
        Object[] ls = listener.getListenerList();
        boolean found = false;
        for(int i = ls.length-1; i >= 1; i-=2) {
        	if(ls[i] == l) {
        		found = true;
        		break;
        	}
        }
		if(!found)
	        listener.add(RenderChangeListener.class, l);
    }
    
    /** Clears all RenderChangeListeners from the object */
   	public void clearRenderChangeListeners() {
   		listener = new EventListenerList();
   	}
        

    /** Causes rendering to be interrupted.
     */
    public void interruptRendering() {
    	this.setStopFlag(true);
    }
    
    /** Resets the stop rendering flag */
    public void resetStopFlag() {
    	this.setStopFlag(false);
    }
    
    protected synchronized void setStopFlag(boolean value) {
    	stopFlag = value;
    }
    
    protected synchronized boolean getStopFlag() {
    	return stopFlag;
    }
}
