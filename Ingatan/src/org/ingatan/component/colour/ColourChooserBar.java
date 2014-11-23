/*
 * ColourChooserBar.java
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

package org.ingatan.component.colour;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.RoundRectangle2D;
import javax.swing.JPanel;

/**
 * A combination of current colour preview, colour history swatch and an RGB
 * editor. This component is designed for use in the <code>ColourChooserPopup</code>
 * component.
 *
 * @author Thomas Everingham
 * @version 1.0
 */
public class ColourChooserBar extends JPanel {

    /**
     * Colour to paint as the preview.
     */
    private Color colourPreview = Color.WHITE;
    /**
     * Colour of the border of the bar, as well as the border of the selction.
     */
    private Color borders = new Color(26, 97, 110);
    /**
     * Colour of the background of the bar.
     */
    private Color background = new Color(222, 233, 233);
    /**
     * Saves the 10 most recently used colours
     */
    private HistorySwatch swatch;
    /**
     * Provides the RBG values for the currently selected colour, and allows the user
     * to edit these values.
     */
    private RGBInfoPanel rgbInfo;

    public ColourChooserBar(int historyLength) {
        swatch = new HistorySwatch(historyLength);
        rgbInfo = new RGBInfoPanel(0, 0, 0);

        this.setOpaque(false);
        this.setLayout(null);
        this.setSize(swatch.getWidth() + rgbInfo.getWidth() + 30, 20);


        this.add(swatch);
        swatch.setLocation(25, 2);
        this.add(rgbInfo);
        rgbInfo.setLocation(28+swatch.getWidth(), 3);
        //this.validate();
    }

    /**
     * Gets the RGB info panel used by this component.
     * @return the RGB info panel used by this component.
     */
    public RGBInfoPanel getRGBInfoPanel()
    {
        return rgbInfo;
    }

    /**
     * Gets the history swatch instance used by this component.
     * @return the history swatch instance used by this comoponent.
     */
    public HistorySwatch getHistorySwatch()
    {
        return swatch;
    }

    /**
     * Sets the preview colour to be displayed by this component.
     * @param colour the preview colour to be displayed by this component.
     */
    public void setPreviewColour(Color colour)
    {
        if (colour != null)
            colourPreview = colour;
    }


    @Override
    public void paintComponent(Graphics g) {
        //super painting tasks
        super.paintComponent(g);

        //initialise required objects
        Graphics2D g2d = (Graphics2D) g;
        RoundRectangle2D.Float shapeBg = new RoundRectangle2D.Float(0.0f, 0.0f, this.getWidth() - 3, this.getHeight() - 3, 6.0f, 6.0f);
        RoundRectangle2D.Float shapePreview = new RoundRectangle2D.Float(3.0f, 3.0f, 18.0f, 11.0f, 6.0f, 6.0f);

        //set graphics options
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        //fill the background of the bar
        g2d.setPaint(background);
        g2d.fill(shapeBg);
        //fill the border of the bar
        g2d.setPaint(borders);
        g2d.draw(shapeBg);

        //fill the background of the preview rectangle
        g2d.setPaint(colourPreview);
        g2d.fill(shapePreview);
        //fill the border of the bar
        g2d.setPaint(borders);
        g2d.draw(shapePreview);

    }
}
