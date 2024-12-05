package Controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.layout.AnchorPane;

import java.io.IOException;

public class CustomerServicesController {

	@FXML
	private AnchorPane home;

	@FXML
	private void handleCustomerServicesAction(ActionEvent event) {
		try {
			loadFXML("/FXML/customer.fxml");
		} catch (IOException e) {
			System.out.println(e.getMessage());
		}
	}

	@FXML
	private void handleOtherServicesAction(ActionEvent event) {
		try {
			loadFXML("/FXML/showAllServices.fxml");
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
