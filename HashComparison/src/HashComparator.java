import java.math.BigInteger;
import java.util.Date;

public class HashComparator {
	
	
	static int compareFailures(int open, int cuckoo, int seperate, int seperate1way){
		return Math.min(Math.min(open,seperate), Math.min(cuckoo,seperate1way));		
	}
	
	public static void main(String[] args) {
		
		BigInteger maxTabSize;
		maxTabSize = Constants.maxTabSize;
		
		Read4mFile readFile;
		readFile = new Read4mFile(Constants.fileName);
		
		System.out.println("Before Processing");

		System.out.println("Cuckoo Hashing starts at :" +new Date());
		AbstractHashComparator cuckooHash = new CuckooHash(maxTabSize);
		readFile.process(cuckooHash);
		int cuckoo = cuckooHash.fail;
		cuckooHash = null;
		System.out.println("Cuckoo Hashing Ends at :" +new Date());
		
		System.out.println("Seperate Chaining starts at :" +new Date());
		AbstractHashComparator seperateHash = new SeperateChaining(maxTabSize);
		readFile.process(seperateHash);
		int seperate = seperateHash.fail;
		seperateHash = null;
		System.out.println("Seperate Chaining Ends at :" +new Date());
		
		System.out.println("SChaining 1 way starts at :" +new Date());
		AbstractHashComparator seperateHash1way = new SeperateChaining1way(maxTabSize);
		readFile.process(seperateHash1way);
		int seperate1way = seperateHash1way.fail;
		seperateHash1way = null;
		System.out.println("SChaining 1 way Ends at :" +new Date());

		System.out.println("Open Addressing Starts at :" + new Date());
		AbstractHashComparator openAddHash = new OpenAddressingHash(maxTabSize);
		readFile.process(openAddHash);
		int open = openAddHash.fail;
		openAddHash = null;
		System.out.println("Open Addressing Ends at :" +new Date());
		
		
		System.out.println(compareFailures(open, cuckoo, seperate, seperate1way));
		
		System.out.println("Completed");
	}

}
