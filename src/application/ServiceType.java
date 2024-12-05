package application;

public class ServiceType {
	private int serviceTypeId;
	private String serviceTypeName;

	public ServiceType(int serviceTypeId, String serviceTypeName) {
		setServiceTypeId(serviceTypeId);
		setServiceTypeName(serviceTypeName);
	}

	public int getServiceTypeId() {
		return serviceTypeId;
	}

	public void setServiceTypeId(int serviceTypeId) {
		this.serviceTypeId = serviceTypeId;
	}

	public String getServiceTypeName() {
		return serviceTypeName;
	}

	public void setServiceTypeName(String serviceTypeName) {
		this.serviceTypeName = serviceTypeName;
	}

}
