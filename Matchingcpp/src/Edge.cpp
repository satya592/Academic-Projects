//package proj7;
#include <iostream>
#include <fstream>
#include <string>
#include <algorithm>
#include <vector>
#include <ctype.h>
#include <iomanip>      // std::setfill, std::setw
#include <time.h>
#include <unistd.h>
#include <queue>
#include <list>

class Edge{
	public:
	int nextNodeNum;
	bool matched;// = false;
	int weight;
	
	Edge(){
		this->nextNodeNum = 0;
		this->weight = 0;
		this->matched = 0;
	}

	Edge(int v, int weight, bool matched){
		this->nextNodeNum = v;
		this->weight = weight;
		this->matched = matched;
	}
	//Getters
/*	int getNextNodeNum(){
		
	}
	boolean getMatched(){
		
	}
*/	
};
