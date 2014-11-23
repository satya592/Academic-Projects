import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetAddress;
//import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Date;

import javax.swing.JOptionPane;

/**
 * 
 */

/**
 * @author Satyam
 *
 */
public class Server {
		public static void main(String[] args) throws IOException {
//			ServerSocket listener = new ServerSocket(7777);
			String serverAddress = JOptionPane.showInputDialog(
			"Enter IP Address of a machine that is\n");
			String serverPort = JOptionPane.showInputDialog(
			"Enter port no\n");
//			String serverPort = "7777";
//		InetSocketAddress address = new InetSocketAddress("97.77.53.171", 7000);
//		InetAddress add = InetAddress.getByName("192.168.205.1");
		InetAddress add = InetAddress.getByName(serverAddress);
		ServerSocket listener = new ServerSocket(Integer.parseInt(serverPort),10,add);
//		ServerSocket listener = new ServerSocket(Integer.parseInt(serverPort));
//		InetSocketAddress address = InetSocketAddress.createUnresolved("satyamserver.no-ip.biz", 9091);
//		System.out.println("Resolved: "+ address.isUnresolved());
//		listener.bind(address);
//		System.out.println(java.net.InetAddress addr = java.net.InetAddress.getLocalHost());
		try {
		while (true) {
		Socket socket = listener.accept();
		try {
//		PrintReader in = new PrintReader	socket.getInputStream();
		PrintWriter out =
		new PrintWriter(socket.getOutputStream(), true);
		//Sending the date back to client
		out.println(new Date().toString() + "  " + InetAddress.getLocalHost().getHostName() 
				+ "  "+  InetAddress.getLocalHost());
		
		} finally {
		socket.close();
		} }
		}finally {listener.close();}
		}
	}
