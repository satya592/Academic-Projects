/*
 * AnsFieldHint.java
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

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.StringReader;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.text.AttributeSet;
import javax.swing.text.StyleConstants;
import org.ingatan.ThemeConstants;
import org.ingatan.component.image.ImageAcquisitionDialog;
import org.ingatan.component.text.EmbeddedGraphic;
import org.ingatan.component.text.EmbeddedImage;
import org.ingatan.component.text.EmbeddedMathTeX;
import org.ingatan.component.text.GeneralRichTextTransferHandler;
import org.ingatan.component.text.RichTextArea;
import org.ingatan.event.RichTextToolbarEvent;
import org.ingatan.event.RichTextToolbarListener;
import org.ingatan.io.IOManager;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.jdom.output.XMLOutputter;

/**
 *
 * @author Thomas Everingham
 * @version 1.0
 */
public class AnsFieldHint extends JButton implements IAnswerField {

    /**
     * Whether or not this answer field is currently in edit or quiz context. <code>
     * true</code> if in the edit context (i.e. library manager, answer field list editor).
     */
    private boolean isEditContext = true;
    /**
     * The libraryID of the library that this answer field instance exists within. This is
     * used to access and write images from and to the library.
     */
    private String parentLibID = "";
    /**
     * The text area that is used to edit and display the hint.
     */
    private RichTextArea txtArea = new RichTextArea();
    /**
     * The popup menu that is shown when the hint button is pressed.
     */
    private JPopupMenu popup = new JPopupMenu();

    /**
     * Creates a new instance of <code>AnsFieldHint</code>.
     */
    public AnsFieldHint() {
        this.setMargin(new Insets(1, 1, 1, 1));
        this.setFont(ThemeConstants.niceFont);
        this.setAction(new HintAction());

        txtArea.getScroller().setPreferredSize(new Dimension(350, 150));
        txtArea.getToolbar().addRichTextToolbarListener(new TextToolbarListener());
        txtArea.setTransferHandler(new GeneralRichTextTransferHandler());

        popup.add(txtArea.getScroller());

        rebuild();
    }

    /**
     * Rebuilds this answer field to reflect the current state of the
     * <code>isEditContext</code> flag.
     */
    public void rebuild() {
        if (isEditContext) {
            txtArea.setEditable(true);
            txtArea.setToolbarVisible(true);
            txtArea.setOpaque(true);
        } else {
            txtArea.setEditable(false);
            txtArea.setToolbarVisible(false);
            txtArea.setOpaque(false);
            txtArea.getScroller().setBorder(BorderFactory.createEmptyBorder());
        }
    }

    public String getDisplayName() {
        return "Hint Popup";
    }

    public boolean isOnlyForAnswerArea() {
        return false;
    }

    public float checkAnswer() {
        return 0.0f;
    }

    public int getMaxMarks() {
        return 0;
    }

    public int getMarksAwarded() {
        return 0;
    }

    public void displayCorrectAnswer() {
        return;
    }

    public void setContext(boolean inEditContext) {
        isEditContext = inEditContext;
        rebuild();
    }

    public String writeToXML() {
        Document doc = new Document();

        //lib metadata
        Element e = new Element(this.getClass().getName());
        e.setAttribute("parentLib", parentLibID);
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
            Logger.getLogger(AnsFieldHint.class.getName()).log(Level.SEVERE, "While trying to create a JDOM document in the readInXML method.", ex);
        } catch (IOException ex) {
            Logger.getLogger(AnsFieldHint.class.getName()).log(Level.SEVERE, "While trying to create a JDOM document in the readInXML method.", ex);
        }

        //nothing to parse, so leave
        if (doc == null) {
            return;
        }

        parentLibID = doc.getRootElement().getAttributeValue("parentLib");
        String correctAnswer = doc.getRootElement().getText().replace(RichTextArea.CHARCODE_OPENING_SQUARE_BRACKET, "[").replace(RichTextArea.CHARCODE_CLOSING_SQUARE_BRACKET, "]");
        txtArea.setRichText(correctAnswer);
    }

    public String getParentLibraryID() {
        return parentLibID;
    }

    public void setParentLibraryID(String id) {
        parentLibID = id;
    }

    public void setQuizContinueListener(ActionListener listener) {
        //this is not implemented as there is no logical event that should trigger the QuizContinue action.
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

    /**
     * Action associated with the user pressing the 'hint' button.
     */
    private class HintAction extends AbstractAction {

        public HintAction() {
            super("Hint");
        }

        public void actionPerformed(ActionEvent e) {
            popup.show(AnsFieldHint.this, 20, 15);
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
                if (AnsFieldHint.this.getRootPane().getParent() instanceof JDialog) {
                    JOptionPane.showMessageDialog(AnsFieldHint.this, "Cannot insert an image into this field of the edit answer field dialog. Cannot save\n"
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
                                Logger.getLogger(AnsFieldHint.class.getName()).log(Level.SEVERE, "IO Exception occurred while attempting to load an image through the IOManager\n"
                                        + "to create an EmbeddedImage for a RichTextArea. This occurred during user initiated ImageAcquisition from\n"
                                        + "a resource already added to the library. Image ID=" + id + " library=" + fromLib, ex);
                            }
                        } else {
                            BufferedImage img = null;
                            try {
                                //otherwise the image must be copied into the current library from the source library.
                                img = IOManager.loadImage(fromLib, id);
                            } catch (IOException ex) {
                                Logger.getLogger(AnsFieldHint.class.getName()).log(Level.SEVERE, "IO Exception occurred while attempting to load an image through the IOManager\n"
                                        + " to create an EmbeddedImage for a RichTextArea. This occurred during user initiated ImageAcquisition from\n"
                                        + "a resource from a different library to the one containing the destination FlexiQuestionContainer. Image ID=" + id + " , from library=" + fromLib
                                        + " , destination library={" + getParentLibraryID() + "}", ex);
                            }
                            try {
                                id = IOManager.saveImage(getParentLibraryID(), img, id);
                            } catch (IOException ex) {
                                Logger.getLogger(AnsFieldHint.class.getName()).log(Level.SEVERE, "IO Exception occurred while attempting to save an image to a library through the IOManager\n"
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
                            Logger.getLogger(AnsFieldHint.class.getName()).log(Level.SEVERE, "IO Exception occurred while attempting to save an image to a library through the IOManager"
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
                        int resp = JOptionPane.showConfirmDialog(AnsFieldHint.this, "This image is larger than the recommended maximum size. Would you\n"
                                + "like Ingatan to shrink the image to the largest recommended size?", "Large Image", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);

                        if (resp == JOptionPane.YES_OPTION) {
                            EmbeddedImage ei = (EmbeddedImage) eg;
                            ei.resizeTo(ei.getMaxRecommendedSize(),true);
                            try {
                                IOManager.saveImageWithOverWrite(ei.getImage(), ei.getParentLibraryID(), ei.getImageID());
                            } catch (IOException ex) {
                                Logger.getLogger(AnsFieldHint.class.getName()).log(Level.SEVERE, "ocurred while trying to save an embeddedImage with rewrite using IOManager\n"
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
