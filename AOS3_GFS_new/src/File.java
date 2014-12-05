import java.io.Serializable;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

public class File implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -3502154583442601824L;
	String fileName;
	
	ConcurrentHashMap<Integer, Integer> fileChunks = new ConcurrentHashMap<Integer, Integer>();

	public File(File file) {
		this.fileName = new String(file.fileName);
		for (Entry<Integer, Integer> e : file.fileChunks.entrySet()) {
			this.fileChunks.put(new Integer(e.getKey()),
					new Integer(e.getValue()));
		}

	}

	public File(String file) {
		fileName = file;
	}

	synchronized boolean addChunk(Integer chunkName, Integer size) {
		// if (fileChunks.get(chunkName) == null) {
		fileChunks.put(chunkName, size);
		return true;
		// } else {
		// return false;
		// }
	}

	private static void log(String message) {
		System.out.println(message);
	}

	public int getChunk(Integer chunkName) {
		if (fileChunks.get(chunkName) == null) {
			return fileChunks.get(chunkName);
		} else {
			return -1;
		}
	}

	public String toString() {
		StringBuilder str = new StringBuilder();
		for (Entry<Integer, Integer> e : fileChunks.entrySet()) {
			str.append(e.getKey() + "=" + e.getValue() + ";");
		}

		return "{" + str.toString() + "}";

	}
}
