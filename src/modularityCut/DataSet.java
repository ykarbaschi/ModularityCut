package modularityCut;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class DataSet {
    String address;
    int startFrameNumber;
    int endFrameNumber;

    public DataSet(String theAddress){
        address = theAddress;
    }

    public String[] readDataSet() {
        String[] lines = null;

        try {
            lines = Files.readAllLines(new File(address)
                    .toPath()).toArray(new String[0]);
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }

        return lines;
    }

    public Map<Integer, Track> interpretDataMCTChallenge(String[] theLines){
        Scanner scanner;
        Map<Integer, Track> tracks = new HashMap<>();

        int frame;
        int x, y, ID;
        for (String line : theLines) {
            scanner = new Scanner(line);
            scanner.nextInt();
            frame = scanner.nextInt();
            ID = scanner.nextInt();
            x = scanner.nextInt();
            y = scanner.nextInt();

            if (!tracks.containsKey(ID)) {
                Tracks2D track2D = new Tracks2D(ID, 0);
                track2D.addPointData(new Point2D(x, y, frame));
                tracks.put(ID, track2D);
            } else {
                Track track2D = tracks.get(ID);
                track2D.addPointData(new Point2D(x, y, frame));
                tracks.put(ID, track2D);
            }
        }

        // set start and end frame number
        String temp = theLines[0];
        scanner = new Scanner(temp);
        scanner.nextInt();
        startFrameNumber = scanner.nextInt();

        temp = theLines[theLines.length -1];
        scanner = new Scanner(temp);
        scanner.nextInt();
        endFrameNumber = scanner.nextInt();
        //******************************

        return tracks;
    }

    public Map<Integer, Track> interpretDataMOTChallenge(String[] theLines){
        Scanner scanner;
        Map<Integer, Track> tracks = new HashMap<>();

        int frame;
        int x, y, ID;
        for (String line : theLines) {
            scanner = new Scanner(line).useDelimiter(",");
            frame = scanner.nextInt();
            ID = scanner.nextInt();
            x = scanner.nextInt();
            y = scanner.nextInt();

            if (!tracks.containsKey(ID)) {
                Tracks2D track2D = new Tracks2D(ID, 0);
                track2D.addPointData(new Point2D(x, y, frame));
                tracks.put(ID, track2D);
            } else {
                Track track2D = tracks.get(ID);
                track2D.addPointData(new Point2D(x, y, frame));
                tracks.put(ID, track2D);
            }
        }

        // set start and end frame number
        String temp = theLines[0];
        scanner = new Scanner(temp).useDelimiter(",");
        startFrameNumber = scanner.nextInt();

        temp = theLines[theLines.length -1];
        scanner = new Scanner(temp).useDelimiter(",");
        endFrameNumber = scanner.nextInt();
        //******************************

        return tracks;
    }
}
