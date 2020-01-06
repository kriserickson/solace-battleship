package com.solace.battleship.engine;

import com.solace.battleship.events.*;

import org.junit.Before;
import org.junit.Test;

import static junit.framework.TestCase.*;

public class GameEngineTest {

  private GameEngine gameEngine;
  private static final String sessionId = "0xFFFF";

  @Before
  public void setup() {
    gameEngine = new GameEngine();
  }

  // join requests

  @Test
  public void testRequestToJoinGamePlayer1() {
    PlayerJoined request = new PlayerJoined();
    request.setPlayerName(PlayerName.Player1);
    request.setPlayerNickname("TK");
    request.setSessionId(sessionId);

    JoinResult joinResult = new JoinResult(PlayerName.Player1, true, GameEngine.PLAYER_JOIN_SUCCESS);

    assertEquals(gameEngine.requestToJoinGame(request), joinResult);
    assertFalse(gameEngine.canGameStart(sessionId));
    assertNull(gameEngine.getGameStartAndStartGame(sessionId));
  }

  @Test
  public void testRequestToJoinGamePlayer2() {

    PlayerJoined request = new PlayerJoined();
    request.setPlayerName(PlayerName.Player2);
    request.setPlayerNickname("TK");
    request.setSessionId(sessionId);

    JoinResult joinResult = new JoinResult(PlayerName.Player2, true, GameEngine.PLAYER_JOIN_SUCCESS);

    assertEquals(gameEngine.requestToJoinGame(request), joinResult);
    assertFalse(gameEngine.canGameStart(sessionId));
    assertNull(gameEngine.getGameStartAndStartGame(sessionId));
  }

  @Test
  public void testBothPlayersJoined() {

    PlayerJoined request1 = new PlayerJoined();
    request1.setPlayerName(PlayerName.Player1);
    request1.setPlayerNickname("TK");
    request1.setSessionId(sessionId);
    PlayerJoined request2 = new PlayerJoined();
    request2.setPlayerName(PlayerName.Player2);
    request2.setPlayerNickname("TK");
    request2.setSessionId(sessionId);

    GameStart gameStart = new GameStart();
    gameStart.setPlayerJoined(request1);
    gameStart.setPlayerJoined(request2);

    JoinResult joinResult1 = new JoinResult(PlayerName.Player1, true, GameEngine.PLAYER_JOIN_SUCCESS);
    JoinResult joinResult2 = new JoinResult(PlayerName.Player2, true, GameEngine.PLAYER_JOIN_SUCCESS);

    assertEquals(gameEngine.requestToJoinGame(request1), joinResult1);
    assertEquals(gameEngine.requestToJoinGame(request2), joinResult2);
    assertTrue(gameEngine.canGameStart(sessionId));
    assertEquals(gameEngine.getGameStartAndStartGame(sessionId), gameStart);
  }

  @Test
  public void testDuplicateJoinMessage() {

    PlayerJoined request = new PlayerJoined();
    request.setPlayerName(PlayerName.Player1);
    request.setPlayerNickname("TK");
    request.setSessionId(sessionId);

    JoinResult joinResult = new JoinResult(PlayerName.Player1, true, GameEngine.PLAYER_JOIN_SUCCESS);
    JoinResult joinResultBad = new JoinResult(PlayerName.Player1, false, GameEngine.PLAYER_ALREADY_JOINED_ERROR);

    assertEquals(gameEngine.requestToJoinGame(request), joinResult);
    assertEquals(gameEngine.requestToJoinGame(request), joinResultBad);
    assertFalse(gameEngine.canGameStart(sessionId));
    assertNull(gameEngine.getGameStartAndStartGame(sessionId));
  }

  @Test
  public void testInvalidSession() {

    assertFalse(gameEngine.canGameStart(sessionId));
    assertNull(gameEngine.getGameStartAndStartGame(sessionId));
  }

  @Test
  public void testPlayerJoinAfterGameStart() {

    PlayerJoined request1 = new PlayerJoined();
    request1.setPlayerName(PlayerName.Player1);
    request1.setPlayerNickname("TK");
    request1.setSessionId(sessionId);
    PlayerJoined request2 = new PlayerJoined();
    request2.setPlayerName(PlayerName.Player2);
    request2.setPlayerNickname("TK");
    request2.setSessionId(sessionId);

    GameStart gameStart = new GameStart();
    gameStart.setPlayerJoined(request1);
    gameStart.setPlayerJoined(request2);

    JoinResult joinResult1 = new JoinResult(PlayerName.Player1, true, GameEngine.PLAYER_JOIN_SUCCESS);
    JoinResult joinResult2 = new JoinResult(PlayerName.Player2, true, GameEngine.PLAYER_JOIN_SUCCESS);
    JoinResult joinResult3 = new JoinResult(PlayerName.Player1, false, GameEngine.GAME_ALREADY_STARTED_ERROR);

    assertEquals(gameEngine.requestToJoinGame(request1), joinResult1);
    assertEquals(gameEngine.requestToJoinGame(request2), joinResult2);
    assertTrue(gameEngine.canGameStart(sessionId));
    assertEquals(gameEngine.getGameStartAndStartGame(sessionId), gameStart);
    assertEquals(gameEngine.requestToJoinGame(request1), joinResult3);
  }

  public void joinBothPlayers() {
    /** setup board */
    PlayerJoined request1 = new PlayerJoined();
    request1.setPlayerName(PlayerName.Player1);
    request1.setPlayerNickname("ANDREW");
    request1.setSessionId(sessionId);
    PlayerJoined request2 = new PlayerJoined();
    request2.setPlayerName(PlayerName.Player2);
    request2.setPlayerNickname("ANDREW");
    request2.setSessionId(sessionId);
    gameEngine.requestToJoinGame(request1);
    gameEngine.requestToJoinGame(request2);
    gameEngine.getGameStartAndStartGame(sessionId);
  }

  // board set requests

  @Test
  public void testRequestToSetBoardPlayer1() {
    /** setup board */
    joinBothPlayers();
    /** -+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+ */
    // form request
    PrivateBoardCellState[][] board = {
        { PrivateBoardCellState.empty, PrivateBoardCellState.empty, PrivateBoardCellState.empty,
            PrivateBoardCellState.empty, PrivateBoardCellState.ship },
        { PrivateBoardCellState.empty, PrivateBoardCellState.empty, PrivateBoardCellState.empty,
            PrivateBoardCellState.empty, PrivateBoardCellState.ship },
        { PrivateBoardCellState.empty, PrivateBoardCellState.empty, PrivateBoardCellState.empty,
            PrivateBoardCellState.empty, PrivateBoardCellState.ship },
        { PrivateBoardCellState.empty, PrivateBoardCellState.empty, PrivateBoardCellState.empty,
            PrivateBoardCellState.empty, PrivateBoardCellState.ship },
        { PrivateBoardCellState.empty, PrivateBoardCellState.empty, PrivateBoardCellState.empty,
            PrivateBoardCellState.empty, PrivateBoardCellState.ship } };
    BoardSetRequest boardSetRequest = new BoardSetRequest();
    boardSetRequest.setPlayerName(PlayerName.Player1);
    boardSetRequest.setBoard(board);
    boardSetRequest.setSessionId(sessionId);
    // mock successful response
    BoardSetResult boardSetResult = new BoardSetResult(PlayerName.Player1, true, GameEngine.BOARD_SET_SUCCESS);
    // check that the request works
    assertEquals(gameEngine.requestToSetBoard(boardSetRequest), boardSetResult);
    assertFalse(gameEngine.canMatchStart(sessionId));
    assertNull(gameEngine.getMatchStartAndStartMatch(sessionId));
  }

  @Test
  public void testRequestToSetBoardPlayer2() {
    /** setup board */
    joinBothPlayers();
    /** -+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+ */
    // form request
    PrivateBoardCellState[][] board = {
        { PrivateBoardCellState.empty, PrivateBoardCellState.empty, PrivateBoardCellState.empty,
            PrivateBoardCellState.empty, PrivateBoardCellState.ship },
        { PrivateBoardCellState.empty, PrivateBoardCellState.empty, PrivateBoardCellState.empty,
            PrivateBoardCellState.empty, PrivateBoardCellState.ship },
        { PrivateBoardCellState.empty, PrivateBoardCellState.empty, PrivateBoardCellState.empty,
            PrivateBoardCellState.empty, PrivateBoardCellState.ship },
        { PrivateBoardCellState.empty, PrivateBoardCellState.empty, PrivateBoardCellState.empty,
            PrivateBoardCellState.empty, PrivateBoardCellState.ship },
        { PrivateBoardCellState.empty, PrivateBoardCellState.empty, PrivateBoardCellState.empty,
            PrivateBoardCellState.empty, PrivateBoardCellState.ship } };
    BoardSetRequest boardSetRequest = new BoardSetRequest();
    boardSetRequest.setPlayerName(PlayerName.Player2);
    boardSetRequest.setBoard(board);
    boardSetRequest.setSessionId(sessionId);
    // mock successful response
    BoardSetResult boardSetResult = new BoardSetResult(PlayerName.Player2, true, GameEngine.BOARD_SET_SUCCESS);
    // check that the request works
    assertEquals(gameEngine.requestToSetBoard(boardSetRequest), boardSetResult);
    assertFalse(gameEngine.canMatchStart(sessionId));
    assertNull(gameEngine.getMatchStartAndStartMatch(sessionId));
  }

  @Test
  public void testBothPlayersSetBoard() {
    /** setup board */
    joinBothPlayers();
    /** -+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+ */
    // form board set requests
    PrivateBoardCellState[][] board = {
        { PrivateBoardCellState.empty, PrivateBoardCellState.empty, PrivateBoardCellState.empty,
            PrivateBoardCellState.empty, PrivateBoardCellState.ship },
        { PrivateBoardCellState.empty, PrivateBoardCellState.empty, PrivateBoardCellState.empty,
            PrivateBoardCellState.empty, PrivateBoardCellState.ship },
        { PrivateBoardCellState.empty, PrivateBoardCellState.empty, PrivateBoardCellState.empty,
            PrivateBoardCellState.empty, PrivateBoardCellState.ship },
        { PrivateBoardCellState.empty, PrivateBoardCellState.empty, PrivateBoardCellState.empty,
            PrivateBoardCellState.empty, PrivateBoardCellState.ship },
        { PrivateBoardCellState.empty, PrivateBoardCellState.empty, PrivateBoardCellState.empty,
            PrivateBoardCellState.empty, PrivateBoardCellState.ship } };
    BoardSetRequest boardSetRequest1 = new BoardSetRequest();
    boardSetRequest1.setPlayerName(PlayerName.Player1);
    boardSetRequest1.setBoard(board);
    boardSetRequest1.setSessionId(sessionId);
    BoardSetRequest boardSetRequest2 = new BoardSetRequest();
    boardSetRequest2.setPlayerName(PlayerName.Player2);
    boardSetRequest2.setBoard(board);
    boardSetRequest2.setSessionId(sessionId);
    // mock successful response
    BoardSetResult boardSetResult1 = new BoardSetResult(PlayerName.Player1, true, GameEngine.BOARD_SET_SUCCESS);
    BoardSetResult boardSetResult2 = new BoardSetResult(PlayerName.Player2, true, GameEngine.BOARD_SET_SUCCESS);
    MatchStart matchStart = new MatchStart();
    matchStart.setPlayer1Board(boardSetResult1);
    matchStart.setPlayer2Board(boardSetResult2);
    // check that the set board request works
    assertEquals(gameEngine.requestToSetBoard(boardSetRequest1), boardSetResult1);
    assertEquals(gameEngine.requestToSetBoard(boardSetRequest2), boardSetResult2);
    // check that the match starts properly
    assertTrue(gameEngine.canMatchStart(sessionId));
    assertEquals(gameEngine.getMatchStartAndStartMatch(sessionId), matchStart);
  }
}
