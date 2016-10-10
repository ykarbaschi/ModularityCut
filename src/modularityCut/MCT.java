package modularityCut;

import Jama.Matrix;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MCT {

    public static void main(String[] args){
        SocialGraph data1Cam1 = new SocialGraph();
        String address = "DataSets//MCT_Challenge//annotation//Dataset1//Cam1.dat";

        Map<Integer, Track> AllTracks = data1Cam1.interpretDataMCTChallenge(
                data1Cam1.readDataSet(address));

        ModularityMeasure modularityMeasure = new ModularityMeasure(AllTracks);

        boolean useDirVelocity = true;

        data1Cam1.ExportAdjMatrix(data1Cam1.createAdjMatrix(
               data1Cam1.convertToList(AllTracks), useDirVelocity));

       /* data1Cam1.ExportAdjMatrix(data1Cam1.createAdjMatrix(
                      data1Cam1.convertToList(AllTracks)));*/

        ArrayList<Matrix> structure = modularityMeasure.findCommunityStructure(useDirVelocity);

        modularityMeasure.ExportCommunity(structure);

    }
}
