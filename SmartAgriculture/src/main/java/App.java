import static spark.Spark.post;
import static spark.Spark.staticFiles;

import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;

public class App {

	public static void main(String[] args) {
		
		staticFiles.externalLocation("src/main/resources/public");

		post("/centerpol", (req, res) -> {
			String json = req.body();
			System.out.println(json);
			DocumentContext parsed = JsonPath.parse(json);
			double centerPointLat = parsed.read("$.centerPolResult[0]", Double.class);			
			double centerPointLng = parsed.read("$.centerPolResult[1]", Double.class);
			
			int gridID = getGrid.findeGrid(centerPointLat, centerPointLng);
			
			return "";
			//Martin an browser ein json zurück geben
		});
		
		//post("/")
	}
}

