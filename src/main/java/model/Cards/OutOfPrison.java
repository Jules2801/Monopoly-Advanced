package model.Cards;

import java.io.Serializable;

public class OutOfPrison extends Card implements Serializable {

    private static final long serialVersionUID = 1L;

    public OutOfPrison() {
        super( "Sortie de prison", "Vous êtes actuellement en prison ? Pas de panique ! Grâce à l'avocat Sékou, vous avez un droit de véto et vous pouvez sortir de la prison juste en utilisant cette carte ! La magie de la corruption.", "#ECBDFF", "outofprison");
    }
}
