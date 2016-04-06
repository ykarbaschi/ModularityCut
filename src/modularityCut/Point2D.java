package modularityCut;

public class Point2D implements TrackPoint{
    private double X;
    private double Y;
    private int frame;

    Point2D(double theX, double theY, int theFrame){
        X = theX;
        Y = theY;
        frame = theFrame;
    }

    public double getX(){return X;}
    public double getY(){return Y;}
    public double getZ(){return 0;}
    public int getFrame(){return frame;}
}
