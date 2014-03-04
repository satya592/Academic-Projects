//package proj7;



public class Edge{
	int nextNodeNum =-1;
	boolean matched = false;
	int weight = -1;
	
	Edge(int v, int weight, boolean matched){
		this.nextNodeNum = v;
		this.weight = weight;
		this.matched = matched;
	}
	//Getters
/*	int getNextNodeNum(){
		
	}
	boolean getMatched(){
		
	}
*/	
}