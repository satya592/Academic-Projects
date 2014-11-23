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

    RenderChangeListener.java
*/

package com.approximatrix.charting.event;

/**
 * Defines the interface for a RenderChangeListener.  The listener will be
 * notified if a change in the source component requires that the chart be
 * re-rendered.
 *
 * @author armstrong
 * @version 1.0
 */
public interface RenderChangeListener extends java.util.EventListener {

    /** This method is called, whenever an event is created.
     * @param evt the event object
     */    
    public void renderUpdateRequested(RenderChangeEvent evt);
}

