import eduze.livestream.AudioCapturer;
import eduze.livestream.AudioReceiver;
import eduze.livestream.ScreenCapturer;
import eduze.livestream.ScreenReceiver;
import eduze.livestream.exchange.Connector;
import eduze.livestream.exchange.server.FrameBuffer;
import eduze.livestream.exchange.server.FrameBufferImpl;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import javax.sound.sampled.AudioFormat;
import javax.xml.ws.Endpoint;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.testng.Assert.*;

/**
 * Created by Admin on 6/15/2016.
 */
public class AudioReceiverTest {
    FrameBuffer serverBuffer = null;
    eduze.livestream.exchange.client.FrameBuffer clientBuffer = null;

    AudioCapturer audioCapturer = null;
    Endpoint endpoint = null;

    AudioReceiver audioReceiver = null;
    CountDownLatch countDownLatch = null;
    @BeforeMethod
    public void setUp() throws Exception {
        serverBuffer = new FrameBufferImpl(5);
        endpoint = Endpoint.publish("http://localhost:8000/testScreen", serverBuffer);

        clientBuffer = Connector.obtainFrameBuffer("http://localhost:8000/testScreen");

        audioCapturer = new AudioCapturer(clientBuffer);
        audioCapturer.startCapture();

        audioReceiver = new AudioReceiver(clientBuffer);
    }

    @AfterMethod
    public void tearDown() throws Exception {
        audioCapturer.stopCapture();
        audioReceiver.stopReceiving();
        endpoint.stop();
        Thread.currentThread().sleep(2000);
    }

    private boolean done = false;
    @Test(singleThreaded = true)
    public void testSetPlayReceived() throws Exception {
        countDownLatch = new CountDownLatch(1);
        done = false;
        audioReceiver.setPlayReceived(true);
        audioReceiver.addAudioReceivedListener(new AudioReceiver.AudioReceivedListener() {
            @Override
            public void AudioReceived(byte[] frame) {
                done = true;
                countDownLatch.countDown();
            }
        });
        audioReceiver.startReceiving();
        countDownLatch.await(5000, TimeUnit.MILLISECONDS);
        if(!done)
            Assert.fail("Didn't receive frame within time limit");
    }

    @Test(singleThreaded = true)
    public void testSetFormat() throws Exception {
        countDownLatch = new CountDownLatch(1);
        done = false;
        audioReceiver.setFormat(new AudioFormat(8000.0f, 16, 1, true, true));
        audioReceiver.addAudioReceivedListener(new AudioReceiver.AudioReceivedListener() {
            @Override
            public void AudioReceived(byte[] frame) {
                done = true;
                countDownLatch.countDown();
            }
        });
        audioReceiver.startReceiving();
        countDownLatch.await(5000, TimeUnit.MILLISECONDS);
        if(!done)
            Assert.fail("Didn't receive frame within time limit");
    }

    @Test
    public void testIsReceiving() throws Exception {
        audioReceiver.startReceiving();
        Assert.assertTrue(audioReceiver.isReceiving());
        audioReceiver.stopReceiving();
        Assert.assertFalse(audioReceiver.isReceiving());
    }

    @Test
    public void testStartReceiving() throws Exception {
        countDownLatch = new CountDownLatch(1);
        done = false;
        audioReceiver.addAudioReceivedListener(new AudioReceiver.AudioReceivedListener() {
            @Override
            public void AudioReceived(byte[] frame) {
                done = true;
                countDownLatch.countDown();
            }
        });
        audioReceiver.startReceiving();
        countDownLatch.await(5000, TimeUnit.MILLISECONDS);
        if(!done)
            Assert.fail("Didn't receive frame within time limit");
    }


}