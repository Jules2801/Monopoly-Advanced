package model.Geometry;

import java.io.Serializable;

public class RealCoordinate implements Serializable {

    private static final long serialVersionUID = 1L;

    //Les ratios sont là pour convertir les coordonées classique vers les coordonées de la fenetre
    private double xRatio;

    private double yRatio;
    private double x;
    private double y;

    public RealCoordinate(double x, double y) {
        this.x = x;
        this.y = y;
        this.xRatio = 50;
        this.yRatio = 50;
    }


    public RealCoordinate coordinateToReal(Coordinate coordinate){
        return new RealCoordinate(coordinate.getX()*xRatio, coordinate.getY()*yRatio);
    }
    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }
}
