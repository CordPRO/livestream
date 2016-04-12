package eduze.livestream.exchange;

import eduze.livestream.exchange.client.FrameBuffer;
import eduze.livestream.exchange.client.FrameBufferImplServiceLocator;

import javax.xml.rpc.ServiceException;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by Madhawa on 12/04/2016.
 */

/**
 * Utility tool to connect a to FrameBuffer Service
 */
public class Connector {
    private Connector()
    {

    }

    /**
     * Obtains interface to FrameBuffer Service from URL
     * @param url URL of FrameBuffer service to connect to
     * @return Interface to FrameBuffer Service Connected
     * @throws ServiceException
     */
    public static FrameBuffer obtainFrameBuffer(URL url) throws ServiceException {
        FrameBufferImplServiceLocator locator = new FrameBufferImplServiceLocator();
        return locator.getFrameBufferImplPort(url);
    }

    /**
     * Obtains interface to FrameBuffer Service from String URL
     * @param url URL of FrameBuffer to connect
     * @return Interface to FrameBuffer Service Connected
     * @throws ServiceException
     * @throws MalformedURLException
     */
    public static FrameBuffer obtainFrameBuffer(String url) throws ServiceException, MalformedURLException {
        return obtainFrameBuffer(new URL(url));
    }
}
