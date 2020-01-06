package com.solace.battleship.flows;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.solace.battleship.engine.IGameEngine;
import com.solace.battleship.events.GameStart;
import com.solace.battleship.events.JoinResult;
import com.solace.battleship.events.PlayerJoined;
import com.solace.battleship.events.PlayerName;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.cloud.stream.binding.BinderAwareChannelResolver;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * JoinProcessorTest
 */
@SpringBootApplication
@RunWith(SpringRunner.class)
@SpringBootTest(classes = JoinProcessor.class)
public class JoinProcessorTest {
    @Autowired
    private JoinProcessor.JoinRequestBinding processor;

    @MockBean
    private BinderAwareChannelResolver resolver;

    @MockBean
    private IGameEngine gameEngine;

    @Before
    public void setup() {
        MessageChannel mockChannel = Mockito.mock(MessageChannel.class);
        Mockito.when(mockChannel.send(any(Message.class))).thenReturn(true);

        Mockito.when(resolver.resolveDestination(anyString())).thenReturn(mockChannel);
    }

    @Test
    /**
     * Tests the JoinProcessor when just player1 joins the game
     */
    public void testPlayer1Join() {
        String sessionId = "00xfff";
        PlayerJoined joinRequest = new PlayerJoined();
        joinRequest.setPlayerName(PlayerName.player1);
        joinRequest.setSessionId(sessionId);
        joinRequest.setPlayerNickname("TK");
        JoinResult joinResult = new JoinResult(PlayerName.player1, true, "Player1 Joined!");
        Mockito.when(gameEngine.requestToJoinGame(joinRequest)).thenReturn(joinResult);
        Mockito.when(gameEngine.canGameStart(sessionId)).thenReturn(false);
        Message<PlayerJoined> message = MessageBuilder.withPayload(joinRequest).setHeader("reply-to", "ReplyTopic")
                .build();
        processor.join_request().send(message);
        verify(resolver.resolveDestination("ReplyTopic"), times(1)).send(any(Message.class));

    }

    @Test
    /**
     * Tests the JoinProcessor when just player2 joins the game
     */
    public void testPlayer2Join() {
        String sessionId = "00xfff";
        PlayerJoined joinRequest = new PlayerJoined();
        joinRequest.setPlayerName(PlayerName.player2);
        joinRequest.setSessionId(sessionId);
        joinRequest.setPlayerNickname("TK");
        JoinResult joinResult = new JoinResult(PlayerName.player2, true, "Player2 Joined!");
        Mockito.when(gameEngine.requestToJoinGame(joinRequest)).thenReturn(joinResult);
        Mockito.when(gameEngine.canGameStart(sessionId)).thenReturn(false);
        Message<PlayerJoined> message = MessageBuilder.withPayload(joinRequest).setHeader("reply-to", "ReplyTopic")
                .build();
        processor.join_request().send(message);
        verify(resolver.resolveDestination("ReplyTopic"), times(1)).send(any(Message.class));

    }

    @Test
    /**
     * Tests the JoinProcessor when both player1 and player2 joins the game
     */
    public void testBothPlayersJoined() {
        String sessionId = "00xfff";
        PlayerJoined joinRequest1 = new PlayerJoined();
        joinRequest1.setPlayerName(PlayerName.player1);
        joinRequest1.setSessionId(sessionId);
        joinRequest1.setPlayerNickname("TK");
        PlayerJoined joinRequest2 = new PlayerJoined();
        joinRequest2.setPlayerName(PlayerName.player2);
        joinRequest2.setSessionId(sessionId);
        joinRequest2.setPlayerNickname("TK2");

        JoinResult joinResult1 = new JoinResult(PlayerName.player1, true, "Player1 Joined!");
        JoinResult joinResult2 = new JoinResult(PlayerName.player2, true, "Player1 Joined!");

        Mockito.when(gameEngine.requestToJoinGame(joinRequest1)).thenReturn(joinResult1);
        Mockito.when(gameEngine.requestToJoinGame(joinRequest2)).thenReturn(joinResult2);

        Mockito.when(gameEngine.canGameStart(sessionId)).thenReturn(false);

        Message<PlayerJoined> message1 = MessageBuilder.withPayload(joinRequest1).setHeader("reply-to", "ReplyTopic1")
                .build();
        Message<PlayerJoined> message2 = MessageBuilder.withPayload(joinRequest2).setHeader("reply-to", "ReplyTopic2")
                .build();

        processor.join_request().send(message1);

        Mockito.when(gameEngine.canGameStart(sessionId)).thenReturn(true);
        GameStart gameStart = new GameStart();
        gameStart.setPlayerJoined(joinRequest1);
        gameStart.setPlayerJoined(joinRequest2);
        Mockito.when(gameEngine.getGameStartAndStartGame(sessionId)).thenReturn(gameStart);
        processor.join_request().send(message2);
        verify(resolver, times(1)).resolveDestination("ReplyTopic1");
        verify(resolver, times(1)).resolveDestination("ReplyTopic2");
        verify(resolver, times(1)).resolveDestination("SOLACE/BATTLESHIP/" + sessionId + "/GAME-START/CONTROLLER");
    }
}