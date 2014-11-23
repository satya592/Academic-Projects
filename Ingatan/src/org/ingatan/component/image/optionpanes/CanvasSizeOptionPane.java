/*
 * CanvasSizeOptionPane.java
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
import org.ingatan.component.text.NumericJTextField;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import javax.swing.*;

/**
 * A component used for setting the canvas size of the image editor. This component
 * was designed to be used within a <code>JPopupMenu</code>.
 *
 * @author Thomas Everingham
 * @version 1.0
 */
public class CanvasSizeOptionPane extends JPanel {
    /**
     * A nicer font for the components; smaller version of the panel default.
     */
    private Font niceFont = new Font(this.getFont().getFamily(),Font.PLAIN,9);
    /**
     * Canvas size label.
     */
    private JLabel lblTitle = new JLabel("Canvas size: ");
    /**
     * Width label.
     */
    private JLabel lblWidth = new JLabel("w:");
    /**
     * Height label.
     */
    private JLabel lblHeight = new JLabel("h:");
    /**
     * Numeric only text field for the width.
     */
    private NumericJTextField txtWidth = new NumericJTextField(0);
    /**
     * Numeric only text field for the height.
     */
    private NumericJTextField txtHeight = new NumericJTextField(0);
    /**
     * The accept button.
     */
    private JButton btnAccept = new JButton();
    /**
     * Focus listener implementation so that when the text fields gain focus, the value is already highlighted.
     */
    private TextFocus txtFocus = new TextFocus();


    public CanvasSizeOptionPane(Action a)
    {
        super();
        this.setLayout(new BoxLayout(this,BoxLayout.Y_AXIS));

        txtWidth.setPreferredSize(new Dimension(27,15));
        txtWidth.setMinimumSize(new Dimension(27,15));
        txtWidth.setMaximumSize(new Dimension(27,15));
        txtWidth.setFont(niceFont);

        txtHeight.setFont(niceFont);
        txtHeight.setPreferredSize(new Dimension(27,15));
        txtHeight.setMinimumSize(new Dimension(27,15));
        txtHeight.setMaximumSize(new Dimension(27,15));

        InputMap inmap = txtWidth.getInputMap();
        inmap.put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), a);
        inmap = txtHeight.getInputMap();
        inmap.put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), a);

        txtHeight.addFocusListener(txtFocus);
        txtWidth.addFocusListener(txtFocus);

        btnAccept.setAction(a);
        btnAccept.setIcon(new ImageIcon(CanvasSizeOptionPane.class.getResource("/resources/icons/accept.png")));
        btnAccept.setPreferredSize(new Dimension(18,18));
        btnAccept.setMaximumSize(new Dimension(18,18));
        lblWidth.setFont(niceFont);
        lblHeight.setFont(niceFont);
        lblTitle.setFont(niceFont);

        Box lblBox = Box.createHorizontalBox();
        Box mainBox = Box.createHorizontalBox();

        lblBox.add(lblTitle);
        this.add(lblBox);

        mainBox.add(lblWidth);
        mainBox.add(txtWidth);
        mainBox.add(Box.createHorizontalStrut(3));
        mainBox.add(lblHeight);
        mainBox.add(txtHeight);
        mainBox.add(Box.createHorizontalStrut(7));
        mainBox.add(btnAccept);
        this.add(mainBox);
        
        
        this.setBorder(BorderFactory.createEmptyBorder(0, 2, 0, 2));
        this.setPreferredSize(new Dimension(70,45));
    }

    /**
     * Gets the size specified in the text boxes.
     * @return the size specified in the text boxes.
     */
    public Dimension getSpecifiedSize()
    {
        return new Dimension(Integer.valueOf(txtWidth.getText()),Integer.valueOf(txtHeight.getText()));
    }

    /**
     * Sets the specified size to the text boxes.
     * @param newSize the size that you wish to be set to the text boxes.
     */
    public void setSpecifiedSize(Dimension newSize)
    {
        txtWidth.setText(String.valueOf(newSize.width));
        txtHeight.setText(String.valueOf(newSize.height));
    }


    /**
     * Focus listener used to highlight text in the text boxes when they gain focus.
     */
    public class TextFocus implements FocusListener
    {

        public void focusGained(FocusEvent e) {
            ((JTextField) e.getComponent()).setSelectionStart(0);
            ((JTextField) e.getComponent()).setSelectionEnd(((JTextField) e.getComponent()).getText().length());
        }

        public void focusLost(FocusEvent e) {
            
        }

    }

}
