package com.solace.battleship.flows;

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
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.cloud.stream.binding.BinderAwareChannelResolver;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.test.context.junit4.SpringRunner;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

/**
 * BoardSetRequestProcessorTest
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = JoinProcessor.class)
public class BoardSetRequestProcessorTest {
    @Autowired
    private JoinProcessor.JoinRequestBinding processor;

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

    // TODO

}