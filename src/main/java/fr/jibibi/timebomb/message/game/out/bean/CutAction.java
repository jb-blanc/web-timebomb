package fr.jibibi.timebomb.message.game.out.bean;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class CutAction {
    String name;
    int index;
    String type;
}
