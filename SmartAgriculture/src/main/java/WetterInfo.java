import java.io.Serializable;
import java.time.LocalDate;

// CSV: ID, datum, lat, long, maxTemp, minTemp, Regenwahrscheinlichkeit in %
//Generate one WetterInfo per CSV Line
public class WetterInfo implements Serializable {
	@Override
	public String toString() {
		return gridKey + "," + datum + "," + lat + "," + lon + "," + maxTemp + "," + minTemp + ","
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