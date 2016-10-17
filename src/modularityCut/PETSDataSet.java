package modularityCut;

import Jama.Matrix;

import java.util.ArrayList;
import java.util.Map;

public class PETSDataSet {

    public static void main(String[] args){
        DataSet dataSet = new DataSet("DataSets//PETS09-S2L1//gt.txt");

        Map<Integer, Track> AllTracks = dataSet.interpretDataMOTChallenge(
                dataSet.readDataSet());

        // window size in frames
        int window = 100;
        boolean useDirVelocity = true;

        SocialGraph socialGraphMCT = new SocialGraph(window, dataSet.startFrameNumber, dataSet.endFrameNumber);

        socialGraphMCT.exportAdjMatrixWithWindow(
                socialGraphMCT.calcAllAdjMatrix(
                        socialGraphMCT.convertToList(AllTracks), useDirVelocity));


    }
}
