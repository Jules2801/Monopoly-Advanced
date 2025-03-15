package model.Cards;

import java.io.Serializable;

public class Shield extends Card implements Serializable {

    private static final long serialVersionUID = 1L;

    public Shield() {
        super("Bouclier", "Même si nous vivons dans un pays incroyable, nous ne sommes pas épargné des catastrophes naturelles... Vraiment ? Utiliser cette carte dans un de vos biens pour en être protégé !", "#BDF9FF", "shield");
    }
}
