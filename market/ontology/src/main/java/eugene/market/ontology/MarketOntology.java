package eugene.market.ontology;

import eugene.market.ontology.field.AvgPx;
import eugene.market.ontology.field.ClOrdID;
import eugene.market.ontology.field.CumQty;
import eugene.market.ontology.field.ExecType;
import eugene.market.ontology.field.LeavesQty;
import eugene.market.ontology.field.OrdStatus;
import eugene.market.ontology.field.OrdType;
import eugene.market.ontology.field.OrderID;
import eugene.market.ontology.field.OrderQty;
import eugene.market.ontology.field.Price;
import eugene.market.ontology.field.Side;
import eugene.market.ontology.field.Symbol;
import eugene.market.ontology.message.ExecutionReport;
import eugene.market.ontology.message.NewOrderSingle;
import jade.content.onto.BeanOntology;
import jade.content.onto.Ontology;
import jade.content.onto.OntologyException;

/**
 * Defines the Market Ontology used to send messages between Agents and the Market.
 *
 * @author Jakub D Kozlowski
 * @since 0.2
 */
public final class MarketOntology extends BeanOntology {

    /**
     * Singleton instance of {@link MarketOntology}.
     */
    private static final MarketOntology INSTANCE = new MarketOntology();

    /**
     * Name of this ontology.
     */
    public static final String NAME = "market-ontology";

    /**
     * Private constructor.
     *
     */
    private MarketOntology() {
        super(NAME);

        try {
            add(AvgPx.class, false);
            add(ClOrdID.class, false);
            add(CumQty.class, false);
            add(ExecType.class, false);
            add(LeavesQty.class, false);
            add(OrderID.class, false);
            add(OrderQty.class, false);
            add(OrdStatus.class, false);
            add(OrdType.class, false);
            add(Price.class, false);
            add(Side.class, false);
            add(Symbol.class, false);

            add(NewOrderSingle.class, false);
            add(ExecutionReport.class, false);
        }
        catch (OntologyException e) {
            e.printStackTrace();
        }
    }

    /**
     * Returns the singleton instance of this {@link MarketOntology}.
     *
     * @return Singleton instance of this {@link MarketOntology}.
     */
    public static Ontology getInstance() {
        return INSTANCE;
    }
}