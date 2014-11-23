/*
 * PreferencesDialog.java
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

package org.ingatan.component;

import org.ingatan.io.IOManager;
import org.ingatan.io.ParserWriter;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import javax.swing.AbstractAction;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import org.ingatan.ThemeConstants;

/**
 * Allows the user to set the symbol menu keymap, as well as a couple of other preferences. The symbol menu can be summoned in almost any of the
 * Ingatan text fields and text areas by pressing Ctrl+Space, followed by any character on the keyboard.
 * This brings up an inline menu that allows the user to select a symbol to insert. <br>
 * <br>
 * This dialog allows the user to alter the symbols that correspond to each character on the keyboard,
 * whether that be completely, or just by changing the order of the default characters.
 * @author Thomas Everingham
 * @version 1.0
 */
public class PreferencesDialog extends JDialog {
    /**
     * All <code>SymbolEntry</code> items in the list.
     */
    private ArrayList<SymbolEntry> symbolEntries = new ArrayList<SymbolEntry>();
    /**
     * Content pane for the scroller. Container for the <code>SymbolEntry</code> objects.
     */
    private JPanel scrollerContent = new JPanel();
    /**
     * JScroller for the <code>SymbolEntry</code> object list.
     */
    private JScrollPane scroller = new JScrollPane(scrollerContent);
    /**
     * Button for saving changes to the symbol menu character map.
     */
    private JButton btnSave = new JButton(new SaveAction());
    /**
     * Button for resetting fields to their default values.
     */
    private JButton btnDefaults = new JButton(new DefaultAction());
    /**
     * Options pane for preferences not related to the symbol menu.
     */
    private OptionsPane optionPane = new OptionsPane();
    /**
     * info label for above the symbol menu settings.
     */
    private JLabel lblSymbolMenuInfo = new JLabel("<html><h4>Symbol Menu Configuration</h4>The symbol menu is shown whenever you press ctrl+space " +
            "in a text field, followed by any letter or number on the keyboard. " +
            "The symbols shown for a particular number or letter can be set below. " +
            "Note: by capitalising a letter, the corresponding symbols are also capitalised.");
    /**
     * heading for the options pane
     */
    private JLabel lblOptionsPane = new JLabel("<html><h4>General Preferences</h4>");

    public PreferencesDialog(JFrame owner) {
        super(owner);
        this.setModal(true);
        this.setTitle("Preferences");
        this.setIconImage(IOManager.windowIcon);
        
        scrollerContent.setLayout(new BoxLayout(scrollerContent, BoxLayout.Y_AXIS));

        
        rebuild();


        scroller.setAlignmentX(LEFT_ALIGNMENT);
        scroller.setOpaque(false);
        scrollerContent.setOpaque(false);
        btnSave.setMargin(new Insets(1, 1, 1, 1));
        btnDefaults.setMargin(new Insets(1, 1, 1, 1));
        lblSymbolMenuInfo.setFont(ThemeConstants.niceFont);
        
        this.getContentPane().setLayout(new BoxLayout(this.getContentPane(), BoxLayout.Y_AXIS));
        this.getContentPane().add(lblOptionsPane);
        this.getContentPane().add(optionPane);
        this.getContentPane().add(lblSymbolMenuInfo);
        this.getContentPane().add(scroller);


        optionPane.selectionButtonGroup.setSelected(optionPane.radioSelectionDot.getModel(), !IOManager.isUsingMichaelAsSelectionIndicator());
        optionPane.selectionButtonGroup.setSelected(optionPane.radioSelectionMichael.getModel(), IOManager.isUsingMichaelAsSelectionIndicator());

        Box horiz = Box.createHorizontalBox();
        horiz.add(btnSave);
        horiz.add(Box.createHorizontalStrut(5));
        horiz.add(btnDefaults);
        horiz.setAlignmentX(LEFT_ALIGNMENT);

        this.getContentPane().add(horiz);

        this.setSize(new Dimension(350,400));
        this.setLocationRelativeTo(null);
        
        this.validate();
    }


    private void rebuild() {
        HashMap<String,String> characterMap = IOManager.getSymbolMenuCharacterMap();
        char[] characters = new char[]{'a','b','c','d','e','f','g','h','i','j','k','l','m','n','o','p','q','r','s','t','u','v','w','x','y','z','0','1','2','3','4','5','6','7','8','9'};

        scrollerContent.removeAll();

        SymbolEntry newEntry;
        String symbolList;
        
        for (int i = 0; i < characters.length; i++) {
            symbolList = characterMap.get(String.valueOf(characters[i]));
            if (symbolList == null) symbolList = " ";
            newEntry = new SymbolEntry(characters[i], symbolList);
            symbolEntries.add(newEntry);
            scrollerContent.add(newEntry);
        }

        this.validate();
    }


    private class SaveAction extends AbstractAction {
        public SaveAction() {
            super("Save");
        }

        public void actionPerformed(ActionEvent e) {
            Iterator<SymbolEntry> iterate = symbolEntries.iterator();

            SymbolEntry curEntry;
            HashMap<String,String> charMap = IOManager.getSymbolMenuCharacterMap();

            String curList;
            while (iterate.hasNext()) {
                curEntry = iterate.next();
                curList = curEntry.getSymbolList();
                if (curList.isEmpty())
                    curList = " ";
                charMap.put(String.valueOf(curEntry.getCharacter()), curList);

                //if this is a letter, add the upper case equivalent as well.
                if (Character.isLetter(curEntry.getCharacter())) {
                    charMap.put(String.valueOf(Character.toUpperCase(curEntry.getCharacter())),curList.toUpperCase());
                }
            }

            IOManager.setUseMichaelAsSelectionIndicator(optionPane.radioSelectionMichael.getModel().isSelected());

            ParserWriter.writePreferencesFile(charMap);
            PreferencesDialog.this.setVisible(false);
        }

    }

    private class DefaultAction extends AbstractAction {
        public DefaultAction() {
            super("Defaults");
        }

        public void actionPerformed(ActionEvent e) {
            int resp = JOptionPane.showConfirmDialog(PreferencesDialog.this, "Are you sure you wish to reset to defaults? The current configuration will be lost.", "Reset to Default", JOptionPane.YES_NO_OPTION);
            if (resp == JOptionPane.YES_OPTION) {
                IOManager.defaultSymbolMenuCharacterMap();
                rebuild();
            }


        }

    }


    /**
     * Options pane containing other options for Ingatan. Whenever a new option must be set, it can
     * be included here.
     */
    private class OptionsPane extends JPanel {
        ButtonGroup selectionButtonGroup = new ButtonGroup();
        JLabel lblSelectionGraphic = new JLabel("How to show selection in the question list:");
        JRadioButton radioSelectionMichael = new JRadioButton("Use Michael");
        JRadioButton radioSelectionDot = new JRadioButton("Use Dots");

        public OptionsPane() {
            lblSelectionGraphic.setFont(ThemeConstants.niceFont);
            radioSelectionDot.setFont(ThemeConstants.niceFont);
            selectionButtonGroup.add(radioSelectionDot);
            radioSelectionMichael.setFont(ThemeConstants.niceFont);
            selectionButtonGroup.add(radioSelectionMichael);

            this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
            this.add(lblSelectionGraphic);
            this.add(radioSelectionDot);
            this.add(radioSelectionMichael);
        }
    }


    /**
     * A single character entry. Consists of a label tha
     */
    private class SymbolEntry extends JPanel {
        /**
         * Which character this symbol entry corresponds to.
         */
        private JLabel lblCharacter = new JLabel();
        /**
         * The character to which this symbol entry corresponds.
         */
        private char character;
        /**
         * Text field allowing the user to enter the symbols they require for this
         * character.
         */
        private JTextField txtSymbols = new JTextField();

        public SymbolEntry(char character, String symbols) {
            super();
            this.character = character;
            lblCharacter.setText(String.valueOf(character) + ": ");
            txtSymbols.setText(symbols);

            setUpGUI();
        }

        /**
         * Get the list of symbols that have been entered for this character.
         * @return the list of symbols that have been entered for this character.
         */
        public String getSymbolList() {
            return txtSymbols.getText();
        }

        /**
         * Get the character that this entry represents.
         * @return the character that this entry represents.
         */
        public char getCharacter() {
            return character;
        }

        private void setUpGUI() {
            this.setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
            this.setOpaque(false);

            lblCharacter.setMaximumSize(new Dimension(20,20));
            txtSymbols.setMaximumSize(new Dimension(120,20));

            this.add(lblCharacter);
            this.add(Box.createHorizontalStrut(5));
            this.add(txtSymbols);
            this.setMaximumSize(new Dimension(145, 22));
            this.setMinimumSize(new Dimension(145,22));
            this.setPreferredSize(new Dimension(145,22));

        }
    }
}
