//package proj7;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;
import java.util.StringTokenizer;

class Matching{

	Node A[] = null; 
	Node B[] = null;
	Queue<NodeInfo> queue = new LinkedList<NodeInfo>();
	int numEdges= 0;
	int maxNumMatches=0;
	LinkedList<Integer> aQ = new LinkedList<Integer>();
	LinkedList <Integer> bQ = new LinkedList<Integer>();
	Matching() throws Exception{
		
	BufferedReader br = new BufferedReader(new FileReader("input1.txt"));
	System.out.println("Started executing");
	String currentLine = br.readLine();
	StringTokenizer stkn = new StringTokenizer(currentLine);
	
	if(stkn.hasMoreTokens()){
		String str = stkn.nextToken();
		A = new Node[Integer.parseInt(str)];
		for(int i=0;i<Integer.parseInt(str); i++)
			A[i] =  new Node();
		str = stkn.nextToken();
		B = new Node[Integer.parseInt(str)];
		for(int i=0;i<Integer.parseInt(str); i++)
			B[i] =  new Node();
		str = stkn.nextToken();
		numEdges = Integer.parseInt(str);
	}
	
	while ((currentLine = br.readLine()) != null) {
		stkn = new StringTokenizer(currentLine);
		String str1 = stkn.nextToken();
		String str2 = stkn.nextToken();
		String str3 = stkn.nextToken();
		int index1 = Integer.parseInt(str1)-1;
		int index2 = Integer.parseInt(str2)-1;
		int weight = Integer.parseInt(str3);
	if(	A[index1].matched == false && B[index2].matched == false ){
		A[index1].matched = true;
		B[index2].matched = true;
	
		A[index1].nextNode.add(new Edge(index2,weight,true));
		B[index2].nextNode.add(new Edge(index1,weight,true));
		maxNumMatches++;
	}
	else{
		A[index1].nextNode.add(new Edge(index2,weight,false));
		B[index2].nextNode.add(new Edge(index1,weight,false));
	}
	
	
}
	br.close();
}
	void printAll(){
		System.out.println("MaxMatchings:"+this.maxNumMatches);
		Node N = null;
		for(int k=0;k<A.length;k++){
			N = A[k];
			for(int i=0;i<N.nextNode.size();i++){
					System.out.print("("+(k+1)+","+(N.nextNode.get(i).nextNodeNum+1)+","+(N.nextNode.get(i).matched)+"), "+"QueuedList:");
					
			}
		}
		System.out.println("MaxMatchings:"+this.maxNumMatches);

	}

	
	void print(){
		System.out.println("MaxMatchings:"+this.maxNumMatches);
		Node N = null;
		int counter = 0;
		for(int k=0;k<A.length;k++){
			N = A[k];
			for(int i =0;i<N.nextNode.size();i++){
				if(N.nextNode.get(i).matched == true)
					{
					counter++;
					System.out.print("("+(k+1)+","+(N.nextNode.get(i).nextNodeNum+1)+"), ");
					break;
					}
			}
		}
		System.out.println("MaxMatchings:"+this.maxNumMatches+"counter="+counter+"Length"+A.length);
	}
	

	void findMaxMatches(){
		int lenA = A.length;
		int lenB = B.length;
		int level = 0;
		ArrayList<Integer> visted = new ArrayList<Integer>();
//		ArrayList<Integer> Bvisted = new ArrayList<Integer>();

		char unMatched = '\0';
		if(lenA < lenB){
			unMatched = 'a';
			for(int i = 0;i<lenA;i++)
				{	
				if(A[i].matched == false)
					{
					visted.add(i);
					if(CheckAugmentaryPath(level,unMatched,i,visted,new ArrayList<Integer>()))
						this.maxNumMatches++;
					visted.remove(new Integer(i));
					}
				}
		}else{
			unMatched = 'b';
			for(int i = 0;i<lenB;i++)
				{
				if(B[i].matched == false)
					{
					visted.add(i);
					if(CheckAugmentaryPath(level,unMatched,i,new ArrayList<Integer>(),visted))
						this.maxNumMatches++;
					visted.remove(new Integer(i));
					}
				}
		}
	}
/*
//Find max matches	
	void findMaxMatches(){
		int lenA = A.length;
		int lenB = B.length;

		char marker = '\0';
		if(lenA < lenB){
			marker = 'a';
			for(int i = 0;i<lenA;i++)
				{	
				if(A[i].matched == false)
					{
//					System.out.println("A:");
					if(CheckAugmentaryBFSPath(marker,i,0)){
//						System.out.println("\nTop:"+marker+(i+1));
						A[i].matched = true;
						this.maxNumMatches++;
//						this.print();
					}
					}
				}
		}else{
			marker = 'b';
			for(int i = 0;i<lenB;i++)
				{
				if(B[i].matched == false)
					{
//					System.out.println("B:");
					if(CheckAugmentaryBFSPath(marker,i,0)){
						System.out.println("\nTop:"+marker+(i+1));
						B[i].matched = true;
						this.maxNumMatches++;
//						this.print();
					}
					}
				}
		}
		
		
	}
*/
//########################## to the get the node	
	Node getNode(char marker, int index){ 

		switch(marker){
		case 'a':
			return A[index];
		case 'b':
			return B[index];
		default:
			System.out.println("Failed to getNode");
			return null;
		}
	}
	//########################## to the get the node	
	void setNode(char marker, int index,boolean value){ 
		switch(marker){
		case 'a':
		 A[index].matched = value;
		 break;
	case 'b':
		B[index].matched = value;
		break;
	default:
		System.out.println("Failed to setNode");
			break;
		}
	}
//########################## to the get the node	
void setEdge(char marker, int index,int nextnode,boolean value){ 
	switch(marker){
	case 'a':
		 A[index].nextNode.get(nextnode).matched = value;
		 break;
	case 'b':
		B[index].nextNode.get(nextnode).matched = value;
		break;
	default:
		System.out.println("Failed to setEdge");
		break;
	}
}

//########################## to the get the node	
void filpEdge(char marker, int index,int nextnode){ 
	switch(marker){
	case 'a':
		for(int i =0;i<A[index].nextNode.size();i++){
		 if(A[index].nextNode.get(i).nextNodeNum == nextnode)
			A[index].nextNode.get(i).matched = !(A[index].nextNode.get(i).matched);
		}
		 break;
	case 'b':
		for(int i = 0;i<B[index].nextNode.size();i++){
			 if(B[index].nextNode.get(i).nextNodeNum == nextnode)
				 B[index].nextNode.get(i).matched = !(B[index].nextNode.get(i).matched);
		}
		break;
	default:
		System.out.println("Failed to flip:"+marker);
		break;
	}
}

//#####################set incoming
void setIncoming(char marker, int index,int incoming){
	switch(marker){
	case 'a':
			A[index].incoming = incoming;
		 break;
	case 'b':
		B[index].incoming = incoming;
		break;
	default:
		System.out.println("Failed to setincoming:"+marker);
		break;
	}
}

	
//########################New marker
	char getnewMarker(char marker){
		switch(marker){
		case 'a':
			return 'b';
		case 'b':
			return 'a';
		default:
			System.out.println("Failed to getMarker");
			return 'f';
		}
	}

//########################New marker
	Node getNextNode(char marker,int index){
		switch(marker){
		case 'a':
			return B[index];
		case 'b':
			return A[index];
		default:
			System.out.println("Failed to getNode");
			return null;
		}
	}
	
	
//######################## Roll back and reset the incoming node values
	void rollBack(char marker,int index){//,int newmarker,int indexFound){
		Node current = getNode(marker,index);
		int incoming = -1,lindex = index;
		char lmarker = marker;
		while(current.incoming != -1  ){
			
			incoming = current.incoming;
			//current.incoming = -1;
			this.setIncoming(marker, lindex, -1);
			marker = getnewMarker(marker);
			current = getNode(marker,incoming);
			lindex = incoming;
		}
		this.setIncoming(lmarker, index, -1);
	}
	
//##########################Rollback visited nodes
	void rollback()
	{
//		int alen = aQ.size();
//		int blen = bQ.size();
		while(aQ.size()>0){
			A[aQ.get(0)].incoming = -1;
//			System.out.println("A"+(aQ.get(0)+1));
			aQ.remove();
		}
		while(bQ.size()>0){
			B[bQ.get(0)].incoming = -1;
//			System.out.println("B"+(bQ.get(0)+1));
			bQ.remove();
		}
			
	}

//######################## Roll back and reset the incoming node values
	void setPath(char marker,int index){
		Node current = getNode(marker,index);
		int incoming = -1,size = 0;
		current.matched = true;// set the current as matched -->Leaf node
		while(current.incoming != -1){
			incoming = current.incoming;
			// set the edge
			size = current.nextNode.size();
			if(size == 0) System.out.println("SetPath failed: incoming is set but parent not found");
			this.filpEdge(marker, index, incoming);
			index = incoming;
			current.incoming = -1;//reset the incoming node
			marker = getnewMarker(marker);//next marker
//			System.out.print("in:"+marker+(incoming+1));
			current = getNode(marker,incoming);//getting parent
		}
		
		if(current.incoming == -1){
//			System.out.print("Reached top");
			this.setNode(marker, index, true);
			current.matched = true;//set the current as matched-->Root node
		}
	}
	
	
	void setQueue(char marker,int index){
		switch(marker){
		case 'a':
			aQ.add(index);
//			System.out.println("A"+(index+1));
			break;
		case 'b':
			bQ.add(index);
//			System.out.println("B"+(index+1));
			break;
		default:
			System.out.println("Failed at SetQueue");
		}
	}
	
	boolean CheckAugmentaryBFSPath(char marker,int index,int level){
//		int len=0;
		int lindex = 0;
//		int noOfEdges = 0;
//		Node current = getNode(marker,index);
//		Node nextNode = null;
		boolean result = false;
//		char newmarker = getnewMarker(marker);
//		boolean freeEdgeFound = false;
		NodeInfo nodeInfo;
		setQueue(marker,index);
		queue.add(new NodeInfo(level,index,marker));
		while(queue.isEmpty() == false && result == false){
			nodeInfo = queue.peek();
			level = nodeInfo.level;
			marker = nodeInfo.marker;
			lindex = nodeInfo.index;
//			newmarker = getnewMarker(marker);
//			current = getNode(marker,index);
//			noOfEdges = current.nextNode.size();
//			freeEdgeFound = false;
//			System.out.println("Level :"+level);		
//			System.out.println("qSize::"+queue.size());
			result = checkAugAtLevels(marker,lindex,level);
		}
//		System.out.println("returning :"+result);
//		if(result) {
//			len = current.nextNode.size();
//			for(int i=0;i<len;i++){
//				
//				lindex = current.nextNode.get(0).nextNodeNum;		
//				
//				if(getNode(newmarker,lindex).incoming == index){
//				
//				}
//			}
//		}
		rollback();
		queue.clear();
		return result;
	}

	
	
	boolean checkAugAtLevels(char marker,int index,int level){
//		boolean freeEdgeFound = false;
		NodeInfo nodeInfo;
		char newmarker = '\0';
		int noOfEdges = 0;
		Node current = null;
		Node nextNode = null;
		boolean result = false;

		while(queue.isEmpty() == false && queue.peek().level == level){
			//initialize the pointers
//			System.out.println("qSize::"+queue.size());
			nodeInfo = queue.poll();
			level = nodeInfo.level;
			marker = nodeInfo.marker;
			index = nodeInfo.index;
			newmarker = getnewMarker(marker);
			current = getNode(marker,index);
			noOfEdges = current.nextNode.size();
//			freeEdgeFound = false;
//			System.out.println("Level:"+level + "qsize:" + queue.size()+"edges:"+noOfEdges);
			if(level%2 == 0){//Even level

				for(int  i = 0;i<noOfEdges;i++){
					if(current.nextNode.get(i).matched == false){ // Free Edge found
//						freeEdgeFound = true;
						nextNode = getNode(newmarker,current.nextNode.get(i).nextNodeNum);
						if(nextNode.matched == false)//unmatched node at odd level is found -->success
						{
//							freeEdgeFound = true;
							System.out.print("crw:fr"+(index+1)+marker+(current.nextNode.get(i).nextNodeNum+1));
							current.nextNode.get(i).matched = true;//edge is set to true
							
//							current.nextNode.set(i, new Edge(current.nextNode.get(i).nextNodeNum, current.nextNode.get(i).weight, true));
							this.filpEdge(marker, index, current.nextNode.get(i).nextNodeNum);//false to true
//							nextNode.incoming = index;
							System.out.print("crw:"+(current.nextNode.get(i).nextNodeNum+1)+current.nextNode.get(i).matched+marker+(index+1));
							setPath(marker, index);
							setNode(newmarker, current.nextNode.get(i).nextNodeNum, true);
//							nextNode.matched = true;
							return true;
						}
						else{//push to queue if matched node at odd level found
							if(nextNode.incoming == -1)
							{
//								freeEdgeFound = true;
//								nextNode.incoming = index;
								this.setIncoming(newmarker, current.nextNode.get(i).nextNodeNum, index);
//								System.out.print("setting incoming:"+newmarker+(current.nextNode.get(i).nextNodeNum+1)+"="+(index+1));
//								System.out.println("Q1:"+marker+(nextNode.incoming+1) +nextNode.matched+newmarker+(current.nextNode.get(i).nextNodeNum+1));
								//push to queue if matched node at even level found
								setQueue(newmarker,current.nextNode.get(i).nextNodeNum);
								queue.add(new NodeInfo(level+1,current.nextNode.get(i).nextNodeNum,newmarker));
							}else{ //cycle found
//								System.out.println("cycle found1:"+marker+(nextNode.incoming+1) +nextNode.matched+newmarker+(i+1));//								
//								rollBack(marker, index);
//								return false;
								continue;
							}
						}
					}//if free edge found
				}//loop on edges
//				if(freeEdgeFound == false) //no solution, when no free edges
//				{
//					rollBack(marker, index);
////					current.incoming = -1;
////					return false;
//					continue;
//				}
			}//Even level
			else{//Odd level

				for(int  i = 0;i<noOfEdges;i++){
					if(current.nextNode.get(i).matched == true){ // Matched Edge found
//						nextNode.get(i).matched
//						freeEdgeFound = true;
//						System.out.println(marker+"("+(index+1)+","+(current.nextNode.get(i).nextNodeNum+1)+","+(current.nextNode.get(i).matched)+")"+newmarker+":"+
//								current.nextNode.get(i).nextNodeNum);
						nextNode = getNode(newmarker,current.nextNode.get(i).nextNodeNum);
//						System.out.println("In:"+nextNode.incoming+"matched:"+nextNode.matched);
						if(nextNode.matched == true)//unmatched node at odd level is found -->success
						{
							if(nextNode.incoming == -1)
							{
//								nextNode.incoming = index;
								this.setIncoming(newmarker, current.nextNode.get(i).nextNodeNum, index);
//								System.out.print("setting incoming:"+newmarker+(current.nextNode.get(i).nextNodeNum+1)+"="+(index+1));
//								System.out.println("Q2:"+marker+(nextNode.incoming+1) +nextNode.matched+newmarker+(current.nextNode.get(i).nextNodeNum+1));
								//push to queue if matched node at even level found
								setQueue(newmarker,current.nextNode.get(i).nextNodeNum);
								queue.add(new NodeInfo(level+1,current.nextNode.get(i).nextNodeNum,newmarker));
//								break;
							}else{ //cycle found
//								System.out.println("cycle found2:"+marker+(nextNode.incoming+1) +nextNode.matched+newmarker+(current.nextNode.get(i).nextNodeNum+1));
//								rollBack(marker, index);
								continue;
//								return false;
							}
						}
					}//if matched edge found
				}//loop on edges
//				if(freeEdgeFound == false) //no matched edge found
//				{
////					current.incoming = -1;
//					rollBack(marker, index);
////					return false;
//				}
			}//odd level

		}//Queue is not empty
//		System.out.println("qSize::"+queue.size());
		return result;
	}
	
	boolean CheckAugmentaryPath(int level,char marker,int index,ArrayList<Integer> Acount,ArrayList<Integer> Bcount){
//		System.out.println(Acount.size()+"$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$"+Bcount.size());
		Node current = null;
		boolean result = false;
		char newmarker = '\0';
		if(marker == 'a'){
			current =  A[index];
			newmarker = 'b';
		}else{
			current =  B[index];
			newmarker = 'a';
		}
		if(level%2 == 0){		//Even level
		if(current.nextNode.size() == 0)	return false;
			//find free edge
		for(int i = 0;i<current.nextNode.size();i++){
		if(current.nextNode.get(i).matched == false){
			if(marker == 'a'){
				if(Bcount.contains(new Integer(current.nextNode.get(i).nextNodeNum)))	
				return false;
				Acount.add(index);
			}else{
				if(Acount.contains(new Integer(current.nextNode.get(i).nextNodeNum)))	
				return false;
				Bcount.add(index);
			}
			if(result = CheckAugmentaryPath(1+level,newmarker,current.nextNode.get(i).nextNodeNum,Acount,Bcount))
				{
				current.nextNode.get(i).matched = true;
				current.matched =  true;
				if(marker == 'a'){
					System.out.print(Acount.remove(new Integer(index)));
				}else{
					System.out.print(Bcount.remove(new Integer(index)));
				}
				break;
				}
			if(marker == 'a'){
				System.out.print(Acount.remove(new Integer(index)));
			}else{
				System.out.print(Bcount.remove(new Integer(index)));
			}

		}//check for free edge
		}//loop the Array
		return result;
		}//Even level
		else{		//Odd level
			if(current.matched == false)
			{
				current.matched = true;
				return true;
			}
//			System.out.println(current.matched+"##################################################################"+current.nextNode.size());
			for(int i = 0;i<current.nextNode.size();i++){//,System.out.println("i="+i)){
				if(current.nextNode.get(i).matched == true){
					if(marker == 'a'){
								if(Bcount.contains(new Integer(current.nextNode.get(i).nextNodeNum)))
							return false;
						Acount.add(index);
					}else{
						if(Acount.contains(new Integer(current.nextNode.get(i).nextNodeNum)))
								return false;
						Bcount.add(index);
					}
					if(result = CheckAugmentaryPath(1+level,newmarker,current.nextNode.get(i).nextNodeNum,Acount,Bcount))
						{
						current.nextNode.get(i).matched = false;
						current.matched =  true;
						if(marker == 'a'){
							System.out.print(Acount.remove(new Integer(index)));
						}else{
							System.out.print(Bcount.remove(new Integer(index)));
						}
						break;
						}
					if(marker == 'a'){
						System.out.print(Acount.remove(new Integer(index)));
					}else{
						System.out.print(Bcount.remove(new Integer(index)));
					}

					break; //____________________________________________________comment if needed
				}//check for matched edge
			}//loop the Arraylist of edges
			return result;
		}//odd level
	}
	
	public static void main(String args[]) throws Exception{
		Matching a = new Matching();
//		a.printAll();
		a.print();
		a.findMaxMatches();
		a.print();
//		System.out.print("a.maxNumMatches=");
//		System.out.println(a.maxNumMatches);
	}
	
}