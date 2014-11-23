/*
 * QuestionListToolbar.java
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
import org.ingatan.component.text.RichTextToolbar;
import org.ingatan.component.text.SimpleTextField;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;

/**
 * Toolbar for the <code>QuestionList</code> component. Incorporates: <ul>
 * <li>move questions up/down</li>
 * <li>select/deselect all</li>
 * <li>maximise/minimise all</li>
 * <li>search</li>
 * <li>group selected questions</li>
 * </ul>
 * Notifies any added action listeners of button presses (or search event).
 * @author Thomas Everingham
 * @version 1.0
 */
public class QuestionListToolbar extends PaintedJPanel {

    /**
     * Select all questions button was pressed.
     */
    public static final int SELECT_ALL = 0;
    /**
     * De-select all questions button was pressed.
     */
    public static final int SELECT_NONE = 1;
    /**
     * Expand all questions button was pressed.
     */
    public static final int EXPAND_ALL = 2;
    /**
     * Minimise all questions button was pressed.
     */
    public static final int MINIMISE_ALL = 3;
    /**
     * Move all selected questions up one button was pressed.
     */
    public static final int MOVE_UP = 4;
    /**
     * Move all selected questions down one button was pressed.
     */
    public static final int MOVE_DOWN = 5;
    /**
     * Group all selected questions button was pressed.
     */
    public static final int GROUP_SELECTION = 6;
    /**
     * Copy all selected questions button was pressed.
     */
    public static final int COPY = 7;
    /**
     * Paste questions from the clipboard button was pressed.
     */
    public static final int PASTE = 8;
    /**
     * Cut all selected questions button was pressed.
     */
    public static final int CUT = 9;
    /**
     * The search button was pressed, or the user hit the enter key in the search text field.
     */
    public static final int SEARCH = 10;
    /**
     * The create new question button was pressed.
     */
    public static final int NEW_QUESTION = 11;
    /**
     * Delete all selected questions button was pressed.
     */
    public static final int DELETE_SELECTED = 12;
    /**
     * Button to select all questions in the question list.
     */
    protected JButton btnSelectAll;
    /**
     * Button to deselect all questions in the question list.
     */
    protected JButton btnSelectNone;
    /**
     * Button to expand all questions in the question list.
     */
    protected JButton btnExpandAll;
    /**
     * Button to minimise all questions in the question list.
     */
    protected JButton btnMinimiseAll;
    /**
     * Button to move any currently selected questions up one space.
     */
    protected JButton btnMoveUp;
    /**
     * Button to move any currently selected questions down one space.
     */
    protected JButton btnMoveDown;
    /**
     * Button to group all currently selected questions into one contiguous
     * sequence.
     */
    protected JButton btnGroupSelection;
    /**
     * Button to copy a question or the selected questions.
     */
    protected JButton btnCopy;
    /**
     * Button the paste a question or question(s) from the clipboard.
     */
    protected JButton btnPaste;
    /**
     * Button to cut a question or the selected questions.
     */
    protected JButton btnCut;
    /**
     * Button to search for the current search term.
     */
    protected JButton btnSearchAction;
    /**
     * Button used to insert a new question container.
     */
    protected JButton btnNew;
    /**
     * Button used to delete the currently selected questions.
     */
    protected JButton btnDelete;
    /**
     * Field for searching the questions in the question list.
     */
    protected SimpleTextField txtSearch;
    /**
     * The action listeners that have been added to the question list toolbar.
     */
    protected ActionListener[] actionListeners = new ActionListener[0];

    /**
     * Creates a new instance of the <code>QuestionListToolbar</code>.
     */
    public QuestionListToolbar() {
        setUpGUI();
        txtSearch.addKeyListener(new SearchTextListener());
    }

    private void setUpGUI() {
        this.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));
        this.setBorder(BorderFactory.createEmptyBorder(1, 2, 1, 1));
        this.setOpaque(false);
        this.setSize(330, 24);
        this.setMinimumSize(new Dimension(400, 26));
        this.setPreferredSize(new Dimension(400, 26));

        btnNew = createButton("/resources/icons/add.png", new NewAction(), this);
        btnNew.setToolTipText("Add a new question");

        btnDelete = createButton("/resources/icons/remove.png", new DeleteAction(), this);
        btnDelete.setToolTipText("Delete the currently selected questions");

        this.add(Box.createHorizontalStrut(1));

        btnCopy = createButton("/resources/icons/copy.png", new CopyAction(), this);
        btnCopy.setToolTipText("Copy the currently selected questions");

        btnCut = createButton("/resources/icons/cut.png", new CutAction(), this);
        btnCut.setToolTipText("Cut the currently selected questions");

        btnPaste = createButton("/resources/icons/paste.png", new PasteAction(), this);
        btnPaste.setToolTipText("Paste questions from the clipboard");

        this.add(Box.createHorizontalStrut(1));

        btnSelectAll = createButton("/resources/icons/selectAll.png", new SelectAllAction(), this);
        btnSelectAll.setToolTipText("Select all questions");

        btnSelectNone = createButton("/resources/icons/selectNone.png", new SelectNoneAction(), this);
        btnSelectNone.setToolTipText("Select no questions");

        btnExpandAll = createButton("/resources/icons/expand.png", new ExpandAllAction(), this);
        btnExpandAll.setToolTipText("Expand all questions");

        btnMinimiseAll = createButton("/resources/icons/minimise.png", new MinimiseAllAction(), this);
        btnMinimiseAll.setToolTipText("Contract all questions");

        btnGroupSelection = createButton("/resources/icons/groupQuestions.png", new GroupSelectionAction(), this);
        btnGroupSelection.setToolTipText("Move selected questions next to one another");

        this.add(Box.createHorizontalStrut(1));

        btnMoveUp = createButton("/resources/icons/moveUp.png", new MoveUpAction(), this);
        btnMoveUp.setToolTipText("Move selected questions up one");

        btnMoveDown = createButton("/resources/icons/moveDown.png", new MoveDownAction(), this);
        btnMoveDown.setToolTipText("Move selected questions down one");

        txtSearch = new SimpleTextField();
        txtSearch.setMinimumSize(new Dimension(100, 22));
        txtSearch.setPreferredSize(new Dimension(100, 22));
        txtSearch.setBorder(BorderFactory.createEmptyBorder());
        txtSearch.setFont(ThemeConstants.tableCellEditorFont);
        this.add(txtSearch);

        btnMoveDown = createButton("/resources/icons/search.png", new SearchAction(), this);
        btnMoveDown.setToolTipText("Search for all questions containing these terms");


    }

    /**
     * Instantiates a JButton and sets required properties. Loads the specified resource
     * as the button's icon.
     *
     * @param resourceName the image resource used as this button's icon.
     * @return the created JButton.
     */
    private JButton createButton(String resourceName, AbstractAction action, JComponent addTo) {
        JButton btn = new JButton();
        btn.setAction(action);
        btn.setIcon(new ImageIcon(RichTextToolbar.class.getResource(resourceName)));
        btn.setBorder(BorderFactory.createEmptyBorder(3, 3, 3, 3));
        btn.setSize(btn.getIcon().getIconWidth(), btn.getIcon().getIconHeight());
        addTo.add(btn);
        return btn;
    }

    /**
     * Gets the content of the search text field.
     * @return the content of the search text field.
     */
    public String getSearchText() {
        return txtSearch.getText();
    }

    /**
     * Sets the search text field's text to an empty string.
     */
    public void clearSearchText() {
        txtSearch.setText("");
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

    private class SelectAllAction extends AbstractAction {

        public void actionPerformed(ActionEvent e) {
            for (int i = 0; i < actionListeners.length; i++) {
                actionListeners[i].actionPerformed(new ActionEvent(QuestionListToolbar.this, SELECT_ALL, "SelectAll"));
            }
        }
    }

    private class SelectNoneAction extends AbstractAction {

        public void actionPerformed(ActionEvent e) {
            for (int i = 0; i < actionListeners.length; i++) {
                actionListeners[i].actionPerformed(new ActionEvent(QuestionListToolbar.this, SELECT_NONE, "SelectNone"));
            }
        }
    }

    private class ExpandAllAction extends AbstractAction {

        public void actionPerformed(ActionEvent e) {
            for (int i = 0; i < actionListeners.length; i++) {
                actionListeners[i].actionPerformed(new ActionEvent(QuestionListToolbar.this, EXPAND_ALL, "ExpandAll"));
            }
        }
    }

    private class MinimiseAllAction extends AbstractAction {

        public void actionPerformed(ActionEvent e) {
            for (int i = 0; i < actionListeners.length; i++) {
                actionListeners[i].actionPerformed(new ActionEvent(QuestionListToolbar.this, MINIMISE_ALL, "MinimiseAll"));
            }
        }
    }

    private class MoveUpAction extends AbstractAction {

        public void actionPerformed(ActionEvent e) {
            for (int i = 0; i < actionListeners.length; i++) {
                actionListeners[i].actionPerformed(new ActionEvent(QuestionListToolbar.this, MOVE_UP, "MoveUp"));
            }
        }
    }

    private class MoveDownAction extends AbstractAction {

        public void actionPerformed(ActionEvent e) {
            for (int i = 0; i < actionListeners.length; i++) {
                actionListeners[i].actionPerformed(new ActionEvent(QuestionListToolbar.this, MOVE_DOWN, "MoveDown"));
            }
        }
    }

    private class GroupSelectionAction extends AbstractAction {

        public void actionPerformed(ActionEvent e) {
            for (int i = 0; i < actionListeners.length; i++) {
                actionListeners[i].actionPerformed(new ActionEvent(QuestionListToolbar.this, GROUP_SELECTION, "GroupSelection"));
            }
        }
    }

    private class SearchAction extends AbstractAction {

        public void actionPerformed(ActionEvent e) {
            for (int i = 0; i < actionListeners.length; i++) {
                actionListeners[i].actionPerformed(new ActionEvent(QuestionListToolbar.this, SEARCH, "Search"));
            }
        }
    }

    private class NewAction extends AbstractAction {

        public void actionPerformed(ActionEvent e) {
            for (int i = 0; i < actionListeners.length; i++) {
                actionListeners[i].actionPerformed(new ActionEvent(QuestionListToolbar.this, NEW_QUESTION, "NewQuestion"));
            }
        }
    }

    private class DeleteAction extends AbstractAction {

        public void actionPerformed(ActionEvent e) {
            for (int i = 0; i < actionListeners.length; i++) {
                actionListeners[i].actionPerformed(new ActionEvent(QuestionListToolbar.this, DELETE_SELECTED, "DeleteSelected"));
            }
        }
    }

    private class CutAction extends AbstractAction {

        public void actionPerformed(ActionEvent e) {
            for (int i = 0; i < actionListeners.length; i++) {
                actionListeners[i].actionPerformed(new ActionEvent(QuestionListToolbar.this, CUT, "Cut"));
            }
        }
    }

    private class CopyAction extends AbstractAction {

        public void actionPerformed(ActionEvent e) {
            for (int i = 0; i < actionListeners.length; i++) {
                actionListeners[i].actionPerformed(new ActionEvent(QuestionListToolbar.this, COPY, "Copy"));
            }
        }
    }

    private class PasteAction extends AbstractAction {

        public void actionPerformed(ActionEvent e) {
            for (int i = 0; i < actionListeners.length; i++) {
                actionListeners[i].actionPerformed(new ActionEvent(QuestionListToolbar.this, PASTE, "Paste"));
            }
        }
    }

    private class SearchTextListener implements KeyListener {

        public void keyTyped(KeyEvent ke) {
        }

        public void keyPressed(KeyEvent ke) {
            if (ke.getKeyCode() == KeyEvent.VK_ENTER) {
                new SearchAction().actionPerformed(null);
            }
        }

        public void keyReleased(KeyEvent ke) {
        }
    }
}
