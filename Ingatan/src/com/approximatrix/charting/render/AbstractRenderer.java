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

    AbstractRenderer.java
    Created on 21. Juni 2001, 12:32
*/

package com.approximatrix.charting.render;

import java.awt.image.BufferedImage;
import java.awt.Rectangle;
import java.awt.Image;
import java.awt.Graphics2D;
import java.awt.Dimension;
import java.awt.Color;
import java.awt.geom.AffineTransform;

// Imported for render changes
import com.approximatrix.charting.event.RenderChangeListener;
import com.approximatrix.charting.event.RenderChangeEvent;
import javax.swing.event.EventListenerList;


/**
 * The AbstractRenderer provides default implementations for the set and
 * get methods of every Renderer. Especially it provides a default mechanism
 * for scaling Renderer instances whose actual bounds are smaller than their
 * preferred size. As a consequence, every Renderer instance only needs
 * to implement paintDefault() which has to render the object from coordinates
 * 0,0 onwards using the preferred size.
 * @author mueller armstrong
 * @version 1.0
 */
public abstract class AbstractRenderer implements Renderer {

	/** Flag which indicates whether rendering should be performed directly to 
	 * the Graphics2D.  True forces direct rendering, false signals the use of 
	 * the buffer image method (Openchart2 1.0 compatible).
	 */
	private static boolean DirectRender = true;

    /** The listener list. */
    protected EventListenerList listener = new EventListenerList();
	
	/** The bounds within which this object should be rendered. */
    Rectangle bounds = new Rectangle(0, 0, 
                                     Integer.MAX_VALUE, 
                                     Integer.MAX_VALUE);
    
    /** Sets the direct rendering flag appropriately.  Only necessary to maintain
     * Openchart2 1.0 compatibility.
     * 
     * @param value true enables direct rendering the the Grpahics2D object, false uses a buffer image
     */
    public static void setDirectRender(boolean value)  {
    	DirectRender = value;
    }
    
    /** Returns the current state of the direct rendering flag.
     * 
     * @return true if rendering is performed directly to the Graphics2D object, false if using a buffer image
     */
    public static boolean getDirectRender() {
    	return DirectRender;
    }
    
    /** Creates new AbstractRenderer */
    public AbstractRenderer() {
    }
    
    /** Sets the bounds the layout manager has assigned to
     * this renderer. Those, of course, have to be
     * considered in the rendering process.
     * @param bounds the new bounds for the renderer.
     */
    public void setBounds(Rectangle bounds) {
    	if(this.isDifferentBounds(bounds)) {
    		this.bounds = bounds;
    		this.fireRenderChangeEvent();
    	}
    }
    
    /** Gets the bounds for this renderer.
     * @return the bounds of this renderer. If <code>setBounds</code> has not
     * been called before, the bounds computed from
     * <code>getPreferredSize</code> is returned.
     */
    public Rectangle getBounds() {
        return bounds;
    }
    
    /** Calls the paintDefault method, passing directly the Graphics2D object
     * on which to paint.  Unlike the <code>render()</code> method, this
     * method does no scaling and does not adhere to bounds.  However, Java
     * AWT is safe enough thatthis shouldn't cause a problem.
     * 
     * @param g the Graphics2D object in which to render
     */
    private void directRender(Graphics2D g) {;
    	g.translate(getBounds().x, getBounds().y);
        paintDefault(g);
        g.translate(-getBounds().x, -getBounds().y);
        return;
    }
  
    /** Renders the Object in the Graphics object. Creates a BufferedImage
     * and the corresponding Graphics2D object to paint in. The Image is
     * created using the preferred size. Afterwards <code>paintDefault</code>
     * is called to perform a standard painting in the Graphics object.
     * If the bounds and the preferred size don't match the image is 
     * scaled afterwards.
     * @param g the Graphics2D object in which to render
     */
    public void render(Graphics2D g) {        

    	if(DirectRender)
    		this.directRender(g);
    	else {
    		Dimension d = getPreferredSize();
    		if((int)d.width <= 0 || (int)d.width > 1E+05) {
    			d.width = 1;
    		}
    		if((int)d.height <= 0 || (int)d.height > 1E+05) {
    			d.height = 1;
    		}

    		BufferedImage im = new BufferedImage((int)d.width,
    				(int)d.height,
    				BufferedImage.TYPE_INT_RGB);
    		Graphics2D g2 = im.createGraphics();
    		g2.setColor(Color.white);
    		g2.fillRect(0, 0, d.width, d.height);
    		g2.setColor(Color.black);

    		paintDefault(g2);

    		if(d.width > getBounds().getWidth() ||
    				d.height > getBounds().getHeight()) {
    			// Scale Image
    			Image scale = im.getScaledInstance((int)getBounds().getWidth(),
    					(int)getBounds().getHeight(),
    					Image.SCALE_SMOOTH);

    			g.drawImage(scale,
    					(int)getBounds().getX(),
    					(int)getBounds().getY(),
    					null);
    		} else             
    			g.drawImage(im, (int)getBounds().getX(), 
    					(int)getBounds().getY(), null);
    	}
    }
    
    /** This method is called by the paint method to do the actual painting.
     * The painting is supposed to start at point (0,0) and the size is
     * always the same as the preferred size. The paint method performs
     * the possible scaling.
     * @param g the Graphics2D object to paint in.
     */
    public abstract void paintDefault(Graphics2D g);
    
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

   	/** Returns whether a new bounds Rectangle is the same object or equal to
   	 * another Rectangle
   	 * 
   	 * @param nb the new bounds to compare to the current bounds
   	 * @return true if the bounds are different, false if they are at least equal in value
   	 */ 
   	protected boolean isDifferentBounds(Rectangle nb) {
   		Rectangle cb = this.getBounds();
   		
   		if(cb == nb)
   			return false;
   		else if(cb.equals(nb))
   			return false;
   		else 
   			return true;
   	}

}
