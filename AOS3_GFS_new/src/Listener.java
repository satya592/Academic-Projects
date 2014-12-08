import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ConcurrentHashMap;

public class Listener {

	/**
	 * A private thread to handle capitalization requests on a particular
	 * socket. The client terminates the dialogue by sending a single line
	 * containing only a period.
	 */
	Listener(int port, int type) {
		System.out.println("The Listener is running.");

		int clientNumber = 0;
		ServerSocket listener = null;
		try {
			listener = new ServerSocket(port);

			while (type == ListenerWrapper.DEMON) {//HeartBeats
				ConcurrentHashMap<String, HeartBeatListener> clients = new ConcurrentHashMap<String, HeartBeatListener>();

				Socket socket = listener.accept();
				String clientName = socket.getInetAddress().getHostAddress();
				log("Listening to " + clientName);
				if (clients.get(clientName) != null) {
					log((socket.toString()));
					clients.get(clientName).interrupt();
				}
				HeartBeatListener client = new HeartBeatListener(socket,
						clientNumber++);
				clients.put(clientName, client);
				client.start();
			}
			log("Listing requests...");
			while (type == ListenerWrapper.REQ) {
				Socket socket = listener.accept();
				String clientName = socket.getInetAddress().getHostAddress();
				log("Received msg from:" + clientName);
				Thread msgRequestHandler = new Thread(new RequestHandler(
						socket, ListenerWrapper.REQ));
				msgRequestHandler.start();
			}
			while (type == ListenerWrapper.SER_REQ) {
				Socket socket = listener.accept();
				String clientName = socket.getInetAddress().getHostAddress();
				log("Received msg from:" + clientName);
				Thread msgRequestHandler = new Thread(new RequestHandler(
						socket, ListenerWrapper.REQ));
				msgRequestHandler.start();
			}
			while (type == ListenerWrapper.META_REQ) {
				Socket socket = listener.accept();
				String clientName = socket.getInetAddress().getHostAddress();
				log("Received meta msg from:" + clientName);
				Thread metamsgRequestHandler = new Thread(new RequestHandler(
						socket, ListenerWrapper.META_REQ));
				metamsgRequestHandler.start();
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				log("listener closed");
				listener.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

	private void log(String message) {
		System.out.println(message);
	}

	class RequestHandler implements Runnable {

		ObjectOutputStream out;
		ObjectInputStream in;
		int type;

		RequestHandler(Socket socket, int type) {
			try {
				in = new ObjectInputStream(socket.getInputStream());
				out = new ObjectOutputStream(socket.getOutputStream());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			this.type = type;
		}

		@Override
		public void run() {
			try {
				if (type == ListenerWrapper.META_REQ) {
					MetaMessage msg;
					msg = (MetaMessage) in.readObject();
					log(msg.toString());
					MetaMessage ret_msg = MetaServer.requestHandler(msg);
					out.writeObject(ret_msg);
				} else if (type == ListenerWrapper.REQ) {
					Message msg = (Message) in.readObject();
					log(msg.toString());
					Message ret_msg = Server.requestHandler(msg);
					out.writeObject(ret_msg);
				}
				
				else if (type == ListenerWrapper.SER_REQ) {
					Message msg = (Message) in.readObject();
					log(msg.toString());
					Message ret_msg = Server.serverRequestHandler(msg);
					out.writeObject(ret_msg);
				}
			} catch (ClassNotFoundException | IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
	}

	class HeartBeatListener extends Thread {
		private Socket socket;
		private int clientNumber;

		public HeartBeatListener(Socket socket, int clientNumber) {
			this.socket = socket;
			this.clientNumber = clientNumber;
			log("New connection with client# " + clientNumber + " at " + socket);
		}

		/**
		 * Services this thread's client by first sending the client a welcome
		 * message then repeatedly reading strings and sending back the
		 * capitalized version of the string.
		 */
		public void run() {
			try {

				ObjectInputStream in = new ObjectInputStream(
						socket.getInputStream());

				PrintWriter out = new PrintWriter(socket.getOutputStream(),
						true);
				String serverName = socket.getInetAddress().getHostName();
				// Send a welcome message to the client.
				out.println("Hello, you are client #" + clientNumber + ".");

				// Get messages from the client, line by line; return them
				// capitalized
				int count = 0;

				MetaServer.updateMetaData(serverName,
						new FileSystem(serverName));

				while (count < 3) {
					try {
						Thread.sleep(4000);
						if (socket.getInputStream().available() > 0) {
							count = 0;
							FileSystem input = (FileSystem) in.readObject();

							log("HB received:" + input.toString());

							if (MetaServer.updateMetaData(serverName, input))
								log("Metadata updated");
							else
								log("Already up to date");

						} else
							count++;

					} catch (Exception e) {
						e.printStackTrace();
					}
				}

				MetaServer.setServer(serverName, false, null);

			} catch (IOException e) {
				log("Error handling client# " + clientNumber + ": " + e);
			} finally {
				try {
					socket.close();
				} catch (IOException e) {
					log("Couldn't close a socket, what's going on?");
				}
				log("Connection with client# " + clientNumber + " closed");
			}
		}

		/**
		 * Logs a simple message. In this case we just write the message to the
		 * server applications standard output.
		 */
		private void log(String message) {
			// System.out.println(message);
		}
	}

}