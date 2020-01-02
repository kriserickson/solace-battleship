package com.solace.battleship.flows;

import com.solace.battleship.engine.GameEngine;
import com.solace.battleship.engine.IGameEngine;
import com.solace.battleship.events.*;
import junit.framework.TestCase;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.cloud.stream.binding.BinderAwareChannelResolver;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;


/**
 * MoveRequestProcessorTest
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = MoveRequestProcessor.class)
public class MoveRequestProcessorTest {
    @Autowired
    private MoveRequestProcessor.MoveRequestBinding processor;

    @MockBean
    private BinderAwareChannelResolver resolver;

    @MockBean
    private IGameEngine gameEngine;

    @Before
    public void setup(){
        MessageChannel mockChannel = Mockito.mock(MessageChannel.class);
        Mockito.when(mockChannel.send(any(Message.class))).thenReturn(true);
        Mockito.when(resolver.resolveDestination(anyString())).thenReturn(mockChannel);
    }

    @Test
    public void testPlayer1MoveMiss(){
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
        // run integration tests
        Mockito.when(gameEngine.requestToMakeMove(moveRequest)).thenReturn(mockedMissResponse);
        Message<Move> message = MessageBuilder.withPayload(moveRequest).setHeader("reply-to", "ReplyTopic")
                .build();
        processor.move_request().send(message);
        verify(resolver.resolveDestination("ReplyTopic"),times(1)).send(any(Message.class));
    }

    @Test
    public void testPlayer2MoveMiss(){
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
        // run integration tests
        Mockito.when(gameEngine.requestToMakeMove(moveRequest)).thenReturn(mockedMissResponse);
        Message<Move> message = MessageBuilder.withPayload(moveRequest).setHeader("reply-to", "ReplyTopic")
                .build();
        processor.move_request().send(message);
        verify(resolver.resolveDestination("ReplyTopic"),times(1)).send(any(Message.class));
    }

    @Test
    public void testPlayer1MoveHit(){
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
        Move moveRequest = new Move(PlayerName.Player1,0, 4);
        moveRequest.setSessionId(sessionId);
        MoveResponseEvent moveReponse = new MoveResponseEvent();
        // mock successful HIT
        KnownBoardCellState[][] boardWithHit = new KnownBoardCellState[5][5];
        boardWithHit[moveRequest.getX()][moveRequest.getY()] = KnownBoardCellState.hit;
        MoveResponseEvent mockedHitResponse = new MoveResponseEvent();
        mockedHitResponse.setSessionId(sessionId);
        mockedHitResponse.setPlayer(PlayerName.Player1);
        mockedHitResponse.setMove(moveRequest);
        mockedHitResponse.setPlayerBoard(boardWithHit);
        mockedHitResponse.setMoveResult(PrivateBoardCellState.ship);
        // run integration tests
        Mockito.when(gameEngine.requestToMakeMove(moveRequest)).thenReturn(mockedHitResponse);
        Message<Move> message = MessageBuilder.withPayload(moveRequest).setHeader("reply-to", "ReplyTopic")
                .build();
        processor.move_request().send(message);
        verify(resolver.resolveDestination("ReplyTopic"),times(1)).send(any(Message.class));
    }

    @Test
    public void testPlayer2MoveHit(){
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
        Move moveRequest = new Move(PlayerName.Player2,0, 4);
        moveRequest.setSessionId(sessionId);
        // mock successful HIT
        KnownBoardCellState[][] boardWithHit = new KnownBoardCellState[5][5];
        boardWithHit[moveRequest.getX()][moveRequest.getY()] = KnownBoardCellState.hit;
        MoveResponseEvent mockedHitResponse = new MoveResponseEvent();
        mockedHitResponse.setSessionId(sessionId);
        mockedHitResponse.setPlayer(PlayerName.Player2);
        mockedHitResponse.setMove(moveRequest);
        mockedHitResponse.setPlayerBoard(boardWithHit);
        mockedHitResponse.setMoveResult(PrivateBoardCellState.ship);
        // run integration tests
        Mockito.when(gameEngine.requestToMakeMove(moveRequest)).thenReturn(mockedHitResponse);
        Message<Move> message = MessageBuilder.withPayload(moveRequest).setHeader("reply-to", "ReplyTopic")
                .build();
        processor.move_request().send(message);
        verify(resolver.resolveDestination("ReplyTopic"),times(1)).send(any(Message.class));
    }

    @Test
    public void testCompleteGame() {
        /** setup board */
        String sessionId = "0xFFFF";
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
        PrivateBoardCellState[][] board = {{PrivateBoardCellState.empty, PrivateBoardCellState.empty, PrivateBoardCellState.empty, PrivateBoardCellState.empty, PrivateBoardCellState.ship}, {PrivateBoardCellState.empty, PrivateBoardCellState.empty, PrivateBoardCellState.empty, PrivateBoardCellState.empty, PrivateBoardCellState.ship}, {PrivateBoardCellState.empty, PrivateBoardCellState.empty, PrivateBoardCellState.empty, PrivateBoardCellState.empty, PrivateBoardCellState.ship}, {PrivateBoardCellState.empty, PrivateBoardCellState.empty, PrivateBoardCellState.empty, PrivateBoardCellState.empty, PrivateBoardCellState.ship}, {PrivateBoardCellState.empty, PrivateBoardCellState.empty, PrivateBoardCellState.empty, PrivateBoardCellState.empty, PrivateBoardCellState.ship}};
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
        // make moves
        // Player 1 moves are correct
        Move moveRequestPlayer1_1 = new Move(PlayerName.Player1, 0, 4);
        moveRequestPlayer1_1.setSessionId(sessionId);
        Move moveRequestPlayer1_2 = new Move(PlayerName.Player1, 1, 4);
        moveRequestPlayer1_2.setSessionId(sessionId);
        Move moveRequestPlayer1_3 = new Move(PlayerName.Player1, 2, 4);
        moveRequestPlayer1_3.setSessionId(sessionId);
        Move moveRequestPlayer1_4 = new Move(PlayerName.Player1, 3, 4);
        moveRequestPlayer1_4.setSessionId(sessionId);
        Move moveRequestPlayer1_5 = new Move(PlayerName.Player1, 4, 4);
        moveRequestPlayer1_5.setSessionId(sessionId);
        // Player 2 moves are incorrect :(
        Move moveRequestPlayer2_1 = new Move(PlayerName.Player2, 0, 3);
        moveRequestPlayer2_1.setSessionId(sessionId);
        Move moveRequestPlayer2_2 = new Move(PlayerName.Player2, 1, 3);
        moveRequestPlayer2_2.setSessionId(sessionId);
        Move moveRequestPlayer2_3 = new Move(PlayerName.Player2, 2, 3);
        moveRequestPlayer2_3.setSessionId(sessionId);
        Move moveRequestPlayer2_4 = new Move(PlayerName.Player2, 3, 3);
        moveRequestPlayer2_4.setSessionId(sessionId);
        Move moveRequestPlayer2_5 = new Move(PlayerName.Player2, 4, 3);
        moveRequestPlayer2_5.setSessionId(sessionId);
        gameEngine.requestToMakeMove(moveRequestPlayer1_1);
        gameEngine.requestToMakeMove(moveRequestPlayer2_1);
        gameEngine.requestToMakeMove(moveRequestPlayer1_2);
        gameEngine.requestToMakeMove(moveRequestPlayer2_2);
        gameEngine.requestToMakeMove(moveRequestPlayer1_3);
        gameEngine.requestToMakeMove(moveRequestPlayer2_3);
        gameEngine.requestToMakeMove(moveRequestPlayer1_4);
        gameEngine.requestToMakeMove(moveRequestPlayer2_4);
        // except for last move
        /** -+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+ */
        // mock final HIT
        KnownBoardCellState[][] boardWithHit = new KnownBoardCellState[5][5];
        boardWithHit[moveRequestPlayer1_5.getX()][moveRequestPlayer1_5.getY()] = KnownBoardCellState.hit;
        MoveResponseEvent mockedHitResponse = new MoveResponseEvent();
        mockedHitResponse.setSessionId(sessionId);
        mockedHitResponse.setPlayer(PlayerName.Player1);
        mockedHitResponse.setMove(moveRequestPlayer1_5);
        mockedHitResponse.setPlayerBoard(boardWithHit);
        mockedHitResponse.setMoveResult(PrivateBoardCellState.ship);
        Mockito.when(gameEngine.requestToMakeMove(moveRequestPlayer1_5)).thenReturn(mockedHitResponse);
        // mock MatchEnd
        MatchEnd mockMatchEnd = new MatchEnd();
        mockMatchEnd.setSessionId(sessionId);
        mockMatchEnd.setPlayer1Score(5);
        mockMatchEnd.setPlayer2Score(0);
        // run integration tests
        Mockito.when(gameEngine.shouldMatchEnd(sessionId)).thenReturn(true);
        Mockito.when(gameEngine.endMatch(sessionId)).thenReturn(mockMatchEnd);
        Message<Move> message = MessageBuilder.withPayload(moveRequestPlayer1_5).setHeader("reply-to", "ReplyTopic")
                .build();

        processor.move_request().send(message);

        verify(resolver,times(1)).resolveDestination("ReplyTopic");
        verify(resolver,times(1)).resolveDestination("SOLACE/BATTLESHIP/"+sessionId+"/MATCH-END/CONTROLLER");
    }
}