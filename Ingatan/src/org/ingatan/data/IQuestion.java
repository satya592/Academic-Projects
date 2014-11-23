/*
 * IQuestion.java
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
 * Interface for Questions.
 *
 * @author Thomas Everingham
 * @version 1.0
 */
public interface IQuestion {
    /**
     * Indicates that this question is a flexi-question. A flexi-question is
     * of the standard Ingatan 2.0 type. It has a text field for the question text,
     * a text field for the answer text (which includes answer fields) and a text field
     * for the post-answer text.
     */
    public static final int FLEXI_QUESTION = 0;
    /**
     * Indicates that this question is a table-question. This question type is
     * ideal for vocabulary training, and that sort of thing. It is more a
     * flash-card style set-up. The user enter data into two columns of a table,
     * and can choose to have the question side and answer side reversible, as well
     * as how the questions should be asked, e.g. multiple choice or written.
     */
    public static final int TABLE_QUESTION = 1;
    /**
     * Indicates that this question is of a type developed later on. If the question
     * is of this type, then use the getQuestionTypeName method to retreive a string
     * description of this question.
     */
    public static final int OTHER_QUESTION = 2;

    /**
     * Gets the question type. Compare with one of the class fields: FLEXI_QUESTION,
     * TABLE_QUESTION, or OTHER_QUESTION.
     * @return the question type integer.
     */
    public int getQuestionType();

    /**
     * Gets a string descriptiong of this question type.
     * @return a string descriptiong of this question type.
     */
    public String getQuestionTypeName();

    /**
     * Get the library ID of the library that this question belongs to.
     * @return the libraryID of the library that this question belongs to.
     */
    public String getParentLibrary();

    /**
     * Set the libraryID of the library that this question belongs to.
     * @param parent the libraryID of the library that this question belongs to.
     */
    public void setParentLibrary(String parent);

    /**
     * Gets a single string representation of the content of this question
     * which can be used by external search methods.
     * @return a signle string representation of the content of this question.
     */
    public String getSearchableRepresentation();
}
