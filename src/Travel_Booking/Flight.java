package Travel_Booking;

import java.sql.Timestamp;

public class Flight {
    private int flightId;
    private String airline;
    private String origin;
    private String destination;
    private Timestamp departure;
    private Timestamp arrival;
    private double price;
    private int seatsAvailable;
    private FlightStatus status;
    private Timestamp createdAt;

    public enum FlightStatus {
        SCHEDULED, DELAYED, CANCELED, COMPLETED
    }

    // Getters and setters
    public int getFlightId() { return flightId; }
    public void setFlightId(int flightId) { this.flightId = flightId; }
    public String getAirline() { return airline; }
    public void setAirline(String airline) { this.airline = airline; }
    public String getOrigin() { return origin; }
    public void setOrigin(String origin) { this.origin = origin; }
    public String getDestination() { return destination; }
    public void setDestination(String destination) { this.destination = destination; }
    public Timestamp getDeparture() { return departure; }
    public void setDeparture(Timestamp departure) { this.departure = departure; }
    public Timestamp getArrival() { return arrival; }
    public void setArrival(Timestamp arrival) { this.arrival = arrival; }
    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }
    public int getSeatsAvailable() { return seatsAvailable; }
    public void setSeatsAvailable(int seatsAvailable) { this.seatsAvailable = seatsAvailable; }
    public FlightStatus getStatus() { return status; }
    public void setStatus(FlightStatus status) { this.status = status; }
    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }
}