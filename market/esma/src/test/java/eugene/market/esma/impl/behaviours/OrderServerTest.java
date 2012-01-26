package eugene.market.esma.impl.behaviours;

import eugene.market.esma.MarketAgent;
import eugene.market.esma.impl.Repository;
import eugene.market.esma.impl.execution.ExecutionEngine;
import org.testng.annotations.Test;

import static eugene.market.ontology.Defaults.defaultSymbol;
import static org.mockito.Mockito.mock;

/**
 * Tests {@link OrderServer}.
 *
 * @author Jakub D Kozlowski
 * @since 0.3
 */
public class OrderServerTest {

    @Test(expectedExceptions = NullPointerException.class)
    public void testConstructorNullAgent() {
        new OrderServer(null, defaultSymbol);
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void testConstructorNullSymbol() {
        new OrderServer(mock(MarketAgent.class), null);
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void testConstructorNullExecutionEngine() {
        new OrderServer(mock(MarketAgent.class), null, mock(Repository.class), defaultSymbol);
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void testConstructorNullOrderRepository() {
        new OrderServer(mock(MarketAgent.class), mock(ExecutionEngine.class), null, defaultSymbol);
    }
}
