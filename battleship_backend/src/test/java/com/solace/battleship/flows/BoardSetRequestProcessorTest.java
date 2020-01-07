package com.solace.battleship.flows;

import com.solace.battleship.engine.GameEngine;
import com.solace.battleship.events.*;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.cloud.stream.binding.BinderAwareChannelResolver;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.test.context.junit4.SpringRunner;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import org.springframework.integration.support.MessageBuilder;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import com.solace.battleship.engine.IGameEngine;

/**
 * BoardSetRequestProcessorTest
 */
@SpringBootApplication
@RunWith(SpringRunner.class)
@SpringBootTest(classes = BoardSetRequestProcessor.class)
public class BoardSetRequestProcessorTest {

    @Autowired
  private BoardSetRequestProcessor.BoardSetRequestBinding processor;

  @MockBean
  private BinderAwareChannelResolver resolver;

  @MockBean
  private IGameEngine gameEngine;

  private static final String sessionId = "0xFFFF";

  @Before
  public void setup() {
    MessageChannel mockChannel = Mockito.mock(MessageChannel.class);
    Mockito.when(mockChannel.send(any(Message.class))).thenReturn(true);
    Mockito.when(resolver.resolveDestination(anyString())).thenReturn(mockChannel);
  }

  @Test
  public void testPlayer1SetBoard() {
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
    boardSetRequest.setPlayerName(PlayerName.player1);
    boardSetRequest.setBoard(board);
    boardSetRequest.setSessionId(sessionId);
    // mock successful response
    BoardSetResult boardSetResult = new BoardSetResult(PlayerName.player1, true, GameEngine.BOARD_SET_SUCCESS);
    // run integration tests
    Mockito.when(gameEngine.requestToSetBoard(boardSetRequest)).thenReturn(boardSetResult);
    Message<BoardSetRequest> message = MessageBuilder.withPayload(boardSetRequest).setHeader("reply-to", "ReplyTopic")
        .build();
    processor.board_set_request().send(message);
    verify(resolver.resolveDestination("ReplyTopic"), times(1)).send(any(Message.class));
  }

  @Test
  public void testPlayer2SetBoard() {
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
    boardSetRequest.setPlayerName(PlayerName.player2);
    boardSetRequest.setBoard(board);
    boardSetRequest.setSessionId(sessionId);
    // mock successful response
    BoardSetResult boardSetResult = new BoardSetResult(PlayerName.player2, true, GameEngine.BOARD_SET_SUCCESS);
    // run integration tests
    Mockito.when(gameEngine.requestToSetBoard(boardSetRequest)).thenReturn(boardSetResult);
    Message<BoardSetRequest> message = MessageBuilder.withPayload(boardSetRequest).setHeader("reply-to", "ReplyTopic")
        .build();
    processor.board_set_request().send(message);
    verify(resolver.resolveDestination("ReplyTopic"), times(1)).send(any(Message.class));
  }

  @Test
  public void testBothPlayersSetBoards() {
    /** -+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+ */
    // BOARD SET
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
    boardSetRequest1.setPlayerName(PlayerName.player1);
    boardSetRequest1.setBoard(board);
    boardSetRequest1.setSessionId(sessionId);
    BoardSetRequest boardSetRequest2 = new BoardSetRequest();
    boardSetRequest2.setPlayerName(PlayerName.player2);
    boardSetRequest2.setBoard(board);
    boardSetRequest2.setSessionId(sessionId);

    BoardSetResult boardSetResult1 = new BoardSetResult(PlayerName.player1, true, GameEngine.BOARD_SET_SUCCESS);
    Mockito.when(gameEngine.requestToSetBoard(boardSetRequest1)).thenReturn(boardSetResult1);
    Message<BoardSetRequest> message1 = MessageBuilder.withPayload(boardSetRequest1)
        .setHeader("reply-to", "ReplyTopic1").build();

    processor.board_set_request().send(message1);

    BoardSetResult boardSetResult2 = new BoardSetResult(PlayerName.player2, true, GameEngine.BOARD_SET_SUCCESS);
    Mockito.when(gameEngine.requestToSetBoard(boardSetRequest2)).thenReturn(boardSetResult2);
    Message<BoardSetRequest> message2 = MessageBuilder.withPayload(boardSetRequest2)
        .setHeader("reply-to", "ReplyTopic2").build();

    MatchStart matchStart = new MatchStart();
    matchStart.setPlayer1Board(boardSetResult1);
    matchStart.setPlayer2Board(boardSetResult2);
    Mockito.when(gameEngine.canMatchStart(sessionId)).thenReturn(true);
    Mockito.when(gameEngine.getMatchStartAndStartMatch(sessionId)).thenReturn(matchStart);

    processor.board_set_request().send(message2);

    verify(resolver, times(1)).resolveDestination("ReplyTopic1");
    verify(resolver, times(1)).resolveDestination("ReplyTopic2");
    verify(resolver, times(1)).resolveDestination("SOLACE/BATTLESHIP/" + sessionId + "/MATCH-START/CONTROLLER");
  }


}