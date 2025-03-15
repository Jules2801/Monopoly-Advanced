package gui;

import states.State;

public interface StateMethods {

    public void updateLogic(long deltaT);

    public void exit();

    public void transitionTo(State s);

    public void enter();

    public static State getInstance(){return null;};
}
