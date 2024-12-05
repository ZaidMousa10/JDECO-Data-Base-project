package Controller;

import application.Customer;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.GridPane;
import javafx.util.converter.DefaultStringConverter;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Month;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import DBConnection.DBHandler;

public class CustomerController {
    @FXML
    private TableView<Customer> customerTable;
    @FXML
    private TableColumn<Customer, Integer> customerIdColumn;
    @FXML
    private TableColumn<Customer, String> customerNameColumn;
    @FXML
    private TableColumn<Customer, String> customerAddressColumn;
    @FXML
    private TableColumn<Customer, String> customerPhoneNumberColumn;
    @FXML
    private TableColumn<Customer, String> customerEmailColumn;
    @FXML
    private TableColumn<Customer, Date> customerEntryDateColumn;

    @FXML
    private TextField nameField;
    @FXML
    private TextField addressField;
    @FXML
    private TextField phoneNumberField;
    @FXML
    private TextField emailField;
    @FXML
    private TextField entryDateField;

    private ObservableList<Customer> customerList = FXCollections.observableArrayList();
    private Connection connection;
    private PreparedStatement pst;
    private DBHandler handler = new DBHandler();

    @FXML
    public void initialize() {
        customerIdColumn.setCellValueFactory(new PropertyValueFactory<>("customerID"));
        customerNameColumn.setCellValueFactory(new PropertyValueFactory<>("fullName"));
        customerAddressColumn.setCellValueFactory(new PropertyValueFactory<>("address"));
        customerPhoneNumberColumn.setCellValueFactory(new PropertyValueFactory<>("phoneNumber"));
        customerEmailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));
        customerEntryDateColumn.setCellValueFactory(new PropertyValueFactory<>("entryDate"));

        customerTable.setEditable(true);
        customerAddressColumn.setCellFactory(TextFieldTableCell.forTableColumn(new DefaultStringConverter()));
        customerPhoneNumberColumn.setCellFactory(TextFieldTableCell.forTableColumn(new DefaultStringConverter()));

        customerAddressColumn.setOnEditCommit(event -> {
            Customer customer = event.getRowValue();
            customer.setAddress(event.getNewValue());
            updateCustomerData(customer);
        });

        customerPhoneNumberColumn.setOnEditCommit(event -> {
            Customer customer = event.getRowValue();
            customer.setPhoneNumber(event.getNewValue());
            updateCustomerData(customer);
        });

        loadCustomerData();
    }

    private void loadCustomerData() {
        customerList.clear();
        String query = "SELECT * FROM Customer";
        connection = handler.getConnection();
        try {
            pst = connection.prepareStatement(query);
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                customerList.add(new Customer(rs.getInt("customerID"), rs.getString("cPassword"), rs.getString("fullName"),
                        rs.getString("address"), rs.getString("phoneNumber"), rs.getString("email"), rs.getDate("entryDate")));
            }
            customerTable.setItems(customerList);
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

    private void updateCustomerData(Customer customer) {
        String query = "UPDATE Customer SET address = ?, phoneNumber = ? WHERE customerID = ?";
        connection = handler.getConnection();
        try {
            pst = connection.prepareStatement(query);
            pst.setString(1, customer.getAddress());
            pst.setString(2, customer.getPhoneNumber());
            pst.setInt(3, customer.getCustomerID());
            pst.executeUpdate();
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
    private void handleDeleteCustomer(ActionEvent event) {
        Customer selectedCustomer = customerTable.getSelectionModel().getSelectedItem();
        if (selectedCustomer == null) {
            showAlert(Alert.AlertType.ERROR, "Selection Error!", "Please select a customer to delete");
            return;
        }

        connection = handler.getConnection();
        try {
            connection.setAutoCommit(false);

            String deleteServiceDetailQuery = "DELETE FROM ServiceDetail WHERE serviceId IN (SELECT serviceId FROM Service WHERE customerId = ?)";
            pst = connection.prepareStatement(deleteServiceDetailQuery);
            pst.setInt(1, selectedCustomer.getCustomerID());
            pst.executeUpdate();

            String deleteTaskQuery = "DELETE FROM Task WHERE serviceId IN (SELECT serviceId FROM Service WHERE customerId = ?)";
            pst = connection.prepareStatement(deleteTaskQuery);
            pst.setInt(1, selectedCustomer.getCustomerID());
            pst.executeUpdate();

            String deleteServiceQuery = "DELETE FROM Service WHERE customerId = ?";
            pst = connection.prepareStatement(deleteServiceQuery);
            pst.setInt(1, selectedCustomer.getCustomerID());
            pst.executeUpdate();

            String deleteCustomerQuery = "DELETE FROM Customer WHERE customerID = ?";
            pst = connection.prepareStatement(deleteCustomerQuery);
            pst.setInt(1, selectedCustomer.getCustomerID());
            pst.executeUpdate();

            connection.commit();
            loadCustomerData();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            try {
                connection.rollback();
            } catch (SQLException ex) {
                System.out.println(ex.getMessage());
            }
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
    private void handleShowChart(ActionEvent event) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Customer Count Chart");
        alert.setHeaderText("Select Year");

        int currentYear = java.time.LocalDate.now().getYear();
        Spinner<Integer> yearSpinner = new Spinner<>();
        SpinnerValueFactory<Integer> yearValueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(currentYear - 10, currentYear + 10, currentYear);
        yearSpinner.setValueFactory(yearValueFactory);

        GridPane gridPane = new GridPane();
        gridPane.setHgap(10);
        gridPane.setVgap(10);
        gridPane.setPadding(new Insets(20));
        gridPane.add(new Label("Year:"), 0, 0);
        gridPane.add(yearSpinner, 1, 0);

        alert.getDialogPane().setContent(gridPane);

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            int year = yearSpinner.getValue();
            Map<String, Integer> customerCountPerMonth = getCustomerCountPerMonth(year);
            CategoryAxis xAxis = new CategoryAxis();
            xAxis.setLabel("Month");

            NumberAxis yAxis = new NumberAxis();
            yAxis.setLabel("Customer Count");

            BarChart<String, Number> barChart = new BarChart<>(xAxis, yAxis);
            barChart.setTitle(String.format("Customer Count per Month for %d", year));

            XYChart.Series<String, Number> dataSeries = new XYChart.Series<>();
            dataSeries.setName("Customers");

            for (Map.Entry<String, Integer> entry : customerCountPerMonth.entrySet()) {
                dataSeries.getData().add(new XYChart.Data<>(entry.getKey(), entry.getValue()));
            }

            barChart.getData().add(dataSeries);

            Alert chartAlert = new Alert(Alert.AlertType.INFORMATION);
            chartAlert.setTitle("Customer Count Chart");
            chartAlert.setHeaderText(String.format("Customer Count per Month for %d", year));

            GridPane chartGridPane = new GridPane();
            chartGridPane.setHgap(10);
            chartGridPane.setVgap(10);
            chartGridPane.setPadding(new Insets(20));
            chartGridPane.add(barChart, 0, 0);

            chartAlert.getDialogPane().setContent(chartGridPane);
            chartAlert.showAndWait();
        }
    }

    private Map<String, Integer> getCustomerCountPerMonth(int year) {
        Map<String, Integer> customerCountPerMonth = new HashMap<>();
        String query = "SELECT MONTH(entryDate) AS month, COUNT(*) AS count FROM Customer WHERE YEAR(entryDate) = ? GROUP BY MONTH(entryDate)";

        try (Connection connection = handler.getConnection();
             PreparedStatement pst = connection.prepareStatement(query)) {
            pst.setInt(1, year);
            try (ResultSet rs = pst.executeQuery()) {
                while (rs.next()) {
                    int month = rs.getInt("month");
                    int count = rs.getInt("count");
                    customerCountPerMonth.put(getMonthName(month), count);
                }
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return customerCountPerMonth;
    }

    private String getMonthName(int month) {
        return Month.of(month).name();
    }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}