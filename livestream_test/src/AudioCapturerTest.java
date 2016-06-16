import eduze.livestream.AudioCapturer;
import eduze.livestream.ScreenCapturer;
import eduze.livestream.exchange.Connector;
import eduze.livestream.exchange.client.FrameBuffer;
import eduze.livestream.exchange.server.FrameBufferImpl;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import javax.sound.sampled.AudioFormat;
import javax.xml.ws.Endpoint;
import java.rmi.RemoteException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

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
        Thread.currentThread().sleep(1000);
        testFrameBufferServer = new FrameBufferImpl(bufferSize);
        endpoint = Endpoint.publish("http://localhost:1024/testBuffer", testFrameBufferServer);
        frameBufferClient = Connector.obtainFrameBuffer("http://localhost:1024/testBuffer");

        audioCapturer = new AudioCapturer(frameBufferClient);
    }

    @AfterMethod
    public void tearDown() throws Exception {
        Thread.currentThread().sleep(200);
        audioCapturer.stopCapture();
        endpoint.stop();
        Thread.currentThread().sleep(1000);
    }

    @Test(singleThreaded = true)
    public void testIsCapturing() throws Exception {
        audioCapturer.startCapture();
        Assert.assertTrue(audioCapturer.isCapturing());
        audioCapturer.stopCapture();
        Assert.assertFalse(audioCapturer.isCapturing());
    }

    public void testReception(String testName) throws RemoteException, InterruptedException {
        lock = new CountDownLatch(1);
        audioCapturer.startCapture();
        done = false;
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                int segmentId = 0;
                int frameIndex = 0;
                while(true)
                {
                    eduze.livestream.exchange.client.FramePullResult framePullResult = null;
                    try {
                        framePullResult = frameBufferClient.pullFrames(segmentId,frameIndex);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                    segmentId = framePullResult.getSegmentIndex();
                    frameIndex = framePullResult.getNextFrameId();
                    if(framePullResult.getData() != null)
                        break;
                }
                done = true;
                lock.countDown();
            }
        });
        t.start();
        lock.await(50000, TimeUnit.MILLISECONDS);
        if(!done)
            Assert.fail(testName + " failed");

        audioCapturer.stopCapture();

    }
    @Test(singleThreaded = true)
    public void testStartCapture() throws Exception {
        audioCapturer.startCapture();
        testReception("Receive Audio");
        audioCapturer.stopCapture();
    }

    @Test(singleThreaded = true)
    public void testSetFormat() throws Exception {
        AudioFormat af = new AudioFormat(8000.0f, 16, 1, true, true);
        audioCapturer.setFormat(af);
        audioCapturer.startCapture();
        testReception("Receive Audio in Format");
        audioCapturer.stopCapture();
    }

}