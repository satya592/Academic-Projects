import java.util.Collections;
import java.util.Map.Entry;
import java.util.Set;

	public class ServerInfo {
		String name;
		boolean status;
		FileSystem fs;
		int size;
		
		ServerInfo(String name) {
			this.name = name;
			status = true;
			size=0;
		}

		ServerInfo(String name, boolean status, FileSystem fs) {
			this.name = name;
			this.status = status;
			this.fs = fs;
			if(fs!=null)this.size=fs.size;
			else size=0;
		}

		ServerInfo(String name, boolean status, FileSystem fs, int size) {
			this.name = name;
			this.status = status;
			this.fs = fs;
			if(fs!=null)this.size=fs.size;
			else size=0;
		}

		synchronized void setStatus(boolean status) {
			this.status = status;
//			if (!status)
//				this.fs = null;
		}

		synchronized int getSize() {
			return size;
		}
		
		@Override
		public String toString(){
			if(fs!=null)
			return name+"("+size+"){"+fs.toString()+"}";
			else 
				return name+"("+size+"){null}";
		}

		synchronized boolean updateMetaData(String name, FileSystem fs) {

			if (this.name != null && this.name.equals(name)
					&& this.status == true && fs != null && fs.fileInfo != null) {
				for (Entry<String, File> e : fs.fileInfo.entrySet()) {
					if (this.fs.fileInfo.get(e.getKey()) == null) {
						this.fs.addFile(e.getKey(), e.getValue());
					} else {
						File newF = e.getValue();
						File curF = this.fs.fileInfo.get(e.getKey());
						for (Entry<Integer, Integer> c : newF.fileChunks
								.entrySet()) {
							if (curF.fileChunks.get(c.getKey()) == null) {
								curF.fileChunks.put(c.getKey(), c.getValue());
							} else {
								if (curF.fileChunks.get(c.getKey()) < c
										.getValue())
									curF.fileChunks.put(c.getKey(),
											c.getValue());
							}
						}
					}
				}
				
				this.size = this.fs.size;
				return true;

			} else if (this.name.equals(name) && !this.status) {
				this.fs = new FileSystem(fs);
				this.status = true;
				
				this.size = this.fs.size;
				return true;
			} else {
				this.size = this.fs.size;
				log(this.name + "!=" + name + "mismatch in serverinfo update");
				return false;
			}
		}

		 private static void log(String string) {
//			 System.out.println(string);
		}

		int findMaxChunk(String fname) {
			if (fs != null)
				log("findMaxChunk:" + fname + "//" + status + "//"
						+ this.fs.toString());

			if (this.status == true) {
				if (this.fs != null && this.fs.fileInfo != null
						&& this.fs.fileInfo.get(fname) != null
						&& this.fs.fileInfo.get(fname).fileChunks != null) {
					Set<Integer> chunks = this.fs.fileInfo.get(fname).fileChunks
							.keySet();

					if (chunks != null) {
						log(chunks.toString());
						return Collections.max(chunks);
					} else {
						return -1;
					}

				} else
					return -2;
			} else {

				return -3;
			}
		}

		 int findSize(String fname, Integer chunkNo) {
			if (fs != null)
				log("findSize:" + fname + "//" + status + "//"
						+ this.fs.toString());

			if (status == true) {
				if (this.fs != null && this.fs.fileInfo != null
						&& this.fs.fileInfo.get(fname) != null
						&& this.fs.fileInfo.get(fname).fileChunks != null) {
					if (this.fs.fileInfo.get(fname).fileChunks.get(chunkNo) == null)
						return -1;
					else
						return this.fs.fileInfo.get(fname).fileChunks
								.get(chunkNo);
				} else
					return -2;
			} else {
				return -3;
			}
		}

		 int fileLock(String fname, Integer chunkNo) {
			if (fs != null)
				log("findSize:" + fname + "//" + status + "//"
						+ this.fs.toString());

			if (status == true) {
				if (this.fs != null && this.fs.fileInfo != null
						&& this.fs.fileInfo.get(fname) != null
						&& this.fs.fileInfo.get(fname).fileChunks != null) {
					if (this.fs.fileInfo.get(fname).fileChunks.get(chunkNo) == null)
						return -1;
					else
						
					return this.fs.fileInfo.get(fname).fileChunks.get(chunkNo);

				} else
					return -2;
			} else {
				return -3;
			}
		}

	}
