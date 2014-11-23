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
 
    DefaultChart.java
    Created on 26. Juni 2001, 22:49
 */

package com.approximatrix.charting;

import java.util.*;
import java.awt.Rectangle;
import java.awt.Graphics2D;
import java.awt.Dimension;
import java.awt.Color;

import com.approximatrix.charting.coordsystem.CoordSystem;
import com.approximatrix.charting.coordsystem.CoordSystemUtilities;
import com.approximatrix.charting.model.ChartDataModel;
import com.approximatrix.charting.render.AbstractChartRenderer;
import com.approximatrix.charting.render.RowColorModel;


/** The Default class to create a chart.
 * @author mueller
 * @version 1.0
 */
public class DefaultChart extends AbstractChart {
    
    public static int LINEAR_X_LINEAR_Y = 0;
    public static int NO_COORDINATE_SYSTEM = 1;
    
    /** Creates new empty DefaultChart.*/
    protected DefaultChart() {
    }
    
    /** Creates a new DefaultChart with the given model
     * and title string and no coordinate system.
     * @param model the ChartDataModel
     * @param title the title String
     */
    public DefaultChart(ChartDataModel model, String title) {
        this();
        setChartDataModel(model);
        setRowColorModel(new RowColorModel(model));
        
        setLegend(new Legend(getRowColorModel()));
        setTitle(new Title(title));
    }
    
    /** Creates a new DefaultChart with the given model
     * and title string and a coordinate system.
     * @param model the ChartDataModel
     * @param title the title String
     * @param coord the id of the coordinate system configuration
     */
    public DefaultChart(ChartDataModel model, String title, int coord) {
        this(model, title);
        
        if(coord == DefaultChart.LINEAR_X_LINEAR_Y)
            this.setCoordSystem(CoordSystemUtilities.BuildDefaultCoordSystem(model));
    }
    
    /** Creates a new DefaultChart with the given model
     * and title string and a coordinate system.
     * 
     * @param model the ChartDataModel
     * @param title the title String
     * @param coord the id of the coordinate system configuration
     * @param xaxis the x-axis' unit
     * @param yaxis the y-axis' unit
     */
    public DefaultChart(ChartDataModel model, String title, int coord, 
                        String xaxis, String yaxis) {
        this(model, title, coord);
        
        this.getCoordSystem().setXAxisUnit(xaxis);
        this.getCoordSystem().setYAxisUnit(yaxis);
    }
    
    /** Should compute the preferred size of the Chart
     * @return <CODE>null</CODE>
     */    
    public Dimension getPreferredSize() {
        return null;
    }
    
    /** Does the layout of the title, legend and coordinate system and
     * calls the render method of all those including the ChartRenderers.
     * @param g the <CODE>Graphics2D</CODE> object to paint in.
     */    
    public void render(Graphics2D g) {
        
        //g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, 
        //                   RenderingHints.VALUE_ANTIALIAS_ON);
        
        
        int width = (int)getBounds().getWidth();
        int height = (int)getBounds().getHeight(); 
        
        Title t = getTitle();
        Legend l = getLegend();
        CoordSystem c = getCoordSystem();
        Collection renderer = getChartRenderer().values();
        
        g.setColor(Color.white);
        g.fillRect(0, 0, width, height);
        
        g.setColor(Color.black);
                
        int titleheight = MISSING_TITLE_HEIGHT;
        int legendwidth = MISSING_LEGEND_WIDTH;
        
        if(t != null) {
            Dimension title = t.getPreferredSize();
            t.setBounds(new Rectangle((int)(width/2
                                      - (int)(title.getWidth()/2)), 
                                      0, 
                                      (int)title.getWidth(), 
                                      (int)title.getHeight()));
            t.render(g);
            titleheight  = (int)t.getBounds().getHeight();
        }
        if(l != null) {
            Dimension legend = l.getPreferredSize();
            l.setBounds(new Rectangle((int)(width - legend.getWidth()), 
                                      (int)(height/2 - legend.getHeight()/2 + titleheight),
                                      (int)legend.getWidth(),
                                      (int)legend.getHeight()));
            l.render(g);
            legendwidth = (int)l.getBounds().getWidth();
        }
        if(c != null) {
            c.setBounds(new Rectangle(0, (int)titleheight, 
                                      (int)(width - legendwidth), 
                                      (int)(height - titleheight)));
        }
        if(! renderer.isEmpty()) {
            Iterator i = renderer.iterator();
            while(i.hasNext()) {
                AbstractChartRenderer cr = (AbstractChartRenderer)i.next();
                
                // This routine has been modified by jba to
                // allow for changing the clipping to all be within the Axes.
                //int xoffset = 27;
                //xoffset = c.getLeftMargin();
				
                //cr.setBounds(new Rectangle(xoffset, (int)titleheight, 
                //                           (int)(width - legendwidth)-xoffset, 
                //                           (int)(height - titleheight) - 5));
                
                // cr.setPointToPixelTranslator()
                cr.setBounds(c.getInnerBounds());
                
                cr.render(g);
            }
        }
        if(c != null)
            c.render(g);
                
		/*
        g.setColor(Color.pink);
		
        g.draw(t.getBounds());
        g.draw(l.getBounds());
        g.draw(c.getBounds());
        g.draw(c.getInnerBounds());
		*/
        //System.out.println("** Bounds: "+c.getBounds());
        //System.out.println("** InnerBounds: "+c.getInnerBounds());
    }
 
}
