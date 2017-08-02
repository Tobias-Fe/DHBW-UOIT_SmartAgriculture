import java.io.Serializable;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SQLContext;

import de.uniluebeck.itm.util.logging.Logging;
import scala.Tuple2;

public class Main {
	static JavaPairRDD<String, WetterInfo> wetterInfoMitEinemWetterProGrid = null;

	// CSV: ID, datum, lat, long, maxTemp, minTemp, Regenwahrscheinlichkeit in %
	public static class WetterInfo implements Serializable {
		@Override
		public String toString() {
			return gridKey + "," + datum + "," + lat + "," + lon
					+ "," + maxTemp + "," + minTemp + ","
					+ regenWahrscheinlichkeit + "," + resultDatum;
		}

		public String gridKey; // ID aus CSV
		public String datum;
		public double lat;
		public double lon;
		public double maxTemp;
		public double minTemp;
		public double regenWahrscheinlichkeit;
		public LocalDate resultDatum;
	}

	public static void main(String[] args) {
		Logging.setLoggingDefaults();

		// String fileName = "src/main/resources/testdatenwetter2.csv";
		// String fileName = "src/main/resources/sumo-sim-out.csv";
		// String fileName = "src/main/resources/test.csv"; //Should return 10 for
		// vehicle id 0

		String fileName = "src/main/resources/wetterdaten-neu.csv";

		SparkConf conf = new SparkConf().setMaster("local[*]").setAppName("Smart Agriculture");
		JavaSparkContext sc = new JavaSparkContext(conf);

		SQLContext sqlContext = new SQLContext(sc);

		JavaRDD<Row> csvLines = sqlContext.read().format("com.databricks.spark.csv").option("inferSchema", "true")
				.option("header", "false").load(fileName).javaRDD();

		// JavaRDD<Object> firstColumn = csvLines.map(row -> new Tuple7<>(row.get(0),
		// row.get(1), row.get(2), row.get(3),
		// row.get(4), row.get(5), row.get(6)));

		WetterInfo wetterInfo = new WetterInfo();
		wetterInfoMitEinemWetterProGrid = csvLines.mapToPair(row -> {
			wetterInfo.gridKey = "" + row.getInt(0);
			wetterInfo.datum = row.getString(1);

			DateTimeFormatter pattern = DateTimeFormatter.ofPattern("E MMM dd HH:mm:ss z yyyy", Locale.US);
			wetterInfo.resultDatum = LocalDate.parse(wetterInfo.datum, pattern);

			wetterInfo.lat = row.getDouble(2);
			wetterInfo.lon = row.getDouble(3);
			if (!row.isNullAt(4))
				wetterInfo.maxTemp = row.getDouble(4);
			if (!row.isNullAt(5))
				wetterInfo.minTemp = row.getDouble(5);
			if (!row.isNullAt(6))
				wetterInfo.regenWahrscheinlichkeit = row.getDouble(6);

			return new Tuple2<String, WetterInfo>(wetterInfo.gridKey, wetterInfo);
		})/*
			 * .reduceByKey((w1, w2) -> { return w1.resultDatum.isBefore(w2.resultDatum) ?
			 * w1 : w2; // return w1.resultDatum.compareTo(w2.resultDatum) ? w1 : w2; })
			 */;

		JavaPairRDD<String, WetterInfo> ausgabeReduced = wetterInfoMitEinemWetterProGrid.reduceByKey((w1, w2) -> {
			return w1.resultDatum.isBefore(w2.resultDatum) ? w1 : w2;
		});
		//

		wetterInfoMitEinemWetterProGrid.mapToPair(a -> new Tuple2<String, String>(a._1, a._2.toString())).reduceByKey((a, b) -> a)
				.foreach(tuple -> {
					System.out.println(tuple._2.split(",")); //array in denen die einzelen Elemente drin sind
					
					System.out.println(tuple._1 + ": " + tuple._2 + " / ");
//					System.out.println(tuple._2.split(",")[3]); Um eine Stelle aus dem Array zu bekommen
				});
		// ausgabeReduced.foreach(tuple -> System.out.println(tuple._1 + ": " +
		// tuple._2));

		// Action
		System.out.println("Looking at " + csvLines.count() + " data lines");

		// System.out.println(firstColumn.collect());
		// Auslesen der sumo-sim-out.csv
		// JavaPairRDD<Integer, Double> mapToPair =csvLines.mapToPair(row -> new
		// Tuple2<>(row.getInt(1), row.getDouble(9)));
		// JavaPairRDD<Integer, Double> reduceByKey = mapToPair.reduceByKey((a, b) -> a
		// + b);

		// Ausgabe
		// reduceByKey.foreach(tuple -> System.out.println(tuple._1 + ": " + tuple._2));

		sc.close();

	}

}
