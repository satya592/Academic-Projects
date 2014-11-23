/*
 * SimpleTextField.java
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

import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import javax.swing.JTextField;
import javax.swing.KeyStroke;

/**
 * An extension of JTextField, but with a SymbolMenu. Takes care of all SymbolMenu
 * handling.
 *
 * @author Thomas Everingham
 * @version 1.0
 */
public class SimpleTextField extends JTextField {

    /**
     * The symbol menu used by this text field.
     */
    private SymbolMenu symbolMenu;
    /**
     * This is the key that is pressed to activate the symbol menu. Once the symbol
     * menu is activated, the next keystrok will display the symbol menu and all
     * corresponding special characters. Default is Ctrl+Space.
     */
    private KeyStroke symbolMenuActivateKey = KeyStroke.getKeyStroke(KeyEvent.VK_SPACE, KeyEvent.CTRL_DOWN_MASK);
    /**
     * Value set to true when the activating keystroke has been heard. The next keystroke
     * will determine the base character for the symbol menu.
     */
    private boolean menuActivated = false;
    /**
     * The size of the <code>SimpleTextField</code> before the symbol menu was shown. The field will
     * be restored to this size once the <code>SymbolMenu</code> has been hidden.
     */
    private Dimension previousSize = null;
    /**
     * The minimum size of the <code>SimpleTextField</code> before the symbol menu was shown. The field will
     * be restored to this size once the <code>SymbolMenu</code> has been hidden.
     */
    private Dimension previousMinimumSize = null;
    /**
     * The preferred size of the <code>SimpleTextField</code> before the symbol menu was shown. The field will
     * be restored to this size once the <code>SymbolMenu</code> has been hidden.
     */
    private Dimension previousPreferredSize = null;
    /**
     * The maximum size of the <code>SimpleTextField</code> before the symbol menu was shown. The field will
     * be restored to this size once the <code>SymbolMenu</code> has been hidden.
     */
    private Dimension previousMaximumSize = null;

    public SimpleTextField() {
        this("");
    }

    public SimpleTextField(int columns) {
        super(columns);
        setUp();
    }

    public SimpleTextField(String text) {
        this.setText(text);
        setUp();
    }

    /**
     * Get the current key associated with telling the menu that the next character to
     * be typed is the new base character. Supports modifiers.
     *
     * @return the current menu activate key.
     */
    public KeyStroke getSymbolMenuActivateKey() {
        return symbolMenuActivateKey;
    }

    /**
     * Get the SymbolMenu instance associated with this component. This allows you
     * to set the colours of the component.
     *
     * @return the SymbolMenu instance associated with this component.
     */
    public SymbolMenu getSymbolMenu() {
        return symbolMenu;
    }

    /**
     * Sets the menu activate key. This is the key that tells the SymbolMenu instance that
     * the next key to be pressed is the new base character. Supports modifiers.
     *
     * @param symbolMenuActivateKey the new menu activate key.
     */
    public void setSymbolMenuActivateKey(KeyStroke symbolMenuActivateKey) {
        this.symbolMenuActivateKey = symbolMenuActivateKey;
    }

    /**
     * Setup code common to both constructors.
     */
    private void setUp() {
        symbolMenu = new SymbolMenu();
        this.add(symbolMenu);
        symbolMenu.setVisible(false);
        this.addKeyListener(new TextKeyListener());
        symbolMenu.addKeyListener(new MenuKeyListener());
        symbolMenu.addFocusListener(new SymbolMenuListener());
    }

    /**
     * KeyListener for the SimpleTextField.
     */
    private class TextKeyListener implements KeyListener {

        public void keyTyped(KeyEvent e) {
        }

        public void keyPressed(KeyEvent e) {
            if ((e.getKeyCode() == symbolMenuActivateKey.getKeyCode()) && ((e.getModifiersEx() & symbolMenuActivateKey.getModifiers()) == InputEvent.CTRL_DOWN_MASK)) {
                menuActivated = true;
            } else if (menuActivated) {

                //shift will deactivate the symbol menu otherwise, meaning that capital letters can't be accessed
                if (e.getKeyCode() == KeyEvent.VK_SHIFT) {
                    return;
                }

                menuActivated = false;
                if (symbolMenu.charHasSpecialChars(e.getKeyChar())) //if the baseCharacter is valid...
                {
                    //then set the base character,
                    symbolMenu.setBaseCharacter(e.getKeyChar());
                    //and position the symbol menu logically at the caret
                    Point p = SimpleTextField.this.getCaret().getMagicCaretPosition();

                    if (p == null) {
                        p = new Point(2, SimpleTextField.this.getHeight() / 2 - 8);
                    }

                    symbolMenu.setVisible(true);

                    if (SimpleTextField.this.getWidth() < (symbolMenu.getWidth() + 20)) {
                        setTempSize(new Dimension(symbolMenu.getWidth()+p.x+10, (int) SimpleTextField.this.getHeight()));
                    }

                    if (symbolMenu.getStringWidth() > (SimpleTextField.this.getWidth() - p.x)) {
                        int proposedXLocation = SimpleTextField.this.getWidth() - (symbolMenu.getStringWidth() + 5);
                        if (proposedXLocation < 0) {
                            proposedXLocation = 0;
                        }
                        symbolMenu.setLocation(proposedXLocation, p.y - 1);
                    } else {
                        symbolMenu.setLocation(p.x + 1, p.y - 1);
                    }

                    SimpleTextField.this.getRootPane().validate();
                    
                    symbolMenu.requestFocus();
                    symbolMenu.repaint();
                    SimpleTextField.this.repaint();
                }
            }
        }

        public void keyReleased(KeyEvent e) {
        }
    }

    private void setTempSize(Dimension newSize) {
        previousSize = this.getSize();
        previousMaximumSize = this.getMaximumSize();
        previousMinimumSize = this.getMinimumSize();
        previousPreferredSize = this.getPreferredSize();

        this.setSize(newSize);
        this.setMaximumSize(newSize);
        this.setMinimumSize(newSize);
        this.setPreferredSize(newSize);
    }

    private void revertSize() {

        if ((previousSize == null) || (previousMaximumSize == null) || (previousMinimumSize == null) || (previousPreferredSize == null)) {
            return;
        }

        this.setSize(previousSize);
        this.setMaximumSize(previousMaximumSize);
        this.setMinimumSize(previousMinimumSize);
        this.setPreferredSize(previousPreferredSize);
    }

    /**
     * Listener for the symbol menu. If it loses focus, it has been hidden and we need to restore this
     * <code>SimpleTextField</code> to its original size.
     */
    private class SymbolMenuListener implements FocusListener {

        public void focusGained(FocusEvent e) {
        }

        public void focusLost(FocusEvent e) {
            revertSize();
        }
    }

    /**
     * KeyListener for the SymbolMenu.
     */
    private class MenuKeyListener implements KeyListener {

        public void keyTyped(KeyEvent e) {
        }

        public void keyPressed(KeyEvent e) {
            if (e.getKeyCode() == 35) //end
            {
                symbolMenu.moveSelectionEnd();
            } else if (e.getKeyCode() == 36) //home
            {
                symbolMenu.moveSelectionStart();
            } else if (e.getKeyCode() == 37) //left
            {
                symbolMenu.moveSelectionLeft();
            } else if (e.getKeyCode() == 39) //right
            {
                symbolMenu.moveSelectionRight();
            } else if (e.getKeyCode() == 10) { //enter key
                SimpleTextField.this.select(SimpleTextField.this.getCaretPosition() - 1, SimpleTextField.this.getCaretPosition());
                SimpleTextField.this.replaceSelection("" + symbolMenu.getSelectedCharacter());
                symbolMenu.setVisible(false);
                revertSize();
                SimpleTextField.this.requestFocus();
            } else if (e.getKeyCode() == 27) { //escape key
                symbolMenu.setVisible(false);
                SimpleTextField.this.requestFocus();
            } else if (e.getKeyCode() == 8) {
                symbolMenu.moveSelectionLeft();
            } else {
                symbolMenu.moveSelectionRight();
            }
            symbolMenu.repaint();
        }

        public void keyReleased(KeyEvent e) {
        }
    }
}
