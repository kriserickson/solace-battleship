package com.solace.battleship.events;

import java.util.Objects;

/**
 * The Result of a BoardSetEvent request
 * 
 * @author Andrew Roberts, Thomas Kunnumpurath
 */
public class BoardSetResult {
    private PlayerName playerName;
    private boolean success;
    private String message;

    public PlayerName getPlayerName() {
        return playerName;
    }

    public void setPlayerName(final PlayerName playerName) {
        this.playerName = playerName;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(final boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(final String message) {
        this.message = message;
    }

    public BoardSetResult() {
    }

    public BoardSetResult(PlayerName playerName, boolean success, String message) {
        this.playerName = playerName;
        this.success = success;
        this.message = message;
    }

    public boolean getSuccess() {
        return this.success;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this)
            return true;
        if (!(o instanceof BoardSetResult)) {
            return false;
        }
        BoardSetResult boardSetResult = (BoardSetResult) o;
        return Objects.equals(playerName, boardSetResult.playerName) && success == boardSetResult.success
                && Objects.equals(message, boardSetResult.message);
    }

    @Override
    public int hashCode() {
        return Objects.hash(playerName, success, message);
    }

    @Override
    public String toString() {
        return "{" + " playerName='" + getPlayerName() + "'" + ", success='" + isSuccess() + "'" + ", message='"
                + getMessage() + "'" + "}";
    }

}
