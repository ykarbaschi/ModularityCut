package modularityCut;

import Jama.EigenvalueDecomposition;
import Jama.Matrix;

import java.util.*;

public class ModularityMeasure {

    SocialGraph socialGraph = new SocialGraph();
    private static Matrix bigS;
    ArrayList<Matrix> All_S;
    private static double Qmax;
    private static int LastAssignedGroup = 0;
    private static Queue<Integer> uncheckedGroups;
    private static int currentGroupID;
    private static Map<Integer, Track> tracks_All;

    public ModularityMeasure(Map<Integer, Track> tracks) {
        tracks_All = tracks;
        uncheckedGroups = new PriorityQueue<>();
        All_S = new ArrayList<>();
    }

    public double[] getEigenValues(double[][] modularityMatrix) {
        Matrix modularity = new Matrix(modularityMatrix);
        EigenvalueDecomposition EigenOfModularity = new EigenvalueDecomposition(modularity);
        return EigenOfModularity.getRealEigenvalues();
    }

    public double getMaximumModularity(double[][] modularityMatrix, double totalMatrixStrength) {
        Matrix modularity = new Matrix(modularityMatrix);
        EigenvalueDecomposition EigenOfModularity = new EigenvalueDecomposition(modularity);

        Matrix eigenVector = EigenOfModularity.getV();
        int indexOfMaximum = getIndexOfMaximumEigenValue(EigenOfModularity.getRealEigenvalues());

        double[] eigenValues = EigenOfModularity.getRealEigenvalues();

        Matrix dominantEigenVector = eigenVector.getMatrix(0, eigenVector.getColumnDimension() - 1,
                indexOfMaximum, indexOfMaximum);

        Matrix labelVectorOfDominantEigenVector = calculateLabelVector(dominantEigenVector);

        //Look at the paper Equation 15...
        double sigmaOfAllEigenValuesAndEigenVector = 0;
        for (int i = 0; i < eigenVector.getColumnDimension(); i++) {
            Matrix eigenVector_i = eigenVector.getMatrix(0, eigenVector.getColumnDimension() - 1,
                    i, i);
            sigmaOfAllEigenValuesAndEigenVector = sigmaOfAllEigenValuesAndEigenVector +
                    Math.pow((eigenVector_i.times(labelVectorOfDominantEigenVector)).get(0, 0), 2) *
                            eigenValues[indexOfMaximum];
        }

        return sigmaOfAllEigenValuesAndEigenVector / 4 * totalMatrixStrength;
    }

    private Matrix calculateLabelVector(Matrix EigenVector) {
        // EigenVector should be COLUMN VECTOR
        Matrix labelVector = new Matrix(EigenVector.getRowDimension(), 1);
        // labelVector is a COLUMN VECTOR

        for (int i = 0; i < EigenVector.getRowDimension(); i++) {
            if (EigenVector.get(i, 0) >= 0) labelVector.set(i, 0, 1);
            else labelVector.set(i, 0, -1);
        }
        return labelVector;
    }

    private int getIndexOfMaximumEigenValue(double[] eigenValues) {

        int IndexOfMax = 0;
        double max = eigenValues[0];
        for (int i = 0; i < eigenValues.length; i++) {
            if (eigenValues[i] > max) {
                max = eigenValues[i];
                IndexOfMax = i;
            }
        }
        return IndexOfMax;
    }

    public ArrayList<Matrix> findCommunityStructure() {

        double[][] AdjMatrix = socialGraph.createAdjacencyMatrix(socialGraph.convertToList(tracks_All));
        List<Double> totalConnectionStrength = socialGraph.calculateTotalConnectionStrength(AdjMatrix);
        double totalMatrixStrength = socialGraph.calculateTotalMatrixStrength(AdjMatrix);
        double[][] modularity_All = socialGraph.calculateModularityMatrix(
                AdjMatrix, totalConnectionStrength, totalMatrixStrength);

        Matrix modularityMatrix_All = new Matrix(modularity_All);

        bigS = new Matrix(modularityMatrix_All.getColumnDimension(), 1, 1);

        // for first time Qmax = Tr(S_t * B * S)
        Qmax = (((bigS.transpose()).times(modularityMatrix_All)).times(bigS)).trace();

        uncheckedGroups.add(LastAssignedGroup);

        All_S.add(bigS);

        while (uncheckedGroups.peek() != null) {
            currentGroupID = uncheckedGroups.poll();

            Map<Integer, Track> tracksWithCurrentID = new HashMap<>();

            tracks_All.entrySet().stream()
                                .filter(entry -> entry.getValue().getGroup() == currentGroupID)
                                .forEach(entry -> tracksWithCurrentID.put(entry.getKey(), entry.getValue()));

            evaluateDivideSubGraph(modularityMatrix_All, tracksWithCurrentID);
        }

        return All_S;
    }

    private void evaluateDivideSubGraph(Matrix modularityMatrix_all, Map<Integer, Track> tracksWithCurrentID) {

        double[][] AdjMatrix = socialGraph.createAdjacencyMatrix(socialGraph.convertToList(tracksWithCurrentID));
        List<Double> totalConnectionStrength = socialGraph.calculateTotalConnectionStrength(AdjMatrix);
        double totalMatrixStrength = socialGraph.calculateTotalMatrixStrength(AdjMatrix);
        double[][] modularity = socialGraph.calculateModularityMatrix(
                AdjMatrix, totalConnectionStrength, totalMatrixStrength);

        Matrix modularityMatrix = new Matrix(modularity);
        Matrix labelVectorOfDominantEigenVector = findLabelVectorForMaxQ(modularityMatrix);

        // we have to keep a copy of tracks_All before changing it
        //and roll back if the group was not good.
        Map<Integer, Track> beforeChangeGroup = tracks_All;

        Matrix newBigS = updateBigS(labelVectorOfDominantEigenVector, tracksWithCurrentID);

        double new_Q = calculateNewQ(newBigS, modularityMatrix_all);

        if (new_Q - Qmax > 0) {
            All_S.add(newBigS);
            bigS = newBigS;
            Qmax = new_Q;
            LastAssignedGroup++;
            uncheckedGroups.add(currentGroupID);
            uncheckedGroups.add(LastAssignedGroup);
        }else{
            tracks_All = beforeChangeGroup;
        }
    }

    private Matrix updateBigS(Matrix labelVector, Map<Integer, Track> tracksWithCurrentID) {

        ArrayList<Track> ListOfTracks = socialGraph.convertToList(tracksWithCurrentID);
        int tempLastGroup = LastAssignedGroup;

        for (int i = 0; i < ListOfTracks.size(); i++) {
            if (labelVector.get(i, 0) == -1) {
                tempLastGroup = LastAssignedGroup + 1;
                int ID = ListOfTracks.get(i).getID();
                Track tempTrack = tracks_All.get(ID);
                tempTrack.setGroup(tempLastGroup);
                tracks_All.put(ID, tempTrack);
            }
        }

        ListOfTracks = socialGraph.convertToList(tracks_All);

        Matrix newS = new Matrix(ListOfTracks.size(), tempLastGroup + 1, 0);

        for (int i = 0; i < ListOfTracks.size(); i++) {
            newS.set(i, ListOfTracks.get(i).getGroup(), 1);
        }

        return newS;
    }

    private double calculateNewQ(Matrix newBigS, Matrix modularityMatrix_all) {

        return (((newBigS.transpose()).times(modularityMatrix_all)).times(newBigS)).trace();
    }

    private Matrix findLabelVectorForMaxQ(Matrix modularity_b) {
        EigenvalueDecomposition EigenOfModularity = new EigenvalueDecomposition(modularity_b);
        Matrix eigenVector = EigenOfModularity.getV();
        int indexOfMaximum = getIndexOfMaximumEigenValue(EigenOfModularity.getRealEigenvalues());

        Matrix dominantEigenVector = eigenVector.getMatrix(0, eigenVector.getColumnDimension() - 1,
                indexOfMaximum, indexOfMaximum);

        return calculateLabelVector(dominantEigenVector);
    }
}