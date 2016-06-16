/**
 * FrameBuffer.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package eduze.livestream.exchange.client;

public interface FrameBuffer extends java.rmi.Remote {

    /**
     * Used by writer to indicate that frames pushed from here on belongs to a new file. The readers will not receive frames that belong to previous file when they call pullframe.
     */
    public void startNewSegment() throws java.rmi.RemoteException;
    /**
     * Obtain current segment id
     * @return Segment ID
     */
    public int getSegmentID() throws java.rmi.RemoteException;
    /**
     * Pushes a byte-array(frame) to buffer
     * @param frame Array of bytes to be pushed to server
     * @return Index of next frame to be in next pushFrame
     */
    public int pushFrame(byte[] frame) throws java.rmi.RemoteException;
    /**
     * Reads previously un-read byte-arrays (frames) in buffer by the calling user
     * Two parameters segmentID and startFrameID are used to determine unread frames of user
     * @param segmentID if available, give SegmentID received from previous pushFrame. Otherwise give 0
     * @param startFrameId if available, give FrameID received from previous pushFrame. Otherwise give 0
     * @return FramePullResult that contains previously un-read frames in buffer by the calling user
     */
    public eduze.livestream.exchange.client.FramePullResult pullFrames(int segmentID, int startFrameId) throws java.rmi.RemoteException;
}
