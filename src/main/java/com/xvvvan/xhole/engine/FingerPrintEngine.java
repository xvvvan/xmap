package com.xvvvan.xhole.engine;

import com.xvvvan.xhole.Request;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yaml.snakeyaml.Yaml;

import java.io.InputStream;

import static com.xvvvan.xhole.engine.XholeEngine.xholeBanners;

public class FingerPrintEngine {
    public String name;
    //解析fingerprint
    public xHoleTemplate templates;
    public String bannerFrom;
    public int num;

    private final Logger logger = LoggerFactory.getLogger(EholeEngine.class);
    public FingerPrintEngine() {
        this.bannerFrom = "FingerPrint";
    }
    public void init(){
        Yaml yaml = new Yaml();
        load(yaml,"fingerprinthub-web-fingerprints");
        load(yaml,"nacos");
        load(yaml,"zabbix");
        load(yaml,"dahua");
        load(yaml,"weblogic");

//        int size = templates.http.size()+templates2.http.size()+4;
//        xholeBanners.add()
//        fingerPrintEngine1.http.getMatchers().size();

        logger.info(this.getClass().getName()+"load success "+num+" finger");
    }
    private void load(Yaml yaml,String name){
        InputStream r6 = FingerPrintEngine.class.getClassLoader().getResourceAsStream("finger/"+name+".yaml");
        xHoleTemplate template = yaml.loadAs(r6, xHoleTemplate.class);
        template.compile();
        for (Request request : template.http) {
            num+=request.getMatchers().size();
        }
        xholeBanners.add(template);
    }

}
