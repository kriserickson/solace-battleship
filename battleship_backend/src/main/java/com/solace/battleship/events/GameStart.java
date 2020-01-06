package com.solace.battleship.events;

import java.util.Objects;

/**
 * A GameStart Event
 * 
 * @author Andrew Roberts, Thomas Kunnumpurath
 */
public class GameStart {

    private PlayerJoined player1;
    private PlayerJoined player2;

    public GameStart() {
    }

    public boolean setPlayerJoined(PlayerJoined request) {
        if (request.getPlayerName() == PlayerName.player1 && getPlayer1() == null) {
            setPlayer1(request);
            return true;
        } else if (request.getPlayerName() == PlayerName.player2 && getPlayer2() == null) {
            setPlayer2(request);
            return true;
        }
        return false;
    }

    public PlayerJoined getPlayer1() {
        return this.player1;
    }

    public void setPlayer1(final PlayerJoined player1) {
        this.player1 = player1;
    }

    public PlayerJoined getPlayer2() {
        return this.player2;
    }

    public void setPlayer2(final PlayerJoined player2) {
        this.player2 = player2;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this)
            return true;
        if (!(o instanceof GameStart)) {
            return false;
        }
        GameStart gameStart = (GameStart) o;
        return Objects.equals(this.player1, gameStart.player1) && Objects.equals(this.player2, gameStart.player2);
    }

    @Override
    public int hashCode() {
        return Objects.hash(player1, player2);
    }

    @Override
    public String toString() {
        return "{" + " Player1='" + getPlayer1() + "'" + ", Player2='" + getPlayer2() + "'" + "}";
    }

}
