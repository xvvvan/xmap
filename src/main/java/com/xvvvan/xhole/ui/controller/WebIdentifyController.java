package com.xvvvan.xhole.ui.controller;

import com.alibaba.excel.EasyExcel;
import com.xvvvan.assertmap.controller.AssertMapController;
import com.xvvvan.global.TableController;
import com.xvvvan.global.VirtualThreadPool;
import com.xvvvan.xhole.engine.XholeEngine;
import com.xvvvan.xhole.ui.UIUpdaterThread;
import com.xvvvan.xhole.ui.data.WebIdentifyData;
import com.xvvvan.xhole.ui.util.UrlUtil;
import javafx.application.Platform;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.paint.Color;

import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.*;





public class WebIdentifyController implements TableController, Initializable {
    UIUpdaterThread uiUpdaterThread;
    @FXML
    TextArea textArea;
    @FXML
    TableView<WebIdentifyData> tableView;
    @FXML
    ProgressBar progressBar;
    @FXML
    Label log;
    @FXML
    CheckBox wildCardCheckBox;
    @FXML
    CheckBox evilCheckBox;
    @FXML
    CheckBox removeDeadCheckBox;
    private final ObservableList<WebIdentifyData> list = FXCollections.observableArrayList();
    public final ObservableList<String> important = FXCollections.observableArrayList();
    private XholeEngine engine;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        tableView = initTable();

        engine = new XholeEngine();

        uiUpdaterThread = new UIUpdaterThread(tableView,progressBar);
        UIUpdaterThread.removeWildCard.bind(wildCardCheckBox.selectedProperty());
//        UIUpdaterThread.removeDead.bind(removeDeadCheckBox.selectedProperty());
        VirtualThreadPool.execute(uiUpdaterThread);
        removeDeadCheckBox.selectedProperty().addListener((observableValue, aBoolean, select) -> {
            if(select){
                tableView.getItems().removeIf(item -> item.statuscode==null);
            }
        });
//        if(dangerFinger.get().isEmpty()){
//            String text = AssertMapController.INSTANCE.dangerFingerTextField.getText();
//            String[] split = text.split(",");
//            important.addAll(split);
//        }
//
//
//        dangerFinger.addListener((observableValue, s1, t1) -> important.setAll(s1.split(",")));
    }

    private TableView<WebIdentifyData> initTable(){
        tableView.setEditable(true);
//        tableView.set
        TableColumn<WebIdentifyData, Integer> id = new TableColumn<>("序号");
        TableColumn<WebIdentifyData, String> url = new TableColumn<>("网址");
        TableColumn<WebIdentifyData, String> ip = new TableColumn<>("IP");
        TableColumn<WebIdentifyData, Integer> port = new TableColumn<>("端口");
        TableColumn<WebIdentifyData, String> cms = new TableColumn<>("指纹");
        TableColumn<WebIdentifyData, String> server = new TableColumn<>("服务");
        TableColumn<WebIdentifyData, Integer> statuscode = new TableColumn<>("状态");
        TableColumn<WebIdentifyData, Integer> length = new TableColumn<>("长度");
        TableColumn<WebIdentifyData, String> title = new TableColumn<>("标题");
        TableColumn<WebIdentifyData, String> engine = new TableColumn<>("引擎");
        TableColumn<WebIdentifyData, String> matched = new TableColumn<>("匹配");


        id.setCellValueFactory(param -> new SimpleObjectProperty<>(Integer.valueOf(param.getValue().id)));
        id.setPrefWidth(60);
        url.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().url));
        url.setCellFactory(TextFieldTableCell.forTableColumn());
        url.setPrefWidth(150);
        ip.setCellValueFactory(param -> new SimpleObjectProperty<>(param.getValue().ip));
        ip.setCellFactory(TextFieldTableCell.forTableColumn());
        ip.setPrefWidth(100);
        port.setCellValueFactory(param -> new SimpleObjectProperty<>(param.getValue().port));
        port.setPrefWidth(60);
        cms.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().cms));
        cms.setCellFactory(col -> {
            return new TableCell<WebIdentifyData, String>() {
                @Override
                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                    if (!empty && item != null) {
                        // 检查是否包含特定的字符串
                        boolean containsFingerText = important.contains(item);

                        // 根据条件设置背景颜色
                        if (containsFingerText) {
                            setBackground(new Background(new BackgroundFill(Color.color(1, 0, 0, 0.5), null, null)));
                        } else {
                            setBackground(null);
                        }
                        setText(item);
                    }
                }
            };
        });
//        cms.setCellFactory(TextFieldTableCell.forTableColumn());
        cms.setPrefWidth(100);
        server.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().server));
        server.setPrefWidth(60);
        statuscode.setCellValueFactory(param -> {
            String code = param.getValue().statuscode==null?"0":param.getValue().statuscode;
            return new SimpleObjectProperty<>(Integer.valueOf(code));
        });
        statuscode.setPrefWidth(60);
        length.setCellValueFactory(param -> new SimpleObjectProperty<>(param.getValue().length));
        title.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().title));
        title.setCellFactory(TextFieldTableCell.forTableColumn());
        title.setPrefWidth(100);
        engine.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().engine));
        engine.setPrefWidth(60);
        matched.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().matched));
        matched.setCellFactory(TextFieldTableCell.forTableColumn());
        matched.setPrefWidth(100);
        tableView.getColumns().add(id);
        tableView.getColumns().addAll(new ArrayList<TableColumn<WebIdentifyData,String>>(){{ add(url);add(title);add(ip);}});
        tableView.getColumns().add(port);
        tableView.getColumns().add(statuscode);
        tableView.getColumns().addAll(new ArrayList<TableColumn<WebIdentifyData,String>>(){{add(cms);add(server);}});
        tableView.getColumns().add(length);
        tableView.getColumns().add(engine);
        tableView.getColumns().add(matched);
        tableView.setItems(list);
//        list.add(new WebIdentifyData(1,"1","1",1,"1","80",22,111,""));
        return tableView;
    }

    private void log(String s){
        Platform.runLater(()-> log.setText(s));
    }

    @Override
    public void output() {
//        List<WebIdentifyData> webIdentifyData = list.ge
        Date date = new Date();
        SimpleDateFormat dateFormat= new SimpleDateFormat("yyyyMMddhhmmss");
        String timeName = dateFormat.format(date);
        String filename = "web资产识别" + timeName + ".xlsx";
        EasyExcel.write(filename, WebIdentifyData.class).sheet("cms").doWrite(list);
        log(filename+"output success");
    }

    @Override
    public void clear() {
        list.clear();
        progressBar.setProgress(0.0);
        UIUpdaterThread.Id.set(1);
    }

    @Override
    public void run() {
        progressBar.setProgress(0.0);
        String text = textArea.getText();
        String[] split = text.split("\n");
        List<String>[] webTargets = UrlUtil.getWebTargets(Arrays.asList(split));
        List<String> usefulList = webTargets[0];
        engine.start(usefulList);
    }
    public void submit(List<String> urls){
        //目前任务数量+urls 的length
//        String result = String.join("\n", urls);
//        Platform.runLater(()->{
//            textArea.appendText(result);
//        });
        engine.start(urls);
    }
}
