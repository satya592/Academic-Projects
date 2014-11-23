/*
 * ColourChooserPane.java
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

import org.ingatan.event.ColourChooserPaneEvent;
import org.ingatan.event.ColourChooserPaneListener;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.JPanel;

/**
 * This is an extension to the JPanel which serves as a minimalistic pop-up colour chooser.
 * An instance is constructed with a String representing the location of the image file to use as the
 * colour chooser image, as well as the swatch history lenght. The panel then paints this image upon itself, and listens for clicks or mouse
 * motion.<br>
 *<br>
 * The colour chooser uses the <code>ColourChooserBar</code> class, which provides a bar containing a preview of the currently
 * selected colour, a swatch of the recently selected colours, and a minimalistic RGB value editor.<br>
 *<br>
 * The <code>currentColour</code> field is updated by the listeners that are implemented within this class. Any change int
 * the <code>currentColour</code> field will fire a new ColourChooserPaneEvent which informs all <code>ColourChooserPaneListeners</code>
 * that a new colour has been selected, as well as how it was selected; by clicking the swatch, dragging the mouse, clicking the image
 * or setting RGB values manually.<br>
 *<br>
 * If one wishes to save and restore this history from and to the inline swatch, then the <code>getColourHistory</colour>
 * and <code>setColourHistory</code> methods may be used. One may also set the current colour using the <code>setRGBValues</code>.<br>
 *<br>
 * Invalid coordinates are handled by ignoring that the event occurred and leaving the <code>currentColour</code>
 * field unchanged.<br>
 *
 * @author Thomas Everingham
 * @version 1.0
 */
public class ColourChooserPane extends JPanel {

    /**
     * The image to be used by the colour chooser. This may be a swatch, or a gradient of colours,
     * or anything that is desired. A colour can be chosen from any pixel within this image.
     */
    private BufferedImage img = null;
    /**
     * The currently selected colour. The default is black.
     */
    private Color currentColour = Color.black;
    /**
     * A bar showing the currently selected colour, the history of 10 recently used
     * colours, and the RGB value of the currently selected colour.
     */
    private ColourChooserBar statusBar;
    /**
     * The listeners associated with this chooser.
     */
    private ColourChooserPaneListener[] chooserListeners = new ColourChooserPaneListener[0];

    /**
     * Creates a new instance of ColourChooserPane.
     *
     * @param chooserImageResource the image to be used by the colour chooser.
     */
    public ColourChooserPane(String chooserImageResource, int colourHistoryLength) {
        try {

            img = ImageIO.read(Thread.currentThread().getContextClassLoader().getResource(chooserImageResource));
        } catch (Exception e) {
            Logger.getLogger(ColourChooserPane.class.getName()).log(Level.SEVERE, "while trying to load the colour chooser image.", e);
        }
        if (img != null) {
            this.setSize(img.getWidth(), img.getHeight());
            this.setPreferredSize(new Dimension(img.getWidth(), img.getHeight()));
        }
        this.addMouseListener(new ImageMouseListener());
        this.addMouseMotionListener(new ImageMotionListener());
        statusBar = new ColourChooserBar(colourHistoryLength);
        statusBar.getRGBInfoPanel().addTextFieldKeyListener(new RGBKeyListener());
        statusBar.getHistorySwatch().addMouseListener(new SwatchMouseListener());
        this.setLayout(null);
        this.add(statusBar);
        statusBar.setLocation(2, img.getHeight() - statusBar.getHeight() +2);
        statusBar.validate();
        this.validate();


    }

    /**
     * Adds a <code>RichTextToolbarListener</code> to this <code>RichTextToolbar</code> instance.
     * @param listener
     */
    public void addColourChooserPaneListener(ColourChooserPaneListener listener) {
        if (chooserListeners.length == 0) {
            chooserListeners = new ColourChooserPaneListener[]{listener};
        } else {
            ColourChooserPaneListener[] temp = new ColourChooserPaneListener[chooserListeners.length+1];
            System.arraycopy(chooserListeners, 0, temp, 0, chooserListeners.length);
            temp[chooserListeners.length] = listener;
            chooserListeners = temp;
        }
    }

    /**
     * Removes a <code>RichTextToolbarListener</code> from this <code>RichTextToolbar</code> instance.
     * @param listener the <code>RichTextToolbarListener</code> to remove.
     * @return true if the listener could be found and removed, and false otherwise.
     */
    public boolean removeColourChooserPaneListener(ColourChooserPaneListener listener)
    {
        if (chooserListeners.length == 0)
            return false;
        if (chooserListeners.length == 1)
            if (chooserListeners[0].equals(listener))
            {
                chooserListeners = new ColourChooserPaneListener[0];
                return true;
            }
            else
                return false;

        int index = -1;
        //get the index
        for (int i = 0; i < chooserListeners.length; i++)
        {
            if (chooserListeners[i].equals(listener))
            {
                index = i;
                break;
            }
        }

        //if index is -1, we have not found the listener
        if (index == -1)
            return false;

        //otherwise, get rid of the listener
        ColourChooserPaneListener[] temp = new ColourChooserPaneListener[chooserListeners.length-1];
        if (index == 0)
        {
            System.arraycopy(chooserListeners, 1, temp, 0, chooserListeners.length-1);
            chooserListeners = temp;
            return true;
        }
        else if (index == chooserListeners.length-1)
        {
            System.arraycopy(chooserListeners, 0, temp, 0, chooserListeners.length-1);
            chooserListeners = temp;
            return true;
        }
        else //the index is not on the edge of the array
        {
            System.arraycopy(chooserListeners, 0, temp, 0, index);
            System.arraycopy(chooserListeners, index+1, temp, index, chooserListeners.length-index-1);
            chooserListeners = temp;
            return true;
        }
    }

    /**
     * Sets the current colour, the status bar RGB values, and the status bar preview colour,
     * in accordance with the specified R, G and B values.
     *
     * @param r the value for red.
     * @param g the value for green.
     * @param b the value for blue.
     */
    public void setRGB(int r, int g, int b)
    {
        currentColour = new Color(r,g,b);
        statusBar.getRGBInfoPanel().setRGB(r, g, b);
        statusBar.setPreviewColour(currentColour);
        statusBar.repaint();
    }

    @Override
    public void paintComponent(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.drawImage(img, 0, 0, this);
    }

    /**
     * Gets the currently selected colour.
     * @return the currently selected colour.
     */
    public Color getCurrentColour() {
        return currentColour;
    }

    /**
     * MouseMotionListener to respond to a drag event on <code>this</code>
     */
    public class ImageMotionListener implements MouseMotionListener {

        public void mouseDragged(MouseEvent e) {
            //firstly, get rid of any invalid coords
            if (!new Rectangle(0,0,img.getWidth(),img.getHeight()).contains(e.getPoint()))
                return;
            if (statusBar.getBounds().contains(e.getPoint())) {
                return;
            }
            currentColour = new Color(img.getRGB(e.getX(), e.getY()));

            //notify any listeners of the colour change
            for (int i = 0; i < chooserListeners.length; i++)
                chooserListeners[i].colourSelected(new ColourChooserPaneEvent(ColourChooserPane.this, ColourChooserPaneEvent.IMAGE_DRAG,currentColour));

            statusBar.getRGBInfoPanel().setRGB(currentColour.getRed(), currentColour.getGreen(), currentColour.getBlue());
            statusBar.setPreviewColour(currentColour);
            statusBar.repaint();

        }

        public void mouseMoved(MouseEvent e) {
        }
    }

    /**
     * MouseListener to respond to mouse clicks on <code>this</code>
     */
    public class ImageMouseListener implements MouseListener {

        public void mouseClicked(MouseEvent e) {
        }

        public void mousePressed(MouseEvent e) {
            //firstly, get rid of any invalid coords
            if (!new Rectangle(0,0,img.getWidth(),img.getHeight()).contains(e.getPoint()))
                return;
            if (statusBar.getBounds().contains(e.getPoint())) {
                return;
            }
            statusBar.setPreviewColour(currentColour);
            statusBar.repaint();
        }

        public void mouseReleased(MouseEvent e) {
            //firstly, get rid of any invalid coords
            if (!new Rectangle(0,0,img.getWidth(),img.getHeight()).contains(e.getPoint()))
                return;
            if (!statusBar.getBounds().contains(e.getPoint()))
            {
                currentColour = new Color(img.getRGB(e.getX(), e.getY()));
                //notify any listeners of the colour change
                for (int i = 0; i < chooserListeners.length; i++)
                    chooserListeners[i].colourSelected(new ColourChooserPaneEvent(ColourChooserPane.this, ColourChooserPaneEvent.IMAGE_CLICK,currentColour));
            }
            if (currentColour.equals(statusBar.getHistorySwatch().getColor(0)))
                return;
            statusBar.getRGBInfoPanel().setRGB(currentColour.getRed(), currentColour.getGreen(), currentColour.getBlue());
            statusBar.setPreviewColour(currentColour);
            statusBar.getHistorySwatch().addColour(currentColour);
            statusBar.repaint();
        }

        public void mouseEntered(MouseEvent e) {
        }

        public void mouseExited(MouseEvent e) {
        }
    }

    /**
     * Listens for any mouse interaction with the swatch. Sets the current colour
     * to the swatch square that was clicked.
     */
    public class SwatchMouseListener implements MouseListener {

        public void mouseClicked(MouseEvent e) {
            //This is the event we are interested in for setting the currentColour value
            //If component name is not null, then it must have been set to an index
            if (e.getComponent().getName() != null) {

                //the mouse event handling in HistorySwatch.java has already moved the clicked on colour up
                //to the top of the list, so the new colour is whatever exists at index 0
                currentColour = statusBar.getHistorySwatch().getColor(0);
                
                //notify any listeners of the colour change
                for (int i = 0; i < chooserListeners.length; i++)
                    chooserListeners[i].colourSelected(new ColourChooserPaneEvent(ColourChooserPane.this, ColourChooserPaneEvent.SWATCH,currentColour));
                statusBar.setPreviewColour(currentColour);
                statusBar.repaint();
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
     * Listens for enter event in the RGB text fields of the status bar.
     */
    public class RGBKeyListener implements KeyListener {

        public void keyTyped(KeyEvent e) {

        }

        public void keyPressed(KeyEvent e) {
            if (e.getKeyCode() == KeyEvent.VK_ENTER)
            {
                int r = statusBar.getRGBInfoPanel().getR();
                int g = statusBar.getRGBInfoPanel().getG();
                int b = statusBar.getRGBInfoPanel().getB();


                if (((r >= 0) && (r <= 255)) && ((g >= 0) && (g <= 255)) && ((b >= 0) && (b <= 255)))
                {
                    Color specifiedColour = new Color(r,g,b);
                    if (currentColour.equals(specifiedColour))
                        return;
                    currentColour = specifiedColour;
                    //notify any listeners of the colour change
                    for (int i = 0; i < chooserListeners.length; i++)
                        chooserListeners[i].colourSelected(new ColourChooserPaneEvent(ColourChooserPane.this, ColourChooserPaneEvent.RGB_INFO,currentColour));
                    statusBar.setPreviewColour(currentColour);
                    statusBar.getHistorySwatch().addColour(currentColour);
                    statusBar.repaint();
                }
                else
                {
                    statusBar.getRGBInfoPanel().setRGB(currentColour.getRed(), currentColour.getGreen(), currentColour.getBlue());
                }
            }
        }

        public void keyReleased(KeyEvent e) {

        }

    }
}
