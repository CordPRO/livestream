package eduze.livestream.exchange.server;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */



import javax.jws.WebService;
import javax.xml.ws.Endpoint;

/**
 *
 * @author Madhawa
 */
@WebService(endpointInterface= "eduze.livestream.exchange.server.FrameBuffer")
public class FrameBufferImpl implements FrameBuffer {

    private int bufferSize = 10;
    private byte[][] frames = null;
    private int nextFrameId = 0;

    private int segmentID = 0;

    public FrameBufferImpl(int bufferSize) {
        this.bufferSize = bufferSize;
        frames = new byte[bufferSize][];
    }

    public int pushFrame(byte[] frame) {
        int slotId = nextFrameId % bufferSize;
        frames[slotId] = frame;
        System.out.println(Integer.toString(frame.length)+ " bytes received");
        return ++nextFrameId;
    }

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

    public void startNewSegment() {


        segmentID++;
        frames = new byte[bufferSize][];
        nextFrameId = 0;
        System.out.println("New Segment Started");
    }

    /**
     * @return the segmentID
     */
    public int getSegmentID() {
        return segmentID;
    }

/*    public static void main(String[] args) {
        Endpoint.publish("http://0.0.0.0:8000/audiorelay", new FrameBufferImpl(5));
        Endpoint.publish("http://0.0.0.0:8000/audiorelay2", new FrameBufferImpl(5));
        Endpoint.publish("http://0.0.0.0:8000/screenrelay", new FrameBufferImpl(2));
        Endpoint.publish("http://0.0.0.0:8000/screenrelay2", new FrameBufferImpl(2));

        Endpoint.publish("http://0.0.0.0:8000/screenrelayoutput", new FrameBufferImpl(2));
    }*/

}
