package application;

import java.time.LocalDate;

public class Meter {
	private int meterId;
	private String meterDetail;
	private String payType;
	private String customerName;
	private String employeeName;
	private LocalDate dateOfDeployment;

	public Meter(int meterId, String meterDetail, String payType, String customerName, String employeeName,
			LocalDate dateOfDeployment) {
		super();
		this.meterId = meterId;
		this.meterDetail = meterDetail;
		this.payType = payType;
		this.customerName = customerName;
		this.employeeName = employeeName;
		this.dateOfDeployment = dateOfDeployment;
	}

	public int getMeterId() {
		return meterId;
	}

	public void setMeterId(int meterId) {
		this.meterId = meterId;
	}

	public String getMeterDetail() {
		return meterDetail;
	}

	public void setMeterDetail(String meterDetail) {
		this.meterDetail = meterDetail;
	}

	public String getPayType() {
		return payType;
	}

	public void setPayType(String payType) {
		this.payType = payType;
	}

	public LocalDate getDateOfDeployment() {
		return dateOfDeployment;
	}

	public void setDateOfDeployment(LocalDate dateOfDeployment) {
		this.dateOfDeployment = dateOfDeployment;
	}

	public String getCustomerName() {
		return customerName;
	}

	public void setCustomerName(String customerName) {
		this.customerName = customerName;
	}

	public String getEmployeeName() {
		return employeeName;
	}

	public void setEmployeeName(String employeeName) {
		this.employeeName = employeeName;
	}

}
