/*
 * ColourSwatchPane.java
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
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.beans.PropertyChangeListener;
import javax.swing.*;

/**
 * Provides a history of recent colours, as well as a swatch of common colours. Also
 * provides a 'current foreground colour' and 'current background colour' indicator.
 *
 * @author Thomas Everingham
 * @version 1.0
 */
public class ColourSwatchPane extends JPanel {

    /**
     * Here, the history swatch is used to provide 24 preset colours for the user to select from.
     */
    private HistorySwatch swatch = new HistorySwatch(24);
    /**
     * The foreground colour preview JPanel.
     */
    private JPanel fgColour = new JPanel();
    /**
     * The background colour preview JPanel.
     */
    private JPanel bgColour = new JPanel();
    /**
     * Boolean indicating whether or not the user has invoked the <code>ColourChooserPane</code> by
     * pressing the foreground or background preview JPanel. Recording this lets the <code>ColourChooserPaneListener</code>
     * know whether to set the selected colour to the foreground or background.
     */
    private boolean settingFgColour = true;
    /**
     * Holds the <code>ColourChooserPane</code> used.
     */
    private JPopupMenu colourPopup = new JPopupMenu();
    /**
     * Holds the <code>TransparencyOptionPane</code> used.
     */
    private JPopupMenu transparencyPopup = new JPopupMenu();
    /**
     * Transparency options pane.
     */
    private TransparencyOptionPane transparencyOptions = new TransparencyOptionPane();
    /**
     * The <code>ColourChooserPane</code> allows further specification of colour.
     */
    private ColourChooserPane colourChooser = new ColourChooserPane("resources/colour_choose_small.png", 6);
    /**
     * Listens for any mouse interaction on the foreground or background preview JPanels.
     */
    private ColourMouseListener fgbgListener = new ColourMouseListener();
    /**
     * Colour of the border.
     */
    private Color borders = new Color(26, 97, 110);
    /**
     * Swap the foreground and background colours.
     */
    private JLabel lblSwap;
    /**
     * Used as a button to show the transparency option pane as a popup.
     */
    private JLabel lblTransparent;

    /**
     * The preset colours provided.
     */
    private Color[] swatchColours = new Color[]{
        new Color(252, 194, 182),
        new Color(252, 194, 98),
        new Color(253, 243, 150),
        new Color(178, 254, 181),
        new Color(166, 237, 246),
        new Color(170, 175, 249),
        new Color(208, 176, 213),
        new Color(255, 255, 255),
        new Color(249, 11, 0),
        new Color(181, 86, 0),
        new Color(253, 237, 3),
        new Color(2, 254, 13),
        new Color(2, 251, 224),
        new Color(20, 43, 245),
        new Color(158, 71, 151),
        new Color(170, 170, 170),
        new Color(170, 7, 0),
        new Color(103, 58, 0),
        new Color(125, 130, 0),
        new Color(0, 126, 18),
        new Color(0, 129, 108),
        new Color(6, 12, 121),
        new Color(78, 11, 86),
        new Color(0, 0, 0)};

    /**
     * Creates a new instance of the swatch pane.
     */
    public ColourSwatchPane() {
        fgColour.setBackground(Color.black);
        fgColour.setMaximumSize(new Dimension(20, 20));
        fgColour.setPreferredSize(new Dimension(20, 20));
        fgColour.setMinimumSize(new Dimension(10, 10));
        fgColour.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(Color.black, 1), BorderFactory.createLineBorder(Color.white, 1)));
        fgColour.setToolTipText("Foreground Colour");
        fgColour.addMouseListener(fgbgListener);

        bgColour.setBackground(Color.white);
        bgColour.setMaximumSize(new Dimension(15, 15));
        bgColour.setPreferredSize(new Dimension(15, 15));
        bgColour.setMinimumSize(new Dimension(10, 10));
        bgColour.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(Color.black, 1), BorderFactory.createLineBorder(Color.white, 1)));
        bgColour.setToolTipText("Background Colour");
        bgColour.addMouseListener(fgbgListener);

        swatch.setPreferredSize(new Dimension(140, 55));
        swatch.setColourToFrontUponSelection(false);
        swatch.addMouseListener(new PresetSwatchListener());
        swatch.setSquareSideLength(15);
        swatch.setSwatch(swatchColours);

        colourChooser.addColourChooserPaneListener(new ColourChooserListener());
        colourPopup.insert(colourChooser, 0);
        transparencyPopup.insert(transparencyOptions, 0);

        this.setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
        this.setBorder(BorderFactory.createLineBorder(borders));
        this.add(swatch);
        this.add(Box.createHorizontalStrut(4));
        Box vert = Box.createVerticalBox();

        //put the background and foreground colour previews side by side
        Box colBox = Box.createHorizontalBox();
        colBox.add(bgColour);
        colBox.add(Box.createHorizontalStrut(2));
        colBox.add(fgColour);
        colBox.add(Box.createHorizontalStrut(4));

        lblSwap = new JLabel(new ImageIcon(ColourSwatchPane.class.getResource("/resources/icons/switch.png")));
        lblSwap.setPreferredSize(new Dimension(16,16));
        lblTransparent = new JLabel(new ImageIcon(ColourSwatchPane.class.getResource("/resources/icons/paletteTransparent.png")));
        lblTransparent.setPreferredSize(new Dimension(10,10));
        lblSwap.addMouseListener(new SwapAndTransparencyLabelMouseListener());
        lblTransparent.addMouseListener(new SwapAndTransparencyLabelMouseListener());
        //put the swap and transparency buttons side by side.
        Box optBox = Box.createHorizontalBox();
        optBox.add(lblSwap);
        optBox.add(Box.createHorizontalStrut(3));
        optBox.add(lblTransparent);
        optBox.add(Box.createHorizontalStrut(5));

        vert.add(Box.createVerticalStrut(3));
        vert.add(colBox);
        vert.add(optBox);
        vert.add(Box.createVerticalStrut(3));

        this.add(vert);
    }

    /**
     * Adds the specified <code>PropertyChangeListener</code> to the background and
     * foreground colour preview <code>JPanels</code>.
     * @param listener the listener to add.
     */
    public void addColourPreviewPropertyChangeListener(PropertyChangeListener listener)
    {
        bgColour.addPropertyChangeListener(listener);
        fgColour.addPropertyChangeListener(listener);
    }

    /**
     * Set the foreground colour to that specified.
     * @param newColour the new foreground colour.
     */
    public void setForegroundColour(Color newColour)
    {
        fgColour.setBackground(newColour);
    }

    /**
     * Set the background colour to that specified.
     * @param newColour the new background colour.
     */
    public void setBackgroundColour(Color newColour)
    {
        bgColour.setBackground(newColour);
    }

    /**
     * Gets the currently selected background colour.
     * @return the currently selected background colour.
     */
    public Color getSelectedBackgroundColour()
    {
        Color bg = bgColour.getBackground();
        return new Color(bg.getRed(), bg.getGreen(), bg.getBlue(), transparencyOptions.getBackgroundTransparency());
    }

    /**
     * Gets the currently selected background colour without any transparency.
     * @return the currently selected background colour without any transparency.
     */
    public Color getSelectedBackgroundColourOpaque()
    {
        return bgColour.getBackground();
    }

    /**
     * Gets the currently selected foreground colour.
     * @return the currently selected foreground colour.
     */
    public Color getSelectedForegroundColour()
    {
        Color fg = fgColour.getBackground();
        return new Color(fg.getRed(), fg.getGreen(), fg.getBlue(), transparencyOptions.getForegroundTransparency());
    }

    /**
     * Gets the currently selected foreground colour without any transparency.
     * @return the currently selected foreground colour without any transparency.
     */
    public Color getSelectedForegroundColourOpaque()
    {
        return fgColour.getBackground();
    }

    /**
     * Listens for interaction with the foreground or background preview JPanels.
     * The <code>ColourChooserPane</code> has its RGB value set each time. This is because
     * the colour may have changed since last time if the user clicked on of the preset
     * swatch colours.
     */
    private class ColourMouseListener implements MouseListener {

        public void mouseClicked(MouseEvent e) {
            if (e.getComponent().equals(fgColour)) {
                settingFgColour = true;
            } else if (e.getComponent().equals(bgColour)) {
                settingFgColour = false;
            }

            colourChooser.setRGB(e.getComponent().getBackground().getRed(),
                    e.getComponent().getBackground().getGreen(),
                    e.getComponent().getBackground().getBlue());
            colourPopup.show(ColourSwatchPane.this, e.getX()+e.getComponent().getX(), e.getY()+e.getComponent().getY());
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
     * Listens for mouse interaction with the <code>ColourChooserPane</code> popup.
     */
    private class ColourChooserListener implements ColourChooserPaneListener {

        public void colourSelected(ColourChooserPaneEvent e) {
            if (e.getChangeSourceID() == ColourChooserPaneEvent.IMAGE_DRAG) {
                return; //not interested in image drag events
            }

            if (settingFgColour) {
                fgColour.setBackground(e.getNewSelectedColour());
            } else {
                bgColour.setBackground(e.getNewSelectedColour());
            }

            Color c = e.getNewSelectedColour();


            colourPopup.setVisible(false);
        }
    }

    private class SwapAndTransparencyLabelMouseListener implements MouseListener
    {

        public void mouseClicked(MouseEvent e) {
            if (e.getSource().equals(lblSwap))
            {
                Color fg = ColourSwatchPane.this.fgColour.getBackground();
                Color bg = ColourSwatchPane.this.bgColour.getBackground();
                ColourSwatchPane.this.bgColour.setBackground(fg);
                ColourSwatchPane.this.fgColour.setBackground(bg);
            }
            else if (e.getSource().equals(lblTransparent))
            {
                transparencyPopup.show(ColourSwatchPane.this, e.getX()+e.getComponent().getX(), e.getY()+e.getComponent().getY());
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
     * Listens for mouse interaction with the preset swatch, and sets the current colour
     * selection data to any selected colour. Left click does foreground colour, right does
     * background colour.
     */
    private class PresetSwatchListener implements MouseListener
    {

        public void mouseClicked(MouseEvent e) {
            if (e.getComponent().getName() != null)
            {
                int index;
                try {
                    index = Integer.valueOf(e.getComponent().getName());
                }
                catch (NumberFormatException ex) {
                    index = 0;
                }
                if (e.getButton() == MouseEvent.BUTTON1)
                    fgColour.setBackground(swatch.getColor(index));
                else if (e.getButton() == MouseEvent.BUTTON3)
                    bgColour.setBackground(swatch.getColor(index));
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
}
