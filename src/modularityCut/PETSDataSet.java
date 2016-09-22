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

        //Matrix LastGrouping = structure.get(structure.size()-1);

        for (int i = 0; i < structure.size(); i++) {
            System.out.println("Layer" + i + ":");
            Matrix GroupInfo = structure.get(i);

            for (int j = 0; j < GroupInfo.getColumnDimension(); j++) {
                System.out.println("Group " + j + " Has:");
                for (int k = 0; k < GroupInfo.getRowDimension(); k++) {
                    if (GroupInfo.get(k, j) == 1)
                        System.out.print(k + ", ");
                }
                System.out.println();
            }
        }
    }
}
