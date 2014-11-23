/*
 * Thumbnail.java
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

package org.ingatan.component.image;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.geom.Area;
import java.awt.geom.RoundRectangle2D;
import javax.swing.ImageIcon;
import javax.swing.JPanel;

/**
 * Displays an <code>ImageIcon</code> in the style of a thumbnail.
 *
 * @author Thomas Everingham
 * @version 1.0
 */
public class Thumbnail extends JPanel {

    /**
     * The thumbnail to paint.
     */
    Image thumbnail;
    /**
     * The size of the thumbnail.
     */
    int iconSize = 64;
    /**
     * Colour of the border of the thumbnail
     */
    private Color borders = new Color(26, 97, 110);
    /**
     * Colour of the background of the thumbnail.
     */
    private Color background = new Color(222, 233, 233);
    /**
     * Colour of the background of the thumbnail.
     */
    private Color background2 = new Color(26,97,110,40);
    /**
     * Colour of the selection of the thumbnail.
     */
    private Color selection = new Color(255,60,44,150);
    /**
     * This component is painted differently to indicate a selection.
     */
    private boolean selected = false;
    /**
     * When using this component as part of an array, it may be helpful to keep track of
     * the index of this component by using the constructor(IconImage, int, int), or by
     * the method <code>setIndex</code>.
     */
    private int index = -1;

    /**
     * Construct a new instance of <code>Thumbnail</code>
     *
     * @param thumbnail the thumbnail to paint.
     * @param iconSize the size of the image; recommended sizes are: 64, 48, 32, 16. Must be between 0 and 151.
     */
    public Thumbnail(ImageIcon thumbnail, int iconSize) {
        if ((iconSize <= 0) || (iconSize > 150)) {
            if (iconSize <= 0) {
                iconSize = 64;
            }
            if (iconSize > 150) {
                iconSize = 150;
            }
        }
        this.iconSize = iconSize;
        this.thumbnail = thumbnail.getImage().getScaledInstance(iconSize, iconSize, Image.SCALE_SMOOTH);

        this.setOpaque(false);
        this.setPreferredSize(new Dimension(iconSize + 8, iconSize + 8));
        this.setMaximumSize(new Dimension(iconSize + 8, iconSize + 8));
        this.setMinimumSize(new Dimension(iconSize + 8, iconSize + 8));
    }

    /**
     * Construct a new instance of <code>Thumbnail</code>
     *
     * @param thumbnail the thumbnail to paint.
     * @param iconSize the size of the image; recommended sizes are: 64, 48, 32, 16. Must be between 0 and 151.
     * @param index when used in an array, it may be helpful to keep track at what index this particular thumbnail was placed.
     */
    public Thumbnail(ImageIcon thumbnail, int iconSize, int index) {
        if ((iconSize <= 0) || (iconSize > 150)) {
            if (iconSize <= 0) {
                iconSize = 64;
            }
            if (iconSize > 150) {
                iconSize = 150;
            }
        }
        this.iconSize = iconSize;
        this.thumbnail = thumbnail.getImage().getScaledInstance(iconSize, iconSize, Image.SCALE_SMOOTH);
        this.index = index;

        this.setOpaque(false);
        this.setPreferredSize(new Dimension(iconSize + 8, iconSize + 8));
        this.setMaximumSize(new Dimension(iconSize + 8, iconSize + 8));
        this.setMinimumSize(new Dimension(iconSize + 8, iconSize + 8));
    }

    /**
     * Construct a new instance of <code>Thumbnail</code>
     *
     * @param thumbnail the thumbnail to paint.
     * @param iconSize the size of the image; recommended sizes are: 64, 48, 32, 16. Must be between 0 and 151.
     */
    public Thumbnail(Image thumbnail, int iconSize) {
        if ((iconSize <= 0) || (iconSize > 150)) {
            if (iconSize <= 0) {
                iconSize = 64;
            }
            if (iconSize > 150) {
                iconSize = 150;
            }
        }

        this.iconSize = iconSize;
        this.thumbnail = thumbnail.getScaledInstance(iconSize, iconSize, Image.SCALE_SMOOTH);
        this.setOpaque(false);
        this.setPreferredSize(new Dimension(iconSize + 8, iconSize + 8));
        this.setMaximumSize(new Dimension(iconSize + 8, iconSize + 8));
        this.setMinimumSize(new Dimension(iconSize + 8, iconSize + 8));
    }

    /**
     * Gets the thumbnail image.
     * @return the thumbnail image.
     */
    public Image getThumbnail() {
        return thumbnail;
    }

    /**
     * Gets the side length of the thumbnail; the thumbnail is always square.
     *
     * @return the side length of the icon.
     */
    public int getIconSize() {
        return iconSize;
    }

    public int getIndex()
    {
        return index;
    }

    public void setIndex(int newIndex)
    {
        this.index = index;
    }

    /**
     * Sets the new side length for this thumbnail; the thumbnail is always square.
     * You should set this before calling <code>setImage</code>, as <code>setImage</code>
     * automatically scales to whatever icon size is currently set.
     * Quality loss will occur if, for example, you set a new image, and then set a larger
     * side length.
     * @param iconSize the new side length for this thumbnail.
     */
    public void setIconSize(int iconSize) {
        this.iconSize = iconSize;
        this.thumbnail = thumbnail.getScaledInstance(iconSize, iconSize, Image.SCALE_SMOOTH);
        this.setPreferredSize(new Dimension(iconSize + 8, iconSize + 8));
        this.setMaximumSize(new Dimension(iconSize + 8, iconSize + 8));
        this.setMinimumSize(new Dimension(iconSize + 8, iconSize + 8));
    }

    /**
     * Sets the new image for this thumbnail to display.
     * @param img the new image for this thumbnail to display.
     */
    public void setImage(Image img) {
        this.thumbnail = img.getScaledInstance(iconSize, iconSize, Image.SCALE_SMOOTH);
    }

    /**
     * Sets the new image for this thumbnail to display.
     * @param img the new image for this thumbnail to display.
     */
    public void setImage(ImageIcon img) {
        this.thumbnail = img.getImage().getScaledInstance(iconSize, iconSize, Image.SCALE_SMOOTH);
    }

    /**
     * Gets whether or not this thumbnail is in the state of being selected. If it is,
     * it is painted differently.
     * @return whether or not this thumbnail is in the state of being selected.
     */
    public boolean isSelected() {
        return selected;
    }

    /**
     * Sets whether or not this thumbnail is selected. Selected thumbnails are
     * painted a little differently.
     * @param selected whether or not this thumbnail is selected.
     */
    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        //initialise required objects
        Graphics2D g2d = (Graphics2D) g;
        RoundRectangle2D.Float shapeBorder = new RoundRectangle2D.Float(0.0f, 0.0f, this.getWidth() - 3, this.getHeight() - 3, 6.0f, 6.0f);
        RoundRectangle2D.Float shapeInset;
        if (iconSize >= 28)
            shapeInset = new RoundRectangle2D.Float(3.0f, 3.0f, this.getWidth() - 9, this.getHeight() - 9, 10.0f, 10.0f);
        else
            shapeInset = new RoundRectangle2D.Float(2.0f, 2.0f, this.getWidth() - 7, this.getHeight() - 7, 8.0f, 8.0f);
        Area outer = new Area(shapeBorder);
        Area inner = new Area(shapeInset);
        outer.subtract(inner);

        //set graphics options
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        g2d.drawImage(thumbnail, 3, 3, this);

        //fill the background of the menu
        g2d.setPaint(background);
        g2d.fill(outer);
        if (selected)
            g2d.setPaint(selection);
        else
            g2d.setPaint(background2);
        g2d.fill(outer);

        //draw the border
        g2d.setPaint(borders);
        g2d.draw(outer);




    }
}
