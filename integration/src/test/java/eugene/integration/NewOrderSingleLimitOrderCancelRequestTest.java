package eugene.integration;

import com.google.common.collect.Sets;
import eugene.market.book.Order;
import eugene.market.client.Application;
import eugene.market.client.ApplicationAdapter;
import eugene.market.client.Session;
import eugene.market.ontology.field.OrderQty;
import eugene.market.ontology.field.Price;
import eugene.market.ontology.field.enums.ExecType;
import eugene.market.ontology.field.enums.OrdStatus;
import eugene.market.ontology.field.enums.OrdType;
import eugene.market.ontology.field.enums.Side;
import eugene.market.ontology.message.ExecutionReport;
import eugene.market.ontology.message.NewOrderSingle;
import eugene.market.ontology.message.OrderCancelRequest;
import eugene.market.ontology.message.data.AddOrder;
import eugene.market.ontology.message.data.DeleteOrder;
import eugene.simulation.agent.Simulation;
import eugene.simulation.agent.SimulationAgent;
import eugene.simulation.ontology.Start;
import jade.core.Agent;
import jade.wrapper.AgentContainer;
import jade.wrapper.AgentController;
import jade.wrapper.ControllerException;
import org.mockito.ArgumentCaptor;
import org.mockito.InOrder;
import org.testng.annotations.Test;

import java.util.Collections;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static eugene.integration.CountingApplication.TIMEOUT;
import static eugene.market.client.Applications.proxy;
import static eugene.market.client.Sessions.initiate;
import static eugene.market.esma.AbstractMarketAgentTest.getNakedContainer;
import static eugene.market.ontology.Defaults.defaultOrdQty;
import static eugene.market.ontology.Defaults.defaultPrice;
import static eugene.market.ontology.Defaults.defaultSymbol;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;

/**
 * Creates a {@link OrdType#LIMIT} order and cancels it.
 *
 * @author Jakub D Kozlowski
 * @since 0.6
 */
public class NewOrderSingleLimitOrderCancelRequestTest {
    
    @Test
    public void newOrderSingleLimitOrderCancelRequest() throws ControllerException, InterruptedException {

        final AgentContainer container = getNakedContainer();

        final CountDownLatch latch = new CountDownLatch(4);
        final Application application = mock(Application.class);
        final Application proxy = proxy(
                application,
                new ApplicationAdapter() {
                    @Override
                    public void onStart(Start start, Agent agent, Session session) {
                        final NewOrderSingle newOrderSingle = new NewOrderSingle();
                        newOrderSingle.setOrdType(
                                OrdType.LIMIT.field());
                        newOrderSingle.setOrderQty(
                                new OrderQty(defaultOrdQty));
                        newOrderSingle.setPrice(new Price(
                                defaultPrice));
                        newOrderSingle.setSide(
                                Side.BUY.field());

                        session.send(newOrderSingle);

                        final OrderCancelRequest orderCancel = new OrderCancelRequest();
                        orderCancel.setClOrdID(newOrderSingle.getClOrdID());
                        orderCancel.setOrderQty(newOrderSingle.getOrderQty());
                        orderCancel.setSide(newOrderSingle.getSide());

                        session.send(orderCancel);
                    }
                },
                new CountingApplication(latch)
        );

        final Set<Agent> agents = Sets.newHashSet();
        agents.add(new Agent() {
            @Override
            public void setup() {
                final Simulation simulation = (Simulation) getArguments()[0];
                addBehaviour(initiate(proxy, simulation));
            }
        });

        final SimulationAgent simulation = new SimulationAgent(defaultSymbol, TIMEOUT, Collections.EMPTY_SET, agents);
        final AgentController simulationController = container.acceptNewAgent(SimulationAgent.NAME, simulation);
        simulationController.start();

        latch.await(TIMEOUT, TimeUnit.MILLISECONDS);

        final InOrder inOrder = inOrder(application);

        final ArgumentCaptor<NewOrderSingle> newOrderSingle = ArgumentCaptor.forClass(NewOrderSingle.class);
        inOrder.verify(application).fromApp(newOrderSingle.capture(), any(Session.class));
        assertThat(newOrderSingle.getValue().getSymbol().getValue(), is(defaultSymbol));
        assertThat(newOrderSingle.getValue().getOrderQty().getValue(), is(defaultOrdQty));
        assertThat(newOrderSingle.getValue().getOrdType(), is(OrdType.LIMIT.field()));
        assertThat(newOrderSingle.getValue().getPrice().getValue(), is(defaultPrice));
        assertThat(newOrderSingle.getValue().getSide(), is(Side.BUY.field()));

        final ArgumentCaptor<ExecutionReport> newExecutionReport = ArgumentCaptor.forClass(ExecutionReport.class);
        inOrder.verify(application).toApp(newExecutionReport.capture(), any(Session.class));
        assertThat(newExecutionReport.getValue().getAvgPx().getValue(), is(Order.NO_PRICE));
        assertThat(newExecutionReport.getValue().getClOrdID().getValue(), is(
                newOrderSingle.getValue().getClOrdID().getValue()));
        assertThat(newExecutionReport.getValue().getCumQty().getValue(), is(Order.NO_QTY));
        assertThat(newExecutionReport.getValue().getExecType(), is(ExecType.NEW.field()));
        assertThat(newExecutionReport.getValue().getLeavesQty().getValue(), is(defaultOrdQty));
        assertThat(newExecutionReport.getValue().getOrdStatus(), is(OrdStatus.NEW.field()));
        assertThat(newExecutionReport.getValue().getSide(), is(Side.BUY.field()));
        assertThat(newExecutionReport.getValue().getSymbol().getValue(), is(defaultSymbol));

        final ArgumentCaptor<AddOrder> addOrder = ArgumentCaptor.forClass(AddOrder.class);
        inOrder.verify(application).toApp(addOrder.capture(), any(Session.class));
        assertThat(addOrder.getValue().getOrderID().getValue(), is(
                newExecutionReport.getValue().getOrderID().getValue()));
        assertThat(addOrder.getValue().getPrice().getValue(), is(defaultPrice));
        assertThat(addOrder.getValue().getOrderQty().getValue(), is(defaultOrdQty));
        assertThat(addOrder.getValue().getSide(), is(Side.BUY.field()));
        assertThat(addOrder.getValue().getSymbol().getValue(), is(defaultSymbol));

        final ArgumentCaptor<ExecutionReport> cancelExecutionReport = ArgumentCaptor.forClass(ExecutionReport.class);
        inOrder.verify(application).toApp(cancelExecutionReport.capture(), any(Session.class));
        assertThat(cancelExecutionReport.getValue().getAvgPx().getValue(), is(Order.NO_PRICE));
        assertThat(cancelExecutionReport.getValue().getClOrdID().getValue(), is(
                newOrderSingle.getValue().getClOrdID().getValue()));
        assertThat(cancelExecutionReport.getValue().getCumQty().getValue(), is(Order.NO_QTY));
        assertThat(cancelExecutionReport.getValue().getExecType(), is(ExecType.CANCELED.field()));
        assertThat(cancelExecutionReport.getValue().getLeavesQty().getValue(), is(defaultOrdQty));
        assertThat(cancelExecutionReport.getValue().getOrdStatus(), is(OrdStatus.CANCELED.field()));
        assertThat(cancelExecutionReport.getValue().getSide(), is(Side.BUY.field()));
        assertThat(cancelExecutionReport.getValue().getSymbol().getValue(), is(defaultSymbol));

        final ArgumentCaptor<DeleteOrder> deleteOrder = ArgumentCaptor.forClass(DeleteOrder.class);
        inOrder.verify(application).toApp(deleteOrder.capture(), any(Session.class));
        assertThat(deleteOrder.getValue().getOrderID().getValue(), is(
                newExecutionReport.getValue().getOrderID().getValue()));

        inOrder.verifyNoMoreInteractions();

        container.kill();
    }
}