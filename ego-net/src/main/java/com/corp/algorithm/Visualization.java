package com.corp.algorithm;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.json.JSONArray;
import org.json.JSONObject;

public class Visualization {
    private String colors[] = { "#c71969", "#c71919", "#FE2EC8", "#8419c7", "#138DF8", "#39F813", "#19C95C", "#E62F57",
	    "#9ADC49", "#1919c7", "#8E1AC8", "#F88A13", "#E0E750" };

    private Random random;
    private final int colorNum = colors.length;
    private final int majorColor = 4;
    private String inputFile;
    private String outputFile;
    private Map<String, Integer> nodeInfo;
    private List<Pair<String, String>> edgesList;

    public Visualization(String inputFile, String outputFile) {
	this.random = new Random(System.currentTimeMillis());
	this.inputFile = inputFile;
	this.outputFile = outputFile;
	this.nodeInfo = new HashMap<>();
	this.edgesList = new ArrayList<>();
    }

    public void loadData() throws IOException {
	BufferedReader reader = new BufferedReader(new FileReader(inputFile));
	String line;
	while ((line = reader.readLine()) != null) {
	    String values[] = line.split(" ");
	    addNode(values[0]);
	    addNode(values[1]);
	    edgesList.add(new Pair<String, String>(values[0], values[1]));

	}

	reader.close();
    }

    private void addNode(String nodeId) {
	if (nodeInfo.containsKey(nodeId)) {
	    int degree = nodeInfo.get(nodeId) + 1;
	    nodeInfo.put(nodeId, degree);
	} else {
	    nodeInfo.put(nodeId, 1);
	}
    }

    public void createJsonData() throws IOException {
	JSONObject data = new JSONObject();
	
	JSONArray nodes = new JSONArray();
	for (Map.Entry<String, Integer> entry : nodeInfo.entrySet()) {

	    JSONObject node = new JSONObject();
	    node.put("size", entry.getValue());
	    node.put("label", entry.getKey());
	    node.put("attributes", new JSONObject());
	    node.put("id", entry.getKey());

	    if (entry.getValue() > 80) {
		node.put("color", colors[random.nextInt(majorColor)]);
	    } else {
		node.put("color", colors[random.nextInt(colorNum - majorColor) + majorColor]);
	    }

	    node.put("y", random.nextInt(5000) - 2500);
	    node.put("x", random.nextInt(10000) - 5000);

	    nodes.put(node);
	}
	data.put("nodes", nodes);

	JSONArray edges = new JSONArray();
	for (Pair<String, String> edge : edgesList) {
	    JSONObject line = new JSONObject();
	    line.put("sourceID", edge.left);
	    line.put("attributes", new JSONObject());
	    line.put("targetID", edge.right);
	    line.put("size", 1);
	    edges.put(line);
	}
	data.put("edges", edges);
	BufferedWriter writer = new BufferedWriter(new FileWriter(outputFile));
	writer.write(data.toString());
	writer.close();
    }

    public static void main(String[] args) throws IOException {
	Visualization visualization = new Visualization("input.edges", "origin.json");
	visualization.loadData();
	visualization.createJsonData();

    }

}
