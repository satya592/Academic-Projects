/*
 * JMathTeXCreationPane.java
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

import be.ugent.caagt.jmathtex.ParseException;
import be.ugent.caagt.jmathtex.TeXConstants;
import be.ugent.caagt.jmathtex.TeXFormula;
import org.ingatan.component.image.optionpanes.JMathTeXOptionPane;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

/**
 * This pane is presented as a tool as part of the <code>ImageAcquisitionDialog</code>/. It
 * allows the user to create a math text formula for direct insertion. Adding math text in this
 * way allows it to be edited later.
 *
 * @author Thomas Everingham
 * @version 1.0
 */
public class JMathTeXCreationPane extends JPanel {

    /**
     * Where the TeX code is entered.
     */
    JTextField txtMath = new JTextField();
    /**
     * The option pane as taken from the image editor. Allows the user to specify render size
     * as well as provides buttons for inserting template code (e.g. integrate) and a cheat sheet
     * for common symbols.
     */
    JMathTeXOptionPane optionPane = new JMathTeXOptionPane(txtMath);
    /**
     * Preview of the JMatTeX render.
     */
    JPanel preview = new JPanel();
    /**
     * Button which redraws the preview.
     */
    JButton drawPreview = new JButton(new DrawAction());

    /**
     * Creates a new <code>JMathTeXCreationPane</code>.
     */
    public JMathTeXCreationPane() {
        this.setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
        this.add(optionPane);
        this.add(Box.createHorizontalStrut(4));
        optionPane.setVisible(true);

        Box colBox = Box.createVerticalBox();
        Box rowBox = Box.createHorizontalBox();

        txtMath.setMaximumSize(new Dimension(700,25));
        txtMath.setMinimumSize(new Dimension(100,25));
        drawPreview.setMaximumSize(new Dimension(100,25));
        rowBox.add(txtMath);
        rowBox.add(drawPreview);

        colBox.add(rowBox);
        colBox.add(preview);
        preview.setBorder(BorderFactory.createEtchedBorder());
        this.add(colBox);

    }

    /**
     * Clears the math text box.
     */
    public void clearTextField() {
        txtMath.setText("");
    }

    /**
     * Gets the math text that has been entered by the user.
     * @return the math text that has been entered by the user.
     */
    public String getMathText()
    {
        return txtMath.getText();
    }

    /**
     * Gets the render size that has been selected by the user.
     * @return the render size that has been selected by the user.
     */
    public int getRenderSize()
    {
        return optionPane.getRenderSize();
    }

    /**
     * Call JMathTeX to render the formula, and then draw it to the preview pane.
     */
    public class DrawAction extends AbstractAction {

        public DrawAction() {
            super("Preview");
        }

        public void actionPerformed(ActionEvent e) {
            //BufferedImage preview;

            TeXFormula formula = null;
            try {
                formula = new TeXFormula(txtMath.getText());
            } catch (ParseException ex) {
              //  preview = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
            }

            if (txtMath.getText().isEmpty()) {
                //preview = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
            }
            formula.setBackground(Color.white);
            formula.setColor(Color.black);
            Icon icon = formula.createTeXIcon(TeXConstants.STYLE_DISPLAY, optionPane.getRenderSize());
            //preview = new BufferedImage(icon.getIconWidth() + 1, icon.getIconHeight() + 5, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2d = (Graphics2D) preview.getGraphics();
            g2d.setColor(Color.white);
            g2d.fillRect(0, 0, preview.getWidth(), preview.getHeight());
            icon.paintIcon(new JLabel(), g2d, 0, 5);
        }
    }
}
