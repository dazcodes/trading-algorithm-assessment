package codingblackfemales.gettingstarted;

import codingblackfemales.action.Action;
import codingblackfemales.action.CancelChildOrder;
import codingblackfemales.action.CreateChildOrder;
import codingblackfemales.action.NoAction;
import codingblackfemales.algo.AlgoLogic;
import codingblackfemales.sotw.ChildOrder;
import codingblackfemales.sotw.SimpleAlgoState;
import codingblackfemales.sotw.marketdata.AskLevel;
import codingblackfemales.sotw.marketdata.BidLevel;
import codingblackfemales.util.Util;
import messages.order.Side;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MyAlgoLogic implements AlgoLogic {

    private static final Logger logger = LoggerFactory.getLogger(MyAlgoLogic.class);

    // Thresholds for buy and sell actions based on percentage deviations
    private static final double buyThreshold = -1.5; // Buy when bid is 1.5% lower than recent trades
    private static final double sellThreshold = 1.5; // Sell if best ask is 1.5% higher than recent trades
    private static final double spreadThreshold = -2.5; // Minimum spread threshold as a percentage ideally wanted 0.5%


    @Override
    public Action evaluate(SimpleAlgoState state) {

        var orderBookAsString = Util.orderBookToString(state);

//shows current state of order book and current state of active orders
        logger.info("[MYALGO] The state of the order book is:\n" + orderBookAsString);
        logger.info("Active Orders:" + state.getActiveChildOrders().toString());

        var totalOrderCount = state.getChildOrders().size();

        //make sure we have an exit condition...
        if (totalOrderCount > 10) {
            return NoAction.NoAction;
        }

        final BidLevel bidlevel = state.getBidAt(0);
        final long bestBidPrice = bidlevel.price;
        final long bidQuantity = bidlevel.quantity;



        final AskLevel asklevel = state.getAskAt(0);
        final long bestAskPrice = asklevel.price;
        final long askQuantity = asklevel.quantity;




        //calculate the spread
        final long spread = bestAskPrice - bestBidPrice;
        logger.info("[MYALGO] Spread between ask and bid " + spread);

        //calculate the midPrice
        final long midPrice =(bestBidPrice + bestBidPrice)/2;
        logger.info("[MYALGO] Mid-price calculated:" + midPrice);

        //Calculate the spread as a percentage of the mid-price
        final double spreadPercentage = (double) spread / midPrice * 100;
        logger.info("[MYALGO] Spread percentage: " + spreadPercentage + "%");


        // not to create or cancel orders because spread is small
        if (spreadPercentage < spreadThreshold) {
            logger.info("[MYALGO] The spread is small (" + spread + "), No Action");
            return NoAction.NoAction;

        }

        //Count the buy and sell orders

        long buyOrdersCount = state.getChildOrders().stream().filter(ChildOrder -> ChildOrder.getSide() == Side.BUY).count();
        long sellOrdersCount = state.getChildOrders().stream().filter(ChildOrder -> ChildOrder.getSide() == Side.SELL).count();

//count the total filled quantity
//        logger.info("[MYALGO] Total filled buy quantity: " + totalFilledBuyQuantity);
//        logger.info("[MYALGO] Total filled sell quantity: " + totalFilledSellQuantity);

        //create new buy orders if below best bid is threshold below threshold
        if (buyOrdersCount < 5 && bestBidPrice < midPrice * (1 + buyThreshold /100.0)) {
            return createBuyOrder(state, buyOrdersCount, bidQuantity, bestBidPrice);
        }

        //create new sell orders if  price above threshold percentage
        if (sellOrdersCount < 5 && bestAskPrice > midPrice * (1 + sellThreshold / 100.0)) {
            return createSellOrder(state, sellOrdersCount, askQuantity, bestAskPrice);
        }

        //cancel orders that don't match the best price

        return cancelOrders(state, bestBidPrice, bestAskPrice);
    }

    //create method to create new bid order
    public Action createBuyOrder(SimpleAlgoState state, long buyOrdersCount, long bidQuantity, long bestBidPrice) {
        logger.info("[MYALGO] Creating new buy order: " + state.getChildOrders().size() + " orders and add to new buy order " + bidQuantity + " @ " + bestBidPrice);
        logger.info(state.getActiveChildOrders().toString());
        logger.info("Buy order count is:" + buyOrdersCount);
        //creates a new child order
        return new CreateChildOrder(Side.BUY, bidQuantity, bestBidPrice);

    }

    // create a method to create a new sell order
    public Action createSellOrder(SimpleAlgoState state, long sellOrdersCount, long askQuantity, long bestAskPrice) {
        logger.info("[MYALGO] Creating new sell order:" + state.getChildOrders().size() + " orders and add to new sell order " + askQuantity + " @ " + bestAskPrice);
        logger.info("Sell order count is:" + sellOrdersCount);
        //creates a new child order
        return new CreateChildOrder(Side.SELL, askQuantity, bestAskPrice);
    }

    //create a method to cancel orders that don't match best price or fall below thresholds
    public Action cancelOrders(SimpleAlgoState state, long bestBidPrice, long bestAskPrice){
        for (ChildOrder order : state.getActiveChildOrders()){
            logger.info("Order ID: #" + order.getOrderId() + ", Side: " + order.getSide() + ", Price: " + order.getPrice() + ", Quantity: " + order.getQuantity());

            // Cancel buy orders not matching the best price or below the buy threshold
            if (order.getSide() == Side.BUY && order.getPrice() < bestBidPrice) {
                logger.info("[MYALGO] Cancel BUY order #" + order.getOrderId() + " with price " + order.getPrice());
                return new CancelChildOrder(order);
            }

            // Cancel sell orders not matching the best price or below the sell threshold
            if (order.getSide() == Side.SELL && order.getPrice() > bestAskPrice ) {
                logger.info("[MYALGO] Cancel SELL order #" + order.getOrderId() + " with price " + order.getPrice());
                return new CancelChildOrder(order);
            }
        }

        return NoAction.NoAction;
    }
}






