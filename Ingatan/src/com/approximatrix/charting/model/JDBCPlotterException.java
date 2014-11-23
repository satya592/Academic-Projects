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

     JDBCPlotterException.java
     Created on 26. October 2002
 */

package com.approximatrix.charting.model;

/**
 * This class encapsulates all JDBCPlotter exceptions.
 * @author  mueller
 */
public class JDBCPlotterException extends java.lang.Exception {

    private Throwable cause;
    
    /**
     * Creates a new instance of <code>JDBCPlotterException</code> without detail message.
     */
    public JDBCPlotterException() {
    }

    /**
     * Constructs an instance of <code>JDBCPlotterException</code> with the specified detail message.
     * @param msg the detail message.
     */
    public JDBCPlotterException(String msg) {
        super(msg);
    }
    
    /**
     * Constructs an instance of <code>JDBCPlotterException</code> with the specified detail message
     * and the specified cause.
     * @param msg the detail message.
     * @param cause the Throwable that caused this Exception
     */
    public JDBCPlotterException(String msg, Throwable cause) {
        super(msg);
        this.cause = cause;
    }    
    
    /** Returns the Throwable that caused the Exception to be thrown. */
    public Throwable getCause() {
        return cause;
    }    
}