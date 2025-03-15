package model.Cell;

import model.Geometry.Coordinate;

import java.io.Serializable;

public class Road extends AbstractCell implements Serializable {
    private static final long serialVersionUID = 1L;


    public Road(Coordinate coordinate) {
        super(coordinate);
    }

    @Override
    public String description() {
        return "Vous pouvez vous déplacer sur cette case.";
    }
}
