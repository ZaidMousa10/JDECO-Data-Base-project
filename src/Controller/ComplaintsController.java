package Controller;

import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import DBConnection.DBHandler;

public class ComplaintsController {

    @FXML
    private ComboBox<String> complaintTypeComboBox;
    @FXML
    private TextArea descriptionArea;

    private DBHandler handler;
    private Connection connection;
    private PreparedStatement pst;

    @FXML
    public void initialize() {
        handler = new DBHandler();
        complaintTypeComboBox.getItems().addAll("Service Issue", "Product Quality", "Billing", "Other");
    }

    @FXML
    private void handleSubmit() {
        String complaintType = complaintTypeComboBox.getValue();
        String description = descriptionArea.getText();

        if (complaintType == null || complaintType.isEmpty() || description.isEmpty()) {
            showAlert("Error", "Please fill in all fields");
            return;
        }

        String query = "INSERT INTO Complaint (details, complaintDate, customerId) VALUES (?, ?, ?)";
        connection = handler.getConnection();

        try {
            pst = connection.prepareStatement(query);
            pst.setString(1, description);
            pst.setString(2, new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
            pst.setInt(3, LoginController.getCustomer().getCustomerID());
            pst.executeUpdate();
            showAlert("Success", "Complaint submitted successfully");
        } catch (SQLException e) {
			System.out.println(e.getMessage());
            showAlert("Error", "An error occurred while submitting the complaint");
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}

