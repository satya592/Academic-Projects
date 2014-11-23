/*
 * PaintedJPanel.java
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
package org.ingatan.component;

import java.awt.BasicStroke;
import java.awt.Color;
import org.ingatan.ThemeConstants;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.RoundRectangle2D;
import javax.swing.JPanel;

/**
 * Paints a border and background onto the panel. This was implemeneted late into the
 * project, so many components can be found which take care of their own painting rather
 * than extending this.
 *
 * @author Thomas Everingham
 * @version 1.0
 */
public class PaintedJPanel extends JPanel {

    Color border = ThemeConstants.borderUnselected;
    Color bg = ThemeConstants.backgroundUnselected;
    float borderWidth = 1.0f;

    public PaintedJPanel() {
    }

    public PaintedJPanel(Color border, Color bg) {
        this.border = border;
        this.bg = bg;
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        //initialise required objects
        Graphics2D g2d = (Graphics2D) g;
        RoundRectangle2D.Float shapeBorder = new RoundRectangle2D.Float(0.0f, 0.0f, this.getWidth() - 3, this.getHeight() - 3, 6.0f, 6.0f);

        //set graphics options
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);


        //fill the backgroundUnselected of the pane
        g2d.setPaint(bg);
        g2d.fill(shapeBorder);

        //draw the border
        g2d.setPaint(border);
        g2d.setStroke(new BasicStroke(borderWidth));
        g2d.draw(shapeBorder);

        paintExtension(g);
    }

    public void setBackgroundColour(Color newBg) {
        bg = newBg;
    }

    public void setBorderColour(Color newBorder) {
        border = newBorder;
    }

    public void setBorderWeigth(float weight)
    {
        borderWidth = weight;
    }

    /**
     * override me to continue painting in extension classes
     */
    public void paintExtension(Graphics g) {
    }
}
