//package com.xvvvan.xhole.ui.data;
//
//import com.xvvvan.xhole.engine.XholeEngine;
//import javafx.beans.property.SimpleIntegerProperty;
//import javafx.beans.property.SimpleStringProperty;
//
//import java.util.concurrent.atomic.AtomicBoolean;
//
//public class WebIdentifyData {
//    public SimpleIntegerProperty id;
//    public SimpleStringProperty url;
//    public SimpleStringProperty ip;
//    public SimpleIntegerProperty port;
//    public SimpleStringProperty cms;
//    public SimpleStringProperty server;
//    public SimpleIntegerProperty statuscode;
//    public SimpleIntegerProperty length;
//    public SimpleStringProperty title;
//    public SimpleStringProperty engine;
//    public SimpleStringProperty matched;
//    private AtomicBoolean first = new AtomicBoolean(true);
//
//    public int getId() {
//        return id.get();
//    }
//
//    public SimpleIntegerProperty idProperty() {
//        return id;
//    }
//
//    public void setId(int id) {
//        this.id.set(id);
//    }
//
//    public String getUrl() {
//        return url.get();
//    }
//
//    public SimpleStringProperty urlProperty() {
//        return url;
//    }
//
//    public void setUrl(String url) {
//        this.url.set(url);
//    }
//
//    public String getIp() {
//        return ip.get();
//    }
//
//    public SimpleStringProperty ipProperty() {
//        return ip;
//    }
//
//    public void setIp(String ip) {
//        this.ip.set(ip);
//    }
//
//    public int getPort() {
//        return port.get();
//    }
//
//    public SimpleIntegerProperty portProperty() {
//        return port;
//    }
//
//    public void setPort(int port) {
//        this.port.set(port);
//    }
//
//    public String getCms() {
//        return cms.get();
//    }
//
//    public SimpleStringProperty cmsProperty() {
//        return cms;
//    }
//
//    public void setCms(String cms) {
//        this.cms.set(cms);
//    }
//
//    public String getServer() {
//        return server.get();
//    }
//
//    public SimpleStringProperty serverProperty() {
//        return server;
//    }
//
//    public void setServer(String server) {
//        this.server.set(server);
//    }
//
//    public int getStatuscode() {
//        return statuscode.get();
//    }
//
//    public SimpleIntegerProperty statuscodeProperty() {
//        return statuscode;
//    }
//
//    public void setStatuscode(int statuscode) {
//        this.statuscode.set(statuscode);
//    }
//
//    public int getLength() {
//        return length.get();
//    }
//
//    public SimpleIntegerProperty lengthProperty() {
//        return length;
//    }
//
//    public void setLength(int length) {
//        this.length.set(length);
//    }
//
//    public String getTitle() {
//        return title.get();
//    }
//
//    public SimpleStringProperty titleProperty() {
//        return title;
//    }
//
//    public void setTitle(String title) {
//        this.title.set(title);
//    }
//
//    public String getEngine() {
//        return engine.get();
//    }
//
//    public SimpleStringProperty engineProperty() {
//        return engine;
//    }
//
//    public void setEngine(String engine) {
//        this.engine.set(engine);
//    }
//
//    public String getMatched() {
//        return matched.get();
//    }
//
//    public SimpleStringProperty matchedProperty() {
//        return matched;
//    }
//
//    public void setMatched(String matched) {
//        this.matched.set(matched);
//    }
//
//    public WebIdentifyData(){
//        this.id = new SimpleIntegerProperty(0);
//        this.url = new SimpleStringProperty("");
//        this.ip = new SimpleStringProperty("");
//        this.port = new SimpleIntegerProperty(0);
//        this.cms = new SimpleStringProperty("");
//        this.server = new SimpleStringProperty("");
//        this.statuscode = new SimpleIntegerProperty(0);
//        this.length = new SimpleIntegerProperty(0);
//        this.title = new SimpleStringProperty("");
//        this.engine = new SimpleStringProperty("");
//        this.matched = new SimpleStringProperty("");
//    }
//    public WebIdentifyData(Integer id, String url, String ip, Integer port, String cms, String server, Integer statuscode, Integer length, String title,String engine,String matched) {
//        this.id = new SimpleIntegerProperty(id);
//        this.url = new SimpleStringProperty(url);
//        this.ip = new SimpleStringProperty(ip);
//        this.port = new SimpleIntegerProperty(port);
//        this.cms = new SimpleStringProperty(cms);
//        this.server = new SimpleStringProperty(server);
//        this.statuscode = new SimpleIntegerProperty(statuscode);
//        this.length = new SimpleIntegerProperty(length);
//        this.title = new SimpleStringProperty(title);
//        this.engine = new SimpleStringProperty(engine);
//        this.matched = new SimpleStringProperty(matched);
//    }
//    public WebIdentifyData(WebIdentifyData1 data){
//        this.id = new SimpleIntegerProperty(0);
//        this.url = new SimpleStringProperty(data.url);
//        this.ip = new SimpleStringProperty(data.ip);
//        this.port = new SimpleIntegerProperty(data.port);
//        this.cms = new SimpleStringProperty(data.cms);
//        this.server = new SimpleStringProperty(data.server);
//        this.statuscode = new SimpleIntegerProperty(Integer.parseInt(data.statuscode));
//        this.length = new SimpleIntegerProperty(data.length);
//        this.title = new SimpleStringProperty(data.title);
//        this.engine = new SimpleStringProperty(data.engine);
//        this.matched = new SimpleStringProperty(data.matched);
//
//    }
//
//    public void update(WebIdentifyData1 data) {
//        if(data.isMatchedSuccess()){
//            String matched1 = data.getMatched();
//
//            if(!this.matched.get().isEmpty()){
//                this.matched.set(this.matched.get()+matched1);
//            }else {
//                this.matched.set(matched1);
//            }
//            String cms1 = data.getCms();
//            if(!this.cms.get().isEmpty()){
//                this.cms.set(this.cms.get()+cms1);
//            }else {
//                this.cms.set(cms1);
//            }
//            this.id.set(0);
//            this.url.set(data.url);
//            this.ip.set(data.ip);
//            this.port.set(data.port);
//            this.server.set(data.server);
//            this.statuscode.set(Integer.parseInt(data.statuscode));
//            this.length.set(data.length);
//            this.title.set(data.title);
//            this.engine.set(data.engine);
//            first.compareAndSet(true,false);
//        }else {
//            if(first.get()){
//                XholeEngine.log.info(data.url+" success ");
//                this.id.set(0);
//                this.url.set(data.url);
//                this.ip.set(data.ip);
//                this.port.set(data.port);
//                this.cms.set(data.cms);
//                this.server.set(data.server);
//                this.statuscode.set(Integer.parseInt(data.statuscode));
//                this.length.set(data.length);
//                this.title.set(data.title);
//                this.engine.set(data.engine);
//                this.matched.set(data.matched);
//                first.compareAndSet(true,false);
//            }
//        }
//
//
//    }
//
//    @Override
//    public String toString() {
//        return "WebIdentifyData{" +
//                "id=" + id +
//                ", url=" + url +
//                ", ip=" + ip +
//                ", port=" + port +
//                ", cms=" + cms +
//                ", server=" + server +
//                ", statuscode=" + statuscode +
//                ", length=" + length +
//                ", title=" + title +
//                ", engine=" + engine +
//                ", matched=" + matched +
//                ", first=" + first +
//                '}';
//    }
//}
