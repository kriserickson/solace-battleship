package com.solace.battleship.events;

import java.util.Objects;

/**
 * The result of a JoinRequest
 * 
 * @author Andrew Roberts, Thomas Kunnumpurath
 */
public class JoinResult {

    private PlayerName playerName;
    private boolean success;
    private String message;

    public JoinResult(final PlayerName playerName, final boolean success, final String message) {
        this.playerName = playerName;
        this.success = success;
        this.message = message;
    }

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

    public boolean getSuccess() {
        return this.success;
    }

    public JoinResult playerName(PlayerName playerName) {
        this.playerName = playerName;
        return this;
    }

    public JoinResult success(boolean success) {
        this.success = success;
        return this;
    }

    public JoinResult message(String message) {
        this.message = message;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this)
            return true;
        if (!(o instanceof JoinResult)) {
            return false;
        }
        JoinResult joinResult = (JoinResult) o;
        return Objects.equals(playerName, joinResult.playerName) && success == joinResult.success
                && Objects.equals(message, joinResult.message);
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
