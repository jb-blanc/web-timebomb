package fr.jibibi.timebomb.service;

import fr.jibibi.timebomb.bean.Card;
import fr.jibibi.timebomb.bean.GameSettings;
import fr.jibibi.timebomb.bean.Pair;
import fr.jibibi.timebomb.bean.Player;
import fr.jibibi.timebomb.controller.GameController;
import fr.jibibi.timebomb.controller.LobbyController;
import fr.jibibi.timebomb.enums.CardType;
import fr.jibibi.timebomb.enums.Teams;
import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Service
public class GameService {
    private static final Logger log = LoggerFactory.getLogger(GameService.class);

    @Autowired
    private LobbyController lobbyController;
    @Autowired
    private GameController gameController;

    @Setter @Getter
    private List<Player> players;
    @Setter @Getter
    private Deque<Card> cards;
    @Setter @Getter
    private List<Card> discarded;
    @Setter @Getter
    private Player currentPlayer;
    @Setter @Getter
    private Integer currentTurnCardCount;
    @Getter @Setter
    private Integer remainingCuts;
    @Getter @Setter
    private GameSettings settings;
    @Getter @Setter
    private boolean gameRunning;

    @PostConstruct
    public void init(){
        this.players = new ArrayList<>();
        this.discarded = new ArrayList<>();
        this.gameRunning = false;
    }

    public void reset() {
        this.gameRunning = false;
        this.players.forEach(Player::reset);
        cards = new ArrayDeque<>();
        discarded = new ArrayList<>();
        this.currentPlayer = null;
        this.currentTurnCardCount = 5;
        this.remainingCuts = players.size();
    }

    public void cardCut(String player, Card c){
        remainingCuts -= 1;
        this.discarded.add(c);
        this.currentPlayer = players.stream().filter(p -> p.getName().equals(player)).findFirst().get();
    }

    public boolean isEndTurn(){
        return this.remainingCuts == 0;
    }

    public void decreaseTurnCount(){
        if(isEndTurn()) {
            this.currentTurnCardCount -= 1;
        }
    }

    public Integer getDefusedWiresCount(){
        return Math.toIntExact(discarded.stream().filter(p -> p.getType().equals(CardType.DEFUSE)).count());
    }

    public void endTurn(){
        List<Card> cards = players.stream().map(Player::getCards).flatMap(Collection::stream).filter(pair -> !pair.getValue2()).map(Pair::getValue1).collect(Collectors.toList());
        Collections.shuffle(cards);
        this.cards = new ArrayDeque<>(cards);
        this.remainingCuts = players.size();
        players.forEach(p -> p.setCards(new ArrayList<>()));
        dealCards(this.currentTurnCardCount);
    }

    public Teams getWinner(){
        AtomicBoolean bigben = new AtomicBoolean(false);
        for(Player p : players){
            p.getCards().stream().filter(Pair::getValue2).forEach(pair -> {
                if(pair.getValue1().getType().equals(CardType.BIGBEN)){
                    bigben.set(true);
                }
            });
        };

        Teams winner = null;

        if(bigben.get()) { winner = Teams.MORIARTY;}
        else if(getDefusedWiresCount().equals(settings.getDefuseCardCount())){ winner = Teams.SHERLOCK;}
        else if(isEndTurn() && currentTurnCardCount <= 1){ winner = Teams.MORIARTY;}

        if(winner != null){
            gameRunning = false;
        }

        return winner;
    }

    public Player newPlayer(String name) {
        Optional<Player> p = this.players.stream().filter(player -> player.getName().equals(name)).findFirst();
        if(!p.isPresent()) {
            Player player = new Player(name);
            players.add(player);
            return player;
        }
        return p.get();
    }

    public void removePlayer(String name) {
        this.players.stream().filter(player -> player.getName().equals(name)).findFirst().ifPresent(player -> players.remove(player));
    }

    public void startGame() {
        this.reset();
        if (players.size() < 4 || players.size() > 8) {
            lobbyController.sendMessage("Nombre de joueurs incorrect");
        }
        else{
            this.gameRunning = true;
            if(settings == null){
                settings = new GameSettings();
            }
            settings.newGame(players.size());
            createCards();
            createTeams();
            dealCards(currentTurnCardCount);
            pickRandomCurrentPlayer();
            displayGameState();
        }
    }

    private void pickRandomCurrentPlayer() {
        Random rd = new Random();
        currentPlayer = players.get(rd.nextInt(players.size()));
    }

    private void displayGameState() {
        log.info("============= GAME STATE ================");
        log.info("Current player : {}", currentPlayer.getName());
        for (Player p : players) {
            log.info(p.toString());
        }
        log.info("=========================================");
    }

    private void createTeams() {
        int countMori = 0;
        int countSher = 0;
        Random rand = new Random();
        for(Player p : players){
            if(countMori == settings.getMoriartyCount()){
                p.setTeam(Teams.SHERLOCK);
                countSher += 1;
            }
            else if(countSher == settings.getSherlockCount()){
                p.setTeam(Teams.MORIARTY);
                countMori += 1;
            }
            else{
                int r = rand.nextInt(1000);
                if(r < 500){
                    p.setTeam(Teams.SHERLOCK);
                    countSher += 1;
                }
                else{
                    p.setTeam(Teams.MORIARTY);
                    countMori += 1;
                }
            }
        }
    }

    private void createCards() {
        List<Card> lcards = new ArrayList<>();
        lcards.add(new Card(CardType.BIGBEN));
        for(int i=0;i<settings.getDefuseCardCount();i++){
            lcards.add(new Card(CardType.DEFUSE));
        }
        for(int i=0;i<settings.getNormalCardCount();i++){
            lcards.add(new Card(CardType.NORMAL));
        }
        Collections.shuffle(lcards);
        cards = new ArrayDeque<>(lcards);
    }

    private void dealCards(int cardCount) {
        for(int i=0; i<cardCount; i++) {
            for (Player p : players) {
                p.addCard(cards.poll());
            }
        }
    }

}
