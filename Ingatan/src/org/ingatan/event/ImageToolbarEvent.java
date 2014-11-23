/*
 * ImageToolbarEvent.java
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

import org.ingatan.component.image.ImageToolbar;

/**
 * Encapsulates an event originating from instances of the <code>ImageToolbar</code> class.
 *
 * @author Thomas Everingham
 * @version 1.0
 */
public class ImageToolbarEvent {

    /**
     * The rectangular selection tool was clicked.
     */
    public static final int SELECT_RECT_BUTTON = 0;
    /**
     * The oval selection tool was clicked.
     */
    public static final int SELECT_OVAL_BUTTON = 1;
    /**
     * The polygon selection tool was clicked.
     */
    public static final int SELECT_POLYGON_BUTTON = 2;
    /**
     * The crop button was clicked.
     */
    public static final int CROP_BUTTON = 3;
    /**
     * The scale button was clicked.
     */
    public static final int SCALE_BUTTON = 4;
    /**
     * The rotate button was clicked.
     */
    public static final int ROTATE_BUTTON = 5;
    /**
     * The flip vertical button was clicked.
     */
    public static final int FLIP_VERTICAL_BUTTON = 6;
    /**
     * The flip horizontal button was clicked.
     */
    public static final int FLIP_HORIZONTAL_BUTTON = 7;
    /**
     * The draw line button was clicked.
     */
    public static final int DRAW_LINE_BUTTON = 8;
    /**
     * The draw line button was clicked.
     */
    public static final int DRAW_ARROW_BUTTON = 9;
    /**
     * The draw rectangle button was clicked.
     */
    public static final int DRAW_RECTANGLE_BUTTON = 10;
    /**
     * The draw circle button was clicked.
     */
    public static final int DRAW_CIRCLE_BUTTON = 11;
    /**
     * The draw rounded rectangle button was clicked.
     */
    public static final int DRAW_ROUNDED_RECT_BUTTON = 12;
    /**
     * The draw polygon button was clicked.
     */
    public static final int DRAW_POLYGON_BUTTON = 13;
    /**
     * The stamp button was clicked.
     */
    public static final int STAMP_BUTTON = 14;
    /**
     * The bucket fill button was clicked.
     */
    public static final int BUCKET_FILL_BUTTON = 15;
    /**
     * The eraser button was clicked.
     */
    public static final int ERASER_BUTTON = 16;
    /**
     * The pencil button was clicked.
     */
    public static final int PENCIL_BUTTON = 17;
    /**
     * The eyedropper button was clicked.
     */
    public static final int EYEDROPPER_BUTTON = 18;
    /**
     * The add text button was clicked.
     */
    public static final int TEXT_BUTTON = 19;
    /**
     * The chemistry draw utilities button was clicked.
     */
    public static final int CHEM_BUTTON = 20;
    /**
     * The math draw utilities button was clicked.
     */
    public static final int MATH_BUTTON = 21;
    /**
     * The brightness and contrast button was clicked.
     */
    public static final int BRIGHTNESS_CONTRAST_BUTTON = 22;
    
    /**
     * The colour chooser that fired this event.
     */
    private ImageToolbar source;
    /**
     * The new selected colour.
     */
    private int eventID;

    public ImageToolbarEvent(ImageToolbar source, int eventID)
    {
        this.source = source;
        this.eventID = eventID;
    }

    /**
     * Gets the <code>ImageToolbar</code> instance at which this event originated.
     * @return the <code>ImageToolbar</code> instance at which this event originated.
     */
    public ImageToolbar getSource() {
        return source;
    }

    /**
     * Gets the eventID for this event. This is used to identify which toolbar button was
     * clicked; compare the returned eventID with the fields of this class.
     * @return the eventID for this event.
     */
    public int getEventID() {
        return eventID;
    }

    /**
     * Gets the human readable description of this event.
     * @return the human readable description of this event.
     */
    public String getEventDescription() {
        return getEventIDDescription(eventID);
    }

    /**
     * Gets the human readable description of the event corresponding to the specified eventID.
     * @param eventID the eventID of interest.
     * @return the human readable description of the event corresponding to the specified eventID.
     */
    public String getEventIDDescription(int eventID) {
        switch (eventID) {
            case 0:
                return "Rectangular Selection Button Pressed";
            case 1:
                return "Oval Selection Button Pressed";
            case 2:
                return "Polygon Selection Button Pressed";
            case 3:
                return "Crop Button Pressed";
            case 4:
                return "Scale Button Pressed";
            case 5:
                return "Rotate Button Pressed";
            case 6:
                return "Draw Line Button Pressed";
            case 7:
                return "Draw Rectangle Button Pressed";
            case 8:
                return "Draw Oval Button Pressed";
            case 9:
                return "Draw Rounded Rectangle Button Pressed";
            case 10:
                return "Draw Polygon Button Pressed";
            case 11:
                return "Stamp Button Pressed";
            case 12:
                return "Bucket Fill Button Pressed";
            case 13:
                return "Eraser Button Pressed";
            case 14:
                return "Pencil Button Pressed";
            case 15:
                return "Add Text Button Pressed";
            case 16:
                return "Chemistry Drawing Utilities Button Pressed";
            case 17:
                return "Math Drawing Utilities Button Pressed";
            case 18:
                return "Brightness and Contrast Button Pressed";
            case 19:
                return "Pencil Button Pressed";
            default:
                return "Could not recognise that eventID.";
        }
    }
}
