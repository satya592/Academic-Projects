#include "Edge.cpp"
using namespace std;
class Node{
	//int node;
public:
	int incoming ;
	bool matched;// = false;
	vector<Edge> nextNode; // = new vector<Edge>();
	

	Node(){
		this->matched = false;
		this->incoming = -1;
	}
};
