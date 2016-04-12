package eduze.livestream.exchange.server;

import javax.jws.WebMethod;
import javax.jws.WebService;

/**
 * Created by Fujitsu on 3/27/2016.
 */
@WebService
public interface FrameBuffer {
    @WebMethod
    public int pushFrame(byte[] frame);
    @WebMethod
    public FramePullResult pullFrames(int segmentID, int startFrameId);
    @WebMethod
    public void startNewSegment();
    @WebMethod
    public int getSegmentID();

}
