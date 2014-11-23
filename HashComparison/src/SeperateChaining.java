import java.math.BigInteger;
import java.util.ArrayList;


public class SeperateChaining extends AbstractHashComparator{
	SeparateChainingHashObj hashTable[];
	
	SeperateChaining(BigInteger maxTabSize){
		this.maxTabSize = maxTabSize;
		this.q = Constants.q;
		hashTable = new SeparateChainingHashObj[maxTabSize.intValue()];
		int i = 0;
		for(i = 0; i<maxTabSize.intValue();i++){
			hashTable[i] = new SeparateChainingHashObj();
			hashTable[i].key = new ArrayList<BigInteger>();
		}
	}
	
	public void Display(){
		int i = 0;
		for(i=0;i<maxTabSize.intValue();i++)
			System.out.println(hashTable[i].key);
	}
	
	public void Insert(BigInteger value){
		int index1, index2, index;
		index1 = value.mod(maxTabSize).intValue();
		index2 = ((value.divide(maxTabSize)).mod(maxTabSize)).intValue();
		insert++;
		index = hashTable[index1].key.size() > hashTable[index2].key.size() ? index2 : index1;
		
		hashTable[index].key.add(value);
	}
	
	public void Delete(BigInteger value){
		int index1, index2;
		remove++;
		index1 = value.mod(maxTabSize).intValue();
		index2 = ((value.divide(maxTabSize)).mod(maxTabSize)).intValue();
		
		if(hashTable[index1].key.contains(value)){
			hashTable[index1].key.remove(value);
		}
		else if(hashTable[index2].key.contains(value)){
			hashTable[index2].key.remove(value);
		}
		
	}
	
	
	public boolean Find(BigInteger value){
		find++;
		int index1, index2;
		index1 = value.mod(maxTabSize).intValue();
		index2 = ((value.divide(maxTabSize)).mod(maxTabSize)).intValue();
		
		if(hashTable[index1].key.contains(value)){
			return true;
		}
		else if(hashTable[index2].key.contains(value)){
			fail++;
			return true;
		}
		else{
			fail++;
			return false;
		}
	}
	
	public void FindMax(){
		max = new BigInteger("0");
		int i = 0, j = 0;
		
		for(i = 0;i< hashTable.length;i++){
			for(j = 0; j< hashTable[i].key.size(); j++){
				if(hashTable[i].key.get(j).compareTo(max) > 0 )
					max = hashTable[i].key.get(j);
			}
		}
		
	}
	public void FindMin(){
		min = new BigInteger("10000000000000000000");
		int i , j = 0;
		for(i = 0;i< hashTable.length;i++){
			for(j = 0; j< hashTable[i].key.size(); j++){
				if(hashTable[i].key.get(j).compareTo(min) < 0 )
					min = hashTable[i].key.get(j);
			}
		}
	}
}
