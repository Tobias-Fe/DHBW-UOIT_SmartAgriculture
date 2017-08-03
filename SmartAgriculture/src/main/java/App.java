import static spark.Spark.post;
import static spark.Spark.staticFiles;

import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;

import net.minidev.json.JSONObject;
import spark.Spark;

public class App {
	private static WetterInfo wetterInfoForGridID = null;

	public static void main(String[] args) {

		staticFiles.externalLocation("src/main/resources/public");

		post("/centerpol", (req, res) -> {
			String json = req.body();
			System.out.println(json);
			DocumentContext parsed = JsonPath.parse(json);

			System.out.println(parsed);
			//double centerPointLat = parsed.read("$.centerPolResult[0]", Double.class);
			//double centerPointLng = parsed.read("$.centerPolResult[1]", Double.class);

			int gridId = 1;
			// wetterInfoForGridID = GetWeatherRows.getWetterInfoForGridID(gridId);
			 double maxTemp = /*wetterInfoForGridID.maxTemp*/ 20.0;
			 System.out.println(maxTemp);
			 double minTemp = /*wetterInfoForGridID.minTemp*/ 10.0;
			System.out.println(minTemp);
			 double regenwahrscheinlichkeit = /*wetterInfoForGridID.regenWahrscheinlichkeit*/ 5.0;

			 JSONObject antwort = new JSONObject();
			 antwort.put("maxtemp", maxTemp);
			 antwort.put("mintemp", minTemp);
			 antwort.put("rain", regenwahrscheinlichkeit);

			// System.out.println(wetterInfoForGridID.minTemp);

			// return wetterInfoForGridID.minTemp;
			// Martin an browser ein json zurück geben

			// int gridID = getGrid.findeGrid(centerPointLat, centerPointLng);

			return antwort;
			// Martin an browser ein json zurück geben
//			double centerPointLat = parsed.read("$.centerPolResult[0]", Double.class);			
//			double centerPointLng = parsed.read("$.centerPolResult[1]", Double.class);
			
//			System.out.println(wetterInfoForGridID.minTemp);
			

			//Martin an browser ein json zurück geben

//			int gridID = getGrid.findeGrid(centerPointLat, centerPointLng);
			
//			wetterInfoForGridID = GetWeatherRows.getWetterInfoForGridID(gridID);
//			System.out.println(wetterInfoForGridID.maxTemp);
			//return wetterInfoForGridID.minTemp;
			
	
			//Martin an browser ein json zurück geben

		});

//		staticFiles.externalLocation("src/main/resources/public");
//		Spark.post("/waypoints", (req, res) -> {
//			System.out.println("Received Post Request...");
//			Gson gson = new Gson();
//
//			FromWebbrowser fromWebbrowser = gson.fromJson(req.body(), FromWebbrowser.class);
//
//			ToWebbrowser toWebBrowser = new ToWebbrowser();
//			toWebBrowser.waypoints = new LatLonDanger[tmp.size()];
//
//			return gson.toJson(routePointsToWeb);
//		});

		// post("/")
	}
}
