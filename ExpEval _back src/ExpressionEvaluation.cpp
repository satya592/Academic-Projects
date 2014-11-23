//============================================================================
// Name        : ExpressionEvaluation.cpp
// Author      : Satyam
// Version     :
// Copyright   : Your copyright notice
// Description : Evaluate arthematic expression
//============================================================================

#include "ExpressionEvaluation.h"

using namespace std;

//Validate the input string
bool Expression::validChar(){
//	const char *op = strchr(operators, *s);
	for(unsigned short i=0;i<statement.length();i++){
		if(('0'<= statement[i] && statement[i] <= '9') || strchr(K_OPERATORS, statement[i]) != NULL){
//			cout<<"validChar:"<<strchr(operators, statement[i])<<endl;
			continue;
		}else{ // some invalid character i
//			cout<<"validchar1:false";
			return false;
		}
	}
	return true;
}

bool Expression::digits (const char *&expression) {
//	string zero("0"), sten("10");
//	cout<<"expression:"<<expression<<endl;
	string str = "";
//	PolyLinkedList* no= new PolyLinkedList("0");
//	PolyLinkedList* ten= new PolyLinkedList("10");// , *temp = 0,*temp1 = 0;
	while (isdigit(*expression)){
//		cout<<"isdigit:"<<*expression;
//		no = no->Add(no->Mul(ten,no), new PolyLinkedList(chattoString(*expression++)));//no = 10 * no + (*s++ - '0')
//		no->Insert(*expression++);
//		cout<<"NO:";no->Print();
		str += *expression++;
	}

	cout<<str<<endl;
	while (isspace(*expression)){ ++expression;}//cout<<"again space\n";}
	if (isdigit(*expression)) {//no->Print();
//	cout<<"Not-pushed->digits:false\n";
	return false; }//space is not allowed in between the number
//	no->Print();
//	PolyLinkedList* no= new PolyLinkedList(str);
	numbers.push(str);
//	cout<<"-pushed\n";
return true;
}

bool Expression::doOperation(char operate){
	PolyLinkedList *A=0,*B=0;
//	cout<<"do operations start:";
	if(operate == ')'){
		while(!operators.empty() && operators.top() != '('){
			if(!doOperation(operators.top())){
				//sufficient numbers does not exist in stack ||  invalid operator || negative integer came as a result
			operators.pop();
//			cout<<"pop-dooperation\n";
			}//else cout<<"dooperation failed\n";
		}
		if(!operators.empty() && operators.top() == '(') {
			operators.pop(); // pop '(' from stack
//			cout<<"print:()";numbers.top()->Print();
		}
		else {
//			cout<<"doOperation1:false";
			return false;} // operators stack is empty so return false
	}else if(numbers.size() > 1){
		 A = new PolyLinkedList(numbers.top());
		 numbers.pop();
		 B = new PolyLinkedList(numbers.top());
		 numbers.pop();
		 cout<<"Doing "<<endl;
		 B->Print();
		 cout<<operate<<endl;
		 A->Print();
			PolyLinkedList* C = 0;
			bool notNegative = true;
			switch(operate){
			case '+':
//				cout<<"Add operator\n";
				C = C->Add(B,A); // should always be B,A since
				numbers.push(C->toString());
//				C->Print(); cout<<"-pushed\n";
				operators.pop();
//				cout<<"+:pop\n";
				break;
			case '-':
//				cout<<"Sub operator\n";
				C = C->Sub(B,A,notNegative );
				KNEGATIVE = not(notNegative); //set flag
				if(notNegative){
					numbers.push(C->toString());
//					C->Print();cout<<"-pushed\n";
					}
				operators.pop();
//				cout<<"-:pop\n";
				return notNegative;
			case '*':
//				cout<<"Mul operator\n";
				C = C->Mul(A,B);
				numbers.push(C->toString());
//				C->Print();cout<<"-pushed\n";
				operators.pop();
//				cout<<"*:pop\n";
				break;
			case '^':
//				cout<<"power operator\n";
				C = C->Pow(B,A);
				numbers.push(C->toString());
//				C->Print();cout<<"-pushed\n";
				operators.pop();
//				cout<<"^:pop\n";
				break;
			case '(':
//				cout<<"parenthesis operator\n"; //recursive call until ( is found
//				cout<<"doOperation2:false";
				return false;
			default:
//				cout<<"invalid operator\n";
//				cout<<"doOperation3:false";
				return false;
			}
			cout<<"=";
			C->Print();
			cout<<endl;
	}else{
//		cout<<"doOperation4:false";
		return false;
	}
	return true;
}

bool Expression::yesOperator (const char *&expression) {
	char ch = *expression;
	bool popedOpenBrace =false;
	if(!operators.empty()){
//		cout<<"top:"<<operatorValue.find(operators.top())->second<<"|"<<"ch:"<<operatorValue.find(ch)->second;
		if((operatorValue.find(operators.top())->second <= operatorValue.find(ch)->second)
				|| *expression == '('){//if stack top has a small Precedence than < current operator, push on the stack
			operators.push(*expression);
//			cout<<*expression<<"-pushed\n";
		}else{// 1*2+3+4* 1*2*3+4 ()
			while(!operators.empty() && (operatorValue.find(operators.top())->second > operatorValue.find(ch)->second)){
				//while stack is not empty and top element precedence is > current operator, pop the operator
				// only )=-1 can have least precedence than (=1
				//rest all operators have more precendence than (=1

				if(operators.top() !='('){
//					cout<<operators.top()<<":yesOperator:"<<*expression;
					if( !doOperation(operators.top())){ // if ) comes then pop until (, if matching ( does not found return error
//						cout<<"yesOperator1:false";
						return false;
					}
				}else if(operators.top() =='(' ){
//					cout<<operators.top()<<"-poped\n";
					operators.pop();
					popedOpenBrace = true;
//					cout<<"pop-yesoperator\n";
					}
			}
			if(*expression != ')'){
				operators.push(*expression); //we have removed all high precedence operators now place this operator on stack
//				cout<<*expression<<"-pushed\n";
			}
			else {
//				cout<<"yesOperator2:false";
				if(!popedOpenBrace) return false; //If a ) is encounterd and corresponding ( is not seen
			}
		}
	}else if(*expression != ')'){//operator stack is empty - never ever push ) on an empty stack
		operators.push(*expression);
//		cout<<*expression<<"-pushed\n";
	}else return false; // ) cant be pushed on empty stack

	if(*expression != '\0')++expression;
	return true;
}

bool Expression::tokens(char const*& expression){
if(*expression != '\0'){
	while (isspace(*expression)){
//		cout<<"again space\n";
		++expression;}
	if(!isdigit(*expression)){
		if(!yesOperator(expression)){
//			cout<<"tokens1:false";
			return false; }// if this executes then expression is already incremented there
		while (isspace(*expression)){
//			cout<<"again space\n";
			++expression;}
	}
	if(isdigit(*expression)) { // if next operator is followed by no  ( (1*3)) //operator can followed by operator
			if (!digits(expression)) {
//				cout<<"tokens2:false";
				return false;}
		}
	return this->tokens(expression); //recursion
}else{//until expression is empty
//	cout<<"true:tokens";
	return true;
}
}

bool Expression::tokenize(){
	char const* expression =  statement.c_str();
//	cout<<"tokenizer\n";
//	while (isspace(*expression)){cout<<"again space\n"; ++expression;}
//	if (isdigit(*expression)) {
//		if (!digits(expression)) {cout<<"tokenize1:false"; return false;}
//	}else{
//
//	}
return this->tokens(expression);
}

bool Expression::validate(){
	while(!operators.empty()){
//		cout<<"size:"<<operators.size();
		if(!doOperation(operators.top()))
			return false;
		}
	if(numbers.size() > 1)
		return false;
	return true;
}

void signal_callback_handler(int signum)
	{
	   printf("Caught signal %d\n",signum);
	   // Cleanup and close up stuff here

	   // Terminate program
	   exit(signum);
	}

int main() {
	Node a;
//	cout<<"sizeof a node:"<<sizeof(a)<<endl;
//	cout<<"sizeof a node exp:"<<sizeof(a.exp)<<endl;
//	cout<<"sizeof a node no:"<<sizeof(a.no)<<endl;
//	cout<<"sizeof a node next:"<<sizeof(a.next)<<endl;
	cout << "Evaluate arithmetic expressions" << endl; // prints Evaluate arthematic expression
	string statement;
	vector<string> statements;
	cout<<"Enter list of valid arithmetic expressions: enter 0 to exit"<<endl;
	ofstream outfile;
	outfile.open("output.txt");

	ifstream infile;

	while(true){
		std::getline(cin, statement);
		statements.push_back(statement);
		if(statement.compare("0") == 0){//cout<<"tata";
			break;}
		}//while
	cout<<"____________________________________________________\n";
	for(unsigned i=0; i<statements.size() ;i++){
		Expression Exp(statements[i]);
		if(statements[i].compare("0") == 0) cout<<"Bye."<<endl;
		else if(Exp.validChar()){
//			outfile<<i<<":"<<endl;
			if(Exp.tokenize()){
				//outfile<<Exp.statement<<"Valid"<<endl;
//				cout<<"____________________________________________________\n";
					if(Exp.validate()){//if(true){//
					if(!Exp.numbers.empty()) {cout<<Exp.numbers.top();cout<<endl;}
					else cout<<"syntax error"<<endl;
//					cout<<"num:"<<Exp.numbers.size()<<endl;
//					cout<<"ope:"<<Exp.operators.size()<<endl;
			}else{
				if(KNEGATIVE){
					cout<<"Negative numbers are not supported.\n";
					KNEGATIVE = false; //reset
				}else{
					cout<<"syntax error"<<endl;
				}
			}
			}else cout<<"syntax error"<<endl;
		}else cout<<"syntax error"<<endl;
	}
	cout<<"____________________________________________________\n";

	infile.open("input.txt");
	   while(!infile.eof()) // To get you all the lines.
	        {
		   	   getline(infile,statement); // Saves the line in STRING.
		   	   Expression Exp(statement);
				if(statement.compare("0") == 0) outfile<<"Bye."<<endl;
				else if(Exp.validChar()){
						if(Exp.tokenize()){
							if(Exp.validate()){//if(true){//
								if(!Exp.numbers.empty()) {outfile<<Exp.numbers.top();outfile<<endl;}
								else outfile<<"syntax error"<<endl;
							}else{
								if(KNEGATIVE){
									outfile<<"Negative numbers are not supported."<<endl;
									KNEGATIVE = false; //reset
								}else{
									outfile<<"syntax error"<<endl;
								}
							}
						}else outfile<<"syntax error"<<endl;
				}else outfile<<"syntax error"<<endl;
	        }
	infile.close();


	return 0;
}
