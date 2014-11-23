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

    AbstractColorScale.java
*/

package com.approximatrix.charting.render.colorscale;

import java.awt.Color;

/** Simple partial implementation of a ColorScale to allow for the calling
 * of individual color scaling methods when an array is passed.
 * 
 * @author armstrong
 */
public abstract class AbstractColorScale implements ColorScale {

	public Color[] getColors(double[] value) {
		if(value == null) return null;
		Color[] ret = new Color[value.length];
		for(int i=0;i<value.length;i++)
			ret[i] = getColor(value[i]);
		return ret;
	}

	public Color[] getColors(int[] value) {
		if(value == null) return null;
		Color[] ret = new Color[value.length];
		for(int i=0;i<value.length;i++)
			ret[i] = getColor(value[i]);
		return ret;
	}

}
