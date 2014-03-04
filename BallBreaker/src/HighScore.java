import java.lang.*;
import java.util.*;
import java.io.*;

public class HighScore implements Serializable
{
	//File file=new File("");
	public int[] scores;
	HighScore()
	{
		scores = new int[]{0};
	}
	void updateScores(int score) 
	{
		//for(int i=0; i<=)
		if(scores[0]==0 || scores[0] < score) 
		{
			scores[0]=score;
			try{
			ObjectOutputStream out =new ObjectOutputStream(new FileOutputStream("scores.txt"));
			out.writeObject(scores);
			out.flush();
			out.close();
			}catch(Exception e){e.printStackTrace();}
		}
	}
	
	void readScores()
	{
		try{
			ObjectInputStream in =new ObjectInputStream(new FileInputStream("scores.txt"));
			scores= (int[])in.readObject();
			//			System.out.println(scores[0]);
			in.close();
		}catch(Exception e){e.printStackTrace();}
	}
	
	}