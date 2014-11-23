import java.math.BigInteger;


public class CuckooHash extends AbstractHashComparator{
	
	Hash hashTable1[];
	Hash hashTable2[];
	
	CuckooHash(BigInteger maxTabSize){
		this.maxTabSize = maxTabSize;
		hashTable1 = new Hash[maxTabSize.intValue()];
		hashTable2 = new Hash[maxTabSize.intValue()];
		int i = 0;
		for(i = 0; i<maxTabSize.intValue();i++){
			hashTable1[i] = new Hash();
			hashTable1[i].key = new BigInteger("-1");
			
			hashTable2[i] = new Hash();
			hashTable2[i].key = new BigInteger("-1");
		}
	}
	
	public void Display(){
		int i = 0;
		for(i=0;i<maxTabSize.intValue();i++)
			System.out.print(hashTable1[i].key + " ");
	
		for(i=0;i<maxTabSize.intValue();i++)
			System.out.print(hashTable2[i].key + " ");
	}
	
	public void Insert(BigInteger value){
		BigInteger index1 = value.mod(maxTabSize);
		BigInteger index2 = (value.divide(maxTabSize)).mod(maxTabSize);
		insert++;
		if(hashTable1[index1.intValue()].equals(new BigInteger("-1"))){
			hashTable1[index1.intValue()].key = value;
		}
		else{	
			int flag = 2;
			BigInteger dummy;
			while(true){
				if(flag == 1){
					if(hashTable1[index1.intValue()].key.intValue() == -1){
						hashTable1[index1.intValue()].key = value;
						return;
					}
					else{
						dummy = hashTable1[index1.intValue()].key;
						hashTable1[index1.intValue()].key = value;
						value = dummy;
						flag = 2;
					}
				}
				else if(flag == 2){
					if(hashTable2[index2.intValue()].key.intValue() == -1){
						hashTable2[index2.intValue()].key = value;
						return;
					}
					else{
						dummy = hashTable2[index2.intValue()].key;
						hashTable2[index2.intValue()].key = value;
						value = dummy;
						flag = 1;
						
					}
				}
				
				index1 = value.mod(maxTabSize);
				index2 = (value.divide(maxTabSize)).mod(maxTabSize);
			}
			
		}
		
	}
	
	public void Delete(BigInteger value){
		int index1, index2;
		remove++;
		index1 = value.mod(maxTabSize).intValue();
		index2 = ((value.divide(maxTabSize)).mod(maxTabSize)).intValue();
		
		if (hashTable1[index1].key.equals(value)){
			hashTable1[index1].key = new BigInteger("-1");
		}
		else if(hashTable2[index2].key.equals(value)){
			hashTable2[index2].key = new BigInteger("-1");
		}
		
	}
	
	
	public boolean Find(BigInteger value){
		int index1, index2;
		find++;
		index1 = value.mod(maxTabSize).intValue();
		index2 = ((value.divide(maxTabSize)).mod(maxTabSize)).intValue();
		
		if (hashTable1[index1].key.equals(value)){
			return true;
		}
		else if (hashTable2[index2].key.equals(value)){
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
		int i = 0;
		for(i = 0; i < maxTabSize.intValue(); i++ ){
			if(hashTable1[i].key.compareTo(max) > 0)
				max = hashTable1[i].key;
		}
		for(i = 0; i < maxTabSize.intValue(); i++ ){
			if(hashTable2[i].key.compareTo(max) > 0)
				max = hashTable2[i].key;
		}
	
	}
	public void FindMin(){ 
		min = new BigInteger("10000000000000000000");
		int i = 0;
		for(i = 0; i < maxTabSize.intValue(); i++ ){
			if(!hashTable1[i].key.equals(new BigInteger("-1")) && hashTable1[i].key.compareTo(min) < 0)
				min = hashTable1[i].key;
		}
		for(i = 0; i < maxTabSize.intValue(); i++ ){
			if(!hashTable2[i].key.equals(new BigInteger("-1")) && hashTable2[i].key.compareTo(min) < 0)
				min = hashTable2[i].key;
		}
	}
	
}
