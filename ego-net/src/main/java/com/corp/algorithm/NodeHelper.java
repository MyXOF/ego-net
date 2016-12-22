package com.corp.algorithm;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;


public class NodeHelper {

    public static void main(String[] args) throws IOException {
	// TODO Auto-generated method stub
	BufferedReader reader = new BufferedReader(new FileReader("dataset/3437.edges"));
	String line = null;
	Set<String> set = new HashSet<>();
	while((line = reader.readLine()) != null){
	    String values[] = line.split(" ");
	    set.add(values[0]);
	    set.add(values[1]);
	}
	System.out.println(set.size());
	reader.close();
		
		
    }

}
