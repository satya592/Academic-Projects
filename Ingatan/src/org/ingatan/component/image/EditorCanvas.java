/*
 * EditorCanvas.java
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

package org.ingatan.component.image;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import javax.swing.*;

/**
 * This extension of JPanel performs the following:
 * <ul>
 *     <li>Allows the user to write to an encapsualted <code>BufferedImage</code> using various tools</li>
 *     <li>Renders the <code>BufferedImage</code> to itself.</li>
 *     <li>Keeps an undo-redo record in the form of previous buffered images.</li>
 *     <li>Keeps track of the current selection (if any) that is made.</li>
 * </ul>
 * @author Thomas Everingham
 * @version 1.0
 */
public class EditorCanvas extends JPanel {

    /**
     * Length of the undo history.
     */
    protected final int undoHistoryLength = 5;
    /**
     * The actual canvas image.
     */
    protected BufferedImage drawing;
    /**
     * The image onto which all previews are temporarily drawn.
     */
    protected BufferedImage glassPane;
    /**
     * The array of saved image states which the user may revert to
     */
    protected BufferedImage[] undos = new BufferedImage[undoHistoryLength];
    /**
     * An array of image states which the user has reverted from
     */
    protected BufferedImage[] redos = new BufferedImage[undoHistoryLength];

    /**
     * Multiplier for image width and height upon repaint, and divisor for mouse coordinates of MouseEvents.
     */
    protected float zoomFactor = 1.0f;

    public EditorCanvas() {

        this.setLayout(null);
        drawing = new BufferedImage(500, 500, BufferedImage.TYPE_INT_ARGB);
        Graphics g = drawing.createGraphics();
        g.setColor(Color.white);
        g.fillRect(0, 0, drawing.getWidth(), drawing.getHeight());

        glassPane = new BufferedImage(500, 500, BufferedImage.TYPE_INT_ARGB);
        clearGlassPane();
    }

    /**
     * Gets the canvas onto which the image is drawn.
     * @return the canvas onto which the image is drawn.
     */
    public BufferedImage getCanvasBufferedImage() {
        return drawing;
    }

    /**
     * Sets the canvas onto which the image is drawn.
     * @param newImg the canvas onto which the image will be drawn.
     */
    public void setCanvasBufferedImage(BufferedImage newImg) {
        drawing = newImg;
    }

    /**
     * Clears the glass pane of all construction lines that have been drawn to it.
     */
    public void clearGlassPane() {
        Graphics g = glassPane.createGraphics();
        Graphics2D g2d = (Graphics2D) g;
        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.CLEAR, 0.0f));
        g2d.fillRect(0, 0, glassPane.getWidth(), glassPane.getHeight());
    }

    /**
     * Gets the glass pane <code>BufferedImage</code> which sits over the top of the canvas.
     * @return the glass pane <code>BufferedImage</code> which sits over the top of the canvas.
     */
    public BufferedImage getGlassPaneBufferedImage() {
        return glassPane;
    }

    /**
     * Sets the glass pane <code>BufferedImage</code> which sits over the top of the canvas.
     * @param newImg the glass pane to be used.
     */
    public void setGlassPaneBufferedImage(BufferedImage newImg) {
        glassPane = newImg;
    }

    /**
     * The canvas will multiply its size and intercept and alter MouseEvents, based
     * on the zoom factor set here. The zoom factor is a float which multiplies the image
     * width and height upon repaint, and alters any mouse event coordinates correspondingly.
     * A zoom factor of 1 will give the actual size of the image, 2 will double the size of the
     * image, etc.
     * @param zoomFactor a float which multiplies the image width and height upon repaint. Must be a value greater
     * than 0 and less than 10.
     */
    public void setZoomFactor(float zoomFactor)
    {
        if (zoomFactor <= 0.0)
            zoomFactor = 0.1f;
        if (zoomFactor > 10.0)
            zoomFactor = 10.0f;

        this.zoomFactor = zoomFactor;

        this.repaint();
        this.setPreferredSize(new Dimension((int) (drawing.getWidth()*zoomFactor), (int) (drawing.getHeight()*zoomFactor)));
    }

    /**
     * Gets the float that multiplies image width and height upon repaint, and
     * is the divisor of the coordinates of any mouse input event.
     * @return the zoom factor.
     */
    public float getZoomFactor()
    {
        return zoomFactor;
    }

    public void setUndoPoint() {
        //reset zoom factor first, restore at the end
        float zf = getZoomFactor();
        setZoomFactor(1.0f);

        //move all of the current undo points 'down' to make a slot
        //at the start of the array.
        for (int i = undos.length - 2; i >= 0; i--) {
            undos[i + 1] = undos[i];
        }
        //add current buffered image as a new undo point
        undos[0] = new BufferedImage(drawing.getWidth(), drawing.getHeight(), BufferedImage.TYPE_INT_ARGB);
        Graphics g = undos[0].createGraphics();
        g.drawImage(drawing, 0, 0, drawing.getWidth(), drawing.getHeight(), this);

        //restore zoom factor
        setZoomFactor(zf);
    }

    public void undo() {
        //reset zoom factor first, restore at the end
        float zf = getZoomFactor();
        setZoomFactor(1.0f);


        if (undos[0] == null) {
            return;
        }
        //move all of the current redo points 'down' to make a slot
        //at the start of the array.
        for (int i = redos.length - 2; i >= 0; i--) {
            redos[i + 1] = redos[i];
        }
        //set the current state as a redo point.
        redos[0] = new BufferedImage(drawing.getWidth(), drawing.getHeight(), BufferedImage.TYPE_INT_ARGB);
        Graphics g = redos[0].createGraphics();
        g.drawImage(drawing, 0, 0, drawing.getWidth(), drawing.getHeight(), this);

        //set the current image to the most recent undo point
        glassPane = new BufferedImage(undos[0].getWidth(), undos[0].getHeight(), BufferedImage.TYPE_INT_ARGB);
        drawing = new BufferedImage(undos[0].getWidth(), undos[0].getHeight(), BufferedImage.TYPE_INT_ARGB);
        this.setPreferredSize(new Dimension(undos[0].getWidth(), undos[0].getHeight()));
        g = drawing.createGraphics();
        g.drawImage(undos[0], 0, 0, undos[0].getWidth(), undos[0].getHeight(), this);

        //move all undo points 'up one'.. unused points will now be 'null'.
        for (int i = 0; i < undos.length - 1; i++) {
            undos[i] = undos[i + 1];
        }
        undos[undos.length - 1] = null;

        //restore zoom factor
        setZoomFactor(zf);

        repaint();
    }

    public void redo() {
        //reset zoom factor first, restore at the end
        float zf = getZoomFactor();
        setZoomFactor(1.0f);

        if (redos[0] == null) {
            return;
        }
        //save current state as an undo point.
        setUndoPoint();
        //set the image to the most recent redo point
        drawing = new BufferedImage(redos[0].getWidth(), redos[0].getHeight(), BufferedImage.TYPE_INT_ARGB);
        glassPane = new BufferedImage(redos[0].getWidth(), redos[0].getHeight(), BufferedImage.TYPE_INT_ARGB);
        this.setPreferredSize(new Dimension(redos[0].getWidth(), redos[0].getHeight()));
        Graphics g = drawing.createGraphics();
        g.drawImage(redos[0], 0, 0, redos[0].getWidth(), redos[0].getHeight(), this);

        //move the redo points up to get rid of redos[0]
        for (int i = 0; i < redos.length - 1; i++) {
            redos[i] = redos[i + 1];
        }
        redos[redos.length - 1] = null;

        //restore zoom factor
        setZoomFactor(zf);

        repaint();
    }

    /**
     * Clear the undo and redo history for this editor canvas.
     */
    public void clearUndoHistory() {
        undos = new BufferedImage[undoHistoryLength];
        redos = new BufferedImage[undoHistoryLength];
    }

    @Override
    public void setPreferredSize(Dimension size)
    {
        int width = (int) (size.width / zoomFactor);
        int height = (int) (size.height / zoomFactor);

        super.setPreferredSize(size);
        super.setSize(size);
        super.setMinimumSize(size);

        BufferedImage newDrawing = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = (Graphics2D) newDrawing.createGraphics();
        g2d.setColor(Color.white);
        g2d.fillRect(0, 0, width, height);
        if ((width <= drawing.getWidth()) && (height <= drawing.getHeight()))
            g2d.drawImage(drawing.getSubimage(0, 0, width, height), 0, 0, this);
        else if ((width <= drawing.getWidth()) && (height > drawing.getHeight()))
            g2d.drawImage(drawing.getSubimage(0, 0, width, drawing.getHeight()), 0, 0, this);
        else if ((width > drawing.getWidth()) && (height > drawing.getHeight()))
            g2d.drawImage(drawing.getSubimage(0, 0, drawing.getWidth(), drawing.getHeight()), 0, 0, this);
        else if ((width > drawing.getWidth()) && (height <= drawing.getHeight()))
            g2d.drawImage(drawing.getSubimage(0, 0, drawing.getWidth(), height), 0, 0, this);

        glassPane = new BufferedImage(width,height,BufferedImage.TYPE_INT_ARGB);
        drawing = newDrawing;
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(drawing, 0, 0, (int) (drawing.getWidth()*zoomFactor), (int) (drawing.getHeight()*zoomFactor), this);
        g.drawImage(glassPane, 0, 0, (int) (glassPane.getWidth()*zoomFactor), (int) (glassPane.getHeight()*zoomFactor), this);
    }

    @Override
    protected void processMouseEvent(MouseEvent e) {
        MouseEvent newE = new MouseEvent(e.getComponent(), e.getID(), e.getWhen(), e.getModifiers(), (int) (e.getX()/zoomFactor), (int) (e.getY()/zoomFactor), e.getClickCount(), e.isPopupTrigger(), e.getButton()) ;
        super.processMouseEvent(newE);
    }

    @Override
    protected void processMouseMotionEvent(MouseEvent e) {
        MouseEvent newE = new MouseEvent(e.getComponent(), e.getID(), e.getWhen(), e.getModifiers(), (int) (e.getX()/zoomFactor), (int) (e.getY()/zoomFactor), e.getClickCount(), e.isPopupTrigger(), e.getButton()) ;
        super.processMouseMotionEvent(newE);
    }

}
