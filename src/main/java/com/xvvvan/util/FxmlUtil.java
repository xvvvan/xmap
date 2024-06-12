package com.xvvvan.util;

import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.layout.AnchorPane;

import java.io.IOException;
import java.util.Optional;

public class FxmlUtil {
    public static Node getView(String name){

        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(loader.getClassLoader().getResource("com/xvvvan/uiframe/fxml/"+name+".fxml"));
        try {
            return loader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     *
     * @param name fxml name
     * @return key:Node value:Controller
     */
    public static Pair<Optional<Parent>,Object> loadView(String name){
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(FxmlUtil.class.getClassLoader().getResource(name+".fxml"));
        try {
            Optional<Parent> view = Optional.of(loader.load());
            Object controller = loader.getController();
            return new Pair<>(view,controller);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
    public static void setAnchor(AnchorPane pane, Node content, Double top, Double bottom, Double left, Double right){
        AnchorPane.setBottomAnchor(content,bottom);
        AnchorPane.setLeftAnchor(content,left);
        AnchorPane.setRightAnchor(content,right);
        AnchorPane.setTopAnchor(content,top);
        pane.getChildren().add(content);
    }
    public static void setFullAnchorPane(AnchorPane pane,Node node){
        pane.getChildren().clear();
        AnchorPane.setBottomAnchor(node,0.0);
        AnchorPane.setLeftAnchor(node,0.0);
        AnchorPane.setRightAnchor(node,0.0);
        AnchorPane.setTopAnchor(node,0.0);
        pane.getChildren().add(node);
    }
}
