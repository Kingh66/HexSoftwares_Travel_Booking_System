package Travel_Booking;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public class Payment {
    private int paymentId;
    private int bookingId;
    private BigDecimal amount;
    private PaymentMethod paymentMethod;
    private String transactionId;
    private LocalDateTime paymentDate;
    private PaymentStatus status;

    public enum PaymentStatus {
        SUCCESS, FAILED, PENDING
    }

    public enum PaymentMethod {
        CREDIT_CARD, DEBIT_CARD, PAYPAL, BANK_TRANSFER
    }

    public Payment(int bookingId, BigDecimal amount, PaymentMethod paymentMethod) {
        this.bookingId = bookingId;
        this.amount = amount;
        this.paymentMethod = paymentMethod;
        this.transactionId = generateTransactionId();
        this.paymentDate = LocalDateTime.now();
        this.status = PaymentStatus.PENDING;
    }

    public boolean processPayment() {
        try {
            // Validate payment details
            if (!validatePayment()) {
                this.status = PaymentStatus.FAILED;
                return false;
            }

            // Simulate payment processing (replace with actual payment gateway integration)
            boolean paymentSuccess = simulatePaymentProcessing();

            if (paymentSuccess) {
                this.status = PaymentStatus.SUCCESS;
                saveToDatabase();
                updateBookingPaymentStatus();
                return true;
            } else {
                this.status = PaymentStatus.FAILED;
                return false;
            }
        } catch (Exception e) {
            this.status = PaymentStatus.FAILED;
            return false;
        }
    }

    private boolean validatePayment() {
        // Add actual validation logic based on payment method
        return amount.compareTo(BigDecimal.ZERO) > 0;
    }

    private boolean simulatePaymentProcessing() {
        // Simulate 95% success rate for demo purposes
        return Math.random() < 0.95;
    }

    private void saveToDatabase() throws SQLException {
        String sql = "INSERT INTO payments (booking_id, amount, payment_method, transaction_id, status) " +
                     "VALUES (?, ?, ?, ?, ?)";
        
        DatabaseManager.executeUpdate(sql,
            this.bookingId,
            this.amount,
            this.paymentMethod.toString(),
            this.transactionId,
            this.status.toString()
        );
    }

    private void updateBookingPaymentStatus() throws SQLException {
        String sql = "UPDATE bookings SET payment_status = ? WHERE booking_id = ?";
        DatabaseManager.executeUpdate(sql, 
            this.status == PaymentStatus.SUCCESS ? "paid" : "pending",
            this.bookingId
        );
    }

    private String generateTransactionId() {
        return "TX-" + System.currentTimeMillis() + "-" + (int)(Math.random() * 10000);
    }

    // Getters
    public int getPaymentId() { return paymentId; }
    public int getBookingId() { return bookingId; }
    public BigDecimal getAmount() { return amount; }
    public PaymentMethod getPaymentMethod() { return paymentMethod; }
    public String getTransactionId() { return transactionId; }
    public LocalDateTime getPaymentDate() { return paymentDate; }
    public PaymentStatus getStatus() { return status; }

    // Static method to retrieve payment history
    public static List<Map<String, Object>> getPaymentHistory(int userId) throws SQLException {
        String sql = "SELECT p.* FROM payments p " +
                     "JOIN bookings b ON p.booking_id = b.booking_id " +
                     "WHERE b.user_id = ?";
        return DatabaseManager.executeQuery(sql, userId);
    }
}