/*
 * OptionPane.java
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

/**
 * An interface for all Image Editor option panes.
 *
 * @author Thomas Everingham
 * @version 1.0
 */
public interface OptionPane {

    /**
     * update any relevant components within this option pane for the newly
     * selected colour. For example, the <code>LineOptionPane</code> redraws all
     * of its line preview icons in the new colour.
     * @param newFgColour the new foreground colour that has been selected
     * @param newBgColour the new background colour that has been selected
     */
    public void updateForNewColour(Color newFgColour, Color newBgColour);

    public void updateForAntialias(boolean antialias);

    public void rebuildSelf();
    
}
