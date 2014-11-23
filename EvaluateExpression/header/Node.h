#ifndef Node_H
#define Node_H
#include <stdio.h>
#include <stdlib.h>
#include <iostream>
#include <cstdlib> //srand
#include <ctime> //time
#include <string>
using namespace std;


class Node{
public:
	int exp;
	int no;
	Node *next;

	Node(){
		 exp = 0;
		 no = 0;
		 next = 0;
	 }
	 Node(int no,int exp){
		 this->exp = exp;
		 this->no = no;
		 this->next = 0;
	 }

};

#endif
