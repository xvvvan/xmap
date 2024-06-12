package com.xvvvan.xhole.engine;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.xvvvan.global.Http;
import com.xvvvan.xhole.Request;
import com.xvvvan.xhole.Tuple;
import com.xvvvan.xhole.Types;
import com.xvvvan.xhole.matchers.Matcher;
import com.xvvvan.xhole.matchers.MatcherType;
import com.xvvvan.xhole.operators.Operators;


import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpRequest;
import java.util.*;

/**
 *  todo:dismap part = ico 为图标的md5 需要去请求函数里新增并存入缓存map 已经ok
 *  todo:暂未实现http请求的加载 2024 6.11 ok
 */

public class xHoleTemplate extends Operators {
    public String name;
    public List<Request> http;
    public String bannerFrom;

    public xHoleTemplate(){

    }

    public xHoleTemplate(EholeBanner b){
        this.matchers_condition="or";
        this.matchers = new ArrayList<>();

        Matcher matcher = new Matcher();
        this.name = b.Cms;
        matcher.part = b.Location;

        switch (b.Method){
            case "keyword":
                matcher.type = "word";
                break;
            case "faviconhash":
                matcher.type = "word";
                matcher.part = "icon";
                break;
            case "regular":
                matcher.type = "regex";
                break;
            default:
                matcher.type = "";
        }

        matcher.word = Arrays.asList(b.Keyword);
        matcher.condition = "and";
        this.bannerFrom = "ehole";
        matchers.add(matcher);
    }
//    part = ico 为图标的md5 需要去请求函数里新增并存入缓存map
    public xHoleTemplate(DismapBanner b){
        this.bannerFrom = "dismap";
        this.name = b.getName();
        if(!b.getMode().isEmpty()){
            this.matchers_condition=b.getMode();
        }else {
            this.matchers_condition="or";
        }
        this.matchers = new ArrayList<>();
//        this.requests = new ArrayList<>();
        Map<String, String> ruleMap = b.getRule();
        String types = b.getType();
        if(types.contains("|")){
            String[] parts = types.split("\\|");
            for (String part : parts) {
                Matcher matcher = new Matcher();
                matcher.name = b.getName();
                matcher.word = new ArrayList<>();
                matcher.part = part;
                matcher.type = "word";
                matcher.condition = "or";
                this.processMatcher(ruleMap,matcher,part);
            this.matchers.add(matcher);
            }
        }else {
            Matcher matcher = new Matcher();
            matcher.name = b.getName();
            matcher.type = "word";
            matcher.word = new ArrayList<>();
            matcher.part = b.getType();
            matcher.condition = "or";
            this.processMatcher(ruleMap,matcher,b.getType());
            matchers.add(matcher);
        }
        Map<String, String> http = b.getHttp();
        String method = http.get("ReqMethod");
        String header = http.get("ReqHeader");
        String path = http.get("ReqPath");
        String body = http.get("ReqBody");

        if(!method.isEmpty()||header!=null||!path.isEmpty()||!body.isEmpty()){
            Request req = new Request();
            if(header!=null){
                JSONArray headerArray = JSON.parseArray(header);
                this.http = new ArrayList<>();
                Request request = new Request();
                request.headers = Arrays.stream(headerArray.toArray(String.class)).toList();
            }
            req.method = method;
//            this.requests.add(builder);
            if(!path.isEmpty()){
                req.path = Collections.singletonList(path);
            }
            this.http = List.of(req);

        }

    }

//    匹配的最终map里增加一个bodyMd5 在异步task里加
    public xHoleTemplate(CmsBanner b){
        this.bannerFrom = "cmsbanner";
        this.name = b.cms;
        this.matchers_condition="or";
        this.matchers = new ArrayList<>();
        Matcher matcher = new Matcher();
        matcher.part = "body";
        matcher.type = "word";
        matcher.word = List.of(b.keyword);
        matcher.condition = "and";
        if(b.method.equals("md5")){
            matcher.type = "word";
            matcher.part = "md5Body";
        }
        matchers.add(matcher);
        if(!Objects.equals(b.path, "")){
            Request request = new Request();
            request.path =  Collections.singletonList(b.path);
            this.http = List.of(request);
        }
    }
    private void processMatcher(Map<String, String> ruleMap,Matcher matcher,String part){
//        System.out.println(part);
        String p ="";
        switch (part) {
            case "ico" : p="InIcoMd5";break;
            case "body" : p="InBody";break;
            case "header": p="InHeader";break;
        };

        String keyword = ruleMap.get(p);
        //                        去除小括号
        if(keyword.startsWith("(")){
            keyword = keyword.substring(1,keyword.length()-1);
        }
        if(keyword.contains("|")){
            String[] keywords = keyword.split("\\|");
            matcher.word.addAll(Arrays.asList(keywords));
        }else {
            matcher.word.add(keyword);
        }
    }

    //前置条件 获取iconhash status 状态码 headers body
    public void compile(){
        try {
            super.compile();
            if(requests()!=0){
                for (Request request : this.http) {
                    request.compile();
                }
                if(this.matchers.isEmpty()){
                    this.matchers = new ArrayList<>();
                    for (Request request : this.http) {
                        this.matchers.addAll(request.matchers);
                    }
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    //解析是否有url
    public List<Map<String, Object>> executeWithResult(String url,boolean first){
        Map<String, Object> respMap =null;
        int requests = this.requests();
        if(requests!=0){
            ArrayList<Map<String, Object>> results = new ArrayList<>();
            Request request = this.http.get(0);

            try {
                URI uri = null;
                if(request.path!=null){
                    for (String path : request.path) {
                        if(url.endsWith("/")){
                            uri = new URI(url.substring(0,url.length()-1)+path);
                        }else {
                            uri = new URI(url+path);
                        }
                        HttpRequest.Builder builder = HttpRequest.newBuilder(uri);
                        if(request.headers!=null){
                            for (String header : request.headers) {
                                String[] headers = header.split(":",2);
                                builder.header(headers[0],headers[1]);
                            }
                        }
                        if(request.method!=null){
                            if(request.body!=null){
                                builder.method(request.method, HttpRequest.BodyPublishers.ofString(request.body));
                            }else {
                                builder.method(request.method, HttpRequest.BodyPublishers.noBody());
                            }
                        }else {
                            builder.GET();
                        }
                        respMap = Http.request(builder.build(),first);
                        results.add(respMap);
                    }

                }else {
                    uri = new URI(url);
                    HttpRequest.Builder builder = HttpRequest.newBuilder(uri);
                    if(request.headers!=null){
                        for (String header : request.headers) {
                            String[] headers = header.split(":",2);
                            builder.header(headers[0],headers[1]);
                        }
                    }
                    if(request.method!=null){
                        if(request.body!=null){
                            builder.method(request.method, HttpRequest.BodyPublishers.ofString(request.body));
                        }else {
                            builder.method(request.method, HttpRequest.BodyPublishers.noBody());
                        }
                    }else {
                        builder.GET();
                    }
                    respMap = Http.request(builder.build(),first);
                    results.add(respMap);
                }

            } catch (URISyntaxException ignore) {
//                return results;
//                    throw new RuntimeException(e);
            }


            return results;
        }
        return null;
    }

    public Tuple<Boolean, List<String>> match(Map<String, Object> data, Matcher matcher) {

        Tuple<Boolean, List<String>> result = new Tuple<>(false, new ArrayList<>());
        Tuple<String, Boolean> matchPart = this.getMatchPart(matcher.getPart(), data);
        String item = matchPart.getFirst();
        Boolean ok = matchPart.getSecond();
        if (!matchPart.getSecond() && matcher.getMatcherType() != MatcherType.DSLMatcher) {
            return result;
        }
        switch (matcher.getMatcherType()) {
            case StatusMatcher:
                int statusCode;
                boolean hasStatusCode = getStatusCode(data) != null;
                if (!hasStatusCode) {
                    return result;
                }
                statusCode = getStatusCode(data).getFirst();
                boolean statusMatch = matcher.matchStatusCode(statusCode);
                String statusCodeSnippet = data.get("response").toString() + statusCode;
                List<String> snippets = new ArrayList<>();
                snippets.add(statusCodeSnippet);
                result.setFirst(statusMatch);
                result.setSecond(snippets);
                break;
            case SizeMatcher:
                int size = 0;
                if (ok) {
                    size = item.length();
                }
                boolean sizeMatch = matcher.matchSize(size);
                result.setFirst(sizeMatch);
                break;
            case WordsMatcher:
                Tuple<Boolean, List<String>> wordsMatcher = matcher.matchWords(item, data);
                result.setTuple(wordsMatcher);
                break;
            case RegexMatcher:
                Tuple<Boolean, List<String>> regexMatcher = matcher.matchRegex(item);
                result.setTuple(regexMatcher);
                break;
            case BinaryMatcher:
                Tuple<Boolean, List<String>> binaryMatcher = matcher.matchBinary(item);
                result.setTuple(binaryMatcher);
                break;
            case DSLMatcher:
                boolean matchDSL = matcher.matchDSL(data);
                result.setFirst(matchDSL);
                break;
        }
        return result;
    }
    public Tuple<String, Boolean> getMatchPart(String part, Map<String, Object> data) {
        if (part == null || part.isEmpty()) {
            part = "body";
        }
        if (part.equals("header")) {
            part = "all_headers";
        }
        String itemStr = "";
        if (part.equals("all")) {
            itemStr = Types.toString(data.get("body")) +
                    Types.toString(data.get("all_headers"));
        } else {
            Object item = data.get(part);
            if (item == null) {
                return new Tuple<>("", false);
            }
            itemStr = Types.toString(item);
        }
        return new Tuple<>(itemStr, true);
    }
    public Tuple<Integer, Boolean> getStatusCode(Map<String, Object> data) {
        String statusCodeValue = (String)data.get("status_code");

        if (statusCodeValue == null) {
            return new Tuple<>(0, false);
        }
        int statusCode;
        try {
            statusCode = Integer.parseInt(statusCodeValue);
        } catch (ClassCastException e) {
            return new Tuple<>(0, false);
        }
        return new Tuple<>(statusCode, true);
    }
    public List<Request> getHttp() {
        return http;
    }

    public void setHttp(List<Request> http) {
        this.http = http;
    }
    public int requests(){
        if(this.http != null){

            List<Request> http = this.getHttp();
            int size = http.size();
            int num = 0;
            for (Request request : http) {
                if(request.path!=null){
                    num = num+request.path.size();
                }
            }
            if(num!=0){
                return size*num;
            }else {
                return size;
            }

        }else {
            return 0;
        }
    }
    @Override
    public String toString() {
        return "xHoleTemplate{" +
                "name='" + name + '\'' +
                ", matchers=" + matchers +
                ", http=" + http +
                ", bannerFrom='" + bannerFrom + '\'' +
                '}';
    }
}
