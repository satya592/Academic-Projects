import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class MultiServer {

	private static int totalNoOfClients = 0;
	private static ArrayList<Integer> Clients = new ArrayList<Integer>();

	public synchronized static void removedClient(int clientno) {
		totalNoOfClients--;
		Clients.remove(clientno);
	}

	public static int getTotalNoOfClients() {
		return totalNoOfClients;
	}

	public synchronized static void addClient(int clientno) {
		totalNoOfClients++;
		Clients.add(clientno);
	}

	public static void printTotalNoOfClients() {
		System.out.println("Total No Of Clients: " + totalNoOfClients);
	}

	public static void main(String argv[]) throws Exception {

		String serverAddress = Configuration.server;
		String serverPort = Configuration.portNo;
		int clientNo = 0;
		InetAddress add = InetAddress.getByName(serverAddress);
		ServerSocket welcomeSocket = new ServerSocket(
				Integer.parseInt(serverPort), 10, add);
		ArrayList<Socket> Clients = new ArrayList<Socket>();

		Responder h = new Responder();
		// server runs for infinite time and
		// wait for clients to connect

		while (true) {
			// waiting..
			System.out.println("Waiting..");
			Socket connectionSocket = welcomeSocket.accept();

			Clients.add(connectionSocket);
			clientNo++;// client id starts with 0
			addClient(clientNo);

			// on connection establishment start a new thread for each client
			// each thread shares a common responder object
			// which will be used to respond every client request
			// need to synchronize method of common object not to have
			// unexpected behavior
			Thread t = new Thread(new MyServer(h, clientNo, connectionSocket));
			// start thread
			t.start();

		}
	}
}