package com.solace.battleship.events;

import java.util.Objects;

/**
 * An event signifying the start of a Match
 * 
 * @author Thomas Kunnumpurath, Andrew Roberts
 */
public class MatchStart {

    private BoardSetResult player1Board;
    private BoardSetResult player2Board;

    public BoardSetResult getPlayer2Board() {
        return player2Board;
    }

    public void setPlayer2Board(final BoardSetResult player2Board) {
        this.player2Board = player2Board;
    }

    public BoardSetResult getPlayer1Board() {
        return player1Board;
    }

    public void setPlayer1Board(final BoardSetResult player1Board) {
        this.player1Board = player1Board;
    }

    public MatchStart(BoardSetResult player1Board, BoardSetResult player2Board) {
        this.player1Board = player1Board;
        this.player2Board = player2Board;
    }

    public MatchStart player1Board(BoardSetResult player1Board) {
        this.player1Board = player1Board;
        return this;
    }

    public MatchStart player2Board(BoardSetResult player2Board) {
        this.player2Board = player2Board;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this)
            return true;
        if (!(o instanceof MatchStart)) {
            return false;
        }
        MatchStart matchStart = (MatchStart) o;
        return Objects.equals(player1Board, matchStart.player1Board)
                && Objects.equals(player2Board, matchStart.player2Board);
    }

    @Override
    public int hashCode() {
        return Objects.hash(player1Board, player2Board);
    }

    @Override
    public String toString() {
        return "{" + " player1Board='" + getPlayer1Board() + "'" + ", player2Board='" + getPlayer2Board() + "'" + "}";
    }

}
