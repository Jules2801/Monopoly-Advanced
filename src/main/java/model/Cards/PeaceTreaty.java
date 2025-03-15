package model.Cards;

import java.io.Serializable;

public class PeaceTreaty extends Card implements Serializable {

    private static final long serialVersionUID = 1L;

    public PeaceTreaty() {
        super("Traité de paix", "Lorsque vous cliquez dans un immeuble appartenant déjà à un joueur, vous pouvez aquérir son bien gratuitement et en prime le loyer du nouveau bien augmente de 15%", "#C1FFBD", "peacetreaty");
    }
}
