import eduze.livestream.exchange.server.FrameBufferImpl;

import javax.xml.ws.Endpoint;

/**
 * Created by Madhawa on 12/04/2016.
 */
public class Server {

   public static void main(String[] args) {
        Endpoint.publish("http://0.0.0.0:8000/audiorelay", new FrameBufferImpl(5));
        Endpoint.publish("http://0.0.0.0:8000/audiorelay2", new FrameBufferImpl(5));
        Endpoint.publish("http://0.0.0.0:8000/screenrelay", new FrameBufferImpl(2));
        Endpoint.publish("http://0.0.0.0:8000/screenrelay2", new FrameBufferImpl(2));

        Endpoint.publish("http://0.0.0.0:8000/screenrelayoutput", new FrameBufferImpl(2));
    }
}
