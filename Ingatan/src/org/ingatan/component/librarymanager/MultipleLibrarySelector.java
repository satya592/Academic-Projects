/*
 * MultipleLibrarySelector.java
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

import java.awt.event.MouseEvent;
import org.ingatan.ThemeConstants;
import org.ingatan.io.IOManager;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseListener;
import java.util.Arrays;
import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

/**
 * Component made up of a library browser and a selection list. With this component, the
 * user may browse libraries by group, and use -> and &lt;- buttons to add or remove them
 * from a list of currently selected libraries. This component is used when setting up
 * a quiz, and also when choosing libraries to export.<br><br>
 * Clean up the variable names, and.. implementation a bit. This is not the neatest class.
 * 
 * @author Thomas Everingham
 * @version 1.0
 */
public class MultipleLibrarySelector extends JPanel {

    /**
     * The new library button was pressed.
     */
    public static final int DESELECT_LIBRARIES = 0;
    /**
     * The delete library button was pressed.
     */
    public static final int SELECT_LIBRARIES = 1;
    /**
     * The selected group has changed.
     */
    public static final int GROUP_SELECTION_CHANGED = 5;
    /**
     * The selected library has changed.
     */
    public static final int LIBRARY_SELECTION_CHANGED = 6;
    /**
     * Name of the default group that contains all libraries.
     */
    public static final String ALL_LIBRARIES_GROUP_NAME = "- All libraries -";
    /**
     * Index of the default group that contains all libraries.
     */
    public static final int ALL_LIBRARIES_GROUP_INDEX = 0;
    /**
     * Combo box loaded with all library groups that have been created.
     */
    protected JComboBox comboGroups = new JComboBox();
    /**
     * List box with all libraries held by the currently selected group.
     */
    protected JList listLibraries = new JList();
    /**
     * List of selected libraries as built by the user.
     */
    protected JList selectedLibraries = new JList();
    /**
     * Label for selected libraries list.
     */
    protected JLabel lblSelection = new JLabel("Selection:");
    /**
     * Scroll pane for the library list box.
     */
    protected JScrollPane scrollLibraries = new JScrollPane(listLibraries);
    /**
     * Scroll pane for the selected libraries list box.
     */
    protected JScrollPane scrollSelectedLibs = new JScrollPane(selectedLibraries);
    /**
     * Button used to add a library or libraries to the selected libraries list.
     */
    protected JButton btnSelectLibraries;
    /**
     * Button used to remove a library or libraries from the selected libraries list.
     */
    protected JButton btnDeselectLibraries;
    /**
     * Action listeners are added to listen for change in selection or actions on the
     * library/group edit buttons.
     */
    protected ActionListener[] actionListeners = new ActionListener[0];
    /**
     * Library IDs for the libraries in the selected libraries list
     */
    protected String[] selectedLibraryIDs = new String[0];
    /**
     * Library IDs for entries in the library list for the currently selected group.
     */
    protected String[] libListLibIDs = new String[0];
    /**
     * Action for all events of interest. This action notifies all action listeners of what has happened.
     */
    private AbstractAction libraryBrowserAction = new BrowserAction();

    /**
     * Creates a new LibraryBrowser object.
     */
    public MultipleLibrarySelector() {
        setUpGUI();
        updateGroups();
        updateLibraries();
    }

    private void setUpGUI() {
        this.setLayout(new BoxLayout(this, BoxLayout.X_AXIS));

        btnDeselectLibraries = createButton("/resources/icons/moveLeft.png", "", "Add to selection");
        btnSelectLibraries = createButton("/resources/icons/moveRight.png", "", "Remove from selection");
        comboGroups.setFont(ThemeConstants.niceFont);
        comboGroups.setAction(libraryBrowserAction);

        listLibraries.addMouseListener(new LibraryListMouseListener());
        listLibraries.addListSelectionListener(new SelectionListener());
        selectedLibraries.addMouseListener(new LibraryListMouseListener());
        selectedLibraries.addListSelectionListener(new SelectionListener());

        //sizes
        comboGroups.setMaximumSize(new Dimension(225, 20));
        comboGroups.setMinimumSize(new Dimension(200, 20));
        scrollLibraries.setMaximumSize(new Dimension(225, 175));
        scrollLibraries.setMinimumSize(new Dimension(200, 175));
        scrollSelectedLibs.setMaximumSize(new Dimension(225, 175));
        scrollSelectedLibs.setMinimumSize(new Dimension(200, 175));

        //sizes

        Box vert = Box.createVerticalBox();

        vert.add(comboGroups);
        vert.add(scrollLibraries);
        vert.setMaximumSize(new Dimension(225, 175));
        vert.setAlignmentY(CENTER_ALIGNMENT);
        this.add(vert);
        this.add(Box.createHorizontalStrut(10));

        vert = Box.createVerticalBox();
        vert.add(btnSelectLibraries);
        vert.add(btnDeselectLibraries);
        vert.setBorder(BorderFactory.createLineBorder(ThemeConstants.borderUnselected));
        vert.setMaximumSize(new Dimension(25, 46));
        vert.setAlignmentY(CENTER_ALIGNMENT);
        this.add(vert);
        this.add(Box.createHorizontalStrut(10));
        vert = Box.createVerticalBox();
        vert.add(lblSelection);
        vert.setAlignmentY(CENTER_ALIGNMENT);
        lblSelection.setAlignmentX(LEFT_ALIGNMENT);
        scrollSelectedLibs.setAlignmentX(LEFT_ALIGNMENT);
        vert.add(Box.createVerticalStrut(3));
        vert.setMaximumSize(new Dimension(225, 175));
        vert.add(scrollSelectedLibs);
        this.add(vert);
    }

    /**
     * Gets the library IDs for all the libraries that have been moved to the selected
     * libraries list.
     * @return the library IDs for all selected libraries.
     */
    public String[] getSelectedLibraryIDs() {
        return selectedLibraryIDs;
    }

    /**
     * Returns the list of selected (highlighted) libraries in the JList that has focus, or
     * the selected (highlighted) libraries in the unselected library list if neither list is focussed.
     * @return the list of selected (highlighted) libraries in one of the JLists.
     */
    public String[] getHighlightedLibraryIDs() {
        String[] retVal;
        if (selectedLibraries.hasFocus())
        {
            int[] highlighted = selectedLibraries.getSelectedIndices();
            retVal = new String[highlighted.length];
            for (int i = 0; i < highlighted.length; i++)
            {
                retVal[i] = selectedLibraryIDs[highlighted[i]];
            }
        }
        else
        {
            int[] highlighted = listLibraries.getSelectedIndices();
            retVal = new String[highlighted.length];
            for (int i = 0; i < highlighted.length; i++)
            {
                retVal[i] = libListLibIDs[highlighted[i]];
            }
        }
        
        return retVal;
    }

    /**
     * Set the group to browse the libraries of.
     * @param groupName the group name of the group to browse.
     */
    public void setSelectedGroup(String groupName) {
        comboGroups.setSelectedItem(groupName);
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
        String[] libNames;
        //load what libraries exist within the currently selected group
        if (comboGroups.getSelectedIndex() == ALL_LIBRARIES_GROUP_INDEX) {
            libNames = IOManager.getLibraryNames();
            libListLibIDs = IOManager.getLibraryIDs();
        } else {
            libNames = IOManager.getGroup((String) comboGroups.getSelectedItem()).getlibraryNames();
            libListLibIDs = IOManager.getGroup((String) comboGroups.getSelectedItem()).getLibraryIDs();
        }

        //now remove any that are already in the selected list

        for (int i = 0; i < selectedLibraries.getModel().getSize(); i++) {
            for (int j = 0; j < libListLibIDs.length; j++) {
                if (libListLibIDs[j].equals(selectedLibraryIDs[i])) {
                    libListLibIDs = IOManager.removeIndexFromArray(j, libListLibIDs);
                    libNames = IOManager.removeIndexFromArray(j, libNames);
                    break;
                }
            }
        }

        listLibraries.setListData(libNames);
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
            btn.setIcon(new ImageIcon(MultipleLibrarySelector.class.getResource(iconPath)));
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

    private class BrowserAction extends AbstractAction {

        public void actionPerformed(ActionEvent e) {

            ActionEvent newEvent = null;

            if (e.getSource() instanceof JButton) {
                JButton src = (JButton) e.getSource();
                if (src.equals(btnDeselectLibraries)) {
                    newEvent = new ActionEvent(MultipleLibrarySelector.this, MultipleLibrarySelector.DESELECT_LIBRARIES, "DeselectLibraries");
                    deselectLibraries();
                } else if (src.equals(btnSelectLibraries)) {
                    newEvent = new ActionEvent(MultipleLibrarySelector.this, MultipleLibrarySelector.SELECT_LIBRARIES, "SelectLibraries");
                    selectLibraries();
                }
            } else if (e.getSource() instanceof JComboBox) {
                newEvent = new ActionEvent(MultipleLibrarySelector.this, MultipleLibrarySelector.GROUP_SELECTION_CHANGED, "GroupSelectionChanged");

                updateLibraries();
            }

            if (newEvent == null) {
                return;
            }

            for (int i = 0; i < actionListeners.length; i++) {
                actionListeners[i].actionPerformed(newEvent);
            }
        }

        /**
         * Removes libraries from the selected list
         */
        public void deselectLibraries() {
            int[] selectedLibs = selectedLibraries.getSelectedIndices();
            //add the library at each index
            String[] newSelectedLibraryIDs = Arrays.copyOf(selectedLibraryIDs, selectedLibraryIDs.length);
            for (int i = 0; i < selectedLibs.length; i++) {
                newSelectedLibraryIDs = IOManager.removeItemFromArray(selectedLibraryIDs[selectedLibs[i]], newSelectedLibraryIDs);
            }
            selectedLibraryIDs = newSelectedLibraryIDs;
            String[] tempLibraryNames = new String[selectedLibraryIDs.length];
            for (int i = 0; i < selectedLibraryIDs.length; i++) {
                tempLibraryNames[i] = IOManager.getLibraryName(selectedLibraryIDs[i]);
            }
            selectedLibraries.setListData(tempLibraryNames);
            updateLibraries();
        }

        /**
         * Moves libraries to the selected list
         */
        public void selectLibraries() {
            //get all selected indices

            int[] selectedLibs = listLibraries.getSelectedIndices();
            //add the library at each index
            for (int i = 0; i < selectedLibs.length; i++) {
                selectedLibraryIDs = IOManager.appendToArray(libListLibIDs[selectedLibs[i]], selectedLibraryIDs);
            }
            String[] tempLibraryNames = new String[selectedLibraryIDs.length];
            for (int i = 0; i < selectedLibraryIDs.length; i++) {
                tempLibraryNames[i] = IOManager.getLibraryName(selectedLibraryIDs[i]);
            }
            selectedLibraries.setListData(tempLibraryNames);
            updateLibraries();
        }
    }

    /**
     * Listens for double click on the library list to move selected library from one list to the other.
     */
    private class LibraryListMouseListener implements MouseListener {

        public void mouseClicked(MouseEvent e) {
            if (e.getClickCount() == 2) {
                if (e.getSource() == listLibraries) {
                    new BrowserAction().selectLibraries();
                } else if (e.getSource() == selectedLibraries) {
                    new BrowserAction().deselectLibraries();
                }
            }
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

    private class SelectionListener implements ListSelectionListener {

        public void valueChanged(ListSelectionEvent e) {
            ActionEvent newEvent = new ActionEvent(MultipleLibrarySelector.this, MultipleLibrarySelector.LIBRARY_SELECTION_CHANGED, "LibrarySelectionChanged");
            for (int i = 0; i < actionListeners.length; i++) {
                actionListeners[i].actionPerformed(newEvent);
            }
        }

    }


}
