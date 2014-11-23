/*
 * AnsFieldSelfGraded.java
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

import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import org.ingatan.ThemeConstants;
import org.ingatan.component.image.ImageAcquisitionDialog;
import org.ingatan.component.text.EmbeddedGraphic;
import org.ingatan.component.text.EmbeddedImage;
import org.ingatan.component.text.EmbeddedMathTeX;
import org.ingatan.component.text.RichTextArea;
import org.ingatan.event.RichTextToolbarEvent;
import org.ingatan.event.RichTextToolbarListener;
import org.ingatan.io.IOManager;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.FocusListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.StringReader;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.AbstractAction;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.text.AttributeSet;
import javax.swing.text.StyleConstants;
import org.ingatan.component.text.GeneralRichTextTransferHandler;
import org.ingatan.component.text.RichTextToolbar;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.jdom.output.XMLOutputter;

/**
 * Free form answer field which provides just a rich text area and the ability to
 * set the maximum number of marks that can be awarded. This answer field is marked
 * by the user at quiz time - the user compares the answer they have just given with the
 * answer which is preset as the correct answer, and then selects how many marks to award.
 *
 * Because the quiz window has no ability to perform this intermediate evaluation stage,
 * this stage is done by showing a modal dialogue when the checkAnswer method is called.
 *
 * @author Thomas Everingham
 * @version 1.0
 */
public class AnsFieldSelfGraded extends JPanel implements IAnswerField {

    /**
     * Rich text area for answer entry.
     */
    private RichTextArea txtArea;
    /**
     * Label for the number of marks to award spinner.
     */
    private JLabel lblPoints = new JLabel("Marks: ");
    /**
     * Spinner so that the user can set the maximum number of marks that may be awarded.
     */
    private JSpinner spinMarks = new JSpinner(new SpinnerNumberModel(1, 0, 20, 1));
    /**
     * The ID of the parent library. Allows for saving/loading images through the IOManager.
     */
    private String parentLibraryID = "";
    /**
     * The correct answer in rich text markup form.
     */
    private String correctAnswer = "";
    /**
     * Whether or not the answer field should display itself in edit context, or quiz context.
     */
    private boolean inEditContext = true;

    /**
     * Creates a new instance of <code>AnsFieldTrueFalse</code>.
     */
    public AnsFieldSelfGraded() {
        this.setOpaque(false);
        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        txtArea = new RichTextArea();
        txtArea.getScroller().setAlignmentX(LEFT_ALIGNMENT);
        txtArea.getScroller().setMaximumSize(new Dimension(600, 500));
        txtArea.getScroller().setMinimumSize(new Dimension(200, 100));
        txtArea.getToolbar().addRichTextToolbarListener(new TextToolbarListener());
        txtArea.addFocusListener(new TextFocusListener());
        txtArea.setTransferHandler(new GeneralRichTextTransferHandler());

        ((JSpinner.DefaultEditor) spinMarks.getEditor()).getTextField().setEditable(false);

        this.setPreferredSize(new Dimension(400, 150));


        Box horiz = Box.createHorizontalBox();
        horiz.setAlignmentX(LEFT_ALIGNMENT);
        horiz.setMaximumSize(new Dimension(80, 20));

        lblPoints.setFont(ThemeConstants.niceFont);

        spinMarks.setToolTipText("The maximum number of marks the user can award themself at quiz time.");

        horiz.add(lblPoints);
        horiz.add(spinMarks);

        this.add(txtArea.getScroller());
        this.add(horiz);
    }

    public String getDisplayName() {
        return "Self Graded Question";
    }

    public boolean isOnlyForAnswerArea() {
        return true;
    }

    public float checkAnswer() {
        AnswerEvaluationDialog evaluateDialog = new AnswerEvaluationDialog();
        evaluateDialog.setModal(true);
        evaluateDialog.setVisible(true);
        return evaluateDialog.getCorrectnessValue();
    }

    public int getMaxMarks() {
        return ((SpinnerNumberModel) spinMarks.getModel()).getNumber().intValue();
    }

    public int getMarksAwarded() {
        return (int) (checkAnswer() * getMaxMarks());
    }

    public void displayCorrectAnswer() {
        txtArea.setRichText(txtArea.getRichText() + "[" + RichTextArea.TAG_NEW_LINE + "][" + RichTextArea.TAG_NEW_LINE + "][" + RichTextArea.TAG_BOLD + "]------Correct answer------[" + RichTextArea.TAG_BOLD + "][" + RichTextArea.TAG_NEW_LINE + "]" + correctAnswer);
        txtArea.setCaretPosition(0);
        txtArea.setEditable(false);
        txtArea.setToolbarVisible(false);
        txtArea.setFocusable(false);
    }

    public void setContext(boolean inLibraryContext) {
        lblPoints.setVisible(inLibraryContext);
        spinMarks.setVisible(inLibraryContext);

        inEditContext = inLibraryContext;

        if (inEditContext) {
            txtArea.setRichText(correctAnswer);
        } else {
            txtArea.setText("");
        }
    }

    public String writeToXML() {
        Document doc = new Document();

        //lib metadata
        Element e = new Element(this.getClass().getName());
        e.setAttribute("marks", String.valueOf(((SpinnerNumberModel) spinMarks.getModel()).getNumber().intValue()));
        e.setAttribute("parentLib", parentLibraryID);
        //version field allows future versions of this field to be back compatible.
        //especially important for default fields!
        e.setAttribute("version", "1.0");
        e.setText(txtArea.getRichText());
        doc.setRootElement(e);

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
            Logger.getLogger(AnsFieldSelfGraded.class.getName()).log(Level.SEVERE, "While trying to create a JDOM document in the readInXML method.", ex);
        } catch (IOException ex) {
            Logger.getLogger(AnsFieldSelfGraded.class.getName()).log(Level.SEVERE, "While trying to create a JDOM document in the readInXML method.", ex);
        }

        //nothing to parse, so leave
        if (doc == null) {
            return;
        }

        spinMarks.setValue(Integer.valueOf(doc.getRootElement().getAttributeValue("marks")));
        parentLibraryID = doc.getRootElement().getAttributeValue("parentLib");
        correctAnswer = doc.getRootElement().getText().replace(RichTextArea.CHARCODE_OPENING_SQUARE_BRACKET, "[").replace(RichTextArea.CHARCODE_CLOSING_SQUARE_BRACKET, "]");

        if (inEditContext) {
            txtArea.setRichText(correctAnswer);
        } else {
            txtArea.setText("");
        }
    }

    public String getParentLibraryID() {
        return parentLibraryID;
    }

    public void setParentLibraryID(String id) {
        parentLibraryID = id;
    }

    public void setQuizContinueListener(ActionListener listener) {
        //this is not implemented, as there is no logical event that should trigger the quiz continue action.
    }

    public void resaveImagesAndResources(String newLibraryID) {
        //traverse the rich text area for any embedded images, and reset their parentLibrary values
        //as well as resaving resources to the new library
        int runCount;
        int paragraphCount = txtArea.getDocument().getDefaultRootElement().getElementCount();
        javax.swing.text.Element curEl = null;
        AttributeSet curAttr = null;
        AttributeSet prevAttr = null;

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

    private class AnswerEvaluationDialog extends JDialog {

        /**
         * Rich text area that displays the user's answer.
         */
        private RichTextArea txtUserAnswer = new RichTextArea();
        /**
         * Rich text area that displays the pre-set correct answer.
         */
        private RichTextArea txtCorrectAnswer = new RichTextArea();
        /**
         * Content pane for the dialogue.
         */
        private JPanel contentPane = new JPanel();
        /**
         * Button to pass the result on to the quiz window and continue.
         */
        private JButton btnContinue = new JButton(new ContinueAction());
        /**
         * Label for the user answer text area.
         */
        private JLabel lblUserAnswer = new JLabel("Your Answer:");
        /**
         * Label for the correct answer text area.
         */
        private JLabel lblCorrectAnswer = new JLabel("Correct Answer:");
        /**
         * Label for the number of marks to award.
         */
        private JLabel lblMarksToAward = new JLabel("Grade:");
        /**
         * Label for the maximum number of marks that can be awarded.
         */
        private JLabel lblMaxMarks = new JLabel("/" + getMaxMarks());
        /**
         * Spinner for setting the number of marks to award for this question.
         */
        private JSpinner spinMarkToAward = new JSpinner(new SpinnerNumberModel(getMaxMarks(), 0, getMaxMarks(), 1));

        public AnswerEvaluationDialog() {
            super();
            this.setContentPane(contentPane);
            contentPane.setLayout(new BoxLayout(contentPane, BoxLayout.Y_AXIS));

            txtUserAnswer.setEditable(false);
            txtUserAnswer.setRichText(txtArea.getRichText());
            txtUserAnswer.setCaretPosition(0);
            txtUserAnswer.setFocusable(false);
            txtUserAnswer.setToolbarVisible(false);
            txtUserAnswer.getScroller().setAlignmentX(LEFT_ALIGNMENT);
            lblUserAnswer.setFont(ThemeConstants.hugeFont.deriveFont(16.0f));
            lblUserAnswer.setAlignmentX(LEFT_ALIGNMENT);

            txtCorrectAnswer.setEditable(false);
            txtCorrectAnswer.setFocusable(false);
            txtCorrectAnswer.setToolbarVisible(false);
            txtCorrectAnswer.setRichText(correctAnswer);
            txtCorrectAnswer.setCaretPosition(0);
            txtCorrectAnswer.getScroller().setAlignmentX(LEFT_ALIGNMENT);
            lblCorrectAnswer.setFont(ThemeConstants.hugeFont.deriveFont(16.0f));
            lblCorrectAnswer.setAlignmentX(LEFT_ALIGNMENT);

            contentPane.add(lblUserAnswer);
            contentPane.add(txtUserAnswer.getScroller());
            contentPane.add(lblCorrectAnswer);
            contentPane.add(txtCorrectAnswer.getScroller());

            lblMarksToAward.setFont(ThemeConstants.hugeFont.deriveFont(16.0f));
            lblMaxMarks.setFont(ThemeConstants.hugeFont.deriveFont(16.0f));
            spinMarkToAward.setFont(ThemeConstants.hugeFont.deriveFont(16.0f));
            spinMarkToAward.setMaximumSize(new Dimension(50, 50));
            ((JSpinner.DefaultEditor) spinMarkToAward.getEditor()).getTextField().setEditable(false);

            Box horiz = Box.createHorizontalBox();
            horiz.add(lblMarksToAward);
            horiz.add(spinMarkToAward);
            horiz.add(Box.createHorizontalStrut(5));
            horiz.add(lblMaxMarks);
            horiz.add(Box.createHorizontalStrut(10));
            horiz.add(btnContinue);
            horiz.setMaximumSize(new Dimension(220, 30));
            horiz.setAlignmentX(LEFT_ALIGNMENT);

            contentPane.add(horiz);

            this.setSize(600, 400);
            this.setLocationRelativeTo(null);
        }


        @Override
        public void requestFocus() {
            txtArea.requestFocus();
        }

        /**
         * Get the decimal ratio of the grade given for this question by the user.
         * @return
         */
        public float getCorrectnessValue() {
            if (getMaxMarks() == 0) {
                return 0.0f;
            }
            return ((SpinnerNumberModel) spinMarkToAward.getModel()).getNumber().floatValue() / getMaxMarks();
        }

        private class ContinueAction extends AbstractAction {

            public ContinueAction() {
                super("Continue");
            }

            public void actionPerformed(ActionEvent e) {
                AnswerEvaluationDialog.this.setVisible(false);
            }
        }
    }

    private class TextFocusListener implements FocusListener {

        public void focusGained(FocusEvent e) {
            ((RichTextArea) e.getSource()).setToolbarVisible(true);
        }

        public void focusLost(FocusEvent e) {
            if (e.getOppositeComponent() instanceof RichTextToolbar) {
                return;
            }

            ((RichTextArea) e.getSource()).setToolbarVisible(false);
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
                if (AnsFieldSelfGraded.this.getRootPane().getParent() instanceof JDialog) {
                    JOptionPane.showMessageDialog(AnsFieldSelfGraded.this, "Cannot insert an image into the field in the edit answer field dialog. Cannot save\n"
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
                                Logger.getLogger(AnsFieldSelfGraded.class.getName()).log(Level.SEVERE, "IO Exception occurred while attempting to load an image through the IOManager\n"
                                        + "to create an EmbeddedImage for a RichTextArea. This occurred during user initiated ImageAcquisition from\n"
                                        + "a resource already added to the library. Image ID=" + id + " library=" + fromLib, ex);
                            }
                        } else {
                            BufferedImage img = null;
                            try {
                                //otherwise the image must be copied into the current library from the source library.
                                img = IOManager.loadImage(fromLib, id);
                            } catch (IOException ex) {
                                Logger.getLogger(AnsFieldSelfGraded.class.getName()).log(Level.SEVERE, "IO Exception occurred while attempting to load an image through the IOManager\n"
                                        + " to create an EmbeddedImage for a RichTextArea. This occurred during user initiated ImageAcquisition from\n"
                                        + "a resource from a different library to the one containing the destination FlexiQuestionContainer. Image ID=" + id + " , from library=" + fromLib
                                        + " , destination library={" + getParentLibraryID() + "}", ex);
                            }
                            try {
                                id = IOManager.saveImage(getParentLibraryID(), img, id);
                            } catch (IOException ex) {
                                Logger.getLogger(AnsFieldSelfGraded.class.getName()).log(Level.SEVERE, "IO Exception occurred while attempting to save an image to a library through the IOManager\n"
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
                            Logger.getLogger(AnsFieldSelfGraded.class.getName()).log(Level.SEVERE, "IO Exception occurred while attempting to save an image to a library through the IOManager"
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
                        int resp = JOptionPane.showConfirmDialog(AnsFieldSelfGraded.this, "This image is larger than the recommended maximum size. Would you\n"
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

                if (e.getSource().equals(txtArea.getToolbar())) {
                    txtArea.insertComponent(eg);
                }
            }
        }

        public void fontChanged(RichTextToolbarEvent e) {
        }
    }
}
