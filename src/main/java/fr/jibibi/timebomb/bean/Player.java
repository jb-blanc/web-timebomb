package fr.jibibi.timebomb.bean;

import fr.jibibi.timebomb.enums.Teams;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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
        Optional<Pair<Card, Boolean>> first = cards.stream().filter(p -> p.getValue1().getId() == id).findFirst();
        if(first.isPresent()) {
            Pair<Card, Boolean> pair = first.get();
            pair.setValue2(true);
            return pair.getValue1();
        }
        else
            return null;
    }

    @Override
    public String toString() {
        return String.format("Player %s (%s) : %s", name, team.name(), cards.stream().map(Pair::toString).collect(Collectors.joining(",")));
    }
}
