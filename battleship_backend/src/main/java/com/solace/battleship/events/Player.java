package com.solace.battleship.events;

import java.util.Objects;

/**
 * An object representing a player and its associated states
 * 
 * @author Andrew Roberts, Thomas Kunnumpurath
 */
public class Player {

    private PlayerName name;
    private String nickname;
    private PrivateBoardCellState[][] internalBoardState;
    private KnownBoardCellState[][] publicBoardState;
    private boolean isTurn;

    public PlayerName getName() {
        return name;
    }

    public void setName(PlayerName name) {
        this.name = name;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public PrivateBoardCellState[][] getInternalBoardState() {
        return internalBoardState;
    }

    public void setInternalBoardState(PrivateBoardCellState[][] internalBoardState) {
        this.internalBoardState = internalBoardState;
    }

    public KnownBoardCellState[][] getPublicBoardState() {
        return publicBoardState;
    }

    public void setPublicBoardState(KnownBoardCellState[][] publicBoardState) {
        this.publicBoardState = publicBoardState;
    }

    public boolean isTurn() {
        return isTurn;
    }

    public void setTurn(boolean turn) {
        isTurn = turn;
    }

    public Player() {
        KnownBoardCellState[][] tmpArray = new KnownBoardCellState[5][5];
        for (int i = 0; i < tmpArray.length; i++) {
            for (int j = 0; j < tmpArray.length; j++) {
                tmpArray[i][j] = KnownBoardCellState.empty;
            }
        }
        this.publicBoardState = tmpArray;
    }

    public Player(PlayerName name, String nickname, PrivateBoardCellState[][] internalBoardState,
            KnownBoardCellState[][] publicBoardState, boolean isTurn) {
        this.name = name;
        this.nickname = nickname;
        this.internalBoardState = internalBoardState;
        this.publicBoardState = publicBoardState;
        this.isTurn = isTurn;
    }

    public boolean isIsTurn() {
        return this.isTurn;
    }

    public boolean getIsTurn() {
        return this.isTurn;
    }

    public void setIsTurn(boolean isTurn) {
        this.isTurn = isTurn;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this)
            return true;
        if (!(o instanceof Player)) {
            return false;
        }
        Player player = (Player) o;
        return Objects.equals(name, player.name) && Objects.equals(nickname, player.nickname)
                && Objects.equals(internalBoardState, player.internalBoardState)
                && Objects.equals(publicBoardState, player.publicBoardState) && isTurn == player.isTurn;
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, nickname, internalBoardState, publicBoardState, isTurn);
    }

    @Override
    public String toString() {
        return "{" + " name='" + getName() + "'" + ", nickname='" + getNickname() + "'" + ", internalBoardState='"
                + getInternalBoardState() + "'" + ", publicBoardState='" + getPublicBoardState() + "'" + ", isTurn='"
                + isIsTurn() + "'" + "}";
    }

}
