/*
    OpenChart2 Java Charting Library and Toolkit
    Copyright (C) 2005-2007 Approximatrix, LLC
    Copyright (C) 2001  Sebastian Müller
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

    ChartPanel.java
    Created on 6. September 2001, 14:10
*/

package com.approximatrix.charting.swing;

import com.approximatrix.charting.BufferedChart;
import com.approximatrix.charting.model.ChartDataModel;
import com.approximatrix.charting.event.RenderChangeListener;
import com.approximatrix.charting.event.RenderChangeEvent;

import java.awt.Rectangle;
import java.awt.Graphics;

import java.awt.event.*;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.awt.Color;

/**
 * This Panel provides the possibility to include a Chart into a Swing 
 * Application. I choose not to make every Chart extend JComponent because
 * of the overhead this would have meant. Instead, this class is an adaptor.
 * It implements the Chart interface and contains a DefaultChart instance
 * to which all Chart calls are promoted.
 * @author  mueller, armstrong
 */
public class BufferedChartPanel extends AbstractChartPanel implements RenderChangeListener {

	
	/** The selection rectangle for processing purposes */
	private Rectangle currentRect = null;
	
	/** The selection rectangle for actual drawing purposes */
	private Rectangle grRect = null;
	
    
    /** Creates new ChartPanel */
    private BufferedChartPanel() {
    	super();
    }
    
    /** Creates a new ChartPanel with the given model
     * and title string. 
     * @param model the ChartDataModel
     * @param title the title String
     */
    public BufferedChartPanel(ChartDataModel model, String title) {
        this();
        chart = new BufferedChart(model, title);

        // Listens for redraw requests
        chart.addRenderChangeListener(this);
    }

    /** Creates a new ChartPanel with the given model
     * and title string and a coordinate system.
     * @param model the ChartDataModel
     * @param title the title String
     * @param coord the id of the coordinate system configuration
     */
    public BufferedChartPanel(ChartDataModel model, String title, int coord) {
        this();
        chart = new BufferedChart(model, title, coord);
        
        // Listens for redraw requests
        chart.addRenderChangeListener(this);
    }

    /** Paints the ChartPanel. Calls <code>chart.render((Graphics2D)graphics)</code>
     * @param graphics the Graphics2D object to paint in
     */
    public void paint(Graphics graphics) {
    	super.paint(graphics);
    	
        /* For selction rectangles */
        if (currentRect != null) {
        	graphics.setXORMode(Color.white); 
            //depending on image colors
        	graphics.drawRect(grRect.x, grRect.y, 
        			grRect.width - 1, grRect.height - 1);
        }
    }

    /** Registers a mouse dragged event and updates the size of the 
     * selection rectangle being drawn on the panel if appropriate.
     * 
     */
    public void mouseDragged(MouseEvent e){
    	//System.out.print(e.getX());
    	//System.out.print(",");
    	//System.out.println(e.getY());
    	updateSize(e);
    }
    
    /** Registers the first press of a mouse button for selection purposes
     */
    public void mousePressed(MouseEvent e) {
    	currentRect = new Rectangle(e.getX(), e.getY(), 0, 0);
    	updateSize(e);
    	//updateDrawableRect(getWidth(), getHeight());
    }
    
    /** Registers the release of the mouse button and disables selection mode
     */
    public void mouseReleased(MouseEvent e) {
    	updateSize(e);
    	currentRect = null;
    	repaint();
    }
    
    /** Updates the current selection rectangle on the chart if appropriate.  This
     * routine should only be called from the MouseListener functions.
     * 
     * @param e the mouse event that caused the update function to be called
     */
    private void updateSize(MouseEvent e) {
    	int width = e.getX() - currentRect.x;
    	int height = e.getY() - currentRect.y;
    	
    	currentRect.setSize(width,height);
    	
    	if(currentRect.width < 0 || currentRect.height < 0 ||
    	   grRect == null) {
    		grRect = new Rectangle(Math.min(currentRect.x,currentRect.x+width),
    								 Math.min(currentRect.y,currentRect.y+height),
    								 Math.abs(width),Math.abs(height));
    	} else {
    		grRect.setSize(width,height);
    	}
    	
    	repaint();
    }

    /** Listens for RenderChangeEvents from the chart.  When encountered,
     * the panel will request a redraw to ensure th echart appears.
     *
     * @param evt the event object
     */    
    public void renderUpdateRequested(RenderChangeEvent evt) {
    	this.repaint();
    }
    
    /** Pass-through to the chart object to determine if buffering is
     * enabled.
     *
     * @return true if buffering is enabled, false otherwise
     */
    public boolean isBuffering() {
    	return ((BufferedChart)chart).isBuffering();
    }
    
    /** Pass-through to the chart to enable/disable buffered drawing
     *
     * @param value true to enable buffering, false otherwise
     */
    public void setBuffering(boolean value) {
    	((BufferedChart)chart).setBuffering(value);
    }
    
    /** Implements the Printable print interface.  Causes the panel's chart to
     * be printed at screen size based on the automatically passed print arguments.
     * 
     */
    public int print(Graphics g, PageFormat pf, int page) throws PrinterException {
    	boolean buf = this.isBuffering();
    	this.setBuffering(false);
    	int ret_val = super.print(g, pf, page);
    	this.setBuffering(buf);
    	return ret_val;
    }

	@Override
	public boolean isOptimizedDrawingEnabled() {
		return false;
	}
    
}
