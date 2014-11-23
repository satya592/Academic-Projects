/*
 * StrokeOutline.java
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

import java.awt.Shape;
import java.awt.Stroke;

/**
 *
 * @author Thomas Everingham
 * @version 1.0
 */
public class StrokeOutline implements Stroke {

    private Stroke strokeA;
    private Stroke strokeB;

    public StrokeOutline(Stroke strokeA, Stroke strokeB) {
        this.strokeA = strokeA;
        this.strokeB = strokeB;
    }

    public Shape createStrokedShape(Shape strokeShape) {
        return strokeB.createStrokedShape(strokeA.createStrokedShape(strokeShape));
    }
}
