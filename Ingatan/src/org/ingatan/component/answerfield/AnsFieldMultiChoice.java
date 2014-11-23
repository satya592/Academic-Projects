/*
 * AnsFieldMultiChoice.java
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
import org.ingatan.component.image.ImageAcquisitionDialog;
import org.ingatan.component.text.EmbeddedGraphic;
import org.ingatan.component.text.EmbeddedImage;
import org.ingatan.component.text.EmbeddedMathTeX;
import org.ingatan.component.text.NumericJTextField;
import org.ingatan.component.text.RichTextArea;
import org.ingatan.component.text.RichTextToolbar;
import org.ingatan.event.RichTextToolbarEvent;
import org.ingatan.event.RichTextToolbarListener;
import org.ingatan.io.IOManager;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.ListIterator;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.AttributeSet;
import javax.swing.text.StyleConstants;
import org.ingatan.component.text.GeneralRichTextTransferHandler;
import org.jdom.DataConversionException;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.jdom.output.XMLOutputter;

/**
 * A multiple choice style answer field that supports rich text for each possible
 * selection, from 2 to 10 possible answers, and a graded marking scheme if desired.
 *
 * @author Thomas Everingham
 * @version 1.0
 */
public class AnsFieldMultiChoice extends JPanel implements IAnswerField {

    /**
     * Listens for focus change in any of option text areas and hides/shows the
     * rich text toolbar accordingly.
     */
    private static TextFocusListener textFocusListener = new TextFocusListener();
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
     * The ID of the library within which this answer field exists. This is important for accessing images. It is originally passed to
     * the answer field when it is inserted by the Answer Field Palette in the Library Manager Window, through the setParentLibraryID method.
     */
    private String parentLibID = "";
    /**
     * Listens for INSERT_PICTURE events on the <code>RichTextToolbar</code> belonging to each <code>RichTextArea</code> in
     * the <code>OptionEntry</code>s. Inserts any aquired image into the corresponding <code>RichTextArea</code>.
     */
    private TextToolbarListener textToolbarListener = new TextToolbarListener();
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
    public AnsFieldMultiChoice() {
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

            //add the 'add option' button
            this.add(btnAddOption);

        } else { //BUILD FOR DISPLAY IN QUIZ -------------------------
            //add all of the option fields that exist in optionEntries
            Collections.shuffle(optionEntries, IOManager.random);
            ListIterator<OptionEntry> iterate = optionEntries.listIterator();
            OptionEntry curEntry;
            while (iterate.hasNext()) {
                curEntry = iterate.next();
                curEntry.setEditContext(false);
                //remove mouse listener in case it has previously been added, doesn't matter if it has never been added
                curEntry.radioButton.removeMouseListener(optionMouseListener);
                curEntry.radioButton.addMouseListener(optionMouseListener);
                this.add(curEntry);
                this.add(Box.createVerticalStrut(8));
            }
        }
    }

    public String getDisplayName() {
        return "Rich Multiple Choice";
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
                float retVal = (float) curEntry.getMarks();
                if (retVal > 1.0) {
                    return 1.0f;
                } else {
                    return retVal;
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
        //highlight any answer with more than 0.5 correctness
        this.setEnabled(false);

        Iterator<OptionEntry> iterate = optionEntries.iterator();
        OptionEntry curEntry;

        while (iterate.hasNext()) {
            curEntry = iterate.next();
            curEntry.radioButton.setEnabled(false);
            if (curEntry.getMarks() == 1.0f) {
                curEntry.setBorder(BorderFactory.createLineBorder(ThemeConstants.quizPassGreen, 2));
            } else if (curEntry.getMarks() >= 0.5f) {
                curEntry.setBorder(BorderFactory.createLineBorder(ThemeConstants.alrightAnswerOrange, 2));
            }
        }
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
        rootElement.setAttribute("parentLibID", getParentLibraryID());
        rootElement.setAttribute("maxMarks", txtMaxMarks.getText());
        //version field allows future versions of this field to be back compatible.
        //especially important for default fields!
        rootElement.setAttribute("version", "1.0");

        //add an entry for each option
        ListIterator<OptionEntry> iterate = optionEntries.listIterator();
        OptionEntry curOption;
        while (iterate.hasNext()) {
            curOption = iterate.next();
            rootElement.addContent(new Element("optionEntry").setAttribute("selected", String.valueOf(curOption.isSelected())).setAttribute("pctMarks", String.valueOf(curOption.getMarks())).setText(curOption.getRichText()));
        }

        //return the XML document as String representation
        return new XMLOutputter().outputString(doc);
    }

    public void readInXML(String xml) {

        if (xml == null) {
            return;
        }


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
            Logger.getLogger(AnsFieldMultiChoice.class.getName()).log(Level.SEVERE, "While trying to create a JDOM document in the readInXML method.", ex);
        } catch (IOException ex) {
            Logger.getLogger(AnsFieldMultiChoice.class.getName()).log(Level.SEVERE, "While trying to create a JDOM document in the readInXML method.", ex);
        }

        //nothing to parse, so leave
        if (doc == null) {
            return;
        }

        //empty any elements that currently exist (if reading from XML, don't want any other data)
        optionEntries = new ArrayList<OptionEntry>();

        parentLibID = doc.getRootElement().getAttributeValue("parentLibID");

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
                curEntry.setSelected(curEl.getAttribute("selected").getBooleanValue());
                curEntry.setMarks(curEl.getAttribute("pctMarks").getDoubleValue());
                curEntry.setRichText(curEl.getText().replace(RichTextArea.CHARCODE_OPENING_SQUARE_BRACKET, "[").replace(RichTextArea.CHARCODE_CLOSING_SQUARE_BRACKET, "]"));

            } catch (DataConversionException ex) {
                Logger.getLogger(AnsFieldMultiChoice.class.getName()).log(Level.SEVERE, "While trying to create an OptionEntry for an xml entry in the readInXML method.", ex);
            }

            optionEntries.add(curEntry);
        }


        //rebuild the answer field
        rebuild();

    }

    /**
     * This method generates a simple multiple choice list of unformatted text from an array of strings. Note: overwrites any
     * current data in the answer field.
     * A maximum of 10 entries are allowed for this answer field, and a minimum of 2. If less than 2 options
     * are specified, no changes will be made (returns straight away). If more than 10 options are specified, the first 10 will be added
     * and the following disregarded.<br>
     * <br>
     * Each entry must also have a corresponding element in the <code>percentageMarksPerOption</code> array. This
     * value must be between 0.0 and 1.0 inclusive, and indicates the proportion of <code>maxMarks</code> that will
     * be awarded if that particular option is chosen. <code>maxMarks</code> indicates the maximum number of marks
     * awarded for a particular option being chosen (i.e. where <code>percentageMarksPerOption</code>=1.0).<br>
     * <br>
     * If the <code>percentageMarksPerOption</code> array is matched to the size fo the <code>options</code> array
     * if it is not the same length by either inserting elements of value 0.0 or disregarding excess elements. If the
     * elements in this array are not valid (i.e. not between 0.0 and 1.0 inclusive), they are set to the nearest valid value, so
     * negative elements become 0.0 and elements > 1.0 are set to 1.0. If the maxMarks value is less than 0, it is set to 0.
     * @param options the array of choices to add. Each element in this array corresponds to one option.
     * @param percentageMarksPerOption proportion of <code>maxMarks</code> to award for the option described by the corresponding element
     * in the <code>options</code> array. See above for incorrect size behaviour.
     * @param maxMarks multiplier of the percentageMarksPerOption elements. Number of marks awarded if the <code>percetnageMarksPerOption</code> value is 1.0.
     */
    public void setSimpleOptions(String[] options, float[] percentageMarksPerOption, int maxMarks) {
        if (options.length < 2) {
            return;
        }

        //if the percentageMarksPerOption array is too short, then lengthen it and fill the new elements as 0.0f.
        if (percentageMarksPerOption.length < options.length) {
            float[] temp = new float[options.length];
            Arrays.fill(temp, 0.0f);
            System.arraycopy(percentageMarksPerOption, 0, temp, 0, percentageMarksPerOption.length);
            percentageMarksPerOption = temp;
        }

        //if the maxMarks value is invalid, fix it
        if (maxMarks < 0) {
            maxMarks = 0;
        }

        optionEntries = new ArrayList<OptionEntry>();

        OptionEntry curEntry;
        for (int i = 0; i < options.length && i <= 10; i++) {
            curEntry = new OptionEntry();
            curEntry.setRichText(options[i] + "[" + RichTextArea.TAG_DOCUMENT_END + "]");

            //ensure that the value for percentage marks per option is valid
            if (percentageMarksPerOption[i] < 0.0) {
                percentageMarksPerOption[i] = 0.0f;
            } else if (percentageMarksPerOption[i] > 1.0) {
                percentageMarksPerOption[i] = 1.0f;
            }

            curEntry.setMarks(percentageMarksPerOption[i]);
            this.txtMaxMarks.setText(String.valueOf(maxMarks));
            optionEntries.add(curEntry);
        }

        rebuild();
    }

    public String getParentLibraryID() {
        return parentLibID;
    }

    public void setParentLibraryID(String id) {
        parentLibID = id;
    }

    public void setQuizContinueListener(ActionListener listener) {
        actionListener = listener;
    }

    public void resaveImagesAndResources(String newLibraryID) {
        Iterator<OptionEntry> iterate = optionEntries.iterator();
        while (iterate.hasNext()) {
            traverseTextArea(iterate.next().getRichTextArea(), newLibraryID);
        }
    }

    private void traverseTextArea(RichTextArea txtArea, String newLibraryID) {
        //traverse the rich text area for any embedded images, and reset their parentLibrary values
        //as well as resaving resources to the new library
        int runCount;
        int paragraphCount = txtArea.getDocument().getDefaultRootElement().getElementCount();
        javax.swing.text.Element curEl = null;
        AttributeSet curAttr = null;

        for (int i = 0; i < paragraphCount; i++) {
            //each paragraph has 'runCount' runs
            runCount = txtArea.getDocument().getDefaultRootElement().getElement(i).getElementCount();
            for (int j = 0; j < runCount; j++) {
                curEl = txtArea.getDocument().getDefaultRootElement().getElement(i).getElement(j);
                curAttr = curEl.getAttributes();

                if (curEl.getName().equals(StyleConstants.ComponentElementName)) //this is a component
                {
                    //this run is a component. May be an answer field, picture or math text component.
                    Component o = (Component) curAttr.getAttribute(StyleConstants.ComponentAttribute);
                    if (o instanceof EmbeddedImage) {
                        IOManager.copyImage(((EmbeddedImage) o).getParentLibraryID(), ((EmbeddedImage) o).getImageID(), newLibraryID);
                        ((EmbeddedImage) o).setParentLibraryID(newLibraryID);

                    }
                }
            }
        }
    }

    /**
     * Listens for double click on a radio button as an event triggering the
     * quiz continue action.
     */
    private class OptionMouseListener implements MouseListener {

        public void mouseClicked(MouseEvent e) {
            if ((e.getClickCount() == 2) && (actionListener != null) && (libraryContext == false)) {
                actionListener.actionPerformed(new ActionEvent(AnsFieldMultiChoice.this, 0, ""));
            }
        }

        public void mousePressed(MouseEvent e) {}
        public void mouseReleased(MouseEvent e) {}
        public void mouseEntered(MouseEvent e) {}
        public void mouseExited(MouseEvent e) {}

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
        private RichTextArea txtArea = new RichTextArea();
        /**
         * Button to remove a this entry
         */
        private JButton btnRemove = new JButton(new RemoveAction());
        /**
         * Spinner for selecting the proportion of marks to award if this option is chosen.
         */
        private JSpinner spinnerMarks = new JSpinner(new SpinnerNumberModel(0.0, 0.0, 1.0, 0.1));
        /**
         * Label for the spinner. The spinner is used to select the proportion of marks to award if this option is chosen.
         */
        private JLabel lblPercentMarks = new JLabel("% of marks:");

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
            btnRemove.setToolTipText("Delete this option.");

            btnAddOption.setFont(ThemeConstants.niceFont);
            btnAddOption.setMargin(new Insets(1, 1, 1, 1));

            spinnerMarks.setMaximumSize(new Dimension(50, 20));
            spinnerMarks.setPreferredSize(spinnerMarks.getMaximumSize());
            spinnerMarks.setAlignmentX(LEFT_ALIGNMENT);
            spinnerMarks.setBorder(BorderFactory.createLineBorder(ThemeConstants.backgroundUnselected.darker()));
            spinnerMarks.setToolTipText("The proportion of maximum marks to award for this option, e.g. if this is 0.5 and the max marks is 4, 2 marks will be awarded.");
            ((JSpinner.DefaultEditor) spinnerMarks.getEditor()).getTextField().setEditable(false);

            lblPercentMarks.setFont(ThemeConstants.niceFont);
            lblPercentMarks.setMaximumSize(new Dimension(100, 15));
            lblPercentMarks.setAlignmentX(LEFT_ALIGNMENT);

            buttonGroup.add(radioButton);
            radioButton.setOpaque(false);
            radioButton.setAlignmentX(CENTER_ALIGNMENT);

            txtArea.setToolbarVisible(false);
            txtArea.addFocusListener(textFocusListener);
            txtArea.getDocument().addDocumentListener(new TextAreaListener(txtArea));
            txtArea.getScroller().setBorder(BorderFactory.createLineBorder(ThemeConstants.backgroundUnselected.darker()));
            txtArea.getToolbar().addRichTextToolbarListener(textToolbarListener);
            txtArea.setTransferHandler(new GeneralRichTextTransferHandler());

            Box vert = Box.createVerticalBox();
            vert.add(radioButton);
            vert.add(btnRemove);
            this.add(vert);
            vert.setMaximumSize(new Dimension(25, 60));
            this.add(txtArea.getScroller());
            txtArea.getScroller().setMaximumSize(new Dimension(600, 150));
            txtArea.getScroller().setMinimumSize(new Dimension(200, 65));

            vert = Box.createVerticalBox();
            vert.add(lblPercentMarks);
            vert.add(spinnerMarks);
            vert.setMaximumSize(new Dimension(75, 60));
            this.add(Box.createHorizontalStrut(8));
            this.add(vert);
        }

        /**
         * Get the proportion of the maximum marks that should be awarded for this answer
         * @return the proportion of max marks awarded if this option entry is selected
         */
        public double getMarks() {
            return ((SpinnerNumberModel) spinnerMarks.getModel()).getNumber().doubleValue();
        }

        /**
         * Set the proportion of <code>maxMarks</code> that should be awarded if this option is chosen.
         * @param marks the proportion of <code>maxMarks</code> that should be awarded if this option is chosen.
         */
        public void setMarks(double marks) {
            if (marks > 1.0) {
                marks = 1.0;
            }
            if (marks < 0.0) {
                marks = 0.0;
            }

            ((SpinnerNumberModel) spinnerMarks.getModel()).setValue(marks);
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
        public String getRichText() {
            return txtArea.getRichText();
        }

        /**
         * Sets the rich text of the <code>RichTextArea</code> instance associated with this <code>OptionEntry</code>.
         * @param text the plain text representation of the rich text to be set.
         */
        public void setRichText(String text) {
            txtArea.setRichText(text);
        }

        /**
         * Returns the <code>RichTextArea</code> used by this <code>OptionEntry</code>. This
         * is particularly useful for access to insert content such as pictures.
         * @return the <code>RichTextArea</code> used by this <code>OptionEntry</code>.
         */
        public RichTextArea getRichTextArea() {
            return txtArea;
        }

        /**
         * Set how the option entry should be shown - as its editor mode or as its
         * display mode.
         * @param edit <code>true</code> if the optionEntry should be shown in its editor mode
         */
        public void setEditContext(boolean edit) {
            btnRemove.setVisible(edit);
            lblPercentMarks.setVisible(edit);
            spinnerMarks.setVisible(edit);
            txtArea.setToolbarVisible(false);
            txtArea.setEditable(edit);
            //if edit==false, this supresses the toolbar being shown by setting the area unfocussable
            txtArea.setFocusable(edit);
            txtArea.setOpaque(edit);

            if (!edit) {
                txtArea.getScroller().setBorder(BorderFactory.createMatteBorder(0, 0, 0, 0, ThemeConstants.borderUnselected));
            } else {
                txtArea.getScroller().setBorder(BorderFactory.createEtchedBorder());
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
                    JOptionPane.showMessageDialog(AnsFieldMultiChoice.this, "There must be at least two options in this answer field. Otherwise\n"
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

        public void resetSize(RichTextArea txtArea) {

            Dimension d = null;
            Rectangle r = null;
            javax.swing.text.Document doc = null;

            try {
                doc = txtArea.getDocument();

                d = txtArea.getPreferredSize();
                r = txtArea.modelToView(doc.getLength());

                d.height = r.y + r.height + 25;

                if (d.getHeight() < txtArea.getScroller().getMaximumSize().getHeight()) {
                    if (d.getHeight() < txtArea.getScroller().getMinimumSize().getHeight()) {
                        d.height = (int) txtArea.getScroller().getMinimumSize().getHeight();
                    }
                    txtArea.getScroller().setPreferredSize(d);
                } else {
                    txtArea.getScroller().setPreferredSize(txtArea.getScroller().getMaximumSize());
                }

                txtArea.getScroller().validate();
                AnsFieldMultiChoice.this.validate();

            } catch (Exception e2) {
                //ignore this exception, nothing can be done.
            }

        }

        private class TextAreaListener implements DocumentListener {

            RichTextArea listenee;
            Runnable resetSize;

            public TextAreaListener(RichTextArea listenee) {
                this.listenee = listenee;
                resetSize = new Runnable() {

                    public void run() {
                        resetSize(TextAreaListener.this.listenee);
                    }
                };

            }

            public void insertUpdate(DocumentEvent e) {
                SwingUtilities.invokeLater(resetSize);
            }

            public void removeUpdate(DocumentEvent e) {
                SwingUtilities.invokeLater(resetSize);
            }

            public void changedUpdate(DocumentEvent e) {
                SwingUtilities.invokeLater(resetSize);
            }
        }
    }

    private static class TextFocusListener implements FocusListener {

        public void focusGained(FocusEvent e) {
            ((RichTextArea) e.getComponent()).setToolbarVisible(true);
        }

        public void focusLost(FocusEvent e) {
            if (!(e.getOppositeComponent() instanceof RichTextToolbar)) {
                ((RichTextArea) e.getComponent()).setToolbarVisible(false);
            }
        }
    }

    /**
     * This toolbar listener just listens for the RichTextToolbarEvent.INSERT_PICTURE event ID.
     * The class takes care of inserting a picture from the many sources available, into a RichTextArea.
     */
    private class TextToolbarListener implements RichTextToolbarListener {

        public void buttonPressed(RichTextToolbarEvent e) {
            if (e.getEventID() == RichTextToolbarEvent.INSERT_PICTURE) {
                //check if we are in the answer field editor dialog
                if (AnsFieldMultiChoice.this.getRootPane().getParent() instanceof JDialog) {
                    JOptionPane.showMessageDialog(AnsFieldMultiChoice.this, "Cannot insert an image into the field in the edit answer field dialog. Cannot save\n"
                            + "an image as a default value for this field.", "Cannot Insert Image", JOptionPane.INFORMATION_MESSAGE);
                    return;
                }
                //open an image acquisition dialog
                ImageAcquisitionDialog imgDialog = IOManager.getImageAcquisitionDialog();
                imgDialog.setVisible(true);

                EmbeddedGraphic eg = null;
                //this is the value at position 0 in the imageAquisitionDialog's aquired image data array (the id of the image if it exists already)
                String id = "";
                //if the image is from a library, this is where it is from (ID)
                String fromLib = "";
                //if we're dealing with an image.
                if ((imgDialog.getAcquisitionSource() != ImageAcquisitionDialog.NONE) && (imgDialog.getAcquisitionSource() != ImageAcquisitionDialog.FROM_MATH_TEXT)) {
                    //FROM LIBRARY------------------------------------------------------------------------------
                    if (imgDialog.getAcquisitionSource() == ImageAcquisitionDialog.FROM_LIBRARY) {
                        fromLib = imgDialog.getAcquiredImageData().split("\n")[1];
                        id = imgDialog.getAcquiredImageData().split("\n")[0];
                        //if this is from the library to which this question belongs, then we do not need to resave it, simply insert it.
                        if (fromLib.equals(getParentLibraryID())) {
                            try {
                                eg = new EmbeddedImage(IOManager.loadImage(fromLib, id), id, getParentLibraryID());
                            } catch (IOException ex) {
                                Logger.getLogger(AnsFieldMultiChoice.class.getName()).log(Level.SEVERE, "IO Exception occurred while attempting to load an image through the IOManager\n"
                                        + "to create an EmbeddedImage for a RichTextArea contained by a FlexiQuestionContainer. This occurred during user initiated ImageAcquisition from\n"
                                        + "a resource already added to the library. Image ID=" + id + " library=" + fromLib, ex);
                            }
                        } else {
                            BufferedImage img = null;
                            try {
                                //otherwise the image must be copied into the current library from the source library.
                                img = IOManager.loadImage(fromLib, id);
                            } catch (IOException ex) {
                                Logger.getLogger(AnsFieldMultiChoice.class.getName()).log(Level.SEVERE, "IO Exception occurred while attempting to load an image through the IOManager\n"
                                        + " to create an EmbeddedImage for a RichTextArea contained by a FlexiQuestionContainer. This occurred during user initiated ImageAcquisition from\n"
                                        + "a resource from a different library to the one containing the destination FlexiQuestionContainer. Image ID=" + id + " , from library=" + fromLib
                                        + " , destination library={" + getParentLibraryID() + "}", ex);
                            }
                            try {
                                id = IOManager.saveImage(getParentLibraryID(), img, id);
                            } catch (IOException ex) {
                                Logger.getLogger(AnsFieldMultiChoice.class.getName()).log(Level.SEVERE, "IO Exception occurred while attempting to save an image to a library through the IOManager\n"
                                        + ". This occurred during user initiated ImageAcquisition from a resource from a different library to the one containing the destination FlexiQuestionContainer.\n"
                                        + "Image ID=" + id + " , from library=" + fromLib + " , destination library={" + getParentLibraryID() + "}", ex);
                            }
                            eg = new EmbeddedImage(img, id, getParentLibraryID());
                        }
                        //FROM COLLECTION/NEW/FILE------------------------------------------------------------------------------
                    } else if ((imgDialog.getAcquisitionSource() == ImageAcquisitionDialog.FROM_COLLECTION) || (imgDialog.getAcquisitionSource() == ImageAcquisitionDialog.FROM_FILE)
                            || (imgDialog.getAcquisitionSource() == ImageAcquisitionDialog.FROM_NEW) || (imgDialog.getAcquisitionSource() == ImageAcquisitionDialog.FROM_CHEM_STRUCTURE)) {
                        try {
                            id = IOManager.saveImage(getParentLibraryID(), imgDialog.getAcquiredImage(), imgDialog.getAcquiredImageData());
                        } catch (IOException ex) {
                            Logger.getLogger(AnsFieldMultiChoice.class.getName()).log(Level.SEVERE, "IO Exception occurred while attempting to save an image to a library through the IOManager"
                                    + ". This occurred during user initiated ImageAcquisition from a collection, from file or from a newly created image."
                                    + " Destination library={" + getParentLibraryID() + "}", ex);
                        }
                        eg = new EmbeddedImage(imgDialog.getAcquiredImage(), id, getParentLibraryID());
                    }
                    //MATH-TEXT or USER CANCELLED------------------------------------------------------------------------------
                } else { //we're dealing with 'user cancelled' or math text
                    if (imgDialog.getAcquisitionSource() == ImageAcquisitionDialog.NONE) {
                        return;
                    } else {
                        //split into math-text-data and render-size
                        String[] mathText = imgDialog.getAcquiredImageData().split("\n");
                        String[] colourVals = mathText[2].split(",");
                        eg = new EmbeddedMathTeX(mathText[0], Integer.valueOf(mathText[1]), new Color(Integer.valueOf(colourVals[0]), Integer.valueOf(colourVals[1]), Integer.valueOf(colourVals[2])));
                    }
                }

                if (eg instanceof EmbeddedImage) {
                    if (((EmbeddedImage) eg).isImageTooLarge()) {
                        int resp = JOptionPane.showConfirmDialog(AnsFieldMultiChoice.this, "This image is larger than the recommended maximum size. Would you\n"
                                + "like Ingatan to shrink the image to the largest recommended size?", "Large Image", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);

                        if (resp == JOptionPane.YES_OPTION) {
                            EmbeddedImage ei = (EmbeddedImage) eg;
                            ei.resizeTo(ei.getMaxRecommendedSize(),true);
                            try {
                                IOManager.saveImageWithOverWrite(ei.getImage(), ei.getParentLibraryID(), ei.getImageID());
                            } catch (IOException ex) {
                                Logger.getLogger(AnsFieldSelfGraded.class.getName()).log(Level.SEVERE, "ocurred while trying to save an embeddedImage with rewrite using IOManager\n"
                                        + "in order to save a resized version of the image upon user request (user was just told the image is larger than recommended, and asked if they would\n"
                                        + "like it resized).", ex);
                            }
                        }
                    }
                }

                Iterator<OptionEntry> iterate = optionEntries.iterator();
                OptionEntry temp;
                while (iterate.hasNext()) {
                    temp = iterate.next();
                    if (e.getSource().equals(temp.getRichTextArea().getToolbar())) {
                        temp.getRichTextArea().insertComponent(eg);
                        temp.resetSize(temp.getRichTextArea());
                    }
                }
            }
        }

        public void fontChanged(RichTextToolbarEvent e) {
        }
    }
}
