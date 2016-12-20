package com.corp.algorithm;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.FlatMapFunction;
import org.apache.spark.api.java.function.Function2;
import org.apache.spark.api.java.function.PairFunction;

import scala.Tuple2;
import scala.Tuple3;

public class SparkJob implements Serializable {
    private static final long serialVersionUID = -2923808944226683129L;

    private String filePath;
    private transient JavaSparkContext context;
    private String applicationName;
    private int vertexNum;
    private transient JavaRDD<String> textFile;

    private int partitionNum;

    public SparkJob(String filePath, String applicationName, int vertexNum) {
	this.filePath = filePath;
	this.applicationName = applicationName;
	this.vertexNum = vertexNum;
    }

    public void init() {
	this.context = new JavaSparkContext(
		new SparkConf().setAppName(applicationName).set("spark.executor.memory", "4096m"));
	this.partitionNum = Utils.sqrt(vertexNum);
	this.textFile = context.textFile(filePath);
    }

    public void start() throws IOException {
	JavaPairRDD<Tuple3<Integer, Integer, Integer>, Map<Integer, Set<Integer>>> mapResultInput = mapStage();
	JavaPairRDD<Integer, Set<Tuple2<Integer, Integer>>> egoNetAll = reduceStage(mapResultInput);
//	egoNetAll.saveAsTextFile("hdfs://192.168.130.15:9000/user/hadoop/xuyi/out.txt");
	List<Tuple2<Integer, Set<Tuple2<Integer, Integer>>>> egoNetList = egoNetAll.collect();
//	for(Tuple2<Integer, Set<Tuple2<Integer, Integer>>> v : egoNetList){
//	    System.out.println(v._1);
//	    System.out.println("    "+v._2.toString());
//	    
//	}
	Utils.createReulstJsonFile(Config.EGONET_OUTPUT_PATH, egoNetList);
    }

    public void stop() {
	context.stop();
    }

    private JavaPairRDD<Tuple3<Integer, Integer, Integer>, Map<Integer, Set<Integer>>> mapStage() {
	JavaRDD<Tuple2<Tuple3<Integer, Integer, Integer>, Tuple2<Integer, Integer>>> inputGraph = textFile.flatMap(
		new FlatMapFunction<String, Tuple2<Tuple3<Integer, Integer, Integer>, Tuple2<Integer, Integer>>>() {
		    private static final long serialVersionUID = -8938245938069063839L;

		    @Override
		    public Iterator<Tuple2<Tuple3<Integer, Integer, Integer>, Tuple2<Integer, Integer>>> call(
			    String line) throws Exception {
			List<Tuple2<Tuple3<Integer, Integer, Integer>, Tuple2<Integer, Integer>>> tmp = new ArrayList<>();
			String values[] = line.split(Config.SPILTOR);
			int edge1 = Integer.parseInt(values[0]);
			int edge2 = Integer.parseInt(values[1]);
			int i = Utils.caculateHashcode(edge1, partitionNum);
			int j = Utils.caculateHashcode(edge2, partitionNum);
			if (i == j) {
			    for (int z = 1; z <= partitionNum; z++) {
				if (z == i)
				    continue;
				for (int w = 1; w <= partitionNum; w++) {
				    if (w == i || w == z)
					continue;
				    tmp.add(new Tuple2<Tuple3<Integer, Integer, Integer>, Tuple2<Integer, Integer>>(
					    Utils.sort(i, z, w), new Tuple2<Integer, Integer>(edge1, edge2)));
				}
			    }
			} else {
			    for (int z = 1; z <= partitionNum; z++) {
				if (z == i || z == j)
				    continue;
				tmp.add(new Tuple2<Tuple3<Integer, Integer, Integer>, Tuple2<Integer, Integer>>(
					Utils.sort(i, j, z), new Tuple2<Integer, Integer>(edge1, edge2)));
			    }
			}
			return tmp.iterator();
		    }
		});

	JavaPairRDD<Tuple3<Integer, Integer, Integer>, Map<Integer, Set<Integer>>> pairs = inputGraph.mapToPair(
		new PairFunction<Tuple2<Tuple3<Integer, Integer, Integer>, Tuple2<Integer, Integer>>, Tuple3<Integer, Integer, Integer>, Map<Integer, Set<Integer>>>() {
		    private static final long serialVersionUID = 8841335909526357351L;

		    @Override
		    public Tuple2<Tuple3<Integer, Integer, Integer>, Map<Integer, Set<Integer>>> call(
			    Tuple2<Tuple3<Integer, Integer, Integer>, Tuple2<Integer, Integer>> tuple2)
			    throws Exception {
			Tuple3<Integer, Integer, Integer> key = tuple2._1;
			Tuple2<Integer, Integer> edge = tuple2._2;
			Map<Integer, Set<Integer>> value = new HashMap<>();
			Set<Integer> vertex1 = new HashSet<>();
			vertex1.add(edge._2);
			value.put(edge._1, vertex1);
			Set<Integer> vertex2 = new HashSet<>();
			vertex2.add(edge._1);
			value.put(edge._2, vertex2);
			return new Tuple2<Tuple3<Integer, Integer, Integer>, Map<Integer, Set<Integer>>>(key, value);
		    }
		});

	JavaPairRDD<Tuple3<Integer, Integer, Integer>, Map<Integer, Set<Integer>>> mapResultInput = pairs.reduceByKey(
		new Function2<Map<Integer, Set<Integer>>, Map<Integer, Set<Integer>>, Map<Integer, Set<Integer>>>() {
		    private static final long serialVersionUID = -6357330622704270977L;

		    @Override
		    public Map<Integer, Set<Integer>> call(Map<Integer, Set<Integer>> p1, Map<Integer, Set<Integer>> p2)
			    throws Exception {
			for (Map.Entry<Integer, Set<Integer>> entry : p2.entrySet()) {
			    if (p1.containsKey(entry.getKey())) {
				p1.get(entry.getKey()).addAll(entry.getValue());
			    } else {
				p1.put(entry.getKey(), entry.getValue());
			    }
			}
			return p1;
		    }
		});

	return mapResultInput;
    }

    private JavaPairRDD<Integer, Set<Tuple2<Integer, Integer>>> reduceStage(
	    JavaPairRDD<Tuple3<Integer, Integer, Integer>, Map<Integer, Set<Integer>>> mapResultInput) {
	JavaRDD<Tuple2<Integer, Set<Tuple2<Integer, Integer>>>> flatMap = mapResultInput.flatMap(
		new FlatMapFunction<Tuple2<Tuple3<Integer, Integer, Integer>, Map<Integer, Set<Integer>>>, Tuple2<Integer, Set<Tuple2<Integer, Integer>>>>() {
		    private static final long serialVersionUID = -1491530690173059414L;

		    @Override
		    public Iterator<Tuple2<Integer, Set<Tuple2<Integer, Integer>>>> call(
			    Tuple2<Tuple3<Integer, Integer, Integer>, Map<Integer, Set<Integer>>> input)
			    throws Exception {
			List<Tuple2<Integer, Set<Tuple2<Integer, Integer>>>> tmp = new ArrayList<>();
			EgoNet egoNet = Utils.constructEgoNet(input._2);
			for (Map.Entry<Integer, Set<Tuple2<Integer, Integer>>> entry : egoNet.result.entrySet()) {
			    tmp.add(new Tuple2<Integer, Set<Tuple2<Integer, Integer>>>(entry.getKey(),
				    entry.getValue()));
			}
			return tmp.iterator();
		    }
		});	
	
	JavaPairRDD<Integer, Set<Tuple2<Integer, Integer>>> pairRDD = flatMap.mapToPair(
		new PairFunction<Tuple2<Integer, Set<Tuple2<Integer, Integer>>>, Integer, Set<Tuple2<Integer, Integer>>>() {
		    private static final long serialVersionUID = -4370916093767390579L;

		    @Override
		    public Tuple2<Integer, Set<Tuple2<Integer, Integer>>> call(
			    Tuple2<Integer, Set<Tuple2<Integer, Integer>>> input) throws Exception {
			return new Tuple2<Integer, Set<Tuple2<Integer, Integer>>>(input._1, input._2);
		    }
		});
	
	JavaPairRDD<Integer, Set<Tuple2<Integer, Integer>>> egoNetAll = pairRDD.reduceByKey(
		new Function2<Set<Tuple2<Integer, Integer>>, Set<Tuple2<Integer, Integer>>, Set<Tuple2<Integer, Integer>>>() {
		    private static final long serialVersionUID = -8964883092335889951L;

		    @Override
		    public Set<Tuple2<Integer, Integer>> call(Set<Tuple2<Integer, Integer>> input1,
			    Set<Tuple2<Integer, Integer>> input2) throws Exception {
			input1.addAll(input2);
			return input1;
		    }
		});

	return egoNetAll;
    }

    public static void main(String[] args) throws IOException {
//	SparkJob job = new SparkJob("hdfs://192.168.130.15:9000/user/hadoop/xuyi/test.txt", "ego-net", 4039);
	SparkJob job = new SparkJob("input.edges", "ego-net", 148);
	job.init();
	job.start();
	job.stop();
    }
}
