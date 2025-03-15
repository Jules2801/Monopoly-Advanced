package model.Save;

import controller.GameplayController;
import gui.App;
import model.Game;
import states.Gameplay;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;

public class LoadGame {
    public static Game load(String fileName) {
        Game game = null;
        try (FileInputStream fileIn = new FileInputStream(fileName);
             ObjectInputStream in = new ObjectInputStream(fileIn)) {
            game = (Game) in.readObject();
            App.gameModel = game;
            System.out.println("Game loaded successfully.");
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return game;
    }

}

