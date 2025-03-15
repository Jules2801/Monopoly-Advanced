package model.Cards;

import model.Player;

import java.io.Serializable;

public class Card implements Serializable {

    private static final long serialVersionUID = 1L;

    private String nom;
    private String description;
    private String color;
    private String effect;
    public boolean isPlayable = false;

    public Card(String nom, String description, String color, String effect) {
        this.nom = nom;
        this.description = description;
        this.color = color;
        this.effect = effect;
    }

    public void appliquerEffet(Player joueur) {
        // Implémentation de l'effet sur le joueur
    }

    public String getNom() {
        return nom;
    }

    public String getDescription() {
        return description;
    }

    public String getColor(){return color;}
    public String getEffect(){return effect;}
}
