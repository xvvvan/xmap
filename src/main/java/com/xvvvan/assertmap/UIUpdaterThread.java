package com.xvvvan.assertmap;

import com.xvvvan.assertmap.data.AssertData;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TableView;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

public class UIUpdaterThread implements Runnable{

    public  final BlockingQueue<AssertData> taskQueue = new ArrayBlockingQueue<>(10240);
    private final TableView<AssertData> tableView;
    private final ProgressBar progressBar;
    private volatile boolean running = true;
    public  final AtomicInteger Id= new AtomicInteger(1);
    public  final AtomicInteger TASK_NUMBER= new AtomicInteger(0);
    public  final AtomicInteger PROGRESS_NUMBER= new AtomicInteger(1);
    public  SimpleStringProperty timeout = new SimpleStringProperty("1");
    public UIUpdaterThread(TableView<AssertData> tableView, ProgressBar progressBar){
        this.tableView = tableView;
        this.progressBar = progressBar;
    }

    public void addTask(AssertData assertData) {
            taskQueue.add(assertData);
    }

    public void reset(){
        Id.set(1);
        TASK_NUMBER.set(0);
        taskQueue.clear();
    }

    public void stopThread() {
        running = false;
    }
    @Override
    public void run() {
        while (running) {
            try {

                List<AssertData> latestTask = new ArrayList<>();
//                System.out.println("获取结果"+taskQueue.size()+"更新频率 "+timeout.getValue()+"s");
//                每次最多取500个
                while (!taskQueue.isEmpty() ) {
                    AssertData take = taskQueue.take();
                    take.setId(Id.getAndIncrement());
                    latestTask.add(take);
                }

                Platform.runLater(() -> {
                    tableView.getItems().addAll(latestTask);
                    if(TASK_NUMBER.get()!=0){
                        double i = (double) PROGRESS_NUMBER.get() / TASK_NUMBER.get();
                        progressBar.setProgress(i);
                    }
                });

                Thread.sleep(getTime());

            } catch (InterruptedException e) {
                // 处理中断异常
                Thread.currentThread().interrupt();
            }
        }
    }
    public Integer getTime() {
        return Integer.parseInt(timeout.getValue())*1000;
    }

}
