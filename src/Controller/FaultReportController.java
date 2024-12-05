package Controller;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;

import DBConnection.DBHandler;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;

public class FaultReportController {

	@FXML
	private ComboBox<String> faultTypeComboBox;
	@FXML
	private TextArea descriptionArea;
	@FXML
	private Button submitButton;
	private DBHandler handler;

	@FXML
	public void initialize() {
		handler = new DBHandler();
		faultTypeComboBox.getItems().addAll("Power Outage", "Voltage Issue", "Flickering Lights", "Electrical Fire",
				"Other");
	}

	@FXML
	private void handleSubmit() {
		String faultType = faultTypeComboBox.getValue();
		String description = descriptionArea.getText();
		int customerId = LoginController.getCustomer().getCustomerID();
		String query = "INSERT INTO Fault (details, faultDate, customerId) VALUES (?, ?, ?)";

		try (Connection connection = handler.getConnection();
				PreparedStatement pst = connection.prepareStatement(query)) {
			Date date = new Date();
			Timestamp timestamp = new Timestamp(date.getTime());

			pst.setString(1, description);
			pst.setTimestamp(2, timestamp);
			pst.setInt(3, customerId);
			pst.executeUpdate();

			Platform.runLater(() -> {
				Alert alert = new Alert(Alert.AlertType.INFORMATION);
				alert.setHeaderText(null);
				alert.setContentText("Fault report submitted successfully!");
				alert.showAndWait();
			});

		} catch (SQLException e) {
			System.out.println(e.getMessage());
			Platform.runLater(() -> {
				Alert alert = new Alert(Alert.AlertType.ERROR);
				alert.setHeaderText(null);
				alert.setContentText("Failed to submit fault report.");
				alert.showAndWait();
			});
		}
	}

}
