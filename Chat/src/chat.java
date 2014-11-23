//Tetris Program

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

class MyCanvas extends JComponent {
    

  /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

public void paint(Graphics g) {
      
      g.drawString("Text: " + "Hi sindhu", 20, 20);
      g.drawString("Attachments: " , 20, 400);  
	  g.drawString("Pic: ", 300, 20);
//	  g.drawRect(20, 20, 250, 400);
//      g.drawRect(290, 20, 120, 75); //Main game box and next shape box */
}
}

 
  
public class chat { 
     public static void main(String[] args) {
  
		JFrame frame = new JFrame();
		JButton button = new JButton("SEND");
		JTextArea tarea = new JTextArea("type here",20,500);
		frame.add(button);
		frame.add(tarea);
		button.setLocation(300,380);
		button.setSize(80,40);
		button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e)	{
               System.exit(0);  
			}
			});  

		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(600,600);
		frame.getContentPane().add(new MyCanvas());
		frame.setVisible(true);
	
//		final JLabel pauseLabel = new JLabel("PAUSE");
//		pauseLabel.setBounds(200, 200, 70 ,20);
//		Font font = new Font("Serif", Font.PLAIN, 22);
//		pauseLabel.setFont(font);
//		pauseLabel.setForeground(Color.blue);
//		pauseLabel.setLocation(220,200);
//		pauseLabel.setVisible(false);
//		frame.add(pauseLabel);
    
		frame.addMouseMotionListener(new MouseMotionListener() {
        
        public void mouseDragged(MouseEvent event) {}

        public void mouseMoved(MouseEvent event) {
//            int xevent = event.getX();
//            int yevent = event.getY();
//            if(xevent > 30 && xevent < 230 && yevent > 50 && yevent < 450)
//                pauseLabel.setVisible(true);
//            else
//                pauseLabel.setVisible(false);
			}
			});
		}
    }
  