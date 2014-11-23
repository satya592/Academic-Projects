import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.Socket;
import javax.swing.JOptionPane;
/**
 * 
 */
/**
 * @author Satyam
 */
public class Client {
	public static void main(String[] args) throws Exception{
			String serverAddress = JOptionPane.showInputDialog(
			"Enter IP Address of a machine that is\n" );
//			String serverAddress = "satyamserver.no-ip.biz";
			String serverPort = JOptionPane.showInputDialog(
			"port number:");
			Socket s = new Socket(serverAddress, Integer.parseInt(serverPort));
//		Socket s = new Socket("x`",7878);
			BufferedReader input =
			new BufferedReader(new InputStreamReader(s.getInputStream()));
			String answer = input.readLine();
			JOptionPane.showMessageDialog(null, answer);
			s.close();
			System.exit(0);
	}

}
