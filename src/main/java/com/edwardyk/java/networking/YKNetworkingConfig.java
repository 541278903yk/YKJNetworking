package com.edwardyk.java.networking;

import java.util.Map;

public class YKNetworkingConfig {

    public interface YKNetworkingDownloadConfigCallBack {

        Object onExecuteData(YKNetworkingRequest request, byte[] datas);
    }

    public interface YKNetworkingConfigCacheRequest {

        void cacheRequest(YKNetworkingRequest request, YKNetworkingResponse response, Error error);
    }


    private static YKNetworkingConfig _instance;

    public long timeoutInterval = 60;

    public Map<String, String> defaultHeader = null;

    public Map<String, String> defaultParams = null;

    public YKNetworkingConfigCacheRequest cacheRequest = null;

    public YKNetworkingDownloadConfigCallBack downloadConfigCallBack = null;

    public static YKNetworkingConfig getInstance(){
        if(_instance == null) {
            _instance = new YKNetworkingConfig();
        }
        return _instance;
    }
    private YKNetworkingConfig() {

    }
}
