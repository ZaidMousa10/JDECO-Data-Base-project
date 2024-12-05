package Controller;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.ResourceBundle;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.util.Duration;
import DBConnection.DBHandler;

public class SignUpController implements Initializable {

    @FXML
    private AnchorPane parentPane;
    @FXML
    private Button login;
    @FXML
    private TextField name;
    @FXML
    private TextField address;
    @FXML
    private Button signup;
    @FXML
    private RadioButton male;
    @FXML
    private RadioButton female;
    @FXML
    private Button forgotpassword;
    @FXML
    private PasswordField password;
    @FXML
    private ImageView progres;
    @FXML
    private TextField phone;
    @FXML
    private TextField email;
    private Connection connection;
    private DBHandler handler;
    private PreparedStatement pst;
    private ToggleGroup genderGroup;

    @Override
    public void initialize(URL arg0, ResourceBundle arg1) {
        progres.setVisible(false);
        name.setStyle("-fx-text-inner-color: #a0a2ab;");
        password.setStyle("-fx-text-inner-color: #a0a2ab;");
        email.setStyle("-fx-text-inner-color: #a0a2ab;");
        phone.setStyle("-fx-text-inner-color: #a0a2ab;");
        genderGroup = new ToggleGroup();
        male.setToggleGroup(genderGroup);
        female.setToggleGroup(genderGroup);
        handler = new DBHandler();
    }

    public void signUpAction(ActionEvent e) {
        if (validateFields()) {
            showProgressIndicator();
            PauseTransition pt = new PauseTransition(Duration.seconds(3));
            insertCustomerData();
            pt.setOnFinished(ev -> {
                hideSignUpWindow();
                showLoginWindow();
            });
            pt.play();
        }
    }

    private boolean validateFields() {
        if (name.getText().isEmpty()) {
            showAlert("Name is empty");
            return false;
        }
        if (password.getText().isEmpty()) {
            showAlert("Password is empty");
            return false;
        }
        if (email.getText().isEmpty()) {
            showAlert("Email is empty");
            return false;
        }
        if (!validateEmail(email.getText())) {
            showAlert("Invalid email format");
            return false;
        }
        if (phone.getText().isEmpty()) {
            showAlert("Phone number is empty");
            return false;
        }
        if (address.getText().isEmpty()) {
            showAlert("Address is empty");
            return false;
        }
        if (getGender().isEmpty()) {
            showAlert("Gender is empty");
            return false;
        }
        return true;
    }

    private boolean validateEmail(String email) {
        String emailRegex = "^[a-zA-Z0-9_+&-]+(?:\\.[a-zA-Z0-9_+&-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
        Pattern pattern = Pattern.compile(emailRegex);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    private void showProgressIndicator() {
        progres.setVisible(true);
    }

    private void hideProgressIndicator() {
        progres.setVisible(false);
    }

    private void hideSignUpWindow() {
        Stage stage = (Stage) name.getScene().getWindow();
        stage.hide();
    }

    private void showLoginWindow() {
        try {
            Stage login = new Stage();
            Image icon = new Image("file:C://Users//ACTC//Desktop//jdecoImage//logo-20-1713086037.png");
            login.getIcons().add(icon);
            Parent root = FXMLLoader.load(getClass().getResource("/FXML/login.fxml"));
            Scene scene = new Scene(root);
            login.setScene(scene);
            login.setResizable(false);
            login.show();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    private void insertCustomerData() {
        String insertQuery = "INSERT INTO Customer (CPassword, fullName, Address, PhoneNumber, email, EntryDate) VALUES (?, ?, ?, ?, ?, ?)";
        connection = handler.getConnection();
        try {
            pst = connection.prepareStatement(insertQuery, PreparedStatement.RETURN_GENERATED_KEYS);
            pst.setString(1, password.getText());
            pst.setString(2, name.getText());
            pst.setString(3, address.getText());
            pst.setString(4, phone.getText());
            pst.setString(5, email.getText());
            pst.setString(6, getCurrentFormattedDate());
            pst.executeUpdate();

            ResultSet generatedKeys = pst.getGeneratedKeys();
            if (generatedKeys.next()) {
                int customerId = generatedKeys.getInt(1);
                showAlert(Alert.AlertType.INFORMATION, "Success", "Your ID: " + customerId + " Will be used to login.");
            } else {
                showAlert(Alert.AlertType.ERROR, "Insertion Error", "Failed to retrieve customer ID.");
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            showAlert(Alert.AlertType.ERROR, "Database Error", "Could not insert customer data.");
        } finally {
            try {
                if (pst != null) pst.close();
                if (connection != null) connection.close();
            } catch (SQLException e) {
                System.out.println(e.getMessage());
            }
        }
    }

    private String getCurrentFormattedDate() {
        Date date = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return formatter.format(date);
    }

    private void showAlert(String message) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText(null);
            alert.setContentText(message);
            hideProgressIndicator();
            alert.showAndWait();
        });
    }

    @FXML
    public void loginAction(ActionEvent e1) throws IOException {
        signup.getScene().getWindow().hide();
        Stage login = new Stage();
        Parent root = FXMLLoader.load(getClass().getResource("/FXML/login.fxml"));
        Scene scene = new Scene(root);
        Image icon = new Image("file:C://Users//ACTC//Desktop//jdecoImage//logo-20-1713086037.png");
        login.getIcons().add(icon);
        login.setScene(scene);
        login.show();
        login.setResizable(false);
    }

    public String getGender() {
        String gen = "";
        if (male.isSelected()) {
            gen = "Male";
        } else if (female.isSelected()) {
            gen = "Female";
        }
        return gen;
    }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}