package gui;

import states.*;

//Simple enum to keep track of the current state of the game

public enum GameStates {
    MENU(Menu.getInstance()),
    ADDPLAYERS(AddPlayers.getInstance()),
    GAMEPLAY(Gameplay.getInstance()),
    WINROUND(WinRound.getInstance()), SAVEMENU(SaveMenu.getInstance());

    private State currentState;

    private GameStates(State initial_state) {
        currentState = initial_state;
    }

    public State getState(){
        return currentState;
    }

    public String showState(){
        return this.name();
    }

    public void changeState(State s){
        allState();

        currentState.exit();
        s.enter();
        App.currentInstance = s;

        allState();
    }

    public void allState(){
        String s = "TOUT LES STATES : \n" +
                "MENU : " + MENU.showState()
                + "\nADDPLAYERS : " + ADDPLAYERS.showState()
                + "\nGAMEPLAY : " + GAMEPLAY.showState()
                + "\nWINROUND : " + WINROUND.showState() + "\n\n\n";
    }

}
