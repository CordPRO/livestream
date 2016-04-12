package eduze.livestream;

import eduze.livestream.exchange.client.FrameBuffer;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.TargetDataLine;
import java.rmi.RemoteException;

/**
 * Created by Fujitsu on 3/28/2016.
 */

/**
 * Captures Audio Input and Pushes Frames to provided FrameBuffer
 */
public class AudioCapturer extends AbstractCapturer {
    //Captures Audio Input and pushes frames to FrameBuffer

    private AudioFormat format; //audio format to be used for capturing
    private TargetDataLine microphone; //Audio Input line from hardware
    private boolean keepCapturing = false; //is the capturing running
    private Thread captureThread = null; //Thread used for capturing


    /**
     *
     * @param buffer FrameBuffer used to write captured frames
     */
    public AudioCapturer(FrameBuffer buffer)
    {
        super(buffer);
        this.format = new AudioFormat(8000.0f, 16, 1, true, true);
    }

    /**
     *
     * @return True if Capturing is running. Otherwise False.
     */
    @Override
    public synchronized boolean isCapturing()
    {
        return keepCapturing;
    }

    /**
     * Begins Capturing Audio
     */
    @Override
    public synchronized void startCapture() throws RemoteException {
        if(isCapturing()) //If capturing has began already, don't do anything
            return;

        //Setup Capture Thread
        captureThread = new Thread(new Runnable() {

            @Override
            public void run() {

                try {
                    //Open Line-in
                    microphone = AudioSystem.getTargetDataLine(getFormat());
                    microphone.open(getFormat());

                    microphone.start();

                    //Setup feed thread
                    int numBytesRead;
                    byte[] data = new byte[microphone.getBufferSize() / 5];

                    //Capture loop
                    while(isCapturing())
                    {
                        //Pushes each block of bytes as a new frame to FrameBuffer
                        numBytesRead =  microphone.read(data, 0, data.length);

                        byte[] sizedData = new byte[numBytesRead];
                        System.arraycopy(data, 0, sizedData, 0, numBytesRead);
                        try {
                            writeBuffer.pushFrame(sizedData);
                        } catch (RemoteException e) {
                            e.printStackTrace();
                            notifyException(e);
                        }
                    }


                } catch (LineUnavailableException e) {
                    e.printStackTrace();
                    notifyException(e);
                }
                finally {
                    //Close line-in connections
                    microphone.stop();
                    microphone.close();
                }
            }
        });

        //Start the capture thread
        keepCapturing = true;
        writeBuffer.startNewSegment(); //Indicate that a new segment has begun in FrameBuffer
        captureThread.start();
    }

    /**
     * Stop Capturing of Audio
     */
    public synchronized void stopCapture()
    {
        keepCapturing = false;
    }

    /**
     *
     * @return AudioFormat used for capturing. Should match the audio format of receiver
     */
    public synchronized AudioFormat getFormat() {
        return format;
    }

    /**
     *
     * @param format AudioFormat used for capturing. Should match the audio format of receiver
     * @throws IllegalStateException if Capturing has already begun
     */
    public synchronized void setFormat(AudioFormat format) throws IllegalStateException {
        if(isCapturing())
            throw new IllegalStateException("Capturing has already started");
        this.format = format;
    }
}
