import java.awt.Color;
import java.awt.Component;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.SystemColor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JButton;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JViewport;
import javax.swing.border.CompoundBorder;

public class WB {

	private JFrame frame;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					WB window = new WB();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public WB() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame("Chat");
		frame.getContentPane().setBackground(SystemColor.activeCaption);
		frame.setBounds(100, 100, 427, 296);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);
		frame.setResizable(false);
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(10, 0, 296, 202);
		frame.getContentPane().add(scrollPane);

		final JEditorPane editorPane = new JEditorPane();
		editorPane.setBorder(new CompoundBorder());
		editorPane.setBackground(Color.WHITE);
		editorPane.setEditable(false);
		scrollPane.setViewportView(editorPane);

		JScrollPane scrollPane_1 = new JScrollPane();
		scrollPane_1.setBounds(10, 213, 296, 49);
		frame.getContentPane().add(scrollPane_1);

		JEditorPane editorPane_1;
		editorPane_1 = new JEditorPane();
		scrollPane_1.setViewportView(editorPane_1);

		JButton btnSend = new JButton("SEND");
		btnSend.setFont(new Font("Tahoma", Font.BOLD, 15));
		btnSend.setBackground(Color.LIGHT_GRAY);
		btnSend.setToolTipText("Click here to send the message");
		btnSend.setForeground(Color.BLACK);
		// btnSend.setBackground(bg);;
		btnSend.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
			}
		});
		btnSend.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(MouseEvent e) {
				Component edit1 = frame.getContentPane().getComponent(0);
				Component edit2 = frame.getContentPane().getComponent(1);
				// edit1.setVisible(false);
				// edit2.setVisible(false);
				JViewport viewport1 = ((JScrollPane) edit1).getViewport();
				JViewport viewport2 = ((JScrollPane) edit2).getViewport();

				JEditorPane editorPane1 = (JEditorPane) viewport1.getView();
				JEditorPane editorPane2 = (JEditorPane) viewport2.getView();
				// if(editorPane2.getText().isEmpty())
				editorPane1.setText("hi this is ur chat history" + "\n"
						+ editorPane2.getText());
				editorPane2.setText("");
				frame.repaint();
			}
		});
		btnSend.setBounds(316, 213, 95, 49);
		frame.getContentPane().add(btnSend);

	}
}
