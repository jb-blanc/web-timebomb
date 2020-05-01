package fr.jibibi.timebomb.bean;

import fr.jibibi.timebomb.enums.Teams;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@NoArgsConstructor
public class Player {

    @Getter @Setter
    private String name;
    @Getter @Setter
    private Teams team;
    @Getter @Setter
    private List<Pair<Card,Boolean>> cards;

    public Player(String name) {
        this.name = name;
        this.cards = new ArrayList<>();
    }

    public void reset() {
        this.team = null;
        this.cards = new ArrayList<>();
    }

    public void addCard(Card poll) {
        cards.add(new Pair<>(poll,false));
    }

    public Card removeCard(int id){
        Pair<Card, Boolean> pair = cards.stream().filter(p -> p.getValue1().getId() == id).findFirst().get();
        pair.setValue2(true);
        return pair.getValue1();
    }

    @Override
    public String toString() {
        return String.format("Player %s (%s) : "+ cards.stream().map(Pair::toString).collect(Collectors.joining(",")), name, team.name());
    }
}
