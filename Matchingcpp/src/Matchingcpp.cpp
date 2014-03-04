//package proj7;
#include "Node.cpp"
#include "NodeInfo.cpp"

class Matching{
public:
	Node* A ;
	int alength,blength;
	Node* B ;
	queue<NodeInfo> Queue;// = new Queue<NodeInfo>();
	int numEdges;// = 0;
	int maxNumMatches;// =0;
	list<int> aQ;// = new list<int>();
	list <int> bQ;// = new list<int>();

	// split: receives a char delimiter; returns a vector of strings
	// By default ignores repeated delimiters, unless argument rep == 1.
	vector<string> split(char delim, int rep, string work) {
		vector<string> flds;
	    if (!flds.empty()) flds.clear();  // empty vector if necessary
	    //string work = "12,123,43,64,893";
	    string buf = "";
	    unsigned i = 0;
	    while (i < work.length()) {
	        if (work[i] != delim){
	           if(work[i] != '(' && work[i] != ')' && work[i] != ',' && work[i] != ' ') buf += work[i];//cout<<work[i]<<endl;}
	        }else if (rep == 1) {
	            flds.push_back(buf);
	            buf = "";
	        } else if (buf.length() > 0) {
	            flds.push_back(buf);
	            buf = "";
	        }
	        i++;
	    }
	    if (!buf.empty())
	        flds.push_back(buf);
	    return flds;
	};



	Matching(){
		ifstream infile;
		vector <string> fields;
		string STRING;
		infile.open("input.txt");
		int  i = 0;

		getline(infile,STRING); // Saves the line in STRING.
//		cout<<STRING<<endl; // Prints our STRING.
		fields = split(' ',1,STRING);
		for (unsigned k = 0; k < fields.size(); k++)
		{
			switch(k+1){
			case 1: alength = atoi(fields[k].c_str());
					A = new Node[alength];
					for (int i = 0; i < alength; ++i)
					{
					    A[i] = Node();
					}
					break;
			case 2: blength = atoi(fields[k].c_str());
					this->B = new Node[blength];
					for (int i = 0; i < blength; ++i)
					{
						B[i] = Node();
					}

					break;
			case 3: this->numEdges = atoi(fields[k].c_str());
					break;
			}
		}

		int a,b,e;
//		Edge edgea,edgeb;
		while(!infile.eof()) // To get you all the lines.
		{
			getline(infile,STRING); // Saves the line in STRING.
//		cout<<STRING<<endl; // Prints our STRING.
			fields = split(' ',1,STRING);
		for (unsigned k = 0; k < fields.size(); k++)
		{
			switch(k+1){
			case 1: a = atoi(fields[k].c_str())-1;
					break;
			case 2: b = atoi(fields[k].c_str())-1;
					break;
			case 3: e = atoi(fields[k].c_str());
					break;
			}
		}
//		cout<<A[a]<<B[b]<<e<<endl;
		if(	A[a].matched == false && B[b].matched == false ){
			A[a].matched = true;
			B[b].matched = true;

			Edge edgea(b,e,true);
			A[a].nextNode.push_back(edgea);
			Edge edgeb(a,e,true);
			B[b].nextNode.push_back(edgeb);
			maxNumMatches++;
		}
		else{
			Edge edgea(b,e,false);
			A[a].nextNode.push_back(edgea);
			Edge edgeb(a,e,false);
			B[b].nextNode.push_back(edgeb);
		}

			i++;
		}
		infile.close();
	};

	void printAll(){
		cout<<("MaxMatchings:"+this->maxNumMatches);
		for(int k=0;k<this->alength;k++){
			for(unsigned int i=0;i<A[k].nextNode.size();i++){
					cout<<"("<<(k+1)<<","<<(A[k].nextNode[i].nextNodeNum+1)<<","<<(A[k].nextNode[i].matched)<<"), "<<"QueuedList:";
			}
		}
		cout<<("MaxMatchings:"+this->maxNumMatches);
	};

	
	void print(){
		ofstream outfile;
		outfile.open("output.txt");
cout<<("MaxMatchings:"+this->maxNumMatches);
outfile<<("MaxMatchings:"+this->maxNumMatches);
		int counter = 0;
		for(int k=0;k<alength;k++){
			for(unsigned int i =0;i<A[k].nextNode.size();i++){
				if(A[k].nextNode[i].matched == true)
					{
					counter++;
					outfile<<"("<<(k+1)<<","<<(A[k].nextNode[i].nextNodeNum+1)<<"),";
					break;
					}
			}
		}
//outfile<<("MaxMatchings:"+this->maxNumMatches);
cout<<endl<<"MaxMatchings:"<<this->maxNumMatches;//<<"counter="<<counter<<"Length"<<alength<<endl;
outfile.close();
	};
	
/*
	void findMaxMatches(){
		int lenA = alength;
		int lenB = blength;
		int level = 0;
		ArrayList<int> visted = new ArrayList<int>();
//		ArrayList<int> Bvisted = new ArrayList<int>();

		char unMatched = '\0';
		if(lenA < lenB){
			unMatched = 'a';
			for(int i = 0;i<lenA;i++)
				{	
				if(A[i].matched == false)
					{
					visted.add(i);
					if(CheckAugmentaryPath(level,unMatched,i,visted,new ArrayList<int>()))
						this->maxNumMatches++;
					visted.remove(new int(i));
					}
				}
		}else{
			unMatched = 'b';
			for(int i = 0;i<lenB;i++)
				{
				if(B[i].matched == false)
					{
					visted.add(i);
					if(CheckAugmentaryPath(level,unMatched,i,new ArrayList<int>(),visted))
						this->maxNumMatches++;
					visted.remove(new int(i));
					}
				}
		}
	}
*/
//Find max matches	
	void findMaxMatches(){
		int lenA = alength;
		int lenB = blength;

		char marker = '\0';
		if(lenA < lenB){
			marker = 'a';
			for(int i = 0;i<lenA;i++)
				{	
				if(A[i].matched == false)
					{
//					cout<<("A:");
					if(CheckAugmentaryBFSPath(marker,i,0)){
//						cout<<"\nTop:"<<marker<<(i+1);
						A[i].matched = true;
						this->maxNumMatches++;
//						this->print();
					}
					}
				}
		}else{
			marker = 'b';
			for(int i = 0;i<lenB;i++)
				{
				if(B[i].matched == false)
					{
//					cout<<("B:");
					if(CheckAugmentaryBFSPath(marker,i,0)){
//						cout<<("\nTop:"+marker+(i+1));
						B[i].matched = true;
						this->maxNumMatches++;
//						this->print();
					}
					}
				}
		}
		
		
	};

//########################## to the get the node	
	inline Node getNode(char marker, int index){

		switch(marker){
		case 'a':
			return A[index];
		case 'b':
			return B[index];
		default:
			cout<<("Failed to getNode");
			return Node();
		}
	};

//########################## to the get the node
	void setNode(char marker, int index,bool value){
		switch(marker){
		case 'a':
		 A[index].matched = value;
		 break;
	case 'b':
		B[index].matched = value;
		break;
	default:
		cout<<("Failed to setNode");
			break;
		}
	};

//########################## to the get the node	
void setEdge(char marker, int index,int nextnode,bool value){
	switch(marker){
	case 'a':
		 A[index].nextNode[nextnode].matched = value;
		 break;
	case 'b':
		B[index].nextNode[nextnode].matched = value;
		break;
	default:
		cout<<("Failed to setEdge");
		break;
	}
};

//########################## to the get the node	
void filpEdge(char marker, int index,int nextnode){ 
	switch(marker){
	case 'a':
		for(unsigned int i =0;i<A[index].nextNode.size();i++){
		 if(A[index].nextNode[i].nextNodeNum == nextnode)
			A[index].nextNode[i].matched = !(A[index].nextNode[i].matched);
		}
		for(unsigned int i =0;i<B[nextnode].nextNode.size();i++){
			if(B[nextnode].nextNode[i].nextNodeNum == index){
				B[nextnode].nextNode[i].matched = !(B[nextnode].nextNode[i].matched);
			}
		}
		 break;
	case 'b':
		for(unsigned int i = 0;i<B[index].nextNode.size();i++){
			 if(B[index].nextNode[i].nextNodeNum == nextnode){
				 B[index].nextNode[i].matched = !(B[index].nextNode[i].matched);
			 }
		}
		for(unsigned int i = 0;i<A[nextnode].nextNode.size();i++){
			 if(A[nextnode].nextNode[i].nextNodeNum == index){
				 A[nextnode].nextNode[i].matched = !(A[nextnode].nextNode[i].matched);
			 }
		}
		break;
	default:
		cout<<("Failed to flip:"+marker);
		break;
	}
};


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
		cout<<("Failed to setincoming:"+marker);
		break;
	}
};

	
//########################New marker
	char getnewMarker(char marker){
		switch(marker){
		case 'a':
			return 'b';
		case 'b':
			return 'a';
		default:
			cout<<("Failed to getMarker");
			return 'f';
		}
	};


//########################New marker
	Node getNextNode(char marker,int index){
		switch(marker){
		case 'a':
			return B[index];
		case 'b':
			return A[index];
		default:
			cout<<("Failed to getNode");
			return Node();
		}
	};
	
//######################## Roll back and reset the incoming node values
	void rollBack(char marker,int index){//,int newmarker,int indexFound){
		Node current = getNode(marker,index);
		int incoming = -1,lindex = index;
		char lmarker = marker;
		while(current.incoming != -1  ){
			
			incoming = current.incoming;
			//current.incoming = -1;
			this->setIncoming(marker, lindex, -1);
			marker = getnewMarker(marker);
			current = getNode(marker,incoming);
			lindex = incoming;
		}
		this->setIncoming(lmarker, index, -1);
	};
	
//##########################Rollback visited nodes
	void rollback()
	{
		while(aQ.size()>0){
			A[aQ.front()].incoming = -1;
			aQ.pop_front();
		}
		while(bQ.size()>0){
			B[bQ.front()].incoming = -1;
			bQ.pop_front();
		}
	};

//######################## Roll back and reset the incoming node values
	void setPath(char marker,int index){
		Node current = getNode(marker,index);
		int incoming = -1,size = 0;
		current.matched = true;// set the current as matched -->Leaf node
		while(current.incoming != -1){
			incoming = current.incoming;
			// set the edge
			size = current.nextNode.size();
			if(size == 0) cout<<("SetPath failed: incoming is set but parent not found");
			this->filpEdge(marker, index, incoming);
			index = incoming;
			current.incoming = -1;//reset the incoming node
			marker = getnewMarker(marker);//next marker
//cout<<("in:"+marker+(incoming+1));
			current = getNode(marker,incoming);//getting parent
		}
		if(current.incoming == -1){
//cout<<"Reached top";
			this->setNode(marker, index, true);
//			current.matched = true;//set the current as matched-->Root node
		}
	};
	
	void setQueue(char marker,int index){
		switch(marker){
		case 'a':
			aQ.push_back(index);
//			cout<<("A"+(index+1));
			break;
		case 'b':
			bQ.push_back(index);
//			cout<<("B"+(index+1));
			break;
		default:
			cout<<("Failed at SetQueue");
		}
	};
	
	bool CheckAugmentaryBFSPath(char marker,int index,int level){
//		int len=0;
		int lindex = 0;
//		int noOfEdges = 0;
//		Node current = getNode(marker,index);
//		Node nextNode = NULL;
		bool result = false;
//		char newmarker = getnewMarker(marker);
//		bool freeEdgeFound = false;
		setQueue(marker,index);
		NodeInfo nodeInfo(level,index,marker);
		Queue.push(nodeInfo);
		while(Queue.empty() == false && result == false){
			nodeInfo = Queue.front();
			level = nodeInfo.level;
			marker = nodeInfo.marker;
			lindex = nodeInfo.index;
//			newmarker = getnewMarker(marker);
//			current = getNode(marker,index);
//			noOfEdges = current.nextNode.size();
//			freeEdgeFound = false;
//			cout<<("Level :"+level);
//			cout<<("qSize::"+Queue.size());
			result = checkAugAtLevels(marker,lindex,level);
//			cout<<result;
		}
//		cout<<("returning :"+result);
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
		while(Queue.empty() == false)Queue.pop();
		return result;
	};

	
	
	bool checkAugAtLevels(char marker,int index,int level){
//		bool freeEdgeFound = false;
		NodeInfo nodeInfo(0,0,0);
		char newmarker = '\0';
		int noOfEdges = 0;
		Node current;
		Node nextNode;
		bool result = false;

		while(Queue.empty() == false && Queue.front().level == level){
			//initialize the pointers
//cout<<"qSize::"<<Queue.size();
			nodeInfo = Queue.front();
			level = nodeInfo.level;
			marker = nodeInfo.marker;
			index = nodeInfo.index;
			newmarker = getnewMarker(marker);
			current = getNode(marker,index);
			noOfEdges = current.nextNode.size();
//			freeEdgeFound = false;
//cout<<"Level:"<<level <<"qsize:" << Queue.size()<<"edges:"<<noOfEdges<<endl;
			if(level%2 == 0){//Even level

				for(int  i = 0;i<noOfEdges;i++){
//cout<<"matched:"<<current.matched<<"next:"<<current.nextNode[i].nextNodeNum<<"freeE:"<<current.nextNode[i].matched<<endl;
					if(current.nextNode[i].matched == false){ // Free Edge found
//						freeEdgeFound = true;
						nextNode = getNode(newmarker,current.nextNode[i].nextNodeNum);
//cout<<"next:"<<nextNode.matched<<endl;
						if(nextNode.matched == false)//unmatched node at odd level is found -->success
						{
//							freeEdgeFound = true;
//cout<<"crw:fr"+(index+1)<<marker<<(current.nextNode[i].nextNodeNum+1);
							current.nextNode[i].matched = true;//edge is set to true
							
//							current.nextNode.set(i, new Edge(current.nextNode[i].nextNodeNum, current.nextNode[i].weight, true));
							this->filpEdge(marker, index, current.nextNode[i].nextNodeNum);//false to true
//							nextNode.incoming = index;
//cout<<"crw:"+(current.nextNode[i].nextNodeNum+1)<<"matched:"<<current.nextNode[i].matched+marker+(index+1);
							setPath(marker, index);
							setNode(newmarker, current.nextNode[i].nextNodeNum, true);
//							nextNode.matched = true;
							return true;
						}
						else{//push to Queue if matched node at odd level found
							if(nextNode.incoming == -1)
							{
//								freeEdgeFound = true;
//								nextNode.incoming = index;
								this->setIncoming(newmarker, current.nextNode[i].nextNodeNum, index);
//cout<<"setting incoming:"<<newmarker<<current.nextNode[i].nextNodeNum+1<<"="<<(index+1);
//								cout<<("Q1:"+marker+(nextNode.incoming+1) +nextNode.matched+newmarker+(current.nextNode[i].nextNodeNum+1));
								//push to Queue if matched node at even level found
								setQueue(newmarker,current.nextNode[i].nextNodeNum);
								NodeInfo inodeInfo(level+1,current.nextNode[i].nextNodeNum,newmarker);
								Queue.push(inodeInfo);
							}else{ //cycle found
//cout<<("cycle found1:"+marker+(nextNode.incoming+1) +nextNode.matched+newmarker+(i+1));//
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
					if(current.nextNode[i].matched == true){ // Matched Edge found
//						nextNode[i].matched
//						freeEdgeFound = true;
//						cout<<(marker+"("+(index+1)+","+(current.nextNode[i].nextNodeNum+1)+","+(current.nextNode[i].matched)+")"+newmarker+":"+
//								current.nextNode[i].nextNodeNum);
						nextNode = getNode(newmarker,current.nextNode[i].nextNodeNum);
//						cout<<("In:"+nextNode.incoming+"matched:"+nextNode.matched);
						if(nextNode.matched == true)//unmatched node at odd level is found -->success
						{
							if(nextNode.incoming == -1)
							{
//								nextNode.incoming = index;
								this->setIncoming(newmarker, current.nextNode[i].nextNodeNum, index);
//								cout<<("setting incoming:"+newmarker+(current.nextNode[i].nextNodeNum+1)+"="+(index+1));
//								cout<<("Q2:"+marker+(nextNode.incoming+1) +nextNode.matched+newmarker+(current.nextNode[i].nextNodeNum+1));
								//push to Queue if matched node at even level found
								setQueue(newmarker,current.nextNode[i].nextNodeNum);
								NodeInfo inodeInfo(level+1,current.nextNode[i].nextNodeNum,newmarker);
								Queue.push(inodeInfo);
//								break;
							}else{ //cycle found
//cout<<("cycle found2:"+marker+(nextNode.incoming+1) +nextNode.matched+newmarker+(current.nextNode[i].nextNodeNum+1));
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
			Queue.pop();
		}//Queue is not empty
//		cout<<("qSize::"+Queue.size());
		return result;
	}
/*
	bool CheckAugmentaryPath(int level,char marker,int index,ArrayList<int> Acount,ArrayList<int> Bcount){
//		cout<<(Acount.size()+"$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$"+Bcount.size());
		Node current = NULL;
		bool result = false;
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
		if(current.nextNode[i].matched == false){
			if(marker == 'a'){
				if(Bcount.contains(new int(current.nextNode[i].nextNodeNum)))
				return false;
				Acount.add(index);
			}else{
				if(Acount.contains(new int(current.nextNode[i].nextNodeNum)))
				return false;
				Bcount.add(index);
			}
			if(result = CheckAugmentaryPath(1+level,newmarker,current.nextNode[i].nextNodeNum,Acount,Bcount))
				{
				current.nextNode[i].matched = true;
				current.matched =  true;
				if(marker == 'a'){
					cout<<(Acount.remove(new int(index)));
				}else{
					cout<<(Bcount.remove(new int(index)));
				}
				break;
				}
			if(marker == 'a'){
				cout<<(Acount.remove(new int(index)));
			}else{
				cout<<(Bcount.remove(new int(index)));
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
//			cout<<(current.matched+"##################################################################"+current.nextNode.size());
			for(int i = 0;i<current.nextNode.size();i++){//,cout<<("i="+i)){
				if(current.nextNode[i].matched == true){
					if(marker == 'a'){
								if(Bcount.contains(new int(current.nextNode[i].nextNodeNum)))
							return false;
						Acount.add(index);
					}else{
						if(Acount.contains(new int(current.nextNode[i].nextNodeNum)))
								return false;
						Bcount.add(index);
					}
					if(result = CheckAugmentaryPath(1+level,newmarker,current.nextNode[i].nextNodeNum,Acount,Bcount))
						{
						current.nextNode[i].matched = false;
						current.matched =  true;
						if(marker == 'a'){
							cout<<(Acount.remove(new int(index)));
						}else{
							cout<<(Bcount.remove(new int(index)));
						}
						break;
						}
					if(marker == 'a'){
						cout<<(Acount.remove(new int(index)));
					}else{
						cout<<(Bcount.remove(new int(index)));
					}

					break; //____________________________________________________comment if needed
				}//check for matched edge
			}//loop the Arraylist of edges
			return result;
		}//odd level
	}
	*/
};



int main() {
	Matching* a = new Matching();
//		a.printAll();
//	a->print();
	a->findMaxMatches();
	a->print();
//		cout<<("a.maxNumMatches=");
//		cout<<(a.maxNumMatches);
	return 0;
};

