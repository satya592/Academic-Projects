/*
 * TableQuestionUnit.java
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

import org.ingatan.data.IQuestion;
import org.ingatan.data.TableQuestion;
import java.util.Arrays;

/**
 * This class is used to represent a single entry in a TableQuestion. The class also
 * keeps a reference to the original table question, so that it has access to whether or 
 * no it is appropriate to ask the question in reverse, as well as the question templates.<br>
 * <br>
 * This class is used by the quiz manager to handle table questions so that they can be broken up
 * by correctness.
 *
 * @author Thomas Everingham
 * @version 1.0
 */
public class TableQuestionUnit implements IQuestion {
    /**
     * The question column text for this unit.
     */
    private String question = "";
    /**
     * The answer column text for this unit.
     */
    private String answer = "";
    /**
     * The parent <code>TableQuestion</code> instance, providing access to the
     * question templates and table question options.
     */
    private TableQuestion parentQuestion;
    /**
     * The correctness of this question (marksAwarded / marksAvailable)
     */
    int indexInTableQuestion = -1;

    /**
     * Creates a new <code>TableQuestionUnit</code> instance.
     * @param question the question column text for this unit.
     * @param answer the answer column text for this unit.
     * @param ques the <code>TableQuestion</code> instance that this unit belongs to.
     */
    public TableQuestionUnit(String question, String answer, TableQuestion ques, int indexInQuestion) {
        this.question = question;
        this.answer = answer;
        this.parentQuestion = ques;
        this.indexInTableQuestion = indexInQuestion;
    }

    /**
     * Gets the correctness value for this unit: MarksAwarded/MarksAvailable.
     * @return the correctness value for this unit.
     */
    public float getHistoricCorrectness() {
        return ((float) parentQuestion.getMarksAwarded()[indexInTableQuestion] / (float) parentQuestion.getMarksAvailable()[indexInTableQuestion]);
    }

    /**
     * This is a table question unit.
     * @return IQuestion.OTHER_QUESTION.
     */
    public int getQuestionType() {
        return OTHER_QUESTION;
    }

    /**
     * Gets the answer column data. Each entry in the array is a possible question,
     * or possible answer, depending on which way the question is being asked. This
     * array does not represent an entire column of data from the <code>TableQuestion</code>
     * but the possible answers/questions entered in a single cell of the <code>TableQuestion</code>.
     * @return the answer column data for this question.
     */
    public String[] getQuestionColumnData() {
        return question.split(",,");
    }

    /**
     * Gets the answer column data. Each entry in the array is a possible question,
     * or possible answer, depending on which way the question is being asked. This
     * array does not represent an entire column of data from the <code>TableQuestion</code>
     * but the possible answers/questions entered in a single cell of the <code>TableQuestion</code>.
     * @return the answer column data for this question.
     */
    public String[] getAnswerColumnData() {
        return answer.split(",,");
    }

    /**
     * Gets the number of times this question unit has been asked
     * @return the number of times this question unit has been asked.
     */
    public int getTimesAsked() {
        return parentQuestion.getTimesAsked()[indexInTableQuestion];
    }

    /**
     * Gets the index of this entry within the parent TableQuestion
     * @return the index of this entery within the parent TableQuestion
     */
    public int getIndexInTableQuestion() {
        return indexInTableQuestion;
    }

    public String getQuestionTypeName() {
        return "TableQuestionUnit";
    }

    public String getParentLibrary() {
        return parentQuestion.getParentLibrary();
    }

    public TableQuestion getParentTableQuestion() {
        return parentQuestion;
    }

    public void setParentLibrary(String parent) {
        this.parentQuestion.setParentLibrary(parent);
    }

    public String getSearchableRepresentation() {
        return (question + " : " + answer + " : " + parentQuestion.getQuestionTemplateFwd() + " : " + parentQuestion.getQuestionTemplateBwd());
    }
}
