package fr.jibibi.timebomb.message.out.bean;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class CardInfo {
    String type;
    Boolean cut;
    Integer id;
}
