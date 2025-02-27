package com.edwardyk.java.networking;

import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;

public class YKNetworking {

    private YKNetworkingRequest request;

    public String prefixUrl = null;

    public Map<String,YKNetworkingRequest> requestDictionary = new HashMap<>();

    public YKNetworkingHandleResponse handleResponse = null;

    public YKNetworkingDynamicParams dynamicParams = null;

    public YKNetworkingDynamicHeader dynamicHeader = null;

    public Map<String, String> commHeader = null;

    public Map<String, String> commParams = null;

    private Map<String, String> inputHeader = null;

    private Map<String, String> inputParams = null;

    public interface YKNetworkingDynamicHeader {

        Map<String, String> dynamicHeader(YKNetworkingRequest request);
    }

    public interface YKNetworkingDynamicParams {

        Map<String, String> dynamicParams(YKNetworkingRequest request);
    }

    public interface YKNetworkingHandleResponse {

        Error callBack(YKNetworkingRequest request, YKNetworkingResponse response);
    }

    public interface YKNetworkingProgress extends Cloneable {
        void progress(float progress);
    }

    private YKNetworkingRequest getRequest() {
        if (request == null){
            request = new YKNetworkingRequest();
            request.handleResponse = this.handleResponse;

            if (YKNetworkingConfig.getInstance().defaultHeader != null) {
                Iterator<String> it = YKNetworkingConfig.getInstance().defaultHeader.keySet().iterator();
                while (it.hasNext()){
                    String key = it.next();
                    request.header.put(key,YKNetworkingConfig.getInstance().defaultHeader.get(key));
                }
            }

            if (this.commHeader != null) {
                Iterator<String> it = this.commHeader.keySet().iterator();
                while (it.hasNext()){
                    String key = it.next();
                    request.header.put(key,this.commHeader.get(key));
                }
            }

            if (YKNetworkingConfig.getInstance().defaultParams != null) {
                Iterator<String> it = YKNetworkingConfig.getInstance().defaultParams.keySet().iterator();
                while (it.hasNext()){
                    String key = it.next();
                    request.params.put(key,YKNetworkingConfig.getInstance().defaultParams.get(key));
                }
            }

            if (this.commParams != null) {
                Iterator<String> it = this.commParams.keySet().iterator();
                while (it.hasNext()){
                    String key = it.next();
                    request.params.put(key,this.commParams.get(key));
                }
            }
        }
        return request;
    }

    public YKNetworking() {

    }

    public YKNetworking(String prefixUrl) {
        this.prefixUrl = prefixUrl;
    }

    public YKNetworking(String prefixUrl, Map<String, String> commHeader, Map<String, String> commParams) {
        this.prefixUrl = prefixUrl;
        this.commHeader = commHeader;
        this.commParams = commParams;
    }

    public YKNetworking(String prefixUrl,YKNetworkingHandleResponse handleResponse) {
        this.prefixUrl = prefixUrl;
        this.handleResponse = handleResponse;
    }

    public YKNetworking url(String url) {
        String urlStr;

        if (url.startsWith("http://") || url.startsWith("https://")){
            this.getRequest().urlStr = url;
            try {
                URL url1 = new URL(url);
                this.getRequest().path = url1.getPath();
            } catch (Exception exception) {

            }
            return this;
        }
        String prefix = "";
        if (this.prefixUrl != null){
            prefix = this.prefixUrl;
        }

        if (prefix == null || prefix.length() == 0){
            this.getRequest().urlStr = url;
            return this;
        }

        // 处理重复斜杠的问题
        String removeSlash = null;
        if (prefix.length() >0 && url.length() >0){
            String lastCharInPrefix = prefix.substring(prefix.length()-1);
            String firstCharInUrl = url.substring(0,1);
            if (lastCharInPrefix.equals("/") && firstCharInUrl.equals("/")){
                removeSlash = prefix.substring(0,prefix.length()-1);
            }
        }
        if (removeSlash != null){
            prefix = removeSlash;
        }

        urlStr = new StringBuffer().append(prefix).append(url).toString();


        try {
            URL url1 = new URL(urlStr);
            this.getRequest().path = url1.getPath();
        } catch (Exception exception) {

        }
        this.getRequest().urlStr = urlStr;



        return this;
    }

    public YKNetworking method(YKNetworkingRequestMethod method) {
        this.getRequest().method = method;
        return this;
    }

    public YKNetworking params(Map<String,String> params) {

        this.inputParams = params;
        if (params != null) {
            Iterator<String> it = params.keySet().iterator();
            while (it.hasNext()){
                String key = it.next();
                request.params.put(key,params.get(key));
            }
        }

        return this;
    }

    public YKNetworking header(Map<String,String> header) {
        this.inputHeader = header;
        if (header != null) {
            Iterator<String> it = header.keySet().iterator();
            while (it.hasNext()){
                String key = it.next();
                request.header.put(key,header.get(key));
            }
        }

        return this;
    }

    public YKNetworking disableDynamicParams() {
        this.getRequest().disableDynamicParams = true;
        return this;
    }

    public YKNetworking disableDynamicHeader() {
        this.getRequest().disableDynamicHeader = true;
        return this;
    }

    public YKNetworking disableHandleResponse() {
        this.getRequest().disableHandleResponse = true;
        return this;
    }

    /** 设置下载路径
     *  设置后默认调用下载方式
     *  **/
    public YKNetworking downloadDestPath(String destPath) {
        this.getRequest().destPath = destPath;
        this.getRequest().isDownload = true;
        return this;
    }

    public YKNetworking downloadDestPath(String destPath, String fileName) {
        this.getRequest().downFileName = fileName;
        return downloadDestPath(destPath);
    }

    public YKNetworking progress(YKNetworkingProgress progressCallBack) {
        this.getRequest().progressCallBack = progressCallBack;
        return this;
    }

    public YKNetworking mockData(Object mockData) {
        this.getRequest().mockData = mockData;
        return this;
    }

    public YKNetworking fileFieldName(String fileFieldName) {
        this.getRequest().fileFieldName = fileFieldName;
        return this;
    }
    
    /** 设置上传数据
     *  设置后默认调用上传方式
     *  **/
    public YKNetworking uploadData(byte[] data, String fileName, String mimeType, String formDataName) {
        this.getRequest().uploadFileData = data;
        this.getRequest().uploadName = fileName;
        this.getRequest().uploadMimeType = mimeType;
        this.getRequest().formDataName = formDataName;
        this.getRequest().isUpload = true;
        return this;
    }


    public YKNetworking GET(String url) {
        return this.method(YKNetworkingRequestMethod.YKNetworkingRequestMethodGET).url(url);
    }

    public YKNetworking POST(String url) {
        return this.method(YKNetworkingRequestMethod.YKNetworkingRequestMethodPOST).url(url);
    }

    public YKNetworking PATCH(String url){
        return this.method(YKNetworkingRequestMethod.YKNetworkingRequestMethodPATCH).url(url);
    }

    public void execute(YKNetworkingComplate callBack) {
        YKNetworking weakNetworking = this;
        try {
            YKNetworkingRequest requestCopy = (YKNetworkingRequest) weakNetworking.getRequest().clone();
            Boolean canContinue = weakNetworking.handleConfigWithRequest(requestCopy);
            if (!canContinue) {
                Error error = new Error("不允许请求");
                weakNetworking.saveTask(weakNetworking.request, new YKNetworkingResponse(), error);
                callBack.onComplate(new YKNetworkingRequest(), new YKNetworkingResponse(), error);
                return;
            }

            if (requestCopy.isUpload) {

                YKBaseNetworking.upload(requestCopy, new YKBaseNetworking.YKBaseNetworkingResult() {
                    @Override
                    public void success(YKNetworkingRequest request, YKNetworkingResponse response) {
                        weakNetworking.saveTask(request, response, null);
                        callBack.onComplate(request,response,null);
                    }

                    @Override
                    public void failure(YKNetworkingRequest request, Boolean isCache, Object responseObject, Error error) {
                        YKNetworkingResponse response = new YKNetworkingResponse();
                        weakNetworking.saveTask(request, response, error);
                        callBack.onComplate(request, response,error);
                    }
                });

            } else if (requestCopy.isDownload) {

                YKBaseNetworking.download(requestCopy, new YKBaseNetworking.YKBaseNetworkingResult() {
                    @Override
                    public void success(YKNetworkingRequest request, YKNetworkingResponse response) {
                        weakNetworking.saveTask(request, response, null);
                        callBack.onComplate(request,response,null);
                    }

                    @Override
                    public void failure(YKNetworkingRequest request, Boolean isCache, Object responseObject, Error error) {
                        YKNetworkingResponse response = new YKNetworkingResponse();
                        weakNetworking.saveTask(request, response, error);
                        callBack.onComplate(request, response,error);
                    }
                });
            } else {
                YKBaseNetworking.request(requestCopy, new YKBaseNetworking.YKBaseNetworkingResult() {
                    @Override
                    public void success(YKNetworkingRequest request, YKNetworkingResponse response) {
                        weakNetworking.saveTask(request, response, null);
                        callBack.onComplate(request,response,null);
                    }

                    @Override
                    public void failure(YKNetworkingRequest request, Boolean isCache, Object responseObject, Error error) {
                        YKNetworkingResponse response = new YKNetworkingResponse();
                        weakNetworking.saveTask(request, response, error);
                        callBack.onComplate(request, response,error);
                    }
                });
            }
        }catch (Exception exception) {
            callBack.onComplate(new YKNetworkingRequest(), new YKNetworkingResponse(), new Error(exception.getMessage()));
        }
        this.request = null;
    }


    private Boolean handleConfigWithRequest(YKNetworkingRequest request) throws CloneNotSupportedException {
        if (request.name == null || request.name.length() > 0) {
            request.name = UUID.randomUUID().toString();
        }

        try {

            YKNetworkingConfig config = YKNetworkingConfig.getInstance();

            //处理默认头部和默认参数
            if (!request.disableDynamicHeader && (this.dynamicHeader != null)) {
                Map<String, String> dynamicHeader = null;
                if (this.dynamicHeader != null) {
                    dynamicHeader = this.dynamicHeader.dynamicHeader(request);
                }

                if (dynamicHeader != null) {
                    Iterator<String> it = dynamicHeader.keySet().iterator();
                    while (it.hasNext()) {
                        String key = it.next();
                        if (this.inputHeader != null && !this.inputHeader.containsKey(key)) {
                            request.header.put(key,dynamicHeader.get(key));
                        }else  {
                            request.header.put(key,dynamicHeader.get(key));
                        }

                    }
                }
            }

            if (!request.disableDynamicParams && (this.dynamicParams != null)) {
                Map<String, String> dynamicParams = null;
                if (this.dynamicParams != null) {
                    dynamicParams = this.dynamicParams.dynamicParams(request);
                }

                if (dynamicParams != null) {
                    Iterator<String> it = dynamicParams.keySet().iterator();
                    while (it.hasNext()) {
                        String key = it.next();
                        if (this.inputParams != null && !this.inputParams.containsKey(key)) {
                            request.params.put(key,dynamicParams.get(key));
                        }else  {
                            request.params.put(key,dynamicParams.get(key));
                        }
                    }
                }
            }


            this.requestDictionary.put(request.name,request);

            return true;
        }catch (Exception ex) {
            throw ex;
        }
    }

    public void cancelAllRequest()
    {
        for (String name:this.requestDictionary.keySet()) {
            YKNetworkingRequest request = this.requestDictionary.get(name);
            if (request.call!=null) {
                request.call.cancel();
            }
        }
        this.requestDictionary.clear();
    }

    public void cancelRequestWithName(String name) {
        if (this.requestDictionary.keySet().contains(name)) {
            YKNetworkingRequest request = this.requestDictionary.get(name);
            if (request.call!=null) {
                request.call.cancel();
            }
            this.requestDictionary.remove(name);
        }
    }

    private void saveTask(YKNetworkingRequest request, YKNetworkingResponse response, Error error) {
        if (YKNetworkingConfig.getInstance().cacheRequest != null) {
            YKNetworkingConfig.getInstance().cacheRequest.cacheRequest(request, response, error);
        }
    }

}
