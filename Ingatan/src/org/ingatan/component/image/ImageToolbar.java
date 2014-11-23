/*
 * ImageToolbar.java
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

import org.ingatan.event.ImageToolbarEvent;
import org.ingatan.event.ImageToolbarListener;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Arrays;
import javax.swing.*;
import javax.swing.border.Border;

/**
 * Provides a toolbar for the image editor. Buttons events can be accessed by implementing
 * an <code>ImageToolbarListener</code> and comparing the EventID with the fields of <code>ImageToolbarEvent</code>.
 * @author Thomas Everingham
 * @version 1.0
 */
public class ImageToolbar extends JPanel {
    /**
     * The select regtangular area tool.
     */
    public static final int SELECT_RECT = 0;
    /**
     * The select oval area tool.
     */
    public static final int SELECT_OVAL = 1;
    /**
     * The select polygon tool.
     */
    public static final int SELECT_POLY = 2;
    /**
     * The crop tool
     */
    public static final int CROP = 3;
    /**
     * The scale tool.
     */
    public static final int SCALE = 4;
    /**
     * The rotate tool.
     */
    public static final int ROTATE = 5;
    /**
     * The flip vertical tool.
     */
    public static final int FLIP_VERTICAL = 6;
    /**
     * The flip horizontal tool.
     */
    public static final int FLIP_HORIZONTAL = 7;
    /**
     * The draw line tool.
     */
    public static final int DRAW_LINE = 8;
    /**
     * The draw arrow tool.
     */
    public static final int DRAW_ARROW = 9;
    /**
     * The draw rectangle tool.
     */
    public static final int DRAW_RECT = 10;
    /**
     * The draw oval tool.
     */
    public static final int DRAW_OVAL = 11;
    /**
     * The draw rounded rectangle tool.
     */
    public static final int DRAW_ROUNDED_RECT = 12;
    /**
     * The draw polygon tool.
     */
    public static final int DRAW_POLY = 13;
    /**
     * The stamp tool.
     */
    public static final int STAMP = 14;
    /**
     * The bucket fill tool.
     */
    public static final int BUCKET_FILL = 15;
    /**
     * The eraser tool.
     */
    public static final int ERASER = 16;
    /**
     * The pencil tool.
     */
    public static final int PENCIL = 17;
    /**
     * The eyedropper tool.
     */
    public static final int EYEDROPPER = 18;
    /**
     * The add text tool.
     */
    public static final int TEXT = 19;
    /**
     * The math TeX tool
     */
    public static final int MATH = 21;
    /**
     * The brightness and contrast tool.
     */
    public static final int BRIGHTNESS_CONTRAST = 22;

    /**
     * The rectangular selection button.
     */
    private JButton btnSelectRect;
    /**
     * The oval selection button.
     */
    private JButton btnSelectOval;
    /**
     * The polygon selection button.
     */
    private JButton btnSelectPolyform;
    /**
     * The crop button.
     */
    private JButton btnCrop;
    /**
     * The resize button.
     */
    private JButton btnResize;
    /**
     * The rotate button.
     */
    private JButton btnRotate;
    /**
     * The flip button.
     */
    private JButton btnFlipVertical;
    /**
     * The flip button.
     */
    private JButton btnFlipHorizontal;
    /**
     * The draw line button.
     */
    private JButton btnLine;
    /**
     * The draw arrow button.
     */
    private JButton btnArrow;
    /**
     * The draw rectangle button.
     */
    private JButton btnRectangle;
    /**
     * The draw oval button.
     */
    private JButton btnOval;
    /**
     * The draw rounded rectangle button.
     */
    private JButton btnRoundedRect;
    /**
     * The draw polygon button.
     */
    private JButton btnPolyform;
    /**
     * The stamp button.
     */
    private JButton btnStamp;
    /**
     * The bucket fill button.
     */
    private JButton btnBucketFill;
    /**
     * The eraser button.
     */
    private JButton btnEraser;
    /**
     * The pencil button.
     */
    private JButton btnPencil;
    /**
     * The eyedropper button.
     */
    private JButton btnEyedropper;
    /**
     * The add text button.
     */
    private JButton btnText;
    /**
     * The math drawing utilities button.
     */
    private JButton btnMath;
    /**
     * The brightness and contrast button.
     */
    private JButton btnBrightnessContrast;

    /**
     * The mouse listener for the toolbar. This passes all clicks on to any
     * <code>ToolbarEventListeners</code> that have been added.
     */
    private ToolbarMouseListener toolbarMouseListener = new ToolbarMouseListener();
    /**
     * The listener associated with this toolbar.
     */
    private ImageToolbarListener[] toolbarListeners = new ImageToolbarListener[0];
    /**
     * Colour of the border of the panel
     */
    private Color borders = new Color(26, 97, 110);

    /**
     * Border for a selected button.
     */
    private Border selectedBorder = BorderFactory.createLineBorder(new Color(26, 97, 110, 50),3);
    /**
     * Border for a deselected button.
     */
    private Border deselectedBorder = BorderFactory.createEmptyBorder(3,3,3,3);

    /**
     * The current tool.
     */
    protected int currentTool = ImageToolbar.SELECT_RECT;

    public ImageToolbar()
    {
        setUpGUI();
    }

    /**
     * Set up the toolbar buttons.
     */
    private void setUpGUI()
    {
        this.setLayout(new FlowLayout(FlowLayout.LEFT,0,0));

        this.setBorder(BorderFactory.createMatteBorder(1, 1, 1, 2, borders)); // the canvas touches the bottom; border appears 3
        btnSelectRect = createButton("/resources/icons/image/select_rectangle.png", "Select a rectangular region");
        btnSelectOval = createButton("/resources/icons/image/select_oval.png","Select an oval region");
        btnSelectPolyform = createButton("/resources/icons/image/select_polyform.png","Select a polygon region");
        btnCrop = createButton("/resources/icons/image/crop.png","Crop the image to the selected region");
        btnResize = createButton("/resources/icons/image/shape_handles.png","Resize the image or selected region");
        btnFlipVertical = createButton("/resources/icons/image/shape_flip_vertical.png","Flip the image or selected region vertically");
        btnFlipHorizontal = createButton("/resources/icons/image/shape_flip_horizontal.png","Flip the image or selected region horizontally");
        btnRotate = createButton("/resources/icons/image/arrow_rotate_anticlockwise.png","Rotate the image or selected region");
        btnLine = createButton("/resources/icons/image/line.png","Draw a line");
        btnArrow = createButton("/resources/icons/image/arrow.png","Draw an arrow");
        btnRectangle = createButton("/resources/icons/image/shape_square.png","Draw a rectangle");
        btnOval = createButton("/resources/icons/image/shape_circle.png","Draw an oval");
        btnRoundedRect = createButton("/resources/icons/image/shape_roundedRectangle.png","Draw a rounded rectangle");
        btnPolyform = createButton("/resources/icons/image/shape_polyform.png","Draw a polygon");
        btnStamp = createButton("/resources/icons/image/stamp.png","Stamp a preset onto your image");
        btnBucketFill = createButton("/resources/icons/image/paintcan.png","Bucket fill an area");
        btnEraser = createButton("/resources/icons/image/eraser.png","Erase part of your image");
        btnPencil = createButton("/resources/icons/image/pencil.png","Pencil; a free hand drawing tool");
        btnEyedropper = createButton("/resources/icons/image/eyedropper.png","Choose a colour from your image.");
        btnText = createButton("/resources/icons/image/font_add.png","Add text to your image");
        btnMath = createButton("/resources/icons/image/math.png","Open the math drawing utilities");
        btnBrightnessContrast = createButton("/resources/icons/image/contrast.png","Adjust the brightness and contrast of your image or the current selection");
    }

    /**
     * Sets the currently selected tool. Use the fields of this class to specify.
     * @param currentTool the tool that should be set as the current tool.
     */
    public void setCurrentTool(int currentTool)
    {
        this.currentTool = currentTool;
        this.setOnlyButtonSelected(currentTool);
    }

    /**
     * Gets the 'current tool'. This is the tool most recently set as the current tool.
     * Compare with the fields of this class.
     * @return the current tool.
     */
    public int getCurrentTool()
    {
        return currentTool;
    }


    /**
     * Adds a <code>ImageToolbarListener</code> to this <code>ImageToolbar</code> instance.
     * @param listener the <code>ImageToolbarListener</code> to add.
     */
    public void addImageToolbarListener(ImageToolbarListener listener) {
        if (toolbarListeners.length == 0) {
            toolbarListeners = new ImageToolbarListener[]{listener};
        } else {
            ImageToolbarListener[] temp = new ImageToolbarListener[toolbarListeners.length + 1];
            System.arraycopy(toolbarListeners, 0, temp, 0, toolbarListeners.length);
            temp[toolbarListeners.length] = listener;
            toolbarListeners = temp;
        }
    }

    /**
     * Removes a <code>ImageToolbarListener</code> from this <code>ImageToolbar</code> instance.
     * @param listener the <code>ImageToolbarListener</code> to remove.
     * @return true if the listener could be found and removed, and false otherwise.
     */
    public boolean removeImageToolbarListener(ImageToolbarListener listener) {
        if (toolbarListeners.length == 0) {
            return false;
        }
        if (toolbarListeners.length == 1) {
            if (toolbarListeners[0].equals(listener)) {
                toolbarListeners = new ImageToolbarListener[0];
                return true;
            } else {
                return false;
            }
        }

        int index = -1;
        //get the index
        for (int i = 0; i < toolbarListeners.length; i++) {
            if (toolbarListeners[i].equals(listener)) {
                index = i;
                break;
            }
        }

        //if index is -1, we have not found the listener
        if (index == -1) {
            return false;
        }

        //otherwise, get rid of the listener
        ImageToolbarListener[] temp = new ImageToolbarListener[toolbarListeners.length - 1];
        if (index == 0) {
            System.arraycopy(toolbarListeners, 1, temp, 0, toolbarListeners.length - 1);
            toolbarListeners = temp;
            return true;
        } else if (index == toolbarListeners.length - 1) {
            System.arraycopy(toolbarListeners, 0, temp, 0, toolbarListeners.length - 1);
            toolbarListeners = temp;
            return true;
        } else //the index is not on the edge of the array
        {
            System.arraycopy(toolbarListeners, 0, temp, 0, index);
            System.arraycopy(toolbarListeners, index + 1, temp, index, toolbarListeners.length - index - 1);
            toolbarListeners = temp;
            return true;
        }
    }


    public void fireButtonPress(int button)
    {
        for (int i = 0; i < toolbarListeners.length; i++)
        {
            toolbarListeners[i].buttonPress(new ImageToolbarEvent(this, button));
        }
    }



    /**
     * Instantiates a JButton and sets required properties. Loads the specified resource
     * as the button's icon.
     *
     * @param resourceName the image resource used as this button's icon.
     * @return the created JButton.
     */
    private JButton createButton(String resourceName, String tooltip) {
        JButton btn = new JButton();
        btn.addMouseListener(toolbarMouseListener);
        btn.setToolTipText(tooltip);
        btn.setIcon(new ImageIcon(ImageToolbar.class.getResource(resourceName)));
        btn.setBorder(BorderFactory.createEmptyBorder(3, 3, 3, 3));
        btn.setSize(btn.getIcon().getIconWidth(), btn.getIcon().getIconHeight());
        this.add(btn);
        return btn;

    }
    /**
     * Sets which buttons should have a 'selected' highlight drawn to them.
     * @param selected a boolean of length 23. Each member of this array
     *        represents the selection state of each toolbar button <i>as it occurs on the toolbar</i>.
     *        This means that the boolean at index 0 represents the state of the rectangular selection button,
     *        and index 1 the oval selection button, etc.
     */
    public void setButtonSelection(boolean[] selected)
    {
        if (selected.length < 23)
            throw new IndexOutOfBoundsException("Only " + selected.length + " of 23 required booleans passed.");
        else if (selected == null)
            throw new NullPointerException("Button selection boolean array == null");

        if (selected[0]) btnSelectRect.setBorder(selectedBorder); else btnSelectRect.setBorder(deselectedBorder);
        if (selected[1]) btnSelectOval.setBorder(selectedBorder); else btnSelectOval.setBorder(deselectedBorder);
        if (selected[2]) btnSelectPolyform.setBorder(selectedBorder); else btnSelectPolyform.setBorder(deselectedBorder);
        if (selected[3]) btnCrop.setBorder(selectedBorder); else btnCrop.setBorder(deselectedBorder);
        if (selected[4]) btnResize.setBorder(selectedBorder); else btnResize.setBorder(deselectedBorder);
        if (selected[5]) btnFlipVertical.setBorder(selectedBorder); else btnFlipVertical.setBorder(deselectedBorder);
        if (selected[6]) btnFlipHorizontal.setBorder(selectedBorder); else btnFlipHorizontal.setBorder(deselectedBorder);
        if (selected[7]) btnRotate.setBorder(selectedBorder); else btnRotate.setBorder(deselectedBorder);
        if (selected[8]) btnLine.setBorder(selectedBorder); else btnLine.setBorder(deselectedBorder);
        if (selected[9]) btnArrow.setBorder(selectedBorder); else btnArrow.setBorder(deselectedBorder);
        if (selected[10]) btnRectangle.setBorder(selectedBorder); else btnRectangle.setBorder(deselectedBorder);
        if (selected[11]) btnOval.setBorder(selectedBorder); else btnOval.setBorder(deselectedBorder);
        if (selected[12]) btnRoundedRect.setBorder(selectedBorder); else btnRoundedRect.setBorder(deselectedBorder);
        if (selected[13]) btnPolyform.setBorder(selectedBorder); else btnPolyform.setBorder(deselectedBorder);
        if (selected[14]) btnStamp.setBorder(selectedBorder); else btnStamp.setBorder(deselectedBorder);
        if (selected[15]) btnBucketFill.setBorder(selectedBorder); else btnBucketFill.setBorder(deselectedBorder);
        if (selected[16]) btnEraser.setBorder(selectedBorder); else btnEraser.setBorder(deselectedBorder);
        if (selected[17]) btnPencil.setBorder(selectedBorder); else btnPencil.setBorder(deselectedBorder);
        if (selected[18]) btnEyedropper.setBorder(selectedBorder); else btnEyedropper.setBorder(deselectedBorder);
        if (selected[19]) btnText.setBorder(selectedBorder); else btnText.setBorder(deselectedBorder);
        if (selected[21]) btnMath.setBorder(selectedBorder); else btnMath.setBorder(deselectedBorder);
        if (selected[22]) btnBrightnessContrast.setBorder(selectedBorder); else btnBrightnessContrast.setBorder(deselectedBorder);
    }

    /**
     * Sets all other buttons to an empty border apart from the specified button
     * which is given a semi transparent 3px border.
     * @param button the button which should have a border.
     */
    public void setOnlyButtonSelected(int button)
    {
        boolean[] passVal = new boolean[23];
        Arrays.fill(passVal, false);

        if (button == ImageToolbarEvent.SELECT_RECT_BUTTON)
            passVal[0]=true;
        else if (button == ImageToolbarEvent.SELECT_OVAL_BUTTON)
            passVal[1]=true;
        else if (button == ImageToolbarEvent.SELECT_POLYGON_BUTTON)
            passVal[2]=true;
        else if (button == ImageToolbarEvent.CROP_BUTTON)
            passVal[3]=true;
        else if (button == ImageToolbarEvent.SCALE_BUTTON)
            passVal[4]=true;
        else if (button == ImageToolbarEvent.FLIP_VERTICAL_BUTTON)
            passVal[5]=true;
        else if (button == ImageToolbarEvent.FLIP_HORIZONTAL_BUTTON)
            passVal[6]=true;
        else if (button == ImageToolbarEvent.ROTATE_BUTTON)
            passVal[7]=true;
        else if (button == ImageToolbarEvent.DRAW_LINE_BUTTON)
            passVal[8]=true;
        else if (button == ImageToolbarEvent.DRAW_ARROW_BUTTON)
            passVal[9]=true;
        else if (button == ImageToolbarEvent.DRAW_RECTANGLE_BUTTON)
            passVal[10]=true;
        else if (button == ImageToolbarEvent.DRAW_CIRCLE_BUTTON)
            passVal[11]=true;
        else if (button == ImageToolbarEvent.DRAW_ROUNDED_RECT_BUTTON)
            passVal[12]=true;
        else if (button == ImageToolbarEvent.DRAW_POLYGON_BUTTON)
            passVal[13]=true;
        else if (button == ImageToolbarEvent.STAMP_BUTTON)
            passVal[14]=true;
        else if (button == ImageToolbarEvent.BUCKET_FILL_BUTTON)
            passVal[15]=true;
        else if (button == ImageToolbarEvent.ERASER_BUTTON)
            passVal[16]=true;
        else if (button == ImageToolbarEvent.PENCIL_BUTTON)
            passVal[17]=true;
        else if (button == ImageToolbarEvent.EYEDROPPER_BUTTON)
            passVal[18]=true;
        else if (button == ImageToolbarEvent.TEXT_BUTTON)
            passVal[19]=true;
        else if (button == ImageToolbarEvent.CHEM_BUTTON)
            passVal[20]=true;
        else if (button == ImageToolbarEvent.MATH_BUTTON)
            passVal[21]=true;
        else if (button == ImageToolbarEvent.BRIGHTNESS_CONTRAST_BUTTON)
            passVal[22]=true;

        setButtonSelection(passVal);
    }

    /**
    * Dispatch the given event to all the toolbar listeners added to this toolbar.
    * @param e the ImageToolbarEvent to dispatch.
    */
    public void dispatchToolbarEvent(ImageToolbarEvent e)
    {
        for (int i = 0; i < toolbarListeners.length; i++)
                    toolbarListeners[i].buttonPress(e);
    }

    /**
     * Generates <code>ImageToolbarEvent</code>s and fires the <code>buttonPress</code> event of any registered
     * <code>ImageToolbarListeners</code>.
     */
    private class ToolbarMouseListener implements MouseListener
    {

        public void mouseClicked(MouseEvent e) {
            if (e.getComponent().equals(btnSelectRect)) {
                for (int i = 0; i < toolbarListeners.length; i++)
                    toolbarListeners[i].buttonPress(new ImageToolbarEvent(ImageToolbar.this,
                            ImageToolbarEvent.SELECT_RECT_BUTTON));

            }
            else if (e.getComponent().equals(btnSelectOval)) {
                for (int i = 0; i < toolbarListeners.length; i++)
                    toolbarListeners[i].buttonPress(new ImageToolbarEvent(ImageToolbar.this,
                            ImageToolbarEvent.SELECT_OVAL_BUTTON));
            }
            else if (e.getComponent().equals(btnSelectPolyform)) {
                for (int i = 0; i < toolbarListeners.length; i++)
                    toolbarListeners[i].buttonPress(new ImageToolbarEvent(ImageToolbar.this,
                            ImageToolbarEvent.SELECT_POLYGON_BUTTON));
            }
            else if (e.getComponent().equals(btnCrop)) {
                for (int i = 0; i < toolbarListeners.length; i++)
                    toolbarListeners[i].buttonPress(new ImageToolbarEvent(ImageToolbar.this,
                            ImageToolbarEvent.CROP_BUTTON));
            }
            else if (e.getComponent().equals(btnResize)) {
                for (int i = 0; i < toolbarListeners.length; i++)
                    toolbarListeners[i].buttonPress(new ImageToolbarEvent(ImageToolbar.this,
                            ImageToolbarEvent.SCALE_BUTTON));
            }
            else if (e.getComponent().equals(btnFlipVertical)) {
                for (int i = 0; i < toolbarListeners.length; i++)
                    toolbarListeners[i].buttonPress(new ImageToolbarEvent(ImageToolbar.this,
                            ImageToolbarEvent.FLIP_VERTICAL_BUTTON));
            }
            else if (e.getComponent().equals(btnFlipHorizontal)) {
                for (int i = 0; i < toolbarListeners.length; i++)
                    toolbarListeners[i].buttonPress(new ImageToolbarEvent(ImageToolbar.this,
                            ImageToolbarEvent.FLIP_HORIZONTAL_BUTTON));
            }
            else if (e.getComponent().equals(btnRotate)) {
                for (int i = 0; i < toolbarListeners.length; i++)
                    toolbarListeners[i].buttonPress(new ImageToolbarEvent(ImageToolbar.this,
                            ImageToolbarEvent.ROTATE_BUTTON));
            }
            else if (e.getComponent().equals(btnLine)) {
                for (int i = 0; i < toolbarListeners.length; i++)
                    toolbarListeners[i].buttonPress(new ImageToolbarEvent(ImageToolbar.this,
                            ImageToolbarEvent.DRAW_LINE_BUTTON));
            }
            else if (e.getComponent().equals(btnArrow)) {
                for (int i = 0; i < toolbarListeners.length; i++)
                    toolbarListeners[i].buttonPress(new ImageToolbarEvent(ImageToolbar.this,
                            ImageToolbarEvent.DRAW_ARROW_BUTTON));
            }
            else if (e.getComponent().equals(btnRectangle)) {
                for (int i = 0; i < toolbarListeners.length; i++)
                    toolbarListeners[i].buttonPress(new ImageToolbarEvent(ImageToolbar.this,
                            ImageToolbarEvent.DRAW_RECTANGLE_BUTTON));
            }
            else if (e.getComponent().equals(btnOval)) {
                for (int i = 0; i < toolbarListeners.length; i++)
                    toolbarListeners[i].buttonPress(new ImageToolbarEvent(ImageToolbar.this,
                            ImageToolbarEvent.DRAW_CIRCLE_BUTTON));
            }
            else if (e.getComponent().equals(btnRoundedRect)) {
                for (int i = 0; i < toolbarListeners.length; i++)
                    toolbarListeners[i].buttonPress(new ImageToolbarEvent(ImageToolbar.this,
                            ImageToolbarEvent.DRAW_ROUNDED_RECT_BUTTON));
            }
            else if (e.getComponent().equals(btnPolyform)) {
                for (int i = 0; i < toolbarListeners.length; i++)
                    toolbarListeners[i].buttonPress(new ImageToolbarEvent(ImageToolbar.this,
                            ImageToolbarEvent.DRAW_POLYGON_BUTTON));
            }
            else if (e.getComponent().equals(btnStamp)) {
                for (int i = 0; i < toolbarListeners.length; i++)
                    toolbarListeners[i].buttonPress(new ImageToolbarEvent(ImageToolbar.this,
                            ImageToolbarEvent.STAMP_BUTTON));
            }
            else if (e.getComponent().equals(btnBucketFill)) {
                for (int i = 0; i < toolbarListeners.length; i++)
                    toolbarListeners[i].buttonPress(new ImageToolbarEvent(ImageToolbar.this,
                            ImageToolbarEvent.BUCKET_FILL_BUTTON));
            }
            else if (e.getComponent().equals(btnEraser)) {
                for (int i = 0; i < toolbarListeners.length; i++)
                    toolbarListeners[i].buttonPress(new ImageToolbarEvent(ImageToolbar.this,
                            ImageToolbarEvent.ERASER_BUTTON));
            }
            else if (e.getComponent().equals(btnPencil)) {
                for (int i = 0; i < toolbarListeners.length; i++)
                    toolbarListeners[i].buttonPress(new ImageToolbarEvent(ImageToolbar.this,
                            ImageToolbarEvent.PENCIL_BUTTON));
            }
            else if (e.getComponent().equals(btnEyedropper)) {
                for (int i = 0; i < toolbarListeners.length; i++)
                    toolbarListeners[i].buttonPress(new ImageToolbarEvent(ImageToolbar.this,
                            ImageToolbarEvent.EYEDROPPER_BUTTON));
            }
            else if (e.getComponent().equals(btnText)) {
                for (int i = 0; i < toolbarListeners.length; i++)
                    toolbarListeners[i].buttonPress(new ImageToolbarEvent(ImageToolbar.this,
                            ImageToolbarEvent.TEXT_BUTTON));
            }
            else if (e.getComponent().equals(btnMath)) {
                for (int i = 0; i < toolbarListeners.length; i++)
                    toolbarListeners[i].buttonPress(new ImageToolbarEvent(ImageToolbar.this,
                            ImageToolbarEvent.MATH_BUTTON));
            }
            else if (e.getComponent().equals(btnBrightnessContrast)) {
                for (int i = 0; i < toolbarListeners.length; i++)
                    toolbarListeners[i].buttonPress(new ImageToolbarEvent(ImageToolbar.this,
                            ImageToolbarEvent.BRIGHTNESS_CONTRAST_BUTTON));
            }

        }

        public void mousePressed(MouseEvent e) {
            //if the component is either of the flip buttons, calling mouseClicked(e) will result in a two click events
            //for the buttons. Two flips has a net effect of no change to the image :-P
            if (((e.getComponent().equals(btnFlipHorizontal)) || (e.getComponent().equals(btnFlipVertical))) == false) {
                mouseClicked(e);
            }
        }

        public void mouseReleased(MouseEvent e) {

        }

        public void mouseEntered(MouseEvent e) {

        }

        public void mouseExited(MouseEvent e) {

        }

    }
}
