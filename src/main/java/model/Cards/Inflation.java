package model.Cards;

import java.io.Serializable;

public class Inflation extends Card implements Serializable {

    private static final long serialVersionUID = 1L;

    public Inflation() {
        super("Inflation", "Oh mon dieu, l'inflation touche notre beau pays ! Mais ce n'est pas grave, parce que cela signifie qu'on peut augmenter les prix et dire que c'est de sa faute ! Vraiment, incroyable le capitalisme <3", "FFFCBD", "inflation");
    }
}
