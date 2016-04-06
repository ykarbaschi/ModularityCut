package modularityCut;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SocialGraph {

    public Map<Integer, Track> readData() {
        return new HashMap<>();
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
                    AdjMatrix[i][j] = 1;
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

        // weight according to the paper
        return Math.exp( -1 * upperDivision / (2 * getVariance(distances)));
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
                        (totalConnectionStrength.get(i) * totalConnectionStrength.get(j)) / 2 * totalMatrixStrength;
            }
        }
        return modularityMatrix;
    }
}
