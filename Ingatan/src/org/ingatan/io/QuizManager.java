/*
 * QuizManager.java
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

package org.ingatan.io;

import org.ingatan.component.quiztime.TableQuestionUnit;
import org.ingatan.data.FlexiQuestion;
import org.ingatan.data.IQuestion;
import org.ingatan.data.Library;
import org.ingatan.data.TableQuestion;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Prepares all questions from the selected libraries, and generates the order in
 * which questions should be asked based on the correctness ratings of those questions.
 * The priority scheme is: <ul>
 * <li>Questions that have less than 15% correctness or have never been asked</li>
 * <li>Questions that have between 15% and 35% correctness</li>
 * <li>Questions that have between 35% and 50% correctness</li>
 * <li>Questions that have between 50% and 70% correctness</li>
 * <li>Questions that have between 70% and 85% correctness</li>
 * <li>Questions that have 85% or more correctness</li>
 * </ul>
 * Not all of these categories will be full. In every 20 questions asked, 10 will be from the lowest
 * category, and 10 will be chosen randomly from any category.
 *
 * This ensures that well known questions (high correctness) are also asked occassionally, so that
 * the quiz is not entirely skewed toward the questions that the user potentially has no idea about.
 * 
 * @author Thomas Everingham
 * @version 1.0
 */
public class QuizManager {

    /**
     * Category 1: index of the vector of questions that have never been asked, or have
     * less than 15% correctness. Also the category that contains <i>all</i> questions
     * when the questions have not been randomised.
     */
    private static final int CAT_ONE = 0;
    /**
     * Category 2: index of the vector of questions having between 15% and 35% correctness.
     */
    private static final int CAT_TWO = 1;
    /**
     * Category 3: index of the vector of questions having between 35% and 50% correctness
     */
    private static final int CAT_THREE = 2;
    /**
     * Category 4: index of the vector of questions having between 50% and 70% correctness
     */
    private static final int CAT_FOUR = 3;
    /**
     * Category 5: index of the vector of questions having between 70% and 85% correctness
     */
    private static final int CAT_FIVE = 4;
    /**
     * Category 6: index of the vector of questions having more than 85% correctness
     */
    private static final int CAT_SIX = 5;
    /**
     * The number of categories that exist
     */
    private static final int CATEGORY_COUNT = 6;
    /**
     * Holds all questions from the libraries included in this quiz. If the questions have been randomised,
     * the questions are broken up into the following arrays:<ul>
     * <li>questionBucket.get(CAT_ONE), the array of questions that have never been asked, or have less than 15% correctness</li>
     * <li>questionBucket.get(CAT_TWO), the questions having between 15% and 35% correctness</li>
     * <li>questionBucket.get(CAT_THREE), the questions having between 35% and 50% correctness</li>
     * <li>questionBucket.get(CAT_FOUR), the questions having between 50% and 70% correctness</li>
     * <li>questionBucket.get(CAT_FIVE), the questions having between 70% and 85% correctness</li>
     * <li>questionBucket.get(CAT_SIX), the questions that have more than 85% correctness</li>
     * </ul>
     * If the questions have not been randomised, then they will all exist within category 1.
     */
    private ArrayList<ArrayList<IQuestion>> questionBucket = new ArrayList<ArrayList<IQuestion>>(6);
    /**
     * The libraries used by this quiz manager.
     */
    private ArrayList<Library> libraries = new ArrayList<Library>(0);
    /**
     * Whether or not the questions are randomised and asked based on correctness values, etc.
     */
    private boolean randomise = true;
    /**
     * Keeps track of where the QuizManager is up to on the biasScheme. It is incremented each time
     * a question is asked, and reset to zero each time it reaches biasScheme.length. The bias scheme
     * is the list of categories as questions should be asked from them. For example, questions should
     * be more frequently taken from category 1 than category 6.
     */
    private int schemeIndex = 0;
    /**
     * This provides the mechanism for bias within question choosing. Each time a question is
     * selected, it is taken from the category listed in this array at the current schemeIndex.
     * The schemeIndex is then incremeneted for next time until it reaches biasScheme.length, where
     * it is returned to zero. Currently, the scheme is 1,1,2,1,3,1,2,4,1,2,5,1,2,3,1,6,3,4,5,2,
     * which has been chosen as it contains 12 entries for 1 and 2, 3 for 3, and 5 for 4,5, and 6,
     * which should be a good distribution. Keep in mind that, upon a question access for a particular category, if that category is empty, then the next
     * category down (numerically) will be used, and if all of the lower categories are empty, higher up (numerically) categories will
     * be used.
     */
    private int[] biasScheme = new int[]{CAT_ONE, CAT_ONE, CAT_TWO, CAT_ONE, CAT_THREE, CAT_ONE, CAT_TWO, CAT_FOUR, CAT_ONE, CAT_TWO, CAT_FIVE, CAT_ONE, CAT_TWO, CAT_THREE, CAT_ONE, CAT_SIX, CAT_THREE, CAT_FOUR, CAT_FIVE, CAT_TWO};
    /**
     * Pretty record of which libraries were used by this quiz manager. This is used for printing later.
     */
    private String librariesUsed = "";

    /**
     * Creates a new quiz manager containing the specified libraries.
     * @param libraryIDs the libraries to use for this quiz.
     * @param randomise <code>true</code> if the questions should be asked randomly,
     * and <code>false</code> if they should be asked as they appear in the library ID array.
     */
    public QuizManager(String[] libraryIDs, boolean randomise) {

        this.randomise = randomise;

        //set up bucket
        for (int i = 0; i < CATEGORY_COUNT; i++) {
            questionBucket.add(new ArrayList<IQuestion>(0));
        }

        for (int i = 0; i < libraryIDs.length; i++) {
            //load and add library to the libraries array
            try {
                libraries.add(IOManager.loadLibrary(libraryIDs[i]));
                librariesUsed += IOManager.getLibraryName(libraryIDs[i]) + ((i < libraryIDs.length -1) ? ", " : "");
            } catch (IOException ex) {
                Logger.getLogger(QuizManager.class.getName()).log(Level.SEVERE, "Ocurred during quiz manager construction while trying to\n"
                        + "load each library from ID using IOManager", ex);
            }
            
        }

        //extract the questions from each library
        for (int i = 0; i < libraries.size(); i++) {
            IQuestion[] ques = libraries.get(i).getQuestions();

            for (int j = 0; j < ques.length; j++) {
                //if we need to randomise the questions, then sort them into the appropriate buckets now
                if (randomise) {
                    addQuestionToBucket(ques[j]);
                } //otherwise, just append them to the category 1 vector
                else {
                    if (ques[j] instanceof FlexiQuestion)
                        questionBucket.get(CAT_ONE).add(ques[j]);
                    else if (ques[j] instanceof TableQuestion) {
                        for (int k = 0; k < ((TableQuestion) ques[j]).getQuestionUnits().length; k++) {
                            questionBucket.get(CAT_ONE).add(((TableQuestion) ques[j]).getQuestionUnits()[k]);
                        }
                    }
                }
            }
        }

        //the questions are asked from the end of the arraylist backward, so reversing the
        //array list will mean the questions are asked in the order they were added
        if (!randomise) {
            Collections.reverse(questionBucket.get(CAT_ONE));
        }
        //otherwise, we want the questions to be random, so shake the bucket.
        else
        {
            shakeBucket();
        }
    }

    /**
     * Gets a pretty representation of the libraries used (names) by this quiz manager.
     * @return the libraries (names) used by this quiz manager.
     */
    public String getLibrariesUsed() {
        return librariesUsed;
    }

    /**
     * Randomise each vector in the question bucket.
     */
    public void shakeBucket() {
        //shuffle each category
        for (int i = 0; i < CATEGORY_COUNT; i++) {
            Collections.shuffle(questionBucket.get(i));
        }
    }

    /**
     * Adds a question to the question bucket. If the question is a <code>TableQuestion</code>
     * instance, then it is broken up into <code>TableQuestionUnits</code>.
     * @param question the question to add.
     */
    private void addQuestionToBucket(IQuestion question) {
        float correctness = 0;

        if (question instanceof FlexiQuestion) {
            //get the correctness value
            if ((((FlexiQuestion) question).getTimesAsked() == 0) || (((FlexiQuestion) question).getMarksAvailable() == 0)) {
                correctness = 0;
            } else {
                correctness = ((float) ((FlexiQuestion) question).getMarksAwarded() / (float) ((FlexiQuestion) question).getMarksAvailable());
            }

        } else if (question instanceof TableQuestion) {
            TableQuestionUnit[] units = ((TableQuestion) question).getQuestionUnits();
            //add each table question unit to a bucket
            for (int i = 0; i < units.length; i++) {
                addQuestionToBucket(units[i]);
            }
            return;
        } else if (question instanceof TableQuestionUnit) {
            if (((TableQuestionUnit) question).getTimesAsked() == 0) {
                correctness = 0;
            } else {
                correctness = ((TableQuestionUnit) question).getHistoricCorrectness();
            }
        }


        //add the question to the appropriate bucket
        if (correctness < 0.15) {
            questionBucket.get(CAT_ONE).add(question);
        } else if ((correctness >= 0.15) && (correctness < 0.35)) {
            questionBucket.get(CAT_TWO).add(question);
        } else if ((correctness >= 0.35) && (correctness < 0.50)) {
            questionBucket.get(CAT_THREE).add(question);
        } else if ((correctness >= 0.50) && (correctness < 0.70)) {
            questionBucket.get(CAT_FOUR).add(question);
        } else if ((correctness >= 0.70) && (correctness < 0.85)) {
            questionBucket.get(CAT_FIVE).add(question);
        } else if (correctness >= 0.85) {
            questionBucket.get(CAT_SIX).add(question);
        }
    }

    /**
     * Gets the current number of questions in the question bucket. This does not
     * include the questions that have been asked.
     * @return the current number of questions in the question bucket.
     */
    public int getCurrentQuestionCount() {
        return (questionBucket.get(CAT_ONE).size() + questionBucket.get(CAT_TWO).size() + questionBucket.get(CAT_THREE).size()
                + questionBucket.get(CAT_FOUR).size() + questionBucket.get(CAT_FIVE).size() + questionBucket.get(CAT_SIX).size());
    }

    /**
     * Checks whether or not there are more questions that can be asked.
     * @return <code>true</code> if there are more questions in the list.
     */
    public boolean hasMoreQuestions() {
        if (getCurrentQuestionCount() > 0) {
            return true;
        } else {
            return false;
        }
    }


    /**
     * Ask the ParserWriter to save this question by writing out its library to file. Does
     * not pack the library.
     * @param question the question to save.
     * @throws IOException if there is a problem loading the library.
     */
    public void saveQuestion(IQuestion question) throws IOException {
        ParserWriter.writeLibraryFile(IOManager.getLibraryFromID(question.getParentLibrary()));
    }

    /**
     * Gets the next question to ask. You should call <code>hasMoreQuestions</code>
     * first so as to avoid a null pointer exception.
     * @return the next question to be asked, or null if no question can be found.
     */
    public IQuestion getNextQuestion() {
        IQuestion retVal = getQuestionFrom(biasScheme[schemeIndex]);
        schemeIndex += 1;
        if (schemeIndex == biasScheme.length) {
            schemeIndex = 0;
        }

        return retVal;
    }

    /**
     * Gets a question (and removes it) from the specified category. If the specified category is empty,
     * then the next category <i>down</i> is used as the source of the question. For example, if category
     * 2 (CAT_TWO) is empty, then a question will be taken from category 1 (CAT_ONE). If that fails, then
     * a category above the specified question will be tried. If that fails, <code>null</code> is returned.
     * @param category the category to take a question from.
     * @return a question either from the specified category, or a category first below, and then above the
     * specified category. Returns <code>null</code> if no question can be found (you should call <code>hasMoreQuestions</code> first.
     */
    private IQuestion getQuestionFrom(int category) {
        //return null if there are no questions to return
        if (hasMoreQuestions() == false) {
            return null;
        }

        //ensure a question can be found
        while (questionBucket.get(category).size() == 0) {
            category = category - 1;
            //if we've tried all categories down to category 1, then start working up
            //to category 6.
            if (category < CAT_ONE) {
                category = CAT_ONE;
                while (questionBucket.get(category).size() == 0) {
                    category = category + 1;

                    //if we've tried all categories up to category six (the last one), then return null
                    if (category > CAT_SIX) {
                        return null;
                    }
                }
            }
        }

        //get the last question in the bucket, remove it from the vector, and
        IQuestion retVal = questionBucket.get(category).get(questionBucket.get(category).size()-1);
        questionBucket.get(category).remove(questionBucket.get(category).size() - 1);
        return retVal;
    }
}
