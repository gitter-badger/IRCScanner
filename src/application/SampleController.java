package application;

import java.util.HashMap;
import java.util.List;

import application.Client.IrcListener;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Side;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;

public class SampleController {
    @FXML
    private ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList();
    private HashMap                       map          = new HashMap();
    public SploitParse                    sploit       = new SploitParse();
    @FXML
    private Label                         nbCtcp;
    @FXML
    private PieChart                      chart;
    @FXML
    private TextField                     serverTxtField;
    @FXML
    private TextField                     portTxtField;
    @FXML
    private TextField                     nickTxtField;
    @FXML
    private TextField                     channelTxtField;
    @FXML
    private TableView<UsrIrc>             tview;
    @FXML
    private TableColumn                   nickColumn;
    @FXML
    private TableColumn                   clientColumn;
    @FXML
    private TableColumn                   exploitColumn;
    private Client                        client;

    public SampleController() {}

    public void addToTable(String nick, String client) {
        final ObservableList<UsrIrc> data = tview.getItems();

        data.add(new UsrIrc(nick, client, this));
    }

    public void addlist(String client) {
        pieChartData.clear();

        Object i = map.get(client);

        if (map.containsKey(client)) {
            map.remove(client, i);
            map.put(client, (Integer) i + 1);
        } else {
            map.put(client, 1);
        }

        map.forEach((k, v) -> pieChartData.add(new PieChart.Data((String) k, (int) v)));
        Platform.setImplicitExit(false);
        Platform.runLater(() -> chart.setData(pieChartData));
    }

    @FXML
    public void initialize() {
        nickColumn.setCellValueFactory(new PropertyValueFactory<UsrIrc, String>("nick"));
        clientColumn.setCellValueFactory(new PropertyValueFactory<UsrIrc, String>("client"));
        exploitColumn.setCellValueFactory(new PropertyValueFactory<UsrIrc, String>("exploit"));
    }

    public void startTheClient() {
        getChart().setLegendSide(Side.LEFT);
        this.client = new Client(serverTxtField.getText(),
                                 nickTxtField.getText(),
                                 channelTxtField.getText(),
                                 Integer.parseInt(portTxtField.getText()),
                                 new MagicListener());

        try {
            this.client.connect();
        } catch (Exception e) {
            System.out.println("pépin");
        }
    }

    public PieChart getChart() {
        return chart;
    }

    public void setChart(PieChart chart) {
        this.chart = chart;
    }

    public String getNbCtcp() {
        return nbCtcp.getText();
    }

    public void setNbCtcp(String nbCtcpStr) {
        this.nbCtcp.setText(nbCtcpStr);
    }

    public class MagicListener implements IrcListener {

        // Indique si le who est terminé
        private boolean whoTermine;

        public MagicListener() {
            this.whoTermine = false;
        }

        // Traitement de la liste des users -> CTCP pour tous l'monde
        @Override
        public void listReceived(List<String> pseudos) {
            String magicString = new String(new byte[] { 0x01 });

            for (String pseudo : pseudos) {
                client.send("PRIVMSG " + pseudo + " " + magicString + "VERSION" + magicString);
            }
        }

        // Dans l'cas ou le serv reply "messageTargetTooFast" -> wait 15 sec
        @Override
        public void messageTargetTooFastReceived(String string) {
            try {
                Thread.sleep(15000);
            } catch (InterruptedException e) {}
        }

        // Traitement du MOTD -> Send le who
        @Override
        public void motdReceived() {
            client.send("WHO " + channelTxtField.getText());
        }

        // Traitement des notices (ajout dans l'table & chart)
        @Override
        public void noticeReceived(String pseudo, String version) {
            String versionSimple = version.split(" ")[0];

            addToTable(pseudo, version);
            Platform.runLater(
                () -> {
                    addlist(versionSimple);
                });
            Platform.runLater(
                () -> {
                    setNbCtcp(Integer.toString(Integer.parseInt(getNbCtcp()) + 1));
                });
        }

        @Override
        public void otherMessageReceived(String message) {

            // System.out.println("message non traité : " + message);
        }

        // Reply au ping
        @Override
        public void pingReceived(String count) {
            client.send("PONG :" + count);
        }
    }
}