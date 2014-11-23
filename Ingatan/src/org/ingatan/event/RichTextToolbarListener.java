/*
 * RichTextToolbarListener.java
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

/**
 * Listener interface for the RichTextToolbar.
 *
 * @author Thomas Everingham
 * @version 1.0
 */
public interface RichTextToolbarListener {

    /**
     * This event is fired when any of the toolbar buttons are clicked by the user.
     * You may determine which button has been clicked by comparing e.getEventID() with
     * one of the event fields held by RichTextToolbarEvent. e.g. <code>e.getEventID() == RichTextToolbarEvent.BOLD_BUTTON</code>
     * @param e the object describing this event.
     */
    public void buttonPressed(RichTextToolbarEvent e);

    /**
     * This event is fired when the font face, font size or font colour are changed.
     * You may determine which has been changed by comparing e.getEventID() with
     * one of the event fields held by RichTextToolbarEvent. e.g. <code>e.getEventID() == RichTextToolbarEvent.FONT_FAMILY_CHANGED</code>
     * @param e the object describing this event.
     */
    public void fontChanged(RichTextToolbarEvent e);
    
}
