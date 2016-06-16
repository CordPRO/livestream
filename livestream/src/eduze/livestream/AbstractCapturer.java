package eduze.livestream;

/**
 * Created by Madhawa on 31/03/2016.
 */

import eduze.livestream.exchange.client.FrameBuffer;

import javax.swing.*;
import java.rmi.RemoteException;
import java.util.ArrayList;

/**
    Abstract parent class of AudioCapture and ScreenCapture
 */
public abstract class AbstractCapturer {
    protected final FrameBuffer writeBuffer; //frame buffer to write to
    protected ArrayList<ExceptionListener> exceptionListeners; //list of exception listeners

    protected AbstractCapturer(FrameBuffer writeBuffer)
    {
        exceptionListeners = new ArrayList<>();
        this.writeBuffer = writeBuffer;
    }

    /**
     *
     * @return FrameBuffer used to write the captured data
     */
    public synchronized FrameBuffer getWriteBuffer()
    {
        return writeBuffer;
    }

    /**
     *
     * @return true if capturing is running. Otherwise returns false
     */
    public abstract boolean isCapturing();

    /**
     * Begin Capture to provided writeBuffer
     */
    public abstract void startCapture() throws RemoteException;

    /**
     * stop Capturing to writeBuffer.
     */
    public abstract void stopCapture();

    /**
     * Notify the user on an exception
     * @param e Exception to be notified about
     */
    protected synchronized void notifyException(final Exception e)
    {

        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                for (ExceptionListener listener : exceptionListeners) {
                    listener.onException(e);
                }
            }
        });


    }

    /**
     * Registers an exception listener to be notified on exception
     * @param listener
     */
    public synchronized void addExceptionListener(ExceptionListener listener)
    {
        exceptionListeners.add(listener);
    }

    /**
     * Unregisters an already registered exception listener
     * @param listener
     */
    public synchronized void removeExceptionListener(ExceptionListener listener)
    {
        exceptionListeners.remove(listener);
    }



}
