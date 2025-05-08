package Travel_Booking;

import java.sql.Timestamp;
import java.util.Set;

public class Hotel {
    private int hotelId;
    private String name;
    private String location;
    private String description;
    private double pricePerNight;
    private int roomsAvailable;
    private Set<String> amenities;
    private double rating;
    private Timestamp createdAt;

    // Getters and setters
    public int getHotelId() { return hotelId; }
    public void setHotelId(int hotelId) { this.hotelId = hotelId; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public double getPricePerNight() { return pricePerNight; }
    public void setPricePerNight(double pricePerNight) { this.pricePerNight = pricePerNight; }
    public int getRoomsAvailable() { return roomsAvailable; }
    public void setRoomsAvailable(int roomsAvailable) { this.roomsAvailable = roomsAvailable; }
    public Set<String> getAmenities() { return amenities; }
    public void setAmenities(Set<String> amenities) { this.amenities = amenities; }
    public double getRating() { return rating; }
    public void setRating(double rating) { this.rating = rating; }
    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }
}