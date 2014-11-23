/*
    OpenChart2 Java Charting Library and Toolkit
    Copyright (C) 2005-2008 Approximatrix, LLC
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

    ImageRenderer.java
*/

package com.approximatrix.charting.render;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;

import com.approximatrix.charting.coordsystem.CoordSystem;
import com.approximatrix.charting.model.IntegratedImageModel;

/** Renderer for creating dot plots.  Renderer is relatively simple
 * because the model itself holds an image of the data.  The renderer
 * buffers an appropriate-for-drawing image, which is only recreated
 * if the coordinate system transform changes.
 * 
 * @author armstrong
 */
public class ImageRenderer extends AbstractChartRenderer {

	/** Buffered version of the coordinate transform used for painting */
	private AffineTransform last = null;
	
	/** BufferedImage object used for plotting */
	private Image renderImage = null;

	/** The image model for rendering */
	private IntegratedImageModel dpmodel = null;
	
	/** The hint for scaling the viewable image to fit the bounds */
	private int scaleHints = Image.SCALE_FAST;
	
	/** Creates the renderer from the coordinate system and the model
	 * 
	 * @param cs the coordinate system
	 * @param model the model for rendering
	 */
	public ImageRenderer(CoordSystem cs, IntegratedImageModel model) {
		super(cs,model);
		this.coord = cs;
		this.model = model;
		this.dpmodel = model;
		this.renderImage = null;
	}

	/** Renders the image.  If the transform has changed, the buffer is refreshed. */
	public boolean renderChart(Graphics2D g) {
		if(last != coord.getTransform(CoordSystem.FIRST_YAXIS) || renderImage == null) {
			try {
				renderImage = dpmodel.getViewableImage(); 
			} catch(NullPointerException npe) {
				System.err.println("Error in scaling/displaying image...");
				npe.printStackTrace();
				return false;
			}
		}
		
		Rectangle rect = coord.getInnerBounds();
		g.drawImage(renderImage.getScaledInstance(rect.width, rect.height, this.scaleHints),rect.x, rect.y, null);
		return true;
	}

	/** Sets the image scaling hint
	 * 
	 * @param value a valid scaling hint
	 * @see java.awt.Image
	 */
	public void setScaleHint(int value) {
		this.scaleHints = value;
	}
}
