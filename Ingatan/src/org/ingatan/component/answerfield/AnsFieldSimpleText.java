/*
 * AnsFieldSimpleText.java
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

import java.awt.event.ComponentEvent;
import java.awt.event.KeyEvent;
import org.ingatan.ThemeConstants;
import org.ingatan.component.text.SimpleTextField;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentListener;
import java.awt.event.KeyListener;
import java.io.IOException;
import java.io.StringReader;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import org.jdom.DataConversionException;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.jdom.output.XMLOutputter;

/**
 * BasicTextField answer field. At library manager time, takes a list of possible correct answers, and at
 * quiz time it allows the user to enter some text.
 * @author Thomas Everingham
 * @version 1.o
 */
public class AnsFieldSimpleText extends JPanel implements IAnswerField {

    /**
     * Possible correct answers.
     */
    private String[] correctAnswers = new String[0];
    /**
     * The number of marks that are awarded if the answer matches
     * the answer given.
     */
    private int marksIfCorrect = 1;
    /**
     * Whether or not this component is currently being displayed in the library
     * manager (<code>true</code> value) or quiz time (<code>false</code> value).
     */
    private boolean inLibManager = true;
    /**
     * Text entry field.
     */
    private SimpleTextField txtField = new SimpleTextField(11);
    /**
     * Text field for setting how many marks to award for a correct answer
     */
    private JSpinner spinMarks = new JSpinner(new SpinnerNumberModel(1, 0, 20, 1));
    /**
     * Button for the user at quiz time that provides a hint for the answer, letter by letter.
     */
    private JButton btnGiveHint = new JButton(new GiveHintAction());
    /**
     * Popup menu with which the hint is displayed.
     */
    private JPopupMenu popupHint = new JPopupMenu();
    /**
     * JLabel added to the hint popup menu to display the hints.
     */
    private JLabel lblHint = new JLabel();
    /**
     * Check box for edit time that allows user to set whether or not hints are allowed.
     */
    private JCheckBox chkHints = new JCheckBox("Hints");
    /**
     * Number of hints that have been given: an extra letter is given each time.
     */
    private int hintsGiven = 0;
    /**
     * Action listener assigned by the quiz window. actionPerformed is called on this
     * if the user double pressed enter in the text field.
     */
    private ActionListener actionListener = null;

    /**
     * Create a new instance of <code>BasicTextField</code>.
     */
    public AnsFieldSimpleText() {
        this.setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
        this.setOpaque(false);
        this.setMaximumSize(new Dimension(250, 100));
        this.setMinimumSize(new Dimension(50, 100));

        txtField.setAlignmentX(LEFT_ALIGNMENT);
        txtField.addKeyListener(new ContinueKeyListener());
        txtField.getSymbolMenu().addComponentListener(new TextComponentListener());

        btnGiveHint.setFont(ThemeConstants.niceFont);
        btnGiveHint.setAlignmentX(LEFT_ALIGNMENT);
        btnGiveHint.setToolTipText("Gives one letter of a possible correct answer each time you click.");


        lblHint.setFont(ThemeConstants.niceFont);
        chkHints.setFont(ThemeConstants.niceFont);
        chkHints.setOpaque(false);
        chkHints.setMinimumSize(new Dimension(40,20));
        chkHints.setMaximumSize(new Dimension(40,20));
        chkHints.setToolTipText("If selected, a hint button will appear at quiz time that gives one letter of a correct answer each time it is pressed.");

        spinMarks.setMaximumSize(new Dimension(28, 20));
        spinMarks.setMinimumSize(new Dimension(28, 20));

        popupHint.add(lblHint);


        rebuild();

    }

    /**
     * Removes all components and adds them again based on the current context (library editor or quiz mode).
     */
    private void rebuild() {
        this.removeAll();

        if (inLibManager) {
            txtField.setMaximumSize(new Dimension(400, 25));
            txtField.setMinimumSize(new Dimension(200,20));
            txtField.setFont(ThemeConstants.tableCellEditorFont);
            txtField.setBorder(BorderFactory.createLineBorder(ThemeConstants.backgroundUnselected.darker()));
            txtField.setToolTipText("Separate possible answers using double comma (,,)");

            spinMarks.setBorder(BorderFactory.createLineBorder(ThemeConstants.backgroundUnselected.darker()));

            this.add(txtField);
            this.add(Box.createVerticalStrut(2));
            this.add(spinMarks);
            this.add(Box.createVerticalStrut(2));
            this.add(chkHints);
            String cat = "";
            for (int i = 0; i < correctAnswers.length; i++) {
                cat += correctAnswers[i] + ",,";
            }
            txtField.setText(cat);

            if (txtField.getText().isEmpty()) {
                txtField.setText("ans1,,ans2");
            }

            spinMarks.setValue(marksIfCorrect);


        } else {
            txtField.setMaximumSize(new Dimension(getAverageAnswerWidth(txtField.getFontMetrics(txtField.getFont())), 30));
            txtField.setMinimumSize(new Dimension(getAverageAnswerWidth(txtField.getFontMetrics(txtField.getFont())), 30));
            txtField.setToolTipText("Enter your answer here.");

            this.setMaximumSize(txtField.getMaximumSize());
            this.setMinimumSize(txtField.getMinimumSize());

            txtField.setBorder(BorderFactory.createLineBorder(ThemeConstants.borderUnselected));
            this.add(txtField);

            txtField.setFont(ThemeConstants.niceFont.deriveFont(14.0f));
            txtField.setText("");
            if (chkHints.isSelected()) {
                this.add(btnGiveHint);
                btnGiveHint.setMargin(new Insets(0, 0, 0, 0));
                //make room for the hints button
                this.setMaximumSize(new Dimension(txtField.getMaximumSize().width + btnGiveHint.getMinimumSize().width, 23));
                this.setMinimumSize(new Dimension(txtField.getMinimumSize().width + btnGiveHint.getMinimumSize().width,23));
            }
        }
    }

    public int getAverageAnswerWidth(FontMetrics fm) {
        float widthSum = 0;

        for (int i = 0; i < correctAnswers.length; i++) {
            widthSum += fm.stringWidth(correctAnswers[i]);
        }

        if (correctAnswers.length == 0)
            return 60;

        if ((widthSum / correctAnswers.length) < 60) {
            return 60;
        } else if ((widthSum / correctAnswers.length) > 160) {
            return 160;
        } else {
            return (int) (widthSum / correctAnswers.length);
        }
    }

    public String getDisplayName() {
        return "Simple Text Box";
    }

    public boolean isOnlyForAnswerArea() {
        return true;
    }

    public float checkAnswer() {
        for (int i = 0; i < correctAnswers.length; i++) {
            if (correctAnswers[i].toLowerCase().equals(txtField.getText().toLowerCase())) {
                return 1.0f;
            }
        }
        return 0.0f;
    }

    public int getMaxMarks() {
        return marksIfCorrect;
    }

    public int getMarksAwarded() {
        return (int) (marksIfCorrect * checkAnswer());
    }

    public void displayCorrectAnswer() {
        txtField.setEditable(false);
        JLabel lblCorrectOrNot = new JLabel();
        String ansText = "<html><body>";
        if (checkAnswer() == 1.0f) {
            ansText += "<b>CORRECT</b>";
            lblCorrectOrNot.setForeground(ThemeConstants.quizPassGreen);
        } else {
            ansText += "<b>INCORRECT</b>";
            lblCorrectOrNot.setForeground(ThemeConstants.quizFailRed);
        }
        lblCorrectOrNot.setText(ansText);
        lblCorrectOrNot.setFont(ThemeConstants.niceFont);


        ansText = "<html><body>";

        ansText += "possible answers are:<ul>";
        for (int i = 0; i < correctAnswers.length; i++) {
            ansText += "<li>" + correctAnswers[i] + "</li>";
        }
        ansText += "</ul>";
        JLabel lblAnsDisplay = new JLabel(ansText);
        lblAnsDisplay.setFont(ThemeConstants.niceFont);
        btnGiveHint.setVisible(false);

        Box vert = Box.createVerticalBox();
        vert.add(txtField);
        vert.add(lblCorrectOrNot);
        vert.add(lblAnsDisplay);
        this.add(vert);

        this.setMaximumSize(new Dimension(Math.max(lblAnsDisplay.getMinimumSize().width,lblCorrectOrNot.getMinimumSize().width), lblAnsDisplay.getMinimumSize().height + lblCorrectOrNot.getMinimumSize().height + 30));
    }

    public void setContext(boolean inLibraryContext) {
        inLibManager = inLibraryContext;
        rebuild();
    }

    public void setValues(String[] correctAnswers, int marksAvailable) {
        this.correctAnswers = correctAnswers;
        this.marksIfCorrect = marksAvailable;
        rebuild();
    }

    public String writeToXML() {
        //create JDOM document and root element
        Document doc = new Document();
        Element rootElement = new Element(this.getClass().getName());
        doc.setRootElement(rootElement);
        rootElement.setAttribute("marks", String.valueOf(spinMarks.getValue()));
        rootElement.setAttribute("useHints", String.valueOf(chkHints.isSelected()));
        //version field allows future versions of this field to be back compatible.
        //especially important for default fields!
        rootElement.setAttribute("version", "1.0");
        rootElement.setText(txtField.getText());

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
            Logger.getLogger(AnsFieldSimpleText.class.getName()).log(Level.SEVERE, "While trying to create a JDOM document in the readInXML method.", ex);
        } catch (IOException ex) {
            Logger.getLogger(AnsFieldSimpleText.class.getName()).log(Level.SEVERE, "While trying to create a JDOM document in the readInXML method.", ex);
        }

        //nothing to parse, so leave
        if (doc == null) {
            return;
        }

        try {
            spinMarks.setValue(doc.getRootElement().getAttribute("marks").getIntValue());
            txtField.setText(doc.getRootElement().getText());
            correctAnswers = doc.getRootElement().getText().split(",,");
            marksIfCorrect = doc.getRootElement().getAttribute("marks").getIntValue();
            chkHints.setSelected(doc.getRootElement().getAttribute("useHints").getBooleanValue());
        } catch (DataConversionException ex) {
            Logger.getLogger(AnsFieldSimpleText.class.getName()).log(Level.SEVERE, "While reading in from XML, attempting to convert the number of marks awarded for a correct answer\n"
                    + "into an integer value (from String).", ex);
        }

    }

    public void setQuizContinueListener(ActionListener listener) {
        actionListener = listener;
    }

    /**
     * This listener is added to the text field's symbol menu so that this answer field is resized when the
     * symbol menu is shown. This allows the text field to resize to accomodate for the symbol menu's size.
     */
    private class TextComponentListener implements ComponentListener {

        public void componentResized(ComponentEvent e) {
        }

        public void componentMoved(ComponentEvent e) {
        }

        public void componentShown(ComponentEvent e) {
            AnsFieldSimpleText.this.setMaximumSize(txtField.getMaximumSize());
            AnsFieldSimpleText.this.setMinimumSize(txtField.getMinimumSize());
            AnsFieldSimpleText.this.setSize(txtField.getMinimumSize());
        }

        public void componentHidden(ComponentEvent e) {
        }
    }

    /**
     * Listens for the enter key and triggers the quiz continue action.
     */
    private class ContinueKeyListener implements KeyListener {

        public void keyTyped(KeyEvent e) {
        }

        public void keyPressed(KeyEvent e) {
            if ((actionListener != null) && (e.getKeyCode() == KeyEvent.VK_ENTER) && (e.getModifiers() == 0) && (inLibManager == false)) {
                actionListener.actionPerformed(new ActionEvent(AnsFieldSimpleText.this, 0, ""));
            }
        }

        public void keyReleased(KeyEvent e) {
        }
    }

    private class GiveHintAction extends AbstractAction {

        public GiveHintAction() {
            super("Hint");
        }

        public void actionPerformed(ActionEvent e) {
            hintsGiven++;

            String buildText = "<html><h3>Hints</h3>The following are partial possible answers:<ul>";
            for (int i = 0; i < correctAnswers.length; i++) {
                if (correctAnswers[i].length() > hintsGiven) {
                    buildText += "<li>" + correctAnswers[i].substring(0, hintsGiven) + "</li>";
                } else {
                    buildText += "<li>" + correctAnswers[i] + "</li>";
                }
            }
            lblHint.setText(buildText + "</ul>");

            popupHint.show((Component) e.getSource(), 12, 12);
        }
    }

    public String getParentLibraryID() {
        //to access saved images, the answer field must know which library it belongs to
        //in order to make a request. This answer field does not use images, and so has
        //not implemented the parent library ID.
        return "";
    }

    public void setParentLibraryID(String id) {
        //to access saved images, the answer field must know which library it belongs to
        //in order to make a request. This answer field does not use images, and so has
        //not implemented the parent library ID.
        return;
    }

    public void resaveImagesAndResources(String lib) {
        //not implemented as true/false does not use images or file resources
    }

    @Override
    public void requestFocus() {
        txtField.requestFocus();
    }
}
