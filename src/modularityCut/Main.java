package modularityCut;

import Jama.Matrix;

import java.util.ArrayList;

public class Main {

    public static void main(String[] args){
        MCT_Challenge_Data mctData = new MCT_Challenge_Data();
        ModularityMeasure modularityMeasure = new ModularityMeasure( mctData.readData());
        ArrayList<Matrix> structure = modularityMeasure.findCommunityStructure();
        System.out.println("");
    }
}
