package com.xvvvan.xhole.task;

import com.xvvvan.xhole.Tuple;
import com.xvvvan.xhole.engine.xHoleTemplate;
import com.xvvvan.xhole.operators.Result;
import com.xvvvan.xhole.ui.data.WebIdentifyData;

import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;


public class MatchTaskCallable0 implements Callable<WebIdentifyData> {

    private xHoleTemplate matcher;
    private Map<String, Object>  requestMap;

    public MatchTaskCallable0(xHoleTemplate matcher, Map<String, Object>  requestMap){
        this.matcher= matcher;
        this.requestMap = requestMap;
    }

    @Override
    public WebIdentifyData call() throws Exception {
        Tuple<Result, Boolean> match = matcher.execute(requestMap, matcher::match, true);
//        System.out.println(matcher);
        WebIdentifyData webIdentifyData = new WebIdentifyData(
                (String) requestMap.get("ip"),
                Integer.parseInt((String) requestMap.get("port")),
                (String) requestMap.get("url"),
                "",
                (String) requestMap.get("server"),
                (String) requestMap.get("status"),
                Integer.parseInt((String) requestMap.get("length")),
                (String) requestMap.get("title"),
                matcher.bannerFrom,
                "",
                false
        );
        if(match.getSecond()){
            webIdentifyData.setMatchedSuccess(true);
            StringBuilder matchedStringBuilder = new StringBuilder();
            StringBuilder cmsStringBuilder = new StringBuilder();
            Result result = match.getFirst();

            for (Map.Entry<String, List<String>> entry : result.matches.entrySet()) {
                String key = entry.getKey();
                if(key!=null){
                    cmsStringBuilder.append(key).append(",");
                }
                List<String> value = entry.getValue();
                for (String s : value) {
                    matchedStringBuilder.append(s).append(",");
                }
            }

//            for (List<String> value : result.matches.values()) {
//                for (String s : value) {
//                    matchedStringBuilder.append(s).append(",");
//                }
//            }http://101.133.229.199
            if(matcher.name==null){
                webIdentifyData.setCms(cmsStringBuilder.toString());
            }else {
                webIdentifyData.setCms(matcher.name+","+cmsStringBuilder);
            }

            webIdentifyData.setMatched(matchedStringBuilder.toString());
        }

        return webIdentifyData;
    }
}
