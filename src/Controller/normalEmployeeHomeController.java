package Controller;

import java.io.IOException;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;

public class normalEmployeeHomeController {
	@FXML
	private AnchorPane home;
	@FXML
	private Button requests;

	@FXML
	private Button meters;

	@FXML
	private Button electricService;

	@FXML
	private Button customerServices;

	@FXML
	void handleRequestsAction(ActionEvent event) {
		try {
			loadFXML("/FXML/requests.fxml");
		} catch (IOException e) {
			System.out.println(e.getMessage());
		}
	}

	@FXML
	void handleMetersAction(ActionEvent event) {
		try {
			loadFXML("/FXML/meter.fxml");
		} catch (IOException e) {
			System.out.println(e.getMessage());
		}
	}

	@FXML
	void handleElectricServiceAction(ActionEvent event) throws IOException {
		try {
			loadFXML("/FXML/serviceType.fxml");
		} catch (IOException e) {
			System.out.println(e.getMessage());
		}
	}

	@FXML
	void handleCustomerServicesAction(ActionEvent event) {
		try {
			loadFXML("/FXML/customerService.fxml");
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