//============================================================================
// Name        : ExpressionEvaluation.cpp
// Author      : Satyam
// Version     :
// Copyright   : Your copyright notice
// Description : Evaluate arthematic expression
//============================================================================

#include <iostream>
#include <ExpressionEvaluation.h>
#include <regex>
#include <vector>
#include <string.h>

using namespace std;
bool expr (const char *&, int& , int );

bool n_expr (const char *&s, int &result) {
    int n = 0;
    while (isdigit(*s)) n = 10 * n + (*s++ - '0');
    result = n;
    return true;
}

bool p_expr (const char *&s, int &result) {
    if (expr(++s, result, ')')) {
        ++s;
        return true;
    }
    return false;
}

bool o_expr (const char *&s, int &result, int eos) {
    int oresult = 0;
    const char *op = strchr("+-*/", *s);
    if (op == 0) return false;
    if (!expr(++s, oresult, eos)) return false;
    switch (*op) {
    case '+': result += oresult; break;
    case '-': result -= oresult; break;
    case '*': result *= oresult; break;
    case '/': result /= oresult; break;
    default: return false;
    }
    return true;
}

bool expr (const char *&s, int &result, int eos = 0) {
    while (isspace(*s)) ++s;
    if (*s == eos) return false;
    if (isdigit(*s)) {
        if (!n_expr(s, result)) return false;
    } else if (*s == '(') {
        if (!p_expr(s, result)) return false;
    } else return false;
    while (isspace(*s)) ++s;
    if (*s == eos) return true;
    return o_expr(s, result, eos);
}

bool Expression::validate(){
	int result;
	char const* test =  statement.c_str();
	bool res = expr(test,result,0);
	statement += ":" + result;
	cout<< result<<endl;
	return res;
}

//int main() {
//	cout << "Evaluate arthematic expression" << endl; // prints Evaluate arthematic expression
//	string statement;
//	vector<string> statements;
//	cout<<"Enter a valid arthematic expression: enter 0 to exit"<<endl;
//	while(true){
//		cin>>statement;
//		statements.push_back(statement);
//		if(statement.compare("0") == 0){//cout<<"tata";
//			break;}
//		}//while
//	for(unsigned i=0; i<statements.size() ;i++){
//		Expression Exp(statements[i]);
//		if(statements[i].compare("0") == 0) cout<<"Bye"<<endl;
//		else if(Exp.validate()){
//			cout<<i<<":"<<endl;
//		}else cout<<"syntax error"<<endl;
//	}
//	return 0;
//}
