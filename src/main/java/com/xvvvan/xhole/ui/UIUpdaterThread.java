package com.xvvvan.xhole.ui;

import com.xvvvan.xhole.engine.XholeEngine;
import com.xvvvan.xhole.ui.data.WebIdentifyData;
import javafx.application.Platform;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TableView;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

public class UIUpdaterThread implements Runnable{
//    public static UIUpdaterThread instance;
    public static final BlockingQueue<WebIdentifyData> taskQueue = new ArrayBlockingQueue<>(10240);
    public static SimpleBooleanProperty removeDead = new SimpleBooleanProperty(false);
    private final TableView<WebIdentifyData> tableView;
    private final ProgressBar progressBar;
    private volatile boolean running = true;
    public static final AtomicInteger Id= new AtomicInteger(1);

    public static SimpleStringProperty timeout = new SimpleStringProperty("1");
    public static SimpleBooleanProperty removeWildCard = new SimpleBooleanProperty(false);
    public UIUpdaterThread(TableView<WebIdentifyData> tableView, ProgressBar progressBar){
        this.tableView = tableView;
        this.progressBar = progressBar;
    }
    public void stopThread() {
        running = false;
    }
    @Override
    public void run() {
        while (running) {
            try {
//                每次最多取500个
                while (!taskQueue.isEmpty()) {
                    WebIdentifyData take = taskQueue.take();
                    take.setId(String.valueOf(Id.getAndIncrement()));
                    Platform.runLater(() -> {
                        tableView.getItems().add(take);

                        if(XholeEngine.tasksNumber.get()!=0){
                            double i = XholeEngine.processNumber.get() / XholeEngine.tasksNumber.get();
                            progressBar.setProgress(i);
                        }

                    });
                }
                Thread.sleep(500); // 5秒更新一次
            } catch (InterruptedException e) {
                // 处理中断异常
                e.printStackTrace();
                Thread.currentThread().interrupt();
            }
        }
    }
//    public Integer getTime() {
//        return Integer.parseInt(timeout.getValue())*1000;
//    }

}
