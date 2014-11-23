/*
 * ExportDialog.java
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

package org.ingatan.component.librarymanager;

import org.ingatan.io.IOManager;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

/**
 * This dialog allows the user to select multiple libraries from any group, and export them
 * to a specified location on the hard disk.
 *
 * @author Thomas Everingham
 * @version 1.0
 */
public class ExportDialog extends JDialog {

    /**
     * Allows the user to select multiple libraries.
     */
    protected MultipleLibrarySelector libSelector = new MultipleLibrarySelector();
    /**
     * Button used to show to save dialog where the user may specify the location to export the libraries.
     */
    protected JButton btnExport = new JButton("Export");
    /**
     * The content pane for this dialog.
     */
    protected JPanel contentPane = new JPanel();

    /**
     * Creates a new <code>ExportDialog</code>.
     * @param parent the owner of the dialog.
     */
    public ExportDialog(JFrame parent) {
        super(parent);
        this.setTitle("Export Libraries");
        this.setIconImage(IOManager.windowIcon);
        this.setModal(true);
        this.setContentPane(contentPane);
        contentPane.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

        contentPane.setLayout(new BoxLayout(contentPane, BoxLayout.Y_AXIS));
        contentPane.add(libSelector);
        libSelector.setAlignmentX(RIGHT_ALIGNMENT);
        btnExport.setAlignmentX(CENTER_ALIGNMENT);
        btnExport.setToolTipText("Export the selected libraries.");
        contentPane.add(btnExport);
        btnExport.setAction(new ExportAction());

        this.setPreferredSize(new Dimension(515, 270));
        this.setMinimumSize(new Dimension(515, 270));
        this.setMaximumSize(new Dimension(515, 270));
        this.setResizable(false);
    }

    private class ExportAction extends AbstractAction {

        public ExportAction() {
            super("Export");
        }

        public void actionPerformed(ActionEvent ae) {
            String[] libraryIDs = libSelector.getSelectedLibraryIDs();

            JFileChooser choose = new JFileChooser();
            choose.setDialogTitle("Export Libraries to...");
            choose.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            choose.setAcceptAllFileFilterUsed(false);
            choose.showSaveDialog(ExportDialog.this);
           
            File dest = choose.getSelectedFile();

            for (int i = 0; i < libraryIDs.length; i++) {
                try {
                    IOManager.copy(IOManager.getLibraryPath() + libraryIDs[i], dest.getPath());
                } catch (IOException ex) {
                    JOptionPane.showMessageDialog(ExportDialog.this, "IOException while attempting to export the library with ID: " + libraryIDs[i] + ".", "IO Exception", JOptionPane.ERROR_MESSAGE);
                    Logger.getLogger(ExportDialog.class.getName()).log(Level.SEVERE, null, ex);
                }
            }

            ExportDialog.this.setVisible(false);
        }
    }
}
