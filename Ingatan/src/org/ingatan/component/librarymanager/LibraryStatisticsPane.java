/*
 * LibraryStatisticsPane.java
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

import org.ingatan.ThemeConstants;
import org.ingatan.data.FlexiQuestion;
import org.ingatan.data.IQuestion;
import org.ingatan.data.Library;
import org.ingatan.data.TableQuestion;
import java.awt.Dimension;
import javax.swing.BorderFactory;
import javax.swing.JProgressBar;
import javax.swing.JTextPane;
import javax.swing.SwingConstants;

/**
 *
 * @author Thomas Everingham
 * @version 1.0
 */
public class LibraryStatisticsPane extends JTextPane {

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
    Library lib;

    /**
     * Creates a new LibraryStatisticsPane.
     */
    public LibraryStatisticsPane() {
        this.setOpaque(false);
        this.setBorder(BorderFactory.createMatteBorder(1,0,1,0,ThemeConstants.borderUnselected));
        this.setEditable(false);
    }

    /**
     * Creates a new LibraryStatisticsPane with the specified library.
     * @param lib the library from which statistics should be generated.
     */
    public LibraryStatisticsPane(Library lib) {
        this();
        setLibrary(lib);
    }

    /**
     * Sets the library from which the statistics should be generated.
     * @param lib the library from which the statistics should be generated.
     */
    public void setLibrary(Library lib) {
        this.lib = lib;
        this.buildStats();
    }

    /**
     * Clear statistics panel.
     */
    public void clearStats() {
        this.setText("");
    }

    /**
     * Build the statistics fromt he current library.
     */
    public void buildStats() {
        String build = "";
        build += "Description: " + lib.getDescription();

        build += "\n\nQuestion count: " + lib.getQuestionCount();

        int totalAsks = 0;
        int totalAwarded = 0;
        int totalAvailable = 0;
        IQuestion ques;
        for (int i = 0; i < lib.getQuestions().length; i++) {
            ques = lib.getQuestions()[i];
            if (ques.getQuestionType() == IQuestion.FLEXI_QUESTION) {
                totalAsks += ((FlexiQuestion) ques).getTimesAsked();
                totalAwarded += ((FlexiQuestion) ques).getMarksAwarded();
                totalAvailable += ((FlexiQuestion) ques).getMarksAvailable();
            } else if (ques.getQuestionType() == IQuestion.TABLE_QUESTION) {
                for (int j = 0; j < ((TableQuestion) ques).getTimesAsked().length; j++) {
                    totalAsks += ((TableQuestion) ques).getTimesAsked()[j];
                    totalAwarded += ((TableQuestion) ques).getMarksAwarded()[j];
                    totalAvailable += ((TableQuestion) ques).getMarksAvailable()[j];
                }
            }
        }

        build += "\nTotal asks: " + totalAsks;
        build += "\nMarks awarded / available: " + totalAwarded + "/" + totalAvailable;
        this.setText(build + "\n\n");
        JProgressBar progBar = new JProgressBar(SwingConstants.HORIZONTAL, 0, 100);
        this.insertComponent(progBar);
        progBar.setValue((int) (100 * ((float) totalAwarded/totalAvailable)));
        progBar.setPreferredSize(new Dimension(this.getWidth()-20,20));
        progBar.setStringPainted(true);
        
    }
}
