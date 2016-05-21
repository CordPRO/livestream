package eduze.livestream;

import eduze.livestream.exchange.client.FrameBuffer;
import eduze.livestream.exchange.client.FrameBufferImplServiceLocator;
import eduze.livestream.exchange.client.FramePullResult;

import javax.swing.*;
import javax.xml.rpc.ServiceException;
import java.net.URL;
import java.rmi.RemoteException;
import java.util.ArrayList;

/**
 * Created by Fujitsu on 3/29/2016.
 */

/**
 * Directs the input from a FrameBuffer to a pre-defined output FrameBuffer. The input buffer can be changed at runtime. Hence, this would behave as a switch.
 */
public class Multiplexer {
    private URL outputURL; //url to output buffer to which frames are written
    private URL inputURL = null; //url of input buffer from which frames are fetched
    private boolean running = false; //is the multiplexer running
    private Thread thread = null; //worker thread
    private boolean switchPending = false; //is the input URL changed by the user
    private ArrayList<DataReceivedListener> dataReceivedListeners;

    /**
     *
     * @param outputURL The URL of FrameBuffer Service to which frames will be written
     */
    public Multiplexer(URL outputURL)
    {
        this.outputURL = outputURL; dataReceivedListeners = new ArrayList<>();
    }

    /**
     *
     * @return URL of FrameBuffer to which output frames are written
     */
    public synchronized URL getOutputURL() {
        return outputURL;
    }

    /**
     * Internal method to notify that input URL switch is pending
     */
    private synchronized void markSwitchPending()
    {
        switchPending = true;
    }

    /**
     * Internal method to notify that input URL switch has been completed
     */
    private synchronized void clearSwitchPending()
    {
        switchPending = false;
    }

    /**
     *
     * @return True if their exist a due request to change inputURL
     */
    private synchronized boolean isSwitchPending()
    {
        return switchPending;
    }

    /**
     *
     * @return URL of FrameBuffer from which frames are fed in
     */
    public synchronized URL getInputURL() {
        return inputURL;
    }

    /**
     * Change the input FrameBuffer of the multiplexer. Can be done at runtime.
     * @param inputURL URL of FrameBuffer Service Component
     */
    public synchronized void setInputURL(URL inputURL) {
        this.inputURL = inputURL;
        markSwitchPending();
    }

    /**
     *
     * @return True if the multiplexer is running. i.e. Inputs from InputURL are fed to Output URL.
     */
    public synchronized boolean isRunning() {
        return running;
    }

    /**
     * Internal method to update running state
     * @param running
     */
    private synchronized void setRunning(boolean running) {
        this.running = running;
    }

    /**
     * Stop the Multiplexer. A multiplexer stops switching of Frames from Input to Output when its stopped.
     */
    public synchronized void Stop()
    {
        setRunning(false);
    }

    /**
     * Starts the Multiplexer. A multiplexer starts directing frames from input to output when its started.
     */
    public synchronized void Start()
    {
        if(isRunning())
            return;

        //if an input url is already assigned, mark that a new switch is pending. this will ensure that start new segment is called in output stream
        if(getInputURL() != null)
            markSwitchPending();

        thread = new Thread(new Runnable() {
            @Override
            public void run() {
                //open output connection

                FrameBufferImplServiceLocator outputServiceLocator = null;
                FrameBuffer outputBuffer = null;

                FrameBufferImplServiceLocator inputServiceLocator = null;
                FrameBuffer inputBuffer = null;

                //State Variables
                int segmentID = 0;
                int nextFrameID = 0;

                while(isRunning())
                {
                    try{
                        if(outputBuffer == null)
                        {
                            outputServiceLocator = new FrameBufferImplServiceLocator();
                            outputBuffer = outputServiceLocator.getFrameBufferImplPort(getOutputURL());
                        }
                        if(isSwitchPending())
                        {
                            //a switch is pending, implement  it
                            inputServiceLocator = new FrameBufferImplServiceLocator();
                            if(getInputURL() != null && !("".equals(getInputURL())))
                            {
                                inputBuffer = inputServiceLocator.getFrameBufferImplPort(getInputURL());
                            }
                            else
                                inputBuffer = null;

                            //reset state variables
                            segmentID = 0;
                            nextFrameID = 0;

                            outputBuffer.startNewSegment(); //notify start of a new segment
                            clearSwitchPending();
                        }
                        else
                        {
                            //tunnel input to output
                            if(inputBuffer != null)
                            {
                                //if an input frame is available, relay input from it to output
                                FramePullResult pullResult = inputBuffer.pullFrames(segmentID,nextFrameID);
                                segmentID = pullResult.getSegmentIndex();
                                nextFrameID = pullResult.getNextFrameId();
                                byte[][] data = pullResult.getData();
                                if(data != null)
                                {
                                    for(byte[] block : data)
                                    {
                                        outputBuffer.pushFrame(block);
                                        notifyDataReceived(block);
                                    }
                                }

                            }
                        }
                    }
                    catch (ServiceException e)
                    {
                        e.printStackTrace();
                        //TODO: Notify user
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }

                }

                inputBuffer = null;
                inputServiceLocator = null;
                outputBuffer = null;
                outputServiceLocator = null;
            }
        });

        //Start worker thread
        setRunning(true);
        thread.start();
    }


    /**
     * Interface to notify user when an data frame is received from ReadBuffer
     */
    public interface DataReceivedListener
    {
        public void DataReceived(byte[] frame);
    }


    /**
     *
     * @param listener DataReceivedListener to be notified when a new frame is received.
     */
    public synchronized void addDataReceivedListener(DataReceivedListener listener)
    {
        dataReceivedListeners.add(listener);
    }

    /**
     *
     * @param listener DataReceivedListener to be removed from list of notifiers
     */
    public synchronized void removeDataReceivedListener(DataReceivedListener listener)
    {
        dataReceivedListeners.remove(listener);
    }

    /**
     * Notifiers all DataReceivedListeners that a new frame has been received
     * @param frame Frame to be notified
     */
    private synchronized void notifyDataReceived(final byte[] frame)
    {
        //Notify at the UI thread
        SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {
                synchronized (Multiplexer.this)
                {
                    for(DataReceivedListener lister : dataReceivedListeners)
                    {
                        lister.DataReceived(frame);
                    }
                }

            }
        });

    }

}
