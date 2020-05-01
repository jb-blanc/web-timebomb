package fr.jibibi.timebomb.service;

import fr.jibibi.timebomb.bean.User;
import fr.jibibi.timebomb.exceptions.PlayerAlreadyExistsException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class PlayerService {
    private static final Logger log = LoggerFactory.getLogger(PlayerService.class);

    private Map<String, User> users = new ConcurrentHashMap<>();
    private List<String> usernames = new ArrayList<>();

    /**
     * Register a new user on the server
     * @param name the player name
     * @return the UUID associated with the user
     */
    public String registerNewUser(String name) throws PlayerAlreadyExistsException {
        if(!this.userNameExists(name)){
            User u = new User(name);
            users.put(u.getUuid().toString(), u);
            usernames.add(name);
            log.info("[NEW_USER][{}][{}]",u.getUuid(),name);
            return u.getUuid().toString();
        }
        throw new PlayerAlreadyExistsException("Username is already taken");
    }

    /**
     * Remove the suer from the server
     * @param uuid the user UUID to remove
     */
    public void removeUser(String uuid){
        if(users.containsKey(uuid)) {
            log.info("[REMOVE_USER][{}][{}]", uuid, users.get(uuid).getName());
            usernames.remove(users.get(uuid).getName());
            users.remove(uuid);
        }
    }

    /**
     * Retrieve a user by UUID
     * @param uuid the user UUID
     * @return the user found
     */
    public User getUser(String uuid){
        return users.get(uuid);
    }

    /**
     * Does the player with the given UUID exists
     * @param uuid the UUID to search for
     * @return true if he exists, false otherwise
     */
    public boolean userExists(String uuid){
        return this.users.containsKey(uuid);
    }

    /**
     * Does the player with the given name exists
     * @param name the name to search for
     * @return true if he exists, false otherwise
     */
    public boolean userNameExists(String name){
        return this.usernames.contains(name);
    }
}
