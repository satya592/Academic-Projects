import javax.swing.*;

import java.awt.*;
import java.net.URL;
import java.util.LinkedList;
public class Bricks 
{
	//Brick Location
	int x;
	int y;

	//Bricks Dimensions
	int width;
	int height;

	//Graphic picture of a Brick
	ImageIcon brick2;

	//Panel to Draw to
	JPanel panel;

	//Where the Bricks will be Placed
	Bricks(JPanel p, int xloc, int yloc)
	{
		x = xloc;
		y = yloc;

		width = 35;
		height = 20;

		//Silver Brick
		URL url = getClass().getResource("brick2.png");
		brick2 = new ImageIcon(url);
		 
		panel = p;
	}

	//Draws the Bricks
	public void draw(Graphics g)
	{
			g.drawImage(brick2.getImage(), x, y, width, height, panel);
	}

	boolean b_collision(int ballX, int ballY, int ballW, int ballH)
		{
			if( ballX < x+width &&
				ballX + ballW > x &&
				ballY+ballH > y &&
				ballY < y+height)
				return true;
			else
				return false;
		}

	synchronized public void remove(Bricks rb)
	{
		for( Bricks b : ((GamePanel)panel).bricks )
		{
			if(b.x==rb.x && b.y==rb.y )
				{((GamePanel)panel).bricks.remove(b);break;}
		} 
	}
	
	synchronized public void remove(GoldBricks rb)
	{
		for( GoldBricks b : ((GamePanel)panel).gbricks )
		{
			if(b.x==rb.x && b.y==rb.y )
				{((GamePanel)panel).gbricks.remove(b);break;}
		} 
	}
}