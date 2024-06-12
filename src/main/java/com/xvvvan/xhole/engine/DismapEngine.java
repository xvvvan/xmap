package com.xvvvan.xhole.engine;


import com.alibaba.fastjson.JSON;
import com.xvvvan.util.FileUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.util.ArrayList;

import static com.xvvvan.xhole.engine.XholeEngine.xholeBanners;


public class DismapEngine {
    public static ArrayList<DismapBanner> bannerList;
    private final Logger logger = LoggerFactory.getLogger(DismapEngine.class);
    public void init(){

        if (bannerList==null){
            InputStream resourceAsStream = DismapEngine.class.getClassLoader().getResourceAsStream("finger/dismapfinger.json");
            try {
                assert resourceAsStream != null;
                String probe = FileUtil.convertStreamToString(resourceAsStream);
                bannerList = (ArrayList<DismapBanner>) JSON.parseArray(probe, DismapBanner.class);
                for (DismapBanner dismapBanner : bannerList) {
                    xHoleTemplate xholeBanner = new xHoleTemplate(dismapBanner);
                    xholeBanner.compile();

                    xholeBanners.add(xholeBanner);
                }
            }  catch (Exception e) {
                logger.error(e.getMessage());
            }
            logger.info(this.getClass().getName()+"load success "+bannerList.size()+" finger");

        }

    }

    //遍历keywords 如果有一个不存在就返回false 全都存在才是true

}
