package fr.jibibi.timebomb.message.game.out;

import fr.jibibi.timebomb.enums.Teams;
import fr.jibibi.timebomb.message.game.out.bean.CutAction;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashMap;

@NoArgsConstructor
@AllArgsConstructor
@Getter @Setter
public class GameStateMessage {

    private String currentPlayer;
    private Boolean gameEnded;
    private Teams winner;
    private CutAction cut;
    private Integer cutRemaining;
    private Integer roundRemaining;
    private Integer defusedWires;
    private boolean turnEnded;
    private HashMap<String, Teams> reveal;

}
