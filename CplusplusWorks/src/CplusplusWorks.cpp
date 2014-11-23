//============================================================================
// Name        : CplusplusWorks.cpp
// Author      : Satyam Kotikalapudi
// Version     :
// Copyright   : Satyam Kotikalapudi
// Description : Hello World in C++, Ansi-style
//============================================================================

#include <iostream>
using namespace std;
class Test{
	int t;
	public:
	Test(){
		t=10;
	}
	void print(){
		cout<<t<<endl;
	}
};


int _main() {
Test t;
int *ptr = (int*)&t;
t.print();
*ptr=100;
t.print();
	return 0;
}
