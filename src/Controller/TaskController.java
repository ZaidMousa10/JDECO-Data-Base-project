package Controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.ComboBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.util.converter.IntegerStringConverter;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import DBConnection.DBHandler;
import application.Task;

public class TaskController {
    @FXML
    private TableView<Task> taskTable;
    @FXML
    private TableColumn<Task, Integer> taskIdColumn;
    @FXML
    private TableColumn<Task, String> toDoDateColumn;
    @FXML
    private TableColumn<Task, Integer> serviceIdColumn;
    @FXML
    private TableColumn<Task, Integer> employeeIdColumn;
    @FXML
    private TableColumn<Task, String> customerNameColumn;
    @FXML
    private TableColumn<Task, Integer> meterIdColumn;
    @FXML
    private TableColumn<Task, String> locationColumn;
    private ObservableList<Task> taskList = FXCollections.observableArrayList();
    private Connection connection;
    private PreparedStatement pst;
    private DBHandler handler = new DBHandler();
	private ObservableList<Integer> options1 = FXCollections.observableArrayList();;

    @FXML
    public void initialize() {
        taskIdColumn.setCellValueFactory(new PropertyValueFactory<>("taskId"));
        toDoDateColumn.setCellValueFactory(new PropertyValueFactory<>("toDoDate"));
        serviceIdColumn.setCellValueFactory(new PropertyValueFactory<>("serviceId"));
        employeeIdColumn.setCellValueFactory(new PropertyValueFactory<>("employeeId"));
        customerNameColumn.setCellValueFactory(new PropertyValueFactory<>("customerName"));
        meterIdColumn.setCellValueFactory(new PropertyValueFactory<>("meterId"));
        locationColumn.setCellValueFactory(new PropertyValueFactory<>("location"));

        taskTable.setEditable(true);
        locationColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        meterIdColumn.setCellFactory(ComboBoxTableCell.forTableColumn(options1));
        taskTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
			if (newSelection != null) {
				String query = "SELECT DISTINCT M.meterId FROM meter M WHERE M.meterId NOT IN (SELECT SD.meterID FROM serviceDetail SD where sd.meterID is not null)";				 connection = handler.getConnection();
			        try {
			            pst = connection.prepareStatement(query);
			            ResultSet rs = pst.executeQuery();
			            while (rs.next()) {
			                options1.add(rs.getInt("meterID"));
			            }
			            taskTable.setItems(taskList);
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
		});
        locationColumn.setOnEditCommit(event -> {
            Task task = event.getRowValue();
            task.setLocation(event.getNewValue());
            String updateServiceDetailQuery = "UPDATE ServiceDetail SET location = ? WHERE serviceId = ?";
            Connection connection = null;
            PreparedStatement pst = null;
            try {
                connection = handler.getConnection();
                pst = connection.prepareStatement(updateServiceDetailQuery);
                pst.setString(1, task.getLocation());
                pst.setInt(2, task.getServiceId());
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
        });

        meterIdColumn.setOnEditCommit(event -> {
            Task task = event.getRowValue();
            Integer newMeterId = event.getNewValue();

            if (isValidMeterId(newMeterId)) {
                task.setMeterId(newMeterId);
                updateTask(task);
                loadTaskData();
            } else {
                showAlert(Alert.AlertType.ERROR, "Validation Error", "Meter ID is not available in the meter table or it used.");
                loadTaskData();
            }
        });
        loadTaskData();
    }


    private boolean isValidMeterId(Integer meterId) {
        String meterQuery = "SELECT COUNT(*) FROM Meter WHERE meterId = ?";
        String serviceDetailQuery = "SELECT COUNT(*) FROM ServiceDetail WHERE meterId = ?";
        try (Connection connection = handler.getConnection();
             PreparedStatement meterPst = connection.prepareStatement(meterQuery);
             PreparedStatement serviceDetailPst = connection.prepareStatement(serviceDetailQuery)) {
            
            meterPst.setInt(1, meterId);
            try (ResultSet rs = meterPst.executeQuery()) {
                if (rs.next() && rs.getInt(1) == 0) {
                    return false;
                }
            }
            
            serviceDetailPst.setInt(1, meterId);
            try (ResultSet rs = serviceDetailPst.executeQuery()) {
                if (rs.next() && rs.getInt(1) > 0) {
                    return false;
                }
            }
            
            return true;
        } catch (SQLException e) {
			System.out.println(e.getMessage());
        }
        return false;
    }


    private void loadTaskData() {
        taskList.clear();
        String query = "SELECT T.*, C.fullName AS customerName, SD.location FROM Task T "
                + "JOIN Service S ON T.serviceId = S.serviceId "
                + "JOIN Customer C ON S.customerId = C.customerId "
                + "JOIN ServiceDetail SD ON S.serviceId = SD.serviceId "
                + "WHERE SD.meterId IS NULL";
        connection = handler.getConnection();
        try {
            pst = connection.prepareStatement(query);
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                taskList.add(new Task(rs.getInt("taskId"), rs.getString("toDoDate"), rs.getInt("serviceId"),
                		rs.getInt("employeeId"), rs.getString("customerName"), 0, rs.getString("location")));
            }
            taskTable.setItems(taskList);
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

    private void updateTask(Task task) {
        String updateTaskQuery = "UPDATE Task SET employeeId = ? WHERE taskId = ?";
        String updateServiceDetailQuery = "UPDATE ServiceDetail SET meterId = ?, employeeId = ?, location = ? WHERE serviceId = ?";
        Connection connection = null;
        PreparedStatement pst = null;

        try {
            connection = handler.getConnection();
            pst = connection.prepareStatement(updateTaskQuery);
            pst.setInt(1, LoginController.getEmployee().getEmployeeId());
            pst.setInt(2, task.getTaskId());
            pst.executeUpdate();
            pst.close();

            pst = connection.prepareStatement(updateServiceDetailQuery);
            pst.setInt(1, task.getMeterId());
            pst.setInt(2, LoginController.getEmployee().getEmployeeId());
            pst.setString(3, task.getLocation());
            pst.setInt(4, task.getServiceId());
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
    public void handleSubmit(ActionEvent event) {
        Task selectedTask = taskTable.getSelectionModel().getSelectedItem();
        if (selectedTask != null) {
            deleteTask(selectedTask.getTaskId(), selectedTask.getServiceId());
            taskList.remove(selectedTask);
        } else {
            showAlert(Alert.AlertType.WARNING, "No Task Selected", "Please select a task to delete.");
        }
    }

    private void deleteTask(int taskId, int serviceId) {
        String deleteTaskQuery = "DELETE FROM Task WHERE taskId = ?";
        String deleteServiceDetailQuery = "DELETE FROM ServiceDetail WHERE serviceId = ?";
        String deleteServiceQuery = "DELETE FROM Service WHERE serviceId = ?";
        Connection connection = null;
        PreparedStatement pst = null;

        try {
            connection = handler.getConnection();

            pst = connection.prepareStatement(deleteTaskQuery);
            pst.setInt(1, taskId);
            pst.executeUpdate();
            pst.close();

            pst = connection.prepareStatement(deleteServiceDetailQuery);
            pst.setInt(1, serviceId);
            pst.executeUpdate();
            pst.close();

            pst = connection.prepareStatement(deleteServiceQuery);
            pst.setInt(1, serviceId);
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

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}