package Controller;

import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ResourceBundle;
import DBConnection.DBHandler;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;

public class EditProfileController extends Email implements Initializable {

	@FXML
	private AnchorPane an;
	@FXML
	private ImageView profileImageView;
	@FXML
	private TextField nameTextField;
	@FXML
	private TextField emailTextField, CodeTextField;
	@FXML
	private PasswordField passwordField;
	@FXML
	private Text username;
	@FXML
	private Text email;
	@FXML
	private HBox passBar;
	@FXML
	private HBox editEmail, VerfEmail;
	@FXML
	private Button editpass;
	@FXML
	private Button editEmailButton;
	@FXML
	private Button VerfEmailButton;
	@FXML
	private PasswordField oldpasswordField;
	@FXML
	private PasswordField newpasswordField;
	@FXML
	private PasswordField confirmnewpasswordField;
	@FXML
	private Button continueEdetingPass;
	@FXML
	private HBox resetpass;
	@FXML
	private Button reset;
	private String code = "";
	private DBHandler handler;
	private Connection connection;

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		code = GenerateCode();
		System.out.println(LoginController.getInstance().username());
		username.setText(LoginController.getInstance().username());
		email.setText(ForgotPasswordController.getEmail(LoginController.getInstance().username()));
		passBar.setVisible(false);
		editEmail.setVisible(false);
		VerfEmail.setVisible(false);
		resetpass.setVisible(false);
		emailTextField.setOnKeyPressed(event -> {
			if (event.getCode() == KeyCode.ENTER) {
				SendVerfication();
				event.consume();
			}
		});
		CodeTextField.setOnKeyPressed(event -> {
			if (event.getCode() == KeyCode.ENTER) {
				AfterVerfAction();
			}
		});
		oldpasswordField.setOnKeyPressed(event -> {
			if (event.getCode() == KeyCode.ENTER) {
				confirmnewpasswordFieldAction();
			}
		});
		confirmnewpasswordField.setOnKeyPressed(event -> {
			if (event.getCode() == KeyCode.ENTER) {
				resetAction();
			}
		});
	}

	@FXML
	private void openPass() {
		Popp pop = new Popp();
		pop.setContent(passBar);
		pop.setPopupContainer(an);
		pop.setSource(editpass);
		editpass.setOnMouseClicked(e -> {
			pop.show(Popp.PopupVPosition.TOP, Popp.PopupHPosition.RIGHT, 50, 42);
			passBar.setVisible(true);
		});
	}

	@FXML
	private void openEmail(ActionEvent e) {
		Popp pop = new Popp(an, editEmail);
		pop.setSource(editEmailButton);
		editEmailButton.setOnMouseClicked(e1 -> {
			pop.show(Popp.PopupVPosition.TOP, Popp.PopupHPosition.RIGHT, 190, 44);
			editEmail.setVisible(true);
		});
	}

	@FXML
	public void VerfAction() {
		VerfEmail.setVisible(true);
		System.out.println("Verf Email clicked!");
	}

	@FXML
	public void SendVerfication() {
		if (sendResetEmail(emailTextField.getText(), code)) {
			showAlert("Success", "A Verfication Code has been sent to your email (" + emailTextField.getText() + ").");
			editEmail.setVisible(false);
			VerfAction();
		} else {
			showAlert("Error", "Failed to send the Verfication Code. Please try again.");
		}
	}

	@FXML
	public void confirmnewpasswordFieldAction() {
		if (oldpasswordField.getText().equals(LoginController.getInstance().password())) {
			passBar.setVisible(false);
			oldpasswordField.setText("");
			resetpass.setVisible(true);
		} else {
			showAlert("Error", "The Enterd Password Is Wrong Please Try Again.");
		}
	}

	@FXML
	public void AfterVerfAction() {
		handler = new DBHandler();
		String VerfCode = CodeTextField.getText();
		PreparedStatement preparedStatement = null;
		try {
			if (VerfCode.equals(code)) {
				String updateQuery = "UPDATE customer SET email = ? WHERE customerId = ?";
				String updateQuery1 = "UPDATE employee SET email = ? WHERE employeeId = ?";
				connection = handler.getConnection();
				if (LoginController.getCustomer() == null) {
					preparedStatement = connection.prepareStatement(updateQuery1);
					preparedStatement.setInt(2, LoginController.getEmployee().getEmployeeId());
				} else {
					preparedStatement = connection.prepareStatement(updateQuery);
					preparedStatement.setInt(2, LoginController.getCustomer().getCustomerID());
				}
				preparedStatement.setString(1, emailTextField.getText());
				int rowsAffected = preparedStatement.executeUpdate();
				if (rowsAffected > 0) {
					showAlert("Success", emailTextField.getText() + " Is Your New Email Address");
					VerfEmail.setVisible(false);
					emailTextField.setText("");
					CodeTextField.setText("");
					email.setText(emailTextField.getText());
					System.out.println("Email updated successfully for username: " + LoginController.getInstance().username());
				} else {
					System.out.println("Username not found or email update failed.");
				}
			} else {
				showAlert("Error", "Invalid Verification Code. Please try again.");
			}
		} catch (SQLException e) {
			System.out.println(e.getMessage());
			showAlert("Error", "An error occurred while updating the email. Please try again.");
		} finally {
			try {
				if (preparedStatement != null) {
					preparedStatement.close();
				}
				if (connection != null) {
					connection.close();
				}
			} catch (SQLException ex) {
				System.out.println(ex.getMessage());
			}
		}
	}

	@FXML
	public void resetAction() {
		String newPassword = newpasswordField.getText();
		String confirmPassword = confirmnewpasswordField.getText();
		PreparedStatement preparedStatement = null;

		try {
			// Check if passwords match
			if (newPassword.isEmpty() || confirmPassword.isEmpty()) {
				showAlert("Error", "Please enter a password.");
			} else {
				// Check if the new password and the confirmation password are equal
				if (newPassword.equals(confirmPassword)) {
					handler = new DBHandler();
					String updateQuery = "UPDATE customer SET CPassword = ? WHERE customerId = ?";
					String updateQuery1 = "UPDATE employee SET EPassword = ? WHERE employeeId = ?";
					connection = handler.getConnection();
					if (LoginController.getCustomer() == null) {
						preparedStatement = connection.prepareStatement(updateQuery1);
						preparedStatement.setInt(2, LoginController.getEmployee().getEmployeeId());
					} else {
						preparedStatement = connection.prepareStatement(updateQuery);
						preparedStatement.setInt(2, LoginController.getCustomer().getCustomerID());
					}
					preparedStatement.setString(1, newPassword);
					int rowsAffected = preparedStatement.executeUpdate();
					if (rowsAffected > 0) {
						showAlert("Success", "Password Updated Successfully.");
						resetpass.setVisible(false);
						newpasswordField.setText("");
						confirmnewpasswordField.setText("");
						System.out.println("Password updated successfully for username: "
								+ LoginController.getInstance().username());
					} else {
						System.out.println("Username not found. Password update failed.");
					}
				} else if (newPassword.equals("") || confirmPassword.equals("")) {
					showAlert("Error", "Passwords do not match. Please re-enter.");
				} else {
					showAlert("Error", "Passwords do not match. Please re-enter.");
				}
			}
		} catch (SQLException e) {
			System.out.println(e.getMessage());
			showAlert("Error", "An error occurred while updating the password. Please try again.");
		} finally {
			try {
				if (preparedStatement != null) {
					preparedStatement.close();
				}
				if (connection != null) {
					connection.close();
				}
			} catch (SQLException ex) {
				System.out.println(ex.getMessage());
			}
		}
	}

}
