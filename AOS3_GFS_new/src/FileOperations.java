import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.RandomAccessFile;

public class FileOperations {

	static boolean createDirectory(String dir) {
		File file = new File("./" + dir);
		if (!file.exists()) {
			if (file.mkdir()) {
				System.out.println("Directory is created!");
				return true;
			} else {
				System.out.println("Failed to create directory!");
				return false;
			}
		} else
			return false;
	}

	static boolean copyFile(String source,String destination,String file){
		File src = new File("./" + source+"/"+file);
		File desti = new File("./"+destination+"/"+file);
		// check if file exist, otherwise create the file before writing
		if (src.exists()) {
	        if (!desti.exists()) {
	            InputStream in;
				try {
					in = new FileInputStream(src);
	            OutputStream out = new FileOutputStream(desti);

	            // Copy the bits from instream to outstream
	            byte[] buf = new byte[8192];
	            int len;
	            while ((len = in.read(buf)) > 0) {
	                out.write(buf, 0, len);
	            }
	            in.close();
	            out.close();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	        return true;
	        }
		}
return false;
	}
	
	static String[] listOfFiles(String dir) {
		File file = new File("./" + dir);
		File[] files = file.listFiles();

		if (files == null)
			return null;

		String[] fileNames = new String[files.length];
		int i = 0;
		for (File f : files) {
			// System.out.println(f.getName());
			fileNames[i++] = f.getName();
		}
		return fileNames;
	}

	static boolean createFile(String fileName) {
		File myFile = new File("./" + fileName);
		// check if file exist, otherwise create the file before writing
		if (!myFile.exists()) {
			try {
				myFile.createNewFile();
				log(fileName + " created");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return true;
		} else
			log(fileName + " not created");
		return false;
	}

	static boolean writeFile(String fileName, int offSet, String data) {
		File myFile = new File("./" + fileName);
		if (myFile.exists()) {
			try {
				RandomAccessFile file = new RandomAccessFile(fileName, "rw");
				file.seek(offSet);
				file.write(data.getBytes());
				System.out.println(new String(data) + "written into "
						+ fileName);
				file.close();

			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} finally {
			}
			return true;
		} else
			return false;

	}

	static String readFile(String fileName, int offSet, int size) {
		File myFile = new File("./" + fileName);
		// check if file exist, otherwise create the file before writing

		if (myFile.exists()) {
			byte[] strData = new byte[size];
			try {
				RandomAccessFile file = new RandomAccessFile(fileName, "r");
				file.seek(offSet);
				file.read(strData);
				file.close();
				log("Read:" + new String(strData));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} finally {
			}
			return new String(strData);
		} else
			return null;
	}

	static void log(String data) {
		System.out.println(data);
	}

	static int countCharsBuffer(String f, String charsetName) {
		return (int)new File(f).length();

//		BufferedReader reader;
//		int charCount = 0;
//		try {
//			reader = new BufferedReader(new InputStreamReader(
//					new FileInputStream(new File(f)), charsetName));
//			char[] cbuf = new char[9000];
//			int read = 0;
//			while ((read = reader.read(cbuf)) > -1) {
//				charCount += read;
//			}
//			reader.close();
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//
//		return charCount;
	}

	public static boolean deleteDirectory(String dir) {
		File directory = new File("./" + dir);
		if (directory.exists()) {
			File[] files = directory.listFiles();
			if (null != files) {
				for (int i = 0; i < files.length; i++) {
					if (files[i].isDirectory()) {
						deleteDirectory(files[i].getName());
					} else {
						files[i].delete();
					}
				}
			}
		}
		return (directory.delete());
	}

	// public static void main(String[] args) {
	// FileOperations.createFile("file1.txt1");
	//
	// }

	// public static void maining(String[] args) {
	// // FileOperations.createDirectory("10.123");
	// FileOperations.createFile("10.123/file1");
	// if (FileOperations.writeFile("10.123/file1",
	// FileOperations.countCharsBuffer("10.123/file1", "US-ASCII"),
	// "Hi mangu".getBytes()))
	// log("Written");
	// else
	// log("Failed to write");
	// log(FileOperations.countCharsBuffer("10.123/file1", "US-ASCII") + "");
	//
	// if (FileOperations.writeFile("10.123/file1",
	// FileOperations.countCharsBuffer("10.123/file1", "US-ASCII"),
	// "Hi mangu".getBytes()))
	// log("Written");
	// log(FileOperations.countCharsBuffer("10.123/file1", "US-ASCII") + "");
	// if (FileOperations.writeFile("10.123/file1",
	// FileOperations.countCharsBuffer("10.123/file1", "US-ASCII"),
	// "\0\0\0\0\0\0\0\0\0\0".getBytes()))
	// log("Written");
	// log(FileOperations.countCharsBuffer("10.123/file1", "US-ASCII") + "");
	// log("list of files");
	// for (String s : FileOperations.listOfFiles("10.123"))
	// log(s);
	// int size = FileOperations.countCharsBuffer("10.123/file1", "US-ASCII");
	// for (int i = 0; i < size; i++)
	// log(FileOperations.readFile("10.123/file1", i, size - i));
	// log(FileOperations.countCharsBuffer("10.123/file1", "US-ASCII") + "");
	// }

}
