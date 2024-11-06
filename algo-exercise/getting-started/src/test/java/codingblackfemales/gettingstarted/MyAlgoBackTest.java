package codingblackfemales.gettingstarted;

;
import codingblackfemales.algo.AlgoLogic;

import messages.order.Side;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;


public class MyAlgoBackTest extends AbstractAlgoBackTest {

    @Override
    public AlgoLogic createAlgoLogic() {
        return new MyAlgoLogic();
    }


    @Before
    public void setUp() {

        System.out.println(" MyAlgoBackTest");
    }

    @Test

    public void testOrderCount() throws Exception{
        send(createTick());
        send(createTick2());

        var state = container.getState();

        //Check buy order is created
        long createBuyOrder = state.getChildOrders().stream().filter(childOrder -> childOrder.getSide()==Side.BUY).count();
        assertEquals("At least one BUY order should be created", 5, createBuyOrder);

        //check sell order is created
        long createSellOrder = state.getChildOrders().stream().filter(childOrder -> childOrder.getSide()==Side.SELL).count();
        assertEquals("At least one SELL order should be created", 5, createSellOrder);




    }

    @Test
    public void testExampleBackTest() throws Exception {
        //create a sample market data tick....
        send(createTick());

        //when: market data moves towards us
        send(createTick2());

        //then: get the state
        var state = container.getState();



    }

    @Test public  void testForOrderManagement () throws Exception{
        send(createTick());
        send(createTick2());
        send(createTickWithIncreasingVolume());
        send(createTickWithHighThreshold());

        //check algo has a max order limit of 10
        assertTrue("There should be at least 10 child orders", container.getState().getChildOrders().size() <=10 );
    }




    @Test
    public void testCreateOrderWithHighThreshold() throws Exception {
        // Arrange: Send a tick with a high spread
        send(createTickWithHighThreshold());
    }


}
