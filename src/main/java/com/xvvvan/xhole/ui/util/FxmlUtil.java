package com.xvvvan.xhole.ui.util;

import javafx.fxml.FXMLLoader;
import javafx.scene.Node;

import java.io.IOException;

public class FxmlUtil {
    public static Node getView(String name){
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(loader.getClassLoader().getResource("com/xvvvan/xhole/fxml/"+name+".fxml"));
        try {
            return loader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
