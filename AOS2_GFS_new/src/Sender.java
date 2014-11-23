import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;

/**
 * A simple Swing-based client for the capitalization server. It has a main
 * frame window with a text field for entering strings and a textarea to see the
 * results of capitalizing them.
 */
public class Sender {

	final static int HBSENDER = 1;
	final static int MSGSENDER = 2;
	final static int META_MSGSENDER = 3;
	int type;

	/**
	 * Constructs the client by laying out the GUI and registering a listener
	 * with the textfield so that pressing Enter in the listener sends the
	 * textfield contents to the server.
	 */
	public Sender(int type) {
		this.type = type;
	}

	/**
	 * Implements the connection logic by prompting the end user for the
	 * server's IP address, connecting, setting up streams, and consuming the
	 * welcome messages from the server. The HeartBeatListener protocol says
	 * that the server sends three lines of text to the client immediately after
	 * establishing a connection.
	 */
	static public void connectToServer(String serverAddress, String port,
			FileSystem Fs) {

		// Get the server address from a dialog box.
		// Make connection and initialize streams
		try {
			Socket socket = new Socket(serverAddress, Integer.valueOf(port));
			BufferedReader in = new BufferedReader(new InputStreamReader(
					socket.getInputStream()));
			OutputStream outStream = socket.getOutputStream();
			ObjectOutputStream out = new ObjectOutputStream(outStream);

			// Consume the initial welcoming messages from the server
			System.out.println(in.readLine() + "\n");
			while (true) {
				try {
					Thread.sleep(4000);
					FileSystem newFs = new FileSystem(Fs);
					System.out.println("Sending fs: " + newFs);
					out.writeObject(newFs);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		} catch (NumberFormatException | IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

	}

	static public Message messageToFileServer(String serverAddress,
			String port, Message msg) {
		// Get the server address from a dialog box.
		// Make connection and initialize streams
		log("trying to connect..");
		Message returnMsg = null;

		try {
			Socket socket = new Socket(serverAddress, Integer.valueOf(port));
			log("connected..");

			OutputStream outStream = socket.getOutputStream();
			ObjectOutputStream out = new ObjectOutputStream(outStream);

			ObjectInputStream in = new ObjectInputStream(
					socket.getInputStream());

			// Consume the initial welcoming messages from the server
			// System.out.println(in.readLine() + "\n");
			try {
				out.writeObject(msg);
				log("Seding msg:" + msg);
				returnMsg = (Message) in.readObject();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} finally {
				try {
					socket.close();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
		} catch (NumberFormatException | IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		return returnMsg;
	}

	static public MetaMessage messageToMetaServer(String serverAddress,
			String port, MetaMessage msg) {
		// Get the server address from a dialog box.
		// Make connection and initialize streams
		log("trying to connect..");
		Socket socket;
		MetaMessage returnMsg = null;

		try {
			socket = new Socket(serverAddress, Integer.valueOf(port));
			log("connected..");

			OutputStream outStream = socket.getOutputStream();
			ObjectOutputStream out = new ObjectOutputStream(outStream);

			ObjectInputStream in = new ObjectInputStream(
					socket.getInputStream());

			// Consume the initial welcoming messages from the server
			// System.out.println(in.readLine() + "\n");
			try {
				out.writeObject(msg);
				log("Seding msg:" + msg);
				returnMsg = (MetaMessage) in.readObject();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} finally {
				try {
					socket.close();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
		} catch (NumberFormatException | IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		return returnMsg;
	}

	private static void log(String message) {
		// System.out.println(message);
	}
}