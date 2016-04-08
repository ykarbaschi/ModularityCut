package modularityCut;

import Jama.Matrix;

import java.util.ArrayList;

public class Main {

    public static void main(String[] args){
        MCT_Challenge_Data mctData = new MCT_Challenge_Data();
        ModularityMeasure modularityMeasure = new ModularityMeasure( mctData.readData());
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
