/*
 * EmbeddedMathTeX.java
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
import org.ingatan.ThemeConstants;
import org.ingatan.component.colour.ColourChooserPane;
import org.ingatan.event.ColourChooserPaneEvent;
import org.ingatan.event.ColourChooserPaneListener;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import javax.swing.AbstractAction;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPopupMenu;
import javax.swing.JTextField;

/**
 * A JPanel which displays rendered JMathTeX. This is used as an embedded component for the
 * <code>RichTextArea</code> as it is able to pass back its JMathTeX for
 * serialisation.
 *
 * @author Thomas Everingham
 * @version 1.0
 */
public class EmbeddedMathTeX extends EmbeddedGraphic {

    /**
     * Text field for entering the formula.
     */
    protected JTextField txtFormula = new JTextField("Enter TeX here");
    /**
     * Numeric only text field for specifying the size of the render.
     */
    protected NumericJTextField txtSize = new NumericJTextField(25);
    /**
     * Label for the render size text field.
     */
    protected JLabel lblSize = new JLabel("size: ");
    /**
     * Popup that holds the text field used to edit the formula. This is an embedded
     * formula, so it is usually being displayed, not edited.
     */
    protected JPopupMenu popupFormulaEdit = new JPopupMenu();
    /**
     * Colour chooser for the render colour.
     */
    protected ColourChooserPane colChoose = new ColourChooserPane("resources/colour_choose_small.png", 5);
    /**
     * Popup for the colour chooser
     */
    protected JPopupMenu popupColourChoose = new JPopupMenu();
    /**
     * Button providing access to the colour chooser.
     */
    protected JButton btnColChoose = new JButton(new ColourChooserButtonAction());
    /**
     * Rendered formula.
     */
    protected BufferedImage renderedImage;
    /**
     * Render size
     */
    protected int size = 25;
    /**
     * The colour of the rendered math text.
     */
    protected Color renderColour = Color.black;

    public EmbeddedMathTeX(String mathTeX, int size, Color colour) {
        super();
        this.addMouseListener(new PanelMouseListener());
        this.setToolTipText("Click to edit");

        txtFormula.addKeyListener(new FormulaListener());
        txtSize.addKeyListener(new FormulaListener());
        txtFormula.setText(mathTeX);
        txtSize.setText("" + size);
        txtFormula.setMinimumSize(new Dimension(200, 20));
        txtFormula.setPreferredSize(new Dimension(200, 20));
        txtSize.setMinimumSize(new Dimension(75, 20));
        txtSize.setPreferredSize(new Dimension(75, 20));
        colChoose.setMinimumSize(new Dimension(100, 100));
        colChoose.setVisible(true);
        lblSize.setFont(ThemeConstants.niceFont);
        popupColourChoose.add(colChoose);
        btnColChoose.setBackground(renderColour);
        colChoose.addColourChooserPaneListener(new ColourChooserListener());
        renderColour = colour;

        this.size = size;
        popupFormulaEdit.setLayout(new BoxLayout(popupFormulaEdit, BoxLayout.Y_AXIS));
        popupFormulaEdit.add(txtFormula);
        Box horiz = Box.createHorizontalBox();
        horiz.add(lblSize);
        horiz.add(txtSize);
        horiz.add(btnColChoose);
        popupFormulaEdit.add(horiz);

        TeXFormula formula;
        try {
            formula = new TeXFormula(EmbeddedMathTeX.this.txtFormula.getText());
        } catch (ParseException ex) {
            renderedImage = new BufferedImage(0, 0, BufferedImage.TYPE_INT_ARGB);
            repaint();
            return;
        }

        if (EmbeddedMathTeX.this.txtFormula.getText().isEmpty()) {
            renderedImage = new BufferedImage(0, 0, BufferedImage.TYPE_INT_ARGB);
            repaint();
            return;
        }

        formula.setColor(renderColour);
        Icon icon = formula.createTeXIcon(TeXConstants.STYLE_DISPLAY, size);
        renderedImage = new BufferedImage(icon.getIconWidth(), icon.getIconHeight() + 6, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = renderedImage.createGraphics();
        g2.fillRect(0, 0, renderedImage.getWidth(), renderedImage.getHeight());
        icon.paintIcon(new JLabel(), g2, 0, 5);
        EmbeddedMathTeX.this.repaint();

    }

    /**
     * Gets the colour that the text is rendered in.
     * @return the colour that the text is rendered in.
     */
    public Color getRenderColour() {
        return renderColour;
    }

    /**
     * Sets the colour that the text is rendered in.
     * @param renderColour the colour that should be used to render the text.
     */
    public void setRenderColour(Color renderColour) {
        this.renderColour = renderColour;
    }

    /**
     * Gets the text describing the currently rendered math text.
     * @return the current math text.
     */
    public String getMathTeX() {
        return txtFormula.getText();
    }

    /**
     * Gets the render size of the math tex.
     * @return the render size of the math tex.
     */
    public int getRenderSize() {
        return size;
    }

    /**
     * Sets the render size of the math tex.
     * @param size the new render size of the math tex.
     */
    public void setRenderSize(int size) {
        this.size = size;
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (renderedImage == null) {
            return;
        }
        this.setSize(renderedImage.getWidth(), renderedImage.getHeight());
        Dimension d = new Dimension(renderedImage.getWidth(), renderedImage.getHeight());
        this.setPreferredSize(d);
        this.setMaximumSize(d);
        this.setMinimumSize(d);
        g.clearRect(0, 0, this.getWidth(), this.getHeight());
        g.drawImage(renderedImage, 0, 0, this);
    }

    /**
     * Listens for a change to the formula text field.
     */
    public class FormulaListener implements KeyListener {

        public void keyTyped(KeyEvent e) {
        }

        public void keyPressed(KeyEvent e) {
            if (txtSize.getText().isEmpty()) {
                size = 20;
                txtSize.setText("" + 20);
            } else {
                size = Integer.valueOf(txtSize.getText());
            }
            if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                TeXFormula formula;
                try {
                    formula = new TeXFormula(EmbeddedMathTeX.this.txtFormula.getText());
                } catch (ParseException ex) {
                    renderedImage = new BufferedImage(0, 0, BufferedImage.TYPE_INT_ARGB);
                    repaint();
                    return;
                }

                if (EmbeddedMathTeX.this.txtFormula.getText().isEmpty()) {
                    renderedImage = new BufferedImage(0, 0, BufferedImage.TYPE_INT_ARGB);
                    repaint();
                    return;
                }

                formula.setColor(renderColour);
                Icon icon = formula.createTeXIcon(TeXConstants.STYLE_DISPLAY, size);
                renderedImage = new BufferedImage(icon.getIconWidth(), icon.getIconHeight() + 6, BufferedImage.TYPE_INT_ARGB);
                Graphics2D g2 = renderedImage.createGraphics();
                g2.fillRect(0, 0, renderedImage.getWidth(), renderedImage.getHeight());
                icon.paintIcon(new JLabel(), g2, 0, 5);
                EmbeddedMathTeX.this.repaint();
            }
        }

        public void keyReleased(KeyEvent e) {
        }
    }

    /**
     * Shows the popup editor upon mouse click.
     */
    public class PanelMouseListener implements MouseListener {

        public void mouseClicked(MouseEvent e) {
            if (((RichTextArea) EmbeddedMathTeX.this.getParent().getParent()).isEditable() == false) {
                //if the rich text area is not editable, then we should not allow the embedded image to be editable.
                return;
            }

            popupFormulaEdit.show(EmbeddedMathTeX.this, e.getX(), e.getY());
        }

        public void mousePressed(MouseEvent e) {
        }

        public void mouseReleased(MouseEvent e) {
        }

        public void mouseEntered(MouseEvent e) {
        }

        public void mouseExited(MouseEvent e) {
        }
    }

    private class ColourChooserButtonAction extends AbstractAction {

        public void actionPerformed(ActionEvent e) {
            popupColourChoose.show(popupFormulaEdit.getInvoker(), popupFormulaEdit.getX(), popupFormulaEdit.getY());
        }
    }

    private class ColourChooserListener implements ColourChooserPaneListener {

        public void colourSelected(ColourChooserPaneEvent e) {

            if (e.getChangeSourceID() == ColourChooserPaneEvent.IMAGE_DRAG) {
                return;
            }

            btnColChoose.setBackground(e.getNewSelectedColour());
            renderColour = e.getNewSelectedColour();
            popupColourChoose.setVisible(false);

            TeXFormula formula;
            try {
                formula = new TeXFormula(EmbeddedMathTeX.this.txtFormula.getText());
            } catch (ParseException ex) {
                renderedImage = new BufferedImage(0, 0, BufferedImage.TYPE_INT_ARGB);
                repaint();
                return;
            }

            if (EmbeddedMathTeX.this.txtFormula.getText().isEmpty()) {
                renderedImage = new BufferedImage(0, 0, BufferedImage.TYPE_INT_ARGB);
                repaint();
                return;
            }

            formula.setColor(renderColour);
            Icon icon = formula.createTeXIcon(TeXConstants.STYLE_DISPLAY, size);
            renderedImage = new BufferedImage(icon.getIconWidth(), icon.getIconHeight() + 6, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2 = renderedImage.createGraphics();
            g2.fillRect(0, 0, renderedImage.getWidth(), renderedImage.getHeight());
            icon.paintIcon(new JLabel(), g2, 0, 5);

            EmbeddedMathTeX.this.repaint();
        }
    }
}
