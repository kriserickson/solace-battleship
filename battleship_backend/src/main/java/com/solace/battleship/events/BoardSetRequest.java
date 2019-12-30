package com.solace.battleship.events;

import java.util.Objects;

/**
 * An event that represents the setting of a board by a player
 *
 * @author Andrew Roberts, Thomas Kunnumpurath
 */
public class BoardSetRequest {

    private PlayerName playerName;
    private PrivateBoardCellState[][] board;
    private String sessionId;

    public BoardSetRequest(){

    }

    public PlayerName getPlayerName() {
        return this.playerName;
    }

    public void setPlayerName(PlayerName playerName) {
        this.playerName = playerName;
    }

    public PrivateBoardCellState[][] getBoard() {
        return this.board;
    }

    public void setBoard(PrivateBoardCellState[][] board) {
        this.board = board;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BoardSetRequest that = (BoardSetRequest) o;
        return board == that.board &&
                playerName == that.playerName &&
                sessionId.equals(that.sessionId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(playerName, board, sessionId);
    }

    @Override
    public String toString() {
        return "BoardSetRequest{" +
                "playerName=" + playerName +
                ", board=" + board +
                ", sessionId='" + sessionId + '\'' +
                '}';
    }

}
