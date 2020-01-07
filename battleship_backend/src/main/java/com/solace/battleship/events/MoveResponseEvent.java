package com.solace.battleship.events;

import java.util.Arrays;
import java.util.Objects;

/**
 * An object represening the response to a player's moves
 * 
 * @author Andrew Roberts, Thomas Kunnumpurath
 */
public class MoveResponseEvent {

  private PlayerName player;
  private KnownBoardCellState[][] playerBoard;
  private Move move;
  private PrivateBoardCellState moveResult;
  private String sessionId;

  public MoveResponseEvent() {

  }

  public PlayerName getPlayer() {
    return player;
  }

  public void setPlayer(final PlayerName player) {
    this.player = player;
  }

  public KnownBoardCellState[][] getPlayerBoard() {
    return playerBoard;
  }

  public void setPlayerBoard(final KnownBoardCellState[][] playerBoard) {
    this.playerBoard = playerBoard;
  }

  public Move getMove() {
    return move;
  }

  public void setMove(final Move move) {
    this.move = move;
  }

  public PrivateBoardCellState getMoveResult() {
    return moveResult;
  }

  public void setMoveResult(final PrivateBoardCellState moveResult) {
    this.moveResult = moveResult;
  }

  public String getSessionId() {
    return sessionId;
  }

  public void setSessionId(String sessionId) {
    this.sessionId = sessionId;
  }

  @Override
  public boolean equals(Object o) {
    if (o == this)
      return true;
    if (!(o instanceof MoveResponseEvent)) {
      return false;
    }
    MoveResponseEvent moveResponseEvent = (MoveResponseEvent) o;
    return Objects.equals(player, moveResponseEvent.player)
        && Arrays.deepEquals(playerBoard, moveResponseEvent.playerBoard) // arrays deep equals! figured it out
        && Objects.equals(move, moveResponseEvent.move) && Objects.equals(moveResult, moveResponseEvent.moveResult);
  }

  @Override
  public int hashCode() {
    return Objects.hash(player, playerBoard, move, moveResult);
  }

  @Override
  public String toString() {
    return "{" + " player='" + getPlayer() + "'" + ", playerBoard='" + getPlayerBoard() + "'" + ", move='" + getMove()
        + "'" + ", moveResult='" + getMoveResult() + "'" + "}";
  }

}
