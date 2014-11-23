/*
 * EmbeddedImage.java
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

import org.ingatan.component.image.ImageAcquisitionDialog;
import org.ingatan.io.IOManager;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;

/**
 * A JPanel which displays an image. This is used as an embedded component for the
 * <code>RichTextArea</code> as it is able to pass back its image reference for
 * serialisation. The image reference allows this component to be reconstructed
 * later.
 *
 * @author Thomas Everingham
 * @version 1.0
 */
public class EmbeddedImage extends EmbeddedGraphic implements MouseListener {

    /**
     * The maximum <i>recommended</i> width of the image. These two variables are
     * not enforced, but provide the basis for the result of the <code>isImageTooLarge</code>
     * method.
     */
    public static final int MAX_IMAGE_WIDTH = 500;
    /**
     * The maximum <i>recommended</i> height of the image. These two variables are
     * not enforced, but provide the basis for the result of the <code>isImageTooLarge</code>
     * method.
     */
    public static final int MAX_IMAGE_HEIGHT = 500;
    BufferedImage image = null;
    String imageID = "";
    String libraryID = "";
    boolean mouseHover = false;

    /**
     * Creates a new EmbeddedImage object.
     *
     * @param img the image to paint.
     * @param imageID the ID of the image to paint. This is used for serialisation.
     * @param parentLibraryID the ID of the library that this resource exists within
     */
    public EmbeddedImage(BufferedImage img, String imageID, String parentLibraryID) {
        super();
        if (img == null) {
            throw new NullPointerException("Image cannot be null! (EmbeddedImage constructor)");
        } else {
            image = img;
        }

        this.imageID = imageID;
        this.libraryID = parentLibraryID;
        //no matter under what LayoutManager this is used, it should be the same size as the image.
        Dimension size = new Dimension(image.getWidth(), image.getHeight());
        this.setSize(image.getWidth(), image.getHeight());
        this.setPreferredSize(size);
        this.setMaximumSize(size);
        this.setMinimumSize(size);
        this.addMouseListener(this);

        this.setToolTipText("Double click to edit");


    }

    public String getImageID() {
        return imageID;
    }

    public BufferedImage getImage() {
        return image;
    }

    public String getParentLibraryID() {
        return libraryID;
    }

    public void setParentLibraryID(String libID) {
        libraryID = libID;
    }

    public void setImageID(String imgID) {
        imageID = imgID;
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(image, 0, 0, this);

        Graphics2D g2d = (Graphics2D) g;

//---------------------used to have a 'click to edit' sign that appeared
//                     when the user hovered the mouse over the image, but I didn't
//                     like it... a tooltip is used now.
//        if (mouseHover) {
//            g2d.setPaint(new Color(ThemeConstants.backgroundUnselected.getRed(), ThemeConstants.backgroundUnselected.getGreen(), ThemeConstants.backgroundUnselected.getBlue(), 230));
//            g2d.fillRect(1, 0, 40, 20);
//            g2d.setPaint(ThemeConstants.borderUnselected);
//            g2d.setStroke(new BasicStroke(0.1f));
//            g2d.draw(new Rectangle2D.Double(1, 0, 40, 20));
//
//            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
//
//
//            g2d.setFont(new Font(this.getFont().getFamily(), Font.PLAIN, 8));
//
//            g2d.drawString("dbl click", 3, 9);
//            g2d.drawString("to edit", 3, 18);
//        }
    }

    public void mouseClicked(MouseEvent e) {
        if (e.getClickCount() == 2) {
            if (((RichTextArea) this.getParent().getParent()).isEditable() == false) {
                //if the rich text area is not editable, then we should not allow the embedded image to be editable.
                return;
            }
            ImageAcquisitionDialog imgDialog = IOManager.getImageAcquisitionDialog();
            imgDialog.setEditorDocumentImage(image);
            imgDialog.setEditorOnly(true);
            imgDialog.setUpKeyBindingsForEditorPane();
            imgDialog.setVisible(true);
            //do nothing if the user cancelled
            if (imgDialog.getAcquisitionSource() == ImageAcquisitionDialog.NONE) {
                return;
            }

            //otherwise...
            image = imgDialog.getAcquiredImage();
            Dimension size = new Dimension(image.getWidth(), image.getHeight());
            this.setSize(image.getWidth(), image.getHeight());
            this.setPreferredSize(size);
            this.setMaximumSize(size);
            this.setMinimumSize(size);

            //check if the image is too large
            if (isImageTooLarge()) {
                int resp = JOptionPane.showConfirmDialog(EmbeddedImage.this, "This image is larger than the recommended maximum size. Would you\n"
                        + "like Ingatan to shrink the image to the largest recommended size?", "Large Image", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);

                if (resp == JOptionPane.YES_OPTION) {
                    resizeTo(getMaxRecommendedSize(), true);
                }
            }

            try {
                IOManager.saveImageWithOverWrite(image, libraryID, imageID);
            } catch (IOException ex) {
                Logger.getLogger(EmbeddedImage.class.getName()).log(Level.SEVERE, "Was trying to save the image with over-write after it was editted by the user.", ex);
            }


        }
    }

    /**
     * Checks the image dimensions and returns whether either of them breach the
     * values in MAX_IMAGE_WIDTH or MAX_IMAGE_HEIGHT. The user will not be forced
     * to not use an image which is too large, but this method can be used to determine
     * whether or nto they should be warned about large images.
     * @return <code>true</code> if the image is larger than the recommended maximum size.
     */
    public boolean isImageTooLarge() {
        if ((image.getWidth() > MAX_IMAGE_WIDTH) || (image.getHeight() > MAX_IMAGE_HEIGHT)) {
            return true;
        }
        return false;
    }

    /**
     * Get the maximum recommended size for images in Ingatan.
     * @return the maximum recommended size for images in Ingatan.
     */
    public Dimension getMaxRecommendedSize() {
        return new Dimension(MAX_IMAGE_WIDTH, MAX_IMAGE_HEIGHT);
    }

    /**
     * Resizes both the component and content to the specified dimension.
     * @param d the new size of the image adn component.
     * @param respectAspectRatio if <code>false</code> the image should be resized to the dimension d, if
     * <code>true</code> the image will have its largest side resized to the corresponding dimension in d.
     */
    public void resizeTo(Dimension d, boolean respectAspectRatio) {
        BufferedImage newImg;

        if (respectAspectRatio) {
            //get the aspect ratio of the image
            double ratio = image.getWidth() / image.getHeight();

            if (image.getWidth() > image.getHeight()) {
                if (ratio > 20) ratio = 20;
                newImg = new BufferedImage((int) d.getWidth(), (int) (d.getWidth() / ratio), image.getType());
            } else {
                if (ratio < 0.05) ratio = 0.05;
                newImg = new BufferedImage((int) (d.getHeight() * ratio), (int) d.getHeight(), image.getType());
            }

        } else {
            newImg = new BufferedImage((int) d.getWidth(), (int) d.getHeight(), image.getType());
        }


        newImg.createGraphics().drawImage(image, 0, 0, (int) newImg.getWidth(), (int) newImg.getHeight(), this);
        image = newImg;
        this.setSize((int) image.getWidth(), (int) image.getHeight());
        Dimension newD = new Dimension(image.getWidth(), image.getHeight());
        this.setPreferredSize(newD);
        this.setMaximumSize(newD);
        this.setMinimumSize(newD);
    }

    public void mousePressed(MouseEvent e) {
    }

    public void mouseReleased(MouseEvent e) {
    }

    public void mouseEntered(MouseEvent e) {
        mouseHover = true;
        repaint();
    }

    public void mouseExited(MouseEvent e) {
        mouseHover = false;
        repaint();
    }
}
