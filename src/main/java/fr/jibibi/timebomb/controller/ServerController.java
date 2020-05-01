package fr.jibibi.timebomb.controller;

import fr.jibibi.timebomb.bean.Game;
import fr.jibibi.timebomb.bean.User;
import fr.jibibi.timebomb.exceptions.GameCreationFailesException;
import fr.jibibi.timebomb.exceptions.PlayerAlreadyExistsException;
import fr.jibibi.timebomb.message.server.in.CreateGameMessage;
import fr.jibibi.timebomb.message.server.in.ExitServerMessage;
import fr.jibibi.timebomb.message.server.in.JoinRoomMessage;
import fr.jibibi.timebomb.message.server.in.JoinServerMessage;
import fr.jibibi.timebomb.message.server.out.CreatedGameMessage;
import fr.jibibi.timebomb.message.server.out.JoinedMessage;
import fr.jibibi.timebomb.message.server.out.JoinedRoomMessage;
import fr.jibibi.timebomb.service.LobbyService;
import fr.jibibi.timebomb.service.PlayerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.stereotype.Controller;

@Controller
public class ServerController {

    private static final Logger log = LoggerFactory.getLogger(ServerController.class);

    @Autowired
    private LobbyService lobbyService;

    @Autowired
    private PlayerService playerService;

    @MessageMapping("/join")
    @SendToUser("/server/joined")
    public JoinedMessage updatePlayerList(JoinServerMessage message) throws PlayerAlreadyExistsException {
        String uuid = playerService.registerNewUser(message.getPlayer());
        log.info("New player joined server : {} => {}", uuid, message.getPlayer());
        return new JoinedMessage(uuid);
    }

    @MessageMapping("/create")
    @SendToUser("/server/created")
    public CreatedGameMessage createGame(CreateGameMessage message) throws GameCreationFailesException {
        String gameId = lobbyService.createNewGame(message.getPlayerUUID()).getGameId().toString();
        log.info("New game created by [{}]{} => {}", message.getPlayerUUID(), playerService.getUser(message.getPlayerUUID()).getName(), gameId);
        return new CreatedGameMessage(gameId);
    }

    @MessageMapping("/joinRoom")
    @SendToUser("/server/joinedRoom")
    public JoinedRoomMessage createGame(JoinRoomMessage message) throws GameCreationFailesException {
        Game g = lobbyService.getGame(message.getGameUUID());
        User user = playerService.getUser(message.getPlayerUUID());
        g.getUsers().add(user);
        log.info("Player [{}]{} joined game {}", user.getUuid(), user.getName(), message.getGameUUID());
        return new JoinedRoomMessage(message.getGameUUID(), g != null);
    }

    @MessageMapping("/quit")
    public void quitGame(ExitServerMessage message){
        log.info("Player [{}]{} has quit", message.getUuid(), playerService.getUser(message.getUuid()).getName());
        playerService.removeUser(message.getUuid());
    }

}
