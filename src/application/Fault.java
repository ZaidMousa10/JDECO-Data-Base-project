package application;

public class Fault {
	private int faultId;
	private String details;
	private String date;
	private int customerId;
	private String customerName;
	
	
	
	public Fault(int faultId, String details, String date, int customerId, String customerName) {
		this.faultId = faultId;
		this.details = details;
		this.date = date;
		this.customerId = customerId;
		this.customerName = customerName;
	}
	public int getFaultId() {
		return faultId;
	}
	public void setFaultId(int faultId) {
		this.faultId = faultId;
	}
	public String getDetails() {
		return details;
	}
	public void setDetails(String details) {
		this.details = details;
	}
	public String getDate() {
		return date;
	}
	public void setDate(String date) {
		this.date = date;
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

	
}
