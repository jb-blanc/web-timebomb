package fr.jibibi.timebomb.bean;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class User {

    private String name;
    private UUID uuid;

    public User(String name){
        this.name = name;
        this.uuid = UUID.randomUUID();
    }

}
