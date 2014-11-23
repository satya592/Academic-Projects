/*
 * LibraryManagerWindow.java
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
 * If you find this program or any part of it useful, please tell me about it! I would be delighted
 * to hear from you at tom.ingatan@gmail.com.
 */
package org.ingatan.component.librarymanager;

import java.awt.event.MouseEvent;
import org.ingatan.ThemeConstants;
import org.ingatan.component.answerfield.IAnswerField;
import org.ingatan.component.text.RichTextArea;
import org.ingatan.data.FlexiQuestion;
import org.ingatan.data.IQuestion;
import org.ingatan.data.Library;
import org.ingatan.data.TableQuestion;
import org.ingatan.io.IOManager;
import org.ingatan.io.ParserWriter;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Insets;
import java.awt.KeyboardFocusManager;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.SwingConstants;
import org.ingatan.component.text.EmbeddedImage;
import org.jdom.DataConversionException;

/**
 * The library manager is Ingatan's content creation centre. It provides an interface
 * to create and sort groups, libraries and questions.
 * 
 * @author Thomas Everingham
 * @version 1.0
 */
public class LibraryManagerWindow extends JFrame implements WindowListener {

    /**
     * Allows for browsing of libraries by group. Also incorporates the group
     * editor button, library editor button, library create/delete buttons, and
     * the library export/import tool button.
     */
    LibraryBrowser libBrowser = new LibraryBrowser();
    /**
     * Where an open library is displayed. A continuous scrolling list of all questions
     * within the library, directly editable within the list.
     */
    QuestionList questionList = new QuestionList();
    /**
     * A toolbar with several question list related tools, such as group questions,
     * minimise/maximise all, select/deselect all, cut, copy, and paste, search, etc.
     */
    QuestionListToolbar questionToolbar = new QuestionListToolbar();
    /**
     * Displays statistics about the currently focussed question.
     */
    QuestionStatisticsBar questionStats = new QuestionStatisticsBar();
    /**
     * Displays statistics and information about the currently open library.
     */
    LibraryStatisticsPane libraryStats = new LibraryStatisticsPane();
    /**
     * Allows the user to insert answer fields into questions.
     */
    AnswerFieldPalette palette = new AnswerFieldPalette();
    /**
     * A popup that allows the user whether they would like to import or export libraries,
     * and then opens the corresponding dialog.
     */
    JPopupMenu exportImportMenu = new JPopupMenu();
    /**
     * A popup that allows the user to choose a tablequestion or a flexiquestion.
     */
    JPopupMenu questionTypeMenu = new JPopupMenu();
    /**
     * Popup telling the user that no library is selected.
     */
    JPopupMenu questionTypeMenuError = new JPopupMenu();
    /**
     * Content pane for the frame.
     */
    JPanel contentPane = new JPanel();
    /**
     * When this window is closed, this JFrame is set to be visible.
     */
    JFrame returnToOnClose;
    /**
     * For peace of mind of users who feel odd about simply closing the window.
     */
    JButton btnSaveExit = new JButton("Save and Close");

    /**
     * Creates a new LibraryManager instance.
     */
    public LibraryManagerWindow(JFrame returnToUponClose) {
        super("Library Manager");
        this.setIconImage(IOManager.windowIcon);
        returnToOnClose = returnToUponClose;
        this.setContentPane(contentPane);

        //yawn v
        setUpGUI();

        this.setSize(new Dimension((int) this.getWidth() + 20, 600));
        //ensures data is saved and packed away when the window is closed.
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        this.addWindowListener(this);

        libBrowser.setSelectedGroup(IOManager.getPreviouslySelectedGroup());

        KeyboardFocusManager.getCurrentKeyboardFocusManager().addPropertyChangeListener(new KeyboardFocusPropertyListener());

    }

    /**
     * GUI tasks.
     */
    private void setUpGUI() {
        exportImportMenu.add(new ImportAction());
        exportImportMenu.add(new ExportAction());
        questionTypeMenu.add(new FlexiQuestionTypeAction());
        questionTypeMenu.add(new TableQuestionTypeAction());
        questionTypeMenuError.add("No library selected");


        //set all the sizes.............................................
        libraryStats.setMaximumSize(new Dimension(250, 225));
        libraryStats.setPreferredSize(new Dimension(200, 225));
        libraryStats.setMinimumSize(new Dimension(200, 130));
        libraryStats.setAlignmentX(LEFT_ALIGNMENT);

        libBrowser.setMaximumSize(new Dimension(250, 1000));
        libBrowser.setMinimumSize(new Dimension(250, 300));
        libBrowser.setAlignmentX(LEFT_ALIGNMENT);
        libBrowser.addActionListener(new libBrowserActionListener());

        palette.setMinimumSize(new Dimension(300, 200));
        palette.setMaximumSize(new Dimension(310, 300));
        palette.setAlignmentX(LEFT_ALIGNMENT);
        palette.addActionListener(new AnswerFieldPaletteListener());

        questionList.scroller.setMinimumSize(new Dimension(600, 300));
        questionList.scroller.setMaximumSize(new Dimension(questionList.getMaximumQuestionContainerWidth(), 1500));
        questionList.setAlignmentX(LEFT_ALIGNMENT);

        questionStats.setMinimumSize(new Dimension(400, 25));
        questionStats.setMaximumSize(new Dimension(900, 25));
        questionStats.setAlignmentX(LEFT_ALIGNMENT);

        questionToolbar.setMinimumSize(new Dimension(396, 26));
        questionToolbar.setMaximumSize(new Dimension(396, 26));
        questionToolbar.setAlignmentX(LEFT_ALIGNMENT);
        questionToolbar.addActionListener(new ListToolbarListener());

        btnSaveExit.setMargin(new Insets(3, 15, 3, 15));
        btnSaveExit.setFont(ThemeConstants.niceFont);
        btnSaveExit.setIcon(new ImageIcon(LibraryManagerWindow.class.getResource("/resources/icons/accept.png")));
        btnSaveExit.addMouseListener(new SaveExitListener());

        //set all the sizes.............................................


        contentPane.setLayout(new BoxLayout(contentPane, BoxLayout.Y_AXIS));
        Box header = Box.createHorizontalBox();
        JLabel heading = new JLabel("Library Manager");
        heading.setFont(new Font(contentPane.getFont().getFamily(), Font.PLAIN, 26));
        heading.setHorizontalAlignment(SwingConstants.LEFT);
        header.setAlignmentX(LEFT_ALIGNMENT);
        header.add(Box.createHorizontalStrut(7));
        header.setMaximumSize(new Dimension(500, 100));
        header.add(heading);
        heading.setForeground(new Color(70, 70, 70));

        Box body = Box.createHorizontalBox();
        body.add(Box.createHorizontalStrut(30));
        body.setAlignmentX(LEFT_ALIGNMENT);
        Box vert = Box.createVerticalBox();
        vert.add(libBrowser);
        Box horiz = Box.createHorizontalBox();
        horiz.setMaximumSize(new Dimension(250, 200));
        horiz.setAlignmentX(LEFT_ALIGNMENT);
        horiz.add(Box.createHorizontalStrut(22));
        horiz.add(libraryStats);
        vert.add(Box.createVerticalStrut(10));
        vert.add(horiz);
        vert.setAlignmentY(TOP_ALIGNMENT);
        vert.setMaximumSize(new Dimension(250, 490));
        body.add(vert);


        //spacing between the library browser and question list
        body.add(Box.createHorizontalStrut(20));

        vert = Box.createVerticalBox();
        vert.setAlignmentY(TOP_ALIGNMENT);
        horiz = Box.createHorizontalBox();
        horiz.add(questionToolbar);
        horiz.setAlignmentX(LEFT_ALIGNMENT);
        vert.add(horiz);
        vert.add(Box.createVerticalStrut(10));
        vert.add(questionList);
        vert.add(Box.createVerticalStrut(10));
        horiz = Box.createHorizontalBox();
        horiz.add(questionStats);

        horiz.setAlignmentX(LEFT_ALIGNMENT);
        vert.add(horiz);
        vert.setMaximumSize(new Dimension(questionList.getMaximumQuestionContainerWidth(), 1500));

        body.add(vert);

        body.add(Box.createHorizontalStrut(30));

        vert = Box.createVerticalBox();
        JLabel lblAnsFields = new JLabel("Answer Fields");
        lblAnsFields.setAlignmentX(LEFT_ALIGNMENT);
        lblAnsFields.setForeground(ThemeConstants.borderSelected);
        vert.add(Box.createVerticalStrut(30));
        vert.add(lblAnsFields);
        vert.add(Box.createVerticalStrut(3));
        vert.add(palette);
        vert.add(Box.createVerticalStrut(10));
        vert.add(btnSaveExit);
        vert.setAlignmentY(TOP_ALIGNMENT);
        vert.setMaximumSize(new Dimension(210, 400));
        vert.setPreferredSize(new Dimension(200, 300));
        vert.setMinimumSize(new Dimension(200, 300));
        body.add(vert);
        contentPane.add(Box.createVerticalStrut(7));
        contentPane.add(header);
        contentPane.add(Box.createVerticalStrut(15));
        contentPane.add(body);
        this.validate();
        this.pack();
        this.setLocation(200, 100);
    }

    /**
     * Saves the currently selected library to the temporary folder (does not re-package library)
     * @throws IOException if there is a problem opening the library for writing
     */
    private void saveLibrary(String libraryID) throws IOException {
        //get the currently open library
        Library lib;
        lib = IOManager.getLibraryFromID(libraryID);

        //update the question data with the text in the library list question containers
        questionList.updateQuestionsWithContent();
        //update library with data from the question list
        lib.setQuestions(questionList.getQuestions());
        ParserWriter.writeLibraryFile(lib);
    }

    public void windowOpened(WindowEvent e) {
        if (IOManager.isFirstTimeLoadingLibManager()) {
            RichTextArea dispArea = new RichTextArea();

            dispArea.setPreferredSize(new Dimension(400, 280));
            dispArea.setSize(new Dimension(400, 280));
            dispArea.setMinimumSize(new Dimension(400, 280));

            dispArea.setBorder(BorderFactory.createEmptyBorder());
            dispArea.setEditable(false);
            dispArea.setOpaque(false);

            dispArea.setRichText("[aln]0[!aln][fam]Dialog[!fam][sze]16[!sze][col]51,51,51[!col]Welcome to Ingatan[sze]12[!sze][br]"
                    + "This message will only be shown once.[br][br]"
                    + "The library manager [u]automatically saves your work as you go[u]. Simply close the library manager window when you are done.[br][br]"
                    + "To get started, click the green + icon to the left to create a new library, and then the green + icon "
                    + "up the top to create a new question, as shown below.[br][br][aln]1[!aln][end]");

            BufferedImage img = null;
            try {
                img = ImageIO.read(Thread.currentThread().getContextClassLoader().getResource("resources/getStarted.png"));
            } catch (Exception ex) {
                Logger.getLogger(LibraryManagerWindow.class.getName()).log(Level.SEVERE, "While trying to load /resources/gettingStarted.png for the library manager's virgin load.", ex);
            }

            if (img != null) {
                EmbeddedImage eImg = new EmbeddedImage(img, "", "");
                eImg.setToolTipText("");
                dispArea.setCaretPosition(dispArea.getDocument().getLength());
                dispArea.insertComponent(eImg);
            }

            JOptionPane.showMessageDialog(LibraryManagerWindow.this, dispArea, "Welcome to Ingatan", JOptionPane.INFORMATION_MESSAGE);
            IOManager.setFirstTimeLoadingLibManager(false);
            ParserWriter.writePreferencesFile(IOManager.getSymbolMenuCharacterMap());
        }
    }

    /**
     * When the library manager window is closed, the IOManager is told to clean up
     * the temp directory and re-package all libraries that have been opened.
     * @param e
     */
    public void windowClosing(WindowEvent e) {
        IOManager.setPreviouslySelectedGroup(libBrowser.getSelectedGroupName());
        try {
            if (libBrowser.getSelectedLibraryID() != null) {
                saveLibrary(libBrowser.getSelectedLibraryID());
            }
            IOManager.cleanUpAndRepackage();
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(LibraryManagerWindow.this, "An IO exception occurred while trying to save the library data :-(\n\n"
                    + "Unfortunately loss of data may have occurred.", "IO Exception", JOptionPane.ERROR_MESSAGE);
            Logger.getLogger(LibraryManagerWindow.class.getName()).log(Level.SEVERE, "Could not save library data while closing the library manager.", ex);
        }

        //show the return to on close window
        returnToOnClose.setVisible(true);
    }

    public void windowClosed(WindowEvent e) {
    }

    public void windowIconified(WindowEvent e) {
    }

    public void windowDeiconified(WindowEvent e) {
    }

    public void windowActivated(WindowEvent e) {
    }

    public void windowDeactivated(WindowEvent e) {
    }

    /**
     * Responds to actions on the library browser.
     */
    private class libBrowserActionListener implements ActionListener {

        public void actionPerformed(ActionEvent e) {

            switch (e.getID()) {
                case LibraryBrowser.ADD_LIBRARY_ACTION:
                    LibraryEditorDialog newLib = new LibraryEditorDialog(LibraryManagerWindow.this, true, libBrowser.getSelectedGroupName());
                    newLib.setLibraryNameText("Charles");
                    newLib.setLibraryDescriptionText("");
                    newLib.libraryName.setSelectionStart(0);
                    newLib.libraryName.setSelectionEnd(newLib.libraryName.getText().length());
                    newLib.setTitle("Create Library");
                    newLib.setVisible(true);
                    libBrowser.updateLibraries();
                    break;
                case LibraryBrowser.REMOVE_LIBRARY_ACTION:
                    if (libBrowser.getSelectedLibraryIndex() == -1) {
                        JOptionPane.showMessageDialog(LibraryManagerWindow.this, "There is no library selected.", "Can't delete", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                    int resp = JOptionPane.showConfirmDialog(LibraryManagerWindow.this, "Are you sure you wish to delete this library and all "
                            + "the questions that it contains?", "Confirm deletion", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
                    if (resp == JOptionPane.YES_OPTION) {
                        IOManager.deleteLibrary(libBrowser.getSelectedLibraryID());
                        libBrowser.notifyOfLibraryDeletion();
                        libBrowser.updateLibraries();
                        questionList.removeAll();
                        LibraryManagerWindow.this.validate();
                    }
                    break;
                case LibraryBrowser.EDIT_LIBRARY_ACTION:
                    LibraryEditorDialog libEdit = new LibraryEditorDialog(LibraryManagerWindow.this, false, null);
                    libEdit.setLibraryNameText(libBrowser.getSelectedLibraryName());
                    try {
                        libEdit.setLibraryDescriptionText(IOManager.getLibraryFromID(libBrowser.getSelectedLibraryID()).getDescription());
                    } catch (IOException ex) {
                        JOptionPane.showMessageDialog(LibraryManagerWindow.this, "Failed to load library with ID: '" + libBrowser.getSelectedLibraryID() + "'.", "IO Exception", JOptionPane.ERROR_MESSAGE);
                        Logger.getLogger(LibraryManagerWindow.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    libEdit.setTitle("Edit Library");
                    libEdit.libraryName.setSelectionStart(0);
                    libEdit.libraryName.setSelectionEnd(libEdit.libraryName.getText().length());
                    libEdit.setVisible(true);
                    try {
                        IOManager.editLibrary(libBrowser.getSelectedLibraryID(), libEdit.getLibraryNameText(), libEdit.getLibraryDescriptionText(), libEdit.getClearStatistics());
                    } catch (IOException ex) {
                        JOptionPane.showMessageDialog(LibraryManagerWindow.this, "An IOException was thrown while attempting to make these changes to the library.", "Edit library", JOptionPane.ERROR_MESSAGE);
                        Logger.getLogger(LibraryManagerWindow.class.getName()).log(Level.SEVERE, "While trying to save library editor changes to file using IOManager.editLibrary().", e);
                    }
                    break;
                case LibraryBrowser.EDIT_GROUPS_ACTION:
                    new GroupEditorDialog(LibraryManagerWindow.this).setVisible(true);
                    String currentSelection = libBrowser.getSelectedGroupName();
                    libBrowser.updateGroups();
                    libBrowser.setSelectedGroup(currentSelection);
                    libBrowser.updateLibraries();
                    break;
                case LibraryBrowser.EXPORT_IMPORT_ACTION:
                    exportImportMenu.show(libBrowser.btnExport.getParent(), libBrowser.btnExport.getX(), libBrowser.btnExport.getY());
                    break;
                case LibraryBrowser.GROUP_SELECTION_CHANGED:
                    questionList.updateQuestionsWithContent();
                    if (libBrowser.getSelectedLibraryID() != null) {
                        try {
                            //not getPreviouslySelectedLibraryID as the library selection hasn't changed, just the group selection
                            saveLibrary(libBrowser.getSelectedLibraryID());
                        } catch (IOException ex) {
                            Logger.getLogger(LibraryManagerWindow.class.getName()).log(Level.SEVERE, "While trying to save library with ID: " + libBrowser.getSelectedLibraryID() + "\n"
                                    + "as a result of GROUP_SELECTION_CHANGED event (from LibraryBrowser).", ex);
                        }
                    }
                    questionList.removeAll();
                    libraryStats.clearStats();
                    questionStats.setQuestion(null);
                    questionStats.repaint();
                    questionList.repaint();
                    break;
                case LibraryBrowser.LIBRARY_SELECTION_CHANGED:
                    //try to save the previously selected library
                    try {
                        //no library was previously selected
                        questionList.updateQuestionsWithContent();
                        if (libBrowser.getPreviouslySelectedLibraryID() != null) {
                            saveLibrary(libBrowser.getPreviouslySelectedLibraryID());
                        }
                    } catch (IOException ex) {
                        JOptionPane.showMessageDialog(LibraryManagerWindow.this, "Could not save the library '" + IOManager.getLibraryName(libBrowser.getPreviouslySelectedLibraryID()) + "' to the temp directory.", "Error", JOptionPane.ERROR_MESSAGE);
                        Logger.getLogger(LibraryManagerWindow.class.getName()).log(Level.SEVERE, "While trying to save the library: " + libBrowser.getPreviouslySelectedLibraryID() + " to the temporary directory.", e);
                    }
                    //must load the selected library
                    if ((libBrowser.getSelectedLibraryIndex() == -1) || (libBrowser.getSelectedLibraryID().isEmpty())) {
                        //no library is now selected, so exit
                        return;
                    }
                    //remove everything from the question list before loading the new library
                    questionList.removeAll();
                    IQuestion[] ques = new IQuestion[0];
                    Library lib = null;
                    //try to load the library
                    try {
                        LibraryManagerWindow.this.setEnabled(false);
                        LibraryManagerWindow.this.setTitle("Please Wait -- Loading Library: " + libBrowser.getSelectedLibraryName());
                        lib = IOManager.getLibraryFromID(libBrowser.getSelectedLibraryID());
                        LibraryManagerWindow.this.setTitle("Library Manager");
                        LibraryManagerWindow.this.setEnabled(true);
                        ques = lib.getQuestions();
                    } catch (IOException ex) {
                        JOptionPane.showMessageDialog(LibraryManagerWindow.this, "Failed to load library with ID: '" + libBrowser.getSelectedLibraryID() + "'.", "IO Exception", JOptionPane.ERROR_MESSAGE);
                        Logger.getLogger(LibraryManagerWindow.class.getName()).log(Level.SEVERE, "while loading library id: " + libBrowser.getSelectedLibraryID() + " using IOManager.getLibraryFromID()", ex);
                    }
                    //construct the question list
                    for (int i = 0; i < ques.length; i++) {
                        questionList.addQuestion(ques[i]);
                    }
                    questionList.validate();

                    //set statistics
                    libraryStats.clearStats();
                    libraryStats.setLibrary(lib);
                    questionStats.setQuestion(null);
                    questionStats.repaint();
                    questionList.repaint();
                    break;
                default:
                    break;

            }
        }
    }

    private class ExportAction extends AbstractAction {

        public ExportAction() {
            super("Export libraries...");
        }

        public void actionPerformed(ActionEvent e) {
            ExportDialog d = new ExportDialog(LibraryManagerWindow.this);
            d.setVisible(true);
        }
    }

    private class ImportAction extends AbstractAction {

        public ImportAction() {
            super("Import libraries...");
        }

        public void actionPerformed(ActionEvent e) {
            //show the file chooser
            JFileChooser chooser = new JFileChooser();
            chooser.setDialogTitle("Import Libraries...");
            chooser.setMultiSelectionEnabled(true);
            chooser.setApproveButtonText("Import");
            if (chooser.showOpenDialog(LibraryManagerWindow.this) == JFileChooser.APPROVE_OPTION) {
                //ask if the libraries should be added to a group
                String addToGroup = (String) JOptionPane.showInputDialog(LibraryManagerWindow.this, "Select the group to which the libraries should be added.", "Add to group", JOptionPane.QUESTION_MESSAGE, null, IOManager.getGroupListWithNoneOption(), 0);
                //null means that the library should not be added to any group when it is imported
                if (addToGroup.equals("None")) {
                    addToGroup = null;
                }

                //get the array of selected files
                File[] selectedFiles = chooser.getSelectedFiles();
                String libraryID;
                //holds the names of libraries that could not be copied
                String couldNotCopy = "";
                //flag set when a library could not be copied
                boolean IOError = false;
                for (int i = 0; i < selectedFiles.length; i++) {
                    libraryID = selectedFiles[i].getName();
                    try {
                        //add the file to the IOManager variables
                        IOManager.importLibrary(selectedFiles[i], addToGroup);
                    } catch (IOException ex) {
                        IOError = true;
                        couldNotCopy += selectedFiles[i].getAbsolutePath() + "\n";
                    }
                    //if an error occurred while copying any files...
                    if (IOError) {
                        JOptionPane.showMessageDialog(LibraryManagerWindow.this, "An IOException occurred while copying one or more of the selected libraries.\nThe following libraries failed:\n\n"
                                + couldNotCopy + "\nThese libraries have not been imported. Ensure you have\n adequate read/write permissions.", "IO Exception While Copying", JOptionPane.ERROR_MESSAGE);
                    }
                    //rebuild the libraries list
                    LibraryManagerWindow.this.libBrowser.updateLibraries();
                }
            }
        }
    }

    private class FlexiQuestionTypeAction extends AbstractAction {

        public FlexiQuestionTypeAction() {
            super("Flexi-question type", new ImageIcon(LibraryManagerWindow.class.getResource("/resources/icons/flexi.png")));
        }

        public void actionPerformed(ActionEvent e) {
            FlexiQuestion question = new FlexiQuestion(libBrowser.getSelectedLibraryID(), "", "", "", false, 0, 0, 0);
            questionList.addQuestion(question);
            questionList.scrollerContent.validate();
            questionList.repaint();
            LibraryManagerWindow.this.validate();
        }
    }

    private class TableQuestionTypeAction extends AbstractAction {

        public TableQuestionTypeAction() {
            super("Flash card type",new ImageIcon(LibraryManagerWindow.class.getResource("/resources/icons/flashcard.png")));
        }

        public void actionPerformed(ActionEvent e) {
            TableQuestion question = new TableQuestion(libBrowser.getSelectedLibraryID(), "", "", new String[]{""}, new String[]{""}, false, TableQuestion.WRITTEN, ThemeConstants.tableCellEditorFont.getFontName(), ThemeConstants.tableCellEditorFont.getSize(), new int[]{0}, new int[]{0}, 1, new int[]{0});
            questionList.addQuestion(question);
            questionList.scrollerContent.validate();
            questionList.repaint();
            LibraryManagerWindow.this.validate();
        }
    }

    /**
     * Listens for answer field button presses and then inserts the answer fields into the question
     * containers if suitable. Will only insert a question container into an answer field rich text area.
     */
    private class AnswerFieldPaletteListener implements ActionListener {

        public void actionPerformed(ActionEvent e) {
            Component c = KeyboardFocusManager.getCurrentKeyboardFocusManager().getFocusOwner();
            if (c == null) {
                return;
            }
            if (c instanceof RichTextArea) {
                RichTextArea rta = (RichTextArea) c;
                FlexiQuestionContainer flexiQ;

                try {
                    flexiQ = (FlexiQuestionContainer) rta.getParent().getParent().getParent().getParent();
                    if (flexiQ == null) {
                        throw new NullPointerException();
                    }
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(LibraryManagerWindow.this, "Can only insert an answer field into a flexi question text area.\n"
                            + "Detected due to exception thrown when casting RichTextArea's ancestor to FlexiQuestionContainer.", "Cannot Insert Answer Field", JOptionPane.INFORMATION_MESSAGE);
                    return;
                }
                //try to instantiate the answer field, and then add it to the text field
                try {
                    IAnswerField ansField = (IAnswerField) IOManager.getAnswerFieldClass(e.getActionCommand()).newInstance();
                    if (ansField.isOnlyForAnswerArea() && (rta.equals(flexiQ.answerText) == false)) {
                        JOptionPane.showMessageDialog(LibraryManagerWindow.this, "The '" + ansField.getDisplayName() + "' answer field\n"
                                + "can only be inserted into the answer text area.", "Cannot Insert Answer Field", JOptionPane.INFORMATION_MESSAGE);
                        return;
                    }
                    //ensure that the rich text area currently in focus is one of the three flexi question container rich text areas (and not, for instance
                    //the rich text area of a self-graded question answer field).
                    if (rta.equals(flexiQ.answerText) || rta.equals(flexiQ.questionText) || rta.equals(flexiQ.postAnswerText)) {
                        ansField.readInXML((String) IOManager.getAnswerFieldsFile().getAnswerFieldDefaults().get(ansField.getClass().getName()));
                        ansField.setParentLibraryID(libBrowser.getSelectedLibraryID());
                        rta.insertComponent((JComponent) ansField);
                        ansField.setContext(true);
                    } else {
                        JOptionPane.showMessageDialog(LibraryManagerWindow.this, "Can only insert an answer field into a flexi question text area.", "Cannot Insert Answer Field", JOptionPane.INFORMATION_MESSAGE);
                        return;
                    }
                } catch (InstantiationException ex) {
                    JOptionPane.showMessageDialog(LibraryManagerWindow.this, "There was a problem instantiating the answer field.\nIt cannot be inserted.", "Instantiation Exception", JOptionPane.ERROR_MESSAGE);
                    return;
                } catch (IllegalAccessException ex) {
                    JOptionPane.showMessageDialog(LibraryManagerWindow.this, "There was a problem accessing the answer field.\nIt cannot be inserted.", "Illegal Access Exception", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            }
        }
    }

    private class SaveExitListener implements MouseListener {

        public void mouseClicked(MouseEvent e) {
            LibraryManagerWindow.this.windowClosing(null);
            LibraryManagerWindow.this.dispose();
        }

        public void mousePressed(MouseEvent e) {
        }

        public void mouseReleased(MouseEvent e) {
        }

        public void mouseEntered(MouseEvent e) {
        }

        public void mouseExited(MouseEvent e) {
        }

    }

    private class KeyboardFocusPropertyListener implements PropertyChangeListener {

        public void propertyChange(PropertyChangeEvent evt) {
            if (evt.getPropertyName().equals("focusOwner")) {
                if (questionList.isAncestorOf((Component) evt.getNewValue())) {
                    questionStats.setQuestion(questionList.getQuestionFromFocussedComponent((Component) evt.getNewValue()));
                }
            }
        }
    }

    /**
     * Listens to the question list toolbar. Takes care of button presses for adding/removing questions, select/deselect all, etc.
     */
    private class ListToolbarListener implements ActionListener {

        public void actionPerformed(ActionEvent e) {
            switch (e.getID()) {
                case QuestionListToolbar.NEW_QUESTION:
                    if (libBrowser.getSelectedLibraryIndex() == -1) {
                        questionTypeMenuError.show(questionToolbar, questionToolbar.btnNew.getX(), questionToolbar.btnNew.getY() + 16);
                    } else {
                        questionTypeMenu.show(questionToolbar, questionToolbar.btnNew.getX(), questionToolbar.btnNew.getY() + 16);
                    }
                    break;
                case QuestionListToolbar.DELETE_SELECTED:
                    ArrayList<AbstractQuestionContainer> containers = questionList.getSelectedContainers();
                    if (containers.size() == 0) {
                        JOptionPane.showMessageDialog(LibraryManagerWindow.this, "There are no questions selected.\n\nYou can select a question by clicking once\non the tab at the top left of the question.", "Cannot delete", JOptionPane.INFORMATION_MESSAGE);
                        return;
                    }
                    int resp = JOptionPane.showConfirmDialog(LibraryManagerWindow.this, "Are you sure you "
                            + "want to delete all of the currently selected questions?", "Confirm delete", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
                    if (resp == JOptionPane.YES_OPTION) {
                        for (int i = 0; i < containers.size(); i++) {
                            questionList.removeQuestion(containers.get(i));
                        }
                        questionList.scrollerContent.validate();
                        questionList.repaint();
                    }
                    break;
                case QuestionListToolbar.CUT:
                    questionList.cutQuestions(questionList.getSelectedContainers());
                    break;
                case QuestionListToolbar.COPY:
                    questionList.copyQuestions(questionList.getSelectedContainers());
                    break;
                case QuestionListToolbar.PASTE:
                    try {
                        questionList.pasteQuestions(libBrowser.getSelectedLibraryID());
                    } catch (DataConversionException ex) {
                        //ignore; this probably means the clipboard data was not valid question data
                    } catch (IOException ex) {
                        Logger.getLogger(LibraryManagerWindow.class.getName()).log(Level.SEVERE, "While pasting questions to the current library (questionList.pasteQuestions())", ex);
                    } catch (UnsupportedFlavorException ex) {
                        //ignore; invalid question data
                    }
                    break;
                case QuestionListToolbar.EXPAND_ALL:
                    questionList.maximiseAll();
                    break;
                case QuestionListToolbar.MINIMISE_ALL:
                    questionList.minimiseAll();
                    break;
                case QuestionListToolbar.SELECT_ALL:
                    questionList.selectAll();
                    break;
                case QuestionListToolbar.SELECT_NONE:
                    questionList.deselectAll();
                    break;
                case QuestionListToolbar.MOVE_UP:
                    questionList.moveQuestionsUp(questionList.getSelectedContainers());
                    break;
                case QuestionListToolbar.MOVE_DOWN:
                    questionList.moveQuestionsDown(questionList.getSelectedContainers());
                    break;
                case QuestionListToolbar.GROUP_SELECTION:
                    questionList.groupContainers(questionList.getSelectedContainers());
                    break;
                case QuestionListToolbar.SEARCH:
                    questionList.searchFor(questionToolbar.getSearchText());
                    break;
            }
        }
    }
}
