package com.corp.algorithm;

import scala.Tuple2;
import scala.Tuple3;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.FlatMapFunction;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.api.java.function.Function2;
import org.apache.spark.api.java.function.PairFunction;

public final class App {
    private static final String SPILTOR = ",";
    private static final int PARTIITON_NUM = 3;

    public static int caculateHashcode(int number) {
	return number % PARTIITON_NUM;
    }

    public static Tuple3<Integer, Integer, Integer> sort(int n1, int n2, int n3) {
	int[] tmp = { n1, n2, n3 };
	Arrays.sort(tmp);
	return new Tuple3<Integer, Integer, Integer>(tmp[0], tmp[1], tmp[2]);
    }

    public static void main(String[] args) throws Exception {
	JavaSparkContext context = new JavaSparkContext(new SparkConf().setAppName("word count"));
	JavaRDD<String> textFile = context.textFile("test");

	JavaRDD<Tuple2<Tuple3<Integer, Integer, Integer>, Tuple2<Integer, Integer>>> inputGraph = textFile.flatMap(
		new FlatMapFunction<String, Tuple2<Tuple3<Integer, Integer, Integer>, Tuple2<Integer, Integer>>>() {
		    private static final long serialVersionUID = -8938245938069063839L;

		    @Override
		    public Iterator<Tuple2<Tuple3<Integer, Integer, Integer>, Tuple2<Integer, Integer>>> call(
			    String line) throws Exception {
			List<Tuple2<Tuple3<Integer, Integer, Integer>, Tuple2<Integer, Integer>>> tmp = new ArrayList<>();
			String values[] = line.split(SPILTOR);
			int edge1 = Integer.parseInt(values[0]);
			int edge2 = Integer.parseInt(values[1]);
			int i = caculateHashcode(edge1);
			int j = caculateHashcode(edge2);
			if (i == j) {
			    for (int z = 1; z <= PARTIITON_NUM; z++) {
				if (z == i)
				    continue;
				for (int w = 1; w <= PARTIITON_NUM; w++) {
				    if (w == i || w == z)
					continue;
				    tmp.add(new Tuple2<Tuple3<Integer, Integer, Integer>, Tuple2<Integer, Integer>>(
					    sort(i, z, w), new Tuple2<Integer, Integer>(edge1, edge2)));
				}
			    }
			} else {
			    for (int z = 1; z <= PARTIITON_NUM; z++) {
				if (z == i || z == j)
				    continue;
				tmp.add(new Tuple2<Tuple3<Integer, Integer, Integer>, Tuple2<Integer, Integer>>(
					sort(i, j, z), new Tuple2<Integer, Integer>(edge1, edge2)));
			    }
			}
			return tmp.iterator();
		    }
		});

	JavaPairRDD<Tuple3<Integer, Integer, Integer>, Tuple2<Map<Integer, Integer>, List<Tuple2<Integer, Integer>>>> pairs = inputGraph
		.mapToPair(
			new PairFunction<Tuple2<Tuple3<Integer, Integer, Integer>, Tuple2<Integer, Integer>>, Tuple3<Integer, Integer, Integer>, Tuple2<Map<Integer, Integer>, List<Tuple2<Integer, Integer>>>>() {

			    private static final long serialVersionUID = -7725391314961777859L;

			    @Override
			    public Tuple2<Tuple3<Integer, Integer, Integer>, Tuple2<Map<Integer, Integer>, List<Tuple2<Integer, Integer>>>> call(
				    Tuple2<Tuple3<Integer, Integer, Integer>, Tuple2<Integer, Integer>> tuple2)
				    throws Exception {
				Tuple3<Integer, Integer, Integer> key = tuple2._1;
				Map<Integer, Integer> info = new HashMap<>();
				info.put(tuple2._2._1, 1);
				info.put(tuple2._2._2, 1);
				List<Tuple2<Integer, Integer>> edges = new ArrayList<>();
				edges.add(tuple2._2);
				Tuple2<Map<Integer, Integer>, List<Tuple2<Integer, Integer>>> value = new Tuple2<Map<Integer, Integer>, List<Tuple2<Integer, Integer>>>(
					info, edges);
				return new Tuple2<Tuple3<Integer, Integer, Integer>, Tuple2<Map<Integer, Integer>, List<Tuple2<Integer, Integer>>>>(
					key, value);
			    }
			});

	JavaPairRDD<Tuple3<Integer, Integer, Integer>, Tuple2<Map<Integer, Integer>, List<Tuple2<Integer, Integer>>>> mapResultInput = pairs.reduceByKey(new Function2<Tuple2<Map<Integer,Integer>,List<Tuple2<Integer,Integer>>>, Tuple2<Map<Integer,Integer>,List<Tuple2<Integer,Integer>>>, Tuple2<Map<Integer,Integer>,List<Tuple2<Integer,Integer>>>>() {

	    private static final long serialVersionUID = 4332795275892019942L;

	    @Override
	    public Tuple2<Map<Integer, Integer>, List<Tuple2<Integer, Integer>>> call(
		    Tuple2<Map<Integer, Integer>, List<Tuple2<Integer, Integer>>> pair1,
		    Tuple2<Map<Integer, Integer>, List<Tuple2<Integer, Integer>>> pair2) throws Exception {
		Map<Integer, Integer> mapInfo = pair1._1;
//		Map<Integer, Integer> mapInfo = new HashMap<>();
//		for (Map.Entry<Integer, Integer> entry : pair1._1.entrySet()) {
//		    mapInfo.put(entry.getKey(), entry.getValue());
//		}
		for(Map.Entry<Integer, Integer> entry : pair2._1.entrySet()){
		    if(mapInfo.containsKey(entry.getKey())){
			int valueTmp = mapInfo.get(entry.getKey()) + entry.getValue();
			mapInfo.put(entry.getKey(), valueTmp);
		    }else{
			mapInfo.put(entry.getKey(), entry.getValue());
		    }
		}
		List<Tuple2<Integer, Integer>> edges = pair1._2;
		edges.addAll(pair2._2);
		return new Tuple2<Map<Integer,Integer>, List<Tuple2<Integer,Integer>>>(mapInfo, edges);
	    }
	});

	List<Tuple2<Tuple3<Integer, Integer, Integer>, Tuple2<Map<Integer, Integer>, List<Tuple2<Integer, Integer>>>>> result =  mapResultInput.collect();

	for(Tuple2<Tuple3<Integer, Integer, Integer>, Tuple2<Map<Integer, Integer>, List<Tuple2<Integer, Integer>>>> info : result){
	    System.out.println(info._1.toString());
	    for (Map.Entry<Integer, Integer> entry : info._2._1.entrySet()) {
		System.out.println("\t" + entry.getKey() + ": " + entry.getValue());
	    }
	    
	    for(Tuple2<Integer, Integer> edge : info._2._2){
		System.out.println("\t"+edge.toString());
	    }
	}
	
	JavaRDD<List<Tuple2<Integer, Integer>>> ego = mapResultInput.map(new Function<Tuple2<Tuple3<Integer,Integer,Integer>,Tuple2<Map<Integer,Integer>,List<Tuple2<Integer,Integer>>>>, List<Tuple2<Integer, Integer>>>() {

	    @Override
	    public List<Tuple2<Integer, Integer>> call(
		    Tuple2<Tuple3<Integer, Integer, Integer>, Tuple2<Map<Integer, Integer>, List<Tuple2<Integer, Integer>>>> info)
		    throws Exception {
		Map<Integer, Integer> degreeMap = info._2._1;
		List<Tuple2<Integer, Integer>> edges = info._2._2;
		
		return null;
	    }
	});
	


	context.stop();
    }
}
