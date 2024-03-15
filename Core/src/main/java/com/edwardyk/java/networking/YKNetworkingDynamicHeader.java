package com.edwardyk.java.networking;

import java.util.Map;

public interface YKNetworkingDynamicHeader {

    Map<String, String> dynamicHeader(YKNetworkingRequest request);
}
