import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

class MyServer implements Runnable {

	Responder h;
	int clientNo;
	Socket connectionSocket;

	public MyServer(Responder h, int clientNo, Socket connectionSocket) {
		// this.h = h;
		this.h = new Responder();
		this.connectionSocket = connectionSocket;
		this.clientNo = clientNo;
	}

	@Override
	public void run() {
		String auth = "";
		try {
			BufferedReader inFromClient = new BufferedReader(
					new InputStreamReader(connectionSocket.getInputStream()));
			// Client Authorization
			// System.out.println("Waiting for auth input:" + clientNo);
			int retry = Configuration.MaxRetries;
			while (retry > 0) {
				auth = inFromClient.readLine();
				System.out.println("Read:" + auth + " from " + clientNo);
				System.out
						.println(Authentication.getInstance().auth.toString());
				DataOutputStream outToClient = new DataOutputStream(
						connectionSocket.getOutputStream());

				if (Authentication.getInstance().validation(auth) == false) {
					ChatRoom.getInstance().RemoveClient(clientNo);
					System.out.println("Auth failed for " + clientNo + ","
							+ auth.substring(0, auth.indexOf('=')));
					outToClient.writeBytes("fail\n");
					retry--;
					// MultiServer.removedClient(clientNo);
				} else {
					outToClient.writeBytes("Success\n");
					System.out.println("Success\n");
					break;
				}
			}
			if (retry == 0)
				return;
			h.responderMethod(connectionSocket, this.clientNo,
					auth.substring(0, auth.indexOf('=')));
		} catch (Exception ex) {
			Logger.getLogger(MyServer.class.getName()).log(Level.SEVERE, null,
					ex);
			ex.printStackTrace();
		}
	}
}