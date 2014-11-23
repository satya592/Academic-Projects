/*
 * TableQuestionContainer.java
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

import org.ingatan.component.text.DataTable;
import org.ingatan.data.TableQuestion;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.SpinnerListModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;
import org.ingatan.ThemeConstants;
import org.ingatan.component.text.EmbeddedImage;
import org.ingatan.component.text.RichTextArea;

/**
 * This question container type is used for more flashcard style questions. It is good
 * for things like vocabulary training. It consists of a dynamic table with two columns.
 * The user may set whether the questions are asked in written form (where the answer is
 * typed in by the user), multiple choice form (where several randomly chosen options are
 * taken from the data, or random alternation between written and multiple choice.
 *
 * There is also an option for setting whether or not the table data is reversible; whether
 * questions can be asked back to front.
 * @author Thomas Everingham
 * @version 1.0
 */
public class TableQuestionContainer extends AbstractQuestionContainer {

    /**
     * The table used in this container.
     */
    private DataTable table = new DataTable();
    /**
     * The JScrollPane that holds the table.
     */
    private JScrollPane scroller = new JScrollPane(table);
    /**
     * The pane displaying standard options for the whole table question
     */
    private TableQuestionOptionPane optionPane = new TableQuestionOptionPane();
    /**
     * Holds the <code>TransparencyOptionPane</code> used.
     */
    private JPopupMenu settingsPopup = new JPopupMenu();
    /**
     * The question that this <code>TableQuestionContainer</code> holds.
     */
    private TableQuestion tblQuestion;
    /**
     * Button to show the help info box for table questions.
     */
    private JButton btnHelp = new JButton(new HelpButtonAction());
    /**
     * Button to show the options popup.
     */
    private JButton btnSettings = new JButton(new SettingsButtonAction());
    /**
     * Check box that allows the user to set whether the enter key moves the cell to the
     * right in the JTable, or down.
     */
    private JCheckBox checkEnterKeyMovesCellRight = new JCheckBox(new EnterKeyActionAssignmentAction());

    /**
     * Create a new <code>TableQuestionContainer</code> object.
     */
    public TableQuestionContainer(TableQuestion ques) {
        super(ques);
        tblQuestion = ques;
        contentPanel.setPreferredSize(null);
        //this sets the action for the 'enter key move right' checkbox of the option pane.
        //this checkbox allows the user to specify whether the enter key will shift the cell
        //right or down.
        checkEnterKeyMovesCellRight.setSelected(true);
        checkEnterKeyMovesCellRight.setFont(ThemeConstants.niceFont);
        checkEnterKeyMovesCellRight.setOpaque(false);
        optionPane.setFontsComboActionListener(new FontChangeListener());
        //set option panel data
        optionPane.getAskInReverse().setSelected(tblQuestion.isAskInReverse());
        SpinnerListModel askStyleModel = ((SpinnerListModel) optionPane.getAskStyle().getModel());
        askStyleModel.setValue(askStyleModel.getList().get(tblQuestion.getQuizMethod()));

        optionPane.getMarksPerAnswer().setText("" + tblQuestion.getMarksPerCorrectAnswer());
        optionPane.getFwdQuestionTemplate().setText(tblQuestion.getQuestionTemplateFwd());
        optionPane.getBwdQuestionTemplate().setText(tblQuestion.getQuestionTemplateBwd());

        SpinnerListModel fontModel = ((SpinnerListModel) optionPane.getFontSpinner().getModel());
        try {
            fontModel.setValue(ques.getFontFamilyName());

        } catch (IllegalArgumentException e) {
            fontModel.setValue(this.getFont().getFamily());
        }
        ((SpinnerNumberModel) optionPane.getFontSizeSpinner().getModel()).setValue(ques.getFontSize());

        btnHelp.setFont(ThemeConstants.niceFont);
        btnHelp.setMargin(new Insets(1, 1, 1, 1));
        btnHelp.setIcon(new ImageIcon(LibraryManagerWindow.class.getResource("/resources/icons/help.png")));

        btnSettings.setFont(ThemeConstants.niceFont);
        btnSettings.setMargin(new Insets(1, 1, 1, 1));
        btnSettings.setIcon(new ImageIcon(LibraryManagerWindow.class.getResource("/resources/icons/wrench.png")));

        //vertical box layout
        this.setLayoutOfContentPane(new BoxLayout(contentPanel, BoxLayout.PAGE_AXIS));
        this.contentPanel.setBorder(BorderFactory.createEmptyBorder(7, 20, 7, 20));
        this.addToContentPane(Box.createVerticalStrut(3), false);

        //add components
        this.addToContentPane(scroller, false);
        scroller.setAlignmentX(LEFT_ALIGNMENT);
        scroller.setOpaque(false);
        table.setOpaque(false);
        table.getTableHeader().setReorderingAllowed(false);
        table.getModel().addTableModelListener(new TableListener());

        table.setFont(new Font(ques.getFontFamilyName(), Font.PLAIN, ques.getFontSize()));

        this.addToContentPane(Box.createVerticalGlue(), false);
        settingsPopup.add(optionPane);
        Box horiz = Box.createHorizontalBox();
        horiz.setAlignmentX(LEFT_ALIGNMENT);
        horiz.add(btnSettings);
        horiz.add(Box.createHorizontalStrut(15));
        horiz.add(btnHelp);
        horiz.add(Box.createHorizontalStrut(15));
        horiz.add(checkEnterKeyMovesCellRight);
        this.addToContentPane(horiz, false);

        //set data
        String[] col1Data = tblQuestion.getCol1Data();
        String[] col2Data = tblQuestion.getCol2Data();
        String[][] newData = new String[col1Data.length][2];

        for (int i = 0; i < col1Data.length && i < col2Data.length; i++) {
            //there are only two columns in the TableQuestion table.
            newData[i][0] = col1Data[i];
            newData[i][1] = col2Data[i];
        }

        ((DefaultTableModel) table.getModel()).setDataVector(newData, new String[]{"Side 1", "Side 2"});
        table.registerSynchronisedData(ques.getTimesAskedArrayList());
        table.registerSynchronisedData(ques.getMarksAwardedArrayList());
        table.registerSynchronisedData(ques.getMarksAvailableArrayList());
    }

    /**
     * This method is called by the override of the content panel's paintComponent() method
     * in the AbstractQuestionContainer. This allows for general content panel painting to
     * be taken care of by the abstract container (i.e. borders and background), and any
     * extra painting work to be carried out here. For the TableQuestionContainer,
     * when the container is minimised, a preview of table content is drawn to the content
     * pane and the table is made invisible.
     * @param g2d Graphics2D object as specified by the content panel's paintComponent() method.
     */
    @Override
    protected void paintContentPanel(Graphics2D g2d) {
        //this override is called by the override of paintComponent() of the content panel in the 
        //AbstractQuestionContainer, so that extra
        if (minimised) {
            contentPanel.setBorder(BorderFactory.createEmptyBorder(50, 1, 1, 1));

            //construct a string containing the first 5 rows of data, if that much
            //data exists
            String strPrint = "";
            int rowsToPreview = 5;
            for (int i = 0; i < rowsToPreview && i < table.getRowCount(); i++) {
                for (int j = 0; j < table.getColumnCount(); j++) {
                    //don't want to preview empty cells
                    if (((String) table.getValueAt(i, j)).trim().equals("") == false) {
                        strPrint += table.getValueAt(i, j);
                        if ((i < table.getRowCount() - 1) || (j < table.getColumnCount() - 1)) {
                            strPrint += ", ";
                        }
                    }
                }
            }
            if (strPrint.equals("")) {
                strPrint = "empty table";
            }

            g2d.drawString(strPrint, 10, 20);
        } else {
            scroller.setVisible(true);
            contentPanel.setBorder(BorderFactory.createEmptyBorder(7, 20, 7, 20));
            this.revalidate();
        }
    }

    private class EnterKeyActionAssignmentAction extends AbstractAction {

        public EnterKeyActionAssignmentAction() {
            super("enter key moves cell right");
        }

        public void actionPerformed(ActionEvent e) {
            if (checkEnterKeyMovesCellRight.isSelected()) {
                table.enterMovesLeftToRight = true;
            } else {
                table.enterMovesLeftToRight = false;
            }
        }
    }

    /**
     * Get the column 1 data
     * @return the column 1 table data.
     */
    public String[] getColumn1Data() {
        Vector v = ((DefaultTableModel) table.getModel()).getDataVector();
        String[] column = new String[v.size()];
        for (int i = 0; i < column.length; i++) {
            column[i] = (String) ((Vector) v.get(i)).get(0);
        }

        return column;
    }

    /**
     * Get the column 2 data
     * @return the column 2 table data.
     */
    public String[] getColumn2Data() {
        Vector v = ((DefaultTableModel) table.getModel()).getDataVector();
        String[] column = new String[v.size()];
        for (int i = 0; i < column.length; i++) {
            column[i] = (String) ((Vector) v.get(i)).get(1);
        }

        return column;
    }

    /**
     * Gets the option pane, from which the options can be retrieved.
     * @return the option pane for this TableQuestionContainer.
     */
    public TableQuestionOptionPane getOptionPane() {
        return optionPane;
    }

    private class TableListener implements TableModelListener {

        public void tableChanged(TableModelEvent e) {
            table.setSize(table.getWidth(), table.getModel().getRowCount() * table.getRowHeight() + 20);
            scroller.setPreferredSize(new Dimension((table.getWidth() > 30) ? table.getWidth() : 100, (table.getHeight() >= 40) ? table.getHeight() : 40));

            //the following if-else structure allows the content panel to grow and shrink with the table,
            //but stops it from exceding the maximum height. If it exceeds the maximum height, it grows past
            //the boundary of the question container and pushes the containers below it down the question list
            //with empty space appearing between the two containers.
            if (contentPanel.getPreferredSize().height > 350) {
                contentPanel.setPreferredSize(new Dimension(contentPanel.getPreferredSize().width, 350));
            } else {
                contentPanel.setPreferredSize(null);
                if (contentPanel.getPreferredSize().height > 350) {
                    contentPanel.setPreferredSize(new Dimension(contentPanel.getPreferredSize().width, 350));
                }
            }

            //if this is not done, the container does not auto-resize. Todo: find out why and
            //change the following.
            TableQuestionContainer.this.minimise();
            TableQuestionContainer.this.maximise();
        }
    }

    /**
     * When the help button is pressed.
     */
    private class HelpButtonAction extends AbstractAction {

        public HelpButtonAction() {
            super("Help");
        }

        public void actionPerformed(ActionEvent e) {
            RichTextArea dispArea = new RichTextArea();

            dispArea.setPreferredSize(new Dimension(450, 415));
            dispArea.setSize(new Dimension(450, 415));
            dispArea.setMinimumSize(new Dimension(450, 415));

            dispArea.setBorder(BorderFactory.createEmptyBorder());
            dispArea.setEditable(false);
            dispArea.setOpaque(false);

            dispArea.setRichText("[aln]0[!aln][fam]Dialog[!fam][sze]16[!sze][col]51,51,51[!col]Flashcard Question Help[sze]12[!sze][br]"
                    + "Flashcard questions are great for vocabulary training, and they support custom fonts which means you can use kanji.[br][br]"
                    + "[u]Flashcard Entry[u][br]"
                    + "When typing, press [b]enter[b] to move to the next cell, and [b]backspace[b] to clear the "
                    + "current cell; the table will automatically grow and shrink.[br][br]"
                    + "You can separate possible answers on one side of a flashcard using double commas, as shown below."
                    + "[br][br][end]");

            BufferedImage img = null;
            try {
                img = ImageIO.read(Thread.currentThread().getContextClassLoader().getResource("resources/help/tques_help_double_comma.png"));
            } catch (Exception ex) {
                Logger.getLogger(LibraryManagerWindow.class.getName()).log(Level.SEVERE, "While trying to load /resources/help/tques_help_double_comma.png for the library manager's virgin load.", ex);
            }

            if (img != null) {
                EmbeddedImage eImg = new EmbeddedImage(img, "", "");
                eImg.setToolTipText("");
                dispArea.setCaretPosition(dispArea.getDocument().getLength());
                dispArea.insertComponent(eImg);
            }

            //dispArea.setCaretPosition(dispArea.getDocument().getLength());
            dispArea.insertRichText("[aln]1[!aln][br][br][aln]0[!aln][col]51,51,51[!col][fam]Dialog[!fam][u]Question Templates[u][br]"
                    + "Question templates (found in settings popup) insert the question term into a sentence at quiz time, where !osqb;q!csqb; is"
                    + "used as a placeholder. For example:[br]"
                    + "[aln]1[!aln][i]Translate !osqb;q!csqb; from English to Swedish.[i][br][fam]Dialog[!fam][aln]0[!aln][fam]Dialog[!fam]"
                    + "The reverse question template is the template used when the card is asked in reverse, for example:[br]"
                    + "[aln]1[!aln][i]Translate !osqb;q!csqb; from Swedish to English.[i][br][fam]Dialog[!fam][aln]0[!aln][fam]Dialog[!fam][end]");

            JOptionPane.showMessageDialog(TableQuestionContainer.this, dispArea, "Flashcard Question Help", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    /**
     * When the settings button is pressed.
     */
    private class SettingsButtonAction extends AbstractAction {

        public SettingsButtonAction() {
            super("Settings");
        }

        public void actionPerformed(ActionEvent e) {
            settingsPopup.show(btnSettings, 15, 18);
        }
    }

    private class FontChangeListener implements ActionListener {

        public void actionPerformed(ActionEvent e) {
            table.setFont(optionPane.getSelectedFont());
            table.repaint();
            table.mtce.getComponent().setFont(optionPane.getSelectedFont());
        }
    }
}
