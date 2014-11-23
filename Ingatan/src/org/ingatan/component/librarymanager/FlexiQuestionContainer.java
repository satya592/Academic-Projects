/*
 * FlexiQuestionContainer.java
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

import java.awt.datatransfer.DataFlavor;
import org.ingatan.ThemeConstants;
import org.ingatan.component.answerfield.IAnswerField;
import org.ingatan.component.image.ImageAcquisitionDialog;
import org.ingatan.component.text.EmbeddedGraphic;
import org.ingatan.component.text.EmbeddedImage;
import org.ingatan.component.text.EmbeddedMathTeX;
import org.ingatan.component.text.RichTextArea;
import org.ingatan.component.text.RichTextToolbar;
import org.ingatan.data.FlexiQuestion;
import org.ingatan.event.RichTextToolbarEvent;
import org.ingatan.event.RichTextToolbarListener;
import org.ingatan.io.IOManager;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.KeyboardFocusManager;
import java.awt.Rectangle;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.ActionEvent;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.StringReader;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.TransferHandler;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.AttributeSet;
import javax.swing.text.Element;
import javax.swing.text.StyleConstants;
import org.ingatan.image.ImageUtils;
import org.jdom.Document;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.jdom.output.XMLOutputter;

/**
 * This question container type is used for freeform questions. It consists of three rich text
 * fields; question text, answer text, and post-answer text. The post-answer text field is optional,
 * and is hidden when the option is not selected.
 *
 * @author Thomas Everingham
 * @version 1.0
 */
public class FlexiQuestionContainer extends AbstractQuestionContainer {

    private static Dimension TEXT_AREA_MAX_SIZE = new Dimension(1000, 550);
    private static Dimension TEXT_AREA_MIN_SIZE = new Dimension(200, 120);
    private static Dimension TEXT_AREA_PREF_SIZE = new Dimension(400, 300);
    private static int MAX_CONTAINER_HEIGHT = 450;
    /**
     * Static text focus listener is added to every <code>RichTextArea</code> contained
     * within a flexi question container. This allows the rich text toolbar to
     * 'follow the focus' of the rich text areas. Because this object is static, only
     * one rich text toolbar is shown between all instances of FlexiQuestionContainer. If this
     * does not suit, this listener can be manually removed from the <code>RichTextArea</code>s of
     * a particular <code>FlexiQuestionContainer</code>.
     */
    private static TextFocusListener textFocusListener = new TextFocusListener();
    /**
     * The question text area.
     */
    protected RichTextArea questionText = new RichTextArea();
    /**
     * The post answer text area.
     */
    protected RichTextArea postAnswerText = new RichTextArea();
    /**
     * The answer text area.
     */
    protected RichTextArea answerText = new RichTextArea();
    /**
     * The post-answer text is optional, and if not used, the field will be hidden.
     * This checkbox allows for the option to be set.
     */
    protected JCheckBox usePostAnswerText = new JCheckBox(new UsePostAnswerTextAction());
    /**
     * Label for question text field.
     */
    protected JLabel lblQuestion = new JLabel("Question text: ");
    /**
     * Label for answer text field.
     */
    protected JLabel lblAnswer = new JLabel("Answer text:");
    /**
     * Label for post-answer text field.
     */
    protected JLabel lblPostAnswer = new JLabel("Post-answer text:");
    /**
     * The question object encapsulating the data represented by this component.
     */
    protected FlexiQuestion flexiQuestion;

    /**
     * Create a new <code>FlexiQuestionContainer</code>.
     */
    public FlexiQuestionContainer(FlexiQuestion ques) {
        super(ques);
        this.flexiQuestion = ques;

        RichTextTransferHandler rtth = new RichTextTransferHandler();
        contentPanel.setPreferredSize(null);

        //set the maximum and minimum sizes for rich text area scrollers. These
        //sizes are used when resizing the areas to match their content so that
        //the fields are not made too small or too large.
        questionText.getScroller().setMaximumSize(TEXT_AREA_MAX_SIZE);
        questionText.getScroller().setMinimumSize(TEXT_AREA_MIN_SIZE);
        questionText.getScroller().setPreferredSize(TEXT_AREA_PREF_SIZE);
        questionText.setTransferHandler(rtth);
        answerText.getScroller().setMaximumSize(TEXT_AREA_MAX_SIZE);
        answerText.getScroller().setMinimumSize(TEXT_AREA_MIN_SIZE);
        answerText.getScroller().setPreferredSize(TEXT_AREA_PREF_SIZE);
        answerText.setTransferHandler(rtth);
        postAnswerText.getScroller().setMaximumSize(TEXT_AREA_MAX_SIZE);
        postAnswerText.getScroller().setMinimumSize(TEXT_AREA_MIN_SIZE);
        postAnswerText.getScroller().setPreferredSize(TEXT_AREA_PREF_SIZE);
        postAnswerText.setTransferHandler(rtth);

        //this following are set so that when we use the modelToView method, the area has positive size
        //otherwise the method returns false.
        questionText.setSize(TEXT_AREA_PREF_SIZE);
        answerText.setSize(TEXT_AREA_PREF_SIZE);
        postAnswerText.setSize(TEXT_AREA_PREF_SIZE);


        lblQuestion.setFont(ThemeConstants.niceFont);
        lblAnswer.setFont(ThemeConstants.niceFont);
        lblPostAnswer.setFont(ThemeConstants.niceFont);
        usePostAnswerText.setFont(ThemeConstants.niceFont);

        RichTextToolbarListener textToolbarListener = new TextToolbarListener();
        answerText.setToolbarVisible(false);
        answerText.getToolbar().addRichTextToolbarListener(textToolbarListener);
        postAnswerText.setToolbarVisible(false);
        postAnswerText.getToolbar().addRichTextToolbarListener(textToolbarListener);
        postAnswerText.getScroller().setVisible(false);
        lblPostAnswer.setVisible(false);
        questionText.setToolbarVisible(false);
        questionText.getToolbar().addRichTextToolbarListener(textToolbarListener);
        usePostAnswerText.setOpaque(false);

        //set layout and add components
        this.setLayoutOfContentPane(new BoxLayout(contentPanel, BoxLayout.PAGE_AXIS));
        contentPanel.setBorder(BorderFactory.createEmptyBorder(7, 7, 7, 7));

        this.addToContentPane(lblQuestion, true);
        this.addToContentPane(questionText.getScroller(), false);
        this.addToContentPane(lblAnswer, true);
        this.addToContentPane(answerText.getScroller(), false);
        this.addToContentPane(lblPostAnswer, true);
        this.addToContentPane(postAnswerText.getScroller(), false);
        this.addToContentPane(usePostAnswerText, true);

        this.validate();
        questionText.getScroller().validate();
        answerText.getScroller().validate();
        postAnswerText.getScroller().validate();


        //add static focus listener so that the toolbar follows the focus of the
        //text fields
        questionText.addFocusListener(textFocusListener);
        answerText.addFocusListener(textFocusListener);
        postAnswerText.addFocusListener(textFocusListener);

        //set data
        questionText.setRichText(flexiQuestion.getQuestionText());
        answerText.setRichText(flexiQuestion.getAnswerText());
        postAnswerText.setRichText(flexiQuestion.getPostAnswerText());

        usePostAnswerText.setSelected(flexiQuestion.isUsingPostAnswerText());
        postAnswerText.getScroller().setVisible(flexiQuestion.isUsingPostAnswerText());
        lblPostAnswer.setVisible(flexiQuestion.isUsingPostAnswerText());

        //must traverse answerText field and find any pre-existing answer fields
        //so that they can be made aware of edit context
        SwingUtilities.invokeLater(new Runnable() {

            public void run() {
                contextualiseAnswerFields(answerText);
                //answer fields are not confined to the answerText area, and so
                //the other text areas must also be contextualised (this was not
                //always the case).
                contextualiseAnswerFields(questionText);
                contextualiseAnswerFields(postAnswerText);
            }
        });

        //set the listeners that will resize the text areas upon document change
        questionText.getStyledDocument().addDocumentListener(new TextAreaListener(questionText));
        answerText.getStyledDocument().addDocumentListener(new TextAreaListener(answerText));
        postAnswerText.getStyledDocument().addDocumentListener(new TextAreaListener(postAnswerText));

        //do an initial resize based on the newly set text
        resetSize(questionText);
        resetSize(answerText);
        resetSize(postAnswerText);

    }

    /**
     * Traverses the elements of the answerText <code>RichTextArea</code> and tells
     * all IAnswerField components found that they exist in the library editor context.
     */
    public void contextualiseAnswerFields(RichTextArea textArea) {
        int runCount;
        int paragraphCount = textArea.getDocument().getDefaultRootElement().getElementCount();
        Element curEl = null;
        AttributeSet curAttr = null;
        AttributeSet prevAttr = null;

        for (int i = 0; i < paragraphCount; i++) {
            //each paragraph has 'runCount' runs
            runCount = textArea.getDocument().getDefaultRootElement().getElement(i).getElementCount();
            for (int j = 0; j < runCount; j++) {
                curEl = textArea.getDocument().getDefaultRootElement().getElement(i).getElement(j);
                curAttr = curEl.getAttributes();

                if (curEl.getName().equals(StyleConstants.ComponentElementName)) //this is a component
                {
                    //this run is a component. May be an answer field, picture or math text component.
                    Component o = (Component) curAttr.getAttribute(StyleConstants.ComponentAttribute);
                    if (o instanceof IAnswerField) {
                        ((IAnswerField) o).setContext(true);
                    }
                }
            }
        }
    }

    /**
     * Tries to paste image data from the clipboard (image or URL).
     */
    private boolean attemptImagePaste() {
        Component focusOwner = KeyboardFocusManager.getCurrentKeyboardFocusManager().getFocusOwner();
        if ((focusOwner instanceof RichTextArea == false) || (FlexiQuestionContainer.this.isAncestorOf(focusOwner) == false)) {
            //in either of these cases, the focus owner should not be pasted into.
            return false;
        }

        BufferedImage imgFromClipboard = null;
        String imageID = "";

        try {
            imgFromClipboard = ImageUtils.getImageFromClipboard(false, true);
        } catch (UnsupportedFlavorException ex) {
            return false;
        } catch (IOException ex) {
            return false;
        }

        //double check that an image was retreived from the clipboard
        if (imgFromClipboard == null) {
            return false;
        }
        try {
            imageID = IOManager.saveImage(FlexiQuestionContainer.this.getQuestion().getParentLibrary(), imgFromClipboard, "paste(" + imgFromClipboard.getWidth() + "x" + imgFromClipboard.getHeight() + ")");
        } catch (IOException ex) {
            Logger.getLogger(FlexiQuestionContainer.class.getName()).log(Level.SEVERE, "While trying to save an image pasted from the clipboard to the library: " + FlexiQuestionContainer.this.getQuestion().getParentLibrary(), ex);
        }

        if (imageID.equals("")) {
            //IOManager did not successfully save the image.
            return false;
        }

        //otherwise, create the embedded image
        EmbeddedImage eg = new EmbeddedImage(imgFromClipboard, imageID, FlexiQuestionContainer.this.getQuestion().getParentLibrary());

        if (eg.isImageTooLarge()) {
            int resp = JOptionPane.showConfirmDialog(FlexiQuestionContainer.this, "This image is larger than the recommended maximum size. Would you\n"
                    + "like Ingatan to shrink the image to the largest recommended size?", "Large Image", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);

            if (resp == JOptionPane.YES_OPTION) {
                EmbeddedImage ei = (EmbeddedImage) eg;
                ei.resizeTo(ei.getMaxRecommendedSize(), true);
                try {
                    IOManager.saveImageWithOverWrite(ei.getImage(), ei.getParentLibraryID(), ei.getImageID());
                } catch (IOException ex) {
                    Logger.getLogger(FlexiQuestionContainer.class.getName()).log(Level.SEVERE, "ocurred while trying to save a embeddedImage with rewrite using IOManager\n"
                            + "in order to save a resized version of the PASTED image upon user request (user was just told the image is larger than recommended, and asked if they would\n"
                            + "like it resized).", ex);
                }
            }
        }

        ((RichTextArea) focusOwner).insertComponent(eg);
        return true;
    }

    @Override
    public void maximise() {
        super.maximise();
        int prefHeight = questionText.getSize().height + answerText.getSize().height + 80;
        if (usePostAnswerText.isSelected()) {
            prefHeight += postAnswerText.getSize().height + 30;
        }
        if (prefHeight > MAX_CONTAINER_HEIGHT) {
            prefHeight = MAX_CONTAINER_HEIGHT;
        }
        this.setPreferredSize(new Dimension(this.getPreferredSize().width, prefHeight));
        this.setMaximumSize(new Dimension((int) 1000, MAX_CONTAINER_HEIGHT));
    }

    @Override
    protected void paintContentPanel(Graphics2D g2d) {
        if (minimised) {
            contentPanel.setBorder(BorderFactory.createEmptyBorder(50, 1, 1, 1));
            String printMe = questionText.getText();
            if (printMe.length() > 50) {
                printMe = printMe.substring(0, 48) + "...";
            }

            if (printMe.isEmpty()) {
                printMe = "empty question";
            }

            g2d.drawString(printMe, 10, 20);
        } else {
            contentPanel.setBorder(BorderFactory.createEmptyBorder(7, 7, 7, 7));
        }
    }

    /**
     * Static text focus listener allows for the rich text toolbar to follow which
     * <code>RichTextArea</code> has focus. The same instance of this listener is added
     * to every <code>RichTextArea</code> contained by a <code>FlexiQuestionContainer</code>.
     */
    public static class TextFocusListener implements FocusListener {

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
     * Listens for changes to the document, and then calls the <code>resetSize</code>
     * method. This method resets the preferred size of the <code>RichTextArea</code> whose
     * document has changed to suit the size of the content, within the bounds of
     * TEXT_AREA_MAX_SIZE and TEXT_AREA_MIN_SIZE fields of <code>FlexiQuestionContainer</code>.
     */
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

    public void resetSize(RichTextArea txtArea) {
        try {
            javax.swing.text.Document doc = txtArea.getDocument();
            Dimension d = txtArea.getPreferredSize();
            Rectangle r = txtArea.modelToView(doc.getLength());

            d.height = r.y + r.height + txtArea.getToolbar().getHeight();

            if (d.getHeight() < txtArea.getScroller().getMaximumSize().getHeight()) {
                //if the height needed for content is less than the minimum size of the scroller, then set to min size of scroller
                if (d.getHeight() < txtArea.getScroller().getMinimumSize().getHeight()) {
                    d.height = (int) txtArea.getScroller().getMinimumSize().getHeight();
                }
                txtArea.getScroller().setPreferredSize(d);
            } else {
                txtArea.getScroller().setPreferredSize(txtArea.getScroller().getMaximumSize());
            }

            //the following if-else structure allows the content panel to grow and shrink
            //but stops it from exceding the maximum height. If it exceeds the maximum height, it grows past
            //the boundary of the question container and pushes the containers below it down the question list
            //with empty space appearing between the two containers.
            if (contentPanel.getPreferredSize().height > MAX_CONTAINER_HEIGHT) {
                contentPanel.setPreferredSize(new Dimension(contentPanel.getPreferredSize().width, MAX_CONTAINER_HEIGHT));
            } else {
                contentPanel.setPreferredSize(null);
                if (contentPanel.getPreferredSize().height > MAX_CONTAINER_HEIGHT) {
                    contentPanel.setPreferredSize(new Dimension(contentPanel.getPreferredSize().width, MAX_CONTAINER_HEIGHT));
                }
            }

            minimise();
            maximise();

            txtArea.getScroller().validate();
            FlexiQuestionContainer.this.validate();

        } catch (Exception e2) {
            Logger.getLogger(FlexiQuestionContainer.class.getName()).log(Level.WARNING, "Could not resize the text area to fit content.", e2);
        }
    }

    /**
     * Updates the interface and question object when the use post answer option
     * is changed via the supplied checkbox.
     */
    private class UsePostAnswerTextAction extends AbstractAction {

        public UsePostAnswerTextAction() {
            super("use post-answer text");
        }

        public void actionPerformed(ActionEvent e) {
            if (usePostAnswerText.isSelected()) {
                flexiQuestion.setUsePostAnswerText(true);
                postAnswerText.getScroller().setVisible(true);
                lblPostAnswer.setVisible(true);

                //resize to fit it
                int prefHeight = questionText.getSize().height + answerText.getSize().height + 80;
                if (usePostAnswerText.isSelected()) {
                    prefHeight += postAnswerText.getSize().height + 80;
                }
                if (prefHeight > MAX_CONTAINER_HEIGHT) {
                    prefHeight = MAX_CONTAINER_HEIGHT;
                }
                FlexiQuestionContainer.this.setPreferredSize(new Dimension(FlexiQuestionContainer.this.getPreferredSize().width, prefHeight));
                FlexiQuestionContainer.this.setMaximumSize(new Dimension((int) 1000, MAX_CONTAINER_HEIGHT));
            } else {
                flexiQuestion.setUsePostAnswerText(false);
                postAnswerText.getScroller().setVisible(false);
                lblPostAnswer.setVisible(false);
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
                //open an image acquisition dialog
                ImageAcquisitionDialog imgDialog = IOManager.getImageAcquisitionDialog();
                imgDialog.setVisible(true);

                EmbeddedGraphic eg = null;
                String id = "";
                //if the image is from a library, this is where it is from
                String fromLib = "";
                //if we're dealing with an image.
                if ((imgDialog.getAcquisitionSource() != ImageAcquisitionDialog.NONE) && (imgDialog.getAcquisitionSource() != ImageAcquisitionDialog.FROM_MATH_TEXT)) {
                    //FROM LIBRARY------------------------------------------------------------------------------
                    if (imgDialog.getAcquisitionSource() == ImageAcquisitionDialog.FROM_LIBRARY) {
                        fromLib = imgDialog.getAcquiredImageData().split("\n")[1];
                        id = imgDialog.getAcquiredImageData().split("\n")[0];
                        //if this is from the library to which this question belongs, then we do not need to resave it, simply insert it.
                        if (fromLib.equals(FlexiQuestionContainer.this.getQuestion().getParentLibrary())) {
                            try {
                                eg = new EmbeddedImage(IOManager.loadImage(fromLib, id), id, FlexiQuestionContainer.this.getQuestion().getParentLibrary());
                            } catch (IOException ex) {
                                Logger.getLogger(FlexiQuestionContainer.class.getName()).log(Level.SEVERE, "IO Exception occurred while attempting to load an image through the IOManager\n"
                                        + "to create an EmbeddedImage for a RichTextArea contained by a FlexiQuestionContainer. This occurred during user initiated ImageAcquisition from\n"
                                        + "a resource already added to the library. Image ID=" + id + " library=" + fromLib, ex);
                            }
                        } else {
                            BufferedImage img = null;
                            try {
                                //otherwise the image must be copied into the current library from the source library.
                                img = IOManager.loadImage(fromLib, id);
                            } catch (IOException ex) {
                                Logger.getLogger(FlexiQuestionContainer.class.getName()).log(Level.SEVERE, "IO Exception occurred while attempting to load an image through the IOManager\n"
                                        + " to create an EmbeddedImage for a RichTextArea contained by a FlexiQuestionContainer. This occurred during user initiated ImageAcquisition from\n"
                                        + "a resource from a different library to the one containing the destination FlexiQuestionContainer. Image ID=" + id + " , from library=" + fromLib
                                        + " , destination library=" + FlexiQuestionContainer.this.getQuestion().getParentLibrary(), ex);
                            }
                            try {
                                id = IOManager.saveImage(FlexiQuestionContainer.this.getQuestion().getParentLibrary(), img, id);
                            } catch (IOException ex) {
                                Logger.getLogger(FlexiQuestionContainer.class.getName()).log(Level.SEVERE, "IO Exception occurred while attempting to save an image to a library through the IOManager\n"
                                        + ". This occurred during user initiated ImageAcquisition from a resource from a different library to the one containing the destination FlexiQuestionContainer.\n"
                                        + "Image ID=" + id + " , from library=" + fromLib + " , destination library=" + FlexiQuestionContainer.this.getQuestion().getParentLibrary(), ex);
                            }
                            eg = new EmbeddedImage(img, id, FlexiQuestionContainer.this.getQuestion().getParentLibrary());
                        }
                        //FROM COLLECTION/NEW/FILE------------------------------------------------------------------------------
                    } else if ((imgDialog.getAcquisitionSource() == ImageAcquisitionDialog.FROM_COLLECTION) || (imgDialog.getAcquisitionSource() == ImageAcquisitionDialog.FROM_FILE)
                            || (imgDialog.getAcquisitionSource() == ImageAcquisitionDialog.FROM_NEW) || (imgDialog.getAcquisitionSource() == ImageAcquisitionDialog.FROM_CHEM_STRUCTURE)) {
                        try {
                            id = IOManager.saveImage(flexiQuestion.getParentLibrary(), imgDialog.getAcquiredImage(), imgDialog.getAcquiredImageData());
                        } catch (IOException ex) {
                            Logger.getLogger(FlexiQuestionContainer.class.getName()).log(Level.SEVERE, "IO Exception occurred while attempting to save an image to a library through the IOManager"
                                    + ". This occurred during user initiated ImageAcquisition from a collection, from file or from a newly created image."
                                    + " Destination library=" + FlexiQuestionContainer.this.getQuestion().getParentLibrary(), ex);
                        }
                        eg = new EmbeddedImage(imgDialog.getAcquiredImage(), id, FlexiQuestionContainer.this.getQuestion().getParentLibrary());
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
                        int resp = JOptionPane.showConfirmDialog(FlexiQuestionContainer.this, "This image is larger than the recommended maximum size. Would you\n"
                                + "like Ingatan to shrink the image to the largest recommended size?", "Large Image", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);

                        if (resp == JOptionPane.YES_OPTION) {
                            EmbeddedImage ei = (EmbeddedImage) eg;
                            ei.resizeTo(ei.getMaxRecommendedSize(), true);
                            try {
                                IOManager.saveImageWithOverWrite(ei.getImage(), ei.getParentLibraryID(), ei.getImageID());
                            } catch (IOException ex) {
                                Logger.getLogger(FlexiQuestionContainer.class.getName()).log(Level.SEVERE, "ocurred while trying to save an embeddedImage with rewrite using IOManager\n"
                                        + "in order to save a resized version of the image upon user request (user was just told the image is larger than recommended, and asked if they would\n"
                                        + "like it resized).", ex);
                            }
                        }
                    }
                }

                //lastly, add the image to the appropriate text box
                if (e.getSource().equals(questionText.getToolbar())) {
                    questionText.insertComponent(eg);
                    resetSize(questionText);
                } else if (e.getSource().equals(answerText.getToolbar())) {
                    answerText.insertComponent(eg);
                    resetSize(answerText);
                } else if (e.getSource().equals(postAnswerText.getToolbar())) {
                    postAnswerText.insertComponent(eg);
                    resetSize(postAnswerText);
                }
            }
        }

        public void fontChanged(RichTextToolbarEvent e) {
        }
    }

    public String createClipboardData() {
        //if a RichTextArea is focussed:
        if (KeyboardFocusManager.getCurrentKeyboardFocusManager().getFocusOwner() instanceof RichTextArea) {
            RichTextArea txtArea = (RichTextArea) KeyboardFocusManager.getCurrentKeyboardFocusManager().getFocusOwner();

            //if there is no selection, return empty sting
            if (txtArea.getSelectionStart() == txtArea.getSelectionEnd()) {
                return "";
            }

            Document doc = new Document();

            //lib metadata
            org.jdom.Element e = new org.jdom.Element(this.getClass().getName());
            e.setAttribute("parentLib", question.getParentLibrary());
            e.setText(txtArea.getRichText(txtArea.getSelectionStart(), txtArea.getSelectionEnd()));
            doc.setRootElement(e);

            XMLOutputter fmt = new XMLOutputter();
            return fmt.outputString(doc);
        } else {
            return "";
        }
    }

    public class RichTextTransferHandler extends TransferHandler {

        @Override
        public int getSourceActions(JComponent c) {
            return TransferHandler.COPY_OR_MOVE;
        }

        @Override
        protected Transferable createTransferable(JComponent c) {
            return new StringSelection(createClipboardData());
        }

        @Override
        public boolean importData(TransferHandler.TransferSupport s) {
            return importData((JComponent) s.getComponent(), s.getTransferable(), s.isDrop());
        }

        @Override
        public boolean importData(JComponent c, Transferable t) {
            return importData(c, t, false);
        }

        private boolean importData(JComponent c, Transferable t, boolean isDrop) {
            //try to paste image data from the clipboard
            if (attemptImagePaste()) {
                return true;
            }

            if (t.isDataFlavorSupported(DataFlavor.stringFlavor) == false) {
                return false;
            }

            try {
                ((RichTextArea) c).replaceSelection("");
                ((RichTextArea) c).insertRichText(parseData((String) t.getTransferData(DataFlavor.stringFlavor)));
            } catch (UnsupportedFlavorException ex) {
                Logger.getLogger(FlexiQuestionContainer.class.getName()).log(Level.SEVERE, "While trying to get transfer data from RichTextTransferable.", ex);
            } catch (IOException ex) {
                Logger.getLogger(FlexiQuestionContainer.class.getName()).log(Level.SEVERE, "While trying to get transfer data from RichTextTransferable.", ex);
            }

            //remove any stray answer fields - these are fields that return true for isOnlyForAnswerArea
            //but have been added to either the question text or post-answer text.
            if (((RichTextArea) c).equals(answerText) == false) {
                removeStrayAnswerFields((RichTextArea) c);
            }

            //contextualise the answer fields now they have been pasted, otherwise they will
            //have their default context values.
            contextualiseAnswerFields((RichTextArea) c);

            return true;
        }

        @Override
        protected void exportDone(JComponent source, Transferable data, int action) {
            super.exportDone(source, data, action);
            if (action == TransferHandler.MOVE) {
                ((RichTextArea) source).replaceSelection("");
            }
        }

        /**
         * Removes any answer fields that return <code>true</code> to their <code>isOnlyForAnswerArea</code>
         * method, but have been added to the specified <code>txtArea</code>.
         * @param txtArea the text area to search for stray answer fields in.
         */
        private void removeStrayAnswerFields(RichTextArea txtArea) {
            int runCount;
            int paragraphCount = txtArea.getDocument().getDefaultRootElement().getElementCount();
            Element curEl = null;
            AttributeSet curAttr = null;
            AttributeSet prevAttr = null;

            for (int i = 0; i < paragraphCount; i++) {
                //each paragraph has 'runCount' runs
                runCount = txtArea.getDocument().getDefaultRootElement().getElement(i).getElementCount();
                for (int j = 0; j < runCount; j++) {
                    curEl = txtArea.getDocument().getDefaultRootElement().getElement(i).getElement(j);
                    if (curEl != null) {
                        curAttr = curEl.getAttributes();

                        if (curEl.getName().equals(StyleConstants.ComponentElementName)) //this is a component
                        {
                            //this run is a component. May be an answer field, picture or math text component.
                            Component o = (Component) curAttr.getAttribute(StyleConstants.ComponentAttribute);
                            if (o instanceof IAnswerField) {
                                if (((IAnswerField) o).isOnlyForAnswerArea()) {
                                    //remove the answer field by selecting it and setting it to ""
                                    txtArea.setSelectionStart(curEl.getStartOffset());
                                    txtArea.setSelectionEnd(curEl.getEndOffset());
                                    txtArea.replaceSelection("");
                                }
                            }
                        }
                    }
                }
            }
        }

        /**
         * Parses clipboard representation of rich text area markup. Resolves all references if the parent library ID has changed. Saves
         * all resolved resources.
         * @param xml the clipboard representation of copied rich text markup.
         * @return the raw rich text representation that can now be passed to a <code>RichTextArea</code>. All references in this text have been resolved
         * to the newly created files (if library id has changed).
         */
        private String parseData(String xml) {

            //nothing to parse, so leave
            if (xml.trim().equals("") == true) {
                return null;
            }

            //try to build document from input string, if this doesn't work, we have plain text
            SAXBuilder sax = new SAXBuilder();
            Document doc = null;
            try {
                doc = sax.build(new StringReader(xml));
            } catch (JDOMException ex) {
                return xml + "[end]";
            } catch (IOException ex) {
                return xml + "[end]";
            }

            //nothing to parse, so leave
            if (doc == null) {
                return null;
            }

            String parentLibID = doc.getRootElement().getAttributeValue("parentLib");
            String richText = doc.getRootElement().getText();

            //need to ensure we are in the same library as the original library, if not, need to resolve images/files
            if (parentLibID.equals(question.getParentLibrary()) == false) {
                RichTextArea tempEditArea = new RichTextArea();
                tempEditArea.setRichText(richText);

                //traverse the rich text area for any components, and reset their parentLibrary values
                int runCount;
                int paragraphCount = tempEditArea.getDocument().getDefaultRootElement().getElementCount();
                Element curEl = null;
                AttributeSet curAttr = null;

                for (int i = 0; i < paragraphCount; i++) {
                    //each paragraph has 'runCount' runs
                    runCount = tempEditArea.getDocument().getDefaultRootElement().getElement(i).getElementCount();
                    for (int j = 0; j < runCount; j++) {
                        curEl = tempEditArea.getDocument().getDefaultRootElement().getElement(i).getElement(j);
                        curAttr = curEl.getAttributes();

                        if (curEl.getName().equals(StyleConstants.ComponentElementName)) //this is a component
                        {
                            //this run is a component. May be an answer field, picture or math text component.
                            Component o = (Component) curAttr.getAttribute(StyleConstants.ComponentAttribute);
                            if (o instanceof IAnswerField) {
                                ((IAnswerField) o).resaveImagesAndResources(question.getParentLibrary());
                                ((IAnswerField) o).setParentLibraryID(question.getParentLibrary());
                            } else if (o instanceof EmbeddedImage) {
                                String id = IOManager.copyImage(((EmbeddedImage) o).getParentLibraryID(), ((EmbeddedImage) o).getImageID(), question.getParentLibrary());
                                ((EmbeddedImage) o).setParentLibraryID(question.getParentLibrary());
                                ((EmbeddedImage) o).setImageID(id);
                            }
                        }
                    }
                }
                richText = tempEditArea.getRichText();
            }

            //this will be challenging particularly for answer fields. Guess: load field, change parent library, save field? What triggers IOManager save of images?
            return richText;
        }
    }

    /**
     * Get the question text in rich text format.
     * @return the question text in rich text format.
     */
    public String getQuestionText() {
        return questionText.getRichText();
    }

    /**
     * Get the answer text in rich text format.
     * @return the answer text in rich text format.
     */
    public String getAnswerText() {
        return answerText.getRichText();
    }

    /**
     * Get the post answer text in rich text format.
     * @return the post answer text in rich text format.
     */
    public String getPostAnswerText() {
        return postAnswerText.getRichText();
    }

    /**
     * Gets whether or not the post answer text is being used.
     * @return <code>true</code> if the post answer text is being used.
     */
    public boolean getUsePostAnswerText() {
        return usePostAnswerText.isSelected();
    }
}
