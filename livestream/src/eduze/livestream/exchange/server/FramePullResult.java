package eduze.livestream.exchange.server;


import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * A result set received from FrameBuffer.pullFrame method.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name="FramePullResult")
public class FramePullResult{

    @XmlElement(name="bufferOverRun")
    protected boolean bufferOverRun = false;
    @XmlElement(name="data")
    protected byte[][] data = null;
    @XmlElement(name="nextFrameId")
    protected int nextFrameId = 0;
    @XmlElement(name="segmentIndex")
    private int segmentIndex = 0;

    /**
     * @return true if reader has lost some frames due to buffer overflow
     */
    public boolean isBufferOverRun() {
        return bufferOverRun;
    }

    /**
     * @return data received from buffer. If no new data is available, returns null
     */
    public byte[][] getData() {
        return data;
    }

    /**
     * @param bufferOverRun the bufferOverRun to set
     */
    public void setBufferOverRun(boolean bufferOverRun) {
        this.bufferOverRun = bufferOverRun;
    }

    /**
     * @param data the data to set
     */
    public void setData(byte[][] data) {
        this.data = data;
    }

    /**
     * @param lastFrameId the lastFrameId to set
     */
    public void setNextFrameId(int lastFrameId) {
        this.nextFrameId = lastFrameId;
    }

    /**
     * @return the nextFrameId
     */
    public int getNextFrameId() {
        return nextFrameId;
    }

    /**
     * @return the segmentIndex
     */
    public int getSegmentIndex() {
        return segmentIndex;
    }

    /**
     * @param segmentIndex the segmentIndex to set
     */
    public void setSegmentIndex(int segmentIndex) {
        this.segmentIndex = segmentIndex;
    }

}