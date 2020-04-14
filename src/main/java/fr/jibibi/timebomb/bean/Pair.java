package fr.jibibi.timebomb.bean;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class Pair<E,S> {

    E value1;
    S value2;

    @Override
    public String toString() {
        return "["+value1+"|"+value2+"]";
    }
}
