import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Hashtable;

class Sender implements Runnable {

	Node node;
	int count;

	public Sender(Node node) {
		this.node = node;
		switch (node.type) {
		case NodeType.CLNT:
			count = node.servercount + 1;
			break;
		case NodeType.SER:
			// 1 for MetaServer 1 for HeartBeat
			count = node.clientcount + 1 + 1;
			break;
		case NodeType.META:
			count = node.clientcount + node.servercount;
			break;
		default:
			count = -1;
		}
	}

	public Sender(String nodeName, String port) {
		this.node = null;
	}

	@Override
	public void run() {
		InetAddress address;
		try {
			address = InetAddress.getByName(node.processID);
			node.serverSocket = new ServerSocket(Integer.parseInt(node.port),
					25, address);
			int counter = 0;
			Hashtable<String, InputStream> inputStrm = new Hashtable<String, InputStream>();
			Hashtable<String, ObjectInputStream> objInputStrm = new Hashtable<String, ObjectInputStream>();
			Hashtable<String, OutputStream> outputStrm = new Hashtable<String, OutputStream>();
			Hashtable<String, ObjectOutputStream> objOutputStrm = new Hashtable<String, ObjectOutputStream>();
			while (counter < this.count) {
				// waiting..
				System.out.println("Total peers connected are " + counter);
				System.out.println("Waiting for connections..");
				Socket connectionSocket;
				connectionSocket = node.serverSocket.accept();
				counter++;

				System.out.println(connectionSocket.toString());
				System.out.println("Just connected to client"
						+ connectionSocket.getRemoteSocketAddress());

				connectionSocket.setTcpNoDelay(true);

				InputStream inStream = connectionSocket.getInputStream();
				ObjectInputStream in = new ObjectInputStream(inStream);
				String key = connectionSocket.getInetAddress().getHostName();

				if (node.inputObjects.get(key) == null) {
					node.inputObjects.put(key, in);
					inputStrm.put(key, inStream);
					objInputStrm.put(key, in);
				}

				System.out.println(in.readObject());

				OutputStream outStream = connectionSocket.getOutputStream();
				ObjectOutputStream out = new ObjectOutputStream(outStream);
				InetSocketAddress sockaddress = (InetSocketAddress) (connectionSocket
						.getRemoteSocketAddress());
				key = sockaddress.getHostName();

				if (node.outputObjects.get(key) == null) {

					node.outputObjects.put(key, out);
					outputStrm.put(key, outStream);
					objOutputStrm.put(key, out);

					out.writeObject(node.processID + ": hi " + key);
					node.peers.put(key, connectionSocket);
				} else {// Meta Server again for Heart Beat
					HeartBeatSender tHB = new HeartBeatSender(out,
							node.processID, key);
					// Start HeartBeat sending thread...
					System.out.println("######HB THREAD STARTED");
					new Thread(tHB).start();
				}

			}
			System.out.println("All peers are connected");

			synchronized (node) {
				node.systemUp = true;
			}

			Message msg = null;

			while (node.systemUp) {

				Thread.sleep(5);

				if (node.outQ.size() != 0) {
					msg = node.outQ.peek();

					ObjectOutputStream wrtObj = node.outputObjects
							.get(msg.receiver);
					System.out.println("sent:" + msg.toString());
					synchronized (wrtObj) {
						wrtObj.writeObject(msg);
					}

					// Remove from Queue
					synchronized (node.outQ) {
						node.outQ.poll();
					}
				}
			}
			Thread.sleep(1000);

			System.out.println("Sender thread is exiting...");
		} catch (Exception e) {
			e.printStackTrace();
			// clientNo++;// client id starts with 0
		}
	}
}