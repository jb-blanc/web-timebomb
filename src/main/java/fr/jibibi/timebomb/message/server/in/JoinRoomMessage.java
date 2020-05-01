package fr.jibibi.timebomb.message.server.in;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class JoinRoomMessage {

    private String gameUUID;
    private String playerUUID;

}