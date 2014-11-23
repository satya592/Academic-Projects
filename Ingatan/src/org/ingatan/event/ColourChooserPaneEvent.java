/*
 * ColourChooserPaneEvent.java
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

package org.ingatan.event;

import org.ingatan.component.colour.ColourChooserPane;
import java.awt.Color;

/**
 *
 * Encapsulates an event originating from a <code>ColourChooserPane</code> object.
 *
 * @author Thomas Everingham
 * @version 1.0
 */
public class ColourChooserPaneEvent {

    /**
     * A new colour was set after the user clicked a point within the image.
     */
    public final static int IMAGE_CLICK = 0;
    /**
     * A new colour was set while the user dragged the mouse over the image.
     */
    public final static int IMAGE_DRAG = 1;
    /**
     * A new colour was set when the user clicked a swatch square.
     */
    public final static int SWATCH = 2;
    /**
     * A new colour was set when the user manually altered the RGB values.
     */
    public final static int RGB_INFO = 3;


    /**
     * The colour chooser that fired this event.
     */
    private ColourChooserPane source;

    /**
     * The new selected colour.
     */
    private Color newColour;

    /**
     * Indicates how the colour was changed, whether by mouse click, drag or manual setting
     * of RBG values, etc.
     */
    private int changeSourceID;

    public ColourChooserPaneEvent(ColourChooserPane source, int changeSourceID, Color newSelectedColour)
    {
        this.source = source;
        this.changeSourceID = changeSourceID;
        this.newColour = newSelectedColour;
    }

    /**
     * Gets the <code>ColourChooserPane</code> object which generated this event.
     * @return the <code>ColourChooserPane</code> object which generated this event.
     */
    public ColourChooserPane getSource()
    {
        return source;
    }

    /**
     * Gets the new selected colour that has resulted from this event.
     * @return the new selected colour that has resulted from this event.
     */
    public Color getNewSelectedColour()
    {
        return newColour;
    }

    /**
     * Gets the changeSourceID associated with this event. This provides information
     * as to <i>how</i> the colour was changed. Compare this value with the change source
     * constants held by the <code>ColourChooserPaneEvent</code> class.
     * @return the changeSourceID associated with this event.
     */
    public int getChangeSourceID()
    {
        return changeSourceID;
    }

    /**
     * Gets the string description of the source of a colour change event.
     * @param sourceID the integer ID of the event.
     * @return the string description of the source of a colour change event.
     */
    public String getChangeSourceText(int sourceID)
    {
        switch (sourceID)
        {
            case 0:
                return "Image Was Clicked";
            case 1:
                return "Mouse Was Dragged On Image";
            case 2:
                return "Swatch Colour Was Chosen";
            case 3:
                return "RGB Values Were Set";
            default:
                return "Unknown ID";
        }
    }

    /**
     * Gets the string description of the source of this colour change event.
     * @return the string description of the source of this colour change event.
     */
    public String getChangeSourceText()
    {
        return getChangeSourceText(changeSourceID);
    }
}
