/*
 * SimpleTextArea.java
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

import java.awt.Point;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import javax.swing.JTextPane;
import javax.swing.KeyStroke;

/**
 * This class is a simple extension of the JTextArea component, but with the
 * addition of a SymbolMenu. All of the event handling is taken care of for the
 * symbol menu. You are able to set the activation key, which is the key that
 * indicates the next character will become the base character for what the symbol menu
 * will display.
 * 
 * When the symbol menu is visible, the left and right keys allow the user to select a character.
 * The enter key will insert that character, and the escape key will close the menu without inserting
 * a special character, but leaving the base character.
 *
 * Additionally, the back space key will move the selection to the left, and any other key will move
 * the selection to the right.
 *
 * It is suggested that the activation key is something like Control+space, as this is a natural
 * gesture for the hands, and is psychologically removed from the value of any character, reducing confusion.
 *
 * If the menu loses focus at any point while being displayed, it will hide itself
 * with no insertion being made.
 *
 * @author Thomas Everingham
 * @version 1.0
 */
public class SimpleTextArea extends JTextPane {

    /**
     * The symbol menu used by this text field.
     */
    private SymbolMenu symbolMenu;
    /**
     * This is the key that is pressed to activate the symbol menu. Once the symbol
     * menu is activated, the next keystrok will display the symbol menu and all
     * corresponding special characters. Default is F1.
     */
    private KeyStroke symbolMenuActivateKey = KeyStroke.getKeyStroke(KeyEvent.VK_SPACE, KeyEvent.CTRL_DOWN_MASK);
    /**
     * Value set to true when the activating keystroke has been heard. The next keystroke
     * will determine the base character for the symbol menu.
     */
    private boolean menuActivated = false;

    public SimpleTextArea() {
        setUp();
    }

    public SimpleTextArea(String text) {
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
    }

    /**
     * KeyListener for the SimpleTextField.
     */
    public class TextKeyListener implements KeyListener {

        public void keyTyped(KeyEvent e) {
        }

        public void keyPressed(KeyEvent e) {
            if ((e.getKeyCode() == symbolMenuActivateKey.getKeyCode()) && ((e.getModifiersEx() & symbolMenuActivateKey.getModifiers()) == InputEvent.CTRL_DOWN_MASK)) {
                menuActivated = true;
            } else if (menuActivated) {
                //shift will deactivate the symbol menu otherwise, meaning that capital letters can't be accessed
                if (e.getKeyCode() == KeyEvent.VK_SHIFT) return;
                menuActivated = false;
                if (symbolMenu.charHasSpecialChars(e.getKeyChar())) //if the baseCharacter is valid...
                {
                    //then set the base character,
                    symbolMenu.setBaseCharacter(e.getKeyChar());
                    //and position the symbol menu logically at the caret
                    Point p = SimpleTextArea.this.getCaret().getMagicCaretPosition();
                    if (p == null) {
                        p = new Point(2, SimpleTextArea.this.getHeight() / 2 - 8);
                    }
                    if (symbolMenu.getStringWidth() > (SimpleTextArea.this.getWidth() - p.x)) {
                        symbolMenu.setLocation(SimpleTextArea.this.getWidth() - (symbolMenu.getStringWidth() + 5), p.y - 1);
                    } else {
                        symbolMenu.setLocation(p.x + 1, p.y - 1);
                    }

                    symbolMenu.setVisible(true);
                    symbolMenu.requestFocus();
                    symbolMenu.repaint();
                }
            }
        }

        public void keyReleased(KeyEvent e) {
        }
    }

    /**
     * KeyListener for the SymbolMenu.
     */
    public class MenuKeyListener implements KeyListener {

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
                SimpleTextArea.this.select(SimpleTextArea.this.getCaretPosition()-1, SimpleTextArea.this.getCaretPosition());
                SimpleTextArea.this.replaceSelection("" + symbolMenu.getSelectedCharacter());
                symbolMenu.setVisible(false);
                SimpleTextArea.this.requestFocus();
            } else if (e.getKeyCode() == 27) { //escape key
                symbolMenu.setVisible(false);
                SimpleTextArea.this.requestFocus();
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
