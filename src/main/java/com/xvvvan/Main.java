package com.xvvvan;

import javafx.application.Application;


/**
 * 计划开源
 * Todo：解析icon ok
 * Todo：增加文本快捷输出 ok
 * Todo：一键自动化收集 src模块 攻防模块 ok
 * Todo：解析输入的目标 分解domain 还是ip ip是否转换为c段 域名是否抓取主域名 ok
 * Todo：增加 icon 图标 column no
 * Todo：增加通过去除黑产关键字  no
 * Todo：增加输出html报告模块
 *
 * @author Xvvvan
 */
public class Main {
    public static void main(String[] args) {
        System.setProperty("jdk.httpclient.allowRestrictedHeaders", "connection,content-length,expect,host,upgrade");
        System.setProperty("jdk.internal.httpclient.disableHostnameVerification", Boolean.TRUE.toString());
        Application.launch(App.class);

    }
}