package modularityCut;

import java.io.*;
import java.util.*;

class TimeFrameInfo {
    int start;
    int end;
    double[][] Adj;
}

public class SocialGraph {
    int frameWindow, beginningFrame, lastFrame;

    public SocialGraph(int theFrameWindow, int theStart, int theEnd) {
        frameWindow = theFrameWindow;
        beginningFrame = theStart;
        lastFrame = theEnd;
    }

    public ArrayList calcAllAdjMatrix(List<Track> tracks, boolean dirVelocity) {

        ArrayList<TimeFrameInfo> result = new ArrayList<>();

        for (int i = beginningFrame; i < lastFrame; i += frameWindow) {
            TimeFrameInfo info = new TimeFrameInfo();
            info.start = i;

            if (i + frameWindow > lastFrame)
                info.end = lastFrame;
            else
                info.end = i + frameWindow - 1;

            info.Adj = createAdjMatrix(tracks, info.start, info.end, dirVelocity);
            result.add(info);
        }

        return result;
    }

    public ArrayList<Track> convertToList(Map<Integer, Track> tracks) {
        // only for making an ordered List for all members in a map

        ArrayList<Track> result = new ArrayList<>();
        for (Map.Entry<Integer, Track> entry : tracks.entrySet()) {
            result.add(entry.getValue());
        }
        return result;
    }

    public double[][] calcAdjPositionMatrix(List<Track> tracks, int startFrame, int endFrame) {

        if (tracks.size() == 0) throw new IllegalArgumentException("Map is Empty, Couldn't create Adj Matrix");

        int sizeOfAdjMatrix = tracks.size();
        double[][] AdjMatrix = new double[sizeOfAdjMatrix][sizeOfAdjMatrix];

        for (int i = 0; i < sizeOfAdjMatrix; i++) {
            for (int j = 0; j < sizeOfAdjMatrix; j++) {
                if (i == j)
                    // check to see if self similarity = 0 has any effects.
                    //AdjMatrix[i][j] = 0;
                    //AdjMatrix[i][j] = 1;
                    AdjMatrix[i][j] = Math.exp(1);
                else if (AdjMatrix[i][j] == 0 && haveOverlap(tracks.get(i), tracks.get(j))) {
                    int[] commonFrames = calculateCommonFrames(tracks.get(i), tracks.get(j));
                    if (haveOverlapWithStartFrame(commonFrames, startFrame, endFrame)) {

                        AdjMatrix[i][j] = calculateWeight(tracks.get(i), tracks.get(j), startFrame, endFrame);
                        AdjMatrix[j][i] = AdjMatrix[i][j];
                    }
                }
            }
        }
        return AdjMatrix;
    }

    public double[][] calcAdjPositionMatrix(List<Track> tracks) {

        if (tracks.size() == 0) throw new IllegalArgumentException("Map is Empty, Couldn't create Adj Matrix");

        int sizeOfAdjMatrix = tracks.size();
        double[][] AdjMatrix = new double[sizeOfAdjMatrix][sizeOfAdjMatrix];

        for (int i = 0; i < sizeOfAdjMatrix; i++) {
            for (int j = 0; j < sizeOfAdjMatrix; j++) {
                if (i == j)
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

    public double[][] createAdjMatrix(List<Track> tracks, int startFrame, int endFrame, boolean dirVelocity) {
        if (tracks.size() == 0) throw new IllegalArgumentException("Map is Empty, Couldn't create Adj Matrix");

        double[][] posAdjMatrix = calcAdjPositionMatrix(tracks, startFrame, endFrame);

        double[][] velocityAdjMatrix = new double[tracks.size()][tracks.size()];
        double[][] directionAdjMatrix = new double[tracks.size()][tracks.size()];
        ;
        if (dirVelocity) {
            velocityAdjMatrix = calcAdjVelocityMatrix(tracks, startFrame, endFrame);
            directionAdjMatrix = calcAdjDirMatrix(tracks, startFrame, endFrame);
        } else {
            for (int i = 0; i < tracks.size(); i++) {
                for (int j = 0; j < tracks.size(); j++) {
                    velocityAdjMatrix[i][j] = 1;
                    directionAdjMatrix[i][j] = 1;
                }
            }
        }

        double[] avgVelocity = calcAvgVelocity(tracks, startFrame, endFrame);

        //double Cv = calcCv(avgVelocity);
        double Cv = 0.00001;

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

    public double[][] createAdjMatrix(List<Track> tracks, boolean dirVelocity) {
        if (tracks.size() == 0) throw new IllegalArgumentException("Map is Empty, Couldn't create Adj Matrix");

        double[][] posAdjMatrix = calcAdjPositionMatrix(tracks);

        double[][] velocityAdjMatrix = new double[tracks.size()][tracks.size()];
        double[][] directionAdjMatrix = new double[tracks.size()][tracks.size()];
        ;
        if (dirVelocity) {
            velocityAdjMatrix = calcAdjVelocityMatrix(tracks);
            directionAdjMatrix = calcAdjDirMatrix(tracks);
        } else {
            for (int i = 0; i < tracks.size(); i++) {
                for (int j = 0; j < tracks.size(); j++) {
                    velocityAdjMatrix[i][j] = 1;
                    directionAdjMatrix[i][j] = 1;
                }
            }
        }

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

    private double[][] calcAdjDirMatrix(List<Track> tracks, int startFrame, int endFrame) {
        if (tracks.size() == 0) throw new IllegalArgumentException("Map is Empty, Couldn't create Adj Matrix");

        TrackPoint[] direction = calcDirection(tracks, startFrame, endFrame);

        int sizeOfAdjMatrix = tracks.size();
        double[][] directionAdjMatrix = new double[sizeOfAdjMatrix][sizeOfAdjMatrix];

        for (int i = 0; i < sizeOfAdjMatrix; i++) {
            for (int j = 0; j < sizeOfAdjMatrix; j++) {

                if (i == j)
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

    private double[][] calcAdjDirMatrix(List<Track> tracks) {
        if (tracks.size() == 0) throw new IllegalArgumentException("Map is Empty, Couldn't create Adj Matrix");

        TrackPoint[] direction = calcDirection(tracks);

        int sizeOfAdjMatrix = tracks.size();
        double[][] directionAdjMatrix = new double[sizeOfAdjMatrix][sizeOfAdjMatrix];

        for (int i = 0; i < sizeOfAdjMatrix; i++) {
            for (int j = 0; j < sizeOfAdjMatrix; j++) {

                if (i == j)
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

    private TrackPoint[] calcDirection(List<Track> tracks, int startFrame, int endFrame) {
        int sizeOfAdjMatrix = tracks.size();
        TrackPoint[] result = new TrackPoint[sizeOfAdjMatrix];

        Track fakeTrack = new Tracks2D(-1, -1);
        for (int i = startFrame; i < endFrame + 1; i++) {
            fakeTrack.addPointData(new Point2D(-1, -1, i));
        }

        for (int i = 0; i < sizeOfAdjMatrix; i++) {

            Track theTrack = tracks.get(i);
            if (haveOverlap(theTrack, fakeTrack)) {
                int[] startStopFrame = calculateCommonFrames(theTrack, fakeTrack);

                int indexOfStart = 0 , indexOfStop =0 ;
                for (int j = 0; j < theTrack.length(); j++) {
                    if(theTrack.getPointData(j).getFrame() == startStopFrame[0])
                        indexOfStart = j;
                    else if(theTrack.getPointData(j).getFrame() == startStopFrame[1])
                        indexOfStop = j;
                }

                result[i] = (theTrack.getDirection(theTrack, indexOfStart, indexOfStop));
            } else
                result[i] = new Point2D(0, 0, -1);
        }
        return result;
    }

    private TrackPoint[] calcDirection(List<Track> tracks) {
        int sizeOfAdjMatrix = tracks.size();
        TrackPoint[] result = new TrackPoint[sizeOfAdjMatrix];

        for (int i = 0; i < sizeOfAdjMatrix; i++) {
            Track theTrack = tracks.get(i);
            result[i] = (theTrack.getDirection(theTrack, 0, theTrack.length() - 1));
        }
        return result;
    }

    public double[][] calcAdjVelocityMatrix(List<Track> tracks, int startFrame, int endFrame) {

        if (tracks.size() == 0) throw new IllegalArgumentException("Map is Empty, Couldn't create Adj Matrix");

        double[] avgVelocity = calcAvgVelocity(tracks, startFrame, endFrame);

        double Cv = calcCv(avgVelocity);

        int sizeOfAdjMatrix = tracks.size();
        double[][] velocityAdjMatrix = new double[sizeOfAdjMatrix][sizeOfAdjMatrix];

        for (int i = 0; i < sizeOfAdjMatrix; i++) {
            for (int j = 0; j < sizeOfAdjMatrix; j++) {

                if (i == j)
                    velocityAdjMatrix[i][j] = Math.exp(1);
                else if (velocityAdjMatrix[i][j] == 0) {

                    velocityAdjMatrix[i][j] = Math.exp(1 - (Math.abs(avgVelocity[i] - avgVelocity[j]) / Cv));
                    velocityAdjMatrix[j][i] = velocityAdjMatrix[i][j];
                }
            }
        }
        return velocityAdjMatrix;
    }

    public double[][] calcAdjVelocityMatrix(List<Track> tracks) {

        if (tracks.size() == 0) throw new IllegalArgumentException("Map is Empty, Couldn't create Adj Matrix");

        double[] avgVelocity = calcAvgVelocity(tracks);

        double Cv = calcCv(avgVelocity);

        int sizeOfAdjMatrix = tracks.size();
        double[][] velocityAdjMatrix = new double[sizeOfAdjMatrix][sizeOfAdjMatrix];

        for (int i = 0; i < sizeOfAdjMatrix; i++) {
            for (int j = 0; j < sizeOfAdjMatrix; j++) {

                if (i == j)
                    velocityAdjMatrix[i][j] = Math.exp(1);
                else if (velocityAdjMatrix[i][j] == 0) {

                    velocityAdjMatrix[i][j] = Math.exp(1 - (Math.abs(avgVelocity[i] - avgVelocity[j]) / Cv));
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

    private double[] calcAvgVelocity(List<Track> tracks, int startFrame, int endFrame) {
        int sizeOfAdjMatrix = tracks.size();
        double[] velocityMatrix = new double[sizeOfAdjMatrix];

        Track fakeTrack = new Tracks2D(-1, -1);
        for (int i = startFrame; i < endFrame + 1; i++) {
            fakeTrack.addPointData(new Point2D(-1, -1, i));
        }

        for (int i = 0; i < sizeOfAdjMatrix; i++) {
            Track theTrack = tracks.get(i);
            if (haveOverlap(theTrack, fakeTrack)) {
                int[] startStopFrame = calculateCommonFrames(theTrack, fakeTrack);

                int indexOfStart = 0 , indexOfStop =0 ;
                for (int j = 0; j < theTrack.length(); j++) {
                    if(theTrack.getPointData(j).getFrame() == startStopFrame[0])
                        indexOfStart = j;
                    else if(theTrack.getPointData(j).getFrame() == startStopFrame[1])
                        indexOfStop = j;
                }

                velocityMatrix[i] = (theTrack.getDifferenceOfPosition(theTrack,
                        indexOfStart, indexOfStop)) / (endFrame - startFrame);
            } else
                velocityMatrix[i] = 0;
        }
        return velocityMatrix;
    }

    private double[] calcAvgVelocity(List<Track> tracks) {
        int sizeOfAdjMatrix = tracks.size();
        double[] velocityMatrix = new double[sizeOfAdjMatrix];

        for (int i = 0; i < sizeOfAdjMatrix; i++) {
            Track theTrack = tracks.get(i);
            velocityMatrix[i] = (theTrack.getDifferenceOfPosition(theTrack, 0, theTrack.length() - 1)) / theTrack.length();
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

    private boolean haveOverlapWithStartFrame(int[] commonTracksFrames, int startFrame, int endFrame) {

        int startCommon = commonTracksFrames[0];
        int endCommon = commonTracksFrames[1];

        if (startCommon <= endFrame && endCommon >= startFrame) return true;
        else return false;
    }

    public int[] calculateCommonFrames(Track trackA, Track trackB) {

        // swap if trackA is after trackB
        if (trackA.getPointData(0).getFrame() > trackB.getPointData(0).getFrame()) {
            Track temp = trackA;
            trackA = trackB;
            trackB = temp;
        }

        int[] result = new int[2];
        boolean seenStartFrame = false;
        for (int i = 0; i < trackA.length(); i++) {
            for (int j = 0; j < trackB.length(); j++) {
                if (trackA.getPointData(i).getFrame() == trackB.getPointData(j).getFrame()) {
                    if (!seenStartFrame) {
                        result[0] = i;
                        seenStartFrame = true;
                    }else {
                        result[1] = i;
                        break;
                    }
                }
            }
        }
        result[0] = trackA.getPointData(result[0]).getFrame();
        result[1] = trackA.getPointData(result[1]).getFrame();
        return result;
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
        return Math.exp(1 - (squaredUpperDivision / (distances.size() * 100)));

    }

    public double calculateWeight(Track trackA, Track trackB, int startFrame, int endFrame) {

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
                if (trackA.getPointData(i).getFrame() == trackB.getPointData(j).getFrame() &&
                        trackA.getPointData(i).getFrame() >= startFrame &&
                        trackA.getPointData(i).getFrame() <= endFrame) {
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
        return Math.exp(1 - (squaredUpperDivision / (distances.size() * 100)));

    }

    private double getMean(List<Double> data) {
        double sum = 0.0;

        for (double a : data)
            sum += a;

        return sum / data.size();
    }

    private double getVariance(List<Double> data) {
        double mean = getMean(data);
        double temp = 0;

        for (double a : data)
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
