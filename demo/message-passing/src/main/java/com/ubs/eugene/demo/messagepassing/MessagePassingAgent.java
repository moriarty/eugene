package com.ubs.eugene.demo.messagepassing;

import com.ubs.eugene.demo.messagepassing.internal.MessagePassingBehaviour;
import jade.core.Agent;

/**
 * Implements an agent that waits for a message from a specified agent,
 * then passes that message to another agent and finishes.
 *
 * Agent expects the following arguments:
 * <ol>
 *     <li>AID of Agent to receive the message from.</li>
 *     <li>AIR of Agent to pass message to.</li>
 * </ol>
 *
 * @author Jakub D Kozlowski
 */
public class MessagePassingAgent extends Agent {

    /**
     * Collects the arguments and starts
     * {@link com.ubs.eugene.demo.messagepassing.internal.MessagePassingBehaviour}.
     *
     */
    @Override
    protected void setup() {
        assert getArguments().length == 2;
        assert getArguments()[0] instanceof String;
        assert getArguments()[1] instanceof String;

        final String from = (String) getArguments()[0];
        final String to = (String) getArguments()[1];

        addBehaviour(new MessagePassingBehaviour(this, from, to));
    }
}
