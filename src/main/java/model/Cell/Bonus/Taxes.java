package model.Cell.Bonus;

import model.Player;
import model.Geometry.Coordinate;

import java.io.Serializable;

public class Taxes extends Bonus implements Serializable {

    private static final long serialVersionUID = 1L;

    private int price; //Prix de la taxe (impots,facture d'eau...)
    public int getPrice(){return price;}

    public Taxes(Coordinate coordinate, String name,int price) {
        super(coordinate, name);
        this.price = price;
    }

    @Override
    public boolean action(Player player) {
        return player.pay(price);
    }

    @Override
    public String description() {
        return getName() +": Vous devez payer un montant de "+ price +"$";
    }
}
