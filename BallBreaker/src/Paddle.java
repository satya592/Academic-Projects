import javax.swing.*;

import java.awt.*;
import java.awt.event.*;
import java.net.URL;


public class Paddle extends JPanel
{
	ImageIcon pic;
	JPanel panel;

	boolean m_left;
	boolean m_right;

	int paddlex = 212;
	int paddley = 550;

	private class Engine extends Thread
	{
		public void run()
		{
			while(true)
			{
				if( m_left )
				//Moves Paddle to the left
					paddlex -= 10;
					//Makes sure Paddle doesnt go Past Left Wall
					if(paddlex <= 10)
						 paddlex = 10;
				if( m_right )
				//Moves Paddle to the Right
					paddlex += 10;
					//Makes sure Paddle doesnt go past Right Wall
					if(paddlex >= 400)
						paddlex = 400;

				try{ Thread.sleep(20);}
				catch(Exception e){}
			}
		}
	}


	Paddle(JPanel p)
	{
		URL url = getClass().getResource("Paddle.png");
		pic = new ImageIcon(url);
		panel = p;

		//Starts Paddle's
		Engine engine = new Engine();
		engine.start();

	}

	public void draw(Graphics g)
	{
		//Draws the Paddle at the Default Location
		//g.drawRoundRect(paddlex, paddley, 100, 10, 10, 10);
		//g.setColor(Color.getHSBColor(1000, 0, 10));
		//g.fillRoundRect(paddlex, paddley, 100, 10, 10, 10);
		g.drawImage(pic.getImage(),paddlex,paddley,100,10, panel);
	}

}