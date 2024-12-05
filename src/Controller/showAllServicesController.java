package Controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import DBConnection.DBHandler;
import application.Service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Date;
import java.util.Optional;

public class showAllServicesController {
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
	private TableColumn<Service, String> locationColumn;
	@FXML
	private TableColumn<Service, String> customerIDColumn;
	@FXML
	private TableColumn<Service, String> customerNameColumn;
	@FXML
	private Button showDetailsButton, showChartButton;
	@FXML
	private Button deleteButton;
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
		customerIDColumn.setCellValueFactory(new PropertyValueFactory<>("customerId"));
		customerNameColumn.setCellValueFactory(new PropertyValueFactory<>("customerName"));
		locationColumn.setCellValueFactory(new PropertyValueFactory<>("location"));
		servicesTable.setEditable(true);
		locationColumn.setCellFactory(TextFieldTableCell.forTableColumn());

		locationColumn.setOnEditCommit(event -> {
			Service service = event.getRowValue();
			service.setLocation(event.getNewValue());
			String updateServiceDetailQuery = "UPDATE ServiceDetail SET location = ? WHERE serviceId = ?";
			Connection connection = null;
			PreparedStatement pst = null;
			try {
				connection = handler.getConnection();
				pst = connection.prepareStatement(updateServiceDetailQuery);
				pst.setString(1, service.getLocation());
				pst.setInt(2, service.getServiceId());
				pst.executeUpdate();
				loadServicesData();
			} catch (SQLException e) {
				System.out.println(e.getMessage());
			} finally {
				try {
					if (pst != null)
						pst.close();
					if (connection != null)
						connection.close();
				} catch (SQLException e) {
					System.out.println(e.getMessage());
				}
			}
		});
		loadServicesData();
	}

	private void loadServicesData() {
		servicesList.clear();
		String query = "SELECT S.serviceId, S.serviceDate, ST.serviceTypeName, S.debt, SD.meterId, C.customerId, C.fullName, SD.location "
				+ "FROM Service S " + "JOIN ServiceType ST ON S.serviceTypeId = ST.serviceTypeId "
				+ "JOIN ServiceDetail SD ON S.serviceId = SD.serviceId "
				+ "JOIN Customer C ON S.customerId = C.customerId where SD.meterId is not null";
		connection = handler.getConnection();
		try {
			pst = connection.prepareStatement(query);
			ResultSet rs = pst.executeQuery();
			while (rs.next()) {
				String status = rs.getInt("meterId") == 0 ? "Pended" : "Completed";
				servicesList.add(new Service(rs.getInt("serviceId"), rs.getDouble("debt"),
						rs.getString("serviceTypeName"), status, rs.getDate("serviceDate"), rs.getInt("customerId"),
						rs.getString("fullName"), rs.getString("location")));
			}
			servicesTable.setItems(servicesList);
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		} finally {
			try {
				if (pst != null)
					pst.close();
				if (connection != null)
					connection.close();
			} catch (SQLException e) {
				System.out.println(e.getMessage());
			}
		}
	}

	@FXML
	private void handleDelete(ActionEvent event) {
		Service selectedService = servicesTable.getSelectionModel().getSelectedItem();
		if (selectedService == null) {
			showAlert(Alert.AlertType.ERROR, "Selection Error!", "Please select a Service to delete");
			return;
		}

		// Show input dialog to enter the cancellation reason
		TextInputDialog dialog = new TextInputDialog();
		dialog.setTitle("Cancellation Reason");
		dialog.setHeaderText("Enter the reason for cancellation:");
		dialog.setContentText("Reason:");

		Optional<String> result = dialog.showAndWait();
		if (!result.isPresent() || result.get().trim().isEmpty()) {
			showAlert(Alert.AlertType.ERROR, "Input Error", "Cancellation reason cannot be empty.");
			return;
		}

		String cancellationReason = result.get().trim();

		connection = handler.getConnection();
		PreparedStatement deleteServiceDetailPst = null;
		PreparedStatement deleteTaskPst = null;
		PreparedStatement deleteServicePst = null;
		PreparedStatement insertCanceledServicePst = null;

		try {
			connection.setAutoCommit(false);

			String deleteTaskQuery = "DELETE FROM Task WHERE serviceId = ?";
			deleteTaskPst = connection.prepareStatement(deleteTaskQuery);
			deleteTaskPst.setInt(1, selectedService.getServiceId());
			deleteTaskPst.executeUpdate();

			// Delete from ServiceDetail table
			String deleteServiceDetailQuery = "DELETE FROM ServiceDetail WHERE serviceId = ?";
			deleteServiceDetailPst = connection.prepareStatement(deleteServiceDetailQuery);
			deleteServiceDetailPst.setInt(1, selectedService.getServiceId());
			deleteServiceDetailPst.executeUpdate();

			// Delete from Service table
			String deleteServiceQuery = "DELETE FROM Service WHERE serviceId = ?";
			deleteServicePst = connection.prepareStatement(deleteServiceQuery);
			deleteServicePst.setInt(1, selectedService.getServiceId());
			deleteServicePst.executeUpdate();

			String insertCanceledServiceQuery = "INSERT INTO CanceledService "
					+ "(service_id, customer_id, cancel_date, reason, applyed_at) VALUES (?, ?, ?, ?, ?)";
			insertCanceledServicePst = connection.prepareStatement(insertCanceledServiceQuery);
			insertCanceledServicePst.setInt(1, selectedService.getServiceId());
			insertCanceledServicePst.setInt(2, selectedService.getCustomerId());
			insertCanceledServicePst.setDate(3, new java.sql.Date(System.currentTimeMillis()));
			insertCanceledServicePst.setString(4, cancellationReason);
			insertCanceledServicePst.setDate(5, new java.sql.Date(selectedService.getServiceDate().getYear(),
					selectedService.getServiceDate().getMonth(), selectedService.getServiceDate().getDay()));

			insertCanceledServicePst.executeUpdate();

			connection.commit();
			loadServicesData();
		} catch (SQLException e) {
			System.out.println(e.getMessage());
			try {
				connection.rollback();
			} catch (SQLException ex) {
				System.out.println(ex.getMessage());
			}
		} finally {
			try {
				if (deleteServiceDetailPst != null)
					deleteServiceDetailPst.close();
				if (deleteTaskPst != null)
					deleteTaskPst.close();
				if (deleteServicePst != null)
					deleteServicePst.close();
				if (insertCanceledServicePst != null)
					insertCanceledServicePst.close();
				if (connection != null)
					connection.close();
			} catch (SQLException e) {
				System.out.println(e.getMessage());
			}
		}
	}

	@FXML
	private void handleShowDetails(ActionEvent event) {
		Service selectedService = servicesTable.getSelectionModel().getSelectedItem();
		if (selectedService == null) {
			showAlert(Alert.AlertType.ERROR, "Selection Error!", "Please select a Service to view details");
			return;
		}

		Connection connection = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		try {
			connection = handler.getConnection();

			// Query to get the employee who added the service
			String serviceEmployeeQuery = "SELECT E.fullName FROM employee E WHERE E.employeeId = "
					+ "(SELECT SD.employeeId FROM serviceDetail SD WHERE SD.serviceId = ?)";
			pst = connection.prepareStatement(serviceEmployeeQuery);
			pst.setInt(1, selectedService.getServiceId());
			rs = pst.executeQuery();
			String serviceEmployee = rs.next() ? rs.getString("fullName") : "Unknown";

			// Query to get the meter ID
			String meterQuery = "SELECT SD.meterId FROM serviceDetail SD WHERE SD.serviceId = ?";
			pst = connection.prepareStatement(meterQuery);
			pst.setInt(1, selectedService.getServiceId());
			rs = pst.executeQuery();
			int meterId = rs.next() ? rs.getInt("meterId") : 0;

			// Query to get the employee who added the meter
			String meterEmployeeQuery = "SELECT E.fullName FROM employee E WHERE E.employeeId = "
					+ "(SELECT M.employeeId FROM meter M WHERE M.meterId = ?)";
			pst = connection.prepareStatement(meterEmployeeQuery);
			pst.setInt(1, meterId);
			rs = pst.executeQuery();
			String meterEmployee = rs.next() ? rs.getString("fullName") : "Unknown";

			String details = String.format(
					"Service ID: %d\nService Added By: %s\nService Date: %s\nService Type: %s\nDebt: "
					+ "%.2f\nLocation: %s\nMeter ID: %d\nMeter Added By: %s\nCustomer ID: %d\nCustomer Name: %s",
					selectedService.getServiceId(), serviceEmployee, selectedService.getServiceDate(),
					selectedService.getServiceType(), selectedService.getDebt(), selectedService.getLocation(), meterId,
					meterEmployee, selectedService.getCustomerId(), selectedService.getCustomerName());

			showAlert(Alert.AlertType.INFORMATION, "Service Details", details);
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		} finally {
			try {
				if (rs != null)
					rs.close();
				if (pst != null)
					pst.close();
				if (connection != null)
					connection.close();
			} catch (SQLException e) {
				System.out.println(e.getMessage());
			}
		}
	}

	@FXML
	private void handleShowChart(ActionEvent event) {
	    Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
	    alert.setTitle("Service Status Chart");
	    alert.setHeaderText("Select Month and Year");

	    ComboBox<String> monthComboBox = new ComboBox<>();
	    monthComboBox.setItems(FXCollections.observableArrayList(
	        "January", "February", "March", "April", "May", "June",
	        "July", "August", "September", "October", "November", "December"
	    ));
	    monthComboBox.getSelectionModel().select(LocalDate.now().getMonthValue() - 1); 

	    int currentYear = java.time.LocalDate.now().getYear();
	    Spinner<Integer> yearSpinner = new Spinner<>();
	    SpinnerValueFactory<Integer> yearValueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(currentYear - 10, currentYear + 10, currentYear);
	    yearSpinner.setValueFactory(yearValueFactory);

	    GridPane gridPane = new GridPane();
	    gridPane.setHgap(10);
	    gridPane.setVgap(10);
	    gridPane.setPadding(new Insets(20));
	    gridPane.add(new Label("Month:"), 0, 0);
	    gridPane.add(monthComboBox, 1, 0);
	    gridPane.add(new Label("Year:"), 0, 1);
	    gridPane.add(yearSpinner, 1, 1);

	    alert.getDialogPane().setContent(gridPane);

	    Optional<ButtonType> result = alert.showAndWait();
	    if (result.isPresent() && result.get() == ButtonType.OK) {
	        int month = monthComboBox.getSelectionModel().getSelectedIndex() + 1;
	        int year = yearSpinner.getValue();

	        int acceptedServicesCount = getAcceptedServicesCount(month, year);
	        int canceledServicesCount = getCanceledServicesCount(month, year);

	        PieChart pieChart = new PieChart();
	        pieChart.getData().add(new PieChart.Data("Accepted Services", acceptedServicesCount));
	        pieChart.getData().add(new PieChart.Data("Canceled Services", canceledServicesCount));

	        String statisticsText = String.format("Accepted Services: %d\nCanceled Services: %d", acceptedServicesCount, canceledServicesCount);

	        Alert chartAlert = new Alert(Alert.AlertType.INFORMATION);
	        chartAlert.setTitle("Service Status Chart");
	        chartAlert.setHeaderText(String.format("Service Status for %02d/%d", month, year));

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
	    }
	}

	private int getAcceptedServicesCount(int month, int year) {
	    int count = 0;
	    String query = "SELECT COUNT(*) FROM Service WHERE MONTH(serviceDate) = ? AND YEAR(serviceDate) = ?";
	    try (Connection connection = handler.getConnection();
	         PreparedStatement pst = connection.prepareStatement(query)) {
	        pst.setInt(1, month);
	        pst.setInt(2, year);
	        try (ResultSet rs = pst.executeQuery()) {
	            if (rs.next()) {
	                count = rs.getInt(1);
	            }
	        }
	    } catch (SQLException e) {
			System.out.println(e.getMessage());
	    }
	    return count;
	}

	private int getCanceledServicesCount(int month, int year) {
	    int count = 0;
	    String query = "SELECT COUNT(*) FROM CanceledService WHERE MONTH(cancel_date) = ? AND YEAR(cancel_date) = ?";
	    try (Connection connection = handler.getConnection();
	         PreparedStatement pst = connection.prepareStatement(query)) {
	        pst.setInt(1, month);
	        pst.setInt(2, year);
	        try (ResultSet rs = pst.executeQuery()) {
	            if (rs.next()) {
	                count = rs.getInt(1);
	            }
	        }
	    } catch (SQLException e) {
			System.out.println(e.getMessage());
	    }
	    return count;
	}


	private void showAlert(Alert.AlertType alertType, String title, String message) {
		Alert alert = new Alert(alertType);
		alert.setTitle(title);
		alert.setHeaderText(null);
		alert.setContentText(message);
		alert.showAndWait();
	}

}
