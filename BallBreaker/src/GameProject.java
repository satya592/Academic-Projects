import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class GameProject extends JApplet 
{
	
	public static void main(String [] args)
	{
		//	create a new frame
		JFrame frame = new JFrame("BallBreaker*satyawannu@yahoo.com*08144065565/09000551718");
		
		//make a panel
		GamePanel panel = new GamePanel();

		//put panel in frame
		frame.getContentPane().add(panel);

		//set the frames size
		frame.setSize(515, 700);
		
		//make the frame not resizable 
		frame.setResizable(false);
		//Applet ends when u close window  
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		//make the frame visible
		frame.setVisible(true);


	}
	public void init()
	{
		JPanel panel = new GamePanel();
		getContentPane().add(panel);
	}
	
	
}