package modularityCut;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class ModularityMeasureTest {
    ModularityMeasure modularityMeasure;
    double[][] modularityMatrix;
    double totalMatrixStrength;

    @Before
    public void setUp() {

        SocialGraph socialGraph = new SocialGraph();
        Tracks2D trackA = new Tracks2D(0, 0);
        trackA.addPointData(new Point2D(1, 1, 1));
        trackA.addPointData(new Point2D(2, 2, 2));
        trackA.addPointData(new Point2D(3, 3, 3));

        Tracks2D trackB = new Tracks2D(1, 0);
        trackB.addPointData(new Point2D(1, 3, 2));
        trackB.addPointData(new Point2D(4, 2, 3));
        trackB.addPointData(new Point2D(5, 3, 4));

        Tracks2D trackC = new Tracks2D(2, 0);
        trackC.addPointData(new Point2D(5, 2, 4));
        trackC.addPointData(new Point2D(2, 26, 5));
        trackC.addPointData(new Point2D(36, 3, 6));

        Tracks2D trackD = new Tracks2D(3, 0);
        trackD.addPointData(new Point2D(54, 24, 4));
        trackD.addPointData(new Point2D(24, 26, 5));
        trackD.addPointData(new Point2D(36, 33, 6));
        HashMap<Integer, Track> tracks = new HashMap<>();
        tracks.put(0, trackA); tracks.put(1, trackB); tracks.put(2, trackC); tracks.put(3, trackD);

        double[][] adjMatrix = socialGraph.createAdjacencyMatrix(socialGraph.convertToList(tracks));

        modularityMeasure = new ModularityMeasure(tracks);

        List<Double> totalConnectionStrength = socialGraph.calculateTotalConnectionStrength(adjMatrix);
        totalMatrixStrength = socialGraph.calculateTotalMatrixStrength(adjMatrix);

        modularityMatrix = socialGraph.calculateModularityMatrix(adjMatrix,
                totalConnectionStrength, totalMatrixStrength);
    }
}
