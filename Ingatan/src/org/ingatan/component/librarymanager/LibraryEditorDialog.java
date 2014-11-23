/*
 * LibraryEditorDialog.java
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

import org.ingatan.ThemeConstants;
import org.ingatan.component.text.SimpleTextArea;
import org.ingatan.component.text.SimpleTextField;
import org.ingatan.io.IOManager;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

/**
 * Provides a dialog allowing the user to edit the name of the library, the description
 * of the library, and to clear the 'correctness' values for all questions that it contains.
 *
 * @author Thomas Everingham
 * @version 1.0
 */
public class LibraryEditorDialog extends JDialog {

    /**
     * Label for the library name field.
     */
    protected JLabel lblLibraryName = new JLabel("Library name:");
    /**
     * Label fro the library description field.
     */
    protected JLabel lblLibraryDesc = new JLabel("Library description:");
    /**
     * Library name edit field.
     */
    protected SimpleTextField libraryName = new SimpleTextField();
    /**
     * Library description edit field.
     */
    protected SimpleTextArea libraryDescription = new SimpleTextArea();
    /**
     * Checkbox for clearing the correctness values for all questions within this library.
     */
    protected JCheckBox checkboxClearStatistics = new JCheckBox("Clear answer statistics for all questions");
    /**
     * Button to accept changes.
     */
    protected JButton btnOkay = new JButton(new ProceedAction());
    /**
     * Button to cancel changes.
     */
    protected JButton btnCancel = new JButton(new CancelAction());
    /**
     * Content pane of the dialog.
     */
    protected JPanel contentPane = new JPanel();
    /**
     * Group to add the new library to, or null if the library should not be added to any particular group.
     */
    protected String groupName;

    /**
     * Creates a new <code>LibraryEditorDialog</code>.
     * @param parent the parent window for this dialog.
     * @param newLibrary whether or not this dialog is being used to create a new library, false
     *        if this dialog is being used to edit a library.
     * @param groupName if creating a new library, then it will be added to the specified group. Put this
     * parameter as <code>null</code> if this is not desired.
     */
    public LibraryEditorDialog(JFrame parent, boolean newLibrary, String groupName) {
        super(parent);
        this.setModal(true);
        this.setIconImage(IOManager.windowIcon);
        this.setContentPane(contentPane);
        this.groupName = groupName;


        contentPane.setLayout(new BoxLayout(contentPane, BoxLayout.Y_AXIS));

        setUpGUI();
        if (newLibrary) {
            checkboxClearStatistics.setVisible(false);
        }

        libraryName.addKeyListener(new EnterKeyListener());
        libraryDescription.addKeyListener(new EnterKeyListener());
        checkboxClearStatistics.addKeyListener(new EnterKeyListener());

        libraryName.requestFocus();
        libraryName.selectAll();

        this.setPreferredSize(new Dimension(220, 325));
        this.setMinimumSize(new Dimension(220, 325));
        this.setMaximumSize(new Dimension(220, 325));

        this.setResizable(false);
    }

    private void setUpGUI() {
        contentPane.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        libraryName.setMaximumSize(new Dimension(200, 25));
        lblLibraryName.setAlignmentX(LEFT_ALIGNMENT);
        lblLibraryName.setHorizontalAlignment(SwingConstants.LEFT);
        libraryName.setBorder(BorderFactory.createLineBorder(ThemeConstants.borderUnselected));
        lblLibraryDesc.setHorizontalAlignment(SwingConstants.LEFT);
        lblLibraryDesc.setAlignmentX(LEFT_ALIGNMENT);
        libraryDescription.setBorder(BorderFactory.createLineBorder(ThemeConstants.borderUnselected));
        libraryName.setAlignmentX(LEFT_ALIGNMENT);
        libraryDescription.setMaximumSize(new Dimension(200, 125));
        libraryDescription.setAlignmentX(LEFT_ALIGNMENT);
        checkboxClearStatistics.setFont(ThemeConstants.niceFont);
        checkboxClearStatistics.setAlignmentX(LEFT_ALIGNMENT);


        contentPane.add(lblLibraryName);
        contentPane.add(Box.createVerticalStrut(3));
        contentPane.add(libraryName);
        contentPane.add(Box.createVerticalStrut(10));
        contentPane.add(lblLibraryDesc);
        contentPane.add(Box.createVerticalStrut(3));
        contentPane.add(libraryDescription);
        contentPane.add(Box.createVerticalStrut(8));
        contentPane.add(checkboxClearStatistics);
        contentPane.add(Box.createVerticalStrut(20));
        Box horiz = Box.createHorizontalBox();
        horiz.setAlignmentX(LEFT_ALIGNMENT);
        horiz.setMaximumSize(new Dimension(200, 40));
        horiz.add(btnOkay);
        horiz.add(Box.createHorizontalStrut(5));
        horiz.add(btnCancel);
        contentPane.add(horiz);

        this.pack();

    }

    /**
     * Sets the library name text.
     * @param text the new library name text.
     */
    public void setLibraryNameText(String text) {
        libraryName.setText(text);
    }

    /**
     * Sets the library description text.
     * @param text the library description text.
     */
    public void setLibraryDescriptionText(String text) {
        libraryDescription.setText(text);
    }

    /**
     * Gets the library name text.
     * @return the library name text.
     */
    public String getLibraryNameText() {
        return libraryName.getText();
    }

    /**
     * Gets the library description text.
     * @return the library description text.
     */
    public String getLibraryDescriptionText() {
        return libraryDescription.getText();
    }

    /**
     * Gets whether or not the statistics of all questions contained by this library
     * should be cleared.
     * @return <code>true</code> if the statistics should be cleared.
     */
    public boolean getClearStatistics() {
        return checkboxClearStatistics.isSelected();
    }

    private class ProceedAction extends AbstractAction {

        public ProceedAction() {
            super("Okay");
        }

        public void actionPerformed(ActionEvent e) {
            if (checkboxClearStatistics.isVisible() == false) {
                //we are creating a new library
                if (libraryName.getText().trim().isEmpty()) {
                    JOptionPane.showMessageDialog(LibraryEditorDialog.this, "The library must have a name.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                LibraryEditorDialog.this.setEnabled(false);
                try {
                    IOManager.createLibrary(libraryName.getText(), libraryDescription.getText(), groupName);
                } catch (IOException ex) {
                    JOptionPane.showMessageDialog(LibraryEditorDialog.this, "while attempting to create a new library with name: " + libraryName.getText(), "IO Exception", JOptionPane.ERROR_MESSAGE);
                    Logger.getLogger(LibraryEditorDialog.class.getName()).log(Level.SEVERE, null, ex);
                }
                LibraryEditorDialog.this.setEnabled(true);
                LibraryEditorDialog.this.setVisible(false);
            } else {
                LibraryEditorDialog.this.setVisible(false);
                //library edits are saved back in the LibraryManagerWindow code
            }
        }
    }

    private class CancelAction extends AbstractAction {

        public CancelAction() {
            super("Cancel");
        }

        public void actionPerformed(ActionEvent e) {
            LibraryEditorDialog.this.setVisible(false);
        }
    }

    private class EnterKeyListener implements KeyListener {

        public void keyTyped(KeyEvent ke) {
        }

        public void keyPressed(KeyEvent ke) {
            if (ke.getKeyCode() == KeyEvent.VK_ENTER) {
                if (ke.getSource().equals(libraryName)) {
                    libraryDescription.requestFocus();
                    libraryDescription.selectAll();
                } else if (ke.getSource().equals(libraryDescription)) {
                    if (checkboxClearStatistics.isVisible()) {
                        checkboxClearStatistics.requestFocus();
                    } else {
                        new ProceedAction().actionPerformed(null);
                    }
                } else if (ke.getSource().equals(checkboxClearStatistics)) {
                    new ProceedAction().actionPerformed(null);
                }
            }
        }

        public void keyReleased(KeyEvent ke) {
        }
    }
}
