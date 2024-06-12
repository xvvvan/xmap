package com.xvvvan.xhole;

import com.xvvvan.xhole.engine.xHoleTemplate;
import com.xvvvan.xhole.engine.XholeEngine;
import com.xvvvan.xhole.operators.Result;
import com.xvvvan.xhole.ui.data.WebIdentifyData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;


public class Matcher {
    private static final Logger logger = LoggerFactory.getLogger(Matcher.class);

    //匹配无需额外请求路径的指纹
    public static WebIdentifyData match(Map<String, Object> requestMap){
        WebIdentifyData webIdentifyData = new WebIdentifyData(requestMap);
//        初始状态
        for (xHoleTemplate template : XholeEngine.xholeBanners) {
            Tuple<Result, Boolean> match = template.execute(requestMap,template::match,true);
            webIdentifyData.update(template.name,match);
        }
        try{
            for (xHoleTemplate template : XholeEngine.xholeBannerWithPaths) {
                List<Map<String, Object>> results = template.executeWithResult((String) requestMap.get("url"), false);
                for (Map<String, Object> result : results) {
                    Tuple<Result, Boolean> match = template.execute(result, template::match, true);
                    webIdentifyData.update(template.name,match);
                }
            }
        }catch (Exception ignore){

        }
        return webIdentifyData;
    }
    public static WebIdentifyData match(String url){
        WebIdentifyData webIdentifyData = new WebIdentifyData();
        webIdentifyData.setUrl(url);
        webIdentifyData.setStatuscode("0");
//        初始状态
//        for (xHoleTemplate template : XholeEngine.xholeBanners) {
//            Map<String, Object> result = template.executeWithResult(url,false);
//            Tuple<Result, Boolean> match = template.execute(result, template::match, true);
//            webIdentifyData.update(template.name,match);
//
//        };
        return webIdentifyData;
    }
}
