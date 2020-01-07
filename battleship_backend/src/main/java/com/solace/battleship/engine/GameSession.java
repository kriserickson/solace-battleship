package com.solace.battleship.engine;

import java.util.Objects;

import com.solace.battleship.BattleshipServer;
import com.solace.battleship.events.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Object represents a game's session
 */
public class GameSession {

  private static final Logger log = LoggerFactory.getLogger(GameSession.class);

  private GameState gameState;
  private final String sessionId;

  private GameStart gameStart;
  private MatchStart matchStart;
  private int player1Score;
  private int player2Score;
  private Player player1;
  private Player player2;

  public GameSession(String sessionId) {
    this.sessionId = sessionId;
    this.gameState = GameState.WAITING_FOR_JOIN;
    this.gameStart = new GameStart();
    this.matchStart = new MatchStart();
    this.player1 = new Player();
    this.player2 = new Player();
    this.player1Score = 5;
    this.player2Score = 5;
  }

  public String getSessionId() {
    return this.sessionId;
  }

  public GameState getGameState() {
    return this.gameState;
  }

  public void setGameState(GameState gameState) {
    this.gameState = gameState;
  }

  public GameStart getGameStart() {
    return gameStart;
  }

  public void setGameStart(GameStart gameStart) {
    this.gameStart = gameStart;
  }

  public MatchStart getMatchStart() {
    return matchStart;
  }

  public void setMatchStart(MatchStart matchStart) {
    this.matchStart = matchStart;
  }

  public PrivateBoardCellState[][] getPlayer1Board() {
    return player1.getInternalBoardState();
  }

  public void setPlayer1Board(PrivateBoardCellState[][] player1Board) {
    this.player1.setInternalBoardState(player1Board);
  }

  public PrivateBoardCellState[][] getPlayer2Board() {
    return player2.getInternalBoardState();
  }

  public void setPlayer2Board(PrivateBoardCellState[][] player2Board) {
    this.player2.setInternalBoardState(player2Board);
  }

  /**
   * Function to set a player's board state based on provided PlayerName
   *
   * @param request a request to set a players board
   * @return The result of a board set request
   */
  public boolean setBoard(BoardSetRequest request) {
    if (request.getPlayerName().equals(PlayerName.player1) && this.getPlayer1().getInternalBoardState() == null) {
      this.setPlayer1Board(request.getBoard());
      return true;
    } else if (request.getPlayerName().equals(PlayerName.player2)
        && this.getPlayer2().getInternalBoardState() == null) {
      this.setPlayer2Board(request.getBoard());
      return true;
    }
    return false; // error
  }

  /**
   * Function to make a move, will update known public board
   *
   * @param request a request to set a players board
   * @return The result of a move, i.e. "ship" or "empty"
   */
  public MoveResponseEvent makeMove(Move request) {
    MoveResponseEvent response = new MoveResponseEvent();
    response.setSessionId(request.getSessionId());
    response.setPlayer(request.getPlayer());
    response.setMove(request);

    if ((request.getPlayer() == PlayerName.player1) && (this.getGameState() != GameState.PLAYER1_TURN)) {
      // I don't know if we want to implement error classes too ...
      response.setPlayerBoard(null);
      response.setMoveResult(PrivateBoardCellState.empty);
      return response;
    }
    if ((request.getPlayer() == PlayerName.player2) && (this.getGameState() != GameState.PLAYER2_TURN)) {
      // I don't know if we want to implement error classes too ...
      response.setPlayerBoard(null);
      response.setMoveResult(PrivateBoardCellState.empty);
      return response;
    }

    if (request.getPlayer() == PlayerName.player1) {
      log.info(player2.toString());

      PrivateBoardCellState moveResult = this.getPlayer2Board()[request.getX()][request.getY()];
      // if the move results in a hit
      if (moveResult == PrivateBoardCellState.ship) {
        // update the score
        this.decrementPlayer2Score();
        // update player's public board state with new move
        KnownBoardCellState[][] updatedBoard = this.getPlayer2().getPublicBoardState();
        updatedBoard[request.getX()][request.getY()] = KnownBoardCellState.hit;
        this.getPlayer2().setPublicBoardState(updatedBoard);
        // update player turn
        this.setGameState(GameState.PLAYER2_TURN);
        // format response
        response.setPlayerBoard(updatedBoard);
        response.setMoveResult(moveResult);
      } else { // move was a miss
        // update player's public board state with new move
        KnownBoardCellState[][] updatedBoard = this.getPlayer2().getPublicBoardState();
        updatedBoard[request.getX()][request.getY()] = KnownBoardCellState.miss;
        this.getPlayer2().setPublicBoardState(updatedBoard);
        // update player turn
        this.setGameState(GameState.PLAYER2_TURN);
        // format response
        response.setPlayerBoard(updatedBoard);
        response.setMoveResult(moveResult);
      }
    } else {
      PrivateBoardCellState moveResult = this.getPlayer1Board()[request.getX()][request.getY()];
      // if the move results in a hit
      if (moveResult == PrivateBoardCellState.ship) {
        // update the score
        this.decrementPlayer1Score();
        // update player's public board state with new move
        KnownBoardCellState[][] updatedBoard = this.getPlayer1().getPublicBoardState();
        updatedBoard[request.getX()][request.getY()] = KnownBoardCellState.hit;
        this.getPlayer1().setPublicBoardState(updatedBoard);
        // update player turn
        this.setGameState(GameState.PLAYER1_TURN);
        // format response
        response.setPlayerBoard(updatedBoard);
        response.setMoveResult(moveResult);
      } else { // move was a miss
        // update player's public board state with new move
        KnownBoardCellState[][] updatedBoard = this.getPlayer1().getPublicBoardState();
        updatedBoard[request.getX()][request.getY()] = KnownBoardCellState.miss;
        this.getPlayer1().setPublicBoardState(updatedBoard);
        // update player turn
        this.setGameState(GameState.PLAYER1_TURN);
        // format response
        response.setPlayerBoard(updatedBoard);
        response.setMoveResult(moveResult);
      }
    }
    return response;
  }

  public Player getPlayer1() {
    return player1;
  }

  public void setPlayer1(Player player1) {
    this.player1 = player1;
  }

  public int getPlayer1Score() {
    return this.player1Score;
  }

  public void setPlayer1Score(int player1Score) {
    this.player1Score = player1Score;
  }

  public void decrementPlayer1Score() {
    int newScore = this.player1Score - 1;
    this.player1Score = newScore;
  }

  public int getPlayer2Score() {
    return this.player2Score;
  }

  public void setPlayer2Score(int player2Score) {
    this.player2Score = player2Score;
  }

  public void decrementPlayer2Score() {
    int newScore = this.player2Score - 1;
    this.player2Score = newScore;
  }

  public Player getPlayer2() {
    return this.player2;
  }

  public void setPlayer2(Player player2) {
    this.player2 = player2;
  }

  @Override
  public boolean equals(Object o) {
    if (o == this)
      return true;
    if (!(o instanceof GameSession)) {
      return false;
    }
    GameSession gameSession = (GameSession) o;
    return Objects.equals(gameState, gameSession.gameState) && Objects.equals(gameStart, gameSession.gameStart)
        && Objects.equals(sessionId, gameSession.sessionId) && player1Score == gameSession.player1Score
        && player2Score == gameSession.player2Score && Objects.equals(player1, gameSession.player1)
        && Objects.equals(player2, gameSession.player2);
  }

  @Override
  public int hashCode() {
    return Objects.hash(gameState, gameStart, sessionId, player1Score, player2Score, player1, player2);
  }

  @Override
  public String toString() {
    return "{" + " gameState='" + getGameState() + "'" + ", gameStart='" + getGameStart() + "'" + ", sessionId='"
        + getSessionId() + "'" + ", player1Score='" + getPlayer1Score() + "'" + ", player2Score='" + getPlayer2Score()
        + "'" + ", player1='" + getPlayer1() + "'" + ", player2='" + getPlayer2() + "'" + "}";
  }

}