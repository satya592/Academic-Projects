import java.math.BigInteger;
import java.util.ArrayList;


public class SeperateChaining1way extends AbstractHashComparator{
	SeparateChainingHashObj hashTable[];
	
	SeperateChaining1way(BigInteger maxTabSize){
		this.maxTabSize = maxTabSize;
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
		int index;
		index = value.mod(maxTabSize).intValue();
		insert++;
		hashTable[index].key.add(value);
	}
	
	public void Delete(BigInteger value){
		int index;
		remove++;
		index = value.mod(maxTabSize).intValue();
	
		if(hashTable[index].key.contains(value)){
			hashTable[index].key.remove(value);
		}
	}
	
	
	public boolean Find(BigInteger value){
		find++;
		int index;
		index = value.mod(maxTabSize).intValue();
		
		if(hashTable[index].key.contains(value)){
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