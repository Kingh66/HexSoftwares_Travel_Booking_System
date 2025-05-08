package Travel_Booking;

import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class Booking {
    private int bookingId;
    private int userId;
    private ServiceType serviceType;
    private int serviceId;
    private Timestamp bookingDate;
    private BookingStatus status;
    private double totalPrice;
    private PaymentStatus paymentStatus;
    private Date checkIn;
    private Date checkOut;
    private String notes;

    public enum ServiceType {
        FLIGHT, HOTEL, TRANSPORT;

        public static ServiceType fromString(String type) {
            try {
                return ServiceType.valueOf(type.toUpperCase());
            } catch (IllegalArgumentException e) {
                return FLIGHT; // Default value
            }
        }
    }
    
    public enum BookingStatus {
        CONFIRMED, CANCELLED, COMPLETED;

        public static BookingStatus fromString(String status) {
            try {
                return BookingStatus.valueOf(status.toUpperCase());
            } catch (IllegalArgumentException e) {
                return CONFIRMED; // Default value
            }
        }
    }
    
    public enum PaymentStatus {
        PAID, PENDING, REFUNDED;

        public static PaymentStatus fromString(String status) {
            try {
                return PaymentStatus.valueOf(status.toUpperCase());
            } catch (IllegalArgumentException e) {
                return PENDING; // Default value
            }
        }
    }

    // Constructors
    public Booking() {
        this.bookingDate = new Timestamp(System.currentTimeMillis());
        this.status = BookingStatus.CONFIRMED;
        this.paymentStatus = PaymentStatus.PENDING;
    }

    public Booking(int userId, ServiceType serviceType, int serviceId, double totalPrice) {
        this();
        setUserId(userId);
        setServiceType(serviceType);
        setServiceId(serviceId);
        setTotalPrice(totalPrice);
    }

    // Enhanced getters and setters with validation
    public int getBookingId() { return bookingId; }
    public void setBookingId(int bookingId) { 
        if(bookingId < 0) throw new IllegalArgumentException("Invalid booking ID");
        this.bookingId = bookingId; 
    }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { 
        if(userId <= 0) throw new IllegalArgumentException("Invalid user ID");
        this.userId = userId; 
    }

    public ServiceType getServiceType() { return serviceType; }
    public void setServiceType(ServiceType serviceType) { 
        this.serviceType = serviceType != null ? serviceType : ServiceType.FLIGHT;
    }

    public int getServiceId() { return serviceId; }
    public void setServiceId(int serviceId) { 
        if(serviceId <= 0) throw new IllegalArgumentException("Invalid service ID");
        this.serviceId = serviceId; 
    }

    public Timestamp getBookingDate() { return bookingDate; }
    public void setBookingDate(Timestamp bookingDate) { 
        this.bookingDate = bookingDate != null ? bookingDate : new Timestamp(System.currentTimeMillis());
    }

    public BookingStatus getStatus() { return status; }
    public void setStatus(BookingStatus status) { 
        this.status = status != null ? status : BookingStatus.CONFIRMED;
    }

    public double getTotalPrice() { return totalPrice; }
    public void setTotalPrice(double totalPrice) { 
        if(totalPrice < 0) throw new IllegalArgumentException("Price cannot be negative");
        this.totalPrice = totalPrice; 
    }

    public PaymentStatus getPaymentStatus() { return paymentStatus; }
    public void setPaymentStatus(PaymentStatus paymentStatus) { 
        this.paymentStatus = paymentStatus != null ? paymentStatus : PaymentStatus.PENDING;
    }

    public Date getCheckIn() { return checkIn; }
    public void setCheckIn(Date checkIn) { 
        if(checkIn != null && checkOut != null && checkIn.after(checkOut)) {
            throw new IllegalArgumentException("Check-in date cannot be after check-out");
        }
        this.checkIn = checkIn; 
    }

    public Date getCheckOut() { return checkOut; }
    public void setCheckOut(Date checkOut) { 
        if(checkIn != null && checkOut != null && checkOut.before(checkIn)) {
            throw new IllegalArgumentException("Check-out date cannot be before check-in");
        }
        this.checkOut = checkOut; 
    }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { 
        this.notes = notes != null ? notes.trim() : null; 
    }

    // Business logic methods
    public boolean isCancellable() {
        return status == BookingStatus.CONFIRMED && 
               paymentStatus != PaymentStatus.REFUNDED;
    }

    public int getNightsStay() {
        if(checkIn == null || checkOut == null) return 0;
        long diff = checkOut.getTime() - checkIn.getTime();
        return (int) (diff / (1000 * 60 * 60 * 24));
    }

    // Data serialization
    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("bookingId", bookingId);
        map.put("userId", userId);
        map.put("serviceType", serviceType.name());
        map.put("serviceId", serviceId);
        map.put("bookingDate", bookingDate);
        map.put("status", status.name());
        map.put("totalPrice", totalPrice);
        map.put("paymentStatus", paymentStatus.name());
        map.put("checkIn", checkIn);
        map.put("checkOut", checkOut);
        map.put("notes", notes);
        return map;
    }

    @Override
    public String toString() {
        return "Booking{" +
            "bookingId=" + bookingId +
            ", serviceType=" + serviceType +
            ", status=" + status +
            ", totalPrice=" + totalPrice +
            '}';
    }
}