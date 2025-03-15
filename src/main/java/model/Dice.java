package model;


import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import model.Tools.Observer;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Dice implements Serializable {
    private int valeur1;
    private int valeur2;
    private final Random random;
    public List<Observer> observerList = new ArrayList<>();

    public Dice() {
        random = new Random();
    }

    // Méthode pour lancer les dés et mettre à jour les valeurs
    public void roll() {
        valeur1 = 1 + random.nextInt(6); // Un nombre entre 1 et 6
        valeur2 = 1 + random.nextInt(6);


        for(Observer l : observerList){
            System.out.println(observerList.size());
            l.update();
        }
    }

    public void resetValues(){
        this.valeur1 = -1;
        this.valeur2 = -1;
    }

    public int getValue1() {
        return valeur1;
    }

    public int getValue2() {
        return valeur2;
    }

    public int getSum() {
        return valeur1 + valeur2;
    }

    public boolean isPerfect(){
        valeur1 = 1 + random.nextInt(6);
        valeur2 = 1 + random.nextInt(6);

        for(Observer l : observerList){
            System.out.println(observerList.size());
            l.update();
        }
        return valeur1 == valeur2;
    }

    public boolean isEqual(){return valeur1 == valeur2;}

    public void addObserver(Observer observer) {
        observerList.add(observer);
    }

    public void removeObserver(Observer observer) {
        observerList.remove(observer);
    }
}
