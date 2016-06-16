package eduze.livestream;

import eduze.livestream.exchange.client.FrameBuffer;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.rmi.RemoteException;


/**
 * Created by Fujitsu on 3/28/2016.
 */

/**
 * Repeatedly take screen captures at regularly intervals and push to FrameBuffer
 */
public class ScreenCapturer extends AbstractCapturer {

    private String imageEncoderFormat = "png";
    private int captureInterval; //time-gap between two captures
    private boolean keepCapturing = false; //is capturing running

    private Thread captureThread = null; //thread used for screen capturing.

    /**
     * 
     * @param buffer FrameBuffer to write the captured frames
     * @param interval Interval of capturing
     */
    public ScreenCapturer(FrameBuffer buffer, int interval)
    {
        super(buffer);
        this.setCaptureInterval(interval);
    }

    /**
     *
     * @return true if screen capturing is running. Otherwise returns false
     */
    @Override
    public synchronized boolean isCapturing()
    {
        return keepCapturing;
    }

    /**
     * Begin Screen Capturing to FrameBuffer writeBuffer
     */
    @Override
    public synchronized void startCapture() throws RemoteException {
        //ignore if capturing is already running
        if(isCapturing())
            return;

        //setup thread for screen capture
        captureThread = new Thread(new Runnable() {

            @Override
            public void run() {
                while(isCapturing())
                {
                    try {
                        //Take screen-captures in UI Thread since we are dealing with UI
                        SwingUtilities.invokeAndWait(new Runnable() {
                            @Override
                            public void run() {
                                //Setup buffers
                                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                                BufferedImage image = null;

                                try {
                                    //Take screen capture
                                    image = new Robot().createScreenCapture(new Rectangle(Toolkit.getDefaultToolkit().getScreenSize()));
                                    ImageIO.write(image, getImageEncoderFormat(), outputStream);
                                    //push the frame to buffer
                                    writeBuffer.pushFrame(outputStream.toByteArray());
                                } catch (AWTException e) {
                                    e.printStackTrace();
                                    notifyException(e);
                                }
                                catch (RemoteException e)
                                {
                                    notifyException(e);
                                    e.printStackTrace();
                                }
                                catch (IOException e) {
                                    notifyException(e);
                                    e.printStackTrace();
                                }
                                finally {
                                    try {
                                        if(outputStream != null)
                                            outputStream.close();
                                    } catch (IOException e) {
                                        notifyException(e);
                                        e.printStackTrace();
                                    }
                                }


                            }
                        });
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } catch (InvocationTargetException e) {
                        e.printStackTrace();
                        notifyException(e);
                    }
                    try {
                        //wait for inter-capture time interval
                        Thread.currentThread().sleep(getCaptureInterval());
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                        notifyException(e);
                    }
                }

            }
        });

        //start the thread
        keepCapturing = true;
        writeBuffer.startNewSegment();
        captureThread.start();
    }

    /**
     * stop Screen Capturing
     */
    @Override
    public synchronized void stopCapture()
    {
        keepCapturing = false;
    }

    /**
     *
     * @return String representation of encoded image format. eg - png
     */
    public synchronized String getImageEncoderFormat() {
        return imageEncoderFormat;
    }

    /**
     *
     * @param imageEncoderFormat String representation of encoded image format. eg - png
     * @throws IllegalStateException Thrown if capturing has already started
     */
    public synchronized void setImageEncoderFormat(String imageEncoderFormat) throws  IllegalStateException{
        if(isCapturing())
            throw new IllegalStateException("Capturing has already begun");
        this.imageEncoderFormat = imageEncoderFormat;
    }

    /**
     *
     * @return Inter frame capture time interval
     */
    public synchronized int getCaptureInterval() {
        return captureInterval;
    }

    /**
     *
     * @param captureInterval Inter frame capture time interval
     */
    public synchronized void setCaptureInterval(int captureInterval) {
        this.captureInterval = captureInterval;
    }
}
