/*
 * ZipTools.java
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

package org.ingatan.io;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Enumeration;
import java.util.zip.*;

/**
 * Methods for accessing and writing zip files. Zip files are the basis of the
 * library filtype used by Ingatan. The methods within this class do not handle
 * directories within zip files.
 *
 * @author Thomas Everingham
 * @version 1.0
 */
public class ZipTools {

    protected static final int BUFFER_SIZE = 2048;

    /**
     * Creates a new zip file at the specified destination containing all of the
     * specified files. Resulting zip contains only the files, does not construct any directories.
     * @param filenames the files to be included within the zip file.
     * @param zipDestination where to save the newly created zip file.
     * @throws IOException when there is a problem writing the zip to the specified location.
     */
    public static void createZip(String[] filenames, String zipDestination) throws IOException {

        // Create a buffer for reading the files
        byte[] buf = new byte[1024];

        try {
            // Create the ZIP file
            ZipOutputStream out = new ZipOutputStream(new FileOutputStream(zipDestination));

            // Compress the files
            for (int i = 0; i < filenames.length; i++) {
                FileInputStream in = new FileInputStream(filenames[i]);

                // Add ZIP entry to output stream.
                out.putNextEntry(new ZipEntry(new File(filenames[i]).getName()));

                // Transfer bytes from the file to the ZIP file
                int len;
                while ((len = in.read(buf)) > 0) {
                    out.write(buf, 0, len);
                }

                // Complete the entry
                out.closeEntry();
                in.close();
            }

            // Complete the ZIP file
            out.close();
        } catch (IOException e) {
            throw new IOException("IO problem while attempting to create zip. ZipUtils.createZip. \n" + e.getMessage());
        }
    }

    /**
     * Convenience method which takes an array of File objects rather than an array of Strings.
     * The array is simply converted to an array of String file paths and then passed to
     * createZip(String[], String).
     * @param files the array of files to include in the zip.
     * @param zipDestination the destination of the created zip file.
     */
    public static void createZip(File[] files, File zipDestination) throws IOException {
        String[] filenames = new String[files.length];
        for (int i = 0; i < files.length; i++) {
            filenames[i] = files[i].getPath();
        }

        createZip(filenames, zipDestination.getPath());
    }

    /**
     * Unzip the specified zip file into the destination path.
     * @param zipFilename the zip file to extract.
     * @param destinationPath the path into which the zip files should be created.
     * @throws IOException if there is a problem accessing the zip file or writing the extracted files
     */
    public static void unzip(String zipFilename, String destinationPath) throws IOException {
        BufferedOutputStream outputStream = null;
        BufferedInputStream inputStream = null;
        ZipEntry zipEntry = null;
        ZipFile zipFile = new ZipFile(zipFilename);
        Enumeration en = zipFile.entries();
        try {
            while (en.hasMoreElements()) {
                zipEntry = (ZipEntry) en.nextElement();
                inputStream = new BufferedInputStream(zipFile.getInputStream(zipEntry));
                int count;
                byte data[] = new byte[BUFFER_SIZE];
                FileOutputStream fos = new FileOutputStream(destinationPath + zipEntry.getName());
                outputStream = new BufferedOutputStream(fos, BUFFER_SIZE);
                while ((count = inputStream.read(data, 0, BUFFER_SIZE))
                        != -1) {
                    outputStream.write(data, 0, count);
                }
                outputStream.flush();
                outputStream.close();
                inputStream.close();
            }

        } catch (IOException e) {
            throw new IOException("IO problem while accessing zip file or writing to destination [destination = " + destinationPath + "]. ZipTools.unzip.\n" + e.getMessage());
        }
    }

    /**
     * Extracts a single file from the specified zip, and writes it to the specified directory.
     * @param zipFilename the zip from whcih to extract the file.
     * @param fileToExtract the name of the file to be extracted within the zip file. You may use the <code>existsInZip</code>
     * method to ensure that the file exists within the zip file.
     * @param extractTo Path of the destination of the extracted file. Do not include the file name.
     * @throws IOException if there is a problem accessing the zip file or writing the extracted file
     */
    public static void extractSingleFile(String zipFilename, String fileToExtract, String extractTo) throws IOException {
        try {
            BufferedOutputStream outputStream = null;
            BufferedInputStream inputStream = null;
            ZipEntry zipEntry;
            ZipFile zipFile = new ZipFile(zipFilename);

            zipEntry = zipFile.getEntry(fileToExtract);
            inputStream = new BufferedInputStream(zipFile.getInputStream(zipEntry));
            int count;
            byte data[] = new byte[BUFFER_SIZE];
            FileOutputStream fos = new FileOutputStream(extractTo + zipEntry.getName());
            outputStream = new BufferedOutputStream(fos, BUFFER_SIZE);
            while ((count = inputStream.read(data, 0, BUFFER_SIZE))
                    != -1) {
                outputStream.write(data, 0, count);
            }
            outputStream.flush();
            outputStream.close();
            inputStream.close();

        } catch (IOException e) {
            throw new IOException("IO problem while accessing zip file or writing to destination. ZipTools.extractSingleFile.\n" + e.getMessage());
        }
    }

    /**
     * Gets the list of files contained by the zip file.
     * @param zipFileName the zip file from which the file list should be retreived.
     * @return String array of the files contained by the zip file.
     * @throws IOException if the zip file at 'zipFileName' could not be accessed.
     */
    public static String[] getZipFileList(String zipFileName) throws IOException {
        String[] entryList = new String[0];
        ZipFile zf;
        try {
            // Open the ZIP file
            zf = new ZipFile(zipFileName);

            // Enumerate each entry
            for (Enumeration entries = zf.entries(); entries.hasMoreElements();) // Get the entry name
            {
                String newEntryList[] = new String[entryList.length + 1];
                System.arraycopy(entryList, 0, newEntryList, 0, entryList.length);
                newEntryList[entryList.length] = ((ZipEntry) entries.nextElement()).getName();
                entryList = newEntryList;
            }
        } catch (IOException e) {
            throw new IOException("IO problem when attempting access of: " + zipFileName + "\n" + e.getMessage());
        }

        return entryList;
    }

    /**
     * Checks to see whether the specified filename exists within the specified
     * zip file.
     * @param filename the file that may or may not exist within the zip file.
     * @param zipFileName the zip file to check.
     * @return true if the file 'filename' exists as an entry within the zip file located at 'zipFileName'.
     * @throws IOException if the zip at 'zipFileName' cannot be accessed.
     */
    public static boolean existsInZip(String filename, String zipFileName) throws IOException {
        String[] files;
        try {
            files = getZipFileList(zipFileName);
        } catch (IOException e) {
            throw new IOException("Could not retrieve file list from specified zip file: " + zipFileName + "\n" + e.getMessage());
        }

        for (int i = 0; i < files.length; i++) {
            if (files[i].equals(filename)) {
                return true;
            }
        }

        return false;
    }
}
