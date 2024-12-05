package application;

import java.util.Date;

public class Employee {
	private int employeeId;
	private String fullName;
	private String gobName;
	private String ePassword;
	private int salary;
	private String eRole;
	private String phoneNumber;
	private String address;
	private Date dateOfEmployment;
	private String email;

	public Employee(int employeeId, String fullName, String gobName, String ePassword, int salary, String eRole,
			String phoneNumber, String address, Date dateOfEmployment, String email) {
		this.employeeId = employeeId;
		this.fullName = fullName;
		this.gobName = gobName;
		this.ePassword = ePassword;
		this.salary = salary;
		this.eRole = eRole;
		this.phoneNumber = phoneNumber;
		this.address = address;
		this.dateOfEmployment = dateOfEmployment;
		this.setEmail(email);
	}

	public int getEmployeeId() {
		return employeeId;
	}

	public void setEmployeeId(int employeeId) {
		this.employeeId = employeeId;
	}

	public String getFullName() {
		return fullName;
	}

	public void setFullName(String fullName) {
		this.fullName = fullName;
	}

	public String getGobName() {
		return gobName;
	}

	public void setGobName(String gobName) {
		this.gobName = gobName;
	}

	public String getEPassword() {
		return ePassword;
	}

	public void setePassword(String ePassword) {
		this.ePassword = ePassword;
	}

	public int getSalary() {
		return salary;
	}

	public void setSalary(int salary) {
		this.salary = salary;
	}

	public String getERole() {
		return eRole;
	}

	public void seteRole(String eRole) {
		this.eRole = eRole;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public Date getDateOfEmployment() {
		return dateOfEmployment;
	}

	public void setDateOfEmployment(Date dateOfEmployment) {
		this.dateOfEmployment = dateOfEmployment;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

}
