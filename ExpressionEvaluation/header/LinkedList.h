#ifndef PolyLinkedList_H
#define PolyLinkedList_H
#include "Node.h"

enum sign{positive = 0, negative = 1};

class PolyLinkedList{

	public:
	enum sign sign; //0 is positive 1 is negative
	Node * Head;

	 PolyLinkedList(){
		 sign = positive;
		 Head = 0;
	 };
	 PolyLinkedList(Node* Head, enum sign sign=positive){
		 this->Head = Head;
		 this->sign = sign;
	 };

	 PolyLinkedList(int n,enum sign sign=positive){//Generates a random linked list of length n
		Node* temp = new Node() ;
		this->sign = sign;
		Head = temp;
		for(short int i =0;i<n;i++){
			if(i+1<n) temp->next = new Node();
			temp->exp = i;
			temp->no = rand()%10;
//			cout<<"INIT:("<<temp->no<<","<<temp->exp<<")";
			temp = temp->next;
		}
	 };
	 PolyLinkedList(string No, enum sign sign=positive){
		 Node* temp = new Node() ;
		 Head = temp;
		 this->sign = sign;
		 int ifNeg = 0;
//		 bool firstNonZero = true;
//cout<<"Create:"<<No<<endl;
		 if(No[0] == '-') {this->sign = negative;ifNeg=1;}
		 short zeros = 0;
		 for(zeros = ifNeg;No[zeros]=='0';zeros++);
//cout<<"zeros"<<zeros;
		 string::reverse_iterator rit=No.rbegin();
		 for (unsigned short i=0; rit + zeros!=No.rend(); ++rit,i++){
			 if((unsigned)i+1 < No.length()-ifNeg)
					temp->next = new Node();
				temp->exp = i;
				temp->no = *rit - '0';
//				cout<<i<<":"<<temp->no<<endl;
				temp = temp->next;
		 }
//cout<<"After Creation- reverse print";
//	temp = Head;
//	while(temp!=NULL){
//		cout<<temp->no;
//		temp = temp->next;
//	}
//	cout<<endl;
	 };
	 PolyLinkedList(int no,int exp,enum sign sign=positive){//unused construct
			Node* temp = new Node() ;
			Head = temp;
			Head->exp = exp;
			Head->no = no;
			this->sign = sign;
	 };

	 ~PolyLinkedList()
	   {
		 	Node* del;
			Node* temp = del = this->Head;
			while(temp !=0){
				temp=temp->next;
				delete del;
				del = temp;
			}
	   };

	 void Clean(Node * clean)
	   {
		 	Node* del;
			Node* temp = del = clean;
			while(temp != 0){
				temp=temp->next;
				delete del;
				del = temp;
			}
	   };


	 string toString(){
	 		 	string display = "";
	 			Node* temp = this->Head;
	 			while(temp !=0){
	 				//cout<<temp->no<<endl;
	 				//display+=temp->no+48;
	 				display.push_back(temp->no+48);
	 				temp=temp->next;
	 			}
	 			string result="";
//	 			if(display.length( ) == 0) result = "0";
//	 			else{
	 			if(sign == negative) result = "-";
	 			bool firstTime = true;
//	 			cout<<display<<endl;
	 			for (short i=display.length( )-1; i>=0 ; i--) {
//					cout<<display.length()<<" "<<i<<": ";
	 				if(firstTime){
						if(display[i] == '0') continue;
						else firstTime = false;
					}
	 				result = result + display[i];
	 			}
//	 			}
	 			if(result == "") result = "0";
	 			return result;
	 	 };

	 string toStringNoSign(){
	 		 	string display = "";
	 			Node* temp = this->Head;
	 			while(temp !=0){
	 				display+=temp->no+48;
	 				temp=temp->next;
	 			}
	 			string result="";
//	 			if(display.length( ) == 0) result = "0";
//	 			else{
//	 			if(sign == negative) result = "-";
	 			bool firstTime = true;
//	 			cout<<display<<endl;
	 			for (short i=display.length( )-1; i>=0 ; i--) {
//					cout<<display.length()<<" "<<i<<": ";
	 				if(firstTime){
						if(display[i] == '0') continue;
						else firstTime = false;
					}
	 				result = result + display[i];
	 			}
//	 			}
	 			if(result == "") result = "0";
	 			return result;
	 	 };


	 void Print(){
		 	string disply = "";
			Node* temp = this->Head;
			bool firstNonZero = true;
			while(temp !=0){
				disply+=temp->no+48;
				temp=temp->next;
			}
			if(sign == negative) cout<<"-";
			 for (string::reverse_iterator rit=disply.rbegin(); rit!=disply.rend(); ++rit){
				 if(firstNonZero && *rit == '0') continue;
				 else {
					 firstNonZero = false;
					 cout<<*rit;//<<"yes";

				 }
			 }
			 if(disply =="") cout<<"0";

	 };

	 void Print(ostream &outfile){
		 	string disply = "";
			Node* temp = this->Head;
			bool firstNonZero = true;
			while(temp !=0){
				disply+=temp->no+48;
				temp=temp->next;
			}
			if(sign == negative) outfile<<"-";
			 for (string::reverse_iterator rit=disply.rbegin(); rit!=disply.rend(); ++rit){
				 if(firstNonZero && *rit == '0') continue;
				 else {
					 firstNonZero = false;
					 outfile<<*rit;
				 }
			 }

	 };


	 PolyLinkedList* Copy(){
		 bool firstTime = true;
		 Node* temp = this->Head,*newTemp;

		 PolyLinkedList* New = new PolyLinkedList();
		 New->sign =  this->sign;
		 while(temp != 0){
			 if(firstTime) { New->Head = newTemp = new Node();firstTime =false;}
			 else newTemp = newTemp->next = new Node();
			 newTemp->exp = temp->exp;
			 newTemp->no = temp->no;
			 temp = temp->next;
		 }
		 return New;
	 };
		uint8_t chartoNo(char ch){
			switch(ch){
			case '0': return 0;break;
			case '1': return 1;break;
			case '2': return 2;break;
			case '3': return 3;break;
			case '4': return 4;break;
			case '5': return 5;break;
			case '6': return 6;break;
			case '7': return 7;break;
			case '8': return 8;break;
			case '9': return 9;break;
			default: return 0;
			}
		};
//insert at head
	 void Insert(char ch, int exp = 0){
		 Node* temp = new Node(chartoNo(ch),exp);
		 temp->next = this->Head;
		 this->Head = temp;
	 };

 //insert at head
	 void Insert(int digit, int exp = 0){
		 Node* temp = new Node(digit,exp);
		 temp->next = this->Head;
		 this->Head = temp;
	 };


//to add zeros
	 string AddZeros(string str,int n){
		string zeros = "";
		 for(unsigned short i = 1;i<=n;i++)
			 zeros+="0";
		 return str+zeros;
	 }



	 bool Compare(PolyLinkedList* a, PolyLinkedList* b){
		 if(a->sign == b->sign){
			 string astr = a->toStringNoSign();
			 string bstr  = b->toStringNoSign();
			 int alength = astr.size();
			 int blength = bstr.size();
			 if(alength > blength ) return true;
			 else if(alength < blength ) return false;
			 else{
//				 cout<<astr<<"alen==blen"<<bstr<<astr.compare(bstr)<<endl;
				 return astr.compare(bstr)>= 0 ;
			 }
		 }else
		 {
//			 cout<<"asign!=bsign\n";
//			 a->Print();
//			 cout<<" ";
//			 b->Print();cout<<"so hwa\n";
			 string astr = a->toStringNoSign();
			 string bstr  = b->toStringNoSign();
			 int alength = astr.size();
			 int blength = bstr.size();
//cout<<astr<<":"<<alength<<"--"<<bstr<<":"<<blength<<endl;
			 if(alength > blength ) return true;
			 else if(alength < blength ) return false;
			 else{
//				 cout<<astr<<"alen==blen"<<bstr<<astr.compare(bstr)<<endl;
				 return astr.compare(bstr)>= 0 ;
			 }
		 }
	 };

	 bool CompareWithoutSign(PolyLinkedList* a, PolyLinkedList* b){
//			 cout<<"asign!=bsign\n";
//			 a->Print();
//			 cout<<" ";
//			 b->Print();cout<<"so hwa\n";
			 string astr = a->toStringNoSign();
			 string bstr  = b->toStringNoSign();
			 int alength = astr.size();
			 int blength = bstr.size();
//cout<<astr<<":"<<alength<<"--"<<bstr<<":"<<blength<<endl;
			 if(alength > blength ) return true;
			 else if(alength < blength ) return false;
			 else{
//				 cout<<astr<<"alen==blen"<<bstr<<astr.compare(bstr)<<endl;
				 return astr.compare(bstr)>= 0 ;
			 }
	 };

	 bool CompareWithoutSign(string astr, string bstr){
//			 cout<<"asign!=bsign\n";
//			 a->Print();
//			 cout<<" ";
//			 b->Print();cout<<"so hwa\n";
			 int alength = astr.size();
			 int blength = bstr.size();
//cout<<astr<<":"<<alength<<"--"<<bstr<<":"<<blength<<endl;
			 if(alength > blength ) return true;
			 else if(alength < blength ) return false;
			 else{
//				 cout<<astr<<"alen==blen"<<bstr<<astr.compare(bstr)<<endl;
				 return astr.compare(bstr)>= 0 ;
			 }
	 };


	 Node* Add(Node* a, Node* b);
	 PolyLinkedList* Add(PolyLinkedList* a, PolyLinkedList* b);
	 PolyLinkedList* Sub(PolyLinkedList* a, PolyLinkedList* b, bool &c);
	 PolyLinkedList* Mul(PolyLinkedList* a, PolyLinkedList* b);
	 PolyLinkedList* Pow(PolyLinkedList* a, PolyLinkedList* b);
	 PolyLinkedList* Pow(PolyLinkedList* a, long b);
	 PolyLinkedList* Div(PolyLinkedList* a, PolyLinkedList* b, bool sendReminder);
	 PolyLinkedList* Srt(PolyLinkedList* a,bool& error);
};

#endif
