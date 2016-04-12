package eduze.livestream;

import eduze.livestream.exchange.client.FrameBuffer;
import eduze.livestream.exchange.client.FramePullResult;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.rmi.RemoteException;
import java.util.ArrayList;

/**
 * Created by Fujitsu on 3/28/2016.
 */
public class ScreenReceiver extends AbstractReceiver {
    private final int receiveInterval; //how often should the buffer be checked for new screen captures
    private boolean keepReceiving = false; //is the receiver running
    private  Thread receiveThread = null; //thread used to receive screen captures


    //list of listeners to be notified when a new screen capture is received
    private ArrayList<ScreenReceivedListener> screenReceivedListeners = new ArrayList<>();

    /**
     *
     * @param readBuffer the buffer to listen for screen captures
     * @param receiveInterval frequency to look for screen captures in buffer
     */
    public ScreenReceiver(FrameBuffer readBuffer, int receiveInterval) {
        super(readBuffer);
        this.receiveInterval = receiveInterval;
    }

    /**
     *
     * @return true if Screen capturing is running. Otherwise return false.
     */
    @Override
    public synchronized boolean isReceiving()
    {
        return keepReceiving;
    }

    /**
     * Stop listening to readBuffer for screen captures
     */
    @Override
    public synchronized void stopReceiving()
    {
        keepReceiving = false;
    }

    /**
     * Start receiving screen captures from read buffer
     */
    @Override
    public synchronized void startReceiving()
    {
        receiveThread = new Thread(new Runnable() {
            @Override
            public void run() {

                //Sessions variables. i.e. buffer pointers
                int segmentID = 0;
                int nextFrameIndex = 0;

                while(isReceiving()) {

                    try{
                        final FramePullResult result = readBuffer.pullFrames(segmentID, nextFrameIndex);
                        //update session variables
                        segmentID = result.getSegmentIndex();
                        nextFrameIndex = result.getNextFrameId();


                        if (result.getData() != null && result.getData().length > 0) {
                            final byte[] latestFrame = result.getData()[result.getData().length - 1];

                            //if any results are received, process in UI thread
                            try {
                                //Process in UI thread since screen captures are passed to UI
                                SwingUtilities.invokeAndWait(new Runnable() {
                                    @Override
                                    public void run() {
                                        //extract frame

                                        ByteArrayInputStream stream = null;
                                        BufferedImage resultImage = null;
                                        try {
                                            stream = new ByteArrayInputStream(latestFrame);
                                            resultImage = ImageIO.read(stream);
                                            stream.close();
                                        } catch (IOException e) {
                                            e.printStackTrace();
                                            notifyException(e);
                                        }
                                        finally {
                                            if(stream != null)
                                                try {
                                                    stream.close();
                                                } catch (IOException e) {
                                                    e.printStackTrace();
                                                    notifyException(e);
                                                }
                                        }

                                        //notify screen captured to user
                                        notifyScreenReceived(latestFrame, resultImage);

                                    }
                                });
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                                notifyException(e);
                            } catch (InvocationTargetException e) {
                                e.printStackTrace();
                                notifyException(e);
                            }
                            try {
                                Thread.currentThread().sleep(receiveInterval);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                                notifyException(e);
                            }
                        }
                    }
                    catch (RemoteException e)
                    {
                        e.printStackTrace();
                        notifyException(e);
                    }

                }


            }
        });

        //start the receive thread
        keepReceiving = true;
        receiveThread.start();
    }

    /**
     * Listener to be notified on receipt of a screen capture
     */
    public interface ScreenReceivedListener
    {
        public void ScreenReceived(byte[] screen, BufferedImage screenImage);

    }

    /**
     *
     * @param listener Listener to be notified on receipt of screen capture
     */
    public synchronized void addScreenReceivedListener(ScreenReceivedListener listener)
    {
        screenReceivedListeners.add(listener);
    }

    /**
     *
     * @param listener Listener to be removed from list of notifiers of screen captures
     */
    public synchronized void removeScreenReceivedListener(ScreenReceivedListener listener)
    {
        screenReceivedListeners.remove(listener);
    }

    /**
     * Notify all listeners on receipt of screen capture
     * @param screen RawData received
     * @param screenImage Processed Image
     */
    private synchronized void notifyScreenReceived(byte[] screen, BufferedImage screenImage)
    {
        for(ScreenReceivedListener lister : screenReceivedListeners)
        {
            lister.ScreenReceived(screen,screenImage);
        }
    }

}
