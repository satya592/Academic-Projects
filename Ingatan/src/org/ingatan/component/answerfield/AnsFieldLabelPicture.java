/*
 * AnsFieldLabelPicture.java
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
package org.ingatan.component.answerfield;

import be.ugent.caagt.jmathtex.TeXConstants;
import be.ugent.caagt.jmathtex.TeXFormula;
import java.awt.event.ActionListener;
import org.ingatan.ThemeConstants;
import org.ingatan.component.image.ImageAcquisitionDialog;
import org.ingatan.component.librarymanager.FlexiQuestionContainer;
import org.ingatan.component.text.SimpleTextField;
import org.ingatan.io.IOManager;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import org.jdom.DataConversionException;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.jdom.output.XMLOutputter;

/**
 * Answer field that allows the user to specify a picture and label points on the picture.
 * At quiz time, the user must label each of the points again.
 *
 * @author Thomas Everingham
 * @version 1.0
 */
public class AnsFieldLabelPicture extends JPanel implements IAnswerField, MouseListener {

    /**
     * When writing to XML, any less than symbols are converted to this char code so that
     * it is not possible for the splitting based on <;> to fail.
     */
    private static final String CHAR_CODE_LESS_THAN = "!lt;";
    /**
     * When writing to XML, any greater than symbols are converted to this char code so that
     * it is not possible for the splitting based on <;> to fail.
     */
    private static final String CHAR_CODE_GREATER_THAN = "!gt;";
    /**
     * The library within which this answer field exists. Allows for access of
     * the image file.
     */
    private String parentLibraryID = "";
    /**
     * The ID for the image that this field uses. This ID is used with the parent
     * library name to reload the image as part of deserialisation.
     */
    private String imageID = "";
    /**
     * An array list of LabelPoints (JButtons) - these are small buttons used as the points on the image
     */
    private ArrayList<LabelPoint> labelPoints = new ArrayList<LabelPoint>();
    /**
     * Action that is fired when any of the LabelPoints are clicked.
     */
    private ShowEditorAction showEditorAction = new ShowEditorAction();
    /**
     * The image to be labelled. This is painted to the picture JPanel.
     */
    private BufferedImage image = null;
    /**
     * JButton for showing the I
     * mageAquisitionDialog.
     */
    private JButton btnAcquireImage = new JButton(new ImageAcquisitionAction());
    /**
     * The JPanel onto which the graphic and points are drawn.
     */
    private PictureFrame picturePanel = new PictureFrame();
    /**
     * Label for the marks per label spinner.
     */
    private JLabel lblMarks = new JLabel("Marks per label: ");
    /**
     * Spinner for the user to set the number of marks to award per correct label answered.
     */
    private JSpinner spinMarks = new JSpinner(new SpinnerNumberModel(1, 0, 20, 1));
    /**
     * Whether or not the answer field currently exists within the editor context (true) or
     * the quiz-time context (false).
     */
    private boolean inEditContext = false;
    /**
     * This is set to true by the displayCorrectAnswer method. It indicates to the
     * paint method that the correct answers should also be rendered, and to the floating
     * editor that it should display possible correct answers.
     */
    private boolean isDisplayCorrectAnswer = false;
    /**
     * Shared editor instance for the LabelPoints.
     */
    private FloatingLabelEditor labelEditor = new FloatingLabelEditor("");
    /**
     * Checkbox for allowing the user to set (in edit time) whether or not hints are allowed for this answer field.
     */
    private JCheckBox chkHints = new JCheckBox("Allow Hints");
    /**
     * Button shown during quiz time if hints are allowed that gives the user the first letter of a randomly
     * selected label.
     */
    private JButton btnGiveHint = new JButton(new GiveHintAction());

    public AnsFieldLabelPicture() {
        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        picturePanel.setLayout(null);
        picturePanel.setBorder(BorderFactory.createLineBorder(ThemeConstants.borderUnselected));
        picturePanel.addMouseListener(this);
        lblMarks.setFont(ThemeConstants.niceFont);
        btnAcquireImage.setFont(ThemeConstants.niceFont);
        btnAcquireImage.setMargin(new Insets(1, 1, 1, 1));

        spinMarks.setMaximumSize(new Dimension(35, 25));
        ((JSpinner.DefaultEditor) spinMarks.getEditor()).getTextField().setEditable(false);

        btnGiveHint.setFont(ThemeConstants.niceFont);
        btnGiveHint.setAlignmentX(CENTER_ALIGNMENT);
        chkHints.setFont(ThemeConstants.niceFont);
        chkHints.setOpaque(false);
        chkHints.setAlignmentX(LEFT_ALIGNMENT);

        this.setOpaque(false);
        rebuild();
    }

    public void rebuild() {
        this.removeAll();
        if (image != null) {
            picturePanel.setMinimumSize(new Dimension(image.getWidth(), image.getHeight()));
            picturePanel.setPreferredSize(new Dimension(image.getWidth(), image.getHeight()));
            picturePanel.setMaximumSize(new Dimension(image.getWidth(), image.getHeight()));
        }
        picturePanel.setAlignmentY(CENTER_ALIGNMENT);
        this.add(picturePanel);
        this.add(Box.createVerticalStrut(8));

        if (inEditContext) {
            Box horiz = Box.createHorizontalBox();
            horiz.add(btnAcquireImage);
            horiz.add(Box.createHorizontalStrut(5));
            horiz.add(lblMarks);
            horiz.add(spinMarks);
            horiz.add(Box.createHorizontalStrut(8));
            horiz.add(chkHints);
            horiz.setMaximumSize(new Dimension(300, 20));
            horiz.setAlignmentY(CENTER_ALIGNMENT);
            this.add(horiz);
        } else {
            if (chkHints.isSelected()) {
                this.add(btnGiveHint);
            }
        }

        this.validate();

    }

    public String getDisplayName() {
        return "Label the Picture";
    }

    public boolean isOnlyForAnswerArea() {
        return true;
    }

    public float checkAnswer() {
        //avoid divide by zero
        if (labelPoints.size() == 0) {
            return 0.0f;
        }

        //sum the number of correctly labelled points
        Iterator<LabelPoint> iterate = labelPoints.iterator();
        int correct = 0;
        while (iterate.hasNext()) {
            if (iterate.next().isCorrect()) {
                correct++;
            }
        }

        return ((float) correct / labelPoints.size());
    }

    public int getMaxMarks() {
        return labelPoints.size() * ((SpinnerNumberModel) spinMarks.getModel()).getNumber().intValue();
    }

    public int getMarksAwarded() {
        return (int) (checkAnswer() * getMaxMarks());
    }

    public void displayCorrectAnswer() {
        isDisplayCorrectAnswer = true;
        this.repaint();
    }

    public void setContext(boolean inLibraryContext) {
        inEditContext = inLibraryContext;
        this.rebuild();
    }

    public String writeToXML() {
        Document doc = new Document();

        //lib metadata
        //ATTRIBUTES: version (of this answer field, allows for back compatability), number of marks per correct label, partent library ID, imageID
        //DATA: point locations w form x,y<;>x,y<;>...
        //    : point data w form data<;>data<;>...
        Element e = new Element(this.getClass().getName()).setAttribute("version", "1.0");
        e.setAttribute("parentLibraryID", parentLibraryID);
        e.setAttribute("imageID", imageID);
        e.setAttribute("marks", String.valueOf(((SpinnerNumberModel) spinMarks.getModel()).getNumber().intValue()));
        e.setAttribute("useHints", String.valueOf(chkHints.isSelected()));

        //construct a string for the coordinates of each point and corresponding data
        String pointCoords = "";
        String pointData = "";
        Iterator<LabelPoint> iterate = labelPoints.iterator();
        LabelPoint curPoint;
        while (iterate.hasNext()) {
            curPoint = iterate.next();
            pointCoords += curPoint.getX() + "," + curPoint.getY();
            pointData += curPoint.getCorrectAnswer().replace("<", CHAR_CODE_LESS_THAN).replace(">", CHAR_CODE_GREATER_THAN);
            //don't add <;> for the final element
            if (iterate.hasNext()) {
                pointCoords += "<;>";
                pointData += "<;>";
            }
        }

        e.addContent(new Element("pointCoords").setText(pointCoords));
        e.addContent(new Element("pointData").setText(pointData));
        doc.addContent(e);

        XMLOutputter fmt = new XMLOutputter();
        return fmt.outputString(doc);
    }

    public void readInXML(String xml) {
        //nothing to parse, so leave
        if (xml == null) {
            return;
        }

        if (xml.trim().equals("") == true) {
            return;
        }

        //try to build document from input string
        SAXBuilder sax = new SAXBuilder();
        Document doc = null;
        try {
            doc = sax.build(new StringReader(xml));
        } catch (JDOMException ex) {
            Logger.getLogger(AnsFieldLabelPicture.class.getName()).log(Level.SEVERE, "While trying to create a JDOM document in the readInXML method.", ex);
        } catch (IOException ex) {
            Logger.getLogger(AnsFieldLabelPicture.class.getName()).log(Level.SEVERE, "While trying to create a JDOM document in the readInXML method.", ex);
        }

        //nothing to parse, so leave
        if (doc == null) {
            return;
        }

        //get the image data and load the image
        parentLibraryID = doc.getRootElement().getAttributeValue("parentLibraryID");
        imageID = doc.getRootElement().getAttributeValue("imageID");
        try {
            if (!imageID.isEmpty() && !parentLibraryID.isEmpty()) {
                image = IOManager.loadImage(parentLibraryID, imageID);
            }
        } catch (IOException ex) {
            Logger.getLogger(AnsFieldLabelPicture.class.getName()).log(Level.SEVERE, "Could not load the label-the-picture answer field image through IOManager during deserialisation.", ex);
        }
        try {
            chkHints.setSelected(doc.getRootElement().getAttribute("useHints").getBooleanValue());
        } catch (DataConversionException ex) {
            Logger.getLogger(AnsFieldLabelPicture.class.getName()).log(Level.SEVERE, "While trying to set the useHints checkbox value, reading in from XML", ex);
        }

        spinMarks.setValue(Integer.valueOf(doc.getRootElement().getAttributeValue("marks")));

        //get the data arrays for the LabelPoints
        String[] pointCoords = doc.getRootElement().getChild("pointCoords").getText().split("<;>");
        String[] pointData = doc.getRootElement().getChild("pointData").getText().split("<;>");

        //otherwise "" can be passed and Integer.valueOf("") will fail.
        if (pointCoords.length == 1) {
            if (pointCoords[0].isEmpty()) {
                pointCoords = new String[0];
                pointData = new String[0];
            }
        }

        //generate the LabelPoints array list
        labelPoints = new ArrayList<LabelPoint>();
        for (int i = 0; i < pointCoords.length && i < pointData.length; i++) {
            try {
            addPoint(Integer.valueOf(pointCoords[i].split(",")[0]), Integer.valueOf(pointCoords[i].split(",")[1]), pointData[i].replace(CHAR_CODE_LESS_THAN, "<").replace(CHAR_CODE_GREATER_THAN, ">"));
            } catch (IndexOutOfBoundsException e) {
                //do nothing here... if a point could not be added, then that's too bad.
            }
        }

        this.rebuild();

    }

    public String getParentLibraryID() {
        return parentLibraryID;
    }

    public void setParentLibraryID(String id) {
        parentLibraryID = id;
    }

    public void resaveImagesAndResources(String newParentLibrary) {
        imageID = IOManager.copyResource(parentLibraryID, imageID, newParentLibrary);
    }

    //for picture panel
    public void mouseClicked(MouseEvent e) {
        //do not allow to add or remove points if not in edit context
        if (!inEditContext) {
            return;
        }

        if (e.getSource() instanceof PictureFrame) {
            //add a point on double click
            if ((e.getClickCount() == 2) && (e.getButton() == MouseEvent.BUTTON1)) {
                addPoint(e.getX(), e.getY(), null);
                labelPoints.get(labelPoints.size()-1).showEditor();
            }
        } else if (e.getSource() instanceof LabelPoint) {
            if (e.getButton() == MouseEvent.BUTTON3) {
                removePoint((LabelPoint) e.getSource());
            }
        }
    }

    //for picture panel
    public void mousePressed(MouseEvent e) {
    }

    //for picture panel
    public void mouseReleased(MouseEvent e) {
    }

    //for picture panel
    public void mouseEntered(MouseEvent e) {
    }

    //for picture panel
    public void mouseExited(MouseEvent e) {
    }

    /**
     * Adds a new <code>LabelPoint</code> to the labelPoints array and to the
     * picture panel, at the specified location. The label text may also be
     * specified by setting the <code>content</code> parameter, though this can
     * be set to <code>null</code> if this is not required.m
     * @param x the x coordinate for the label point.
     * @param y the y coordinate for the label point.
     * @param content the label text for this point.
     */
    public void addPoint(int x, int y, String content) {
        LabelPoint newPoint = new LabelPoint();
        newPoint.addMouseListener(this);
        picturePanel.add(newPoint);
        labelPoints.add(newPoint);
        newPoint.setLocation(x, y);
        if (content != null) {
            newPoint.setCorrectAnswer(content);
        }
        picturePanel.validate();
    }

    /**
     * Removes the specified <code>LabelPoint</code> from the labelPoints array,
     * and from the picture panel.
     * @param point the point to remove.
     */
    public void removePoint(LabelPoint point) {
        labelPoints.remove(point);
        picturePanel.remove(point);
        picturePanel.validate();
    }

    /**
     * Removes all <code>LabelPoints</code> from the labelPoints array, and from the picture panel.
     */
    public void removeAllPoints() {
        picturePanel.removeAll();
        labelPoints = new ArrayList<LabelPoint>();
    }

    public void setQuizContinueListener(ActionListener listener) {
        //this is not implemented, as there is no logical event to trigger continue action in the quiz.
    }

    private class GiveHintAction extends AbstractAction {

        public GiveHintAction() {
            super("Hint");
        }

        public void actionPerformed(ActionEvent e) {

            //if there are no points, no hints to give... so leave
            if (labelPoints.size() == 0) {
                return;
            }

            //whether or not an appropriate editor has been found. An appropriate
            //editor is one that is either empty, or that already starts with the letters
            //of the correct answer. Not one that has incorrect content.
            boolean foundAppropriate = false;
            //only want to look for an editor a maximum of 40 times.
            int timesLooked = 0;
            //The appropriate editor that has been chosen.
            LabelPoint chosenPoint;

            while (!foundAppropriate && (timesLooked < 40)) {
                chosenPoint = labelPoints.get(IOManager.random.nextInt(labelPoints.size()));

                if ((chosenPoint.getUserAnswer().isEmpty()) || (chosenPoint.getCorrectAnswer().trim().toLowerCase().startsWith(chosenPoint.getUserAnswer().trim().toLowerCase()))) {
                    //ensure that the correctanswer is long enough to get substring, otherwise just put the whole lot
                    if (chosenPoint.getCorrectAnswer().length() > chosenPoint.getUserAnswer().length()) {
                        chosenPoint.setUserAnswer(chosenPoint.getCorrectAnswer().substring(0, chosenPoint.getUserAnswer().length() + 1).toLowerCase());
                        foundAppropriate = true;
                    }
                }
                timesLooked++;
            }
            picturePanel.repaint();
        }
    }

    /**
     * A popup text field that contains just a text field in quiz mode,
     * but also a delete button in edit mode.
     */
    private class FloatingLabelEditor extends JPopupMenu implements KeyListener {

        /**
         * The text field used to enter/edit label answers.
         */
        private SimpleTextField txtField = new SimpleTextField("Separate possible answers with ,,");
        /**
         * Content pane for the popup.
         */
        private JPanel contentPane = new JPanel();
        /**
         * The LabelPoint that this editor belongs to.
         */
        private LabelPoint parentPoint;
        /**
         * Label sitting just above the text area. Tells the user to separate possible answers with ,, and to press
         * enter to save and tab to traverse points.
         */
        private JLabel lblInfo = new JLabel("<html>Separate possible answers with ,,<br>Enter to save, Tab to traverse points");
        /**
         * Label that sits below the text field and lists the possible correct answers. This is only set visible when the FloatingLabelEditor
         * is shown and the <code>isDisplayCorrectAnswer</code> flag is true.
         */
        private JLabel lblPossibleAnswers = new JLabel();

        /**
         * Create a new instance of <code>FloatingLabelEditor</code>.
         * @param txtFieldText the initial text for the editor.
         */
        public FloatingLabelEditor(String txtFieldText) {
            contentPane.setLayout(new BoxLayout(contentPane, BoxLayout.Y_AXIS));
            this.add(contentPane);

            lblInfo.setFont(ThemeConstants.niceFont.deriveFont(Font.ITALIC));
            lblPossibleAnswers.setFont(ThemeConstants.niceFont);
            lblPossibleAnswers.setVisible(false);
            lblInfo.setAlignmentX(LEFT_ALIGNMENT);
            contentPane.add(lblInfo);

            txtField.setMaximumSize(new Dimension(200, 20));
            txtField.setMinimumSize(new Dimension(100, 20));
            txtField.setPreferredSize(new Dimension(150, 20));
            txtField.addKeyListener(this);
            txtField.setAlignmentX(LEFT_ALIGNMENT);
            txtField.setFocusTraversalKeysEnabled(false);

            if (txtFieldText != null) {
                txtField.setText(txtFieldText);
            }

            contentPane.add(txtField);
            contentPane.add(lblPossibleAnswers);

        }

        @Override
        public void show(Component invoker, int x, int y) {

            //if we are displaying the correct answer, set up the JLabel below the text field
            if (isDisplayCorrectAnswer) {
                lblPossibleAnswers.setVisible(true);
                String build = "<html>Possible answers:<ul>";
                String[] answers = ((LabelPoint) invoker).getCorrectAnswer().split(",,");
                for (int i = 0; i < answers.length; i++) {
                    build += "<li>" + answers[i] + "</li>";
                }
                lblPossibleAnswers.setText(build + "</ul>");
                this.validate();
            }

            //show the panel
            super.show(invoker, x, y);
            txtField.requestFocus();
            txtField.setSelectionStart(0);
            txtField.setSelectionEnd(txtField.getText().length());

        }

        public String getLabelText() {
            return txtField.getText();
        }

        public void setLabelText(String text) {
            txtField.setText(text);
        }

        public void setContext(boolean inEditContext) {
        }

        public void keyTyped(KeyEvent e) {
        }

        public void keyPressed(KeyEvent e) {
            if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                //if we're in the editor mode, we want to set the correct answer
                if (inEditContext) {
                    ((LabelPoint) this.getInvoker()).setCorrectAnswer(txtField.getText());
                } else { //otherwise, in the quiz we want to set the user's answer
                    ((LabelPoint) this.getInvoker()).setUserAnswer(txtField.getText());
                }
                txtField.setText("");
                this.setVisible(false);
            } else if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                txtField.setText("");
                this.setVisible(false);
            } else if (e.getKeyCode() == KeyEvent.VK_TAB) {
                int curIndex = labelPoints.indexOf((LabelPoint) this.getInvoker());
                LabelPoint newPoint;

                //save the data in this field before moving on
                if (inEditContext) {
                    ((LabelPoint) this.getInvoker()).setCorrectAnswer(txtField.getText());
                } else { //otherwise, in the quiz we want to set the user's answer
                    ((LabelPoint) this.getInvoker()).setUserAnswer(txtField.getText());
                }

                //if this is the last point in the array list, then wrap around to the first point
                if (curIndex == (labelPoints.size() - 1)) {
                    newPoint = labelPoints.get(0);
                } else { //otherwise get the next point onward
                    newPoint = labelPoints.get(curIndex + 1);
                }

                this.setVisible(false);
                if (inEditContext) {
                    this.setLabelText(newPoint.getCorrectAnswer());
                } else {
                    this.setLabelText(newPoint.getUserAnswer());
                }
                this.show(newPoint, 4, 4);
            }
            picturePanel.repaint();
        }

        public void keyReleased(KeyEvent e) {
        }
    }

    /**
     * JPanel that paints itself with the current image.
     */
    private class PictureFrame extends JPanel {

        public PictureFrame() {
            super();
            this.setOpaque(false);
        }

        @Override
        public void paintComponent(Graphics g) {
            super.paintComponent(g);

            Graphics2D g2d = (Graphics2D) g;

            if (image == null) {
                return;
            }
            g.drawImage(image, 0, 0, null);

            Iterator<LabelPoint> iterate = labelPoints.iterator();
            LabelPoint curPoint;
            String renderString = "";

            while (iterate.hasNext()) {
                curPoint = iterate.next();
                if (inEditContext) {
                    renderString = curPoint.getCorrectAnswer().replace(",,", "|");
                } else {
                    renderString = curPoint.getUserAnswer();
                }
                Rectangle2D rect = g2d.getFontMetrics(ThemeConstants.niceFont).getStringBounds(renderString, g2d);
                Rectangle2D toDraw = new Rectangle2D.Double(curPoint.getX() - rect.getWidth() / 2, curPoint.getY() + curPoint.getHeight(), rect.getWidth(), rect.getHeight());
                g2d.setPaint(Color.white);
                g2d.fill(toDraw);
                g2d.setFont(ThemeConstants.niceFont);
                g2d.setPaint(Color.black);
                g2d.drawString(renderString, (float) (curPoint.getX() - rect.getWidth() / 2), (float) (curPoint.getY() + curPoint.getHeight() + rect.getHeight() - 1.5f));

                //do some extra painting if we are now displaying the answers
                if (isDisplayCorrectAnswer) {
                    renderString = curPoint.getCorrectAnswer().replace(",,", "|");

                    rect = g2d.getFontMetrics(ThemeConstants.niceFont).getStringBounds(renderString, g2d);
                    toDraw = new Rectangle2D.Double(curPoint.getX() - rect.getWidth() / 2, curPoint.getY() + curPoint.getHeight() * 2 + 5, rect.getWidth(), rect.getHeight());
                    g2d.setPaint(Color.white);
                    g2d.fill(toDraw);
                    g2d.setFont(ThemeConstants.niceFont);
                    //IF this point was correct, paint possible answers in green
                    if (curPoint.isCorrect()) {
                        g2d.setPaint(ThemeConstants.quizPassGreen);
                    } else { //otherwise paint them in red.
                        g2d.setPaint(ThemeConstants.quizFailRed);
                    }
                    g2d.draw(toDraw);
                    g2d.drawString(renderString, (float) (curPoint.getX() - rect.getWidth() / 2), (float) (curPoint.getY() + curPoint.getHeight() * 2 + rect.getHeight() + 2.5f));
                }
            }
        }
    }

    /**
     * A JButton that encapsulates a correct answer for the label.
     */
    private class LabelPoint extends JButton {

        /**
         * The correct answer - the label for this point.
         */
        String correctAnswer = "";
        /**
         * The answer that has been given by the user.
         */
        String userAnswer = "";

        public LabelPoint() {
            this.setMargin(new Insets(0, 0, 0, 0));
            this.setSize(8, 8);
            this.setText("");
            this.setAction(showEditorAction);
            this.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        }

        public String getCorrectAnswer() {
            return correctAnswer;
        }

        public void setCorrectAnswer(String newText) {
            correctAnswer = newText;
        }

        public String getUserAnswer() {
            return userAnswer;
        }

        public void setUserAnswer(String newText) {
            userAnswer = newText;
        }

        /**
         * Checks whether the answer the user has given is equal to one of the
         * possible correct answers.
         * @return <code>true</code> if the user's answer matches one of the
         * possible correct answers.
         */
        public boolean isCorrect() {
            String[] possibleAnswers = correctAnswer.split(",,");
            for (int i = 0; i < possibleAnswers.length; i++) {
                if (possibleAnswers[i].toLowerCase().equals(userAnswer.toLowerCase())) {
                    return true;
                }
            }
            //user answer not found as a correct answer
            return false;
        }

        /**
         * Show the floating label editor popup so that this point can be editted.
         */
        public void showEditor() {
            labelEditor.show(this, 4, 4);
            if (inEditContext) {
                labelEditor.setLabelText(correctAnswer);
            } else {
                labelEditor.setLabelText(userAnswer);
            }
        }
    }

    /**
     * Action for each LabelPoint button; shows the floating editor field.
     */
    private class ShowEditorAction extends AbstractAction {

        public void actionPerformed(ActionEvent e) {
            ((LabelPoint) e.getSource()).showEditor();
        }
    }

    /**
     * Action for the set image button of the AnsFieldLabelPicture answer field.
     */
    private class ImageAcquisitionAction extends AbstractAction {

        /**
         * Maximum <b>recommended</b> width for the image used.
         */
        private static final int MAX_WIDTH = 400;
        /**
         * Maximum <b>recommended</b> height for the image used.
         */
        private static final int MAX_HEIGHT = 400;

        public ImageAcquisitionAction() {
            super("Set Image");
        }

        public void actionPerformed(ActionEvent e) {
            if (AnsFieldLabelPicture.this.getRootPane().getParent() instanceof JDialog) {
                JOptionPane.showMessageDialog(AnsFieldLabelPicture.this, "You cannot set a default image for this answer field.", "Cannot Set Image", JOptionPane.INFORMATION_MESSAGE);
                return;
            }
            ImageAcquisitionDialog imgDialog = IOManager.getImageAcquisitionDialog();//(Window) AnsFieldLabelPicture.this.getRootPane().getParent());

            imgDialog.setVisible(true);

            String fromLib, id;

            //if there has already been an image loaded, and the user is about to replace it (i.e. !=NONE user didn't cancel)
            //then offer for the user to erase all points.
            if ((image != null) && (imgDialog.getAcquisitionSource() != ImageAcquisitionDialog.NONE)) {
                int resp = JOptionPane.showConfirmDialog(AnsFieldLabelPicture.this, "You have changed the image. Would you like to delete all of the\n"
                        + "currently existing label points? ", "Delete Labels?", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);

                if (resp == JOptionPane.YES_OPTION) {
                    AnsFieldLabelPicture.this.removeAllPoints();
                }
            }

            //if we're dealing with an image.
            if ((imgDialog.getAcquisitionSource() != ImageAcquisitionDialog.NONE) && (imgDialog.getAcquisitionSource() != ImageAcquisitionDialog.FROM_MATH_TEXT)) {
                //FROM LIBRARY------------------------------------------------------------------------------
                if (imgDialog.getAcquisitionSource() == ImageAcquisitionDialog.FROM_LIBRARY) {
                    fromLib = imgDialog.getAcquiredImageData().split("\n")[1];
                    id = imgDialog.getAcquiredImageData().split("\n")[0];
                    //if this is from the library to which this question belongs, then we do not need to resave it, simply insert it.
                    if (fromLib.equals(getParentLibraryID())) {
                        try {
                            image = IOManager.loadImage(fromLib, id);
                            imageID = id;
                        } catch (IOException ex) {
                            Logger.getLogger(AnsFieldLabelPicture.class.getName()).log(Level.SEVERE, "IO Exception occurred while attempting to load an image through the IOManager\n"
                                    + "to use as the background for a label-the-picture answer field. Image ID=" + id + " library=" + fromLib, ex);
                        }
                    } else {
                        try {
                            //otherwise the image must be copied into the current library from the source library.
                            image = IOManager.loadImage(fromLib, id);
                        } catch (IOException ex) {
                            Logger.getLogger(AnsFieldLabelPicture.class.getName()).log(Level.SEVERE, "IO Exception occurred while attempting to load an image through the IOManager\n"
                                    + " to use as the background for a label-the-picture answer field. Destination library={" + getParentLibraryID() + "}", ex);
                        }
                        try {
                            imageID = IOManager.saveImage(getParentLibraryID(), image, id);
                        } catch (IOException ex) {
                            Logger.getLogger(AnsFieldLabelPicture.class.getName()).log(Level.SEVERE, "IO Exception occurred while attempting to save an image to a library through the IOManager\n"
                                    + ". Image ID=" + id + " , from library=" + fromLib + " , destination library={" + getParentLibraryID() + "}", ex);
                        }
                    }
                    //FROM COLLECTION/NEW/FILE------------------------------------------------------------------------------
                } else if ((imgDialog.getAcquisitionSource() == ImageAcquisitionDialog.FROM_COLLECTION) || (imgDialog.getAcquisitionSource() == ImageAcquisitionDialog.FROM_FILE)
                        || (imgDialog.getAcquisitionSource() == ImageAcquisitionDialog.FROM_NEW) || (imgDialog.getAcquisitionSource() == ImageAcquisitionDialog.FROM_CHEM_STRUCTURE)) {
                    try {
                        imageID = IOManager.saveImage(getParentLibraryID(), imgDialog.getAcquiredImage(), imgDialog.getAcquiredImageData());
                        image = imgDialog.getAcquiredImage();
                    } catch (IOException ex) {
                        Logger.getLogger(AnsFieldLabelPicture.class.getName()).log(Level.SEVERE, "IO Exception occurred while attempting to save an image to a library through the IOManager"
                                + ". This occurred during user initiated ImageAcquisition from a collection, from file or from a newly created image."
                                + " Destination library={" + getParentLibraryID() + "}", ex);
                    }
                }
                //MATH-TEXT or USER CANCELLED------------------------------------------------------------------------------
            } else { //we're dealing with 'user cancelled' or math text
                if (imgDialog.getAcquisitionSource() == ImageAcquisitionDialog.NONE) {
                    return;
                } else {
                    //split into math-text-data and render-size
                    String[] mathText = imgDialog.getAcquiredImageData().split("\n");
                    String[] colourVals = mathText[2].split(",");
                    //render the MathTeX
                    TeXFormula formula = new TeXFormula(mathText[0]);
                    Icon icon = formula.createTeXIcon(TeXConstants.STYLE_DISPLAY, Integer.valueOf(mathText[1]));
                    image = new BufferedImage(icon.getIconWidth(), icon.getIconHeight() + 6, BufferedImage.TYPE_INT_ARGB);
                    Graphics2D g2 = image.createGraphics();
                    g2.fillRect(0, 0, image.getWidth(), image.getHeight());
                    icon.paintIcon(new JLabel(), g2, 0, 5);

                    //save image to library
                    try {
                        imageID = IOManager.saveImage(getParentLibraryID(), image, "MathTeX(" + colourVals[0] + "r");
                    } catch (IOException ex) {
                        Logger.getLogger(AnsFieldLabelPicture.class.getName()).log(Level.SEVERE, "While attempting to save a mathTeX render to a library through the IOManager\n", ex);
                    }

                }
            }

            if (isImageTooLarge(image)) {
                int resp = JOptionPane.showConfirmDialog(AnsFieldLabelPicture.this, "This image is larger than the recommended maximum size. Would you\n"
                        + "like Ingatan to shrink the image to the largest recommended size?", "Large Image", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);

                if (resp == JOptionPane.YES_OPTION) {
                    resizeToMax(image);
                    try {
                        IOManager.saveImageWithOverWrite(image, getParentLibraryID(), imageID);
                    } catch (IOException ex) {
                        Logger.getLogger(FlexiQuestionContainer.class.getName()).log(Level.SEVERE, "ocurred while trying to save a BufferedImage with rewrite using IOManager\n"
                                + "in order to save a resized version of the image upon user request (user was just told the image is larger than recommended, and asked if they would\n"
                                + "like it resized).", ex);
                    }
                }
            }



            rebuild();
        }

        /**
         * Tests whether the image is larger than the <b>recommended</b> maximum size, based on the MAX_WIDTH and MAX_HEIGHT fields.
         * @param image the image to test.
         * @return <code>true</code> if the image is larger than the recommended maximum size.
         */
        public boolean isImageTooLarge(BufferedImage image) {
            if ((image.getWidth() > MAX_WIDTH) || (image.getHeight() > MAX_HEIGHT)) {
                return true;
            } else {
                return false;
            }
        }

        /**
         * Resizes the image to the maximum recommended size, as specified by the
         * MAX_WIDTH and MAX_HEIGHT fields. Reassigns the parameter image to the new,
         * smaller image.
         */
        public void resizeToMax(BufferedImage image) {
            BufferedImage newImg = new BufferedImage(MAX_WIDTH, MAX_HEIGHT, image.getType());
            newImg.createGraphics().drawImage(image, 0, 0, MAX_WIDTH, MAX_HEIGHT, null);
            image = newImg;
        }
    }
}
