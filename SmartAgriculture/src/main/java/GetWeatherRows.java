import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SQLContext;

import de.uniluebeck.itm.util.logging.Logging;
import scala.Tuple2;

public class GetWeatherRows {
	private static JavaPairRDD<String, WetterInfo> wetterInfoMitEinemWetterProGrid = null;
	

	private static JavaPairRDD<String, WetterInfo> gebeWetterInfos() {
		Logging.setLoggingDefaults();

		String fileName = "src/main/resources/wetterdaten-neu.csv";

		SparkConf conf = new SparkConf().setMaster("local[*]").setAppName("Smart Agriculture");
		JavaSparkContext sc = new JavaSparkContext(conf);

		SQLContext sqlContext = new SQLContext(sc);

		JavaRDD<Row> csvLines = sqlContext.read().format("com.databricks.spark.csv").option("inferSchema", "true")
				.option("header", "false").load(fileName).javaRDD();

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
		});

		wetterInfoMitEinemWetterProGrid.mapToPair(a -> new Tuple2<String, String>(a._1, a._2.toString()))
				.reduceByKey((a, b) -> a).foreach(tuple -> {
					// System.out.println(tuple._2.split(",")); //array in denen die einzelen
					// Elemente drin sind
					// System.out.println(tuple._1 + ": " + tuple._2 + " / ");
					// System.out.println(tuple._2.split(",")[3]); Um eine Stelle aus dem Array zu
					// bekommen
					double[] grid = { Integer.parseInt(tuple._2.split(",")[0]),
							Double.parseDouble(tuple._2.split(",")[2]), Double.parseDouble(tuple._2.split(",")[3]) };
					System.out.println("ID: " + grid[0] + " lat: " + grid[1] + " lon: " + grid[2]);
					// some
				});

		System.out.println("Looking at " + csvLines.count() + " data lines");

		sc.close();
		return wetterInfoMitEinemWetterProGrid;
	}

	public static WetterInfo getWetterInfoForGridID(int gridId) {
		JavaPairRDD<String, WetterInfo> filtered = wetterInfoMitEinemWetterProGrid
				.filter(entry -> entry._1.equals("" + gridId));

		if (filtered.count() == 1)
			return filtered.first()._2;

		return null;
	}

	public static void main(String[] args) {
		gebeWetterInfos();
	}
}
