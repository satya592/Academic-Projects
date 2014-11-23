#include <iostream>
using namespace std;

int main() {
int a = 0x12345678;
char *p = (char *)&a;
for(int i=0;i<sizeof(int);i++)
	cout<<std::hex<<(int)p[i]<< " ";
return 0;
}
