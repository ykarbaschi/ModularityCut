package modularityCut;

import Jama.Matrix;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MCT {

    public static void main(String[] args){
        DataSet dataSet = new DataSet("DataSets//MCT_Challenge//annotation//Dataset1//Cam1.dat");
        Map<Integer, Track> AllTracks = dataSet.interpretDataMCTChallenge(
                dataSet.readDataSet());

        // window size in frames
        int window = 24000;

        SocialGraph socialGraphMCT = new SocialGraph(window, dataSet.startFrameNumber, dataSet.endFrameNumber);

        //ModularityMeasure modularityMeasure = new ModularityMeasure(AllTracks);

        boolean useDirVelocity = true;

        List<Matrix> result = socialGraphMCT.calcAllAdjMatrix(socialGraphMCT.convertToList(AllTracks),
                useDirVelocity);


        System.out.println(result);

        /*socialGraphMCT.ExportAdjMatrix(socialGraphMCT.createAdjMatrix(
               socialGraphMCT.convertToList(AllTracks),
                dataSet.startFrameNumber, dataSet.endFrameNumber,
                useDirVelocity));*/

       /* data1Cam1.ExportAdjMatrix(data1Cam1.createAdjMatrix(
                      data1Cam1.convertToList(AllTracks)));*/

        //ArrayList<Matrix> structure = modularityMeasure.findCommunityStructure(useDirVelocity);

        //modularityMeasure.ExportCommunity(structure);

    }
}
