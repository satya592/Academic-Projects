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

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.util.Map;

import javax.swing.JPanel;

import com.approximatrix.charting.AbstractChart;
import com.approximatrix.charting.Chart;
import com.approximatrix.charting.Legend;
import com.approximatrix.charting.Title;
import com.approximatrix.charting.coordsystem.CoordSystem;
import com.approximatrix.charting.model.ChartDataModel;
import com.approximatrix.charting.render.AbstractChartRenderer;
import com.approximatrix.charting.render.RowColorModel;

/** AbstractChartPanel provides an abstracted class for handling the variety of
 * Swing chart components that may be required.  This class itself implements almost
 * all of the required bethods for a complete charting panel for completeness, but
 * is of little use alone.
 * 
 * @author armstrong
 *
 */
public abstract class AbstractChartPanel extends JPanel 
	implements Chart, MouseMotionListener, MouseListener, Printable {

	/** Constant stating page exists for printing */
    public static final int PAGE_EXISTS = 0;
    
    /** Constant stating page does not exist for printing */
    public static final int NO_SUCH_PAGE = 1;
    
    /** The chart instance to which all method calls are promoted.*/
    protected AbstractChart chart;

    public AbstractChartPanel() {
    	addMouseMotionListener(this);
    	addMouseListener(this);
    }
    
    /** Adds a ChartRenderer with a specific z-coordinate.
     * @param renderer the ChartRenderer
     * @param z its z-coordinate.
     */
    public void addChartRenderer(AbstractChartRenderer renderer, int z) {
        chart.addChartRenderer(renderer, z);
    }    

    /** Returns the Bounds for the ChartPanel.
     * @return the bounds
     */
    public Rectangle getBounds() {
        return chart.getBounds();
    }    

    /** Returns the ChartDataModel.
     * @return the ChartDataModel
     */
    public ChartDataModel getChartDataModel() {
        return chart.getChartDataModel();
    }
    
    /** Returns the Map of all ChartRenderers.
     * @return the Map of Renderers.
     */
    public Map<Integer,AbstractChartRenderer> getChartRenderer() {
        return chart.getChartRenderer();
    }
    
    /** Returns the ChartRenderer with a specific z-coordinate.
     * @param z the z-coordinate of the desired ChartRenderer.
     * @return the ChartRenderer or <CODE>null</CODE> if none has been found.
     */
    public AbstractChartRenderer getChartRenderer(int z) {
        return chart.getChartRenderer(z);
    }
    
    /** Returns the coordinate system.
     * @return the Coordinate System for the Chart. Could be <CODE>null</CODE>.
     */
    public CoordSystem getCoordSystem() {
        return chart.getCoordSystem();
    }
    
    /** Returns this chart's legend.
     * @return the Legend for this Chart. Could be <CODE>null</CODE>.
     */
    public Legend getLegend() {
        return chart.getLegend();
    }
    
    /** Returns the title for this chart.
     * @return this Chart's Title. Could be <CODE>null</CODE>.
     */
    public Title getTitle() {
        return chart.getTitle();
    }
    
    /** Sets the Bounds for this Chart.
     * @param r the <CODE>Rectangle</CODE> object defining the bounds
     */
    public void setBounds(Rectangle r) {
        chart.setBounds(r);
    }
    
    /** Stores the ChartDataModel for this Chart.
     * @param model the ChartDataModel
     */
    public void setChartDataModel(ChartDataModel model) {
        chart.setChartDataModel(model);
    }
    
    /** Sets the Map with all ChartRenderers. The keys
     * have to be the z-coordinates of the ChartRenderers.
     * @param renderer The Map of ChartRenderers.
     */
    public void setChartRenderer(Map renderer) {
        chart.setChartRenderer(renderer);
    }
    
    /** Sets the coordinate system for this chart,
     * which can be null if the ChartRenderer
     * doesn't need a coordinate system, e.g. if it's a
     * PieChart.
     * @param c The Coordinate System for the Chart.
     */
    public void setCoordSystem(CoordSystem c) {
        chart.setCoordSystem(c);
    }
    
    /** Sets the legend for this chart.
     * @param l The Legend this Chart contains.
     */
    public void setLegend(Legend l) {
        chart.setLegend(l);
    }
    
    /** Sets the title for this chart.
     * @param t This Chart's Title.
     */
    public void setTitle(Title t) {
        chart.setTitle(t);
    }
    /** Computes the preferred size of the AbstractChartPanel.
     * @return <code>new java.awt.Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE)</code>
     */
    public Dimension getPreferredSize() {
        return new java.awt.Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE);
    }
    
    /** Paints the ChartPanel. Calls <code>chart.render((Graphics2D)graphics)</code>
     * @param graphics the Graphics2D object to paint in
     */
    public void paint(Graphics graphics) {
    	chart.setBounds(new Rectangle(this.getWidth(), this.getHeight()));
        chart.render((Graphics2D)graphics);
    }
    
    /** Does the layout of the title, legend and coordinate system and
     * calls the render method of all those including the ChartRenderers.
     * @param g the <CODE>Graphics2D</CODE> object to paint in.
     * Just calls paint(Graphics).
     */
    public void render(Graphics2D g) {
        paint(g);
    }
    
    /** Sets the chart's default row color model
     * 
     * @param rcm a valid RowColorModel object
     */
    public void setRowColorModel(RowColorModel rcm) throws NullPointerException {
    	chart.setRowColorModel(rcm);
    }
    
    /** Returns the default RowColorModel used by the chart
     * 
     * @return the chart's default row color model.
     */
    public RowColorModel getRowColorModel() {
    	return chart.getRowColorModel();
    }

    /** Registers a mouse dragged event (unused - for interface compatibility only)
     */
    public void mouseDragged(MouseEvent e){}
    
    /** Registers the first press of a mouse button (unused - for interface compatibility only)
     */
    public void mousePressed(MouseEvent e) {}
    
    /** Registers the release of the mouse button (unused - for interface compatibility only)
     */
    public void mouseReleased(MouseEvent e) {}
    
    /** Registers a mouse entered event (unused - for interface compatibility only)
     */
    public void mouseEntered(MouseEvent e) {}
    
    /** Registers a mouse exited event (unused - for interface compatibility only)
     */
    public void mouseExited(MouseEvent e) {}
    
    /** Registers a mouse clicked event (unused - for interface compatibility only)
     */
    public void mouseClicked(MouseEvent e) {}
    
    /** Registers a mouse moved event (unused - for interface compatibility only)
     */
    public void mouseMoved(MouseEvent e) {}
    
    /** Implements the Printable print interface.  Causes the panel's chart to
     * be printed at screen size based on the automatically passed print arguments.
     * 
     */
    public int print(Graphics g, PageFormat pf, int page) throws PrinterException {
    	int x = (int)pf.getImageableX();
        int y = (int)pf.getImageableY();
        g.translate(x, y);
        if (page == 0) {
          paint(g);
          return Printable.PAGE_EXISTS;
        } else {
          return Printable.NO_SUCH_PAGE;
        }
    }
    
}
