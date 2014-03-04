import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

class Responder {

	// on client process termination or
	// client sends EXIT then to return false to close connection
	// else return true to keep connection alive
	// and continue conversation
	public void responderMethod(Socket connectionSocket, int clientNo,
			String uID) throws Exception {
		BufferedReader inFromClient = new BufferedReader(new InputStreamReader(
				connectionSocket.getInputStream()));
		// // Client Authorization
		ChatRoom.getInstance().AddClient(clientNo, connectionSocket, uID);
		ChatRoom.getInstance().sendData(
				"client " + clientNo + "," + uID + " joined chat",
				connectionSocket, clientNo, uID);
		ChatRoom.getInstance().sendData(uID, connectionSocket, clientNo,
				"joined");

		// }
		String clientSentence = "";
		while (true) {

			try {
				System.out.println("Waiting for input:" + clientNo + "," + uID);

				clientSentence = inFromClient.readLine();
				System.out.println("Read:" + clientSentence + " from " + uID
						+ "," + clientNo);
				// if client process terminates it get null, so close connection
				if (clientSentence == null || clientSentence.equals("EXIT")) {
					ChatRoom.getInstance().RemoveClient(clientNo, uID);
					Thread.sleep(100);
					ChatRoom.getInstance().sendData(uID, connectionSocket,
							clientNo, "left");

					MultiServer.removedClient(clientNo);
					break;
				} else if (clientSentence != null) {
					ChatRoom.getInstance().sendData(clientSentence,
							connectionSocket, clientNo, uID);
				}
			} catch (Exception ex) {
				Logger.getLogger(MyServer.class.getName()).log(Level.SEVERE,
						null, ex);
				ex.printStackTrace();
			}
		}
	}
}