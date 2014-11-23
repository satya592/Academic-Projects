/*
 * Main.java
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

package org.ingatan;

import org.ingatan.io.IOManager;
import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.SwingUtilities;
import org.ingatan.component.MainMenuWindow;

/**
 *
 * @author Thomas Everingham
 * @version 1.0
 */
public class Main {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        for (int i = 0; i < args.length; i++) {
            //if a custom home directory should be used.
            if (args[i].contains("--homeDir="))
            {
                String customHome = args[i].replace("--homeDir=", "").replace("\"", "");
                if (new File(customHome).canWrite() == false) {
                    System.out.println("\nCannot write to " + customHome + ", terminating.");
                    Logger.getLogger(Main.class.getName()).log(Level.SEVERE, "Cannot write to the custom home directory provided through the --homeDir argument.");
                    return;
                }
                else if (new File(customHome).canRead() == false) {
                    System.out.println("\nCannot read from " + customHome + ", terminating.");
                    Logger.getLogger(Main.class.getName()).log(Level.SEVERE, "Cannot read from the custom home directory provided through the --homeDir argument.");
                    return;
                }
                else if (new File(customHome).exists() == false) {
                    System.out.println("\nDirectory " + customHome + " does not exist. Attempting to create.");
                    if (new File(customHome).mkdirs() == false) {
                        System.out.println("    --failed to create the directory. Terminating.");
                        Logger.getLogger(Main.class.getName()).log(Level.SEVERE, "Could not create the custom home directory provided through the --homeDir argument.");
                        return;
                    }
                }
            }
        }
        System.out.println("\nNote: if Ingatan responds very slowly, try executing the JAR\n" +
                "      using \"java -Dsun.java2d.pmoffscreen=false -jar [path_to_ingatan.jar]\"\n");

        //this initiation is only called here, it appears nowhere else in the program
        IOManager.initiateIOManager();
        //create the main menu :-)
        SwingUtilities.invokeLater(new Runnable() {

            public void run() {
                new MainMenuWindow(IOManager.getNewMenuBackground());
            }
        });
    }

}
