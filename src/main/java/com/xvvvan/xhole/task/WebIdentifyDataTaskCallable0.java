package com.xvvvan.xhole.task;

import com.xvvvan.global.Http;
import com.xvvvan.xhole.Matcher;
import com.xvvvan.xhole.engine.XholeEngine;
import com.xvvvan.xhole.ui.UIUpdaterThread;
import com.xvvvan.xhole.ui.data.WebIdentifyData;

import java.util.Map;

/**
 * 带返回结果的任务，返回一个Callable
 * @author xvvvan
 */
public class WebIdentifyDataTaskCallable0 implements Runnable {
    private String url;
    /**
     * 构造函数
     * @param url 任务名称
     */
    public WebIdentifyDataTaskCallable0(String url) {
        this.url = url;
    }
    @Override
    public void run() {

        WebIdentifyData webIdentifyData = null;
//        webIdentifyData.setUrl(url);
        try{
            //完成请求 获取响应
            Map<String, Object> resMap = Http.request(url, "",true);

            String hash = this.getHash(resMap);
            webIdentifyData = Matcher.match(resMap);

            //过滤ip+port+长度相同的响应
            if(repeat(hash)){
                XholeEngine.logger.info(url+" removed wild");
                return;
            }
              //延迟更新ui 否则高并发会卡住ui线程
        UIUpdaterThread.taskQueue.add(webIdentifyData);
        XholeEngine.processNumber.getAndAdd(1.0);
    }catch (Exception e){
            e.printStackTrace();
        XholeEngine.logger.warn(url+" fail to get " + e.getMessage());
        if(webIdentifyData==null){
            WebIdentifyData webIdentifyData1 = new WebIdentifyData();
            webIdentifyData1.setUrl(url);
            UIUpdaterThread.taskQueue.add(webIdentifyData1);
        }


        XholeEngine.processNumber.getAndAdd(1.0);
    }
    }

    private String getHash(Map<String, Object> resMap) {
        return resMap.get("ip")+(String)resMap.get("port")+resMap.get("length");

    }

    private boolean repeat(String key){
        if(UIUpdaterThread.removeWildCard.get()){
            return XholeEngine.filter.contains(key);
        }else {
            XholeEngine.filter.add(key);
            return false;
        }
    }
    //返回全量map


    /**
     * 获取任务名称
     * @return 任务名称
     */
    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
    @Override
    public String toString() {
        return Thread.currentThread().getName()+"MyCallable [ name = " + url + " ]";
    }
}
