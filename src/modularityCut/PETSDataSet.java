package modularityCut;

import Jama.Matrix;

import java.util.ArrayList;

public class PETSDataSet {

    public static void main(String[] args){
        SocialGraph PETS_S2L1 = new SocialGraph();
        String address = "DataSets//PETS09-S2L1//gt.txt";

        ModularityMeasure modularityMeasure = new ModularityMeasure(
                PETS_S2L1.interpretDataMOTChallenge(
                        PETS_S2L1.readDataSet(address)));

        ArrayList<Matrix> structure = modularityMeasure.findCommunityStructure();

        Matrix LastGrouping = structure.get(structure.size()-1);

        for (int i = 0; i < LastGrouping.getColumnDimension(); i++) {
            System.out.println("Group " + i + " Has:");
            for (int j = 0; j < LastGrouping.getRowDimension(); j++) {
                if (LastGrouping.get(j, i) == 1 )
                    System.out.print((j+1) + ", ");
            }
            System.out.println();
        }
    }
}
