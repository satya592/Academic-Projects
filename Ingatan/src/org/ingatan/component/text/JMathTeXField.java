/*
 * JMathTeXField.java
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

package org.ingatan.component.text;

import be.ugent.caagt.jmathtex.ParseException;
import be.ugent.caagt.jmathtex.TeXConstants;
import be.ugent.caagt.jmathtex.TeXFormula;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

/**
 * A panel for entering and previewing JMathTeX strings.
 * 
 * @author Thomas Everingham
 * @version 1.0
 */
public class JMathTeXField extends JPanel {

    JTextField txtFormula = new JTextField("Enter TeX here.");
    BufferedImage renderedImage;

    public JMathTeXField() {
        super();
        this.add(txtFormula);
        txtFormula.addKeyListener(new FormulaListener());
    }

    @Override
    public void paintComponent(Graphics g) {
        g.clearRect(0, 0, this.getWidth(), this.getHeight());
        g.drawImage(renderedImage, 10, 40, this);
    }

    public class FormulaListener implements KeyListener {

        public void keyTyped(KeyEvent e) {
        }

        public void keyPressed(KeyEvent e) {
            if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                TeXFormula formula;
                try {
                    formula = new TeXFormula(JMathTeXField.this.txtFormula.getText());
                } catch (ParseException ex) {
                    renderedImage = new BufferedImage(0,0,BufferedImage.TYPE_INT_ARGB);
                    repaint();
                    return;
                }

                if (JMathTeXField.this.txtFormula.getText().isEmpty())
                {
                    renderedImage = new BufferedImage(0,0,BufferedImage.TYPE_INT_ARGB);
                    repaint();
                    return;
                }

                Icon icon = formula.createTeXIcon(TeXConstants.STYLE_DISPLAY, 25);
                renderedImage = new BufferedImage(icon.getIconWidth(), icon.getIconHeight()+5, BufferedImage.TYPE_INT_ARGB);
                Graphics2D g2 = renderedImage.createGraphics();
		icon.paintIcon(new JLabel(), g2, 0, 5);
                JMathTeXField.this.repaint();
            }
        }

        public void keyReleased(KeyEvent e) {
        }
    }
}
