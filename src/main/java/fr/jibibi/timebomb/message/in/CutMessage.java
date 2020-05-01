package fr.jibibi.timebomb.message.in;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
public class CutMessage {

    @Setter @Getter
    private String player;
    @Setter @Getter
    private Integer id;

}
