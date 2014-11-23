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

package com.approximatrix.charting;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.util.Collection;
import java.util.Iterator;

import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;

import com.approximatrix.charting.coordsystem.CoordSystem;
import com.approximatrix.charting.coordsystem.CoordSystemUtilities;
import com.approximatrix.charting.model.ChartDataModel;
import com.approximatrix.charting.render.AbstractChartRenderer;
import com.approximatrix.charting.render.RowColorModel;

public class ExtendedChart extends AbstractChart {
	   
	/** Constant to specify a linear xy coordinate system during chart
	 * construction
	 */
    public static final int LINEAR_X_LINEAR_Y = 0;
    
    /** Present for future expansion */
    public static final int NO_COORDINATE_SYSTEM = 1;
    
    /** Creates new empty DefaultChart.*/
    protected ExtendedChart() {
    }
    
    /** Creates a new DefaultChart with the given model
     * and title string and no coordinate system.
     * @param model the ChartDataModel
     * @param title the title String
     */
    public ExtendedChart(ChartDataModel model, String title) {
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
    public ExtendedChart(ChartDataModel model, String title, int coord) {
        this(model, title);
        
        if(coord == ExtendedChart.LINEAR_X_LINEAR_Y)
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
    public ExtendedChart(ChartDataModel model, String title, int coord, 
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
    
    /** Zooms in on a specified rectangle area of the chart by setting the max and
     * min values to display.  This routine will have no effect if autoscaling is
     * enabled.  The area to zoom to is specified in terms of the graphics coordinates
     * (the transform to data coordinates is performed here).
     * 
     * @param area A Rectangle specifying the area in graphics coordinates to zoom 
     */
    public void zoom(Rectangle area) {
    	//System.out.println("Zoom Request received");
    	//System.out.println(area);

    	double[] points = {area.x,area.y,area.x+area.width,area.y+area.height};
    	double[] data_points = {0.0,0.0,0.0,0.0};
    	
    	if(area.width == 0 || area.height == 0.0) return;

    	Collection renderer = getChartRenderer().values();
    	if(! renderer.isEmpty()) {
            Iterator i = renderer.iterator();
            while(i.hasNext()) {
                
            	AbstractChartRenderer cr = (AbstractChartRenderer)i.next();
                AffineTransform transform = cr.getTransform(0);
                
                try {
                	transform.inverseTransform(points,0,data_points,0,2);
                	
                	ChartDataModel dm = (cr.getChartDataModel());

                	// Must be set externally
                	//dm.setAutoScale(false);
                	//dm.setManualScale(true);

                	//dm.setMinimumValue(new Double(data_points[3]));
                	//dm.setMaximumValue(new Double(data_points[1]));
                	//dm.setMaximumColumnValue(data_points[2]);
                	//dm.setMinimumColumnValue(data_points[0]);
                	
                	dm.setMaximumValueX(new Double(data_points[2]));
                	dm.setMinimumValueX(new Double(data_points[0]));
                	dm.setMaximumValueY(new Double(data_points[1]));
                	dm.setMinimumValueY(new Double(data_points[3]));
                	
                	dm.fireChartDataModelChangedEvent(this);
                	
                } catch (NoninvertibleTransformException nite) {
                	
                }                
            }
    	}
    	
    }
 
}
