package modularityCut;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.*;

public class SocialGraph {

    public String[] readDataSet(String theAddress) {
        String[] lines = null;

        try {
            lines = Files.readAllLines(new File(theAddress)
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

        return tracks;
    }

    public Map<Integer, Track> interpretDataPETS(String[] theLines){
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

        return tracks;
    }

    public ArrayList<Track> convertToList(Map<Integer, Track> tracks) {
        // only for making an ordered List for all members in a map

        ArrayList<Track> result = new ArrayList<>();
        for (Map.Entry<Integer, Track> entry : tracks.entrySet()) {
            result.add(entry.getValue());
        }
        return result;
    }

    public double[][] createAdjacencyMatrix(List<Track> tracks) {

        if (tracks.size() == 0) throw new IllegalArgumentException("Map is Empty, Couldn't create Adj Matrix");

        int sizeOfAdjMatrix = tracks.size();
        double[][] AdjMatrix = new double[sizeOfAdjMatrix][sizeOfAdjMatrix];

        for (int i = 0; i < sizeOfAdjMatrix; i++) {
            for (int j = 0; j < sizeOfAdjMatrix; j++) {

                if(i == j)
                    // check to see if self similarity = 0 has any effects.
                    //AdjMatrix[i][j] = 0;
                    //AdjMatrix[i][j] = 1;
                    AdjMatrix[i][j] = Math.exp(1);
                else if (AdjMatrix[i][j] == 0 && haveOverlap(tracks.get(i), tracks.get(j))) {

                    AdjMatrix[i][j] = calculateWeight(tracks.get(i), tracks.get(j));
                    AdjMatrix[j][i] = AdjMatrix[i][j];
                }
            }
        }
        return AdjMatrix;
    }

    private boolean haveOverlap(Track trackA, Track trackB) {

        if (trackA.getPointData(0).getFrame() <= trackB.getPointData(trackB.length() - 1).getFrame() &&
                trackA.getPointData(trackA.length() - 1).getFrame() >= trackB.getPointData(0).getFrame()) return true;
        else return false;
    }

    public double calculateWeight(Track trackA, Track trackB) {

        if (trackA.length() == 0 || trackB.length() == 0) {
            throw new IllegalArgumentException("Both of the Tracks should have at least one point");
        }

        List<Double> distances = new ArrayList<>();
        double tempDistance;
        double upperDivision = 0;
        for (int i = 0; i < trackA.length(); i++) {
            for (int j = 0; j < trackB.length(); j++) {
                if (trackA.getPointData(i).getFrame() == trackB.getPointData(j).getFrame()) {
                    // I added tempDistance and List distances to calculate Variance using them later.
                    tempDistance = trackA.getDifferenceOfPosition(trackB, i, j);
                    distances.add(tempDistance);

                    // next line is used to be:
                    // upperDivision = upperDivision + Math.pow((trackA.getDifferenceOfPosition(trackB, i, j)), 2)
                    // but I deleted power 2. because we calculated the distance between two location using euclidean

                    upperDivision = upperDivision + tempDistance;
                }
            }
        }

        // weight according to the paper : Monitoring, Recognizing and Discovering Social Networks
        //return Math.exp( -1 * (upperDivision / (2 * getVariance(distances))));
        return Math.exp( 1- (upperDivision / (2 * getVariance(distances))));

        // weight according to the paper : Detection of Human Groups in Videos
        //return Math.exp( 1 - (upperDivision / (distances.size())));

        // check if -1 has wrong effects : it took forever to complete
        //return Math.exp(upperDivision / (2 * getVariance(distances)));

        //check if variance has wrong effect
        //return Math.exp(-1 * upperDivision);
    }

    private double getMean(List<Double> data) {
        double sum = 0.0;

        for(double a : data)
            sum += a;

        return sum / data.size();
    }

    private double getVariance(List<Double> data) {
        double mean = getMean(data);
        double temp = 0;

        for(double a :data)
            temp += Math.pow(mean - a, 2);

        return temp / data.size();
    }

    public List<Double> calculateTotalConnectionStrength(double[][] adjMatrix) {
        ArrayList<Double> result = new ArrayList<>();
        for (int i = 0; i < adjMatrix.length; i++) {
            double Ki = 0;
            for (int j = 0; j < adjMatrix.length; j++) {
                Ki = Ki + adjMatrix[i][j];
            }
            result.add(Ki);
        }
        return result;
    }

    public double calculateTotalMatrixStrength(double[][] adjMatrix) {
        double result = 0;
        for (int i = 0; i < adjMatrix.length; i++) {
            for (int j = 0; j < adjMatrix.length; j++) {
                result = result + adjMatrix[i][j];
            }
        }
        return result / 2;
    }

    public double[][] calculateModularityMatrix(
            double[][] adjMatrix, List<Double> totalConnectionStrength, double totalMatrixStrength) {

        double[][] modularityMatrix = new double[adjMatrix.length][adjMatrix.length];

        for (int i = 0; i < adjMatrix.length; i++) {
            for (int j = 0; j < adjMatrix.length; j++) {
                modularityMatrix[i][j] = adjMatrix[i][j] -
                        (totalConnectionStrength.get(i) * totalConnectionStrength.get(j)) / (2 * totalMatrixStrength);
            }
        }
        return modularityMatrix;
    }
}
