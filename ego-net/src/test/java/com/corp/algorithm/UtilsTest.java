package com.corp.algorithm;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class UtilsTest {
    private List<int[]> inputGraph;

    @Before
    public void setUp() throws Exception {
	inputGraph = new ArrayList<>();
	int[] tmp1 = {2,5};
	inputGraph.add(tmp1);
	
	int[] tmp2 = {1,3,4};
	inputGraph.add(tmp2);
	
	int[] tmp3 = {2,4};
	inputGraph.add(tmp3);
	
	int[] tmp4 = {2,3,5,6};
	inputGraph.add(tmp4);
	
	int[] tmp5 = {1,4,5};
	inputGraph.add(tmp5);
	
	int[] tmp6 = {4,5};
	inputGraph.add(tmp6);
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void testSqrt() {
	assertEquals(3, Utils.sqrt(6));
	assertEquals(4, Utils.sqrt(10));
    }

    @Test
    public void testConstructEgoNet() {
	Map<Integer, Set<Integer>> subGraph = new HashMap<>();
//	for(int i = 0; i < 6;i++){
//	    Set<Integer> set = new HashSet<>();
//	    for(int v : inputGraph.get(i)){
//		set.add(v);
//	    }
//	    subGraph.put(i+1, set);
//	}
	Set<Integer> set4000 = new HashSet<>();
	set4000.add(4017);
	set4000.add(4033);
	set4000.add(3992);
	subGraph.put(4000,set4000);
	
	Set<Integer> set4016 = new HashSet<>();
	set4016.add(4025);
	subGraph.put(4016,set4016);
	
	Set<Integer> set4017 = new HashSet<>();
	set4017.add(4000);
	set4017.add(3992);
	subGraph.put(4017,set4017);
	
	Set<Integer> set4033 = new HashSet<>();
	set4033.add(4000);
	subGraph.put(4033,set4033);
	
	Set<Integer> set3992 = new HashSet<>();
	set3992.add(4000);
	set3992.add(4017);
	subGraph.put(3992,set3992);
	
	Set<Integer> set4025 = new HashSet<>();
	set4025.add(4016);
	subGraph.put(4025,set4025);
	
	
	EgoNet egoNet = Utils.constructEgoNet(subGraph);
	egoNet.debug();
    }

}
