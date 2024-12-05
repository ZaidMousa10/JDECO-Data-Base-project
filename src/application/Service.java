package application;

import java.util.Date;

public class Service {
    private int serviceId;
    private double debt;
    private String serviceType;
    private String status;
    private Date serviceDate;
    private int customerId;
    private String customerName;
    private String location;

    public Service(int serviceId, double debt, String serviceType, String status, Date serviceDate, int customerId, String customerName, String location) {
        this.serviceId = serviceId;
        this.debt = debt;
        this.serviceType = serviceType;
        this.status = status;
        this.serviceDate = serviceDate;
        this.customerId = customerId;
        this.customerName = customerName;
        this.location = location;
    }

    public int getServiceId() {
        return serviceId;
    }

    public void setServiceId(int serviceId) {
        this.serviceId = serviceId;
    }

    public double getDebt() {
        return debt;
    }

    public void setDebt(double debt) {
        this.debt = debt;
    }

    public String getServiceType() {
        return serviceType;
    }

    public void setServiceType(String serviceType) {
        this.serviceType = serviceType;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Date getServiceDate() {
        return serviceDate;
    }

    public void setServiceDate(Date serviceDate) {
        this.serviceDate = serviceDate;
    }

	public int getCustomerId() {
		return customerId;
	}

	public void setCustomerId(int customerId) {
		this.customerId = customerId;
	}

	public String getCustomerName() {
		return customerName;
	}

	public void setCustomerName(String customerName) {
		this.customerName = customerName;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}
    
    
}
