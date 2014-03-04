#include "LinkedList.h"
using namespace std;
#define BASE 10

Node* PolyLinkedList::Add(Node* a, Node* b){
	PolyLinkedList* A = new PolyLinkedList(a);
	PolyLinkedList* B = new PolyLinkedList(b);
	PolyLinkedList* C = A->Add(A,B);
	Node* c = C->Head;
	return c;
};
//Addition of two poly linked list
PolyLinkedList* PolyLinkedList::Add(PolyLinkedList* a, PolyLinkedList* b){
	Node* result = new Node();
	Node* head = result;
	int carryDigit = 0, carryExp = 0, total = 0;
	bool firstTime = true;
	if(a->Head !=0 && b->Head !=0){
		Node* A = a->Head;
		Node* B = b->Head;

	while(A != 0 && B !=0){
		if(firstTime)  firstTime = false;
			else	result = result->next = new Node();
		if(carryExp>0 && carryExp<A->exp && carryExp<B->exp){//Carry exists and its exp is less than A & B
			result->exp = carryExp;
			result->no = carryDigit;
			carryExp = carryDigit = 0; //clear carries
		}else if(A->exp == B->exp){
			if(carryExp == A->exp){
				total = A->no + B->no + carryDigit;
				carryExp = carryDigit = 0; //clear carries
			}else total = A->no + B->no;
			if(total/BASE == 0){
				result->no = total;
			}else{
				result->no = total%BASE;
				carryDigit = total/BASE;
				carryExp = A->exp + 1;
			}
			result->exp = A->exp;
			A = A->next;
			B = B->next;
		}else if(A->exp < B->exp){
			if(carryExp == A->exp){ //carry exists and equal to A
				total = A->no + carryDigit;
				carryExp = carryDigit = 0; //clear carries
			}else total = A->no ;
			if(total/BASE == 0){
				result->no = total;
			}else{
				result->no = total%BASE;
				carryDigit = total/BASE;
				carryExp = A->exp + 1;
			}
			result->exp = A->exp;
			A = A->next;
		}else if(B->exp < A->exp){
			if(carryExp == B->exp){//carry exists and equals to B
				total = B->no + carryDigit;
				carryExp = carryDigit = 0; //clear carries
			}else total = B->no ;
			if(total/BASE == 0){
				result->no = total;
			}else{
				result->no = total%BASE;
				carryDigit = total/BASE;
				carryExp = B->exp + 1;
			}
			result->exp = B->exp;
			B = B->next;
		}

	}//While A and B both are not empty
	while(A!=0){
		if(firstTime)  firstTime = false;
			else	result = result->next = new Node();
		if(carryExp>0 && carryExp<A->exp){//carry exits and less than to A
			result->exp = carryExp;
			result->no = carryDigit;
			carryExp = carryDigit = 0; //clear carries
		}else if(carryExp>0 && carryExp == A->exp){ //carry exits and equal to A
				result->exp = carryExp;
				total = carryDigit + A->no;
				if(total/BASE == 0){
					result->no = total;
					carryExp = carryDigit = 0; //clear carries
				}else{
					result->no = total%BASE;
					carryDigit = total/BASE;
					carryExp = A->exp + 1;
				}
				A = A->next;
		}else{//carry does exits so copy A
			result->no = A->no;
			result->exp = A->exp;
			A = A->next;
		}
	}//While A is not empty
	while(B!=0){
		if(firstTime)  firstTime = false;
			else	result = result->next = new Node();
		if(carryExp>0 && carryExp<B->exp){//carry exits and less than to A
			result->exp = carryExp;
			result->no = carryDigit;
			carryExp = carryDigit = 0; //clear carries
		}else if(carryExp>0 && carryExp == B->exp){ //carry exits and equal to A
				result->exp = carryExp;
				total = carryDigit + B->no;
				if(total/BASE == 0){
					result->no = total;
					carryExp = carryDigit = 0; //clear carries
				}else{
					result->no = total%BASE;
					carryDigit = total/BASE;
					carryExp = B->exp + 1;
				}
				B = B->next;
		}else{//carry does exits so copy A
			result->no = B->no;
			result->exp = B->exp;
			B = B->next;
		}
	}//While B is not empty
	if(carryExp>0 && A == 0 && B == 0){
		result = result->next = new Node();
		result->exp = carryExp;
		result->no = carryDigit;
		carryExp = carryDigit = 0; //clear carries
	}
	}//If A and B are not empty
	return new PolyLinkedList(head);
};
//sub of two poly linked list
PolyLinkedList* PolyLinkedList::Sub(PolyLinkedList* a, PolyLinkedList* b, bool &NotNegative){
	NotNegative = false;
	bool firstTime=true;
	Node* result = new Node();
	Node* head = result;
	int total = 0;
	Node *Borrow;
	PolyLinkedList* c=0;
//cout<<"a:";a->toString();cout<<"b:";
b->toString();
	if(a->Head !=0 && b->Head !=0){
		NotNegative = true;
		Node* A = a->Head;
		Node* B = b->Head;
	while(A != 0 && B !=0){
//cout<<"A:"<<A->no<<","<<A->exp<<"B:"<<B->no<<","<<B->exp<<endl;
		if(firstTime)  firstTime = false;
			else	result = result->next = new Node();
			if(A->exp == B->exp){
			total = A->no - B->no;
			if(total>= 0){
				result->no = total;
			}else{
//cout<<"Total:"<<total<<"="<<A->no<<"-"<<B->no<<endl;
				total+=BASE;
				result->no = total;
				Borrow = new Node(1,A->exp+1);//Add borrow to B
				B = b->Add(Borrow,B);
			}
			result->exp = A->exp;
			A = A->next;
			B = B->next;
		}else if(A->exp < B->exp){
			result->no = A->no;
			result->exp = A->exp;
//cout<<"TotalA:"<<total<<"="<<A->no<<":"<<A->exp<<endl;
			A = A->next;
		}else if(B->exp < A->exp){
			total = BASE - B->no ;
//cout<<"TotalB:"<<total<<"="<<B->no<<":"<<B->exp<<endl;
			result->no = total;
			result->exp = B->exp;
			Borrow = new Node(1,B->exp+1);//Add borrow to B
			B = b->Add(Borrow,B);
			B = B->next;
		}
	}//While A and B both are not empty
	while(A!=0){
		if(firstTime)  firstTime = false;
			else	result = result->next = new Node();
			result->no = A->no;
			result->exp = A->exp;
			A = A->next;
	}//While A is not empty
	while(B!=0){
		if(firstTime)  firstTime = false;
			else	result = result->next = new Node();
		NotNegative = false;
		break;
	}//If A and B are not empty
	c = new PolyLinkedList(head);
	}//If
c->toString();
return c;
};
//Mul of two poly linked list
PolyLinkedList* PolyLinkedList::Mul(PolyLinkedList* a, PolyLinkedList* b){
//	cout<<"Mul"<<endl;
	Node* result = 0, *totalResultSet = new Node();
	Node* head  = 0;
	int  total = 0;
	bool firstTime = true;
	bool firstTimeCarry = true;
	Node *carry = new Node(0,0);
	Node *totalCarries = new Node(0,0);
	Node *temp = totalCarries;
//	cout<<a->Print()<<":"<<b->Print();
	if(a->Head !=0 && b->Head !=0){
		Node* B = b->Head;
		while(B !=0){
//			cout<<"Mul-1"<<endl;
			Node* A = a->Head;//Re-initialize A
			while(A!=0){//Multiply A with one node of B
//				cout<<"Mul-2"<<endl;
				//if(B->no == 0) break;
				if(firstTime){head = result = new Node();  firstTime = false;}
					else {	result = result->next = new Node();}//Append next node to result}
				result->exp = A->exp + B->exp;
				total = A->no * B->no;
				if(total/BASE>0){//If carry exits
					carry->no = total/BASE;
					carry->exp = result->exp + 1;
					result->no = total%BASE;
				}
				else {
					result->no	= total;
				}
				if(carry->no != 0){//If carry exits add carry to totalCarries Node
					//totalCarries =	b->Add(carry,totalCarries);//B = b->Add(Borrow,B);
					if(!firstTimeCarry){temp->next = new Node(); temp = temp->next;}
					else {firstTimeCarry = false ;}
					temp->exp =carry->exp;
					temp->no =	carry->no;

//					cout<<"fault:"<<carry->no<<":"<<carry->exp;
					carry->exp = 0;
					carry->no = 0;
				}
				A = A->next;
			}//while A is not empty
//		cout<<"\nBefore Head->";head->Print();
//		cout<<"\nCarries->";totalCarries->Print();
		head = a->Add(head,totalCarries );//B = b->Add(Borrow,B);
//		cout<<"\nAfter Head->";head->Print();
		temp = totalCarries = new Node();
		firstTimeCarry = true;
		totalResultSet = a->Add(head,totalResultSet );//B = b->Add(Borrow,B);
//		cout<<"\nResult->";totalResultSet->Print();
//			cout<<"Mul-4"<<endl;
		B = B->next;//Multiple with next element
//		a->Clean(head);
		firstTime = true;
		}//While B is not empty

//		totalResultSet = b->Add(totalCarries,totalResultSet);//Add all carries to head
//		cout<<"Mul-1"<<endl;
	}//If A and B are not empty
	return new PolyLinkedList(totalResultSet);
};
//power of two poly linked list
PolyLinkedList* PolyLinkedList::Pow(PolyLinkedList* a,long b){
	PolyLinkedList* temp = new PolyLinkedList(1,0);
	PolyLinkedList* A;
//	PolyLinkedList* B;
	if(b==0) return temp;
	else if(b==1) return a;
	else {
		A = this->Pow(a,b/2);
//		A->Print();
		if(b%2 == 1) { return Mul(Mul(A,A),a);}
		else	{return Mul(A,A);}
	}
}


PolyLinkedList* PolyLinkedList::Pow(PolyLinkedList* a,PolyLinkedList* b){
//	PolyLinkedList* C = a->Copy();
	PolyLinkedList* temp = new PolyLinkedList(1,0);
	//	cout<<"I am in Pow:";C->Print();
	Node* B = b->Head;
	long n = 0;//10 power 0 is 1
	while(B!=0){
	n += B->no*pow((long)BASE,B->exp);//long double pow (long double base, int exponent);
	B=B->next;
	}
//	while(n)
//	{
//	if (n & 1)
//		temp = Mul(temp,C);
//	n >>= 1;
//	C = Mul(C,C);
//	}
	temp = Pow(a,n);
//	for(long double i=1;i<n;i++){
//	cout<<i<<":"; 	C->Print(); cout<<":"; a->Print(); cout<<endl;
//	C = Mul(a,C);
//	cout<<i<<":"; 	C->Print(); cout<<":"; a->Print();cout<<endl;
//	}
	if(a->sign == negative) {
		if(n%2 == 0) temp->sign = positive;
		else temp->sign = negative;
	}
	return temp;
};

PolyLinkedList* PolyLinkedList::Div(PolyLinkedList* a, PolyLinkedList* b, bool  sendReminder){
	PolyLinkedList* C = new PolyLinkedList(), *A = NULL, *B= NULL;//,*R = NULL;
	A = a->Copy();
//	B = b->Copy();
	int zeros = 0,q = 0;
	bool isnegative =  false,firstTime = false;
	string astr = A->toStringNoSign(),bstr = b->toStringNoSign();
	if(a->CompareWithoutSign(astr,bstr)){
	while(A->CompareWithoutSign(A->toStringNoSign(),bstr)){
		zeros = A->toStringNoSign().length() - bstr.length();
		q=0;
		if(zeros>0){
			do{
				bstr = B->AddZeros(b->toStringNoSign(),zeros);
//cout<<"\nastr:"<<A->toStringNoSign()<<"bstr:"<<bstr<<"zeros:"<<zeros<<endl;
				zeros--;
				astr = A->toStringNoSign() ;
			}while(a->CompareWithoutSign(bstr,astr) && bstr != astr);
			zeros++;
		}
		B = new PolyLinkedList(bstr);
		while(A->CompareWithoutSign(astr,bstr)){
//cout<<"bstr:"<<bstr;
//cout<<"astr:"<<astr<<endl;
//			cin>>isnegative;
			A = A->Sub(A,B,isnegative);
			q++;
			astr = A->toStringNoSign() ;
//cout<<"astr:"<<astr<<"q:"<<q<<endl;
		}

		if(firstTime || q!=0){
			if(C->Head !=0){
				int gap = C->Head->exp - zeros;
				while(gap>1)
					{
					C->Insert(0,zeros+gap-1);
					gap--;
					}
			}
			C->Insert(q,zeros);
			firstTime = true;
//cout<<"cstr:"<<C->toStringNoSign()<<"zeros:"<<zeros<<endl;
		}
//		zeros--;
		bstr = b->toStringNoSign();
	}
//cout<<endl<<C->toString()<<endl;
}else{//a->CompareWithoutSign(a,b) N<B
	C = new PolyLinkedList(0,0);
//cout<<endl<<A->toString()<<endl;
}

	if(sendReminder){ return A;}
	else {
	int NoofNodes = C->Head->exp;
	while(NoofNodes)
		C->Insert(0,--NoofNodes);
	return C;
	}
};

PolyLinkedList* PolyLinkedList::Srt(PolyLinkedList* a,bool& error){
	PolyLinkedList* A = new PolyLinkedList(1,0,positive);
	PolyLinkedList* B = a->Copy();
	PolyLinkedList* Two = new PolyLinkedList(2,0,positive),*C = new PolyLinkedList();
//	cout<<endl;
//	cout<<"A:";A->Print();cout<<endl;
//	cout<<"B:";B->Print();cout<<endl;
//	cout<<"two:";Two->Print();cout<<endl;
	if(a->sign == negative) {error =  true; return A;}
	else{
		do{
			C = A->Add(A,B);
//			cout<<"C:";C->Print();cout<<endl;
			A = A->Div(C,Two,false);
			B = A->Div(a,A,false);
//			cout<<"A:";A->Print();cout<<endl;
//			cout<<"B:";B->Print();cout<<endl;
		}while(A->CompareWithoutSign(A,B)&&A->toStringNoSign()!=B->toStringNoSign());
	return A;
	}
};
//int main(){
//	srand(time(NULL));
//	string n1,n2;
//	PolyLinkedList* A =0,*B=0,*C=0;
//	bool notNegative;
//	while(true){
//	cout<<"Enter two +ve no.\n";
//	cin>>n1;
//	cin>>n2;
//	A = new PolyLinkedList(n1);
//	B = new PolyLinkedList(n2);
//
//	cout<<"\nA:"<<A->toString(); //	A->Print();
//	cout<<"\nB:"<<A->toString(); 	//B->Print();
//
//	C = C->Add(A,B);
//	cout<<"\nA+B="<<C->toString(); 	 //C->Print();
//
//	C = C->Sub(A,B,notNegative );
//	if(notNegative){	cout<<"\nA-B="<<C->toString(); 	//	C->Print();
//	}else {cout<<"\nA-B=syntax error";}
//
//	C = C->Mul(A,B);
//	cout<<"\nA*B="<<C->toString();  	//C->Print();
//
//	C = C->Pow(A,B);
//	cout<<"\nA^B="<<C->toString(); 	// C->Print();
//	 cout<<"\n________________-_-__________________\n";
//}//Infinite loop
//}
