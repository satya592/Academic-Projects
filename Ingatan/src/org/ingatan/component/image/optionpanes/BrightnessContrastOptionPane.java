/*
 * BrightnessContrastOptionPane.java
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
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.RoundRectangle2D;
import javax.swing.*;
import javax.swing.event.ChangeListener;

/**
 * Provides an interface for the user to set the brightness and contrast of the current image
 * or selection. This class consists of one JSlider for each of brightness and contrast,
 * as well as an 'Apply' button, and a 'Reset' button.
 *
 * @author Thomas Everingham
 * @version 1.0
 */
public class BrightnessContrastOptionPane extends JPanel implements OptionPane {
    /**
     * The slider used to alter the brightness of the current image or selection.
     */
    JSlider brightness = new JSlider();
    /**
     * The slider used to alter the contrast of the current image or selection.
     */
    JSlider contrast = new JSlider();
    /**
     * Label for the brightness slider.
     */
    JLabel lblBrightness = new JLabel("Brightness: ");
    /**
     * Label for the contrast slider.
     */
    JLabel lblContrast = new JLabel("Contrast: ");
    /**
     * The 'apply' button.
     */
    JButton btnApply = new JButton("Apply");
    /**
     * The 'reset' button.
     */
    JButton btnReset = new JButton("Reset");
    /**
     * Colour of the border of this panel.
     */
    private Color borders = new Color(26, 97, 110);
    /**
     * Colour of the background of this panel.
     */
    private Color background = new Color(222, 233, 233);
    /**
     * A nicer, smaller font still based on whatever the system has available (as it is taken from the JPanel)
     */
    private Font niceFont = new Font(this.getFont().getFamily(),Font.PLAIN,10);

    /**
     * Returns a new instance of <code>BrightnessContrastOptionPane</code>.
     */
    public BrightnessContrastOptionPane()
    {
        BoxLayout boxLayout = new BoxLayout(this,BoxLayout.Y_AXIS);
        this.setLayout(boxLayout);
        lblBrightness.setFont(niceFont);
        lblContrast.setFont(niceFont);
        btnApply.setFont(niceFont);
        btnReset.setFont(niceFont);
        brightness.setOpaque(false);
        contrast.setOpaque(false);

        brightness.setMaximum(255);
        brightness.setMinimum(-255);
        brightness.setMajorTickSpacing(10);
        brightness.setMinorTickSpacing(1);

        contrast.setMaximum(20);
        contrast.setMinimum(0);
        contrast.setMajorTickSpacing(2);
        contrast.setMinorTickSpacing(1);

        contrast.setValue(10);
        brightness.setValue(0);

        

        this.add(Box.createVerticalStrut(12));
        this.add(lblBrightness);
        this.add(Box.createVerticalStrut(7));
        this.add(brightness);
        this.add(Box.createVerticalStrut(15));
        this.add(lblContrast);
        this.add(Box.createVerticalStrut(7));
        this.add(contrast);
        this.add(Box.createVerticalStrut(20));
        this.add(btnApply);
        this.add(Box.createVerticalStrut(5));
        this.add(btnReset);

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
        g2d.setPaint(background);
        g2d.fill(shapeBorder);

        //draw the border
        g2d.setPaint(borders);
        g2d.draw(shapeBorder);
    }

    /**
     * Adds the specified <code>ChangeListener</code> to both the brightness and contrast sliders.
     * @param change the <code>ChangeListener</code> to add.
     */
    public void addChangeListener(ChangeListener change)
    {
        brightness.addChangeListener(change);
        contrast.addChangeListener(change);
    }

    /**
     * Get the current brightness value.
     * @return the current brightness value.
     */
    public int getBrightness()
    {
        return brightness.getValue();
    }

    /**
     * Checks whether or not one of the values is currently being adjusted.
     * @return whether or not one of the values is currently being adjusted.
     */
    public boolean isEitherSliderChanging()
    {
        if ((brightness.getValueIsAdjusting()) || (contrast.getValueIsAdjusting()))
            return true;
        else
            return false;
    }

    /**
     * Get the current contrast value.
     * @return the current contrast value.
     */
    public int getContrast()
    {
        return contrast.getValue();
    }

    /**
     * Sets the current contrast value.
     * @param newContrast the new contrast. Value must be between 0 and 2.0.
     */
    public void setContrast(double newContrast)
    {
        if (newContrast < 0) newContrast = 0;
        if (newContrast > 2) newContrast = 2;
        contrast.setValue((int) (10.0 * newContrast));
    }

    /**
     * Sets the required brightness value.
     * @param newBrightness the new brightness. Value must be between 0 and 256.
     */
    public void setBrightness(double newBrightness)
    {
        if (newBrightness < 0) newBrightness = 0;
        if (newBrightness > 255) newBrightness = 255;
        brightness.setValue((int) (newBrightness));
    }

    /**
     * Sets an action to be associated with the apply button.
     * @param apply the action to be associated with the apply button.
     */
    public void setActionApplyButton(Action apply)
    {
        btnApply.setAction(apply);
        btnApply.setText("Apply");
    }

    /**
     * Sets an action to be associated with the reset button.
     * @param reset the action to be associated with the reset button.
     */
    public void setActionResetButton(Action reset)
    {
        btnReset.setAction(reset);
        btnReset.setText("Reset");
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
