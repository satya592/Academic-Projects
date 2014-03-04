import javax.swing.*;

import java.awt.*;
import java.net.URL;
import java.util.LinkedList;

public class Border
{
	//Wall Location
	int x;
	int y;

	//Walls Dimensions
	int width;
	int height;

	//Graphic picture of a wall
	ImageIcon top,bottom,left,right;

	//Panel to Draw to
	JPanel panel;

	//Where the walls will be Placed
	Border(JPanel p, int xloc, int yloc)
	{
		x = xloc;
		y = yloc;

		width = 10;
		height = 10;
		URL url1 = getClass().getResource("LeftBorder.png");
		left  = new ImageIcon(url1);
		URL url2 = getClass().getResource("TopBorder.png");
		top  = new ImageIcon(url2);
		URL url3 = getClass().getResource("BottomBorder.png");
		bottom = new ImageIcon(url3);
		URL url4 = getClass().getResource("RightBorder.png");
		right = new ImageIcon(url4);

		panel = p;
	}

	//Draws the wall
	public void draw(Graphics g)
	{
		//Draws the Left Border
		if(y >= 0 && x == 0)
		g.drawImage(left.getImage(), x, y, width, height, panel);

		//Draws the Top Border
		if(y == 0 && x >= 0)
		g.drawImage(top.getImage(), x, y, width, height, panel);

		//Draws the Bottom Border
		if(y == 570 && x >= 10)
		g.drawImage(bottom.getImage(), x, y, width, height, panel);
         
		//Draws the Right Border
		if(y >= 0 && x == 500)
		g.drawImage(right.getImage(), x, y, width, height, panel);
		
		
	}

	boolean collision(int ballX, int ballY, int ballW, int ballH)
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