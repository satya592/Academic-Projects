/*
 * ArrowOptionPane.java
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
import org.ingatan.component.image.ThumbnailPane;
import org.ingatan.image.ImageUtils;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.awt.event.ActionEvent;
import java.awt.event.MouseListener;
import java.awt.geom.Line2D;
import java.awt.geom.Path2D;
import java.awt.image.BufferedImage;
import javax.swing.AbstractAction;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import org.geotools.renderer.geom.Arrow2D;

/**
 * This option pane displays options for drawing arrows - it separates the arrow head option and
 * the line style option into two <code>ThumbnailPane</code>s.
 *
 * @author Thomas Everingham
 * @version 1.0
 */
public class ArrowOptionPane extends JPanel implements OptionPane {

    /**
     * Panel containing all strokes.
     */
    LineOptionPane lineOptions = new LineOptionPane();
    /**
     * Panel containing the arrow head options.
     */
    ThumbnailPane headOptions = new ThumbnailPane(38, 3);
    /**
     * A buffered image to which a sample of each arrow head is painted, ready for the thumbnail pane.
     */
    BufferedImage arrowOptionSample = new BufferedImage(38, 38, BufferedImage.TYPE_INT_RGB);
    /**
     * JTextBox allowing the user to specify whether or not the arrows should be filled.
     */
    JCheckBox fillArrow = new JCheckBox();
    /**
     * Array of arrows that the user may select from (built by calling setUpArrows).
     */
    Arrow2D[] arrows = new Arrow2D[0];
    Color cforeground = Color.black;
    Color cbackground = Color.white;
    boolean antialiasOn = true;

    /**
     * Returns a new instance of <code>ArrowOptionPane</code>
     */
    public ArrowOptionPane() {
        setUpArrows();
        setUpArrowOptions();
        fillArrow.setAlignmentX(CENTER_ALIGNMENT);

        headOptions.setSelectedIndex(0);
        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        

        this.add(fillArrow);
        this.add(Box.createVerticalStrut(10));
        this.add(headOptions);
        this.add(Box.createVerticalStrut(10));
        this.add(lineOptions);



        fillArrow.setAction(new fillChangeAction());
        fillArrow.setFont(new Font(this.getFont().getFamily(), Font.PLAIN, 10));
        fillArrow.setText("Fill arrow");

        fillArrow.setSelected(false);
        
    }

    public void rebuildSelf()
    {
        headOptions.clearThumbnails();
        setUpArrowOptions();
        headOptions.validate();
        lineOptions.rebuildSelf();
        headOptions.setSelectedIndex(headOptions.getSelectedIndex());
    }

    /**
     * Draws icons for each of the available arrow heads. Also sets up the thumbnail pane to display them.
     */
    private void setUpArrowOptions() {
        Line2D line = new Line2D.Float(0.0f, 0.0f, 25.0f, 25.0f);
        for (int i = 0; i < arrows.length; i++) {
            arrowOptionSample = new BufferedImage(38, 38, BufferedImage.TYPE_INT_RGB);
            Graphics2D g2d = arrowOptionSample.createGraphics();
            g2d.setPaint(Color.white);
            Path2D arrowPath = ImageUtils.setArrowAlongLine(arrows[i], line);
            g2d.fillRect(0, 0, 38, 38);
            if (antialiasOn) {
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            } else {
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
            }
            g2d.setStroke(lineOptions.getSelectedStroke());
            g2d.setPaint(cbackground);
            if (fillArrow.isSelected()) {
                g2d.fill(arrowPath);
            }
            g2d.setPaint(cforeground);
            g2d.draw(arrowPath);
            Image strokeImg = (Image) arrowOptionSample;
            ImageIcon ic = new ImageIcon(strokeImg);
            headOptions.addThumbnail(ic);
        }
    }

    /**
     * Gets the currently selected <code>Stroke</code>.
     * @return the currently selected <code>Stroke</code>.
     */
    public Stroke getSelectedStroke()
    {
        return lineOptions.getSelectedStroke();
    }

    /**
     * Gets the currently selected instance of <code>Arrow2D</code>.
     * @return the currently selected instance of <code>Arrow2D</code>.
     */
    public Arrow2D getSelectedArrow()
    {
        return arrows[headOptions.getSelectedIndex()];
    }

    /**
     * Gets whether or not the fill arrow option is selected.
     * @return whether or not the fill arrow option is selected.
     */
    public boolean getFillArrow()
    {
        return fillArrow.isSelected();
    }

    /**
     * Adds the specified <code>MouseListener</code> to every thumbnail
     * that exists in this pane.
     * @param listener the listener to add.
     */
    public void addThumbnailMouseListener(MouseListener listener)
    {
        lineOptions.addThumbnailMouseListener(listener);
        headOptions.addThumbnailMouseListener(listener);
    }

    /**
     * Removes the specified <code>MouseListener</code> to every thumbnail
     * that exists in this pane.
     * @param listener the listener to remove.
     */
    public void removeThumbnailMouseListener(MouseListener listener)
    {
        lineOptions.removeThumbnailMouseListener(listener);
        headOptions.removeThumbnailMouseListener(listener);
    }

    private class fillChangeAction extends AbstractAction {

        public void actionPerformed(ActionEvent e) {
            headOptions.clearThumbnails();
            setUpArrowOptions();
            headOptions.validate();
        }
    }

    /**
     * Update this class upon change of colour.
     * @param newFgColour the new cforeground colour
     * @param newBgColour the new cbackground colour
     */
    public void updateForNewColour(Color newFgColour, Color newBgColour) {
        cforeground = newFgColour;
        cbackground = newBgColour;
        //reload panels
        lineOptions.updateForNewColour(newFgColour, newBgColour);
        headOptions.clearThumbnails();
        setUpArrowOptions();
        headOptions.validate();
    }

    /**
     * Update this class of an antialias setting change.
     * @param antialias
     */
    public void updateForAntialias(boolean antialias) {
        this.antialiasOn = antialias;
        //reload panels
        lineOptions.updateForAntialias(antialias);
        headOptions.clearThumbnails();
        setUpArrowOptions();
        headOptions.validate();
    }

    private void setUpArrows() {
        //create basic arrows with a line and triangular head
        Arrow2D basicArrow1 = new Arrow2D(0, 10, 25, 6);
        basicArrow1.setTailProportion(0.85, 0.0, 0.0);

        Arrow2D basicArrow2 = new Arrow2D(0, 10, 50, 8);
        basicArrow2.setTailProportion(0.85, 0.0, 0.0);

        Arrow2D basicArrow3 = new Arrow2D(0, 10, 70, 12);
        basicArrow3.setTailProportion(0.8, 0.0, 0.0);

        //create sharp arrows with a line and sharper triangular head
        Arrow2D sharpArrow1 = new Arrow2D(0, 10, 25, 4);
        sharpArrow1.setTailProportion(0.6, 0.0, 0.0);

        Arrow2D sharpArrow2 = new Arrow2D(0, 10, 40, 5);
        sharpArrow2.setTailProportion(0.6, 0.0, 0.0);

        Arrow2D sharpArrow3 = new Arrow2D(0, 10, 55, 6);
        sharpArrow3.setTailProportion(0.6, 0.0, 0.0);

        //create chunky arrows with triangular heads
        Arrow2D outlineArrow1 = new Arrow2D(0, 10, 25, 8);
        outlineArrow1.setTailProportion(0.8, 0.3, 0.3);

        Arrow2D outlineArrow2 = new Arrow2D(0, 10, 30, 14);
        outlineArrow2.setTailProportion(0.8, 0.4, 0.4);

        Arrow2D outlineArrow3 = new Arrow2D(0, 10, 35, 17);
        outlineArrow3.setTailProportion(0.8, 0.5, 0.5);

        Arrow2D outlineArrow4 = new Arrow2D(0, 10, 40, 20);
        outlineArrow4.setTailProportion(0.7, 0.3, 0.3);

        Arrow2D outlineArrow5 = new Arrow2D(0, 10, 40, 23);
        outlineArrow5.setTailProportion(0.7, 0.4, 0.4);

        Arrow2D outlineArrow6 = new Arrow2D(0, 10, 45, 26);
        outlineArrow6.setTailProportion(0.6, 0.5, 0.5);

        //create rectangular arrows with arrow heads that are flush with the tail outline
        Arrow2D rectArrow1 = new Arrow2D(0, 10, 25, 6);
        rectArrow1.setTailProportion(0.7, 1.0, 1.0);

        Arrow2D rectArrow2 = new Arrow2D(0, 10, 30, 10);
        rectArrow2.setTailProportion(0.7, 1.0, 1.0);

        Arrow2D rectArrow3 = new Arrow2D(0, 10, 30, 14);
        rectArrow3.setTailProportion(0.7, 1.0, 1.0);

        Arrow2D rectArrow4 = new Arrow2D(0, 10, 35, 18);
        rectArrow4.setTailProportion(0.6, 1.0, 1.0);

        Arrow2D rectArrow5 = new Arrow2D(0, 10, 35, 22);
        rectArrow5.setTailProportion(0.6, 1.0, 1.0);

        Arrow2D rectArrow6 = new Arrow2D(0, 10, 40, 26);
        rectArrow6.setTailProportion(0.5, 1.0, 1.0);

        //create chunky arrows with triangular tails
        Arrow2D tailArrow1 = new Arrow2D(0, 10, 25, 8);
        tailArrow1.setTailProportion(0.75, 0.1, 0.5);

        Arrow2D tailArrow2 = new Arrow2D(0, 10, 25, 18);
        tailArrow2.setTailProportion(0.7, 0.1, 0.5);

        Arrow2D tailArrow3 = new Arrow2D(0, 10, 35, 25);
        tailArrow3.setTailProportion(0.7, 0.05, 0.5);

        //create chunky arrows with triangular tails
        Arrow2D tailArrow4 = new Arrow2D(0, 10, 25, 8);
        tailArrow4.setTailProportion(0.75, 0.5, 0.1);

        Arrow2D tailArrow5 = new Arrow2D(0, 10, 25, 18);
        tailArrow5.setTailProportion(0.7, 0.5, 0.1);

        Arrow2D tailArrow6 = new Arrow2D(0, 10, 25, 18);
        tailArrow6.setTailProportion(0.7, 1.0, 0.1);

        //create long triangle arrows
        Arrow2D triArrow1 = new Arrow2D(0, 10, 500, 8);
        triArrow1.setTailProportion(0.1, 1.0, 1.0);

        Arrow2D triArrow2 = new Arrow2D(0, 10, 500, 14);
        triArrow2.setTailProportion(0.1, 0.0, 0.0);

        Arrow2D triArrow3 = new Arrow2D(0, 10, 500, 17);
        triArrow3.setTailProportion(0.1, 1.0, 1.0);



        arrows = new Arrow2D[]{
                    basicArrow1,
                    basicArrow2,
                    basicArrow3,
                    sharpArrow1,
                    sharpArrow2,
                    sharpArrow3,
                    outlineArrow1,
                    outlineArrow2,
                    outlineArrow3,
                    outlineArrow4,
                    outlineArrow5,
                    outlineArrow6,
                    rectArrow1,
                    rectArrow2,
                    rectArrow3,
                    rectArrow4,
                    rectArrow5,
                    rectArrow6,
                    tailArrow1,
                    tailArrow2,
                    tailArrow3,
                    tailArrow4,
                    tailArrow5,
                    tailArrow6,
                    triArrow1,
                    triArrow2,
                    triArrow3
                };

    }
}
