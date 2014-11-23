/*
 * SelectionOptionPane.java
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
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.RoundRectangle2D;
import javax.swing.Action;
import javax.swing.ButtonGroup;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

/**
 * This class provides the selection option to the user. The user may either choose for the backgroundUnselected colour
 * to be opaque or transparent within the current selection.
 * @author Thomas Everingham
 * @version 1.0
 */
public class SelectionOptionPane extends JPanel implements OptionPane {

    /**
     * Radio button option for keeping the backgroundUnselected colour opaque while it is selected.
     */
    JRadioButton bgOpaque = new JRadioButton("<html>Keep background<br>colour opaque");
    /**
     * Radio button option for setting the backgroundUnselected colour transparent while selected.
     */
    JRadioButton bgTransparent = new JRadioButton("<html>Make background<br>colour transparent");
    /**
     * Button group to synchronise the states of the two radio button options.
     */
    ButtonGroup optionGroup = new ButtonGroup();
    /**
     * Whether or not antialiasing should be used.
     */
    private boolean antialiasOn = true;
    /**
     * The currently selected foreground colour.
     */
    private Color cforeground = Color.black;
    /**
     * The currently selected backgroundUnselected colour.
     */
    private Color cbackground = Color.white;


    public SelectionOptionPane() {

        bgOpaque.setOpaque(false);
        bgOpaque.setSelected(true);
        bgOpaque.setFont(new Font(this.getFont().getFamily(), Font.PLAIN, 10));
        bgTransparent.setOpaque(false);
        bgTransparent.setFont(new Font(this.getFont().getFamily(), Font.PLAIN, 10));
        this.add(bgOpaque);
        this.add(bgTransparent);
        optionGroup.add(bgOpaque);
        optionGroup.add(bgTransparent);

        bgOpaque.setFocusable(false);
        bgTransparent.setFocusable(false);
    }

    /**
     * Update this class upon change of colour.
     * @param newFgColour the new cforeground colour
     * @param newBgColour the new cbackground colour
     */
    public void updateForNewColour(Color newFgColour, Color newBgColour) {
        cforeground = newFgColour;
        cbackground = newBgColour;
    }

    /**
     * Update this class of an antialias setting change.
     * @param antialias
     */
    public void updateForAntialias(boolean antialias)
    {
        this.antialiasOn = antialias;
    }

    /**
     * Checks whether or not the backgroundUnselected colour of a selection should be transparent.
     * @return whether or not the backgroundUnselected colour should be made transparent.
     */
    public boolean isBackgroundTransparent()
    {
        return bgTransparent.isSelected();
    }

    public void setRadioButtonActions(Action a)
    {
        String txt = bgTransparent.getText();
        bgTransparent.setAction(a);
        bgTransparent.setText(txt);

        txt = bgOpaque.getText();
        bgOpaque.setAction(a);
        bgOpaque.setText(txt);
    }

    public void rebuildSelf()
    {
        
    }


    @Override
    public void paintComponent(Graphics g)
    {
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
}
