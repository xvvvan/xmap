package com.xvvvan.xhole.engine;

import java.util.HashMap;
import java.util.Map;

//{"Rank":3,
//        "Name":"Epoint-OA",
//        "Type":"body",
//        "Mode":"",
//        "Rule":
//        {"InBody":"(SourceControl=\"EpointCommon\" ></script>|<script>window.location.href='../netoffice6';</script>)",
    //        "InHeader":"",
    //        "InIcoMd5":""
//        },
//        "Http":{"ReqMethod":"","ReqPath":"","ReqHeader":null,"ReqBody":""}
//}
public class DismapBanner {
    private String name;
    private String type;
    private String mode;
    private Map<String,String> rule;
    private Map<String,String> http;

    public DismapBanner() {
        this.name = "";
        this.type = "";
        this.mode = "";
        this.rule = new HashMap<>();
        this.http = new HashMap<>();
    }

    public DismapBanner(String name, String type, String mode, Map<String, String> rule, Map<String, String> http) {
        this.name = name;
        this.type = type;
        this.mode = mode;
        this.rule = rule;
        this.http = http;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    public Map<String, String> getRule() {
        return rule;
    }

    public void setRule(Map<String, String> rule) {
        this.rule = rule;
    }

    public Map<String, String> getHttp() {
        return http;
    }

    public void setHttp(Map<String, String> http) {
        this.http = http;
    }

    @Override
    public String toString() {
        return "DismapBanner{" +
                "name='" + name + '\'' +
                ", type='" + type + '\'' +
                ", mode='" + mode + '\'' +
                ", rule=" + rule +
                ", http=" + http +
                '}';
    }
}
