package eugene.market.esma.execution.book;

import eugene.market.esma.enums.Side;
import org.testng.annotations.Test;

import static eugene.market.esma.Defaults.defaultOrdQty;
import static eugene.market.esma.Defaults.defaultPrice;
import static eugene.market.esma.execution.MockOrders.buy;
import static eugene.market.esma.execution.MockOrders.order;
import static eugene.market.esma.execution.MockOrders.orderQty;
import static eugene.market.esma.execution.MockOrders.sell;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalToIgnoringCase;
import static org.hamcrest.Matchers.hasItemInArray;
import static org.hamcrest.Matchers.hasToString;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.isIn;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.Matchers.sameInstance;

/**
 * Tests {@link DefaultOrderBook}.
 *
 * @author Jakub D Kozlowski
 * @since 0.2
 */
public class DefaultOrderBookTest {

    @Test
    public void testNewBookEmpty() {
        final OrderBook orderBook = new DefaultOrderBook();

        assertThat(orderBook.isEmpty(Side.SELL), is(true));
        assertThat(orderBook.size(Side.SELL), is(0));
        assertThat(orderBook.peek(Side.SELL), nullValue());

        assertThat(orderBook.isEmpty(Side.BUY), is(true));
        assertThat(orderBook.size(Side.BUY), is(0));
        assertThat(orderBook.peek(Side.BUY), nullValue());
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void testInsertNullOrder() {
        final OrderBook orderBook = new DefaultOrderBook();
        orderBook.insertOrder(null);
    }

    @Test
    public void testInsertLimitBuy() {
        final OrderBook orderBook = new DefaultOrderBook();
        final Order buy = order(buy());
        final OrderStatus orderStatus = orderBook.insertOrder(buy);

        assertThat(orderBook.isEmpty(Side.SELL), is(true));
        assertThat(orderBook.size(Side.SELL), is(0));
        assertThat(orderBook.peek(Side.SELL), nullValue());
        assertThat(orderBook.isEmpty(Side.BUY), is(false));

        assertThat(orderBook.size(Side.BUY), is(1));
        assertThat(orderBook.peek(Side.BUY), is(buy));
        assertThat(buy, isIn(orderBook.getBuyOrders()));
        assertThat(orderStatus, sameInstance(orderBook.getExecutionReport(buy)));
        assertThat(orderStatus.isEmpty(), is(true));
    }

    @Test
    public void testInsertLimitSell() {
        final OrderBook orderBook = new DefaultOrderBook();
        final Order sell = order(sell());
        final OrderStatus orderStatus = orderBook.insertOrder(sell);

        assertThat(orderBook.isEmpty(Side.BUY), is(true));
        assertThat(orderBook.size(Side.BUY), is(0));
        assertThat(orderBook.peek(Side.BUY), nullValue());

        assertThat(orderBook.isEmpty(Side.SELL), is(false));
        assertThat(orderBook.size(Side.SELL), is(1));
        assertThat(orderBook.peek(Side.SELL), is(sell));
        assertThat(sell, isIn(orderBook.getSellOrders()));
        assertThat(orderStatus, sameInstance(orderBook.getExecutionReport(sell)));
        assertThat(orderStatus.isEmpty(), is(true));
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void testExecuteNullPrice() {
        final OrderBook orderBook = new DefaultOrderBook();
        orderBook.execute(null);
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testExecuteNoPrice() {
        final OrderBook orderBook = new DefaultOrderBook();
        orderBook.execute(Order.NO_PRICE);
    }

    @Test(expectedExceptions = IllegalStateException.class)
    public void testExecuteEmptySellSide() {
        final OrderBook orderBook = new DefaultOrderBook();
        final Order buy = order(buy());
        orderBook.insertOrder(buy);

        orderBook.execute(defaultPrice);
    }

    @Test(expectedExceptions = IllegalStateException.class)
    public void testExecuteEmptyBuySide() {
        final OrderBook orderBook = new DefaultOrderBook();
        final Order sell = order(sell());
        orderBook.insertOrder(sell);

        orderBook.execute(defaultPrice);
    }

    @Test
    public void testExecute() {
        final OrderBook orderBook = new DefaultOrderBook();
        final Order buy = order(orderQty(buy(), defaultOrdQty));
        final Order sell = order(orderQty(sell(), defaultOrdQty));
        orderBook.insertOrder(buy);
        orderBook.insertOrder(sell);

        final TradeReport actual = orderBook.execute(defaultPrice);
        assertThat(actual.getPrice(), is(defaultPrice));
        assertThat(actual.getQuantity(), is(defaultOrdQty));
        assertThat(orderBook.isEmpty(Side.BUY), is(true));
        assertThat(orderBook.isEmpty(Side.SELL), is(true));
        assertThat(orderBook.getExecutionReport(buy), nullValue());
        assertThat(orderBook.getExecutionReport(sell), nullValue());
    }
    
    @Test(expectedExceptions = NullPointerException.class)
    public void testCancelNullOrder() {
        final OrderBook orderBook = new DefaultOrderBook();
        orderBook.cancel(null);
    }
    
    @Test
    public void testCancelOrderNotInOrderBook() {
        final OrderBook orderBook = new DefaultOrderBook();
        final Order order = order(buy());
        final OrderStatus orderStatus = orderBook.cancel(order);
        assertThat(orderStatus, nullValue());
    }
    
    @Test
    public void testCancelOrderInOrderBook() {
        final OrderBook orderBook = new DefaultOrderBook();
        final Order order = order(buy());
        final OrderStatus expected = orderBook.insertOrder(order);
        final OrderStatus actual = orderBook.cancel(order);
        
        assertThat(actual, sameInstance(expected));
        assertThat(orderBook.getExecutionReport(order), nullValue());
        assertThat(orderBook.getBuyOrders(), not(hasItemInArray(order)));
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void testSizeNullSide() {
        final OrderBook orderBook = new DefaultOrderBook();
        orderBook.size(null);
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void testIsEmptyNullSide() {
        final OrderBook orderBook = new DefaultOrderBook();
        orderBook.isEmpty(null);
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void testPeekNullSide() {
        final OrderBook orderBook = new DefaultOrderBook();
        orderBook.peek(null);
    }

    @Test
    public void testToStringSell() {
        final OrderBook orderBook = new DefaultOrderBook();
        final Order buy = order(buy());
        orderBook.insertOrder(buy);
        final Order sell = order(sell());
        orderBook.insertOrder(sell);

        final StringBuilder expected = new StringBuilder();
        expected.append(sell).append("\n");
        expected.append(DefaultOrderBook.SEPARATOR);
        expected.append(buy).append("\n");
        assertThat(orderBook, hasToString(equalToIgnoringCase(expected.toString())));
    }
}