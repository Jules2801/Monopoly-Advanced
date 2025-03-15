package model.Cards;

import java.io.Serializable;

public class FamousBuilding extends Card implements Serializable {

    private static final long serialVersionUID = 1L;

    public FamousBuilding() {
        super("Le Diable s'habille en Prada", "Grâce à un film tourné dans votre building devenu très célèbre, la popularité de l'immeuble ne cesse de grandir comme le prix du loyer qui a été multiplié par 5", "yellow", "famousbuilding");
    }
}
