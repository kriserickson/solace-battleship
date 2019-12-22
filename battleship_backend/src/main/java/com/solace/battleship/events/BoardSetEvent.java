package com.solace.battleship.events;

import java.util.Objects;

/**
 * An event that represents the setting of a board by a player
 * 
 * @author Andrew Roberts, Thomas Kunnumpurath
 */
public class BoardSetEvent {

    private PlayerName playerName;
    private int shipsSet;

    public PlayerName getPlayerName() {
        return this.playerName;
    }

    public void setPlayerName(PlayerName playerName) {
        this.playerName = playerName;
    }

    public int getShipsSet() {
        return this.shipsSet;
    }

    public void setShipsSet(int shipsSet) {
        this.shipsSet = shipsSet;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this)
            return true;
        if (!(o instanceof BoardSetEvent)) {
            return false;
        }
        BoardSetEvent boardSetEvent = (BoardSetEvent) o;
        return Objects.equals(playerName, boardSetEvent.playerName) && shipsSet == boardSetEvent.shipsSet;
    }

    @Override
    public int hashCode() {
        return Objects.hash(playerName, shipsSet);
    }

    @Override
    public String toString() {
        return "{" + " playerName='" + getPlayerName() + "'" + ", shipsSet='" + getShipsSet() + "'" + "}";
    }

}
