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

    ExtendedChartPanel.java
*/

package com.approximatrix.charting.swing;

import javax.swing.JPanel;

import com.approximatrix.charting.Chart;
import com.approximatrix.charting.coordsystem.CoordSystem;
import com.approximatrix.charting.ExtendedChart;
import com.approximatrix.charting.GenericChart;
import com.approximatrix.charting.Legend;
import com.approximatrix.charting.Title;
import com.approximatrix.charting.event.*;
import com.approximatrix.charting.model.ChartDataModel;
import com.approximatrix.charting.model.ScatterDataModel;
import com.approximatrix.charting.render.AbstractChartRenderer;

import java.awt.geom.Rectangle2D;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.awt.Rectangle;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Dimension;
import java.util.Map;

import java.awt.event.*;
import java.awt.Color;

/**
 * This Panel provides the possibility to include a Chart into a Swing 
 * Application. I choose not to make every Chart extend JComponent because
 * of the overhead this would have meant. Instead, this class is an adaptor.
 * It implements the Chart interface and contains a DefaultChart instance
 * to which all Chart calls are promoted.
 * @author  mueller, armstrong
 */
public class ExtendedChartPanel extends AbstractChartPanel {
	
	/** Boolean allowing/disallowing zoom */
	private boolean zoom_enabled = false;
	
	/** Mouse button used for zooming */
	private int zoom_button = MouseEvent.BUTTON1;
	
	/** Mouse button for restoring autoscale */
	private int autoscale_button = MouseEvent.BUTTON3;
	
	/** The selection rectangle for processing purposes */
	private Rectangle currentRect = null;
	
	/** The selection rectangle for actual drawing purposes */
	private Rectangle grRect = null;
	
    /** The chart instance to which all method calls are promoted.*/
	//private ExtendedChart chart;
    
    /** Creates new ChartPanel */
    private ExtendedChartPanel() {
    	super();
    }
    
    /** Creates a new ChartPanel with the given model
     * and title string. 
     * @param model the ChartDataModel
     * @param title the title String
     */
    public ExtendedChartPanel(ChartDataModel model, String title) {
        this();
        chart = new ExtendedChart(model, title);
    }

    /** Creates a new ChartPanel with the given model
     * and title string and a coordinate system.
     * @param model the ChartDataModel
     * @param title the title String
     * @param coord the id of the coordinate system configuration
     */
    public ExtendedChartPanel(ChartDataModel model, String title, int coord) {
        this();
        chart = new ExtendedChart(model, title, coord);
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
        // System.out.print("Paint Requested: ");
        // System.out.println(graphics.getClipBounds());
    }
    
    /** Does the layout of the title, legend and coordinate system and
     * calls the render method of all those including the ChartRenderers.
     * @param g the <CODE>Graphics2D</CODE> object to paint in.
     * Just calls paint(Graphics).
     */
    public void render(Graphics2D g) {
        paint(g);
    }
   
    /** Calls the JPanel's paintComponent(g) method only
     */
    public void paintComponent(Graphics g) {
    	super.paintComponent(g);
    	//System.out.println(g.getClipBounds());
        //paint(g);
    }

    /** Registers a mouse dragged event and updates the size of the 
     * selection rectangle being drawn on the panel if appropriate.
     * 
     */
    public void mouseDragged(MouseEvent e){
    	if(!zoom_enabled) return;
    	
    	// This will return immediately if the zoom rectangle is null,
    	// so no safety checking is necessary.
    	updateSize(e);	
    }

    /** Registers the first press of a mouse button for selection purposes
     */
    public void mousePressed(MouseEvent e) {
    	if(!zoom_enabled) return;
    	
    	if(e.getButton() == zoom_button) {
    		currentRect = new Rectangle(e.getX(), e.getY(), 0, 0);
    		grRect = new Rectangle(e.getX(), e.getY(), 0, 0);
    	}
    	//updateSize(e);
    	//updateDrawableRect(getWidth(), getHeight());
    }
    
    /** Registers the release of the mouse button and disables selection mode
     */
    public void mouseReleased(MouseEvent e) {
    	if(!zoom_enabled) return;
    	
    	if(e.getButton() == zoom_button) {
    		updateSize(e);
    		if(chart instanceof ExtendedChart && !(grRect.getWidth() == 0.0 || grRect.getHeight() == 0)) {
    			ExtendedChart echart = (ExtendedChart)chart;
    			echart.getChartDataModel().setAutoScale(false);
    			echart.zoom(grRect);
    		}
    		currentRect = null;
    		repaint();
    	}
    }
    
    /** Registers a clicked event, and, if zoom is enabled, will return the graph to
     * autoscaling if the button causing the event is equivalent to autoscale_button.
     */
    public void mouseClicked(MouseEvent e) {
    	if(!zoom_enabled) return;
    	
    	if(e.getButton() == autoscale_button ) {
    		performAutoScale();
    	}
    }
    
    /** Performs an autoscale, but does not perform any disabling of zooming
     * of the plot panel.
     *
     */
    public void performAutoScale() {
    	chart.getChartDataModel().setAutoScale(true);
		chart.getChartDataModel().fireChartDataModelChangedEvent(this);
		repaint();
    }
    
    /** Allows mouse selection for zooming
     * 
     * @param value true to allow dynamic zoom, false otherwise
     */
    public void enableZoom(boolean value) {
    	zoom_enabled = value;
    }
    
    /** Returns whether or not dynamic zoom is enabled
     * 
     * @return true if zoom is enabled, false otherwise
     */
    public boolean isEnableZoom() {
    	return zoom_enabled;
    }
    
    /** Sets the mouse button used to register a return to autoscaling by clicking.
     * Use NaN to disable this feature.
     * 
     * @param button the button to use, usually taken from MouseEvent.{BUTTON1, BUTTON2, BUTTON3}
     */
    public void setAutoscaleMouseButton(int button) {
    	autoscale_button = button;
    }
    
    /** Sets the mouse button used fo dynamic scaling.
     * Use NaN to disable this feature.
     * 
     * @param button the button to use, usually taken from MouseEvent.{BUTTON1, BUTTON2, BUTTON3}
     */
    public void setZoomMouseButton(int button) {
    	zoom_button = button;
    }
    
    /** Updates the zoom rectangles to draw based on a passed-in MouseEvent.  This routine
     * exit immediately if there is currently no rectangle to zoom with or zooming is not
     * enabled.
     * 
     * @param e a MouseEvent from which to extract data
     */
    private void updateSize(MouseEvent e) {
    	
    	if(currentRect == null || (!zoom_enabled)) return;
    	
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
    
    /** Implements the Printable print interface.  Causes the panel's chart to
     * be printed at screen size based on the automatically passed print arguments.
     * 
     */
    public int print(Graphics g, PageFormat pf, int page) throws PrinterException {
    	int x = (int)pf.getImageableX();
        int y = (int)pf.getImageableY();
        
        chart.setBounds(new Rectangle(0,0,(int)pf.getImageableWidth(),(int)pf.getImageableHeight()));        
        g.translate(x, y);
        
        if (page == 0) {
        	this.paint(g);
          	repaint();
          	return Printable.PAGE_EXISTS;
        } else {
        	repaint();
        	return Printable.NO_SUCH_PAGE;
        }
    }
    
}
