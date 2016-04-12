/**
 * FrameBufferImplServiceLocator.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package eduze.livestream.exchange.client;

public class FrameBufferImplServiceLocator extends org.apache.axis.client.Service implements eduze.livestream.exchange.client.FrameBufferImplService {

    public FrameBufferImplServiceLocator() {
    }


    public FrameBufferImplServiceLocator(org.apache.axis.EngineConfiguration config) {
        super(config);
    }

    public FrameBufferImplServiceLocator(java.lang.String wsdlLoc, javax.xml.namespace.QName sName) throws javax.xml.rpc.ServiceException {
        super(wsdlLoc, sName);
    }

    // Use to get a proxy class for FrameBufferImplPort
    private java.lang.String FrameBufferImplPort_address = "http://localhost:8000/audiorelay";

    public java.lang.String getFrameBufferImplPortAddress() {
        return FrameBufferImplPort_address;
    }

    // The WSDD service name defaults to the port name.
    private java.lang.String FrameBufferImplPortWSDDServiceName = "FrameBufferImplPort";

    public java.lang.String getFrameBufferImplPortWSDDServiceName() {
        return FrameBufferImplPortWSDDServiceName;
    }

    public void setFrameBufferImplPortWSDDServiceName(java.lang.String name) {
        FrameBufferImplPortWSDDServiceName = name;
    }

    public eduze.livestream.exchange.client.FrameBuffer getFrameBufferImplPort() throws javax.xml.rpc.ServiceException {
       java.net.URL endpoint;
        try {
            endpoint = new java.net.URL(FrameBufferImplPort_address);
        }
        catch (java.net.MalformedURLException e) {
            throw new javax.xml.rpc.ServiceException(e);
        }
        return getFrameBufferImplPort(endpoint);
    }

    public eduze.livestream.exchange.client.FrameBuffer getFrameBufferImplPort(java.net.URL portAddress) throws javax.xml.rpc.ServiceException {
        try {
            eduze.livestream.exchange.client.FrameBufferImplPortBindingStub _stub = new eduze.livestream.exchange.client.FrameBufferImplPortBindingStub(portAddress, this);
            _stub.setPortName(getFrameBufferImplPortWSDDServiceName());
            return _stub;
        }
        catch (org.apache.axis.AxisFault e) {
            return null;
        }
    }

    public void setFrameBufferImplPortEndpointAddress(java.lang.String address) {
        FrameBufferImplPort_address = address;
    }

    /**
     * For the given interface, get the stub implementation.
     * If this service has no port for the given interface,
     * then ServiceException is thrown.
     */
    public java.rmi.Remote getPort(Class serviceEndpointInterface) throws javax.xml.rpc.ServiceException {
        try {
            if (eduze.livestream.exchange.client.FrameBuffer.class.isAssignableFrom(serviceEndpointInterface)) {
                eduze.livestream.exchange.client.FrameBufferImplPortBindingStub _stub = new eduze.livestream.exchange.client.FrameBufferImplPortBindingStub(new java.net.URL(FrameBufferImplPort_address), this);
                _stub.setPortName(getFrameBufferImplPortWSDDServiceName());
                return _stub;
            }
        }
        catch (java.lang.Throwable t) {
            throw new javax.xml.rpc.ServiceException(t);
        }
        throw new javax.xml.rpc.ServiceException("There is no stub implementation for the interface:  " + (serviceEndpointInterface == null ? "null" : serviceEndpointInterface.getName()));
    }

    /**
     * For the given interface, get the stub implementation.
     * If this service has no port for the given interface,
     * then ServiceException is thrown.
     */
    public java.rmi.Remote getPort(javax.xml.namespace.QName portName, Class serviceEndpointInterface) throws javax.xml.rpc.ServiceException {
        if (portName == null) {
            return getPort(serviceEndpointInterface);
        }
        java.lang.String inputPortName = portName.getLocalPart();
        if ("FrameBufferImplPort".equals(inputPortName)) {
            return getFrameBufferImplPort();
        }
        else  {
            java.rmi.Remote _stub = getPort(serviceEndpointInterface);
            ((org.apache.axis.client.Stub) _stub).setPortName(portName);
            return _stub;
        }
    }

    public javax.xml.namespace.QName getServiceName() {
        return new javax.xml.namespace.QName("http://server.exchange.livestream.eduze/", "FrameBufferImplService");
    }

    private java.util.HashSet ports = null;

    public java.util.Iterator getPorts() {
        if (ports == null) {
            ports = new java.util.HashSet();
            ports.add(new javax.xml.namespace.QName("http://server.exchange.livestream.eduze/", "FrameBufferImplPort"));
        }
        return ports.iterator();
    }

    /**
    * Set the endpoint address for the specified port name.
    */
    public void setEndpointAddress(java.lang.String portName, java.lang.String address) throws javax.xml.rpc.ServiceException {
        
if ("FrameBufferImplPort".equals(portName)) {
            setFrameBufferImplPortEndpointAddress(address);
        }
        else 
{ // Unknown Port Name
            throw new javax.xml.rpc.ServiceException(" Cannot set Endpoint Address for Unknown Port" + portName);
        }
    }

    /**
    * Set the endpoint address for the specified port name.
    */
    public void setEndpointAddress(javax.xml.namespace.QName portName, java.lang.String address) throws javax.xml.rpc.ServiceException {
        setEndpointAddress(portName.getLocalPart(), address);
    }

}
