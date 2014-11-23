import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.ConnectException;
import java.net.Socket;
import java.net.SocketTimeoutException;

public class Sender implements Runnable {
	String serverName;
	int port;
	int reconnections = 0;
	final int MAX_RECONNECTIONS = 15;
	Node node;

	Sender(String server, String port, Node node) {
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

	public void connect() {

		try {
			System.out.println("Connecting to " + serverName + " on port "
					+ port);
			Socket client = new Socket(serverName, port);
			// node.peers.put(key, client);
			System.out.println("Just connected to server "
					+ client.getRemoteSocketAddress());
			OutputStream outToServer = client.getOutputStream();
			ObjectOutputStream out = new ObjectOutputStream(outToServer);

			out.writeObject("Hello from " + client.getLocalSocketAddress());

			InputStream inFromServer = client.getInputStream();
			ObjectInputStream in = new ObjectInputStream(inFromServer);
			System.out.println(in.readObject());

			node.inputObjects_Send.put(serverName, in);

			Message msg = null;
			while (!node.terminated) {
				// Thread.sleep(10);
				msg = (Message) in.readObject();
				System.out.println(msg);
				synchronized (node.inQ) {
					node.inQ.add(msg);
				}
				if (msg.msg == Message.TERMINATE) {
					System.out.println("Termination message received");
					msg = new Message(Message.CNT, node.processID, msg.sender);
					node.outQ.add(msg);
					Thread.sleep(1000);
					node.terminated = true;
					// break;
				}
				if (msg.msg == Message.CNT && node.initiator) {
					System.out.println("Termination CNT message received from "
							+ msg.sender);
					node.terminated = true;
				}
			}
			System.out.println("Exiting Sender.." + node.processID);
			Thread.sleep(1000);
			client.close();
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
