package application;
	

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;


public class Main extends Application {
	@Override
	public void start(Stage primaryStage) {
		try {
			FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/FXML/Login.fxml"));
	        Parent root = fxmlLoader.load();	
			Scene scene = new Scene(root);
	        Image icon = new Image("file:C:\\Users\\user\\Downloads\\jdecoImage\\jdecoImage\\logo-20-1713086037.png");
	        primaryStage.getIcons().add(icon);
			primaryStage.setScene(scene);
			primaryStage.show();
			primaryStage.setResizable(false);
			
		} catch(Exception e) {
			System.out.println(e.getMessage());		}
	}
	
	public static void main(String[] args) {
		launch(args);
	}
}
