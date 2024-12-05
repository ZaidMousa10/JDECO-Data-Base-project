package Controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.geometry.Insets;
import DBConnection.DBHandler;
import application.Employee;

import java.io.IOException;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.Month;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class EmployeeManagementController {
    @FXML
    private AnchorPane home;
    @FXML
    private TableView<Employee> employeeTable;
    @FXML
    private TableColumn<Employee, Integer> employeeIdColumn;
    @FXML
    private TableColumn<Employee, String> fullNameColumn;
    @FXML
    private TableColumn<Employee, String> gobNameColumn;
    @FXML
    private TableColumn<Employee, String> ePasswordColumn;
    @FXML
    private TableColumn<Employee, Integer> salaryColumn;
    @FXML
    private TableColumn<Employee, String> eRoleColumn;
    @FXML
    private TableColumn<Employee, String> phoneNumberColumn;
    @FXML
    private TableColumn<Employee, String> addressColumn;
    @FXML
    private TableColumn<Employee, Date> dateOfEmploymentColumn;

    private ObservableList<Employee> employeeList = FXCollections.observableArrayList();
    private Connection connection;
    private PreparedStatement pst;
    private DBHandler handler = new DBHandler();
    private static Employee selectedEmployee;

    @FXML
    public void initialize() {
        employeeIdColumn.setCellValueFactory(new PropertyValueFactory<>("employeeId"));
        fullNameColumn.setCellValueFactory(new PropertyValueFactory<>("fullName"));
        gobNameColumn.setCellValueFactory(new PropertyValueFactory<>("gobName"));
        ePasswordColumn.setCellValueFactory(new PropertyValueFactory<>("ePassword"));
        salaryColumn.setCellValueFactory(new PropertyValueFactory<>("salary"));
        eRoleColumn.setCellValueFactory(new PropertyValueFactory<>("eRole"));
        phoneNumberColumn.setCellValueFactory(new PropertyValueFactory<>("phoneNumber"));
        addressColumn.setCellValueFactory(new PropertyValueFactory<>("address"));
        dateOfEmploymentColumn.setCellValueFactory(new PropertyValueFactory<>("dateOfEmployment"));
        loadEmployeeData();
    }

    private void loadEmployeeData() {
        employeeList.clear();
        String query = "SELECT * FROM Employee";
        connection = handler.getConnection();
        try {
            pst = connection.prepareStatement(query);
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                employeeList.add(new Employee(
                    rs.getInt("employeeId"),
                    rs.getString("fullName"),
                    rs.getString("GobName"),
                    rs.getString("EPassword"),
                    rs.getInt("salary"),
                    rs.getString("ERole"),
                    rs.getString("PhoneNumber"),
                    rs.getString("Address"),
                    rs.getDate("DateOfEmployment"),
                    rs.getString("email")
                ));
            }
            employeeTable.setItems(employeeList);
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
    private void handleAddEmployee(ActionEvent event) {
        try {
            loadFXML("/FXML/addEmployee.fxml");
        } catch (IOException e) {
			System.out.println(e.getMessage());
        }
    }

    @FXML
    private void handleEditEmployee(ActionEvent event) {
        try {
            selectedEmployee = employeeTable.getSelectionModel().getSelectedItem();
            if (selectedEmployee == null) {
                showAlert(Alert.AlertType.ERROR, "Selection Error!", "Please select an Employee to edit");
                return;
            }
            loadFXML("/FXML/editEmployee.fxml");
        } catch (IOException e) {
			System.out.println(e.getMessage());
        }
    }

    @FXML
    private void handleDeleteEmployee(ActionEvent event) {
        Employee selectedEmployee = employeeTable.getSelectionModel().getSelectedItem();
        if (selectedEmployee == null) {
            showAlert(Alert.AlertType.ERROR, "Selection Error!", "Please select an Employee to delete");
            return;
        }

        connection = handler.getConnection();
        try {
            String deleteQuery = "DELETE FROM Employee WHERE employeeId = ?";
            pst = connection.prepareStatement(deleteQuery);
            pst.setInt(1, selectedEmployee.getEmployeeId());
            pst.executeUpdate();
            loadEmployeeData();
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
    private void handleShowChart(ActionEvent event) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Employees Chart");
        alert.setHeaderText("Select Year");

        int currentYear = LocalDate.now().getYear();
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

            Map<String, Integer> employeeCountPerMonth = getEmployeeCountPerMonth(year);

            CategoryAxis xAxis = new CategoryAxis();
            xAxis.setLabel("Month");

            NumberAxis yAxis = new NumberAxis();
            yAxis.setLabel("Employee Number");

            BarChart<String, Number> barChart = new BarChart<>(xAxis, yAxis);
            barChart.setTitle(String.format("Employee Number per Month for %d", year));

            XYChart.Series<String, Number> dataSeries = new XYChart.Series<>();
            dataSeries.setName("Employees");

            for (Map.Entry<String, Integer> entry : employeeCountPerMonth.entrySet()) {
                dataSeries.getData().add(new XYChart.Data<>(entry.getKey(), entry.getValue()));
            }

            barChart.getData().add(dataSeries);

            Alert chartAlert = new Alert(Alert.AlertType.INFORMATION);
            chartAlert.setTitle("Employee Count Chart");
            chartAlert.setHeaderText(String.format("Employee Number per Month for %d", year));

            GridPane chartGridPane = new GridPane();
            chartGridPane.setHgap(10);
            chartGridPane.setVgap(10);
            chartGridPane.setPadding(new Insets(20));
            chartGridPane.add(barChart, 0, 0);

            chartAlert.getDialogPane().setContent(chartGridPane);
            chartAlert.showAndWait();
        }
    }

    private Map<String, Integer> getEmployeeCountPerMonth(int year) {
        Map<String, Integer> employeeCountPerMonth = new HashMap<>();
        String query = "SELECT MONTH(DateOfEmployment) AS month, COUNT(*) AS count FROM Employee WHERE YEAR(DateOfEmployment) = ? GROUP BY MONTH(DateOfEmployment)";

        try (Connection connection = handler.getConnection();
             PreparedStatement pst = connection.prepareStatement(query)) {
            pst.setInt(1, year);
            try (ResultSet rs = pst.executeQuery()) {
                while (rs.next()) {
                    int month = rs.getInt("month");
                    int count = rs.getInt("count");
                    employeeCountPerMonth.put(getMonthName(month), count);
                }
            }
        } catch (SQLException e) {
			System.out.println(e.getMessage());
        }
        return employeeCountPerMonth;
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

    public static Employee getSelectedEmployee() {
        return selectedEmployee;
    }

    public void setSelectedEmployee(Employee selectedEmployee) {
        this.selectedEmployee = selectedEmployee;
    }
}
