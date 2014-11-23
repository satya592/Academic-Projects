/*
 * GeneralRichTextTransferHandler.java
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
package org.ingatan.component.text;

import java.awt.Component;
import java.awt.KeyboardFocusManager;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.io.StringReader;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JComponent;
import javax.swing.TransferHandler;
import javax.swing.text.AttributeSet;
import javax.swing.text.Element;
import javax.swing.text.StyleConstants;
import org.ingatan.component.answerfield.IAnswerField;
import org.ingatan.component.librarymanager.FlexiQuestionContainer;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.jdom.output.XMLOutputter;

/**
 * A general transfer handler for the <code>RichTextArea</code>. Does not allow the transfer of answer fields
 * or images. 1) answer fields are not allowed in <code>RichTextArea</code> in the general case, and 2) images
 * cannot be resolved for the general case (imageID and parentID), as this rich text area could be anywhere.<br>
 * <br>
 * The <code>TransferHandler</code> that deals with transfer for the three flexi question <code>RichTextArea</code>s
 * is a subclass (<code>RichTextTransferHandler</code>) of the <code>FlexiQuestionContainer</code> class.
 *
 * @author Thomas Everingham
 * @version 1.0
 */
public class GeneralRichTextTransferHandler extends TransferHandler {

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

        return true;
    }

    @Override
    protected void exportDone(JComponent source, Transferable data, int action) {
        super.exportDone(source, data, action);
        if (action == TransferHandler.MOVE) {
            ((RichTextArea) source).replaceSelection("");
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

            org.jdom.Document doc = new org.jdom.Document();

            //lib metadata
            org.jdom.Element e = new org.jdom.Element(this.getClass().getName());
            e.setAttribute("parentLib", "non-flexiQ-source");
            e.setText(txtArea.getRichText(txtArea.getSelectionStart(), txtArea.getSelectionEnd()));
            doc.setRootElement(e);

            XMLOutputter fmt = new XMLOutputter();
            return fmt.outputString(doc);
        } else {
            return "";
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
        org.jdom.Document doc = null;
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

        String richText = doc.getRootElement().getText();

        //need to get rid of any image components or answer fields. Cannot easily resolve image ID and parentLibraryID
        //for the general case. Answer fields not allowed in the general case.
        RichTextArea tempEditArea = new RichTextArea();
        tempEditArea.setRichText(richText);

        //traverse the rich text area for any components, and reset their parentLibrary values
        int runCount;
        int paragraphCount = tempEditArea.getDocument().getDefaultRootElement().getElementCount();
        Element curEl = null;
        AttributeSet curAttr = null;
        AttributeSet prevAttr = null;

        for (int i = 0; i < paragraphCount; i++) {
            //each paragraph has 'runCount' runs
            runCount = tempEditArea.getDocument().getDefaultRootElement().getElement(i).getElementCount();
            for (int j = 0; j < runCount; j++) {
                curEl = tempEditArea.getDocument().getDefaultRootElement().getElement(i).getElement(j);
                if (curEl != null) {
                    curAttr = curEl.getAttributes();

                    if (curEl.getName().equals(StyleConstants.ComponentElementName)) //this is a component
                    {
                        //remove any answer fields or image components
                        Component o = (Component) curAttr.getAttribute(StyleConstants.ComponentAttribute);
                        if (o instanceof IAnswerField) {
                            tempEditArea.setSelectionStart(curEl.getStartOffset());
                            tempEditArea.setSelectionEnd(curEl.getEndOffset());
                            tempEditArea.replaceSelection("");
                        } else if (o instanceof EmbeddedImage) {
                            tempEditArea.setSelectionStart(curEl.getStartOffset());
                            tempEditArea.setSelectionEnd(curEl.getEndOffset());
                            tempEditArea.replaceSelection("");
                        }
                    }
                }
            }
        }
        richText = tempEditArea.getRichText();

        return richText;
    }
}
