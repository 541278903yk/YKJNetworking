package com.edwardyk.java.networking;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import okhttp3.Call;

public class YKNetworkingRequest implements Cloneable  {

    public String name = null;

    public String urlStr = "";

    public String path = "";

    public YKNetworkingRequestMethod method = YKNetworkingRequestMethod.YKNetworkingRequestMethodGET;

    private String _methodStr = "GET";

    public String getMethodStr() {
        switch (method){
            case YKNetworkingRequestMethodGET:
                _methodStr = "GET";
                break;
            case YKNetworkingRequestMethodPOST:
                _methodStr = "POST";
                break;
            case YKNetworkingRequestMethodPUT:
                _methodStr = "PUT";
                break;
            case YKNetworkingRequestMethodPATCH:
                _methodStr = "PATCH";
                break;
            case YKNetworkingRequestMethodDELETE:
                _methodStr = "DELETE";
                break;
        }
        return _methodStr;
    }

    public Map<String,String> params = new HashMap<>();

    public Map<String,String> header = new HashMap<>();

    public boolean disableDynamicParams = false;

    public boolean disableDynamicHeader = false;

    public boolean disableHandleResponse = false;

    public Object mockData = null;

    public YKNetworking.YKNetworkingProgress progressCallBack = null;

    public YKNetworking.YKNetworkingHandleResponse handleResponse = null;

    public float repeatRequestInterval = 0;

    public String destPath = null;

    public String downFileName = null;

    public byte[] uploadFileData = null;

    public String uploadName = null;

    public String uploadMimeType = null;

    public String fileFieldName = null;

    public String formDataName = null;

    public boolean isUpload = false;

    public boolean isDownload = false;

    public Call call = null;

    public void setParams(Map<String, String> params) {
        this.params = params;
    }

    public void setHeader(Map<String, String> header) {
        this.header = header;
    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
        YKNetworkingRequest newRequest = null;
        try {
            newRequest = (YKNetworkingRequest) super.clone();
        }catch (CloneNotSupportedException exception) {
            exception.printStackTrace();
            throw exception;
        }
        newRequest.params = new HashMap<>();
        newRequest.header = new HashMap<>();
        newRequest.handleResponse = this.handleResponse;
        if (this.params != null) {
            Iterator<String> it = this.params.keySet().iterator();
            while (it.hasNext()) {
                String key = it.next();
                newRequest.params.put(key,this.params.get(key));
            }
        }
        if (this.header != null) {
            Iterator<String> it = this.header.keySet().iterator();
            while (it.hasNext()) {
                String key = it.next();
                newRequest.header.put(key,this.header.get(key));
            }
        }
        return newRequest;
    }
}
