package eduze.livestream;

import eduze.livestream.exchange.client.FrameBuffer;

import javax.swing.*;
import java.util.ArrayList;

/**
 * Created by Madhawa on 31/03/2016.
 */
public abstract class AbstractReceiver {
    protected final FrameBuffer readBuffer; //buffer used to listen to inputs
    protected ArrayList<ExceptionListener> exceptionListeners; //list of exception listeners

    protected AbstractReceiver(FrameBuffer readBuffer)
    {
        this.readBuffer = readBuffer;
    }

    /**
     *
     * @return true if receiver is running. Otherwise return false.
     */
    public abstract boolean isReceiving();

    /**
     * Stops listening to frames through ReadBuffer
     */
    public abstract void stopReceiving();

    /**
     * Starts receiving of frames from ReadBuffer
     */
    public abstract void startReceiving();


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
