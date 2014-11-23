/*
 * RewardsPane.java
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

import com.approximatrix.charting.coordsystem.ClassicCoordSystem;
import com.approximatrix.charting.coordsystem.CoordSystem;
import com.approximatrix.charting.model.ChartDataModel;
import com.approximatrix.charting.model.ObjectChartDataModel;
import com.approximatrix.charting.render.BarChartRenderer;
import com.approximatrix.charting.swing.ExtendedChartPanel;
import java.awt.Dimension;
import java.awt.event.MouseEvent;
import javax.swing.BoxLayout;
import javax.swing.JPanel;

/**
 * A little store with icons (+caption) that cost a particular number of points. Several
 * default icons come bundled with Ingatan, with the user able to add new ones (these are
 * added to the collections folder under new category 'rewards'). The user can add rewards
 * such as "Bottle of Wine", or "Chocolate Bar", etc. and set a number of points required
 * to 'buy' that reward. This is a motivational tool that the user can either enjoy or ignore.
 * 
 * @author Thomas Everingham
 * @version 1.0
 */
public class RewardsPane extends JPanel {

    /**
     * Creates a new <code>QuizHistoryWindow</code>.
     * @param returnToOnClose the window to return to once this window has closed.
     */
    public RewardsPane() {
        this.setSize(new Dimension(500, 500));
        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        
    }
}
