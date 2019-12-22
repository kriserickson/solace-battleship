package com.solace.battleship.events;

import java.util.Objects;

/**
 * A GameStart Event
 * 
 * @author Andrew Roberts, Thomas Kunnumpurath
 */
public class GameStart {

    private PlayerJoined Player1;
    private PlayerJoined Player2;

    public GameStart() {
    }

    public boolean setPlayerJoined(PlayerJoined request) {
        if (request.getPlayerName() == PlayerName.Player1 && getPlayer1() == null) {
            setPlayer1(request);
            return true;
        } else if (request.getPlayerName() == PlayerName.Player2 && getPlayer2() == null) {
            setPlayer2(request);
            return true;
        }
        return false;
    }

    public PlayerJoined getPlayer1() {
        return Player1;
    }

    public void setPlayer1(final PlayerJoined player1) {
        Player1 = player1;
    }

    public PlayerJoined getPlayer2() {
        return Player2;
    }

    public void setPlayer2(final PlayerJoined player2) {
        Player2 = player2;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this)
            return true;
        if (!(o instanceof GameStart)) {
            return false;
        }
        GameStart gameStart = (GameStart) o;
        return Objects.equals(Player1, gameStart.Player1) && Objects.equals(Player2, gameStart.Player2);
    }

    @Override
    public int hashCode() {
        return Objects.hash(Player1, Player2);
    }

    @Override
    public String toString() {
        return "{" + " Player1='" + getPlayer1() + "'" + ", Player2='" + getPlayer2() + "'" + "}";
    }

}
