//============================================================================
// Name        : Test1.cpp

// Author      : Satyam
// Version     :
// Copyright   : Your copyright notice
// Description : Hello World in C++, Ansi-style
//============================================================================

//#include <math.h>
//#include <stdio.h>
//#include <stdlib.h>
//#include <string.h>
#include <cmath>
#include <cstdio>
#include <cwchar>
#include <iostream>

int a[1000];
int b[2000];
int n,c1,c2,i,j,h,g,s;
int getkey(int n)
       {
       return n/50;
       }

 void insert(int n)
       {
          h=getkey(n);
           if(a[h] <=0)
               {
                 a[h]=n;
                 s=0;
                 for(j=1;j<1000;j++)
                      {
                        if (a[j] <=0 )
                           continue;
                          else if (h==j)
                           continue;
                          else
                          {
                          c1=a[j]+a[h];
                          c2=abs(a[j]-a[h]);
                          //printf("here %d",c2);
                          b[s]=c1;
                          s++;
                          b[s]=c2;
                          s++;
                          }
                      }

                 }
                else
                  {
                   int z=n;
                   s=0;
                   for(j=1;j<1000;j++)
                        {
                         if ( a[j]<=0 )
                            continue;
                         //else if (h==j)
                         //break;
                         else
                           {
                         c1=a[j]+z;
                         c2=abs(a[j]-z);
                         b[s]=c1;
                         s++;
                         b[s]=c2;
                         s++;
                            }
                        }
                   }
                  for(g=0;g<s;g++)
                       {
                        int l;
                        l=getkey(b[g]);
                        a[l]=b[g];
                        b[g]=0;
                        }
             }

int main()
{
  n=0;
  FILE* file = fopen ("input.txt", "r");
//  int d[n];
  int i = 0;
//  std::cout<< "hi";
  printf("enter the number of inputs \n");
  fscanf (file, "%d", &n);
  while(n>0)
   {
	  int d[n];
	  i = 0;
  while (i<n)
    {
      fscanf (file, "%d", &d[i]);
//            printf ("%d ", i);
            i++;
    }
  if(n !=0)
  {
// int d[n];
  for(i=0;i<1000;i++)
	a[i]=-1;
  for(i=0;i<n;i++)
	 {
	  insert(d[i]);
	 }

int count=0;
for(i=0;i<1000;i++)
   if(a[i]>0)
{ count++;
//printf("%d \t",a[i]);
}
printf("\nUnique Weights: %d \n",count);
}
else
printf("bye bye");

//  printf("enter the number of inputs \n");
  i = 0;
  fscanf (file, "%d", &n);

   }

}
