package com.solace.battleship.flows;

import com.solace.battleship.engine.IGameEngine;
import com.solace.battleship.events.JoinResult;
import com.solace.battleship.events.PlayerJoined;
import com.solace.battleship.flows.JoinProcessor.JoinRequestBinding;

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
 * This Spring Cloud Stream processor handles join-requests for the Battleship
 * Game
 * 
 * @author Thomas Kunnumpurath
 */
@EnableBinding(JoinRequestBinding.class)
public class JoinProcessor {

    @Autowired
    // Since the output destination will be dynamic, we will need a custom resolver
    private BinderAwareChannelResolver resolver;

    @Autowired
    // An internal construct in order to handle game sessions
    private IGameEngine gameEngine;

    // We define an INPUT to receive data from and dynamically specify the reply to
    // destination depending on the header and state of the game engine

    private static final <T> Message<T> message(T val) {
        return MessageBuilder.withPayload(val).build();
    }

    /*
     * Custom Processor Binding Interface to allow for multiple outputs
     */
    public interface JoinRequestBinding {
        String INPUT = "join_request";

        @Input
        SubscribableChannel join_request();
    }
}