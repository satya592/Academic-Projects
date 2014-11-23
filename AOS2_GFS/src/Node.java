import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.Queue;

interface NodeType {
	int SER = 1;
	int META = 2;
	int CLNT = 3;
}

public class Node {
	final String processID;
	String port;
	boolean firstTime;

	volatile boolean active;
	volatile boolean systemUp;

	int type;
	final int servercount;
	final int clientcount;

	String serverNames[];
	String serverPorts[];

	String clientNames[];
	String clientPorts[];

	String metaServer = null;
	String metaPort = null;

	volatile Hashtable<String, String> servers = new Hashtable<String, String>();

	volatile Hashtable<String, ObjectInputStream> inputObjects = new Hashtable<String, ObjectInputStream>();
	volatile Hashtable<String, ObjectInputStream> inputObjects_Send = new Hashtable<String, ObjectInputStream>();
	volatile Hashtable<String, ObjectOutputStream> outputObjects = new Hashtable<String, ObjectOutputStream>();

	volatile Hashtable<String, Socket> peers = new Hashtable<String, Socket>();

	volatile Queue<Message> inQ = new LinkedList<Message>();
	volatile Queue<Message> outQ = new LinkedList<Message>();

	ServerSocket serverSocket;

	Node() {
		this.processID = findHostName();
		this.active = false;
		this.firstTime = true;
		this.systemUp = false;

		// servercount=2
		// clientcount=2
		// metaserver=dc01.utdallas.edu
		// metaport=1501
		// servers=dc02.utdallas.edu;dc03.utdallas.edu
		// serverports=1502;1503
		// clients=dc04.utdallas.edu;dc05.utdallas.edu
		// clientports=1504;1505

		metaServer = Config.getValue("metaserver");
		metaPort = Config.getValue("metaport");

		String servercount = Config.getValue("servercount");
		String servers = Config.getValue("servers");
		String serverports = Config.getValue("serverports");

		String clientcount = Config.getValue("clientcount");
		String clients = Config.getValue("clients");
		String clientports = Config.getValue("clientports");

		if (this.processID.equals(metaServer)) {
			this.type = NodeType.META;
			this.port = metaPort;
		}

		this.servercount = Integer.valueOf(servercount);
		this.clientcount = Integer.valueOf(clientcount);

		this.serverNames = servers.split(";");
		this.serverPorts = serverports.split(";");

		if (this.serverNames.length != this.servercount
				|| this.serverPorts.length != this.servercount) {
			System.out
					.println("ERROR - server count does not match with no. of addresses");
			System.exit(0);
		}

		for (int i = 0; i < this.serverNames.length; i++) {
			if (this.serverNames[i].equals(this.processID)) {// I am server
				this.type = NodeType.SER;
				this.port = this.serverPorts[i];
				break;
			}
		}

		this.clientNames = clients.split(";");
		this.clientPorts = clientports.split(";");

		if (this.clientNames.length != this.clientcount
				|| this.clientPorts.length != this.clientcount) {
			System.out
					.println("ERROR - client count does not match with no. of addresses");
			System.exit(0);
		}

		for (int i = 0; i < this.clientNames.length; i++) {
			if (this.clientNames[i].equals(this.processID)) {// I am client
				this.type = NodeType.CLNT;
				this.port = this.clientPorts[i];
				break;
			}
		}

	}

	String findHostName() {
		String hostname = "Unknown";

		try {
			InetAddress addr;
			addr = InetAddress.getLocalHost();
			hostname = addr.getHostName();
		} catch (UnknownHostException ex) {
			System.out.println("Hostname can not be resolved");
		}
		return hostname;
	}

	void listenerThreads() {
		// Listeners for MetaServer
		if (this.type != NodeType.META) {
			System.out.println("\\Created Listener thread to  "
					+ this.metaServer + " " + this.metaPort);
			Thread meta = new Thread(new Listener(this.metaServer,
					this.metaPort, this));
			meta.start();
			meta.setName(this.metaServer);
		}
		// Listeners for servers
		if (this.type == NodeType.CLNT || this.type == NodeType.META) {
			Thread[] senders = new Thread[this.servercount];

			for (int i = 0; i < this.servercount; i++) {
				System.out.println(i + 1 + "\\Created Listener thread to "
						+ this.serverNames[i] + " " + this.serverPorts[i]);
				senders[i] = new Thread(new Listener(serverNames[i],
						serverPorts[i], this));
				senders[i].start();
				senders[i].setName(this.serverNames[i]);
			}

		}
		// Listeners for clients
		if (this.type == NodeType.SER || this.type == NodeType.META) {
			Thread[] senders = new Thread[this.clientcount];

			for (int i = 0; i < this.clientcount; i++) {
				System.out.println(i + 1 + "\\Created Listener thread to "
						+ this.clientNames[i] + " " + this.clientPorts[i]);
				senders[i] = new Thread(new Listener(clientNames[i],
						this.clientPorts[i], this));
				senders[i].start();
				senders[i].setName(this.clientNames[i]);
			}
		}
		// HeartBeat Listeners
		if (this.type == NodeType.META) {
			Thread[] senders = new Thread[this.servercount];

			for (int i = 0; i < this.servercount; i++) {
				System.out.println(i + 1
						+ "\\Created Hear beat Listener thread to "
						+ this.serverNames[i] + " " + this.serverPorts[i]);
				senders[i] = new Thread(new Listener(serverNames[i],
						serverPorts[i], this));
				senders[i].start();
				senders[i].setName(this.serverNames[i] + "_HB");
			}
		}

	}

	public static void main(String arg[]) {
		try {

			Node node = new Node();
			System.out.println("Node Id is " + node.processID);
			Thread listener = new Thread(new Sender(node));
			listener.start();

			node.listenerThreads();

			switch (node.type) {
			case NodeType.CLNT: {
				System.out.println("####I AM the CLIENT####");
				Client client = new Client(node);
				client.run();
				break;
			}
			case NodeType.SER: {
				System.out.println("####I AM the SERVER####");
				Server server = new Server(node);
				server.run();
				break;
			}
			case NodeType.META: {
				System.out.println("####I AM the META SERVER####");
				MetaServer metaserver = new MetaServer(node);
				metaserver.run();
				break;
			}
			default:
				System.out.println("###Un Identified Node");
			}

			System.out.println("System is operational");

			System.out.println("Exiting main thread.." + node.processID);
			Thread.sleep(2000);

		} catch (Exception e) {
			e.printStackTrace();
		}

	}
}

class Message implements Serializable {
	private static final long serialVersionUID = 1L;
	static final int BEATS = 0;
	static final int READ = 1;
	static final int WRITE = 2;
	static final int APPEND = 3;
	static final int HB_SER = 4;
	static final int HB_REG = 5;
	static final int HB_ACK = 6;

	int type;
	String data;
	String sender;
	String receiver;

	void setReceiver(String receiver) {
		this.receiver = receiver;
	}

	void setSender(String sender) {
		this.sender = sender;
	}

	/** message type **/
	Message(int msg) {
		this.type = msg;
	}

	/** message type and Data **/
	Message(int msg, String data) {
		this.type = msg;
		this.data = data;
	}

	/** message type, receiver and data **/
	Message(int type, String receiver, String data) {
		this.type = type;
		this.receiver = receiver;
		this.data = data;
	}

	/** message type, sender, receiver and data **/
	Message(int type, String sender, String receiver, String data) {
		this.type = type;
		this.sender = sender;
		this.receiver = receiver;
		this.data = data;
	}

	public String toString() {
		String msgtext;
		switch (type) {
		case READ:
			msgtext = "Read";
			break;
		case BEATS:
			msgtext = "Beats";
			break;
		case WRITE:
			msgtext = "Write";
			break;
		case APPEND:
			msgtext = "Append";
			break;
		default:
			msgtext = "unknown";
		}
		return msgtext + "|" + sender + "->" + receiver + data;
	}
}