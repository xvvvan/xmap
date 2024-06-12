package com.xvvvan;

import com.xvvvan.util.FxmlUtil;
import com.xvvvan.util.Pair;
import javafx.application.Application;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.util.Optional;

/**
 * @author Xvvvan
 */
public class App extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        Pair<Optional<Parent>, Object> pair = FxmlUtil.loadView("assertmap");
        pair.getKey().ifPresent(parent -> {
            Scene scene = new Scene(parent);
            stage.setScene(scene);
            stage.setTitle("xmap by xvvvan");
            stage.show();
        });
    }

    /**
     * exit
     * @throws Exception
     */
    @Override
    public void stop() throws Exception {
        super.stop();
        System.exit(1);

    }
}
