package model.Cell.Bonus;

import model.Cell.Cell;
import model.Geometry.Coordinate;
import model.Player;

import java.io.Serializable;

public class Teleport extends Bonus implements Serializable {

    private static final long serialVersionUID = 1L;

    private Cell targetCell;

    public Teleport(Coordinate coordinate) {
        super(coordinate, "Aéroport");
    }

    public void chooseCell(Cell cell) {
        this.targetCell = cell;
    }

    @Override
    public boolean action(Player player) {
        //envoyer vers les coordonnées
        return false;
    }

    @Override
    public String description() {
        return "Aéroport: permet de se téléporter à un autre emplacement sur le plateau.";
    }
}
