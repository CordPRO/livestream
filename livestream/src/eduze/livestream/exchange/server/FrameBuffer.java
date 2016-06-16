package eduze.livestream.exchange.server;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;

/**
 * A WebService that represents a shared limited capacity buffer with single write and multiple concurrent read features.
 */
@WebService
public interface FrameBuffer {
    /**
     * Pushes a byte-array(frame) to buffer
     * @param frame Array of bytes to be pushed to server
     * @return Index of next frame to be in next pushFrame
     */
    @WebMethod
    public int pushFrame(@WebParam(name = "frame") byte[] frame);

    /**
     * Reads previously un-read byte-arrays (frames) in buffer by the calling user
     * Two parameters segmentID and startFrameID are used to determine unread frames of user
     * @param segmentID if available, give SegmentID received from previous pushFrame. Otherwise give 0
     * @param startFrameId if available, give FrameID received from previous pushFrame. Otherwise give 0
     * @return FramePullResult that contains previously un-read frames in buffer by the calling user
     */
    @WebMethod
    public FramePullResult pullFrames(@WebParam(name = "segmentID") int segmentID, @WebParam(name="startFrameId") int startFrameId);

    /**
     * Used by writer to indicate that frames pushed from here on belongs to a new file. The readers will not receive frames that belong to previous file when they call pullframe.
     */
    @WebMethod
    public void startNewSegment();

    /**
     * Obtain current segment id
     * @return Segment ID
     */
    @WebMethod
    public int getSegmentID();

}
