package modularityCut;

import java.io.*;
import java.nio.file.Files;
import java.util.*;

public class SocialGraph {

    public enum WeightCalculation{OnlyPosition, PositionVelocityDirection}

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

    public double[][] calcAdjPositionMatrix(List<Track> tracks) {

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

    public double[][] createAdjMatrixPosVelocityDir(List<Track> tracks){
        if (tracks.size() == 0) throw new IllegalArgumentException("Map is Empty, Couldn't create Adj Matrix");

        double[][] posAdjMatrix = calcAdjPositionMatrix(tracks);
        double[][] velocityAdjMatrix = calcAdjVelocityMatrix(tracks);
        double[][] directionAdjMatrix = calcAdjDirMatrix(tracks);

        double[] avgVelocity = calcAvgVelocity(tracks);

        //double Cv = calcCv(avgVelocity);
        double Cv = 0.1;

        int sizeOfAdjMatrix = tracks.size();
        double[][] AdjMatrix = new double[sizeOfAdjMatrix][sizeOfAdjMatrix];

        for (int i = 0; i < sizeOfAdjMatrix; i++) {
            for (int j = 0; j < sizeOfAdjMatrix; j++) {

                if (i == j) AdjMatrix[i][j] = Math.exp(1) * Math.exp(1);
                else if (avgVelocity[i] > Cv && avgVelocity[j] > Cv && AdjMatrix[i][j] == 0) {

                    AdjMatrix[i][j] = posAdjMatrix[i][j] * velocityAdjMatrix[i][j] * directionAdjMatrix[i][j];
                    AdjMatrix[j][i] = AdjMatrix[i][j];
                } else if (AdjMatrix[i][j] == 0) {

                    AdjMatrix[i][j] = posAdjMatrix[i][j] * velocityAdjMatrix[i][j];
                    AdjMatrix[j][i] = AdjMatrix[i][j];
                }
            }

        }
        return AdjMatrix;
    }

    private double[][] calcAdjDirMatrix(List<Track> tracks) {
        if (tracks.size() == 0) throw new IllegalArgumentException("Map is Empty, Couldn't create Adj Matrix");

        TrackPoint[] direction = calcDirection(tracks);

        int sizeOfAdjMatrix = tracks.size();
        double[][] directionAdjMatrix = new double[sizeOfAdjMatrix][sizeOfAdjMatrix];

        for (int i = 0; i < sizeOfAdjMatrix; i++) {
            for (int j = 0; j < sizeOfAdjMatrix; j++) {

                if(i == j)
                    // The magniutude is 1 because they are unit length
                    directionAdjMatrix[i][j] = 1;
                else if (directionAdjMatrix[i][j] == 0) {

                    directionAdjMatrix[i][j] = direction[i].getX() * direction[j].getX() +
                            direction[i].getY() * direction[j].getY();
                    directionAdjMatrix[j][i] = directionAdjMatrix[i][j];
                }
            }
        }
        return directionAdjMatrix;
    }

    private TrackPoint[] calcDirection(List<Track> tracks) {
        int sizeOfAdjMatrix = tracks.size();
        TrackPoint[] result = new TrackPoint[sizeOfAdjMatrix];

        for (int i = 0; i < sizeOfAdjMatrix; i++) {
            Track theTrack = tracks.get(i);
            int numberOfPoints = theTrack.length();
            result[i] = (theTrack.getDirection(theTrack, 0, numberOfPoints - 1));
        }
        return result;
    }

    public double[][] calcAdjVelocityMatrix(List<Track> tracks) {

        if (tracks.size() == 0) throw new IllegalArgumentException("Map is Empty, Couldn't create Adj Matrix");

        double[] avgVelocity = calcAvgVelocity(tracks);

        double Cv = calcCv(avgVelocity);

        int sizeOfAdjMatrix = tracks.size();
        double[][] velocityAdjMatrix = new double[sizeOfAdjMatrix][sizeOfAdjMatrix];

        for (int i = 0; i < sizeOfAdjMatrix; i++) {
            for (int j = 0; j < sizeOfAdjMatrix; j++) {

                if(i == j)
                    velocityAdjMatrix[i][j] = Math.exp(1);
                else if (velocityAdjMatrix[i][j] == 0) {

                    velocityAdjMatrix[i][j] = Math.exp(1-(Math.abs(avgVelocity[i] - avgVelocity[j])/ Cv));
                    velocityAdjMatrix[j][i] = velocityAdjMatrix[i][j];
                }
            }
        }
        return velocityAdjMatrix;
    }

    private double calcCv(double[] avgVelocity) {
        double result = 0;
        for (int i = 0; i < avgVelocity.length; i++) {
            result = result + avgVelocity[i];
        }
        return result / avgVelocity.length;
    }

    private double[] calcAvgVelocity(List<Track> tracks) {
        int sizeOfAdjMatrix = tracks.size();
        double[] velocityMatrix = new double[sizeOfAdjMatrix];

        for (int i = 0; i < sizeOfAdjMatrix; i++) {
            Track theTrack = tracks.get(i);
            int numberOfPoints = theTrack.length();
            velocityMatrix[i] = (theTrack.getDifferenceOfPosition(theTrack, 0, numberOfPoints - 1)) / numberOfPoints;
        }

        return velocityMatrix;
    }

    public void ExportAdjMatrix(double[][] adjMatrix) {
        Writer edgeWriter = null;
        Writer nodeWriter = null;
        try {
            edgeWriter = new BufferedWriter(new OutputStreamWriter(
                    new FileOutputStream("Edges.csv"), "utf-8"));

            //edgeWriter.write("Source;Target;Type;Weight\n");
            edgeWriter.write("Source;Target;Weight\n");

            nodeWriter = new BufferedWriter(new OutputStreamWriter(
                    new FileOutputStream("Nodes.csv"), "utf-8"));

            nodeWriter.write("ID\n");

            int numberOfNodes = adjMatrix.length;
            for (int i = 0; i < numberOfNodes; i++) {
                nodeWriter.write(i + "\n");
                for (int j = i; j < numberOfNodes; j++) {
                    edgeWriter.write("" + i);
                    //edgeWriter.write(";" + j);
                    edgeWriter.write(" " + j);
                    //edgeWriter.write(";Undirected");
                    //edgeWriter.write(";" + adjMatrix[i][j] + "\n");
                    edgeWriter.write(" " + adjMatrix[i][j] + "\n");
                }
            }
        } catch (IOException e) {
        } finally {
            try {
                edgeWriter.close();
                nodeWriter.close();
            } catch (Exception ex) {
            }
        }
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
        //Added to calculate Variance based on Squared Distances.
        List<Double> squaredDistances = new ArrayList<>();

        double tempDistance;
        double upperDivision = 0;
        double squaredUpperDivision = 0;
        for (int i = 0; i < trackA.length(); i++) {
            for (int j = 0; j < trackB.length(); j++) {
                if (trackA.getPointData(i).getFrame() == trackB.getPointData(j).getFrame()) {
                    // I added tempDistance and List distances to calculate Variance using them later.
                    tempDistance = trackA.getDifferenceOfPosition(trackB, i, j);
                    distances.add(tempDistance * tempDistance);
                    //Added to calculate Variance based on Squared Distances.
                    squaredDistances.add(tempDistance);

                    // next line is used to be:
                    // upperDivision = upperDivision + Math.pow((trackA.getDifferenceOfPosition(trackB, i, j)), 2)
                    // but I deleted power 2. because we calculated the distance between two location using euclidean

                    upperDivision = upperDivision + tempDistance * tempDistance;

                    squaredUpperDivision = squaredUpperDivision + tempDistance;
                }
            }
        }

        // weight according to the paper : Monitoring, Recognizing and Discovering Social Networks
        //return Math.exp( -1 * (upperDivision / (2 * getVariance(distances))));
        //return Math.exp( -1 * (squaredUpperDivision / (2 * getVariance(squaredDistances))));
        //double diff = distances.get(0) - (distances.get(distances.size()-1));
        //return Math.exp( -1 * (upperDivision / (2 * Math.pow(diff, 2))));
        //return Math.exp( 1 - (upperDivision / (2 * getVariance(distances))));

        // weight according to the paper : Detection of Human Groups in Videos
        //return Math.exp( 1 - (squaredUpperDivision / (distances.size() * (330.0 / 360.0))));
        return Math.exp( 1 - (squaredUpperDivision / (distances.size() * 100)));

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
