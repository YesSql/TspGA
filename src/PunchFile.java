import java.io.*;
import java.util.*;
import java.text.*;

class PunchFile {
	int[][] locations;

	String FilePath;

    public PunchFile(String FilePath) throws IOException {
        BufferedReader in = new BufferedReader(new FileReader(FilePath));

        String currentLine = null;

        List<int[]> points = new ArrayList<int[]>();
        while(true) {
            currentLine = in.readLine();

            if(currentLine == null) {
                break;
            }

            String[] pointsString = currentLine.split(",");
            double x = Double.parseDouble(pointsString[0]);
            double y = Double.parseDouble(pointsString[1]);

            points.add(new int[] {(int)(x*10000), (int)(y*10000)});

        }

        locations = new int[points.size()][2];
        int location =0;
        for(int[] set : points) {
            locations[location][0] = set[0];
            locations[location][1] = set[1];
            location++;
        }
	}

	public int[][] getLocations() {
		return locations;
	}

    public int getLocationCount() {
        return locations.length;
    }

	public void setLocations (int[][] locations) {
		this.locations = locations;
	}

	public void SaveFile(String path) {

	}
}