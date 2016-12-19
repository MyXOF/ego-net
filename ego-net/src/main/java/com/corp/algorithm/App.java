package com.corp.algorithm;

import scala.Tuple2;
import scala.Tuple3;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.PairFunction;

public final class App {
	// private static final String SPACE = " ";
	private static final String SPILTOR = ",";
	private static final int PARTIITON_NUM = 10;

	public static int caculateHashcode(int number) {
		return number % PARTIITON_NUM;
	}

	public static void main(String[] args) throws Exception {
		JavaSparkContext context = new JavaSparkContext(new SparkConf().setAppName("word count"));
		JavaRDD<String> textFile = context.textFile("LICENSE");

		JavaPairRDD<Tuple3<Integer, Integer, Integer>, Tuple2<Integer, Integer>> inputGraph = textFile
				.mapToPair(new PairFunction<String, Tuple3<Integer, Integer, Integer>, Tuple2<Integer, Integer>>() {

					@Override
					public Tuple2<Tuple3<Integer, Integer, Integer>, Tuple2<Integer, Integer>> call(String line) throws Exception {
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
									return new Tuple2<Tuple3<Integer, Integer, Integer>, Tuple2<Integer, Integer>>(
											new Tuple3<Integer, Integer, Integer>(i, z, w),
											new Tuple2<Integer, Integer>(edge1, edge2));
								}
							}
						} else {
							for (int z = 1; z <= PARTIITON_NUM; z++) {
								if (z == i || z == j)
									continue;
								return new Tuple2<Tuple3<Integer, Integer, Integer>, Tuple2<Integer, Integer>>(
										new Tuple3<Integer, Integer, Integer>(i, j, z),
										new Tuple2<Integer, Integer>(edge1, edge2));
							}
						}
						return null;
					}

				});

		// JavaRDD<String> words = textFile.flatMap(new FlatMapFunction<String,
		// String>() {
		//
		// @Override
		// public Iterator<String> call(String s) throws Exception {
		// return Arrays.asList(s.split(SPACE)).iterator();
		// }
		//
		// });
		//
		//
		// JavaPairRDD<String, Integer> pairs = words.mapToPair(new
		// PairFunction<String, String, Integer>() {
		// public Tuple2<String, Integer> call(String s) {
		// return new Tuple2<String, Integer>(s, 1);
		// }
		// });
		// JavaPairRDD<String, Integer> counts = pairs.reduceByKey(new
		// Function2<Integer, Integer, Integer>() {
		// public Integer call(Integer a, Integer b) {
		// return a + b;
		// }
		// });
		// counts.saveAsTextFile("out");
		//
		// List<Tuple2<String, Integer>> output = counts.collect();
		// for (Tuple2<?, ?> tuple : output) {
		// System.out.println(tuple._1() + ": " + tuple._2());
		// }
		context.stop();
	}
}
