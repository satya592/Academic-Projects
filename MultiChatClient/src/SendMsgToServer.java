import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class SendMsgToServer {
	Socket clientSocket;
	DataOutputStream outToServer = null;

	private SendMsgToServer(Socket clientSocket) {
		this.clientSocket = clientSocket;
		try {
			outToServer = new DataOutputStream(clientSocket.getOutputStream());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private static SendMsgToServer instance = null;

	public static SendMsgToServer getInstance(Socket clientSocket) {
		if (instance == null)
			instance = new SendMsgToServer(clientSocket);
		return instance;
	}

	public static SendMsgToServer getInstanceCreated() {
		return instance;
	}

	public void sendThisToServer(String Msg) {
		if (clientSocket.isConnected()) {
			try {
				outToServer.writeBytes(Msg + '\n');
				System.out.println(Msg);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else {
			ClientWindow.getInstance().setHistory("Connection Failed");
		}
	}
}
