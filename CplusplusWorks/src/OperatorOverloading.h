/*
 * OperatorOverloading.h
 *
 *  Created on: Oct 10, 2014
 *      Author: satyamkotikalapudi
 */

#ifndef OPERATOROVERLOADING_H_
#define OPERATOROVERLOADING_H_

class OperatorOverloading {
	int a;
public:
	OperatorOverloading();
	OperatorOverloading(int);
	virtual ~OperatorOverloading();
	OperatorOverloading& operator+(OperatorOverloading&);
	void print();
};

int main(){
	OperatorOverloading a,b;
	a.print();
	b.print();
	OperatorOverloading c = a+b;
	c.print();
}

#endif /* OPERATOROVERLOADING_H_ */
