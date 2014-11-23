/*
 * AnsFieldSimpleMultiChoice.java
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
package org.ingatan.component.answerfield;

import org.ingatan.ThemeConstants;
import org.ingatan.component.text.NumericJTextField;
import org.ingatan.component.text.SimpleTextField;
import org.ingatan.io.IOManager;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.ListIterator;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import org.jdom.DataConversionException;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.jdom.output.XMLOutputter;

/**
 * A multiple choice style answer field that is more simple than the Rich Text multiple choice
 * answer field. It only allows single line answers and no formatting. Only one answer can be selected
 * as correct.
 *
 * @author Thomas Everingham
 * @version 1.0
 */
public class AnsFieldSimpleMultiChoice extends JPanel implements IAnswerField {

    /**
     * Button group for the answer radio buttons
     */
    private ButtonGroup buttonGroup = new ButtonGroup();
    /**
     * The array list of OptionEntry objects. These are individual answer options
     * that have been added to the answer field.
     */
    private ArrayList<OptionEntry> optionEntries = new ArrayList<OptionEntry>(2);
    /**
     * Button for adding a new option to the list of possible answers
     */
    private JButton btnAddOption = new JButton(new AddAction());
    /**
     * Numeric text field that allows the user to set the maximum number of marks that can be awarded.
     */
    private NumericJTextField txtMaxMarks = new NumericJTextField(2);
    /**
     * If in the library context, this flag is true, otherwise behave as though in quiz time.
     */
    private boolean libraryContext = false;
    /**
     * JLabel for the numeric only text field allowing the user to set the maximum number of marks awarded for a particular
     * multi choice selection. This is the number of marks that corresponds to an option that has % marks set to 1.0.
     */
    private JLabel lblMaxMarks = new JLabel("Max marks: ");
    /**
     * Reference to the correct option.
     */
    private OptionEntry correctEntry;
    /**
     * Listens for tab or enter and shifts the focus to the next text field along.
     */
    private OptionEntryKeyListener optionEntryKeyListener = new OptionEntryKeyListener();
    /**
     * Action listener assigned by the quiz window. actionPerformed is called on this
     * if the user double clicks an option (radio button).
     */
    private ActionListener actionListener = null;
    /**
     * Listens for a double click on any radio button option. Fires the actionPerformed event of the
     * <code>actionListener</code> assigned by the quiz window to trigger the continue quiz action.
     */
    private OptionMouseListener optionMouseListener = new OptionMouseListener();

    /**
     * Creates a new instance of <code>AnsFieldMultiChoice</code>.
     */
    public AnsFieldSimpleMultiChoice() {
        super();

        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        //default will be three options 
        optionEntries.add(new OptionEntry());
        optionEntries.add(new OptionEntry());
        optionEntries.add(new OptionEntry());

        lblMaxMarks.setFont(ThemeConstants.niceFont.deriveFont(Font.ITALIC));

        txtMaxMarks.setMaximumSize(new Dimension(50, 20));
        txtMaxMarks.setBorder(BorderFactory.createLineBorder(ThemeConstants.backgroundUnselected.darker()));

        this.setMaximumSize(new Dimension(600, 1000));
        this.setOpaque(false);

        rebuild();
    }

    /**
     * Rebuild this answer field from the content of the <code>optionEntries </code>
     * <code>ArrayList</code>.
     */
    private void rebuild() {
        this.removeAll();

        if (libraryContext) {
            Box horiz = Box.createHorizontalBox();
            horiz.add(lblMaxMarks);
            horiz.add(txtMaxMarks);
            horiz.setAlignmentX(LEFT_ALIGNMENT);
            this.add(horiz);
            this.add(Box.createVerticalStrut(6));

            //add all of the option fields that exist in optionEntries
            ListIterator<OptionEntry> iterate = optionEntries.listIterator();
            OptionEntry curEntry;
            while (iterate.hasNext()) {
                curEntry = iterate.next();
                curEntry.setEditContext(true);
                this.add(curEntry);
                this.add(Box.createVerticalStrut(8));
            }

            btnAddOption.setAlignmentX(LEFT_ALIGNMENT);

            if (correctEntry != null) {
                buttonGroup.setSelected(correctEntry.radioButton.getModel(), true);
            }

            //add the 'add option' button
            this.add(btnAddOption);

        } else { //BUILD FOR DISPLAY IN QUIZ -------------------------
            //add all of the option fields that exist in optionEntries
            Collections.shuffle(optionEntries, IOManager.random);
            ListIterator<OptionEntry> iterate = optionEntries.listIterator();
            OptionEntry curEntry;
            while (iterate.hasNext()) {
                curEntry = iterate.next();
                curEntry.setSelected(false);
                curEntry.setEditContext(false);
                //remove mouse listener if previously added, does not matter if it has never been added
                curEntry.radioButton.removeMouseListener(optionMouseListener);
                curEntry.radioButton.addMouseListener(optionMouseListener);
                this.add(curEntry);
                this.add(Box.createVerticalStrut(8));
            }
        }
    }

    public String getDisplayName() {
        return "Simple Multiple Choice";
    }

    public boolean isOnlyForAnswerArea() {
        return true;
    }

    public float checkAnswer() {
        ListIterator<OptionEntry> iterate = optionEntries.listIterator();
        OptionEntry curEntry;
        while (iterate.hasNext()) {
            curEntry = iterate.next();
            if (curEntry.isSelected()) {
                //if the selected entry is also the correct entry
                if (curEntry == correctEntry) {
                    return 1.0f;
                }
            }
        }
        return 0.0f;
    }

    public int getMaxMarks() {
        return txtMaxMarks.getValue();
    }

    public int getMarksAwarded() {
        return (int) ((float) getMaxMarks() * checkAnswer());
    }

    public void displayCorrectAnswer() {
        ListIterator<OptionEntry> iterate = optionEntries.listIterator();
        while (iterate.hasNext()) {
            iterate.next().radioButton.setEnabled(false);
        }

        correctEntry.setBorder(BorderFactory.createLineBorder(ThemeConstants.quizPassGreen, 2));
    }

    public void setContext(boolean inLibraryContext) {
        libraryContext = inLibraryContext;
        rebuild();
    }

    public String writeToXML() {
        //create JDOM document and root element
        Document doc = new Document();
        Element rootElement = new Element(this.getClass().getName());
        doc.setRootElement(rootElement);
        rootElement.setAttribute("maxMarks", txtMaxMarks.getText());
        //version attribute allows for future versions of this answer field to be back compatible.
        //especially important for these default answer fields!
        rootElement.setAttribute("version", "1.0");

        //add an entry for each option
        ListIterator<OptionEntry> iterate = optionEntries.listIterator();
        OptionEntry curOption;
        while (iterate.hasNext()) {
            curOption = iterate.next();
            rootElement.addContent(new Element("optionEntry").setAttribute("correct", String.valueOf(buttonGroup.isSelected(curOption.radioButton.getModel()))).setText(curOption.getText()));
        }

        //return the XML document as String representation
        return new XMLOutputter().outputString(doc);
    }

    public void readInXML(String xml) {

        //nothing to parse, so leave
        if (xml.trim().equals("") == true) {
            return;
        }


        //try to build document from input string
        SAXBuilder sax = new SAXBuilder();
        Document doc = null;
        try {
            doc = sax.build(new StringReader(xml));
        } catch (JDOMException ex) {
            Logger.getLogger(AnsFieldSimpleMultiChoice.class.getName()).log(Level.SEVERE, "While trying to create a JDOM document in the readInXML method.", ex);
        } catch (IOException ex) {
            Logger.getLogger(AnsFieldSimpleMultiChoice.class.getName()).log(Level.SEVERE, "While trying to create a JDOM document in the readInXML method.", ex);
        }

        //nothing to parse, so leave
        if (doc == null) {
            return;
        }

        //empty any elements that currently exist (if reading from XML, don't want any other data)
        optionEntries = new ArrayList<OptionEntry>();

        //set max marks value
        txtMaxMarks.setText(doc.getRootElement().getAttributeValue("maxMarks"));

        //add all of the option entries defined by xml data
        ListIterator<Element> iterate = doc.getRootElement().getChildren("optionEntry").listIterator();
        Element curEl;
        OptionEntry curEntry;
        while (iterate.hasNext()) {
            curEl = iterate.next();
            curEntry = new OptionEntry();
            try {
                boolean isSelected = curEl.getAttribute("correct").getBooleanValue();
                if (isSelected) {
                    correctEntry = curEntry;
                    buttonGroup.setSelected(curEntry.radioButton.getModel(), true);
                }
                curEntry.setText(curEl.getText());

            } catch (DataConversionException ex) {
                Logger.getLogger(AnsFieldSimpleMultiChoice.class.getName()).log(Level.SEVERE, "While trying to create an OptionEntry for an xml entry in the readInXML method.", ex);
            }

            optionEntries.add(curEntry);
        }


        //rebuild the answer field
        rebuild();

    }

    public String getParentLibraryID() {
        return ""; //not implemented as this answer field does not required Image IO.
    }

    public void setParentLibraryID(String id) {
        return; //not implemented as this answer field does not required Image IO.
    }

    public void setQuizContinueListener(ActionListener listener) {
        actionListener = listener;
    }

    public void resaveImagesAndResources(String lib) {
        //not implemented as true/false does not use images or file resources
    }

    public class AddAction extends AbstractAction {

        public AddAction() {
            super("Add Option");
        }

        public void actionPerformed(ActionEvent e) {
            optionEntries.add(new OptionEntry());
            rebuild();

            if (optionEntries.size() == 10) {
                btnAddOption.setEnabled(false);
            }
        }
    }

    /**
     * Listens for double click on a radio button as an event triggering the
     * quiz continue action.
     */
    private class OptionMouseListener implements MouseListener {

        public void mouseClicked(MouseEvent e) {
            if ((actionListener != null) && (e.getClickCount() == 2) && (libraryContext == false)) {
                actionListener.actionPerformed(new ActionEvent(AnsFieldSimpleMultiChoice.this, 0, ""));
            }
        }

        public void mousePressed(MouseEvent e) {}
        public void mouseReleased(MouseEvent e) {}
        public void mouseEntered(MouseEvent e) {}
        public void mouseExited(MouseEvent e) {}

    }

    /**
     * One multiple choice answer entry
     */
    private class OptionEntry extends JPanel {

        /**
         * Allows the multiple choice answer to be selected (means nothing at edit time)
         */
        private JRadioButton radioButton = new JRadioButton();
        /**
         * Text entry area
         */
        private SimpleTextField txtArea = new SimpleTextField();
        /**
         * Button to remove a this entry
         */
        private JButton btnRemove = new JButton(new RemoveAction());

        public OptionEntry() {
            super();
            this.setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
            this.setOpaque(false);
            this.setAlignmentX(LEFT_ALIGNMENT);

            btnRemove.setMaximumSize(new Dimension(15, 15));
            btnRemove.setPreferredSize(btnRemove.getMaximumSize());
            btnRemove.setMargin(new Insets(0, 0, 0, 0));
            btnRemove.setAlignmentX(CENTER_ALIGNMENT);
            btnRemove.setForeground(ThemeConstants.textColour);
            btnRemove.setFont(ThemeConstants.niceFont.deriveFont(Font.BOLD));

            btnAddOption.setFont(ThemeConstants.niceFont);
            btnAddOption.setMargin(new Insets(1, 1, 1, 1));

            buttonGroup.add(radioButton);
            radioButton.setOpaque(false);
            radioButton.setAlignmentX(CENTER_ALIGNMENT);

            this.add(radioButton);
            this.add(txtArea);
            this.add(btnRemove);
            txtArea.setMaximumSize(new Dimension(600, 150));
            txtArea.setMinimumSize(new Dimension(200, 50));

            txtArea.setFocusTraversalKeysEnabled(false);
            txtArea.addKeyListener(optionEntryKeyListener);
            txtArea.setBorder(BorderFactory.createLineBorder(ThemeConstants.borderUnselectedHover));
        }

        /**
         * Checks whether the radio button associated with this <code>OptionEntry</code> is
         * selected. Radio buttons of all option entries are added to the <code>ButtonGroup</code> owned
         * by the parent instance of <code>AnsFieldMultiChoice</code>. So if selected, this <code>OptionEntry</code>
         * is assumed to be the correct answer.
         * @return <code>true</code> if the radio button associated with this <code>OptionEntry</code> is selected.
         */
        public boolean isSelected() {
            return radioButton.isSelected();
        }

        /**
         * Sets the radio button associated with this entry as selected. Radio buttons of all option entries are added to the <code>ButtonGroup</code> owned
         * by the parent instance of <code>AnsFieldMultiChoice</code>. So if selected, this <code>OptionEntry</code>
         * is assumed to be the correct answer.
         */
        public void setSelected(boolean selected) {
            radioButton.setSelected(true);
        }

        /**
         * Gets the return value of the <code>getRichText</code> method of the
         * <code>RichTextArea</code> instance associated with this <code>OptionEntry</code>.
         * @return the plain text representation of the rich text contained by the <code>RichTextArea</code> instance associated with this <code>OptionEntry</code>.
         */
        public String getText() {
            return txtArea.getText();
        }

        /**
         * Sets the rich text of the <code>RichTextArea</code> instance associated with this <code>OptionEntry</code>.
         * @param text the plain text representation of the rich text to be set.
         */
        public void setText(String text) {
            txtArea.setText(text);
        }

        /**
         * Set how the option entry should be shown - as its editor mode or as its
         * display mode.
         * @param edit <code>true</code> if the optionEntry should be shown in its editor mode
         */
        public void setEditContext(boolean edit) {
            btnRemove.setVisible(edit);
            txtArea.setEditable(edit);
            txtArea.setFocusable(edit);
            txtArea.setOpaque(edit);

            if (!edit) {
                txtArea.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 0, ThemeConstants.borderUnselected));
            } else {
                txtArea.setBorder(BorderFactory.createEtchedBorder());
            }
        }

        /**
         * Performed when the remove button is pressed for this particular
         * possible answer option.
         */
        private class RemoveAction extends AbstractAction {

            public RemoveAction() {
                super("X");
            }

            public void actionPerformed(ActionEvent e) {
                //this field must have at least 2 options, if it does not, then tell the user and then return.
                if (optionEntries.size() <= 2) {
                    JOptionPane.showMessageDialog(AnsFieldSimpleMultiChoice.this, "There must be at least two options in this answer field. Otherwise\n"
                            + "it wouldn't be very multi-choice, now, would it? :)", "Cannot Remove Option", JOptionPane.INFORMATION_MESSAGE);
                    return;
                }

                //remove this option entry, and rebuild the answer field
                optionEntries.remove(OptionEntry.this);
                rebuild();

                //if there are less than 10 options in the list, ensure the add option
                //button is enabled so more can be added if required.
                if (optionEntries.size() < 10) {
                    btnAddOption.setEnabled(true);
                }
            }
        }
    }

    private class OptionEntryKeyListener implements KeyListener {

        public void keyTyped(KeyEvent e) {
        }

        public void keyPressed(KeyEvent e) {
            if ((e.getKeyCode() == KeyEvent.VK_TAB) || (e.getKeyCode() == KeyEvent.VK_ENTER)) {
                OptionEntry src = (OptionEntry) ((SimpleTextField) e.getSource()).getParent();
                optionEntries.get((optionEntries.indexOf(src) == optionEntries.size() - 1) ? 0 : optionEntries.indexOf(src) + 1).txtArea.requestFocus();
            }
        }

        public void keyReleased(KeyEvent e) {
        }
    }
}
