#ifndef PolyLinkedList_H
#define PolyLinkedList_H
#include <Node.h>

class PolyLinkedList{

	public:
	Node * Head;

	 PolyLinkedList(){
		 Head = 0;
	 };
	 PolyLinkedList(Node* Head){
		 this->Head = Head;
	 };
	 PolyLinkedList(int n){
		Node* temp = new Node() ;
		Head = temp;
		for(int i =0;i<n;i++){
			if(i+1<n) temp->next = new Node();
			temp->exp = i;
			temp->no = rand()%10;
//			cout<<"INIT:("<<temp->no<<","<<temp->exp<<")";
			temp = temp->next;
		}
	 };
	 PolyLinkedList(string No){
		 Node* temp = new Node() ;
		 Head = temp;
		 string::reverse_iterator rit=No.rbegin();
		 for (unsigned i=0; rit!=No.rend(); ++rit,i++){
				if(i+1<No.length()) temp->next = new Node();
				temp->exp = i;
				temp->no = *rit - '0';
//				cout<<i<<":"<<temp->no<<endl;
				temp = temp->next;
		 }
	 };
	 PolyLinkedList(int no,int exp){
			Node* temp = new Node() ;
			Head = temp;
			Head->exp = exp;
			Head->no = no;
	 };
	 Node* Add(Node* a, Node* b);
	 PolyLinkedList* Add(PolyLinkedList* a, PolyLinkedList* b);
	 PolyLinkedList* Sub(PolyLinkedList* a, PolyLinkedList* b, bool &c);
	 PolyLinkedList* Mul(PolyLinkedList* a, PolyLinkedList* b);
};

#endif
