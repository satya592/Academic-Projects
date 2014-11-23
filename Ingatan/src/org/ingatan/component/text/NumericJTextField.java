/*
 * NumericJTextField.java
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

package org.ingatan.component.text;

import javax.swing.JTextField;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.PlainDocument;

/**
 * A number only extension of the JTextField. Does not allow negative numbers.
 *
 * @author Thomas Everingham
 * @version 1.0
 */
public class NumericJTextField extends JTextField {

    public NumericJTextField(int initVal) {
        super("" + initVal);
    }

    @Override
    protected Document createDefaultModel() {
        return new NumericTextDocument();
    }

    @Override
    public boolean isValid() {
        try {
            Integer.parseInt(this.getText());
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
        catch (NullPointerException e)
        {
            return false;
        }
    }

    /**
     * Equivalent to Integer.parseInt(this.getText());
     * @return the integer value of the text in this field; returns 0 if field is empty.
     */
    public int getValue() {
        if (getText().trim().isEmpty())
            return 0;
        try {
            return Integer.parseInt(getText());
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    public class NumericTextDocument extends PlainDocument {

        @Override
        public void insertString(int offs, String str, AttributeSet a) throws BadLocationException {
            if (str == null) {
                return;
            }
            String oldString = getText(0, getLength());
            String newString = oldString.substring(0, offs) + str
                    + oldString.substring(offs);
            if (newString.contains("-"))
                return;
            try {
                Integer.parseInt(newString + "0");
                super.insertString(offs, str, a);
            } catch (NumberFormatException e) {
            }
        }
    }
}
