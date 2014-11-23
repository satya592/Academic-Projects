/*
 * ThemeConstants.java
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

package org.ingatan;

import java.awt.Color;
import java.awt.Font;
import javax.swing.JPanel;

/**
 * Encapsulates common colours used throughout the program.
 *
 * @author Thomas Everingham
 * @version 1.0
 */
public abstract class ThemeConstants {
    /**
     * Deep blue/purple
     */
    public static final Color borderUnselected = new Color(30, 101, 114);
    /**
     * Lighter blue colour, gray-ish.
     */
    public static final Color backgroundUnselected = new Color(225, 236, 236);
    /**
     * Deep blue/purple
     */
    public static final Color borderSelected = new Color(35, 77, 90);
    /**
     * Lighter blue colour, gray-ish.
     */
    public static final Color backgroundSelected = new Color(235, 223, 223);
    /**
     * Deep blue/purple
     */
    public static final Color borderSelectedHover = new Color(60, 87, 100);
    /**
     * Lighter blue colour, gray-ish.
     */
    public static final Color backgroundSelectedHover = new Color(237, 230, 230);
    /**
     * Lighter blue/purple, but still as dark as a border should be.
     * This is used for mouse-over of unselected borderUnselected
     */
    public static final Color borderUnselectedHover = new Color(46, 117, 130);
    /**
     * Lighter again, blue colour, gray-ish. This is used for mouse-over of
     * unselected backgrounds.
     */
    public static final Color backgroundUnselectedHover = new Color(230, 243, 243);
    /**
     * Dark grey colour used for headings and text.
     */
    public static final Color textColour = new Color(70, 70, 70);
    /**
     * The green used to indicate that the user acheived more than 50% for a question
     */
    public static final Color quizPassGreen = new Color(0, 204, 0);
    /**
     * The red colour used to indicate the user received a grade of less than 50% for a question
     */
    public static final Color quizFailRed = new Color(204, 0, 0);
    /**
     * The orange colour used to indicate a correct answer that is not the best answer
     */
    public static final Color alrightAnswerOrange = new Color(255, 165, 0);
    /**
     * The red/orange colour used for the 'combo' font in quiz time.
     */
    public static final Color comboColour = new Color(255, 60, 0);
    /**
     * Nicer, smaller font based on the default for the JPanel.
     */
    public static final Font niceFont = new Font(new JPanel().getFont().getFamily(), Font.PLAIN, 10);
    /**
     * Tiny font based on the default for the JPanel.
     */
    public static final Font tinyFont = new Font(new JPanel().getFont().getFamily(), Font.PLAIN, 8);
    /**
     * Large font for stylised background painting.
     */
    public static final Font hugeFont = new Font(new JPanel().getFont().getFamily(), Font.PLAIN, 32);
    /**
     * Font used for the table cell editors.
     */
    public static final Font tableCellEditorFont = new Font(new JPanel().getFont().getFamily(), Font.PLAIN, 14);
    /**
     * Height of rows in DataTables.
     */
    public static final int tableRowHeights = 20;
}
