package eduze.livestream;

/**
 * Created by Fujitsu on 3/28/2016.
 */

import eduze.livestream.exchange.client.FrameBuffer;
import eduze.livestream.exchange.client.FramePullResult;

import javax.sound.sampled.*;
import javax.swing.*;
import java.rmi.RemoteException;
import java.util.ArrayList;

/**
 * Created by Fujitsu on 3/28/2016.
 */

/**
 * Receives audio frames from a FrameBuffer and plays them through line-out
 */
public class AudioReceiver extends AbstractReceiver {

    private boolean keepReceiving = false; //is the receiver running?
    private  Thread receiveThread = null; //thread used to receive audio frames

    private boolean playReceived = true; //should the received frames be played?
    private AudioFormat format; //format of received frames

    //listeners to be notified on received frames
    private ArrayList<AudioReceivedListener> audioReceivedListeners = new ArrayList<>();

    /**
     *
     * @param readBuffer FrameBuffer to stream-in the audio frames
     */
    public AudioReceiver(FrameBuffer readBuffer) {
        super(readBuffer);
        format = new AudioFormat(8000.0f, 16, 1, true, true); //default audio format
    }

    /**
     *
     * @return true if received audio frames are played immediately. Otherwise return false.
     */
    public synchronized boolean isPlayReceived() {
        return playReceived;
    }

    /**
     *
     * @param playReceived set true to automatically play received frames. Otherwise they will not be played. In either case, a listener can be used to extract raw frames.
     */
    public synchronized void setPlayReceived(boolean playReceived) {
        this.playReceived = playReceived;

    }

    /**
     *
     * @return AudioFormat of Audio Frames in ReadBuffer
     */
    public synchronized AudioFormat getFormat() {
        return format;
    }

    /**
     *
     * @param format AudioFormat of Audio frames in ReadBuffer
     * @throws IllegalStateException If format is changed when the receiving is in running state
     */
    public synchronized void setFormat(AudioFormat format) throws IllegalStateException  {
        if(isReceiving())
            throw new IllegalStateException("Receiving has started already");
        this.format = format;
    }

    /**
     *
     * @return true if receiver is running. otherwise return false.
     */
    @Override
    public synchronized boolean isReceiving()
    {
        return keepReceiving;
    }

    /**
     * Stops listening to audio frames through ReadBuffer
     */
    @Override
    public synchronized void stopReceiving()
    {
        keepReceiving = false;
    }

    /**
     * Start Receiving of audio frames from ReadBuffer
     */
    @Override
    public synchronized void startReceiving()
    {
        //setup receiver thread
        receiveThread = new Thread(new Runnable() {
            @Override
            public void run() {
                DataLine.Info lineInfo = new DataLine.Info(SourceDataLine.class, format);
                SourceDataLine sourceline = null;
                try {

                    //session variables i.e. buffer pointers
                    int segmentID = 0;
                    int nextFrameIndex = 0;

                    //open line-out
                    sourceline = (SourceDataLine) AudioSystem.getLine(lineInfo);
                    sourceline.open(format);

                    sourceline.start();

                    while (isReceiving()) {
                        //listen to ReadBuffer
                        try {
                            FramePullResult result = null;
                            result = readBuffer.pullFrames(segmentID, nextFrameIndex);

                            //update session variables
                            segmentID = result.getSegmentIndex();
                            nextFrameIndex = result.getNextFrameId();

                            byte[][] data = result.getData();
                            if(data != null && data.length > 0)
                            {
                                for(byte[] b : data)
                                {
                                    if(isPlayReceived())
                                    {
                                        //push the received frames to line-out
                                        sourceline.write(b,0,b.length);
                                    }
                                    System.out.println(String.valueOf(b.length) + " bytes sent to audio out.");
                                    notifyAudioReceived(b); //notify listeners
                                }
                            }
                            //receive frames



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
                    //close line-out
                    if(sourceline != null)
                    {
                        sourceline.stop();
                        sourceline.close();
                    }

                }


            }

        });
        //start receiver thread
        keepReceiving = true;
        receiveThread.start();
    }

    /**
     * Interface to notify user when an audio frame is received from ReadBuffer
     */
    public interface AudioReceivedListener
    {
        public void AudioReceived(byte[] frame);
    }

    /**
     *
     * @param listener AudioReceivedListener to be notified when a new frame is received.
     */
    public synchronized void addAudioReceivedListener(AudioReceivedListener listener)
    {
        audioReceivedListeners.add(listener);
    }

    /**
     *
     * @param listener AudioReceivedListener to be removed from list of notifiers
     */
    public synchronized void removeAudioReceivedListener(AudioReceivedListener listener)
    {
        audioReceivedListeners.remove(listener);
    }

    /**
     * Notifiers all AudioReceivedListeners that a new frame has been received
     * @param frame Frame to be notified
     */
    private synchronized void notifyAudioReceived(final byte[] frame)
    {
        //Notify at the UI thread
        SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {
                synchronized (AudioReceiver.this)
                {
                    for(AudioReceivedListener lister : audioReceivedListeners)
                    {
                        lister.AudioReceived(frame);
                    }
                }

            }
        });

    }

}

