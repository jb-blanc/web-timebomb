package fr.jibibi.timebomb.controller;

import fr.jibibi.timebomb.message.in.JoinMessage;
import fr.jibibi.timebomb.message.in.QuitMessage;
import fr.jibibi.timebomb.message.out.InfoMessage;
import fr.jibibi.timebomb.message.out.PlayerLobbyMessage;
import fr.jibibi.timebomb.service.GameService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.stereotype.Controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
public class LobbyController {

    private static final Logger log = LoggerFactory.getLogger(LobbyController.class);

    @Autowired
    private GameService gameService;

    @MessageMapping("/updatePlayers")
    @SendTo("/server/players")
    public List<PlayerLobbyMessage> updatePlayerList(String message){
        return gameService.getPlayers().stream().map(p -> new PlayerLobbyMessage(p.getName(), true)).collect(Collectors.toList());
    }

    @MessageMapping("/join")
    @SendToUser("/server/joined")
    public Map<String,Boolean> joinGame(JoinMessage message){
        String playerName = message.getPlayerName();

        if(!gameService.isGameRunning()) {
            log.info("New player joined : {}", playerName);
            gameService.newPlayer(playerName);
        }

        HashMap<String, Boolean> map = new HashMap<>();

        map.put("connected",!gameService.isGameRunning());
        return map;
    }

    @MessageMapping("/quit")
    @SendTo("/server/player")
    public PlayerLobbyMessage quitGame(QuitMessage message){
        String playerName = message.getPlayerName();
        log.info("Player quit : {}", playerName);
        gameService.removePlayer(playerName);
        return new PlayerLobbyMessage(playerName, false);
    }

    @SendTo("/server/info")
    public InfoMessage sendMessage(String message){
        return new InfoMessage(message);
    }

}
