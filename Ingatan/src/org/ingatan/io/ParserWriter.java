/*
 * ParserWriter.java
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

import org.ingatan.component.text.RichTextArea;
import org.ingatan.data.AnswerFieldsFile;
import org.ingatan.data.FlexiQuestion;
import org.ingatan.data.Group;
import org.ingatan.data.IQuestion;
import org.ingatan.data.Library;
import org.ingatan.data.QuizHistoryEntry;
import org.ingatan.data.QuizHistoryFile;
import org.ingatan.data.TableQuestion;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.jdom.Attribute;
import org.jdom.DataConversionException;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.jdom.output.XMLOutputter;

/**
 * This class provides all required utilities to parse files and write them again. One
 * exception to this is that the IOManager reads the groups file itself upon initiation.<br>
 * <br>
 * This class only provides utilities for parsing/writing text files, and does not deal with
 * zipping or extracting library files, or images (these are dealt with by the IOManager).
 * 
 * @author Thomas Everingham
 * @version 1.0
 */
public abstract class ParserWriter {

    /**
     * Write the specified library file to the library file on disk that is specified within
     * that library file.
     * @param lib the library file to write to disk.
     */
    public static void writeLibraryFile(Library lib) {
        Document doc = new Document();

        //lib metadata
        Element e = new Element("library").setAttribute("fileVersion", "1.0").setAttribute("name", lib.getName()).setAttribute("id", lib.getId()).setAttribute("created", lib.getCreationDate().toString());
        e.addContent(new Element("libDesc").setText(lib.getDescription()));
        doc.addContent(e);



        //write question data - for each question in the library:
        for (int i = 0; i < lib.getQuestionCount(); i++) {
            e.addContent(questionToElement(lib.getQuestion(i)));
        }

        XMLOutputter fmt = new XMLOutputter();
        FileOutputStream f = null;
        try {
            f = new FileOutputStream(lib.getFileLibraryFile());
        } catch (FileNotFoundException ex) {
            Logger.getLogger(ParserWriter.class.getName()).log(Level.SEVERE, "while trying to create output stream for the library file in the temp\n"
                    + "directory corresponding to " + lib.getId() + ". File = " + lib.getFileLibraryFile().getAbsolutePath(), ex);
        }
        try {
            fmt.output(doc, f);
            f.close();
        } catch (IOException ex) {
            Logger.getLogger(ParserWriter.class.getName()).log(Level.SEVERE, "While trying to write to the already opened library file output stream (file=" + lib.getFileLibraryFile().getAbsolutePath() + ")\n"
                    + " Writing the xml document to the file.", ex);
        }
    }

    /**
     * Writes a QuizHistoryFile to the file specified by the IOManager in XML format.
     * @param history the file representation to write to disk.
     */
    public static void writeQuizHistoryFile(QuizHistoryFile history) {
        Document doc = new Document();

        //lib metadata
        Element e = new Element("QuizHistory").setAttribute("fileVersion", "1.0").setAttribute("totalScore", String.valueOf(history.getTotalScore()));
        doc.addContent(e);

        ArrayList<QuizHistoryEntry> entries = history.getEntries();
        Iterator<QuizHistoryEntry> iterate = entries.iterator();

        QuizHistoryEntry curEntry;
        Element el;
        while (iterate.hasNext()) {
            curEntry = iterate.next();
            el = new Element("entry");
            el.setAttribute("date", curEntry.getDate());
            el.setAttribute("libraries", curEntry.getLibraries());
            el.setAttribute("percentage", String.valueOf(curEntry.getPercentage()));
            el.setAttribute("qsAnswered", String.valueOf(curEntry.getQuestionsAnswered()));
            el.setAttribute("qsSkipped", String.valueOf(curEntry.getQuestionsSkipped()));
            el.setAttribute("score", String.valueOf(curEntry.getScore()));

            e.addContent(el);
        }

        XMLOutputter fmt = new XMLOutputter();
        FileOutputStream f = null;
        try {
            f = new FileOutputStream(IOManager.getQuizHistoryFilePath());
        } catch (FileNotFoundException ex) {
            Logger.getLogger(ParserWriter.class.getName()).log(Level.SEVERE, "Quiz history file couldn't be found.", ex);
        }
        try {
            fmt.output(doc, f);
            f.close();
        } catch (IOException ex) {
            Logger.getLogger(ParserWriter.class.getName()).log(Level.SEVERE, "Couldn't write out to the quiz history file.", ex);
        }
    }

    /**
     * Parse the XML data from the file specified by the IOManager as a QuizHistoryFile.
     * @return the resulting <code>QuizHistoryFile</code> object.
     */
    public static QuizHistoryFile parseQuizHistoryFile() {
        SAXBuilder sax = new SAXBuilder();
        Document doc = null;
        try {
            doc = sax.build(new File(IOManager.getQuizHistoryFilePath()));
        } catch (JDOMException ex) {
            Logger.getLogger(ParserWriter.class.getName()).log(Level.SEVERE, "While attempting to create a JDOM document from a file.", ex);
        } catch (IOException ex) {
            Logger.getLogger(ParserWriter.class.getName()).log(Level.SEVERE, "While attempting to create a JDOM document from a file.", ex);
        }

        if (doc == null) {
            return null;
        }

        int totalScore = 0;

        ArrayList<QuizHistoryEntry> historyEntries = new ArrayList<QuizHistoryEntry>();
        Iterator<Element> entries = doc.getRootElement().getChildren("entry").iterator();
        Element curEl;
        String date = "??";
        String libraries = "??";
        int percentage = 0;
        int qsAnswered = 0;
        int qsSkipped = 0;
        int score = 0;
        while (entries.hasNext()) {
            curEl = entries.next();

            try {
                totalScore = doc.getRootElement().getAttribute("totalScore").getIntValue();
                date = curEl.getAttributeValue("date");
                libraries = curEl.getAttributeValue("libraries");
                percentage = curEl.getAttribute("percentage").getIntValue();
                qsAnswered = curEl.getAttribute("qsAnswered").getIntValue();
                qsSkipped = curEl.getAttribute("qsSkipped").getIntValue();
                score = curEl.getAttribute("score").getIntValue();
            } catch (DataConversionException ex) {
                Logger.getLogger(ParserWriter.class.getName()).log(Level.SEVERE, "While parsing the QuizHistoryFile.", ex);
            }

            historyEntries.add(new QuizHistoryEntry(date, percentage, qsAnswered, qsSkipped, score, libraries));
        }

        return new QuizHistoryFile(historyEntries, totalScore);

    }

    /**
     * Write the specified library file to string representation, as it is stored in the file
     * on the hard disk.
     * @param lib the library file to write to string representation.
     * @return the library file serialised to string representation, as it is saved to file on the hard disk.
     */
    public static String writeLibraryToString(Library lib) {
        Document doc = new Document();

        //lib metadata
        Element e = new Element("library").setAttribute("encodeVersion", "1.0").setAttribute("name", lib.getName()).setAttribute("id", lib.getId()).setAttribute("created", lib.getCreationDate().toString());
        e.addContent(new Element("libDesc").setText(lib.getDescription()));
        doc.addContent(e);



        //write question data - for each question in the library:
        for (int i = 0; i < lib.getQuestionCount(); i++) {
            e.addContent(questionToElement(lib.getQuestion(i)));
        }

        XMLOutputter fmt = new XMLOutputter();
        return fmt.outputString(doc);
    }

    /**
     * Read a library's XML file in as a Library object. Does not extract a bundled library file.
     * @param libraryFile the library XML file to read.
     * @return the resulting <code>Library</code> object.
     */
    public static Library parseLibraryFile(File libraryFile) throws DataConversionException {
        libraryFile.getPath();

        SAXBuilder sax = new SAXBuilder();
        Document doc = null;
        try {
            doc = sax.build(libraryFile);
        } catch (JDOMException ex) {
            Logger.getLogger(ParserWriter.class.getName()).log(Level.SEVERE, "While trying to build xml document from library file", ex);
        } catch (IOException ex) {
            Logger.getLogger(ParserWriter.class.getName()).log(Level.SEVERE, "While trying to build xml document from library file", ex);
        }

        if (doc == null) {
            return null;
        }

        String libraryName = doc.getRootElement().getAttributeValue("name");
        String libraryID = doc.getRootElement().getAttributeValue("id");
        String libraryDescription = doc.getRootElement().getChildText("libDesc");
        Date creationDate = new Date(doc.getRootElement().getAttributeValue("created"));
        ListIterator questionList = doc.getRootElement().getChildren("question").listIterator();

        //create list of questions
        IQuestion[] questions = new IQuestion[0];
        IQuestion[] temp;
        while (questionList.hasNext()) {
            temp = new IQuestion[questions.length + 1];
            System.arraycopy(questions, 0, temp, 0, questions.length);
            temp[questions.length] = questionFromElement((Element) questionList.next(), libraryID);
            questions = temp;
        }

        //create id to file hashtable
        Hashtable images = new Hashtable<String, File>();
        FlexiQuestion ques;
        for (int i = 0; i < questions.length; i++) {
            //only care about flexi-questions, as table questions do not contain
            //images
            if (questions[i] instanceof FlexiQuestion) {
                ques = (FlexiQuestion) questions[i];
                Pattern p = Pattern.compile("\\[" + RichTextArea.TAG_IMAGE + "\\](.*?)\\[!" + RichTextArea.TAG_IMAGE + "\\]");
                Matcher m = p.matcher(ques.getQuestionText());
                while (m.find()) {
                    images.put(m.group(1).split("<;>")[0], new File(libraryFile.getParent() + "/" + m.group(1).split("<;>")[0]));
                }
                m = p.matcher(ques.getAnswerText());
                while (m.find()) {
                    images.put(m.group(1).split("<;>")[0], new File(libraryFile.getParent() + "/" + m.group(1).split("<;>")[0]));
                }
                m = p.matcher(ques.getPostAnswerText());
                while (m.find()) {
                    images.put(m.group(1).split("<;>")[0], new File(libraryFile.getParent() + "/" + m.group(1).split("<;>")[0]));
                }

            }
        }
        return new Library(libraryName, libraryID, libraryDescription, creationDate, questions, new File(libraryFile.getParent()), libraryFile, images);
    }

    /**
     * Parse the answer field file, accumulating class names and their default values.
     * @param answerFieldFile the file to parse.
     * @return the new <code>AnswerFieldsFile</code>
     */
    public static AnswerFieldsFile parseAnswerFieldFile(File answerFieldFile) {
        //Build XML from file
        SAXBuilder sax = new SAXBuilder();
        Document doc = null;
        try {
            doc = sax.build(answerFieldFile);
        } catch (JDOMException ex) {
            Logger.getLogger(ParserWriter.class.getName()).log(Level.SEVERE, "While trying to build answer fields xml from file", ex);
        } catch (IOException ex) {
            Logger.getLogger(ParserWriter.class.getName()).log(Level.SEVERE, "While trying to build answer fields xml from file", ex);
        }

        if (doc == null) {
            return null;
        }

        //create the list iterator and hash tables
        ListIterator ansFieldIterator = doc.getRootElement().getChildren("answerField").listIterator();
        Hashtable defaultsTable = new Hashtable<String, String>();
        Hashtable classTable = new Hashtable<String, Class>();


        //it is okay if the hard coded answer field added by the IOManager in below method
        //also occur in the answer field file - overwrite will occur and this allows defaults to be set.
        IOManager.addDefaultAnswerFieldsToTables(classTable, defaultsTable);


        //READ IN THE ANSWER FIELDS FILE vv

        Element currentElement;
        String classID;
        Class curClass = null;

        while (ansFieldIterator.hasNext()) {
            currentElement = (Element) ansFieldIterator.next();
            classID = currentElement.getAttribute("classID").getValue();
            try {
                curClass = IOManager.getUrlClassLoader().loadClass(classID);
                classTable.put(classID, curClass);
                defaultsTable.put(classID, currentElement.getText());
            } catch (ClassNotFoundException ignore) {
                //if the class is not found, ignore the fact. When the palette file is resaved,
                //the class name will not be rewritten.
                Logger.getLogger(ParserWriter.class.getName()).log(Level.INFO, "Could not find one of the answer field classes while reading the answer field file.\n"
                        + "This may mean that the class has been deleted externally from Ingatan. The class\n"
                        + "will be removed from the answer field file.", ignore);
            }
        }

        return new AnswerFieldsFile(classTable, defaultsTable);

    }

    /**
     * Writes the specified <code>AnswerFieldsFile</code> to IOManager.ANSWER_FIELD_FILE.
     * @param ansFields the <code>AnswerFieldsFile</code> to take the data from.
     */
    public static void writeAnswerFieldFile(AnswerFieldsFile ansFields) {
        Document doc = new Document();

        //set up root element
        Element e = new Element("AnswerFields");
        doc.setRootElement(e);
        e.setAttribute("fileVersion", "1.0");

        //get the data from the answer field file class
        Hashtable defaultsTable = ansFields.getAnswerFieldDefaults();
        Hashtable classTable = ansFields.getAnswerFields();
        Enumeration classEnum = ansFields.getAnswerFields().keys();

        //traverse the class hashtable and create an element for each answer field
        //and insert the corresponding default value text, if any exists.
        String currentKey;
        while (classEnum.hasMoreElements()) {
            currentKey = (String) classEnum.nextElement();
            e.addContent(new Element("answerField").setAttribute("classID", currentKey).setText((String) defaultsTable.get(currentKey)));
        }

        //write file
        XMLOutputter fmt = new XMLOutputter();
        FileOutputStream f = null;
        try {
            f = new FileOutputStream(IOManager.getAnswerFieldFile());
        } catch (FileNotFoundException ex) {
            Logger.getLogger(ParserWriter.class.getName()).log(Level.SEVERE, "While trying to create an output stream for the answer field file\n"
                    + "file = " + IOManager.getAnswerFieldFile(), ex);
        }
        try {
            fmt.output(doc, f);
            f.close();
        } catch (IOException ex) {
            Logger.getLogger(ParserWriter.class.getName()).log(Level.SEVERE, "While trying to write xml document to the answer field file output stream.\n"
                    + "file = " + IOManager.getAnswerFieldFile(), ex);
        }

    }

    /**
     * Writes out the given question as an XML document
     * @param questionIn the question to write.
     * @return the element containing all data required to reconstruct this question.
     */
    public static Element questionToElement(IQuestion questionIn) {
        Element question;
        Element metaData;
        Element questionData;


        //question header, what type of question?
        question = new Element("question");
        question.setAttribute("type", "" + questionIn.getQuestionType());

        //TABLE QUESTIONS AND FLEXIQUESTIONS MUST BE WRITTEN INDIVIDUALLY
        if (questionIn.getQuestionType() == IQuestion.FLEXI_QUESTION) {
            //Flexi question here
            FlexiQuestion ques = (FlexiQuestion) questionIn;
            metaData = new Element("metaData");
            metaData.setAttribute("version", "1.0");
            metaData.setAttribute("timesAsked", "" + ques.getTimesAsked());
            metaData.setAttribute("marksAwarded", "" + ques.getMarksAwarded());
            metaData.setAttribute("marksAvailable", "" + ques.getMarksAvailable());
            metaData.setAttribute("usePostAnswer", "" + ques.isUsingPostAnswerText());
            question.addContent(metaData);
            questionData = new Element("quesData");
            questionData.addContent(new Element("quesText").setText(ques.getQuestionText()));
            questionData.addContent(new Element("ansText").setText(ques.getAnswerText()));
            questionData.addContent(new Element("postAnsText").setText(ques.getPostAnswerText()));
            question.addContent(questionData);
        } else if (questionIn.getQuestionType() == IQuestion.TABLE_QUESTION) {
            //Table question here
            TableQuestion ques = (TableQuestion) questionIn;

            metaData = new Element("metaData");

            metaData.setAttribute("version", "1.0");
            metaData.setAttribute("marksPerAns", "" + ques.getMarksPerCorrectAnswer());
            metaData.setAttribute("quizMethod", "" + ques.getQuizMethod());
            metaData.setAttribute("askInReverse", "" + ques.isAskInReverse());
            metaData.setAttribute("font", ques.getFontFamilyName());
            metaData.setAttribute("quizFontSize", String.valueOf(ques.getFontSize()));

            //marks awarded
            String strTemp = "";
            int[] temp = ques.getMarksAwarded();
            for (int j = 0; j < temp.length - 1; j++) {
                strTemp += temp[j] + ",";
            }
            strTemp += temp[temp.length - 1];
            metaData.addContent(new Element("marksAwarded").setText(strTemp));

            //marks available
            strTemp = "";
            temp = ques.getMarksAvailable();
            for (int j = 0; j < temp.length - 1; j++) {
                strTemp += temp[j] + ",";
            }
            strTemp += temp[temp.length - 1];
            metaData.addContent(new Element("marksAvailable").setText(strTemp));

            //times asked
            strTemp = "";
            temp = ques.getTimesAsked();
            for (int j = 0; j < temp.length - 1; j++) {
                strTemp += temp[j] + ",";
            }
            strTemp += temp[temp.length - 1];
            metaData.addContent(new Element("timesAsked").setText(strTemp));

            question.addContent(metaData);

            //question data
            questionData = new Element("quesData");
            questionData.addContent(new Element("quesTemplateFwd").setText(ques.getQuestionTemplateFwd()));
            questionData.addContent(new Element("quesTemplateBwd").setText(ques.getQuestionTemplateBwd()));

            //column 1 data
            strTemp = "";
            String[] strArrayTemp = ques.getCol1Data();
            for (int j = 0; j < strArrayTemp.length - 1; j++) {
                strTemp += strArrayTemp[j] + "<;>";
            }
            strTemp += strArrayTemp[strArrayTemp.length - 1];
            questionData.addContent(new Element("quesColumnData").setText(strTemp));

            //column 2 data
            strTemp = "";
            strArrayTemp = ques.getCol2Data();
            for (int j = 0; j < strArrayTemp.length - 1; j++) {
                strTemp += strArrayTemp[j] + "<;>";
            }
            strTemp += strArrayTemp[strArrayTemp.length - 1];
            questionData.addContent(new Element("ansColumnData").setText(strTemp));

            question.addContent(questionData);
        } else if (questionIn.getQuestionType() == IQuestion.OTHER_QUESTION) {
            /* Other question type
             * this section may never be needed
             */
        }

        return question;
    }

    /**
     * Writes out the given question as an XML document
     * @param libParent the parent library of the question.
     * @param e the element that encapsulates the question information
     * @return the element containing all data required to reconstruct this question.
     */
    public static IQuestion questionFromElement(Element e, String libParent) throws DataConversionException {
        int questionType = e.getAttribute("type").getIntValue();

        //TABLE QUESTIONS AND FLEXIQUESTIONS MUST BE WRITTEN INDIVIDUALLY
        if (questionType == IQuestion.FLEXI_QUESTION) {
            //Flexi question here

            int timesAsked = e.getChild("metaData").getAttribute("timesAsked").getIntValue();
            int marksAwarded = e.getChild("metaData").getAttribute("marksAwarded").getIntValue();
            int marksAvailable = e.getChild("metaData").getAttribute("marksAvailable").getIntValue();
            boolean usePostAnswer = e.getChild("metaData").getAttribute("usePostAnswer").getBooleanValue();

            String quesText = e.getChild("quesData").getChildText("quesText");
            String ansText = e.getChild("quesData").getChildText("ansText");
            String postAnsText = e.getChild("quesData").getChildText("postAnsText");


            return new FlexiQuestion(libParent, quesText, ansText, postAnsText, usePostAnswer, marksAwarded, marksAvailable, timesAsked);
        } else if (questionType == IQuestion.TABLE_QUESTION) {
            //Table question here

            int marksPerAns = e.getChild("metaData").getAttribute("marksPerAns").getIntValue();
            int quizMethod = e.getChild("metaData").getAttribute("quizMethod").getIntValue();
            boolean askInReverse = e.getChild("metaData").getAttribute("askInReverse").getBooleanValue();
            String fontName = e.getChild("metaData").getAttributeValue("font");
            int fontSize = e.getChild("metaData").getAttribute("quizFontSize").getIntValue();
            String tempMarksAwarded = e.getChild("metaData").getChild("marksAwarded").getText();
            String tempMarksAvailable = e.getChild("metaData").getChild("marksAvailable").getText();
            String tempTimesAsked = e.getChild("metaData").getChild("timesAsked").getText();
            String quesTemplateFwd = e.getChild("quesData").getChild("quesTemplateFwd").getText();
            String quesTemplateBwd = e.getChild("quesData").getChild("quesTemplateBwd").getText();
            String[] quesColumnData = e.getChild("quesData").getChild("quesColumnData").getText().split("<;>");
            String[] ansColumnData = e.getChild("quesData").getChild("ansColumnData").getText().split("<;>");

            String[] strTimesAsked = tempTimesAsked.split(",");
            int[] timesAsked = new int[strTimesAsked.length];
            String[] strMarksAwarded = tempMarksAwarded.split(",");
            int[] marksAwarded = new int[strMarksAwarded.length];
            String[] strMarksAvailable = tempMarksAvailable.split(",");
            int[] marksAvailable = new int[strMarksAvailable.length];


            for (int i = 0; i < strTimesAsked.length && i < strMarksAwarded.length && i < strMarksAvailable.length; i++) {
                timesAsked[i] = Integer.valueOf(strTimesAsked[i]);
                marksAwarded[i] = Integer.valueOf(strMarksAwarded[i]);
                marksAvailable[i] = Integer.valueOf(strMarksAvailable[i]);
            }

            return new TableQuestion(libParent, quesTemplateFwd, quesTemplateBwd, quesColumnData, ansColumnData, askInReverse, quizMethod, fontName, fontSize, marksAwarded, marksAvailable, marksPerAns, timesAsked);


        } else if (questionType == IQuestion.OTHER_QUESTION) {
            /* Other question type
             * this is just in case question types are ever supported as plugins,
             * so just leave this section blank for now.
             */
        }

        return null;
    }

    /**
     * Gets a text representation of this question in string form. The question can
     * be reconstructed from this string later.
     * @param questionIn the question to get a string representation for.
     * @return the string representation of <code>questionIn</code>.
     */
    public static String questionToString(IQuestion questionIn) {
        return questionToElement(questionIn).getText();
    }

    /**
     * Write the groups array specified to the groups file.
     * @param groups the groups to write to the groups file.
     */
    public static void writeGroupsFile(Group[] groups) {
        Document doc = new Document();

        //get a list of all libraries
        String temp = "";
        String[] libIDs = IOManager.getLibraryIDs();
        String[] libNames = IOManager.getLibraryNames();

        for (int i = 0; i < libIDs.length; i++) {
            temp += libIDs[i] + "<;>";
        }

        doc.setRootElement(new Element("GroupFile").setAttribute("fileVersion", "1.0"));
        doc.getRootElement().addContent(new Element("allLibIDs").setText(temp));

        temp = "";
        for (int i = 0; i < libNames.length; i++) {
            temp += libNames[i] + "<;>";
        }
        doc.getRootElement().addContent(new Element("allLibNames").setText(temp));

        Element e = new Element("groups");

        for (int i = 0; i < groups.length; i++) {

            //create a single string list of library ids for libs in this group
            temp = "";
            libIDs = groups[i].getLibraryIDs();
            for (int j = 0; j < libIDs.length; j++) {
                temp += libIDs[j] + "<;>";
            }

            e.addContent(new Element("group").setAttribute("name", groups[i].getGroupName()).setText(temp));
        }

        doc.getRootElement().addContent(e);

        XMLOutputter fmt = new XMLOutputter();
        FileOutputStream f = null;
        try {
            f = new FileOutputStream(new File(IOManager.getGroupsFile()));
        } catch (FileNotFoundException ex) {
            Logger.getLogger(ParserWriter.class.getName()).log(Level.SEVERE, "Groups file couldn't be found (" + IOManager.getGroupsFile() + ")", ex);
        }
        try {
            fmt.output(doc, f);
            f.close();
        } catch (IOException ex) {
            Logger.getLogger(ParserWriter.class.getName()).log(Level.SEVERE, "While trying to write xml document to the groups file (" + IOManager.getGroupsFile() + ")", ex);
        }
    }

    /**
     * Writes the symbol menu character map to file, as this can be edited by the user. The file structure is
     * simply one line per entry, with the first character on that line as the 'key'. So <br>
     * <br>
     * aäåàáãâæāăą - represents a situation where pressing Ctrl+space and then hitting 'a' will produce a menu
     * with the symbols äåàáãâæāăą.
     *
     * This has now been made the preferences file, as the user can also choose to use Michael to indicate
     * selection in the QuestionList, or a small circle. I couldn't get rid of Michael, but realise that some
     * people might not like him! The first line of this file is now a 1 or 0 corresponding to true or false as
     * to whether or not Michael should be used.
     *
     * @param characterMap the HashMap containing the characters and their corresponding symbol sets.
     */
    public static void writePreferencesFile(HashMap<String, String> characterMap) {
        Document doc = new Document();

        //set up root element
        Element e = new Element("Preferences");
        doc.setRootElement(e);
        e.setAttribute("fileVersion", "1.0");

        //get the data from the answer field file class
        Set<String> keys = characterMap.keySet();
        Iterator<String> iterate = keys.iterator();

        //set-up the symbol menu character map
        String data = "";
        String curKey;

        while (iterate.hasNext()) {
            curKey = iterate.next();
            data += curKey;
            data += characterMap.get(curKey);
            data += "\n";
        }

        //set preferences
        e.setAttribute("firstTimeLoadingLibManager", String.valueOf(IOManager.isFirstTimeLoadingLibManager()));
        e.setAttribute("firstTimeLoadingIngatan", String.valueOf(IOManager.isFirstTimeLoadingIngatan()));
        e.setAttribute("useMichaelForSelection", String.valueOf(IOManager.isUsingMichaelAsSelectionIndicator()));
        e.setAttribute("previousLibManagerGroup", IOManager.getPreviouslySelectedGroup());

        //create array for symbol menu configuration
        String[] symbolMenuConfig = data.split("\n");
        Element symbolMenuElement = new Element("SymbolMenuConfiguration");

        for (int i = 0; i < symbolMenuConfig.length; i++) {
            symbolMenuElement.addContent(new Element("entry").setText(symbolMenuConfig[i]));
        }

        //add all symbol menu config entries
        e.addContent(symbolMenuElement);

        //write file
        XMLOutputter fmt = new XMLOutputter();
        FileOutputStream f = null;
        try {
            f = new FileOutputStream(IOManager.getPreferencesFile());
        } catch (FileNotFoundException ex) {
            Logger.getLogger(ParserWriter.class.getName()).log(Level.SEVERE, "While trying to create an output stream for the preferences file\n"
                    + "file = " + IOManager.getPreferencesFile(), ex);
        }
        try {
            fmt.output(doc, f);
            f.close();
        } catch (IOException ex) {
            Logger.getLogger(ParserWriter.class.getName()).log(Level.SEVERE, "While trying to write xml document to the preferences file output stream.\n"
                    + "file = " + IOManager.getPreferencesFile(), ex);
        }

    }

    /**
     * Parses the symbol menu configuration into a HashMap.
     *
     * All other preferences will be set directly to the IOManager, or other relevant object.
     * 
     * @return the symbol menu configuration content parsed into a HashMap.
     */
    public static HashMap parsePreferencesFile() {
        //Build XML from file
        SAXBuilder sax = new SAXBuilder();
        Document doc = null;
        try {
            doc = sax.build(new File(IOManager.getPreferencesFile()));
        } catch (JDOMException ex) {
            Logger.getLogger(ParserWriter.class.getName()).log(Level.SEVERE, "While trying to build preferences from file to xml", ex);
        } catch (IOException ex) {
            Logger.getLogger(ParserWriter.class.getName()).log(Level.SEVERE, "While trying to build preferences from file to xml", ex);
        }

        //nothing to parse...
        if (doc == null) {
            return null;
        }

        //set preferences
        try {
            IOManager.setUseMichaelAsSelectionIndicator(doc.getRootElement().getAttribute("useMichaelForSelection").getBooleanValue());
            IOManager.setFirstTimeLoadingLibManager(doc.getRootElement().getAttribute("firstTimeLoadingLibManager").getBooleanValue());
            IOManager.setFirstTimeLoadingIngatan(doc.getRootElement().getAttribute("firstTimeLoadingIngatan").getBooleanValue());
        } catch (DataConversionException ex) {
            Logger.getLogger(ParserWriter.class.getName()).log(Level.SEVERE, "While reading the preferences from xml document.", ex);
        }

        //the following record was added to this file version 1.0 after it was written
        Attribute prevGroup = doc.getRootElement().getAttribute("previousLibManagerGroup");
        if (prevGroup == null) {
            //old version of the preferences file, so set it to "- All Libraries -" as this is sure to exist.
            IOManager.setPreviouslySelectedGroup("- All Libraries -");
            System.out.println(" :: old preferences file fixed.");
        } else {
            IOManager.setPreviouslySelectedGroup(prevGroup.getValue());
        }
        


            //set the symbol menu configuration (build hashmap)
            List<Element> symbolMenuData = doc.getRootElement().getChild("SymbolMenuConfiguration").getChildren("entry");
            Iterator<Element> iterate = symbolMenuData.iterator();

            String datum = "";
            HashMap<String, String> characterMap = new HashMap<String, String>();

            while (iterate.hasNext()) {
                datum = iterate.next().getText();
                characterMap.put(String.valueOf(datum.charAt(0)), datum.substring(1));
            }


            return characterMap;
        }
    }
