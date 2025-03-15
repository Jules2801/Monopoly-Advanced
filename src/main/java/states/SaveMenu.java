
package states;

import gui.App;
import gui.GameStates;
import gui.Style;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import static gui.Style.skyBackground;

import model.Game;
import model.Save.LoadGame;

public class SaveMenu extends State {

    public static SaveMenu INSTANCE = new SaveMenu();
    public static SaveMenu getInstance() { return INSTANCE; }

    private Button saveButton;

    public SaveMenu() {
        super("Save Menu");
    }

    @Override
    public void enter() {
        StackPane backgroundColor = new StackPane();
        backgroundColor.setBackground(skyBackground());

        VBox mainBox = makeMainBox();
        backgroundColor.getChildren().add(mainBox);

        getGameRoot().setCenter(backgroundColor);
    }

    private VBox makeMainBox() {
        VBox box = new VBox(50);
        box.setAlignment(Pos.CENTER);

        saveButton = createSaveButton();
        box.getChildren().add(saveButton);

        return box;
    }

    private Button createSaveButton() {
        Button button = new Button("Sauvegarde");
        Style.bigButtonSize(button);
        button.setFont(Style.titleFont);
        Style.putListenerInButton(button);

        button.setOnAction(e -> {

            LoadGame.load("game.sav");
            GameStates.SAVEMENU.changeState(GameStates.GAMEPLAY.getState());
        });

        return button;
    }


    @Override
    public void exit() {
        // Ajouter ici toute logique nécessaire lors de la sortie de l'état SaveMenu
    }
}

