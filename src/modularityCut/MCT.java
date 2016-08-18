package modularityCut;

import Jama.Matrix;

import java.util.ArrayList;

public class MCT {

    public static void main(String[] args){
        SocialGraph data1Cam1 = new SocialGraph();
        String address = "DataSets//MCT_Challenge//annotation//Dataset1//Cam1.dat";

        ModularityMeasure modularityMeasure = new ModularityMeasure(
                data1Cam1.interpretDataMCTChallenge(
                        data1Cam1.readDataSet(address)));

        ArrayList<Matrix> structure = modularityMeasure.findCommunityStructure();

        Matrix LastGrouping = structure.get(structure.size()-1);

        for (int i = 0; i < LastGrouping.getColumnDimension(); i++) {
            System.out.println("Group " + i + " Has:");
            for (int j = 0; j < LastGrouping.getRowDimension(); j++) {
                if (LastGrouping.get(j, i) == 1 )
                    System.out.print(j + ", ");
            }
            System.out.println();
        }
    }
}
