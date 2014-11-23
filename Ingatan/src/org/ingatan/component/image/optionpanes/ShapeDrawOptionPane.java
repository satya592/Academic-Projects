/*
 * ShapeDrawOptionPane.java
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
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.awt.event.MouseListener;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JPanel;

/**
 * Displays all options for drawing shapes. Options are:
 * <ul>
 *     <li>Line style: selected from a <code>ThumbnailPane</code> of several available</li>
 *     <li>Fill style: stroke and fill, stroke only, fill only</li>
 * </ul>
 * @author Thomas Everingham
 * @version 1.0
 */
public class ShapeDrawOptionPane extends JPanel implements OptionPane {
    /**
     * Fill the shape with the background colour, and stroke the outside with
     * the foreground colour.
     */
    public static final int FILL_AND_STROKE = 0;
    /**
     * Stroke the shape with the foreground colour, but do not fill.
     */
    public static final int STROKE_ONLY = 1;
    /**
     * Fill the shape with the background colour, but do not stroke.
     */
    public static final int FILL_ONLY = 2;
    /**
     * Panel containing all strokes.
     */
    LineOptionPane lineOptions = new LineOptionPane();
    /**
     * Panel containing the three fill options: stroke and fill, stroke only, and fill only.
     */
    ThumbnailPane fillOptions = new ThumbnailPane(32,3);
    /**
     * A buffered image to which a sample of each of the three fill options is painted.
     */
    BufferedImage fillOptionSample = new BufferedImage(30, 20, BufferedImage.TYPE_INT_RGB);
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

    /**
     * Returns a new instance of <code>ShapeDrawOptionPane</code>
     */
    public ShapeDrawOptionPane() {
        fillOptions.setHorizontalSpacing(13);
        fillOptions.setVerticalSpacing(8);
        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        this.add(fillOptions);
        fillOptions.setMinimumSize(new Dimension(200, 50));
        fillOptions.setMaximumSize(new Dimension(400, 50));
        this.add(Box.createVerticalStrut(20));
        this.add(lineOptions);
        lineOptions.setSelectedIndex(1);
        setUpFillOptions();
    }

    /**
     * Draws icons for each of the available shape drawing modes. Also sets up the thumbnail pane to display them.
     */
    private void setUpFillOptions() {
        fillOptionSample = new BufferedImage(32, 32, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = fillOptionSample.createGraphics();
        g2d.setPaint(Color.white);
        g2d.fillRect(0, 0, 32, 32);
        if (antialiasOn)
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        else
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
        g2d.setPaint(cbackground);
        Rectangle2D.Float rect = new Rectangle2D.Float(3.0f, 10.0f, 24.0f, 12.0f);
        g2d.setStroke(lineOptions.getSelectedStroke());
        g2d.fill(rect);
        g2d.setPaint(cforeground);
        g2d.draw(rect);
        Image strokeImg = (Image) fillOptionSample;
        ImageIcon ic = new ImageIcon(strokeImg);
        fillOptions.addThumbnail(ic);


        fillOptionSample = new BufferedImage(32, 32, BufferedImage.TYPE_INT_RGB);
        g2d = fillOptionSample.createGraphics();
        g2d.setPaint(Color.white);
        g2d.fillRect(0, 0, 32, 32);
        if (antialiasOn)
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        else
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
        g2d.setStroke(lineOptions.getSelectedStroke());
        g2d.setPaint(cforeground);
        g2d.draw(rect);
        strokeImg = (Image) fillOptionSample;
        ic = new ImageIcon(strokeImg);
        fillOptions.addThumbnail(ic);


        fillOptionSample = new BufferedImage(32, 32, BufferedImage.TYPE_INT_RGB);
        g2d = fillOptionSample.createGraphics();
        g2d.setPaint(Color.white);
        g2d.fillRect(0, 0, 32, 32);
        if (antialiasOn)
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        else
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
        g2d.setPaint(cbackground);
        g2d.setStroke(lineOptions.getSelectedStroke());
        g2d.fill(rect);
        strokeImg = (Image) fillOptionSample;
        ic = new ImageIcon(strokeImg);
        fillOptions.addThumbnail(ic);

    }

    /**
     * Gets the currently selected stroke.
     * @return the currently selected stroke.
     */
    public Stroke getSelectedStroke()
    {
        return lineOptions.getSelectedStroke();
    }

    /**
     * Gets the way in which the shape should be drawn. Three options are available
     * here: fill and stroke, fill only, and stroke only.
     * @return a value indicating the drawing policy; compare this with the static
     * fields of this class.
     */
    public int getDrawPolicy()
    {
        return fillOptions.getSelectedIndex();
    }

    public void rebuildSelf()
    {
        fillOptions.clearThumbnails();
        setUpFillOptions();
        fillOptions.validate();
        fillOptions.setSelectedIndex(fillOptions.getSelectedIndex());
        lineOptions.rebuildSelf();
    }

    /**
     * Adds the specified <code>MouseListener</code> to every thumbnail
     * that exists in this pane.
     * @param listener the listener to add.
     */
    public void addThumbnailMouseListener(MouseListener listener)
    {
        lineOptions.addThumbnailMouseListener(listener);
        fillOptions.addThumbnailMouseListener(listener);
    }

    /**
     * Removes the specified <code>MouseListener</code> to every thumbnail
     * that exists in this pane.
     * @param listener the listener to remove.
     */
    public void removeThumbnailMouseListener(MouseListener listener)
    {
        lineOptions.removeThumbnailMouseListener(listener);
        fillOptions.removeThumbnailMouseListener(listener);
    }

    /**
     * Update this class upon change of colour.
     * @param newFgColour the new cforeground colour
     * @param newBgColour the new cbackground colour
     */
    public void updateForNewColour(Color newFgColour, Color newBgColour) {
        cforeground = newFgColour;
        cbackground = newBgColour;

        lineOptions.updateForNewColour(newFgColour, newBgColour);

        fillOptions.clearThumbnails();
        setUpFillOptions();
        fillOptions.validate();
    }

    /**
     * Update this class of an antialias setting change.
     * @param antialias
     */
    public void updateForAntialias(boolean antialias)
    {
        this.antialiasOn = antialias;

        lineOptions.updateForAntialias(antialias);

        fillOptions.clearThumbnails();
        setUpFillOptions();
        fillOptions.validate();
    }
}
