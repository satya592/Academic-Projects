/*
 * AnsFieldEmbeddedAudio.java
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

import java.awt.Dimension;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.filechooser.FileFilter;
import org.ingatan.ThemeConstants;
import org.ingatan.component.text.RichTextArea;
import org.ingatan.io.IOManager;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.jdom.output.XMLOutputter;
import org.newdawn.easyogg.OggClip;

/**
 * Answer field that allows the user to embed OGG format audio into questions. This is based on
 * the JOgg and JOrbis libraries, using the EasyOgg wrapper from www.cokeandcode.com. The answer field
 * consists of a play button (which doubles as a pause button with correct change in icon), a stop button,
 * and in the edit context only, a 'select file' button.
 *
 * The audio file is saved to this library using the <code>IOManager</code>'s <code>saveResource</code> method.
 * @author Thomas Everingham
 * @version 1.0
 */
public class AnsFieldEmbeddedAudio extends JPanel implements IAnswerField, FocusListener, ActionListener {

    /**
     * Whether or not the answer field is currently in the edit context.
     */
    private boolean inEditContext = true;
    /**
     * The libraryID of library that contains this instance of the answer field.
     */
    private String parentLibraryID = "";
    /**
     * Button in edit context for selecting an audio file.
     */
    private JButton btnLoadFile = new JButton(new LoadAction());
    /**
     * Button for playing the selected audio file.
     */
    private JButton btnPlay = new JButton(new PlayAction());
    /**
     * Button for stopping playback of the selected audio file.
     */
    private JButton btnStop = new JButton(new StopAction());
    /**
     * FileID for the audio file.
     */
    private String audioFileID = "";
    /**
     * Ogg audio clip (easyogg).
     */
    private OggClip audioClip = null;
    /**
     * Whether or not the audio clip is playing.
     */
    private boolean playing = false;

    public AnsFieldEmbeddedAudio() {
        btnPlay.setMargin(new Insets(0, 0, 0, 0));
        btnPlay.setFocusable(false);

        btnStop.setMargin(new Insets(0, 0, 0, 0));
        btnStop.setFocusable(false);

        btnLoadFile.setMargin(new Insets(0, 0, 0, 0));
        btnLoadFile.setFocusable(false);

        this.setMaximumSize(new Dimension(60, 40));
        this.setOpaque(false);
        this.setBorder(BorderFactory.createLineBorder(ThemeConstants.backgroundUnselected));

        this.setFocusable(false);

        rebuild();
    }

    /**
     * Rebuild the component for the current context.
     */
    private void rebuild() {
        this.removeAll();
        this.add(btnPlay);
        this.add(btnStop);
        this.setBorder(BorderFactory.createLineBorder(ThemeConstants.borderUnselected));

        if (inEditContext) {
            this.add(btnLoadFile);
            this.setBorder(BorderFactory.createLineBorder(ThemeConstants.backgroundUnselected));
        }
    }

    public String getDisplayName() {
        return "Embedded Audio";
    }

    public boolean isOnlyForAnswerArea() {
        return false;
    }

    public float checkAnswer() {
        return 1.0f;
    }

    public int getMaxMarks() {
        return 0;
    }

    public int getMarksAwarded() {
        return 0;
    }

    public void displayCorrectAnswer() {
        this.setEnabled(false);
        btnPlay.setEnabled(false);
        btnStop.setEnabled(false);
        if (audioClip != null) {
            audioClip.stop();
        }
    }

    public RichTextArea getContainerTextArea() {
        if (this.getParent() == null)
            return null;
        
        if (this.getParent().getParent() instanceof RichTextArea) {
            return (RichTextArea) this.getParent().getParent();
        } else {
            return null;
        }

    }

    public void setContext(boolean inLibraryContext) {
        inEditContext = inLibraryContext;

        if (this.getContainerTextArea() == null) {
            btnLoadFile.setEnabled(false);
            btnPlay.setEnabled(false);
            btnStop.setEnabled(false);
        } else {
            this.getContainerTextArea().addFocusListener(this);
        }

        rebuild();
    }

    public String writeToXML() {
        Document doc = new Document();

        //data
        Element e = new Element(this.getClass().getName());
        e.setAttribute("parentLibID", parentLibraryID);
        e.setAttribute("audioFileID", audioFileID);
        //version field allows future versions of this field to be back compatible.
        //especially important for these default fields!
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

        parentLibraryID = doc.getRootElement().getAttribute("parentLibID").getValue();
        audioFileID = doc.getRootElement().getAttribute("audioFileID").getValue();

        //input stream for the audio file
        InputStream inputStream = null;
        //load the audio file input stream
        if (audioFileID.trim().isEmpty() == false) {
            try {
                inputStream = IOManager.loadResource(parentLibraryID, audioFileID);
            } catch (IOException ex) {
                Logger.getLogger(AnsFieldEmbeddedAudio.class.getName()).log(Level.SEVERE, "While attempting to get a resource stream for the audio answer field during read from XML.\n"
                        + "parentLibID=" + parentLibraryID + " & audioFileID=" + audioFileID, ex);
            }
        }

        //don't try to create an audio clip if the inputStream is null.
        if (inputStream == null) {
            audioClip = null;
            return;
        }

        //try to create the OggClip object (easyogg)
        try {
            audioClip = new OggClip(inputStream);
            audioClip.addActionListener(this);
        } catch (IOException ex) {
            Logger.getLogger(AnsFieldEmbeddedAudio.class.getName()).log(Level.SEVERE, "Occurred while trying to create the EasyOgg audio clip", ex);
        }
    }

    public String getParentLibraryID() {
        return parentLibraryID;
    }

    public void setParentLibraryID(String id) {
        parentLibraryID = id;
    }

    public void setQuizContinueListener(ActionListener listener) {
        //this is not implemented as there is no logical event that should trigger the ContinueAction of the QuizWindow
    }

    public void resaveImagesAndResources(String newParentLibrary) {
        audioFileID = IOManager.copyResource(parentLibraryID, audioFileID, newParentLibrary);
    }

    public void focusGained(FocusEvent e) {
    }

    public void focusLost(FocusEvent e) {
        if ((audioClip != null) && (audioClip.isPaused() == false)) {
            audioClip.pause();
            btnPlay.getAction().putValue(PlayAction.LARGE_ICON_KEY, new ImageIcon(AnsFieldEmbeddedAudio.class.getResource("/resources/icons/play.png")));
        }
    }

    /**
     * This action is performed when the oggclip playback stops. This is added as an
     * <code>ActionListener</code> to the <code>OggClip</code>.
     * @param e the <code>ActionEvent</code> - will be <code>null</code>.
     */
    public void actionPerformed(ActionEvent e) {
        if (audioClip != null) {
            btnPlay.getAction().putValue(PlayAction.LARGE_ICON_KEY, new ImageIcon(AnsFieldEmbeddedAudio.class.getResource("/resources/icons/play.png")));
            playing = false;
        }
    }

    private class LoadAction extends AbstractAction {

        public LoadAction() {
            super("", new ImageIcon(AnsFieldEmbeddedAudio.class.getResource("/resources/icons/folder.png")));
        }

        public void actionPerformed(ActionEvent e) {
            //set focus to the containing RichTextArea. This text area having focus
            //means that we can pause the player when the text field loses focus.
            getContainerTextArea().requestFocus();

            //if there is an audio clip set, stop playback before changing the file
            if (audioClip != null) {
                audioClip.stop();
                btnPlay.setIcon(new ImageIcon(AnsFieldEmbeddedAudio.class.getResource("/resources/icons/play.png")));
            }

            //set up and show a file chooser
            JFileChooser fileChoose = new JFileChooser();
            fileChoose.setDialogTitle("Select Ogg File");
            //set up the file filter
            fileChoose.setFileFilter(new FileFilter() {

                @Override
                public boolean accept(File f) {
                    if ((f.isDirectory() == true) || (f.getName().endsWith(".ogg")) || (f.getName().contains(".") == false) || (f.getName().endsWith(".oga"))) {
                        return true;
                    } else {
                        return false;
                    }
                }

                @Override
                public String getDescription() {
                    return "Ogg Audio Files";
                }
            });

            fileChoose.showOpenDialog(AnsFieldEmbeddedAudio.this);

            //get the audio file
            File audioFile = fileChoose.getSelectedFile();

            //return if no file selected
            if (audioFile == null) {
                return;
            }

            //ensure it exists and can be read
            if (audioFile.exists() == false) {
                JOptionPane.showMessageDialog(AnsFieldEmbeddedAudio.this, "File " + audioFile.getName() + " does not exist.", "File Doesn't Exist", JOptionPane.ERROR_MESSAGE);
                return;
            } else if (audioFile.canRead() == false) {
                JOptionPane.showMessageDialog(AnsFieldEmbeddedAudio.this, "Cannot read from file: " + audioFile.getName(), "Cannot Read File", JOptionPane.ERROR_MESSAGE);
                return;
            }

            //try to save the audio file to the library
            try {
                audioFileID = IOManager.saveResource(parentLibraryID, audioFile.getAbsolutePath());
            } catch (IOException ex) {
                Logger.getLogger(AnsFieldEmbeddedAudio.class.getName()).log(Level.SEVERE, "Occurred while trying to save the selected audio file to the library.\n"
                        + "libraryID=" + parentLibraryID + " & audioFile=" + audioFile.getAbsolutePath(), ex);
            }

            try {
                audioClip = new OggClip(IOManager.loadResource(parentLibraryID, audioFileID));
                audioClip.addActionListener(this);
            } catch (IOException ex) {
                Logger.getLogger(AnsFieldEmbeddedAudio.class.getName()).log(Level.SEVERE, "While trying to create an new EasyOgg OggClip object from the selected audio file.", ex);
            }

        }
    }

    private class PlayAction extends AbstractAction {

        public PlayAction() {
            super("", new ImageIcon(AnsFieldEmbeddedAudio.class.getResource("/resources/icons/play.png")));
        }

        public void actionPerformed(ActionEvent e) {
            //set focus to the containing RichTextArea. This text area having focus
            //means that we can pause the player when the text field loses focus.
            getContainerTextArea().requestFocus();

            if (audioClip != null) {
                if (audioClip.isPaused() && (audioClip.stopped() == false)) {
                    playing = true;
                    this.putValue(PlayAction.LARGE_ICON_KEY, new ImageIcon(AnsFieldEmbeddedAudio.class.getResource("/resources/icons/pause.png")));
                    audioClip.resume();
                } else if (playing) {
                    playing = false;
                    this.putValue(PlayAction.LARGE_ICON_KEY, new ImageIcon(AnsFieldEmbeddedAudio.class.getResource("/resources/icons/play.png")));
                    audioClip.pause();
                } else if (audioClip.stopped()) {
                    playing = true;
                    this.putValue(PlayAction.LARGE_ICON_KEY, new ImageIcon(AnsFieldEmbeddedAudio.class.getResource("/resources/icons/pause.png")));
                    audioClip.play();
                }
            }
        }
    }

    private class StopAction extends AbstractAction {

        public StopAction() {
            super("", new ImageIcon(AnsFieldEmbeddedAudio.class.getResource("/resources/icons/stop.png")));
        }

        public void actionPerformed(ActionEvent e) {
            //set focus to the containing RichTextArea. This text area having focus
            //means that we can pause the player when the text field loses focus.
            getContainerTextArea().requestFocus();

            if (audioClip != null) {
                audioClip.stop();
                audioClip.close();

                AnsFieldEmbeddedAudio.this.setEnabled(false);

                try {
                    audioClip = new OggClip(IOManager.loadResource(parentLibraryID, audioFileID));
                    audioClip.addActionListener(this);
                } catch (IOException ex) {
                    Logger.getLogger(AnsFieldEmbeddedAudio.class.getName()).log(Level.SEVERE, "While trying to load the audio file for this answer field"
                            + " from file after the playback was stopped.", ex);
                }

                AnsFieldEmbeddedAudio.this.setEnabled(true);

                playing = false;
                btnPlay.getAction().putValue(PlayAction.LARGE_ICON_KEY, new ImageIcon(AnsFieldEmbeddedAudio.class.getResource("/resources/icons/play.png")));
            }
        }
    }
}
