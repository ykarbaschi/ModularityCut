package modularityCut;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SocialGraph {

    public Map<Integer, Track> readData(){return new HashMap<>();}

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

        System.out.println("***YASER..Correct Standard Deviation in denominator For Weights Calculation");
        int sizeOfAdjMatrix = tracks.size();
        double[][] AdjMatrix = new double[sizeOfAdjMatrix][sizeOfAdjMatrix];

        for (int i = 0; i < sizeOfAdjMatrix; i++) {
            for (int j = 0; j < sizeOfAdjMatrix; j++) {

                if (AdjMatrix[i][j] == 0 && haveOverlap(tracks.get(i), tracks.get(j))) {

                    AdjMatrix[i][j] = calculateWeight(tracks.get(i), tracks.get(j), 1);
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

    public double calculateWeight(Track trackA, Track trackB, double scaleFactor) {

        if (trackA.length() == 0 || trackB.length() == 0) {
            throw new IllegalArgumentException("Both of the Tracks should have at least one point");
        }

        double upperDivision = 0;
        for (int i = 0; i < trackA.length(); i++) {
            for (int j = 0; j < trackB.length(); j++) {
                if (trackA.getPointData(i).getFrame() == trackB.getPointData(j).getFrame()) {
                    // in next line I should change... have a method to get the difference for location because
                    // maybe the data may change from 2D to 3D and I dont want to change other class each time the
                    // format of data changed

                    // I will add a method in TrackPoint to calculate difference for each two point
                    System.out.println("check the formula to calculate the difference");

                    upperDivision = upperDivision + Math.pow((trackA.getDifferenceOfPosition(trackB, i, j)), 2);
                }
            }
        }
        return Math.exp(-upperDivision);
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
