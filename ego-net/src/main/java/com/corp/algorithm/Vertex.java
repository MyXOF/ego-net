package com.corp.algorithm;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Vertex{
    public int index;
    public int degree;
    public Set<Integer> edges;
    public int indexAfterArranged;
    public Set<Integer> edgesAfterArranged;
    public boolean flag;
    
    public Vertex(int index, Set<Integer> edges,int indexAfterArranged){
	this.index = index;
	this.edges = edges;
	this.degree = edges.size();
	this.indexAfterArranged = indexAfterArranged;
	this.flag = true;
    }
    
    @Override
    public int hashCode() {
      final int prime = 31;
      int result = (index + edges.hashCode()) % prime;
      return result;
    }

    @Override
    public boolean equals(Object obj) {
      if (this == obj)
        return true;
      if (obj == null)
        return false;
      if (getClass() != obj.getClass())
        return false;
      Vertex other = (Vertex) obj;
      return this.index == other.index;
    }

//    @Override
//    public int compareTo(Vertex o) {
//	return this.degree - o.degree;
//    }
    
    public void addNewEdges(Map<Integer, Integer> info){
	edgesAfterArranged = new HashSet<>();
	for(int v : edges){
	    edgesAfterArranged.add(info.get(v));
	}
    }

}
