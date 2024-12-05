package Controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.DatePicker;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.net.URL;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ResourceBundle;

import DBConnection.DBHandler;
import application.Employee;

public class EditEmployeeController implements Initializable{
	@FXML
	private AnchorPane home;
	@FXML
	private TextField fullNameField;
	@FXML
	private TextField gobNameField;
	@FXML
	private PasswordField ePasswordField;
	@FXML
	private TextField salaryField;
	@FXML
	private TextField eRoleField;
	@FXML
	private TextField phoneNumberField;
	@FXML
	private TextField addressField;
	@FXML
	private TextField emailField;
	private Employee selectedEmployee;

	
	public void setEmployee(Employee employee) {
		this.selectedEmployee = employee;
		populateFields();
	}

	private void populateFields() {
		gobNameField.setText(selectedEmployee.getGobName());
		ePasswordField.setText(selectedEmployee.getEPassword());
		salaryField.setText(String.valueOf(selectedEmployee.getSalary()));
		eRoleField.setText(selectedEmployee.getERole());
		phoneNumberField.setText(selectedEmployee.getPhoneNumber());
		fullNameField.setText(selectedEmployee.getFullName());
		addressField.setText(selectedEmployee.getAddress());
		emailField.setText(selectedEmployee.getEmail());
	}

	@FXML
	private void handleSaveEmployee() {
		String fullName = fullNameField.getText();
		String gobName = gobNameField.getText();
		String ePassword = ePasswordField.getText();
		int salary = Integer.parseInt(salaryField.getText());
		String eRole = eRoleField.getText();
		String phoneNumber = phoneNumberField.getText();
		String address = addressField.getText();
		String email = emailField.getText();

		String sql = "UPDATE Employee SET fullName = ?, gobName = ?, ePassword = ?, salary = ?, eRole = ?,"
				+ " PhoneNumber = ?, Address = ?, Email = ? WHERE employeeId = ?";

		try {
			Connection connection = new DBHandler().getConnection();
			PreparedStatement pst = connection.prepareStatement(sql);
			pst.setString(1, fullName);
			pst.setString(2, gobName);
			pst.setString(3, ePassword);
			pst.setInt(4, salary);
			pst.setString(5, eRole);
			pst.setString(6, phoneNumber);
			pst.setString(7, address);
			pst.setString(8, email);
			pst.setInt(9, selectedEmployee.getEmployeeId());

			int affectedRows = pst.executeUpdate();
			if (affectedRows > 0) {
				showAlert("Success", "Employee Updated", "Employee details have been updated successfully.");
			} else {
				showAlert("Error", "Failed to Update Employee",
						"An error occurred while updating the employee details.");
			}
		} catch (SQLException e) {
			System.out.println(e.getMessage());
			showAlert("Error", "Database Error", "An error occurred while communicating with the database.");
		}
	}

	private void showAlert(String title, String header, String content) {
		Alert alert = new Alert(Alert.AlertType.WARNING);
		alert.initOwner(home.getScene().getWindow());
		alert.setTitle(title);
		alert.setHeaderText(header);
		alert.setContentText(content);
		alert.showAndWait();
	}

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		setEmployee(EmployeeManagementController.getSelectedEmployee());
	}
}
