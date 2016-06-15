import eduze.livestream.ScreenCapturer;
import eduze.livestream.ScreenReceiver;
import eduze.livestream.exchange.Connector;
import eduze.livestream.exchange.server.FrameBuffer;
import eduze.livestream.exchange.server.FrameBufferImpl;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import javax.xml.ws.Endpoint;
import java.awt.image.BufferedImage;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * Created by Admin on 6/15/2016.
 */
public class ScreenReceiverTest {
    FrameBuffer serverBuffer = null;
    eduze.livestream.exchange.client.FrameBuffer clientBuffer = null;

    ScreenCapturer screenCapturer = null;
    Endpoint endpoint = null;

    ScreenReceiver screenReceiver = null;
    CountDownLatch countDownLatch = null;
    @BeforeMethod
    public void setUp() throws Exception {
        serverBuffer = new FrameBufferImpl(5);
        endpoint = Endpoint.publish("http://localhost:8000/testScreen", serverBuffer);

        clientBuffer = Connector.obtainFrameBuffer("http://localhost:8000/testScreen");

        screenCapturer = new ScreenCapturer(clientBuffer,1000);
        screenCapturer.startCapture();

        screenReceiver = new ScreenReceiver(clientBuffer,1000);
    }

    @AfterMethod
    public void tearDown() throws Exception {
        screenCapturer.stopCapture();
        screenReceiver.stopReceiving();
        endpoint.stop();
    }

    @Test
    public void testIsReceiving() throws Exception {
        screenReceiver.startReceiving();
        Assert.assertTrue(screenReceiver.isReceiving());
        screenReceiver.stopReceiving();
        Assert.assertFalse(screenReceiver.isReceiving());
    }

    private boolean done =false;

    @Test
    public void testStartReceiving() throws Exception {
        done = false;
        countDownLatch = new CountDownLatch(1);
        screenReceiver.addScreenReceivedListener(new ScreenReceiver.ScreenReceivedListener() {
            @Override
            public void ScreenReceived(byte[] screen, BufferedImage screenImage) {
                done = true;
                countDownLatch.countDown();
            }
        });
        screenReceiver.startReceiving();
        countDownLatch.await(5000, TimeUnit.MILLISECONDS);
        if(!done)
            Assert.fail("No screen received within specified timelimit");
    }



}