/*
 * RGBInfoPanel.java
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

import org.ingatan.component.text.NumericJTextField;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyListener;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * This panel encapsulates three RBG values as well as providing an interface with
 * which the user may get or set the values. This class has been created as a subcomponent
 * of the <code>ColourChooserPopup</code> class.
 *
 * @author Thomas Everingham
 * @version 1.0
 */
public class RGBInfoPanel extends JPanel {

    /**
     * Numeric only text field for the red value.
     */
    private NumericJTextField rValue;
    /**
     * Numeric only text field for the green value.
     */
    private NumericJTextField gValue;
    /**
     * Numeric only text field for the blue value.
     */
    private NumericJTextField bValue;
    /**
     * Height of the R,G,and B text fields.
     */
    private final int txtHeight = 12;
    /**
     * Width of the R,G, and B text fields.
     */
    private final int txtWidth = 20;
    /**
     * Width of the labels.
     */
    private final int lblWidth = 10;
    /**
     * Font size to use for the RGB labels and text field text.
     */
    private final int fontSize = 9;
    /**
     * Size of the text fields.
     */
    Dimension txtSize;
    /**
     * Font to use for the labels and text feild.
     */
    Font f;
    /**
     * Colour of the border of the textfields, as well as the font colour of the labels.
     */
    private Color borders = new Color(26, 97, 110);

    /**
     * Creates a new RGBInfoPanel object with initial values r, g, and b.
     * 
     * @param r value for red
     * @param g value for green
     * @param b value for blue
     */
    public RGBInfoPanel(int r, int g, int b) {
        this.setLayout(new FlowLayout(FlowLayout.LEFT, 1, 0));

        f = new Font(this.getFont().getFamily(), Font.PLAIN, fontSize);
        txtSize = new Dimension(txtWidth, txtHeight);

        rValue = createTextField(r);
        gValue = createTextField(g);
        bValue = createTextField(b);

        JLabel lblR = createLabel("R:");
        JLabel lblG = createLabel("G:");
        JLabel lblB = createLabel("B:");

        this.add(lblR);
        this.add(rValue);
        this.add(lblG);
        this.add(gValue);
        this.add(lblB);
        this.add(bValue);
        this.setPreferredSize(new Dimension(30 + txtWidth * 3 + 10, 13));
        this.setSize((lblWidth + txtWidth) * 3 + 10, 13);

        this.setOpaque(false);
    }

    /**
     * Instantiates a numeric only text field with the preferred size, specified font
     * and a line border.
     * @param initialValue initial value of the text field
     * @return an instance of <code>NumericJTextField</code> with initial text, size, font and border set.
     */
    private NumericJTextField createTextField(int initialValue) {
        NumericJTextField retVal = new NumericJTextField(initialValue);
        retVal.setPreferredSize(txtSize);
        retVal.setFont(f);
        retVal.setBorder(BorderFactory.createLineBorder(borders));
        retVal.addFocusListener(new RGBTextFocusListener());
        return retVal;
    }


    /**
     * Instantiates a JLabel with the specified text and preferred size, font and foreground colour.
     * @param text the label text.
     * @return an instance of <code>JLabel</code> with font, size, text, and foreground colour properties set.
     */
    private JLabel createLabel(String text) {
        JLabel retVal = new JLabel(text);
        retVal.setFont(f);
        retVal.setForeground(borders);
        retVal.setPreferredSize(new Dimension(lblWidth, 8));
        retVal.setOpaque(false);
        return retVal;
    }

    public void addTextFieldKeyListener(KeyListener listener)
    {
        rValue.addKeyListener(listener);
        gValue.addKeyListener(listener);
        bValue.addKeyListener(listener);
    }

    /**
     * Sets the RGB values to those specified.
     * @param r the value for red
     * @param g the value for green
     * @param b the value for blue
     */
    public void setRGB(int r, int g, int b) {
        rValue.setText("" + r);
        gValue.setText("" + g);
        bValue.setText("" + b);
    }

    /**
     * Gets the value for red.
     * @return the value for red.
     */
    public int getR() {
        if (rValue.getText().isEmpty())
            rValue.setText("0");
        return Integer.valueOf(rValue.getText());
    }

    /**
     * Gets the value for green.
     * @return the value for green.
     */
    public int getG() {
        if (gValue.getText().isEmpty())
            gValue.setText("0");
        return Integer.valueOf(gValue.getText());
    }

    /**
     * Gets the value for blue.
     * @return the value for blue.
     */
    public int getB() {
        if (bValue.getText().isEmpty())
            bValue.setText("0");
        return Integer.valueOf(bValue.getText());
    }

    /**
     * Listens for gain and loss of focus so as to implement 'selectAll-on-focus' and
     * 'deselectAll-on-focus-lost'..
     */
    public class RGBTextFocusListener implements FocusListener {

        public void focusGained(FocusEvent e) {
            NumericJTextField source = ((NumericJTextField) e.getComponent());
            source.setSelectionStart(0);
            source.setSelectionEnd(source.getText().length());
        }

        public void focusLost(FocusEvent e) {
            ((NumericJTextField) e.getComponent()).setSelectionStart(0);
            ((NumericJTextField) e.getComponent()).setSelectionEnd(0);
        }

    }
}
