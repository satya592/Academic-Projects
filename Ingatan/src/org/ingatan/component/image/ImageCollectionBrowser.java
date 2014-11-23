/*
 * ImageCollectionBrowser.java
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

package org.ingatan.component.image;

import org.ingatan.ThemeConstants;
import org.ingatan.io.IOManager;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

/**
 * This is essentially a clip-art library, with the user able to choose from different
 * categories. Images are loaded from the collections folder, with each sub-folder treated
 * as a category and the images within those sub-folders treated as the images belonging
 * to that category.
 *
 * @author Thomas Everingham
 * @version 1.0
 */
public class ImageCollectionBrowser extends JPanel {

    /**
     * Displays all images in the selected category.
     */
    protected ThumbnailPane thumbnailPane;
    /**
     * Scroll pane for the thumbnail pane displaying images
     * in the selected category.
     */
    protected JScrollPane scrollThumbnailPane;
    /**
     * Drop down list of categories.
     */
    protected JComboBox comboCategories;
    /**
     * The array of resource paths of images that belong in the current category.
     */
    protected Category currentCollection;
    /**
     * A preview is loaded for the currently selected image.
     */
    protected PreviewPanel preview;

    /**
     * Creates a new <code>ImageCollectionBrowser</code>.
     */
    public ImageCollectionBrowser() {

        //instantiate components.
        comboCategories = new JComboBox();
        thumbnailPane = new ThumbnailPane(64, 5);
        thumbnailPane.addThumbnailMouseListener(new ThumbnailMouseListener());

        scrollThumbnailPane = new JScrollPane(thumbnailPane);
        preview = new PreviewPanel();

        //Layout
        this.setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
        Box colBox = Box.createVerticalBox();
        //Add categories to the list box
        comboCategories.addActionListener(new CategoryComboActionListener());

        comboCategories.setPreferredSize(new Dimension(150, 25));
        comboCategories.setMaximumSize(new Dimension(700, 25));
        comboCategories.setFont(ThemeConstants.niceFont);
        colBox.add(comboCategories);

        //pretty border for the preview window.
        preview.setBorder(BorderFactory.createLineBorder(ThemeConstants.borderUnselected));
        colBox.add(preview);

        this.add(colBox);

        scrollThumbnailPane.setBorder(BorderFactory.createEmptyBorder());
        this.add(scrollThumbnailPane);

        //looks in the collections folder and creates a category for every folder found, with
        //the images within that folder being used as the images for the category.
        generateCategories();

        if (comboCategories.getItemCount() > 0) {
            currentCollection = (Category) comboCategories.getItemAt(0);
            //load a current collection.
            try {
                loadThumbnailPane();
            } catch (IOException ex) {
                Logger.getLogger(ImageCollectionBrowser.class.getName()).log(Level.SEVERE, "While loading the thumbnail pane for the first category.", ex);
            }
        }
    }

    /**
     * Loads the thumbnail pane with all images in the current collection. Images are
     * loaded using the class loader and the file locations given by the String array that
     * corresponds to the current collection.
     * @throws IOException if there is a problem loading any resource.
     */
    protected void loadThumbnailPane() throws IOException {
        BufferedImage temp;
        BufferedImage thumb;

        thumbnailPane.clearThumbnails();
        //this ensures that all thumbnails are cleared if no new ones are added
        thumbnailPane.setThumbnails(new ImageIcon[0]);

        for (int i = 0; i < currentCollection.getImageFilenames().length; i++) {
            temp = ImageIO.read(new File(new File(IOManager.getCollectionsPath(), currentCollection.getName()), currentCollection.getImageFilenames()[i]));
            thumb = new BufferedImage(64, 64, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g = thumb.createGraphics();
            g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
            g.drawImage(temp, AffineTransform.getScaleInstance(64.0 / temp.getWidth(), 64.0 / temp.getHeight()), this);
            thumbnailPane.addThumbnail(new ImageIcon(thumb));
            temp.flush();
        }
        thumbnailPane.validate();
        thumbnailPane.repaint();
    }

    /**
     * Listens for changes in thumbnail selection, sets clicked thumbnails to
     * selected, and draws the preview.
     */
    private class ThumbnailMouseListener implements MouseListener {

        public void mouseClicked(MouseEvent e) {
            Thumbnail t = null;
            if (e.getComponent() instanceof Thumbnail) {
                t = (Thumbnail) e.getComponent();
                thumbnailPane.setSelectedIndex(t.getIndex());
            } else {
                return;
            }

            //set the preview image
            try {
                preview.setImage(ImageIO.read(new File(new File(IOManager.getCollectionsPath(), currentCollection.getName()), getSelectedImageName())));
            } catch (IOException ex) {
                Logger.getLogger(ImageCollectionBrowser.class.getName()).log(Level.SEVERE, "Problem loading preview panel image using ImageIO.read. "
                        + "File name=" + IOManager.getCollectionsPath() + currentCollection.getName() + getSelectedImageName(), ex);
            }

            preview.repaint();
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
     * Get whatever image is currently selected by the user.
     * @return whatever image is currently selected by the user.
     */
    protected BufferedImage getSelectedImage() {
        try {
            return ImageIO.read(new File(new File(IOManager.getCollectionsPath(), currentCollection.getName()), getSelectedImageName()));
        } catch (IOException ex) {
            Logger.getLogger(ImageCollectionBrowser.class.getName()).log(Level.SEVERE, "Could not load the currently selected collections image. "
                    + "File name=" + IOManager.getCollectionsPath() + currentCollection.getName() + getSelectedImageName(), ex);
            return null;
        }
    }

    /**
     * Gets the file name of the selected image, without the path, but with the extension.
     * This method is intended for providing some basis for an image ID to be generated
     * when this image is saved to a library.
     * @return the file name of the selected image, without the path, with the extension.
     */
    protected String getSelectedImageName() {
        return currentCollection.getImageFilenames()[thumbnailPane.getSelectedIndex()];
    }

    /**
     * Looks in the categories folder and creates a category for any folders contained within. The
     * images within those folders are then taken as the images for that category.
     */
    private void generateCategories() {
        File collectionsPath = new File(IOManager.getCollectionsPath());
        //if the collections path exists
        if (collectionsPath.exists()) {
            //list the files
            File[] categories = collectionsPath.listFiles();
            //for each folder, create a new category
            for (int i = 0; i < categories.length; i++) {
                if (categories[i].isDirectory()) {
                    comboCategories.addItem(new Category(categories[i].getName(), categories[i].list(new fileOnlyFilter())));
                }
            }
        } else {
            Logger.getLogger(ImageCollectionBrowser.class.getName()).log(Level.WARNING, "Collections path does not exist, should have been created at IOManager initialisation.");
        }
    }

    /**
     * Filename filter that rejects any directories.
     */
    private class fileOnlyFilter implements FilenameFilter {

        public boolean accept(File dir, String name) {
            File f = new File(dir, name);
            if (f.isDirectory()) {
                return false;
            } else {
                return true;
            }
        }
    }

    /**
     * Listens for any change of category list selection so that the thumbnail pane may be updated.
     */
    protected class CategoryComboActionListener implements ActionListener {

        public void actionPerformed(ActionEvent e) {
            currentCollection = (Category) comboCategories.getSelectedItem();

            try {
                loadThumbnailPane();
            } catch (IOException ex) {
                Logger.getLogger(ImageCollectionBrowser.class.getName()).log(Level.SEVERE, "while loading the thumbnail pane due to change of category (combolist)", ex);
            }
        }
    }

    /**
     * Encapsulates the name of the category and the filenames to the images that
     * belong to that category.
     */
    public class Category {

        /**
         * Name of this category.
         */
        private String name;
        /**
         * Filenames for the images in this category.
         */
        private String[] images;

        /**
         * Creates a new Category.
         * @param name the name of this category.
         * @param imageFilenames the filenames of the images that belong in this category.
         */
        public Category(String name, String[] imageFilenames) {
            this.name = name;
            this.images = imageFilenames;
        }

        /**
         * Get the name of this category. This is also the name of the parent
         * folder in which the images exist.
         * @return the name of this category.
         */
        public String getName() {
            return name;
        }

        /**
         * Gets the filenames of the images in this category.
         * @return the filenames of the images in this category.
         */
        public String[] getImageFilenames() {
            return images;
        }

        @Override
        public String toString() {
            return name;
        }
    }

    /**
     * Paints the set image onto itself.
     */
    public class PreviewPanel extends JPanel {

        BufferedImage preview = null;

        public PreviewPanel() {
            super();
        }

        public void clearImage() {
            preview = null;
            this.repaint();
        }

        public void setImage(BufferedImage newImg) {
            preview = newImg;
        }

        @Override
        public void paintComponent(Graphics g) {
            super.paintComponent(g);

            if (preview == null) {
                return;
            }

            //draw the image at full size.
            g.drawImage(preview, 0, 0, this);
        }
    }
}
