package com.corp.algorithm;

import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import scala.Tuple2;

public class EgoNet implements Serializable {
    private static final long serialVersionUID = 7505841917042773769L;

    public Map<Integer, Set<Tuple2<Integer, Integer>>> result;

    public EgoNet() {
	result = new HashMap<>();
    }

    public void add(int vertex, int vertexFrom, int vertexTo, int[] dict) {
	int vertex2 = dict[vertex];
	int vertexFrom2 = dict[vertexFrom];
	int vertexTo2 = dict[vertexTo];
	if (result.containsKey(vertex2)) {
	    result.get(vertex2).add(generateEdge(vertexFrom2, vertexTo2));
	} else {
	    Set<Tuple2<Integer, Integer>> edges = new HashSet<>();
	    edges.add(generateEdge(vertexFrom2, vertexTo2));
	    result.put(vertex2, edges);
	}
    }

    public Tuple2<Integer, Integer> generateEdge(int vertexFrom, int vertexTo) {
	if (vertexFrom > vertexTo) {
	    return new Tuple2<Integer, Integer>(vertexTo, vertexFrom);
	}
	return new Tuple2<Integer, Integer>(vertexFrom, vertexTo);
    }
    
    public void debug(){
	for(Map.Entry<Integer, Set<Tuple2<Integer, Integer>>> entry : result.entrySet()){
	    System.out.println(entry.getKey()+" "+entry.getValue());
	}
    }
}
