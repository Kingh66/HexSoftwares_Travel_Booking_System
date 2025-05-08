package Travel_Booking;

import java.sql.Timestamp;

public class Review {
    private int reviewId;
    private int userId;
    private Booking.ServiceType serviceType;
    private int serviceId;
    private int rating;
    private String comment;
    private Timestamp reviewDate;

    // Getters and setters
    public int getReviewId() { return reviewId; }
    public void setReviewId(int reviewId) { this.reviewId = reviewId; }
    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }
    public Booking.ServiceType getServiceType() { return serviceType; }
    public void setServiceType(Booking.ServiceType serviceType) { this.serviceType = serviceType; }
    public int getServiceId() { return serviceId; }
    public void setServiceId(int serviceId) { this.serviceId = serviceId; }
    public int getRating() { return rating; }
    public void setRating(int rating) {
        if(rating < 1 || rating > 5) {
            throw new IllegalArgumentException("Rating must be between 1 and 5");
        }
        this.rating = rating;
    }
    public String getComment() { return comment; }
    public void setComment(String comment) { this.comment = comment; }
    public Timestamp getReviewDate() { return reviewDate; }
    public void setReviewDate(Timestamp reviewDate) { this.reviewDate = reviewDate; }
}