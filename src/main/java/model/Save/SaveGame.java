package model.Save;

import model.Game;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

public class SaveGame {
    public static void save(Game game, String fileName) {
        try (FileOutputStream fileOut = new FileOutputStream(fileName);
             ObjectOutputStream out = new ObjectOutputStream(fileOut)) {
            out.writeObject(game);
            System.out.println("La partie a bien été enregistré !");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
