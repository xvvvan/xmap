package com.xvvvan.xhole.ui.data;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import com.xvvvan.xhole.Tuple;
import com.xvvvan.xhole.operators.Result;

import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

public class WebIdentifyData {
    public String id;
    @ColumnWidth(20)
    @ExcelProperty(value="ip", index=0)
    public String ip;
    @ColumnWidth(10)
    @ExcelProperty(value="port", index=1)
    public Integer port;
    @ColumnWidth(35)
    @ExcelProperty(value="url", index=2)
    public String url;
    @ColumnWidth(20)
    @ExcelProperty(value="cms", index=3)
    public String cms;
    @ColumnWidth(10)
    @ExcelProperty(value="server", index=4)
    public String server;
    @ColumnWidth(10)
    @ExcelProperty(value="statuscode", index=5)
    public String statuscode;
    @ColumnWidth(10)
    @ExcelProperty(value="length", index=6)
    public Integer length;
    @ColumnWidth(35)
    @ExcelProperty(value="title", index=7)
    public String title;

    @ColumnWidth(10)
    @ExcelProperty(value="engine", index=8)
    public String engine;

    @ColumnWidth(10)
    @ExcelProperty(value="matched", index=9)
    public String matched;
    private AtomicBoolean first = new AtomicBoolean(true);
    public boolean matchedSuccess;
    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getCms() {
        return cms;
    }

    public void setCms(String cms) {
        if(!this.cms.isEmpty()){
            this.cms = this.cms+","+cms;
        }else {
            this.cms = cms;
        }

    }

    public String getServer() {
        return server;
    }

    public void setServer(String server) {
        this.server = server;
    }

    public String getStatuscode() {
        return statuscode;
    }

    public void setStatuscode(String statuscode) {
        this.statuscode = statuscode;
    }

    public Integer getLength() {
        return length;
    }

    public void setLength(Integer length) {
        this.length = length;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getEngine() {
        return engine;
    }

    public void setEngine(String engine) {
        this.engine = engine;
    }

    public String getMatched() {
        return matched;
    }

    public void setMatched(String matched) {
        if(!this.matched.isEmpty()){
            this.matched = this.matched+","+matched;
        }else {
            this.matched = matched;
        }

    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public boolean isMatchedSuccess() {
        return matchedSuccess;
    }

    public void setMatchedSuccess(boolean matchedSuccess) {
        this.matchedSuccess = matchedSuccess;
    }

    public WebIdentifyData(){

    }
//    public WebIdentifyData1(WebIdentifyData data){
//        this.ip = data.ip.getValue();
//        this.port = data.port.getValue();
//        this.url = data.url.getValue();
//        this.cms = data.cms.getValue();
//        this.server = data.server.getValue();
//        this.statuscode = data.statuscode.getValue().toString();
//        this.length = data.length.getValue();
//        this.title = data.title.getValue();
//        this.engine = data.engine.getValue();
//    }

    public WebIdentifyData(String ip, Integer port, String url, String cms, String server, String statuscode, Integer length, String title, String engine, String matched, boolean matchedSuccess) {
        this.ip = ip;
        this.port = port;
        this.url = url;
        this.cms = cms;
        this.server = server;
        this.statuscode = statuscode;
        this.length = length;
        this.title = title;
        this.engine = engine;
        this.matched = matched;
        this.matchedSuccess = matchedSuccess;
    }


    public WebIdentifyData (Map<String, Object> requestMap){
        this.ip = (String) requestMap.get("ip");
        this.port = Integer.parseInt((String) requestMap.get("port"));
        this.url = (String) requestMap.get("url");
        this.cms = "";
        this.server = (String) requestMap.get("server");
        this.statuscode = (String) requestMap.get("status");
        this.length = Integer.parseInt((String) requestMap.get("length"));
        this.title = (String) requestMap.get("title");
        this.engine = "xhole";
        this.matched = "";
        this.matchedSuccess = false;

    }
        public void update(WebIdentifyData data) {
        if(data.isMatchedSuccess()){
            String matched1 = data.getMatched();

            if(!this.matched.isEmpty()){
                this.matched=this.matched+matched1;
            }else {
                this.matched = matched1;
            }
            String cms1 = data.getCms();
            if(!this.cms.isEmpty()){
                this.cms=this.cms+cms1;
            }else {
                this.cms=cms1;
            }

            first.compareAndSet(true,false);
        }else {
            if(first.get()){
                this.setId(data.id);
                this.setUrl(data.url);
                this.setIp(data.ip);
                this.setPort(data.port);
                this.setServer(data.server);
                this.setStatuscode(data.statuscode);
                this.setLength(data.length);
                this.setTitle(data.title);
                this.setEngine(data.engine);
                this.setMatched(data.matched);
                first.compareAndSet(true,false);
            }
        }
    }
    public void update(String name,Tuple<Result, Boolean> match) {
        if(match.getSecond()){
            StringBuilder matchedStringBuilder = new StringBuilder();
            StringBuilder cmsStringBuilder = new StringBuilder();
            Result result = match.getFirst();

            for (Map.Entry<String, List<String>> entry : result.matches.entrySet()) {
                String key = entry.getKey();
                if(key==null){
                    break;
                }else {
                    cmsStringBuilder.append(key).append(",");
                }
                List<String> value = entry.getValue();
                for (String s : value) {
                    matchedStringBuilder.append(s).append(",");
                }
            }
            if(cmsStringBuilder.isEmpty()){
                this.setCms(name);
//                String matchedString = matchedStringBuilder.substring(0, matchedStringBuilder.length() - 1);
//                this.setMatched(matchedString);
            }else {
                String cmsString = cmsStringBuilder.substring(0, cmsStringBuilder.length() - 1);
                String matchedString = matchedStringBuilder.substring(0, matchedStringBuilder.length() - 1);

                this.setCms(cmsString);
                this.setMatched(matchedString);
            }

//            if()

        }


    }

    @Override
    public String toString() {
        return "WebIdentifyData1{" +
                "ip='" + ip + '\'' +
                ", port=" + port +
                ", url='" + url + '\'' +
                ", cms='" + cms + '\'' +
                ", server='" + server + '\'' +
                ", statuscode='" + statuscode + '\'' +
                ", length=" + length +
                ", title='" + title + '\'' +
                ", engine='" + engine + '\'' +
                ", matched='" + matched + '\'' +
                ", matchedSuccess=" + matchedSuccess +
                '}';
    }
}
