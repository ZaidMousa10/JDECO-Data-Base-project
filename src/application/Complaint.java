package application;

public class Complaint {
	private int complaintId;
	private String details;
	private String date;
	private int customerId;
	private String customerName;
	
	
	
	public Complaint(int complaintId, String details, String date, int customerId, String customerName) {
		this.complaintId = complaintId;
		this.details = details;
		this.date = date;
		this.customerId = customerId;
		this.customerName = customerName;
	}
	public int getComplaintId() {
		return complaintId;
	}
	public void setComplaintId(int complaintId) {
		this.complaintId = complaintId;
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
