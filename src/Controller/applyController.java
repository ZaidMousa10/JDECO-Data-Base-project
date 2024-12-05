package Controller;

import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;

import DBConnection.DBHandler;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;

public class applyController implements Initializable {
    @FXML
    private Button submit;
    @FXML
    private ComboBox<String> serviceTypeComboBox;
    @FXML
    private TextArea locationArea;

    private DBHandler handler;
    private Connection connection;
    private PreparedStatement pst;

    @Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		handler = new DBHandler();
		connection = handler.getConnection();
		String query = "SELECT * FROM ServiceType";
		try {
			pst = connection.prepareStatement(query);
			try (ResultSet resultSet = pst.executeQuery()) {
				while(resultSet.next()) {
					int serviceTypeID = resultSet.getInt("serviceTypeID");
					String serviceTypeName = resultSet.getString("serviceTypeName");
					serviceTypeComboBox.getItems().add(serviceTypeName);
				} 
			}
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}
	}
    @FXML
    public void handleSubmit(ActionEvent event) {
        String selectedServiceType = serviceTypeComboBox.getValue();
        String location = locationArea.getText();
        
        if (selectedServiceType == null || selectedServiceType.isEmpty() || location.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Form Error!", "Please enter all details.");
            return;
        }
        
        try {
            connection = handler.getConnection();
            
            String insertServiceQuery = "INSERT INTO Service(serviceDate, debt, serviceTypeId, customerId) "
                                        + "VALUES (NOW(), 0, (SELECT serviceTypeId FROM ServiceType WHERE serviceTypeName = ?), ?)";
            pst = connection.prepareStatement(insertServiceQuery, PreparedStatement.RETURN_GENERATED_KEYS);
            pst.setString(1, selectedServiceType);
            pst.setInt(2, LoginController.getCustomer().getCustomerID());
            pst.executeUpdate();
            
            ResultSet generatedKeys = pst.getGeneratedKeys();
            if (generatedKeys.next()) {
                int serviceId = generatedKeys.getInt(1);
                
                String insertTaskQuery = "INSERT INTO Task(toDoDate, serviceId, employeeId) VALUES (NOW(), ?, NULL)";
                PreparedStatement pstTask = connection.prepareStatement(insertTaskQuery);
                pstTask.setInt(1, serviceId);
                pstTask.executeUpdate();
                pstTask.close();

                String insertServiceDetailQuery = "INSERT INTO ServiceDetail(serviceId, location) VALUES (?, ?)";
                PreparedStatement pstServiceDetail = connection.prepareStatement(insertServiceDetailQuery);
                pstServiceDetail.setInt(1, serviceId);
                pstServiceDetail.setString(2, location);
                pstServiceDetail.executeUpdate();
                pstServiceDetail.close();

                showAlert(Alert.AlertType.INFORMATION, "Success", "Service applied successfully.");
            }
        } catch (SQLException e) {
			System.out.println(e.getMessage());
            showAlert(Alert.AlertType.ERROR, "Database Error", "Could not apply for service.");
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
