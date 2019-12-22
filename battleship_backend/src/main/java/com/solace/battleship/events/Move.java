package com.solace.battleship.events;

import java.util.Objects;

/**
 * An object representing a player's move in the game
 * 
 * @author Thomas Kunnumpurath, Andrew Roberts
 */
public class Move {

    private PlayerName player;
    private int x;
    private int y;

    public PlayerName getPlayer() {
        return player;
    }

    public void setPlayer(final PlayerName player) {
        this.player = player;
    }

    public int getX() {
        return x;
    }

    public void setX(final int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(final int y) {
        this.y = y;
    }

    public Move(PlayerName player, int x, int y) {
        this.player = player;
        this.x = x;
        this.y = y;
    }

    public Move player(PlayerName player) {
        this.player = player;
        return this;
    }

    public Move x(int x) {
        this.x = x;
        return this;
    }

    public Move y(int y) {
        this.y = y;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this)
            return true;
        if (!(o instanceof Move)) {
            return false;
        }
        Move move = (Move) o;
        return Objects.equals(player, move.player) && x == move.x && y == move.y;
    }

    @Override
    public int hashCode() {
        return Objects.hash(player, x, y);
    }

    @Override
    public String toString() {
        return "{" + " player='" + getPlayer() + "'" + ", x='" + getX() + "'" + ", y='" + getY() + "'" + "}";
    }

}
