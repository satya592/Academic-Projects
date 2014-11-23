#ifndef Expression_H
#define Expression_H

#include <string>

using namespace std;

class Expression{
string statement;
public:
	Expression(string statement){this->statement = statement;};
	bool validate();
	long evaluate();
};

#endif
