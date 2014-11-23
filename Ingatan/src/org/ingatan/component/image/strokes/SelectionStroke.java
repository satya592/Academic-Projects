/*
 * SelectionStroke.java
 *
 * Copyright (C) 2011 Thomas Everingham
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; version 3 of the License.
 *
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * General Public License for more details.
 *
 * A copy of the GNU General Public License can be found in the file
 * LICENSE.txt provided with the source distribution of this program (see
 * the META-INF directory in the source jar). This license can also be
 * found on the GNU website at http://www.gnu.org/licenses/gpl.html.
 *
 * If you did not receive a copy of the GNU General Public License along
 * with this program, contact the lead developer, or write to the Free
 * Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA
 * 02110-1301, USA.
 *
 * If you find this program useful, please tell me about it! I would be delighted
 * to hear from you at tom.ingatan@gmail.com.
 */

package org.ingatan.component.image.strokes;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Shape;
import java.awt.Stroke;

/**
 * A stroke which uses <code>AdjacentStroke</code> to place two dashed lines next
 * to one another.
 *
 * @author Thomas Everingham
 * @version 1.0
 */
public class SelectionStroke implements Stroke{
    Color col1;
    Color col2;

    public SelectionStroke()
    {
        new SelectionStroke(Color.lightGray, Color.darkGray);
    }

    public SelectionStroke(Color color1, Color color2)
    {
        col1 = color1;
        col2 = color2;
    }

    public Shape createStrokedShape(Shape p) {
        Stroke strokeA = new BasicStroke(1.0f,BasicStroke.CAP_SQUARE,BasicStroke.JOIN_MITER,10.0f,new float[] {2,2},0.0f);
        Stroke strokeB = new BasicStroke(1.0f,BasicStroke.CAP_SQUARE,BasicStroke.JOIN_MITER,10.0f,new float[] {5,5},0.0f);
        return new AdjacentStroke(strokeA,strokeB,1).createStrokedShape(p);
    }


}
