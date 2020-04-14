package fr.jibibi.timebomb.bean;

import fr.jibibi.timebomb.enums.CardType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
public class Card {

    @Getter @Setter
    private CardType type;

    @Override
    public String toString() {
        return type.name();
    }
}
