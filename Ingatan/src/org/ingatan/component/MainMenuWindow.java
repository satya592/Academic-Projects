/*
 * MainMenuWindow.java
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

import java.awt.Color;
import java.awt.event.WindowEvent;
import org.ingatan.component.librarymanager.LibraryManagerWindow;
import org.ingatan.component.quiztime.QuizSettingsDialog;
import org.ingatan.component.quiztime.QuizWindow;
import java.awt.Dimension;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.WindowListener;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import org.ingatan.ThemeConstants;
import org.ingatan.component.text.RichTextArea;
import org.ingatan.io.IOManager;
import org.ingatan.io.ParserWriter;

/**
 * The main menu for Ingatan. This is the first window to be shown, and provides
 * access to: <ul>
 * <li>quiz time</li>
 * <li>the library manager</li>
 * <li>symbol menu configuration</li>
 * <li>quiz records</li>
 * <li>about ingatan</li>
 * <li>exit button</li>
 * </ul>
 *
 * @author Thomas Everingham
 * @version 1.0
 */
public class MainMenuWindow extends JFrame implements WindowListener {

    /**
     * Content pane for the MainMenuWindow.
     */
    private JPanel contentPane = new FishPanel();
    /**
     * Button for showing the library manager
     */
    private JButton btnLibManager = new JButton(new LibraryManagerAction());
    /**
     * Button for showing the quiz creation dialog
     */
    private JButton btnQuizMe = new JButton(new QuizMeAction());
    /**
     * Button for showing the symbol menu configuration window.
     */
    private JButton btnSymbolMenuConfig = new JButton(new SymbolMenuConfigAction());
    /**
     * Button for showing the quiz record dialogue.
     */
    private JButton btnQuizRecord = new JButton(new QuizRecordAction());
    /**
     * Button for showing the about ingatan dialogue.
     */
    private JButton btnAbout = new JButton(new AboutAction());
    /**
     * Image painted to the background of the menu.
     */
    private BufferedImage bgImg;

    /**
     * Creates a new instance of <code>MainMenuWindow</code>.
     */
    public MainMenuWindow(BufferedImage bgImage) {
        super();


        if (IOManager.isFirstTimeLoadingIngatan()) {
            RichTextArea dispArea = new RichTextArea();

            dispArea.setPreferredSize(new Dimension(400, 140));
            dispArea.setSize(new Dimension(400, 140));
            dispArea.setMinimumSize(new Dimension(400, 140));

            dispArea.setBorder(BorderFactory.createEmptyBorder());
            dispArea.setEditable(false);
            dispArea.setOpaque(false);

            dispArea.setRichText("[aln]0[!aln][fam]Dialog[!fam][sze]16[!sze][col]51,51,51[!col]Welcome to Ingatan[sze]12[!sze][br]"
                    + "This message will only be shown once. "
                    + "For a first user's tutorial, please go to ingatan.org/wiki/Tutorial.[br][br]"
                    + "If running Ingatan under OpenJDK, it may run slowly. In this case, load Ingatan using the command:[br]"
                    + "[fam]Monospace[!fam]java -Dsun.java2d.pmoffscreen=false -jar !osqb;path_to_ingatan.jar!csqb;[br][br]"
                    + "[fam]Dialog[!fam]You can view this information again in the About menu.[end]");

            JOptionPane.showMessageDialog(MainMenuWindow.this, dispArea, "Using OpenJDK", JOptionPane.INFORMATION_MESSAGE);
            IOManager.setFirstTimeLoadingIngatan(false);
            ParserWriter.writePreferencesFile(IOManager.getSymbolMenuCharacterMap());
        }


        this.bgImg = bgImage;

        this.setTitle("");
        this.setContentPane(contentPane);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setIconImage(IOManager.windowIcon);
        this.setTitle("Main Menu");

        setUpGUI();

        this.setSize(bgImage.getWidth() + 8, bgImage.getHeight() + 20);
        this.setLocationRelativeTo(this.getParent());
    }

    private void setUpGUI() {
        addWindowListener(this);
        contentPane.setLayout(new BoxLayout(contentPane, BoxLayout.Y_AXIS));
        Dimension buttonSize = new Dimension(160, 25);

        contentPane.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

        contentPane.add(Box.createVerticalStrut(48));

        btnQuizMe.setMinimumSize(buttonSize);
        btnQuizMe.setMaximumSize(buttonSize);
        btnQuizMe.setPreferredSize(buttonSize);
        btnQuizMe.setAlignmentX(CENTER_ALIGNMENT);
        contentPane.add(btnQuizMe);
        contentPane.add(Box.createVerticalStrut(5));


        btnLibManager.setMinimumSize(buttonSize);
        btnLibManager.setMaximumSize(buttonSize);
        btnLibManager.setPreferredSize(buttonSize);
        btnLibManager.setAlignmentX(CENTER_ALIGNMENT);
        contentPane.add(btnLibManager);
        contentPane.add(Box.createVerticalStrut(5));


        btnQuizRecord.setMinimumSize(buttonSize);
        btnQuizRecord.setMaximumSize(buttonSize);
        btnQuizRecord.setPreferredSize(buttonSize);
        btnQuizRecord.setAlignmentX(CENTER_ALIGNMENT);
        contentPane.add(btnQuizRecord);
        contentPane.add(Box.createVerticalStrut(5));


        btnSymbolMenuConfig.setMinimumSize(buttonSize);
        btnSymbolMenuConfig.setMaximumSize(buttonSize);
        btnSymbolMenuConfig.setPreferredSize(buttonSize);
        btnSymbolMenuConfig.setAlignmentX(CENTER_ALIGNMENT);
        contentPane.add(btnSymbolMenuConfig);
        contentPane.add(Box.createVerticalStrut(5));

        btnAbout.setMinimumSize(buttonSize);
        btnAbout.setMaximumSize(buttonSize);
        btnAbout.setPreferredSize(buttonSize);
        btnAbout.setAlignmentX(CENTER_ALIGNMENT);
        contentPane.add(btnAbout);
        contentPane.add(Box.createVerticalStrut(5));

//        btnExit.setMinimumSize(buttonSize);
//        btnExit.setMaximumSize(buttonSize);
//        btnExit.setPreferredSize(buttonSize);
//        btnExit.setAlignmentX(CENTER_ALIGNMENT);
//        contentPane.add(btnExit);



        this.setVisible(true);


    }

    public void windowOpened(WindowEvent e) {
    }

    public void windowClosing(WindowEvent e) {
        ParserWriter.writePreferencesFile(IOManager.getSymbolMenuCharacterMap());
    }

    public void windowClosed(WindowEvent e) {
    }

    public void windowIconified(WindowEvent e) {
    }

    public void windowDeiconified(WindowEvent e) {
    }

    public void windowActivated(WindowEvent e) {
    }

    public void windowDeactivated(WindowEvent e) {
    }

    private class LibraryManagerAction extends AbstractAction {

        public LibraryManagerAction() {
            super("Library Manager");
        }

        public void actionPerformed(ActionEvent e) {
            MainMenuWindow.this.setVisible(false);
            new LibraryManagerWindow(MainMenuWindow.this).setVisible(true);
        }
    }

    private class QuizMeAction extends AbstractAction {

        public QuizMeAction() {
            super("Quiz Me");
        }

        public void actionPerformed(ActionEvent e) {
            //show to quiz settings dialog to ask the user which libraries should be included, as well as whether questions should be randomised
            QuizSettingsDialog qsd = new QuizSettingsDialog(MainMenuWindow.this);
            qsd.setVisible(true);

            //the user cancelled if this is the case
            if (qsd.getGeneratedQuizManager() == null) {
                return;
            }

            MainMenuWindow.this.setVisible(false);
            //create a new quiz window based on the QuizManager generated by this quiz settings dialog
            new QuizWindow(qsd.getGeneratedQuizManager(), MainMenuWindow.this).setVisible(true);
            //dispose of the quiz settings dialog and hide the main menu window
            qsd.dispose();
        }
    }

    public class SymbolMenuConfigAction extends AbstractAction {

        public SymbolMenuConfigAction() {
            super("Preferences");
        }

        public void actionPerformed(ActionEvent e) {
            new PreferencesDialog(MainMenuWindow.this).setVisible(true);
        }
    }

    public class QuizRecordAction extends AbstractAction {

        public QuizRecordAction() {
            super("Quiz Records");
        }

        public void actionPerformed(ActionEvent e) {
            new StatsWindow().setVisible(true);
            MainMenuWindow.this.setVisible(false);
        }
    }

    public class AboutAction extends AbstractAction {

        public AboutAction() {
            super("About");
        }

        public void actionPerformed(ActionEvent e) {
            AboutWindow about = new AboutWindow();
            about.setVisible(true);
        }
    }

    public class ExitAction extends AbstractAction {

        public ExitAction() {
            super("Exit");
        }

        public void actionPerformed(ActionEvent e) {
            System.exit(0);
        }
    }

    public class FishPanel extends JPanel {

        public FishPanel() {
            super();
        }

        @Override
        public void paintComponent(Graphics gn) {
            super.paintComponent(gn);
            Graphics2D g = (Graphics2D) gn;

            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            g.drawImage(bgImg, 0, 0, null);

            g.setFont(ThemeConstants.hugeFont);
            String renderString = "Ingatan";

            g.setPaint(new GradientPaint(5, 0, new Color(255, 255, 255, 0), 5, 13, new Color(255, 255, 255, 255)));
            g.fill(new Rectangle2D.Double(8, 0, g.getFontMetrics().stringWidth(renderString), 15));
            g.setPaint(new GradientPaint(5, 18, new Color(255, 255, 255, 255), 5, 35, new Color(255, 255, 255, 0)));
            g.fill(new Rectangle2D.Double(8, 15, g.getFontMetrics().stringWidth(renderString), 30));

            g.setPaint(Color.black);
            g.drawString(renderString, 8, 26);
        }
    }
}
