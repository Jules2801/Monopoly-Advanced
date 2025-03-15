package model.map;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;
import java.util.Scanner;
import java.util.Stack;

import model.Cell.*;
import model.Cell.Bonus.*;
import model.Cell.Franchise.Franchise;
import model.Cell.Franchise.Heritage.EmptyHeritage;
import model.Cell.Franchise.Restaurants.*;
import model.Geometry.Coordinate;

public class MapLoader {

    //chemin du fichier de sauvegarde
    private static String path = System.getProperty("user.dir") +  "/src/main/resources/Map/map.txt";

    public static void loadMap(Map map) throws FileNotFoundException {
        //charger la model.map depuis le fichier de sauvegarde

        File file = new File(path);
        if (file.exists()) {

            //charge la model.map depuis le fichier de sauvegarde

            Scanner scanner = new Scanner(file);
            Cell[][] cases = map.getMap();
            for (int i = 0; i < map.getHeight(); i++) {
                String line = scanner.nextLine();
                String[] data = line.split(" ");
                for (int j = 0; j < map.getWidth(); j++) {
                    // get from file
                    if (data[j].equals("S")){
                        cases[i][j] = new Start(new Coordinate(j, i));
                    }
                    else if (data[j].equals("B")){
                        Random rd = new Random();
                        int price = (rd.nextInt(5)+ 1)*100;
                        cases[i][j] = new Building(("(" + i + ", " + j + ")"), price, price/4, false, new Coordinate(j, i));
                    }
                    else if (data[j].equals("-") || data[j].equals("|")){
                        cases[i][j] = new Road(new Coordinate(j, i));
                    }
                    else if (data[j].equals("P")){
                        cases[i][j] = new ToJail(new Coordinate(j, i));
                    }
                    else if (data[j].equals("M")){
                        cases[i][j] = new McOcean(new Coordinate(j, i));
                    }
                    else if (data[j].equals("Q")){
                        cases[i][j] = new Bartenders(new Coordinate(j, i));
                    }
                    else if (data[j].equals("K")){
                        cases[i][j] = new Antique(new Coordinate(j, i));
                    }
                    else if (data[j].equals("R")){
                        cases[i][j] = new EmptyRestaurant(new Coordinate(j, i));
                    }
                    else if (data[j].equals("H")){
                        cases[i][j] = new EmptyHeritage(new Coordinate(j, i));
                    }
                }
            } 


        } else {
            System.out.println("Fichier de sauvegarde non trouvé");
        }
    }
    public static void generateBasicMap(Map map) {
        Cell[][] cases = map.getMap();
        int height = map.getHeight();
        int width = map.getWidth();

        // faire un cadre de route
        for (int i = 0; i < height; i++) {
            cases[0][i] = new Road(new Coordinate(i, 0));
            cases[i][0] = new Road(new Coordinate(0, i));
            cases[i][width - 1] = new Road(new Coordinate(width - 1, i));
            cases[height - 1][i] = new Road(new Coordinate(i, height - 1));
        
        }
        
        // Remplir la model.map de bâtiments au centre
        for (int i = 1; i < height -1; i++) {
            for (int j = 1; j < width -1; j++) {
                Random rd = new Random();
                int price = (rd.nextInt(5)+ 1)*100;
                // if(i == 1 || j == 1 || i == height - 2 || j == width - 2){
                    cases[i][j] = new Building(("(" + i + ", " + j + ")"), price, price/4, false, new Coordinate(j, i));
                // }
            }
        }

        //Placer en haut a droite pour des tests
        //cases[0][width-1] = new Teleport(new Coordinate(width-1,0));

        // Placer la case Start en haut à gauche
        cases[0][0] = new Start(new Coordinate(0, 0));

        // Placer la prison en bas à droite
        cases[height - 1][width - 1] = new Jail(new Coordinate(width - 1, height - 1));

    }

    private static void addRandomBonus(Map map,Cell[][] cells){
        ArrayList<Bonus> bonusList = new ArrayList<>();
        bonusList.add(new ToJail(new Coordinate(0,0)));
        bonusList.add(new Teleport(new Coordinate(0,0)));

        bonusList.add(new Luck(new Coordinate(0,0),"Chance"));
        bonusList.add(new Taxes(new Coordinate(0,0),"Impôt",500));
        bonusList.add(new Luck(new Coordinate(0,0),"Chance"));
        bonusList.add(new Luck(new Coordinate(0,0),"Chance"));
        bonusList.add(new Taxes(new Coordinate(0,0),"Facture",100));
        bonusList.add(new Luck(new Coordinate(0,0),"Chance"));

        ArrayList<Coordinate> coordinatesList = new ArrayList<>();
        Random random = new Random();

        for(int y=0;y< cells.length;y++){
            for(int x=0;x<cells[y].length;x++){

                if(map.cross(x,y)){
                    coordinatesList.add(new Coordinate(x,y));
                }
            }
        }
        for(Bonus b : bonusList){
            if(!coordinatesList.isEmpty()){
                int choseCoordinate = random.nextInt(coordinatesList.size());
                Coordinate cd = coordinatesList.get(choseCoordinate);
                b.setCoordinate(cd);
                cells[cd.getY()][cd.getX()] = b;
                coordinatesList.remove(cd);
            }
        }

    }

    private static void addFanchiseHeritage(Map map,Cell[][] cells){

        ArrayList<Franchise> franchisesList = new ArrayList<>();
        franchisesList.add(new EmptyHeritage(new Coordinate(0,0)));
        franchisesList.add(new EmptyRestaurant(new Coordinate(0,0)));
        franchisesList.add(new EmptyRestaurant(new Coordinate(0,0)));

        Random random = new Random();

        for(Franchise franchise : franchisesList){
            boolean isBuilding = false;
            while(!isBuilding){
                int x = random.nextInt(map.getWidth());
                int y = random.nextInt(map.getHeight());
                if(map.nextToRoad(x,y)){
                    cells[y][x] = franchise;
                    franchise.setCoordinate(new Coordinate(x,y));
                    isBuilding = true;
                }
            }
        }


    }

    public static void randomGeneration(Map map){
        generateBasicMap(map);
        Cell[][] cells = map.getMap();



        int height = map.getHeight();
        int width = map.getWidth();

        Random random = new Random();


        //Deux Boucles pour générer des lignes sur l'axe x et y

        //Générer au moins 2 lignes aléatoirement

        int nbXlines = 0;

        while (nbXlines<2){
            for(int xline=2;xline<width-2;xline++){
                int generate = random.nextInt(2);
                if(generate==1&&cells[1][xline-1] instanceof Building&&cells[1][xline+1] instanceof Building){
                    nbXlines++;
                    for(int j=1;j<height-1;j++){
                        cells[j][xline] = new Road(new Coordinate(xline,j));
                    }
                }
            }
        }

        //Vérifion si des paté de maison on une largeur supérieur à 3
        int countX =0;
        boolean toBig = false;

        for(int i=1;i<width-1;i++){
            countX++;
            if(cells[1][i] instanceof Road){
                if (countX>3){
                    toBig = true;
                    break;
                }
            }
        }


        int nbYlines = 0;

        int countY = 1;

        while(nbYlines<2){
            for(int y=2;y<height-2;y++){
                countY++;
                int generate = random.nextInt(2);
                if(generate==1&&cells[y-1][1] instanceof Building&&cells[y+1][1] instanceof Building||(toBig&&countY>3)) {
                    countY = 0;
                    nbYlines++;
                    for (int x = 1; x < width - 1; x++) {
                        cells[y][x] = new Road(new Coordinate(x, y));
                    }
                }
            }
        }


        //on parcour X


        //Compte les lignes de buildings ajouter en X pour éviter de creer des patés de maison trop large et avoir des maps equitable
        int countXBuildingLine = 0;

        for(int i=2;i<width-2;i++){
            //Si c'est une route
            if(cells[1][i] instanceof Road){
                //On initialise la profonfeur j (qui va parcourir) et la prochaine intersection
                int depth = 1;
                int nextIntersectionY = 0;
                int j = 1;
                //tant que j n'est pas arrivé au bout
                while(j<height-1) {

                    //On génère aléatoirement si un batiment sera ajouté
                    int generate = random.nextInt(4);


                    //On ne peut pas combler plus des deux tiers des routes
                    int maxGenerationX = 2*(((nbXlines*nbYlines)+nbXlines)/3);



                    boolean add = (generate!=0)&&countXBuildingLine<maxGenerationX;



                    //tant qu'on est pas allé au bout
                    while (depth < height - 1) {
                        //si on trouve une intersection on stop

                        if((depth+j-1>=height-1)||map.intersection(i,depth+j-1)){
                            break;
                        } else {
                            //on augmente la profondeur
                            depth++;
                        }
                    }

                    nextIntersectionY = (j)+depth-1;

                    if(map.toWide(i,j)&&depth>4){
                        add = false;
                    }


                    while (j<nextIntersectionY){
                        if(add){
                            Random rd = new Random();
                            int price = (rd.nextInt(5)+ 1)*100;
                            cells[j][i] = new Building("test",price,price/4,false,new Coordinate(i,j));
                        }
                        j++;
                    }
                    if(add) countXBuildingLine++;
                    j++;
                    depth = 1;

                }
            }
        }

        int countYBuildingsLine = 0;

        for(int t=2;t<height-2;t++){

            if(cells[t][1] instanceof Road){
                int depth = 1;
                int nextIntersectionX = 1;
                int j = 1;

                //permet d'empecher les impasses
                //lorsque l'intersection a un immeuble au dessus

                int startalternate = random.nextInt(2);
                boolean alternate = startalternate==0;
                boolean activateAlternate = false;


                while(j<width-1) {

                    int generate = random.nextInt(4);
                    int maxGenerationY = 2*(((nbXlines*nbYlines)+nbYlines)/3);
                    boolean add = (generate!=0)&&countYBuildingsLine<maxGenerationY;



                    while (depth < width - 1) {

                        if((depth+j-1>=height-1)||map.intersection(depth+j-1,t)||map.yIntersection(depth+j-1,t)){
                            //(cells[t-1][depth+j-1]) instanceof Building
                            if(map.yPossibleImpass(depth+j-1,t)) activateAlternate=true;

                            if(alternate&&(map.yPossibleImpass(depth+j-1,t)||activateAlternate)){
                                add = false;
                            }
                            alternate = !alternate;
                            break;
                        }else {

                            if(map.getCell(depth+j-1,t-1) instanceof Building&&map.getCell(depth+j-1,t+1) instanceof Road||
                                    map.getCell(depth+j-1,t-1) instanceof Road&&map.getCell(depth+j-1,t+1) instanceof Building){
                                add = false;
                            }
                            depth++;

                        }
                    }


                    nextIntersectionX = depth+j-1;




                    if(depth>3&&(map.topMoreThan(3,depth+j-2,t)|| map.downMoreThan(3,depth+j-2,t))){
                        add = false;
                    }

                    if(map.toHigh(j,t)&&depth>4){
                        add = false;
                    }


                    while (j<nextIntersectionX){
                        if(add){
                            Random rd = new Random();
                            int price = (rd.nextInt(5)+ 1)*100;
                            cells[t][j] = new Building("test",price,price/4,false,new Coordinate(j,t));
                        }
                        j++;
                    }
                    if(add) countYBuildingsLine++;
                    j++;
                    depth = 1;
                }
            }
        }

        addRandomBonus(map,cells);
        addFanchiseHeritage(map,cells);
        addGarden(map,cells);


    }


    private static void addGarden(Map map,Cell[][] cells){
        Random rd = new Random();
        for(int y=2;y< cells.length-2;y++){
            for(int x=2;x<cells[y].length;x++){
                if(map.detectGarden(x,y)){
                    Random random = new Random();
                    int price = rd.nextInt((850 - 650) + 1) + 650;
                    cells[y][x] = new Garden(new Coordinate(x,y),price);
                    map.linkedGarden((Garden)cells[y][x]);
                }
            }
        }
    }

    private static ArrayList<Cell> unlikeRoadList(Map map){
        Cell[][] cells = map.getMap();
        ArrayList<Cell> res = new ArrayList<>();
        int height = map.getHeight();
        int width = map.getWidth();

        for(int i=1;i<height-1;i++){
            for(int j=1;j<width;j++){
                if(map.unlinkRoad(j,i)){
                    res.add(cells[i][j]);
                }
            }
        }
        return  res;
    }




    private static void printMap(Cell[][] cases){
        for(int y=0;y<cases.length;y++){
            System.out.println();
            for(int x=0;x<cases[y].length;x++){
                if(cases[y][x] instanceof Building){
                    System.out.print("X");
                }else{
                    System.out.print(" ");
                }
            }
        }
    }

    
}
