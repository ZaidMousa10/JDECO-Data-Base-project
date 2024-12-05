package Controller;

import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.ResourceBundle;
import DBConnection.DBHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.ToggleButton;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;

public class ResetPasswordController implements Initializable {
	@FXML
	private PasswordField newPasswordField;

	@FXML
	private PasswordField confirmPasswordField;

	@FXML
	private Button resetPasswordButton;

	@FXML
	private Button cancelButton;
	
	private DBHandler handler;
	private Connection connection;
	@FXML
	private ToggleButton showPasswordToggle;

	@FXML
	private void handleShowPasswordAction() {
		if (showPasswordToggle.isSelected()) {
			newPasswordField.setPromptText(newPasswordField.getText());
			newPasswordField.setText("");
			confirmPasswordField.setPromptText(confirmPasswordField.getText());
			confirmPasswordField.setText("");
			showPasswordToggle.setText("Hide Password");
		} else {
			newPasswordField.setText(newPasswordField.getPromptText());
			newPasswordField.setPromptText(null);
			confirmPasswordField.setText(confirmPasswordField.getPromptText());
			confirmPasswordField.setPromptText(null);
			showPasswordToggle.setText("Show Password");
		}
	}

	@FXML
	private void handleResetPassword() {
		try {
			String newPassword = newPasswordField.getText();
			String confirmPassword = confirmPasswordField.getText();
			if (newPassword.isEmpty() || confirmPassword.isEmpty()) {
				showAlert("Error", "Please enter a password.");
			} else {
				if (newPassword.equals(confirmPassword)) {
					String updateQuery = "UPDATE customer SET CPassword = ? WHERE BINARY customerId = ?";
					connection = handler.getConnection();
					PreparedStatement preparedStatement = connection.prepareStatement(updateQuery);
					preparedStatement.setString(1, newPassword);
					preparedStatement.setString(2, LoginController.usr);
					int rowsAffected = preparedStatement.executeUpdate();
					if (rowsAffected > 0) {
						showAlert("Success", "Password reset successful.");
						System.out.println("Password updated successfully for username: " + LoginController.usr);
					} else {
						System.out.println("Username not found. Password update failed.");
					}
					resetPasswordButton.getScene().getWindow().hide();
					Stage login = new Stage();
					Parent root;
					root = FXMLLoader.load(getClass().getResource("/FXML/login.fxml"));
					Scene scene = new Scene(root);
					login.setScene(scene);
					login.show();
					login.setResizable(false);
				} else if (newPassword.equals("") || confirmPassword == "") {
					showAlert("Error", "Passwords do not match. Please re-enter.");
				} else {
					showAlert("Error", "Passwords do not match. Please re-enter.");
				}
			}
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}

	@FXML
	private void handleCancel() {
		Stage stage = (Stage) cancelButton.getScene().getWindow();
		stage.close();
	}

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		handler = new DBHandler();
		confirmPasswordField.setOnKeyPressed(event -> {
			if (event.getCode() == KeyCode.ENTER) {
				try {
					handleResetPassword();
				} catch (Exception e) {
					System.out.println(e.getMessage());
				}
				event.consume();
			}
		});
	}

	private static void showAlert(String title, String content) {
		Alert alert = new Alert(Alert.AlertType.INFORMATION);
		alert.setTitle(title);
		alert.setHeaderText(null);
		alert.setContentText(content);
		alert.showAndWait();
	}
}