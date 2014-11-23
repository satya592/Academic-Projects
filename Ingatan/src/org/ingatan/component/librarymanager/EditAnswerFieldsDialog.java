/*
 * EditAnswerFieldsDialog.java
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
import org.ingatan.component.PaintedJPanel;
import org.ingatan.component.answerfield.IAnswerField;
import org.ingatan.io.IOManager;
import org.ingatan.io.ParserWriter;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.filechooser.FileFilter;

/**
 * This class allows the user to add and remove answer fields, as well as set the default
 * values for answer fields. Answer fields that have already been added to Ingatan appear
 * as instances in a JScrollPane, and the user may edit their default values through whatever
 * interface the author has designed. A 'set as default' button then allows the user to set these new
 * values as the default for that answer field. A checkbox exists next to each answer field so that
 * fields to remove may be selected. These can then be removed using the 'remove selected answer fields' button.
 * Answer fields can be added by selecting the 'add answer field button' and then selecting a file from the hard drive.
 * A breif description of what answer fields are exists at the top of the dialog, as well as a warning to only
 * add answer fields from trusted sources, as they can run any code.
 *
 * @author Thomas Everingham
 * @version 1.0
 */
public class EditAnswerFieldsDialog extends JDialog {

    /**
     * Content of the answer fields scroller.
     */
    protected JPanel scrollerContent = new JPanel();
    /**
     * Scroller for all answer fields.
     */
    protected JScrollPane scrollerAnswerFields = new JScrollPane(scrollerContent);
    /**
     * Button for adding an answer field to Ingatan.
     */
    protected JButton btnAddField = new JButton("Import new");
    /**
     * Button for removing all selected Answer Fields (answer fields can be selected using checkbox).
     */
    protected JButton btnRemoveFields = new JButton("Delete selected");
    /**
     * Button for displaying a how-to for creating answer fields.
     */
    protected JButton btnHowTo = new JButton();
    /**
     * Content pane for this dialog.
     */
    protected JPanel contentPane = new JPanel();
    /**
     * Fired whenever a checkbox is clicked.
     */
    protected AbstractAction checkBoxAction = new CheckBoxAction();
    /**
     * All answer field entries that have been added when the list is built.
     */
    ArrayList<AnswerFieldEntry> ansFieldEntries = new ArrayList<AnswerFieldEntry>();

    public EditAnswerFieldsDialog() {
        this.setContentPane(contentPane);
        this.setModal(true);
        this.setMinimumSize(new Dimension(700, 600));
        this.setTitle("Edit Answer Fields");
        this.setIconImage(IOManager.windowIcon);


        contentPane.setBorder(BorderFactory.createEmptyBorder(0, 8, 8, 8));

        btnAddField.setAction(new AddAnswerFieldAction());
        btnRemoveFields.setAction(new RemoveAnswerFieldsAction());
        btnHowTo.setAction(new HowToAction());

        btnAddField.setFont(ThemeConstants.niceFont);
        btnAddField.setMargin(new Insets(1, 1, 1, 1));

        btnHowTo.setFont(ThemeConstants.niceFont);
        btnHowTo.setMargin(new Insets(1, 1, 1, 1));

        btnRemoveFields.setFont(ThemeConstants.niceFont);
        btnRemoveFields.setMargin(new Insets(1, 1, 1, 1));

        contentPane.setLayout(new BoxLayout(contentPane, BoxLayout.Y_AXIS));
        JLabel info = new JLabel("<html><h3>Answer Fields</h3>Answer fields are plug-ins that allow you to extend Ingatan to support "
                + "question types that are not provided by default. They are simply Java classes that follow the IAnswerField "
                + "interface, and as such may run any code. Only import answer fields that you trust, and preferrably when the source code is available.");
        info.setMaximumSize(new Dimension(700, 40));
        info.setFont(ThemeConstants.niceFont);
        info.setAlignmentX(LEFT_ALIGNMENT);
        contentPane.add(info);
        contentPane.add(Box.createVerticalStrut(10));
        scrollerAnswerFields.setAlignmentX(LEFT_ALIGNMENT);
        contentPane.add(scrollerAnswerFields);


        Box horiz = Box.createHorizontalBox();
        horiz.setMaximumSize(new Dimension(390, 40));
        horiz.setAlignmentX(LEFT_ALIGNMENT);
        horiz.add(btnAddField);
        horiz.add(Box.createHorizontalStrut(5));
        horiz.add(btnRemoveFields);
        horiz.add(Box.createHorizontalStrut(5));
        horiz.add(btnHowTo);
        contentPane.add(Box.createVerticalStrut(20));
        contentPane.add(horiz);

        scrollerContent.setLayout(new BoxLayout(scrollerContent, BoxLayout.Y_AXIS));

        buildList();
    }

    /**
     * Builds the list based on the answer fields class ID list taken from the IOManager.
     * Rebuilds the ansFieldEntries array list as well.
     */
    public void buildList() {
        Enumeration<String> e = IOManager.getAnswerFieldClassIDs();
        String curID;

        scrollerContent.removeAll();
        ansFieldEntries.clear();

        AnswerFieldEntry newEntry;
        while (e.hasMoreElements()) {
            curID = e.nextElement();
            try {
                IAnswerField ansField = (IAnswerField) IOManager.getAnswerFieldClass(curID).newInstance();
                ansField.readInXML(IOManager.getAnswerFieldDefault(curID));
                ansField.setContext(true);

                //if the package name is not an empty string, then it is a default answer field, and we wish to
                //hence disallow selection of this AnswerFieldEntry.
                newEntry = new AnswerFieldEntry(ansField, ansField.getClass().getPackage() == null);
                ansFieldEntries.add(newEntry);
                scrollerContent.add(newEntry);

                scrollerContent.add(Box.createVerticalStrut(25));
            } catch (InstantiationException ex) {
                JOptionPane.showMessageDialog(EditAnswerFieldsDialog.this, "An InstantiationException was thrown "
                        + "while attempting to instantiate the answer field "
                        + "with ID: " + curID + ".\n\nThis Answer Field will be skipped.", "Instantiation Exception", JOptionPane.ERROR_MESSAGE);
                Logger.getLogger(EditAnswerFieldsDialog.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IllegalAccessException ex) {
                JOptionPane.showMessageDialog(EditAnswerFieldsDialog.this, "An IllegalAccessException was thrown "
                        + "while attempting to instantiate the answer field "
                        + "with ID: " + curID + ".\n\nThis Answer Field will be skipped.", "Illegal Access Exception", JOptionPane.ERROR_MESSAGE);
                Logger.getLogger(EditAnswerFieldsDialog.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    /**
     * Action that occurs when the 'import answer field' button is pressed.
     */
    private class AddAnswerFieldAction extends AbstractAction {

        public AddAnswerFieldAction() {
            super("Import Answer Field");
        }

        public void actionPerformed(ActionEvent e) {
            JFileChooser chooser = new JFileChooser();
            chooser.setDialogTitle("Import Answer Field...");
            chooser.setFileFilter(new ClassFilter());
            chooser.setApproveButtonText("Import");
            if (chooser.showOpenDialog(EditAnswerFieldsDialog.this) == JFileChooser.APPROVE_OPTION) {
                //get the class to import
                File f = chooser.getSelectedFile();

                //check that it is unique
                if (new File(IOManager.getAnswerFieldPath() + f.getName()).exists()) {
                    JOptionPane.showMessageDialog(EditAnswerFieldsDialog.this, "The selected class (" + f.getName() + ") already exists as an imported class. No \n"
                            + "two answer fields may share the same name.", "Error: Cannot Import", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                int deleteResponse = JOptionPane.showConfirmDialog(EditAnswerFieldsDialog.this, "Delete the original file after copying it to the .ingatan "
                        + "directory? Choose no if unsure.", "Delete original?", JOptionPane.YES_NO_OPTION);

                //try to copy the class to the .ingatan directory
                try {
                    IOManager.copy(f.getAbsolutePath(), IOManager.getAnswerFieldPath() + f.getName());
                } catch (IOException ex) {
                    JOptionPane.showMessageDialog(EditAnswerFieldsDialog.this, "There was an IOException while attempting to copy the \n"
                            + "class from '" + f.getAbsolutePath() + "'. Could not import the answer field.", "Error: Cannot Import", JOptionPane.ERROR_MESSAGE);
                    Logger.getLogger(EditAnswerFieldsDialog.class.getName()).log(Level.SEVERE, "While trying copy an answer field class to the answer fields directory.\n"
                            + "source = " + f.getAbsolutePath() + "\n"
                            + "destination = " + IOManager.getAnswerFieldPath() + f.getName() + "\n", e);
                    return;
                }

                //try to delete this original if required
                if (deleteResponse == JOptionPane.YES_OPTION) {
                    if (f.delete() == false) {
                        JOptionPane.showMessageDialog(EditAnswerFieldsDialog.this, "Ingatan tried to delete the original file after copying it, but could not.\n"
                                + "This may be due to lack of write permission at the source directory.", "Error deleting file", JOptionPane.ERROR_MESSAGE);
                    }
                }

                //try to load the class (the IOManager has a URLClassLoader that is set to load classes from the answer field folder
                Class newClass;
                try {
                    newClass = IOManager.getUrlClassLoader().loadClass(f.getName().replace(".class", ""));
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(EditAnswerFieldsDialog.this, "Could not load the newly imported answer field. Answer fields must: \n"
                            + "    -Follow the IAnswerField interface\n    -Extend JComponent\n    -Not belong to any package\n    -Have an empty constructor available\n"
                            + "The answer field has not been added.",
                            "Error: Cannot Load Answer Field Class", JOptionPane.ERROR_MESSAGE);

                    int respCopyDel;
                    if (deleteResponse == JOptionPane.YES_OPTION) { //original was deleted
                        respCopyDel = JOptionPane.showConfirmDialog(EditAnswerFieldsDialog.this, "You chose to delete the original. "
                                + "Would you like Ingatan to delete the copy as well, what with it causing this error? \n \n"
                                + "If you choose no, it will be left in the Ingatan directory at: '" + IOManager.getAnswerFieldPath() + "'", "Delete original?", JOptionPane.YES_NO_OPTION);

                    } else {
                        respCopyDel = JOptionPane.YES_OPTION;
                    }

                    if (respCopyDel == JOptionPane.YES_OPTION) {
                        File fdel = new File(IOManager.getAnswerFieldPath() + f.getName());
                        fdel.delete();
                    }

                    Logger.getLogger(LibraryManagerWindow.class.getName()).log(Level.SEVERE, "Couldn't load the answer field class with name: " + f.getName(), e);
                    return;
                } catch (Error er) {
                    JOptionPane.showMessageDialog(EditAnswerFieldsDialog.this, "Could not load the newly imported answer field. Answer fields must: \n"
                            + "    -Follow the IAnswerField interface\n    -Extend JComponent\n    -Not belong to any package\n    -Have an empty constructor available\n"
                            + "The answer field has not been added.",
                            "Error: Cannot Load Answer Field Class", JOptionPane.ERROR_MESSAGE);
                    Logger.getLogger(LibraryManagerWindow.class.getName()).log(Level.SEVERE, "Couldn't load the answer field class with name: " + f.getName(), e);

                    int respCopyDel;
                    if (deleteResponse == JOptionPane.YES_OPTION) { //original was deleted
                        respCopyDel = JOptionPane.showConfirmDialog(EditAnswerFieldsDialog.this, "You chose to delete the original. "
                                + "Would you like Ingatan to delete the copy as well, what with it causing this error? \n \n"
                                + "If you choose no, it will be left in the Ingatan directory at: '" + IOManager.getAnswerFieldPath() + "'", "Delete original?", JOptionPane.YES_NO_OPTION);

                    } else {
                        respCopyDel = JOptionPane.YES_OPTION;
                    }

                    if (respCopyDel == JOptionPane.YES_OPTION) {
                        File fdel = new File(IOManager.getAnswerFieldPath() + f.getName());
                        fdel.delete();
                    }

                    return;
                }

                IOManager.getAnswerFieldsFile().getAnswerFields().put(f.getName().replace(".class", ""), newClass);
                IOManager.getAnswerFieldsFile().getAnswerFieldDefaults().put(f.getName().replace(".class", ""), "");
                ParserWriter.writeAnswerFieldFile(IOManager.getAnswerFieldsFile());

                buildList();
                scrollerContent.validate();
                EditAnswerFieldsDialog.this.repaint();
            }
        }

        private class ClassFilter extends FileFilter {

            @Override
            public boolean accept(File f) {
                if ((f.getName().endsWith(".class")) || (f.isDirectory())) {
                    return true;
                } else {
                    return false;
                }
            }

            @Override
            public String getDescription() {
                return "Java Class (.class)";
            }
        }
    }

    /**
     * Action that occurs when the 'remove selected fields' button is pressed.
     */
    private class RemoveAnswerFieldsAction extends AbstractAction {

        public RemoveAnswerFieldsAction() {
            super("Remove Selected Fields");
        }

        public void actionPerformed(ActionEvent e) {
            int resp = JOptionPane.showConfirmDialog(EditAnswerFieldsDialog.this, "Are you sure you wish to remove all selected answer fields? "
                    + "This change will be permanent.", "Confirm Removal", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);

            if (resp == JOptionPane.NO_OPTION) {
                return;
            }

            Iterator<AnswerFieldEntry> iterate = ansFieldEntries.iterator();
            AnswerFieldEntry curEntry;
            //put any entries to remove in the following list.
            ArrayList<AnswerFieldEntry> toRemove = new ArrayList<AnswerFieldEntry>();

            //if none were removed, tell the user that none were selected.
            boolean anyRemoved = false;

            //iterate all of the answer field entries in the list
            while (iterate.hasNext()) {
                curEntry = iterate.next();

                //if selected, it should be removed.
                if (curEntry.isSelected()) {
                    anyRemoved = true;
                    scrollerContent.remove(curEntry);
                    toRemove.add(curEntry);
                    IOManager.getAnswerFieldsFile().getAnswerFieldDefaults().remove(curEntry.getClassName());
                    IOManager.getAnswerFieldsFile().getAnswerFields().remove(curEntry.getClassName());
                    new File(IOManager.getAnswerFieldPath() + curEntry.getClassName() + ".class").delete();
                }
            }

            ParserWriter.writeAnswerFieldFile(IOManager.getAnswerFieldsFile());
            ansFieldEntries.removeAll(toRemove);

            if (!anyRemoved) {
                JOptionPane.showMessageDialog(EditAnswerFieldsDialog.this, "No answer fields are selected, none were removed.", "No Selection", JOptionPane.INFORMATION_MESSAGE);
            } else {
                scrollerContent.validate();
                scrollerContent.repaint();
            }
        }
    }

    /**
     * Action that occurs when the 'How do I make one?' button is pressed
     */
    private class HowToAction extends AbstractAction {

        public HowToAction() {
            super("How do I make one?");
        }

        public void actionPerformed(ActionEvent e) {
            JOptionPane.showMessageDialog(EditAnswerFieldsDialog.this, "<html><font size='2'>Note: there is a full tutorial available on the Ingatan project website (ingatan.org).<br><br>"
                    + "Creating custom answer fields in Ingatan is easy. An answer field is simply a Java<br>class that has the following features:<br>"
                    + "<ul><li>implements the ingatan.component.answerfield.IAnswerField interface</li>"
                    + "<li>extends JComponent</li>"
                    + "<li>must not belong to any package (i.e. no package line at the top <br>of your source file)</li>"
                    + "<li>contains an empty constructor</li></ul>"
                    + "The IAnswerField interface sets out which methods must appear in your class.<br>Your answer field must be context aware (edit or quiz mode)<br>"
                    + "and also be able to show the correct answers, and grade the answers given<br>by the user. It must also be able to serialise and deserialise<br>"
                    + "itself to/from a string value (XML recommended).<br><br>Please see the IAnswerField javadoc for more information, or<br>consult a tutorial"
                    + " on the Ingatan project website.", "How to Create Answer Fields", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    /**
     * Subclass which defines a single entry in the answer field scroll pane, with
     * an instance of the specified answer field, a 'set value to default' button, and
     * a checkbox for selecting the answer field.
     */
    private class AnswerFieldEntry extends PaintedJPanel {

        /**
         * The answer field component that this AnswerFieldEntry object displays.
         */
        IAnswerField ansField;
        JCheckBox checkboxSelector = new JCheckBox(new CheckBoxAction());
        JButton btnSaveAsDefault = new JButton(new SaveDefaultAction());
        JScrollPane scroller = new JScrollPane();

        public AnswerFieldEntry(IAnswerField ansField, boolean allowSelection) {
            super();
            this.ansField = ansField;
            checkboxSelector.setOpaque(false);

            //do not want to allow the user to delete default answer fields.
            if (!allowSelection) {
                checkboxSelector.setVisible(false);
                checkboxSelector.setSelected(false);
                checkboxSelector.setEnabled(false);
            }

            this.setMinimumSize(new Dimension(400, ((JComponent) ansField).getMinimumSize().height + 90));
            this.setMaximumSize(new Dimension(1000, ((JComponent) ansField).getMinimumSize().height + 40));
            this.setPreferredSize(new Dimension(460, ((JComponent) ansField).getMinimumSize().height + 80));

            btnSaveAsDefault.setMargin(new Insets(3, 1, 3, 1));
            btnSaveAsDefault.setFont(ThemeConstants.niceFont);


            //layout
            this.setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
            this.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
            this.add(Box.createHorizontalStrut(3));
            this.add(checkboxSelector);
            this.add(Box.createHorizontalStrut(15));

            //put the answer field in a scroller and then add the scroller.
            scroller.setViewportView((JComponent) this.ansField);
            scroller.setOpaque(false);
            scroller.getViewport().setOpaque(false);
            scroller.setBorder(BorderFactory.createEmptyBorder());
            ((JComponent) this.ansField).setOpaque(false);
            this.add(scroller);

            this.add(Box.createHorizontalStrut(20));
            //this rigid area stops the scroller layout manager from crushing the AnswerFieldEntries,
            //as the scroller layout manager does not seem to respect the min/max/pref sizes
            this.add(Box.createRigidArea(new Dimension(2, 180)));
            this.add(btnSaveAsDefault);
            JLabel lblName = new JLabel(ansField.getDisplayName());
            lblName.setForeground(ThemeConstants.borderSelected);
            this.add(Box.createHorizontalStrut(40));
            this.add(lblName);

            this.setPreferredSize(this.getLayout().minimumLayoutSize(this));
        }

        @Override
        public void paintExtension(Graphics g) {
            String name = ansField.getDisplayName();
            Graphics2D g2d = (Graphics2D) g;
            g2d.setFont(ThemeConstants.hugeFont.deriveFont(80.0f));
            Color c = ThemeConstants.borderUnselected;
            g2d.setPaint(new Color(c.getRed(), c.getGreen(), c.getBlue(), 30));
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2d.drawString(name, 120, 60);
        }

        /**
         * Checks whether or not this <code>AnswerFieldEntry</code> is selected (selected state is determined by the state of the JCheckBox associated with this entry).
         * @return <code>true</code> if this <code>AnswerFieldEntry</code> is selected.
         */
        public boolean isSelected() {
            return checkboxSelector.isSelected();
        }

        /**
         * Gets the class name for the answer field that this entry describes.
         * @return the class name for the answer field that this entry describes.
         */
        public String getClassName() {
            return ansField.getClass().getName();
        }

        private class SaveDefaultAction extends AbstractAction {

            public SaveDefaultAction() {
                super("set as default");
            }

            public void actionPerformed(ActionEvent e) {
                IOManager.getAnswerFieldsFile().getAnswerFieldDefaults().put(AnswerFieldEntry.this.getClassName(), AnswerFieldEntry.this.ansField.writeToXML());
                ParserWriter.writeAnswerFieldFile(IOManager.getAnswerFieldsFile());
                JOptionPane.showMessageDialog(EditAnswerFieldsDialog.this, "The current value of the answer field '" + AnswerFieldEntry.this.ansField.getDisplayName() + "' has been saved as default.", "Default Value Saved", JOptionPane.INFORMATION_MESSAGE);
            }
        }
    }

    private class CheckBoxAction extends AbstractAction {

        public void actionPerformed(ActionEvent e) {
        }
    }
}
