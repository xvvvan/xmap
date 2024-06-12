package com.xvvvan.assertmap.controller;

import com.google.common.hash.Hashing;

import com.xvvvan.global.TableController;
import com.xvvvan.assertmap.UIUpdaterThread;
import com.xvvvan.config.AssertConfig;
import com.xvvvan.assertmap.data.AssertData;
import com.xvvvan.assertmap.data.AssertData1;
import com.xvvvan.global.VirtualThreadPool;
import com.xvvvan.assertmap.task.FofaTask;
import com.xvvvan.assertmap.task.HunterTask;
import com.xvvvan.assertmap.task.MatrixTask;
import com.xvvvan.util.AddressUtil;
import com.xvvvan.util.FxmlUtil;
import com.xvvvan.util.Pair;
import com.xvvvan.global.Http;
import com.xvvvan.xhole.ui.controller.WebIdentifyController;
import javafx.application.Platform;

import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;

import javafx.util.converter.IntegerStringConverter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
//import javax.net.http.HttpResponse;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.Semaphore;
import java.util.stream.Collectors;


public class AssertMapController implements Initializable, TableController {
    public static AssertMapController INSTANCE;
    @FXML
    public TabPane mainPane;
    @FXML
    public PasswordField fofa_email;
    @FXML
    public PasswordField fofa_key;
    @FXML
    public TextField fofa_api;
    @FXML
    public PasswordField matrix_key;
    @FXML
    public TextField matrix_api;
    @FXML
    public PasswordField hunter_key;
    @FXML
    public TextField hunter_api;
    @FXML
    public CheckBox fingerToggle;

    @FXML
    ComboBox<String> choice;
    @FXML
    TextArea textArea;
    @FXML
    TextField number;
    @FXML
    TextField pagenum;
    @FXML
    TextField icon;
    @FXML
    TableView<AssertData> tableView;
    @FXML
    Label logLabel;
    ObservableList<AssertData> list = FXCollections.observableArrayList();
    private final Logger logger = LoggerFactory.getLogger(AssertMapController.class);
    @FXML
    CheckBox checkBox;
    @FXML
    ProgressBar progressBar;

    @FXML
    Spinner<Integer> concurrentSpinner;
    @FXML
    Spinner<Integer> timeoutSpinner;
    @FXML
    public TextField dangerFingerTextField;
    public AssertConfig config;
    public UIUpdaterThread uiThread;
    WebIdentifyController webController;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        choice.getItems().add("fofa");
        choice.getItems().add("hunter");
//        choice.getItems().add("zoomeye");
        choice.getItems().add("matrix");
        choice.setValue("fofa");
        initTable();
        uiThread = new UIUpdaterThread(tableView,progressBar);
        VirtualThreadPool.execute(uiThread);
        INSTANCE = this;

        this.icon.setOnDragOver(this::drag);
        this.icon.setOnAction(this::getUrlIconHash);

        this.config = new AssertConfig();
        this.config.refresh();
        this.initConfig();

        fofa_key.setOnAction(this::visible);
        this.loadWebIdentifyView();
        ListChangeListener<AssertData> listChangeListener = c -> {
            c.next();
            List<? extends AssertData> addedSubList = c.getAddedSubList();
            ArrayList<String> targets = new ArrayList<>();
            for (AssertData assertData : addedSubList) {
                targets.add(assertData.url.get());
            }

            webController.submit(targets);
            this.log("new web target "+addedSubList.size());

        };

        fingerToggle.selectedProperty().addListener(observable -> {
            if(fingerToggle.isSelected()){
                tableView.getItems().addListener(listChangeListener);
            }else {
                tableView.getItems().removeListener(listChangeListener);
            }
        });
        SpinnerValueFactory.IntegerSpinnerValueFactory cValueFactory  = new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 1024, 1);
        concurrentSpinner.setValueFactory(cValueFactory);
        concurrentSpinner.getValueFactory().setValue(32);
        SpinnerValueFactory.IntegerSpinnerValueFactory tValueFactory  = new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 10, 1);
        timeoutSpinner.setValueFactory(tValueFactory);
        timeoutSpinner.getValueFactory().setValue(6);
        concurrentSpinner.valueProperty().addListener((observableValue, integer, t1) -> VirtualThreadPool.limit = new Semaphore(t1));
        timeoutSpinner.valueProperty().addListener((observableValue, integer, t1) -> {
            Http.timeout.set(t1);
        });
        webController.important.setAll(dangerFingerTextField.getText().split(","));
        dangerFingerTextField.textProperty().addListener((observableValue, s, t1) -> {

            webController.important.setAll(t1.split(","));
        });
//        webController.

    }

//    public void addTab(int index,String title,Parent node){
//        Tab tab = new Tab(title);
//        tab.setClosable(false);
//        tab.setContent(node);
//        mainPane.getTabs().add(index,tab);
//    }
    private void loadWebIdentifyView(){
        Pair<Optional<Parent>, Object> pair = FxmlUtil.loadView("webidentify");
        assert pair != null;
        pair.getKey().ifPresent((node)->{
            Tab tab = new Tab("指纹");
            tab.setClosable(false);
            tab.setContent(node);
            mainPane.getTabs().add(1,tab);
//            mainPane.getSelectionModel().select(tab);
        });
        webController = (WebIdentifyController) pair.getValue();
    }
    private void visible(ActionEvent event){
//        PasswordField source = (PasswordField) event.getSource();
//        source.set
    }

    private void initConfig() {
        fofa_email.setText(config.FOFA_EMAIL);
        fofa_key.setText(config.FOFA_KEY);
        fofa_api.setText(config.FOFA_API);

        hunter_api.setText(config.HUNTER_API);
        hunter_key.setText(config.HUNTER_KEY_STRING);

        matrix_api.setText(config.MATRIX_API);
        matrix_key.setText(config.MATRIX_KEY);
        dangerFingerTextField.setText(config.DANGER_FINGER);
    }

    private void getUrlIconHash(ActionEvent event) {
        VirtualThreadPool.execute(()->{
            HttpResponse<byte[]> httpResponse = Http.get(icon.getText().trim());
            assert httpResponse != null;
            byte[] body = httpResponse.body();
            String s = Base64.getMimeEncoder().encodeToString(body);
            Platform.runLater(()->{
                String iconHash = getIconHash(s);
                icon.setText(iconHash);
            });
        });

    }
    @Override
    public void output() {
        ObservableList<AssertData> items = tableView.getItems();
        if(items.isEmpty()){
            log("[-] 结果为空");
            return;
        }
        List<AssertData1> output = new ArrayList<>();
        for (AssertData item : items) {
            AssertData1 assertData1 = item.toData();
            output.add(assertData1);
        }
        Date now = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
        String currentTime = dateFormat.format(now);
        String filename = currentTime+choice.getValue()+".xlsx";

        try {
            exportToExcel(output,filename);
            log("output success"+filename);
        } catch (IOException e) {
            log(e.getMessage());
        }

    }

    public void generateF(ActionEvent event){

        StringBuilder logSb = new StringBuilder();
        StringBuilder sb = new StringBuilder();
        String input = this.textArea.getText().trim();
        String[] split = input.split("\n");
        logSb.append("累计").append(split.length).append("个").append("\t");

        for (int i = 0; i < split.length; i++) {

            String line = split[i].trim();
            if(AddressUtil.isIPAddress(line.trim())){
                sb.append("ip=").append(AddressUtil.convertIPToC(line));
                if(i!=split.length-1){
                    sb.append("\n");
                }
            }else if(!AddressUtil.extractRootDomain(line.trim()).isEmpty()){
                String rootDomain = AddressUtil.extractRootDomain(line.trim());
                sb.append("domain=").append(rootDomain).append("\n");

                String[] split1 = rootDomain.split("\\.");
                sb.append("cert=").append(split1[0]);
                if(i!=split.length-1){
                    sb.append("\n");
                }
            }else {
                logger.error("unkown type "+line.trim());
            }
        }
        int length = sb.toString().split("\n").length;
        logSb.append("生成").append(length).append("个查询语句");

        Platform.runLater(()->{
            textArea.clear();
            textArea.setText(sb.toString());
            log(logSb.toString());
        });
    }
    public void generateM(ActionEvent event){

        StringBuilder logSb = new StringBuilder();
        StringBuilder sb = new StringBuilder();
        String input = this.textArea.getText().trim();
        String[] split = input.split("\n");
        logSb.append("累计").append(split.length).append("个").append("\t");

        for (int i = 0; i < split.length; i++) {

            String line = split[i].trim();
            if(AddressUtil.isIPAddress(line.trim())){
                sb.append("ip=").append(AddressUtil.convertIPToC(line));
                if(i!=split.length-1){
                    sb.append("\n");
                }
            }else if(!AddressUtil.extractRootDomain(line.trim()).isEmpty()){
                String rootDomain = AddressUtil.extractRootDomain(line.trim());
                sb.append("domain=").append(rootDomain).append("\n");
                sb.append("cert.dns=").append(rootDomain);
                if(i!=split.length-1){
                    sb.append("\n");
                }
            }else {
                logger.error("unkown type "+line.trim());
            }
        }
        int length = sb.toString().split("\n").length;
        logSb.append("生成").append(length).append("个查询语句");

        Platform.runLater(()->{
            textArea.clear();
            textArea.setText(sb.toString());
            log(logSb.toString());
        });
    }
    public void generateH(ActionEvent event){

        StringBuilder logSb = new StringBuilder();
        StringBuilder sb = new StringBuilder();
        String input = this.textArea.getText().trim();
        String[] split = input.split("\n");
        logSb.append("累计").append(split.length).append("个").append("\t");

        for (int i = 0; i < split.length; i++) {

            String line = split[i].trim();
            if(AddressUtil.isIPAddress(line.trim())){
                sb.append("ip=").append(AddressUtil.convertIPToC(line));
                if(i!=split.length-1){
                    sb.append("\n");
                }
            }else if(!AddressUtil.extractRootDomain(line.trim()).isEmpty()){
                String rootDomain = AddressUtil.extractRootDomain(line.trim());
                sb.append("domain=").append(rootDomain).append("\n");
                sb.append("cert=").append(rootDomain);
                if(i!=split.length-1){
                    sb.append("\n");
                }
            }else {
                logger.error("unkown type "+line.trim());
            }
        }
        int length = sb.toString().split("\n").length;
        logSb.append("生成").append(length).append("个查询语句");

        Platform.runLater(()->{
            textArea.clear();
            textArea.setText(sb.toString());
            log(logSb.toString());
        });
    }

    /**
     * 获取icon hash 添加字符串icon_hash=
     */
    public void iconHash(ActionEvent event) {
        String[] split = textArea.getText().trim().split("\n");
        List<String> collect = Arrays.stream(split).map(s -> "icon_hash=" + s ).collect(Collectors.toList());
        String join = String.join("\n", collect);
        Platform.runLater(()->{
            textArea.setText(join);
        });
    }
    public void drag(DragEvent event){
        Dragboard db = event.getDragboard();
        if (db.hasFiles()) {
            // 获取拖拽的文件列表
            java.io.File file = db.getFiles().get(0);
            // 在这里计算文件的哈希值
            String hashValue = calculateHash(file);
            // 将哈希值显示在文本框中
            Platform.runLater((()->{
                icon.setText(hashValue);
            }));
        }
//        event.setDropCompleted(success);
//        event.consume();
    }
    @Override
    public void clear() {
        list.clear();
        uiThread.Id.set(0);
        progressBar.setProgress(0.0);
    }

    @Override
    public void run() {
        progressBar.setProgress(0.0);
        //加载上次的数据
        String value = choice.getValue();
        String page = pagenum.getText();
        int size = Integer.parseInt(number.getText());
        boolean isAll = checkBox.isSelected();
        String text = textArea.getText();
        String[] params = text.split("\n");
//        ui线程
        uiThread.TASK_NUMBER.set(params.length);
//        boolean fingerSelected = fingerToggle.isSelected();
        switch (value){
            case "fofa":{
                VirtualThreadPool.execute(()->{
                    for (String param : params) {
                        Future submit = VirtualThreadPool.submit(new FofaTask(page, size, param, isAll));

                        try {
                            submit.get();

                        } catch (InterruptedException |ExecutionException e) {
                            this.log(e.getMessage());
                        }finally {
                            uiThread.PROGRESS_NUMBER.incrementAndGet();
                        }
                    }
                });
            }
            break;
            case "matrix":{
                VirtualThreadPool.execute(()->{
                    for (String param : params) {
                        Thread unstarted = new Thread(new MatrixTask(page, size, param));
//                    Thread unstarted = Thread.ofVirtual().unstarted(new MatrixTask(page, size, param));
                        unstarted.start();
                        try {
                            uiThread.PROGRESS_NUMBER.incrementAndGet();
                            unstarted.join();
                        } catch (InterruptedException e) {
                            this.log(e.getMessage());
                        }
                    }
                });
            }
            break;
//            domain.suffix="czmetro.net.cn"
            case "hunter":{
                VirtualThreadPool.execute(()->{
                    for (String param : params) {
                        Thread unstarted = new Thread(new HunterTask(page, size, param));
//                    Thread unstarted = Thread.ofVirtual().unstarted(new HunterTask(page, size, param));
                        unstarted.start();
                        try {
                            uiThread.PROGRESS_NUMBER.incrementAndGet();
                            unstarted.join();
                        } catch (InterruptedException e) {
                            this.log(e.getMessage());
                        }
                    }
                });
            }
            break;
            default :
        }
    }
    public TableView<AssertData> initTable(){
//        TableView<AssertData> tableView = new TableView<>();
        tableView.setEditable(true);

        TableColumn<AssertData, Integer> idColumn = new TableColumn<>("序号");
        idColumn.setCellValueFactory(cellData -> cellData.getValue().idProperty().asObject());
        idColumn.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));

        TableColumn<AssertData, String> urlColumn = new TableColumn<>("网址");
        urlColumn.setCellValueFactory(cellData -> cellData.getValue().url);
        urlColumn.setCellFactory(TextFieldTableCell.forTableColumn());
//        urlColumn.setCellFactory(new Callback<TableColumn<AssertData, String>, TableCell<AssertData, String>>() {
//            @Override
//            public TableCell<AssertData, String> call(TableColumn<AssertData, String> assertDataStringTableColumn) {
//                return new TableCell<AssertData, String>(){
//                    @Override
//                    protected void updateItem(String item, boolean empty) {
//                        super.updateItem(item, empty);
//                        if (empty) {
//                            setGraphic(null);
//                        }else {
//                            setOnMouseClicked(event -> {
//                                Desktop d = Desktop.getDesktop();
//                                try {
//                                    d.browse(new URI(item));
//                                } catch (URISyntaxException | IOException e) {
//                                    e.printStackTrace();
//                                }
//                            });
//                        }
//                    }
//                };
//            }
//        });

        TableColumn<AssertData, String> domainColumn = new TableColumn<>("域名");
        domainColumn.setCellValueFactory(cellData -> cellData.getValue().domain);
        domainColumn.setCellFactory(TextFieldTableCell.forTableColumn());

        TableColumn<AssertData, String> titleColumn = new TableColumn<>("标题");
        titleColumn.setCellValueFactory(cellData -> cellData.getValue().title);
        titleColumn.setCellFactory(TextFieldTableCell.forTableColumn());

        TableColumn<AssertData, String> ipColumn = new TableColumn<>("IP");
        ipColumn.setCellValueFactory(cellData -> cellData.getValue().ip);
        ipColumn.setCellFactory(TextFieldTableCell.forTableColumn());

        TableColumn<AssertData, Integer> portColumn = new TableColumn<>("端口");
        portColumn.setCellValueFactory(cellData -> cellData.getValue().port.asObject());
        portColumn.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));

        TableColumn<AssertData, String> iconColumn = new TableColumn<>("图标");
        iconColumn.setCellValueFactory(cellData -> cellData.getValue().icon);
        iconColumn.setCellFactory(TextFieldTableCell.forTableColumn());

        TableColumn<AssertData, String> protocolColumn = new TableColumn<>("协议");
        protocolColumn.setCellValueFactory(cellData -> cellData.getValue().protocol);
        protocolColumn.setCellFactory(TextFieldTableCell.forTableColumn());

        TableColumn<AssertData, String> serverColumn = new TableColumn<>("服务");
        serverColumn.setCellValueFactory(cellData -> cellData.getValue().server);
        serverColumn.setCellFactory(TextFieldTableCell.forTableColumn());

        TableColumn<AssertData, String> platformColumn = new TableColumn<>("平台");
        platformColumn.setCellValueFactory(cellData -> cellData.getValue().platform);
        platformColumn.setCellFactory(TextFieldTableCell.forTableColumn());

        TableColumn<AssertData, String> certColumn = new TableColumn<>("证书");
        certColumn.setCellValueFactory(cellData -> cellData.getValue().cert);
        certColumn.setCellFactory(TextFieldTableCell.forTableColumn());

        TableColumn<AssertData, String> icpColumn = new TableColumn<>("备案");
        icpColumn.setCellValueFactory(cellData -> cellData.getValue().icp);
        icpColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        TableColumn<AssertData, String> cityColumn = new TableColumn<>("城市");
        cityColumn.setCellValueFactory(cellData -> cellData.getValue().city);
        cityColumn.setCellFactory(TextFieldTableCell.forTableColumn());

        TableColumn<AssertData, String> companyColumn = new TableColumn<>("公司");
        companyColumn.setCellValueFactory(cellData -> cellData.getValue().company);
        companyColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        tableView.setItems(list);
        tableView.getColumns().addAll(idColumn, urlColumn,domainColumn,titleColumn,ipColumn,portColumn,iconColumn,protocolColumn,serverColumn,platformColumn,certColumn,icpColumn,cityColumn,companyColumn);
        return tableView;
    }
    public void exportToExcel(List<AssertData1> dataList, String filePath) throws IOException {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Data");

        // Create header row
        Row headerRow = sheet.createRow(0);
        headerRow.createCell(0).setCellValue("ID");
        headerRow.createCell(1).setCellValue("URL");
        headerRow.createCell(2).setCellValue("Domain");
        headerRow.createCell(3).setCellValue("Title");
        headerRow.createCell(4).setCellValue("IP");
        headerRow.createCell(5).setCellValue("Port");
        headerRow.createCell(6).setCellValue("Protocol");
        headerRow.createCell(7).setCellValue("Server");
        headerRow.createCell(8).setCellValue("Platform");
        headerRow.createCell(9).setCellValue("Cert");
        headerRow.createCell(10).setCellValue("ICP");
        headerRow.createCell(11).setCellValue("City");
        headerRow.createCell(12).setCellValue("Company");

        // Populate data rows
        int rowNum = 1;
        for (AssertData1 data : dataList) {
            Row dataRow = sheet.createRow(rowNum++);
            dataRow.createCell(0).setCellValue(data.id);
            dataRow.createCell(1).setCellValue(data.url);
            dataRow.createCell(2).setCellValue(data.domain);
            dataRow.createCell(3).setCellValue(data.title);
            dataRow.createCell(4).setCellValue(data.ip);
            dataRow.createCell(5).setCellValue(data.port);
            dataRow.createCell(6).setCellValue(data.protocol);
            dataRow.createCell(7).setCellValue(data.server);
            dataRow.createCell(8).setCellValue(data.platform);
            dataRow.createCell(9).setCellValue(data.cert);
            dataRow.createCell(10).setCellValue(data.icp);
            dataRow.createCell(11).setCellValue(data.city);
            dataRow.createCell(12).setCellValue(data.company);
        }
        // Auto size columns
        for (int i = 0; i < headerRow.getLastCellNum(); i++) {
            sheet.autoSizeColumn(i);
        }
        // Write to file
        FileOutputStream outputStream = new FileOutputStream(filePath);
        workbook.write(outputStream);
        workbook.close();
        outputStream.close();
    }

    public void log(String text){
        Platform.runLater(()->logLabel.setText(text));
    }
    private String calculateHash(java.io.File file) {
        try {
            byte[] digest = Files.readAllBytes(file.toPath());
            String s = Base64.getMimeEncoder().encodeToString(digest);
            return getIconHash(s);
            // 转换哈希值为十六进制字符串

        } catch (Exception e) {
            log(e.getMessage());
            return "";
        }
    }
    public static String getIconHash(String f) {
        int murmu = Hashing
                .murmur3_32()
                .hashString(f.replaceAll("\r","" )+"\n", StandardCharsets.UTF_8)
                .asInt();
        return String.valueOf(murmu);
    }

    public void save(ActionEvent event) {
        config.save();
        log("配置保存成功！");
        Alert alert = new Alert(Alert.AlertType.INFORMATION, "保存成功");
        alert.show();
    }

    public void openFinger(ActionEvent event) {

        int raw = tableView.getColumns().size();
        if(14==raw){
            TableColumn<AssertData, String> lengthColumn = new TableColumn<>("长度");
            lengthColumn.setCellValueFactory(cellData -> cellData.getValue().length);
            lengthColumn.setCellFactory(TextFieldTableCell.forTableColumn());
            TableColumn<AssertData, String> fingerColumn = new TableColumn<>("指纹");
            fingerColumn.setCellValueFactory(cellData -> cellData.getValue().finger);
            fingerColumn.setCellFactory(TextFieldTableCell.forTableColumn());
            TableColumn<AssertData, String> matchedColumn = new TableColumn<>("匹配值");
            matchedColumn.setCellValueFactory(cellData -> cellData.getValue().matched);
            matchedColumn.setCellFactory(TextFieldTableCell.forTableColumn());

            tableView.getColumns().addAll(4,Arrays.asList(lengthColumn,fingerColumn,matchedColumn));
        }else {
            tableView.getColumns().remove(4,7);
        }

    }

    public void reset(ActionEvent event) {
        fofa_key.clear();
        fofa_email.clear();
        hunter_key.clear();
        matrix_key.clear();
        config.save();
        concurrentSpinner.getValueFactory().setValue(32);
        timeoutSpinner.getValueFactory().setValue(6);
        Alert alert = new Alert(Alert.AlertType.INFORMATION, "重置成功");
        alert.show();
    }
}
