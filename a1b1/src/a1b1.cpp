//============================================================================
// Name        : a1b1.cpp
// Author      : Satyam
// Version     :
// Copyright   : a*b* 
// Description : Hello World in C++, Ansi-style
//============================================================================


#include <iomanip>
#include <sstream>
#include <iostream>
#include <unistd.h>
#include<math.h>

using namespace std;


double oneForm(int n)
{
	double d=1;
for(int i=1;i<n;i++)
	d=d*10+1;
cout<<d<<"for"<<"("<<n<<")";
return d;
}

double constructAB( int a, int b,int digits,int idigits )
{
//	int j;
	int i = digits-1;
//for(int i = digits-1;i>0;i--)
	for(int j = 1;j<=idigits;j++)
	{
	i= digits-j;
		if(a<b){
//			if(b!=0)
				cout<<(oneForm(i)*a)*pow(10,j) + oneForm(j)*b<<endl;
//			else cout<<(oneForm(i)*a)*pow(10,j) + oneForm(j)+b<<endl;
		}
		else {
//			if(b!=0)
				cout<<(oneForm(j)*a)*pow(10,i) + oneForm(i)*b<<endl;
//			else cout<<(oneForm(i)*a)*pow(10,j) + oneForm(j)+b<<endl;

		}
	}
return 0;
}



int main() {
	double d =  123456789012345678;
	int n;
	cout<<"Enter n"<<endl;
	cin>>n;
//	stringstream s ;

//	cout.setf(ios::fixed);
	for(int digits = 5; digits<=18;digits++)
	for(int a=1;a<=9;a++){
		for(int idigits=1;idigits<digits;idigits++){
		for(int b=0;b<=9;b++){
			if(a!=b) d=constructAB(a,b,digits,idigits);
			//if(d%n == 0) break;
			sleep(1);
		}
	}
	}
	cout.setf(ios::fixed);
//	s <<"="<<d;
	cout << "\nThis is a*b* "<<setprecision(0)<< d<< endl; // prints This is a*b*
	return 0;
}

