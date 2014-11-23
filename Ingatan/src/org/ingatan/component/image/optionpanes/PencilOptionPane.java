/*
 * PencilOptionPane.java
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
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.geom.*;
import java.awt.image.BufferedImage;
import javax.swing.ImageIcon;

/**
 * Provides a selection of brushes on a <code>ThumbnailPane</code> for the user to choose from.
 *
 * @author Thomas Everingham
 * @version 1.0
 */
public class PencilOptionPane extends ThumbnailPane implements OptionPane {

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
    //circles
    Ellipse2D circleTinya = new Ellipse2D.Float(15.0f, 15.0f, 1.0f, 1.0f);
    Ellipse2D circleTinyb = new Ellipse2D.Float(14.5f, 14.5f, 2.0f, 2.0f);
    Ellipse2D circleSmalla = new Ellipse2D.Float(14.0f, 14.0f, 3.0f, 3.0f);
    Ellipse2D circleSmallb = new Ellipse2D.Float(13.0f, 13.0f, 5.0f, 5.0f);
    Ellipse2D circleMeda = new Ellipse2D.Float(12.0f, 12.0f, 7.0f, 7.0f);
    Ellipse2D circleMedb = new Ellipse2D.Float(10.0f, 10.0f, 11.0f, 11.0f);
    Ellipse2D circleLargea = new Ellipse2D.Float(7.5f, 7.5f, 16.0f, 16.0f);
    Ellipse2D circleLargeb = new Ellipse2D.Float(5.5f, 5.5f, 20.0f, 20.0f);
    Ellipse2D circleXLarge = new Ellipse2D.Float(3.0f, 3.0f, 25.0f, 25.0f);
    //squares
    Rectangle2D sqTinya = new Rectangle2D.Float(15.0f, 15.0f, 1.0f, 1.0f);
    Rectangle2D sqTinyb = new Rectangle2D.Float(14.5f, 14.5f, 2.0f, 2.0f);
    Rectangle2D sqSmalla = new Rectangle2D.Float(14.0f, 14.0f, 3.0f, 3.0f);
    Rectangle2D sqSmallb = new Rectangle2D.Float(13.0f, 13.0f, 5.0f, 5.0f);
    Rectangle2D sqMeda = new Rectangle2D.Float(12.0f, 12.0f, 7.0f, 7.0f);
    Rectangle2D sqMedb = new Rectangle2D.Float(10.0f, 10.0f, 11.0f, 11.0f);
    Rectangle2D sqLargea = new Rectangle2D.Float(7.5f, 7.5f, 16.0f, 16.0f);
    Rectangle2D sqLargeb = new Rectangle2D.Float(5.5f, 5.5f, 20.0f, 20.0f);
    Rectangle2D sqXLarge = new Rectangle2D.Float(3.0f, 3.0f, 25.0f, 25.0f);
    //slant
    GeneralPath slant3 = new GeneralPath(new Line2D.Float(10.0f, 20.0f, 19.0f, 10.0f));
    GeneralPath slant1 = (GeneralPath) slant3.clone();
    GeneralPath slant2 = (GeneralPath) slant3.clone();
    GeneralPath slant1b = (GeneralPath) slant3.clone();
    GeneralPath slant2b = (GeneralPath) slant3.clone();
    GeneralPath slant3b = new GeneralPath(new Line2D.Float(19.0f, 20.0f, 10.0f, 10.0f));
    /**
     * Buffered image on which to draw the thubmnail for each pencil type.
     */
    BufferedImage pencilSample;
    /**
     * Array of all pencil shapes.
     */
    Shape[] pencils;

    public PencilOptionPane() {
        slant3.append(new Line2D.Float(20.0f, 10.0f, 11.0f, 20.f), true);
        slant3.closePath();
        slant3b.append(new Line2D.Float(11.0f, 10.0f, 20.0f, 20.f), true);
        slant3b.closePath();
        slant1 = (GeneralPath) slant3.createTransformedShape(AffineTransform.getScaleInstance(0.6, 0.6));
        slant1 = (GeneralPath) slant1.createTransformedShape(AffineTransform.getTranslateInstance(6, 6));
        slant2 = (GeneralPath) slant3.createTransformedShape(AffineTransform.getScaleInstance(0.8, 0.8));
        slant2 = (GeneralPath) slant2.createTransformedShape(AffineTransform.getTranslateInstance(4, 4));

        slant1b = (GeneralPath) slant3b.createTransformedShape(AffineTransform.getScaleInstance(0.6, 0.6));
        slant1b = (GeneralPath) slant1b.createTransformedShape(AffineTransform.getTranslateInstance(6, 6));
        slant2b = (GeneralPath) slant3b.createTransformedShape(AffineTransform.getScaleInstance(0.8, 0.8));
        slant2b = (GeneralPath) slant2b.createTransformedShape(AffineTransform.getTranslateInstance(4, 4));

        pencils = new Shape[]{circleTinya, circleTinyb,
            circleSmalla, circleSmallb,
            circleMeda, circleMedb,
            circleLargea, circleLargeb,
            circleXLarge, //----circle end
            sqTinya, sqTinyb,
            sqSmalla, sqSmallb,
            sqMeda, sqMedb,
            sqLargea, sqLargeb,
            sqXLarge, //-----square end
            slant1,
            slant2,
            slant3,
            slant1b,
            slant2b,
            slant3b
        };
        
        setupPencils();
        
    }

    private void setupPencils()
    {
        this.columns = 4;
        this.horizontalSpacing = 6;
        this.verticalSpacing = 8;
        this.thumbnailSize = 32;
        for (int i = 0; i < pencils.length; i++) {
            pencilSample = new BufferedImage(32, 32, BufferedImage.TYPE_INT_RGB);
            Graphics2D g2d = pencilSample.createGraphics();
            g2d.setPaint(Color.white);
            g2d.fillRect(0, 0, 32, 32);
            if (antialiasOn)
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            else
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
            g2d.setStroke(new BasicStroke(1.0f));
            g2d.setPaint(cforeground);
            g2d.fill(pencils[i]);
            g2d.draw(pencils[i]);

            Image eraserImg = (Image) pencilSample;
            ImageIcon ic = new ImageIcon(eraserImg);
            this.addThumbnail(ic);
        }
    }

    /**
     * Gets the shape for the pencil at the given index.
     * @param index the index of the pencil to get.
     * @return the pencil at the given index.
     * @throws IndexOutOfBoundsException
     */
    public Shape getPencil(int index)
    {
        if ((index < 0) || (index >= pencils.length))
        {
            throw new IndexOutOfBoundsException("Index not within bounds.");
        }
        else
        {
            return pencils[index];
        }
    }

    /**
     * Gets the currently selected pencil shape.
     * @return the currently selected pencil shape.
     */
    public Shape getSelectedPencil()
    {
        return pencils[this.getSelectedIndex()];
    }

    public void rebuildSelf()
    {
        reloadPane();
        this.validate();
        this.thumbnails[this.selection].setSelected(true);
        this.repaint();
    }

    public void reloadPane()
    {
        this.clearThumbnails();
        setupPencils();
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
}
