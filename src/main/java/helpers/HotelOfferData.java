package helpers;

public class HotelOfferData {
    private String hotelId;
    private String checkInDate;
    private String checkOutDate;
    private double price;
    private int beds;
    private String bedType;
    private String description;
    private int adults;
    private int rooms;
    
    public HotelOfferData() {
    	
    }

    public HotelOfferData(String hotelId, String checkInDate, String checkOutDate, double price, int beds, String bedType, String description, int adults, int rooms) {
        this.hotelId = hotelId;
        this.checkInDate = checkInDate;
        this.checkOutDate = checkOutDate;
        this.price = price;
        this.beds = beds;
        this.bedType = bedType;
        this.description = description;
        this.adults = adults;
        this.rooms = rooms;
    }

    public String getHotelId() {
        return hotelId;
    }

    public void setHotelId(String hotelId) {
        this.hotelId = hotelId;
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

	public double getPrice() {
		return price;
	}

	public void setPrice(double price) {
		this.price = price;
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

	public int getRooms() {
		return rooms;
	}

	public void setRooms(int rooms) {
		this.rooms = rooms;
	}
    
}

