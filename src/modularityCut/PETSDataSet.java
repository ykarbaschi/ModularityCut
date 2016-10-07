package modularityCut;

import Jama.Matrix;

import java.util.ArrayList;
import java.util.Map;

public class PETSDataSet {

    public static void main(String[] args){
        SocialGraph PETS_S2L1 = new SocialGraph();
        String address = "DataSets//PETS09-S2L1//gt.txt";

        Map<Integer, Track> AllTracks = PETS_S2L1.interpretDataMOTChallenge(
                PETS_S2L1.readDataSet(address));

        ModularityMeasure modularityMeasure = new ModularityMeasure( AllTracks);

        ArrayList<Matrix> structure = modularityMeasure.findCommunityStructure();

        PETS_S2L1.ExportAdjMatrix(PETS_S2L1.calcAdjPositionMatrix(
                PETS_S2L1.convertToList(AllTracks)));

        modularityMeasure.ExportCommunity(structure);
    }
}
