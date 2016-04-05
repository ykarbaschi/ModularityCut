package modularityCut;

public interface Track {
    double getDifferenceOfPosition(Track secondTrack, int indexOfFirst, int indexOfSecond);
    int length();
    TrackPoint getPointData(int index);
    void addPointData(TrackPoint data);
    void setGroup(int theGroup);
    int getGroup();
    void setID(int theID);
    int getID();
}
