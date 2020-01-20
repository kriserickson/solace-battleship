package com.solace.battleship.flows;

import com.solace.battleship.events.PlayerJoined;
import com.solace.battleship.flows.JoinProcessor.JoinRequestBinding;

import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.Input;
import org.springframework.messaging.SubscribableChannel;
import org.springframework.messaging.handler.annotation.Header;

/**
 * This Spring Cloud Stream processor handles join-requests for the Battleship
 * Game
 * 
 * @author Thomas Kunnumpurath
 */
@EnableBinding(JoinRequestBinding.class)
public class JoinProcessor extends AbstractRequestProcessor<PlayerJoined> {

    // We define an INPUT to receive data from and dynamically specify the reply to
    // destination depending on the header and state of the game enginer
    public void handle(PlayerJoined joinRequest, @Header("reply-to") String replyTo) {

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