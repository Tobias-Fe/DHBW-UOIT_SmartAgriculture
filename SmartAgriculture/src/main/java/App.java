import static spark.Spark.post;
import static spark.Spark.staticFiles;

import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;

import net.minidev.json.JSONObject;
import spark.Spark;

public class App {
	private static WetterInfo wetterInfoForGridID = null;

	public static void main(String[] args) {

		//Pfad zu externen Ressourcen
		staticFiles.externalLocation("src/main/resources/public");

		//Post-Request-Response
		post("/centerpol", (req, res) -> {
			//Bekomme JSON
			String json = req.body();
			System.out.println(json);
			//Parse JSON
			DocumentContext parsed = JsonPath.parse(json);

			System.out.println(parsed);
			//Lat vom Mittelpunkt des Grids
			double centerPointLat = parsed.read("$.centerPolResult[0]", Double.class);
			//Lng vom Mittelpunkt des Grids
			double centerPointLng = parsed.read("$.centerPolResult[1]", Double.class);

			//Berechnung der GridID
			int gridID = getGrid.findeGrid(centerPointLat, centerPointLng);
			//Bekomme WetterInfos zu GridID
			wetterInfoForGridID = GetWeatherRows.getWetterInfoForGridID(gridID);
			System.out.println("GridKey: " + wetterInfoForGridID.gridKey);
			double maxTemp = wetterInfoForGridID.maxTemp;
			System.out.println("MaxTemp: " + maxTemp);
			double minTemp = wetterInfoForGridID.minTemp;
			System.out.println("MinTemp: " + minTemp);
			double regenwahrscheinlichkeit = wetterInfoForGridID.regenWahrscheinlichkeit;
			if(Double.isNaN(regenwahrscheinlichkeit)){
				regenwahrscheinlichkeit = 0.0;
			}
			System.out.println("Regenwahrscheinlichkeit: " + regenwahrscheinlichkeit);
			//Erstellung des JSON-Result
			JSONObject antwort = new JSONObject();
			antwort.put("maxtemp", maxTemp);
			antwort.put("mintemp", minTemp);
			antwort.put("rain", regenwahrscheinlichkeit);

			return antwort;

		});

	}
}
