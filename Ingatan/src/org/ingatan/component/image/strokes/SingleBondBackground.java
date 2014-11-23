/*
 * SingleBondBackground.java
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
import java.awt.geom.Line2D;
import java.awt.geom.PathIterator;

/**
 *
 * @author Thomas Everingham
 * @version 1.0
 */
public class SingleBondBackground implements Stroke {

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
        float segmentLength = 1.5f;
        float segmentHeight = 1.0f;
        float segmentSpacing = 5.0f;

        float lineSlope = ((prevY - y) / (prevX - x));
        float segmentSlope = -1 / lineSlope;

        float radius = (float) Math.sqrt(Math.pow(segmentLength, 2.0) / (1.0 + Math.pow(segmentSlope, 2.0)));
        float heightUnit = (float) Math.sqrt(Math.pow(segmentHeight, 2.0) / (1.0 + Math.pow(lineSlope, 2.0)));
        float spacingUnit = (float) Math.sqrt(Math.pow(segmentSpacing, 2.0) / (1.0 + Math.pow(lineSlope, 2.0)));



        float curX = prevX, curY = prevY;
        //if in bottom-right or top-left quadrants
        if (((x > prevX) && (y > prevY)) || ((x < prevX) && (y < prevY))) {
            if ((x < prevX) && (y < prevY)) {
                curX = prevX - 1;
                curY = prevY - 1;
            }
            while (xybetween(curX, curY, prevX, prevY, x, y)) {
                path.moveTo(curX, curY);
                path.lineTo(curX + radius, curY + radius * segmentSlope);
                path.lineTo(curX + radius + 1, (curY + radius * segmentSlope) + heightUnit * lineSlope);

                path.lineTo(curX - radius, curY - radius * segmentSlope);
                path.lineTo(curX - radius - 1, (curY - radius * segmentSlope) - heightUnit * lineSlope);

                path.closePath();

                if ((x < prevX) && (y < prevY)) {
                    curY -= spacingUnit * lineSlope;
                    curX -= spacingUnit;
                } else {
                    curY += spacingUnit * lineSlope;
                    curX += spacingUnit;
                }

                segmentLength += 0.3f;
                radius = (float) Math.sqrt(Math.pow(segmentLength, 2.0) / (1.0 + Math.pow(segmentSlope, 2.0)));
            }
        }//if in bottom-left or top-right quadrants
        else if (((x > prevX) && (y < prevY)) || ((x < prevX) && (y > prevY))) {
            if ((x < prevX) && (y > prevY)) {
                curX = prevX - 1;
            } else {
                curY = prevY - 1;
            }
            while (xybetween(curX, curY, prevX, prevY, x, y)) {
                path.moveTo(curX, curY);
                path.lineTo(curX + radius, curY + radius * segmentSlope);
                path.lineTo(curX + radius + 1, (curY + radius * segmentSlope) + heightUnit * lineSlope);

                path.lineTo(curX - radius, curY - radius * segmentSlope);
                path.lineTo(curX - radius - 1, (curY - radius * segmentSlope) - heightUnit * lineSlope);

                path.closePath();

                if ((x < prevX) && (y > prevY)) {
                    curY -= spacingUnit * lineSlope;
                    curX -= spacingUnit;
                } else {
                    curY += spacingUnit * lineSlope;
                    curX += spacingUnit;
                }

                segmentLength += 0.5f;
                radius = (float) Math.sqrt(Math.pow(segmentLength, 2.0) / (1.0 + Math.pow(segmentSlope, 2.0)));
            }
        }//if a vertical line
        else if (((x == prevX) && (y < prevY)) || ((x == prevX) && (y > prevY))) {
            if (y < prevY) //working upward
            {
                curY -= 1;
            } else if (y > prevY) {
                curY += 1;
            }
            while (((curY < y) && (curY > prevY)) || ((curY > y) && (curY < prevY))) {

                path.moveTo(curX, curY);
                path.lineTo(curX + radius, curY);
                path.lineTo(curX + radius, curY + segmentHeight);
                path.lineTo(curX - radius, curY + segmentHeight);
                path.lineTo(curX - radius, curY);


                if (y < prevY) //working upward
                {
                    curY -= segmentSpacing;
                } else if (y > prevY) {
                    curY += segmentSpacing;
                }

                segmentLength += 0.5f;
                radius = (float) Math.sqrt(Math.pow(segmentLength, 2.0) / (1.0 + Math.pow(segmentSlope, 2.0)));
            }
        }//if a horizontal line
        else if (((x < prevX) && (y == prevY)) || ((x > prevX) && (y == prevY))) {
            if (x < prevX) //left
            {
                curX = prevX - 1;
            } else if (x > prevX) { //working right
                curX = prevX + 1;
            }
            while (((curX < x) && (curX > prevX)) || ((curX > x) && (curX < prevX))) {
                path.moveTo(curX, curY);
                path.lineTo(curX, curY - radius);
                path.lineTo(curX+segmentHeight, curY - radius);
                path.lineTo(curX+segmentHeight, curY + radius);
                path.lineTo(curX, curY + radius);


                if (x < prevX) //working left
                {
                    curX -= segmentSpacing;
                } else if (x > prevX) { //working right
                    curX += segmentSpacing;
                }

                segmentLength += 0.5f;
                radius = segmentLength;
            }
        }
    }

    boolean xybetween(float x, float y, float x1, float y1, float x2, float y2) {
        return (new Line2D.Float(x1, y1, x2, y2).getBounds().contains(x, y));
    }
}
