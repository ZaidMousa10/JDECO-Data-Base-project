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
import application.Complaint;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ComplaintsShowController {
	@FXML
	private TableView<Complaint> complaintsTable;
	@FXML
	private TableColumn<Complaint, Integer> complaintIdColumn;
	@FXML
	private TableColumn<Complaint, String> detailsColumn;
	@FXML
	private TableColumn<Complaint, String> dateColumn;
	@FXML
	private TableColumn<Complaint, String> reportedByColumn;

	private ObservableList<Complaint> complaintsList = FXCollections.observableArrayList();
	private Connection connection;
	private PreparedStatement pst;
	private DBHandler handler = new DBHandler();

	@FXML
	public void initialize() {
		complaintIdColumn.setCellValueFactory(new PropertyValueFactory<>("complaintId"));
		detailsColumn.setCellValueFactory(new PropertyValueFactory<>("details"));
		dateColumn.setCellValueFactory(new PropertyValueFactory<>("date"));
		reportedByColumn.setCellValueFactory(new PropertyValueFactory<>("customerName"));
		complaintsTable.setEditable(true);
		detailsColumn.setCellFactory(TextFieldTableCell.forTableColumn());

		loadComplaintsData();
	}

	private void loadComplaintsData() {
		complaintsList.clear();
		String query = "SELECT * FROM Complaint C JOIN Customer CU on C.customerId = CU.customerId";
		connection = handler.getConnection();
		try {
			pst = connection.prepareStatement(query);
			ResultSet rs = pst.executeQuery();
			while (rs.next()) {
				complaintsList.add(new Complaint(rs.getInt("complaintId"), rs.getString("details"),
						rs.getString("complaintDate"), rs.getInt("customerId"), rs.getString("fullName")));
			}
			complaintsTable.setItems(complaintsList);
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		} finally {
			try {
				if (pst != null)
					pst.close();
				if (connection != null)
					connection.close();
			} catch (SQLException e) {
				System.out.println(e.getMessage());
			}
		}
	}

	@FXML
	private void handleDelete(ActionEvent event) {
		Complaint selectedComplaint = complaintsTable.getSelectionModel().getSelectedItem();
		if (selectedComplaint == null) {
			showAlert(Alert.AlertType.ERROR, "Selection Error!", "Please select a Complaint to delete");
			return;
		}

		connection = handler.getConnection();
		try {
			connection.setAutoCommit(false);

			String deleteComplaintQuery = "DELETE FROM Complaints WHERE complaintId = ?";
			pst = connection.prepareStatement(deleteComplaintQuery);
			pst.setInt(1, selectedComplaint.getComplaintId());
			pst.executeUpdate();

			connection.commit();
			loadComplaintsData();
		} catch (SQLException e) {
			System.out.println(e.getMessage());
			try {
				connection.rollback();
			} catch (SQLException ex) {
				System.out.println(e.getMessage());
			}
		} finally {
			try {
				if (pst != null)
					pst.close();
				if (connection != null)
					connection.close();
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
