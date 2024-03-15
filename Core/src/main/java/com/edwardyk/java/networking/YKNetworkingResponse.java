package com.edwardyk.java.networking;


public class YKNetworkingResponse implements Cloneable {

    public Object rawData = null;
    public Boolean isCache = false;
    public int code = 0;

    public YKNetworkingResponse() {
        rawData = null;
        isCache = false;
    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
        YKNetworkingResponse response = null;
        try {
            response = (YKNetworkingResponse)super.clone();
        }catch (CloneNotSupportedException exception) {
            exception.printStackTrace();
        }
        return response;
    }
}
