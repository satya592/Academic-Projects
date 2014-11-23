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
 
    BufferedChart.java
    Created on 26. Juni 2001, 22:49
 */

package com.approximatrix.charting;

import java.util.*;
import java.awt.geom.Rectangle2D;
import java.awt.Rectangle;
import java.awt.Graphics2D;
import java.awt.Dimension;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.awt.Font;
import java.awt.font.FontRenderContext;
import java.awt.font.TextLayout;

import com.approximatrix.charting.coordsystem.ClassicCoordSystem;
import com.approximatrix.charting.coordsystem.CoordSystem;
import com.approximatrix.charting.coordsystem.CoordSystemUtilities;
import com.approximatrix.charting.model.ChartDataModel;
import com.approximatrix.charting.render.Renderer;
import com.approximatrix.charting.render.AbstractChartRenderer;
import com.approximatrix.charting.render.RowColorModel;
import com.approximatrix.charting.event.RenderChangeListener;
import com.approximatrix.charting.event.RenderChangeEvent;
import com.approximatrix.charting.event.ChartDataModelListener;
import com.approximatrix.charting.event.ChartDataModelEvent;

/** BufferedChart creates a chart that renders only once to a buffered image,
 * then simply displays the image rather than performing a full chart
 * rendering.  This technique improves performance by only re-rendering the
 * chart when absolutely necessary.
 *
 * @author armstrong
 * @version 1.0
 */
public class BufferedChart extends AbstractChart implements RenderChangeListener, ChartDataModelListener {
    
    public static int LINEAR_X_LINEAR_Y = 0;
    public static int NO_COORDINATE_SYSTEM = 1;
    
    private static final Class DEFAULT_COORDSYS = ClassicCoordSystem.class; 

    /** A flag used for debugging only */
    private static final boolean DEBUG_CHART = false;
    
	/** The String displayed during rendering work */
	protected String render_message = "Rendering...";

	/** The font to display the render message */
    protected Font font = new Font("Helvetica", Font.PLAIN, 22);

	/** The rendering thread to perform all drawing */
	private RenderThread rt = null;

	/** The image buffer holding the current rendering of the chart */
	private BufferedImage buffer = null;

	/** The second buffer where the separate thread will draw */
	private BufferedImage drawbuffer = null;
    
    /** Flag indicating a redraw may be necessary */
    private boolean redraw = true;
    
    /** Flag indicating buffering is enabled */
    private boolean enableBuffering = true;
    
    /** Creates new empty DefaultChart.*/
    protected BufferedChart() {
    	super();
    }
    
    /** Creates a new BufferedChart with the given model
     * and title string and no coordinate system.
     * @param model the ChartDataModel
     * @param title the title String
     */
    public BufferedChart(ChartDataModel model, String title) {
        this();
        setChartDataModel(model);
        setRowColorModel(new RowColorModel(model));
        
        setLegend(new Legend(getRowColorModel()));
        setTitle(new Title(title));
        this.initialize();
    }
    
    /** Creates a new BufferedChart with the given model
     * and title string and a coordinate system.
     * @param model the ChartDataModel
     * @param title the title String
     * @param coord the id of the coordinate system configuration
     */
    public BufferedChart(ChartDataModel model, String title, int coord) {
        this(model, title);
        
        if(coord == BufferedChart.LINEAR_X_LINEAR_Y)
        	this.setCoordSystem(CoordSystemUtilities.BuildDefaultCoordSystem(model));
        
        this.initialize();
    }
    
    /** Creates a new BufferedChart with the given model
     * and title string and a coordinate system.
     * 
     * @param model the ChartDataModel
     * @param title the title String
     * @param coord the id of the coordinate system configuration
     * @param xaxis the x-axis' unit
     * @param yaxis the y-axis' unit
     */
    public BufferedChart(ChartDataModel model, String title, int coord, 
                        String xaxis, String yaxis) {
        this(model, title, coord);
        
        this.getCoordSystem().setXAxisUnit(xaxis);
        this.getCoordSystem().setYAxisUnit(yaxis);
        this.initialize();
    }
    
    /** Central calling method for initializing the specifics of this
     * bufferd chart.
     */
    private void initialize() {
    	this.assignListeners();
    	this.setBounds(new Rectangle(1,1));
    	this.initBuffer();
    }
    
    /** Assigns this chart as the listener for all RenderChange events
     */
    private void assignListeners() {

    	ArrayList<Renderer> components = new ArrayList<Renderer>();
    	
    	components.add(super.legend);
    	components.add(super.coord);
    	components.add(super.title);
    	
    	Collection<AbstractChartRenderer> chartrenderers = this.getChartRenderer().values();
    	components.addAll(chartrenderers);
    	
    	// Add this as a listener for all renderers
    	for(Renderer comp : components) {
    		if(comp != null)
    			comp.addRenderChangeListener(this);
    	}
    	
    	// Add this as a data model listener to all chart data models
    	for(AbstractChartRenderer cr : chartrenderers) {
    		if(cr != null) {
    			if(cr.getChartDataModel() != null)
    				cr.getChartDataModel().addChartDataModelListener(this);
    		}
    	}
    }
    
    /** Should compute the preferred size of the Chart
     * @return <CODE>null</CODE>
     */    
    public Dimension getPreferredSize() {
        return null;
    }

	/** Initializes the image buffer based on current bounds
	 */
	private void initBuffer() {
		buffer = createBuffer();
		this.drawNotification();
	}
	
	/** Returns a new BufferedImage that is sized to be the same as this
	 * component's bounds.
	 * 
	 * @return a new, blank BufferedImage of type TYPE_INT_RGB
	 */
	private BufferedImage createBuffer() {
		int w = 1; int h = 1;
		if(this.getBounds() != null) {
			w = (int)this.getBounds().getWidth();
			h = (int)this.getBounds().getHeight();
		}
		return new BufferedImage(w,h,BufferedImage.TYPE_INT_RGB);
	}

	/** Determines if the current buffer is the wrong size
	 *
	 * @return true if the buffer needs to be reinitialized, false otherwise
	 */
	private boolean invalidBuffer() {
		if(buffer == null) 
			return true;
		if(this.getBounds() == null)
			return true;
		return (buffer.getWidth() != (int)this.getBounds().getWidth()) ||
		       (buffer.getHeight() != (int)this.getBounds().getHeight());
	}
    
    /** Sets whether rendering should be buffered for speed
     *
     * @param value true to enable buffering, false otherwise
     */
    public void setBuffering(boolean value) {
    	enableBuffering = value;
    }
    
    /** Returns whether buffering is currently enabled
     *
     * @return true is buffering is on, false otherwise
     */
    public boolean isBuffering() {
    	return enableBuffering;
    }
	
	/** Returns the font used to display the rendering message.
	 * 
	 * @return the current font all messages are displayed in
	 */
	public Font getFont() {
		return this.font;
	}
	
	/** Sets the font which messages are rendered int
	 *
	 * @param newfont the new font to use for rendering messages
	 */
	public void setFont(Font newfont) {
		this.font = newfont;
	}

	/** Returns the current message stating that rendering is occuring
	 *
	 * @return a String to be displayed while the render thread is busy
	 */	
	public String getRenderMessage() {
		return this.render_message;
	}
	
	/** Sets the current rendering message to be displayed while the render
	 * thread is running
	 *
	 * @param value a valid (non-null) String to be displayed during rendering
	 */
	public void setRenderMessage(String value) {
		if(value != null)
			this.render_message = value;
	}
	
	/** Called by the render thread to notify its parent that
	 * rendering of the chart is complete and available on the
	 * drawBuffer
	 */
	public void notifyDisplayReady(boolean success) {
		if(success) {
			this.buffer = this.drawbuffer;
			this.drawbuffer = null;
		} else {
			this.redraw = true;
			if(DEBUG_CHART)
				System.err.println("Error during BufferedChart display update - null BI");
		}
		
		// Notify any listeners that the chart contents have changed
		this.fireRenderChangeEvent();
	}
		
	/** The central rendering routine that handles all drawing operations.
	 * The routine will launch the render thread if buffering is enabled and
	 * an update is needed (this.redraw = true).  Otherwise, it just plots
	 * either the buffered image (if buffeering is enabled) or draws 
	 * directly to the passed graphics object.
	 *
	 * @param g the actual display Graphics2D object on which to be painted
	 */    
    public void render(Graphics2D g) {
    	if(this.invalidBuffer()) {
    		this.initBuffer();
    		this.redraw = true;
    	}
    	
    	if(enableBuffering) {
	    	if(redraw) {
    			// Draw handling
    			this.drawbuffer = this.createBuffer();
    			
    			// Destroy existing render threads
				if(this.rt != null) {
					if(this.rt.isAlive()) {
						this.rt.stopRender();
						try {
							this.rt.join();
						} catch(InterruptedException ie) {
							System.err.println("Rendering Thread Join Failed!");
							ie.printStackTrace();
						}
					}
					this.rt = null;
				}
				
				// Draw a notification on the current display
				
				// Create a new render thread
				this.rt = new RenderThread(this.drawbuffer, this);
				this.rt.start();
    			
	    	} 
	    	redraw = false;
    		this.drawBufferImage(g);
	    	
	    // Buffering is disabled - perform the draw directly onto the
	    // passed Graphics2D object
	    } else {
	    	this.drawChart(g);
	    }
    	
    }

	/** Draws a notification that rendering is in process on the buffer image
	 */
	private void drawNotification() {

		Graphics2D g = this.buffer.createGraphics();

        int width = (int)getBounds().getWidth();
        int height = (int)getBounds().getHeight(); 
        
        g.setColor(Color.white);
        g.fillRect(0, 0, width, height);
        
        g.setColor(Color.gray);
        TextLayout layout = new TextLayout(this.getRenderMessage(), this.getFont(), 
                                           new FontRenderContext(null, true, false));
        Rectangle2D tb = layout.getBounds();
        
        layout.draw(g, (float)((width - tb.getWidth())/2.0), (float)((height - tb.getHeight())/2.0));
	}
    
    /** Draws the current image buffer onto the specified Graphics2D object
     *
     * @param g the target Graphics2D object
     */
    private void drawBufferImage(Graphics2D g) {
        //int width = (int)getBounds().getWidth();
        //int height = (int)getBounds().getHeight(); 
		
		// Fill in background just to be sure
        //g.setColor(Color.white);
        //g.fillRect(0, 0, width, height);
    
    	// Render the image
    	g.drawImage(buffer,0,0,null);
    }
    
    /** Does the layout of the title, legend and coordinate system and
     * calls the render method of all those including the ChartRenderers.
     * @param g the <CODE>Graphics2D</CODE> object to paint in.
     */    
    public void drawChart(Graphics2D g) {
        
        //g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, 
        //                   RenderingHints.VALUE_ANTIALIAS_ON);
        
        
        int width = (int)getBounds().getWidth();
        int height = (int)getBounds().getHeight(); 
        
        Title t = getTitle();
        Legend l = getLegend();
        CoordSystem c = getCoordSystem();
        Collection<AbstractChartRenderer> renderer = getChartRenderer().values();
        for(AbstractChartRenderer cr : renderer)
        	cr.resetStopFlag();
        
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
        for(AbstractChartRenderer cr : renderer) {

        	// This routine has been modified by jba to
        	// allow for changing the clipping to all be within the Axes.
        	//int xoffset = 27;
        	//xoffset = c.getLeftMargin();

        	//cr.setBounds(new Rectangle(xoffset, (int)titleheight, 
        	//		(int)(width - legendwidth)-xoffset, 
        	//		(int)(height - titleheight) - 5));

        	// cr.setPointToPixelTranslator()

        	cr.setBounds(c.getInnerBounds());
        	
        	cr.render(g);
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
    
    /** This method is called, whenever an event is created.  The routine
     * simply flips the redraw flag to true.  The actual redraw is within
     * the render method.
     *
     * @param evt the event object
     */    
    public void renderUpdateRequested(RenderChangeEvent evt) {
    	this.redraw = true;
		this.fireRenderChangeEvent();
		//System.out.println("*** RCE *** "+evt.getSource().getClass().getName());
    }

    /** This method is called, whenever an event is created.
     * @param evt the event object
     */    
    public void chartDataChanged(ChartDataModelEvent evt) {
    	this.redraw = true;
		this.fireRenderChangeEvent();
		//System.out.println("*** CDC *** "+evt.getSource().getClass().getName());
    }


	/** Private class for handling drawing the chart on a separate thread.
	 * The class is simply passed an image on which to draw and a parent
	 * to notify at completiong.  The thread will simply exit when
	 * interrupted 
	 */
	private class RenderThread extends Thread {

		/** Flag to block drawing notify in this particular thread */
		private volatile boolean renderingStoppped = false;
		
		/** The target image to draw to */
		private BufferedImage bi = null;
		
		/** The parent to notify when work is complete */
		private BufferedChart parent = null;
		
		/** Standard constructor accpeting the target image and the
		 * parent object to notify upon completion of the rendering 
		 * work.
		 *
		 * @param image the image on which to draw
		 * @param notify a BufferedChart object to notify when complete
		 */
		public RenderThread(BufferedImage image, BufferedChart notify) {
			this.bi = image;
			this.parent = notify;
		}
		
		/** The actual drawing procedures for the rendering
		 */
		public void run() {

			if(this.bi != null) {
				// Create 2D graphics for drawing
				Graphics2D g = this.bi.createGraphics();
				
				// Draw the chart to this graphics object
				parent.drawChart(g);

				// Once complete, issue notification
				if(!this.renderingStoppped)
					parent.notifyDisplayReady(true);
			} else {
				parent.notifyDisplayReady(false);
			}
		}
		
		public void stopRender() {
			Collection renderer = parent.getChartRenderer().values();
			Iterator i = renderer.iterator();
            while(i.hasNext()) {
                AbstractChartRenderer cr = (AbstractChartRenderer)i.next();
                cr.interruptRendering();
            }
            this.renderingStoppped = true;
		}
		
	}

	// Basic overrrides to ensure that listeners get asssigned and
	// redraws are forced on set/add methods
	
    /** Adds a ChartRenderer with a specific z-coordinate.
     * @param render the ChartRenderer
     * @param z the z-coordinate, the highest coordinate is in front.
     */
    public void addChartRenderer(AbstractChartRenderer render, int z) {
    	super.addChartRenderer(render,z);
    	this.assignListeners();
    	redraw = true;
		this.fireRenderChangeEvent();
    }

    /** Sets the Map with all ChartRenderers. The keys
     * have to be the z-coordinates of the ChartRenderers.
     * @param render a <CODE>java.util.Map</CODE> with all ChartRenderers.
     */
    public void setChartRenderer(Map render) {
    	super.setChartRenderer(render);
    	this.assignListeners();
    	redraw = true;
		this.fireRenderChangeEvent();
    }
    
    /** Sets the coordinate system for this chart,
     * which can be null if the ChartRenderer
     * doesn't need a coordinate system, e.g. if it's a
     * PieChart.
     * @param c the ClassicCoordSystem object
     */
    public void setCoordSystem(CoordSystem c) {
        super.setCoordSystem(c);
        this.assignListeners();
        redraw = true;
		this.fireRenderChangeEvent();
    }
    
    /** Sets the legend for this chart.
     * @param l the Legend
     */
    public void setLegend(Legend l) {
        super.setLegend(l);
        this.assignListeners();
        redraw = true;
		this.fireRenderChangeEvent();
    }
    
    /** Sets the title for this chart.
     * @param t the Title object
     */
    public void setTitle(Title t) {
        super.setTitle(t);
        this.assignListeners();
        redraw = true;
		this.fireRenderChangeEvent();
    }    
    
    /** Sets the RowColorModel for this chart.
     * @param rcm the new RowColorModel
     */
    public void setRowColorModel(RowColorModel rcm) {
       	super.setRowColorModel(rcm);
       	this.assignListeners();
       	redraw = true;
		this.fireRenderChangeEvent();
    }
        
    /** Stores the ChartDataModel for this Chart.
     * @param model the ChartDataModel
     */
    public void setChartDataModel(ChartDataModel model) {
        super.setChartDataModel(model);
        this.assignListeners();
        redraw = true;
		this.fireRenderChangeEvent();
    }

 
}
