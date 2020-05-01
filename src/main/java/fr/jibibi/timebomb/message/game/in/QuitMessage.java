package fr.jibibi.timebomb.message.game.in;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
public class QuitMessage {

    @Getter
    @Setter
    private String playerName;

}
