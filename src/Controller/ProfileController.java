package Controller;


import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

public class ProfileController implements Initializable{

    @FXML
    private ImageView profileImage;
    @FXML
    private Label nameLabel;
    @FXML
    private Label occupationLabel;
    @FXML
    private Button editProfileButton;

    private void loadProfileData() {
        nameLabel.setText(LoginController.getInstance().username());
    }

    @FXML
    private void handleEditProfile() throws IOException {
    	Stage EditProfile = new Stage();
 		Parent root = FXMLLoader.load(getClass().getResource("/FXML/EditProfile.fxml"));
 		Scene scene = new Scene(root);
 		EditProfile.setScene(scene);
 		EditProfile.show();
 		EditProfile.setResizable(false); 
        System.out.println("Edit Profile clicked!");
    }

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
        loadProfileData();
	}
}
