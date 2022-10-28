package fr.jibibi.timebomb.bean;

import fr.jibibi.timebomb.enums.CardType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Random;

@Getter @Setter
@AllArgsConstructor
@ToString
public class Card {
    private static Random random = new Random();
    private static int nextId = random.nextInt(100);

    private CardType type;
    private int id;

    public Card(CardType type){
        this.type = type;
        this.id = nextId++;
    }
}
