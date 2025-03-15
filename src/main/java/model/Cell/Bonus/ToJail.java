package model.Cell.Bonus;

import model.Player;
import model.Geometry.Coordinate;

import java.io.Serializable;

public class ToJail extends Bonus implements Serializable {

    private static final long serialVersionUID = 1L;

    public ToJail(Coordinate coordinate) {
        super(coordinate, "EnPrison");
    }

    @Override
    public boolean action(Player player) {
        //ENVOYER VERS LA PRISON
        //VOIR COMMENT IMPLEMENTER LES TP
        return false;
    }

    @Override
    public String description() {
        return "Allez en prison !";
    }
}
