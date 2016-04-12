package eduze.livestream.exchange.server;

/**
 * Created by Madhawa on 12/04/2016.
 */

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * Created by Fujitsu on 3/27/2016.
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
     * @return the bufferOverRun
     */
    public boolean isBufferOverRun() {
        return bufferOverRun;
    }

    /**
     * @return the data
     */
    public byte[][] getData() {
        return data;
    }

    /**
     * @return the lastFrameId
     */
    public int getLastFrameId() {
        return getNextFrameId();
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