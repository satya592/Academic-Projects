import javax.swing.*;	

public class AnimatorThread extends Thread
{

	JPanel panel;

	AnimatorThread(JPanel p)
	{
		panel = p;
	}

	public void run()
	{
		while(true)
		{
				try{if(((GamePanel)panel).Collision==true) Thread.sleep(0);}
				catch(Exception e) {}
			//	panel.paddle.paddlex;
			//repaint the panel
			panel.repaint();
				
			//wait awhile
			try{ Thread.sleep(0); }
			catch(Exception e) {}
		}
	}


}

