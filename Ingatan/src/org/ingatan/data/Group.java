/*
 * Group.java
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

package org.ingatan.data;

import org.ingatan.io.IOManager;
import java.util.Arrays;

/**
 * Encapsulates a group of libraries.
 *
 * @author Thomas Everingham
 * @version 1.0
 */
public class Group {

    /**
     * The name of this group.
     */
    protected String groupName;
    /**
     * The names of libraries held by this group.
     */
    protected String[] libraryNames;
    /**
     * The id's of libraries held by this group.
     */
    protected String[] libraryIDs;

    /**
     * Creates a new group called <code>name</code> containing the specified libraries.
     * @param name the name of the group.
     * @param libraryIDs the library objects held by this group (identified by ID)
     * @param libNames the names of the libraries corresponding to the libraryIDs
     */
    public Group(String name, String[] libNames, String[] libraryIDs) {
        this.libraryNames = libNames;
        groupName = name;
        this.libraryIDs = libraryIDs;
    }

    /**
     * Gets the name of this group.
     * @return the name of this group.
     */
    public String getGroupName() {
        return groupName;
    }

    /**
     * Sets the name of this group.
     * @param groupName the new name for this group.
     */
    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    /**
     * Gets the names of the libraries held by this gropu.
     * @return the names of the libraries held by this group.
     */
    public String[] getlibraryNames() {
        return libraryNames;
    }

    /**
     * Sets the libraries held by this group.
     * @param libraryIDs the new array of libraries to be held by this group.
     */
    public void setLibraries(String[] libraryIDs) {
        this.libraryIDs = libraryIDs;
        libraryNames = new String[libraryIDs.length];
        for (int i = 0; i < libraryIDs.length; i++) {
            libraryNames[i] = IOManager.getLibraryName(libraryIDs[i]);
        }
    }

    /**
     * Gets whether or not this group contains the library that has the specified ID.
     * @param libID the ID of the library of interest
     * @return <code>true</code> if this group contains the library.
     */
    public boolean containsLibrary(String libID) {
        for (int i = 0; i < libraryIDs.length; i++) {
            if (libraryIDs[i].equals(libID))
                return true;
        }
        return false;
    }

    /**
     * Add the library with the specified libraryID to this group. Checks IOManager to make
     * sure that the library exists, and ensures that the library does not already exist in
     * this group.
     * @param libID The library to add.
     */
    public void addLibrary(String libID) {
        //ensure the library exists, and also get its name
        String libName = IOManager.getLibraryName(libID);
        if (libName.isEmpty()) {
            return;
        }

        //ensure the library does not already exist within this group.
        for (int i = 0; i < libraryIDs.length; i++) {
            if (libraryIDs[i].equals(libID)) {
                return;
            }
        }

        //reconstruct library ID and Name arrays
        String[] tempIDs = new String[libraryIDs.length + 1];
        String[] tempNames = new String[libraryNames.length + 1];
        System.arraycopy(libraryIDs, 0, tempIDs, 0, libraryIDs.length);
        System.arraycopy(libraryNames, 0, tempNames, 0, libraryNames.length);
        tempIDs[libraryIDs.length] = libID;
        tempNames[libraryNames.length] = libName;

        libraryIDs = tempIDs;
        libraryNames = tempNames;
    }

    /**
     * Removes the library with the specified ID from this group. First ensures that
     * the library exists within this group.
     * @param libID the ID of the library to remove from this group.
     */
    public void removeLibrary(String libID) {
        //find the index of the library ID to remove
        int index = -1;
        for (int i = 0; i < libraryIDs.length; i++) {
            if (libraryIDs[i].equals(libID)) {
                index = i;
            }
        }
        //if the library ID was not found.
        if (index == -1) {
            return;
        }

        libraryIDs = IOManager.removeIndexFromArray(index, libraryIDs);
        libraryNames = IOManager.removeIndexFromArray(index, libraryNames);
    }

    /**
     * Gets the IDs of libraries held by this group.
     * @return the IDs of libraries held by this group.
     */
    public String[] getLibraryIDs() {
        return libraryIDs;
    }
}
