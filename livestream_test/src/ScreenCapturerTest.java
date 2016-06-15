import eduze.livestream.ScreenCapturer;
import eduze.livestream.exchange.Connector;
import eduze.livestream.exchange.client.FrameBuffer;
import eduze.livestream.exchange.server.FrameBufferImpl;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import javax.xml.ws.Endpoint;
import java.rmi.RemoteException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * Created by Admin on 6/15/2016.
 */
public class ScreenCapturerTest {
    private int bufferSize = 5;
    Endpoint endpoint = null;

    boolean continueLoop = false;
    boolean done = false;
    CountDownLatch lock = new CountDownLatch(1);

    ScreenCapturer screenCapturer = null;
    FrameBufferImpl testFrameBufferServer = null;
    FrameBuffer frameBufferClient = null;
    @BeforeMethod
    public void setUp() throws Exception {
        testFrameBufferServer = new FrameBufferImpl(bufferSize);
        endpoint = Endpoint.publish("http://localhost:1024/testBuffer", testFrameBufferServer);


        frameBufferClient = Connector.obtainFrameBuffer("http://localhost:1024/testBuffer");
        screenCapturer= new ScreenCapturer(frameBufferClient,1000);
    }

    @AfterMethod
    public void tearDown() throws Exception {
        if(screenCapturer.isCapturing())
            screenCapturer.stopCapture();
        endpoint.stop();
    }

    @Test
    public void testIsCapturing() throws Exception {
        screenCapturer.startCapture();
        Assert.assertEquals(screenCapturer.isCapturing(),true);
    }

    @Test
    public void testStartCapture() throws Exception {
        screenCapturer.startCapture();
        continueLoop = true;
        Thread waitThread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true)
                {
                    try {
                        int segmentId = 0;
                        int frameId = 0;
                        eduze.livestream.exchange.client.FramePullResult result = frameBufferClient.pullFrames(segmentId,frameId);
                        segmentId = result.getSegmentIndex();
                        frameId = result.getNextFrameId();
                        if(result.getData() != null)
                        {
                            done = true;
                            break;
                        }

                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }
                lock.countDown();
            }
        });
        waitThread.start();
        lock.await(4000,TimeUnit.MILLISECONDS);
        if(!done)
        {
            Assert.fail("No Capture in given time frame");
        }
    }

    @Test
    public void testStopCapture() throws Exception {
        Assert.assertFalse(screenCapturer.isCapturing());
        screenCapturer.startCapture();
        Assert.assertTrue(screenCapturer.isCapturing());
        screenCapturer.stopCapture();
        Assert.assertFalse(screenCapturer.isCapturing());
    }

    @Test(singleThreaded = true)
    public void testSetImageEncoderFormat() throws Exception {
        screenCapturer.stopCapture();
        screenCapturer.setImageEncoderFormat("BMP");
        testReception("BMP Test");
        screenCapturer.setImageEncoderFormat("JPG");
        testReception("JPG Test");
        screenCapturer.setImageEncoderFormat("PNG");
        testReception("PNG Test");
    }

    public void testReception(String testName) throws RemoteException, InterruptedException {
        lock = new CountDownLatch(1);
        screenCapturer.startCapture();
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
        lock.await(50000,TimeUnit.MILLISECONDS);
        if(!done)
            Assert.fail(testName + " failed");

        screenCapturer.stopCapture();

    }

    long lastTime = 0;
    boolean firstHit = false;
    int leastTime = Integer.MAX_VALUE;
    int foundCount = 0;
    public int testRunConvergence(String testName, final int count) throws RemoteException, InterruptedException {
        lastTime = System.currentTimeMillis();
        leastTime = Integer.MAX_VALUE;
        foundCount = 0;
        firstHit =  true;
        lock = new CountDownLatch(1);
        screenCapturer.startCapture();
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
                    {
                        long nowTime = System.currentTimeMillis();
                        long delta = nowTime - lastTime;
                        lastTime = nowTime;
                        if(firstHit)
                        {
                            firstHit = false;
                        }
                        else
                        {
                            if(delta < leastTime)
                                leastTime = (int) delta;
                            foundCount++;
                            if(foundCount >= count)
                                break;
                        }

                    }
                }
                done = true;
                lock.countDown();
            }
        });
        t.start();
        lock.await(50000,TimeUnit.MILLISECONDS);
        if(!done)
            Assert.fail(testName + " failed");

        screenCapturer.stopCapture();
        return leastTime;
    }

    long testTime(int time) throws RemoteException, InterruptedException {
        screenCapturer.setCaptureInterval(time);
        long least = testRunConvergence("Timing " + time,3);
        System.out.println("Time taken " + least);
        return least;
    }

    @Test
    public void testSetCaptureInterval1000() throws Exception {
        Assert.assertTrue(testTime(1000) < 1500);
    }

    @Test
    public void testSetCaptureInterval2000() throws Exception {
        Assert.assertTrue(testTime(2000) < 3000);
    }

}