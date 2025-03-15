package controller;

import gui.GameStates;
import states.Menu;
import states.State;
import states.WinRound;

public class WinRoundController{
    public WinRound view;

    public WinRoundController(WinRound state){
        this.view = state;
    }

    public void replay(){
        GameStates.WINROUND.changeState(GameStates.MENU.getState());
    }

    public void quit(){System.exit(0);}

}
