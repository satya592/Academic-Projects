/*
 * LineOptionPane.java
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

package org.ingatan.component.image.optionpanes;

import org.ingatan.component.OptionPane;
import org.ingatan.component.image.ThumbnailPane;
import org.ingatan.component.image.strokes.CurvedStroke;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.Line2D;
import java.awt.image.BufferedImage;
import javax.swing.ImageIcon;
import org.ingatan.component.image.strokes.DoubleBond;
import org.ingatan.component.image.strokes.SingleBondBackground;
import org.ingatan.component.image.strokes.SingleBondForeground;
import org.ingatan.component.image.strokes.TripleBond;

/**
 * Provides a GUI for setting line-style options. Options are:
 * <ul>
 *     <li>Line style</li>
 *     <li>Line weight</li>
 * </ul>
 *
 * The user chooses a preset line style incorporating these two options, from a <code>ThumbnailPane</code>.
 *
 * @author Thomas Everingham
 * @version 1.0
 */
public class LineOptionPane extends ThumbnailPane implements OptionPane {
    /**
     * Whether or not antialiasing should be used.
     */
    private boolean antialiasOn = true;
    /**
     * The currently selected foreground colour.
     */
    private Color cforeground = Color.black;
    /**
     * The currently selected background colour.
     */
    private Color cbackground = Color.white;
    
    //Square capped basic brush strokes
    BasicStroke strokeBasicSize1sq = new BasicStroke(1, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_MITER);
    BasicStroke strokeBasicSize2sq = new BasicStroke(2, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_MITER);
    BasicStroke strokeBasicSize4sq = new BasicStroke(4, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_MITER);
    BasicStroke strokeBasicSize7sq = new BasicStroke(7, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_MITER);
    //Round capped basic brush strokes
    BasicStroke strokeBasicSize1ro = new BasicStroke(4, BasicStroke.CAP_ROUND, BasicStroke.JOIN_MITER);
    BasicStroke strokeBasicSize2ro = new BasicStroke(7, BasicStroke.CAP_ROUND, BasicStroke.JOIN_MITER);
    BasicStroke strokeBasicSize4ro = new BasicStroke(12, BasicStroke.CAP_ROUND, BasicStroke.JOIN_MITER);
    BasicStroke strokeBasicSize7ro = new BasicStroke(17, BasicStroke.CAP_ROUND, BasicStroke.JOIN_MITER);
    //Dashed strokes
    BasicStroke strokeDashSize1a = new BasicStroke(1, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_MITER, 10, new float[]{5}, 0);
    BasicStroke strokeDashSize1b = new BasicStroke(1, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_MITER, 10, new float[]{8, 10}, 0);
    BasicStroke strokeDashSize1c = new BasicStroke(1, BasicStroke.CAP_ROUND, BasicStroke.JOIN_MITER, 10, new float[]{1, 7}, 0);
    BasicStroke strokeDashSize1d = new BasicStroke(1, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_MITER, 10, new float[]{5, 3.5f, 1, 3.5f}, 0);
    BasicStroke strokeDashSize2a = new BasicStroke(2, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_MITER, 10, new float[]{5}, 0);
    BasicStroke strokeDashSize2b = new BasicStroke(2, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_MITER, 10, new float[]{8, 10}, 0);
    BasicStroke strokeDashSize2c = new BasicStroke(2, BasicStroke.CAP_ROUND, BasicStroke.JOIN_MITER, 10, new float[]{1, 7}, 0);
    BasicStroke strokeDashSize2d = new BasicStroke(2, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_MITER, 10, new float[]{5, 6, 1, 6}, 0);
    BasicStroke strokeDashSize4a = new BasicStroke(4, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_MITER, 10, new float[]{5, 7}, 0);
    BasicStroke strokeDashSize4b = new BasicStroke(4, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_MITER, 10, new float[]{8, 14}, 0);
    BasicStroke strokeDashSize4c = new BasicStroke(4, BasicStroke.CAP_ROUND, BasicStroke.JOIN_MITER, 10, new float[]{1, 10}, 0);
    BasicStroke strokeDashSize4d = new BasicStroke(4, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_MITER, 10, new float[]{5, 8, 1, 8}, 0);
    BasicStroke strokeDashSize7a = new BasicStroke(7, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_MITER, 10, new float[]{5, 10}, 0);
    BasicStroke strokeDashSize7b = new BasicStroke(7, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_MITER, 10, new float[]{8, 16}, 0);
    BasicStroke strokeDashSize7c = new BasicStroke(7, BasicStroke.CAP_ROUND, BasicStroke.JOIN_MITER, 10, new float[]{1, 15}, 0);
    BasicStroke strokeDashSize7d = new BasicStroke(7, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_MITER, 10, new float[]{5, 10, 1, 10}, 0);
    //Outline strokes
    Stroke strokeOutline1a = new CompositeStroke(new BasicStroke(3f), new BasicStroke(1f));
    Stroke strokeOutline2a = new CompositeStroke(new BasicStroke(6f), new BasicStroke(1f));
    Stroke strokeOutline3a = new CompositeStroke(new BasicStroke(10f), new BasicStroke(1f));
    Stroke strokeOutline4a = new CompositeStroke(new BasicStroke(15f), new BasicStroke(1f));
    Stroke strokeOutline1b = new CompositeStroke(new BasicStroke(3f), new BasicStroke(2f));
    Stroke strokeOutline2b = new CompositeStroke(new BasicStroke(6f), new BasicStroke(2f));
    Stroke strokeOutline3b = new CompositeStroke(new BasicStroke(10f), new BasicStroke(2f));
    Stroke strokeOutline4b = new CompositeStroke(new BasicStroke(15f), new BasicStroke(2f));
    Stroke curvedStroke0 = new CurvedStroke(CurvedStroke.COORD_CURVES);
    Stroke curvedStroke3 = new CurvedStroke(CurvedStroke.TICK_CURVES);
    Stroke curvedStroke4 = new CurvedStroke(CurvedStroke.SLIGHT_ESS);


    BufferedImage strokeSample;
    private Stroke[] strokes = new Stroke[]{strokeBasicSize1sq,
        strokeBasicSize2sq,
        strokeBasicSize4sq,
        strokeBasicSize7sq,
        strokeBasicSize1ro,
        strokeBasicSize2ro,
        strokeBasicSize4ro,
        strokeBasicSize7ro,
        strokeDashSize1a,
        strokeDashSize1b,
        strokeDashSize1c,
        strokeDashSize1d,
        strokeDashSize2a,
        strokeDashSize2b,
        strokeDashSize2c,
        strokeDashSize2d,
        strokeDashSize4a,
        strokeDashSize4b,
        strokeDashSize4c,
        strokeDashSize4d,
        strokeDashSize7a,
        strokeDashSize7b,
        strokeDashSize7c,
        strokeDashSize7d,
        strokeOutline1a,
        strokeOutline2a,
        strokeOutline3a,
        strokeOutline4a,
        strokeOutline1b,
        strokeOutline2b,
        strokeOutline3b,
        strokeOutline4b,
        new SingleBondForeground(),
        new SingleBondBackground(),
        new DoubleBond(),
        new TripleBond(),
        curvedStroke0,
        curvedStroke3,
        curvedStroke4,
    };

    /**
     * Create a new instance of <code>LineOptionPane</code>
     */
    public LineOptionPane() {
        this.horizontalSpacing = 5;
        this.verticalSpacing = 7;
        this.columns = 4;
        this.thumbnailSize = 29;
        setupLines();

    }

    private void setupLines()
    {
        for (int i = 0; i < strokes.length; i++) {
            strokeSample = new BufferedImage(32, 32, BufferedImage.TYPE_INT_RGB);
            Graphics2D g2d = strokeSample.createGraphics();
            g2d.setPaint(Color.white);
            g2d.fillRect(0, 0, 32, 32);
            if (antialiasOn)
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            else
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
            g2d.setPaint(cforeground);
            g2d.setStroke(strokes[i]);
            g2d.draw(new Line2D.Float(0.0f, 8.0f, 20.0f, 8.0f));
            g2d.draw(new Line2D.Float(0.0f, 16.0f, 32.0f, 30.0f));
            g2d.setPaint(Color.black);

            Image strokeImg = (Image) strokeSample;
            ImageIcon ic = new ImageIcon(strokeImg);
            this.addThumbnail(ic);
        }
    }


    public void rebuildSelf()
    {
        reloadPane();
        validate();
        this.thumbnails[this.selection].setSelected(true);
        repaint();
    }

    public void reloadPane()
    {
        this.clearThumbnails();
        setupLines();
    }

    public Stroke getSelectedStroke()
    {
        return strokes[this.getSelectedIndex()];
    }

    /**
     * Update this class upon change of colour.
     * @param newFgColour the new cforeground colour
     * @param newBgColour the new cbackground colour
     */
    public void updateForNewColour(Color newFgColour, Color newBgColour) {
        cforeground = newFgColour;
        cbackground = newBgColour;
        reloadPane();
        this.validate();
    }

    /**
     * Update this class of an antialias setting change.
     * @param antialias
     */
    public void updateForAntialias(boolean antialias)
    {
        this.antialiasOn = antialias;
        reloadPane();
        this.validate();
    }




    private class CompositeStroke implements Stroke {

        private Stroke stroke1, stroke2;

        public CompositeStroke(Stroke stroke1, Stroke stroke2) {
            this.stroke1 = stroke1;
            this.stroke2 = stroke2;
        }

        public Shape createStrokedShape(Shape shape) {
            return stroke2.createStrokedShape(stroke1.createStrokedShape(shape));
        }
    }

   
}
