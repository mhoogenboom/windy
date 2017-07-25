package com.robinfinch.windy.server;

import com.robinfinch.windy.core.game.Player;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.web.bind.annotation.*;

@RestController
@EnableAutoConfiguration
@RequestMapping("/game/{gameId}")
public class WindyController {

    private GamesManager gamesManager = new GamesManager();

    @RequestMapping(value = "/white", method = RequestMethod.POST)
    void connectWhiteToGame(@PathVariable String gameId, @RequestBody Player player) {

        gamesManager.connectWhiteToGame(player.getName(), gameId);
    }

    @RequestMapping(value = "/black", method = RequestMethod.POST)
    void connectBlackToGame(@PathVariable String gameId, @RequestBody Player player) {

        gamesManager.connectBlackToGame(player.getName(), gameId);
    }

    public static void main(String[] args) throws Exception {
        SpringApplication.run(WindyController.class, args);
    }
}
