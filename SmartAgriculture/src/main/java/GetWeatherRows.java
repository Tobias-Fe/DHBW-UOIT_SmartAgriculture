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
	private static JavaPairRDD<String, WetterInfo> wetterInfoMitEinemWetterProGridResult = null;
	public static volatile boolean running = true;

	//Liefert alle WetterInfos aus CSV
	private static JavaPairRDD<String, WetterInfo> gebeWetterInfos() throws InterruptedException {
		Logging.setLoggingDefaults();
		App.main(null);

		String fileName = "src/main/resources/wetterdaten-neu.csv";

		SparkConf conf = new SparkConf().setMaster("local[*]").setAppName("Smart Agriculture");
		JavaSparkContext sc = new JavaSparkContext(conf);

		SQLContext sqlContext = new SQLContext(sc);

		JavaRDD<Row> csvLines = sqlContext.read().format("com.databricks.spark.csv").option("inferSchema", "true")
				.option("header", "false").load(fileName).javaRDD();

		wetterInfoMitEinemWetterProGrid = csvLines.mapToPair(row -> {
			WetterInfo wetterInfo = new WetterInfo();
			wetterInfo.gridKey = "" + row.getInt(0);
			wetterInfo.datum = row.getString(1);
			//Formatierung des Datums
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
		
		wetterInfoMitEinemWetterProGridResult = wetterInfoMitEinemWetterProGrid
				.reduceByKey((a, b) -> a);
		
		wetterInfoMitEinemWetterProGridResult.foreach(tuple -> {
					System.out.println(tuple._1 + ": " + tuple._2 + " / ");
				});

		System.out.println("Looking at " + csvLines.count() + " data lines");

		//Benötigt, dass der SparkContext nicht beendet wird.
		while (running)
			Thread.sleep(1000);

		sc.close();
		return wetterInfoMitEinemWetterProGridResult;
	}

	//Liefert die WetterInfo für das übergebene Grid.
	public static WetterInfo getWetterInfoForGridID(int gridKey) {
		wetterInfoMitEinemWetterProGridResult.filter(el -> el._1.equals("1")).foreach(el -> System.out.println(el._2));
		
		JavaPairRDD<String, WetterInfo> filtered = wetterInfoMitEinemWetterProGridResult
				.filter(entry -> entry._1.equals("" + gridKey));
		System.out.println("Filtered.count(): " + filtered.count());
		if (filtered.count() == 1)
			return filtered.first()._2;

		return null;

	}

	public static void main(String[] args) throws InterruptedException {
		gebeWetterInfos();
		
	}
}
