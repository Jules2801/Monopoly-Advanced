package model.Cell.Bonus;

import model.Player;
import model.Geometry.Coordinate;

import java.io.Serializable;

public class Start extends Bonus implements Serializable {

    private static final long serialVersionUID = 1L;

    public Start(Coordinate coordinate) {
        super(coordinate, "Départ");
    }

    @Override
    public boolean action(Player player) {
        player.addMoney(500);
        return true;
    }

    @Override
    public String description() {
        return "Lorsque vous passer par la case départ vous recevez 500$ et 1000$ si vous êtes pile dessus.";
    }
}
