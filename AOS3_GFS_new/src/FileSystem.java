import java.io.Serializable;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

public class FileSystem implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -1568600380072550284L;
	String serverName;
	int size=0;
	ConcurrentHashMap<String, File> fileInfo = new ConcurrentHashMap<String, File>();

	FileSystem(String serverName) {
		this.serverName = serverName;
	}
	
	
	FileSystem(FileSystem Fs) {
		this.serverName = new String(Fs.serverName);
		for (Entry<String, File> e : Fs.fileInfo.entrySet()) {
			this.fileInfo.put(new String(e.getKey()), new File(e.getValue()));
			this.size+=e.getValue().size;
		}
		System.out.println("" + this);
	}
/**
 * 
 * @param file
 * @param chunkName
 * @param size
 * @return
 */
	synchronized boolean addFile(String file, Integer chunkName, Integer size) {
		log(file + ":" + chunkName + ":" + size);
		// if (fileInfo.get(file) == null
		// || fileInfo.get(file).fileChunks.get(chunkName) == null) {
		if (fileInfo.get(file) == null){
			fileInfo.put(file, new File(file));
			}
		fileInfo.get(file).addChunk(chunkName, size);
		this.size+=size;

		return true;
		// } else {
		// return false;
		// }
	}

	synchronized boolean updateFile(String file, Integer chunkName, Integer newsize) {
		log(file + ":" + chunkName + ":" + newsize);
		if (fileInfo.get(file) == null)
			return false;
		fileInfo.get(file).addChunk(chunkName, newsize);
		this.size+=size;
		return true;
	}

	synchronized boolean addFile(String fileName, File file) {
		fileInfo.put(fileName, file);
		this.size+=file.size;
		return true;
	}

	int getChunkSize(String file, Integer chunkName) {
		if (fileInfo.get(file) != null
				&& fileInfo.get(file).fileChunks.get(chunkName) == null) {
			return fileInfo.get(file).getChunk(chunkName);
		} else {
			return -1;
		}
	}

	public String toString() {
		StringBuilder str = new StringBuilder();
		for (Entry<String, File> e : fileInfo.entrySet()) {
			str.append(e.getKey() + "=" + e.getValue() + ";");
		}

		return serverName + "{" + str.toString() + "}";
	}

	private void log(String message) {
		System.out.println(message);
	}

}
