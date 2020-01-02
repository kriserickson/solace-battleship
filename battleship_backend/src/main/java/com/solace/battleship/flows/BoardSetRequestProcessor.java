package com.solace.battleship.flows;

import com.solace.battleship.engine.IGameEngine;
import com.solace.battleship.events.BoardSetRequest;
import com.solace.battleship.events.BoardSetResult;
import com.solace.battleship.flows.BoardSetRequestProcessor.BoardSetRequestBinding;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.Input;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.cloud.stream.binding.BinderAwareChannelResolver;
import org.springframework.messaging.Message;
import org.springframework.messaging.SubscribableChannel;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.support.MessageBuilder;

/**
 * This Spring Cloud Stream processor handles board set requests for the Battleship
 * Game
 *
 * @author Andrew Roberts
 */
@SpringBootApplication
@EnableBinding(BoardSetRequestBinding.class)
public class BoardSetRequestProcessor {

    @Autowired
    private BinderAwareChannelResolver resolver;

    @Autowired
    private IGameEngine gameEngine;

    // We define an INPUT to receive data from and dynamically specify the reply to
    // destination depending on the header and state of the game enginer
    @StreamListener(BoardSetRequestBinding.INPUT)
    public void handle(BoardSetRequest boardSetRequest, @Header("reply-to") String replyTo) {
        // Pass the request to the game engine to join the game
        BoardSetResult result = gameEngine.requestToSetBoard(boardSetRequest);
        resolver.resolveDestination(replyTo).send(message(result));

        if (result.isSuccess() && gameEngine.canMatchStart(boardSetRequest.getSessionId())) {
            resolver.resolveDestination("SOLACE/BATTLESHIP/" + boardSetRequest.getSessionId() + "/MATCH-START/CONTROLLER")
                    .send(message(gameEngine.getMatchStartAndStartMatch(boardSetRequest.getSessionId())));
        }

    }

    private static final <T> Message<T> message(T val) {
        return MessageBuilder.withPayload(val).build();
    }

    /*
     * Custom Processor Binding Interface to allow for multiple outputs
     */
    public interface BoardSetRequestBinding {
        String INPUT = "board_set_request";

        @Input
        SubscribableChannel board_set_request();
    }
}