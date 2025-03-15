package model;


public enum Event {
    TSUNAMI,VOLCANO,METEOR;


    public String toString(){
        switch (this){
            case TSUNAMI -> {return "Un tsunami a englouti la ville!";}
            case VOLCANO -> {return "Un volcan a deversé sa lave sur la ville!";}
            case METEOR -> {return "Une météorite c'est écrasée sur la ville!";}
            default -> {return "default";}
        }
    }
}

