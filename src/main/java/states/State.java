package states;

import gui.App;
import gui.StateMethods;
import javafx.scene.layout.BorderPane;

import java.awt.*;

public abstract class State implements StateMethods {
    private final BorderPane gameRoot = App.root;
    private final BorderPane pane;
    private boolean firstTime = true;
    private final String stateName;

    public State(String stateName) {
        this.stateName = stateName;
        this.pane = new BorderPane();
    }

    public BorderPane getPane(){return pane;}
    public BorderPane getGameRoot(){return gameRoot;}

    public String showState() {
        return stateName;
    }

    public void updateGraphics(Graphics g){};

    public void updateLogic(long deltaT){};

    /**
     * Méthode permettant de modifier toutes informations nécessaires lors d'un changement d'état.
     */
    public void exit(){};

    public void transitionTo(State s){};

    /**
     * Méthode permettant d'afficher l'état actuel dans la fenêtre de l'application.
     */
    public void enter(){};

    public boolean isFirstTime(){return firstTime;};
    public void firstTimeSet(){firstTime = false;}

    public static void reloadInstance() {
    }

    public static State getInstance(){return null;};
}
