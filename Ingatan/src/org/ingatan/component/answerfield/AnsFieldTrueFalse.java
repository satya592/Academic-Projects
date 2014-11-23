/*
 * AnsFieldTrueFalse.java
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

import java.awt.event.MouseEvent;
import org.ingatan.ThemeConstants;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseListener;
import java.io.IOException;
import java.io.StringReader;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.AbstractAction;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import org.jdom.DataConversionException;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.jdom.output.XMLOutputter;

/**
 * Answer field which, in edit time, allows the user to set the number of marks to
 * award, and whether true or false is correct. Shows the correct answer by drawing
 * a green box around it. Also allows the user to set the options as "yes or no" instead
 * of "true or false".
 *
 * @author Thomas Everingham
 * @version 1.0
 */
public class AnsFieldTrueFalse extends JPanel implements IAnswerField {

    /**
     * True option for the answerfield.
     */
    private JRadioButton radioTrue = new JRadioButton("true");
    /**
     * Flase option for the answer field.
     */
    private JRadioButton radioFalse = new JRadioButton("false");
    /**
     * Button group for the true and false radio buttons.
     */
    private ButtonGroup radioGroup = new ButtonGroup();
    /**
     * Button that switches the labels of the radio buttons to/from "true and false" and "yes and no".
     */
    private JButton btnToggle = new JButton(new ToggleAction());
    /**
     * Label for the number of marks to award spinner.
     */
    private JLabel lblPoints = new JLabel("Marks: ");
    /**
     * Spinner so that the user can set the number of marks to award for a correct answer.
     */
    private JSpinner spinMarks = new JSpinner(new SpinnerNumberModel(1, 0, 20, 1));
    /**
     * Whether the answer is true or false.
     */
    private boolean correctAnswer = true;
    /**
     * Action listener assigned by the quiz window. actionPerformed is called on this
     * if the user double clicks an option (yes or no radio button).
     */
    private ActionListener actionListener = null;

    /**
     * Creates a new instance of <code>AnsFieldTrueFalse</code>.
     */
    public AnsFieldTrueFalse() {
        this.setOpaque(false);
        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        radioTrue.setOpaque(false);
        radioTrue.setAlignmentX(LEFT_ALIGNMENT);
        radioTrue.setSelected(true);
        radioTrue.addMouseListener(new OptionMouseListener());
        radioGroup.add(radioTrue);

        radioFalse.setOpaque(false);
        radioFalse.setAlignmentX(LEFT_ALIGNMENT);
        radioFalse.addMouseListener(new OptionMouseListener());
        radioGroup.add(radioFalse);

        //make spinner uneditable
        ((JSpinner.DefaultEditor) spinMarks.getEditor()).getTextField().setEditable(false);

        this.add(radioTrue);
        this.add(radioFalse);

        btnToggle.setToolTipText("Toggle between the 'true or false' captions and the 'yes or no' captions.");
        spinMarks.setToolTipText("The number of marks to award if the user gives the correct answer.");


        Box horiz = Box.createHorizontalBox();
        horiz.setAlignmentX(LEFT_ALIGNMENT);
        horiz.setMaximumSize(new Dimension(125, 20));

        btnToggle.setMargin(new Insets(1, 1, 1, 1));
        btnToggle.setMaximumSize(new Dimension(20, 15));
        lblPoints.setFont(ThemeConstants.niceFont);
        btnToggle.setFont(ThemeConstants.niceFont);

        horiz.add(btnToggle);
        horiz.add(Box.createHorizontalStrut(10));
        horiz.add(lblPoints);
        horiz.add(spinMarks);
        this.add(horiz);
    }

    public String getDisplayName() {
        return "True or False";
    }

    public boolean isOnlyForAnswerArea() {
        return true;
    }

    public float checkAnswer() {
        if (radioTrue.isSelected() && correctAnswer) {
            return 1.0f;
        } else if (radioFalse.isSelected() && !correctAnswer) {
            return 1.0f;
        } else {
            return 0.0f;
        }
    }

    public int getMaxMarks() {
        return ((SpinnerNumberModel) spinMarks.getModel()).getNumber().intValue();
    }

    public int getMarksAwarded() {
        return (int) (checkAnswer() * getMaxMarks());
    }

    public void displayCorrectAnswer() {
        radioFalse.setEnabled(false);
        radioTrue.setEnabled(false);

        if (correctAnswer) {
            radioTrue.setText(radioTrue.getText() + "   <- correct");
            radioTrue.setFont(radioTrue.getFont().deriveFont(Font.BOLD));
        } else {
            radioFalse.setText(radioFalse.getText() + "   <- correct");
            radioFalse.setFont(radioTrue.getFont().deriveFont(Font.BOLD));
        }

        this.repaint();
    }

    public void setContext(boolean inLibraryContext) {
        lblPoints.setVisible(inLibraryContext);
        spinMarks.setVisible(inLibraryContext);
        btnToggle.setVisible(inLibraryContext);
        if (!inLibraryContext) {
            radioTrue.setSelected(true);
        }
    }

    public String writeToXML() {
        Document doc = new Document();

        //lib metadata
        Element e = new Element(this.getClass().getName());
        e.setAttribute("correctAnswer", String.valueOf(radioTrue.isSelected()));
        e.setAttribute("trueLabel", radioTrue.getText());
        e.setAttribute("marks", String.valueOf(((SpinnerNumberModel) spinMarks.getModel()).getNumber().intValue()));
        //version field allows future versions of this field to be back compatible.
        //especially important for default fields!
        e.setAttribute("version", "1.0");
        doc.addContent(e);

        XMLOutputter fmt = new XMLOutputter();
        return fmt.outputString(doc);
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
            Logger.getLogger(AnsFieldTrueFalse.class.getName()).log(Level.SEVERE, "While trying to create a JDOM document in the readInXML method.", ex);
        } catch (IOException ex) {
            Logger.getLogger(AnsFieldTrueFalse.class.getName()).log(Level.SEVERE, "While trying to create a JDOM document in the readInXML method.", ex);
        }

        //nothing to parse, so leave
        if (doc == null) {
            return;
        }

        try {
            correctAnswer = doc.getRootElement().getAttribute("correctAnswer").getBooleanValue();
            spinMarks.setValue(Integer.valueOf(doc.getRootElement().getAttributeValue("marks")));
            radioTrue.setText(doc.getRootElement().getAttributeValue("trueLabel"));
            radioFalse.setText((radioTrue.getText().equals("true")) ? "false" : "no");

            if (correctAnswer) {
                radioTrue.setSelected(true);
            } else {
                radioFalse.setSelected(true);
            }

        } catch (DataConversionException ex) {
            Logger.getLogger(AnsFieldTrueFalse.class.getName()).log(Level.SEVERE, "While reading in from XML.", ex);
        }
    }

    public String getParentLibraryID() {
        return ""; //not implemented as this answer field does not require image IO
    }

    public void setParentLibraryID(String id) {
        return; //not implemented as this answer field does not required image IO
    }

    public void setQuizContinueListener(ActionListener listener) {
        actionListener = listener;
    }

    public void resaveImagesAndResources(String lib) {
        //not implemented as true/false does not use images or file resources
    }

    /**
     * Listens for a double click on a radio button as an event to trigger the
     * quiz continue action.
     */
    private class OptionMouseListener implements MouseListener {

        public void mouseClicked(MouseEvent e) {
            if ((actionListener != null) && (e.getClickCount() == 2) && (lblPoints.isVisible() == false)) {
                actionListener.actionPerformed(new ActionEvent(AnsFieldTrueFalse.this, 0, ""));
            }
        }

        public void mousePressed(MouseEvent e) {}
        public void mouseReleased(MouseEvent e) {}
        public void mouseEntered(MouseEvent e) {}
        public void mouseExited(MouseEvent e) {}
    }

    private class ToggleAction extends AbstractAction {

        public ToggleAction() {
            super("<>");
        }

        public void actionPerformed(ActionEvent e) {
            if (radioTrue.getText().equals("true")) {
                radioTrue.setText("yes");
                radioFalse.setText("no");
            } else {
                radioTrue.setText("true");
                radioFalse.setText("false");
            }
        }
    }
}
