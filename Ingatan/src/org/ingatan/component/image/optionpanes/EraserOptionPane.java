/*
 * EraserOptionPane.java
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
import org.ingatan.component.image.ImageEditorPane;
import org.ingatan.component.image.ThumbnailPane;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import javax.swing.ImageIcon;
import org.geotools.renderer.geom.Arrow2D;

/**
 * This pane provides preset erasers to the user.
 *
 * @author Thomas Everingham
 * @version 1.0
 */
public class EraserOptionPane extends ThumbnailPane implements OptionPane {

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

    Rectangle2D simpleTiny = new Rectangle2D.Float(14.5f, 14.5f, 3.0f, 3.0f);
    Rectangle2D simpleSmall = new Rectangle2D.Float(13.0f, 13.0f, 6.0f, 6.0f);
    Rectangle2D simpleMed = new Rectangle2D.Float(11.0f, 11.0f, 10.0f, 10.0f);
    Rectangle2D simpleLarge = new Rectangle2D.Float(6.0f, 6.0f, 20.0f, 20.0f);
    Rectangle2D simpleXLarge = new Rectangle2D.Float(0.0f, 0.0f, 32.0f, 32.0f);

    Ellipse2D.Float circleTiny = new Ellipse2D.Float(14.5f, 14.5f, 3.0f, 3.0f);
    Ellipse2D.Float circleSmall = new Ellipse2D.Float(13.0f, 13.0f, 6.0f, 6.0f);
    Ellipse2D.Float circleMed = new Ellipse2D.Float(11.0f, 11.0f, 10.0f, 10.0f);
    Ellipse2D.Float circleLarge = new Ellipse2D.Float(6.0f, 6.0f, 20.0f, 20.0f);
    Ellipse2D.Float circleXLarge = new Ellipse2D.Float(0.0f, 0.0f, 32.0f, 32.0f);
    Ellipse2D.Float circleXXLarge = new Ellipse2D.Float(0.0f, 0.0f, 50.0f, 50.0f);

    Arrow2D ff = new Arrow2D(14.5, 14.5, 10, 15);

    Shape[] erasers = new Shape[]{simpleTiny, simpleSmall, simpleMed,
        simpleLarge,
        simpleXLarge,
    circleTiny, circleSmall, circleMed, circleLarge, circleXLarge,ff};

    /**
     * Used to draw samples of the erasers for the thumbnails.
     */
    BufferedImage eraserSample;
    /**
     * The <code>ImageEditorPane</code> that owns this option pane.
     */
    ImageEditorPane editorPane;
    /**
     * Creates a new instance of the eraser option pane.
     * @param owner the <code>ImageEditorPane</code> that owns this option pane.
     */
    public EraserOptionPane(ImageEditorPane owner) {
        this.horizontalSpacing = 10;
        this.verticalSpacing = 7;
        this.columns = 3;
        this.thumbnailSize = 32;
        editorPane = owner;
        setupErasers();
    }

    private void setupErasers()
    {
        for (int i = 0; i < erasers.length; i++)
        {
            eraserSample = new BufferedImage(32, 32, BufferedImage.TYPE_INT_RGB);
            Graphics2D g2d = eraserSample.createGraphics();
            g2d.setPaint(Color.white);
            g2d.fillRect(0, 0, 32, 32);
            if (antialiasOn)
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            else
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
            g2d.setStroke(new BasicStroke(1.5f));
            g2d.setPaint(cbackground);
            g2d.fill(erasers[i]);
            g2d.setPaint(Color.darkGray);
            g2d.draw(erasers[i]);

            Image eraserImg = (Image) eraserSample;
            ImageIcon ic = new ImageIcon(eraserImg);
            this.addThumbnail(ic);
        }
    }

    /**
     * Gets the currently selected eraser shape.
     * @return the currently selected eraser shape.
     */
    public Shape getSelectedEraser()
    {
        return erasers[this.getSelectedIndex()];
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
        setupErasers();
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

    @Override
    public void thumbnailSelectionChanged()
    {
        editorPane.updateEraserIcon();
    }
}
