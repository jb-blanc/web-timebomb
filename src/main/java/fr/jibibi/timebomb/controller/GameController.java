package fr.jibibi.timebomb.controller;

import fr.jibibi.timebomb.bean.Card;
import fr.jibibi.timebomb.bean.Player;
import fr.jibibi.timebomb.enums.Teams;
import fr.jibibi.timebomb.message.in.CutMessage;
import fr.jibibi.timebomb.message.in.JoinMessage;
import fr.jibibi.timebomb.message.out.CardsMessage;
import fr.jibibi.timebomb.message.out.GameStateMessage;
import fr.jibibi.timebomb.message.out.bean.CardInfo;
import fr.jibibi.timebomb.message.out.bean.CutAction;
import fr.jibibi.timebomb.message.out.bean.PlayerCardInfo;
import fr.jibibi.timebomb.service.GameService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.stereotype.Controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

@Controller
public class GameController {

    private static final Logger log = LoggerFactory.getLogger(GameController.class);

    @Autowired
    private GameService gameService;

    @MessageMapping("/start")
    @SendTo("/server/started")
    public GameStateMessage startGame(String message){
        log.info("Game is starting : {}", message);
        gameService.startGame();
        return generateGameState(null);
    }

    @MessageMapping("/cut")
    @SendTo("/server/state")
    @SuppressWarnings("OptionalGetWithoutIsPresent")
    public GameStateMessage playerPlayed(CutMessage message){
        log.info("Player has played : {}", message);
        Card c = gameService.getPlayers().stream().filter(p -> p.getName().equals(message.getPlayer())).findFirst().get().removeCard(message.getId());
        gameService.cardCut(message.getPlayer(), c);
        gameService.decreaseTurnCount();

        return generateGameState(new CutAction(message.getPlayer(), message.getId(), c.getType().toString()));
    }

    @MessageMapping("/nextRound")
    @SendTo("/server/newRound")
    public GameStateMessage newRound(String message){
        GameStateMessage gameState = generateGameState(null);

        gameService.endTurn();

        return gameState;
    }


    @MessageMapping("/playersCards")
    @SendToUser("/server/playerCard")
    public CardsMessage retrieveCards(String playerName){
        log.info("Sending cards for player {}", playerName);
        CardsMessage message = new CardsMessage();

        for(Player p : gameService.getPlayers()){
            if(p.getName().equals(playerName)){
                message.setMyTeam(p.getTeam());
                message.setPlayer(p.getCards().stream().map(pair -> new CardInfo(pair.getValue1().getType().toString(), pair.getValue2(), pair.getValue1().getId())).collect(Collectors.toList()));
            }
            else{
                message.getOpponents().add(new PlayerCardInfo(p.getName(), p.getCards()));
            }
        }

        message.setGameState(generateGameState(null));

        return message;
    }


    @MessageMapping("/askReveal")
    @SendToUser("/server/reveal")
    public HashMap<String, Object> revealRoles(JoinMessage player){
        log.info("Sending reveal to player {}", player.getPlayerName());
        HashMap<String, Object> reveals = new HashMap<>();

        HashMap<String, Teams> reveal = new HashMap<>();
        List<PlayerCardInfo> playersCards = new ArrayList<>();

        gameService.getPlayers().forEach(p -> reveal.put(p.getName(),p.getTeam()));

        for(Player p : gameService.getPlayers()){
            playersCards.add(new PlayerCardInfo(p.getName(), p.getCards(), true));
        }


        reveals.put("roles", reveal);
        reveals.put("playersCards", playersCards);
        return reveals;
    }


    private GameStateMessage generateGameState(CutAction cutAction) {
        Teams winner = gameService.getWinner();

        GameStateMessage gameState = new GameStateMessage();
        gameState.setCut(cutAction);
        gameState.setGameEnded(winner != null);
        gameState.setWinner(winner);
        gameState.setCurrentPlayer(gameService.getCurrentPlayer().getName());
        gameState.setCutRemaining(gameService.getRemainingCuts());
        gameState.setRoundRemaining(gameService.getCurrentTurnCardCount() - 1);
        gameState.setTurnEnded(gameService.isEndTurn());
        gameState.setDefusedWires(gameService.getDefusedWiresCount());
        gameState.setRemainingWires(gameService.getPlayers().size() - gameService.getDefusedWiresCount());
        if(winner != null){
            HashMap<String, Teams> reveal = new HashMap<>();
            gameService.getPlayers().forEach(p -> reveal.put(p.getName(),p.getTeam()));
            gameState.setReveal(reveal);
        }
        else{
            gameState.setReveal(null);
        }

        return gameState;
    }
}
