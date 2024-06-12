package com.xvvvan.global;

import com.google.common.hash.Hashing;
import com.xvvvan.assertmap.controller.AssertMapController;
import com.xvvvan.xhole.Tuple;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.io.IOException;

import java.net.InetSocketAddress;
import java.net.ProxySelector;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpHeaders;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.time.Duration;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import static com.xvvvan.util.FileUtil.calculateMD5;

/**
 * 代理
 * 超时
 * 并发
 *
 */
public class Http {
    public static SimpleStringProperty httpProxy = new SimpleStringProperty("");
    public static SimpleStringProperty socksProxy = new SimpleStringProperty("");

    public static SimpleIntegerProperty timeout = new SimpleIntegerProperty(6);
    public static HttpClient httpClient;

    public static HttpRequest.Builder httpBuilder;
    public static Logger logger = LoggerFactory.getLogger(Http.class);

    static {

        HttpClient.Builder builder = null;
        try {
            builder = HttpClient.newBuilder()
                    .followRedirects(HttpClient.Redirect.ALWAYS)
                    .sslContext(sslContext());


        } catch (NoSuchAlgorithmException | KeyManagementException e) {
            throw new RuntimeException(e);
        }
        if(!Objects.equals(httpProxy.get(), "")){
            String[] split = httpProxy.get().split(":");
            builder.proxy(ProxySelector.of(new InetSocketAddress(split[0], Integer.parseInt(split[1]))));

        }
        if(!Objects.equals(socksProxy.get(), "")){
            String[] split = socksProxy.get().split(":");
            builder.proxy(ProxySelector.of(new InetSocketAddress(split[0], Integer.parseInt(split[1]))));
        }
        httpClient = builder.build();
        httpBuilder = HttpRequest.newBuilder()
        .timeout(Duration.ofSeconds(timeout.get()))
                .header("rememberMe", "true");

    }
    private static SSLContext sslContext() throws NoSuchAlgorithmException, KeyManagementException {
        SSLContext sslContext = SSLContext.getInstance("TLS");
        sslContext.init(null, trustManager(), new SecureRandom());

        // 创建所有主机名都验证的主机名验证器
//        HostnameVerifier allHostsValid = (hostname, session) -> true;
//
//        // 安装所有主机名都验证的主机名验证器
//        HttpsURLConnection.setDefaultHostnameVerifier(allHostsValid);
////        httpClient.
//        // 创建所有协议的套接字工厂
//        HttpsURLConnection.setDefaultSSLSocketFactory(sslContext.getSocketFactory());
        return sslContext;
    }

    private static TrustManager[] trustManager() {
        return new TrustManager[]{
                new X509TrustManager() {
                    public X509Certificate[] getAcceptedIssuers() {
                        return null;
                    }

                    public void checkClientTrusted(X509Certificate[] certs, String authType) {
                    }

                    public void checkServerTrusted(X509Certificate[] certs, String authType) {
                    }

                }
        };
    }

    public int getTimeout() {
        return timeout.get();
    }


    public static HttpResponse<byte[]> get (String url){
        try {
            HttpRequest.Builder builder = HttpRequest.newBuilder(new URI(url));
            builder.timeout(Duration.ofSeconds(5000));
            HttpRequest get = builder.GET().build();
            return httpClient.send(get, HttpResponse.BodyHandlers.ofByteArray());
        } catch (Exception e) {
            AssertMapController.INSTANCE.log(e.getMessage());
        }
        return null;
    }
    public static Map<String,Object> request(HttpRequest req,boolean first){
        Map<String,Object> resMap = new HashMap<>();
        URI uri = req.uri();
        StringBuilder headers1 = new StringBuilder();
        String server = "";
        String length = "";
        try {
            HttpResponse<byte[]> response = httpClient.send(req, HttpResponse.BodyHandlers.ofByteArray());

            if(first){
                Tuple<String, String> imageFavicon = getImageFavicon(uri);
                resMap.put("icon",imageFavicon.getFirst());
                resMap.put("ico",imageFavicon.getSecond());
            }

            String ip = uri.getHost();
            // 根据域名查找IP地址
//        InetAddress inetAddress = InetAddress.getByName(host);
            // IP 地址
//        String ip = host;
            int port = uri.getPort();
            if(port==-1){
                if(uri.getScheme().equals("https")){
                    port = 443;
                }
                if(uri.getScheme().equals("http")){
                    port = 80;
                }
            }
            HttpHeaders _headers = response.headers();
            Map<String, List<String>> headers = _headers.map();
            for (String key : headers.keySet()) {
                List<String> strings = headers.get(key);
                StringBuilder sb = new StringBuilder();
                for (String string : strings) {
                    sb.append(string);
                }
                String value = sb.toString();
                headers1.append(key).append(": ").append(value).append("\n");
                if("server".equalsIgnoreCase(key)){
                    server = value;
                }
                if("content-length".equalsIgnoreCase(key)){
                    length = value;
                }
            }
            byte[] body = response.body();
            if (length.isEmpty()){
                length = String.valueOf(body.length);
            }
            String statusCode = String.valueOf(response.statusCode());
            resMap.put("md5Body",getMd5(body));

            resMap.put("url",uri.toString());
            resMap.put("header", headers1.toString());
            resMap.put("server",server);
            resMap.put("status",statusCode);
            resMap.put("body",body);
            resMap.put("length",length);
            resMap.put("ip",ip);
            resMap.put("port",String.valueOf(port));
            if(body!=null){
                String _body = new String(body, Charset.defaultCharset());
                int i = _body.indexOf("tle>");
                if(i!=-1){
                    int ii = _body.indexOf("</tit");
                    String title = _body.substring(i+4, ii);
                    resMap.put("title",title);
                }
            }else {
                resMap.put("title","");
            }
            return resMap;
        } catch (Exception e) {
            AssertMapController.INSTANCE.log(e.getMessage());
        }
        return null;
    }
//    public static CompletableFuture<HttpResponse<byte[]>> sendAsync(HttpRequest req){
//
//        CompletableFuture<HttpResponse<byte[]>> response = httpClient.sendAsync(req, HttpResponse.BodyHandlers.ofByteArray());
//        return response;
//
//    }

    public static byte[] post(String url, String content) throws Exception {

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .POST(HttpRequest.BodyPublishers.ofString(content))
                .header("Content-Type", "application/json") // 假设我们发送的是JSON数据
                .header("User-Agent","Java-HttpClient")
                .build();

        try {
            HttpResponse<byte[]> response = httpClient.send(request, HttpResponse.BodyHandlers.ofByteArray());
            return response.body();
        } catch (Exception e) {
            // 处理异常
            AssertMapController.INSTANCE.log(e.getMessage());
        }
        return null;
    }

//    增加获取charset
    public static Map<String,Object> request(String url, String path, boolean firstIcon) throws Exception {

        StringBuilder headers1 = new StringBuilder();
        String server = "";
        String length = "";

        Map<String,Object> resMap = new HashMap<>();
        URI uri = URI.create(url + path);

        HttpRequest httpRequest =HttpRequest.newBuilder()
                .timeout(Duration.ofSeconds(Http.timeout.get()))
                .header("rememberMe", "true")
                .uri(uri).build();
        HttpResponse<byte[]> response = Http.httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofByteArray());

        if(firstIcon){
            Tuple<String, String> imageFavicon = getImageFavicon(url);
            resMap.put("icon",imageFavicon.getFirst());
            resMap.put("ico",imageFavicon.getSecond());
        }

        String ip = uri.getHost();
        // 根据域名查找IP地址
//        InetAddress inetAddress = InetAddress.getByName(host);
        // IP 地址
//        String ip = host;
        int port = uri.getPort();
        if(port==-1){
            if(uri.getScheme().equals("https")){
                port = 443;
            }
            if(uri.getScheme().equals("http")){
                port = 80;
            }
        }
        HttpHeaders _headers = response.headers();
        Map<String, List<String>> headers = _headers.map();
        for (String key : headers.keySet()) {
            List<String> strings = headers.get(key);
            StringBuilder sb = new StringBuilder();
            for (String string : strings) {
                sb.append(string);
            }
            String value = sb.toString();
            headers1.append(key).append(": ").append(value).append("\n");
            if("server".equalsIgnoreCase(key)){
                server = value;
            }
            if("content-length".equalsIgnoreCase(key)){
                length = value;
            }
        }
        byte[] body = response.body();
        if (length.isEmpty()){
            length = String.valueOf(body.length);
        }
        String statusCode = String.valueOf(response.statusCode());
        resMap.put("md5Body",getMd5(body));

        resMap.put("url",url);
        resMap.put("header", headers1.toString());
        resMap.put("server",server);
        resMap.put("status",statusCode);
        resMap.put("body",body);
        resMap.put("length",length);
        resMap.put("ip",ip);
        resMap.put("port",String.valueOf(port));
        if(body!=null){
            String _body = new String(body, Charset.defaultCharset());
            try {
                int i = _body.indexOf("tle>");
                if(i!=-1){
                    int ii = _body.indexOf("</tit");
                    String title = _body.substring(i+4, ii);
                    resMap.put("title",title);
                }

            }catch (StringIndexOutOfBoundsException e){
                Pattern compile = Pattern.compile("<title.*?>.*</title>");
                Matcher matcher = compile.matcher(new String(body, Charset.defaultCharset()));
                if(matcher.find()){
                    String title = matcher.group(0);
                    String substring = title.substring(title.indexOf(">"), title.length()-8);
                    resMap.put("title",substring);
                }
            }

        }else {
            resMap.put("title","");
        }

        return resMap;
    }
    private static Tuple<String,String> getImageFavicon(String url) {
        try {
            URI uri = new URI(url + "/favicon.ico");
            HttpRequest req = Http.httpBuilder.uri(uri).build();
            HttpResponse<byte[]> send = Http.httpClient.send(req, HttpResponse.BodyHandlers.ofByteArray());
            byte[] body = send.body();
            String s = Base64.getMimeEncoder().encodeToString(body);
            return new Tuple<>(getIconHash(s),getMd5(body));
        } catch (URISyntaxException | IOException | InterruptedException e) {
            return new Tuple<>("cant get","cant get");
        }
    }
    private static Tuple<String,String> getImageFavicon(URI uri) {
        try {

            uri = uri.resolve("/favicon.ico");
            HttpRequest req = Http.httpBuilder.uri(uri).build();
            HttpResponse<byte[]> send = Http.httpClient.send(req, HttpResponse.BodyHandlers.ofByteArray());
            byte[] body = send.body();
            String s = Base64.getMimeEncoder().encodeToString(body);
            return new Tuple<>(getIconHash(s),getMd5(body));
        } catch (IOException | InterruptedException e) {
            return new Tuple<>("cant get","cant get");
        }
    }
    private static String getMd5(byte[] body){
        return calculateMD5(body);
    }
    /**
     * 计算favicon hash
     *
     * @param f favicon的文件对象
     * @return favicon hash值
     */
    public static String getIconHash(String f) {
        int murmu = Hashing
                .murmur3_32()
                .hashString(f.replaceAll("\r","" )+"\n", StandardCharsets.UTF_8)
                .asInt();
        return String.valueOf(murmu);
    }
}
