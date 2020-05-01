package fr.jibibi.timebomb.service;

import fr.jibibi.timebomb.bean.Game;
import fr.jibibi.timebomb.exceptions.GameCreationFailesException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class LobbyService {
    private static final Logger log = LoggerFactory.getLogger(LobbyService.class);

    @Autowired
    private PlayerService playerService;

    private Map<String, Game> games = new ConcurrentHashMap<>();

    /**
     * Create a new game on the server
     * @param userUUID the user creating the game
     * @return the created game
     * @throws GameCreationFailesException if the user is not registered on the server
     */
    public Game createNewGame(String userUUID) throws GameCreationFailesException {
        if(playerService.userExists(userUUID)) {
            Game g = new Game();
            games.put(g.getGameId().toString(), g);
            log.info("[NEW_GAME][{}][{}]", g.getGameId(), playerService.getUser(userUUID).getName());
            return g;
        }
        throw new GameCreationFailesException("You need to be a valid user to create a game.");
    }

    /**
     * Destroy the game on the server.
     * @param uuid UUID of the game to destroy
     */
    public void destroyGame(String uuid){
        games.remove(uuid);
    }

    /**
     * Retrieve a game from the UUID
     * @param uuid uuid of the game to find
     * @return the found game
     */
    public Game getGame(String uuid){
        return games.get(uuid);
    }

}
