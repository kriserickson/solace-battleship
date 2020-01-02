package com.solace.battleship.engine;

import com.solace.battleship.events.*;
import org.junit.Test;

import static junit.framework.TestCase.*;

public class GameEngineTest {

    GameEngine gameEngine = new GameEngine();

    // join requests

    @Test
    public void testRequestToJoinGamePlayer1(){
        String sessionId="0xFFFF";

        PlayerJoined request = new PlayerJoined();
        request.setPlayerName(PlayerName.Player1);
        request.setPlayerNickname("TK");
        request.setSessionId(sessionId);

        JoinResult joinResult = new JoinResult(PlayerName.Player1,true,GameEngine.PLAYER_JOIN_SUCCESS);

        assertEquals(gameEngine.requestToJoinGame(request),joinResult);
        assertFalse(gameEngine.canGameStart(sessionId));
        assertNull(gameEngine.getGameStartAndStartGame(sessionId));
    }

    @Test
    public void testRequestToJoinGamePlayer2(){
        String sessionId="0xFFFF";
        PlayerJoined request = new PlayerJoined();
        request.setPlayerName(PlayerName.Player2);
        request.setPlayerNickname("TK");
        request.setSessionId(sessionId);

        JoinResult joinResult = new JoinResult(PlayerName.Player2,true,GameEngine.PLAYER_JOIN_SUCCESS);

        assertEquals(gameEngine.requestToJoinGame(request),joinResult);
        assertFalse(gameEngine.canGameStart(sessionId));
        assertNull(gameEngine.getGameStartAndStartGame(sessionId));
    }

    @Test
    public void testBothPlayersJoined(){
        String sessionId="0xFFFF";
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

        JoinResult joinResult1 = new JoinResult(PlayerName.Player1,true,GameEngine.PLAYER_JOIN_SUCCESS);
        JoinResult joinResult2 = new JoinResult(PlayerName.Player2,true,GameEngine.PLAYER_JOIN_SUCCESS);

        assertEquals(gameEngine.requestToJoinGame(request1),joinResult1);
        assertEquals(gameEngine.requestToJoinGame(request2),joinResult2);
        assertTrue(gameEngine.canGameStart(sessionId));
        assertEquals(gameEngine.getGameStartAndStartGame(sessionId),gameStart);
    }

    @Test
    public void testDuplicateJoinMessage(){
        String sessionId="0xFFFF";

        PlayerJoined request = new PlayerJoined();
        request.setPlayerName(PlayerName.Player1);
        request.setPlayerNickname("TK");
        request.setSessionId(sessionId);

        JoinResult joinResult = new JoinResult(PlayerName.Player1,true,GameEngine.PLAYER_JOIN_SUCCESS);
        JoinResult joinResultBad = new JoinResult(PlayerName.Player1,false,GameEngine.PLAYER_ALREADY_JOINED_ERROR);


        assertEquals(gameEngine.requestToJoinGame(request),joinResult);
        assertEquals(gameEngine.requestToJoinGame(request),joinResultBad);
        assertFalse(gameEngine.canGameStart(sessionId));
        assertNull(gameEngine.getGameStartAndStartGame(sessionId));
    }

    @Test
    public void testInvalidSession(){
        String sessionId="0xFFFF";
        assertFalse(gameEngine.canGameStart(sessionId));
        assertNull(gameEngine.getGameStartAndStartGame(sessionId));
    }

    @Test
    public void testPlayerJoinAfterGameStart(){
        String sessionId="0xFFFF";
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

        JoinResult joinResult1 = new JoinResult(PlayerName.Player1,true,GameEngine.PLAYER_JOIN_SUCCESS);
        JoinResult joinResult2 = new JoinResult(PlayerName.Player2,true,GameEngine.PLAYER_JOIN_SUCCESS);
        JoinResult joinResult3 = new JoinResult(PlayerName.Player1, false, GameEngine.GAME_ALREADY_STARTED_ERROR);

        assertEquals(gameEngine.requestToJoinGame(request1),joinResult1);
        assertEquals(gameEngine.requestToJoinGame(request2),joinResult2);
        assertTrue(gameEngine.canGameStart(sessionId));
        assertEquals(gameEngine.getGameStartAndStartGame(sessionId),gameStart);
        assertEquals(gameEngine.requestToJoinGame(request1),joinResult3);
    }

    // board set requests

    @Test
    public void testRequestToSetBoardPlayer1(){
        /** setup board */
        String sessionId="0xFFFF";
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
        /** -+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+ */
        // form request
        PrivateBoardCellState[][] board = {{PrivateBoardCellState.empty, PrivateBoardCellState.empty,PrivateBoardCellState.empty,PrivateBoardCellState.empty,PrivateBoardCellState.ship},{PrivateBoardCellState.empty, PrivateBoardCellState.empty,PrivateBoardCellState.empty,PrivateBoardCellState.empty,PrivateBoardCellState.ship},{PrivateBoardCellState.empty, PrivateBoardCellState.empty,PrivateBoardCellState.empty,PrivateBoardCellState.empty,PrivateBoardCellState.ship},{PrivateBoardCellState.empty, PrivateBoardCellState.empty,PrivateBoardCellState.empty,PrivateBoardCellState.empty,PrivateBoardCellState.ship},{PrivateBoardCellState.empty, PrivateBoardCellState.empty,PrivateBoardCellState.empty,PrivateBoardCellState.empty,PrivateBoardCellState.ship}};
        BoardSetRequest boardSetRequest = new BoardSetRequest();
        boardSetRequest.setPlayerName(PlayerName.Player1);
        boardSetRequest.setBoard(board);
        boardSetRequest.setSessionId(sessionId);
        // mock successful response
        BoardSetResult boardSetResult = new BoardSetResult(PlayerName.Player1,true,GameEngine.BOARD_SET_SUCCESS);
        // check that the request works
        assertEquals(gameEngine.requestToSetBoard(boardSetRequest),boardSetResult);
        assertFalse(gameEngine.canMatchStart(sessionId));
        assertNull(gameEngine.getMatchStartAndStartMatch(sessionId));
    }

    @Test
    public void testRequestToSetBoardPlayer2(){
        /** setup board */
        String sessionId="0xFFFF";
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
        /** -+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+ */
        // form request
        PrivateBoardCellState[][] board = {{PrivateBoardCellState.empty, PrivateBoardCellState.empty,PrivateBoardCellState.empty,PrivateBoardCellState.empty,PrivateBoardCellState.ship},{PrivateBoardCellState.empty, PrivateBoardCellState.empty,PrivateBoardCellState.empty,PrivateBoardCellState.empty,PrivateBoardCellState.ship},{PrivateBoardCellState.empty, PrivateBoardCellState.empty,PrivateBoardCellState.empty,PrivateBoardCellState.empty,PrivateBoardCellState.ship},{PrivateBoardCellState.empty, PrivateBoardCellState.empty,PrivateBoardCellState.empty,PrivateBoardCellState.empty,PrivateBoardCellState.ship},{PrivateBoardCellState.empty, PrivateBoardCellState.empty,PrivateBoardCellState.empty,PrivateBoardCellState.empty,PrivateBoardCellState.ship}};
        BoardSetRequest boardSetRequest = new BoardSetRequest();
        boardSetRequest.setPlayerName(PlayerName.Player2);
        boardSetRequest.setBoard(board);
        boardSetRequest.setSessionId(sessionId);
        // mock successful response
        BoardSetResult boardSetResult = new BoardSetResult(PlayerName.Player2,true,GameEngine.BOARD_SET_SUCCESS);
        // check that the request works
        assertEquals(gameEngine.requestToSetBoard(boardSetRequest),boardSetResult);
        assertFalse(gameEngine.canMatchStart(sessionId));
        assertNull(gameEngine.getMatchStartAndStartMatch(sessionId));
    }

    @Test
    public void testBothPlayersSetBoard(){
        /** setup board */
        String sessionId="0xFFFF";
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
        /** -+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+ */
        // form board set requests
        PrivateBoardCellState[][] board = {{PrivateBoardCellState.empty, PrivateBoardCellState.empty,PrivateBoardCellState.empty,PrivateBoardCellState.empty,PrivateBoardCellState.ship},{PrivateBoardCellState.empty, PrivateBoardCellState.empty,PrivateBoardCellState.empty,PrivateBoardCellState.empty,PrivateBoardCellState.ship},{PrivateBoardCellState.empty, PrivateBoardCellState.empty,PrivateBoardCellState.empty,PrivateBoardCellState.empty,PrivateBoardCellState.ship},{PrivateBoardCellState.empty, PrivateBoardCellState.empty,PrivateBoardCellState.empty,PrivateBoardCellState.empty,PrivateBoardCellState.ship},{PrivateBoardCellState.empty, PrivateBoardCellState.empty,PrivateBoardCellState.empty,PrivateBoardCellState.empty,PrivateBoardCellState.ship}};
        BoardSetRequest boardSetRequest1 = new BoardSetRequest();
        boardSetRequest1.setPlayerName(PlayerName.Player1);
        boardSetRequest1.setBoard(board);
        boardSetRequest1.setSessionId(sessionId);
        BoardSetRequest boardSetRequest2 = new BoardSetRequest();
        boardSetRequest2.setPlayerName(PlayerName.Player2);
        boardSetRequest2.setBoard(board);
        boardSetRequest2.setSessionId(sessionId);
        // mock successful response
        BoardSetResult boardSetResult1 = new BoardSetResult(PlayerName.Player1,true,GameEngine.BOARD_SET_SUCCESS);
        BoardSetResult boardSetResult2 = new BoardSetResult(PlayerName.Player2,true,GameEngine.BOARD_SET_SUCCESS);
        MatchStart matchStart = new MatchStart();
        matchStart.setPlayer1Board(boardSetResult1);
        matchStart.setPlayer2Board(boardSetResult2);
        // check that the set board request works
        assertEquals(gameEngine.requestToSetBoard(boardSetRequest1),boardSetResult1);
        assertEquals(gameEngine.requestToSetBoard(boardSetRequest2),boardSetResult2);
        // check that the match starts properly
        assertTrue(gameEngine.canMatchStart(sessionId));
        assertEquals(gameEngine.getMatchStartAndStartMatch(sessionId), matchStart);
    }

    // move requests

    @Test
    public void testRequestToMakeMovePlayer1Miss(){
        /** setup board */
        String sessionId="0xFFFF";
        // join game
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
        // set boards
        PrivateBoardCellState[][] board = {{PrivateBoardCellState.empty, PrivateBoardCellState.empty,PrivateBoardCellState.empty,PrivateBoardCellState.empty,PrivateBoardCellState.ship},{PrivateBoardCellState.empty, PrivateBoardCellState.empty,PrivateBoardCellState.empty,PrivateBoardCellState.empty,PrivateBoardCellState.ship},{PrivateBoardCellState.empty, PrivateBoardCellState.empty,PrivateBoardCellState.empty,PrivateBoardCellState.empty,PrivateBoardCellState.ship},{PrivateBoardCellState.empty, PrivateBoardCellState.empty,PrivateBoardCellState.empty,PrivateBoardCellState.empty,PrivateBoardCellState.ship},{PrivateBoardCellState.empty, PrivateBoardCellState.empty,PrivateBoardCellState.empty,PrivateBoardCellState.empty,PrivateBoardCellState.ship}};
        BoardSetRequest boardSetRequest1 = new BoardSetRequest();
        boardSetRequest1.setPlayerName(PlayerName.Player1);
        boardSetRequest1.setBoard(board);
        boardSetRequest1.setSessionId(sessionId);
        BoardSetRequest boardSetRequest2 = new BoardSetRequest();
        boardSetRequest2.setPlayerName(PlayerName.Player2);
        boardSetRequest2.setBoard(board);
        boardSetRequest2.setSessionId(sessionId);
        gameEngine.requestToSetBoard(boardSetRequest1);
        gameEngine.requestToSetBoard(boardSetRequest2);
        // start the match
        gameEngine.getMatchStartAndStartMatch(sessionId);
        /** -+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+ */
        // form move request
        Move moveRequest = new Move(PlayerName.Player1,1, 1);
        moveRequest.setSessionId(sessionId);
        MoveResponseEvent moveReponse = new MoveResponseEvent();
        // mock successful MISS
        KnownBoardCellState[][] boardWithMiss = new KnownBoardCellState[5][5];
        boardWithMiss[moveRequest.getX()][moveRequest.getY()] = KnownBoardCellState.miss;
        MoveResponseEvent mockedMissResponse = new MoveResponseEvent();
        mockedMissResponse.setSessionId(sessionId);
        mockedMissResponse.setPlayer(PlayerName.Player1);
        mockedMissResponse.setMove(moveRequest);
        mockedMissResponse.setPlayerBoard(boardWithMiss);
        mockedMissResponse.setMoveResult(PrivateBoardCellState.empty);
        assertEquals(gameEngine.requestToMakeMove(moveRequest), mockedMissResponse);
    }

    @Test
    public void testRequestToMakeMovePlayer2Miss(){
        /** setup board */
        String sessionId="0xFFFF";
        // join game
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
        // set boards
        PrivateBoardCellState[][] board = {{PrivateBoardCellState.empty, PrivateBoardCellState.empty,PrivateBoardCellState.empty,PrivateBoardCellState.empty,PrivateBoardCellState.ship},{PrivateBoardCellState.empty, PrivateBoardCellState.empty,PrivateBoardCellState.empty,PrivateBoardCellState.empty,PrivateBoardCellState.ship},{PrivateBoardCellState.empty, PrivateBoardCellState.empty,PrivateBoardCellState.empty,PrivateBoardCellState.empty,PrivateBoardCellState.ship},{PrivateBoardCellState.empty, PrivateBoardCellState.empty,PrivateBoardCellState.empty,PrivateBoardCellState.empty,PrivateBoardCellState.ship},{PrivateBoardCellState.empty, PrivateBoardCellState.empty,PrivateBoardCellState.empty,PrivateBoardCellState.empty,PrivateBoardCellState.ship}};
        BoardSetRequest boardSetRequest1 = new BoardSetRequest();
        boardSetRequest1.setPlayerName(PlayerName.Player1);
        boardSetRequest1.setBoard(board);
        boardSetRequest1.setSessionId(sessionId);
        BoardSetRequest boardSetRequest2 = new BoardSetRequest();
        boardSetRequest2.setPlayerName(PlayerName.Player2);
        boardSetRequest2.setBoard(board);
        boardSetRequest2.setSessionId(sessionId);
        gameEngine.requestToSetBoard(boardSetRequest1);
        gameEngine.requestToSetBoard(boardSetRequest2);
        // start the match
        gameEngine.getMatchStartAndStartMatch(sessionId);
        // player 1 goes first
        Move moveRequestPlayer1 = new Move(PlayerName.Player1,1, 1);
        moveRequestPlayer1.setSessionId(sessionId);
        gameEngine.requestToMakeMove(moveRequestPlayer1);
        /** -+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+ */
        // form move request
        Move moveRequest = new Move(PlayerName.Player2,1, 1);
        moveRequest.setSessionId(sessionId);
        MoveResponseEvent moveReponse = new MoveResponseEvent();
        // mock successful MISS
        KnownBoardCellState[][] boardWithMiss = new KnownBoardCellState[5][5];
        boardWithMiss[moveRequest.getX()][moveRequest.getY()] = KnownBoardCellState.miss;
        MoveResponseEvent mockedMissResponse = new MoveResponseEvent();
        mockedMissResponse.setSessionId(sessionId);
        mockedMissResponse.setPlayer(PlayerName.Player2);
        mockedMissResponse.setMove(moveRequest);
        mockedMissResponse.setPlayerBoard(boardWithMiss);
        mockedMissResponse.setMoveResult(PrivateBoardCellState.empty);
        // check response
        assertEquals(gameEngine.requestToMakeMove(moveRequest), mockedMissResponse);
    }

    @Test
    public void testRequestToMakeMovePlayer1Hit(){
        /** setup board */
        String sessionId="0xFFFF";
        // join game
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
        // set boards
        PrivateBoardCellState[][] board = {{PrivateBoardCellState.empty, PrivateBoardCellState.empty,PrivateBoardCellState.empty,PrivateBoardCellState.empty,PrivateBoardCellState.ship},{PrivateBoardCellState.empty, PrivateBoardCellState.empty,PrivateBoardCellState.empty,PrivateBoardCellState.empty,PrivateBoardCellState.ship},{PrivateBoardCellState.empty, PrivateBoardCellState.empty,PrivateBoardCellState.empty,PrivateBoardCellState.empty,PrivateBoardCellState.ship},{PrivateBoardCellState.empty, PrivateBoardCellState.empty,PrivateBoardCellState.empty,PrivateBoardCellState.empty,PrivateBoardCellState.ship},{PrivateBoardCellState.empty, PrivateBoardCellState.empty,PrivateBoardCellState.empty,PrivateBoardCellState.empty,PrivateBoardCellState.ship}};
        BoardSetRequest boardSetRequest1 = new BoardSetRequest();
        boardSetRequest1.setPlayerName(PlayerName.Player1);
        boardSetRequest1.setBoard(board);
        boardSetRequest1.setSessionId(sessionId);
        BoardSetRequest boardSetRequest2 = new BoardSetRequest();
        boardSetRequest2.setPlayerName(PlayerName.Player2);
        boardSetRequest2.setBoard(board);
        boardSetRequest2.setSessionId(sessionId);
        gameEngine.requestToSetBoard(boardSetRequest1);
        gameEngine.requestToSetBoard(boardSetRequest2);
        // start the match
        gameEngine.getMatchStartAndStartMatch(sessionId);
        /** -+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+ */
        // form move request
        Move moveRequest = new Move(PlayerName.Player1,0, 4); // there's a ship at 0,4 1,4 2,4 3,4 4,4
        moveRequest.setSessionId(sessionId);
        // mock successful MISS
        KnownBoardCellState[][] boardWithHit = new KnownBoardCellState[5][5];
        boardWithHit[moveRequest.getX()][moveRequest.getY()] = KnownBoardCellState.hit;
        MoveResponseEvent mockedHitResponse = new MoveResponseEvent();
        mockedHitResponse.setSessionId(sessionId);
        mockedHitResponse.setPlayer(PlayerName.Player1);
        mockedHitResponse.setMove(moveRequest);
        mockedHitResponse.setPlayerBoard(boardWithHit);
        mockedHitResponse.setMoveResult(PrivateBoardCellState.ship);
        assertEquals(gameEngine.requestToMakeMove(moveRequest), mockedHitResponse);
    }

    @Test
    public void testRequestToMakeMovePlayer2Hit(){
        /** setup board */
        String sessionId="0xFFFF";
        // join game
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
        // set boards
        PrivateBoardCellState[][] board = {{PrivateBoardCellState.empty, PrivateBoardCellState.empty,PrivateBoardCellState.empty,PrivateBoardCellState.empty,PrivateBoardCellState.ship},{PrivateBoardCellState.empty, PrivateBoardCellState.empty,PrivateBoardCellState.empty,PrivateBoardCellState.empty,PrivateBoardCellState.ship},{PrivateBoardCellState.empty, PrivateBoardCellState.empty,PrivateBoardCellState.empty,PrivateBoardCellState.empty,PrivateBoardCellState.ship},{PrivateBoardCellState.empty, PrivateBoardCellState.empty,PrivateBoardCellState.empty,PrivateBoardCellState.empty,PrivateBoardCellState.ship},{PrivateBoardCellState.empty, PrivateBoardCellState.empty,PrivateBoardCellState.empty,PrivateBoardCellState.empty,PrivateBoardCellState.ship}};
        BoardSetRequest boardSetRequest1 = new BoardSetRequest();
        boardSetRequest1.setPlayerName(PlayerName.Player1);
        boardSetRequest1.setBoard(board);
        boardSetRequest1.setSessionId(sessionId);
        BoardSetRequest boardSetRequest2 = new BoardSetRequest();
        boardSetRequest2.setPlayerName(PlayerName.Player2);
        boardSetRequest2.setBoard(board);
        boardSetRequest2.setSessionId(sessionId);
        gameEngine.requestToSetBoard(boardSetRequest1);
        gameEngine.requestToSetBoard(boardSetRequest2);
        // start the match
        gameEngine.getMatchStartAndStartMatch(sessionId);
        // player 1 goes first
        Move moveRequestPlayer1 = new Move(PlayerName.Player1,1, 1);
        moveRequestPlayer1.setSessionId(sessionId);
        gameEngine.requestToMakeMove(moveRequestPlayer1);
        /** -+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+ */
        // form move request
        Move moveRequest = new Move(PlayerName.Player2,0, 4); // there's a ship at 0,4 1,4 2,4 3,4 4,4
        moveRequest.setSessionId(sessionId);
        // mock board if successful hit
        KnownBoardCellState[][] boardWithHit = new KnownBoardCellState[5][5];
        boardWithHit[moveRequest.getX()][moveRequest.getY()] = KnownBoardCellState.hit;
        // mock move response event
        MoveResponseEvent mockedHitResponse = new MoveResponseEvent();
        mockedHitResponse.setSessionId(sessionId);
        mockedHitResponse.setPlayer(PlayerName.Player2);
        mockedHitResponse.setMove(moveRequest);
        mockedHitResponse.setPlayerBoard(boardWithHit);
        mockedHitResponse.setMoveResult(PrivateBoardCellState.ship);
        assertEquals(gameEngine.requestToMakeMove(moveRequest), mockedHitResponse);
    }

    @Test
    public void testCompleteGame(){
        /** setup board */
        String sessionId="0xFFFF";
        // join game
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
        // set boards
        PrivateBoardCellState[][] board = {{PrivateBoardCellState.empty, PrivateBoardCellState.empty,PrivateBoardCellState.empty,PrivateBoardCellState.empty,PrivateBoardCellState.ship},{PrivateBoardCellState.empty, PrivateBoardCellState.empty,PrivateBoardCellState.empty,PrivateBoardCellState.empty,PrivateBoardCellState.ship},{PrivateBoardCellState.empty, PrivateBoardCellState.empty,PrivateBoardCellState.empty,PrivateBoardCellState.empty,PrivateBoardCellState.ship},{PrivateBoardCellState.empty, PrivateBoardCellState.empty,PrivateBoardCellState.empty,PrivateBoardCellState.empty,PrivateBoardCellState.ship},{PrivateBoardCellState.empty, PrivateBoardCellState.empty,PrivateBoardCellState.empty,PrivateBoardCellState.empty,PrivateBoardCellState.ship}};
        BoardSetRequest boardSetRequest1 = new BoardSetRequest();
        boardSetRequest1.setPlayerName(PlayerName.Player1);
        boardSetRequest1.setBoard(board);
        boardSetRequest1.setSessionId(sessionId);
        BoardSetRequest boardSetRequest2 = new BoardSetRequest();
        boardSetRequest2.setPlayerName(PlayerName.Player2);
        boardSetRequest2.setBoard(board);
        boardSetRequest2.setSessionId(sessionId);
        gameEngine.requestToSetBoard(boardSetRequest1);
        gameEngine.requestToSetBoard(boardSetRequest2);
        // start the match
        gameEngine.getMatchStartAndStartMatch(sessionId);
        /** -+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+ */
        // MOCK FULL GAME OF MOVES
        // Player 1 moves are correct
        Move moveRequestPlayer1_1 = new Move(PlayerName.Player1,0, 4);
        moveRequestPlayer1_1.setSessionId(sessionId);
        Move moveRequestPlayer1_2 = new Move(PlayerName.Player1,1, 4);
        moveRequestPlayer1_2.setSessionId(sessionId);
        Move moveRequestPlayer1_3 = new Move(PlayerName.Player1,2, 4);
        moveRequestPlayer1_3.setSessionId(sessionId);
        Move moveRequestPlayer1_4 = new Move(PlayerName.Player1,3, 4);
        moveRequestPlayer1_4.setSessionId(sessionId);
        Move moveRequestPlayer1_5 = new Move(PlayerName.Player1,4, 4);
        moveRequestPlayer1_5.setSessionId(sessionId);
        // Player 2 moves are incorrect :(
        Move moveRequestPlayer2_1 = new Move(PlayerName.Player2,0, 3);
        moveRequestPlayer2_1.setSessionId(sessionId);
        Move moveRequestPlayer2_2 = new Move(PlayerName.Player2,1, 3);
        moveRequestPlayer2_2.setSessionId(sessionId);
        Move moveRequestPlayer2_3 = new Move(PlayerName.Player2,2, 3);
        moveRequestPlayer2_3.setSessionId(sessionId);
        Move moveRequestPlayer2_4 = new Move(PlayerName.Player2,3, 3);
        moveRequestPlayer2_4.setSessionId(sessionId);
        Move moveRequestPlayer2_5 = new Move(PlayerName.Player2,4, 3);
        moveRequestPlayer2_5.setSessionId(sessionId);

        gameEngine.requestToMakeMove(moveRequestPlayer1_1);
        gameEngine.requestToMakeMove(moveRequestPlayer2_1);
        gameEngine.requestToMakeMove(moveRequestPlayer1_2);
        gameEngine.requestToMakeMove(moveRequestPlayer2_2);
        gameEngine.requestToMakeMove(moveRequestPlayer1_3);
        gameEngine.requestToMakeMove(moveRequestPlayer2_3);
        gameEngine.requestToMakeMove(moveRequestPlayer1_4);
        gameEngine.requestToMakeMove(moveRequestPlayer2_4);
        gameEngine.requestToMakeMove(moveRequestPlayer1_5);

        // mock MatchEnd
        MatchEnd mockMatchEnd = new MatchEnd();
        mockMatchEnd.setSessionId(sessionId);
        mockMatchEnd.setPlayer1Score(5);
        mockMatchEnd.setPlayer2Score(0);

        assertEquals(gameEngine.shouldMatchEnd(sessionId), true);
        assertEquals(gameEngine.endMatch(sessionId), mockMatchEnd);
    }
}
