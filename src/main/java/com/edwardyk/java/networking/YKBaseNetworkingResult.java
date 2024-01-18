package com.edwardyk.java.networking;

interface YKBaseNetworkingResult
{
    void success(YKNetworkingRequest request, YKNetworkingResponse response);

    void failure(YKNetworkingRequest request, Boolean isCache, Object responseObject, Error error);
}
