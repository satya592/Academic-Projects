/*
    OpenChart2 Java Charting Library and Toolkit
    Copyright (C) 2005-2007 Approximatrix, LLC
    Copyright (C) 2001  Sebastian M�ller
    http://www.approximatrix.com

    This library is free software; you can redistribute it and/or
    modify it under the terms of the GNU Lesser General Public
    License as published by the Free Software Foundation; either
    version 2.1 of the License, or (at your option) any later version.

    This library is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
    Lesser General Public License for more details.

    You should have received a copy of the GNU Lesser General Public
    License along with this library; if not, write to the Free Software
    Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA

    BarChartRenderer.java
    Created on 31. October 2001, 12:32
 */

package com.approximatrix.charting.render;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.font.LineMetrics;
import java.awt.font.FontRenderContext;
import java.text.DecimalFormat;

import com.approximatrix.charting.coordsystem.CoordSystem;
import com.approximatrix.charting.model.ChartDataModel;
import com.approximatrix.charting.model.ChartDataModelConstraints;


/**
 * This renderer creates a BarChart. This will only work on a ChartDataModel
 * with non-numeric x-axis values, because I couldn't make any sense out of
 * a bar chart with a numeric x-axis like in a line chart.
 * @author  mueller
 * @version 1.0
 */
public class BarChartRenderer extends AbstractChartRenderer {
	protected DecimalFormat barTopFormat;
	protected float boxWidth = 1.0f;
	protected Font barTopFont;

	/** Creates new BarChartRenderer
	 * @param cs the ClassicCoordSystem used to translate into pixel space
	 * @param model the DataModel that should be rendered
	 */
	public BarChartRenderer(CoordSystem cs, ChartDataModel model) {
		super(cs, model);
	}

	/** Creates new BarChartRenderer
	 * @param cs the ClassicCoordSystem used to translate into pixel space
	 * @param model the DataModel that should be rendered
	 * @param topFormat if not null, the values that each box represents will be
	 * printed at the top of the box.
	 * @param boxWidth a value between 0.0 and 1.0 representing how much of the 
	 * alloted space each box should consume.  If 1.0 is passed, then each box will 
	 * completely fill it's allotted space, alternately I suggest you don't pass 0.0
	 */
	public BarChartRenderer(CoordSystem cs, ChartDataModel model, 
			DecimalFormat topFormat, Font topFont, float boxWidth) {
		super(cs, model);
		barTopFormat = topFormat;
		this.barTopFont = topFont;
		this.boxWidth = boxWidth;
	}


	/** Finally renders the Object in the Graphics object.
	 * @param g the Graphics2D object in which to render
	 */
	public boolean renderChart(Graphics2D g) {
		ChartDataModel m = getChartDataModel();
		ChartDataModelConstraints con = m.getChartDataModelConstraints(CoordSystem.FIRST_YAXIS);

		if(m.isColumnNumeric())
			return false;

		RowColorModel rcm = getRowColorModel();
		AffineTransform yaxis1 = getTransform(CoordSystem.FIRST_YAXIS);        
		AffineTransform yaxis2 = getTransform(CoordSystem.SECOND_YAXIS);

		int datasetcount = m.getDataSetNumber();

		Point2D point1 = yaxis1.transform(new Point2D.Float(con.getMinimumX().floatValue(), 
				con.getMaximumY().floatValue()),
				null);
		Point2D point2 = yaxis1.transform(new Point2D.Float(con.getMaximumX().floatValue(), 
				con.getMaximumY().floatValue()),
				null);
		Point2D value = point1;

		
		int dataunitwidth = 1;
		if(con.getMaximumX().intValue() != 0)
			dataunitwidth = (int)((point2.getX() - point1.getX()) / Math.abs(con.getMaximumX().doubleValue()));
		else if(con.getMinimumX().intValue() != 0)
			dataunitwidth = (int)((point2.getX() - point1.getX()) / Math.abs(con.getMinimumX().doubleValue()));
		
		int boxwidth = (int)(dataunitwidth * boxWidth / datasetcount);
		float margin = (float)(dataunitwidth * ((1.0 - boxWidth)/2f));

		Point2D pointzero;
		if(con.getMinimumY().floatValue() > 0)
			pointzero = yaxis1.transform(new Point2D.Float(con.getMinimumX().floatValue(),
					con.getMinimumY().floatValue()),
					null);
		else if(con.getMaximumY().floatValue() < 0)
			pointzero = yaxis1.transform(new Point2D.Float(con.getMinimumX().floatValue(),
					con.getMaximumY().floatValue()),
					null);
		else
			pointzero = yaxis1.transform(new Point2D.Float(con.getMinimumX().floatValue(),
					0f),
					null);

		FontRenderContext columnTopfrc = null;
		LineMetrics lm = null;
		DecimalFormat df = null;
		Rectangle2D fontRec = null;
		String columnTop = null;
		if (barTopFormat != null) {
			g.setFont(barTopFont);
			columnTopfrc = new FontRenderContext(null, false, false);
		}
				
		/* We paint the values starting at x-value "0".
		 * As we only render BarCharts for ChartDataModels with
		 * non-numeric x-axis values we don't have to read those
		 * values from the data model. You can look in 
		 * ObjectChartDataModel to see, how the x-axis bounds
		 * are defined: the minimum value is always 0, the maximum
		 * value is the amount of non-numeric x-axis values.
		 */
		 for(int i = 0; i < datasetcount && !this.getStopFlag(); i++) {
			 //System.out.println("** DataSet "+i);

			 for(int j = 0; j < m.getDataSetLength(i) && !this.getStopFlag(); j++) {
				 yaxis1.transform(new Point2D.Float((float)j, m.getValueAt(i, j).floatValue()),
						 value); 

				 Rectangle2D box = 
					 new Rectangle2D.Float((float)(value.getX() + margin + i*boxwidth),
							 (float)Math.min(value.getY(), pointzero.getY()),
							 (float)boxwidth,
							 (float)Math.abs(pointzero.getY() - value.getY()));

				 g.setColor(rcm.getColor(i));
				 g.fill(box);
				 g.setColor(Color.black);
				 g.draw(box);

				 if (barTopFormat != null) {
					 //get value for zero'th set, index j
					 columnTop = barTopFormat.format(model.getValueAt(i,j).doubleValue());
					 fontRec = barTopFont.getStringBounds(columnTop, columnTopfrc);
					 lm = barTopFont.getLineMetrics(columnTop, columnTopfrc);
					 g.drawString(columnTop, 
							 (float)(value.getX() + i*boxwidth + 
									 (boxwidth - fontRec.getWidth())/2) - lm.getLeading(),
									 (float)(Math.min(value.getY(), pointzero.getY())-lm.getDescent()));
				 }
				 
			 }

		 }
		 boolean completed = !this.getStopFlag();
		 this.resetStopFlag();
		 return completed;
	}//end render method    

}
