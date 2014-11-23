/*
 * HistorySwatch.java
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

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JPanel;

/**
 * This swatch was made for holding a used colour history for about 10 colours. You may
 * set the number of colours that should be remembered (up to 1000). It is important for you to set
 * the size of the swatch to suite the number of colours you wish to have displayed. The
 * default size will display all swatch squares in a thin, straight, horizontal line. If you have a great
 * number of swatch squares, you may prefer to make the height of the swatch larger and have
 * multiple rows.
 * 
 * @author Thomas Everingham
 * @version 1.0
 */
public class HistorySwatch extends JPanel {

    /**
     * Holds the history of colours used by the user
     */
    private Color[] colours;
    /**
     * An array of JPanels; each panel is one swatch square.
     */
    private JPanel[] swatchSquares;
    /**
     * Side length of the swatch squares.
     */
    private int squareSideLength = 10;
    /**
     * Colour of the border of the swatch squares.
     */
    private Color borders = new Color(26, 97, 110);

    /**
     * Determines whether or not squares are bumped up to the front of the swatch upon
     * mouse click. Default is true.
     */
    private boolean bumpUpOnClick = true;

    public HistorySwatch(int historyLength) {
        //do not allow a history length of more than 1000
        if (historyLength > 1000) {
            historyLength = 1000;
        }
        if (historyLength == 0) {
            historyLength = 1;
        }

        this.setLayout(new FlowLayout(FlowLayout.LEFT, 2, 2));

        //set the two arrays to specified length
        colours = new Color[historyLength];
        swatchSquares = new JPanel[historyLength];
        SwatchSquareListener ssl = new SwatchSquareListener();
        this.setOpaque(false);
        for (int i = 0; i < historyLength; i++) {
            JPanel p = new JPanel();
            Color c = Color.white;
            colours[i] = c;
            p.setName("" + i);
            p.addMouseListener(ssl);
            swatchSquares[i] = p;
            p.setBackground(c);
            p.setPreferredSize(new Dimension(squareSideLength, squareSideLength));
            p.setBorder(BorderFactory.createLineBorder(borders));
            this.add(p);
        }

        //set the minimal size according to the layout manager
        this.setSize((squareSideLength + 2) * historyLength + 2, squareSideLength+2);
    }

    /**
     * Sets whether or not swatch squares are bumped to the front of the swatch upon
     * being selected. The default is true.
     * @param toFront whether or not swatch squares are bumped to the front of the
     * swatch upon being selected.
     */
    public void setColourToFrontUponSelection(boolean toFront)
    {
        bumpUpOnClick = toFront;
    }

    /**
     * Sets the side length of the swatch squares to the specified value. Default is 10.
     * @param length desired side length of the swatch squares. Value must lie between
     *        4 and 26.
     */
    public void setSquareSideLength(int length)
    {
        if (length <= 4)
            length = 5;
        if (length >= 26)
            length = 25;
        squareSideLength = length;
        Dimension newSize = new Dimension(length, length);
        for (int i = 0; i < swatchSquares.length; i++)
            swatchSquares[i].setPreferredSize(newSize);
    }

    /**
     * Gets the side length of the swatch squares.
     * @return the side lengthe of the swatch squares.
     */
    public int getSquareSideLength()
    {
        return squareSideLength;
    }

    /**
     * Sets the swatch to the specified colour array. If the specified array
     * is larger than the number of colours currently held, x, then only the first
     * x colours will be copied from the specified array. If the specified array
     * is smaller than the number of colours currently held, then all colours will
     * be copied from the specified array to the most recent region of the history array.
     *
     * @param colours the colours to set in history.
     */
    public void setSwatch(Color[] colours) {
        if (this.colours.length == colours.length) {
            this.colours = colours;
        } else if (this.colours.length < colours.length) {
            System.arraycopy(colours, 0, this.colours, 0, this.colours.length);
        } else if (this.colours.length > colours.length) {
            System.arraycopy(colours, 0, this.colours, 0, colours.length);
        }

        repaintColours();
    }

    /**
     * Returns the array of colours currently held.
     * @return the array of colours currently held.
     */
    public Color[] getSwatch() {
        return colours;
    }

    /**
     * Gets the colour at the specified index.
     * @param index the index of the required colour.
     * @return the colour at the specified index.
     */
    public Color getColor(int index) {
        return colours[index];
    }

    /**
     * Repaints the colours array to the JPanel swatch squares.
     */
    private void repaintColours() {
        for (int i = 0; i < colours.length; i++) {
            swatchSquares[i].setBackground(colours[i]);
        }
    }

    @Override
    public void addMouseListener(MouseListener m)
    {
        //super.addMouseListener(m);
        for (int i = 0; i < swatchSquares.length; i++)
        {
            swatchSquares[i].addMouseListener(m);
        }
    }

    /**
     * Moves the colour of the specified index to the front of the history - all other
     * colours are moved 'down one', and the least recent colour is lost.
     * @param colourIndex the index of the colour to move to the front.
     */
    private void moveColourToFront(int colourIndex) {
        //ensure that colour is not already at the front (i.e. <= 0)
        if (colourIndex <= 0) {
            if (colourIndex == 0) {
                return;
            } else {
                throw new IndexOutOfBoundsException();
            }
        }
        //ensure that the index is within bounds.
        if (colourIndex > colours.length - 1) {
            throw new IndexOutOfBoundsException();
        }
        Color[] tempColours = new Color[colours.length];
        Color toFront = colours[colourIndex];
        //move all colours 'up one'.
        System.arraycopy(colours, 0, tempColours, 1, colourIndex);
        //if colour is not at the end, then there is more to copy...
        if (colourIndex != colours.length - 1) {
            System.arraycopy(colours, colourIndex + 1, tempColours, colourIndex + 1, colours.length - colourIndex - 1);
        }
        tempColours[0] = toFront;
        colours = tempColours;
        repaintColours();
    }

    /**
     * Adds a new colour to the colour history.
     * @param newColour the new colour to be added.
     */
    public void addColour(Color newColour) {
        colours[colours.length - 1] = newColour;
        moveColourToFront(colours.length - 1);
    }

    /**
     * This listener passes on any 'mouseClicked' events that occurred on swatch squares
     * to the JPanel (<code>this</code>) mouse listener, setting the eventID int of the MouseEvent
     * to the index of the swatch square that was clicked.
     * In this way, the developer may simply add a <code>MouseListener</code> to the <code>InlineHistroySwatch</code>
     * object, and receive any swatch <i>square</i> event and the index of the swatch square at which it occurred.
     */
    public class SwatchSquareListener implements MouseListener {

        public void mouseClicked(MouseEvent e) {
            MouseListener[] listeners = HistorySwatch.this.getMouseListeners();
            MouseEvent newE = new MouseEvent((JComponent) e.getSource(), Integer.valueOf(e.getComponent().getName()), e.getWhen(), e.getModifiers(), e.getX(), e.getY(), e.getClickCount(), false);
            for (int i = 0; i < listeners.length; i++) {
                listeners[i].mouseClicked(newE);
            }
            if ((e.getComponent().getName() != null) && (bumpUpOnClick)) {
                moveColourToFront(Integer.valueOf(e.getComponent().getName()));
            }
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

    /**
     * This class essentially renames the <code>getID</code> method of the MouseEvent class to
     * <code>getClickedColourIndex</code>, as well as providing a couple of convenience methods.
     */
    public class InlineSwatchEvent extends MouseEvent {

        public InlineSwatchEvent(Component source, int id, long when, int modifiers, int x, int y, int clickCount, boolean popupTrigger) {
            super(source, id, when, modifiers, x, y, clickCount, popupTrigger);
        }

        public int getClickedColourIndex() {
            return super.getID();
        }
    }
}
