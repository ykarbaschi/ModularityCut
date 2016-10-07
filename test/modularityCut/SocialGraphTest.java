package modularityCut;

import org.junit.Before;
import org.junit.Test;

import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class SocialGraphTest {
    SocialGraph socialGraph;

    @Before
    public void setUp() {
        socialGraph = new SocialGraph();
    }

    @Test
    public void canary() {
        assertTrue(true);
    }

    @Test
    public void getTwoTracksWithStartAndEndTimeThenCalculateWeightsOfTheEdge() {
        Tracks2D trackA = new Tracks2D();
        trackA.addPointData(new Point2D(1, 1, 1));
        trackA.addPointData(new Point2D(2, 2, 2));
        trackA.addPointData(new Point2D(3, 3, 3));

        Tracks2D trackB = new Tracks2D();
        trackB.addPointData(new Point2D(1, 3, 2));
        trackB.addPointData(new Point2D(4, 2, 3));
        trackB.addPointData(new Point2D(5, 3, 4));

        double result = socialGraph.calculateWeight(trackA, trackB);

        assertEquals(0.018315638888734165, result, 0);
    }

    @Test
    public void getFourTracksOfPeopleAndCreateAnAdjMatrix() {

        Tracks2D trackA = new Tracks2D();
        trackA.addPointData(new Point2D(1, 1, 1));
        trackA.addPointData(new Point2D(2, 2, 2));
        trackA.addPointData(new Point2D(3, 3, 3));

        Tracks2D trackB = new Tracks2D();
        trackB.addPointData(new Point2D(1, 3, 2));
        trackB.addPointData(new Point2D(4, 2, 3));
        trackB.addPointData(new Point2D(5, 3, 4));

        Tracks2D trackC = new Tracks2D();
        trackC.addPointData(new Point2D(5, 2, 4));
        trackC.addPointData(new Point2D(2, 26, 5));
        trackC.addPointData(new Point2D(36, 3, 6));

        Tracks2D trackD = new Tracks2D();
        trackD.addPointData(new Point2D(54, 24, 4));
        trackD.addPointData(new Point2D(24, 26, 5));
        trackD.addPointData(new Point2D(36, 33, 6));

        ArrayList<Track> tracks = new ArrayList<>();
        tracks.add(trackA);
        tracks.add(trackB);
        tracks.add(trackC);
        tracks.add(trackD);
        double[][] adjMatrix = socialGraph.calcAdjPositionMatrix(tracks);

        for (double[] weightRow : adjMatrix)
            for (double weight : weightRow)
                assertTrue(weight < 1 && weight > 0);
    }

    @Test
    public void getATrackWithOnePointAndCreateAdjMatrix() {
        Tracks2D trackA = new Tracks2D();
        trackA.addPointData(new Point2D(1, 1, 1));
        trackA.addPointData(new Point2D(2, 2, 2));
        trackA.addPointData(new Point2D(3, 3, 3));

        ArrayList<Track> tracks = new ArrayList();
        tracks.add(trackA);
        double[][] matrix = socialGraph.calcAdjPositionMatrix(tracks);
        assertEquals(1, matrix[0][0], 0);
    }

    @Test
    public void sendEmptyListToCalculateAdjacencyMatrix() {
        ArrayList<Track> tracks = new ArrayList();
        try {
            socialGraph.calcAdjPositionMatrix(tracks);
            fail("List is Empty");
        } catch (Exception e) {
        }
    }

    @Test
    public void getAdjMatrixAndCalculateTotalConnectionStrength() {

        Tracks2D trackA = new Tracks2D();
        trackA.addPointData(new Point2D(1, 1, 1));
        trackA.addPointData(new Point2D(2, 2, 2));
        trackA.addPointData(new Point2D(3, 3, 3));

        Tracks2D trackB = new Tracks2D();
        trackB.addPointData(new Point2D(1, 3, 2));
        trackB.addPointData(new Point2D(4, 2, 3));
        trackB.addPointData(new Point2D(5, 3, 4));

        Tracks2D trackC = new Tracks2D();
        trackC.addPointData(new Point2D(5, 2, 4));
        trackC.addPointData(new Point2D(2, 26, 5));
        trackC.addPointData(new Point2D(36, 3, 6));

        Tracks2D trackD = new Tracks2D();
        trackD.addPointData(new Point2D(54, 24, 4));
        trackD.addPointData(new Point2D(24, 26, 5));
        trackD.addPointData(new Point2D(36, 33, 6));
        ArrayList<Track> tracks = new ArrayList();
        tracks.add(trackA);
        tracks.add(trackB);
        tracks.add(trackC);
        tracks.add(trackD);
        double[][] adjMatrix = socialGraph.calcAdjPositionMatrix(tracks);

        List<Double> totalConnectionStrength = socialGraph.calculateTotalConnectionStrength(adjMatrix);
        assertEquals(4, totalConnectionStrength.size());
    }

    @Test
    public void getAdjMatrixAndCalculateTotalMatrixStrength() {
        Tracks2D trackA = new Tracks2D();
        trackA.addPointData(new Point2D(1, 1, 1));
        trackA.addPointData(new Point2D(2, 2, 2));
        trackA.addPointData(new Point2D(3, 3, 3));

        Tracks2D trackB = new Tracks2D();
        trackB.addPointData(new Point2D(1, 3, 2));
        trackB.addPointData(new Point2D(4, 2, 3));
        trackB.addPointData(new Point2D(5, 3, 4));

        Tracks2D trackC = new Tracks2D();
        trackC.addPointData(new Point2D(5, 2, 4));
        trackC.addPointData(new Point2D(2, 26, 5));
        trackC.addPointData(new Point2D(36, 3, 6));

        Tracks2D trackD = new Tracks2D();
        trackD.addPointData(new Point2D(54, 24, 4));
        trackD.addPointData(new Point2D(24, 26, 5));
        trackD.addPointData(new Point2D(36, 33, 6));
        ArrayList<Track> tracks = new ArrayList();
        tracks.add(trackA);
        tracks.add(trackB);
        tracks.add(trackC);
        tracks.add(trackD);
        double[][] adjMatrix = socialGraph.calcAdjPositionMatrix(tracks);

        double totalMatrixStrength = socialGraph.calculateTotalMatrixStrength(adjMatrix);

        assertEquals(2.3861950800601766, totalMatrixStrength, 0);
    }

    @Test
    public void getAdjMatrixAndOtherParamsCalculateModularityMatrixBij() {
        Tracks2D trackA = new Tracks2D();
        trackA.addPointData(new Point2D(1, 1, 1));
        trackA.addPointData(new Point2D(2, 2, 2));
        trackA.addPointData(new Point2D(3, 3, 3));

        Tracks2D trackB = new Tracks2D();
        trackB.addPointData(new Point2D(1, 3, 2));
        trackB.addPointData(new Point2D(4, 2, 3));
        trackB.addPointData(new Point2D(5, 3, 4));

        Tracks2D trackC = new Tracks2D();
        trackC.addPointData(new Point2D(5, 2, 4));
        trackC.addPointData(new Point2D(2, 26, 5));
        trackC.addPointData(new Point2D(36, 3, 6));

        Tracks2D trackD = new Tracks2D();
        trackD.addPointData(new Point2D(54, 24, 4));
        trackD.addPointData(new Point2D(24, 26, 5));
        trackD.addPointData(new Point2D(36, 33, 6));
        ArrayList<Track> tracks = new ArrayList();
        tracks.add(trackA);
        tracks.add(trackB);
        tracks.add(trackC);
        tracks.add(trackD);
        double[][] adjMatrix = socialGraph.calcAdjPositionMatrix(tracks);

        List<Double> totalConnectionStrength = socialGraph.calculateTotalConnectionStrength(adjMatrix);
        double totalMatrixStrength = socialGraph.calculateTotalMatrixStrength(adjMatrix);

        double[][] modularityMatrix = socialGraph.calculateModularityMatrix(adjMatrix,
                totalConnectionStrength, totalMatrixStrength);

        assertTrue(modularityMatrix[1][2] == modularityMatrix[2][1]);
    }
}
