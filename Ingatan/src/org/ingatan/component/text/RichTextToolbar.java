/*
 * RichTextToolbar.java
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

import org.ingatan.component.colour.ColourChooserPane;
import org.ingatan.component.ThinScrollComboBoxUI;
import org.ingatan.event.ColourChooserPaneListener;
import org.ingatan.event.RichTextToolbarEvent;
import org.ingatan.event.RichTextToolbarListener;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JTextField;
import javax.swing.border.Border;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;
import javax.swing.text.StyleConstants;

/**
 * Provides the GUI for a rich text editor toolbar. You may listen for events using
 * by adding a RichTextToolbarListener.<br>
 *<br>
 * This class does the following:<br>
 * <ul>
 * <li>sets up all buttons and combo boxes</li>
 * <li>loads all icon resources</li>
 * <li>assigns actions to the buttons, the actions simply tell all the <code>RichTextToolbarListener</code>s that
 * a button has been pressed.</li>
 * <li>listens for combo box events and tells the <code>RichTextToolbarListener</code>s that an aspect of the font
 * has changed (i.e. size,face,colour).</li>
 * <li>displays an undecorated JFrame when the font colour button is pressed. This frame is positioned at the button's screen location,
 * and houses the colour chooser. This class listens for the loss of focus of the chooser, and hides the chooser if loss of focus occurs.</li>
 * <li>Allows you to add and remove <code>ColourChooserPaneListener</code>s which can be used to detect any change in colour selection, as well as how
 * that change came about (i.e. through click of the image, or swatch, or setting of the RGB values, etc.)</li>
 *</ul>
 * @author Thomas Everingham
 * @version 1.0
 */
public class RichTextToolbar extends JPanel {

    /**
     * Toolbar buttons are laid out one after the other, horizontally, with the 
     * font and font size combos toward the end. 
     */
    public static final int HORIZONTAL_LAYOUT = 0;
    /**
     * Toolbar buttons are laid out vertically in horizontal groups of maximum 4 buttons.
     * Font and font size combo boxes are placed at the bottom.
     */
    public static final int VERTICAL_LAYOUT = 1;
    /**
     * Bold button.
     */
    private JButton btnBold;
    /**
     * Italic button.
     */
    private JButton btnItalic;
    /**
     * Underline button.
     */
    private JButton btnUnderline;
    /**
     * Superscript button.
     */
    private JButton btnSuperscript;
    /**
     * Subscript button.
     */
    private JButton btnSubscript;
    /**
     * Text colour button. This will bring up a small swatch.
     */
    private JButton btnTextColour;
    /**
     * Insert picture button. Brings up the image acquisition dialogue.
     */
    private JButton btnInsertPicture;
    /**
     * Align left button.
     */
    private JButton btnAlignLeft;
    /**
     * Align centre button.
     */
    private JButton btnAlignCentre;
    /**
     * Align right button.
     */
    private JButton btnAlignRight;
    /**
     * Font chooser combo box.
     */
    private JComboBox comboFonts;
    /**
     * Font size chooser combo box.
     */
    private JComboBox comboSizes;
    /**
     * The listener associated with this toolbar.
     */
    private RichTextToolbarListener[] toolbarListeners = new RichTextToolbarListener[0];
    /**
     * Frame used as the colour chooser.
     */
    private ColourChooserPane colourChooser;
    /**
     * Colour of the border of 'on' toggle fields
     */
    private Color borders = new Color(26, 97, 110, 50);
    /**
     * Blocks the JComboBox action event from being fired. This allows the selection
     * to be set programmatically.
     */
    private boolean blockComboActionEvent = false;
    /**
     * Popup menu which shows the colour chooser.
     */
    JPopupMenu colourPopup;
    /**
     * How the setUpGUI function should layout the toolbar.
     */
    int layout;

    /**
     * Creates a new RichTextToolbar instance.
     * @param layout either horizontal or vertical; use one of the two values held by this class.
     */
    public RichTextToolbar(int layout) {
        if ((layout < 0) || (layout > 1)) {
            layout = 0;
        } else {
            this.layout = layout;
        }
        setUpGUI();
        toolbarListeners = new RichTextToolbarListener[0];
        colourChooser = new ColourChooserPane("resources/colour_choose_small.png", 6);
        colourPopup = new JPopupMenu();
        colourPopup.insert(colourChooser, 0);
    }

    /**
     * Adds a <code>RichTextToolbarListener</code> to this <code>RichTextToolbar</code> instance.
     * @param listener <code>the RichTextToolbarListener</code> to add.
     */
    public void addRichTextToolbarListener(RichTextToolbarListener listener) {
        if (toolbarListeners.length == 0) {
            toolbarListeners = new RichTextToolbarListener[]{listener};
        } else {
            RichTextToolbarListener[] temp = new RichTextToolbarListener[toolbarListeners.length + 1];
            System.arraycopy(toolbarListeners, 0, temp, 0, toolbarListeners.length);
            temp[toolbarListeners.length] = listener;
            toolbarListeners = temp;
        }
    }

    /**
     * Removes a <code>RichTextToolbarListener</code> from this <code>RichTextToolbar</code> instance.
     * @param listener the <code>RichTextToolbarListener</code> to remove.
     * @return true if the listener could be found and removed, and false otherwise.
     */
    public boolean removeRichTextToolbarListener(RichTextToolbarListener listener) {
        if (toolbarListeners.length == 0) {
            return false;
        }
        if (toolbarListeners.length == 1) {
            if (toolbarListeners[0].equals(listener)) {
                toolbarListeners = new RichTextToolbarListener[0];
                return true;
            } else {
                return false;
            }
        }

        int index = -1;
        //get the index
        for (int i = 0; i < toolbarListeners.length; i++) {
            if (toolbarListeners[i].equals(listener)) {
                index = i;
                break;
            }
        }

        //if index is -1, we have not found the listener
        if (index == -1) {
            return false;
        }

        //otherwise, get rid of the listener
        RichTextToolbarListener[] temp = new RichTextToolbarListener[toolbarListeners.length - 1];
        if (index == 0) {
            System.arraycopy(toolbarListeners, 1, temp, 0, toolbarListeners.length - 1);
            toolbarListeners = temp;
            return true;
        } else if (index == toolbarListeners.length - 1) {
            System.arraycopy(toolbarListeners, 0, temp, 0, toolbarListeners.length - 1);
            toolbarListeners = temp;
            return true;
        } else //the index is not on the edge of the array
        {
            System.arraycopy(toolbarListeners, 0, temp, 0, index);
            System.arraycopy(toolbarListeners, index + 1, temp, index, toolbarListeners.length - index - 1);
            toolbarListeners = temp;
            return true;
        }
    }

    /**
     * Set whether or not the insert picture button should be shown.
     * @param use whether or not the insert picture button should be shown.
     */
    public void setUseInsertPictureButton(boolean use) {
        btnInsertPicture.setVisible(use);
    }

    /**
     * Set whether or not the text colour chooser button should be shown.
     * @param use whether or not the text colour chooser button should be shown.
     */
    public void setUseTextColourButton(boolean use) {
        btnTextColour.setVisible(use);
    }

    public void addColourChooserPaneListener(ColourChooserPaneListener listener) {
        colourChooser.addColourChooserPaneListener(listener);
    }

    public boolean removeColourChooserPaneListener(ColourChooserPaneListener listener) {
        return colourChooser.removeColourChooserPaneListener(listener);
    }

    /**
     * Gets an array of all <code>RichTextToolbarListener</code>s that have been added to this <code>RichTextToolbar</code>.
     * @return an array of all <code>RichTextToolbarListener</code>s that have been added to this <code>RichTextToolbar</code>.
     */
    public RichTextToolbarListener[] getRichTextToolbarListeners() {
        return toolbarListeners;
    }

    /**
     * do all GUI set up work, including instantiations.
     */
    private void setUpGUI() {
        if (this.layout == RichTextToolbar.HORIZONTAL_LAYOUT) {
            this.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));
            this.setOpaque(false);
            this.setSize(330, 24);

            btnBold = createButton("/resources/icons/text_bold.png", new BoldAction(), this);
            btnBold.setToolTipText("Bold (Ctrl+B)");
            btnBold.setFocusable(false);
            btnItalic = createButton("/resources/icons/text_italic.png", new ItalicAction(), this);
            btnItalic.setToolTipText("Italic (Ctrl+I)");
            btnItalic.setFocusable(false);
            btnUnderline = createButton("/resources/icons/text_underline.png", new UnderlineAction(), this);
            btnUnderline.setToolTipText("Underline (Ctrl+U)");
            btnUnderline.setFocusable(false);
            btnSuperscript = createButton("/resources/icons/text_superscript.png", new SuperscriptAction(), this);
            btnSuperscript.setToolTipText("Superscript (Ctrl+up)");
            btnSuperscript.setFocusable(false);
            btnSubscript = createButton("/resources/icons/text_subscript.png", new SubscriptAction(), this);
            btnSubscript.setToolTipText("Subscript (Ctrl+down)");
            btnSubscript.setFocusable(false);
            btnAlignLeft = createButton("/resources/icons/text_align_left.png", new AlignLeftAction(), this);
            btnAlignLeft.setToolTipText("Align text left (Ctrl+L)");
            btnAlignLeft.setFocusable(false);
            btnAlignCentre = createButton("/resources/icons/text_align_center.png", new AlignCentreAction(), this);
            btnAlignCentre.setToolTipText("Align text centre (Ctrl+E)");
            btnAlignCentre.setFocusable(false);
            btnAlignRight = createButton("/resources/icons/text_align_right.png", new AlignRightAction(), this);
            btnAlignRight.setToolTipText("Align text right (Ctrl+R)");
            btnAlignRight.setFocusable(false);
            btnInsertPicture = createButton("/resources/icons/image_add.png", new InsertPictureAction(), this);
            btnInsertPicture.setToolTipText("Insert an image");
            btnInsertPicture.setFocusable(false);
            btnTextColour = createButton("/resources/icons/color_swatch.png", new TextColourAction(), this);
            btnTextColour.setToolTipText("Change text colour");
            btnTextColour.setFocusable(false);
            comboFonts = createCombo(GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames(), this);
            comboFonts.setPreferredSize(new Dimension(70, 22));
            comboFonts.setFocusable(false);
            comboSizes = createCombo(new String[]{"8", "10", "12", "14", "16", "18", "20", "22", "24", "28", "32", "36", "40", "48", "52"}, this);
            comboSizes.setEditable(true);
            comboSizes.setFocusable(false);
            ((JTextField) comboSizes.getEditor().getEditorComponent()).setDocument(new NumericTextDocument());
            blockComboActionEvent = true;
            comboSizes.setSelectedItem("10");
            blockComboActionEvent = false;
            comboSizes.setPreferredSize(new Dimension(30, 22));
        }
        else if (this.layout == RichTextToolbar.VERTICAL_LAYOUT)
        {
            this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
            this.setOpaque(false);
            this.setSize(330, 24);

            Box boxBIU = Box.createHorizontalBox();
            Box boxAlign = Box.createHorizontalBox();
            Box boxFont = Box.createHorizontalBox();

            btnBold = createButton("/resources/icons/text_bold.png", new BoldAction(), boxBIU);
            btnBold.setToolTipText("Bold");
            btnItalic = createButton("/resources/icons/text_italic.png", new ItalicAction(), boxBIU);
            btnItalic.setToolTipText("Italic");
            btnUnderline = createButton("/resources/icons/text_underline.png", new UnderlineAction(), boxBIU);
            btnUnderline.setToolTipText("Underline");
            btnSuperscript = createButton("/resources/icons/text_superscript.png", new SuperscriptAction(), boxBIU);
            btnSuperscript.setToolTipText("Superscript");
            btnSubscript = createButton("/resources/icons/text_subscript.png", new SubscriptAction(), boxBIU);
            btnSubscript.setToolTipText("Subscript");
            boxBIU.add(Box.createHorizontalGlue());

            this.add(boxBIU);

            btnAlignLeft = createButton("/resources/icons/text_align_left.png", new AlignLeftAction(), boxAlign);
            btnAlignLeft.setToolTipText("Align text left");
            btnAlignCentre = createButton("/resources/icons/text_align_center.png", new AlignCentreAction(), boxAlign);
            btnAlignCentre.setToolTipText("Align text centre");
            btnAlignRight = createButton("/resources/icons/text_align_right.png", new AlignRightAction(), boxAlign);
            btnAlignRight.setToolTipText("Align text right");
            btnInsertPicture = createButton("/resources/icons/image_add.png", new InsertPictureAction(), boxAlign);
            btnInsertPicture.setToolTipText("Insert an image");
            btnTextColour = createButton("/resources/icons/color_swatch.png", new TextColourAction(), boxAlign);
            btnTextColour.setToolTipText("Change text colour");
            boxAlign.add(Box.createHorizontalGlue());

            this.add(boxAlign);

            this.add(Box.createVerticalStrut(5));

            comboFonts = createCombo(GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames(),boxFont);
            comboFonts.setPreferredSize(new Dimension(70, 22));
            comboSizes = createCombo(new String[]{"8", "10", "12", "14", "16", "18", "20", "22", "24", "28", "32", "36", "40", "48", "52"}, boxFont);

            this.add(boxFont);
            boxFont.setMaximumSize(new Dimension(300,20));
            this.add(Box.createVerticalStrut(100));

            comboSizes.setEditable(true);
            ((JTextField) comboSizes.getEditor().getEditorComponent()).setDocument(new NumericTextDocument());
            blockComboActionEvent = true;
            comboSizes.setSelectedItem("10");
            blockComboActionEvent = false;
            comboSizes.setPreferredSize(new Dimension(30, 22));
        }
    }

    /**
     * Gets the currently selected font family name.
     * @return the currently selected font family name.
     */
    public String getSelectedFontFamily() {
        return (String) comboFonts.getSelectedItem();
    }

    /**
     * Gets the currently selected font size.
     * @return the currently selected font size.
     */
    public int getSelectedFontSize() {
        return Integer.valueOf((String) comboSizes.getSelectedItem());
    }

    /**
     * Instantiates a JButton and sets required properties. Loads the specified resource
     * as the button's icon.
     *
     * @param resourceName the image resource used as this button's icon.
     * @return the created JButton.
     */
    private JButton createButton(String resourceName, AbstractAction action, JComponent addTo) {
        JButton btn = new JButton();
        btn.setAction(action);
        btn.setIcon(new ImageIcon(RichTextToolbar.class.getResource(resourceName)));
        btn.setBorder(BorderFactory.createEmptyBorder(3, 3, 3, 3));
        btn.setSize(btn.getIcon().getIconWidth(), btn.getIcon().getIconHeight());
        addTo.add(btn);
        return btn;
    }

    /**
     * Hides the colour frame.
     */
    public void hideColourChooser() {
        colourPopup.setVisible(false);
    }

    /**
     * Used to set indicators that particular styles are active within the context that the toolbar exists. Commonly
     * used to show the current formatting of selected text, or the text around the current caret location.
     * @param bold should bold be selected
     * @param italic should italic be selected
     * @param underline should underline be selected
     * @param superscript should superscript be selected
     * @param subscript should subscript be selected
     * @param alignment which alignment should be selected
     * @param font contains the family name and size to display
     */
    public void setButtonToggles(boolean bold, boolean italic, boolean underline, boolean superscript, boolean subscript, int alignment, Font font) {
        Border border = BorderFactory.createLineBorder(borders, 3);
        Border emptyBorder = BorderFactory.createEmptyBorder(3, 3, 3, 3);

        if (bold) {
            btnBold.setBorder(border);
        } else {
            btnBold.setBorder(emptyBorder);
        }

        if (italic) {
            btnItalic.setBorder(border);
        } else {
            btnItalic.setBorder(emptyBorder);
        }

        if (underline) {
            btnUnderline.setBorder(border);
        } else {
            btnUnderline.setBorder(emptyBorder);
        }

        if (superscript) {
            btnSuperscript.setBorder(border);
        } else {
            btnSuperscript.setBorder(emptyBorder);
        }

        if (subscript) {
            btnSubscript.setBorder(border);
        } else {
            btnSubscript.setBorder(emptyBorder);
        }

        if (alignment == StyleConstants.ALIGN_LEFT) {
            btnAlignLeft.setBorder(border);
            btnAlignCentre.setBorder(emptyBorder);
            btnAlignRight.setBorder(emptyBorder);
        } else if (alignment == StyleConstants.ALIGN_CENTER) {
            btnAlignLeft.setBorder(emptyBorder);
            btnAlignCentre.setBorder(border);
            btnAlignRight.setBorder(emptyBorder);
        } else if (alignment == StyleConstants.ALIGN_RIGHT) {
            btnAlignLeft.setBorder(emptyBorder);
            btnAlignCentre.setBorder(emptyBorder);
            btnAlignRight.setBorder(border);
        }

        blockComboActionEvent = true;
        comboFonts.setSelectedItem(font.getFamily());
        comboSizes.setSelectedItem("" + font.getSize());
        blockComboActionEvent = false;

    }

    /**
     * Gets the currently selected colour of the ColourChooserPane instance associated with this RichTextToolbar.
     * @return the currently selected colour of the ColourChooserPane instance associated with this RichTextToolbar.
     */
    public Color getSelectedColour() {
        return colourChooser.getCurrentColour();
    }

    /**
     * Instantiates a JComboBox and sets its UI to a custom one created so that
     * the scrollbars would be thinner. This method also makes the down arrow buttons
     * thinner.
     * @param listItems list of items to add to the combo box.
     * @return the created JComboBox instance.
     */
    private JComboBox createCombo(String[] listItems, JComponent addTo) {
        JComboBox combo = new JComboBox(listItems);
        combo.setUI(new ThinScrollComboBoxUI());
        combo.setFont(new Font(this.getFont().getFamily(), Font.PLAIN, 9));
        Component[] c = combo.getComponents();
        combo.addActionListener(new ComboActionListener());

        //make the button thinner
        for (int i = 0; i < c.length; i++) {
            if (c[i] instanceof JButton) {
                JButton b = ((JButton) c[i]);
                b.setPreferredSize(new Dimension(6, b.getSize().height));
                b.setBorder(BorderFactory.createLineBorder(new Color(26, 97, 110, 100)));
            }
        }
        addTo.add(combo);
        return combo;
    }

    /**
     * Inform all <code>RichTextToolbarListener</code>s that the bold button
     * has been pressed.
     */
    private class BoldAction extends AbstractAction {

        public void actionPerformed(ActionEvent e) {
            if (toolbarListeners.length != 0) {
                for (int i = 0; i < toolbarListeners.length; i++) {
                    toolbarListeners[i].buttonPressed(new RichTextToolbarEvent(RichTextToolbar.this, RichTextToolbarEvent.BOLD_BUTTON));
                }
            }
        }
    }

    /**
     * Inform all <code>RichTextToolbarListener</code>s that the italic button
     * has been pressed.
     */
    private class ItalicAction extends AbstractAction {

        public void actionPerformed(ActionEvent e) {
            if (toolbarListeners.length != 0) {
                for (int i = 0; i < toolbarListeners.length; i++) {
                    toolbarListeners[i].buttonPressed(new RichTextToolbarEvent(RichTextToolbar.this, RichTextToolbarEvent.ITALIC_BUTTON));
                }
            }
        }
    }

    /**
     * Inform all <code>RichTextToolbarListener</code>s that the underline button
     * has been pressed.
     */
    private class UnderlineAction extends AbstractAction {

        public void actionPerformed(ActionEvent e) {
            if (toolbarListeners.length != 0) {
                for (int i = 0; i < toolbarListeners.length; i++) {
                    toolbarListeners[i].buttonPressed(new RichTextToolbarEvent(RichTextToolbar.this, RichTextToolbarEvent.UNDERNLINE_BUTTON));
                }
            }
        }
    }

    /**
     * Inform all <code>RichTextToolbarListener</code>s that the superscript button
     * has been pressed.
     */
    private class SuperscriptAction extends AbstractAction {

        public void actionPerformed(ActionEvent e) {
            if (toolbarListeners.length != 0) {
                for (int i = 0; i < toolbarListeners.length; i++) {
                    toolbarListeners[i].buttonPressed(new RichTextToolbarEvent(RichTextToolbar.this, RichTextToolbarEvent.SUPERSCRIPT_BUTTON));
                }
            }
        }
    }

    /**
     * Inform all <code>RichTextToolbarListener</code>s that the subscript button
     * has been pressed.
     */
    private class SubscriptAction extends AbstractAction {

        public void actionPerformed(ActionEvent e) {
            if (toolbarListeners.length != 0) {
                for (int i = 0; i < toolbarListeners.length; i++) {
                    toolbarListeners[i].buttonPressed(new RichTextToolbarEvent(RichTextToolbar.this, RichTextToolbarEvent.SUBSCRIPT_BUTTON));
                }
            }
        }
    }

    /**
     * Inform all <code>RichTextToolbarListener</code>s that the align left button
     * has been pressed.
     */
    private class AlignLeftAction extends AbstractAction {

        public void actionPerformed(ActionEvent e) {
            if (toolbarListeners.length != 0) {
                for (int i = 0; i < toolbarListeners.length; i++) {
                    toolbarListeners[i].buttonPressed(new RichTextToolbarEvent(RichTextToolbar.this, RichTextToolbarEvent.JUSTIFY_LEFT));
                }
            }
        }
    }

    /**
     * Inform all <code>RichTextToolbarListener</code>s that the align centre button
     * has been pressed.
     */
    private class AlignCentreAction extends AbstractAction {

        public void actionPerformed(ActionEvent e) {
            if (toolbarListeners.length != 0) {
                for (int i = 0; i < toolbarListeners.length; i++) {
                    toolbarListeners[i].buttonPressed(new RichTextToolbarEvent(RichTextToolbar.this, RichTextToolbarEvent.JUSTIFY_CENTRE));
                }
            }
        }
    }

    /**
     * Inform all <code>RichTextToolbarListener</code>s that the align right button
     * has been pressed.
     */
    private class AlignRightAction extends AbstractAction {

        public void actionPerformed(ActionEvent e) {
            if (toolbarListeners.length != 0) {
                for (int i = 0; i < toolbarListeners.length; i++) {
                    toolbarListeners[i].buttonPressed(new RichTextToolbarEvent(RichTextToolbar.this, RichTextToolbarEvent.JUSTIFY_RIGHT));
                }
            }
        }
    }

    /**
     * Inform all <code>RichTextToolbarListener</code>s that the insert picture button
     * has been pressed.
     */
    private class InsertPictureAction extends AbstractAction {

        public void actionPerformed(ActionEvent e) {
            if (toolbarListeners.length != 0) {
                for (int i = 0; i < toolbarListeners.length; i++) {
                    toolbarListeners[i].buttonPressed(new RichTextToolbarEvent(RichTextToolbar.this, RichTextToolbarEvent.INSERT_PICTURE));
                }
            }
        }
    }

    /**
     * shows the colour chooser frame, and informs all <code>RichTextToolbarListener</code>s that the font colour button
     * has been pressed.
     */
    private class TextColourAction extends AbstractAction {

        public void actionPerformed(ActionEvent e) {
            colourPopup.show(RichTextToolbar.this, btnTextColour.getX(), btnTextColour.getY());
            if (toolbarListeners.length != 0) {
                for (int i = 0; i < toolbarListeners.length; i++) {
                    toolbarListeners[i].buttonPressed(new RichTextToolbarEvent(RichTextToolbar.this, RichTextToolbarEvent.FONT_COLOUR_BUTTON));
                }
            }
        }
    }

    /**
     * Listens for any selection actions on the combo boxes so that the 'font change' events
     * will be fired.
     */
    private class ComboActionListener implements ActionListener {

        public void actionPerformed(ActionEvent e) {
            if (blockComboActionEvent) {
                return;
            }
            int eventID = -1;
            if (e.getSource().equals(comboSizes)) {
                eventID = RichTextToolbarEvent.FONT_SIZE_CHANGED;
            } else if (e.getSource().equals(comboFonts)) {
                eventID = RichTextToolbarEvent.FONT_FAMILY_CHANGED;
            }

            if (toolbarListeners.length != 0) {
                for (int i = 0; i < toolbarListeners.length; i++) {
                    toolbarListeners[i].fontChanged(new RichTextToolbarEvent(RichTextToolbar.this, eventID));
                }
            }
        }
    }

    public class NumericTextDocument extends PlainDocument {

        @Override
        public void insertString(int offs, String str, AttributeSet a) throws BadLocationException {
            if (str == null) {
                return;
            }
            String oldString = getText(0, getLength());
            String newString = oldString.substring(0, offs) + str
                    + oldString.substring(offs);
            if (newString.contains("-")) {
                return;
            }
            try {
                Integer.parseInt(newString + "0");
                super.insertString(offs, str, a);
            } catch (NumberFormatException e) {
            }
        }
    }
}
