import java.io.*;
import java.math.BigInteger;


public class Read4mFile {
	private String filename;

	Read4mFile(String filename){
		this.filename = filename;
	}
	
	
	void parse(String Line, AbstractHashComparator hash){
		String[] lines = new String[10];
		BigInteger dummy;
		if(Line != null){
			lines = Line.split(" ");
			int i = 0;
			
			if(lines[0].equals("Insert")){
				for(i=1;i<lines.length;i++){
					if(lines[i]!="0"){
						dummy = new BigInteger(lines[i]);
						if(dummy.compareTo(hash.max) > 0){
							hash.max = dummy;
						}
						if(dummy.compareTo(hash.min) < 0){
							hash.min = dummy;
						}
						hash.Insert(new BigInteger(lines[i]));
					}
				}
			}
			
			else if(lines[0].equals("Remove")){
				hash.Delete(new BigInteger(lines[1]));
			}
			
			else if(lines[0].equals("Find")){
				hash.Find(new BigInteger(lines[1]));
			}
			else if(lines[0].equals("FindMax")){
//				if(hash.Find(hash.max)){
//					System.out.println(hash.max);
//				}
//				else
					hash.FindMax();
					System.out.println("Max:" + hash.max);
			}
			else if(lines[0].equals("FindMin")){
//				if(hash.Find(hash.min)){
//					System.out.println(hash.min);
//				}
//				else
					hash.FindMin();
					System.out.println("Min:" + hash.min);
			}
			else if(lines[0].equals("Size")){
				hash.Size();
			}
					
			else{
				System.out.println(Line);
			}
		}
	}
	
	void process(AbstractHashComparator hash){
		BufferedReader br = null;
		try {
			 
			String nextLine;
			br = new BufferedReader(new FileReader(this.filename));
 			while ((nextLine = br.readLine()) != null) {
				parse(nextLine, hash);		
			}
 
		} 
		catch (Exception e) {
			e.printStackTrace();
		} 
		finally {
			try {
				if (br != null)
					br.close();
			} 
			catch (Exception ex) {
				ex.printStackTrace();
			}
		}
	
	}
		
}
