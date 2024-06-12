package com.xvvvan.xhole.ui.util;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class UrlUtil {
    public static List<String>[] getWebTargets(List arrayList){
        List<String> wrongUrl = new ArrayList<>();
        List<String> rightUrl = new ArrayList<>();
        List[] result = {rightUrl,wrongUrl};
        Iterator<String> iterator = arrayList.iterator();
        while (iterator.hasNext()) {
            String i = iterator.next();
            if(i.contains("'") || i.contains("\"")){
                i = i.replace("'","");
                i = i.replace("\"","");
            }
//            if(i.length()<8 || !(i.contains(".") || (i.contains("\"")))){
//                wrongUrl.add(i);
//                iterator.remove();
//                continue;
//            }
            if(!isHavehttp(i)){
                String addHttpUrl="http://"+i;
                rightUrl.add(addHttpUrl);
//                System.out.println(i);
            }else {
                rightUrl.add(i);
            }
        }
        return result;
    }
    /**
     * 判断是否以http开头
     */
    public static boolean isHavehttp(String url){
        if(url.startsWith("http://")||url.startsWith("https://")){
            return true;
        }
        return false;
    }
}
