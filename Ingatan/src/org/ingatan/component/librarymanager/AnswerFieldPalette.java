/*
 * AnswerFieldPalette.java
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
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Enumeration;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

/**
 * The answer field palette provides an interface for selecting/inserting answer field components into the answer text
 * of questions. It populates its scrollpane with a new JButton for each answer field, which is labelled with the string returned
 * by the AnswerField interface's getDisplayName() method. Use an action listener to listen for interaction with this component. You can
 * determine which answer field button has been pressed by looking at the command string of the ActionEvent object - this will be the class
 * ID of the answer field, which can then be used to retrieve the class itself from the IOManager.
 * 
 * @author Thomas Everingham
 * @version 1.0
 */
public class AnswerFieldPalette extends PaintedJPanel {

    /**
     * Action listeners that have been added to this <code>AnswerFieldPalette</code>.
     */
    protected ActionListener[] actionListeners;
    /**
     * Scroller for all of the answer field buttons.
     */
    protected JScrollPane scroller;
    /**
     * Content pane of the scroller
     */
    protected JPanel scrollerContent = new JPanel();

    /**
     * Creates a new <code>AnswerFieldPalette</code>.
     */
    public AnswerFieldPalette() {
        //actions listeners that have been added to this palette
        actionListeners = new ActionListener[0];
        //create a scrolling container
        scroller = new JScrollPane();

        scroller.setBorder(BorderFactory.createEmptyBorder());
        this.add(scroller);

        //conent pane for the scroller
        scrollerContent.setOpaque(false);
        scrollerContent.setLayout(new BoxLayout(scrollerContent, BoxLayout.Y_AXIS));

        scroller.setViewportView(scrollerContent);
        scroller.setMinimumSize(new Dimension(180, 100));
        scroller.setPreferredSize(new Dimension(180, 270));
        scroller.getViewport().setOpaque(false);
        scroller.setOpaque(false);

        scroller.setColumnHeaderView(createButton(new EditAnswerFieldsAction()));
        //populate the scrollerContent with answer field buttons
        buildAnswerFieldList();

        scroller.validate();
        this.validate();
        this.repaint();
    }

    /**
     * Builds or re-builds the answer field list from the IOManager's class ID enumeration
     */
    public void buildAnswerFieldList() {
        scrollerContent.removeAll();
        //separates the answer field options with the 'edit fields' button
        scrollerContent.add(Box.createVerticalStrut(6));

        //enumerate the answer field classes
        Enumeration classIDEnum = IOManager.getAnswerFieldClassIDs();

        //add a button for each of the answer fields
        while (classIDEnum.hasMoreElements()) {
            scrollerContent.add(createButton(new AnswerFieldButtonAction((String) classIDEnum.nextElement())));
        }

        scrollerContent.setPreferredSize(scrollerContent.getLayout().minimumLayoutSize(scrollerContent));
    }

    /**
     * Creates buttons with standard properties.
     * @param action the action that should be performed when this button is clicked.
     * @return a JButton with standard properties and specified action set.
     */
    public JButton createButton(AbstractAction action) {
        JButton retVal = new JButton(action);
        retVal.setMargin(new Insets(3, 3, 3, 3));
        retVal.setFocusable(false);
        retVal.setPreferredSize(new Dimension(150, 25));
        retVal.setMinimumSize(new Dimension(150, 25));
        retVal.setMaximumSize(new Dimension(150, 25));
        retVal.setFont(ThemeConstants.niceFont);
        return retVal;
    }

    @Override
    public void setMinimumSize(Dimension d) {
        super.setMinimumSize(d);
        scroller.setMinimumSize(d);
    }

    @Override
    public void setMaximumSize(Dimension d) {
        super.setMaximumSize(d);
        scroller.setMaximumSize(d);
    }

    @Override
    public void setPreferredSize(Dimension d) {
        super.setPreferredSize(d);
        scroller.setPreferredSize(d);
    }

    

    /**
     * Adds an <code>ActionListener</code> to this <code>AnswerFieldPalette</code> instance.
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
     * Removes a <code>ActionListener</code> from this <code>AnswerFieldPalette</code> instance.
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

    /**
     * Action for the answer field buttons. Notifies all action listeners that an answer field
     * button was pressed, and provides the classID of the answer field as the action's command string.
     */
    private class AnswerFieldButtonAction extends AbstractAction {

        String classID;

        public AnswerFieldButtonAction(String classID) {
            super();
            this.classID = classID;
            Class c = IOManager.getAnswerFieldClass(classID);
            String name = "";
            try {
                name = ((IAnswerField) c.newInstance()).getDisplayName();
            } catch (InstantiationException ex) {
                Logger.getLogger(AnswerFieldPalette.class.getName()).log(Level.SEVERE, "When an answer field button was pressed", ex);
            } catch (IllegalAccessException ex) {
                Logger.getLogger(AnswerFieldPalette.class.getName()).log(Level.SEVERE, "When an answer field button was pressed", ex);
            }
            super.putValue(NAME, name);
        }

        public void actionPerformed(ActionEvent e) {
            for (int i = 0; i < actionListeners.length; i++) {
                actionListeners[i].actionPerformed(new ActionEvent(AnswerFieldPalette.this, 0, classID));
            }
        }
    }

    private class EditAnswerFieldsAction extends AbstractAction {

        public EditAnswerFieldsAction() {
            super("Edit Answer Fields List");
        }

        public void actionPerformed(ActionEvent e) {
            new EditAnswerFieldsDialog().setVisible(true);
            buildAnswerFieldList();
            AnswerFieldPalette.this.validate();
            AnswerFieldPalette.this.repaint();
        }
    }
}
