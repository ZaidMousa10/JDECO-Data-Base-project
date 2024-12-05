package Controller;

import java.io.IOException;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;

public class FaultsAndComolaintsController {
	@FXML
	private AnchorPane home;
	@FXML
	private Button faults;

	@FXML
	private Button complaints;
	
	@FXML
	void handleComplaintAction(ActionEvent event) throws IOException {
		try {
			loadFXML("/FXML/complaintShow.fxml");
		} catch (IOException e) {
			System.out.println(e.getMessage());
		}
	}

	@FXML
	void handleFaultsAction(ActionEvent event) {
		try {
			loadFXML("/FXML/faultsShow.fxml");
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
