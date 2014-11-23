/*
    OpenChart2 Java Charting Library and Toolkit
    Copyright (C) 2005-2008 Approximatrix, LLC
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

    InteractiveChartPanel.java
*/

package com.approximatrix.charting.swing;

import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.geom.NoninvertibleTransformException;

import javax.swing.Timer;
import javax.swing.event.EventListenerList;

import com.approximatrix.charting.coordsystem.CoordSystem;
import com.approximatrix.charting.event.InfoAvailableEvent;
import com.approximatrix.charting.event.InfoAvailableListener;
import com.approximatrix.charting.event.RenderChangeEvent;
import com.approximatrix.charting.event.RenderChangeListener;
import com.approximatrix.charting.model.ChartDataModel;

/** Extension to ExtendedChartPanel that provides a method for extracting
 * information from the plot simply by holding a mouse over the position.
 * If a CoordinateFeedback object is provided, this panel will call the
 * object's information method, passing in the current data-space coordinates.
 * 
 * @author armstrong
 */
public abstract class InteractiveChartPanel extends ExtendedChartPanel implements ActionListener {

	/** The listener list. */
    protected EventListenerList listener = new EventListenerList();
	
    /** Time of no mouse movement at which point the feedback info is displayed */
	private final int WAIT_FOR_TIP = 500;
	
	/** Time object that is started every time mouse movement is registered. */
	protected Timer moveTimer = null;
	
	/** The graphics-space mouse position */
	protected Point mp = null;
	
	/** Constructs and initializes the panel, passing all parameters
	 * through to the superclass.  
	 *
	 * @param arg0 the data model associated with this panel
	 * @param arg1 the title String
	 * @param arg2 the id of the coordinate system configuration
	 */
	public InteractiveChartPanel(ChartDataModel arg0, String arg1, int arg2) {
		super(arg0, arg1, arg2);
		initialize();
	}

	/** Constructs and initializes the panel, passing all parameters
	 * through to the superclass.  
	 *
	 * @param arg0 the data model associated with this panel
	 * @param arg1 the title String
	 */
	public InteractiveChartPanel(ChartDataModel arg0, String arg1) {
		super(arg0, arg1);
		initialize();
	}

    /** Initializes, but does not start, the mouse movement timer. */
	private void initialize() {
		moveTimer = new Timer(WAIT_FOR_TIP,this);
		moveTimer.setRepeats(false);
	}
	
	/** One component of the mouse listener that starts the timer once
	 * the mouse has moved.  If this is called when the timer is already
	 * running, the timer is restarted.
	 *
	 * @see com.approximatrix.charting.swing.AbstractChartPanel#mouseMoved(java.awt.event.MouseEvent)
	 */
	@Override
	public void mouseMoved(MouseEvent arg0) {
		moveTimer.stop();

		if(this.getCoordSystem().getInnerBounds().contains(arg0.getPoint())) {
			mp = arg0.getPoint();
			moveTimer.start();			
		}
		
	}

	/** Action listener implementation that causes the tip to appear
	 * over the current window.
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	public void actionPerformed(ActionEvent arg0) {
		if(mp == null) return;
		
		Point abspt = new Point(this.getLocationOnScreen());
		abspt.translate(mp.x,mp.y);
		
		Point transformed = new Point();
		try {
			this.getCoordSystem().getDefaultTransform(CoordSystem.FIRST_YAXIS).createInverse().transform(mp,transformed);
		} catch (NoninvertibleTransformException nite) {
			// Failed the inverse
			System.err.println("Inverse graphics- to data-space transform failed");
			return;
		}
		this.fireInfoAvailable(this, transformed);
	}

	public void fireInfoAvailable(Object src, Point datapoint) {
		InfoAvailableEvent event = new InfoAvailableEvent(src,datapoint);
        Object[] ls = listener.getListenerList();
        for (int i = (ls.length - 2); i >= 0; i-=2) {
            if (ls[i] == InfoAvailableListener.class) {
                ((InfoAvailableListener)ls[i + 1]).onInformationAvailable(event);
            }
        }
	}
	
	/** Removes a InfoAvailableListener.
     * @param l the InfoAvailableListener
     */
    public void removeInfoAvailableListener(InfoAvailableListener l) {
        listener.remove(InfoAvailableListener.class, l);
    }
    
    /** Adds a InfoAvailableListener.
     * @param l the InfoAvailableListener
     */
    public void addInfoAvailableListener(InfoAvailableListener l) {
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
	        listener.add(InfoAvailableListener.class, l);
    }
    
    /** Clears all InfoAvailableListener from the object */
   	public void clearInfoAvailableListener() {
   		listener = new EventListenerList();
   	}
	
}
