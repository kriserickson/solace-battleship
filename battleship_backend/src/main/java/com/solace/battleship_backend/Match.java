package com.solace.battleship_backend;

public class Match {
    private String id = null;

    public Match(String matchId) {
        this.id = matchId;
    }

    public String getId() {
        return id;
    }
}
