package fr.jibibi.timebomb.message.out;

import fr.jibibi.timebomb.enums.Teams;
import fr.jibibi.timebomb.message.out.bean.CardInfo;
import fr.jibibi.timebomb.message.out.bean.PlayerCardInfo;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
public class CardsMessage {

    private Teams myTeam;
    private List<CardInfo> player;
    private List<PlayerCardInfo> opponents;
    private GameStateMessage gameState;

    public CardsMessage() {
        this.player = new ArrayList<>();
        this.opponents = new ArrayList<>();
    }

}
