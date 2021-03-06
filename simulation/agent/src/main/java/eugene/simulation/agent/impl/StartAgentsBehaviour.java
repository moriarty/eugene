/**
 * Copyright 2012 Jakub Dominik Kozlowski <mail@jakub-kozlowski.com>
 * Distributed under the The MIT License.
 * (See accompanying file LICENSE.txt)
 */
package eugene.simulation.agent.impl;

import com.google.common.base.Stopwatch;
import eugene.simulation.agent.Simulation;
import eugene.simulation.agent.Symbol;
import eugene.utils.annotation.Nullable;
import eugene.utils.behaviour.BehaviourResult;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.OneShotBehaviour;
import jade.wrapper.AgentContainer;
import jade.wrapper.AgentController;
import jade.wrapper.StaleProxyException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.Set;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Starts the {@link Agent}s.
 *
 * @author Jakub D Kozlowski
 * @since 0.6
 */
public class StartAgentsBehaviour extends OneShotBehaviour {

    private static final String ERROR_MSG = "Failed to Start Agents";

    private final Logger LOG = LoggerFactory.getLogger(StartAgentsBehaviour.class);

    private final BehaviourResult<AID> marketAgent;

    private final Symbol symbol;

    private final Set<Agent> agents;

    private final BehaviourResult<Set<String>> result = new BehaviourResult<Set<String>>();

    public StartAgentsBehaviour(final BehaviourResult<AID> marketAgent, final Symbol symbol, final Set<Agent> agents) {
        checkNotNull(marketAgent);
        checkNotNull(symbol);
        checkNotNull(agents);
        checkArgument(!agents.isEmpty());
        this.marketAgent = marketAgent;
        this.symbol = symbol;
        this.agents = agents;
    }

    @Override
    public void action() {
        final Set<String> started = new HashSet<String>();
        final Stopwatch stopwatch = new Stopwatch().start();
        try {
            int i = 0;
            for (final Agent a : agents) {
                final Simulation simulation = new SimulationImpl(myAgent.getAID(), marketAgent.getObject(), symbol);
                a.setArguments(new Simulation[]{simulation});
                final AgentContainer container = myAgent.getContainerController();
                final AgentController controller = container.acceptNewAgent(a.getClass().getSimpleName() + i++, a);
                controller.start();
                started.add(controller.getName());
            }

            LOG.info("Starting agents took {}", stopwatch.stop());
            result.success(started);
        }
        catch (StaleProxyException e) {
            LOG.error(ERROR_MSG, e);
            result.fail(started);
        }
    }

    /**
     * Returns {@link AID#ISGUID}s of started agents.
     *
     * @return {@link AID#ISGUID}s of started agents.
     */
    @Nullable
    public Set<String> getStarted() {
        return result.getObject();
    }

    /**
     * Gets the {@link BehaviourResult} used by this {@link StartAgentsBehaviour}.
     *
     * @return {@link BehaviourResult} used by this {@link StartAgentsBehaviour}.
     */
    public BehaviourResult<Set<String>> getResult() {
        return result;
    }

    @Override
    public int onEnd() {
        return result.getResult();
    }
}
