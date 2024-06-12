package com.xvvvan.assertmap.data;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

public class AssertData {
    public SimpleIntegerProperty id;
    public SimpleStringProperty url;
    public SimpleStringProperty domain;
    public SimpleStringProperty title;
    public SimpleStringProperty ip;
    public SimpleIntegerProperty port;
    public SimpleStringProperty icon;
    public SimpleStringProperty protocol;
    public SimpleStringProperty server;
    public SimpleStringProperty platform;
    public SimpleStringProperty cert ;
    public SimpleStringProperty icp ;
    public SimpleStringProperty city ;
    public SimpleStringProperty company ;
    public SimpleStringProperty finger ;
    public SimpleStringProperty length ;
    public SimpleStringProperty matched ;
    public AssertData(){}
    public AssertData(Integer id, String url, String domain, String title, String ip, Integer port, String icon,String protocol, String server, String platform,
                      String cert, String icp,String city,String company) {
        this.id = new SimpleIntegerProperty(id);
        this.url = new SimpleStringProperty(url);
        this.domain = new SimpleStringProperty(domain);
        this.title = new SimpleStringProperty(title);
        this.ip = new SimpleStringProperty(ip);
        this.port = new SimpleIntegerProperty(port);
        this.icon = new SimpleStringProperty(icon);
        this.protocol = new SimpleStringProperty(protocol);
        this.server = new SimpleStringProperty(server);
        this.platform = new SimpleStringProperty(platform);
        this.cert = new SimpleStringProperty(cert);
        this.icp = new SimpleStringProperty(icp);
        this.city = new SimpleStringProperty(city);
        this.company = new SimpleStringProperty(company);


    }
    public AssertData(Integer id, String url, String domain, String title, String ip, Integer port, String icon,String protocol, String server, String platform,
                      String cert, String icp,String city,String company,String finger,String length,String matched) {
        this.id = new SimpleIntegerProperty(id);
        this.url = new SimpleStringProperty(url);
        this.domain = new SimpleStringProperty(domain);
        this.title = new SimpleStringProperty(title);
        this.ip = new SimpleStringProperty(ip);
        this.port = new SimpleIntegerProperty(port);
        this.icon = new SimpleStringProperty(icon);
        this.protocol = new SimpleStringProperty(protocol);
        this.server = new SimpleStringProperty(server);
        this.platform = new SimpleStringProperty(platform);
        this.cert = new SimpleStringProperty(cert);
        this.icp = new SimpleStringProperty(icp);
        this.city = new SimpleStringProperty(city);
        this.company = new SimpleStringProperty(company);
        this.finger = new SimpleStringProperty(finger);
        this.length = new SimpleStringProperty(length);
        this.matched = new SimpleStringProperty(matched);

    }

    public AssertData(AssertData1 data) {
        this.id = new SimpleIntegerProperty(data.id);
        this.url = new SimpleStringProperty(data.url);
        this.domain = new SimpleStringProperty(data.domain);
        this.title = new SimpleStringProperty(data.title);
        this.ip = new SimpleStringProperty(data.ip);
        this.port = new SimpleIntegerProperty(data.port);
        this.icon = new SimpleStringProperty(data.icon);
        this.protocol = new SimpleStringProperty(data.protocol);
        this.server = new SimpleStringProperty(data.server);
        this.platform = new SimpleStringProperty(data.platform);
        this.cert = new SimpleStringProperty(data.cert);
        this.icp = new SimpleStringProperty(data.icp);
        this.city = new SimpleStringProperty(data.city);
        this.company = new SimpleStringProperty(data.company);
    }
    public AssertData1 toData() {
        AssertData1 data = new AssertData1();
        data.id = this.id.get();
        data.url = this.url.get();
        data.domain = this.domain.get();
        data.title = this.title.get();
        data.ip = this.ip.get();
        data.port = this.port.get();
        data.icon = this.icon.get();
        data.protocol = this.protocol.get();
        data.server = this.server.get();
        data.platform = this.platform.get();
        data.cert = this.cert.get();
        data.icp = this.icp.get();
        data.city = this.city.get();
        data.company = this.company.get();
        return data;
    }
    public int getId() {
        return id.get();
    }

    public SimpleIntegerProperty idProperty() {
        return id;
    }

    public void setId(int id) {
        this.id.set(id);
    }
}
