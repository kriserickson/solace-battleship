package com.solace.battleship.engine;

import java.util.Objects;

import com.solace.battleship.events.GameStart;
import com.solace.battleship.events.Player;

/**
 * Object represents a game's session
 */
public class GameSession {

    private GameState gameState;

    private GameStart gameStart;
    private final String sessionId;
    private int player1Score;
    private int player2Score;
    private Player player1;
    private Player player2;

    public GameSession(String sessionId) {
        this.sessionId = sessionId;
        this.gameState = GameState.WAITING_FOR_JOIN;
        this.gameStart = new GameStart();
        this.player1Score = 5;
        this.player2Score = 5;
    }

    public GameStart getGameStart() {
        return gameStart;
    }

    public void setGameStart(GameStart gameStart) {
        this.gameStart = gameStart;
    }

    public Player getPlayer1() {
        return player1;
    }

    public void setPlayer1(Player player1) {
        this.player1 = player1;
    }

    public GameState getGameState() {
        return this.gameState;
    }

    public void setGameState(GameState gameState) {
        this.gameState = gameState;
    }

    public String getSessionId() {
        return this.sessionId;
    }

    public int getPlayer1Score() {
        return this.player1Score;
    }

    public void setPlayer1Score(int player1Score) {
        this.player1Score = player1Score;
    }

    public int getPlayer2Score() {
        return this.player2Score;
    }

    public void setPlayer2Score(int player2Score) {
        this.player2Score = player2Score;
    }

    public Player getPlayer2() {
        return this.player2;
    }

    public void setPlayer2(Player player2) {
        this.player2 = player2;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this)
            return true;
        if (!(o instanceof GameSession)) {
            return false;
        }
        GameSession gameSession = (GameSession) o;
        return Objects.equals(gameState, gameSession.gameState) && Objects.equals(gameStart, gameSession.gameStart)
                && Objects.equals(sessionId, gameSession.sessionId) && player1Score == gameSession.player1Score
                && player2Score == gameSession.player2Score && Objects.equals(player1, gameSession.player1)
                && Objects.equals(player2, gameSession.player2);
    }

    @Override
    public int hashCode() {
        return Objects.hash(gameState, gameStart, sessionId, player1Score, player2Score, player1, player2);
    }

    @Override
    public String toString() {
        return "{" + " gameState='" + getGameState() + "'" + ", gameStart='" + getGameStart() + "'" + ", sessionId='"
                + getSessionId() + "'" + ", player1Score='" + getPlayer1Score() + "'" + ", player2Score='"
                + getPlayer2Score() + "'" + ", player1='" + getPlayer1() + "'" + ", player2='" + getPlayer2() + "'"
                + "}";
    }

}