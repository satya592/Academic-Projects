/*
 * QuizHistoryEntry.java
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

/**
 * Describes a single quiz history entry, the results and libraries used.
 *
 * @author Thomas Everingham
 * @version 1.0
 */
public class QuizHistoryEntry {

    /**
     * The date the quiz was taken.
     */
    private String date;
    /**
     * The percentage acheived.
     */
    private int percentage;
    /**
     * The number of questions that were answered.
     */
    private int questionsAnswered;
    /**
     * The number of questions that were skipped.
     */
    private int questionsSkipped;
    /**
     * The score awarded for this quiz.
     */
    private int score;
    /**
     * The libraries used in this quiz.
     */
    private String libraries;

    /**
     * Creates a new instance of <code>QuizHistoryEntry</code>.
     * @param date the date the quiz was taken.
     * @param percentage the percentage awarded for the quiz.
     * @param questionsAnswered the total number of questions answered during the quiz.
     * @param questionsSkipped the number of questions skipped in the quiz.
     * @param score the total score awarded for this quiz.
     * @param libraries the libraries used in this quiz.
     */
    public QuizHistoryEntry(String date, int percentage, int questionsAnswered, int questionsSkipped, int score, String libraries) {
        this.date = date;
        this.percentage = percentage;
        this.questionsAnswered = questionsAnswered;
        this.questionsSkipped = questionsSkipped;
        this.score = score;
        this.libraries = libraries;
    }

    public String getDate() {
        return date;
    }

    public String getLibraries() {
        return libraries;
    }

    public int getPercentage() {
        return percentage;
    }

    public int getQuestionsAnswered() {
        return questionsAnswered;
    }

    public int getQuestionsSkipped() {
        return questionsSkipped;
    }

    public int getScore() {
        return score;
    }
}
