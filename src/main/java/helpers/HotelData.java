package helpers;

public class HotelData {
	// Hotel Suche List
	private String cityName;
	private String cityCode;
	private String[] amenities;
	// Hotel Suche getMultiHotelOffers
	private String checkInDate;
	private String checkOutDate;
	private int rooms;
	private int beds;
	private String bedType;
	private String description;
	private int adults;
	private String roomCategory;
	private String checkInTime;
	private String checkOutTime;
	private String hotelName;
	private String hotelCode;
	private double price;
	private HotelOfferData offer;
	
	public HotelData() {
		
	}
	public HotelData(String cityCode, String[] amenities, String hotelName, String hotelCode) {
		this.cityCode = cityCode;
		this.amenities = amenities;
		this.hotelName = hotelName;
		this.hotelCode = hotelCode;
	}

	public HotelData(String cityName, String cityCode, String[] amenities, String checkInDate, String checkOutDate,
			int rooms, int beds, String bedType, String description, int adults, String roomCategory,
			String checkInTime, String checkOutTime, String hotelName, String hotelCode, double price) {
		this.cityName = cityName;
		this.cityCode = cityCode;
		this.amenities = amenities;
		this.checkInDate = checkInDate;
		this.checkOutDate = checkOutDate;
		this.rooms = rooms;
		this.beds = beds;
		this.bedType = bedType;
		this.description = description;
		this.adults = adults;
		this.roomCategory = roomCategory;
		this.checkInTime = checkInTime;
		this.checkOutTime = checkOutTime;
		this.hotelName = hotelName;
		this.hotelCode = hotelCode;
		this.price = price;
	}
	public HotelData(String cityCode, String[] amenities) {
		this.cityCode = cityCode;
		this.amenities = amenities;
	}
	public HotelOfferData getOffer() {
		return offer;
	}
	public void setOffer(HotelOfferData offer) {
		this.offer = offer;
	}
	public String getCityName() {
		return cityName;
	}
	public void setCityName(String cityName) {
		this.cityName = cityName;
	}
	public String getCityCode() {
		return cityCode;
	}
	public void setCityCode(String cityCode) {
		this.cityCode = cityCode;
	}
	public String[] getAmenities() {
		return amenities;
	}
	public void setAmenities(String[] amenities) {
		this.amenities = amenities;
	}
	public String getCheckInDate() {
		return checkInDate;
	}
	public void setCheckInDate(String checkInDate) {
		this.checkInDate = checkInDate;
	}
	public String getCheckOutDate() {
		return checkOutDate;
	}
	public void setCheckOutDate(String checkOutDate) {
		this.checkOutDate = checkOutDate;
	}
	public int getRooms() {
		return rooms;
	}
	public void setRooms(int rooms) {
		this.rooms = rooms;
	}
	public int getBeds() {
		return beds;
	}
	public void setBeds(int beds) {
		this.beds = beds;
	}
	public String getBedType() {
		return bedType;
	}
	public void setBedType(String bedType) {
		this.bedType = bedType;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public int getAdults() {
		return adults;
	}
	public void setAdults(int adults) {
		this.adults = adults;
	}
	public String getRoomCategory() {
		return roomCategory;
	}
	public void setRoomCategory(String roomCategory) {
		this.roomCategory = roomCategory;
	}
	public String getCheckInTime() {
		return checkInTime;
	}
	public void setCheckInTime(String checkInTime) {
		this.checkInTime = checkInTime;
	}
	public String getCheckOutTime() {
		return checkOutTime;
	}
	public void setCheckOutTime(String checkOutTime) {
		this.checkOutTime = checkOutTime;
	}
	public String getHotelName() {
		return hotelName;
	}
	public void setHotelName(String hotelName) {
		this.hotelName = hotelName;
	}
	public String getHotelCode() {
		return hotelCode;
	}
	public void setHotelCode(String hotelCode) {
		this.hotelCode = hotelCode;
	}
	public double getPrice() {
		return offer != null ? offer.getPrice() : 0.0;
	}
	public void setPrice(double price) {
		this.price = price;
	}
	
}
