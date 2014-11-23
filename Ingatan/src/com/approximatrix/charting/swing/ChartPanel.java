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

    ChartPanel.java
    Created on 6. September 2001, 14:10
*/

package com.approximatrix.charting.swing;

import javax.swing.JPanel;

import com.approximatrix.charting.Chart;
import com.approximatrix.charting.coordsystem.CoordSystem;
import com.approximatrix.charting.DefaultChart;
import com.approximatrix.charting.Legend;
import com.approximatrix.charting.Title;
import com.approximatrix.charting.event.*;
import com.approximatrix.charting.model.ChartDataModel;
import com.approximatrix.charting.render.AbstractChartRenderer;

import java.awt.geom.Rectangle2D;
import java.awt.Rectangle;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Dimension;
import java.util.Map;

import java.awt.event.*;
import java.awt.Color;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.awt.print.PageFormat;

/**
 * This Panel provides the possibility to include a Chart into a Swing 
 * Application. I choose not to make every Chart extend JComponent because
 * of the overhead this would have meant. Instead, this class is an adaptor.
 * It implements the Chart interface and contains a DefaultChart instance
 * to which all Chart calls are promoted.
 * @author  mueller, armstrong
 */
public class ChartPanel extends AbstractChartPanel {

	/** The selection rectangle for processing purposes */
	private Rectangle currentRect = null;
	
	/** The selection rectangle for actual drawing purposes */
	private Rectangle grRect = null;
	
    /** Creates new ChartPanel */
    private ChartPanel() {
    	super();
    }
    
    /** Creates a new ChartPanel with the given model
     * and title string. 
     * @param model the ChartDataModel
     * @param title the title String
     */
    public ChartPanel(ChartDataModel model, String title) {
        this();
        chart = new DefaultChart(model, title);
    }

    /** Creates a new ChartPanel with the given model
     * and title string and a coordinate system.
     * @param model the ChartDataModel
     * @param title the title String
     * @param coord the id of the coordinate system configuration
     */
    public ChartPanel(ChartDataModel model, String title, int coord) {
        this();
        chart = new DefaultChart(model, title, coord);
    }
    
    /** Computes the preferred size of the ChartPanel.
     * @return <code>new java.awt.Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE)</code>
     */
    public Dimension getPreferredSize() {
        return new java.awt.Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE);
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
    
}
