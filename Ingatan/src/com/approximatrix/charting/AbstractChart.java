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
 
    AbstractChart.java
    Created on 22. Juni 2001, 00:05
 */

package com.approximatrix.charting;

import java.awt.Rectangle;
import java.awt.Graphics2D;
import java.util.Map;
import java.util.HashMap;
import java.util.Iterator;

import com.approximatrix.charting.coordsystem.CoordSystem;
import com.approximatrix.charting.model.ChartDataModel;
import com.approximatrix.charting.render.AbstractChartRenderer;
import com.approximatrix.charting.render.AbstractRenderer;
import com.approximatrix.charting.render.RowColorModel;

/** Implements the standard getter and setter methods for a chart.
 * @author mueller
 * @version 1.0
 */
public abstract class AbstractChart extends AbstractRenderer implements Chart {

	protected static final int MISSING_LEGEND_WIDTH = 30;
	protected static final int MISSING_TITLE_HEIGHT = 20;
	
    protected HashMap<Integer, AbstractChartRenderer> renderer = new HashMap<Integer, AbstractChartRenderer>();
    
    protected Rectangle bounds;
    protected Legend legend;
    protected CoordSystem coord;
    protected Title title;
    protected RowColorModel rcModel;
    
    protected ChartDataModel model;
    
    /** Creates new AbstractChart */
    public AbstractChart() {
    }

    /** Adds a ChartRenderer with a specific z-coordinate.
     * @param render the ChartRenderer
     * @param z the z-coordinate, the highest coordinate is in front.
     */
    public void addChartRenderer(AbstractChartRenderer render, int z) {
        renderer.put(new Integer(z), render);
        render.setRowColorModel(rcModel);
    }
    
    /** Returns the Map of all ChartRenderers.
     * @return a <CODE>java.util.Map</CODE> with all ChartRenderers
     */
    public Map<Integer, AbstractChartRenderer> getChartRenderer() {
        return renderer;
    }
    
    /** Returns the ChartRenderer with a specific z-coordinate.
     * @param z the z-coordinate
     * @return the ChartRenderer or null.
     */
    public AbstractChartRenderer getChartRenderer(int z) {
        return (AbstractChartRenderer)renderer.get(new Integer(z));
    }
    
    /** Returns the coordinate system.
     * @return a ClassicCoordSystem object or null if there is none.
     */
    public CoordSystem getCoordSystem() {
        return coord;
    }
    
    /** Returns this chart's legend.
     * @return the Legend object.
     */
    public Legend getLegend() {
        return legend;
    }
    
    /** Returns the title for this chart.
     * @return a Title object.
     */
    public Title getTitle() {
        return title;
    }
    
    /** Returns the RowColorModel for this chart.  Note that
     * this is simply the default row color model, and not 
     * necessarily the model used by all renderers.
     * 
     * @return a RowColorModel object.
     */
    public RowColorModel getRowColorModel() {
        return rcModel;
    }
    
    /** Sets the Map with all ChartRenderers. The keys
     * have to be the z-coordinates of the ChartRenderers.
     * @param render a <CODE>java.util.Map</CODE> with all ChartRenderers.
     */
    public void setChartRenderer(Map render) {
        Iterator i = render.values().iterator();
        int z = 0;
        while(i.hasNext()) {
            addChartRenderer((AbstractChartRenderer)i.next(), z);
        }
    }
    
    /** Sets the coordinate system for this chart,
     * which can be null if the ChartRenderer
     * doesn't need a coordinate system, e.g. if it's a
     * PieChart.
     * @param c the ClassicCoordSystem object
     */
    public void setCoordSystem(CoordSystem c) {
        coord = c;
    }
    
    /** Sets the legend for this chart.
     * @param l the Legend
     */
    public void setLegend(Legend l) {
        legend = l;
    }
    
    /** Sets the title for this chart.
     * @param t the Title object
     */
    public void setTitle(Title t) {
        title = t;
    }    
    
    /** Sets the RowColorModel for this chart.  Also updates any
     * renderers using the previous default model to this one.
     * 
     * @param rcm the new RowColorModel
     */
    public void setRowColorModel(RowColorModel rcm) throws NullPointerException {
    	// Cannot be null.
    	if(rcm == null) throw new NullPointerException();
    	
    	// First, walk through the map of renderers and determine if any
    	// are using the default row color model - they should be updated.
    	for(AbstractChartRenderer acr : getChartRenderer().values()) {
    		if(acr == null) continue;
    		if(acr.getRowColorModel() == this.rcModel) acr.setRowColorModel(rcm);
    	}
    	
    	// The legend should be updated as well, but again only if it is the
    	// same model as the default model.
    	if(getLegend() != null) {
    		if(getLegend().getRowColorModel() == this.rcModel)
    			getLegend().setRowColorModel(rcm);
    	}
    	
    	// Now store the default value 
        this.rcModel = rcm;
    }
        
    /** Stores the ChartDataModel for this Chart.
     * @param model the ChartDataModel
     */
    public void setChartDataModel(ChartDataModel model) {
        this.model = model;
    }
    
    /** Returns the ChartDataModel.
     * @return the ChartDataModel for the Chart
     */
    public ChartDataModel getChartDataModel() {
        return model;
    }
    
    /** Sets the Bounds for this Chart. This is important for rendering the chart and
     * always has to be done before rendering.
     * @param r the <CODE>Rectangle</CODE> object defining this chart's bounds.
     */
    public void setBounds(Rectangle r) {
        this.bounds = r;
    }
    
    /** Returns the Bounds for the Chart.
     * @return a <CODE>Rectangle</CODE> object defining this chart's bounds.
     */
    public Rectangle getBounds() {
        return bounds;
    }
    
    /** This method is called by the paint method to do the actual painting.
     * The painting is supposed to start at point (0,0) and the size is
     * always the same as the preferred size. The paint method performs
     * the possible scaling.
     * @param g the <CODE>Graphics2D</CODE> object to paint in.
     */
    public void paintDefault(Graphics2D g) {
    }
    
    /** Does the layout of the title, legend and coordinate system and
     * calls the render method of all those including the ChartRenderers.
     * @param g the <CODE>Graphics2D</CODE> object to paint in.
     */    
    public void render(Graphics2D g) {}
}
