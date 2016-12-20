package com.corp.algorithm;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class EdgeMatrix {
    public List<boolean[]> matrix;
    private int length;
    private Vertex[] vertexs;
    private Map<Integer, Integer> indexAfterArrangedMap;
    public int[] indexBeforeArranged;
    private int size;
    
    public EdgeMatrix(int length){
	this.length = length;
	this.matrix = new ArrayList<>();
	this.vertexs = new Vertex[length];
	this.indexAfterArrangedMap = new HashMap<>();
	this.indexBeforeArranged = new int[length];
	this.size = length;
    }
    
    public void addVertex(int pos,int index, Set<Integer> edges){
	vertexs[pos] = new Vertex(index, edges, pos);
	indexAfterArrangedMap.put(index, pos);
	indexBeforeArranged[pos] = index;
    }
    
    public void arrangeNeighbourIndex(){
	for(Vertex vertex : vertexs){
	    vertex.edgesAfterArranged = new HashSet<>();
	    for(int n: vertex.edges){
		vertex.edgesAfterArranged.add(indexAfterArrangedMap.get(n));
	    }
	    addEdgesInfo(vertex.edgesAfterArranged);
	}
    }
    
    private void addEdgesInfo(Set<Integer> edges){
	boolean[] vector = new boolean[length];
	for(int i = 0;i < length;i++){
	    vector[i] = false;
	}
	for(int edge : edges){
	    vector[edge] = true;
	}
	matrix.add(vector);
    }
    
    private void removeEdge(int v1, int v2){
	if(matrix.get(v1)[v2]){
	    matrix.get(v1)[v2] = false;
	    vertexs[v1].degree--;
	}
	if(matrix.get(v2)[v1]){
	    matrix.get(v2)[v1] = false;
	    vertexs[v2].degree--;
	}
    }
    
    public boolean[] getNeighbour(int v){
	return matrix.get(v);
    }
    
    public void removeVertexAndAllEdges(int index){
	boolean[] vector = matrix.get(index);
	for(int i = 0; i < length;i++){
	    if(vector[i]) removeEdge(index, i);
	}
	vertexs[index].flag = false;
	size--;
    }
    
    public boolean hasEdge(int v1, int v2){
	return matrix.get(v1)[v2] || matrix.get(v2)[v1];
    }
    
    public boolean hasNext(){
	return size > 0;
    }
    
    public Vertex getNext(){
	Vertex currentVertex = null;
	int i = 0;
	for(i = 0; i < length;i++){
	    if(vertexs[i].flag){
		currentVertex = vertexs[i];
		break;
	    }
	}
	if(currentVertex == null) return null;
	
	for(int j = i+1;j < length;j++){
	    if(vertexs[j].flag & vertexs[j].degree < currentVertex.degree){
		currentVertex = vertexs[j];
	    }
	}
	
	return currentVertex;
    }
}
