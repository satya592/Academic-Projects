/*
 * SingleBondForeground.java
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
import java.awt.geom.GeneralPath;
import java.awt.geom.PathIterator;

/**
 *
 * @author Thomas Everingham
 * @version 1.0
 */
public class SingleBondForeground implements Stroke {

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
                    markPoint(result, currentX, currentY, previousX, previousY);
                    break;
                case PathIterator.SEG_MOVETO:
                    //drawLines(result, coords[2], coords[3]);
                    break;
                case PathIterator.SEG_CLOSE:
                    result.closePath();
                    break;
                case PathIterator.SEG_CUBICTO:
                    markPoint(result, currentX, currentY, previousX, previousY);
                    break;
                case PathIterator.SEG_QUADTO:
                    markPoint(result, currentX, currentY, previousX, previousY);
                    break;
            }

            previousX = currentX;
            previousY = currentY;
            path.next();
        }

        return result;
    }

    void markPoint(GeneralPath path, float x, float y, float prevX, float prevY) {
        int radius = 2;
        //if in bottom-right or top-left quadrants
        if (((x > prevX) && (y > prevY)) || ((x < prevX) && (y < prevY))) {
            path.moveTo(prevX, prevY); // Begin a new sub-path
            path.lineTo(x + radius, y - radius); // Add a line segment to it
            path.lineTo(x - radius, y + radius); // Add a second line segment
            path.closePath(); // Go back to last moveTo position
        }//if in bottom-left or top-right quadrants
        else if (((x > prevX) && (y < prevY)) || ((x < prevX) && (y > prevY))) {
            path.moveTo(prevX, prevY); // Begin a new sub-path
            path.lineTo(x - radius, y - radius); // Add a line segment to it
            path.lineTo(x + radius, y + radius); // Add a second line segment
            path.closePath(); // Go back to last moveTo position
        }//if a vertical line
        else if (((x == prevX) && (y < prevY)) || ((x == prevX) && (y > prevY)))
        {
            path.moveTo(prevX, prevY); // Begin a new sub-path
            path.lineTo(x - radius, y); // Add a line segment to it
            path.lineTo(x + radius, y); // Add a second line segment
            path.closePath(); // Go back to last moveTo position
        }//if a horizontal line
        else if (((x < prevX) && (y == prevY)) || ((x > prevX) && (y == prevY)))
        {
            path.moveTo(prevX, prevY); // Begin a new sub-path
            path.lineTo(x, y-radius); // Add a line segment to it
            path.lineTo(x, y+radius); // Add a second line segment
            path.closePath(); // Go back to last moveTo position
        }
    }
}
