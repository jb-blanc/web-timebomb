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
    public static Random random = new Random();
    public static int NEXT_ID = random.nextInt(100);

    private CardType type;
    private int id;

    public Card(CardType type){
        this.type = type;
        this.id = NEXT_ID++;
    }
}
