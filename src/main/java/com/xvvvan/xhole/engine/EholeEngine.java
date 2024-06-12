package com.xvvvan.xhole.engine;

import com.alibaba.fastjson.JSON;
import com.xvvvan.util.FileUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.util.ArrayList;

import static com.xvvvan.xhole.engine.XholeEngine.xholeBanners;

//todo:icon hash计算需要做一下
//todo:补充gonmap的web识别
//todo:httpx的web识别
//
public class EholeEngine {
    public static ArrayList<EholeBanner> bannerList;
    private final Logger logger = LoggerFactory.getLogger(EholeEngine.class);
    public void init(){

        if (bannerList==null){
            InputStream resourceAsStream = EholeEngine.class.getClassLoader().getResourceAsStream("finger/eholefinger.json");
            try {
                assert resourceAsStream != null;
                String probe = FileUtil.convertStreamToString(resourceAsStream);
                bannerList = (ArrayList<EholeBanner>) JSON.parseArray(probe, EholeBanner.class);
                for (EholeBanner eholeBanner : bannerList) {
                    xHoleTemplate xholeBanner = new xHoleTemplate(eholeBanner);
                    xholeBanner.compile();
                    xholeBanners.add(xholeBanner);
                }
            }  catch (Exception e) {
                logger.error(e.getMessage());
            }
            logger.info(this.getClass().getName()+"load success "+bannerList.size()+" finger");

        }

    }
}
