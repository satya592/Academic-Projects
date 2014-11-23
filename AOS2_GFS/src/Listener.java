import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.ConnectException;
import java.net.Socket;
import java.net.SocketTimeoutException;

public class Listener implements Runnable {
	String serverName;
	int port;
	int reconnections = 0;
	final int MAX_RECONNECTIONS = 15;
	Node node;

	Listener(String server, String port, Node node) {
		this.serverName = server;
		this.port = Integer.valueOf(port);
		this.node = node;
	}

	private void tryToReconnect() {

		System.out.println("I will try to reconnect in 10 seconds... ("
				+ this.reconnections + "/10)");
		try {
			Thread.sleep(10000); // milliseconds
		} catch (InterruptedException e) {
		}

		if (this.reconnections < MAX_RECONNECTIONS) {
			this.reconnections++;
			this.connect();

		} else {
			System.out
					.println("Reconnection failed, exeeded max reconnection tries. Shutting down.");
			// this.disconnect();
			System.exit(0);
			return;
		}

	}

	void register(ObjectOutputStream out, ObjectInputStream in) {
		try {
			String tname = Thread.currentThread().getName();
			System.out.println("Thread name:" + tname);
			if (tname.substring(tname.length() - 2).equals("HB")) {
				out.writeObject(new Message(Message.HB_SER, node.processID,
						serverName, null));
				Message msg = (Message) in.readObject();
				if (msg.type == Message.HB_REG) {
					out.writeObject(new Message(Message.HB_ACK, node.processID,
							serverName, null));
					while (true) {
						Thread.sleep(5000);
						System.out.println("Waiting for Heartbeat from "
								+ serverName);
						in.readObject();
					}
				}
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public void connect() {

		try {
			System.out.println("Connecting to " + serverName + " on port "
					+ port);
			Socket client = new Socket(serverName, port);
			// node.peers.put(key, client);
			OutputStream outToServer = client.getOutputStream();
			ObjectOutputStream out = new ObjectOutputStream(outToServer);

			out.writeObject("Hello from " + client.getLocalSocketAddress());
			InputStream inFromServer = client.getInputStream();
			ObjectInputStream in = new ObjectInputStream(inFromServer);
			System.out.println(in.readObject());

			register(out, in);

			synchronized (node) {
				node.inputObjects_Send.put(serverName, in);
				System.out.println(node.inputObjects_Send.size()
						+ ".CONNECTED:" + client.getRemoteSocketAddress());

			}

			Message msg = null;
			while (true) {
				// Thread.sleep(10);
				msg = (Message) in.readObject();
				System.out.println(msg);
				synchronized (node.inQ) {
					node.inQ.add(msg);
				}
			}

		} catch (ConnectException e) {
			System.out.println("Error while connecting. " + e.getMessage());
			this.tryToReconnect();
		} catch (SocketTimeoutException e) {
			System.out.println("Connection: " + e.getMessage() + ".");
			this.tryToReconnect();
		} catch (Exception e) {
			// e.printStackTrace();
		}

	}

	@Override
	public void run() {
		this.connect();
	}

}
