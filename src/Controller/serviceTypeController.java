package Controller;

import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;

import DBConnection.DBHandler;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.TableView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import application.ServiceType;

public class serviceTypeController implements Initializable {
    @FXML
    private Button submit, edit, delete;
    @FXML
    private TextField serviceTypeName;
    @FXML
    private TableView<ServiceType> serviceTypeTable;
    @FXML
    private TableColumn<ServiceType, String> serviceTypeColumn;

    private DBHandler handler;
    private Connection connection;
    private PreparedStatement pst;

    private ObservableList<ServiceType> serviceTypeList = FXCollections.observableArrayList();

    @Override
    public void initialize(URL arg0, ResourceBundle arg1) {
        handler = new DBHandler();
        loadServiceTypes();
        serviceTypeColumn.setCellValueFactory(new PropertyValueFactory<>("serviceTypeName"));
        serviceTypeTable.setItems(serviceTypeList);
    }

    @FXML
    public void handleSubmit(ActionEvent event) {
        try {
            String name = serviceTypeName.getText();
            if (!name.isEmpty()) {
                connection = handler.getConnection();
                String insertQuery = "INSERT INTO ServiceType(serviceTypeName) VALUES (?)";
                pst = connection.prepareStatement(insertQuery);
                pst.setString(1, name);
                pst.executeUpdate();
                showAlert(AlertType.INFORMATION, "Success", "Service Type Added", "The service type has been successfully added.");
                loadServiceTypes(); // Reload the table
            } else {
                showAlert(AlertType.ERROR, "Error", "Empty Field", "Service type name cannot be empty.");
            }
        } catch (SQLException e) {
            showAlert(AlertType.ERROR, "Database Error", "Failed to Add Service Type", "An error occurred while adding the service type. Please try again.");
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

    @FXML
    public void handleEdit(ActionEvent event) {
        ServiceType selectedServiceType = serviceTypeTable.getSelectionModel().getSelectedItem();
        if (selectedServiceType != null) {
            try {
                String name = serviceTypeName.getText();
                if (!name.isEmpty()) {
                    connection = handler.getConnection();
                    String updateQuery = "UPDATE ServiceType SET serviceTypeName = ? WHERE serviceTypeId = ?";
                    pst = connection.prepareStatement(updateQuery);
                    pst.setString(1, name);
                    pst.setInt(2, selectedServiceType.getServiceTypeId());
                    pst.executeUpdate();
                    showAlert(AlertType.INFORMATION, "Success", "Service Type Updated", "The service type has been successfully updated.");
                    loadServiceTypes(); // Reload the table
                } else {
                    showAlert(AlertType.ERROR, "Error", "Empty Field", "Service type name cannot be empty.");
                }
            } catch (SQLException e) {
                showAlert(AlertType.ERROR, "Database Error", "Failed to Update Service Type", "An error occurred while updating the service type. Please try again.");
    			System.out.println(e.getMessage());
            } finally {
                try {
                    if (pst != null) pst.close();
                    if (connection != null) connection.close();
                } catch (SQLException e) {
        			System.out.println(e.getMessage());
                }
            }
        } else {
            showAlert(AlertType.ERROR, "No Selection", "No Service Type Selected", "Please select a service type to edit.");
        }
    }

    @FXML
    public void handleDelete(ActionEvent event) {
        ServiceType selectedServiceType = serviceTypeTable.getSelectionModel().getSelectedItem();
        if (selectedServiceType != null) {
            try {
                connection = handler.getConnection();
                String deleteQuery = "DELETE FROM ServiceType WHERE serviceTypeId = ?";
                pst = connection.prepareStatement(deleteQuery);
                pst.setInt(1, selectedServiceType.getServiceTypeId());
                pst.executeUpdate();
                showAlert(AlertType.INFORMATION, "Success", "Service Type Deleted", "The service type has been successfully deleted.");
                loadServiceTypes(); // Reload the table
            } catch (SQLException e) {
                showAlert(AlertType.ERROR, "Database Error", "Failed to Delete Service Type", "An error occurred while deleting the service type. Please try again.");
    			System.out.println(e.getMessage());
            } finally {
                try {
                    if (pst != null) pst.close();
                    if (connection != null) connection.close();
                } catch (SQLException e) {
        			System.out.println(e.getMessage());
                }
            }
        } else {
            showAlert(AlertType.ERROR, "No Selection", "No Service Type Selected", "Please select a service type to delete.");
        }
    }

    private void loadServiceTypes() {
        serviceTypeList.clear();
        try {
            connection = handler.getConnection();
            String query = "SELECT * FROM ServiceType";
            pst = connection.prepareStatement(query);
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                serviceTypeList.add(new ServiceType(rs.getInt("serviceTypeId"), rs.getString("serviceTypeName")));
            }
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

    private void showAlert(AlertType alertType, String title, String header, String content) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
