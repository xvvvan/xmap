package com.xvvvan.assertmap.task;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.xvvvan.assertmap.controller.AssertMapController;
import com.xvvvan.assertmap.data.AssertData;
import com.xvvvan.assertmap.data.AssertData1;
import com.xvvvan.global.Http;


import java.net.http.HttpResponse;
import java.util.Optional;


public class MatrixTask implements Runnable {
    String page;
    String queryLine;
    int size;
    String city="";
    String cert="";
    private String icon="";
    private String server="";
    private String icp="";
    private String url="";
    private String domain="";
    private String title="";
    private String ip="";
    private int port=0;
    private String protocol="";
    private String company="";

    public MatrixTask(String page, int size, String queryLine){
        this.page = page;
        this.queryLine = queryLine;
        this.size = size;
    }
//    app=apache-shiro


    @Override
    public void run() {
        String query =AssertMapController.INSTANCE.config.getMatrixParam(page, String.valueOf(size)) + queryLine;
        HttpResponse<byte[]> resp = Http.get(query);
        assert resp != null;
        JSONObject obj = JSON.parseObject(resp.body());
        String total = obj.getString("total");
        JSONArray data = obj.getJSONArray("data");
        String message = "matrix: "+queryLine+" get" + total + "个资产！";
        AssertMapController.INSTANCE.log(message);

        for (int index = 0; index < data.size(); index++) {
            JSONObject object = data.getJSONObject(index);
            JSONObject web = object.getJSONObject("web");

            String title = web.getString("title");
            Optional.ofNullable(title).ifPresent((o)-> this.title =o);

            String ip = object.getString("ip");
            Optional.ofNullable(ip).ifPresent((o)-> this.ip =o);

            int port = object.getInteger("port");
            Optional.of(port).ifPresent((o)-> this.port =o);
            String protocol = object.getString("protocol");
            Optional.of(protocol).ifPresent((String o) -> this.protocol =o);

            String server = web.getString("server");
            Optional.ofNullable(server).ifPresent((o)-> this.server =o);

            String icon = web.getString("favicon_mmh3_hash");
            Optional.ofNullable(icon).ifPresent((o)-> this.icon =o);

            String icp = web.getString("icp");
            Optional.ofNullable(icp).ifPresent((o)-> this.icp =o);

            JSONObject dom = object.getJSONObject("domain");
            Optional.ofNullable(dom).ifPresent((o)->{
                String domain1 = o.getString("domain");
                Optional.ofNullable(domain1).ifPresent(o1->this.domain=o1);
                String company1 = o.getString("record_company");
                Optional.ofNullable(company1).ifPresent(o1->this.company=o1);
            });

            JSONObject address = object.getJSONObject("address");

            Optional.ofNullable(address).ifPresent((addr)-> this.city = addr.getString("city"));

            JSONObject dom1 = object.getJSONObject("cert");
            Optional.ofNullable(dom1).ifPresent((addr)-> this.cert = addr.getString("md5"));

            if(this.protocol.equalsIgnoreCase("http")||this.protocol.equalsIgnoreCase("https")) {
                if(!this.domain.isEmpty()){
                    if(this.port!=80&&this.port!=443){
                        this.url =this.protocol + "://" + this.domain + ":" + this.port;
                    }else {
                        this.url =this.protocol + "://" + this.domain;
                    }
                }else {
                    this.url = this.protocol + "://" + this.ip + ":" + this.port;
                }

            }
            AssertData1 data0 = new AssertData1(0,this.url,this.domain,this.title, this.ip,this.port,this.icon, this.protocol, this.server, "matrix",this.cert, this.icp,this.city,this.company);
            AssertData assertData = new AssertData(data0);
            //异步更新线程
            AssertMapController.INSTANCE.uiThread.addTask(assertData);
            this.reset();
        }
    }
    private void reset(){
         city="";
         cert="";
         icon="";
         server="";
         icp="";
         url="";
         domain="";
         title="";
         ip="";
         port=0;
         protocol="";
         company="";
    }

}
