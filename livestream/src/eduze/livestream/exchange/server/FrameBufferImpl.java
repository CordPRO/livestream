package eduze.livestream.exchange.server;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */



import javax.jws.WebService;
import javax.xml.ws.Endpoint;

/**
 * A WebService that represents a shared limited capacity buffer with single write and multiple concurrent read features.
 */
@WebService(endpointInterface= "eduze.livestream.exchange.server.FrameBuffer")
public class FrameBufferImpl implements FrameBuffer {
    //Buffer Paramters
    private int bufferSize = 10;
    private byte[][] frames = null;
    private int nextFrameId = 0;

    private int segmentID = 0;

    /**
     * Constructor of FrameBuffer. Pass the FrameBuffer to EndPoint.publish() inorder to publish FrameBuffer as a Service.
     * @param bufferSize Capacity of Buffer in terms of number of frames.
     */
    public FrameBufferImpl(int bufferSize) {
        this.bufferSize = bufferSize;
        frames = new byte[bufferSize][];
    }

    /**
     * Pushes a byte-array(frame) to buffer
     * @param frame Array of bytes to be pushed to server
     * @return Index of next frame to be in next pushFrame
     */
    public int pushFrame(byte[] frame) {
        int slotId = nextFrameId % bufferSize;
        frames[slotId] = frame;
        System.out.println(Integer.toString(frame.length)+ " bytes received");
        return ++nextFrameId;
    }

    /**
     * Reads previously un-read byte-arrays (frames) in buffer by the calling user
     * Two parameters segmentID and startFrameID are used to determine unread frames of user
     * @param segmentID if available, give SegmentID received from previous pushFrame. Otherwise give 0
     * @param startFrameId if available, give FrameID received from previous pushFrame. Otherwise give 0
     * @return FramePullResult that contains previously un-read frames in buffer by the calling user
     */
    public FramePullResult pullFrames(int segmentID, int startFrameId) {
        if (segmentID == this.segmentID) {
            if (nextFrameId - startFrameId > bufferSize) {
                //buffer overrun. return all available frames
                byte[][] resultsData = new byte[bufferSize][];
                for (int i = 0; i < bufferSize; i++) {
                    int slot = (i + nextFrameId) % bufferSize;
                    resultsData[i] = frames[slot];
                }

                FramePullResult result = new FramePullResult();
                result.setBufferOverRun(true);
                result.setData(resultsData);
                result.setNextFrameId( nextFrameId);
                result.setSegmentIndex( segmentID);
                return result;
            } else {
                int frameCount = nextFrameId - startFrameId;
                byte[][] resultsData = new byte[frameCount][];
                for (int i = 0; i < frameCount; i++) {
                    int slot = (startFrameId + i) % bufferSize;
                    resultsData[i] = frames[slot];
                }

                FramePullResult result = new FramePullResult();
                result.setBufferOverRun(false);
                result.setData(resultsData);
                result.setNextFrameId(nextFrameId);
                result.setSegmentIndex(segmentID);
                return result;
            }
        } else {

            int dataCount = 0;
            for(int i = 0; i < bufferSize; i++)
            {
                //see whether the buffer is filled
                if(frames[i] != null)
                    dataCount++;
            }
            if(dataCount == bufferSize)
            {
                //buffer is filled
                //return all available frames
                byte[][] resultsData = new byte[bufferSize][];
                for (int i = 0; i < bufferSize; i++) {
                    int slot = (i + nextFrameId) % bufferSize;
                    resultsData[i] = frames[slot];
                }

                FramePullResult result = new FramePullResult();
                result.setBufferOverRun(true);
                result.setData(resultsData);
                result.setNextFrameId(nextFrameId);
                result.setSegmentIndex( this.segmentID);
                return result;
            }
            else
            {
                //buffer is partly filled
                //return all available frames
                byte[][] resultsData = new byte[dataCount][];
                for (int i = 0; i < dataCount; i++) {
                    int slot = (i + nextFrameId - dataCount) % bufferSize;
                    resultsData[i] = frames[slot];
                }

                FramePullResult result = new FramePullResult();
                result.setBufferOverRun(false);
                result.setData(resultsData);
                result.setNextFrameId(nextFrameId);
                result.setSegmentIndex( this.segmentID);
                return result;
            }

        }

    }


    /**
     * Used by writer to indicate that frames pushed from here on belongs to a new file. The readers will not receive frames that belong to previous file when they call pullframe.
     */
    public void startNewSegment() {


        segmentID++;
        frames = new byte[bufferSize][];
        nextFrameId = 0;
        System.out.println("New Segment Started");
    }

    /**
     * Obtain current segment id
     * @return Segment ID
     */
    public int getSegmentID() {
        return segmentID;
    }


}
