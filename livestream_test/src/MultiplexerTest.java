import eduze.livestream.Multiplexer;
import eduze.livestream.exchange.Connector;
import eduze.livestream.exchange.client.FrameBuffer;
import eduze.livestream.exchange.server.FrameBufferImpl;
import org.apache.axis.utils.ByteArrayOutputStream;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import javax.xml.ws.Endpoint;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.net.URL;
import java.rmi.RemoteException;
import java.util.Scanner;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.testng.Assert.*;

/**
 * Created by Admin on 6/15/2016.
 */
public class MultiplexerTest {
    FrameBuffer outputBuffer = null;
    FrameBuffer inputBuffer1 = null;
    FrameBuffer inputBuffer2 = null;

    Endpoint outputEndpoint = null;
    Endpoint input1Endpoint = null;
    Endpoint input2Endpoint = null;
    int bufferSize = 5;

    boolean done = false;
    CountDownLatch lock = null;

    Multiplexer multiplexer = null;
    @BeforeMethod
    public void setUp() throws Exception {
        Thread.currentThread().sleep(100);
        FrameBufferImpl outputBufferS = new FrameBufferImpl(bufferSize);
        outputEndpoint = Endpoint.publish("http://localhost:8000/output",outputBufferS);
        outputBuffer = Connector.obtainFrameBuffer("http://localhost:8000/output");

        FrameBufferImpl inputBuffer1s = new FrameBufferImpl(bufferSize);
        input1Endpoint = Endpoint.publish("http://localhost:8000/input1",inputBuffer1s);
        inputBuffer1 = Connector.obtainFrameBuffer("http://localhost:8000/input1");

        FrameBufferImpl inputBuffer2s = new FrameBufferImpl(bufferSize);
        input2Endpoint = Endpoint.publish("http://localhost:8000/input2",inputBuffer2s);
        inputBuffer2 = Connector.obtainFrameBuffer("http://localhost:8000/input2");

        multiplexer = new Multiplexer(new URL("http://localhost:8000/output"));
    }

    @AfterMethod
    public void tearDown() throws Exception {
        outputEndpoint.stop();
        input1Endpoint.stop();
        input2Endpoint.stop();
        Thread.currentThread().sleep(100);
    }

    int segmentId = 0;
    int frameIndex = 0;

    String reception = "";
    public String testReception(String testName) throws RemoteException, InterruptedException {
        lock = new CountDownLatch(1);
        done = false;
        reception = "";
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {

                while(true)
                {
                    eduze.livestream.exchange.client.FramePullResult framePullResult = null;
                    try {
                        framePullResult = outputBuffer.pullFrames(segmentId,frameIndex);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                    segmentId = framePullResult.getSegmentIndex();
                    frameIndex = framePullResult.getNextFrameId();
                    if(framePullResult.getData() != null)
                    {
                        for(byte[] bytes : framePullResult.getData())
                        {
                            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);
                            Scanner sc = new Scanner(byteArrayInputStream);
                            reception += sc.nextLine();
                            sc.close();
                            try {
                                byteArrayInputStream.close();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                        break;
                    }

                }
                done = true;
                lock.countDown();
            }
        });
        t.start();
        lock.await(50000, TimeUnit.MILLISECONDS);
        if(!done)
            Assert.fail(testName + " failed");

       return reception;

    }

    public void writeToBuffer(FrameBuffer buffer, String value) throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        PrintStream printStream =new PrintStream(byteArrayOutputStream);
        printStream.print(value);
        printStream.close();
        buffer.pushFrame(byteArrayOutputStream.toByteArray());
        byteArrayOutputStream.close();
    }

    @Test
    public void testSetInputURL() throws Exception {
        multiplexer.Start();
        multiplexer.setInputURL(new URL("http://localhost:8000/input1"));
        writeToBuffer(inputBuffer1,"hello");
        Assert.assertEquals(testReception("test hello"),"hello");
        writeToBuffer(inputBuffer2,"bye");
        multiplexer.setInputURL(new URL("http://localhost:8000/input2"));
        Assert.assertEquals(testReception("test hello"),"bye");
        multiplexer.Stop();

    }

    @Test
    public void testIsRunning() throws Exception {
        multiplexer.Start();
        Assert.assertTrue(multiplexer.isRunning());
        multiplexer.Stop();
        Assert.assertFalse(multiplexer.isRunning());
    }

    @Test
    public void testStart() throws Exception {
        multiplexer.setInputURL(new URL("http://localhost:8000/input1"));
        multiplexer.Start();
        writeToBuffer(inputBuffer1,"hello");
        Assert.assertEquals(testReception("test hello"),"hello");
        multiplexer.Stop();
    }

}