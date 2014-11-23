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
 
    AbstractCoordSystem.java 
 */

package com.approximatrix.charting.coordsystem;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.text.DecimalFormat;

import javax.swing.event.EventListenerList;

import com.approximatrix.charting.Axis;
import com.approximatrix.charting.event.RenderChangeEvent;
import com.approximatrix.charting.event.RenderChangeListener;
import com.approximatrix.charting.render.AbstractRenderer;
import com.approximatrix.charting.render.Renderer;

/** Implements some common components of a Coordinate System object.
 * Provides the render listener functionality, unit storage, decimal
 * formats for tick marks, fonts, bounds, and margin information. 
 * 
 * @author jarmstrong
 *
 */
public abstract class AbstractCoordSystem extends AbstractRenderer implements CoordSystem {

    /** The listener list. */
    protected EventListenerList listener = new EventListenerList();	
	
	/** The x-axis caption string. */
    protected String xaxis_unit = "x";
    
	/** The y-axis caption string. */
    protected String yaxis_unit = "y";
    
    /** DecimalFormat used throught on the Yaxis of the ClassicCoordSystem*/
    protected DecimalFormat dfY;
    
    /** DecimalFormat used throught on the Xaxis of the ClassicCoordSystem*/
    protected DecimalFormat dfX;
    
    /** The font used for labeling tick marks */
    protected Font tickFont = new Font("sans", Font.PLAIN, 10);
    
    /** The font used for axis units */
    protected Font unitFont = new Font("sans", Font.PLAIN, 10);
    
	/** The xaxis.*/
    protected Axis xaxis;
    
    /** The first y-axis. */
	protected Axis yaxis;
	
	/** The second y-axis. */
    protected Axis yaxis2;

    /** The left margin */
	protected int leftmargin = 75;
	
	/** The top margin. */
    protected int topmargin = 20;
	
	/** The right margin. */
    protected int rightmargin = 30;
    
	/** The bottom margin. */
    protected int bottommargin = 30;
    
    /** if true only the tick will be painted on the yaxis.  Alternately, if false, a 
     * light grey line will paint across the background of the chart.*/
    protected boolean shouldPaintOnlyTick = true;
    
    /** If true, the labels will be painted. If false, only the ticks will display. */
    protected boolean shouldPaintLabels = true;
    
    /** If true, a grid is painted along with the tick marks. */
    protected boolean shouldPaintGrid = false;
    
    /** If true, axes will be drawn, but nothing is drawn if false */
    protected boolean shouldPaintAxes = true;
    
	/** The bounds within which this object should be rendered. */
    Rectangle bounds = new Rectangle(0, 0, 
                                     Integer.MAX_VALUE, 
                                     Integer.MAX_VALUE);
    
	/** The multiplication matrix for the first y-axis and the x-axis. */
    protected AffineTransform y1transform;
	/** The multiplication matrix for the second y-axis and the x-axis. */
    protected AffineTransform y2transform;
    
    /** Flag to center labels for object x-axis between tick marks */
    protected boolean centerObjectLabelsBetweenTicks = true;
    
    /**
     * @deprecated Deprecated in version 1.4 - use getTickFont()
     */
	public Font getFont() {
		return getTickFont();
	}
	
	public Font getTickFont() {
		return tickFont;
	}

	/* (non-Javadoc)
	 * @see com.approximatrix.charting.coordsystem.CoordSystem#getUnitFont()
	 */
	public Font getUnitFont() {
		return unitFont;
	}

	public Axis getXAxis() {
		return xaxis;
	}

	public String getXAxisUnit() {
		return xaxis_unit;
	}

	public DecimalFormat getXDecimalFormat() {
		return dfX;
	}

	public String getYAxisUnit() {
		return yaxis_unit;
	}

	public DecimalFormat getYDecimalFormat() {
		return dfY;
	}

	public abstract void paintDefault(Graphics2D g);

	/* (non-Javadoc)
	 * @see com.approximatrix.charting.RenderingCoordSystem#render(java.awt.Graphics2D)
	 */
    public void render(Graphics2D g) {
        paintDefault(g);
    }

    public void setFont(Font f) {
		this.setTickFont(f);
	}
	
	public void setTickFont(Font f) {
		tickFont = f;
	}
	
	/** 
	 * @see com.approximatrix.charting.coordsystem.CoordSystem#setUnitFont(java.awt.Font)
	 */
	public void setUnitFont(Font f) {
		unitFont = f;
	}

	/* (non-Javadoc)
	 * @see com.approximatrix.charting.render.Renderer#getBounds()
	 */
	public Rectangle getBounds() {
		return bounds;
	}
    
	/* (non-Javadoc)
	 * @see com.approximatrix.charting.RenderingCoordSystem#getPreferredSize()
	 */
    public Dimension getPreferredSize() {
        return new Dimension(Integer.MIN_VALUE, Integer.MIN_VALUE);
    }
	
    /* (non-Javadoc)
	 * @see com.approximatrix.charting.RenderingCoordSystem#setBounds(java.awt.Rectangle)
	 */
    public void setBounds(Rectangle bounds) {
    	
    	// Return immediately if nothing is different
    	if(!this.isDifferentBounds(bounds))
    		return;
    	
    	this.bounds = bounds;
	
        setRightMargin(computeRightMargin());
        setLeftMargin(computeLeftMargin());
	
        setTopMargin(computeTopMargin());
        setBottomMargin(computeBottomMargin());
        
        xaxis.setLength((int)(bounds.getWidth()) - getLeftMargin() - getRightMargin());
        //System.out.println("** xaxis.length = "+xaxis.getLength());
        yaxis.setLength((int)(bounds.getHeight()) - getTopMargin() - getBottomMargin());
        //System.out.println("** yaxis.length = "+yaxis.getLength());
        
        if(yaxis2 != null) 
            yaxis2.setLength((int)(bounds.getHeight()) - getTopMargin() - getBottomMargin());
        
        setTransforms();
        this.fireRenderChangeEvent();
    }
    
    /** Sets axes transforms for this coordinate system
     */
    protected void setTransforms() {
    	setTransform(getDefaultTransform(FIRST_YAXIS), FIRST_YAXIS);
        if(yaxis2 != null)
            setTransform(getDefaultTransform(SECOND_YAXIS), SECOND_YAXIS);
    }
	
    /* (non-Javadoc)
	 * @see com.approximatrix.charting.RenderingCoordSystem#isPaintOnlyTick()
	 */
    public boolean isPaintOnlyTick() {
		return shouldPaintOnlyTick;
	}
   
    /* (non-Javadoc)
	 * @see com.approximatrix.charting.RenderingCoordSystem#isPaintLabels()
	 */
    public boolean isPaintLabels() {
        return shouldPaintLabels;
    }
    
    public boolean isPaintGrid() {
    	return shouldPaintGrid;
    }
    
    /* (non-Javadoc)
	 * @see com.approximatrix.charting.RenderingCoordSystem#setPaintGrid(boolean)
	 */
    public void setPaintGrid(boolean value) {
    	shouldPaintGrid = value;
        this.fireRenderChangeEvent();
    }
    
    /* (non-Javadoc)
	 * @see com.approximatrix.charting.RenderingCoordSystem#getPaintGrid()
	 */
    public boolean getPaintGrid() {
    	return shouldPaintGrid;
    }

    
    /* (non-Javadoc)
	 * @see com.approximatrix.charting.RenderingCoordSystem#setPaintLabels(boolean)
	 */ 
    public void setPaintLabels(boolean label) {
        shouldPaintLabels = label;
        this.fireRenderChangeEvent();
    }
    
    /* (non-Javadoc)
	 * @see com.approximatrix.charting.RenderingCoordSystem#isPaintAxes()
	 */
    public boolean isPaintAxes() {
    	return shouldPaintAxes;
    }
    
    /* (non-Javadoc)
	 * @see com.approximatrix.charting.RenderingCoordSystem#setPaintAxes(boolean)
	 */
    public void setPaintAxes(boolean axes) {
    	shouldPaintAxes = axes;
        this.fireRenderChangeEvent();
    }

	public void setXAxis(Axis a) {
		xaxis = a;
	}

    /* (non-Javadoc)
	 * @see com.approximatrix.charting.RenderingCoordSystem#setFirstYAxis(com.approximatrix.charting.Axis)
	 */
    public void setFirstYAxis(Axis a) {
        yaxis = a;
    }
    
    /* (non-Javadoc)
	 * @see com.approximatrix.charting.RenderingCoordSystem#getFirstYAxis()
	 */
    public Axis getFirstYAxis() {
        return yaxis;
    }
    
    /* (non-Javadoc)
	 * @see com.approximatrix.charting.RenderingCoordSystem#setSecondYAxis(com.approximatrix.charting.Axis)
	 */
    public void setSecondYAxis(Axis a) {
        yaxis2 = a;
    }
    
    /* (non-Javadoc)
	 * @see com.approximatrix.charting.RenderingCoordSystem#getSecondYAxis()
	 */
    public Axis getSecondYAxis() {
        return yaxis2;
    }
	
	/** Sets the X axis unit to be displayed along the x axis
	 * 
	 * @param xunit the text to display
	 */
	public void setXAxisUnit(String xunit) {
		xaxis_unit = xunit;
		this.fireRenderChangeEvent();
	}
	
	/** Sets the Y axis unit to be displayed along the y axis
	 * 
	 * @param yunit the text to display
	 */
	public void setYAxisUnit(String yunit) {
		yaxis_unit = yunit;
		this.fireRenderChangeEvent();
	} 

    /**
	 * @see com.approximatrix.charting.coordsystem.CoordSystem#setXDecimalFormat(java.text.DecimalFormat)
	 */
	public void setXDecimalFormat(DecimalFormat df) {
		dfX = df;
		this.fireRenderChangeEvent();
	}

	/**
	 * @see com.approximatrix.charting.coordsystem.CoordSystem#setYDecimalFormat(java.text.DecimalFormat)
	 */
	public void setYDecimalFormat(DecimalFormat df) {
		dfY = df;
		this.fireRenderChangeEvent();
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
	
    /** Returns the left margin. */
    protected int getLeftMargin() {
        return leftmargin;
    }
    
    /** Returns the right margin. */
    protected int getRightMargin() {
        return rightmargin;
    }
    
    /** Returns the top margin. */
    protected int getTopMargin() {
        return topmargin;
    }
    
    /** Returns the bottom margin. */
    protected int getBottomMargin() {
        return bottommargin;
    }
    
    /** Sets the left margin.
     * @param margin the new margin value 
     */
    protected void setLeftMargin(int margin) {
        leftmargin = margin;
    }
    
    /** Sets the right margin.
     * @param margin the new margin value 
     */
    protected void setRightMargin(int margin) {
        rightmargin = margin;
    }
    
    /** Sets the top margin.
     * @param margin the new margin value 
     */
    protected void setTopMargin(int margin) {
        topmargin = margin;
    } 
    
    /* (non-Javadoc)
	 * @see com.approximatrix.charting.RenderingCoordSystem#setBottomMargin(int)
	 */
    protected void setBottomMargin(int margin) {
        bottommargin = margin;
    } 
   	
    /** Computes the left margin.
     * @param margin the new margin value 
     */
    protected abstract int computeLeftMargin();
    
    /** Computes the right margin.
     * @param margin the new margin value 
     */
    protected abstract int computeRightMargin();
    
    /** Computes the top margin.
     * @param margin the new margin value 
     */
    protected abstract int computeTopMargin();
    
    /** Computes the bottom margin.
     * @param margin the new margin value 
     */
    protected abstract int computeBottomMargin();
    
    /* (non-Javadoc)
	 * @see com.approximatrix.charting.RenderingCoordSystem#getInnerBounds()
	 */
    public Rectangle getInnerBounds() {
        Rectangle b = getBounds();
        Rectangle i = new Rectangle((int)b.getX() + getLeftMargin() - 1,
        (int)b.getY() + getTopMargin() - 1,
        (int)b.getWidth() - (getLeftMargin() + getRightMargin()) + 2,
        (int)b.getHeight() - (getTopMargin() + getBottomMargin()) + 2);
        return i;
    }
    
	public AffineTransform getDefaultTransform(int axis) {
		double x_pt2px = 0;
        double y_pt2px = 0;
        double xcoord0 = 0;
        double ycoord0 = 0;
        
        x_pt2px = 1 / getXAxis().getPointToPixelRatio();
        //System.out.println("** x_pt2px = "+getXAxis().getPointToPixelRatio());
        xcoord0 = getBounds().getX() + getLeftMargin() + getXAxis().getPixelForValue(0.0);
        
        switch(axis) {
            case FIRST_YAXIS:
                y_pt2px = 1 / getFirstYAxis().getPointToPixelRatio();
                ycoord0 = getBounds().getY() + getBounds().getHeight() - getBottomMargin() -
                getFirstYAxis().getPixelForValue(0.0);
                break;
            case SECOND_YAXIS:
                y_pt2px = 1 / getSecondYAxis().getPointToPixelRatio();
                ycoord0 = getBounds().getY() + getBounds().getHeight() - getBottomMargin() -
                getSecondYAxis().getPixelForValue(0.0);
                break;
        }
        return new AffineTransform(x_pt2px, 0f, 0f,
        -y_pt2px, xcoord0, ycoord0);
	}
    
    /* (non-Javadoc)
	 * @see com.approximatrix.charting.RenderingCoordSystem#getTransform(int)
	 */
    public AffineTransform getTransform(int axis) {
        switch(axis) {
            case(FIRST_YAXIS): return y1transform;
            case(SECOND_YAXIS): return y2transform;
        }
        
        return null;
    }
	
    /* (non-Javadoc)
	 * @see com.approximatrix.charting.RenderingCoordSystem#setTransform(java.awt.geom.AffineTransform, int)
	 */
    public void setTransform(AffineTransform at, int axis) {
        switch(axis) {
            case(FIRST_YAXIS): y1transform = at; break;
            case(SECOND_YAXIS): y2transform = at; break;
        }
        super.fireRenderChangeEvent();
    }

	public boolean isCenterLabelsBetweenTicks() {
		return centerObjectLabelsBetweenTicks;
	}

	public void setCenterLabelsBetweenTicks(boolean value) {
		centerObjectLabelsBetweenTicks = value;
	}
}
