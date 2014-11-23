/*
 * OperatorOverloading.cpp
 *
 *  Created on: Oct 10, 2014
 *      Author: satyamkotikalapudi
 */

#include "OperatorOverloading.h"
#include <iostream>
using namespace std;

OperatorOverloading::OperatorOverloading() {
	// TODO Auto-generated constructor stub
a=10;
}

OperatorOverloading::OperatorOverloading(int a) {
	// TODO Auto-generated constructor stub
this->a=a;
}

OperatorOverloading::~OperatorOverloading() {
	// TODO Auto-generated destructor stub
}

OperatorOverloading& OperatorOverloading::operator+(OperatorOverloading& a){
return *(new OperatorOverloading(this->a +a.a));
}

void OperatorOverloading::print(){
	cout<<this->a<<endl;
}
