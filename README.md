# Monopoly (VR1b)
![Monopoly](src/main/resources/Menu/monopoly-logo.png)

Jeu inspiré du Monopoly

- Agrane Benlalam Adam Christophe
- Cisse   Sekou
- Fernez  Dimitri
- Peris   Jules
- Maissouradze    Nicolas

## Lancement du jeu
Il faut java 21 et lancer la commande suivante : 
```
./gradlew run
```

## Rapport
`Rapport_vr1b.pdf`

## Structure du projet
Les fichiers sources sont dans `src/main/java/monopoly/`

Les ressources sont dans `src/main/resources/`

## Règles du jeu
Le jeu se joue de 2 à 5 joueurs, chacun possède un pion et commence avec 1000€ le but du jeu est d'être le dernier joueur en jeu.
le joueur lance un dé à 6 faces et avance du nombre de cases correspondant au résultat du dé, il peut acheter ou amélorer la case adjacente sur laquelle il est tombé.
Si un joueur tombe sur une case appartenant à un autre joueur, il doit payer un loyer à ce joueur.
Si un joueur n'a plus d'argent et qu'il ne peut plus vendre , il est éliminé du jeu.

## Eléments de jeu
 - Les Batiments qui correspond a la couleur du joueur, le niveau du batiment et la direction du batiment

 ![batiment constructible](src/main/resources/Buildings/Level0.png)
 ![batiment Niv 1](src/main/resources/Buildings/Green/L1_South_Green.png)
 ![batiment Niv 2](src/main/resources/Buildings/Blue/L2_South_Blue.png)
 ![batiment Niv 3](src/main/resources/Buildings/Orange/L3_South_Orange.png)
 ![batiment Niv 4](src/main/resources/Buildings/Red/L4_South_Red.png)
 ![batiment détruit](src/main/resources/Buildings/Destroy/L1_South_Destroy.png)


- Les cartes


- Les cases bonus

![bonus Jail](src/main/resources/Map/Jail.png)
![bonus Lucky](src/main/resources/Map/Lucky.png)
![bonus StartCell](src/main/resources/Map/StartCell.png)
![bonus TaxeIcon](src/main/resources/Map/TaxeIcon.png)
![bonus TeleportCell](src/main/resources/Map/TeleportCell.png)
![bonus ToJail](src/main/resources/Map/ToJail.png)

- Les joueurs
![joueur blue](src/main/resources/Players/BluePlayer.png)
![joueur green](src/main/resources/Players/GreenPlayer.png)
![joueur orange](src/main/resources/Players/OrangePlayer.png)
![joueur purple](src/main/resources/Players/PurplePlayer.png)
![joueur red](src/main/resources/Players/RedPlayer.png)

