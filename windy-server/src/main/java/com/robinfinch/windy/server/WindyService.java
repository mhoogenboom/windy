package com.robinfinch.windy.server;

import com.robinfinch.windy.core.game.Player;
import com.robinfinch.windy.core.game.RemoteGameStatus;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@EnableAutoConfiguration
@RequestMapping("/game/{gameId}")
public class WindyService {

    private GamesManager gamesManager = new GamesManager();

    @RequestMapping(value = "/white", method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void connectWhiteToGame(@PathVariable String gameId, @RequestBody Player player) {

        gamesManager.connectWhiteToGame(player.getName(), gameId);
    }

    @RequestMapping(value = "/black", method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void connectBlackToGame(@PathVariable String gameId, @RequestBody Player player) {

        gamesManager.connectBlackToGame(player.getName(), gameId);
    }

    @RequestMapping(value = "/status", method = RequestMethod.GET)
    @ResponseStatus(HttpStatus.OK)
    RemoteGameStatus getStatus(@PathVariable String gameId) {
        return gamesManager.getStatus(gameId);
    }

    public static void main(String[] args) throws Exception {
        SpringApplication.run(WindyService.class, args);
    }
}
