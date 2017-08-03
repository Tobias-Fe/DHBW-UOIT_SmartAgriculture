public class getGrid {

	public static int findeGrid(double ilat, double ilon) {
		float lastLat = 0;
		float lastLon = 0;
		int x = 1;
		/* start at ,625 (0,5+0,125 to be at the middle of each grid) */
		/* started at a point south west of germany (lat: 47.625 | lon 5) */
		for (float lat = 47.625f; lat <= 55; lat += 0.25f) { // 55
			// x++;
			
			for (float lon = 5; lon <= 14; lon = lon + 0.25f) { // 14

				if (ilat >= lastLat &&ilat <= lat && ilon >= lastLon && ilon <= lon) {
					return x;
				}
				lastLon=lon;
				x++;
			}
			lastLat = lat;
		}

		return -1;
	}
}
