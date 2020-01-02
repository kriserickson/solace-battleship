package com.solace.battleship.events;

import java.util.Objects;

public class MatchEnd {

    private int player1Score;
    private int player2Score;
    private String sessionId;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MatchEnd matchEnd = (MatchEnd) o;
        return player1Score == matchEnd.player1Score &&
                player2Score == matchEnd.player2Score &&
                sessionId.equals(matchEnd.sessionId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(player1Score, player2Score, sessionId);
    }

    public MatchEnd(){

    }

    public int getPlayer1Score() {
        return player1Score;
    }

    public void setPlayer1Score(int player1Score) {
        this.player1Score = player1Score;
    }

    public int getPlayer2Score() {
        return player2Score;
    }

    public void setPlayer2Score(int player2Score) {
        this.player2Score = player2Score;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }
}
