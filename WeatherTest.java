import java.io.IOException;
import java.io.PrintWriter;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import org.json.JSONException;

import net.aksingh.owmjapis.DailyForecast;
import net.aksingh.owmjapis.OpenWeatherMap;

public class WeatherTest {
	public static final void main(String[] args) throws IOException, JSONException {
		
		FileWriter fw = new FileWriter("generated.csv", true);
		BufferedWriter bw = new BufferedWriter(fw);
		StringBuilder sb = new StringBuilder();
		
		BufferedReader br = new BufferedReader(new FileReader("generated.csv"));     
		if (br.readLine() != null) {
		    PrintWriter clear = new PrintWriter("generated.csv");
		    clear.print("");
		    clear.close();
		    String a = br.readLine();
		}
		br.close();
		
      
		boolean isMetric = true;
		String owmApiKey = "abe51a1df90d73ffde7e428c68da2f45"; /* YOUR OWM API KEY HERE */
		byte forecastDays = 3;
		OpenWeatherMap.Units units = (isMetric)
        ? OpenWeatherMap.Units.METRIC
        : OpenWeatherMap.Units.IMPERIAL;
		OpenWeatherMap owm = new OpenWeatherMap(units, owmApiKey);	
		
		try {
			int x = 0;
			/* start at ,625 (0,5+0,125 to be at the middle of each grid) */
			/* started at a point south west of germany (lat: 47.625 | lon 5) */ 	
			for(float lat= 47.625f;lat<=55;lat+=0.25f){
				x++;
				for(float lon=5;lon<=14;lon=lon+0.25f){
					DailyForecast forecast2 = owm.dailyForecastByCoordinates(lat, lon, forecastDays);
					int numForecasts = forecast2.getForecastCount();
					x++;
					if(lon>=14){
						lon=5;
		      			break;
		   			}	
				    
					for (int i = 0; i < numForecasts; i++) {
						DailyForecast.Forecast dayForecast = forecast2.getForecastInstance(i);
				        DailyForecast.Forecast.Temperature temperature = dayForecast.getTemperatureInstance();
				        // System.out.println(lat + " / " + lon);
				        // append by ID
				        sb.append(x);
				        sb.append(',');
				        // append by Date and Time
				        sb.append(dayForecast.getDateTime());
				        sb.append(',');
				        // append by latitute
				        sb.append(lat);
				        sb.append(',');
				        // append by longitute
				        sb.append(lon);
				        sb.append(',');
				        // append by maximum Temperature
				        sb.append(temperature.getMaximumTemperature());
				        sb.append(',');
				        // append by minimum Temperature
				        sb.append(temperature.getMinimumTemperature());
				        sb.append(',');
				        // append by Rain Chance and New-Line command
				        sb.append(dayForecast.getRain());
				        sb.append('\n');
					}
				}
			}
		// Write the appended String and Close it to fill next one
		bw.write(sb.toString());
		bw.close();
		}
		
		catch (IOException e) {
			e.printStackTrace();
		}
	}
}