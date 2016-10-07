package modularityCut;

public interface Track {
    // I used TrackPoint for direction. But it better to use a separate data type for direction
    TrackPoint getDirection(Track secondTrack, int indexOfFirst, int indexOfSecond);

    double getDifferenceOfPosition(Track secondTrack, int indexOfFirst, int indexOfSecond);
    int length();
    TrackPoint getPointData(int index);
    void addPointData(TrackPoint data);
    void setGroup(int theGroup);
    int getGroup();
    void setID(int theID);
    int getID();

}
