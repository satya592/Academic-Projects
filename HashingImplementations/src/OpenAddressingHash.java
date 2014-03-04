import java.math.BigInteger;

public class OpenAddressingHash extends AbstractHashComparator{
	Hash hashTable[];
	
	OpenAddressingHash(BigInteger maxTabSize){
		this.maxTabSize = maxTabSize;
		this.q = Constants.q;
		hashTable = new Hash[maxTabSize.intValue()];
		int i = 0;
		for(i = 0; i<maxTabSize.intValue();i++){
			hashTable[i] = new Hash();
			hashTable[i].key = new BigInteger("-1");
			hashTable[i].status = 'E';
		}
	}
	
	public void Display(){
		int i = 0;
		for(i=0;i<maxTabSize.intValue();i++)
			System.out.println(hashTable[i].key);
	}
	
	
	public void Insert(BigInteger value){
		BigInteger dummy = value.mod(maxTabSize);
		insert++;
		if(hashTable[dummy.intValue()].status != 'O'){
			hashTable[dummy.intValue()].key = value;
			hashTable[dummy.intValue()].status = 'O';
		}
		else{
			int index = dummy.intValue(), i = 1, newHash = 0;
			while(true){
				newHash = this.q.intValue() - (value.mod(q)).intValue();
				index = (dummy.intValue() + (i * newHash)) % maxTabSize.intValue();

				if(hashTable[index].status != 'O'){
					hashTable[index].key = value;
					hashTable[index].status = 'O';
					break;
				}
				else
					i++;
			}
		}
		
	}
	
	public void Delete(BigInteger value){
		BigInteger dummy = value.mod(maxTabSize);
		remove++;
		int firstIndex = dummy.intValue();

		if(hashTable[dummy.intValue()].key.equals(value) && hashTable[dummy.intValue()].status == 'O'){
			hashTable[dummy.intValue()].status = 'D';
			return;

		}
		else{
			int index = dummy.intValue(), i = 1, newHash = 0;
			while(true){
				newHash = this.q.intValue() - (value.mod(q)).intValue();
				index = (dummy.intValue() + (i * newHash)) % maxTabSize.intValue();
				
				if(index == firstIndex)
					return;
				
				if(hashTable[index].key.equals(value) && hashTable[index].status == 'O'){
					hashTable[index].status = 'D';
					return;
				}
				else
					i++;
			}
		}

	}
	
	public boolean Find(BigInteger value){
		find++;
		BigInteger dummy = value.mod(maxTabSize);
		int firstIndex = dummy.intValue();
		
		if(hashTable[dummy.intValue()].key.equals(value) && hashTable[dummy.intValue()].status == 'O'){
			return true;
		}
		
		else{
			
			int index = dummy.intValue(), i = 1, newHash = 0;
			
			while(true){
				newHash = this.q.intValue() - (value.mod(q)).intValue();
				index = (dummy.intValue() + (i * newHash)) % maxTabSize.intValue();
				if(index == firstIndex){
					fail++;
					return false;
				}
				if(hashTable[index].key.equals(value) && hashTable[index].status == 'O'){
					return true;
				}
				else
					i++;
			}
		}
	}
	
	public void FindMax(){ 
		max = new BigInteger("0");
		int i = 0;
		for(i = 0; i < maxTabSize.intValue(); i++ ){
			if(hashTable[i].status == 'O' && hashTable[i].key.compareTo(max) > 0)
				max = hashTable[i].key;
		}
	
	}
	public void FindMin(){ 
		min = new BigInteger("10000000000000000000");
		int i = 0;
		for(i = 0; i < maxTabSize.intValue(); i++ ){
			if(hashTable[i].status == 'O' && hashTable[i].key.compareTo(min) < 0)
				min = hashTable[i].key;
		}
	}

}