package model.Cell;

import gui.Style;
import javafx.scene.image.Image;
import model.Geometry.Coordinate;
import model.Player;
import model.Direction;

import java.io.Serializable;
import java.util.Random;

public class Building extends AbstractCell implements Serializable {

    private static final long serialVersionUID = 1L;

    private final String name;
    private Garden possibleGarden;
    private boolean isVariant;
    private boolean ownGarden;

    public void setOwnGarden(boolean ownGarden) {
        this.ownGarden = ownGarden;
    }

    public boolean getOwnGarden(){
        return this.ownGarden;
    }

    private final boolean danger; // Si le lieu peu être victime de catastrophes

    private boolean destroy;
    private int level;
    private Player owner;
    private int price;
    private int upgradePrice;
    private int rent;
    private boolean mortgaged; //hypotéqué ou non

    // Implémentation de la méthode description() de l'interface Case
    @Override
    public String description() {
        if(isMortgaged()){
            String s = "Immeuble à vendre : \n" + "Hôtel de niveau : " + getLevel() + "\nPrix : " + getPrice() + "$";
            return s;
        }
        if(owner != null){
            if(destroy){
                String s = "Propriétaire :  " + owner.getName() + "\n" +"Détruit"+"\n"+ "Prix des réparations :"+getReparationsPrice()+ "$\n";
                return s;
            }else {
                String s = "Propriétaire :  " + owner.getName() + "\n" + "Hôtel de niveau :  " + level +"\n" + "Loyer :  " + rent +"$\n";
                return s;
            }

        }
        return "Acheter un immeuble pour recevoir des loyers";
    }

    public Building(String name, int price, int rent, boolean danger, Coordinate coordinate) {
        super(coordinate);
        this.name = name;
        this.price = price;
        this.upgradePrice = price*2;
        this.rent = rent;
        this.danger = danger;
        this.level = 0; // niveau initial
        this.destroy = false;
        this.mortgaged = false;
        this.ownGarden = false;
        this.possibleGarden = null;
        Random r = new Random();
        int variant = r.nextInt(2);
        if(variant == 0) isVariant = false;
        else isVariant = true;
    }

    public boolean isVariant() {
        return isVariant;
    }

    public void setPossibleGarden(Garden possibleGarden) {
        this.possibleGarden = possibleGarden;
    }

    public Garden getPossibleGarden() {
        return possibleGarden;
    }

    public String getName() {
        return name;
    }

    public boolean isDangerous() {
        return danger;
    }

    public boolean isMortgaged() {
        return mortgaged;
    }

    public void setMortgaged(boolean mortgaged) {
        this.mortgaged = mortgaged;
    }

    public void destroy(){
        destroy = true;

    }

    public void addOwner(Player player){
        player.addBuilding(this);
        this.setOwner(player);
        this.nextLevel();
        player.pay(this.getPrice());
    }

    public void upgrade(Player player){
        this.nextLevel();
        player.pay(this.getUpdatePrice());
        this.setRent(this.getRent()*this.getLevel());
        this.doubleUpdatePrice();

    }

    public void changeOwner(Player player){
        this.getOwner().getProperties().remove(this);
        this.setOwner(player);
        player.addBuilding(this);
        this.takeBuildingPrice();
    }

    public void repair(){
        destroy = false;
    }

    public boolean isDestroy(){
        return destroy;
    }

    //Retourne le prix des réparations,+ le building a un haut niveau + les reparations sont couteuses
    public int getReparationsPrice(){
        return  price/2 + level*10;
    }
    public void setOwner(Player owner){
        this.owner = owner;
    }


    public void nextLevel(){
        level++;
    }

    public int getMortagePrice(){// Lorsqu'on hypoteque un building la banque nous donne 75% de son prix
        return (int)(price*0.75);
    }

    public int getLevel() {
        return level;
    }

    public Player getOwner() {
        return owner;
    }
    
    public int getPrice() {
        return price;
    }
    public void setPrice(int price){this.price = price;}
    public int getUpdatePrice(){return upgradePrice;}

    public void doubleUpdatePrice(){upgradePrice*= 2;}
    public void olympicUpdatePrice(){rent*=5;}
    public void takeBuildingPrice(){rent*=1.15;}
    public void inflationPrice(){rent *= 1.25;}

    public int getRent() {
        if(!ownGarden) return rent;
        else return (rent + possibleGarden.getRent());
    }
    public void setRent(int rent){this.rent = rent;}

}





