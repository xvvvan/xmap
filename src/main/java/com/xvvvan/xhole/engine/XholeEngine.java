package com.xvvvan.xhole.engine;

import com.google.common.util.concurrent.AtomicDouble;
import com.xvvvan.global.VirtualThreadPool;

import com.xvvvan.xhole.Tuple;
import com.xvvvan.xhole.operators.Result;
import com.xvvvan.xhole.task.WebIdentifyDataTaskCallable0;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

//nuclei 的支持一下 http识别
public class XholeEngine {
    public static List<xHoleTemplate> xholeBanners = new ArrayList<>();
    public static List<xHoleTemplate> xholeBannerWithPaths = new ArrayList<>();

    public static final ConcurrentSkipListSet<String> filter = new ConcurrentSkipListSet<>();
    public static AtomicDouble processNumber = new AtomicDouble(1);
    //存在路径的arr
    public static final Logger logger = LoggerFactory.getLogger(XholeEngine.class);

    public static AtomicInteger tasksNumber = new AtomicInteger(0);


    public XholeEngine(){

        new EholeEngine().init();

        DismapEngine dismapEngine = new DismapEngine();
        dismapEngine.init();
        CmsEngine cmsEngine = new CmsEngine();
        cmsEngine.init();
        FingerPrintEngine fingerPrintEngine = new FingerPrintEngine();
        fingerPrintEngine.init();
        initPaths();
        logger.info("init success");
    }
//    针对一个target发起一个请求，获取响应，存入map 并发获取匹配结果
    public void start(List<String> targets){
        if(tasksNumber.get()==0){
            tasksNumber.set(targets.size());
        }else {
            tasksNumber.set(tasksNumber.get()+ targets.size());
        }
        logger.info("add "+targets.size()+" target wait to execute");
        for (String target : targets) {
            VirtualThreadPool.execute(new WebIdentifyDataTaskCallable0(target));
        }

        logger.info("all task has upload");
    }

    public static void initPaths(){
        for (xHoleTemplate xHole : xholeBanners) {
            if(xHole.requests()!=0){
                xholeBannerWithPaths.add(xHole);
            }
        }
    }
}
