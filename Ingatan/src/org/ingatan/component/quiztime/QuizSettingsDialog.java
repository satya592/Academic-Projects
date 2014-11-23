/*
 * QuizSettingsDialog.java
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
package org.ingatan.component.quiztime;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.ingatan.ThemeConstants;
import org.ingatan.component.librarymanager.MultipleLibrarySelector;
import org.ingatan.io.QuizManager;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import org.ingatan.io.IOManager;

/**
 * This dialog is shown when the user selects the quiz option from the main menu.
 * It allows the user to set which libraries should be included for that particular
 * quiz.
 * 
 * @author Thomas Everingham
 * @version 1.0
 */
public class QuizSettingsDialog extends JDialog {

    /**
     * Content of the answer fields scroller.
     */
    protected MultipleLibrarySelector libSelector = new MultipleLibrarySelector();
    /**
     * Button for returning to the main menu.
     */
    protected JButton btnCancel = new JButton(new CancelAction());
    /**
     * Button for creating a new quiz manager, and begining the quiz
     */
    protected JButton btnBeginQuiz = new JButton(new BeginQuizAction());
    /**
     * Allows the user whether the questions should be randomised or asked as they appear in the MultipleLibrarySelector
     */
    protected JCheckBox chkRandom = new JCheckBox("Randomise question order");
    /**
     * Displays a description of the currently selected library.
     */
    private JLabel lblDescription = new JLabel("Library Description");
    /**
     * The quiz manager generated from the selected libraries
     */
    private QuizManager generatedManager = null;
    /**
     * Content pane for this dialog.
     */
    protected JPanel contentPane = new JPanel();

    public QuizSettingsDialog(JFrame owner) {
        super(owner);
        this.setModal(true);
        this.setContentPane(contentPane);
        this.setTitle("Quiz Setup");
        this.setIconImage(IOManager.windowIcon);
        setUpGUI();
        this.setSize(680, 370);
        libSelector.setSelectedGroup(IOManager.getPreviouslySelectedGroup());
        libSelector.addActionListener(new LibSelectActionListener());
        this.setLocationRelativeTo(null);
    }

    private void setUpGUI() {
        //create a heading
        JLabel heading = new JLabel("New Quiz");
        heading.setFont(new Font(contentPane.getFont().getFamily(), Font.PLAIN, 26));
        heading.setHorizontalAlignment(SwingConstants.LEFT);
        heading.setAlignmentX(LEFT_ALIGNMENT);
        heading.setForeground(new Color(70, 70, 70));

        lblDescription.setFont(new Font(contentPane.getFont().getFamily(), Font.PLAIN, 10));
        lblDescription.setForeground(new Color(70, 70, 70));
        lblDescription.setMinimumSize(new Dimension(200, 200));
        lblDescription.setMaximumSize(new Dimension(200, 200));
        lblDescription.setPreferredSize(new Dimension(200, 200));
        lblDescription.setVerticalTextPosition(SwingConstants.TOP);

        //create brief instructions
        JLabel lblInfo = new JLabel("<html>Select the libraries of questions to include. If you choose, questions will be asked "
                + "at random, with bias toward questions that have been answered incorrectly or that have never been asked.");
        lblInfo.setFont(ThemeConstants.niceFont);
        lblInfo.setHorizontalAlignment(SwingConstants.LEFT);
        lblInfo.setAlignmentX(LEFT_ALIGNMENT);
        lblInfo.setMaximumSize(new Dimension(600, 100));
        lblInfo.setForeground(new Color(70, 70, 70));

        contentPane.setLayout(new BoxLayout(contentPane, BoxLayout.Y_AXIS));
        contentPane.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        contentPane.add(heading);
        contentPane.add(Box.createVerticalStrut(3));
        contentPane.add(lblInfo);
        contentPane.add(Box.createVerticalStrut(10));
        libSelector.setAlignmentX(LEFT_ALIGNMENT);
        Box horiz = new Box(BoxLayout.X_AXIS);
        horiz.add(libSelector);
        horiz.add(Box.createHorizontalStrut(10));
        horiz.add(lblDescription);
        horiz.setAlignmentX(LEFT_ALIGNMENT);
        horiz.setMaximumSize(new Dimension(700, 400));
        contentPane.add(horiz);
        contentPane.add(Box.createVerticalStrut(5));
        chkRandom.setAlignmentX(LEFT_ALIGNMENT);
        contentPane.add(chkRandom);

        horiz = Box.createHorizontalBox();
        horiz.setAlignmentX(LEFT_ALIGNMENT);
        horiz.add(btnBeginQuiz);
        horiz.add(Box.createHorizontalStrut(3));
        horiz.add(btnCancel);

        contentPane.add(horiz);

        chkRandom.setSelected(true);

        this.validate();
        this.pack();
    }

    /**
     * Gets the QuizManager generated from the selected libraries.
     * @return the QuizManager generated from the selected libraries.
     */
    public QuizManager getGeneratedQuizManager() {
        return generatedManager;
    }

    /**
     * Begin button action
     */
    private class BeginQuizAction extends AbstractAction {

        public BeginQuizAction() {
            super("Begin Quiz");
        }

        public void actionPerformed(ActionEvent e) {
            if (libSelector.getSelectedLibraryIDs().length == 0) {
                JOptionPane.showMessageDialog(QuizSettingsDialog.this, "You must select at least one library.", "No Selection", JOptionPane.INFORMATION_MESSAGE);
                return;
            }
            generatedManager = new QuizManager(libSelector.getSelectedLibraryIDs(), chkRandom.isSelected());
            QuizSettingsDialog.this.setVisible(false);
        }
    }

    /**
     * Added to the multiple library selector to listen for library change events so
     * That the library description label can be updated.
     */
    private class LibSelectActionListener implements ActionListener {

        public void actionPerformed(ActionEvent e) {
            if (e.getID() != MultipleLibrarySelector.LIBRARY_SELECTION_CHANGED)
                return;
            
            String[] ids = libSelector.getHighlightedLibraryIDs();
            if (ids.length > 0) {
                try {
                    lblDescription.setText("<html><u>Library: " + IOManager.getLibraryName(ids[0]) + "</u><br><br>" + IOManager.getLibraryFromID(ids[0]).getDescription() + "<html>");
                } catch (IOException ex) {
                    Logger.getLogger(QuizSettingsDialog.class.getName()).log(Level.SEVERE, "Trying to display the description of the library with ID: " + ids[0] + " in quiz settings dialog.", ex);
                }
            } else {
                lblDescription.setText("No library selected.");
            }
        }
    }

    /**
     * Cancel action; dispose of the dialog
     */
    private class CancelAction extends AbstractAction {

        public CancelAction() {
            super("Cancel");
        }

        public void actionPerformed(ActionEvent e) {
            QuizSettingsDialog.this.dispose();
        }
    }
}
