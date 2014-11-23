public class ServerSenderWrapper implements Runnable {
	FileSystem Fs;
	Message msg;
	MetaMessage metamsg;
	String servername;
	String port;

	ServerSenderWrapper(String sname, String port, FileSystem Fs) {
		this.servername = sname;
		this.port = port;
		this.Fs = Fs;
		msg = null;
	}

	ServerSenderWrapper(String sname, String port, Message msg) {
		this.servername = sname;
		this.port = port;
		this.msg = msg;
		Fs = null;
	}

	ServerSenderWrapper(String sname, String port, MetaMessage metamsg) {
		this.servername = sname;
		this.port = port;
		this.metamsg = metamsg;
		Fs = null;
	}

	@Override
	public void run() {
		if (Fs != null) {
			Sender.connectToServer(servername, port, Fs);
		} else if (metamsg != null) {
			log(" META MSG");
			MetaMessage statusMsg = Sender.messageToMetaServer(servername,
					port, metamsg);
			log(statusMsg.toString());
		} else {
			log("MSG");
			Message statusMsg = Sender.messageToFileServer(servername, port,
					msg);
			log(statusMsg.toString());
		}
	}

	private void log(String message) {
		System.out.println(message);
	}

}
