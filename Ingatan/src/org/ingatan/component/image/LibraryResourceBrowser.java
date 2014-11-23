/*
 * LibraryResourceBrowser.java
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
import org.ingatan.data.Library;
import org.ingatan.io.IOManager;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JComboBox;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

/**
 * Displays all image files contained by the selected library.
 *
 * @author Thomas Everingham
 * @version 1.0
 */
public class LibraryResourceBrowser extends JPanel {

    /**
     * Displays all images contained within the currently selected library.
     */
    protected ThumbnailPane thumbnailPane;
    /**
     * Scroller for the thumbnail pane.
     */
    protected JScrollPane scrollThumbnailPane;
    /**
     * Drop down list of all libraries, as obtained from the <code>IOManager</code>.
     */
    protected JComboBox comboLibraryList;
    /**
     * The currently selected library (a library should only exist as a <code>Library</code>
     * object if it must be read or written to - the name of a library can simply be taken from
     * the IOManager).
     */
    protected Library currentLib;
    /**
     * Preview of the currently selected image.
     */
    private PreviewPanel preview;

    /**
     * Creates a new <code>LibraryResourceBrowser</code>, displaying <code>libraryID</code>
     * as the default library (as well as all images within that library).
     * @param libraryID the library that is opened first.
     */
    public LibraryResourceBrowser(String libraryID) {

        //initialise components
        comboLibraryList = new JComboBox(IOManager.getLibraryNames());
        comboLibraryList.addActionListener(new LibraryComboActionListener());

        thumbnailPane = new ThumbnailPane(64, 5);
        thumbnailPane.addThumbnailMouseListener(new ThumbnailMouseListener());

        scrollThumbnailPane = new JScrollPane(thumbnailPane);
        preview = new PreviewPanel();
        if (libraryID.isEmpty() == false) {
            try {
                //load the default library.
                this.currentLib = IOManager.loadLibrary(libraryID);
                loadThumbnailPane();
            } catch (IOException ex) {
                Logger.getLogger(LibraryResourceBrowser.class.getName()).log(Level.SEVERE, "While loading library with ID: " + libraryID + " and then loading the thumbnail pane.", ex);
            }
        }

        //layout
        this.setLayout(new BoxLayout(this, BoxLayout.X_AXIS));

        Box colBox = Box.createVerticalBox();

        comboLibraryList.setPreferredSize(new Dimension(150, 25));
        comboLibraryList.setMaximumSize(new Dimension(700, 25));
        comboLibraryList.setFont(ThemeConstants.niceFont);
        colBox.add(comboLibraryList);

        preview.setBorder(BorderFactory.createLineBorder(ThemeConstants.borderUnselected));
        colBox.add(preview);

        this.add(colBox);

        scrollThumbnailPane.setBorder(BorderFactory.createEmptyBorder());
        this.add(scrollThumbnailPane);

    }

    /**
     * Loads thumbnails from the current library. This is done by loading each image
     * within the library, creating a 64x64 copy, and then flushing the original.
     * This saves on memory.
     * @throws IOException if there is any problem loading the image files.
     */
    protected void loadThumbnailPane() throws IOException {
        Object[] objImages = currentLib.getImages().values().toArray();
        BufferedImage temp;
        BufferedImage thumb;

        thumbnailPane.clearThumbnails();

        for (int i = 0; i < objImages.length; i++) {
            temp = ImageIO.read((File) objImages[i]);
            thumb = new BufferedImage(64, 64, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g = thumb.createGraphics();
            g.drawImage(temp, 0, 0, 64, 64, null);
            temp.flush();
            thumbnailPane.addThumbnail(new ImageIcon(thumb));
        }

        thumbnailPane.revalidate();
    }

    /**
     * Changes the current library to <code>libraryID</code>. Loads and displays the
     * resources located within that library.
     * @param libraryID the library to display.
     * @throws IOException if there is a problem accessing the library.
     */
    protected void changeLibrary(String libraryID) throws IOException {
        //if the library is already loaded, then do nothing
        if (libraryID.equals(currentLib.getId())) {
            return;
        }

        //ask the IOManager to load the new library
        currentLib = IOManager.loadLibrary(libraryID);
        loadThumbnailPane();
    }

    /**
     * Gets the currently selected image ID. This is the ID, unique within at least the
     * library, that acts as a reference to that image resource.
     * @return the currently selected image's ID.
     */
    public String getSelectedImageID() {
        return (String) currentLib.getImages().keySet().toArray()[thumbnailPane.getSelectedIndex()];
    }

    /**
     * Gets the ID of the currently selected library. The ID is the unique identifier
     * for the currently selected library.
     * @return the ID of the currently selected library.
     */
    public String getSelectedLibraryID() {
        //the library list combo box is populated by IOManager.getLibraryNames(),
        //and the IDs for these libraries correspond to the IOManager.getLibraryIDs
        //array.
        return IOManager.getLibraryIDs()[comboLibraryList.getSelectedIndex()];
    }

    /**
     * Listens for changes on the library list so that the displayed images may
     * be updated.
     */
    public class LibraryComboActionListener implements ActionListener {

        public void actionPerformed(ActionEvent e) {
            //get library ID; selected index corresponds to correct ID in the IOManager's ID array
            String libID = IOManager.getLibraryIDs()[comboLibraryList.getSelectedIndex()];
            try {
                //call change library
                changeLibrary(libID);
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(LibraryResourceBrowser.this, "There was a problem loading the selected library (ID=" + libID + ").", "Error loading library", JOptionPane.ERROR_MESSAGE);
            }
        }
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


            //try to paint a preview
            try {
                //get the image
                preview.setImage(IOManager.loadImage(currentLib.getId(), (String) currentLib.getImages().keySet().toArray()[thumbnailPane.getSelectedIndex()]));
            } catch (IOException ex) {
                preview.setImage(null);
                Logger.getLogger(LibraryResourceBrowser.class.getName()).log(Level.SEVERE, "There was a problem loading an image for the preview panel in the ImageAcquisitionDialog,\n" +
                        "in the LibraryResourceBrowser, using IOManager.loadImage().\nImageID=" + currentLib.getImages().keySet().toArray()[thumbnailPane.getSelectedIndex()] +
                        " , libraryID=" + currentLib.getId() + ".", ex);
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
     * Paints the set image onto itself.
     */
    private class PreviewPanel extends JPanel {

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

            if (preview == null) return;

            //draw the image at full size.
            g.drawImage(preview, 0, 0, this);
        }
    }
}
