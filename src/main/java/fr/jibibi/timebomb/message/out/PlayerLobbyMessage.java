package fr.jibibi.timebomb.message.out;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter @Setter
public class PlayerLobbyMessage {

    private String name;
    private boolean present;

}
