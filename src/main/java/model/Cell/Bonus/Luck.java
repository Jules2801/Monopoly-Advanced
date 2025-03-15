package model.Cell.Bonus;

import model.Cards.Card;
import model.Player;
import model.Geometry.Coordinate;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Random;

public class Luck extends Bonus implements Serializable {

    private static final long serialVersionUID = 1L;

    private ArrayList<Card> luckyCards;// Liste des cartes Chance

    public Luck(Coordinate coordinate, String name) {
        super(coordinate, name);
        this.luckyCards = new ArrayList<Card>();
    }

    @Override
    public boolean action(Player player) {
        Random random = new Random();
        int cardIndex = random.nextInt(luckyCards.size());
        player.addCard(luckyCards.get(cardIndex));
        return true;

    }

    @Override
    public String description() {
        return "Tirez une carte "+ getName() ;
    }
}
