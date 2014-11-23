/*
 * GroupEditorDialog.java
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
import org.ingatan.data.Group;
import org.ingatan.io.IOManager;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.io.IOException;
import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

/**
 * Allows the user to edit, delete or create groups, and change which
 * libraries exist in which groups.
 * @author Thomas Everingham
 * @version 1.0
 */
public class GroupEditorDialog extends JDialog {

    /**
     * Allows the user to select a group to edit from a list of all groups that exist.
     */
    protected JComboBox comboGroupList = new JComboBox();
    /**
     * Button for deleting the current group.
     */
    protected JButton btnRemoveGroup;
    /**
     * Button for adding a new group.
     */
    protected JButton btnAddGroup;
    /**
     * Button for renaming a group. Group names must be unique.
     */
    protected JButton btnRenameGroup;
    /**
     * Label that displays the other groups that the selected library belongs to.
     */
    protected JLabel lblBelongsToGroups;
    /**
     * A list of all libraries that exist outside of the currently selected group.
     */
    protected JList listAllLibraries = new JList();
    /**
     * A list of all libraries that exist within the currently selected group.
     */
    protected JList listGroupLibraries = new JList();
    /**
     * Scroll pane for the list of all libraries that exist within the currently selected group.
     */
    protected JScrollPane scrollerListGroupLibs = new JScrollPane(listGroupLibraries);
    /**
     * Scroll pane for the list of all libraries that exist outside of the currently selected group.
     */
    protected JScrollPane scrollerListAllLibs = new JScrollPane(listAllLibraries);
    /**
     * Label for the group chooser combo box.
     */
    protected JLabel lblGroup = new JLabel("Group to edit:");
    /**
     * Label for the list containing all libraries not contained by the currently selected group.
     */
    protected JLabel lblAllLibraries = new JLabel("Libraries not in this group:");
    /**
     * Label for the list containing all libraries contained by the currently selected group.
     */
    protected JLabel lblGroupLibraries = new JLabel("Libraries in this group:");
    /**
     * Button for moving libraries into the currently selected group. Libraries can exist
     * within multiple groups.
     */
    protected JButton btnMoveIntoGroup;
    /**
     * Button for moving libraries out of the currently selected group.
     */
    protected JButton btnRemoveFromGroup;
    /**
     * Content pane for the group editor dialog.
     */
    protected JPanel contentPane = new JPanel();
    /**
     * Fired for all actions of interest.
     */
    protected AbstractAction groupEditorAction = new GroupEditorAction();
    /**
     * Library IDs as they correspond to the name values in the all libraries list
     */
    protected String[] allLibsListIDs;

    public GroupEditorDialog(JFrame parent) {
        super(parent);
        this.setModal(true);
        this.setTitle("Group Editor");
        this.setIconImage(IOManager.windowIcon);
        this.setContentPane(contentPane);
        setUpGUI();
        rebuildGroupsList();

        listAllLibraries.addListSelectionListener(new listSelectionListener());
        listGroupLibraries.addListSelectionListener(new listSelectionListener());
    }

    /**
     * Booorrrringgg...  Sorry :)
     * Sets up the layout and all components required by the GroupEditorDialog.
     */
    private void setUpGUI() {
        contentPane.setLayout(new BoxLayout(contentPane, BoxLayout.X_AXIS));
        contentPane.setAlignmentY(TOP_ALIGNMENT);
        this.setPreferredSize(new Dimension(690, 300));
        this.setResizable(false);



        //COMPONENT ALIGNMENT AND SIZES
        lblGroup.setAlignmentX(LEFT_ALIGNMENT);
        lblGroup.setHorizontalAlignment(SwingConstants.LEFT);

        comboGroupList.setAlignmentX(LEFT_ALIGNMENT);
        comboGroupList.setMaximumSize(new Dimension(200, 25));
        comboGroupList.setMinimumSize(new Dimension(150, 20));
        comboGroupList.setAction(groupEditorAction);

        btnAddGroup = createButton("/resources/icons/add.png", "", "Create a new group");
        btnMoveIntoGroup = createButton("/resources/icons/moveRight.png", "", "Move libraries into this group");
        btnRemoveFromGroup = createButton("/resources/icons/moveLeft.png", "", "Remove libraries from this group");
        btnRemoveGroup = createButton("/resources/icons/remove.png", "", "Delete this group");
        btnRenameGroup = createButton("/resources/icons/image/pencil.png", "", "Rename this group");

        listAllLibraries.setMaximumSize(new Dimension(200, 300));
        listAllLibraries.setMinimumSize(new Dimension(150, 300));
        listAllLibraries.setAlignmentX(LEFT_ALIGNMENT);
        listAllLibraries.setBorder(BorderFactory.createLineBorder(ThemeConstants.borderUnselected));
        listAllLibraries.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);

        listGroupLibraries.setMaximumSize(new Dimension(200, 300));
        listGroupLibraries.setMinimumSize(new Dimension(150, 200));
        listGroupLibraries.setAlignmentX(LEFT_ALIGNMENT);
        listGroupLibraries.setBorder(BorderFactory.createLineBorder(ThemeConstants.borderUnselected));
        listGroupLibraries.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);

        lblBelongsToGroups = new JLabel("");
        lblBelongsToGroups.setMaximumSize(new Dimension(200, 130));
        lblBelongsToGroups.setMaximumSize(new Dimension(200, 130));
        lblBelongsToGroups.setPreferredSize(new Dimension(200, 130));
        lblBelongsToGroups.setFont(ThemeConstants.niceFont);
        lblBelongsToGroups.setAlignmentX(LEFT_ALIGNMENT);
        lblBelongsToGroups.setVerticalAlignment(SwingConstants.TOP);


        //COMPONENT ALIGNMENT AND SIZES

        contentPane.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        //contentPane.add(Box.createHorizontalStrut(8));
        Box vert = Box.createVerticalBox();
        vert.add(lblGroup);
        vert.add(Box.createVerticalStrut(5));
        vert.add(comboGroupList);
        vert.setAlignmentX(LEFT_ALIGNMENT);

        Box horiz = Box.createHorizontalBox();
        horiz.add(btnAddGroup);
        horiz.add(btnRemoveGroup);
        horiz.add(btnRenameGroup);
        horiz.setAlignmentX(LEFT_ALIGNMENT);
        horiz.setBorder(BorderFactory.createLineBorder(ThemeConstants.borderUnselected));
        vert.add(Box.createVerticalStrut(3));
        vert.add(horiz);
        vert.add(Box.createVerticalStrut(15));
        vert.add(lblBelongsToGroups);
        vert.setMaximumSize(new Dimension(200, 230));
        vert.setAlignmentY(BOTTOM_ALIGNMENT);
        vert.add(Box.createGlue());
        contentPane.add(vert);
        contentPane.add(Box.createHorizontalStrut(30));

        vert = Box.createVerticalBox();
        vert.add(lblAllLibraries);
        vert.add(Box.createVerticalStrut(5));
        vert.add(scrollerListAllLibs);
        vert.setAlignmentY(CENTER_ALIGNMENT);
        vert.setMaximumSize(new Dimension(201, 500));
        contentPane.add(vert);

        Box superVert = Box.createVerticalBox();
        vert = Box.createVerticalBox();
        vert.add(btnMoveIntoGroup);
        vert.add(btnRemoveFromGroup);
        vert.setBorder(BorderFactory.createLineBorder(ThemeConstants.borderUnselected));
        vert.setAlignmentY(TOP_ALIGNMENT);
        vert.setMaximumSize(new Dimension(24, 46));
        superVert.add(Box.createVerticalStrut(90));
        superVert.add(vert);
        superVert.add(vert.createGlue());
        contentPane.add(Box.createHorizontalStrut(4));
        contentPane.add(superVert);
        contentPane.add(Box.createHorizontalStrut(4));

        vert = Box.createVerticalBox();
        vert.add(lblGroupLibraries);
        vert.add(Box.createVerticalStrut(5));
        vert.add(scrollerListGroupLibs);
        vert.setAlignmentY(CENTER_ALIGNMENT);

        vert.setMaximumSize(new Dimension(201, 500));
        contentPane.add(vert);

        this.pack();
    }

    /**
     * Rebuild the combo box group list based on the groups list provided by the IOManager.
     */
    private void rebuildGroupsList() {
        comboGroupList.removeAllItems();
        String[] groupList = IOManager.getGroupList();
        for (int i = 0; i < groupList.length; i++) {
            comboGroupList.addItem(groupList[i]);
        }
    }

    /**
     * Creates a new button with the specified text, icon and tooltip text. Sets the border
     * empty, sets not focusable and adds the global action listener.
     * @param iconPath the icon to use with the image. If no icon is required, pass
     *                 an empty string.
     * @param text the button's text
     * @param tooltip the button's tooltip
     * @return the new button with all properties set.
     */
    private JButton createButton(String iconPath, String text, String tooltip) {
        JButton btn = new JButton(groupEditorAction);
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
        btn.setFont(ThemeConstants.niceFont);

        return btn;
    }

    private class GroupEditorAction extends AbstractAction {

        public void actionPerformed(ActionEvent e) {
            Object src = e.getSource();

            if (src.equals(btnAddGroup)) {
                String newGroup;
                String msg = "";
                do {
                    newGroup = (String) JOptionPane.showInputDialog(GroupEditorDialog.this, msg + "Please enter a name for the new "
                            + "group.", "Create a group", JOptionPane.OK_CANCEL_OPTION, null, null, "Wendy");
                    msg = "That group name is already taken.\n\n";
                    //if the user pressed cancel or the specified group name is an empty string
                    if ((newGroup == null) || newGroup.isEmpty()) {
                        return;
                    }
                } while ((IOManager.isGroupNameUnique(newGroup) == false) || (newGroup.equals(LibraryBrowser.ALL_LIBRARIES_GROUP_NAME)));
                //write new group to the IOManager, also saving it to file. Must disable combo box action so that adding a group doesn't fire an item change5
                comboGroupList.setAction(null);
                IOManager.createGroup(newGroup, new String[0]);
                rebuildGroupsList();
                comboGroupList.setSelectedItem(newGroup);
                comboGroupList.setAction(groupEditorAction);
                //clean up lists in wake of groupadd
                rebuildLibraryLists();

            } else if (src.equals(btnMoveIntoGroup)) {
                //no group is selected
                if (comboGroupList.getSelectedItem() == null) {
                    return;
                }
                //no library is selected
                if (listAllLibraries.getSelectedIndex() == -1) {
                    return;
                }
                //get all selected indices
                int[] selectedLibs = listAllLibraries.getSelectedIndices();
                //add the library at each index
                for (int i = 0; i < selectedLibs.length; i++) {
                    IOManager.getGroup((String) comboGroupList.getSelectedItem()).addLibrary(allLibsListIDs[selectedLibs[i]]);
                }
                rebuildLibraryLists();
                IOManager.writeGroupsFile();
            } else if (src.equals(btnRemoveFromGroup)) {
                //no group is selected
                if (comboGroupList.getSelectedItem() == null) {
                    return;
                }
                //no library is selected
                if (listGroupLibraries.getSelectedIndex() == -1) {
                    return;
                }

                //get all selected indices
                int[] selectedLibs = listGroupLibraries.getSelectedIndices();
                String[] IDs = IOManager.getGroup((String) comboGroupList.getSelectedItem()).getLibraryIDs();
                //add the library at each index
                for (int i = 0; i < selectedLibs.length; i++) {
                    IOManager.getGroup((String) comboGroupList.getSelectedItem()).removeLibrary(IDs[selectedLibs[i]]);
                }
                rebuildLibraryLists();
                IOManager.writeGroupsFile();
            } else if (src.equals(btnRemoveGroup)) {
                if (comboGroupList.getSelectedItem() == null) {
                    JOptionPane.showMessageDialog(GroupEditorDialog.this, "There is no group selected.", "Can't delete", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                int resp = JOptionPane.showConfirmDialog(GroupEditorDialog.this, "Are you sure you wish to delete the group '" + ((String) comboGroupList.getSelectedItem()) + "'? The libraries within the group will not be deleted.", "Delete group confirmation", JOptionPane.YES_NO_OPTION);
                if (resp == JOptionPane.YES_OPTION) {
                    IOManager.deleteGroup((String) comboGroupList.getSelectedItem());
                }
                rebuildGroupsList();
            } else if (src.equals(btnRenameGroup)) {
                if (comboGroupList.getSelectedItem() == null) {
                    JOptionPane.showMessageDialog(GroupEditorDialog.this, "There is no group selected.", "Can't rename", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                String newName;
                String msg = "";
                do {
                    newName = (String) JOptionPane.showInputDialog(GroupEditorDialog.this, msg + "Please enter a new name for the group.", "Rename the group", JOptionPane.OK_CANCEL_OPTION, null, null, (String) comboGroupList.getSelectedItem());
                    msg = "That group name is already taken.\n\n";
                    //if the user pressed cancel or the specified name is empty
                    if ((newName == null) || (newName.isEmpty()) || (newName.equals((String) comboGroupList.getSelectedItem()))) {
                        return;
                    }
                } while (IOManager.isGroupNameUnique(newName) == false);

                //write new group name to the IOManager, also saving the change to file.
                IOManager.renameGroup((String) comboGroupList.getSelectedItem(), newName);
                rebuildGroupsList();
                comboGroupList.setSelectedItem(newName);
            } else if (src.equals(comboGroupList)) {
                //if the group list is not empty
                if (comboGroupList.getSelectedItem() != null) {
                    rebuildLibraryLists();
                } else {
                    //if it is empty, make sure the library lists are empty too
                    listAllLibraries.setListData(new Object[0]);
                    listGroupLibraries.setListData(new Object[0]);
                }
            }
        }

        public void rebuildLibraryLists() {
            listGroupLibraries.setListData(IOManager.getGroup((String) comboGroupList.getSelectedItem()).getlibraryNames());
            listAllLibraries.setListData(generateOtherLibraryList(IOManager.getGroup((String) comboGroupList.getSelectedItem())));
        }

        public String[] generateOtherLibraryList(Group group) {
            String[] groupIDs = group.getLibraryIDs();
            String[] allIDs = IOManager.getLibraryIDs();


            for (int i = 0; i < groupIDs.length; i++) {
                for (int j = 0; j < allIDs.length; j++) {
                    if (allIDs[j].compareTo(groupIDs[i]) == 0) {
                        allIDs = IOManager.removeIndexFromArray(j, allIDs);
                        break;
                    }
                }
            }

            String[] toNames = new String[allIDs.length];
            for (int i = 0; i < allIDs.length; i++) {
                toNames[i] = IOManager.getLibraryName(allIDs[i]);
            }

            allLibsListIDs = allIDs;

            return toNames;
        }
    }

    private class listSelectionListener implements ListSelectionListener {

        public void valueChanged(ListSelectionEvent lse) {
            //if there is no library selected, then this event is probably the lists being cleared, etc.
            if (comboGroupList.getSelectedItem() == null) {
                return;
            }

            JList src = ((JList) lse.getSource());
            //if a library ID cannot be obtained from this event, then LEAVE this place!
            if (src.getSelectedValue() == null) {
                return;
            }

            String[] groupsToPrint = new String[0];
            String libID = "";
            if (src.equals(listAllLibraries)) {
                libID = allLibsListIDs[listAllLibraries.getSelectedIndex()];
                groupsToPrint = IOManager.getGroupsThatContain(libID);
            } else if (src.equals(listGroupLibraries)) {
                libID = IOManager.getGroup((String) comboGroupList.getSelectedItem()).getLibraryIDs()[listGroupLibraries.getSelectedIndex()];
                groupsToPrint = IOManager.getGroupsThatContain(libID);
            }

            String newText = "<html><h5>Selected library found in groups: </h5>";
            for (int i = 0; i < groupsToPrint.length-1; i++) {
                newText += groupsToPrint[i] + ", ";
            }
            if (groupsToPrint.length > 1)
                newText += "and " + groupsToPrint[groupsToPrint.length-1];
            else if (groupsToPrint.length == 1)
                newText += "only " + groupsToPrint[0];
            else if (groupsToPrint.length == 0) {
                newText += "This library doesn't belong to any groups yet.";
            }
            try {
                newText += "<h5>Description:</h5>" + IOManager.getLibraryFromID(libID).getDescription();
            } catch (IOException ignore) {/*if we couldn't get the library description, then this is no big deal*/}

            lblBelongsToGroups.setText(newText);
        }
    }
}
