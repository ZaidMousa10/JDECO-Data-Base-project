package Controller;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;


public class HomeController implements Initializable {

    @FXML
    private AnchorPane home;

    @FXML
    private Button faults;
    @FXML
    private Button complaint;
    @FXML
    private Button apply;
    @FXML
    private Button showService;
    @FXML
    private HomePageController homePageController;
	
    @Override
    public void initialize(URL arg0, ResourceBundle arg1) {

    }

    @FXML
    public void faultsAction(ActionEvent e1) throws IOException {
        try {
            loadFXML("/FXML/faults.fxml");
        } catch (IOException e) {
			System.out.println(e.getMessage());
        }
    }

    @FXML
    public void complaintAction(ActionEvent e1) throws IOException {
        try {
            loadFXML("/FXML/complaint.fxml");
        } catch (IOException e) {
			System.out.println(e.getMessage());
        }
    }
    
    @FXML
    public void applyAction(ActionEvent e1) throws IOException {
        try {
            loadFXML("/FXML/apply.fxml");
        } catch (IOException e) {
			System.out.println(e.getMessage());
        }
    }

    @FXML
    public void serviceAction(ActionEvent e1) throws IOException {
        try {
            loadFXML("/FXML/showServices.fxml");
        } catch (IOException e) {
			System.out.println(e.getMessage());
        }
    }
    public void loadFXML(String loc) throws IOException {
        try {
            Parent root = FXMLLoader.load(getClass().getResource(loc));
            setNode(root);
        } catch (IOException e) {
			System.out.println(e.getMessage());
        }
    }

    private void setNode(Node node) {
        home.getChildren().clear();
        home.getChildren().add(node);
    }
}
