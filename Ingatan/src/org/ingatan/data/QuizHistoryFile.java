/*
 * QuizHistoryFile.java
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

package org.ingatan.data;

import java.util.ArrayList;

/**
 * Quiz history including a record for each time a quiz has been entered, the result, and
 * how many questions were asked/skipped and also what libraries were included.
 *
 * @author ThomasEveringham
 * @version 1.0
 */
public class QuizHistoryFile {

    /**
     * All quiz entries that exist in the file.
     */
    private ArrayList<QuizHistoryEntry> historyEntries;
    /**
     * Total historic score.
     */
    private int score;

    /**
     * Creates a new QuizHistoryFile, containing the specified entries.
     * @param entries the quiz history entries to include in the file.
     */
    public QuizHistoryFile(ArrayList<QuizHistoryEntry> entries, int score) {
        historyEntries = entries;
        this.score = score;
    }

    /**
     * Get the total score accumlated over all quizes.
     * @return the total score accumulated over all quizes that have been undertaken.
     */
    public int getTotalScore() {
        return score;
    }

    /**
     * Adds the specified amount to the total running score.
     * @param increment the amount by which the total score should be increased.
     */
    public void addToTotalScore(int increment) {
        score += increment;
    }

    /**
     * Adds a new history entry.
     * @param date the date the quiz was taken.
     * @param percentage the percentage awarded for the quiz.
     * @param questionsAnswered the total number of questions answered during the quiz.
     * @param questionsSkipped the number of questions skipped in the quiz.
     * @param score the total score awarded for this quiz.
     * @param librariesUsed the libraries used in this quiz.
     */
    public void addNewEntry(String date, int percentage, int questionsAnswered, int questionsSkipped, int score, String librariesUsed) {
        historyEntries.add(new QuizHistoryEntry(date, percentage, questionsAnswered, questionsSkipped, score, librariesUsed));
    }

    /**
     * Remove the specified entry from the array list.
     * @param entry the entry to remove.
     */
    public void removeEntry(QuizHistoryEntry entry) {
        historyEntries.remove(entry);
    }

    /**
     * Gets all history entries contained by this file.
     * @return all history entried contained by this file.
     */
    public ArrayList<QuizHistoryEntry> getEntries() {
        return historyEntries;
    }

    
}
