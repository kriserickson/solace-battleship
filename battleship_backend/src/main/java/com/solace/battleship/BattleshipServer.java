package com.solace.battleship;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class BattleshipServer {

    private static final Logger log = LoggerFactory.getLogger(BattleshipServer.class);

    public static void main(String[] args) {
        SpringApplication.run(BattleshipServer.class, args);
    }

}