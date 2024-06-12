package com.xvvvan.assertmap.task;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.xvvvan.assertmap.controller.AssertMapController;
import com.xvvvan.assertmap.data.AssertData;
import com.xvvvan.assertmap.data.AssertData1;
import com.xvvvan.global.Http;


import java.net.http.HttpResponse;
import java.util.Base64;
import java.util.Optional;

public class HunterTask implements Runnable {
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

    public HunterTask(String page, int size, String queryLine){
        this.page = page;
        this.queryLine = queryLine;
        this.size = size;
    }

    @Override
    public void run() {
        //这个得改fofaClient改成configClient
//        updateMessage(params+"is running");
        String query = AssertMapController.INSTANCE.config.getHunterParam(page, String.valueOf(size))+ Base64.getEncoder().encodeToString(queryLine.getBytes());
        HttpResponse<byte[]> resp = Http.get(query);
        assert resp != null;
        JSONObject obj = JSON.parseObject(resp.body());
        int status = obj.getInteger("code");
        if(status!=200){
            AssertMapController.INSTANCE.log(obj.getString("message"));
        }
        JSONObject data = obj.getJSONObject("data");
        //待增加更新hunter积分功能模块
        String rest = data.getString("rest_quota");
        String consume = data.getString("consume_quota");
        String total = data.getString("total");
        JSONArray array = data.getJSONArray("arr");

        int size = array.size();
        String sb = "本次查询语句为" + " " +
                queryLine + " " +
                "消耗点数" + " " +
                consume + " " +
                "剩余点数" + " " +
                rest + " " +
                "总共数量" + " " +
                total + " " +
                "本次查到" + " " +
                size + "个";
        AssertMapController.INSTANCE.log(sb);
        try {
            for(int index=0; index < array.size(); index ++){
                JSONObject _array = array.getJSONObject(index);
                Optional.ofNullable(_array.getString("web_title")).ifPresent(o->this.title=o);
                Optional.ofNullable(_array.getString("ip")).ifPresent(o->this.ip=o);
                Optional.ofNullable(_array.getInteger("port")).ifPresent(o->this.port=o);
                Optional.ofNullable(_array.getString("domain")).ifPresent(o->this.domain=o);
                Optional.ofNullable(_array.getString("protocol")).ifPresent(o->this.protocol=o);
                Optional.ofNullable(_array.getString("url")).ifPresent(o->this.url=o);
                Optional.ofNullable(_array.getString("city")).ifPresent(o->this.city=o);
                Optional.ofNullable(_array.getString("company")).ifPresent(o->this.company=o);
                Optional.ofNullable(_array.getString("icp")).ifPresent(o->this.icp=o);

                JSONArray componentArr = _array.getJSONArray("component");
                Optional.ofNullable(componentArr).ifPresent((arr)->{
                    StringBuilder component = new StringBuilder();
                        for (int i = 0; i < arr.size(); i++) {
                            JSONObject componentObject = arr.getJSONObject(i);
                            String componentName = componentObject.getString("name");
                            String componentVersion = componentObject.getString("version");
                            component.append(componentName).append(" ").append(componentVersion).append(" ");
                        }
                        server = component.toString();
                });
                AssertData1 data0 = new AssertData1(0,this.url,this.domain,this.title, this.ip,this.port,this.icon, this.protocol, this.server, "hunter",this.cert, this.icp,this.city,this.company);
                AssertData assertData = new AssertData(data0);
                //异步更新线程
                AssertMapController.INSTANCE.uiThread.addTask(assertData);
                this.reset();
            }
        }catch (Exception e){
            System.out.println(e.getMessage());
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
