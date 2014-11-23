import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Hashtable;

class Listener implements Runnable {

	Node node;

	public Listener(Node node) {
		this.node = node;
	}

	public Listener(String nodeName, String port) {
		this.node = null;
	}

	@Override
	public void run() {
		InetAddress address;
		try {
			address = InetAddress.getByName(node.processID);
			node.serverSocket = new ServerSocket(Integer.parseInt(node.port),
					25, address);
			int count = 0;
			Hashtable<String, InputStream> inputStrm = new Hashtable<String, InputStream>();
			Hashtable<String, ObjectInputStream> objInputStrm = new Hashtable<String, ObjectInputStream>();
			Hashtable<String, OutputStream> outputStrm = new Hashtable<String, OutputStream>();
			Hashtable<String, ObjectOutputStream> objOutputStrm = new Hashtable<String, ObjectOutputStream>();
			while (count < node.otherPeers) {
				// waiting..
				System.out.println("Total peers connected are " + count);
				System.out.println("Waiting for connections..");
				Socket connectionSocket;
				connectionSocket = node.serverSocket.accept();
				count++;

				System.out.println(connectionSocket.toString());
				System.out.println("Just connected to client"
						+ connectionSocket.getRemoteSocketAddress());

				connectionSocket.setTcpNoDelay(true);

				InputStream inStream = connectionSocket.getInputStream();
				ObjectInputStream in = new ObjectInputStream(inStream);
				String key = connectionSocket.getInetAddress().getHostName();

				node.inputObjects.put(key, in);
				inputStrm.put(key, inStream);
				objInputStrm.put(key, in);

				System.out.println(in.readObject());

				OutputStream outStream = connectionSocket.getOutputStream();
				ObjectOutputStream out = new ObjectOutputStream(outStream);
				InetSocketAddress sockaddress = (InetSocketAddress) (connectionSocket
						.getRemoteSocketAddress());
				key = sockaddress.getHostName();

				node.outputObjects.put(key, out);
				outputStrm.put(key, outStream);
				objOutputStrm.put(key, out);

				out.writeObject(node.processID + ": hi " + key);
				node.peers.put(key, connectionSocket);

			}
			System.out.println("All peers are connected");

			synchronized (node) {
				node.systemUp = true;
			}

			Message msg = null;

			while (!node.terminated || !node.outQ.isEmpty()) {
				// Thread.sleep(5);

				if (node.outQ.size() != 0) {
					msg = node.outQ.peek();

					if (msg != null) {
						msg.setSender(node.processID);

						// CHECK FOR TERMINATION MESSAGE
						if (msg.msg == Message.TERMINATE) {
							for (int i = 0; i < node.otherPeers; i++) {
								msg.setReceiver(node.neighboursNames[i]);
								ObjectOutputStream wrtObj = node.outputObjects
										.get(node.neighboursNames[i]);
								System.out.println("sent:" + msg.toString());
								synchronized (wrtObj) {
									wrtObj.writeObject(msg);
									// Thread.sleep(10);
									wrtObj.flush();
								}
							}
							// ****** CAREFULL ABOUT THE BREAK
							// Thread.sleep(1000);
							// break;
						}

						if (msg.receiver == null) {
							for (int i = 0; i < node.otherPeers; i++) {
								msg.setReceiver(node.neighboursNames[i]);
								System.out.println("sent:" + msg.toString());
								ObjectOutputStream wrtObj = node.outputObjects
										.get(node.neighboursNames[i]);
								synchronized (wrtObj) {
									wrtObj.writeObject(msg);
								}
							}
						} else {
							ObjectOutputStream wrtObj = node.outputObjects
									.get(msg.receiver);
							System.out.println("sent:" + msg.toString());
							synchronized (wrtObj) {
								wrtObj.writeObject(msg);
							}
						}
					} else {
						System.out.println("OUT QUEUE IS EMPTY "
								+ node.outQ.size());
						msg = node.outQ.peek();
					}
					synchronized (node.outQ) {
						node.outQ.poll();
					}
				}
			}
			Thread.sleep(1000);
			for (String key : node.neighboursNames) {
				System.out.println("Closing streams and socket:" + key);
				objInputStrm.get(key).close();
				inputStrm.get(key).close();
				objOutputStrm.get(key).close();
				outputStrm.get(key).close();
				node.peers.get(key).close();
			}

			System.out.println("Listener thread is exiting...");
		} catch (Exception e) {
			e.printStackTrace();
			// clientNo++;// client id starts with 0
		}
	}
}