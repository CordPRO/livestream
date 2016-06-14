import eduze.livestream.exchange.Connector;
import eduze.livestream.exchange.client.FrameBuffer;
import eduze.livestream.exchange.client.FramePullResult;
import eduze.livestream.exchange.server.FrameBufferImpl;
import org.apache.axis.utils.ByteArrayOutputStream;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import javax.xml.ws.Endpoint;

import java.io.ByteArrayInputStream;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.Scanner;

import static org.testng.Assert.*;

/**
 * Created by Admin on 6/14/2016.
 */
public class FrameBufferImplTest {
    private Endpoint endpoint;
    private int bufferSize = 5;
    private int addCount = 20;
    @BeforeMethod
    public void setUp() throws Exception {
        FrameBufferImpl testFrameBuffer = new FrameBufferImpl(bufferSize);
        endpoint = Endpoint.publish("http://localhost:1024/testBuffer",testFrameBuffer);
    }

    @AfterMethod
    public void tearDown() throws Exception {
        endpoint.stop();
    }

    @Test
    public void testPushFrame() throws Exception {
        FrameBuffer frameBuffer = Connector.obtainFrameBuffer("http://localhost:1024/testBuffer");

        for(int i = 0; i < 20; i++)
        {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            PrintStream printStream = new PrintStream(byteArrayOutputStream);
            printStream.print("Hello" + String.valueOf(i));
            printStream.close();
            frameBuffer.pushFrame(byteArrayOutputStream.toByteArray());
        }

    }

    @Test
    public void testPullFrames() throws Exception {
        FrameBuffer frameBuffer = Connector.obtainFrameBuffer("http://localhost:1024/testBuffer");
        frameBuffer.startNewSegment();

        HashMap<String,Boolean> stringMap = new HashMap<>();
        for(int i = 0; i < addCount; i++)
        {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            PrintStream printStream = new PrintStream(byteArrayOutputStream);
            printStream.println("Hello" + String.valueOf(i));
            stringMap.put("Hello" + String.valueOf(i),true);
            printStream.close();
            frameBuffer.pushFrame(byteArrayOutputStream.toByteArray());
            byteArrayOutputStream.close();
        }
        int segmentIndex = 0;
        int frameIndex = 0;

        int totalFramesInBuffer = 0;
        while (true)
        {
            FramePullResult framePullResult = frameBuffer.pullFrames(segmentIndex,frameIndex);
            segmentIndex = framePullResult.getSegmentIndex();
            frameIndex = framePullResult.getNextFrameId();
            if( framePullResult.getData() == null)
                break;
            for(byte[] data : framePullResult.getData())
            {
                ByteArrayInputStream binStream = new ByteArrayInputStream(data);
                Scanner sc = new Scanner(binStream);
                String line =  sc.nextLine();
                Assert.assertTrue(stringMap.containsKey(line));
            }
            totalFramesInBuffer += framePullResult.getData().length;

        }
        Assert.assertEquals(totalFramesInBuffer,Math.min(bufferSize,addCount));

    }

    //Only the items in new segment should be received
    @Test
    public void testStartNewSegment() throws Exception {
        FrameBuffer frameBuffer = Connector.obtainFrameBuffer("http://localhost:1024/testBuffer");
        frameBuffer.startNewSegment();

        for(int i = 0; i < addCount; i++)
        {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            PrintStream printStream = new PrintStream(byteArrayOutputStream);
            printStream.println("Hi" + String.valueOf(i));
            printStream.close();
            frameBuffer.pushFrame(byteArrayOutputStream.toByteArray());
            byteArrayOutputStream.close();
        }
        frameBuffer.startNewSegment();
        for(int i = 0; i < addCount/2; i++)
        {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            PrintStream printStream = new PrintStream(byteArrayOutputStream);
            printStream.println("Bye" + String.valueOf(i));
            printStream.close();
            frameBuffer.pushFrame(byteArrayOutputStream.toByteArray());
            byteArrayOutputStream.close();
        }


        int segmentIndex = 0;
        int frameIndex = 0;

        int totalFramesInBuffer = 0;

        while (true)
        {
            FramePullResult framePullResult = frameBuffer.pullFrames(segmentIndex,frameIndex);
            segmentIndex = framePullResult.getSegmentIndex();
            frameIndex = framePullResult.getNextFrameId();
            if( framePullResult.getData() == null)
                break;
            for(byte[] data : framePullResult.getData())
            {
                ByteArrayInputStream binStream = new ByteArrayInputStream(data);
                Scanner sc = new Scanner(binStream);
                String line =  sc.nextLine();
                if(!line.startsWith("Bye"))
                {
                    Assert.fail("Only the bye messages should be there");
                }
            }
            totalFramesInBuffer += framePullResult.getData().length;

        }
        Assert.assertEquals(totalFramesInBuffer,Math.min(bufferSize,addCount/2));


    }

    @Test
    public void testGetSegmentID() throws Exception {
        FrameBuffer frameBuffer = Connector.obtainFrameBuffer("http://localhost:1024/testBuffer");
        Assert.assertEquals(frameBuffer.getSegmentID(),0);
        frameBuffer.startNewSegment();
        Assert.assertEquals(frameBuffer.getSegmentID(),1);
    }

}