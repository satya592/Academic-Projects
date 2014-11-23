/*
 * QuestionStatisticsBar.java
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

import org.ingatan.data.FlexiQuestion;
import org.ingatan.data.IQuestion;
import org.ingatan.data.TableQuestion;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import javax.swing.JPanel;

/**
 * A thin statistics bar that provides an overview of the currently focussed question.
 * This bar is placed just below the QuestionList in the LibraryManagerWindow.
 * 
 * @author Thomas Everingham
 * @version 1.0
 */
public class QuestionStatisticsBar extends JPanel {
    /**
     * X position for drawing the string.
     */
    private static final int STRING_X_POS = 2;
    /**
     * Y position for drawing the string.
     */
    private static final int STRING_Y_POS = 10;

    /**
     * The question to generate statistics for.
     */
    IQuestion ques;

    /**
     * Creates a new QuestionStatisticsBar.
     */
    public QuestionStatisticsBar() {
        
    }

    /**
     * Creates a new QuestionStatisticsBar with the specified question.
     * @param question the question to display the statistics of.
     */
    public QuestionStatisticsBar(IQuestion question) {
        setQuestion(question);
    }

    /**
     * Sets the question from which to generate statistics.
     * @param question the question from which to generate statistics.
     */
    public void setQuestion(IQuestion question) {
        ques = question;
        this.repaint();
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        FlexiQuestion flex;
        TableQuestion tab;

        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        if (ques == null)
        {
            g2d.drawString(" - - no question selected - - ", STRING_X_POS, STRING_Y_POS);
            return;
        }
        if (ques.getQuestionType() == IQuestion.FLEXI_QUESTION) {
            flex = (FlexiQuestion) ques;

            if (flex.getTimesAsked() == 0)  {
                g2d.drawString("This question has never been asked.", STRING_X_POS, STRING_Y_POS);
                return;
            }

            if (flex.getMarksAvailable() == 0) {
                g2d.drawString("Asked " + flex.getTimesAsked() + " times, and no marks have been available.", STRING_X_POS, STRING_Y_POS);
                return;
            }
            String percentage = String.valueOf(100 * ( (double) flex.getMarksAwarded() / flex.getMarksAvailable()));
            g2d.drawString("Asked " + flex.getTimesAsked() + " times, with a total grade of " + flex.getMarksAwarded() + " / " + flex.getMarksAvailable() + " = " + percentage.substring(0,(percentage.length() > 4) ? 4 : percentage.length()) + "%", STRING_X_POS, STRING_Y_POS);
        }
        else if (ques.getQuestionType() == IQuestion.TABLE_QUESTION) {
            tab = (TableQuestion) ques;
            int totalAsks = 0;
            int totalMarksAvailable = 0;
            int totalMarksAwarded = 0;
            for (int i = 0; i < tab.getTimesAsked().length; i++) {
                totalAsks += tab.getTimesAsked()[i];
                totalMarksAvailable += tab.getMarksAvailable()[i];
                totalMarksAwarded += tab.getMarksAwarded()[i];
            }

            if (totalAsks == 0) {
                g2d.drawString(tab.getCol1Data().length + " entries. No questions have ever been asked from this table.", STRING_X_POS, STRING_Y_POS);
                return;
            }

            if (totalMarksAvailable == 0) {
                g2d.drawString(tab.getCol1Data().length + " entries. " + totalAsks + " questions asked from this table, but no marks have been available.", STRING_X_POS, STRING_Y_POS);
                return;
            }
            String percentage = String.valueOf(100*((double)totalMarksAwarded/totalMarksAvailable));
            g2d.drawString(tab.getCol1Data().length + " entries. " + totalAsks + " questions asked from this table, with total grade of " + totalMarksAwarded + " / " + totalMarksAvailable + " = " + percentage.substring(0,(percentage.length() > 4) ? 4 : percentage.length()) + "%", STRING_X_POS, STRING_Y_POS);
        }
    }
}
