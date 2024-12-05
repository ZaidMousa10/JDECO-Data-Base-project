package Controller;

import javafx.fxml.*;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import java.io.IOException;
import java.net.URL;
import java.util.*;

public class ForgotPasswordController extends Email implements Initializable {

	@FXML
	private AnchorPane root;
	@FXML
	private TextField resetCodeField;
	@FXML
	Button sendcode;

	@FXML
	private void handleResetPassword() throws IOException {
		String enteredCode = resetCodeField.getText();
		if (enteredCode.equals(generateTemporaryPassword)) {
			sendcode.getScene().getWindow().hide();
			Stage signup = new Stage();
			Parent root = FXMLLoader.load(getClass().getResource("/FXML/resetPassword.fxml"));
			Scene scene = new Scene(root);
			signup.setScene(scene);
			signup.show();
			signup.setResizable(false);
		} else {
			showAlert("Error", "Invalid reset code. Please try again.");
		}
	}

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		resetCodeField.setOnKeyPressed(event -> {
			if (event.getCode() == KeyCode.ENTER) {
				try {
					handleResetPassword();
					event.consume();
				} catch (IOException e) {
					System.out.println(e.getMessage());
				}
			}
		});
	}
}
