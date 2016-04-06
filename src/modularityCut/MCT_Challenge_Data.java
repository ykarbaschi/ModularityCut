package modularityCut;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class MCT_Challenge_Data extends SocialGraph {
    private static String[] lines;

    @Override
    public Map<Integer, Track> readData() {
        try {
            lines = Files.readAllLines(new File("MCT_Challenge//annotation//Dataset1//Cam1.dat")
                    .toPath()).toArray(new String[0]);
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }

        Scanner scanner;
        Map<Integer, Track> tracks = new HashMap<>();

        int frame;
        int x, y, ID;
        for (String line : lines) {
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

        return tracks;
    }
}
