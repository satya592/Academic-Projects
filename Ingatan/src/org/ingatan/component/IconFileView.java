/*
 * IconFileView.java
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

import java.awt.Graphics;
import java.awt.Image;
import java.io.File;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.filechooser.FileView;

/**
 * An extension to fileview which allowed me to set custom icons for folders, image files,
 * and non-image files.
 *
 * @author Thomas Everingham
 * @version 1.0
 */
public class IconFileView extends FileView {

    @Override
    public String getName(File f) {
        return null; //let the L&F FileView figure this out
    }

    @Override
    public String getDescription(File f) {
        return null; //let the L&F FileView figure this out
    }

    @Override
    public Boolean isTraversable(File f) {
        return null; //let the L&F FileView figure this out
    }

    @Override
    public Icon getIcon(File f) {
        if (f.isDirectory()) {
            return new ImageIcon(IconFileView.class.getResource("/resources/icons/folder.png"));
        }

        //if there is a full stop then there might be an extension
        if (f.getName().contains(".") && (f.getName().length() > 4)) {
            String ext = f.getName().substring(f.getName().length() - 3, f.getName().length());
            if ((ext.equalsIgnoreCase("jpg")) || (ext.equalsIgnoreCase("jpeg")) || (ext.equalsIgnoreCase("gif")) || (ext.equalsIgnoreCase("png")) || (ext.equalsIgnoreCase("bmp")) || (ext.equalsIgnoreCase("tif")) || (ext.equalsIgnoreCase("tiff"))) {
                return new ImageIcon(IconFileView.class.getResource("/resources/icons/image.png"));
            }
        }
        //no extension, so just return page icon
        return new ImageIcon(IconFileView.class.getResource("/resources/icons/page.png"));
    }
}
