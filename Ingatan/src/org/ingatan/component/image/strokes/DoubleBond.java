/*
 * DoubleBond.java
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
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.GeneralPath;
import java.awt.geom.Line2D;
import java.awt.geom.PathIterator;

/**
 *
 * @author Thomas Everingham
 * @version 1.0
 */
public class DoubleBond implements Stroke {

    public Shape createStrokedShape(Shape p) {
        PathIterator path = p.getPathIterator(null);
        GeneralPath result = new GeneralPath();
        float[] coords = new float[6];
        int type;

        float previousX = -1, previousY = -1;
        float currentX, currentY;
        //while we have still not completed the path traversal
        while (!path.isDone()) {
            type = path.currentSegment(coords);

            if (previousX == -1) {
                previousX = coords[0];
            }
            if (previousY == -1) {
                previousY = coords[1];
            }

            currentX = coords[0];
            currentY = coords[1];



            switch (type) {
                case PathIterator.SEG_LINETO:
                    drawLines(result, currentX, currentY, previousX, previousY);
                    break;
                case PathIterator.SEG_MOVETO:
                    //drawLines(result, coords[2], coords[3]);
                    break;
                case PathIterator.SEG_CLOSE:
                    result.closePath();
                    break;
                case PathIterator.SEG_CUBICTO:
                    drawLines(result, currentX, currentY, previousX, previousY);
                    break;
                case PathIterator.SEG_QUADTO:
                    drawLines(result, currentX, currentY, previousX, previousY);
                    break;
            }

            previousX = currentX;
            previousY = currentY;
            path.next();
        }

        return result;
    }

    void drawLines(GeneralPath path, float x, float y, float prevX, float prevY) {
        BasicStroke b = new BasicStroke(1.0f);
        Line2D line1;
        Line2D line2;
        if (((prevX < x) && (prevY < y)) || ((prevX > x) && (prevY > y)))
        {
            line1 = new Line2D.Float(prevX+1.5f, prevY-1.5f, x+1.5f, y-1.5f);
            line2 = new Line2D.Float(prevX-1.5f, prevY+1.5f, x-1.5f, y+1.5f);
        }
        else
        {
            line1 = new Line2D.Float(prevX-1.5f, prevY-1.5f, x-1.5f, y-1.5f);
            line2 = new Line2D.Float(prevX+1.5f, prevY+1.5f, x+1.5f, y+1.5f);
        }
        
        path.append(b.createStrokedShape(line1), false);
        path.append(b.createStrokedShape(line2), false);
    }
}
