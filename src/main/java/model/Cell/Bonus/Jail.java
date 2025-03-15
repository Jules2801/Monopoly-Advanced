package model.Cell.Bonus;

import model.Player;
import model.Geometry.Coordinate;

import java.io.Serializable;

public class Jail extends Bonus implements Serializable {

    private static final long serialVersionUID = 1L;

    public Jail(Coordinate coordinate) {
        super(coordinate, "Prison");
    }

    @Override
    public boolean action(Player player) {
        player.toJail();
        return true;
    }

    @Override
    public String description() {
        return "En prison vous ne pouvez pas jouer !";
    }
}
