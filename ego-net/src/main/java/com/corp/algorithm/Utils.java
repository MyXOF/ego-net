package com.corp.algorithm;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONObject;

import scala.Tuple2;
import scala.Tuple3;

public class Utils {
    public static int caculateHashcode(int number, int partitionNums) {
	return number % partitionNums;
    }

    public static Tuple3<Integer, Integer, Integer> sort(int n1, int n2, int n3) {
	int[] tmp = { n1, n2, n3 };
	Arrays.sort(tmp);
	return new Tuple3<Integer, Integer, Integer>(tmp[0], tmp[1], tmp[2]);
    }

    public static int sqrt(int num) {
	return (int) Math.sqrt(num) + 1;
    }

    public static EgoNet constructEgoNet(Map<Integer, Set<Integer>> subGraph) {
	int size = subGraph.size();
	EdgeMatrix matrix = new EdgeMatrix(size);

	EgoNet result = new EgoNet();
	int i = 0;
	for (Map.Entry<Integer, Set<Integer>> entry : subGraph.entrySet()) {
	    matrix.addVertex(i, entry.getKey(), entry.getValue());
	    i++;
	}
	matrix.arrangeNeighbourIndex();

	while (matrix.hasNext()) {
	    int currentNode = matrix.getNext().indexAfterArranged;
	    boolean[] vector = matrix.getNeighbour(currentNode);
	    List<Integer> neighbours = new ArrayList<>();
	    for (int j = 0; j < size; j++) {
		if (vector[j] & j != currentNode) {
		    neighbours.add(j);
		    result.add(currentNode, currentNode, j, matrix.indexBeforeArranged);
		    result.add(j, currentNode, j, matrix.indexBeforeArranged);
		}
	    }

	    for (int p = 0; p < neighbours.size(); p++) {
		for (int q = p + 1; q < neighbours.size(); q++) {
		    int n1 = neighbours.get(p);
		    int n2 = neighbours.get(q);
		    if (matrix.hasEdge(n1, n2)) {
			result.add(currentNode, n1, n2, matrix.indexBeforeArranged);
			result.add(n1, currentNode, n2, matrix.indexBeforeArranged);
			result.add(n2, currentNode, n1, matrix.indexBeforeArranged);
		    }
		}
	    }

	    matrix.removeVertexAndAllEdges(currentNode);
	}
	return result;
    }

    public static void createReulstJsonFile(String outputFile,
	    List<Tuple2<Integer, Set<Tuple2<Integer, Integer>>>> egoNetList) throws IOException {
	JSONObject data = new JSONObject();
	for (Tuple2<Integer, Set<Tuple2<Integer, Integer>>> egoNet : egoNetList) {
	    JSONArray edges = new JSONArray();

	    for (Tuple2<Integer, Integer> edge : egoNet._2) {
		JSONObject line = new JSONObject();
		line.put("sourceID", edge._1.toString());
		line.put("attributes", new JSONObject());
		line.put("targetID", edge._2.toString());
		line.put("size", 3);
		edges.put(line);
	    }
	    data.put(egoNet._1.toString(), edges);
	}

	BufferedWriter writer = new BufferedWriter(new FileWriter(outputFile));
	writer.write(data.toString());
	writer.close();
    }
}
