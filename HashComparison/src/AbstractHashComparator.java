import java.math.BigInteger;

public class AbstractHashComparator {
	
	BigInteger maxTabSize;
	BigInteger q;
	int fail;
	int insert;
	int find;
	int remove;
	
	int size;
	BigInteger max = new BigInteger("0"), min = new BigInteger("10000000000000000000");
	
	public void Insert(BigInteger value){ System.out.println("AHC");}
	public void Delete(BigInteger value){ }
	public boolean Find(BigInteger value){ return true;}
	public void Display(){ }
	public void FindMax(){ }
	public void FindMin(){ }
	public void Size(){ 
		size = (insert - remove) > 0 ? insert - remove : 0;
		System.out.println("Size:"+ size);
	}

	
}
