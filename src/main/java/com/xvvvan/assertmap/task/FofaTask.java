package com.xvvvan.assertmap.task;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.xvvvan.assertmap.controller.AssertMapController;
import com.xvvvan.assertmap.data.AssertData;
import com.xvvvan.global.Http;


import java.net.http.HttpResponse;
import java.util.Base64;
import java.util.concurrent.Callable;


public class FofaTask implements Callable {
    String page;
    String queryLine;
    Boolean isAll;

    Boolean fingerSelected;
    int size;
    private final String AFTER = "&&after=";
    private final String BEFORE = "&&before=";
    final String[] regions = {"\"\"","Beijing","Zhejiang","Guangdong","Shanghai","Shandong","Sichuan","Jiangsu","Hubei","Henan","Hunan","Liaoning","Shaanxi","Anhui","Yunnan","Fujian","Chongqing","Hebei","Guizhou","Jiangxi","Heilongjiang","Tianjin","Shanxi","Inner Mongolia Autonomous Region","Gansu","Jilin","Guangxi","Ningxia Hui Autonomous Region","Qinghai","Hainan","Xinjiang Uyghur Autonomous Region"};
    public FofaTask(String page, int size, String queryLine, Boolean isAll){
        this.page = page;
        this.queryLine = queryLine;
        this.isAll = isAll;
        this.size = size;
    }

    public FofaTask(String page, int size, String queryLine, boolean isAll, boolean fingerSelected) {
        this.page = page;
        this.queryLine = queryLine;
        this.isAll = isAll;
        this.size = size;
        this.fingerSelected = fingerSelected;
    }
//    @Override
//    public void run() {
//        String s = new String(Base64.getEncoder().encode(queryLine.getBytes()));
//        String query = AssertMapController.INSTANCE.config.getFofaParam(page, String.valueOf(size), isAll) + s;
//        HttpResponse<byte[]> resp = Http.get(query);
//        assert resp != null;
//        JSONObject obj = JSON.parseObject(resp.body());
//        Boolean error = obj.getBoolean("error");
//        if(error){
//            String errmsg = obj.getString("errmsg");
//            AssertMapController.INSTANCE.log(errmsg);
//            System.out.println(errmsg);
//        }
//        int num = obj.getInteger("size");
//        String message = "fofa: "+queryLine+" get" + num + "个资产！";
//        AssertMapController.INSTANCE.log(message);
//        JSONArray array = obj.getJSONArray("results");
//        for(int index=0; index < array.size(); index ++){
//            JSONArray _array = array.getJSONArray(index);
//            String host = _array.getString(0);
//            String title = _array.getString(1);
//            String ip = _array.getString(2);
//            int port = Integer.parseInt(_array.getString(3));
//            String domain = _array.getString(4);
//            String protocol = _array.getString(5);
//            String icp = _array.getString(6);
//            String city = _array.getString(7);
//            String server = _array.getString(8);
//            String url = host;
//            if(protocol.equalsIgnoreCase("http")||protocol.equalsIgnoreCase("https")){
//                if(domain.isEmpty()){
//
//                }
//                if(!host.contains("//")){
//                    url = protocol + "://" + host;
//                }
//            }else {
//                url = "";
//            }
//            AssertData1 data = new AssertData1(0, url,domain,title, ip,port,"", protocol, server, "fofa","", icp,city,"");
////            更新表格内容 及textarea内容
//            AssertData assertData = new AssertData(data);
//            //异步更新线程
//            AssertMapController.INSTANCE.uiThread.addTask(assertData);
//
//        }
//    }

    @Override
    public Object call() throws Exception {
        String s = new String(Base64.getEncoder().encode(queryLine.getBytes()));
        String query = AssertMapController.INSTANCE.config.getFofaParam(page, String.valueOf(size), isAll) + s;
        HttpResponse<byte[]> resp = Http.get(query);
        assert resp != null;
        JSONObject obj = JSON.parseObject(resp.body());
        Boolean error = obj.getBoolean("error");
        if(error){
            String errmsg = obj.getString("errmsg");
            AssertMapController.INSTANCE.log(errmsg);
            System.out.println(errmsg);
        }
        int num = obj.getInteger("size");
        String message = "fofa: "+queryLine+" get" + num + "个资产！";
        AssertMapController.INSTANCE.log(message);
        JSONArray array = obj.getJSONArray("results");
        for(int index=0; index < array.size(); index ++){
            JSONArray _array = array.getJSONArray(index);
            String host = _array.getString(0);
            String title = _array.getString(1);
            String ip = _array.getString(2);
            int port = Integer.parseInt(_array.getString(3));
            String domain = _array.getString(4);
            String protocol = _array.getString(5);
            String icp = _array.getString(6);
            String city = _array.getString(7);
            String server = _array.getString(8);
            String url = host;
            if(protocol.equalsIgnoreCase("http")||protocol.equalsIgnoreCase("https")){
                if(domain.isEmpty()){

                }
                if(!host.contains("//")){
                    url = protocol + "://" + host;
                }

            }else {
                url = "";
            }
//            if(!url.isEmpty()){
//                try {
//                    Future<WebIdentifyData> scan = XholeEngine.scan(url);
//                    WebIdentifyData webIdentifyData1 = scan.get();
//                    String cms = webIdentifyData1.getCms();
//                    Integer length = webIdentifyData1.getLength();
//                    String matched = webIdentifyData1.getMatched();
//                    AssertData data = new AssertData(0, url,domain,title,
//                            ip,port,"", protocol, server, "fofa","", icp,city,"",String.valueOf(length),cms,matched);
//                    AssertMapController.INSTANCE.uiThread.addTask(data);
//                    return true;
//                }catch (Exception e){
//                    AssertData assertData = new AssertData(0, url,domain,title, ip,port,"", protocol, server, "fofa","", icp,city,"");
//                    //异步更新线程
//                    AssertMapController.INSTANCE.uiThread.addTask(assertData);
//                    return true;
//                }
//
//            }else {
                //            更新表格内容 及textarea内容
                AssertData assertData = new AssertData(0, url,domain,title, ip,port,"", protocol, server, "fofa","", icp,city,"");
                //异步更新线程
                AssertMapController.INSTANCE.uiThread.addTask(assertData);
//            }

        }
        return true;
    }
}
