/*
 * FileChooserPreviewPane.java
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

package org.ingatan.component;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.JFileChooser;
import javax.swing.JPanel;

/**
 * Provides a preview for any selected image.
 *
 * @author Thomas Everingham
 * @version 1.0
 */
public class FileChooserPreviewPane extends JPanel implements PropertyChangeListener {

    /**
     * The buffered image to paint as the preview.
     */
    BufferedImage preview = null;
    /**
     * The file currently selected.
     */
    File file = null;
    /**
     * If an OutOfMemoryError is encountered, this flag is set so that the paint
     * method knows to paint "Image too large".
     */
    boolean memError = false;

    public FileChooserPreviewPane() {
        this.setPreferredSize(new Dimension(250, 200));
        this.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
    }

    /**
     * Listen for a property change on the file chooser (i.e. a new file was
     * selected, therefore update the preview.)
     *
     * @param e the property change event.
     */
    public void propertyChange(PropertyChangeEvent e) {
        boolean update = false;
        String property = e.getPropertyName();

        if (JFileChooser.DIRECTORY_CHANGED_PROPERTY.equals(property)) {
            file = null;
            preview = null;
            this.repaint();

        } else if (JFileChooser.SELECTED_FILE_CHANGED_PROPERTY.equals(property)) {
            file = (File) e.getNewValue();
            update = true;
        }

        if (update) {
            try {
                preview = null;
                System.gc();
                preview = ImageIO.read(file);
            } catch (IOException ex) {
                preview = null;
            } catch (IllegalArgumentException ex) {
                preview = null;
            } catch (OutOfMemoryError ex)
            {
                preview = null;
                memError = true;
            }
            this.repaint();
        }
    }

    /**
     * Paint the preview to the specified graphics context, and if the preview cannot
     * be painted because it is not an image, or because we are out of memory, then
     * write a message to the preview window.
     * @param g
     */
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        if (preview != null) {
            g2d.drawImage(preview, 5, 5, this.getWidth()-10, this.getHeight()-10, this);
        } else {
            g2d.clearRect(0, 0, this.getWidth(), this.getHeight());
            if (memError)
                g2d.drawString("Image file is too large", 3, this.getHeight() / 2);
            else
                g2d.drawString("Cannot load a preview", 3, this.getHeight() / 2);
        }
    }
}
