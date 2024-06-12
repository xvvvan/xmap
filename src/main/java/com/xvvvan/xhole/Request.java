package com.xvvvan.xhole;

import com.xvvvan.xhole.operators.Operators;

import java.net.http.HttpRequest;
import java.util.List;
import java.util.Map;

public class Request extends Operators{
    public String method;
    public List<String> path;
    public List<String> headers;
    public String body;
    public List<HttpRequest> compiledRequests;
//    List<Operators> matchers;

    public Request() {
    }

    public int requests(){
        return this.path.size();
    }

    public Tuple<Boolean, List<String>> match(Map<String, Object> data, Matcher matcher){return null;}

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public List<String> getPath() {
        return path;
    }

    public void setPath(List<String> path) {
        this.path = path;
    }

    public List<String> getHeaders() {
        return headers;
    }

    public void setHeaders(List<String> headers) {
        this.headers = headers;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }


}
