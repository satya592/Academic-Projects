/*
 * JMathTeXOptionPane.java
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
import org.ingatan.component.text.NumericJTextField;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

/**
 * Option pane for the JMathTeX tool in the image editor. Also used in the ImageAcquisitionDialog.
 * Provides buttons for inserting TeX templates (e.g. fractions), and a cheat sheet for symbol codes.
 *
 * @author Thomas Everingham
 * @version 1.0
 */
public class JMathTeXOptionPane extends JPanel implements OptionPane {

    /**
     * Size of the math text render.
     */
    NumericJTextField txtSize = new NumericJTextField(20);
    /**
     * Label for the render lblSize field.
     */
    JLabel lblSize = new JLabel("Size: ");
    /**
     * Size of the math text render.
     */
    int renderSize = 20;
    /**
     * The text box instance used to edit the formula that should be rendered.
     */
    private JTextField editorField;
    /**
     * Cheat sheet button for the fraction.
     */
    private JButton btnFraction = new JButton(new FractionAction());
    /**
     * Cheat sheet button for superscript.
     */
    private JButton btnSuperscript = new JButton(new SupAction());
    /**
     * Cheat sheet button for subscript.
     */
    private JButton btnSubscript = new JButton(new SubAction());
    /**
     * Cheat sheet button for an integral.
     */
    private JButton btnIntegral = new JButton(new IntegralAction());
    /**
     * Cheat sheet button for sum.
     */
    private JButton btnSum = new JButton(new SumAction());
    /**
     * Cheat sheet button for a product.
     */
    private JButton btnProduct = new JButton(new ProductAction());
    /**
     * Cheat sheet button for a square root.
     */
    private JButton btnSqrt = new JButton(new SqrtAction());
    /**
     * Cheat sheet button for an nth root.
     */
    private JButton btnNthRoot = new JButton(new NthRootAction());
    /**
     * Cheat sheet button for a space.
     */
    private JButton btnSpace = new JButton(new SpaceAction());
    /**
     * Cheat sheet button for roman font.
     */
    private JButton btnRoman = new JButton(new RomanFontAction());
    /**
     * Cheat sheet button for displaying symbol cheat sheet window.
     */
    private JButton btnSymbolCheat = new JButton(new SymbolCheatSheetAction());
    /**
     * Displays the symbol cheat sheet.
     */
    private JPopupMenu cheatSheetPopup = new JPopupMenu();

    public JMathTeXOptionPane(JTextField editor) {
        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        editorField = editor;

        cheatSheetPopup.add(new CheatSheet());

        Box boxSize = Box.createHorizontalBox();
        boxSize.add(lblSize);
        boxSize.add(txtSize);
        this.setBorder(BorderFactory.createEmptyBorder(3, 3, 3, 3));
        this.add(boxSize);
        this.add(btnFraction);
        this.add(btnSuperscript);
        this.add(btnSubscript);
        this.add(btnIntegral);
        this.add(btnSum);
        this.add(btnProduct);
        this.add(btnSqrt);
        this.add(btnNthRoot);
        this.add(btnSpace);
        this.add(btnRoman);
        this.add(btnSymbolCheat);
        lblSize.setFont(ThemeConstants.niceFont);
        lblSize.setLocation(5, 3);
        txtSize.getDocument().addDocumentListener(new sizeDocListener());
        txtSize.setPreferredSize(new Dimension(100, 20));
        txtSize.setMaximumSize(new Dimension(200, 20));
        
        btnFraction.setFont(ThemeConstants.niceFont);
        btnFraction.setFocusable(false);
        btnFraction.setMargin(new Insets(1, 1, 1, 1));
        btnSuperscript.setFont(ThemeConstants.niceFont);
        btnSuperscript.setFocusable(false);
        btnSuperscript.setMargin(new Insets(1, 1, 1, 1));
        btnSubscript.setFont(ThemeConstants.niceFont);
        btnSubscript.setFocusable(false);
        btnSubscript.setMargin(new Insets(1, 1, 1, 1));
        btnIntegral.setFont(ThemeConstants.niceFont);
        btnIntegral.setFocusable(false);
        btnIntegral.setMargin(new Insets(1, 1, 1, 1));
        btnSum.setFont(ThemeConstants.niceFont);
        btnSum.setFocusable(false);
        btnSum.setMargin(new Insets(1, 1, 1, 1));
        btnProduct.setFont(ThemeConstants.niceFont);
        btnProduct.setFocusable(false);
        btnProduct.setMargin(new Insets(1, 1, 1, 1));
        btnSqrt.setFont(ThemeConstants.niceFont);
        btnSqrt.setFocusable(false);
        btnSqrt.setMargin(new Insets(1, 1, 1, 1));
        btnNthRoot.setFont(ThemeConstants.niceFont);
        btnNthRoot.setFocusable(false);
        btnNthRoot.setMargin(new Insets(1, 1, 1, 1));
        btnSpace.setFont(ThemeConstants.niceFont);
        btnSpace.setFocusable(false);
        btnSpace.setMargin(new Insets(1, 1, 1, 1));
        btnRoman.setFont(ThemeConstants.niceFont);
        btnRoman.setFocusable(false);
        btnRoman.setMargin(new Insets(1, 1, 1, 1));
        btnSymbolCheat.setFont(ThemeConstants.niceFont);
        btnSymbolCheat.setFocusable(false);
        btnSymbolCheat.setMargin(new Insets(1, 1, 1, 1));
        this.validate();
    }

    /**
     * Not used.
     */
    public void updateForNewColour(Color newFgColour, Color newBgColour) {
    }

    /**
     * Not used.
     */
    public void updateForAntialias(boolean antialias) {
    }

    /**
     * Not used.
     */
    public void rebuildSelf() {
    }

    /**
     * Gets the lblSize at which the math text should be rendered.
     * @return the lblSize at which the math text should be rendered.
     */
    public int getRenderSize() {
        return renderSize;
    }

    /**
     * Listens for any changes to the document.
     */
    public class sizeDocListener implements DocumentListener {

        public void insertUpdate(DocumentEvent e) {
            try {
                renderSize = Integer.valueOf(txtSize.getText());
            } catch (NumberFormatException ignore) {
            }
        }

        public void removeUpdate(DocumentEvent e) {
            try {
                renderSize = Integer.valueOf(txtSize.getText());
            } catch (NumberFormatException ignore) {
            }
        }

        public void changedUpdate(DocumentEvent e) {
        }
    }

    private class FractionAction extends AbstractAction {

        public FractionAction() {
            super("Fraction");
        }

        public void actionPerformed(ActionEvent e) {
            editorField.replaceSelection("\\frac{numerator}{denominator}");
        }
    }

    private class SupAction extends AbstractAction {

        public SupAction() {
            super("Superscript");
        }

        public void actionPerformed(ActionEvent e) {
            editorField.replaceSelection("{a}^{b}");
        }
    }

    private class SubAction extends AbstractAction {

        public SubAction() {
            super("Subscript");
        }

        public void actionPerformed(ActionEvent e) {
            editorField.replaceSelection("{a}_{b}");
        }
    }

    private class IntegralAction extends AbstractAction {

        public IntegralAction() {
            super("Integral");
        }

        public void actionPerformed(ActionEvent e) {
            editorField.replaceSelection("\\int_{from}^{to}");
        }
    }

    private class SumAction extends AbstractAction {

        public SumAction() {
            super("Sum");
        }

        public void actionPerformed(ActionEvent e) {
            editorField.replaceSelection("\\sum_{from}^{to}");
        }
    }

    private class ProductAction extends AbstractAction {

        public ProductAction() {
            super("Product");
        }

        public void actionPerformed(ActionEvent e) {
            editorField.replaceSelection("\\prod_{from}^{to}");
        }
    }

    private class SymbolCheatSheetAction extends AbstractAction {

        public SymbolCheatSheetAction() {
            super("Symbols Cheat Sheet");
        }

        public void actionPerformed(ActionEvent e) {
            cheatSheetPopup.show(btnSymbolCheat, 0, 0);
        }
    }

    private class SqrtAction extends AbstractAction {

        public SqrtAction() {
            super("Square Root");
        }

        public void actionPerformed(ActionEvent e) {
            editorField.replaceSelection("\\sqrt{term}");
        }
    }

    private class NthRootAction extends AbstractAction {

        public NthRootAction() {
            super("Nth Root");
        }

        public void actionPerformed(ActionEvent e) {
            editorField.replaceSelection("\\sqrt[root]{term}");
        }
    }

    private class SpaceAction extends AbstractAction {

        public SpaceAction() {
            super("Space");
        }

        public void actionPerformed(ActionEvent e) {
            editorField.replaceSelection("\\nbsp");
        }
    }

    private class RomanFontAction extends AbstractAction {

        public RomanFontAction() {
            super("Roman Font");
        }

        public void actionPerformed(ActionEvent e) {
            editorField.replaceSelection("\\mathrm{text-here}");
        }
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
        g2d.setPaint(ThemeConstants.backgroundUnselected);
        g2d.fill(shapeBorder);

        //draw the border
        g2d.setPaint(ThemeConstants.borderUnselected);
        g2d.draw(shapeBorder);
    }

    private class CheatSheet extends JPanel {
        BufferedImage sheet = null;

        public CheatSheet() {
            try {
                sheet = ImageIO.read(Thread.currentThread().getContextClassLoader().getResource("resources/symbolsCheatSheet.png"));
            } catch (Exception ignore) {
            }
            if (sheet != null) {
                this.setSize(sheet.getWidth(), sheet.getHeight());
                this.setPreferredSize(new Dimension(sheet.getWidth(), sheet.getHeight()));
            }
        }

        @Override
        public void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (sheet != null)
                g.drawImage(sheet, 0, 0, this);
        }
    }
}
