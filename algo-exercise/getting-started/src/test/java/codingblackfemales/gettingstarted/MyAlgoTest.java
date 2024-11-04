package codingblackfemales.gettingstarted;

import codingblackfemales.action.Action;
import codingblackfemales.action.NoAction;
import codingblackfemales.algo.AlgoLogic;
import codingblackfemales.service.MarketDataService;
import codingblackfemales.service.OrderService;
import codingblackfemales.sotw.marketdata.BidLevel;
import org.agrona.concurrent.UnsafeBuffer;
import codingblackfemales.sotw.SimpleAlgoState;
import codingblackfemales.sotw.SimpleAlgoStateImpl;
import messages.order.Side;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import codingblackfemales.container.RunTrigger;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;



/**
 * This test is designed to check your algo behavior in isolation of the order book.
 *
 * You can tick in market data messages by creating new versions of createTick() (ex. createTick2, createTickMore etc..)
 *
 * You should then add behaviour to your algo to respond to that market data by creating or cancelling child orders.
 *
 * When you are comfortable you algo does what you expect, then you can move on to creating the MyAlgoBackTest.
 *
 */
public class MyAlgoTest extends AbstractAlgoTest {
    private SimpleAlgoState algoState;
    private MarketDataService marketDataService;
    private OrderService orderService;


    @Override
    public AlgoLogic createAlgoLogic() {
        //this adds your algo logic to the container classes
        return new MyAlgoLogic();
    }

    @Before
    public void setUp() {
        marketDataService = new MarketDataService(new RunTrigger());
        orderService = new OrderService(new RunTrigger());
        algoState = new SimpleAlgoStateImpl(marketDataService, orderService);
        System.out.println(" MyAlgoTest");
    }


@Test
        public void testDispatchThroughSequencer () throws Exception {

            //create a sample market data tick....
            send(createTick());
            SimpleAlgoState state = container.getState();
            Action action = createAlgoLogic().evaluate(state);
            assertEquals("Do not action if spread is below threshold", NoAction.NoAction, action);
        }
    }

