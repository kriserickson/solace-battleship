package com.solace.battleship.flows;

import com.solace.battleship.events.BoardSetRequest;
import com.solace.battleship.events.BoardSetResult;
import com.solace.battleship.flows.BoardSetRequestProcessor.BoardSetRequestBinding;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.Input;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.messaging.SubscribableChannel;
import org.springframework.messaging.handler.annotation.Header;

/**
 * This Spring Cloud Stream processor handles board set requests for the
 * Battleship Game
 *
 * @author Andrew Roberts
 */
@EnableBinding(BoardSetRequestBinding.class)
public class BoardSetRequestProcessor extends AbstractRequestProcessor<BoardSetRequest> {

  // We define an INPUT to receive data from and dynamically specify the reply to
  // destination depending on the header and state of the game enginer

  /*
   * Custom Processor Binding Interface to allow for multiple outputs
   */
  public interface BoardSetRequestBinding {
    String INPUT = "board_set_request";

    @Input
    SubscribableChannel board_set_request();
  }
}