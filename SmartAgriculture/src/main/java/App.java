import static spark.Spark.post;
import static spark.Spark.staticFiles;

import org.apache.spark.api.java.JavaPairRDD;

import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;

public class App {
	private static WetterInfo wetterInfoForGridID = null;

	public static void main(String[] args) {
		
		
		staticFiles.externalLocation("src/main/resources/public");

		post("/centerpol", (req, res) -> {
			String json = req.body();
			System.out.println(json);
			DocumentContext parsed = JsonPath.parse(json);
			double centerPointLat = parsed.read("$.centerPolResult[0]", Double.class);			
			double centerPointLng = parsed.read("$.centerPolResult[1]", Double.class);
			
			int gridId = 1;
			wetterInfoForGridID = GetWeatherRows.getWetterInfoForGridID(gridId);
			
//			System.out.println(wetterInfoForGridID.minTemp);
			
			//return wetterInfoForGridID.minTemp;
			//Martin an browser ein json zurück geben

			int gridID = getGrid.findeGrid(centerPointLat, centerPointLng);
			
			return "";
			//Martin an browser ein json zurück geben
		});
		
		//post("/")
	}
}

