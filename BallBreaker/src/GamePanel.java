import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.LinkedList;
import java.applet.*;


public class GamePanel extends JPanel implements KeyListener//,MouseListener
{
	static boolean start=false;

	//Paddle Object
	Paddle paddle;
	
	//Background Music
	AudioClip bg_music;

	//Background Image)
	ImageIcon background;

	//Create the Border
	LinkedList<Border> walls;

	//Create the Bricks
	LinkedList<Bricks> bricks;

	//Create the Gold Bricks
	LinkedList<GoldBricks> gbricks;

	//create the Ball
	ball BouncingBall;
	
	//Its makes the  threads Animator and Ball synchronized
	public  boolean Collision;

	GamePanel()
	{
		//Add Image Background
		background = new ImageIcon(getClass().getResource("background1 (2).png"));
		bg_music = Applet.newAudioClip(getClass().getResource("willy.mid"));
		bg_music.loop();

		//Creates a Paddle
		paddle = new Paddle(this);

		//Creates the Wall Border of the Game
		walls = new LinkedList<Border>();
		setWalls();

		//Creates the Bricks of the Game
		bricks = new LinkedList<Bricks>();
		//setBricks();

		//Creates the Gold Bricks of the Game
		gbricks = new LinkedList<GoldBricks>();
		setGoldBricks();
		
		
		//create an animation thread
		AnimatorThread at = new AnimatorThread(this);
		at.start();
		
		BouncingBall = new ball(this);
		
		addKeyListener(this);
		
		setFocusable(true);
		addMouseListener(new MouseAdapter(){public void mousePressed(MouseEvent me){start=true;}});
				
		//if(start==true)
		{ BouncingBall.start();}
		
	}

	public void paintComponent(Graphics g)
	{
		
		
		//super.paintComponent(g);
		//try{if(this.Collision == true) Thread.sleep(20);}
		//catch(Exception e) {} 
		Font font= new Font("Helvetica",Font.BOLD+Font.PLAIN,32);
		Font font1= new Font("Helvetica",Font.PLAIN,15);
		Font font2= new Font("Helvetica",Font.PLAIN,17);
		Font font4= new Font("Helvetica",Font.PLAIN,17);	
		//Draw the Background
		//g.drawImage(background.getImage(), 0, 0, 525, 1025, this);
		g.drawImage(background.getImage(), 0, 0, 525, 570, this);
		g.setFont(font2);
		g.setColor(Color.white );
		g.fillRect(0, 570,515 , 130 );
		g.setColor(Color.black );
		//Draw the Ball
		BouncingBall.draw(g);
		
		g.setFont(font1);
		g.setColor(Color.black );
		g.drawString("Press 'A' to move Left 'D' to Right",110,600);
				
		g.setFont(font);
		g.setColor(Color.red);
		
		if(BouncingBall.noBricks==-1){ g.drawString("Congratulations ! You Won",50,245);background = new ImageIcon(getClass().getResource("fireworks.jpg"));this.repaint(); }
		if(BouncingBall.noBricks==-2) 
		{			
			g.drawString("You Lost!  ",200,325); 
		}
		//Draw the paddle
		paddle.draw(g);

		//Draw the walls
		for( Border w : walls )
			w.draw(g);
		if(Collision==true) for(int i=0 ;i<10000 ;i++ )
		Collision=false;
		for( Bricks b : bricks)
			b.draw(g);

		for( GoldBricks gb : gbricks)
			gb.draw(g);
		g.setFont(font4);
		g.setColor(Color.blue);
		g.drawString("Developed@\"ManguDreams\" 	  EMail:Satyawannu@gmail.com ",25,665);
		
	}

	public void setWalls()
	{
		for(int x = 0; x <= 700; x += 10)
		{
			walls.add( new Border(this, x, 0) );
			walls.add( new Border(this, x, 570) );
			walls.add( new Border(this, 0, x) );
			walls.add( new Border(this, 500, x) );
		}
	}
/*
	public void setBricks()
	{
			
		
		for(int y = 20;y  < 200; y += 35)
		{
			//Outermost Left Silver Line of Bricks
			bricks.add( new Bricks(this, 50,y + 30));

			//Outtermost Right Silver Line of Bricks
			bricks.add( new Bricks(this, 20, y + 30));
		}

		
		for(int y = 20;y  < 200; y += 35)
		{
			//Outermost Left Silver Line of Bricks
			bricks.add( new Bricks(this, 150,y + 30));

			//Outtermost Right Silver Line of Bricks
			bricks.add( new Bricks(this, 120, y + 30));
		}


		for(int y = 20;y  < 200; y += 35)
		{
			//Outermost Left Silver Line of Bricks
			bricks.add( new Bricks(this, 250,y + 30));

			//Outtermost Right Silver Line of Bricks
			bricks.add( new Bricks(this, 220, y + 30));
		}

		
		for(int y = 20;y  < 200; y += 35)
		{
			//Outermost Left Silver Line of Bricks
			bricks.add( new Bricks(this, 350,y + 30));

			//Outtermost Right Silver Line of Bricks
			bricks.add( new Bricks(this, 320, y + 30));
		}


		for(int y = 20;y  < 200; y += 35)
		{
			//Outermost Left Silver Line of Bricks
			bricks.add( new Bricks(this, 450,y + 30));

			//Outtermost Right Silver Line of Bricks
			bricks.add( new Bricks(this, 420, y + 30));
		}
		
	}
*/
	 public void setGoldBricks()
	{
		
		for(int x=35;x<455;x+=35)
		{
			gbricks.add( new GoldBricks(this, x, 70));
		}
		for(int x=70;x<420;x+=35)
		{
			gbricks.add( new GoldBricks(this, x,90));
     	}
		for(int x=105;x<385;x+=35)
		{
			gbricks.add( new GoldBricks(this, x,110));
		}
	/*	for(int x=140;x<350;x+=35)
		{
			gbricks.add( new GoldBricks(this, x,130));
		}
		for(int x=175;x<315;x+=35)
		{
			gbricks.add( new GoldBricks(this, x,150));
		}
		for(int x=210;x<280;x+=35)
		{
			gbricks.add( new GoldBricks(this, x,170));
		}
		for(int x=225;x<245;x+=35)
		{
			gbricks.add( new GoldBricks(this, x,190));
		}
	 */
	}


		public void keyPressed(KeyEvent e)
		{
			char k = e.getKeyChar();
			if ( k == 'd'|| k=='D' )
				paddle.m_right = true;
			if ( k == 'a' || k=='A' )
				paddle.m_left = true;
		}
		public void keyReleased(KeyEvent e)
		{
			char k = e.getKeyChar();

			if( k == 'd'|| k=='D' )
				paddle.m_right = false;
			if( k == 'a' || k=='A')
				paddle.m_left = false;
		}
		public void keyTyped(KeyEvent e)
		{}
		public void mousePressed(MouseEvent e)
		{
			start=true;
		}
		public void mouseExited(MouseEvent e)
		{
			}
		public void mouseEntered(MouseEvent e)
		{
			}
		public void mouseReleased(MouseEvent e)
		{
			}
		public void mouseClicked(MouseEvent e)
		{
			}
		public void removeBrick(Bricks b)
	{
		Collision=true;
		bricks.remove(b);
	}
		public void removeBrick(GoldBricks b)
	{
		Collision=true;
		gbricks.remove(b);
	}

}
