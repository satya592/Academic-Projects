/*
 * TableQuestionOptionPane.java
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

import javax.swing.event.ChangeEvent;
import org.ingatan.ThemeConstants;
import org.ingatan.component.text.NumericJTextField;
import org.ingatan.component.text.SimpleTextField;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.awt.event.ActionListener;
import java.util.Arrays;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerListModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeListener;

/**
 * Option pane that appears below the table in the TableQuestionContainer. Includes
 * options for how the table questions are asked, whether the data is reversible,
 * and also a couple of checkboxes for the input behaviour of the table.
 *
 * @author Thomas Everingham
 * @version 1.0
 */
public class TableQuestionOptionPane extends JPanel {

    /**
     * Maximum size of the text fields and combo boxes.
     */
    private final static Dimension MAX_FIELD_SIZE = new Dimension(140, 20);
    /**
     * Minimum size of the text fields and combo boxes.
     */
    private final static Dimension MIN_FIELD_SIZE = new Dimension(50, 20);
    /**
     * The ask style determines how the questions will be asked at quiz time. A question taken
     * from the first column can be put to the user, and the answer can then be typed. This is
     * the written style. Alternatively, multiple choice questions can automatically be generated
     * from the other data in the table. This is good for the early stages of learning.
     * Both methods may be used, in which case they are used randomly during quiz time.
     */
    private JSpinner askStyle = new JSpinner(new SpinnerListModel(Arrays.asList(new String[]{"Text field", "Multiple choice", "Either at Random"})));
    /**
     * If this is checked then, at quiz time, questions will be generated from both columns, rather than
     * just one. This is useful for vocabulary training, where the user would not want to rote learn
     * just Spanish to Swedish, for example, but also Swedish to Spanish.
     */
    private JCheckBox askInReverse = new JCheckBox("can ask in reverse");
    /**
     * The question template allows the user to set a template for each question, where
     * the data taken from the table is substituted into the template. This makes things
     * a little nicer than just being presented with a word.
     */
    private SimpleTextField fwdQuestionTemplate = new SimpleTextField();
    /**
     * The question template allows the user to set a template for each question, where
     * the data taken from the table is substituted into the template. This makes things
     * a little nicer than just being presented with a word.
     *
     * This is a second, bwd (backward) template. This field only appears if the
     * 'can ask in reverse' checkbox is ticked.
     */
    private SimpleTextField bwdQuestionTemplate = new SimpleTextField();
    /**
     * listener that is notified when the fonts combo box changes value.
     */
    private ActionListener listener = null;
    /**
     * Number of marks to award per correct answer.
     */
    private NumericJTextField marksPerAnswer = new NumericJTextField(1);
    /**
     * Font chooser combo box.
     */
    private JSpinner spinnerFonts;
    /**
     * Spinner for font size.
     */
    private JSpinner spinnerFontSize = new JSpinner(new SpinnerNumberModel(12, 3, 200, 1));
    /**
     * "Answer field to use" label.
     */
    private JLabel lblAskStyle = new JLabel("Answer field to use in quiz: ");
    /**
     * "Marks per correct answer:" label.
     */
    private JLabel lblNumberMarks = new JLabel("Marks per correct answer: ");
    /**
     * "Question template (forward)" label.
     */
    private JLabel lblForwardTemplate = new JLabel("Question template (forward): ");
    /**
     * "Question template (backward)" label.
     */
    private JLabel lblBackwardTemplate = new JLabel("Question template (backward): ");
    /**
     * "Font:" label.
     */
    private JLabel lblFont = new JLabel("Font and Size (quiz-time): ");

    /**
     * Creates a new <code>TableQuestionOptionPane<code>.
     */
    public TableQuestionOptionPane() {

        lblAskStyle.setFont(ThemeConstants.niceFont);
        lblNumberMarks.setFont(ThemeConstants.niceFont);
        lblForwardTemplate.setFont(ThemeConstants.niceFont);
        lblBackwardTemplate.setFont(ThemeConstants.niceFont);
        lblFont.setFont(ThemeConstants.niceFont);
        askStyle.setFont(ThemeConstants.niceFont);
        askInReverse.setFont(ThemeConstants.niceFont);
        fwdQuestionTemplate.setFont(ThemeConstants.niceFont);
        bwdQuestionTemplate.setFont(ThemeConstants.niceFont);

        spinnerFonts = createFontsSpinner(GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames());
        ((SpinnerListModel) spinnerFonts.getModel()).setValue(this.getFont().getFamily());
        
        JSpinner.NumberEditor numberEditor = new JSpinner.NumberEditor(spinnerFontSize);
        spinnerFontSize.setFont(ThemeConstants.niceFont);
        spinnerFontSize.setEditor(numberEditor);
        spinnerFontSize.setMaximumSize(new Dimension(50,MAX_FIELD_SIZE.height));
        spinnerFontSize.setMinimumSize(new Dimension(50,MAX_FIELD_SIZE.height));
        spinnerFontSize.setPreferredSize(new Dimension(50,MAX_FIELD_SIZE.height));
        spinnerFontSize.setToolTipText("The size of the font for display during a quiz.");
        spinnerFonts.setToolTipText("The font to use for the table data. Allows you to use kanji, etc. as part of the table question.");


        bwdQuestionTemplate.setToolTipText("The question if asking backwards - use [q] where you would like the question word to appear.");
        fwdQuestionTemplate.setToolTipText("The question if asking forwards - use [q] where you would like the question word to appear.");



        this.setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
        this.setOpaque(false);
        askInReverse.setOpaque(false);
        askStyle.setBackground(Color.white);
        spinnerFonts.setBackground(Color.white);

        this.add(askInReverse);
        askInReverse.setAlignmentX(LEFT_ALIGNMENT);

        Box b = Box.createHorizontalBox();
        b.setAlignmentX(LEFT_ALIGNMENT);
        b.add(lblFont);
        b.add(Box.createHorizontalGlue());
        b.add(spinnerFontSize);
        b.add(Box.createHorizontalStrut(2));
        b.add(spinnerFonts);
        spinnerFonts.setMaximumSize(MAX_FIELD_SIZE);
        spinnerFonts.setPreferredSize(MAX_FIELD_SIZE);
        spinnerFonts.setMinimumSize(MIN_FIELD_SIZE);

        this.add(b);

        b = Box.createHorizontalBox();
        b.setAlignmentX(LEFT_ALIGNMENT);
        b.add(lblAskStyle);
        b.add(Box.createHorizontalGlue());
        b.add(askStyle);
        askStyle.setMaximumSize(MAX_FIELD_SIZE);
        askStyle.setPreferredSize(MAX_FIELD_SIZE);
        askStyle.setMinimumSize(MIN_FIELD_SIZE);

        this.add(b);

        b = Box.createHorizontalBox();
        b.setAlignmentX(LEFT_ALIGNMENT);
        b.add(lblNumberMarks);
        b.add(Box.createHorizontalGlue());
        b.add(marksPerAnswer);
        marksPerAnswer.setMaximumSize(MAX_FIELD_SIZE);
        marksPerAnswer.setPreferredSize(MAX_FIELD_SIZE);
        marksPerAnswer.setMinimumSize(MIN_FIELD_SIZE);

        this.add(b);

        b = Box.createHorizontalBox();
        b.setAlignmentX(LEFT_ALIGNMENT);
        b.add(lblForwardTemplate);
        b.add(Box.createHorizontalGlue());
        b.add(fwdQuestionTemplate);
        fwdQuestionTemplate.setMaximumSize(MAX_FIELD_SIZE);
        fwdQuestionTemplate.setPreferredSize(MAX_FIELD_SIZE);
        fwdQuestionTemplate.setMinimumSize(MIN_FIELD_SIZE);

        this.add(b);

        b = Box.createHorizontalBox();
        b.setAlignmentX(LEFT_ALIGNMENT);
        b.add(lblBackwardTemplate);
        b.add(Box.createHorizontalGlue());
        b.add(bwdQuestionTemplate);
        bwdQuestionTemplate.setMaximumSize(MAX_FIELD_SIZE);
        bwdQuestionTemplate.setPreferredSize(MAX_FIELD_SIZE);
        bwdQuestionTemplate.setMinimumSize(MIN_FIELD_SIZE);

        this.add(b);

        this.validate();
    }

    private JSpinner createFontsSpinner(String[] listItems) {
        JSpinner ret = new JSpinner(new SpinnerListModel(Arrays.asList(listItems)));
        ret.setFont(new Font(this.getFont().getFamily(), Font.PLAIN, 9));
        ret.addChangeListener(new SpinnerChangeListener());
        return ret;
    }

    public JCheckBox getAskInReverse() {
        return askInReverse;
    }

    public JSpinner getAskStyle() {
        return askStyle;
    }

    public JSpinner getFontSpinner() {
        return spinnerFonts;
    }

    public JSpinner getFontSizeSpinner() {
        return spinnerFontSize;
    }

    public SimpleTextField getBwdQuestionTemplate() {
        return bwdQuestionTemplate;
    }

    public SimpleTextField getFwdQuestionTemplate() {
        return fwdQuestionTemplate;
    }

    public NumericJTextField getMarksPerAnswer() {
        return marksPerAnswer;
    }

    private class SpinnerChangeListener implements ChangeListener {
        public void stateChanged(ChangeEvent e) {
            if (listener == null)
                return;

            listener.actionPerformed(null);
        }
    }

    /**
     * Sets the action listener is fired when a change in font selection occurs.
     */
    public void setFontsComboActionListener(ActionListener listener) {
        this.listener = listener;
    }

    public Font getSelectedFont() {
    return new Font((String) (((SpinnerListModel) spinnerFonts.getModel()).getValue()), Font.PLAIN,ThemeConstants.tableCellEditorFont.getSize());
    }

    /**
     * Gets the specified quiz-time display font size.
     * @return the quiz-time display font size.
     */
    public int getSelectedFontSize() {
        return ((SpinnerNumberModel) spinnerFontSize.getModel()).getNumber().intValue();
    }
}

