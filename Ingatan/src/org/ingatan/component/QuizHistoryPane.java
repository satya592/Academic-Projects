/*
 * QuisHistoryPane.java
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
package org.ingatan.component;

import java.awt.Color;
import org.ingatan.ThemeConstants;
import org.ingatan.data.QuizHistoryEntry;
import org.ingatan.io.IOManager;
import org.ingatan.io.ParserWriter;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

/**
 * Panel displaying the recent quiz history. 
 * 
 * @author ThomasEveringham
 * @version 2.0
 */
public class QuizHistoryPane extends JPanel {

    /**
     * Content of the scroll pane, contains each record.
     */
    private JPanel scrollerContent = new JPanel();
    /**
     * Scroll pane for the records.
     */
    private JScrollPane scroller = new JScrollPane();
    /**
     * Holds the menu items (sidebar).
     */
    private PaintedJPanel menuPanel = new PaintedJPanel();

    /**
     * Creates a new <code>QuizHistoryWindow</code>.
     * @param returnToOnClose the window to return to once this window has closed.
     */
    public QuizHistoryPane() {
        this.setSize(new Dimension(500, 500));
        this.setLayout(new FlowLayout());
        scroller.setViewportView(scrollerContent);
        scroller.setAlignmentX(LEFT_ALIGNMENT);
        scroller.setPreferredSize(new Dimension(500,300));
        scroller.setBorder(BorderFactory.createMatteBorder(1, 0, 1, 0, ThemeConstants.borderUnselected));
        scrollerContent.setLayout(new BoxLayout(scrollerContent, BoxLayout.Y_AXIS));

        this.add(scroller);

        rebuild();

    }

    /**
     * Rebuild the record list based on the IOManager's file.
     */
    public void rebuild() {
        scrollerContent.removeAll();

        if (IOManager.getQuizHistoryFile().getEntries().size() == 0) {
            scrollerContent.add(new JLabel("<html><h3>No Quiz History Entries Exist</h3>"));
            this.validate();
        } else {

            ArrayList<QuizHistoryEntry> recordList = new ArrayList<QuizHistoryEntry>(IOManager.getQuizHistoryFile().getEntries());
            Collections.reverse(recordList);
            Iterator<QuizHistoryEntry> iterate = recordList.iterator();

            while (iterate.hasNext()) {
                scrollerContent.add(new QuizRecord(iterate.next()));
                scrollerContent.add(Box.createVerticalStrut(10));
            }
        }
        QuizHistoryPane.this.validate();
        scrollerContent.repaint();
    }

    /**
     * Single quiz result entry.
     */
    private class QuizRecord extends PaintedJPanel {

        /**
         * JLabel showing the data for this record.
         */
        private JLabel lblRecord = new JLabel();
        /**
         * Button that allows the user to remove this particular record.
         */
        private JButton btnDelete = new JButton(new QuizRecordDeleteAction());
        /**
         * The entry that this quiz record represents.
         */
        private QuizHistoryEntry record;

        /**
         * Creates a new QuizRecord.
         * @param record the QuizHistoryEntry to create this record from.
         */
        public QuizRecord(QuizHistoryEntry record) {
            this.record = record;
            this.setBorder(BorderFactory.createEmptyBorder(0, 8, 8, 8));
            this.setBorderWeigth(2.0f);

            btnDelete.setMargin(new Insets(1, 1, 1, 1));
            btnDelete.setFont(ThemeConstants.niceFont);
            btnDelete.setMaximumSize(new Dimension(20, 15));

            lblRecord.setFont(ThemeConstants.niceFont);

            this.setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
            this.add(lblRecord);
            this.add(btnDelete);

            //get the length of the libraries used in the quiz corresponding to this record.
            int librariesCharLength = record.getLibraries().length();
            //libs is the field we will edit for dispaly.
            String libs = record.getLibraries();
            //k is an offset that helps us place line breaks after commas if possible.
            int k = 0;
            //if the libraries record is too long (in characters), we must wrap it by inserting <br> tags.
            if (librariesCharLength > 45) {
                //from i = 0 to the number of 45 character runs in the string
                for (int i = 0; i < (((int) (librariesCharLength / 45)) - 1); i++) {
                    //k allows us to iterate forward from position 50(i+1) until we find a character that is a comma.
                    k = 0;
                    //while the character with offset k is not a comma, increment k, unless out of bounds.
                    while (libs.charAt(i * 50 + 50 + k) != ',') {
                        k++;
                        //if we go out of index bounds, break and leave k as 0.
                        if (i * (50 + i) + k >= libs.length()) {
                            k = 0;
                            break;
                        }
                    }
                    //put the line break after the comma
                    k++;
                    libs = libs.substring(0, 50 * (i + 1) + k) + "<br>" + libs.substring(50 * (i + 1) + k);
                }
            }

            String buildString = "<html><h4>" + libs + " - " + record.getPercentage() + "%</h4>";
            buildString += "Taken on " + record.getDate() + ": " + record.getQuestionsAnswered() + " answered, " + record.getQuestionsSkipped() + " skipped. "
                    + "Score awarded: " + record.getScore();
            lblRecord.setText(buildString);

            //set colour of the record to indicate score
            if (record.getPercentage() <= 40) {
                this.setBorderColour(new Color(225, 71, 71));
            } else if ((record.getPercentage() <= 75) && (record.getPercentage() > 40)) {
                this.setBorderColour(ThemeConstants.borderUnselected);//new Color(240, 230, 40));
            } else if ((record.getPercentage() > 75) && (record.getPercentage() <= 100)) {
                this.setBorderColour(new Color(153, 220, 37));
            }
        }

        /**
         * Action for the delete button.
         */
        private class QuizRecordDeleteAction extends AbstractAction {

            public QuizRecordDeleteAction() {
                super("X");
            }

            public void actionPerformed(ActionEvent e) {
                //this change will be saved to file when this window is closed.
                IOManager.getQuizHistoryFile().removeEntry(record);
                ParserWriter.writeQuizHistoryFile(IOManager.getQuizHistoryFile());
                rebuild();
            }
        }
    }
}
