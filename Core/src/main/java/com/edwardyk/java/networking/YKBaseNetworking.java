package com.edwardyk.java.networking;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.*;

class YKBaseNetworking {

    static void request(YKNetworkingRequest request, YKBaseNetworkingResult result) {
        try{

            YKBaseNetworking.configWithRequest(request);

            if (request.progressCallBack != null) {
                request.progressCallBack.progress(0);
            }

            if (request.mockData != null) {
                YKNetworkingResponse response = new YKNetworkingResponse();

                response.rawData = request.mockData;
                response.code = 200;
                if (request.progressCallBack != null) {
                    request.progressCallBack.progress(1);
                }
                result.success(request,response);
                return;
            }

            Call call = YKBaseNetworking.createRequestCall(request);

            call.enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    String errorMessage = e.getMessage();
                    if (request.progressCallBack != null) {
                        request.progressCallBack.progress(1);
                    }
                    result.failure(request,false,errorMessage,new Error(errorMessage));
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    YKNetworkingResponse ykresponse = new YKNetworkingResponse();
                    String bodyStr = response.body().string();


                    ykresponse.rawData = bodyStr;
                    ykresponse.code = response.code();

                    if (request.progressCallBack != null) {
                        request.progressCallBack.progress(1);
                    }

                    if ((!request.disableHandleResponse) && request.handleResponse != null) {
                        Error error = request.handleResponse.callBack(request,ykresponse);

                        if (error == null) {
                            result.success(request,ykresponse);
                        } else {
                            result.failure(request,true,null,error);
                        }
                    } else {
                        result.success(request,ykresponse);
                    }
                }
            });
            request.call = call;

        }catch (Exception ex){
            result.failure(request,false,ex.getMessage(),new Error(ex.getMessage()));
        }
    }

    static void upload(YKNetworkingRequest request, YKBaseNetworkingResult result) {
        try {
            YKBaseNetworking.configWithRequest(request);

            if (request.uploadMimeType == null || request.uploadFileData.length <= 0 || request.uploadName == null || request.formDataName == null) {

                result.failure(request,false,"上传内容错误",new Error("上传内容错误"));
                return;
            }

            Call call = YKBaseNetworking.createUploadCall(request);
            call.enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    String errorMessage = e.getMessage();
                    result.failure(request,false,errorMessage,new Error(errorMessage));
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    YKNetworkingResponse ykresponse = new YKNetworkingResponse();
                    String bodyStr = response.body().string();

                    ykresponse.rawData = bodyStr;
                    ykresponse.code = response.code();

                    if (!request.disableHandleResponse && (request.handleResponse != null)) {
                        Error error = request.handleResponse.callBack(request,ykresponse);
                        if (error == null) {
                            result.success(request,ykresponse);
                        } else  {
                            result.failure(request,true,null,error);
                        }
                    } else {
                        result.success(request,ykresponse);
                    }
                }
            });

            request.call = call;

        }catch (Exception exception) {
            result.failure(request,false,exception.getMessage(),new Error(exception.getMessage()));
        }
    }


    static void download(YKNetworkingRequest request, YKBaseNetworkingResult result) {
        try {
            YKBaseNetworking.configWithRequest(request);

            if (request.destPath == null || request.destPath.length() == 0) {

                result.failure(request,false,"下载内容错误",new Error("下载内容错误"));
                return;
            }

            Call call = YKBaseNetworking.createDownloadCall(request);
            call.enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    String errorMessage = e.getMessage();
                    result.failure(request,false,errorMessage,new Error(errorMessage));
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    YKNetworkingResponse ykresponse = new YKNetworkingResponse();
                    byte[] datas = response.body().bytes();

                    ykresponse.code = response.code();
                    if (YKNetworkingConfig.getInstance().downloadConfigCallBack != null) {
                        ykresponse.rawData = YKNetworkingConfig.getInstance().downloadConfigCallBack.onExecuteData(request,datas);
                    } else {
                        ykresponse.rawData = datas;
                    }
                    result.success(request,ykresponse);
                }
            });

            request.call = call;

        }catch (Exception exception) {
            result.failure(request,false,exception.getMessage(),new Error(exception.getMessage()));
        }
    }


    private static Call createRequestCall(YKNetworkingRequest request) {

        FormBody body = null;
        String url = request.urlStr;
        if (request.params != null && request.params.size() > 0) {
            if (request.method != YKNetworkingRequestMethod.YKNetworkingRequestMethodGET) {
                FormBody.Builder builder = new FormBody.Builder();
                for (String key : request.params.keySet()) {
                    builder.add(key, request.params.get(key));
                }

                body = builder.build();
            }else  {
                StringBuilder stringBuffer = new StringBuilder();
                stringBuffer.append(url).append("?");
                boolean isFirst = true;
                for (String key:request.params.keySet()) {
                    if (isFirst){
                        isFirst = false;
                    }else {
                        stringBuffer.append("&");
                    }
                    stringBuffer.append(key);
                    stringBuffer.append("=");
                    stringBuffer.append(request.params.get(key));
                }
                url = stringBuffer.toString();
            }
        }

        long timeout = 60000;
        if (YKNetworkingConfig.getInstance().timeoutInterval != 60 && YKNetworkingConfig.getInstance().timeoutInterval > 0){
            timeout = YKNetworkingConfig.getInstance().timeoutInterval * 1000;

        }

        OkHttpClient okHttpClient = new OkHttpClient().newBuilder().connectTimeout(timeout, TimeUnit.MILLISECONDS)
                .readTimeout(timeout, TimeUnit.MILLISECONDS)
                .build();


        Request.Builder requestbuilder = new Request.Builder().url(url).method(request.getMethodStr(),body);

        if (request.header != null && request.header.size() > 0){
            for (String key:request.header.keySet()) {
                requestbuilder.addHeader(key,request.header.get(key));
            }
        }
        return okHttpClient.newCall(requestbuilder.build());
    }

    private static Call createUploadCall(YKNetworkingRequest request) {

        MultipartBody.Builder builder = new MultipartBody.Builder().setType(MultipartBody.FORM);

        if (request.params != null && request.params.size() > 0) {
            for (String key: request.params.keySet()) {
                builder.addFormDataPart(key,request.params.get(key));
            }
        }

        RequestBody dataBody = RequestBody.create(MediaType.parse(request.uploadMimeType), request.uploadFileData);
        if (request.progressCallBack != null) {
            YKNetworkingProgressRequestBody newBody = new YKNetworkingProgressRequestBody(dataBody, new YKNetworkingProgressRequestBody.YKNetworkingProgressRequestBodyCallBack() {
                @Override
                public void progress(long didFinish, long total) {
                    request.progressCallBack.progress((float) didFinish / (float) total);
                }
            });
            builder.addFormDataPart(request.formDataName,request.uploadName,newBody);
        } else {
            builder.addFormDataPart(request.formDataName,request.uploadName,dataBody);
        }

        OkHttpClient okHttpClient = new OkHttpClient();

        Request.Builder requestbuilder = new Request.Builder().url(request.urlStr).method(request.getMethodStr(),builder.build());

        if (request.header != null && request.header.size() > 0){
            for (String key:request.header.keySet()) {
                requestbuilder.addHeader(key,request.header.get(key));
            }
        }

        return okHttpClient.newCall(requestbuilder.build());
    }

    private static Call createDownloadCall(YKNetworkingRequest request) {

        OkHttpClient okHttpClient = new OkHttpClient();

        Request.Builder requestbuilder = new Request.Builder().url(request.urlStr).method(request.getMethodStr(),null);

        if (request.header != null && request.header.size() > 0){
            for (String key:request.header.keySet()) {
                requestbuilder.addHeader(key,request.header.get(key));
            }
        }
        return okHttpClient.newCall(requestbuilder.build());
    }

    private static void configWithRequest(YKNetworkingRequest request) {

    }
}
