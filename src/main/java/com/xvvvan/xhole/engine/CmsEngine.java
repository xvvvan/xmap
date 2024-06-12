package com.xvvvan.xhole.engine;

import com.alibaba.fastjson.JSON;
import com.xvvvan.util.FileUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import java.io.InputStream;
import java.util.ArrayList;


import static com.xvvvan.xhole.engine.XholeEngine.xholeBanners;

public class CmsEngine {
    public static ArrayList<CmsBanner> bannerList;
    private  final Logger logger = LoggerFactory.getLogger(CmsEngine.class);

    public void init() {
        if (bannerList==null){
            InputStream resourceAsStream = EholeEngine.class.getClassLoader().getResourceAsStream("finger/cms.json");
            try {
                assert resourceAsStream != null;
                String probe = FileUtil.convertStreamToString(resourceAsStream);
                bannerList = (ArrayList<CmsBanner>) JSON.parseArray(probe, CmsBanner.class);
                for (CmsBanner cmsBanner : bannerList) {
                    xHoleTemplate xholeBanner = new xHoleTemplate(cmsBanner);
                    xholeBanner.compile();
                    xholeBanners.add(xholeBanner);
                }
            }  catch (Exception e) {
                e.printStackTrace();
            }
            logger.info(this.getClass().getName()+"load success "+bannerList.size()+" finger");
        }
    }
}
