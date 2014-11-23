/*
 * SelectorTab.java
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
package org.ingatan.component.librarymanager;

import org.ingatan.ThemeConstants;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.geom.RoundRectangle2D;
import javax.swing.JPanel;
import org.ingatan.io.IOManager;

/**
 * This is the selection tab for question containers. It is simply a painted JPanel
 * extension which encapsulates selection state.
 * 
 * @author Thomas Everingham
 * @version 1.0
 */
public class SelectorTab extends JPanel {

    protected boolean selected = false;
    protected boolean mouseOver = false;
    protected Image selectedImage;

    public SelectorTab(boolean initialState, int width, int height) {
        selected = initialState;
        this.setVisible(true);
        this.setMaximumSize(new Dimension(width + 2, height + 2));
        this.setMinimumSize(new Dimension(width - 2, height - 2));
        this.setPreferredSize(new Dimension(width, height));
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2d = (Graphics2D) g;
        RoundRectangle2D.Float shapeBorder = new RoundRectangle2D.Float(0.0f, 0.0f, this.getWidth() - 3, this.getHeight() - 3, 6.0f, 6.0f);

        //set graphics options
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);



        //fill the shape
        //this superfluous if-else block is as it is so that the background colours while selected
        //can be changed later if desired. Originally, selection was indicated by a red colour being
        //painted to the background, but now a little michael with a red ball is drawn to the selected tabs.
        if ((selected) && (mouseOver)) {
            g2d.setPaint(ThemeConstants.backgroundUnselectedHover);
        } else if ((selected) && (!mouseOver)) {
            g2d.setPaint(ThemeConstants.backgroundUnselected);
        } else if ((!selected) && (mouseOver)) {
            g2d.setPaint(ThemeConstants.backgroundUnselectedHover);
        } else if ((!selected) && (!mouseOver)) {
            g2d.setPaint(ThemeConstants.backgroundUnselected);
        }

        g2d.fill(shapeBorder);

        //paint the selected-state icon
        if (selected) {
            g2d.drawImage(selectedImage, (this.getWidth() - selectedImage.getWidth(this) - 2) / 2, (this.getHeight() - selectedImage.getHeight(this)-2) / 2, this);
        }

        //draw the border
        if ((selected) && (mouseOver)) {
            g2d.setPaint(ThemeConstants.borderSelectedHover);
        } else if ((selected) && (!mouseOver)) {
            g2d.setPaint(ThemeConstants.borderSelected);
        } else if ((!selected) && (mouseOver)) {
            g2d.setPaint(ThemeConstants.borderUnselectedHover);
        } else if ((!selected) && (!mouseOver)) {
            g2d.setPaint(ThemeConstants.borderUnselected);
        }
        g2d.draw(shapeBorder);
    }

    /**
     * Gets whether or not this selector is in the MouseOver state.
     * @return whether or not this selector is in the MouseOver state.
     */
    public boolean isMouseOver() {
        return mouseOver;
    }

    /**
     * Sets whether or not this selector is in the MouseOver state.
     * @param mouseOver true if this selector is in the MouseOver state.
     */
    public void setMouseOver(boolean mouseOver) {
        this.mouseOver = mouseOver;
    }

    /**
     * Gets whether or not this selector is in the Selected state.
     * @return whether or not this selector is in the Selected state.
     */
    public boolean isSelected() {
        return selected;
    }

    /**
     * Sets whether or not this selector is in the Selected state.
     * @param selected true if this selector is in the Selected state.
     */
    public void setSelected(boolean selected) {
        this.selected = selected;
        selectedImage = IOManager.getSelectionIcon();
    }
}
