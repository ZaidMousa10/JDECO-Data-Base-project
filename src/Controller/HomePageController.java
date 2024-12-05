package Controller;

import java.awt.Desktop;
import java.io.IOException;
import java.net.*;
import java.time.LocalDateTime;
import java.util.ResourceBundle;
import com.jfoenix.controls.*;
import javafx.animation.FadeTransition;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.*;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import javafx.stage.*;
import javafx.util.Duration;

public class HomePageController implements Initializable {
	@FXML
	private AnchorPane holderPane;
	@FXML
	private static AnchorPane home;
	@FXML
	private AnchorPane anchor;
	@FXML
	private Text welcome;
	@FXML
	private JFXToolbar toolBar;
	@FXML
	private HBox toolBarRight;
	@FXML
	private Label lblMenu;
	@FXML
	private VBox overflowContainer;
	@FXML
	private Button btnLogOut, btnExit;
	@FXML
	private Button homeButton;
	@FXML
	private Button aboutButton;
	@FXML
	private Button contactButton;
	@FXML
	private Button btnProfile;

	private static HomePageController instance;

	public HomePageController() {
		instance = this;
	}

	public static HomePageController getInstance() {
		return instance;
	}

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		JFXRippler rippler = new JFXRippler(lblMenu);
		rippler.setMaskType(JFXRippler.RipplerMask.RECT);
		toolBarRight.getChildren().add(rippler);
		openMenus();
		creatPage();
		if (LoginController.getCustomer() != null) {
			setUsername(LoginController.getCustomer().getFullName());
		}else if (LoginController.getEmployee() != null) {
			setUsername(LoginController.getEmployee().getFullName());
		}
	}

	public void setUsername(String user) {
		LocalDateTime now = LocalDateTime.now();
		if (now.getHour() < 12)
			welcome.setText("Good Morning, " + user);
		else
			welcome.setText("Good Evening, " + user);
	}

	private void openMenus() {
		Popp pop = new Popp();
		pop.setContent(overflowContainer);
		pop.setPopupContainer(anchor);
		pop.setSource(lblMenu);
		lblMenu.setOnMouseClicked(e -> {
			pop.show(Popp.PopupVPosition.TOP, Popp.PopupHPosition.RIGHT, -1, 45);
		});
	}

	private void setNode(Node node) {
		holderPane.getChildren().clear();
		holderPane.getChildren().add((Node) node);
		FadeTransition ft = new FadeTransition(Duration.millis(500));
		ft.setNode(node);
		ft.setFromValue(0.1);
		ft.setToValue(1);
		ft.setCycleCount(1);
		ft.setAutoReverse(false);
		ft.play();
	}

	void creatPage() {
		try {
			if (LoginController.getCustomer() != null) {
				home = FXMLLoader.load(getClass().getResource("/FXML/Home.fxml"));
			}else {
				if (LoginController.getEmployee().getERole().equalsIgnoreCase("admin"))
					home = FXMLLoader.load(getClass().getResource("/FXML/employeeHome.fxml"));
                	else 
        				home = FXMLLoader.load(getClass().getResource("/FXML/normalEmployeeHome2.fxml"));
			}
			setNode(home);
		} catch (IOException e) {
			System.out.println(e.getMessage());
		}
	}

	public void homeBtn(ActionEvent he) {
		try {
			if (LoginController.getCustomer() != null) {
				home = FXMLLoader.load(getClass().getResource("/FXML/Home.fxml"));
			}else {
				if (LoginController.getEmployee().getERole().equalsIgnoreCase("admin"))
					home = FXMLLoader.load(getClass().getResource("/FXML/employeeHome.fxml"));
                	else 
        				home = FXMLLoader.load(getClass().getResource("/FXML/normalEmployeeHome2.fxml"));
			}	setNode(home);
		} catch (IOException e) {
			System.out.println(e.getMessage());
		}
	}

	@FXML
	void exit(ActionEvent event) {
		Platform.exit();
	}

	@FXML
	void logOut(ActionEvent event) {
		btnLogOut.getScene().getWindow().hide();
		Stage login = new Stage();
		Parent root;
		try {
			if (LoginController.getCustomer() != null) {
				LoginController.setCustomer(null);
			}else {
				LoginController.setEmployee(null);
			}
			root = FXMLLoader.load(getClass().getResource("/FXML/login.fxml"));
			Scene scene = new Scene(root);
	        Image icon = new Image("file:C:\\Users\\user\\Downloads\\jdecoImage\\jdecoImage\\logo-20-1713086037.png");
	        login.getIcons().add(icon);
			login.setScene(scene);
			login.show();
			login.setResizable(false);
		} catch (IOException e) {
			System.out.println(e.getMessage());
		}
	}

	@FXML
	void about(ActionEvent event) throws IOException {
		openWebPage("http://www.google.com/index.html");
	}

	@FXML
	void contact(ActionEvent event) throws IOException {
		openWebPage("https://www.linkedin.com/in/mohammadrjoub/");
		openWebPage("https://www.linkedin.com/in/abdelrahman-salhab/");
		openWebPage("www.linkedin.com/in/zaid-mousa-0879882b8");
	}

	private static void openWebPage(String url) {
		try {
			Desktop.getDesktop().browse(new URI(url));
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}

	@FXML
	void showProfile(ActionEvent event) throws IOException {
		Stage profile = new Stage();
        Image icon = new Image("file:C:\\Users\\user\\Downloads\\jdecoImage\\jdecoImage\\logo-20-1713086037.png");
		Parent root = FXMLLoader.load(getClass().getResource("/FXML/EditProfile.fxml"));
		Scene scene = new Scene(root);
		profile.getIcons().add(icon);
		profile.setScene(scene);
		profile.show();
		profile.setResizable(false);
	}
}
