import java.awt.Color;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.Socket;

import javax.swing.Box;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

public class MyClient {

	public static void main(String argv[]) throws Exception {
		// String sentence;
		// String modifiedSentence;

		// BufferedReader inFromUser = new BufferedReader(new InputStreamReader(
		// System.in));
		String serverPort = Configuration.portNo;

		// String userID = JOptionPane.showInputDialog("Enter user id\n");
		JPanel panel = new JPanel();
		JPanel panel2 = new JPanel();
		JPanel maxRetry = new JPanel();
		JLabel Idlabel = new JLabel("User-ID :");
		JLabel label = new JLabel("Password:");
		JLabel errormsg = new JLabel("Error-UserID/Password mismatch");
		errormsg.setForeground(Color.RED);
		JLabel maxRetry1 = new JLabel("You have reached max retries");
		maxRetry1.setForeground(Color.RED);

		JTextField id = new JPasswordField(10);
		JPasswordField pass = new JPasswordField(10);
		panel.add(Idlabel);
		panel.add(id);
		panel.add(Box.createHorizontalStrut(15)); // a spacer
		panel.add(label);
		panel.add(pass);
		maxRetry.add(maxRetry1);
		String[] options = new String[] { "OK", "Cancel" };
		boolean auth = true;
		Socket clientSocket = null;
		DataOutputStream outToServer = null;
		BufferedReader inFromServer = null;
		int option = 1;
		String Id = "";
		String pwd = "";
		String status = "";
		String authcheck = "";
		int retry = Configuration.MaxRetries;
		do {
			auth = true;
			option = 1; // clear the fields
			while (option == 1) {
				id.setText("");
				pass.setText("");
				System.out.println(option);
				id.setFocusable(true);
				option = JOptionPane.showOptionDialog(null, panel,
						"User Authentication", JOptionPane.NO_OPTION,
						JOptionPane.PLAIN_MESSAGE, null, options, options[1]);
			}
			if (option == 0) // pressing OK button
			{
				Id = id.getText();
				char[] password = pass.getPassword();
				pwd = new String(password);
				System.out.println(Id + " Your password is: " + pwd);
			} else {// close the login and exit
				System.out.println("BYE BYE");
				return;
			}
			if (clientSocket == null || clientSocket.isConnected() == false) {
				try {
					clientSocket = new Socket(Configuration.server,
							Integer.parseInt(serverPort));
					outToServer = new DataOutputStream(
							clientSocket.getOutputStream());
					inFromServer = new BufferedReader(new InputStreamReader(
							clientSocket.getInputStream()));
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
			authcheck = Id + "=" + pwd;
			if (clientSocket.isConnected() && auth) {
				System.out.println("Auth request sent to Server");
				outToServer.writeBytes(authcheck + "\n");
				System.out.println("waiting from Server");
				status = inFromServer.readLine();
				System.out.println(status);
				if (status.equals("fail")) {
					retry--;
					auth = false;
					panel2.add(errormsg);
					JOptionPane.showMessageDialog(null, panel2,
							"Incorrect ID/Password", JOptionPane.ERROR_MESSAGE,
							null);
				}
			}
		} while (auth == false && retry != 0);// && option != -1);
		if (retry == 0) {
			JOptionPane.showMessageDialog(null, maxRetry,
					"User Authentication Failed", JOptionPane.ERROR_MESSAGE,
					null);
		}
		if (auth == true) {
			// Always create this first, which will send msg to server on
			// request, Before using it, it has to be created
			SendMsgToServer.getInstance(clientSocket);

			ClientWindow.getInstance(Id);
			Thread t = new Thread(new DisplayChat(clientSocket));
			// start thread
			t.setPriority(Thread.MAX_PRIORITY);
			t.start();

			ClientWindow.getInstance().setHistory(
					"Welcome to the Chat-Room,"
							+ ClientWindow.getInstance().getUserName());
			ClientWindow.getInstance().redraw();

			try {
				t.join();
			} catch (InterruptedException e) {
			}
		} else {
			System.out.println("auth failed");
		}
		clientSocket.close();
	}
}