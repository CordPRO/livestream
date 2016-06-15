import eduze.livestream.AudioCapturer;
import eduze.livestream.ScreenCapturer;
import eduze.livestream.exchange.Connector;
import eduze.livestream.exchange.client.FrameBuffer;
import eduze.livestream.exchange.server.FrameBufferImpl;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import javax.xml.ws.Endpoint;
import java.util.concurrent.CountDownLatch;

import static org.testng.Assert.*;

/**
 * Created by Admin on 6/15/2016.
 */
public class AudioCapturerTest {
    private int bufferSize = 5;
    Endpoint endpoint = null;

    boolean continueLoop = false;
    boolean done = false;
    CountDownLatch lock = null;

    FrameBufferImpl testFrameBufferServer = null;
    FrameBuffer frameBufferClient = null;

    AudioCapturer audioCapturer = null;

    @BeforeMethod
    public void setUp() throws Exception {
        testFrameBufferServer = new FrameBufferImpl(bufferSize);
        endpoint = Endpoint.publish("http://localhost:1024/testBuffer", testFrameBufferServer);
        frameBufferClient = Connector.obtainFrameBuffer("http://localhost:1024/testBuffer");

        audioCapturer = new AudioCapturer(frameBufferClient);
    }

    @AfterMethod
    public void tearDown() throws Exception {
        audioCapturer.stopCapture();
        endpoint.stop();
    }

    @Test
    public void testIsCapturing() throws Exception {
        audioCapturer.startCapture();
        Assert.assertTrue(audioCapturer.isCapturing());
        audioCapturer.stopCapture();
        Assert.assertFalse(audioCapturer.isCapturing());
    }

    @Test
    public void testStartCapture() throws Exception {

    }

    @Test
    public void testStopCapture() throws Exception {

    }

    @Test
    public void testSetFormat() throws Exception {

    }

}