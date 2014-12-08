import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.PriorityQueue;
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
		String masterServer = null;
		String server1 = null;
		String server2 = null;
		MetaMessage reply = new MetaMessage(msg.type, MetaMessage.STATUS_FAIL,
				msg.fileName, msg.offSet, msg.len, null, null, null, null, null);

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
			maxchunk = findMaxchunk(fname);
		String servers3[] =findServer2Append(fname);
		if(servers3!=null&&servers3.length == 3)System.out.println("Selected servers:"+servers3[0]+":"+servers3[1]+":"+servers3[2]);
		
			if (maxchunk <= -1 || servers3 == null) {
				reply.data = "Error:File does not exist";
				break;
			}
			
			int chunksize = servers.get(servers3[0]).findSize(fname, maxchunk);
			log("chunk size of " + chunksize);

			if ((8192 - chunksize) >= len) {
				reply.masterServer = servers3[0];
				reply.server1=servers3[1];
				reply.server2=servers3[2];				
				reply.chunkNo = maxchunk;
				reply.status = MetaMessage.STATUS_SUCCESS;
				
				servers.get(reply.masterServer).fs.addFile(fname,
						maxchunk , chunksize+len,true);
				servers.get(reply.server1).fs.addFile(fname,
						maxchunk , chunksize+len,false);
				servers.get(reply.server2).fs.addFile(fname,
						maxchunk , chunksize+len,false);

				
			} else {

				// append fill with nulls
				Message rpyMsg = Sender.messageToFileServer(servers3[0], Config
						.getValue(servers3[0]), new Message(Message.APPEND,
						Message.STATUS_REQ, fname, servers3[1], servers3[2], (maxchunk), 0, 0, null));

				if (rpyMsg != null && rpyMsg.status == Message.STATUS_SUCCESS) {
					log(rpyMsg.toString());

					servers.get(servers3[0]).fs.addFile(fname,
							maxchunk , 8192,false);
					servers.get(servers3[1]).fs.addFile(fname,
							maxchunk , 8192,false);
					servers.get(servers3[2]).fs.addFile(fname,
							maxchunk , 8192,false);

				} else {
					if (rpyMsg != null)
						log(rpyMsg.data);
					reply.data = "Error:Failed to append nulls:" + fname;

					log("" + reply.data);

					break;
				}

				//So far we are getting top 3 available servers
				String[] available = MetaServer.loadBalance();
				if(available.length >=3 ){
					reply.masterServer = available[0];
					reply.server1 = available[1];
					reply.server2 = available[2];
				}else{
					break;
				}
				System.out.println("Selected servers:"+reply.masterServer+":"+reply.server1+":"+reply.server2);
				
				// Create file
				Message rpy2Msg = Sender.messageToFileServer(reply.masterServer,
						Config.getValue(reply.masterServer), new Message(
								Message.CREATE, Message.STATUS_REQ, fname,
								available[1], available[2], (maxchunk + 1), 0, 0, null));

				if (rpy2Msg != null && rpy2Msg.status == Message.STATUS_SUCCESS) {
					log(rpy2Msg.toString());
				} else {
					reply.data = "Error:Failed to create chunk file on file server:"
							+ fname + (maxchunk + 1);
					break;
				}

				if (rpyMsg.status == Message.STATUS_SUCCESS
						&& rpy2Msg.status == Message.STATUS_SUCCESS) {
					reply.chunkNo = maxchunk + 1;
					reply.status = MetaMessage.STATUS_SUCCESS;

					servers.get(reply.masterServer).fs.addFile(fname,
							maxchunk + 1, len,true);
					servers.get(reply.server1).fs.addFile(fname,
							maxchunk + 1, len,false);
					servers.get(reply.server2).fs.addFile(fname,
							maxchunk + 1, len,false);
				}

			}

			break;

		case MetaMessage.WRITE:
			maxchunk = -1;
			maxchunk = findMaxchunk(fname);
			if (maxchunk > -1) {
				reply.data = "Error:File already exists:";
				break;
			}

			String[] available = MetaServer.loadBalance();
			if(available.length >=3 ){
				reply.masterServer = available[0];
				reply.server1 = available[1];
				reply.server2 = available[2];
			}else{
				reply.data = "Error:Server not available";
				break;
			}
			
			System.out.println("selected servers:"+reply.masterServer+":"+reply.server1+":"+reply.server2);

			// create file
			Message rpyMsg = Sender.messageToFileServer(reply.masterServer,
					Config.getValue(reply.masterServer), new Message(
							Message.CREATE, Message.STATUS_REQ, fname, reply.server1, reply.server2, 1, 0, 0,
							null));

			if (rpyMsg == null || rpyMsg.status == Message.STATUS_FAIL) {
				reply.data = "Error:Create file on Server failed";
			} else {
				log(rpyMsg.toString());
			}

			if (rpyMsg.status == Message.STATUS_SUCCESS) {
				servers.get(reply.masterServer).fs.addFile(fname, 1, len,true);
				servers.get(reply.server1).fs.addFile(fname, 1, len,true);
				servers.get(reply.server2).fs.addFile(fname, 1, len,true);
				reply.fileName = fname;
				reply.chunkNo = 1;
				reply.status = MetaMessage.STATUS_SUCCESS;
			}
			break;

		case MetaMessage.READ:

			int maxchunkSize = -1;
			for (Entry<String, ServerInfo> e : servers.entrySet()) {

				if(e.getValue().fs !=null) log(e.getValue().fs.toString());
				else continue;

				int chunkSize = e.getValue().findSize(fname, offSet / 8192 + 1);

				log(e.getValue() + " chunksize of " + (offSet / 8192 + 1) + ":"
						+ chunkSize);

				if (chunkSize > -1 && e.getValue().fs.isMaster(fname)) {
					masterServer  = e.getKey();
					maxchunkSize = chunkSize;
					break;
				}
			}

			if (maxchunkSize <= -1) {
				reply.data = "Error:Fail does not exist to read";
				break;
			}

			reply.masterServer = masterServer ;
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
		log(reply.toString());
		return reply;
	}
	/**
	 * 
	 * @param serverName
	 */
	synchronized static void replicateFailedNode(String serverName){
		System.out.println("Replication started...."+serverName);
		PriorityQueue<ServerInfo> sortedServs = new PriorityQueue<ServerInfo>(20,
				new ServerComparator());
		PriorityQueue<ServerInfo> remainingServs = new PriorityQueue<ServerInfo>(20,
				new ServerComparator());
		FileSystem failedFs = null;
		
		
	    Iterator it = servers.entrySet().iterator();
	    while (it.hasNext()) {
	        Map.Entry pairs = (Map.Entry)it.next();
//	        System.out.println(pairs.getKey() + " = " + ((ServerInfo)pairs.getValue()).toString());
	        if(!((String)pairs.getKey()).equals(serverName)) 
	        	sortedServs.add((ServerInfo) pairs.getValue());
	        else
	        	failedFs = ((ServerInfo) pairs.getValue()).fs;
	    }
	    
	    if(sortedServs.size()<3) return ;
	    
	    System.out.println(servers.get(serverName));
	    
	    if(servers.get(serverName).fs==null || servers.get(serverName).fs.fileInfo == null){
	    	System.out.println("Nothing to replicate");
	    }
	    
		for (Entry<String, File> e : servers.get(serverName).fs.fileInfo.entrySet()) {
			String fileName = e.getKey() ;
			Integer fileChunks[] = e.getValue().fileChunks.keySet().toArray(new Integer[e.getValue().fileChunks.size()]);
			
			for(Integer chunkNo:fileChunks){
				System.out.println("coping:"+fileName+chunkNo);
				String destination = null;
				while(!sortedServs.isEmpty()){
					ServerInfo dest = sortedServs.poll();
					if(dest.findSize(fileName, chunkNo)>-1){
						remainingServs.add(dest);
					}else if(destination ==null){
						destination = dest.name;
					}
					if(remainingServs.size()>0 && destination!=null){
					System.out.println("coping:"+fileName+chunkNo+remainingServs.peek().name+"-->"+destination);
					FileOperations.copyFile(remainingServs.peek().name, destination, fileName+chunkNo);
					System.out.println("copied to:"+destination);
					servers.get(destination).fs.addFile(fileName, chunkNo, e.getValue().fileChunks.get(chunkNo), servers.get(destination).fs.isMaster(fileName));
					sortedServs.add(dest);
					while(!remainingServs.isEmpty()){
						sortedServs.add(remainingServs.poll());							
					}
					break;

				}
				
				}
			}
		}

	}
	
	/**
	 * 
	 * @param fname
	 * @return
	 */
	private static int findMaxchunk(String fname) {
		int maxchunk=-1;
		for (Entry<String, ServerInfo> e : servers.entrySet()) {
			if(!e.getValue().status)
				continue;
			int chunk = e.getValue().findMaxChunk(fname);
			if (chunk > maxchunk) {
				maxchunk = chunk;
			}
		}
		return maxchunk;
	}


	/**
	 * 
	 * @param fileName
	 * @return
	 */
	static String[] findServer2Append(String fileName){
		int maxchunk=-1;
		maxchunk = findMaxchunk(fileName);
		ArrayList<String> servs = new ArrayList<String>();
		
		for (Entry<String, ServerInfo> e : servers.entrySet()) {

			if(!e.getValue().status)
				continue;
			
			if(e.getValue().fs !=null) log(e.getValue().fs.toString());
			else continue;

			int chunkSize = e.getValue().findSize(fileName, maxchunk);
			System.out.println(fileName+"("+chunkSize + ")"+e.getKey());

			if (chunkSize > -1){
			if( e.getValue().fs.isMaster(fileName)) {
				servs.add(0,e.getKey());
			}else if(servs.size()<3){
				servs.add(e.getKey());
			}else {
				System.out.println("ERROR: More than 3 replicas found");
				break;
			}
			}
		}
		String servArr[] = servs.toArray(new String[servs.size()]);
		System.out.println("FindServers:"+servArr[0]+":"+servArr[1]+":"+servArr[2]);

		if(servArr.length==3){
			return servArr;		
		}
		return null;
		}

	private static class ServerComparator implements Comparator<ServerInfo> {
		@Override
		public int compare(ServerInfo x, ServerInfo y) {
			if (x.size < y.size) {
				return -1;
			}
			if (x.size > y.size) {
				return 1;
			}
			return 0;
		}

	}


	private static String[] loadBalance() {
		PriorityQueue<ServerInfo> sortedServs = new PriorityQueue<ServerInfo>(20,
				new ServerComparator());
		
		ArrayList<String> top = new ArrayList<String>();
		
	    Iterator it = servers.entrySet().iterator();
	    while (it.hasNext()) {
	        Map.Entry pairs = (Map.Entry)it.next();
//	        System.out.println(pairs.getKey() + " = " + ((ServerInfo)pairs.getValue()).toString());
			if(!((ServerInfo)pairs.getValue()).status)
				continue;
	        sortedServs.add((ServerInfo) pairs.getValue());
	    }
	    
	    while(!sortedServs.isEmpty()){
	    	ServerInfo sInfo = sortedServs.poll();
//	    	System.out.println(sInfo.name);
	    	if(sInfo.status){
	    		top.add(sInfo.name);
	    	}
	    }

	    String[] topArr = top.toArray(new String[top.size()]);
	    
	    System.out.println("Load:"+Arrays.toString(topArr));
	    
	    return topArr;
	}

	static boolean updateMetaData(String serverName, FileSystem fs) {

		fs = new FileSystem(fs);
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
//		 System.out.println(message);
	}
	
}
