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

    ChartDataModelConstraints.java
    Created on 30. Juni 2001, 22:20
*/

package com.approximatrix.charting.model;

/**
 * This interface is used by the rendering algorithm for a coordinate system.
 * It defines the range of values.
 * @author mueller armstrong
 */
public interface ChartDataModelConstraints {
        
    /** Returns the minimum value of all datasets.
     * @return a Number object defining the smallest value.
     * @deprecated As of release 1.3, replaced by {@link #getMinimumY()}
     */
    public Number getMinimumValue();

    /** Returns the maximum value of all datasets.
     * @return a Number object defining the maximum value
     * @deprecated As of release 1.3, replaced by {@link #getMaximumY()} 
     */
    public Number getMaximumValue();

    /** Returns the minimum column value.
     * @return In the case of non-numeric x-values this should be 0, in the case
     * of numeric x-values this should nomen est omen be the smallest
     * value.
     * @deprecated As of release 1.3, replaced by {@link #getMinimumX()} 
     */
    public double getMinimumColumnValue();
    
    /** Returns the maximum column value.
     * @return  In the case of non-numeric x-values this should be the amount of columns - 1, in the case of numeric x-values this should nomen est omen be the smallest value.
     * @deprecated As of release 1.3, replaced by {@link #getMaximumX()} 
     */
    public double getMaximumColumnValue();
    
    /** Returns the minimum value of all datasets.
     * @return a Number object defining the smallest value.
     */
    public Number getMinimumY();

    /** Returns the maximum value of all datasets.
     * @return a Number object defining the maximum value
     */
    public Number getMaximumY();

    /** Returns the minimum column value.
     * @return In the case of non-numeric x-values this should be 0, in the case
     * of numeric x-values this should nomen est omen be the smallest
     * value.
     */
    public Number getMinimumX();
    
    /** Returns the maximum column value.
     * @return  In the case of non-numeric x-values this should be the amount of columns - 1, in the case of numeric x-values this should nomen est omen be the smallest value.
     */
    public Number getMaximumX();
}

