//============================================================================
// Name        : FrameSizeFinder.cpp
// Author      : Satyam
// Version     :
// Copyright   : Your copyright notice
// Description : Find frame size and schedule the taskset
//============================================================================

#include <iostream>
#include <fstream>
#include <string>
#include <algorithm>
#include <vector>
#include <ctype.h>
#include <iomanip>      // std::setfill, std::setw
#include <time.h>
//#include <maxValue.cpp>

using namespace std;

int getGCD(int a, int b)
{
	int temp;
	while( b!= 0) {
		temp = a % b;
		a = b;
		b = temp;
	}
        return a;
}

vector<int> getFactors(int num){
	vector<int> factors;
	for(int i = 1; i <= num; i++)
	{
	    if(num%i == 0)
	    {
	    	factors.push_back(i);
//	        cout << i << endl;
	    }
	}
return factors;
}

float getMaxE(vector<float> element){
	float big = element[0];
	for(unsigned i=0; i<element.size();i++)
	    {
		 if (element[i] > big)
		    big = element[i];
	    //cout<<big<<endl;
	    }
	return big;
}

vector<int> getMerge(vector<int> a,vector<int> b){
	vector<int> c;
	unsigned i=0,j=0;
	while(i<a.size()&&j<b.size()){
		if(a[i]<b[j]) c.push_back(a[i++]);
		else if(a[i]>b[j]) c.push_back(b[j++]);
		else {c.push_back(b[j++]);i++;}
	}
	if(i<a.size()){
	while(i<a.size()){
		c.push_back(a[i++]);
	  }
	}else while(j<b.size()){
		c.push_back(b[j++]);
	}
	return c;
}

long getLCM(int a, int b){
    return a*b/getGCD(a,b);
}

long getLCM(vector<int> element)
{
	long lLCM = element[0];
	for (unsigned k = 1; k < element.size(); k++){
		lLCM = getLCM(lLCM,element[k]);
//	cout<<lLCM<<endl;
	}
	return lLCM;
}
// split: receives a char delimiter; returns a vector of strings
// By default ignores repeated delimiters, unless argument rep == 1.
vector<string> split(char delim, int rep, string work) {
	vector<string> flds;
    if (!flds.empty()) flds.clear();  // empty vector if necessary
    //string work = "12,123,43,64,893";
    string buf = "";
    unsigned i = 0;
    while (i < work.length()) {
        if (work[i] != delim){
           if(work[i] != '(' && work[i] != ')' && work[i] != ',' && work[i] != ' ') buf += work[i];//cout<<work[i]<<endl;}
        }else if (rep == 1) {
            flds.push_back(buf);
            buf = "";
        } else if (buf.length() > 0) {
            flds.push_back(buf);
            buf = "";
        }
        i++;
    }
    if (!buf.empty())
        flds.push_back(buf);
    return flds;
}

void swap(unsigned &a, unsigned &b){
	unsigned temp;
	temp = a;
	a = b;
	b = temp;
}

void swap(float &a, float &b){
	float temp;
	temp = a;
	a = b;
	b = temp;
}

vector<int> getFrameSize(vector<int> frames, vector<int> period, vector<int> deadline , float minFrameSize)
{
	//2f-gcd(p,f) - D <= 0 is a valid framesize
	vector<int> fAccepted;
	bool skip = false;
	for(unsigned i= 0;i<frames.size();i++)
	{
		skip = false;
		if(frames[i] < minFrameSize) continue;
		for(unsigned j=0; j<period.size();j++){
//			cout<<"2*"<<frames[i]<<"-"<<getGCD(period[j],frames[i])<< "-"<< deadline[j]<<" :"<<(2*frames[i]-getGCD(period[j],frames[i]) - deadline[j])<<endl;
			if((2*frames[i]-getGCD(period[j],frames[i]) - deadline[j]) <= 0) continue;
			else skip=true; break;
		}
		if(!skip) fAccepted.push_back(frames[i]);
	}
return fAccepted;
}

void getTupple(vector<float> &phase, vector<int> &period,vector<float> &execution,vector<int> &deadline)
{
	string STRING;
	ifstream infile;
	vector <string> fields;
	infile.open("tuples.txt");
	int  i = 0;
	   while(!infile.eof()) // To get you all the lines.
	        {
		        getline(infile,STRING); // Saves the line in STRING.
		        //cout<<STRING<<endl; // Prints our STRING.
		        fields = split(',',1,STRING);
		        for (unsigned k = 0; k < fields.size(); k++)
		           {
		        	switch(k+1){
		        	case 1: phase.push_back(atof(fields[k].c_str()));//cout<<phase[i]<<endl;
		        	break;
		        	case 2: period.push_back(atoi(fields[k].c_str()));//cout<<period[i]<<endl;
		        	break;
		        	case 3: execution.push_back(atof(fields[k].c_str()));//cout<<execution[i]<<endl;
		        	break;
		        	case 4: deadline.push_back(atoi(fields[k].c_str()));//cout<<deadline[i]<<endl;
		        	break;
		        	}
		           }
	        i++;
	        }
	infile.close();
//	system("pause");
}

void schedule(unsigned iFrame, unsigned iMajorCycle, vector<float> &phase, vector<int> &period, vector<float> &execution, vector<int> &deadline){
	unsigned noFrames = iMajorCycle/iFrame , noOfTasks = phase.size();
	unsigned noOfJobsInMajCyc[noOfTasks], totalNoNodes = noFrames + 2 ;
	vector<float> remainingExe = execution;
	ofstream outfile;
	outfile.open("results.txt", ios::app);
	outfile<<"For Frame size: "<<iFrame<<endl;
	outfile<<setfill('-')<<setw(80)<<"-"<<endl;
	//	cout<<totalNoNodes<<endl;
	struct job {
	  unsigned jobNo;
	  unsigned jobIndex;
	  float startTime;
	  unsigned deadline;
	  unsigned frameNo;
	  string frames;
	  float start;
	  float end;
	} ;
	//Find total no of nodes
	for(unsigned i = 0;i<noOfTasks;i++){
		noOfJobsInMajCyc[i] = iMajorCycle/period[i];
		totalNoNodes += noOfJobsInMajCyc[i];
		//if(execution[i]<=iFrame && period[i]<=iFrame) jobsInFrame[j++] = i;
		}
	unsigned totalNoJobs = totalNoNodes - noFrames - 2;
	job jobsToSchedule[totalNoJobs];
	//Fill the jobs table
	for(unsigned i = 0,k = 0 ;i<noOfTasks;i++){
		for(unsigned j = iMajorCycle/period[i];j>0;j--,k++){
		jobsToSchedule[k].jobNo = i;
		jobsToSchedule[k].jobIndex = j;
		jobsToSchedule[k].startTime = phase[i] + (j-1)*period[i];
		jobsToSchedule[k].deadline = jobsToSchedule[k].startTime + deadline[i];
		//cout<<"jobNo: "<<i+1<<", startTime: "<<jobsToSchedule[k].startTime<<", jobno: "<<k<<endl;
	}
	}
	//Jobs Scheduling
	//Sort jobs based on earlist deadline
	for (unsigned k = 1; k < totalNoJobs; k++){
	  for (unsigned i = 0; i <totalNoJobs -1 - k; i++)
		   if (jobsToSchedule[i].deadline > jobsToSchedule[i+1].deadline ){
			   swap(jobsToSchedule[i].deadline,jobsToSchedule[i+1].deadline );
			   swap(jobsToSchedule[i].jobNo,jobsToSchedule[i+1].jobNo );
			   swap(jobsToSchedule[i].jobIndex,jobsToSchedule[i+1].jobIndex );
			   swap(jobsToSchedule[i].startTime,jobsToSchedule[i+1].startTime );
		  }
	}
	//Sort jobs based on start deadline
	for (unsigned k = 1; k < totalNoJobs; k++){
	  for (unsigned i = 0; i <totalNoJobs -1 - k; i++)
		   if (jobsToSchedule[i].startTime > jobsToSchedule[i+1].startTime ){
			   swap(jobsToSchedule[i].deadline,jobsToSchedule[i+1].deadline );
			   swap(jobsToSchedule[i].jobNo,jobsToSchedule[i+1].jobNo );
			   swap(jobsToSchedule[i].jobIndex,jobsToSchedule[i+1].jobIndex );
			   swap(jobsToSchedule[i].startTime,jobsToSchedule[i+1].startTime );
		  }
	}

	float remainingFrame;
	//Scheduling jobs in the frames
	//for (unsigned i = 0; i < noFrames; i++){
		int i = 0;
		remainingFrame = iFrame;
		char intStr[10];
		for (unsigned k = 0; k < totalNoJobs && remainingFrame != 0; k++){
			if(jobsToSchedule[k].startTime <= (i+1)*iFrame - remainingFrame){
				jobsToSchedule[k].start = (i)*iFrame + iFrame - remainingFrame;
			if(remainingFrame >= remainingExe[jobsToSchedule[k].jobNo]){
				remainingFrame -= remainingExe[jobsToSchedule[k].jobNo];
				jobsToSchedule[k].end = (i+1)*iFrame - remainingFrame;
				jobsToSchedule[k].frameNo = i+1;
				itoa(jobsToSchedule[k].frameNo,intStr,10);
				jobsToSchedule[k].frames = string(intStr);
			} else {
				remainingExe[jobsToSchedule[k].jobNo] = remainingExe[jobsToSchedule[k].jobNo] - remainingFrame;
				jobsToSchedule[k].frameNo = i+1;
				itoa(jobsToSchedule[k].frameNo,intStr,10);
				jobsToSchedule[k].frames = string(intStr);
				i++;
				remainingFrame = iFrame;
				remainingFrame -= remainingExe[jobsToSchedule[k].jobNo];
				jobsToSchedule[k].frameNo = i+1;
				itoa(jobsToSchedule[k].frameNo,intStr,10);
				jobsToSchedule[k].frames +=", " + string(intStr);
			}
			}else{ //Nothing left in this frame to schedule so jump to the next frame
				i++;
				remainingFrame = iFrame;
				jobsToSchedule[k].start = (i)*iFrame + iFrame - remainingFrame;
				if(remainingFrame >= remainingExe[jobsToSchedule[k].jobNo]){
					remainingFrame -= remainingExe[jobsToSchedule[k].jobNo];
					jobsToSchedule[k].end = (i+1)*iFrame - remainingFrame;
					jobsToSchedule[k].frameNo = i+1;
					itoa(jobsToSchedule[k].frameNo,intStr,10);
					jobsToSchedule[k].frames += string(intStr);
				}
			}
		}
	for (unsigned k = 0; k < totalNoJobs; k++){
		outfile<<setiosflags(ios::left)<<setfill(' ')<<"start: "<<setw(6)<<jobsToSchedule[k].startTime<<"| Execution:"<<setw(9)<<execution[jobsToSchedule[k].jobNo]
		<<"| jobno: ("<<setw(3)<<jobsToSchedule[k].jobNo+1<<","<<setw(4)<<jobsToSchedule[k].jobIndex<<")"<<"| Frame: "<<setw(6)<<jobsToSchedule[k].frames<<
		"| Deadline: "<<setw(9)<<jobsToSchedule[k].deadline<<"|(Start,End): ("<<jobsToSchedule[k].start<<"-"<<jobsToSchedule[k].end<<")"<<endl;
		//"("<<jobsToSchedule[k].start<<"-"<<jobsToSchedule[k].end<<")"<<endl;
	}

//	cout<<totalNoNodes<<endl;
//		int graph[V][V] = { {0, 16, 13, 0, 0, 0},
//	                        {0, 0, 10, 12, 0, 0},
//	                        {0, 4, 0, 0, 14, 0},
//	                        {0, 0, 9, 0, 0, 20},
//	                        {0, 0, 0, 7, 0, 4},
//	                        {0, 0, 0, 0, 0, 0}
//	                      };
// Filling up the Graph of nodes to find the max-flow
//	int graph[totalNoNodes][totalNoNodes];
//	for(unsigned i = 0; i < totalNoNodes; i++){
//		for(unsigned j = 0; j < totalNoNodes; j++){
//			if(i == j) graph[i][j] = 0; // no edge to same node
//			else if(i == 0 ){// when it is from source
//				if(j < noOfTasks) graph[i][j] = execution[j]; // Source to Jobs
//				else graph[i][j] = 0; // no edge from soure to frames and sink
//			}else if(j == 0 || i == totalNoNodes-1){
//				graph[i][j] = 0;
//			}else if(i < totalNoNodes-1 && j < totalNoNodes-1 && j >= totalNoNodes-noFrames-1){// For all tasks from
//				graph[i][j] = 0;
//			}else if(j == totalNoNodes-1 && i < totalNoNodes-noFrames-1){
//				graph[i][j] = 0;
//			}else {
//
//			}
//		}
//	}
//	for(int i = phase.size(); i < phase.size(); i++){
//		// graph[i][totalNoFrames-1] =  add all the inflows to the frames over here
//	}
//cout<<totalNoNodes<<endl;
	//cout << "The maximum possible flow is " << fordFulkerson(graph, 0, totalNoNodes-1);
outfile.close();
}

int main() {
	vector<int>  period, deadline, factors, accepted,noMinFrame;
	vector<float> phase, execution,utilization;
	float utilize,totalSysUtilization = 0,minFrame;
	ofstream outfile;
	time_t ltime; /* calendar time */
	ltime=time(NULL); /* get current cal time */
	outfile.open ("results.txt", ios::app);
	outfile<<endl<<setfill('-')<<setw(110)<<"-"<<endl;
	outfile<<"Author: Satyam Kotikalapudi Objective: Scheduling RTS tasks TimeStamp:"<<asctime( localtime(&ltime) );
	outfile<<setfill('-')<<setw(110)<<"-"<<endl;
	getTupple(phase, period, execution, deadline);
    for (unsigned k = 0; k < phase.size(); k++){
    	utilize = execution[k]/period[k];
    	totalSysUtilization += utilize ;
    	utilization.push_back(utilize);
    	outfile<<setiosflags(ios::left)<<setfill(' ')<<"ph("<<setw(2)<<k<<"):"<<setw(8)<<phase[k]<<"|"<<"p("<<setw(2)<<k<<"):"
    			<<setw(5)<<period[k]<<"|"<<"e("<<setw(2)<<k<<"):"<<setw(9)<<execution[k]<<"|"<<"D("<<setw(2)<<k<<"):"<<setw(5)<<deadline[k]<<"|"
    			<<"u("<<setw(2)<<k<<"):"<<utilization[k]<<endl;
    }
	outfile<<setfill('-')<<setw(80)<<"-"<<endl;
    outfile<<"Total system Utilization: "<<totalSysUtilization<<endl;
    minFrame = getMaxE(execution);
    outfile<<"Min Frame size: "<<minFrame<<endl;
    long lHyperPeriod = getLCM(period);
    outfile<<"HyperPeriod: "<<lHyperPeriod<<endl;
    for (unsigned k = 0; k < period.size(); k++)
    factors = getMerge(factors, getFactors(period[k]));
    outfile<<"Factors: {"<<factors[0];
    for (unsigned k = 1; k < factors.size(); k++)
       outfile<<", "<<factors[k];
    outfile<<"}"<<endl;
    accepted = getFrameSize(factors,period,deadline,minFrame);
    noMinFrame = getFrameSize(factors,period,deadline,0);
    outfile<<"Acceptable Frame sizes: {"<<accepted[0];
        for (unsigned k = 1; k < accepted.size(); k++)
           outfile<<", "<<accepted[k];
        outfile<<"}"<<endl;
	outfile<<"Acceptable Frame sizes(leaving miniframe constraint): {"<<noMinFrame[0];
			for (unsigned k = 1; k < noMinFrame.size(); k++)
			   outfile<<", "<<noMinFrame[k];
			outfile<<"}"<<endl;
	outfile<<setfill('-')<<setw(110)<<"-"<<endl;
	outfile.close();
	schedule(accepted[accepted.size()-1], lHyperPeriod, phase, period, execution, deadline);
	return 0;
}
