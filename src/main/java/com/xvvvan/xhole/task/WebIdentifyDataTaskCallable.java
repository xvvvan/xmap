//package com.xvvvan.xhole.engine.task;
//
//
//import com.google.common.hash.Hashing;
//import com.xvvvan.xhole.Tuple;
//import com.xvvvan.xhole.config.XholeConfig;
//import com.xvvvan.xhole.engine.XholeBanner;
//import com.xvvvan.xhole.engine.XholeEngine;
//import com.xvvvan.xhole.ui.data.WebIdentifyData;
//import java9.util.concurrent.CompletableFuture;
//import javafx.application.Platform;
//import javafx.collections.ObservableList;
//import javafx.scene.control.ProgressBar;
//
//import java.io.IOException;
//import java.net.InetAddress;
//import java.net.URI;
//import java.net.URISyntaxException;
//import javax.net.http.HttpClient;
//import javax.net.http.HttpHeaders;
//import javax.net.http.HttpRequest;
//import javax.net.http.HttpResponse;
//import java.nio.charset.StandardCharsets;
//import java.time.Duration;
//import java.util.*;
//import java.util.concurrent.Callable;
//import java.util.concurrent.Future;
//import java.util.concurrent.TimeUnit;
//import java.util.concurrent.atomic.AtomicInteger;
//import java.util.regex.Matcher;
//import java.util.regex.Pattern;
//
//import static com.xvvvan.xhole.ui.util.FileUtil.calculateMD5;
//
///**
// * 带返回结果的任务，返回一个Callable
// * @author xvvvan
// */
//public class WebIdentifyDataTaskCallable implements Callable<WebIdentifyData> {
//
//    private String url;
//    private String statusCode = "";
//    private StringBuilder headers = new StringBuilder();
//    private String server = "";
//    private String host = "";
//    private String length = "";
//    private String favicon = "";
//    private String md5 = "";
//    private String ip = "";
//    private int port = 0;
//    private HttpClient client;
//    private ObservableList<WebIdentifyData> list;
//    private AtomicInteger id ;
//    private ProgressBar progressBar;
//    private HttpRequest.Builder builder;
//    int taskNum;
//
//    public WebIdentifyDataTaskCallable() {}
//
//    /**
//     * 构造函数
//     * @param url 任务名称
//     */
//    public WebIdentifyDataTaskCallable(String url, AtomicInteger id , ObservableList<WebIdentifyData> list, ProgressBar progressBar,int taskNumber ) {
//        this.url = url;
//        this.client = XholeConfig.httpClient;
//        this.list = list;
//        this.id = id;
//        this.progressBar = progressBar;
//        this.taskNum = taskNumber;
//    }
//    public WebIdentifyDataTaskCallable (String url, HttpRequest.Builder builder,AtomicInteger id , ObservableList<WebIdentifyData> list) {
//
//        this.setUrl(url);
//        this.setClient(XholeConfig.httpClient);
//        this.setList(list);
//        this.setId(id);
//        this.setBuilder(builder);
//
//    }
//
//    @Override
//    public WebIdentifyData call() throws Exception {
//        URI uri = URI.create(url);
//        HttpRequest.Builder httpRequest = null;
//        if(builder!=null){
//            httpRequest = builder.uri(uri)
//                    .timeout(Duration.ofSeconds(XholeConfig.timeout));
//        }else {
//            httpRequest = HttpRequest.newBuilder()
//                    .uri(uri)
//                    .timeout(Duration.ofSeconds(XholeConfig.timeout))
//                    .header("rememberMe", "true");
//        }
//
//        try{
//            Map<String, Object> resMap = this.getMap(httpRequest, "",true);
//            List<Future<WebIdentifyData>> futures = new ArrayList<>();
//            for (XholeBanner xholeBanner : XholeEngine.xholeBanners) {
//
//                    futures.add(XholeEngine.virtualThreadPool.submit(new MatchTaskCallable(xholeBanner, resMap, id, list)));
//
//                if(xholeBanner.requests!=null){
//                    if(xholeBanner.path!=null){
//                        for (HttpRequest.Builder builder : xholeBanner.requests) {
//                            for (String path : xholeBanner.path) {
//                                if(path!=null&& !path.isEmpty()){
//                                    Map<String, Object> map = null;
//                                    map = this.getMap(builder, path,false);
//
//                                        futures.add(XholeEngine.virtualThreadPool.submit(new MatchTaskCallable(xholeBanner, map,id,list)));
//
//
//                                }
//                            }
//                        }
//                    }
//                }
//            }
//            WebIdentifyData webIdentifyData = new WebIdentifyData();
//            for (Future<WebIdentifyData> future : futures) {
//                WebIdentifyData webIdentifyData1 = future.get(1,TimeUnit.SECONDS);
//                webIdentifyData.update(webIdentifyData1);
//            }
//
//            Platform.runLater(() -> {
//                webIdentifyData.setId(String.valueOf(id.getAndIncrement()));
//                list.add(webIdentifyData);
//            });
//        }catch (Exception e){
//            XholeEngine.log.warning(url+" fail to get " + e.getMessage());
//        }finally {
//            if(progressBar!=null){
//                Platform.runLater(() -> progressBar.setProgress(progressBar.getProgress()+1.0 /taskNum));
//            }
//        }
//        return null;
//    }
//
//    private Map<String,Object> getMap(HttpRequest.Builder httpRequestBuilder,String path,boolean firstIcon) throws Exception {
//        String statusCode = "";
//        StringBuilder headers1 = new StringBuilder();
//        String server = "";
//        String length = "";
//
//        Map<String,Object> resMap = new HashMap<>();
//        URI uri = new URI(url + path);
//        HttpRequest httpRequest = httpRequestBuilder.uri(uri).build();
//        CompletableFuture<HttpResponse<byte[]>> httpResponseCompletableFuture = XholeConfig.httpClient.sendAsync(httpRequest, HttpResponse.BodyHandlers.ofByteArray());
//        HttpResponse<byte[]> response = httpResponseCompletableFuture.get(XholeConfig.timeout, TimeUnit.SECONDS);
//        if(firstIcon){
//            Tuple<String, String> imageFavicon = getImageFavicon(url);
//            favicon = imageFavicon.getFirst();
//            md5 = imageFavicon.getSecond();
//        }
//
//        String host = uri.getHost();
//        // 根据域名查找IP地址
//        InetAddress inetAddress = InetAddress.getByName(host);
//        // IP 地址
//        String ip = inetAddress.getHostAddress();
//        int port = uri.getPort();
//        if(port==-1){
//            if(uri.getScheme().equals("https")){
//                port = 443;
//            }
//            if(uri.getScheme().equals("http")){
//                port = 80;
//            }
//        }
//
//        HttpHeaders _headers = response.headers();
//        Map<String, List<String>> headers = _headers.map();
//        for (String key : headers.keySet()) {
//            List<String> strings = headers.get(key);
//            StringBuilder sb = new StringBuilder();
//            for (String string : strings) {
//                sb.append(string);
//            }
//            String value = sb.toString();
//            headers1.append(key).append(": ").append(value).append("\n");
//            if("Server".equals(key)){
//                server = value;
//            }
//            if("Content-Length".equals(key)){
//                length = value;
//            }
//        }
//        byte[] body = response.body();
//        if ("".equals(length)){
//            length = String.valueOf(body.length);
//        }
//        statusCode = String.valueOf(response.statusCode());
//        resMap.put("md5Body",getMd5(body));
//        resMap.put("icon",favicon);
//        resMap.put("ico",md5);
//        resMap.put("url",url);
//        resMap.put("header", headers1.toString());
//        resMap.put("server",server);
//        resMap.put("status",statusCode);
//        resMap.put("body",body);
//        resMap.put("length",length);
//        resMap.put("ip",ip);
//        resMap.put("port",String.valueOf(port));
//        if(body!=null){
//            Pattern compile = Pattern.compile("<title>.*</title>");
//            Matcher matcher = compile.matcher(new String(body));
//            if(matcher.find()){
//                String title = matcher.group(0);
//                String substring = title.substring(7, title.length()-8);
//                resMap.put("title",substring);
//            }
//        }
//        return resMap;
//    }
//    private Tuple<String,String> getImageFavicon(String url) {
//        try {
//            URI uri = new URI(url + "/favicon.ico");
//            HttpRequest req = HttpRequest.newBuilder(uri).build();
//            HttpResponse<byte[]> send = client.send(req, HttpResponse.BodyHandlers.ofByteArray());
//            byte[] body = send.body();
//            String s = Base64.getMimeEncoder().encodeToString(body);
//            return new Tuple<>(getIconHash(s),getMd5(body));
//        } catch (URISyntaxException | IOException | InterruptedException e) {
//            return new Tuple<>("cant get","cant get");
//        }
//    }
//    private String getMd5(byte[] body){
//        return calculateMD5(body);
//    }
//
//    /**
//     * 获取任务名称
//     * @return 任务名称
//     */
//    public String getUrl() {
//        return url;
//    }
//    @Override
//    public String toString() {
//        return Thread.currentThread().getName()+"MyCallable [ name = " + url + " ]";
//    }
//
//    /**
//     * 计算favicon hash
//     *
//     * @param f favicon的文件对象
//     * @return favicon hash值
//     */
//    public static String getIconHash(String f) {
//        int murmu = Hashing
//                .murmur3_32()
//                .hashString(f.replaceAll("\r","" )+"\n", StandardCharsets.UTF_8)
//                .asInt();
//        return String.valueOf(murmu);
//    }
//
//    public void setUrl(String url) {
//        this.url = url;
//    }
//
//    public void setStatusCode(String statusCode) {
//        this.statusCode = statusCode;
//    }
//
//    public void setHeaders(StringBuilder headers) {
//        this.headers = headers;
//    }
//
//    public void setServer(String server) {
//        this.server = server;
//    }
//
//    public void setLength(String length) {
//        this.length = length;
//    }
//
//    public void setClient(HttpClient client) {
//        this.client = client;
//    }
//
//    public void setList(ObservableList<WebIdentifyData> list) {
//        this.list = list;
//    }
//
//    public void setId(AtomicInteger id) {
//        this.id = id;
//    }
//
//    public void setProgressBar(ProgressBar progressBar) {
//        this.progressBar = progressBar;
//    }
//
//    public void setBuilder(HttpRequest.Builder builder) {
//        this.builder = builder;
//    }
//
//    public void setTaskNum(int taskNum) {
//        this.taskNum = taskNum;
//    }
//}
