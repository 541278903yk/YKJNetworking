package com.edwardyk.java.networking;

import java.io.IOException;

import okio.Buffer;
import okio.BufferedSink;
import okio.ForwardingSink;
import okio.Okio;

import okhttp3.*;

public class YKNetworkingProgressRequestBody extends RequestBody {

    private YKNetworkingProgressRequestBodyCallBack callBack = null;

    private RequestBody body = null;

    private YKNetworkingProgressRequestBody() {

    }

    public YKNetworkingProgressRequestBody(RequestBody body, YKNetworkingProgressRequestBodyCallBack callBack) {
        this.body = body;
        this.callBack = callBack;
    }

    public interface YKNetworkingProgressRequestBodyCallBack {

        void progress(long currentBytes,long totalBytes);
    }

    @Override
    public MediaType contentType() {
        return body.contentType();
    }

    @Override
    public long contentLength() throws IOException {
        return body.contentLength();
    }



    @Override
    public void writeTo(BufferedSink sink) throws IOException {

        BufferedSink bufferedSink = Okio.buffer(new ForwardingSink(sink) {

            private long currentBytes = 0L;
            private long totalBytes = 0L;
            @Override
            public void write(Buffer source, long byteCount) throws IOException {
                super.write(source, byteCount);

                if (totalBytes ==0) {
                    totalBytes = contentLength();
                }
                currentBytes += byteCount;
                callBack.progress(currentBytes,totalBytes);
            }
        });

        body.writeTo(bufferedSink);
        bufferedSink.flush();
    }
}
