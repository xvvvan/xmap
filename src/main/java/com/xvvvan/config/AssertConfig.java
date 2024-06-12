package com.xvvvan.config;

import com.xvvvan.assertmap.controller.AssertMapController;

import java.util.Arrays;
import java.util.List;
import java.util.prefs.Preferences;

public class AssertConfig {
    public String FOFA_API;
    public String FOFA_EMAIL;
    public String FOFA_KEY;
    public  String HUNTER_API;
    public  String[] HUNTER_KEY;
    public  String HUNTER_KEY_STRING;
    public  String ZOOMEYE_API;
    public  String[] ZOOMEYE_KEY;
    public  String SHODAN_API;
    public  String[] SHODAN_KEY;
    public  String MATRIX_API;
    public  String MATRIX_KEY;
    public String DANGER_FINGER;


     {
        //加载各项配置
        refresh();

    }
    public void refresh(){

        Preferences prefs = Preferences.userRoot().node("xassertmap");

        FOFA_KEY = prefs.get("FOFA_KEY", "");
        FOFA_EMAIL = prefs.get("FOFA_EMAIL", "");
        FOFA_API = prefs.get("FOFA_API", "");

        HUNTER_KEY_STRING = prefs.get("HUNTER_KEY", "");
        HUNTER_KEY = prefs.get("HUNTER_KEY", "").split(",");
        HUNTER_API = prefs.get("HUNTER_API", "");

        MATRIX_KEY = prefs.get("MATRIX_KEY", "");
        MATRIX_API = prefs.get("MATRIX_API", "");
        DANGER_FINGER = prefs.get("DANGER_FINGER", "");

    }

    public void save(){
        Preferences prefs = Preferences.userRoot().node("xassertmap");
        prefs.put("FOFA_KEY", AssertMapController.INSTANCE.fofa_key.getText().trim());
        prefs.put("FOFA_EMAIL", AssertMapController.INSTANCE.fofa_email.getText().trim());
        prefs.put("FOFA_API", AssertMapController.INSTANCE.fofa_api.getText().trim());

        prefs.put("HUNTER_KEY", AssertMapController.INSTANCE.hunter_key.getText().trim());
        prefs.put("HUNTER_API", AssertMapController.INSTANCE.hunter_api.getText().trim());

        prefs.put("MATRIX_KEY", AssertMapController.INSTANCE.matrix_key.getText().trim());
        prefs.put("MATRIX_API", AssertMapController.INSTANCE.matrix_api.getText().trim());
        prefs.put("DANGER_FINGER", AssertMapController.INSTANCE.dangerFingerTextField.getText().trim());
        refresh();
    }
    //加载
    public final List<String> FIELDS_LIST = Arrays.asList("host","title","ip","port","domain","protocol","icp","city","server");

    //计算fofa的查询参数
    public  String getFofaParam(String page, String size,boolean isAll) {
        String all = isAll ? "&full=true" : "";
        StringBuilder builder = new StringBuilder(FOFA_API).append("?email=").append(FOFA_EMAIL).append("&key=").append(FOFA_KEY).append(all).append("&page=");
        if(page != null) {
            return builder.append(page).append("&size=").append(size).append("&fields=").append(getFields()).append("&qbase64=").toString();
        }else{
            return builder.append(1).append("&size=").append(size).append("&fields=").append(getFields()).append("&qbase64=").toString();
        }
    }
    //&api-key={api-key}&search={search}&page=1&page_size=10&is_web=1
    //遍历hunterkey 然后生成语句并执行
    public  String getHunterParam(String page, String size) {
        return HUNTER_API + "?api-key=" +
                HUNTER_KEY[0] + "&page=" + page +
                "&page_size=" + size + "&is_web=" + 1 + "&search=";
    }
    //+"&limit=20&page=2"
    public  String getMatrixParam(String page, String size) {

        return MATRIX_API + "?apikey=" +
                MATRIX_KEY + "&limit=" + size + "&page=" + page + "&";
    }

    public  String getFields(){
        StringBuilder builder = new StringBuilder();
        for(String i : FIELDS_LIST){
            builder.append(i).append(",");
        }
        String a = builder.toString();
        return a.substring(0,a.length()-1);
    }

    @Override
    public String toString() {
        return "UserData{" +
                "fofaApi='" + FOFA_API + '\'' +
                ", fofaEmail='" + FOFA_EMAIL + '\'' +
                ", fofaKey='" + FOFA_KEY + '\'' +
                ", hunterApi='" + HUNTER_API + '\'' +
                ", hunterKey=" + Arrays.toString(HUNTER_KEY) +
                ", zoomeyeApi='" + ZOOMEYE_API + '\'' +
                ", zoomeyeKey=" + Arrays.toString(ZOOMEYE_KEY) +
                ", shodanApi='" + SHODAN_API + '\'' +
                ", shodanKey=" + Arrays.toString(SHODAN_KEY) +
                ", matrixApi='" + MATRIX_API + '\'' +
                ", matrixKey='" + MATRIX_KEY + '\'' +
                '}';
    }
}

