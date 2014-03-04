import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.SystemColor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JButton;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JViewport;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingConstants;
import javax.swing.border.CompoundBorder;

public class ClientWindow {

	private JFrame frmMultiChatWindow;
	private StringBuilder History;
	private StringBuilder ActiveFriends;
	private String UserName;;
	private boolean allSet = false;
	private static ClientWindow instance = null;
	private JTextField textField;

	public static ClientWindow getInstance(String username) {
		if (instance == null)
			instance = new ClientWindow(username);
		return instance;
	}

	public static ClientWindow getInstance() {
		return instance;
	}

	public synchronized void setHistory(String msgReceived) {
		History.append(msgReceived);
		History.append("\n");
		System.out.println("SetHistory:" + msgReceived);
		if (allSet == false) {
			initialize();
		}
		Component edit1 = frmMultiChatWindow.getContentPane().getComponent(0);
		JViewport viewport1 = ((JScrollPane) edit1).getViewport();
		JEditorPane editorPane1 = (JEditorPane) viewport1.getView();
		editorPane1.setText(ClientWindow.getInstance().getHistory());
		redraw();
		frmMultiChatWindow.repaint();
	}

	public synchronized String getHistory() {
		return History.toString();
	}

	public synchronized void redraw() {
		frmMultiChatWindow.repaint();
	}

	/**
	 * Launch the application. Create the application.
	 */
	private ClientWindow(String userName) {
		History = new StringBuilder();// .append('\0');
		this.UserName = userName;
		this.ActiveFriends = new StringBuilder();

		frmMultiChatWindow = new JFrame("Multi Chat Window " + (char) 0xA9
				+ " Author: Satyam & Sindhu");

		frmMultiChatWindow.setFont(new Font("Dialog", Font.BOLD, 10));
		frmMultiChatWindow.setTitle("Multi Chat Window " + (char) 0xA9
				+ " Author: Satyam & Sindhu");
		frmMultiChatWindow.getContentPane().setBackground(
				SystemColor.activeCaption);
		frmMultiChatWindow.setBounds(100, 100, 427, 296);
		frmMultiChatWindow.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		frmMultiChatWindow.getContentPane().setLayout(null);
		frmMultiChatWindow.setResizable(false);
		JScrollPane scrollPane = new JScrollPane();
		scrollPane
				.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		scrollPane.setBounds(10, 0, 296, 202);
		frmMultiChatWindow.getContentPane().add(scrollPane);

		final JEditorPane editorPane = new JEditorPane();
		editorPane.setBorder(new CompoundBorder());
		editorPane.setBackground(Color.WHITE);
		editorPane.setEditable(false);
		editorPane.setName("ChatHistory");
		scrollPane.setViewportView(editorPane);

		JScrollPane scrollPane_1 = new JScrollPane();
		scrollPane_1
				.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		scrollPane_1.setBounds(10, 213, 296, 49);
		frmMultiChatWindow.getContentPane().add(scrollPane_1);

		JEditorPane editorPane_1;
		editorPane_1 = new JEditorPane();
		scrollPane_1.setViewportView(editorPane_1);
		editorPane_1.setName("TextEditor");

		JButton btnSend = new JButton("SEND");
		btnSend.setName("SendButton");
		btnSend.setFont(new Font("Tahoma", Font.BOLD, 15));
		btnSend.setBackground(Color.LIGHT_GRAY);
		btnSend.setToolTipText("Click here to send the message");
		btnSend.setForeground(Color.BLACK);

		btnSend.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
			}
		});
		btnSend.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(MouseEvent e) {
				Component edit1 = frmMultiChatWindow.getContentPane()
						.getComponent(0);
				Component edit2 = frmMultiChatWindow.getContentPane()
						.getComponent(1);
				// edit1.setVisible(false);
				// edit2.setVisible(false);
				JViewport viewport1 = ((JScrollPane) edit1).getViewport();
				JViewport viewport2 = ((JScrollPane) edit2).getViewport();

				JEditorPane editorPane1 = (JEditorPane) viewport1.getView();
				JEditorPane editorPane2 = (JEditorPane) viewport2.getView();
				// if(editorPane2.getText().isEmpty())
				String sendingText = editorPane2.getText();
				if (!sendingText.isEmpty()) {
					ClientWindow.getInstance().setHistory("Me :" + sendingText);
					editorPane1
							.setText(ClientWindow.getInstance().getHistory());
					editorPane2.setText("");
					SendMsgToServer.getInstanceCreated().sendThisToServer(
							sendingText);
				}
				redraw();
			}
		});
		btnSend.setBounds(316, 213, 95, 49);
		frmMultiChatWindow.getContentPane().add(btnSend);
		frmMultiChatWindow.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				JFrame frame = (JFrame) e.getSource();

				int result = JOptionPane.showConfirmDialog(frame,
						"Are you sure you want to exit the application?",
						"Exit Application", JOptionPane.YES_NO_OPTION);

				if (result == JOptionPane.YES_OPTION) {
					System.out.println("EXIT");
					SendMsgToServer.getInstanceCreated().sendThisToServer(
							"EXIT");
					frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				}
			}
		});
		// btnSend.setBackground(bg);;

		JEditorPane editorPane_2 = new JEditorPane();
		editorPane_2.setFont(new Font("Tahoma", Font.ITALIC, 11));
		editorPane_2.setBackground(SystemColor.inactiveCaptionBorder);
		editorPane_2.setEditable(false);
		editorPane_2.setBounds(316, 50, 95, 152);
		editorPane_2.setName("Friends List");
		frmMultiChatWindow.getContentPane().add(editorPane_2);

		JLabel lblActiveFriends = new JLabel("Active Friends");
		lblActiveFriends.setFont(new Font("Tahoma", Font.BOLD, 12));
		lblActiveFriends.setForeground(Color.BLACK);
		lblActiveFriends.setBackground(Color.LIGHT_GRAY);
		lblActiveFriends.setBounds(316, 25, 95, 14);
		frmMultiChatWindow.getContentPane().add(lblActiveFriends);

		textField = new JTextField();
		textField.setBackground(SystemColor.controlShadow);
		textField.setHorizontalAlignment(SwingConstants.CENTER);
		textField.setFont(new Font("Tahoma", Font.BOLD, 13));
		textField.setEditable(false);
		textField.setBounds(316, 0, 86, 20);
		frmMultiChatWindow.getContentPane().add(textField);
		textField.setColumns(10);
		textField.setName("UserName");
		textField.setText(UserName + "'s");
		Component[] comps = frmMultiChatWindow.getContentPane().getComponents();

		// List<Component> compList = new ArrayList<Component>();
		int i = 0;
		System.out.println("Components");
		for (Component comp : comps) {
			System.out.println(i + ":" + comp.getName() + "=>"
					+ comp.toString());
			i++;
		}

		allSet = true;

		frmMultiChatWindow.setVisible(true);
		// EventQueue.invokeLater(new Runnable() {
		// public void run() {
		// initialize();
		// // frmMultiChatWindow.setVisible(true);
		// }
		// });
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {

	}

	public StringBuilder getActiveFriends() {
		return ActiveFriends;
	}

	public void addToActiveFriends(String friendName) {
		if (ActiveFriends.toString().contains(friendName) == false) {
			System.out.println("Appending " + friendName
					+ "to active friends list");
			ActiveFriends.append(friendName);
			ActiveFriends.append("\n");
			if (this.allSet == false) {
				this.initialize();
			}
			JEditorPane edit1 = (JEditorPane) frmMultiChatWindow
					.getContentPane().getComponent(3);
			edit1.setText(ActiveFriends.toString());
			redraw();
		}
	}

	public void removeToActiveFriends(String friendName) {
		if (ActiveFriends.toString().contains(friendName) == true) {
			System.out.println("Removig " + friendName
					+ "to active friends list");
			ActiveFriends = new StringBuilder(ActiveFriends.toString().replace(
					friendName + "\n", ""));
			JEditorPane edit1 = (JEditorPane) frmMultiChatWindow
					.getContentPane().getComponent(3);
			edit1.setText(ActiveFriends.toString());
			redraw();
		}

	}

	public String getUserName() {
		return UserName;
	}

	public void setUserName(String userName) {
		UserName = userName;
		JTextField edit1 = (JTextField) frmMultiChatWindow.getContentPane()
				.getComponent(5);
		edit1.setText(UserName + "'s");
		redraw();
	}
}
