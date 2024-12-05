package Controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import DBConnection.DBHandler;
import application.Fault;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class FaultShowController {
    @FXML
    private TableView<Fault> faultsTable;
    @FXML
    private TableColumn<Fault, Integer> faultIdColumn;
    @FXML
    private TableColumn<Fault, String> detailsColumn;
    @FXML
    private TableColumn<Fault, String> dateColumn;
    @FXML
    private TableColumn<Fault, String> reportedByColumn;

    private ObservableList<Fault> faultsList = FXCollections.observableArrayList();
    private Connection connection;
    private PreparedStatement pst;
    private DBHandler handler = new DBHandler();

    @FXML
    public void initialize() {
    	faultIdColumn.setCellValueFactory(new PropertyValueFactory<>("faultId"));
        detailsColumn.setCellValueFactory(new PropertyValueFactory<>("details"));
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("date"));
        reportedByColumn.setCellValueFactory(new PropertyValueFactory<>("customerName"));
        faultsTable.setEditable(true);
        detailsColumn.setCellFactory(TextFieldTableCell.forTableColumn());

        loadFaultsData();
    }

    private void loadFaultsData() {
    	faultsList.clear();
        String query = "SELECT * FROM Fault F JOIN Customer C on F.customerId = C.customerId";
        connection = handler.getConnection();
        try {
            pst = connection.prepareStatement(query);
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
            	faultsList.add(new Fault(
                    rs.getInt("faultId"),
                    rs.getString("details"),
                    rs.getString("faultDate"),
                    rs.getInt("customerId"),
                    rs.getString("fullName")
                ));
            }
            faultsTable.setItems(faultsList);
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

    @FXML
    private void handleDelete(ActionEvent event) {
        Fault selectedFault = faultsTable.getSelectionModel().getSelectedItem();
        if (selectedFault == null) {
            showAlert(Alert.AlertType.ERROR, "Selection Error!", "Please select a Fault to delete");
            return;
        }

        connection = handler.getConnection();
        try {
            connection.setAutoCommit(false);

            String deleteFaultQuery = "DELETE FROM Fault WHERE faultId = ?";
            pst = connection.prepareStatement(deleteFaultQuery);
            pst.setInt(1, selectedFault.getFaultId());
            pst.executeUpdate();

            connection.commit();
            loadFaultsData();
        } catch (SQLException e) {
			System.out.println(e.getMessage());
            try {
                connection.rollback();
            } catch (SQLException ex) {
    			System.out.println(ex.getMessage());
            }
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
