package fr.jibibi.timebomb.message.out.bean;

import fr.jibibi.timebomb.bean.Card;
import fr.jibibi.timebomb.bean.Pair;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class PlayerCardInfo {
    String name;
    List<CardInfo> cards;

    public PlayerCardInfo(String name, List<Pair<Card, Boolean>> cards, boolean force){
        this.name = name;
        this.cards = new ArrayList<>();
        cards.forEach(pair -> {
            String type = pair.getValue1().getType().toString();
            Boolean cut = pair.getValue2();
            this.cards.add(new CardInfo((cut || force) ? type : "NOTVISIBLE", cut, pair.getValue1().getId()));
        });
    }

    public PlayerCardInfo(String name, List<Pair<Card, Boolean>> cards){
        this(name,cards,false);
    }
}
