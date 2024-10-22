package helpers;

public class TransferOfferRequest {
    private String startDateTime;
    private String startLocationCode;
    private String endAddressLine;
    private String endCityName;
    private String endZipCode;
    private String endLocationCode;
    private String transferType;
    private int passengers;
	private TransferData transferData;
	private float preis;
	private String currency;
    
    public String getCurrency() {
		return currency;
	}

	public void setCurrency(String currency) {
		currency = currency;
	}

	public TransferOfferRequest(TransferData transferData) {
        this.transferData = transferData;
    }

	public TransferData getTransferData() {
		return transferData;
	}

	public void setTransferData(TransferData transferData) {
		this.transferData = transferData;
	}

	public float getPreis() {
		return preis;
	}

	public void setPreis(float preis) {
		this.preis = preis;
	}

	public TransferOfferRequest(String startDateTime, String startLocationCode, String endAddressLine,
			String endCityName, String endZipCode, String endLocationCode, String transferType, int passengers,
			TransferData transferData, float preis, String currency) {
		this.startDateTime = startDateTime;
		this.startLocationCode = startLocationCode;
		this.endAddressLine = endAddressLine;
		this.endCityName = endCityName;
		this.endZipCode = endZipCode;
		this.endLocationCode = endLocationCode;
		this.transferType = transferType;
		this.passengers = passengers;
		this.transferData = transferData;
		this.preis = preis;
		this.currency = currency;
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

    public String getEndCountryCode() {
        return endLocationCode;
    }

    public void setEndCountryCode(String endCountryCode) {
        this.endLocationCode = endCountryCode;
    }

    public String getTransferType() {
        return transferType;
    }

    public void setTransferType(String transferType) {
        this.transferType = transferType;
    }

    public int getPassengers() {
        return passengers;
    }

    public String getEndLocationCode() {
		return endLocationCode;
	}

	public void setEndLocationCode(String endLocationCode) {
		this.endLocationCode = endLocationCode;
	}

	public void setPassengers(int passengers) {
        this.passengers = passengers;
    }
}
