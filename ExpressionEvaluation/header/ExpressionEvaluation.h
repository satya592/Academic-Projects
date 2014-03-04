#ifndef Expression_H
#define Expression_H

#include "LinkedList.h"

#define OPERATORS "(+-*^/%r )"
const char *K_OPERATORS = OPERATORS;

const map<char,int> operatorValue = {{'(',1}, {'+',2}, {'-',2}, {'*',3}, {'/',3},{'%',3}, {'^',4},{'r',5}, {')',-1}};
bool KNEGATIVE = false;


using namespace std;

class Expression{

public:
	string statement;
//	stack<PolyLinkedList *> numbers;
	stack<char> operators;
	stack<string> numbers;

	void removeSpaces(){
//		string dest = statement;
//		for (string::iterator itr = statement.begin();itr != statement.end(); ++itr)
//			if (!isspace(*itr))
//				{*(dest) = *itr;dest++;}
//		statement = dest;
//		std::string::iterator end_pos = std::remove(statement.begin(), statement.end(), ' ');
//		statement.erase(end_pos, statement.end());
//		statement.erase(std::replace(statement.begin(), statement.end(), ' '), statement.end());
		for(unsigned int i = 0; i <= statement.length(); i++){
			if(statement[i] == ' ')
				statement.erase(i, 1);
		}
	}

	Expression(string statement){this->statement = statement; };
	bool validate();
	long evaluate();
	bool validChar();
	bool tokenize();
	bool tokens(char const*&);
	bool yesOperator (const char *&);
	bool digits (const char *&s);
	bool doOperation(char);
	string chattoString(char ch){
		switch(ch){
		case '0': return "0";break;
		case '1': return "1";break;
		case '2': return "2";break;
		case '3': return "3";break;
		case '4': return "4";break;
		case '5': return "5";break;
		case '6': return "6";break;
		case '7': return "7";break;
		case '8': return "8";break;
		case '9': return "9";break;
		default: return "failed";
		}
	}
};

#endif
