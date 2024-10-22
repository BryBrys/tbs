package helpers;

public class TransferData {
    private String id;
    private String carType;
    private String provider;
    private double price;
    private String currency;
    private String pickUpLocation;
    private String dropOffLocation;
    private String pickUpDateTime;
    private String dropOffDateTime;
    private int passengers;
    private int baggage;
    private String description;
    private String startDateTime;
    private String startLocationCode;
    private String endAddressLine;
    private String endCityName;
    private String endZipCode;
    private String endLocationCode;
    private String transferType;
    
    public TransferData() {
    	
    }

    public TransferData(String carType, String provider, double price, String currency, String pickUpLocation,
			String dropOffLocation, String pickUpDateTime, String dropOffDateTime, int passengers, int baggage,
			String description) {
		this.carType = carType;
		this.provider = provider;
		this.price = price;
		this.currency = currency;
		this.pickUpLocation = pickUpLocation;
		this.dropOffLocation = dropOffLocation;
		this.pickUpDateTime = pickUpDateTime;
		this.dropOffDateTime = dropOffDateTime;
		this.passengers = passengers;
		this.baggage = baggage;
		this.description = description;
	}
	// Getters and Setters

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getCarType() {
		return carType;
	}

	public void setCarType(String carType) {
		this.carType = carType;
	}

	public String getProvider() {
		return provider;
	}

	public void setProvider(String provider) {
		this.provider = provider;
	}

	public double getPrice() {
		return price;
	}

	public void setPrice(double price) {
		this.price = price;
	}

	public String getCurrency() {
		return currency;
	}

	public void setCurrency(String currency) {
		this.currency = currency;
	}

	public String getPickUpLocation() {
		return pickUpLocation;
	}

	public void setPickUpLocation(String pickUpLocation) {
		this.pickUpLocation = pickUpLocation;
	}

	public String getDropOffLocation() {
		return dropOffLocation;
	}

	public void setDropOffLocation(String dropOffLocation) {
		this.dropOffLocation = dropOffLocation;
	}

	public String getPickUpDateTime() {
		return pickUpDateTime;
	}

	public void setPickUpDateTime(String pickUpDateTime) {
		this.pickUpDateTime = pickUpDateTime;
	}

	public String getDropOffDateTime() {
		return dropOffDateTime;
	}

	public void setDropOffDateTime(String dropOffDateTime) {
		this.dropOffDateTime = dropOffDateTime;
	}

	public int getPassengers() {
		return passengers;
	}

	public void setPassengers(int passengers) {
		this.passengers = passengers;
	}

	public int getBaggage() {
		return baggage;
	}

	public void setBaggage(int baggage) {
		this.baggage = baggage;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getStartDateTime() {
		return startDateTime;
	}

	public void setStartDateTime(String startDateTime) {
		this.startDateTime = startDateTime;
	}

	public String getStartLocationCode() {
		return startLocationCode;
	}

	public void setStartLocationCode(String startLocationCode) {
		this.startLocationCode = startLocationCode;
	}

	public String getEndAddressLine() {
		return endAddressLine;
	}

	public void setEndAddressLine(String endAddressLine) {
		this.endAddressLine = endAddressLine;
	}

	public String getEndCityName() {
		return endCityName;
	}

	public void setEndCityName(String endCityName) {
		this.endCityName = endCityName;
	}

	public String getEndZipCode() {
		return endZipCode;
	}

	public void setEndZipCode(String endZipCode) {
		this.endZipCode = endZipCode;
	}

	public String getEndLocationCode() {
		return endLocationCode;
	}

	public void setEndLocationCode(String endLocationCode) {
		this.endLocationCode = endLocationCode;
	}

	public String getTransferType() {
		return transferType;
	}

	public void setTransferType(String transferType) {
		this.transferType = transferType;
	}
}
