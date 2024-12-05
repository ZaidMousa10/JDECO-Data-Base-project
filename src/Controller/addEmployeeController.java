package Controller;


import java.net.URL;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ResourceBundle;

import DBConnection.DBHandler;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;

public class addEmployeeController implements Initializable{
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
	private DatePicker dateOfEmploymentPicker;
	@FXML
	private	TextField emailField;
	private Connection connection;
	private DBHandler handler;
	
	
	@FXML
	private void handleAddEmployee(ActionEvent event) {
	    String fullName = fullNameField.getText();
	    String gobName = gobNameField.getText();
	    String ePassword = ePasswordField.getText();
	    String salaryText = salaryField.getText();
	    String eRole = eRoleField.getText();
	    String phoneNumber = phoneNumberField.getText();
	    String address = addressField.getText();
	    LocalDate dateOfEmploymentValue = dateOfEmploymentPicker.getValue();
	    String email = emailField.getText();

	    if (fullName.isEmpty() || gobName.isEmpty() || ePassword.isEmpty() || salaryText.isEmpty() || eRole.isEmpty()
	    		|| phoneNumber.isEmpty() || address.isEmpty() || dateOfEmploymentValue == null || email.isEmpty()) {
	        showAlert("Error", "Missing Information", "Please fill in all fields.");
	        return; 
	    }
	    int salary;
	    try {
	        salary = Integer.parseInt(salaryText);
	        if (salary < 0) {
	            showAlert("Error", "Invalid Salary", "Salary must be a non-negative integer.");
	            return; 
	        }
	    } catch (NumberFormatException e) {
	        showAlert("Error", "Invalid Salary", "Salary must be a valid integer.");
	        return; 
	    }

	    String sql = "INSERT INTO Employee (fullName, gobName, ePassword, salary, eRole, PhoneNumber, "
	    		+ "Address, DateOfEmployment, Email) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
	    try {
	        handler = new DBHandler();
	        connection = handler.getConnection();
	        PreparedStatement pst = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
	        pst.setString(1, fullName);
	        pst.setString(2, gobName);
	        pst.setString(3, ePassword);
	        pst.setInt(4, salary);
	        pst.setString(5, eRole);
	        pst.setString(6, phoneNumber);
	        pst.setString(7, address);
	        pst.setDate(8, Date.valueOf(dateOfEmploymentValue));
	        pst.setString(9, email);

	        int affectedRows = pst.executeUpdate();

	        if (affectedRows > 0) {
	            ResultSet generatedKeys = pst.getGeneratedKeys();
	            if (generatedKeys.next()) {
	                int generatedEmployeeId = generatedKeys.getInt(1);
	                showAlert("Success", "Employee Added", "Employee added with ID: " + generatedEmployeeId + " used to LogIn");
	                clearFields();
	            }
	        } else {
	            showAlert("Error", "Failed to Add Employee", "An error occurred while adding the employee.");
	        }
	    } catch (SQLException e) {
			System.out.println(e.getMessage());
	        showAlert("Error", "Database Error", "An error occurred while communicating with the database.");
	    } finally {
	        if (connection != null) {
	            try {
	                connection.close();
	            } catch (SQLException e) {
	    			System.out.println(e.getMessage());
	            }
	        }
	    }
	}

	private void clearFields() {
		fullNameField.clear();
		gobNameField.clear();
		ePasswordField.clear();
		salaryField.clear();
		eRoleField.clear();
		phoneNumberField.clear();
		addressField.clear();
		dateOfEmploymentPicker.setValue(null);
		emailField.clear();
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
		dateOfEmploymentPicker.setEditable(false);
	}
}
