package application;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.*;
import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.*;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.util.Duration;
import DBConnection.DBHandler;



public class SignupController implements Initializable  {
	
	
	@FXML
	private AnchorPane parentPane;
	@FXML
	private Button login;
	@FXML
	private TextField name;
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
	
	
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		progres.setVisible(false);
		name.setStyle("-fx-text-inner-color: #a0a2ab;");
		password.setStyle("-fx-text-inner-color: #a0a2ab;");
		email.setStyle("-fx-text-inner-color: #a0a2ab;");
		phone.setStyle("-fx-text-inner-color: #a0a2ab;");

		
		handler = new DBHandler();
		}
	public void signUpAction(ActionEvent e) {
		if(!name.getText().equals("")&& !password.getText().equals("")) {
		progres.setVisible(true);
		PauseTransition pt = new PauseTransition();
		pt.setDuration(Duration.seconds(3));
		pt.setOnFinished(ev ->{
			
			try {
				signup.getScene().getWindow().hide();
				Stage login = new Stage();
				Parent root;
				root = FXMLLoader.load(getClass().getResource("/FXML/Sample.fxml"));
				Scene scene = new Scene(root);
				login.setScene(scene);
				login.show();
				login.setResizable(false);
			} catch (IOException e1) {
				System.out.println(e1.getMessage());
				}
					});
		pt.play();
		String insert="INSERT INTO carusers(username,password,gender,remember_me_token,email,phone) "+"VALUES(?,?,?,?,?,?)";
		connection=handler.getConnection();
		try {
			pst=connection.prepareStatement(insert);
		} catch (SQLException e1) {
			System.out.println(e1.getMessage());
		}
		try {
			pst.setString(1,name.getText());
			pst.setString(2,password.getText());
			pst.setString(3,getGender());
			pst.setString(4,"");
			pst.setString(5,email.getText());
			pst.setString(6,phone.getText());

			pst.executeUpdate();
		} catch (SQLException e1) {
			System.out.println(e1.getMessage());
		}}
		else if(name.getText().equals("")) {
			progres.setVisible(true);
			PauseTransition pt = new PauseTransition();
			pt.setDuration(Duration.seconds(1));
			pt.setOnFinished(ev ->{
				Platform.runLater(() -> {

				Alert alert = new Alert(Alert.AlertType.ERROR);
				alert.setHeaderText(null);
				alert.setContentText("Username Empty");
				progres.setVisible(false);
				alert.showAndWait();
				});
			});
			pt.play();
		}
		else if(password.getText().equals("")) {
			progres.setVisible(true);
			PauseTransition pt = new PauseTransition();
			pt.setDuration(Duration.seconds(1));
			pt.setOnFinished(ev ->{
				Platform.runLater(() -> {

				Alert alert = new Alert(Alert.AlertType.ERROR);
				alert.setHeaderText(null);
				alert.setContentText("Password Empty");
				progres.setVisible(false);
				alert.showAndWait();
			});
			});

			pt.play();
		}
	}
	@FXML
	public void loginAction(ActionEvent e1) throws IOException {
		signup.getScene().getWindow().hide();
		Stage login = new Stage();
		Parent root = FXMLLoader.load(getClass().getResource("/FXML/Sample.fxml"));
		Scene scene = new Scene(root);
		login.setScene(scene);
		login.show();
		login.setResizable(false);
	}
	public String getGender() {
		String gen="";
		if(male.isSelected()) {
			gen="Male";
		}else if(female.isSelected()) {
			gen="Female";
		}
		return gen;
	}
}
