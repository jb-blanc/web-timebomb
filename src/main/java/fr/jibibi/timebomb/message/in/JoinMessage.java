package fr.jibibi.timebomb.message.in;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
public class JoinMessage {

    @Getter
    @Setter
    private String playerName;

}
