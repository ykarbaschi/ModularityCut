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
    public double getDifferenceOfPosition(Track secondTrack, int indexOfFirst, int indexOfSecond) {
        double diffOfXs = Math.pow(this.getPointData(indexOfFirst).getX() -
                secondTrack.getPointData(indexOfSecond).getX(), 2);
        double diffOfYs = Math.pow(this.getPointData(indexOfFirst).getY() -
                secondTrack.getPointData(indexOfSecond).getY(), 2);

        return Math.sqrt(diffOfXs + diffOfYs);
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
