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

    RenderChangeEvent.java
*/

package com.approximatrix.charting.event;

/**
 * Implements a RenderChangeEvent.  This event is used to signal a change in
 * the data of a renderable component that may require a re-render of the
 * component in the chart. 
 * @author armstrong
 * @version 1.0
 */
public class RenderChangeEvent extends java.util.EventObject {

    /** Creates new RenderChangeEvent */
    public RenderChangeEvent() {
        super(null);
    }
    
    /** Creates new RenderChangeEvent with the given source.
     * @param src the Object that provoked the event
     */
    public RenderChangeEvent(Object src) {
        super(src);
    }
}
