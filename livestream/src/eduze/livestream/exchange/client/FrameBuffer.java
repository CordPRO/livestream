/**
 * FrameBuffer.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package eduze.livestream.exchange.client;

public interface FrameBuffer extends java.rmi.Remote {
    public void startNewSegment() throws java.rmi.RemoteException;
    public int pushFrame(byte[] arg0) throws java.rmi.RemoteException;
    public eduze.livestream.exchange.client.FramePullResult pullFrames(int arg0, int arg1) throws java.rmi.RemoteException;
    public int getSegmentID() throws java.rmi.RemoteException;
}
