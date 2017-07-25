package com.robinfinch.windy.server;

import com.robinfinch.windy.core.game.Game;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@EnableAutoConfiguration
public class WindyController {

    private GamesManager gamesManager = new GamesManager();

    @RequestMapping(value = "/game/white", method = RequestMethod.POST)
    void connectWhiteToGame(@RequestBody ConnectionRequest request) {

        gamesManager.connectWhiteToGame(request.getName(), request.getGameId());
    }

    @RequestMapping(value = "/game/black", method = RequestMethod.POST)
    void connectBlackToGame(@RequestBody ConnectionRequest request) {

        gamesManager.connectBlackToGame(request.getName(), request.getGameId());
    }
    
    public static void main(String[] args) throws Exception {
        SpringApplication.run(WindyController.class, args);
    }
}
