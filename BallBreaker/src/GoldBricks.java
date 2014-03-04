import javax.swing.*;

import java.awt.*;
import java.net.URL;
import java.util.LinkedList;

public class GoldBricks// extends Bricks
{
	//Brick Location
	int x;
	int y;

	//Bricks Dimensions
	int width;
	int height;

	//Graphic picture of a Brick
	ImageIcon brick1;

	//Panel to Draw to
	JPanel panel;

	//Where the Bricks will be Placed
	GoldBricks(JPanel p, int xloc, int yloc)
	{
		x = xloc;
		y = yloc;

		width = 35;
		height = 20;

		//Gold Brick
		URL url = getClass().getResource("brick1.png");
		brick1 = new ImageIcon(url);

		panel = p;
	}

	//Draws the Bricks
	public void draw(Graphics g)
	{
			g.drawImage(brick1.getImage(), x, y, width, height, panel);
	}

	boolean gb_collision(int ballX, int ballY, int ballW, int ballH)
		{
			if( ballX < x+width &&
				ballX + ballW > x &&
				ballY+ballH > y &&
				ballY < y+height)
				return true;
			else
				return false;
		}


}