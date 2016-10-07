package modularityCut;

import java.util.ArrayList;
import java.util.List;

public class Tracks2D implements Track {

    List<TrackPoint> points;
    private int ID = -1;
    private int group = -1;

    public Tracks2D(int theID, int theGroup) {

        points = new ArrayList<>();
        ID = theID;
        group = theGroup;
    }

    public Tracks2D() {points = new ArrayList<>();}

    @Override
    public TrackPoint getDirection(Track secondTrack, int indexOfFirst, int indexOfSecond) {
        double diffOfXs = this.getPointData(indexOfFirst).getX() -
                secondTrack.getPointData(indexOfSecond).getX();

        double diffOfYs = this.getPointData(indexOfFirst).getY() -
                secondTrack.getPointData(indexOfSecond).getY();

        double size = Math.sqrt(Math.pow(diffOfXs, 2) + Math.pow(diffOfYs, 2));

        diffOfXs = diffOfXs / size;
        diffOfYs = diffOfYs / size;

        return new Point2D(diffOfXs, diffOfYs, -1);
    }

    @Override
    public double getDifferenceOfPosition(Track secondTrack, int indexOfFirst, int indexOfSecond) {

        // Euclidean distance...
        double diffOfXs = Math.pow(this.getPointData(indexOfFirst).getX() -
                secondTrack.getPointData(indexOfSecond).getX(), 2);

        double diffOfYs = Math.pow(this.getPointData(indexOfFirst).getY() -
                secondTrack.getPointData(indexOfSecond).getY(), 2);

        //I spent 3 weeks then I figured out I don't need to sqrt the distances! Shit!
        // And wasted more time to test dozens of other formulas.
        // PAY MORE ATTENTION TO IMPLEMENTING FORMULAS
        return Math.sqrt(diffOfXs + diffOfYs);
        //return diffOfXs + diffOfYs;
    }

    @Override
    public int length() {
        return points.size();
    }

    @Override
    public TrackPoint getPointData(int index) {
        return points.get(index);
    }

    @Override
    public void addPointData(TrackPoint data) {
        points.add(data);
    }

    @Override
    public void setGroup(int theGroup) {
        group = theGroup;
    }

    @Override
    public int getGroup() {
        return group;
    }

    @Override
    public void setID(int theID) {ID = theID;}

    @Override
    public int getID() {
        return ID;
    }
}
