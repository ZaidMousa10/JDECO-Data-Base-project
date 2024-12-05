package Controller;

import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;
import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.ResourceBundle;
import java.util.prefs.Preferences;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.*;
import DBConnection.DBHandler;
import application.Customer;
import application.Employee;

public class LoginController implements Initializable  {

    @FXML
    private Button signup;
    @FXML
    private TextField username;
    static String usr;
    @FXML
    protected CheckBox rememberMeCheckbox;
    @FXML
    private Button login;
    @FXML
    private Button forgotpassword;
    @FXML
    private PasswordField password;
    @FXML
    private ImageView progres;
    private DBHandler handler;
    private Connection connection;
    private PreparedStatement pst;
    private static LoginController instance;
    boolean rememberMe = false;
    @FXML
    private ToggleButton showPasswordButton;
    static String Username;
    private static Customer customer;
    private static Employee employee;
    private Preferences preferences;

    public LoginController() {
        instance = this;
    }

    public static LoginController getInstance() {
        return instance;
    }

    public String username() {
        return this.username.getText();
    }

    public String password() {
        return this.password.getText();
    }

    @Override
    public void initialize(URL arg0, ResourceBundle arg1) {
        Username = username.getText();
        progres.setVisible(false);
        username.setStyle("-fx-text-inner-color: #a0a2ab;");
        username.setCursor(Cursor.CLOSED_HAND);
        password.setStyle("-fx-text-inner-color: #a0a2ab;");
        handler = new DBHandler();
        preferences = Preferences.userNodeForPackage(LoginController.class);
        rememberMe = preferences.getBoolean("rememberMe", false);
        rememberMeCheckbox.setSelected(rememberMe);
        if (rememberMe) {
            username.setText(preferences.get("username", ""));
            password.setText(preferences.get("password", ""));
        }
        showPasswordButton.setSelected(false);
        password.setManaged(true);
        password.setVisible(true);
        password.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                handleLoginButtonAction();
                event.consume();
            }
        });
        username.setOnAction(event -> {
            handleLoginButtonAction();
            event.consume();
        });
    }

    @FXML
    public void forgotpasswordAction(ActionEvent e) throws IOException {
        usr = username.getText();
        if (!usr.equals("")) {
            ForgotPasswordController forgotPasswordController = new ForgotPasswordController();
            if (forgotPasswordController.sendCode(usr)) {
                forgotpassword.getScene().getWindow().hide();
                Stage signup = new Stage();
    	        Image icon = new Image("file:C:\\Users\\user\\Downloads\\jdecoImage\\jdecoImage\\logo-20-1713086037.png");
                Parent root = FXMLLoader.load(getClass().getResource("/FXML/fogetPassword.fxml"));
                Scene scene = new Scene(root);
                signup.getIcons().add(icon);
                signup.setScene(scene);
                signup.show();
                signup.setResizable(false);
            }
        } else {
            ForgotPasswordController.showAlert("Error", "Invalid username. Please try again.");
        }
    }

    @FXML
    public void signUpAction(ActionEvent e1) throws IOException {
        login.getScene().getWindow().hide();
        Stage signup = new Stage();
        Image icon = new Image("file:C:\\Users\\user\\Downloads\\jdecoImage\\jdecoImage\\logo-20-1713086037.png");
        Parent root = FXMLLoader.load(getClass().getResource("/FXML/SignUp.fxml"));
        Scene scene = new Scene(root);
        signup.getIcons().add(icon);
        signup.setScene(scene);
        signup.show();
        signup.setResizable(false);
    }

    @FXML
    private void handleLoginButtonAction() {
        progres.setVisible(true);
        Thread thread = new Thread(() -> {
            try {
            	if (Integer.parseInt(username.getText()) >=1000) {
                if (authenticateUser(username.getText(), password.getText()) != null) {
                    this.customer = authenticateUser(username.getText(), password.getText());
                    Platform.runLater(() -> {
                        if (rememberMeCheckbox.isSelected()) {
                            saveCredentialsToPreferences(username.getText(), password.getText());
                        } else {
                            clearCredentialsFromPreferences();
                        }
                        login.getScene().getWindow().hide();
                        Stage home = new Stage();
                        try {
                            Parent root = FXMLLoader.load(getClass().getResource("/FXML/HomePage.fxml"));
                            Scene scene = new Scene(root);
                	        Image icon = new Image("file:C:\\Users\\user\\Downloads\\jdecoImage\\jdecoImage\\logo-20-1713086037.png");
                	        home.getIcons().add(icon);
                            home.setScene(scene);
                            home.show();
                        } catch (IOException e1) {
                			System.out.println(e1.getMessage());
                        }
                        System.out.println("Login successful!");
                    });
                } else {
                    System.out.println("Login failed. Please check your credentials.");
                    Platform.runLater(() -> {
                        Alert alert = new Alert(Alert.AlertType.ERROR);
                        alert.setHeaderText(null);
                        alert.setContentText("Username or Password Is Not Correct");
                        progres.setVisible(false);
                        alert.showAndWait();
                    });
                }
                }else {
                	if (authenticateEmp(username.getText(), password.getText()) != null) {
                        this.employee = authenticateEmp(username.getText(), password.getText());
                        Platform.runLater(() -> {
                            if (rememberMeCheckbox.isSelected()) {
                                saveCredentialsToPreferences(username.getText(), password.getText());
                            } else {
                                clearCredentialsFromPreferences();
                            }
                            login.getScene().getWindow().hide();
                            Stage home = new Stage();
                            try {
                                Parent root = FXMLLoader.load(getClass().getResource("/FXML/HomePage.fxml"));
                                Scene scene = new Scene(root);
                    	        Image icon = new Image("file:C:\\Users\\user\\Downloads\\jdecoImage\\jdecoImage\\logo-20-1713086037.png");
                    	        home.getIcons().add(icon);
                                home.setScene(scene);
                                home.show();
                    			home.setResizable(false);
                            } catch (IOException e1) {
                    			System.out.println(e1.getMessage());
                            }
                        });
                    } else {
                        System.out.println("Login failed. Please check your credentials.");
                        Platform.runLater(() -> {
                            Alert alert = new Alert(Alert.AlertType.ERROR);
                            alert.setHeaderText(null);
                            alert.setContentText("Username or Password Is Not Correct");
                            progres.setVisible(false);
                            alert.showAndWait();
                        });
                    }
				}
            } catch (SQLException e) {
    			System.out.println(e.getMessage());
            }
        });
        thread.start();
    }

    @FXML
    private void handleRememberMeAction() throws SQLException {
        rememberMe = rememberMeCheckbox.isSelected();
        preferences.putBoolean("rememberMe", rememberMe);
        System.out.println("Remember Me selected: " + rememberMe);
    }

    public Customer authenticateUser(String enteredUsername, String enteredPassword) throws SQLException {
        connection = handler.getConnection();
        String query = "SELECT * FROM customer WHERE customerId = ? AND CPassword = ?";
        pst = connection.prepareStatement(query);
        pst.setString(1, enteredUsername);
        pst.setString(2, enteredPassword);
        
        try (ResultSet resultSet = pst.executeQuery()) {
            if (resultSet.next()) {
                int customerID = resultSet.getInt("customerId");
                String cPassword = resultSet.getString("CPassword");
                String fullName = resultSet.getString("fullName");
                String address = resultSet.getString("address");
                String phoneNumber = resultSet.getString("phoneNumber");
                String email = resultSet.getString("email");
                Date entryDate = resultSet.getDate("entryDate");
                
                return new Customer(customerID, cPassword, fullName, address, phoneNumber, email, entryDate);
            } else {
                return null;
            }
        }
    }
    
    public Employee authenticateEmp(String enteredUsername, String enteredPassword) throws SQLException {
        connection = handler.getConnection();
        String query = "SELECT * FROM employee WHERE employeeId = ? AND EPassword = ?";
        pst = connection.prepareStatement(query);
        pst.setString(1, enteredUsername);
        pst.setString(2, enteredPassword);

        try (ResultSet resultSet = pst.executeQuery()) {
            if (resultSet.next()) {
                int employeeId = resultSet.getInt("employeeId");
                String fullName = resultSet.getString("fullName");
                String GobName = resultSet.getString("GobName");
                String EPassword = resultSet.getString("EPassword");
                int salary = resultSet.getInt("salary");
                String ERole = resultSet.getString("ERole");
                String phoneNumber = resultSet.getString("phoneNumber");
                String address = resultSet.getString("address");
                Date dateOfEmployment = resultSet.getDate("DateOfEmployment");
                String email = resultSet.getString("email");
                return new Employee(employeeId, fullName, GobName, EPassword, salary, ERole, phoneNumber, address, dateOfEmployment, email);
            } else {
                return null;
            }
        }
    }
    
    private void saveCredentialsToPreferences(String username, String password) {
        preferences.put("username", username);
        preferences.put("password", password);
    }

    private void clearCredentialsFromPreferences() {
        preferences.remove("username");
        preferences.remove("password");
    }

    @FXML
    private void handleShowPasswordAction() {
        if (showPasswordButton.isSelected()) {
            password.setPromptText(password.getText());
            password.setText("");
            showPasswordButton.setText("Hide");
        } else {
            password.setText(password.getPromptText());
            password.setPromptText(null);
            showPasswordButton.setText("Show");
        }
    }

	public static Customer getCustomer() {
		return customer;
	}

	public static void setCustomer(Customer customer) {
		LoginController.customer = customer;
	}

	public static Employee getEmployee() {
		return employee;
	}

	public static void setEmployee(Employee employee) {
		LoginController.employee = employee;
	}
    
}
