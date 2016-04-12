/**
 * FramePullResult.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package eduze.livestream.exchange.client;

public class FramePullResult  implements java.io.Serializable {
    private boolean bufferOverRun;

    private byte[][] data;

    private int nextFrameId;

    private int segmentIndex;

    public FramePullResult() {
    }

    public FramePullResult(
           boolean bufferOverRun,
           byte[][] data,
           int nextFrameId,
           int segmentIndex) {
           this.bufferOverRun = bufferOverRun;
           this.data = data;
           this.nextFrameId = nextFrameId;
           this.segmentIndex = segmentIndex;
    }


    /**
     * Gets the bufferOverRun value for this FramePullResult.
     * 
     * @return bufferOverRun
     */
    public boolean isBufferOverRun() {
        return bufferOverRun;
    }


    /**
     * Sets the bufferOverRun value for this FramePullResult.
     * 
     * @param bufferOverRun
     */
    public void setBufferOverRun(boolean bufferOverRun) {
        this.bufferOverRun = bufferOverRun;
    }


    /**
     * Gets the data value for this FramePullResult.
     * 
     * @return data
     */
    public byte[][] getData() {
        return data;
    }


    /**
     * Sets the data value for this FramePullResult.
     * 
     * @param data
     */
    public void setData(byte[][] data) {
        this.data = data;
    }

    public byte[] getData(int i) {
        return this.data[i];
    }

    public void setData(int i, byte[] _value) {
        this.data[i] = _value;
    }


    /**
     * Gets the nextFrameId value for this FramePullResult.
     * 
     * @return nextFrameId
     */
    public int getNextFrameId() {
        return nextFrameId;
    }


    /**
     * Sets the nextFrameId value for this FramePullResult.
     * 
     * @param nextFrameId
     */
    public void setNextFrameId(int nextFrameId) {
        this.nextFrameId = nextFrameId;
    }


    /**
     * Gets the segmentIndex value for this FramePullResult.
     * 
     * @return segmentIndex
     */
    public int getSegmentIndex() {
        return segmentIndex;
    }


    /**
     * Sets the segmentIndex value for this FramePullResult.
     * 
     * @param segmentIndex
     */
    public void setSegmentIndex(int segmentIndex) {
        this.segmentIndex = segmentIndex;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof FramePullResult)) return false;
        FramePullResult other = (FramePullResult) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            this.bufferOverRun == other.isBufferOverRun() &&
            ((this.data==null && other.getData()==null) || 
             (this.data!=null &&
              java.util.Arrays.equals(this.data, other.getData()))) &&
            this.nextFrameId == other.getNextFrameId() &&
            this.segmentIndex == other.getSegmentIndex();
        __equalsCalc = null;
        return _equals;
    }

    private boolean __hashCodeCalc = false;
    public synchronized int hashCode() {
        if (__hashCodeCalc) {
            return 0;
        }
        __hashCodeCalc = true;
        int _hashCode = 1;
        _hashCode += (isBufferOverRun() ? Boolean.TRUE : Boolean.FALSE).hashCode();
        if (getData() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getData());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getData(), i);
                if (obj != null &&
                    !obj.getClass().isArray()) {
                    _hashCode += obj.hashCode();
                }
            }
        }
        _hashCode += getNextFrameId();
        _hashCode += getSegmentIndex();
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(FramePullResult.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://server.exchange.livestream.eduze/", "FramePullResult"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("bufferOverRun");
        elemField.setXmlName(new javax.xml.namespace.QName("", "bufferOverRun"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "boolean"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("data");
        elemField.setXmlName(new javax.xml.namespace.QName("", "data"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "base64Binary"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        elemField.setMaxOccursUnbounded(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("nextFrameId");
        elemField.setXmlName(new javax.xml.namespace.QName("", "nextFrameId"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("segmentIndex");
        elemField.setXmlName(new javax.xml.namespace.QName("", "segmentIndex"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
    }

    /**
     * Return type metadata object
     */
    public static org.apache.axis.description.TypeDesc getTypeDesc() {
        return typeDesc;
    }

    /**
     * Get Custom Serializer
     */
    public static org.apache.axis.encoding.Serializer getSerializer(
           java.lang.String mechType, 
           java.lang.Class _javaType,  
           javax.xml.namespace.QName _xmlType) {
        return 
          new  org.apache.axis.encoding.ser.BeanSerializer(
            _javaType, _xmlType, typeDesc);
    }

    /**
     * Get Custom Deserializer
     */
    public static org.apache.axis.encoding.Deserializer getDeserializer(
           java.lang.String mechType, 
           java.lang.Class _javaType,  
           javax.xml.namespace.QName _xmlType) {
        return 
          new  org.apache.axis.encoding.ser.BeanDeserializer(
            _javaType, _xmlType, typeDesc);
    }

}
