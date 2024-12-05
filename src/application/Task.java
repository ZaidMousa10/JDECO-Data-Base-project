package application;

public class Task {
    private int taskId;
    private String toDoDate;
    private int serviceId;
    private int employeeId;
    private String customerName;
    private int meterId;
    private String location;

    // Constructor
    public Task(int taskId, String toDoDate, int serviceId, int employeeId, String customerName, int meterId, String location) {
        this.taskId = taskId;
        this.toDoDate = toDoDate;
        this.serviceId = serviceId;
        this.employeeId = employeeId;
        this.customerName = customerName;
        this.meterId = meterId;
        this.location = location;
    }

    // Getters and setters
    public int getTaskId() {
        return taskId;
    }

    public void setTaskId(int taskId) {
        this.taskId = taskId;
    }

    public String getToDoDate() {
        return toDoDate;
    }

    public void setToDoDate(String toDoDate) {
        this.toDoDate = toDoDate;
    }

    public int getServiceId() {
        return serviceId;
    }

    public void setServiceId(int serviceId) {
        this.serviceId = serviceId;
    }

    public int getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(int employeeId) {
        this.employeeId = employeeId;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public int getMeterId() {
        return meterId;
    }

    public void setMeterId(int meterId) {
        this.meterId = meterId;
    }

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}
    
}


