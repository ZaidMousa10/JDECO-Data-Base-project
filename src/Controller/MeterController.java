package Controller;

import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ResourceBundle;

import DBConnection.DBHandler;
import application.Meter;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;

public class MeterController implements Initializable {
    @FXML
    private TextField meterDetail;
    @FXML
    private TextField payType;
    @FXML
    private DatePicker dateOfDeployment;
    @FXML
    private TableView<Meter> meterTable;
    @FXML
    private TableColumn<Meter, Integer> meterIdColumn;
    @FXML
    private TableColumn<Meter, String> meterDetailColumn;
    @FXML
    private TableColumn<Meter, String> payTypeColumn;
    @FXML
    private TableColumn<Meter, String> deploymentDateColumn;
    @FXML
    private TableColumn<Meter, String> customerNameColumn;
    @FXML
    private TableColumn<Meter, String> employeeNameColumn;
    private DBHandler handler;
    private Connection connection;
    private PreparedStatement pst;
    private ObservableList<Meter> meterList;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        handler = new DBHandler();
        meterList = FXCollections.observableArrayList();
        dateOfDeployment.setEditable(false);
        meterIdColumn.setCellValueFactory(new PropertyValueFactory<>("meterId"));
        meterDetailColumn.setCellValueFactory(new PropertyValueFactory<>("meterDetail"));
        payTypeColumn.setCellValueFactory(new PropertyValueFactory<>("payType"));
        deploymentDateColumn.setCellValueFactory(new PropertyValueFactory<>("dateOfDeployment"));
        employeeNameColumn.setCellValueFactory(new PropertyValueFactory<>("employeeName"));
        customerNameColumn.setCellValueFactory(new PropertyValueFactory<>("customerName"));
        loadMeterData();
    }

    @FXML
    public void handleSubmit(ActionEvent event) {
        String insertQuery = "INSERT INTO Meter (meterDetail, payType, dateOfDeployment, employeeId) VALUES (?, ?, ?, ?)";
        connection = handler.getConnection();
        try {
            pst = connection.prepareStatement(insertQuery);
            pst.setString(1, meterDetail.getText());
            pst.setString(2, payType.getText());
            pst.setDate(3, java.sql.Date.valueOf(dateOfDeployment.getValue()));
            pst.setInt(4, LoginController.getEmployee().getEmployeeId());
            pst.executeUpdate();
            showAlert(Alert.AlertType.INFORMATION, "Success", "Meter record inserted successfully.");
            loadMeterData();
        } catch (SQLException e) {
			System.out.println(e.getMessage());
            showAlert(Alert.AlertType.ERROR, "Database Error", "Could not insert meter data.");
        } finally {
            try {
                if (pst != null) pst.close();
                if (connection != null) connection.close();
            } catch (SQLException e) {
    			System.out.println(e.getMessage());
            }
        }
    }

    private void loadMeterData() {
        meterList.clear();
        String query = "SELECT * FROM Meter";
        String query1 = "SELECT C.fullName FROM Customer C "
                      + "JOIN Service S ON C.customerId = S.customerId "
                      + "JOIN Task T ON S.serviceId = T.serviceId "
                      + "JOIN ServiceDetail SD ON T.serviceId = SD.serviceId "
                      + "JOIN Meter M ON SD.meterId = M.meterId "
                      + "WHERE M.meterId = ?";
        String query2 = "SELECT E.fullName FROM Employee E "
                      + "JOIN Meter M ON E.employeeId = M.employeeId "
                      + "WHERE M.meterId = ?";
        connection = handler.getConnection();
        try {
            pst = connection.prepareStatement(query);
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                int meterId = rs.getInt("meterId");
                String meterDetail = rs.getString("meterDetail");
                String payType = rs.getString("payType");
                LocalDate dateOfDeployment = rs.getDate("dateOfDeployment").toLocalDate();
                String customerName = null;
                String employeeName = null;
                // Fetch customer name
                try (PreparedStatement pst1 = connection.prepareStatement(query1)) {
                    pst1.setInt(1, meterId);
                    ResultSet rs1 = pst1.executeQuery();
                    if (rs1.next()) {
                        customerName = rs1.getString("fullName");
                    }
                }
                // Fetch employee name
                try (PreparedStatement pst2 = connection.prepareStatement(query2)) {
                    pst2.setInt(1, meterId);
                    ResultSet rs2 = pst2.executeQuery();
                    if (rs2.next()) {
                        employeeName = rs2.getString("fullName");
                    }
                }
                meterList.add(new Meter(meterId, meterDetail, payType, customerName, employeeName, dateOfDeployment));
            }
            meterTable.setItems(meterList);
        } catch (SQLException e) {
			System.out.println(e.getMessage());
        } finally {
            try {
                if (pst != null) pst.close();
                if (connection != null) connection.close();
            } catch (SQLException e) {
    			System.out.println(e.getMessage());
            }
        }
    }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
