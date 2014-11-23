import java.util.Collections;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class MetaServer {

	/**
	 * Application method to run the server runs in an infinite loop listening
	 * on port 9898. When a connection is requested, it spawns a new thread to
	 * do the servicing and immediately returns to listening. The server keeps a
	 * unique client number for each client that connects just to show
	 * interesting logging messages. It is certainly not necessary to do this.
	 */
	volatile static ConcurrentHashMap<String, ServerInfo> servers = new ConcurrentHashMap<String, ServerInfo>();

	// volatile static Queue<Message> inQ = new LinkedList<Message>();

	static MetaMessage requestHandler(MetaMessage msg) {
		String fname = msg.fileName;
		int len = msg.len;
		int offSet = msg.offSet;
		String sname = null;
		MetaMessage reply = new MetaMessage(msg.type, MetaMessage.STATUS_FAIL,
				msg.fileName, msg.offSet, msg.len, null, null, null);

		if (msg.fileName == null
				|| (msg.type != MetaMessage.APPEND
						&& msg.type != MetaMessage.READ && msg.type != MetaMessage.WRITE)
				|| msg.status != MetaMessage.STATUS_REQ) {
			reply.data = "Invalid request";
			System.out.println(reply);
			return reply;
		}

		switch (msg.type) {
		case MetaMessage.APPEND:
			// find max chunck size
			int maxchunk = -1;
			for (Entry<String, ServerInfo> e : servers.entrySet()) {

				log(e.getValue().fs.toString());

				int chunk = e.getValue().findMaxChunk(fname);
				log(e.getValue().name + ":" + chunk);

				if (chunk > maxchunk) {
					sname = e.getKey();
					maxchunk = chunk;
				}
			}

			if (maxchunk <= -1 || sname == null) {
				reply.data = "Error:File does not exist";
				break;
			}

			int chunksize = servers.get(sname).findSize(fname, maxchunk);
			log("chunk size of " + chunksize);

			if ((8192 - chunksize) >= len) {
				reply.serverName = sname;
				reply.chunkNo = maxchunk;
				reply.status = MetaMessage.STATUS_SUCCESS;
			} else {
				int fail = 0;
				while (true) {

					int rand = RandomNumber.randomInt(1, servers.size());
					int count = 1;
					for (Entry<String, ServerInfo> e : servers.entrySet()) {
						if (rand == count) {
							reply.serverName = e.getKey();
							break;
						}
						count++;
					}
					if (servers.get(reply.serverName).status)
						break;
					else
						fail++;
					if (fail == servers.size()) {
						reply.serverName = null;
						break;
					}
				}

				// append fill with nulls
				Message rpyMsg = Sender.messageToFileServer(sname, Config
						.getValue(sname), new Message(Message.APPEND,
						Message.STATUS_REQ, fname, (maxchunk), 0, 0, null));

				if (rpyMsg != null && rpyMsg.status == Message.STATUS_SUCCESS) {
					log(rpyMsg.toString());
				} else {
					if (rpyMsg != null)
						log(rpyMsg.data);
					reply.data = "Error:Failed to append nulls:" + fname;

					log("" + reply.data);

					break;
				}

				// Create file
				Message rpy2Msg = Sender.messageToFileServer(reply.serverName,
						Config.getValue(reply.serverName), new Message(
								Message.CREATE, Message.STATUS_REQ, fname,
								(maxchunk + 1), 0, 0, null));

				if (rpy2Msg != null && rpy2Msg.status == Message.STATUS_SUCCESS) {
					log(rpy2Msg.toString());
				} else {
					reply.data = "Error:Failed to create chunk file on file server:"
							+ fname + (maxchunk + 1);
					break;
				}

				if (rpyMsg.status == Message.STATUS_SUCCESS
						&& rpy2Msg.status == Message.STATUS_SUCCESS) {
					// servers.get(reply.serverName).fs.fileInfo.get(fname).fileChunks
					// .put(maxchunk + 1, 0);
					reply.chunkNo = maxchunk + 1;
					reply.status = MetaMessage.STATUS_SUCCESS;

					servers.get(reply.serverName).fs.addFile(fname,
							maxchunk + 1, 0);
				}

			}

			break;

		case MetaMessage.WRITE:
			maxchunk = -1;
			for (Entry<String, ServerInfo> e : servers.entrySet()) {

				int chunk = e.getValue().findMaxChunk(fname);

				if (chunk > maxchunk) {
					sname = e.getKey();
					maxchunk = chunk;
				}
			}

			if (maxchunk > -1) {
				reply.data = "Error:File already exists:";
				break;
			}

			int fail = 0;
			while (true) {
				int rand = RandomNumber.randomInt(1, servers.size());
				int count = 1;
				for (Entry<String, ServerInfo> e : servers.entrySet()) {
					if (rand == count) {
						reply.serverName = e.getKey();
						break;
					}
					count++;
				}
				if (servers.get(reply.serverName).status)
					break;
				else
					fail++;
				if (fail == servers.size()) {
					reply.serverName = null;
					break;
				}
			}
			if (reply.serverName == null) {
				reply.data = "Error:Server not available";
				break;
			}
			log("Create file: " + reply.serverName + " chunk name "
					+ (fname + 1));

			// create file
			Message rpyMsg = Sender.messageToFileServer(reply.serverName,
					Config.getValue(reply.serverName), new Message(
							Message.CREATE, Message.STATUS_REQ, fname, 1, 0, 0,
							null));

			if (rpyMsg == null || rpyMsg.status == Message.STATUS_FAIL) {
				reply.data = "Error:Create file on Server failed";
			} else {
				log(rpyMsg.toString());
			}

			if (rpyMsg.status == Message.STATUS_SUCCESS) {
				servers.get(reply.serverName).fs.addFile(fname, 1, 0);
				reply.fileName = fname;
				reply.chunkNo = 1;
				reply.status = MetaMessage.STATUS_SUCCESS;
			}
			break;

		case MetaMessage.READ:

			int maxchunkSize = -1;
			for (Entry<String, ServerInfo> e : servers.entrySet()) {

				log(e.getValue().fs.toString());

				int chunkSize = e.getValue().findSize(fname, offSet / 8192 + 1);

				log(e.getValue() + " chunksize of " + (offSet / 8192 + 1) + ":"
						+ chunkSize);

				if (chunkSize > -1) {
					sname = e.getKey();
					maxchunkSize = chunkSize;
					break;
				}
			}

			if (maxchunkSize <= -1) {
				reply.data = "Error:Fail does not exist to read";
				break;
			}

			reply.serverName = sname;
			reply.fileName = fname;
			reply.chunkNo = offSet / 8192 + 1;
			reply.offSet = (offSet % 8192);

			log("Available size: " + maxchunkSize + "-" + (offSet % 8192) + "="
					+ (maxchunkSize - (offSet % 8192)));
			if (maxchunkSize - (offSet % 8192) >= 0) {
				reply.status = MetaMessage.STATUS_SUCCESS;

			} else {
				reply.data = "Error:invalid OffSet-" + (offSet);
				log("Read rejected");
			}

			break;
		}
		System.out.println(reply);
		return reply;
	}

	static boolean updateMetaData(String serverName, FileSystem fs) {

		if (serverName == null)
			return false;
		if (servers.get(serverName) == null) {
			log("Before update:" + servers.get(serverName));
			servers.put(serverName, new ServerInfo(serverName, true, fs));
		} else {
			log("Before update:" + servers.get(serverName).fs);
			servers.get(serverName).updateMetaData(serverName, fs);
		}
		log("After update:" + servers.get(serverName).fs);
		System.out.println("Current FS:" + servers.get(serverName).fs);

		return true;
	}

	static boolean setServer(String serverName, boolean status, FileSystem fs) {
		if (serverName == null)
			return false;
		if (servers.get(serverName) == null) {
			servers.put(serverName, new ServerInfo(serverName, status, fs));
			return true;
		} else {
			servers.get(serverName).setStatus(status);
			return true;
		}
	}

	public static void main(String[] args) throws Exception {
		int port = Integer.valueOf(Config.getValue("metaport"));
		int reqPort = Integer.valueOf(Config.getValue("meta_req_port"));

		Thread HBListener = new Thread(new ListenerWrapper(port,
				ListenerWrapper.DEMON));
		HBListener.setPriority(Thread.MAX_PRIORITY);
		HBListener.start();

		Thread RequestListener = new Thread(new ListenerWrapper(reqPort,
				ListenerWrapper.META_REQ));
		RequestListener.setPriority(Thread.MIN_PRIORITY + 4);
		RequestListener.start();
		HBListener.join();
		// while (true) {
		// Thread.sleep(10000);
		// }
	}

	private static void log(String message) {
		// System.out.println(message);
	}

	static class ServerInfo {
		String name;
		boolean status;
		FileSystem fs;

		ServerInfo(String name) {
			this.name = name;
			status = true;
		}

		ServerInfo(String name, boolean status, FileSystem fs) {
			this.name = name;
			this.status = status;
			this.fs = fs;
		}

		synchronized void setStatus(boolean status) {
			this.status = status;
			if (!status)
				this.fs = null;
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
				return true;

			} else if (this.name.equals(name) && !this.status) {
				this.fs = new FileSystem(fs);
				this.status = true;
				return true;
			} else {

				log(this.name + "!=" + name + "mismatch in serverinfo update");
				return false;
			}
		}

		synchronized int findMaxChunk(String fname) {
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

		synchronized int findSize(String fname, Integer chunkNo) {
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

		synchronized int fileLock(String fname, Integer chunkNo) {
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
						Lock(this.fs.fileInfo.get(fname).fileChunks
								.get(chunkNo));
					return this.fs.fileInfo.get(fname).fileChunks.get(chunkNo);

				} else
					return -2;
			} else {
				return -3;
			}
		}

	}
}
