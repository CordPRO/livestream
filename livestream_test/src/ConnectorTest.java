import eduze.livestream.exchange.Connector;
import eduze.livestream.exchange.client.FrameBuffer;
import eduze.livestream.exchange.server.FrameBufferImpl;

import javax.xml.ws.Endpoint;

import java.net.URI;
import java.net.URL;

import static org.testng.Assert.*;

/**
 * Created by Admin on 6/14/2016.
 */
public class ConnectorTest {
    Endpoint endpoint;
    @org.testng.annotations.BeforeMethod
    public void setUp() throws Exception {
        FrameBufferImpl testFrameBuffer = new FrameBufferImpl(5);
        endpoint = Endpoint.publish("http://localhost:1024/testBuffer",testFrameBuffer);

    }

    @org.testng.annotations.AfterMethod
    public void tearDown() throws Exception {
        endpoint.stop();
    }

    @org.testng.annotations.Test
    public void testObtainFrameBuffer() throws Exception {
        FrameBuffer  frameBuffer = Connector.obtainFrameBuffer("http://localhost:1024/testBuffer");
        frameBuffer.startNewSegment();
    }

    @org.testng.annotations.Test
    public void testObtainFrameBuffer1() throws Exception {
        FrameBuffer  frameBuffer = Connector.obtainFrameBuffer(new URL("http://localhost:1024/testBuffer"));
        frameBuffer.startNewSegment();
    }

}