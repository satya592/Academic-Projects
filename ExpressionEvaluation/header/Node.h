#ifndef Node_H
#define Node_H
#include <stdio.h>
#include <stdlib.h>
#include <iostream>
#include <cstdlib> //srand
#include <ctime> //time
#include <string>
#include <cmath>
#include <stack>// std::stack
//#include <regex>
#include <vector>
#include <string.h>
#include <map>
#include <fstream>
#include <stdint.h>
#include <signal.h>

using namespace std;


class Node{
public:
	uint16_t  exp;
	long  no;
	Node *next;

	Node(){
		 exp = 0;
		 no = 0;
		 next = 0;
	 };
	 Node(short no,short exp){
		 this->exp = exp;
		 this->no = no;
		 this->next = 0;
	 };
	 ~Node(){
//		 cout<<"im cleaning nodes"<<endl;
		 Node *current = this;
		 Node *temp = current;
			while(temp != 0){
				temp=temp->next;
				delete current;
				current = temp;
			}
	 };
	 void Print(){
		 	string disply = "";
			Node* temp = this;
			bool firstNonZero = true;
			while(temp !=0){
				disply+=temp->no+48;
				temp=temp->next;
			}
			 for (string::reverse_iterator rit=disply.rbegin(); rit!=disply.rend(); ++rit){
				 if(firstNonZero && *rit == '0') continue;
				 else {
					 firstNonZero = false;
					 cout<<*rit;
				 }
			 }
	 };

};

#endif
