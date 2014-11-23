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

    IndexedColorScale.java
*/

package com.approximatrix.charting.render.colorscale;

import java.awt.Color;
import java.util.HashMap;
import java.util.Map;

/** Provides a color scale object that looks up colors based on an index
 * passed.  The index/Color pairs must be assigned prior to calling a
 * conversion method.  Returns FAILED_LOOKUP when the index does not
 * exist in the Map.
 * 
 * @author armstrong
 */
public class IndexedColorScale extends AbstractColorScale {

	/** The color to return if the index does not exist */
	protected static final Color FAILED_LOOKUP = Color.BLACK;
	
	/** Maps integers to Colors */
	protected Map<Integer,Color> map = null;
	
	/** Constructs the IndexedColorScale object and initializes an empty color
	 * map.
	 */
	public IndexedColorScale() {
		map = new HashMap<Integer,Color>();
	}
	
	/** Constructs the IndexedColorScale object based on the passed map
	 * of index/Color pairs.
	 * 
	 * @param map a Map with Integer keys and Color values
	 */
	public IndexedColorScale(Map<Integer,Color> map) {
		this.map = map;
	}
	
	/** Assigns the specified Color to the specified index in the map.  Overwrites
	 * existing matching indices.
	 * 
	 * @param i index of the added color
	 * @param val the Color value
	 */
	public void setColor(int i, Color val) {
		map.put(new Integer(i), val);
	}
	
	/** Retrieves the color by converting the value by rounding
	 * to an integer before requesting the Color of the rounded index.
	 */
	public Color getColor(double value) {
		return getColor(Math.round((float)value));
	}

	public Color getColor(int value) {
		Color ret = map.get(new Integer(value));
		if(ret == null)
			return FAILED_LOOKUP;
		else
			return ret;
	}
}
