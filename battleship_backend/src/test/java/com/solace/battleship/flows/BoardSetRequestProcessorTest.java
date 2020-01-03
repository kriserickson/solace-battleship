package com.solace.battleship.flows;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;

import com.solace.battleship.engine.IGameEngine;

import org.junit.Before;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.cloud.stream.binding.BinderAwareChannelResolver;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.test.context.junit4.SpringRunner;

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
    public void setup() {
        MessageChannel mockChannel = Mockito.mock(MessageChannel.class);
        Mockito.when(mockChannel.send(any(Message.class))).thenReturn(true);

        Mockito.when(resolver.resolveDestination(anyString())).thenReturn(mockChannel);
    }

    // TODO

}