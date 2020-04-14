package fr.jibibi.timebomb.bean;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
public class GameSettings {

    @Setter @Getter
    private Integer playerCount;
    @Setter @Getter
    private Integer sherlockCount;
    @Setter @Getter
    private Integer moriartyCount;
    @Setter @Getter
    private Integer normalCardCount;
    @Setter @Getter
    private Integer defuseCardCount;
    @Setter @Getter
    private Integer bigBenCount;
    @Setter @Getter
    private Integer totalCardCount;

    public void newGame(Integer playerCount){
        this.playerCount = playerCount;
        this.bigBenCount = 1;
        this.defuseCardCount = playerCount;
        this.totalCardCount = playerCount * 5;
        this.normalCardCount = totalCardCount - defuseCardCount - bigBenCount;
        switch (playerCount){
            case 4:
            case 5:
                this.sherlockCount = 3;
                this.moriartyCount = 2;
                break;
            case 6:
                this.sherlockCount = 4;
                this.moriartyCount = 2;
                break;
            case 7:
            case 8:
                this.sherlockCount = 5;
                this.moriartyCount = 3;
                break;
        }
    }

    @Override
    public String toString() {
        return String.format(
                "Game : [%d Mor. vs %d Sher.][%d normals; %d defuse; %d BigBen][%s cards]",
                moriartyCount,
                sherlockCount,
                normalCardCount,
                defuseCardCount,
                bigBenCount,
                totalCardCount);
    }

}
