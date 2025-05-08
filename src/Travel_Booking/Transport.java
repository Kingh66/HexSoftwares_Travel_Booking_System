package Travel_Booking;

import java.sql.Timestamp;

public class Transport {
    private int transportId;
    private TransportType type;
    private String company;
    private String pickupLocation;
    private String dropoffLocation;
    private Timestamp departure;
    private double price;
    private int capacity;
    private boolean available;
    private Timestamp createdAt;

    public enum TransportType {
        CAR, BUS, TRAIN
    }

    // Getters and setters
    public int getTransportId() { return transportId; }
    public void setTransportId(int transportId) { this.transportId = transportId; }
    public TransportType getType() { return type; }
    public void setType(TransportType type) { this.type = type; }
    public String getCompany() { return company; }
    public void setCompany(String company) { this.company = company; }
    public String getPickupLocation() { return pickupLocation; }
    public void setPickupLocation(String pickupLocation) { this.pickupLocation = pickupLocation; }
    public String getDropoffLocation() { return dropoffLocation; }
    public void setDropoffLocation(String dropoffLocation) { this.dropoffLocation = dropoffLocation; }
    public Timestamp getDeparture() { return departure; }
    public void setDeparture(Timestamp departure) { this.departure = departure; }
    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }
    public int getCapacity() { return capacity; }
    public void setCapacity(int capacity) { this.capacity = capacity; }
    public boolean isAvailable() { return available; }
    public void setAvailable(boolean available) { this.available = available; }
    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }
}