import java.io.DataOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;

public class ChatRoom {
	HashMap<Integer, Socket> clientslist = null;// new
	HashMap<Integer, String> clientsIDs = null;// HashMap<Integer,Socket>();
	ArrayList<Integer> DeadClients = null;
	private static ChatRoom instance = null;
	boolean firstTime = false;

	// File file;

	protected ChatRoom() {
		// Exists only to defeat instantiation.
		clientslist = new HashMap<Integer, Socket>();
		clientsIDs = new HashMap<Integer, String>();
		DeadClients = new ArrayList<Integer>();
		firstTime = true;
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

	}

	public static ChatRoom getInstance() {
		if (instance == null) {
			instance = new ChatRoom();
		}
		return instance;
	}

	synchronized public void AddClient(Integer ClientNo, Socket SId, String Uid) {
		System.out.println("Adding client" + ClientNo + "sid=" + Uid);
		this.clientslist.put(ClientNo, SId);
		this.clientsIDs.put(ClientNo, Uid);
	}

	synchronized public void RemoveClient(Integer ClientNo) {
		{
			this.clientslist.remove(ClientNo);
			this.clientsIDs.remove(ClientNo);
		}
	}

	public void RemoveClient(Integer ClientNo, String uID) {
		{
			// sendData(uID, ClientNo, int ClientId, "Removed");
			RemoveClient(ClientNo);

		}
	}

	public void RemoveClient(Socket sID) {
		{
			for (Integer i : this.clientslist.keySet()) {
				if (sID == this.clientslist.get(i)) {
					this.RemoveClient(i);
				}
			}
		}
	}

	public void AreAlive() {
		for (Socket sID : this.clientslist.values()) {
			if (sID.isConnected() == false) {
				this.RemoveClient(sID);
			}
		}
	}

	public void sendData(String msg, Socket clientNo, int ClientId, String uID)
			throws Exception {
		// Broadcasting msg to all the clients in chat-room except to the same
		for (Socket sID : this.clientslist.values()) {
			if (sID != clientNo) {
				System.out.println("broadcasting:" + msg + " from" + clientNo
						+ "," + uID);
				DataOutputStream outToClient = new DataOutputStream(
						sID.getOutputStream());
				outToClient.writeBytes(uID + ":" + msg + "\n");
			} else {
				// Send Welcome msg to new client
				if (this.clientslist.size() == 1 && this.firstTime == true) {
					System.out.println("broadcasting:" + msg + " from"
							+ clientNo + "," + uID);
					DataOutputStream outToClient = new DataOutputStream(
							sID.getOutputStream());
					outToClient
							.writeBytes("Welcome to New chatroom, ur chatroom hasbeen created\n");
					firstTime = false;
				} else if (uID.equals("joined")) {
					if (sID == clientNo) {
						System.out.println("Welcome-" + msg + " to" + clientNo
								+ "," + uID);
						DataOutputStream outToClient = new DataOutputStream(
								sID.getOutputStream());
						outToClient.writeBytes("\\m/Welcome to the chat room-"
								+ msg + "\\m/\n");
						for (String id : this.clientsIDs.values()) {
							System.out
									.println("Client IDs: So far in the queue:"
											+ id);
							System.out.println("comparing:" + id + "&" + msg);
							if (id.equals(msg) == false) {
								System.out.println("Add user msg sent");
								outToClient.writeBytes("add:" + msg + "\n");
							} else {
								System.out.println("Same user so dont add");
							}
						}
					}
				}

			}
		}
	}
}