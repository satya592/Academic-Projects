//============================================================================
// Name        : Edf_Rm.cpp
// Author      : Satyam
// Version     :
// Copyright   : Your copyright notice
// Description : Scheduling
//============================================================================

#include <iostream>
#include <fstream>
#include <string>
#include <algorithm>
#include <vector>
#include <ctype.h>
#include <iomanip>      // std::setfill, std::setw
#include <time.h>
#include <unistd.h>
//#include <maxValue.cpp>
struct job {
	unsigned jobNo;
	float jobIndex;
	float availTime;
	float executionTime;
	unsigned absdeadline;
	float start;
	float end;
};

using namespace std;

int getGCD(int a, int b) {
	int temp;
	while (b != 0) {
		temp = a % b;
		a = b;
		b = temp;
	}
	return a;
}

vector<int> getFactors(int num) {
	vector<int> factors;
	for (int i = 1; i <= num; i++) {
		if (num % i == 0) {
			factors.push_back(i);
//	        cout << i << endl;
		}
	}
	return factors;
}

float getMaxE(vector<float> element) {
	float big = element[0];
	for (unsigned i = 0; i < element.size(); i++) {
		if (element[i] > big)
			big = element[i];
		//cout<<big<<endl;
	}
	return big;
}

vector<int> getMerge(vector<int> a, vector<int> b) {
	vector<int> c;
	unsigned i = 0, j = 0;
	while (i < a.size() && j < b.size()) {
		if (a[i] < b[j])
			c.push_back(a[i++]);
		else if (a[i] > b[j])
			c.push_back(b[j++]);
		else {
			c.push_back(b[j++]);
			i++;
		}
	}
	if (i < a.size()) {
		while (i < a.size()) {
			c.push_back(a[i++]);
		}
	} else
		while (j < b.size()) {
			c.push_back(b[j++]);
		}
	return c;
}

long getLCM(int a, int b) {
	return a * b / getGCD(a, b);
}

long getLCM(vector<int> element) {
	long lLCM = element[0];
	for (unsigned k = 1; k < element.size(); k++) {
		lLCM = getLCM(lLCM, element[k]);
//	cout<<lLCM<<endl;
	}
	return lLCM;
}
// split: receives a char delimiter; returns a vector of strings
// By default ignores repeated delimiters, unless argument rep == 1.
vector<string> split(char delim, int rep, string work) {
	vector<string> flds;
	if (!flds.empty())
		flds.clear();  // empty vector if necessary
	//string work = "12,123,43,64,893";
	string buf = "";
	unsigned i = 0;
	while (i < work.length()) {
		if (work[i] != delim) {
			if (work[i] != '(' && work[i] != ')' && work[i] != ','
					&& work[i] != ' ')
				buf += work[i];    //cout<<work[i]<<endl;}
		} else if (rep == 1) {
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

void swap(unsigned &a, unsigned &b) {
	unsigned temp;
	temp = a;
	a = b;
	b = temp;
}

void swap(float &a, float &b) {
	float temp;
	temp = a;
	a = b;
	b = temp;
}

void swap(job &a, job &b) {
	swap(a.absdeadline, b.absdeadline);
	swap(a.jobNo, b.jobNo);
	swap(a.jobIndex, b.jobIndex);
	swap(a.availTime, b.availTime);
	swap(a.executionTime, b.executionTime);
	swap(a.start, b.start);
	swap(a.end, b.end);
}

vector<int> getFrameSize(vector<int> frames, vector<int> period,
		vector<int> deadline, float minFrameSize) {
	//2f-gcd(p,f) - D <= 0 is a valid framesize
	vector<int> fAccepted;
	bool skip = false;
	for (unsigned i = 0; i < frames.size(); i++) {
		skip = false;
		if (frames[i] < minFrameSize)
			continue;
		for (unsigned j = 0; j < period.size(); j++) {
//			cout<<"2*"<<frames[i]<<"-"<<getGCD(period[j],frames[i])<< "-"<< deadline[j]<<" :"<<(2*frames[i]-getGCD(period[j],frames[i]) - deadline[j])<<endl;
			if ((2 * frames[i] - getGCD(period[j], frames[i]) - deadline[j])
					<= 0)
				continue;
			else
				skip = true;
			break;
		}
		if (!skip)
			fAccepted.push_back(frames[i]);
	}
	return fAccepted;
}

void getTupple(vector<float> &phase, vector<int> &period,
		vector<float> &execution, vector<int> &deadline) {
	string STRING;
	ifstream infile;
	vector<string> fields;
	infile.open("tuples.txt");
	int i = 0;
	while (!infile.eof()) // To get you all the lines.
	{
		getline(infile, STRING); // Saves the line in STRING.
		//cout<<STRING<<endl; // Prints our STRING.
		fields = split(',', 1, STRING);
		for (unsigned k = 0; k < fields.size(); k++) {
			switch (k + 1) {
			case 1:
				phase.push_back(atof(fields[k].c_str()));//cout<<phase[i]<<endl;
				break;
			case 2:
				period.push_back(atoi(fields[k].c_str()));//cout<<period[i]<<endl;
				break;
			case 3:
				execution.push_back(atof(fields[k].c_str()));//cout<<execution[i]<<endl;
				break;
			case 4:
				deadline.push_back(atoi(fields[k].c_str()));//cout<<deadline[i]<<endl;
				break;
			}
		}
		i++;
	}
	infile.close();
//	system("pause");
}
void sortByArrivalTime(vector<job> &jobsToSchedule) {
//sort based on deadline
	for (unsigned k = 0; k < jobsToSchedule.size(); k++) {
		for (unsigned i = 0; i + 1 < jobsToSchedule.size(); i++) {
			if (jobsToSchedule[i].availTime > jobsToSchedule[i + 1].availTime) {
				swap(jobsToSchedule[i], jobsToSchedule[i + 1]);
			}
		}
	}
//	cout<<"sortbyARRIVALTIME\n";
//	for(unsigned i =0;i<jobsToSchedule.size();i++ )
//	cout<<jobsToSchedule[i].absdeadline <<"::"<<jobsToSchedule[i].availTime<<endl;
}

void sortByDeadline(vector<job> &jobsToSchedule) {
//sort based on deadline
	for (unsigned k = 0; k < jobsToSchedule.size(); k++) {
		for (unsigned i = 0; i + 1 < jobsToSchedule.size(); i++) {
			if (jobsToSchedule[i].absdeadline
					> jobsToSchedule[i + 1].absdeadline) {
				swap(jobsToSchedule[i], jobsToSchedule[i + 1]);
			}
		}
	}
//cout<<"sortbyDEADLINE\n";
//for(unsigned i =0;i<jobsToSchedule.size();i++ )
//cout<<jobsToSchedule[i].absdeadline <<"::"<<jobsToSchedule[i].availTime<<endl;
}

void edfSchedule(vector<job> &jobsToSchedule) {
	//Jobs Scheduling
	//Sort jobs based on earlist deadline
	sortByArrivalTime(jobsToSchedule);
	sortByDeadline(jobsToSchedule);
}

void rmSchedule(vector<job> &jobsToSchedule, vector<int> & period) {
	//Jobs Scheduling
	//Sort jobs based on rate, inverse to period
	for (unsigned k = 1; k < jobsToSchedule.size(); k++) {
		for (unsigned i = 0; i + 1 < jobsToSchedule.size(); i++)
			if (jobsToSchedule[i].availTime > jobsToSchedule[i + 1].availTime) {
				swap(jobsToSchedule[i], jobsToSchedule[i + 1]);
			} else if (jobsToSchedule[i].availTime
					== jobsToSchedule[i + 1].availTime) {
				if (period[jobsToSchedule[i].jobNo]
						> period[jobsToSchedule[i + 1].jobNo]) {
					swap(jobsToSchedule[i], jobsToSchedule[i + 1]);
				}
			}
	}
}

void sort(vector<job> &jobsToSchedule) {
//sort based on deadline
	for (unsigned k = 0; k < jobsToSchedule.size(); k++) {
		for (unsigned i = 0; i + 1 < jobsToSchedule.size(); i++) {
			if (jobsToSchedule[i].absdeadline
					> jobsToSchedule[i + 1].absdeadline) {
				swap(jobsToSchedule[i], jobsToSchedule[i + 1]);
			}
		}
	}

//sort based on start times
	for (unsigned k = 0; k < jobsToSchedule.size(); k++) {
		for (unsigned i = 0; i + 1 < jobsToSchedule.size(); i++) {
//	  	  cout<<jobsToSchedule[i].start<<"<>"<<jobsToSchedule[i+1].start <<endl;
			if ((jobsToSchedule[i].start > jobsToSchedule[i + 1].start)
					|| (jobsToSchedule[i].start < 0)) {
//			  	  cout<<jobsToSchedule[i].start<<"<>"<<jobsToSchedule[i+1].start <<endl;
				if (jobsToSchedule[i + 1].start >= 0) {
					swap(jobsToSchedule[i], jobsToSchedule[i + 1]);
//				  	  cout<<jobsToSchedule[i].start<<"<>"<<jobsToSchedule[i+1].start <<endl;
				}
			}
		}
	}
}

unsigned jobsInit(unsigned &iFrame, vector<float> &phase,
		vector<job> &jobsToSchedule, vector<int> &period,
		vector<float> &execution) {
	job currentJob;
	float jobindex;
	unsigned totalNoJobs = 0;

	for (unsigned k = 0; k < period.size(); k++) {
		currentJob.jobNo = k;
		currentJob.executionTime = execution[k];
		jobindex = 1;
		for (unsigned i = phase[k]; i < iFrame; i += period[k]) {
			totalNoJobs++;
			currentJob.jobIndex = jobindex++;
			currentJob.availTime = i;
			currentJob.absdeadline = i + period[k];
			currentJob.start = -1;
			currentJob.end = -1;
			jobsToSchedule.push_back(currentJob);
		}
	}
	return totalNoJobs;
}

//bool schedule(unsigned iFrame, unsigned iMajorCycle, vector<float> &phase, vector<int> &period, vector<float> &execution, vector<int> &deadline){
////	unsigned noFrames = iMajorCycle/iFrame , noOfTasks = phase.size();
////	unsigned noOfJobsInMajCyc[noOfTasks], totalNoNodes = noFrames + 2 ;
//	vector<float> remainingExe = execution;
//	ofstream outfile;
//	outfile.open("results.txt", ios::app);
////	outfile<<"For Frame size: "<<iFrame<<endl;
//
//	//	cout<<totalNoNodes<<endl;
//
//	unsigned totalNoJobs = 0;
//	vector<job> jobsToSchedule;
//	job currentJob;
//	int ask;
//
//	//Scheduling jobs initialization
//	unsigned exhaustlist = totalNoJobs = jobsInit(iFrame, phase,jobsToSchedule , period, execution);
//
//	outfile<<"Trying to schedule "<<totalNoJobs<<" tasks in (0-"<<iFrame<<") interval"<<endl;
//	cout<<"Trying to schedule "<<totalNoJobs<<" tasks in (0-"<<iFrame<<") interval"<<endl;
//	cout<<"Enter 1 - RM Schedule"<<endl;
//	cout<<"Enter 2 - EDF Schedule"<<endl;
//	cin>>ask;
//
//	if(1==ask){//Scheduling jobs using RM
//		rmSchedule(jobsToSchedule,period);
//		outfile<<"RM schedule "<<endl;
//	}else{//Scheduling jobs using EDF
//		edfSchedule(jobsToSchedule);
//		outfile<<"EDF schedule "<<endl;
//	}
//
//
//	vector<job> dealyedjobsToSchedule;
//		float currentTime = 0;
//		for (unsigned k = 0; iMajorCycle > currentTime && exhaustlist != 0 ; ){cout<<k<<currentTime<<endl;
//			if(dealyedjobsToSchedule.size() >0){cout<<"hello delayed list\n";
//				if(1==ask)rmSchedule(jobsToSchedule,period);
//				else edfSchedule(jobsToSchedule);
////				cout<<"delayedjobs:"<<dealyedjobsToSchedule.size()<<endl;
//				for (unsigned i = 0; i < dealyedjobsToSchedule.size() && iMajorCycle > currentTime; i++){
////					cout<<dealyedjobsToSchedule[i].availTime<<"(</>)"<< currentTime<<endl;
//				if(dealyedjobsToSchedule[i].availTime <= currentTime){
////					if(currentTime + dealyedjobsToSchedule[i].executionTime <= iMajorCycle ){
//					dealyedjobsToSchedule[i].start = currentTime;
//					dealyedjobsToSchedule[i].end = dealyedjobsToSchedule[i].start + dealyedjobsToSchedule[i].executionTime;
//					currentTime += dealyedjobsToSchedule[i].executionTime;
//					for (unsigned j = 0; j < jobsToSchedule.size() ; j++){
//						if(jobsToSchedule[j].jobNo == dealyedjobsToSchedule[i].jobNo  &&
//							jobsToSchedule[j].jobIndex == dealyedjobsToSchedule[i].jobIndex  ){
//							jobsToSchedule[j].start = dealyedjobsToSchedule[i].start;
//							jobsToSchedule[j].end = dealyedjobsToSchedule[i].end;
////							cout<<jobsToSchedule[j].start<<" entered: "<<jobsToSchedule[j].end<<endl;
//							exhaustlist--;
//							break;
//						}
//					}
////					cout<<"scheduled delayed job"<<dealyedjobsToSchedule[i].jobNo + 1<<endl;
//					dealyedjobsToSchedule.erase(dealyedjobsToSchedule.begin() + i);
//					i = -1; // rescan the delayed jobs
//					}else{
//						cout<<"k"<<k<<"size"<<dealyedjobsToSchedule.size()<<"i"<<i<<endl;
//						if(k == totalNoJobs && currentTime < dealyedjobsToSchedule[0].availTime){
//
//							if(1==ask){
//										rmSchedule(dealyedjobsToSchedule,period);}
//							else edfSchedule(dealyedjobsToSchedule);
//
////							sort(dealyedjobsToSchedule);
////							sortByDeadline(dealyedjobsToSchedule);
//							currentTime = dealyedjobsToSchedule[0].availTime;
//							i=-1;//reset the pointer
//		//					cout<<"Ideal time "<<dealyedjobsToSchedule[i].availTime<<"(</>)"<< currentTime<<endl;
//						}else ;//cout<<"something wrong here"<<k<<"<>"<<totalNoJobs;
////						cout<<"delayedjobs: not ready\n";
//					}
////				}//after scheduling the jobs must exit
//			}// for to iterate thru the jobs
//		}//if delayed jobs exist
////			cout<<"hi jobs list"<<"k"<<k<<"<>"<<totalNoJobs<<endl;
//			if(k < totalNoJobs){//cout<<"hi jobs list\n";
//			if(jobsToSchedule[k].availTime <= currentTime && currentTime != iMajorCycle){
//				jobsToSchedule[k].start = currentTime;
//				jobsToSchedule[k].end = jobsToSchedule[k].start + jobsToSchedule[k].executionTime;
//				currentTime += jobsToSchedule[k].executionTime;
//				exhaustlist--;
//				k++;
//			}else{
//				currentJob.absdeadline = jobsToSchedule[k].absdeadline;
//				currentJob.availTime = jobsToSchedule[k].availTime;
//				currentJob.end = jobsToSchedule[k].end;
//				currentJob.executionTime = jobsToSchedule[k].executionTime;
//				currentJob.jobIndex = jobsToSchedule[k].jobIndex;
//				currentJob.jobNo = jobsToSchedule[k].jobNo;
//				currentJob.start = jobsToSchedule[k].start;
////				cout<<"pushing into delay"<<endl;
//				dealyedjobsToSchedule.push_back(currentJob);
////				cout<<"delaying job"<<jobsToSchedule[k].jobNo + 1<<endl;
//				k++;
//			}
//		}else;//{cout<<"list exhaust"<<endl;};
//		}
//		//sort Scheduled jobs before displaying
//		sort(jobsToSchedule);
//		outfile<<setfill('-')<<setw(80)<<"-"<<endl;
//		//print Scheduled jobs
//		string status = "";
//		bool isSchedulable = true;
//	for (unsigned k = 0; k < jobsToSchedule.size(); k++){
//		if(jobsToSchedule[k].absdeadline < jobsToSchedule[k].end || jobsToSchedule[k].end == -1) {status = "miss";isSchedulable = false;}
//		else status = "hit";
//		outfile<<setiosflags(ios::left)<<setfill(' ')<<"start: "<<setw(6)<<jobsToSchedule[k].availTime<<
//				"| Execution:"<<setw(9)<<execution[jobsToSchedule[k].jobNo]
//		<<"| jobno: ("<<setw(3)<<jobsToSchedule[k].jobNo+1<<","<<setw(4)<<jobsToSchedule[k].jobIndex<<")"<<
//		"| Deadline: "<<setw(9)<<jobsToSchedule[k].absdeadline<<"|(Start,End): ("
//		<<setw(6)<<jobsToSchedule[k].start<<","<<setw(6)<<jobsToSchedule[k].end<<")"<<setw(4)<<"|"<<status<<endl;
//	}
//
//outfile.close();
//return isSchedulable;
//}

float max(float &a, float &b) {
	if (a > b)
		return a;
	else
		return b;
}

void printjob(job &a) {
	cout << "##########################################################"
			<< endl;
	cout << "job:" << a.jobNo + 1 << endl;
	cout << "index:" << a.jobIndex << endl;
	cout << "avail time:" << a.availTime << endl;
	cout << "start time:" << a.start << endl;
	cout << "end time:" << a.end << endl;
	cout << "exe time:" << a.executionTime << endl;
	cout << "dead line:" << a.absdeadline << endl;
}

bool ispriority(int ask, job a, job b) {
	bool ispriority = true;
	;
	if (1 == ask) {	//Scheduling jobs using RM
		if (a.jobNo > b.jobNo) {	// b is more priority
			ispriority = true; // if this is true then b is more priority so schedule b
		} else if (a.jobNo == b.jobNo) { // jobs of same tasks
			if (a.jobIndex > b.jobIndex) //consider their periods or indexes
				ispriority = true;
			else if (a.jobIndex < b.jobIndex)
				ispriority = false;
			else {
				cout << "ERROR HERE MEANS DEATH\n";
				printjob(a);
				printjob(b);
				exit(0);
			}
		} else
			ispriority = false;
	} else { //Scheduling jobs using EDF
		if (a.absdeadline > b.absdeadline) { // if this is true then b is more priority so schedule b
			ispriority = true; // if this is true then b is more priority so schedule b
		} else if (a.jobNo == b.jobNo) { // jobs of same tasks
			if (a.jobIndex > b.jobIndex) //consider their periods or indexes
				ispriority = true;
			else
				ispriority = false;
		} else
			ispriority = false;
	}

	return ispriority;
}

bool pschedule(unsigned iFrame, unsigned iMajorCycle, vector<float> &phase,
		vector<int> &period, vector<float> &execution, vector<int> &deadline) {
	vector<float> remainingExe = execution;
	ofstream outfile;
	outfile.open("results.txt", ios::app);
	unsigned totalNoJobs = 0, counter = 0;
	vector<job> jobsToSchedule;
	job currentJob;
	int ask;

	//Scheduling jobs initialization
	unsigned exhaustlist = totalNoJobs = jobsInit(iFrame, phase, jobsToSchedule,
			period, execution);

	outfile << "Trying to schedule " << totalNoJobs << " tasks in (0-" << iFrame
			<< ") interval" << endl;
	cout << "Trying to schedule " << totalNoJobs << " tasks in (0-" << iFrame
			<< ") interval" << endl;
	cout << "Enter 1 - RM Schedule" << endl;
	cout << "Enter 2 - EDF Schedule" << endl;
	cin >> ask;

	if (1 == ask) { //Scheduling jobs using RM
		rmSchedule(jobsToSchedule, period);
		outfile << "p RM schedule " << endl;
	} else { //Scheduling jobs using EDF
		edfSchedule(jobsToSchedule);
		outfile << "p EDF schedule " << endl;
	}
	outfile << setfill('-') << setw(80) << "-" << endl;
	string status = "";
	bool isSchedulable = true;
//	for (unsigned k = 0; k < jobsToSchedule.size(); k++){
//			if(jobsToSchedule[k].absdeadline < jobsToSchedule[k].end || jobsToSchedule[k].end == -1) {status = "miss";isSchedulable = false;}
//			else status = "hit";
//			outfile<<setiosflags(ios::left)<<setfill(' ')<<"start: "<<setw(6)<<jobsToSchedule[k].availTime<<
//					"| Execution:"<<setw(9)<<jobsToSchedule[k].end - jobsToSchedule[k].start//jobsToSchedule[k].executionTime
//			<<"| jobno: ("<<setw(3)<<jobsToSchedule[k].jobNo+1<<","<<setw(4)<<jobsToSchedule[k].jobIndex<<")"<<
//			"| Deadline: "<<setw(9)<<jobsToSchedule[k].absdeadline<<"|(Start,End): ("
//			<<setw(6)<<jobsToSchedule[k].start<<","<<setw(6)<<jobsToSchedule[k].end<<")"<<setw(4)<<"|"<<status<<endl;
//		}

	vector<job> ScheduledList;
	vector<job> dealyedjobsToSchedule;
	float currentTime = 0;
	for (unsigned k = 0;
			iMajorCycle > currentTime
					&& (!dealyedjobsToSchedule.empty()
							|| !jobsToSchedule.empty());) { //cout<<"k"<<k<<"ctime"<<currentTime<<"exha"<<exhaustlist<<endl;
		cout << "count" << counter << "Totaljobs" << totalNoJobs << endl;
//		cout<<"k exists"<<k<<"jobSize"<<jobsToSchedule.size()<<endl;

		if (dealyedjobsToSchedule.size() > 0) {
			cout << "hello delayed list" << dealyedjobsToSchedule.size();

			if (1 == ask)
				rmSchedule(dealyedjobsToSchedule, period);
			else
				edfSchedule(dealyedjobsToSchedule);
//				cout<<"delayedjobs:"<<dealyedjobsToSchedule.size()<<endl;
			for (unsigned i = 0;
					i < dealyedjobsToSchedule.size()
							&& iMajorCycle > currentTime; i++) { //cout<<"i"<<i<<endl;
				cout << "i" << i << dealyedjobsToSchedule[i].availTime
						<< "(</>)" << currentTime << endl;
				if (dealyedjobsToSchedule[0].availTime <= currentTime) {
//					if(currentTime + dealyedjobsToSchedule[i].executionTime <= iMajorCycle ){
					cout << "flag 1:" << dealyedjobsToSchedule.size() << endl;
					if (dealyedjobsToSchedule.size() > 1) {
						if (jobsToSchedule.empty()) { //compare jobs queue, delayed queue 1 and 0
							if (currentTime
									+ dealyedjobsToSchedule[0].executionTime
									<= dealyedjobsToSchedule[1].availTime
									|| !ispriority(ask,
											dealyedjobsToSchedule[0],
											dealyedjobsToSchedule[1])) { // remove the task it is done
								dealyedjobsToSchedule[0].start = currentTime;
								currentTime +=
										dealyedjobsToSchedule[0].executionTime;
								dealyedjobsToSchedule[0].end =
										dealyedjobsToSchedule[0].start
												+ dealyedjobsToSchedule[0].executionTime;
								exhaustlist--;
								//cout<<"insert:s:e:x"<<dealyedjobsToSchedule[i].start<<":"<<dealyedjobsToSchedule[i].end<<":"<<
								//		dealyedjobsToSchedule[i].executionTime<<"/"<<dealyedjobsToSchedule[i+1].availTime;
//						jobsToSchedule.push_back(dealyedjobsToSchedule[i]);
								cout << "print1\n";
								printjob(dealyedjobsToSchedule[0]);
//						jobsToSchedule.insert(jobsToSchedule.begin(),dealyedjobsToSchedule[i]);
								ScheduledList.insert(ScheduledList.begin(),
										dealyedjobsToSchedule[0]);
								k++;
								dealyedjobsToSchedule.erase(
										dealyedjobsToSchedule.begin());
								if (1 == ask) {
									rmSchedule(dealyedjobsToSchedule, period);
								} else
									edfSchedule(dealyedjobsToSchedule);

							} else {//if(dealyedjobsToSchedule[i].end  != dealyedjobsToSchedule[i].start){
								cout << "currentTime:" << currentTime
										<< "!= dealyedjobsToSchedule[1].availTime"
										<< dealyedjobsToSchedule[1].availTime
										<< endl;
								if (currentTime
										!= dealyedjobsToSchedule[1].availTime) {// && !ispriority(ask,dealyedjobsToSchedule[0],dealyedjobsToSchedule[1])) { //skip the iteration

									dealyedjobsToSchedule[0].start =
											currentTime;
									currentTime =
											dealyedjobsToSchedule[1].availTime;
									dealyedjobsToSchedule[0].end =
											dealyedjobsToSchedule[1].availTime;
									if (dealyedjobsToSchedule[0].jobIndex
											- (int) dealyedjobsToSchedule[0].jobIndex
											== 0)
										dealyedjobsToSchedule[0].jobIndex +=
												0.1;
									dealyedjobsToSchedule[0].executionTime -=
											dealyedjobsToSchedule[0].end
													- dealyedjobsToSchedule[0].start;
									// push the delayed task into both the vectors
//						jobsToSchedule.push_back(dealyedjobsToSchedule[i]);
									cout << "print2\n";
									printjob(dealyedjobsToSchedule[0]);
//						jobsToSchedule.insert(jobsToSchedule.begin(),dealyedjobsToSchedule[i]);
									ScheduledList.insert(ScheduledList.begin(),
											dealyedjobsToSchedule[0]);
									k++;
									// now change the delayed job
									dealyedjobsToSchedule[0].availTime =
											dealyedjobsToSchedule[0].end;// avail start is end
									dealyedjobsToSchedule[0].start = -1;
									dealyedjobsToSchedule[0].end = -1;
									dealyedjobsToSchedule[0].jobIndex += 0.1;
									if (1 == ask) {
										rmSchedule(dealyedjobsToSchedule,
												period);
									} else
										edfSchedule(dealyedjobsToSchedule);
								} else {
									cout << "swappingprint2";
									printjob(dealyedjobsToSchedule[0]);
									printjob(dealyedjobsToSchedule[1]);
									swap(dealyedjobsToSchedule[0],
											dealyedjobsToSchedule[1]);
									i = -1;
									continue;
									cout << "after swapping";
									printjob(dealyedjobsToSchedule[0]);
									printjob(dealyedjobsToSchedule[1]);
								}
							}
						} else {	//compare jobs queue, delayed queue 1 and 0
									//compare jobs and delayed queue of 1
							cout << "compare job and delay queue\n";
							printjob(dealyedjobsToSchedule[1]);
							printjob(jobsToSchedule[0]);

							if (dealyedjobsToSchedule[1].availTime
									> jobsToSchedule[0].availTime
									|| (dealyedjobsToSchedule[1].availTime
											== jobsToSchedule[0].availTime
											&& ispriority(ask,
													dealyedjobsToSchedule[1],
													jobsToSchedule[0]))) {

								if (1 == ask) {
									rmSchedule(jobsToSchedule, period);
								} else
									edfSchedule(jobsToSchedule);

								if (currentTime
										+ dealyedjobsToSchedule[0].executionTime
										<= jobsToSchedule[0].availTime
										|| !ispriority(ask,
												dealyedjobsToSchedule[0],
												jobsToSchedule[0])) {// remove the task it is done
									dealyedjobsToSchedule[0].start =
											currentTime;
									currentTime +=
											dealyedjobsToSchedule[0].executionTime;
									dealyedjobsToSchedule[0].end =
											dealyedjobsToSchedule[0].start
													+ dealyedjobsToSchedule[0].executionTime;
									exhaustlist--;
									cout << "print11.1\n";
									printjob(dealyedjobsToSchedule[0]);
									ScheduledList.insert(ScheduledList.begin(),
											dealyedjobsToSchedule[0]);
									k++;
									dealyedjobsToSchedule.erase(
											dealyedjobsToSchedule.begin());

									if (1 == ask) {
										rmSchedule(dealyedjobsToSchedule,
												period);
									} else
										edfSchedule(dealyedjobsToSchedule);

								} else {				//jobs is more priority
									cout << "currentTime:" << currentTime
											<< "!= jobsToSchedule[0].availTime"
											<< jobsToSchedule[0].availTime
											<< endl;
									cout << "max(" << currentTime << ","
											<< dealyedjobsToSchedule[0].availTime
											<< ")"
											<< max(
													dealyedjobsToSchedule[0].availTime,
													currentTime) << endl;
									if (max(dealyedjobsToSchedule[0].availTime,
											currentTime)
											< jobsToSchedule[0].availTime) {// && !ispriority(ask,dealyedjobsToSchedule[0],dealyedjobsToSchedule[1])) { //skip the iteration

										dealyedjobsToSchedule[0].start =
												currentTime;
										currentTime =
												jobsToSchedule[0].availTime;
										dealyedjobsToSchedule[0].end =
												jobsToSchedule[0].availTime;
										if (dealyedjobsToSchedule[0].jobIndex
												- (int) dealyedjobsToSchedule[0].jobIndex
												== 0)
											dealyedjobsToSchedule[0].jobIndex +=
													0.1;
										dealyedjobsToSchedule[0].executionTime -=
												dealyedjobsToSchedule[0].end
														- dealyedjobsToSchedule[0].start;
										cout << "print12.1\n";
										printjob(jobsToSchedule[0]);
										cout << "print12.2\n";
										printjob(dealyedjobsToSchedule[0]);
										ScheduledList.insert(
												ScheduledList.begin(),
												dealyedjobsToSchedule[0]);
										k++;
										// now change the delayed job
										dealyedjobsToSchedule[0].availTime =
												dealyedjobsToSchedule[0].end;// avail start is end
										dealyedjobsToSchedule[0].start = -1;
										dealyedjobsToSchedule[0].end = -1;
										dealyedjobsToSchedule[0].jobIndex +=
												0.1;
										if (1 == ask) {
											rmSchedule(dealyedjobsToSchedule,
													period);
										} else
											edfSchedule(dealyedjobsToSchedule);
									} else
										break;
								}
							} else {// delayed job has more priority then jobs queue
								if (currentTime
										+ dealyedjobsToSchedule[0].executionTime
										<= dealyedjobsToSchedule[1].availTime
										|| !ispriority(ask,
												dealyedjobsToSchedule[0],
												dealyedjobsToSchedule[1])) {// remove the task it is done
									dealyedjobsToSchedule[0].start =
											currentTime;
									currentTime +=
											dealyedjobsToSchedule[0].executionTime;
									dealyedjobsToSchedule[0].end =
											dealyedjobsToSchedule[0].start
													+ dealyedjobsToSchedule[0].executionTime;
									exhaustlist--;
									cout << "print1\n";
									printjob(dealyedjobsToSchedule[0]);
									//						jobsToSchedule.insert(jobsToSchedule.begin(),dealyedjobsToSchedule[i]);
									ScheduledList.insert(ScheduledList.begin(),
											dealyedjobsToSchedule[0]);
									k++;
									dealyedjobsToSchedule.erase(
											dealyedjobsToSchedule.begin());
									if (1 == ask) {
										rmSchedule(dealyedjobsToSchedule,
												period);
									} else
										edfSchedule(dealyedjobsToSchedule);

								} else {
									cout << "currentTime:" << currentTime
											<< "!= dealyedjobsToSchedule[1].availTime"
											<< dealyedjobsToSchedule[1].availTime
											<< endl;
									//delayedjob1 must have less pripority
									if ((currentTime
											!= dealyedjobsToSchedule[1].availTime
											|| ispriority(ask,
													dealyedjobsToSchedule[1],
													dealyedjobsToSchedule[0]))
											&& !(ispriority(ask,
													dealyedjobsToSchedule[0],
													dealyedjobsToSchedule[1])
													&& currentTime
															>= dealyedjobsToSchedule[1].availTime)) {

										dealyedjobsToSchedule[0].start =
												currentTime;
										currentTime =
												dealyedjobsToSchedule[1].availTime;
										dealyedjobsToSchedule[0].end =
												dealyedjobsToSchedule[1].availTime;
										if (dealyedjobsToSchedule[0].jobIndex
												- (int) dealyedjobsToSchedule[0].jobIndex
												== 0)
											dealyedjobsToSchedule[0].jobIndex +=
													0.1;
										dealyedjobsToSchedule[0].executionTime -=
												dealyedjobsToSchedule[0].end
														- dealyedjobsToSchedule[0].start;
										// push the delayed task into both the vectors
										//						jobsToSchedule.push_back(dealyedjobsToSchedule[i]);
										cout << "print02.1\n";
										printjob(dealyedjobsToSchedule[0]);
										printjob(dealyedjobsToSchedule[1]);
										//						jobsToSchedule.insert(jobsToSchedule.begin(),dealyedjobsToSchedule[i]);
										ScheduledList.insert(
												ScheduledList.begin(),
												dealyedjobsToSchedule[0]);
										k++;
										// now change the delayed job
										dealyedjobsToSchedule[0].availTime =
												dealyedjobsToSchedule[0].end;// avail start is end
										dealyedjobsToSchedule[0].start = -1;
										dealyedjobsToSchedule[0].end = -1;
										dealyedjobsToSchedule[0].jobIndex +=
												0.1;
										if (1 == ask) {
											rmSchedule(dealyedjobsToSchedule,
													period);
										} else
											edfSchedule(dealyedjobsToSchedule);
									} else {
										cout << "swappingprint02";
										printjob(dealyedjobsToSchedule[0]);
										printjob(dealyedjobsToSchedule[1]);
										swap(dealyedjobsToSchedule[0],
												dealyedjobsToSchedule[1]);
										i = -1;
										continue;
										cout << "after swapping";
										printjob(dealyedjobsToSchedule[0]);
										printjob(dealyedjobsToSchedule[1]);
									}
								}
							}
						}
					} else {
						cout << "i dont think we have to rite code here\n";
						//compare dealed jobs to jobs to schedule queue
						if (jobsToSchedule.empty()) {
							dealyedjobsToSchedule[0].start = currentTime;
							currentTime +=
									dealyedjobsToSchedule[0].executionTime;
							dealyedjobsToSchedule[0].end =
									dealyedjobsToSchedule[0].start
											+ dealyedjobsToSchedule[0].executionTime;
							exhaustlist--;
							printjob(dealyedjobsToSchedule[0]);
//								jobsToSchedule.insert(jobsToSchedule.begin(),dealyedjobsToSchedule[i]);
							ScheduledList.insert(ScheduledList.begin(),
									dealyedjobsToSchedule[0]);
							cout << "123\n";
//								sleep(1000);
							k++;
							dealyedjobsToSchedule.erase(
									dealyedjobsToSchedule.begin());
							cout << "123456\n";
//								sleep(1000);
							if (1 == ask) {
								rmSchedule(dealyedjobsToSchedule, period);
							} else
								edfSchedule(dealyedjobsToSchedule);
						} else {	//compare jobs and delayed queue
							if (1 == ask) {
								rmSchedule(jobsToSchedule, period);
							} else
								edfSchedule(jobsToSchedule);

							if (currentTime
									+ dealyedjobsToSchedule[0].executionTime
									<= jobsToSchedule[0].availTime
									|| !ispriority(ask,
											dealyedjobsToSchedule[0],
											jobsToSchedule[0])) {// remove the task it is done
								dealyedjobsToSchedule[0].start = currentTime;
								currentTime +=
										dealyedjobsToSchedule[0].executionTime;
								dealyedjobsToSchedule[0].end =
										dealyedjobsToSchedule[0].start
												+ dealyedjobsToSchedule[0].executionTime;
								exhaustlist--;
								cout << "print1.1\n";
								printjob(dealyedjobsToSchedule[0]);
								ScheduledList.insert(ScheduledList.begin(),
										dealyedjobsToSchedule[0]);
								k++;
								dealyedjobsToSchedule.erase(
										dealyedjobsToSchedule.begin());

								if (1 == ask) {
									rmSchedule(dealyedjobsToSchedule, period);
								} else
									edfSchedule(dealyedjobsToSchedule);

							} else {	//jobs is more priority
								cout << "currentTime:" << currentTime
										<< "!= jobsToSchedule[0].availTime"
										<< jobsToSchedule[0].availTime << endl;
								if (max(dealyedjobsToSchedule[0].availTime,
										currentTime)
										< jobsToSchedule[0].availTime) {// && !ispriority(ask,dealyedjobsToSchedule[0],dealyedjobsToSchedule[1])) { //skip the iteration

									dealyedjobsToSchedule[0].start =
											currentTime;
									currentTime = jobsToSchedule[0].availTime;
									dealyedjobsToSchedule[0].end =
											jobsToSchedule[0].availTime;
									if (dealyedjobsToSchedule[0].jobIndex
											- (int) dealyedjobsToSchedule[0].jobIndex
											== 0)
										dealyedjobsToSchedule[0].jobIndex +=
												0.1;
									dealyedjobsToSchedule[0].executionTime -=
											dealyedjobsToSchedule[0].end
													- dealyedjobsToSchedule[0].start;
									cout << "print2.1\n";
									printjob(jobsToSchedule[0]);
									cout << "print2.2\n";
									printjob(dealyedjobsToSchedule[0]);
									ScheduledList.insert(ScheduledList.begin(),
											dealyedjobsToSchedule[0]);
									k++;
									// now change the delayed job
									dealyedjobsToSchedule[0].availTime =
											dealyedjobsToSchedule[0].end;// avail start is end
									dealyedjobsToSchedule[0].start = -1;
									dealyedjobsToSchedule[0].end = -1;
									dealyedjobsToSchedule[0].jobIndex += 0.1;
									if (1 == ask) {
										rmSchedule(dealyedjobsToSchedule,
												period);
									} else
										edfSchedule(dealyedjobsToSchedule);
//										}else{cout<<"swapping"; printjob(dealyedjobsToSchedule[0]);printjob(jobsToSchedule[0]);
//										swap(dealyedjobsToSchedule[0],jobsToSchedule[0]);
//										i =  -1;
//										continue;
//										cout<<"after swapping"; printjob(dealyedjobsToSchedule[0]);printjob(dealyedjobsToSchedule[1]);
								} else
									break;
							}

						}
					}
					if (1 == ask) {
						rmSchedule(dealyedjobsToSchedule, period);
					} else
						edfSchedule(dealyedjobsToSchedule);

					i = -1; // rescan the delayed jobs

				} else {
					//cout<<"count"<<counter<<"k"<<k<<"size"<<dealyedjobsToSchedule.size()<<"i"<<i<<"ctime"<<currentTime<<"dtime"<<dealyedjobsToSchedule[0].availTime<<endl;
					//cout<<jobsToSchedule.size()<<endl;
					if (counter >= totalNoJobs
							&& currentTime
									< dealyedjobsToSchedule[0].availTime) {

						if (1 == ask) {
							rmSchedule(dealyedjobsToSchedule, period);
						} else
							edfSchedule(dealyedjobsToSchedule);

//							sort(dealyedjobsToSchedule);
//							sortByDeadline(dealyedjobsToSchedule);
						currentTime = dealyedjobsToSchedule[0].availTime;
						i = -1;						//reset the pointer
						//					cout<<"Ideal time "<<dealyedjobsToSchedule[i].availTime<<"(</>)"<< currentTime<<endl;
					} else
						;//cout<<"something wrong here"<<k<<"<>"<<totalNoJobs;
//						cout<<"delayedjobs: not ready\n";
				}
//				}//after scheduling the jobs must exit
			}		// for to iterate thru the jobs
		}		//if delayed jobs exist
//			cout<<"hi jobs list"<<"k"<<k<<"<>"<<totalNoJobs<<endl;
//#############################################################################################################
		//cout<<"debugging here"<<jobsToSchedule.size()<<":"<<k<<endl;
		//if(!( k < jobsToSchedule.size())) break;
//			if(counter < totalNoJobs){//cout<<"hi jobs list\n";
		if (jobsToSchedule.size() > 0) {
			counter++;
			cout << "count" << counter << "Totaljobs" << totalNoJobs << endl;
			cout << "k exists" << k << "jobSize" << jobsToSchedule.size()
					<< endl;
			if (jobsToSchedule[0].availTime <= currentTime
					&& currentTime != iMajorCycle) {

				jobsToSchedule[0].start = currentTime;
//					counter++;// to count each element in the jobs to schedule queue

				if (1 == ask) {
					rmSchedule(dealyedjobsToSchedule, period);
					rmSchedule(jobsToSchedule, period);
				} else {
					edfSchedule(dealyedjobsToSchedule);
					edfSchedule(jobsToSchedule);
				}
				if (jobsToSchedule.size() == 1) {	// k+1 job does not exist
					if (dealyedjobsToSchedule.size() == 0) {//only jobs to schedule vector exist!! SIMPLE!!
						//only jobs to schedule vector exist!! SIMPLE!!
						jobsToSchedule[0].end = jobsToSchedule[0].start
								+ jobsToSchedule[0].executionTime;
						currentTime += jobsToSchedule[0].executionTime;
						exhaustlist--; // jobs is scheduled till the end
						ScheduledList.insert(ScheduledList.begin(),
								jobsToSchedule[0]);
						jobsToSchedule.erase(jobsToSchedule.begin());
//							k++;// k the job is scheduled till the end
					} else { //if(k+1 > totalNoJobs && dealyedjobsToSchedule.size() != 0){
							 //Next job is not present in delayed jobs
						cout << "flag 2\n";
						if (currentTime + jobsToSchedule[0].executionTime
								<= dealyedjobsToSchedule[0].availTime
								|| !ispriority(ask, jobsToSchedule[0],
										jobsToSchedule[0])) {//checking only next high priority in the jobsToSchedule[k] vector we have to dealyed queue aswel
								//filling the jobs timings of completed task
							jobsToSchedule[0].end = jobsToSchedule[0].start
									+ jobsToSchedule[0].executionTime;
							currentTime += jobsToSchedule[0].executionTime;
							exhaustlist--; // jobs is scheduled till the end
							ScheduledList.insert(ScheduledList.begin(),
									jobsToSchedule[0]);
							jobsToSchedule.erase(jobsToSchedule.begin());
//								k++;// k the job is scheduled till the end
						} else {
							// now change the delayed job, job in jobstoshedule is paritially executed
							if (true) {
								float pastAvailTime =
										jobsToSchedule[0].availTime;
								jobsToSchedule[0].availTime =
										dealyedjobsToSchedule[0].availTime; // avail start is end
								jobsToSchedule[0].executionTime -=
										dealyedjobsToSchedule[0].availTime
												- jobsToSchedule[0].start; //total execution time - run time = remaining time
								jobsToSchedule[0].start = -1;
								jobsToSchedule[0].end = -1;
								jobsToSchedule[0].jobIndex += 0.2; // make it as .2 since this is the remaining part
								//push the delayed job into delayed queue
								dealyedjobsToSchedule.insert(
										dealyedjobsToSchedule.begin(),
										jobsToSchedule[0]);	//not yet the beginning, so put it in the second position
								//scheduled job fillings
								jobsToSchedule[0].availTime = pastAvailTime;
								jobsToSchedule[0].start = currentTime;
								jobsToSchedule[0].end =
										dealyedjobsToSchedule[0].availTime;
								jobsToSchedule[0].executionTime =
										jobsToSchedule[0].end
												- jobsToSchedule[0].start; //executed time
								jobsToSchedule[0].jobIndex -= 0.1; // decrease one from the index to make it as first

								ScheduledList.insert(ScheduledList.begin(),
										jobsToSchedule[0]);
								jobsToSchedule.erase(jobsToSchedule.begin());
								currentTime =
										dealyedjobsToSchedule[0].availTime; //update current time

//								k++; //job is done in the jobs to schedule queue

								if (1 == ask) {
									rmSchedule(dealyedjobsToSchedule, period);
								} else {
									edfSchedule(dealyedjobsToSchedule);
								}
							} //if to check the start and end
						} //else
						//Next job is present & delayed vector is present
					}
				} else { // k+1 is existing
					cout << "jobs size more than 1" << jobsToSchedule.size()
							<< endl;
					if (dealyedjobsToSchedule.size() == 0) { //delayed jobs not exist
						cout << "flag 3\n";
						if (currentTime + jobsToSchedule[0].executionTime
								<= jobsToSchedule[1].availTime
								|| !ispriority(ask, jobsToSchedule[0],
										jobsToSchedule[1])) { //checking only next high priority in the jobsToSchedule[k] vector we have to dealyed queue aswel
								//filling the jobs timings of completed task
							jobsToSchedule[0].end = jobsToSchedule[0].start
									+ jobsToSchedule[0].executionTime;
							currentTime += jobsToSchedule[0].executionTime;
							exhaustlist--; // jobs is scheduled till the end
//								k++;// k the job is scheduled till the end
							ScheduledList.insert(ScheduledList.begin(),
									jobsToSchedule[0]);
							jobsToSchedule.erase(jobsToSchedule.begin());
						} else {
							// now change the delayed job
							float pastAvailTime = jobsToSchedule[0].availTime;
							jobsToSchedule[0].availTime =
									jobsToSchedule[1].availTime; // avail start is end
							jobsToSchedule[0].executionTime -=
									jobsToSchedule[1].availTime
											- jobsToSchedule[0].start; //total execution time - run time = remaining time
							jobsToSchedule[0].start = -1;
							jobsToSchedule[0].end = -1;
							jobsToSchedule[0].jobIndex += 0.2; // make it as .2 since this is the remaining part
							//push the delayed job into delayed queue
							cout << "print3\n";
							printjob(jobsToSchedule[0]);
							dealyedjobsToSchedule.insert(
									dealyedjobsToSchedule.begin(),
									jobsToSchedule[0]);

							//scheduled job fillings
							jobsToSchedule[0].availTime = pastAvailTime;
							jobsToSchedule[0].start = currentTime;
							jobsToSchedule[0].end = jobsToSchedule[1].availTime;
							jobsToSchedule[0].executionTime =
									jobsToSchedule[0].end
											- jobsToSchedule[0].start; //executed time
							jobsToSchedule[0].jobIndex -= 0.1; // decrease one from the index to make it as first

							ScheduledList.insert(ScheduledList.begin(),
									jobsToSchedule[0]);

							currentTime = jobsToSchedule[1].availTime; //update current time
							cout << "print4\n";
							printjob(jobsToSchedule[1]);
							dealyedjobsToSchedule.insert(
									dealyedjobsToSchedule.begin(),
									jobsToSchedule[1]);

							jobsToSchedule.erase(jobsToSchedule.begin() + 1);
							jobsToSchedule.erase(jobsToSchedule.begin());
//								k+=1; //job is done in the jobs to schedule queue, since we pushed two tasks

							if (1 == ask) {
								rmSchedule(dealyedjobsToSchedule, period);
							} else {
								edfSchedule(dealyedjobsToSchedule);
							}
						}
					} else { //delay also exists
						cout << "flag 4\n";
						if (ispriority(ask, dealyedjobsToSchedule[0],
								jobsToSchedule[1])) {
							cout << "flag 4.1\n";
							if (currentTime + jobsToSchedule[0].executionTime
									<= jobsToSchedule[1].availTime
									|| !ispriority(ask, jobsToSchedule[0],
											jobsToSchedule[1])) { //checking only next high priority in the jobsToSchedule[k] vector we have to dealyed queue aswel
									//filling the jobs timings of completed task
								jobsToSchedule[0].end = jobsToSchedule[0].start
										+ jobsToSchedule[0].executionTime;
								currentTime += jobsToSchedule[0].executionTime;
								exhaustlist--; // jobs is scheduled till the end
								ScheduledList.insert(ScheduledList.begin(),
										jobsToSchedule[0]);
								jobsToSchedule.erase(jobsToSchedule.begin());

								//									k++;// k the job is scheduled till the end
							} else {
								// now change the delayed job
								float pastAvailTime =
										jobsToSchedule[0].availTime;
								jobsToSchedule[0].availTime =
										jobsToSchedule[1].availTime; // avail start is end
								jobsToSchedule[0].executionTime -=
										jobsToSchedule[1].availTime
												- jobsToSchedule[0].start; //total execution time - run time = remaining time
								jobsToSchedule[0].start = -1;
								jobsToSchedule[0].end = -1;
								jobsToSchedule[0].jobIndex += 0.2; // make it as .2 since this is the remaining part
								//push the delayed job into delayed queue
								cout << "print5\n";
								printjob(jobsToSchedule[0]);
								dealyedjobsToSchedule.insert(
										dealyedjobsToSchedule.begin(),
										jobsToSchedule[0]);

								//scheduled job fillings
								jobsToSchedule[0].availTime = pastAvailTime;
								jobsToSchedule[0].start = currentTime;
								jobsToSchedule[0].end =
										jobsToSchedule[1].availTime;
								jobsToSchedule[0].executionTime =
										jobsToSchedule[0].end
												- jobsToSchedule[0].start; //executed time
								jobsToSchedule[0].jobIndex -= 0.1; // decrease one from the index to make it as first

								ScheduledList.insert(ScheduledList.begin(),
										jobsToSchedule[0]);

								currentTime = jobsToSchedule[1].availTime; //update current time
								cout << "print6\n";
								printjob(jobsToSchedule[1]);
								dealyedjobsToSchedule.insert(
										dealyedjobsToSchedule.begin(),
										jobsToSchedule[1]);

								jobsToSchedule.erase(
										jobsToSchedule.begin() + 1);
								jobsToSchedule.erase(jobsToSchedule.begin());
//									k+=1; //job is done in the jobs to schedule queue, since we pushed two tasks

								if (1 == ask) {
									rmSchedule(dealyedjobsToSchedule, period);
								} else {
									edfSchedule(dealyedjobsToSchedule);
								}
							}
						} else { //delayed job is more priority
							cout << "flag 5\n";
							if (currentTime + jobsToSchedule[0].executionTime
									<= dealyedjobsToSchedule[0].availTime
									|| !ispriority(ask, jobsToSchedule[0],
											dealyedjobsToSchedule[0])) { //checking only next high priority in the jobsToSchedule[k] vector we have to dealyed queue aswel
									//filling the jobs timings of completed task
								jobsToSchedule[0].end = jobsToSchedule[0].start
										+ jobsToSchedule[0].executionTime;
								currentTime += jobsToSchedule[0].executionTime;
								exhaustlist--; // jobs is scheduled till the end
//									k++;// k the job is scheduled till the end
								ScheduledList.insert(ScheduledList.begin(),
										jobsToSchedule[0]);
								jobsToSchedule.erase(jobsToSchedule.begin());
							} else {
								// now change the delayed job
								float pastAvailTime =
										jobsToSchedule[0].availTime;
								jobsToSchedule[0].availTime =
										dealyedjobsToSchedule[0].availTime; // avail start is end
								jobsToSchedule[0].executionTime -=
										dealyedjobsToSchedule[0].availTime
												- jobsToSchedule[0].start; //total execution time - run time = remaining time
								jobsToSchedule[0].start = -1;
								jobsToSchedule[0].end = -1;
								jobsToSchedule[0].jobIndex += 0.2; // make it as .2 since this is the remaining part
								//push the delayed job into delayed queue
								cout << "print7\n";
								printjob(jobsToSchedule[0]);
								dealyedjobsToSchedule.insert(
										dealyedjobsToSchedule.begin(),
										jobsToSchedule[0]);	//not yet the beginning, so put it in the second position
								//scheduled job fillings
								jobsToSchedule[0].availTime = pastAvailTime;
								jobsToSchedule[0].start = currentTime;
								jobsToSchedule[0].end =
										dealyedjobsToSchedule[0].availTime;
								jobsToSchedule[0].executionTime =
										jobsToSchedule[0].end
												- jobsToSchedule[0].start; //executed time
								jobsToSchedule[0].jobIndex -= 0.1; // decrease one from the index to make it as first

								ScheduledList.insert(ScheduledList.begin(),
										jobsToSchedule[0]);
								jobsToSchedule.erase(jobsToSchedule.begin());
								currentTime =
										dealyedjobsToSchedule[0].availTime; //update current time

//									k++; //job is done in the jobs to schedule queue

								if (1 == ask) {
									rmSchedule(dealyedjobsToSchedule, period);
								} else {
									edfSchedule(dealyedjobsToSchedule);
								}
							}

						} // only delay exists
					}
				} //k+1
				k++; // k the job is scheduled till the end
			} else { //No jobs exists with the current time
				currentJob.absdeadline = jobsToSchedule[0].absdeadline;
				currentJob.availTime = jobsToSchedule[0].availTime;
				currentJob.end = jobsToSchedule[0].end;
				currentJob.executionTime = jobsToSchedule[0].executionTime;
				currentJob.jobIndex = jobsToSchedule[0].jobIndex;
				currentJob.jobNo = jobsToSchedule[0].jobNo;
				currentJob.start = jobsToSchedule[0].start;
//				cout<<"pushing into delay"<<endl;
				dealyedjobsToSchedule.push_back(currentJob);

				jobsToSchedule.erase(jobsToSchedule.begin());

				counter++;
//				cout<<"delaying job"<<jobsToSchedule[k].jobNo + 1<<endl;
//				k++;
			}

		} else {
			cout << "list exhaust" << endl;
		};
	}
	//sort Scheduled jobs before displaying
	sort(ScheduledList);
	outfile << setfill('-') << setw(80) << "-" << endl;
	//print Scheduled jobs
	status = "";
	isSchedulable = true;
	for (unsigned k = 0; k < ScheduledList.size(); k++) {
		if (ScheduledList[k].absdeadline < ScheduledList[k].end
				|| ScheduledList[k].end == -1) {
			status = "miss";
			isSchedulable = false;
		} else
			status = "hit";
		outfile << setiosflags(ios::left) << setfill(' ') << "start: "
				<< setw(6) << ScheduledList[k].availTime << "| Execution:"
				<< setw(9) << ScheduledList[k].end - ScheduledList[k].start //ScheduledList[k].executionTime
				<< "| jobno: (" << setw(3) << ScheduledList[k].jobNo + 1 << ","
				<< setw(4) << ScheduledList[k].jobIndex << ")" << "| Deadline: "
				<< setw(9) << ScheduledList[k].absdeadline << "|(Start,End): ("
				<< setw(6) << ScheduledList[k].start << "," << setw(6)
				<< ScheduledList[k].end << ")" << setw(4) << "|" << status
				<< endl;
	}

	outfile.close();
	return isSchedulable;
}

void ScheduleUntilScheduleable(unsigned iFrame, unsigned iMajorCycle,
		vector<float> &phase, vector<int> &period, vector<float> &execution,
		vector<int> &deadline) {
	while (true) {
		if (pschedule(iFrame, iMajorCycle, phase, period, execution,
				deadline)) {
			cout << "SUCCESS" << execution[0];
			return;
		} else {
			cout << "FAILED" << execution[0];
			//execution[0] = execution[0] - 0.001;
			return;
		}
	}
}

int main() {
	vector<int> period, deadline, factors, accepted, noMinFrame;
	vector<float> phase, execution, utilization;
	float utilize, totalSysUtilization = 0, minFrame;
	ofstream outfile;
	time_t ltime; /* calendar time */
	ltime = time(NULL); /* get current cal time */
	outfile.open("results.txt", ios::app);
	outfile << endl << setfill('-') << setw(110) << "-" << endl;
	outfile
			<< "Author: Satyam Kotikalapudi Objective: Scheduling RTS tasks TimeStamp:"
			<< asctime(localtime(&ltime));
	outfile << setfill('-') << setw(110) << "-" << endl;
	getTupple(phase, period, execution, deadline);
	for (unsigned k = 0; k < phase.size(); k++) {
		utilize = execution[k] / period[k];
		totalSysUtilization += utilize;
		utilization.push_back(utilize);
		outfile << setiosflags(ios::left) << setfill(' ') << "ph(" << setw(2)
				<< k << "):" << setw(8) << phase[k] << "|" << "p(" << setw(2)
				<< k << "):" << setw(5) << period[k] << "|" << "e(" << setw(2)
				<< k << "):" << setw(9) << execution[k] << "|" << "D("
				<< setw(2) << k << "):" << setw(5) << deadline[k] << "|" << "u("
				<< setw(2) << k << "):" << utilization[k] << endl;
	}
	outfile << setfill('-') << setw(80) << "-" << endl;
	outfile << "Total system Utilization: " << totalSysUtilization << endl;
	minFrame = getMaxE(execution);
//    outfile<<"Min Frame size: "<<minFrame<<endl;
	long lHyperPeriod = getLCM(period);
//    outfile<<"HyperPeriod: "<<lHyperPeriod<<endl;
	for (unsigned k = 0; k < period.size(); k++)
		factors = getMerge(factors, getFactors(period[k]));
//    outfile<<"Factors: {"<<factors[0];
//    for (unsigned k = 1; k < factors.size(); k++)
//       outfile<<", "<<factors[k];
//    outfile<<"}"<<endl;
	accepted = getFrameSize(factors, period, deadline, minFrame);
	noMinFrame = getFrameSize(factors, period, deadline, 0);
//    outfile<<"Acceptable Frame sizes: {"<<accepted[0];
//        for (unsigned k = 1; k < accepted.size(); k++)
//           outfile<<", "<<accepted[k];
//        outfile<<"}"<<endl;
//	outfile<<"Acceptable Frame sizes(leaving miniframe constraint): {"<<noMinFrame[0];
//			for (unsigned k = 1; k < noMinFrame.size(); k++)
//			   outfile<<", "<<noMinFrame[k];
//			outfile<<"}"<<endl;
//	outfile<<setfill('-')<<setw(110)<<"-"<<endl;
	outfile.close();
//	schedule(accepted[accepted.size()-1], lHyperPeriod, phase, period, execution, deadline);
//	schedule(32, 32, phase, period, execution, deadline);
	ScheduleUntilScheduleable(lHyperPeriod, lHyperPeriod, phase, period,
			execution, deadline);
//	ScheduleUntilScheduleable(32, 32, phase, period, execution, deadline);
	return 0;
}
