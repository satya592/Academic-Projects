import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
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

public class Node {
	final String processID;
	final int otherPeers;
	String port;
	long rounds;
	int totalComputationMessages;
	final int maxComputationMessages;
	boolean firstTime;
	volatile long localClock;
	volatile int pendingAcknowledgement;
	volatile boolean active;
	volatile boolean systemUp;
	String parent;
	boolean initiator;
	String neighboursNames[];
	String neighboursPorts[];
	volatile Hashtable<String, String> children = new Hashtable<String, String>();
	volatile Hashtable<String, ObjectInputStream> inputObjects = new Hashtable<String, ObjectInputStream>();
	volatile Hashtable<String, ObjectInputStream> inputObjects_Send = new Hashtable<String, ObjectInputStream>();
	volatile Hashtable<String, ObjectOutputStream> outputObjects = new Hashtable<String, ObjectOutputStream>();
	volatile Hashtable<String, Socket> peers = new Hashtable<String, Socket>();
	volatile Queue<Message> inQ = new LinkedList<Message>();
	volatile Queue<Message> outQ = new LinkedList<Message>();
	volatile Hashtable<String, Long> pendingACK = new Hashtable<String, Long>();
	// Socket peers[];
	ServerSocket serverSocket;
	// assumes the current class is called logger
	volatile boolean terminated;
	// PrintWriter writer;
	File file;
	FileWriter fw;// = new FileWriter(file.getAbsoluteFile(), true);
	BufferedWriter writer;// = new BufferedWriter(fw);

	Node() {
		this.processID = findHostName();
		this.otherPeers = (Integer.valueOf(Config.getValue("nodecount")) - 1);
		this.maxComputationMessages = Integer.valueOf(Config
				.getValue("maxComputationMsgs"));
		this.totalComputationMessages = 0;
		this.active = false;
		this.pendingAcknowledgement = 0;
		this.parent = null;
		this.firstTime = true;
		this.systemUp = false;
		this.terminated = false;
		this.localClock = 0;

		try {
			// writer = new PrintWriter(new File(this.processID));
			file = new File(this.processID);
			// if file doesnt exists, then create it
			if (!file.exists()) {
				file.createNewFile();
			}

			fw = new FileWriter(file.getAbsoluteFile(), true);
			writer = new BufferedWriter(fw);

		} catch (Exception e) {
			e.printStackTrace();
		}

		String initiator = Config.getValue("initiator");
		if (initiator.equals(processID)) {
			this.initiator = true;
			System.out.println("Initiator is:" + this.processID);
		} else {
			this.initiator = false;
		}

		String machines = Config.getValue("machines");
		String ports = Config.getValue("ports");
		String allMachinesNames[] = machines.split(";");
		String allMachinesPorts[] = ports.split(";");
		this.neighboursNames = new String[allMachinesNames.length - 1];
		this.neighboursPorts = new String[allMachinesPorts.length - 1];
		for (int i = 0, j = 0; i < allMachinesNames.length; i++) {
			if (!allMachinesNames[i].equals(this.processID)) {
				this.neighboursNames[j] = allMachinesNames[i];
				this.neighboursPorts[j] = allMachinesPorts[i];
				j++;
			} else {
				this.port = allMachinesPorts[i];
			}
		}

		for (String str : neighboursNames)
			pendingACK.put(str, (long) 0);
		System.out.println("Pending ACK size is " + pendingACK.size());
	}

	void updateLocalClock() {
		localClock++;
	}

	boolean isTerminated() {
		// No children, it is the root and idle
		if (this.children.isEmpty() && this.initiator
				&& (this.pendingAcknowledgement == 0) && !this.active)
			return true;
		else
			return false;
	}

	boolean isReadyToLeave() {
		// No children, it is not the root and idle
		if (this.children.isEmpty() && !this.initiator
				&& (this.pendingAcknowledgement == 0) && !this.active
				&& this.parent != null)
			return true;
		else
			return false;
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

	String recordMsg(Message iMsg, String info) {
		StringBuilder str = new StringBuilder();

		try {
			if (iMsg == null) {
				this.writer.write("Clock:" + localClock + "|" + info + "\n");
				return "IDLE/Terminate";
			}

			this.localClock++;

			str.append("Clock:");
			str.append(localClock);

			switch (iMsg.msg) {
			case Message.ACK:
				str.append("| ACK ");
				break;
			case Message.MSG:
				str.append("| COMP ");
				break;
			case Message.LEV:
				str.append(info);
			default:
				break;
			}

			if (iMsg.msg == Message.MSG || iMsg.msg == Message.ACK) {
				if (iMsg.sender.equals(this.processID)) {// Sending messages
					str.append("| Receiver:");
					str.append(iMsg.receiver);
					str.append("| Pending ACK:");
					str.append(pendingAcknowledgement);
				} else {// Receiving messages
					str.append("| Sender:");
					str.append(iMsg.sender);
					str.append("| Pending ACK:");
					str.append(pendingAcknowledgement);
				}
			}

			this.writer.write(str.toString() + "\n");

		} catch (Exception e) {
		}
		return str.toString();

	}

	// Take action of messages received
	void takeAction(Message iMsg) {
		switch (iMsg.msg) {
		case Message.ACK: {
			this.pendingAcknowledgement--;
			// this.localClock++;

			System.out.println("Clock:" + localClock + "| Pending ACK"
					+ pendingAcknowledgement + "|Child:" + this.children.size()
					+ "|" + iMsg.toString());

			this.recordMsg(iMsg, "");

			if (isTerminated()) {
				synchronized (this.outQ) {
					this.recordMsg(null, "Termination");
					this.outQ.add(new Message(Message.TERMINATE));
					this.terminated = true;

				}
			} else if (isReadyToLeave()) {
				synchronized (this.outQ) {
					this.recordMsg(null,
							"Sending ACK to parent and detaching from tree "
									+ this.parent);
					System.out.println("This should be one(Node):"
							+ this.pendingACK.get(this.parent));
					this.pendingACK.put(this.parent, 0l);

					this.outQ.add(new Message(Message.ACK, this.processID,
							this.parent));
					this.outQ.add(new Message(Message.LEV, this.processID,
							this.parent));

					this.parent = null;
				}
			}
		}
			break;
		case Message.CNT: {
		}
			break;
		case Message.MSG: {// Computation message
			// this.localClock++;
			long pendingACK = this.pendingACK.get(iMsg.sender);
			pendingACK++;
			this.pendingACK.put(iMsg.sender, pendingACK);
			if (!this.active) {// Idle to Active
				this.active = true;

				this.recordMsg(null, "From Idle to Active");
				this.recordMsg(iMsg, "ACK is sent to " + iMsg.sender);

				if (this.parent == null && !this.initiator) {
					this.parent = iMsg.sender;
					Message msg = new Message(Message.JOIN, this.processID,
							iMsg.sender);
					this.recordMsg(iMsg, "Joining as chiled to " + iMsg.sender);
					this.outQ.add(msg);
				}
				// Predictor to perform actions
				Thread init = new Thread(new Predictor(this));
				init.start();
			}
		}
			break;
		case Message.JOIN:
			this.recordMsg(null, "New child joined: " + iMsg.sender);
			this.children.put(iMsg.sender, iMsg.sender);
			break;
		case Message.LEV:
			this.recordMsg(null, "Child detached:" + iMsg.sender);

			if (this.children.get(iMsg.sender) != null)
				this.children.remove(iMsg.sender);

			if (isTerminated()) {
				synchronized (this.outQ) {
					this.recordMsg(null, "Termination");
					this.outQ.add(new Message(Message.TERMINATE));
				}
			}
			if (isReadyToLeave()) {
				synchronized (this.outQ) {

					this.recordMsg(null,
							"Sending ACK to parent and detaching from tree"
									+ this.parent);
					System.out.println("This should be one(Node2):"
							+ this.pendingACK.get(this.parent));
					this.pendingACK.put(this.parent, 0l);

					this.outQ.add(new Message(Message.ACK, this.processID,
							this.parent));

					this.outQ.add(new Message(Message.LEV, this.processID,
							this.parent));

					this.parent = null;
				}
			}

			break;
		case Message.TERMINATE:
			System.out.println("TERMINATED");
			this.terminated = true;
			this.recordMsg(null, "Computation Terminated");

			Message msg = new Message(Message.CNT, this.processID, iMsg.sender);

			try {
				Thread.sleep(3000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

			this.outQ.add(msg);
			// DONE
			break;
		default:
			System.out.println("UNKNOWN MESSAGE RECEIVED");
		}

	}

	public static void main(String arg[]) {
		try {

			Node node = new Node();
			System.out.println("Node Id is " + node.processID);
			Thread listener = new Thread(new Listener(node));
			listener.start();
			Thread[] senders = new Thread[node.otherPeers];
			for (int i = 0; i < node.otherPeers; i++) {
				System.out.println(i + "\\Created Sender thread to "
						+ node.neighboursNames[i] + " "
						+ node.neighboursPorts[i]);
				senders[i] = new Thread(new Sender(node.neighboursNames[i],
						node.neighboursPorts[i], node));
				senders[i].start();
				senders[i].setName(node.neighboursNames[i]);
			}

			while (!node.systemUp
					|| !(node.inputObjects_Send.size() == node.otherPeers)) {
				System.out.println("System in not up..");
				Thread.sleep(10000);
			}

			System.out.println("System is operational");

			// Message msg = new Message(Message.CNT, node.processID, null);
			// node.outQ.add(msg);

			if (node.initiator) {
				System.out.println("I am the INITIATOR " + node.processID);
				node.active = true;
				Thread init = new Thread(new Predictor(node));
				init.start();
			}

			Message inmsg;
			while (!node.terminated) {
				while (node.inQ.size() != 0) {
					synchronized (node.inQ) {
						inmsg = node.inQ.poll();
					}
					node.takeAction(inmsg);
				}
				// Thread.sleep(5);
			}

			System.out.println("Exiting main thread.." + node.processID);
			Thread.sleep(2000);

			// for (String str : node.neighboursNames) {
			// System.out.println("Thread sender is interrupting " + str);
			// node.inputObjects_Send.get(str).close();
			// }

			node.writer.close();
			// System.exit(0);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
}

class Message implements Serializable {
	private static final long serialVersionUID = 1L;
	static final int ACK = 0;
	static final int CNT = 1;
	static final int MSG = 2;
	static final int JOIN = 3;
	static final int LEV = 4;
	static final int TERMINATE = 5;

	int msg;
	String sender;
	String receiver;

	void setReceiver(String receiver) {
		this.receiver = receiver;
	}

	void setSender(String sender) {
		this.sender = sender;
	}

	Message(int msg) {
		this.msg = msg;
	}

	Message(int msg, String receiver) {
		this.msg = msg;
		this.receiver = receiver;
	}

	Message(int msg, String sender, String receiver) {
		this.msg = msg;
		this.sender = sender;
		this.receiver = receiver;
	}

	public String toString() {
		String msgtext;
		switch (msg) {
		case ACK:
			msgtext = "Acknowlege";
			break;
		case CNT:
			msgtext = "Control";
			break;
		case MSG:
			msgtext = "Computation";
			break;
		case JOIN:
			msgtext = "Join";
			break;
		case LEV:
			msgtext = "Leave";
			break;
		case TERMINATE:
			msgtext = "TERMINATE";
			break;
		default:
			msgtext = "unknown";
		}
		return msgtext + " message from " + sender + " to " + receiver;
	}
}