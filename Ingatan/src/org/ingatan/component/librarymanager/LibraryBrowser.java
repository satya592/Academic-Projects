/*
 * LibraryBrowser.java
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

import org.ingatan.data.Group;
import org.ingatan.io.IOManager;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

/**
 * Used in the library manager, this component encapsulates the loaded group and
 * library files, as well as providing an interface by which the user may navigate
 * through groups and select libraries.<br>
 * <br>
 * The component also provides incorporates buttons for editing the group, adding
 * a new library, deleting a library, editing a library and importing/exporting a group/library.
 * 
 * @author Thomas Everingham
 * @version 1.0
 */
public class LibraryBrowser extends JPanel {

    /**
     * The new library button was pressed.
     */
    protected static final int ADD_LIBRARY_ACTION = 0;
    /**
     * The delete library button was pressed.
     */
    protected static final int REMOVE_LIBRARY_ACTION = 1;
    /**
     * The edit library button was pressed.
     */
    protected static final int EDIT_LIBRARY_ACTION = 2;
    /**
     * The edit groups button was pressed.
     */
    protected static final int EDIT_GROUPS_ACTION = 3;
    /**
     * The export/import button was pressed.
     */
    protected static final int EXPORT_IMPORT_ACTION = 4;
    /**
     * The selected group has changed.
     */
    protected static final int GROUP_SELECTION_CHANGED = 5;
    /**
     * The selected library has changed.
     */
    protected static final int LIBRARY_SELECTION_CHANGED = 6;
    /**
     * The name of the default group which contains all libraries that exist in all groups.
     */
    protected static final String ALL_LIBRARIES_GROUP_NAME = "- All Libraries -";
    /**
     * The index of the default group in the groups list which contains all libraries that exist in all groups.
     */
    protected static final int ALL_LIBRARIES_GROUP_INDEX = 0;
    /**
     * Combo box loaded with all library groups that have been created.
     */
    protected JComboBox comboGroups = new JComboBox();
    /**
     * List box with all libraries held by the currently selected group.
     */
    protected JList listLibraries = new JList();
    /**
     * Scroll pane for the library list box.
     */
    protected JScrollPane scrollLibraries = new JScrollPane(listLibraries);
    /**
     * Button used to access the group editor.
     */
    protected JButton btnEditGroup;
    /**
     * Button used to create a new library.
     */
    protected JButton btnAddLib;
    /**
     * Button used to delete a library and all questions within it. The
     * user will be presented with three options: <ul>
     * <li>Remove this library from the current group, but do not delete it (if group != 'all')</li>
     * <li>Delete this library and all questions within it.</li>
     * <li>Delete this library and move all questions to [other library]</li>
     * </ul>
     */
    protected JButton btnRemoveLib;
    /**
     * Button used to access library editor.
     */
    protected JButton btnEditLib;
    /**
     * Button used to acces the library/group importer/exporter.
     */
    protected JButton btnExport;
    /**
     * A nicer component font (smaller) based on the default font.
     */
    protected Font niceFont = new Font(this.getFont().getFamily(), Font.PLAIN, 10);
    /**
     * Action listeners are added to listen for change in selection or actions on the
     * library/group edit buttons.
     */
    protected ActionListener[] actionListeners = new ActionListener[0];
    /**
     * Action fired whenever a library browser action occurs, and the actionListeners that have
     * been added are informed.
     */
    private AbstractAction libraryBrowserAction = new browserAction();
    /**
     * This is set when the selection of the library list is changed
     */
    private String selectedLibraryID = null;
    /**
     * This is set just before the selectedLibraryIndex variable is updated. Having this
     * variable allows the <code>getPreviousSelectedLibrary</code> to work.
     */
    private String previousLibraryID = null;

    /**
     * Creates a new LibraryBrowser object.
     */
    public LibraryBrowser() {
        super();
        setUpGUI();

        updateGroups();
        //set the selected group to the 'all libraries' group, and update the library list.
        comboGroups.setSelectedIndex(ALL_LIBRARIES_GROUP_INDEX);
        listLibraries.setListData(IOManager.getLibraryNames());
        listLibraries.addListSelectionListener(new libListSelectionListener());
        listLibraries.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

    }

    /**
     * Gets the index of the currently selected library.
     * @return the index of the currently selected library, or -1 if no library
     * is selected.
     */
    public int getSelectedLibraryIndex() {
        return listLibraries.getSelectedIndex();
    }

    /**
     * Update the groups combo box with values from the IOManager
     */
    public void updateGroups() {
        comboGroups.setAction(null);
        comboGroups.removeAllItems();
        String[] groupNames = IOManager.getGroupList();
        comboGroups.addItem(ALL_LIBRARIES_GROUP_NAME);
        for (int i = 0; i < groupNames.length; i++) {
            comboGroups.addItem(groupNames[i]);
        }
        comboGroups.setAction(libraryBrowserAction);
    }

    /**
     * Update the libraries field with values from the library manager, based on
     * the current selection in the groups combo list.
     */
    public void updateLibraries() {
        if (comboGroups.getSelectedIndex() == ALL_LIBRARIES_GROUP_INDEX) {
            listLibraries.setListData(IOManager.getLibraryNames());
        } else {
            listLibraries.setListData(IOManager.getGroup((String) comboGroups.getSelectedItem()).getlibraryNames());
        }
    }

    private void setUpGUI() {
        this.setLayout(new BoxLayout(this, BoxLayout.X_AXIS));

        btnAddLib = createButton("/resources/icons/add.png", "", "Add a new library");
        btnEditGroup = createButton("/resources/icons/image/pencil.png", "", "Open the group editor");
        btnEditLib = createButton("/resources/icons/image/pencil.png", "", "Edit the selected library");
        btnExport = createButton("/resources/icons/folder.png", "", "Open the library import/export tool");
        btnRemoveLib = createButton("/resources/icons/remove.png", "", "Remove this library");
        comboGroups.setFont(niceFont);
        comboGroups.setAction(libraryBrowserAction);



        //sizes
        comboGroups.setMaximumSize(new Dimension(250, 30));
        comboGroups.setMinimumSize(new Dimension(200, 20));
        scrollLibraries.setMaximumSize(new Dimension(250, 500));
        scrollLibraries.setMinimumSize(new Dimension(200, 500));
        //sizes

        Box vert = Box.createVerticalBox();
        vert.add(btnEditGroup);
        Box boxBtn = Box.createVerticalBox();
        boxBtn.add(btnAddLib);
        boxBtn.add(btnRemoveLib);
        boxBtn.add(btnEditLib);
        boxBtn.add(btnExport);
        vert.add(Box.createVerticalGlue());
        vert.add(boxBtn);


        this.add(vert);

        vert = Box.createVerticalBox();


        vert.add(comboGroups);


        vert.add(scrollLibraries);
        this.add(vert);
    }

    /**
     * Gets the selected group name.
     * @return the selected group name. <code>null</code> if the 'all libraries' group
     * is selected.
     */
    public String getSelectedGroupName() {
        if (comboGroups.getSelectedIndex() == ALL_LIBRARIES_GROUP_INDEX) {
            return "- All Libraries -";
        }
        return (String) comboGroups.getSelectedItem();
    }

    /**
     * Sets the selected group.
     * @param groupName The name of the group the should be selected.
     */
    public void setSelectedGroup(String groupName) {
        if (groupName == null) {
            comboGroups.setSelectedIndex(ALL_LIBRARIES_GROUP_INDEX);
            updateLibraries();
        } else {
            comboGroups.setSelectedItem(groupName);
        }
    }

    /**
     * Gets the selected group.
     * @return the selected group.
     */
    public Group getSelectedGroup() {
        return IOManager.getGroup(getSelectedGroupName());
    }

    /**
     * Gets the selected library name.
     * @return the selected library name.
     */
    public String getSelectedLibraryName() {
        return (String) listLibraries.getSelectedValue();
    }

    /**
     * Gets the library ID corresponding to the selected library.
     * @return the library ID corresponding to the selected library, or <code>null</code> if no
     * library is selected.
     */
    public String getSelectedLibraryID() {
        return selectedLibraryID;
    }

    /**
     * Called by the LibraryManagerWindow so that the library browser is
     * aware that a library was deleted. The deleted library has to have been
     * the selected library, and so this method sets the selectedLibraryID field
     * to null so that no attempt is made to save it when a new library is selected.
     */
    public void notifyOfLibraryDeletion() {
        selectedLibraryID = null;
    }

    /**
     * Gets the libraryID for the entry at the given index of the libraries list.
     * @param index the index of the entry to get the ID for
     * @return the ID of the library entry at the given index.
     */
    public String getLibraryIDFromIndex(int index) {
        if (comboGroups.getSelectedIndex() == ALL_LIBRARIES_GROUP_INDEX) {
            return IOManager.getLibraryIDs()[index];
        } else {
            return IOManager.getGroup((String) comboGroups.getSelectedItem()).getLibraryIDs()[index];
        }
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
    }

    /**
     * Creates a new button with the specified text, icon and tooltip text. Sets the border
     * empty, sets not focusable and adds the global <code>buttonListener</code> as a <code>MouseListener</code>.
     * @param iconPath the icon to use with the image. If no icon is required, pass
     *                 an empty string.
     * @param text the button's text
     * @param tooltip the button's tooltip
     * @return the new button with all properties set.
     */
    private JButton createButton(String iconPath, String text, String tooltip) {
        JButton btn = new JButton(libraryBrowserAction);
        btn.setText(text);
        btn.setToolTipText(tooltip);
        if (!iconPath.isEmpty()) {
            btn.setIcon(new ImageIcon(LibraryBrowser.class.getResource(iconPath)));
            btn.setSize(btn.getIcon().getIconWidth(), btn.getIcon().getIconHeight());
        } else {
            btn.setSize(16, 16);
        }
        btn.setBorder(BorderFactory.createEmptyBorder(3, 3, 3, 3));
        btn.setFocusable(false);

        btn.setAlignmentX(JComponent.RIGHT_ALIGNMENT);
        btn.setFont(niceFont);

        return btn;
    }

    /**
     * Get the index of the previously selected library.
     * @return the index of the previously selected library.
     */
    public String getPreviouslySelectedLibraryID() {
        return previousLibraryID;
    }

    /**
     * Adds an <code>ActionListener</code> to this <code>QuestionListToolbar</code> instance.
     * @param listener the <code>ActionListener</code> to add.
     */
    public void addActionListener(ActionListener listener) {
        if (actionListeners.length == 0) {
            actionListeners = new ActionListener[]{listener};
        } else {
            ActionListener[] temp = new ActionListener[actionListeners.length + 1];
            System.arraycopy(actionListeners, 0, temp, 0, actionListeners.length);
            temp[actionListeners.length] = listener;
            actionListeners = temp;
        }
    }

    /**
     * Removes a <code>ActionListener</code> from this <code>QuestionListToolbar</code> instance.
     * @param listener the <code>ActionListener</code> to remove.
     * @return true if the listener could be found and removed, and false otherwise.
     */
    public boolean removeActionListener(ActionListener listener) {
        if (actionListeners.length == 0) {
            return false;
        }
        if (actionListeners.length == 1) {
            if (actionListeners[0].equals(listener)) {
                actionListeners = new ActionListener[0];
                return true;
            } else {
                return false;
            }
        }

        int index = -1;
        //get the index
        for (int i = 0; i < actionListeners.length; i++) {
            if (actionListeners[i].equals(listener)) {
                index = i;
                break;
            }
        }

        //if index is -1, we have not found the listener
        if (index == -1) {
            return false;
        }

        //otherwise, get rid of the listener
        ActionListener[] temp = new ActionListener[actionListeners.length - 1];
        if (index == 0) {
            System.arraycopy(actionListeners, 1, temp, 0, actionListeners.length - 1);
            actionListeners = temp;
            return true;
        } else if (index == actionListeners.length - 1) {
            System.arraycopy(actionListeners, 0, temp, 0, actionListeners.length - 1);
            actionListeners = temp;
            return true;
        } else //the index is not on the edge of the array
        {
            System.arraycopy(actionListeners, 0, temp, 0, index);
            System.arraycopy(actionListeners, index + 1, temp, index, actionListeners.length - index - 1);
            actionListeners = temp;
            return true;
        }
    }

    private class browserAction extends AbstractAction {

        public void actionPerformed(ActionEvent e) {

            ActionEvent newEvent = null;

            if (e.getSource() instanceof JButton) {
                JButton src = (JButton) e.getSource();
                if (src.equals(btnAddLib)) {
                    newEvent = new ActionEvent(LibraryBrowser.this, LibraryBrowser.ADD_LIBRARY_ACTION, "AddLibraryButton");
                } else if (src.equals(btnEditGroup)) {
                    newEvent = new ActionEvent(LibraryBrowser.this, LibraryBrowser.EDIT_GROUPS_ACTION, "EditGroupsButton");
                } else if (src.equals(btnEditLib)) {
                    newEvent = new ActionEvent(LibraryBrowser.this, LibraryBrowser.EDIT_LIBRARY_ACTION, "EditLibraryButton");
                } else if (src.equals(btnExport)) {
                    newEvent = new ActionEvent(LibraryBrowser.this, LibraryBrowser.EXPORT_IMPORT_ACTION, "ExportImportButton");
                } else if (src.equals(btnRemoveLib)) {
                    newEvent = new ActionEvent(LibraryBrowser.this, LibraryBrowser.REMOVE_LIBRARY_ACTION, "RemoveLibraryButton");
                }
            } else if (e.getSource() instanceof JComboBox) {
                newEvent = new ActionEvent(LibraryBrowser.this, LibraryBrowser.GROUP_SELECTION_CHANGED, "GroupSelectionChanged");
                //for this event, we will notify the actionListeners first, so that the library can be saved
                for (int i = 0; i < actionListeners.length; i++) {
                    actionListeners[i].actionPerformed(newEvent);
                }
                //update the libraries list
                listLibraries.removeAll();
                //the library has been saved by other means (by LibraryManagerWindow in the group selection changed event handling code)
                //and also the library is no longer selected. By setting selectedLibraryID to null, we stop the selectedLibraryID library
                //from being over-written next time the library selection changes.
                selectedLibraryID = null;
                String[] libNames;
                if (comboGroups.getSelectedIndex() == ALL_LIBRARIES_GROUP_INDEX) {
                    libNames = IOManager.getLibraryNames();
                } else {
                    libNames = IOManager.getGroup((String) comboGroups.getSelectedItem()).getlibraryNames();
                }

                listLibraries.setListData(libNames);
                //can leave early, as we have already notified the action listeners for this particular event.
                return;
            }

            if (newEvent == null) {
                return;
            }


            //Fire actionPerformed on all listeners using whichever action event was created above
            for (int i = 0; i < actionListeners.length; i++) {
                actionListeners[i].actionPerformed(newEvent);
            }
        }
    }

    private class libListSelectionListener implements ListSelectionListener {

        public void valueChanged(ListSelectionEvent e) {
            if (e.getValueIsAdjusting() == false) {
                return;
            }
            previousLibraryID = selectedLibraryID;

            //get the newly selected library
            if (listLibraries.getSelectedIndex() == -1) {
                selectedLibraryID = null;
            } else if (comboGroups.getSelectedIndex() == ALL_LIBRARIES_GROUP_INDEX) {
                selectedLibraryID = IOManager.getLibraryIDs()[listLibraries.getSelectedIndex()];
            } else {
                selectedLibraryID = IOManager.getGroup((String) comboGroups.getSelectedItem()).getLibraryIDs()[listLibraries.getSelectedIndex()];
            }

            selectedLibraryID = getSelectedLibraryID();
            for (int i = 0; i < actionListeners.length; i++) {
                actionListeners[i].actionPerformed(new ActionEvent(LibraryBrowser.this, LibraryBrowser.LIBRARY_SELECTION_CHANGED, "LibrarySelectionChanged"));
            }
        }
    }
}
