package com.edwardyk.java.networking;

import java.util.Map;

public interface YKNetworkingDynamicParams {

    Map<String, String> dynamicParams(YKNetworkingRequest request);
}
