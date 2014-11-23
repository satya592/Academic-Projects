/*
 * FlexiQuestion.java
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
 * This is an implementation of the IQuestion interface. The flexi-question type is
 * of the standard Ingatan 2.0 type. It has a text field for the question text,
 * a text field for the answer text (which includes answer fields) and a text field
 * for the post-answer text. This class encapsulates the text data and question meta-data.
 * 
 * @author Thomas Everingham
 * @version 1.0
 */
public class FlexiQuestion implements IQuestion {

    /**
     * Question field text. This is rich text in a HTML-style markup. The
     * <code>RichTextArea</code> class is able to produce this text based on its
     * content, and also parse this markup in to reproduce the styled text.
     */
    protected String questionText = "";
    /**
     * Answer field text. This is rich text in a HTML-style markup. The
     * <code>RichTextArea</code> class is able to produce this text based on its
     * content, and also parse this markup in to reproduce the styled text. Note that
     * the answer text field mainly holds answer-fields that the user has inserted. These
     * answer fields are components which have settings that the user can adjust. The answer field components
     * serialize themselves. The rich text field serialises them in the form<br>
     * [component tag component="name"] text the component generated here, parsed later by the component [/component tag]
     */
    protected String answerText = "";
    /**
     * Post-answer text. This is rich text that the user sees after submitting his or her answer during quiz mode.
     * This is rich text in a HTML-style markup. The
     * <code>RichTextArea</code> class is able to produce this text based on its
     * content, and also parse this markup in to reproduce the styled text.
     */
    protected String postAnswerText = "";
    /**
     * This sets whether or not the post-answer text field is to be used. If not then the text field
     * will be hidden from view. This is altered through the use of a checkbox.
     */
    protected boolean usePostAnswerText = false;
    /**
     * The sum of marks that have been awarded over all the times this question has been asked.
     * The overall correctness of this question can be calculated as marksAwarded/marksAvailable.
     */
    protected long marksAwarded = 0;
    /**
     * The sum of marks that have been available over all the times this question has been asked.
     * The overall correctness of this question can be calculated as marksAwarded/marksAvailable.
     * The reason this field is saved is that the user may wish to change the number of marks allocated
     * for a particular answer field, without affecting the correctness value acheived thus far.
     */
    protected long marksAvailable = 0;
    /**
     * The number of times this question has been asked during a quiz.
     */
    protected int timesAsked = 0;

    /**
     * The libraryID of the library that this question belongs to.
     */
    protected String parentLibrary;

    public FlexiQuestion(String parent, String questionText, String answerText, String postAnswerText,
            boolean usePostAnswerText, int marksAwarded, int marksAvailable, int timesAsked) {

        this.questionText = questionText;
        this.answerText = answerText;
        this.postAnswerText = postAnswerText;
        this.usePostAnswerText = usePostAnswerText;
        this.marksAwarded = marksAwarded;
        this.marksAvailable = marksAvailable;
        this.timesAsked = timesAsked;
        this.parentLibrary = parent;
    }

    /**
     * Gets the answer text for this question. The text may be formatted with
     * a basic markup that is parsed/interpretted by the <code>RichTextArea</code> class.
     * @return the answer text for this question.
     */
    public String getAnswerText() {
        return answerText;
    }

    /**
     * Sets the answer text for this question.
     * @param answerText the new answer text.
     */
    public void setAnswerText(String answerText) {
        this.answerText = answerText;
    }
    /**
     * Gets the number of marks that have been made available to the user over all
     * times this question has been asked. This is the maximum number of marks that
     * can have been awarded so far.
     * @return the number of marks that have been available to win during quiz time
     * over all times this question has been asked.
     */
    public long getMarksAvailable() {
        return marksAvailable;
    }

    /**
     * Returns the overall all-time correctness of this Flexi Question over all
     * the times it has been asked. Equivalent to <code>getMarksAwarded() / getMarksAvailable()</code>.
     * @return the ratio of marks that have been awarded to the marks that have been available.
     */
    public float getCorrectness() {
        //avoid divide by 0
        if (marksAvailable == 0)
            return 0;

        return (marksAwarded/marksAvailable);
    }

    /**
     * Sets the number of marks that have been made available to win during quiz time
     * over all times this question has been asked. This method is used to update the
     * value.
     * @param marksAvailable new number of marks that have been available.
     */
    public void setMarksAvailable(long marksAvailable) {
        this.marksAvailable = marksAvailable;
    }

    /**
     * Gets the total number of marks that have been awarded during quiz time to
     * answers of this question. The correctness of the question can be found as
     * <code>marksAwarded</code> divided by <code>marksAvailable</code>.
     * @return the total number of marks awarded for answers to this question.
     */
    public long getMarksAwarded() {
        return marksAwarded;
    }

    /**
     * Sets the number of marks that have been awarded to answers of this question
     * during quiz time. This method is used to update the value.
     * @param marksAwarded the new number of marks that have been awarded.
     */
    public void setMarksAwarded(long marksAwarded) {
        this.marksAwarded = marksAwarded;
    }

    /**
     * Gets the post-answer text for this question. The text may be formatted with
     * a basic markup that is parsed/interpretted by the <code>RichTextArea</code> class.
     * @return the post-answer text for this question.
     */
    public String getPostAnswerText() {
        return postAnswerText;
    }

    /**
     * Sets the post-answer text for this question.
     * @param postAnswerText the new post-answer text.
     */
    public void setPostAnswerText(String postAnswerText) {
        this.postAnswerText = postAnswerText;
    }

    /**
     * Gets the question text for this question. The text may be formatted with
     * a basic markup that is parsed/interpretted by the <code>RichTextArea</code> class.
     * @return the question text for this question.
     */
    public String getQuestionText() {
        return questionText;
    }
    /**
     * Sets the question text for this question.
     * @param questionText the new question text.
     */
    public void setQuestionText(String questionText) {
        this.questionText = questionText;
    }

    /**
     * Gets the number of times this question has been asked in a quiz.
     * @return the number of times this question has been asked in a quiz.
     */
    public int getTimesAsked() {
        return timesAsked;
    }

    /**
     * Sets the number of times that this question has been used in a quiz.
     * @param timesAsked the new number of times this question has been asked.
     */
    public void setTimesAsked(int timesAsked) {
        this.timesAsked = timesAsked;
    }

    /**
     * Checks whether or not post-answer text is to be used for this question.
     * Even if post-answer text is not being used, the post answer text may not
     * be empty, as it may have been used previously and not deleted.
     * @return true if post-answer text is being used.
     */
    public boolean isUsingPostAnswerText() {
        return usePostAnswerText;
    }

    /**
     * Sets whether or not post-answer text should be used for this question.
     * @param usePostAnswerText true if post-answer text should be used.
     */
    public void setUsePostAnswerText(boolean usePostAnswerText) {
        this.usePostAnswerText = usePostAnswerText;
    }

    /**
     * Gets the question type.
     * @return value corresponding to a field in that <code>IQuestion</code> interface.
     */
    public int getQuestionType() {
        return IQuestion.FLEXI_QUESTION;
    }

    /**
     * Gets the libraryID of the library that this question belongs to.
     * @return the libraryID of the library that this question belongs to.
     */
    public String getParentLibrary() {
        return parentLibrary;
    }

    /**
     * Gets a description of the question type.
     * @return a description of the question type.
     */
    public String getQuestionTypeName() {
        return "FlexiQuestion";
    }

    /**
     * Gets a single string representation of the content of this question
     * which can be used by external search methods.
     * @return a signle string representation of the content of this question.
     */
    public String getSearchableRepresentation() {
        return questionText + " ]![ " + answerText + " ]![ " + postAnswerText;
    }

    /**
     * Set the libraryID of the library that this question belongs to.
     * @param parent the libraryID of the library that this question belongs to.
     */
    public void setParentLibrary(String parent) {
        this.parentLibrary = parent;
    }
}
