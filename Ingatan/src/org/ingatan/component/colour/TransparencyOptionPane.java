/*
 * TransparencyOptionPane.java
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
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.border.EtchedBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * A simple interface for setting the transparency of the foreground and background
 * colours.
 * 
 * @author Thomas Everingham
 * @version 1.0
 */
public class TransparencyOptionPane extends JPanel {

    JLabel fgLabel = new JLabel("Foreground colour transparency:");
    JSlider fgSlider = new JSlider();
    JLabel bgLabel = new JLabel("Background colour transparency:");
    JSlider bgSlider = new JSlider();
    BufferedImage imgPreview = new BufferedImage(32, 32, BufferedImage.TYPE_INT_ARGB);
    JLabel settingPreview = new JLabel(new ImageIcon(imgPreview));
    Font nicerFont = new Font(this.getFont().getFamily(), Font.PLAIN, 10);

    public TransparencyOptionPane() {

        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        fgSlider.setMaximum(255);
        fgSlider.setMinimum(0);
        fgSlider.setValue(255);
        fgSlider.addChangeListener(new sliderChangeListener());
        bgSlider.setMaximum(255);
        bgSlider.setMinimum(0);
        bgSlider.setValue(255);
        bgSlider.addChangeListener(new sliderChangeListener());

        settingPreview.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));
        Graphics2D g2d = (Graphics2D) imgPreview.createGraphics();
        g2d.setFont(new Font(TransparencyOptionPane.this.getFont().getFamily(), Font.BOLD, 20));
        g2d.setPaint(Color.black);
        g2d.fillRect(0, 0, 16, 32);
        g2d.setPaint(new Color(255, 255, 255, fgSlider.getValue()));
        g2d.drawString("F", 2, 23);
        g2d.setPaint(Color.white);
        g2d.drawString("B", 17, 23);
        g2d.setPaint(new Color(0, 0, 0, bgSlider.getValue()));
        g2d.fillRect(16, 0, 16, 32);

        fgSlider.setFont(nicerFont);
        bgSlider.setFont(nicerFont);
        fgLabel.setFont(nicerFont);
        bgLabel.setFont(nicerFont);

        Box vert = Box.createVerticalBox();
        Box horiz = Box.createHorizontalBox();
        vert.add(fgLabel);
        vert.add(fgSlider);
        vert.add(bgLabel);
        vert.add(bgSlider);

        horiz.add(vert);
        horiz.add(Box.createHorizontalStrut(5));
        horiz.add(settingPreview);
        horiz.add(Box.createHorizontalStrut(5));
        this.add(horiz);

    }

    /**
     * Gets the current value for foreground transparency.
     * @return the current value for foreground transparency.
     */
    public int getForegroundTransparency() {
        return fgSlider.getValue();
    }

    /**
     * Gets the current value for background transparency.
     * @return the current value for background transparency.
     */
    public int getBackgroundTransparency() {
        return bgSlider.getValue();
    }

    public class sliderChangeListener implements ChangeListener {

        public void stateChanged(ChangeEvent e) {
            Graphics2D g2d = (Graphics2D) imgPreview.createGraphics();
            g2d.setFont(new Font(TransparencyOptionPane.this.getFont().getFamily(), Font.BOLD, 20));
            g2d.setPaint(Color.black);
            g2d.fillRect(0, 0, 16, 32);
            g2d.setPaint(new Color(255, 255, 255, fgSlider.getValue()));
            g2d.drawString("F", 2, 23);
            g2d.setPaint(Color.white);
            g2d.drawString("B", 17, 23);
            g2d.setPaint(new Color(0, 0, 0, bgSlider.getValue()));
            g2d.fillRect(16, 0, 16, 32);

            settingPreview = new JLabel(new ImageIcon(imgPreview));
            TransparencyOptionPane.this.repaint();
        }
    }
}
