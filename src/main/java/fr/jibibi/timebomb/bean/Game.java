package fr.jibibi.timebomb.bean;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
public class Game {

    private UUID gameId;
    private List<User> users;

    public Game(){
        this.gameId = UUID.randomUUID();
        this.users = new ArrayList<>();
    }
}
