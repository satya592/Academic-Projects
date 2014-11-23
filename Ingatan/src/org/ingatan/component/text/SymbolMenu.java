/*
 * SymbolMenu.java
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

import org.ingatan.io.IOManager;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.geom.Area;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;
import java.util.HashMap;
import javax.swing.JPanel;

/**
 * Provides a symbol menu which may be used as an inline menu for JTextComponents.
 * Given a base letter, a list of special characters is shown. A character may be
 * selected by using the left and right keys and pressing enter, or clicking
 * one of the characters.
 *
 * The menu will hide itself if it loses focus.
 *
 * Scrolling may be added at a later date so that the menu can be narrower/resized and
 * still usable.
 *
 * @author Thomas Everingham
 * @version 1.0
 */
public class SymbolMenu extends JPanel {

    /**
     * Stores the character arrays (String) corresponding to each latin character.
     */
    private HashMap characterMap = new HashMap<String, String>();
    /**
     * The index that is currently selected of the character array.
     */
    private int currentIndex = 0;
    /**
     * The base character which has most recently been set
     */
    private String currentBaseCharacter = "";

    /**
     * Colour of the border of the menu, as well as the border of the selction.
     */
    private Color borders = new Color(26, 97, 110);
    /**
     * Colour of the selection fill; this must have an associated transparency.
     */
    private Color selectionFill = new Color(220, 97, 110, 50);
    /**
     * Colour of the background of the menu.
     */
    private Color background = new Color(222, 233, 233);

    public SymbolMenu() {
        setUp();
    }

    public SymbolMenu(Color background, Color borders, Color selectionFill) {
        setUp();
        this.background = background;
        this.selectionFill = selectionFill;
        this.borders = borders;
    }

    private void setUp()
    {
        //set up the characterMap
        characterMap = IOManager.getSymbolMenuCharacterMap();
//        characterMap.put("a", "äåàáãâæāăą");
//        characterMap.put("c", "çćĉċč");
//        characterMap.put("d", "ďđ");
//        characterMap.put("e", "èéêëęēěĕɛ");
//        characterMap.put("f", "ƒ");
//        characterMap.put("g", "ĝğġģɠ");
//        characterMap.put("h", "ĥħɦɧ");
//        characterMap.put("i", "ìíîïĩīĭį");
//        characterMap.put("j", "ĵ");
//        characterMap.put("k", "ĸķ");
//        characterMap.put("l", "ĺļľŀł");
//        characterMap.put("m", "ɯɰɱ");
//        characterMap.put("n", "ñńņňŉŋ");
//        characterMap.put("o", "öøœòóőôõōŏǿ");
//        characterMap.put("r", "ŕŗř");
//        characterMap.put("s", "ʂșśŝşš");
//        characterMap.put("t", "ţťŧțʈʇ");
//        characterMap.put("u", "ùúűûüũūŭůų");
//        characterMap.put("w", "ŵʍ");
//        characterMap.put("y", "ŷźżžʏ");
//        characterMap.put("z", "ʐʑʓʒ");
//        characterMap.put("A", "äåàáãâæāăą".toUpperCase());
//        characterMap.put("C", "çćĉċč".toUpperCase());
//        characterMap.put("D", "ďđ".toUpperCase());
//        characterMap.put("E", "èéêëęēěĕɛ".toUpperCase());
//        characterMap.put("F", "ƒ".toUpperCase());
//        characterMap.put("G", "ĝğġģɠ".toUpperCase());
//        characterMap.put("H", "ĥħɦɧ".toUpperCase());
//        characterMap.put("I", "ìíîïĩīĭį".toUpperCase());
//        characterMap.put("J", "ĵ".toUpperCase());
//        characterMap.put("K", "ĸķ".toUpperCase());
//        characterMap.put("L", "ĺļľŀł".toUpperCase());
//        characterMap.put("M", "ɯɰɱ".toUpperCase());
//        characterMap.put("N", "ñńņňŉŋ".toUpperCase());
//        characterMap.put("O", "öøœòóőôõōŏǿ".toUpperCase());
//        characterMap.put("R", "ŕŗř".toUpperCase());
//        characterMap.put("S", "ʂșśŝşš".toUpperCase());
//        characterMap.put("T", "ţťŧțʈʇ".toUpperCase());
//        characterMap.put("U", "ùúűûüũūŭůų".toUpperCase());
//        characterMap.put("W", "ŵʍ".toUpperCase());
//        characterMap.put("Y", "ŷźżžʏ".toUpperCase());
//        characterMap.put("Z", "ʐʑʓʒ".toUpperCase());
//        characterMap.put("0", "asdsad");
//        characterMap.put("1", "παλεθβγδ");
//        characterMap.put("2", "κημιζνξο");
//        characterMap.put("3", "ρςστυφχψω");
//        characterMap.put("4", " ");
//        characterMap.put("5", " ");
//        characterMap.put("6", " ");
//        characterMap.put("7", " ");
//        characterMap.put("8", " ");
//        characterMap.put("9", " ");
//        characterMap.put("", " ");

        this.setSize(100, 20);
        this.setOpaque(false);
        this.addFocusListener(new MenuFocusListener());
    }
    /**
     * Returns the character map used by this component. The character map is
     * preset for this class and is generated in the constructor method.
     */
    public HashMap getCharacterMap() {
        return characterMap;
    }

    /**
     * Returns the array of special characters associated with a specified base
     * character. For example, for base character 'a', 'àáâãäåæ' is returned.
     *
     * If there are no special characters associated with the specified base character,
     * a String containing only that baseCharacter is returned. The base character will
     * never otherwise be included in the returned String.
     *
     * @param baseCharacter the character upon which the returned string of special characters is based
     * @return a string of special characters associated with the specified base character
     */
    public String getSpecialCharacters(char baseCharacter) {
        if (characterMap.containsKey(String.valueOf(baseCharacter))) {
            return (String) characterMap.get(String.valueOf(baseCharacter));
        } else {
            return String.valueOf(String.valueOf(baseCharacter));
        }
    }

    /**
     * Sets the base character for this symbol menu so that it displays the
     * corresponding special charactes.
     *
     * If no special characters are associated with the specified baseCharacter,
     * the baseCharacter is set as an empty String, and if the SymbolMenu is rendered,
     * it will show an empty menu.
     * 
     * @param baseCharacter the base character to set the menu to.
     */
    public void setBaseCharacter(char baseCharacter) {
        currentIndex = 0;
        if (characterMap.containsKey(String.valueOf(baseCharacter))) {
            this.currentBaseCharacter = String.valueOf(baseCharacter);
        } else {
            this.currentBaseCharacter = "";
        }
    }

    /**
     * Check whether or not a particular character has special characters associated
     * with it.
     * @param baseCharacter the base character to check.
     * @return a boolean indicating whether or not special characters are associated.
     */
    public boolean charHasSpecialChars(char baseCharacter) {
        if (characterMap.containsKey(String.valueOf(baseCharacter))) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Moves the current selection index 1 place to the right within the array
     * of special characters. If the currentBaseCharacter is "", then the index will
     * remain at zero. If the index is about to equal the length of the array, then
     * it will be reset to index 0.
     */
    public void moveSelectionRight() {
        int arrayLength = getCurrentCharCount();
        if (currentIndex < (arrayLength - 1)) {
            currentIndex++;
        } else {
            currentIndex = 0;
        }
    }

    /**
     * Moves the current selection index 1 place to the left within the array of
     * special characters. If the currentBaseCharacter is "", then the index will
     * remain as zero. If the index is about to equal -1, then it will be set to
     * arrayLength-1 (wrap around selection).
     */
    public void moveSelectionLeft() {
        int arrayLength = getCurrentCharCount();
        if (currentIndex > 0) {
            currentIndex--;
        } else {
            currentIndex = arrayLength - 1;
        }
    }

    /**
     * Moves the current selection index to the end of the array, length-1. If
     * the length of the array is 0 (that is, "") then the index is left as 0.
     */
    public void moveSelectionEnd() {
        int arrayLength = getCurrentCharCount();
        if (arrayLength > 0) {
            currentIndex = arrayLength - 1;
        } else {
            currentIndex = 0;
        }
    }

    /**
     * Moves the current selection index to the beginning of the array, 0.
     */
    public void moveSelectionStart() {
        currentIndex = 0;
    }

    /**
     * Get the current selection index.
     * @return the current selection index.
     */
    public int getCurrentIndex() {
        return currentIndex;
    }

    /**
     * Get the current base character.
     * @return the current base character, or a question mark if there is none.
     */
    public char getCurrentBaseCharacter() {
        if (currentBaseCharacter.isEmpty()) {
            return '?';
        } else {
            return currentBaseCharacter.charAt(0);
        }
    }

    /**
     * Get the currently selected special character. If the base character is not
     * valid, a question mark will be returned.
     * 
     * @return the currently selected special character.
     */
    public char getSelectedCharacter() {
        //if we don't have "", then get specialCharString.charAt(curIndex)
        if (currentBaseCharacter.isEmpty() == false) {
            return ((String) characterMap.get(currentBaseCharacter)).charAt(currentIndex);
        } else {
            return '?';
        }
    }

    /**
     * Get the character in the special character array at the index specified.
     * @param index the index of the required character.
     * @return the character at the specified index, or a question mark if no valid
     *         base character is set.
     * @throws IndexOutOfBoundsException if the specified index is not valid.
     */
    public char getCharacterAt(int index) throws IndexOutOfBoundsException {
        //if we don't have "", and index is < array.length, and > 0, then return it
        //String array is the array of special characters associated with the currentBaseCharacter
        String array = (String) characterMap.get(currentBaseCharacter);
        if (currentBaseCharacter.isEmpty() == false) {
            if ((index < array.length()) && (index >= 0)) {
                return array.charAt(index);
            } else {
                throw new IndexOutOfBoundsException();
            }
        } else {
            return '?';
        }
    }

    /**
     * Set the current selection index.
     * @param index the new selection index.
     * @throws IndexOutOfBoundsException if the specified index is not valid.
     */
    public void setCurrentIndex(int index) throws IndexOutOfBoundsException {
        //check if the index is valid, and then set. Return if successful
        int arrayLength = getCurrentCharCount();
        if ((index < arrayLength) && (index >= 0)) {
            currentIndex = index;
        } else {
            throw new IndexOutOfBoundsException();
        }
    }

    /**
     * Get the current number of special characters associated with the base character.
     * @return the number of special characters associated with the base character.
     */
    public int getCurrentCharCount() {
        return ((String) characterMap.get(currentBaseCharacter)).length();
    }

    /**
     * Gets the width of the string of special characters associated with the current
     * base character as a dimension when painted in the graphics context of this component.
     * @return The width of the string of special characters associated with the current
     *         base character.
     */
    public int getStringWidth() {
        String str = (String) characterMap.get(currentBaseCharacter);
        String str2 = " ";
        for (int i = 0; i < str.length(); i++) {
            str2 += str.charAt(i) + " ";
        }

        return this.getFontMetrics(this.getFont()).stringWidth(str2);
    }

    /**
     * Sets the colour used for painting the borders of the menu and selection frame.
     * @param borders the colour used for painting the borders of the menu and selection frame.
     */
    public void setBorderColour(Color borders) {
        this.borders = borders;
    }

    /**
     * Sets the colour used for the background of the menu.
     * @param background the colour used for the background of the menu.
     */
    public void setBackgroundColour(Color background) {
        this.background = background;
    }

    /**
     * Sets the colour used to highlight the current selection. This value should
     * have at least partial transparency.
     * @param selectionFill the colour used to highlight the current selection.
     */
    public void setSelectFillColour(Color selectionFill) {
        this.selectionFill = selectionFill;
    }

    /**
     * Paint the SymbolMenu.
     *
     * @param g Graphics object of the JPanel.
     */
    @Override
    public void paintComponent(Graphics g) {
        //run super method
        super.paintComponent(g);

        //initialise required objects
        Graphics2D g2d = (Graphics2D) g;
        RoundRectangle2D.Float shapeBg = new RoundRectangle2D.Float(0.0f, 0.0f, this.getWidth() - 3, this.getHeight() - 3, 6.0f, 6.0f);
        Rectangle2D.Float shapeSelection = new Rectangle2D.Float(14.0f, 0.0f, 10.0f, this.getHeight() - 3);
        String characterArray = (String) characterMap.get(currentBaseCharacter);
        String characters = " ";

        //add a space between the characters for aesthetics
        for (int i = 0; i < characterArray.length(); i++) {
            characters += characterArray.charAt(i) + " ";
        }

        //set the required size of the symbolMenu.
        this.setSize(g2d.getFontMetrics().stringWidth(characters), 20);


        //set graphics options
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        //fill the background of the menu
        g2d.setPaint(background);
        g2d.fill(shapeBg);

        //draw the border
        g2d.setPaint(borders);
        g2d.draw(shapeBg);

        //draw the characters
        g2d.drawString(characters, 0, 14);

        //draw the selection - if we're at index 0 or length-1, then need to do
        //a special render here due to the rounded rectangle corners
        //if we're not at either end of the menu
        if ((currentIndex > 0) && (currentIndex < (characterArray.length() - 1))) {
            shapeSelection.x = g2d.getFontMetrics().stringWidth(characters.substring(0, 2 * currentIndex + 1)) - 2;
            g2d.setPaint(selectionFill);
            g2d.fill(shapeSelection);
            g2d.setPaint(borders);
            g2d.draw(shapeSelection);
        } else if (currentIndex == 0) //if we're at the start of the menu
        {
            //set-up a shape that covers the unwanted portion of the border of the menu
            Rectangle2D.Float shapeDeselection = (Rectangle2D.Float) shapeSelection.clone();
            shapeDeselection.x = g2d.getFontMetrics().stringWidth(characters.substring(0, 2)) + 2;
            shapeDeselection.width = this.getWidth();
            shapeDeselection.height = this.getHeight();
            //set-up the border and unwanted space as areas and subtract one from the other
            Area deselection = new Area(shapeDeselection);
            Area selection = new Area(shapeBg);
            selection.subtract(deselection);
            //we now have the area of selection for the character at index 0; paint it.
            g2d.setPaint(selectionFill);
            g2d.fill(selection);
            g2d.setPaint(borders);
            g2d.draw(selection);
        } else if (currentIndex == (characterArray.length() - 1)) //if we're at the end of the menu
        {
            //set-up a shape that covers the unwanted portion of the border of the menu
            Rectangle2D.Float shapeDeselection = (Rectangle2D.Float) shapeSelection.clone();
            shapeDeselection.x = 0;
            shapeDeselection.width = g2d.getFontMetrics().stringWidth(characters.substring(0, characters.length() - 2)) - 2;
            shapeDeselection.height = this.getHeight();
            //set-up the border and unwanted space as areas and subtract one from the other
            Area deselection = new Area(shapeDeselection);
            Area selection = new Area(shapeBg);
            selection.subtract(deselection);
            //we now have the area of selection for the character at index 0; paint it.
            g2d.setPaint(selectionFill);
            g2d.fill(selection);
            g2d.setPaint(borders);
            g2d.draw(selection);
        }
    }

    public class MenuFocusListener implements FocusListener {

        public void focusGained(FocusEvent e) {
        }

        public void focusLost(FocusEvent e) {
            e.getComponent().setVisible(false);
        }
    }
}
