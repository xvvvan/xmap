import com.xvvvan.xhole.Request;

import com.xvvvan.xhole.engine.FingerPrintEngine;
import com.xvvvan.xhole.engine.xHoleTemplate;
import org.yaml.snakeyaml.Yaml;

import java.io.InputStream;

public class TemplateTest {
    public static void main(String[] args) {
        Yaml yaml = new Yaml();
        InputStream resourceAsStream = FingerPrintEngine.class.getClassLoader().getResourceAsStream("finger/nacos.yaml");
        xHoleTemplate template = yaml.loadAs(resourceAsStream, xHoleTemplate.class);
        for (Request request : template.getHttp()) {
            System.out.println(request);
        }
        InputStream resourceAsStream1 = FingerPrintEngine.class.getClassLoader().getResourceAsStream("finger/fingerprinthub-web-fingerprints.yaml");
        xHoleTemplate template1 = yaml.loadAs(resourceAsStream1, xHoleTemplate.class);
        for (Request request : template1.getHttp()) {
            System.out.println(request);
        }
    }
}
