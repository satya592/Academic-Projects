/*

    
     
    import java.awt.event.ActionEvent;
    import java.awt.event.ActionListener;
    import java.io.*;
    import java.net.*;
    import java.util.*;
    import java.awt.*;
    import javax.swing.*;
    import javax.swing.border.TitledBorder;
     
    public class Server extends JFrame implements ActionListener {
      /**
		 * 
		 */
		private static final long serialVersionUID = 4756366122853213627L;
	// Text area for displaying contents
      private JTextField toclient = new JTextField();
      private JTextArea display = new JTextArea();
      private JButton send = new JButton("Send/Start Server");
      public DatagramSocket aSocket;
      public DatagramPacket request;
      public DatagramPacket reply;
     
      public static void main(String[] args) {
     
        new Server();
      }
     
        public void actionPerformed(ActionEvent e) {
           if (e.getSource() == send){
               try {
     
               String msg = toclient.getText();
               byte [] xy = msg.getBytes();
               reply = new DatagramPacket(xy,xy.length,
               request.getAddress(), request.getPort());
     
               //sending a reply message to client
                    aSocket.send(reply);
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
           }
        }
     
      public Server() {
     
        JPanel input = new JPanel();
        input.setLayout(new BorderLayout());
        input.setBorder(new TitledBorder("Enter Message"));
        input.add(toclient, BorderLayout.CENTER);
        input.add(send, BorderLayout.EAST);
     
        JPanel output = new JPanel();
        output.setLayout(new BorderLayout());
        output.setBorder(new TitledBorder("Conversation"));
        output.add(display, BorderLayout.CENTER);
     
        JPanel gabung = new JPanel();
        gabung.setLayout(new GridLayout(2, 1));
        gabung.add(input);
        gabung.add(output);
     
        this.getContentPane().add(gabung, BorderLayout.NORTH);
        send.addActionListener(this);
     
        setTitle("Chat Server");
        setSize(500, 300);
        setVisible(true);
     
     
     
         }
     
     
    }

*/