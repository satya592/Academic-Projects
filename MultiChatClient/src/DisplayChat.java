import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DisplayChat implements Runnable {
	Socket clientSocket;

	// File file;

	DisplayChat(Socket clientSocket) {
		// String filename = "ChatHistory" + new Date().hashCode() + ".txt";
		// System.out.println(filename);
		// file = new File(filename);
		// if (!file.exists()) {
		// try {
		// file.createNewFile();
		// } catch (IOException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }
		// }

		this.clientSocket = clientSocket;

	}

	public void Display() {
		try {

			BufferedReader inFromServer = new BufferedReader(
					new InputStreamReader(clientSocket.getInputStream()));
			System.out.println("waiting for server..");
			// String WelcomeNote = "\\m/Welcome to the chat room:";
			String sentence = inFromServer.readLine();
			System.out.println(sentence);
			StringTokenizer stkn = new StringTokenizer(sentence, ":");

			System.out.println("Token count for" + sentence + "="
					+ stkn.countTokens());
			System.out.println("Msg received:" + sentence);
			if (stkn.countTokens() != 1) {
				String token = stkn.nextToken();
				if (token.equals("joined")) {
					ClientWindow.getInstance().addToActiveFriends(
							stkn.nextToken());
				} else if (token.equals("left")) {
					ClientWindow.getInstance().removeToActiveFriends(
							stkn.nextToken());
				} else if (token.equals("add")) {
					ClientWindow.getInstance().addToActiveFriends(
							stkn.nextToken());
				} else {
					ClientWindow.getInstance().addToActiveFriends(token);
				}
			} else {
				// do nothing
			}
			// Display msg from the server
			if (ClientWindow.getInstance() != null) {
				ClientWindow.getInstance().setHistory(sentence);
				ClientWindow.getInstance().redraw();
			} else {
				System.out.println("ClientWindow is not initilized");
			}
			// bw.close();

		} catch (Exception ex) {
			Logger.getLogger(MyServer.class.getName()).log(Level.SEVERE, null,
					ex);
			ex.printStackTrace();
		}
	}

	@Override
	public void run() {
		// Authorization part
		// BufferedReader inFromServer;
		// try {
		// inFromServer = new BufferedReader(new InputStreamReader(
		// clientSocket.getInputStream()));
		// String status = inFromServer.readLine();
		// System.out.println(status);
		// if (status == "fail") {
		// System.out.println("Failed");
		// return;
		// } else {
		// System.out.println("Success in display");
		// }
		// } catch (IOException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }

		while (true) {
			System.out.println("Disply thread started..");
			Display();
		}
	}
}