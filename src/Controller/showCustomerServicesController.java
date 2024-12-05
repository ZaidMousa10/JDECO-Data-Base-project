package Controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import DBConnection.DBHandler;
import application.Service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

public class showCustomerServicesController {
	   @FXML
	    private TableView<Service> servicesTable;
	    @FXML
	    private TableColumn<Service, Integer> serviceIdColumn;
	    @FXML
	    private TableColumn<Service, Date> serviceDateColumn;
	    @FXML
	    private TableColumn<Service, String> serviceTypeColumn;
	    @FXML
	    private TableColumn<Service, Double> debtColumn;
	    @FXML
	    private TableColumn<Service, String> statusColumn;

	    private ObservableList<Service> servicesList = FXCollections.observableArrayList();
	    private Connection connection;
	    private PreparedStatement pst;
	    private DBHandler handler = new DBHandler();

	    @FXML
	    public void initialize() {
	        serviceIdColumn.setCellValueFactory(new PropertyValueFactory<>("serviceId"));
	        serviceDateColumn.setCellValueFactory(new PropertyValueFactory<>("serviceDate"));
	        serviceTypeColumn.setCellValueFactory(new PropertyValueFactory<>("serviceType"));
	        debtColumn.setCellValueFactory(new PropertyValueFactory<>("debt"));
	        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));

	        loadServicesData();
	    }

	    private void loadServicesData() {
	        servicesList.clear();
	        String query = "SELECT S.serviceId, S.serviceDate, ST.serviceTypeName, S.debt, SD.meterId, SD.location " +
	                       "FROM Service S " +
	                       "JOIN ServiceType ST ON S.serviceTypeId = ST.serviceTypeId " +
	                       "JOIN ServiceDetail SD ON S.serviceId = SD.serviceId WHERE S.customerId = ?" ;
	        connection = handler.getConnection();
	        try {
	            pst = connection.prepareStatement(query);
	            pst.setInt(1,LoginController.getCustomer().getCustomerID());
	            ResultSet rs = pst.executeQuery();
	            while (rs.next()) {
	            	String status = rs.getInt("meterId")== 0?"Pended":"Completed";
	                servicesList.add(new Service(rs.getInt("serviceId"), rs.getDouble("debt"), rs.getString("serviceTypeName"), 
	                                             status, rs.getDate("serviceDate"), LoginController.getCustomer().getCustomerID(),
	                                             LoginController.getCustomer().getFullName(),rs.getString("location")));
	            }
	            servicesTable.setItems(servicesList);
	        } catch (SQLException e) {
				System.out.println(e.getMessage());
	        } finally {
	            try {
	                if (pst != null) pst.close();
	                if (connection != null) connection.close();
	            } catch (SQLException e) {
	    			System.out.println(e.getMessage());
	            }
	        }
	    }

	    @FXML
	    private void handleRefresh(ActionEvent event) {
	        loadServicesData();
	    }
	    @FXML
	    private void showChart(ActionEvent event) {
	        try {
	            int pendedCount = (int) servicesList.stream().filter(service -> service.getStatus().equals("Pended")).count();
	            int completedCount = (int) servicesList.stream().filter(service -> service.getStatus().equals("Completed")).count();

	            PieChart pieChart = new PieChart();
	            pieChart.getData().add(new PieChart.Data("Pended", pendedCount));
	            pieChart.getData().add(new PieChart.Data("Completed", completedCount));

	            String statisticsText = String.format(
	                "Pended Services: %d\nCompleted Services: %d",
	                pendedCount, completedCount
	            );

	            Alert chartAlert = new Alert(Alert.AlertType.INFORMATION);
	            chartAlert.setTitle("Service Status Chart");
	            chartAlert.setHeaderText("Service Status Distribution");

	            GridPane chartGridPane = new GridPane();
	            chartGridPane.setHgap(10);
	            chartGridPane.setVgap(10);
	            chartGridPane.setPadding(new Insets(20));
	            chartGridPane.add(pieChart, 0, 0);

	            Label statisticsLabel = new Label(statisticsText);
	            statisticsLabel.setStyle("-fx-font-size: 16px;");
	            chartGridPane.add(statisticsLabel, 0, 1);
	            chartAlert.getDialogPane().setContent(chartGridPane);
	            chartAlert.showAndWait();
	        } catch (Exception e) {
	            System.out.println(e.getMessage());
	        }
	    }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}