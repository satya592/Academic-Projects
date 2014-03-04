import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.LinkedList;
import java.net.*;
import java.applet.*;
import java.util.*;



public class ball extends Thread
{
	ImageIcon pic;

	AudioClip paddlehit;

	int ballx;
	int bally;

	int balldx;
	int balldy;

	int ballLeft;
	int ballRight;
	int ballTop;
	int ballBottom;

	int brickLeft;
	int brickRight;
	int brickTop;
	int brickBottom;

	boolean hitLeft;
	boolean hitRight;
	boolean hitTop;
	boolean hitBottom;

	int noBricks;
	int totalNoBricks ;
	int life;
	Random rn;
	JPanel panel;
	HighScore hScore;

	ball(JPanel p)
	{
		panel = p;

		ballx = 250;
		bally = 500;

		balldx = 2;
		balldy = -2;
		noBricks = 0;
		totalNoBricks =0;
		life=2;
		
		hScore = new HighScore();
		hScore.readScores();
		rn= new Random();
		URL url = getClass().getResource("ball.png");
		pic = new ImageIcon(url);

		url = getClass().getResource("Teleport.wav");
		paddlehit = Applet.newAudioClip(url);

	}
	public void run()
	{
		while(true)
		{
			move();
			//Collision Detection
			try{Thread.sleep(10);}
			catch(Exception e){}
		}
	}

	public void draw(Graphics g)
	{
		for(int ballLifes=0 ;ballLifes< life;ballLifes++) g.drawImage(pic.getImage(), 420+ballLifes*20,610, 15,15, panel);
		g.drawString("SCORE :"+ totalNoBricks*35,10,620);
		g.drawString("HighScore :"+ hScore.scores[0],10,640);
		g.drawImage(pic.getImage(), ballx,bally, 15,15, panel);
	}

	public void move()
	{
		try{
		noBricks=0;
		ballx += balldx;
		bally += balldy;
			//Collision Detection
			for( Border w : ((GamePanel)panel).walls )
			{
				//System.out.println(hScore.scores);
				
				if( w.collision( ballx,bally,15,15 ) )
				{   
					//Top Wall
					if(bally <= 10)
					{
					// System.out.println("I hit the Top wall");
					 balldy *= -1;
					 bally += balldy;
					}
					//Bottom Wall
					if(bally >= 635)
					{
						//System.out.println("I hit the bottom wall");
						balldy *= -1;
						bally +=balldy;
					}
					//Left Wall
					if(ballx <= 10)
					{
						//System.out.println("I hit the left wall");
						balldx *= -1;
						ballx += balldx;
					}

					//Right Wall
					if(ballx >= 485)
					{
						//System.out.println("I hit the right wall");
						balldx *= -1;
						ballx += balldx;
					}

				}

					//Checking if the player missed the ball
					if(bally >= ((GamePanel)panel).paddle.paddley + 10)
					{
						if(life==0)
						{
							noBricks=-2;
							ballx = 0;
							bally = -10;
							//hScore.scores=new int[] { totalNoBricks*35};
							hScore.updateScores( totalNoBricks*35 ) ;
							hScore.readScores();
							this.stop();
						}
						else 
							{
							life--;
							ballx = 250;
							bally = 500;

							balldx = 2;
							balldy = -2;
							}
					}
					//Checking to see if were hitting the Paddle
						else if(bally + 15 >= 550 &&
						ballx + 15 >= ((GamePanel)panel).paddle.paddlex &&
						ballx <= ((GamePanel)panel).paddle.paddlex + 100 &&
						bally <= 550 + 100)
					{
						//Play paddlehit
						paddlehit.stop();
						paddlehit.play();
						//if(balldy%2==0 )balldy /=2; 
						balldy *= -1;
						bally += balldy;
					if(ballx+15>=((GamePanel)panel).paddle.paddlex && ballx+15<=((GamePanel)panel).paddle.paddlex +55 )
						{
						balldx=(rn.nextInt()%2)+4;
						balldx *= -1;
						ballx += balldx;
						}
					else if(ballx-15 <= ((GamePanel)panel).paddle.paddlex +100 && ballx+15 >=((GamePanel)panel).paddle.paddlex + 75 )
					{
						balldx=-(rn.nextInt()%2)-4;
						balldx *=-1;
						ballx+=balldx;
					}
					else
					{
						balldx *=-1;
						balldx = 0;
						ballx +=balldx;
					//	balldy*=2;
						}
					}
			}
			if(((GamePanel)panel).Collision==true) Thread.sleep(20);
			
	/*		for( Bricks b : ((GamePanel)panel).bricks )
			{
				noBricks++;
				if( b.b_collision( ballx,bally,15,15 ) )
					{
						 ballLeft = ballx;
						 ballRight = ballx + 15;
						 ballTop = bally;
				 		 ballBottom = bally + 15;
						 brickLeft = b.x;
						 brickRight = b.x + b.width;
						 brickTop = b.y;
						 brickBottom = b.y + b.height;

				//We hit the left side if:
				hitLeft = (ballLeft < brickLeft) && (ballRight > brickLeft);

				//And hit right if:
				hitRight = (ballLeft < brickRight) && (ballRight > brickRight);

				// Top and bottom
				hitTop = (ballTop < brickTop) && (ballBottom > brickTop);
				hitBottom = (ballTop < brickBottom) && (ballBottom > brickBottom);

				if (hitLeft || hitRight)
				{
				  if(balldx==0)balldx=2;
					balldx *= -1;
				  ballx += balldx;
				  System.out.println("Hit Side");
				  ((GamePanel)panel).removeBrick(b);
				}

				if (hitTop || hitBottom)
				{
					if(balldx==0)balldx=2;
				 balldy *= -1;
				 bally += balldy;
				 ((GamePanel)panel).removeBrick(b);
		    	}
			}
		}
*/			for( GoldBricks gb : ((GamePanel)panel).gbricks )
			{
				noBricks++;
				if( gb.gb_collision( ballx,bally,15,15 ) )
					{
					    totalNoBricks+=1;
						 ballLeft = ballx;
						 ballRight = ballx + 15;
						 ballTop = bally;
				 		 ballBottom = bally + 15;
						 brickLeft = gb.x;
						 brickRight = gb.x + gb.width;
						 brickTop = gb.y;
						 brickBottom = gb.y + gb.height;

				//We hit the left side if:
				hitLeft = (ballLeft < brickLeft) && (ballRight > brickLeft);

				//And hit right if:
				hitRight = (ballLeft < brickRight) && (ballRight > brickRight);

				// Top and bottom
				hitTop = (ballTop < brickTop) && (ballBottom > brickTop);
				hitBottom = (ballTop < brickBottom) && (ballBottom > brickBottom);

				if (hitLeft || hitRight)
				{
				  balldx *= -1;
				  ballx += balldx;
				  System.out.println("Hit Side");
				  ((GamePanel)panel).removeBrick(gb);
				}

				if (hitTop || hitBottom)
				{
				 balldy *= -1;
				 bally += balldy;
				 System.out.println("Hit Bottom");
				 ((GamePanel)panel).removeBrick(gb);
		    	}
			}

		}//for
	}catch(Exception e) {}

	if(noBricks==0) 
		{noBricks=-1;
		this.stop();
		}
	
			}
}