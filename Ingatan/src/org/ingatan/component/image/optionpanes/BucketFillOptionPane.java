/*
 * BucketFillOptionPane.java
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

import org.ingatan.ThemeConstants;
import org.ingatan.component.OptionPane;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.RoundRectangle2D;
import javax.swing.*;
import javax.swing.event.ChangeListener;

/**
 * Provides an interface for the user to set the tolerance of the bucket fill tool.
 * A higher tolerance means that a broader range of similar colours are replaced.
 *
 * @author Thomas Everingham
 * @version 1.0
 */
public class BucketFillOptionPane extends JPanel implements OptionPane {
    /**
     * The slider used to alter the tolerance of the bucket fill tool to colour similarity.
     */
    JSlider tolerance = new JSlider();
    /**
     * Label for the tolerance slider.
     */
    JLabel lblTolerance = new JLabel("Tolerance: ");

    /**
     * Returns a new instance of <code>BucketFillOptionPane</code>.
     */
    public BucketFillOptionPane()
    {
        BoxLayout boxLayout = new BoxLayout(this,BoxLayout.Y_AXIS);
        this.setLayout(boxLayout);
        lblTolerance.setFont(ThemeConstants.niceFont);
        tolerance.setOpaque(false);

        tolerance.setMaximum(150);
        tolerance.setMinimum(0);
        tolerance.setMajorTickSpacing(10);
        tolerance.setMinorTickSpacing(1);


        tolerance.setValue(30);

        

        this.add(Box.createVerticalStrut(12));
        this.add(lblTolerance);
        this.add(Box.createVerticalStrut(7));
        this.add(tolerance);

        this.validate();
    }


    @Override
    public void paintComponent(Graphics g)
    {
        super.paintComponent(g);
        //initialise required objects
        Graphics2D g2d = (Graphics2D) g;
        RoundRectangle2D.Float shapeBorder = new RoundRectangle2D.Float(0.0f, 0.0f, this.getWidth() - 3, this.getHeight() - 3, 6.0f, 6.0f);

        //set graphics options
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);


        //fill the background of the pane
        g2d.setPaint(ThemeConstants.backgroundUnselected);
        g2d.fill(shapeBorder);

        //draw the border
        g2d.setPaint(ThemeConstants.borderUnselected);
        g2d.draw(shapeBorder);
    }

    /**
     * Adds the specified <code>ChangeListener</code> to both the brightness and contrast sliders.
     * @param change the <code>ChangeListener</code> to add.
     */
    public void addChangeListener(ChangeListener change)
    {
        tolerance.addChangeListener(change);
    }

    /**
     * Get the current brightness value.
     * @return the current brightness value.
     */
    public int getTolerance()
    {
        return tolerance.getValue();
    }

    /**
     * Checks whether or not the value is currently being adjusted.
     * @return whether or not the value is currently being adjusted.
     */
    public boolean isEitherSliderChanging()
    {
        if (tolerance.getValueIsAdjusting())
            return true;
        else
            return false;
    }

    /**
     * Sets the required tolerance value.
     * @param newTolerance the new tolerance. Value must be between 0 and 300.
     */
    public void setTolerance(double newTolerance)
    {
        if (newTolerance < 0) newTolerance = 0;
        if (newTolerance > 300) newTolerance = 300;
        tolerance.setValue((int) (newTolerance));
    }


    public void updateForNewColour(Color newFgColour, Color newBgColour) {
        return;
    }

    public void updateForAntialias(boolean antialias) {
        return;
    }

    public void rebuildSelf()
    {
        
    }
}
