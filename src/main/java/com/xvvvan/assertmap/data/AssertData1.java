package com.xvvvan.assertmap.data;

public class AssertData1 {
    public int id;
    public String url;
    public String domain;
    public String title;
    public String ip;
    public int port;
    public String icon;
    public String protocol;
    public String server;
    public String platform;
    public String cert ;
    public String icp ;
    public String city ;
    public String company;

    public AssertData1(){}

    public AssertData1(int id, String url, String domain, String title, String ip, int port, String icon,String protocol, String server, String platform, String cert, String icp, String city,String company) {
        this.id = id;
        this.url = url;
        this.domain = domain;
        this.title = title;
        this.ip = ip;
        this.port = port;
        this.icon = icon;
        this.protocol = protocol;
        this.server = server;
        this.platform = platform;
        this.cert = cert;
        this.icp = icp;
        this.city = city;
        this.company = company;
    }

//    public AssertData1(AssertData item) {
//
//    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getProtocol() {
        return protocol;
    }

    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }

    public String getServer() {
        return server;
    }

    public void setServer(String server) {
        this.server = server;
    }

    public String getPlatform() {
        return platform;
    }

    public void setPlatform(String platform) {
        this.platform = platform;
    }

    public String getCert() {
        return cert;
    }

    public void setCert(String cert) {
        this.cert = cert;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getIcp() {
        return icp;
    }

    public void setIcp(String icp) {
        this.icp = icp;
    }
    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }
}
