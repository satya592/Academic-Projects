/*
 * AboutWindow.java
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

import org.ingatan.component.text.RichTextArea;
import javax.swing.JDialog;
import org.ingatan.io.IOManager;

/**
 * About window with information regarding the license.
 *
 * @author Thomas Everingham
 * @version 1.0
 */
public class AboutWindow extends JDialog {
    private RichTextArea txtAbout = new RichTextArea();

    /**
     * Creates a new AboutWindow.
     */
    public AboutWindow() {
        this.setModal(true);
        this.setTitle("About Ingatan");
        this.setIconImage(IOManager.windowIcon);
        
        this.add(txtAbout.getScroller());
        txtAbout.setEditable(false);
        txtAbout.setFocusable(false);
        txtAbout.setToolbarVisible(false);
        this.setSize(400,500);
        this.setLocationRelativeTo(null);

        txtAbout.setRichText("[b][aln]0[!aln][fam]Dialog[!fam][sze]14[!sze][col]51,51,51[!col]Ingatan v1.3.5 (ingatan.org)[br][b][sze]12[!sze]" +
                "Hi! If you find this program useful, please let me know! I would be delighted to hear from you. You can contact me at [b]tom.ingatan@gmail.com.[b][br][br]" +
                "If you find any bugs or have any suggestions, go to [b]ingatan.org[b] and sumbit a new Issue (you will need a google account), or alternatively, email me at the address above.[br][br]" +
                "[u][sze]13[!sze]A Note for OpenJDK Users[u][br][sze]12[!sze]If running Ingatan under OpenJDK, it may run slowly. In this case, load Ingatan using the command:[br]" +
                "[fam]Monospace[!fam]java -Dsun.java2d.pmoffscreen=false -jar !osqb;path_to_ingatan.jar!csqb;[br][br][fam]Dialog[!fam]" +
                "[u][sze]13[!sze]Copyright and Licensing Information[u][sze]12[!sze][br]" +
                "A versatile memory quiz generator (c) Thomas Everingham, 2011. This is free software, released under the GNU General Public License (version 3 of the License). " +
                "A full copy of the GNU General Public License v3 must always be included with this software when distributed; if you did not receive a copy of the GNU General Public License with this software, please write to the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA. " +
                "A copy of the GNU GPL can also be found at: [b]http://www.gnu.org/licenses/gpl.html.[b][br][br]" +
                "[u][sze]13[!sze]Disclaimer[u][sze]12[!sze][br]" +
                "This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details.[br][br]" +
                "[b][sze]14[!sze]Projects Incorporated by Ingatan[b][sze]12[!sze][br]" +
                "[u]JDOM[u][br]jdom.org: Apache-style license (included as JDOM_LICENSE.TXT). (C) 2000-2007 Jason Hunter & Brett McLaughlin.[br]XML reader/writer; I love this.[br][br]" +
                "[u]JMathTeX[u][br]jmathtex.sf.net: GNU GPL[br]A superb, clean, and lightweight java math text rendering library. Original author: Kurt Vermeulen, current developers: Kris Coolsaet, Nico Van Cleemput[br][br]" +
                "[u]SketchEl[u][br]sketchel.sf.net: GNU GPL[br]A great little chemistry structure drawing program that is sure to make my organic chem study much easier. Written by Dr. Alex M. Clark.[br][br]" +
                "[u]Silk Icon Set[u][br]www.famfamfam.com: Creative Commons Attribution 2.5 License[br]A great set of icons by Mark James. Thank you![br][br]" +
                "[u]JOgg and JOrbis[u] from www.jcraft.com under LGPL 3 - Great libraries made so much easier using the EasyOgg wrapper by Kevin from http://www.cokeandcode.com.[br][br][br]" +
                "[u]OpenChart 1.4.3[u] from www.approximatrix.com under LGPL.[br][br]" +
                "[u]Arrow2D.java[u] from www.geotools.org under LGPL 2.1 - Thankyou, I was so glad not to write my own.[br][br][br]" +
                "Thankyou to Hugh for being my bug hunter.[end]");

        txtAbout.setCaretPosition(0);
    }


}
