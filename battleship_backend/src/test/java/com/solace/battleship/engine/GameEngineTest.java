package com.solace.battleship.engine;

import com.solace.battleship.engine.GameEngine;
import com.solace.battleship.events.*;
import org.junit.Test;

import static junit.framework.TestCase.*;

public class GameEngineTest {

    GameEngine gameEngine = new GameEngine();

    @Test
    public void testRequestToJoinGamePlayer1() {
        String sessionId = "0xFFFF";

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
        String sessionId = "0xFFFF";
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
        String sessionId = "0xFFFF";
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
        String sessionId = "0xFFFF";

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
        String sessionId = "0xFFFF";
        assertFalse(gameEngine.canGameStart(sessionId));
        assertNull(gameEngine.getGameStartAndStartGame(sessionId));
    }

    @Test
    public void testPlayerJoinAfterGameStart() {
        String sessionId = "0xFFFF";
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

}
