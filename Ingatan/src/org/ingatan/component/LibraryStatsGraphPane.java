/*
 * LibraryStatsGraphPane.java
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
 * Interactive panel with graphical representation of quiz history for the selected
 * libraries. It allows the user to select the libraries to display, and shows the
 * results for each time the library has been used in quiz, with time of quiz on the
 * x-axis.
 * 
 * @author Thomas Everingham
 * @version 1.0
 */
public class LibraryStatsGraphPane extends JPanel {

    /**
     * Creates a new <code>QuizHistoryWindow</code>.
     * @param returnToOnClose the window to return to once this window has closed.
     */
    public LibraryStatsGraphPane() {
        this.setSize(new Dimension(500, 500));
        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        /** Our labels for each bar */
        String[] names = {"Shea", "Flick", "Will"};
        /** The value associated with each label */
        double[] values = {10.0, 30.0, 60.0};
        Double[][] data = new Double[1][values.length];
        for (int i = 0; i < values.length; i++) {
            data[0][i] = new Double(values[i]);
        }
        String[] series_names = {"Ohmsfords"};
        ChartDataModel model = new ObjectChartDataModel(data, names, series_names);
        CoordSystem coord = new ClassicCoordSystem(model);
        ExtendedChartPanel chart_panel = new ExtendedChartPanel(model, "Disapproval Rating");
        chart_panel.setCoordSystem(coord);
        chart_panel.addChartRenderer(new BarChartRenderer(coord, model), 0);
        chart_panel.setPreferredSize(new Dimension(300, 300));
        chart_panel.setZoomMouseButton(MouseEvent.BUTTON1);
        chart_panel.enableZoom(true);
        this.add(chart_panel);
    }
}
