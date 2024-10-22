package helpers;

public class FlightData {
	private String outboundFlightNumber; 
	private String outboundAirline; 
	private String outboundAirlineName;
	private String outboundDepartureTime; 
	private String outboundArrivalTime; 
	private String outboundDepartureIataCode; 
	private String outboundArrivalIataCode; 
	private String outboundFlightTime;
	private String outboundDepartureDate;
	private String outboundArrivalDate;
	private String returnDepartureDate;
	private String returnArrivalDate;
	private String returnFlightNumber;
	private String returnAirline;
	private String retirnAirlineName;
	private String returnDepartureTime;
	private String returnArrivalTime; 
	private String returnDepartureIataCode;
	private String returnArrivalIataCode; 
	private String returnFlightTime; 
	private boolean outboundDirectFlight;
	private boolean returnDirectFlight;
	private double price;
	private String adults;

	public FlightData() {

	}

	public FlightData(String outboundFlightNumber, String outboundAirline, String outboundAirlineName,
			String outboundDepartureTime, String outboundArrivalTime, String outboundDepartureIataCode,
			String outboundArrivalIataCode, String outboundFlightTime, String returnFlightNumber, String returnAirline,
			String retirnAirlineName, String returnDepartureTime, String returnArrivalTime,
			String returnDepartureIataCode, String returnArrivalIataCode, String returnFlightTime,
			boolean outboundDirectFlight, boolean returnDirectFlight, double price, String outboundDepartureDate,
			String outboundArrivalDate, String returnDepartureDate, String returnArrivalDate, String adults) {
		this.outboundFlightNumber = outboundFlightNumber;
		this.outboundAirline = outboundAirline;
		this.outboundAirlineName = outboundAirlineName;
		this.outboundDepartureTime = outboundDepartureTime;
		this.outboundArrivalTime = outboundArrivalTime;
		this.outboundDepartureIataCode = outboundDepartureIataCode;
		this.outboundArrivalIataCode = outboundArrivalIataCode;
		this.outboundFlightTime = outboundFlightTime;
		this.returnFlightNumber = returnFlightNumber;
		this.returnAirline = returnAirline;
		this.retirnAirlineName = retirnAirlineName;
		this.returnDepartureTime = returnDepartureTime;
		this.returnArrivalTime = returnArrivalTime;
		this.returnDepartureIataCode = returnDepartureIataCode;
		this.returnArrivalIataCode = returnArrivalIataCode;
		this.returnFlightTime = returnFlightTime;
		this.outboundDirectFlight = outboundDirectFlight;
		this.returnDirectFlight = returnDirectFlight;
		this.price = price;
		this.outboundDepartureDate = outboundDepartureDate;
		this.outboundArrivalDate = outboundArrivalDate;
		this.returnDepartureDate = returnDepartureDate;
		this.returnArrivalDate = returnArrivalDate;
		this.adults = adults;
	}

	public String getOutboundFlightNumber() {
		return outboundFlightNumber;
	}

	public void setOutboundFlightNumber(String outboundFlightNumber) {
		this.outboundFlightNumber = outboundFlightNumber;
	}

	public String getOutboundAirline() {
		return outboundAirline;
	}

	public void setOutboundAirline(String outboundAirline) {
		this.outboundAirline = outboundAirline;
	}

	public String getOutboundAirlineName() {
		return outboundAirlineName;
	}

	public void setOutboundAirlineName(String outboundAirlineName) {
		this.outboundAirlineName = outboundAirlineName;
	}

	public String getOutboundDepartureTime() {
		return outboundDepartureTime;
	}

	public void setOutboundDepartureTime(String outboundDepartureTime) {
		this.outboundDepartureTime = outboundDepartureTime;
	}

	public String getOutboundArrivalTime() {
		return outboundArrivalTime;
	}

	public void setOutboundArrivalTime(String outboundArrivalTime) {
		this.outboundArrivalTime = outboundArrivalTime;
	}

	public String getOutboundDepartureIataCode() {
		return outboundDepartureIataCode;
	}

	public void setOutboundDepartureIataCode(String outboundDepartureIataCode) {
		this.outboundDepartureIataCode = outboundDepartureIataCode;
	}

	public String getOutboundArrivalIataCode() {
		return outboundArrivalIataCode;
	}

	public void setOutboundArrivalIataCode(String outboundArrivalIataCode) {
		this.outboundArrivalIataCode = outboundArrivalIataCode;
	}

	public String getOutboundFlightTime() {
		return outboundFlightTime;
	}

	public void setOutboundFlightTime(String outboundFlightTime) {
		this.outboundFlightTime = outboundFlightTime;
	}

	public String getReturnFlightNumber() {
		return returnFlightNumber;
	}

	public void setReturnFlightNumber(String returnFlightNumber) {
		this.returnFlightNumber = returnFlightNumber;
	}

	public String getReturnAirline() {
		return returnAirline;
	}

	public void setReturnAirline(String returnAirline) {
		this.returnAirline = returnAirline;
	}

	public String getRetirnAirlineName() {
		return retirnAirlineName;
	}

	public void setRetirnAirlineName(String retirnAirlineName) {
		this.retirnAirlineName = retirnAirlineName;
	}

	public String getReturnDepartureTime() {
		return returnDepartureTime;
	}

	public void setReturnDepartureTime(String returnDepartureTime) {
		this.returnDepartureTime = returnDepartureTime;
	}

	public String getReturnArrivalTime() {
		return returnArrivalTime;
	}

	public void setReturnArrivalTime(String returnArrivalTime) {
		this.returnArrivalTime = returnArrivalTime;
	}

	public String getReturnDepartureIataCode() {
		return returnDepartureIataCode;
	}

	public void setReturnDepartureIataCode(String returnDepartureIataCode) {
		this.returnDepartureIataCode = returnDepartureIataCode;
	}

	public String getReturnArrivalIataCode() {
		return returnArrivalIataCode;
	}

	public void setReturnArrivalIataCode(String returnArrivalIataCode) {
		this.returnArrivalIataCode = returnArrivalIataCode;
	}

	public String getReturnFlightTime() {
		return returnFlightTime;
	}

	public void setReturnFlightTime(String returnFlightTime) {
		this.returnFlightTime = returnFlightTime;
	}

	public boolean isOutboundDirectFlight() {
		return outboundDirectFlight;
	}

	public void setOutboundDirectFlight(boolean outboundDirectFlight) {
		this.outboundDirectFlight = outboundDirectFlight;
	}

	public boolean isReturnDirectFlight() {
		return returnDirectFlight;
	}

	public void setReturnDirectFlight(boolean returnDirectFlight) {
		this.returnDirectFlight = returnDirectFlight;
	}

	public double getPrice() {
		return price;
	}

	public void setPrice(double price) {
		this.price = price;
	}


	public String getOriginLocationDate() {
		return outboundDepartureDate;
	}

	public void setOriginLocationDate(String originLocationDate) {
		this.outboundDepartureDate = originLocationDate;
	}

	public String getOutboundDepartureDate() {
		return outboundDepartureDate;
	}

	public void setOutboundDepartureDate(String outboundDepartureDate) {
		this.outboundDepartureDate = outboundDepartureDate;
	}

	public String getOutboundArrivalDate() {
		return outboundArrivalDate;
	}

	public void setOutboundArrivalDate(String outboundArrivalDate) {
		this.outboundArrivalDate = outboundArrivalDate;
	}

	public String getReturnDepartureDate() {
		return returnDepartureDate;
	}

	public void setReturnDepartureDate(String returnDepartureDate) {
		this.returnDepartureDate = returnDepartureDate;
	}

	public String getReturnArrivalDate() {
		return returnArrivalDate;
	}

	public void setReturnArrivalDate(String returnArrivalDate) {
		this.returnArrivalDate = returnArrivalDate;
	}

	public String getAdults() {
		return adults;
	}

	public void setAdults(String adults) {
		this.adults = adults;
	}

}