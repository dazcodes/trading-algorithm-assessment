package codingblackfemales.gettingstarted;


import codingblackfemales.algo.AlgoLogic;
import codingblackfemales.container.Actioner;
import codingblackfemales.container.AlgoContainer;
import codingblackfemales.container.RunTrigger;
import codingblackfemales.orderbook.OrderBook;
import codingblackfemales.orderbook.channel.MarketDataChannel;
import codingblackfemales.orderbook.channel.OrderChannel;
import codingblackfemales.orderbook.consumer.OrderBookInboundOrderConsumer;
import codingblackfemales.sequencer.DefaultSequencer;
import codingblackfemales.sequencer.Sequencer;
import codingblackfemales.sequencer.consumer.LoggingConsumer;
import codingblackfemales.sequencer.net.TestNetwork;
import codingblackfemales.service.MarketDataService;
import codingblackfemales.service.OrderService;
import messages.marketdata.*;
import org.agrona.DirectBuffer;
import org.agrona.concurrent.UnsafeBuffer;
import org.junit.Test;

import java.nio.ByteBuffer;

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
    private final MessageHeaderEncoder headerEncoder = new MessageHeaderEncoder();
    private final BookUpdateEncoder encoder = new BookUpdateEncoder();


    @Override
    public AlgoLogic createAlgoLogic() {
        //this adds your algo logic to the container classes


        return new MyAlgoLogic();
    }


    private UnsafeBuffer createTick(long minBidPrice, long maxAskPrice){
        final ByteBuffer byteBuffer = ByteBuffer.allocateDirect(1024);
        final UnsafeBuffer directBuffer = new UnsafeBuffer(byteBuffer);

        //write the encoded output to the direct buffer
        encoder.wrapAndApplyHeader(directBuffer, 0, headerEncoder);

        //set the fields to desired values
        encoder.venue(Venue.XLON);
        encoder.instrumentId(123L);
        encoder.source(Source.STREAM);

        //different ranges for minBidPrice
        encoder.bidBookCount(3)
                .next().price(minBidPrice).size(100L)
                .next().price(minBidPrice - 1).size(200L)
                .next().price(minBidPrice - 2).size(300L);

//different ranges for minAskPrice
        encoder.askBookCount(3)
                .next().price(maxAskPrice).size(150L)
                .next().price(maxAskPrice + 1).size(250L)
                .next().price(maxAskPrice + 2).size(350L);


//set instrument status
        encoder.instrumentStatus(InstrumentStatus.CONTINUOUS);
        return directBuffer;
    }


    @Test

    public void testDispatchThroughSequencer() throws Exception {
        //create a sample market data tick....
//        send(createTick());


        // Tick 1: Basic market data update
        send(createTick(98L, 100L));
        assertEquals(3, container.getState().getChildOrders().size());

        // Tick 2: An increase in the bid prices
        send(createTick(99L, 101L));
        assertEquals(3, container.getState().getChildOrders().size());

        // Tick 3: A decrease in the ask prices
        send(createTick(97L, 99L));
        assertEquals(3, container.getState().getChildOrders().size());

        // Tick 4: The prices are stable
        send(createTick(98L, 100L));
        assertEquals(3, container.getState().getChildOrders().size());

        // Tick 5: Significant price change
        send(createTick(101L, 103L));
        assertEquals(3, container.getState().getChildOrders().size());

        //simple assert to check we had 3 orders created
        assertEquals(container.getState().getChildOrders().size(), 3);

    }




}




