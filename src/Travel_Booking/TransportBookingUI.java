package Travel_Booking;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.*;
import javax.imageio.ImageIO;

public class TransportBookingUI extends JFrame {
    
    private JPanel transportsPanel;
    private static final Color BACKGROUND_START = new Color(12, 46, 97);
    private static final Color BACKGROUND_END = new Color(62, 133, 185);
    private final Map<Transport.TransportType, ImageIcon> transportImages = new HashMap<>();

    public TransportBookingUI() {
        if (!checkAuthentication()) {
            showErrorAndRedirect("Please login to book transport");
            return;
        }
        initializeUI();
        loadTransportImages();
        loadTransports();
    }

    private void initializeUI() {
        setTitle("Transport Booking");
        setSize(1200, 800);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        mainPanel.setOpaque(false);

        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
        headerPanel.add(createBackButton(), BorderLayout.WEST);

        JLabel titleLabel = new JLabel("Available Transport");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        headerPanel.add(titleLabel, BorderLayout.CENTER);

        mainPanel.add(headerPanel, BorderLayout.NORTH);

        // Transport Cards Panel
        transportsPanel = new JPanel(new GridLayout(0, 3, 20, 20));
        transportsPanel.setOpaque(false);
        JScrollPane scrollPane = new JScrollPane(transportsPanel);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        // Background Panel
        JPanel backgroundPanel = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                GradientPaint gradient = new GradientPaint(
                    0, 0, BACKGROUND_START, getWidth(), getHeight(), BACKGROUND_END
                );
                g2d.setPaint(gradient);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        backgroundPanel.add(mainPanel);
        setContentPane(backgroundPanel);
    }

    private JButton createBackButton() {
        JButton btnBack = new JButton("← Back to Dashboard");
        btnBack.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnBack.setForeground(Color.WHITE);
        btnBack.setBackground(new Color(62, 133, 185));
        btnBack.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.WHITE, 1),
            BorderFactory.createEmptyBorder(8, 20, 8, 20)
        ));
        btnBack.setFocusPainted(false);
        btnBack.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        btnBack.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btnBack.setBackground(new Color(12, 46, 97));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btnBack.setBackground(new Color(62, 133, 185));
            }
        });

        btnBack.addActionListener(e -> {
            new UserDashboard().setVisible(true);
            dispose();
        });

        return btnBack;
    }

    private void loadTransportImages() {
        transportImages.put(Transport.TransportType.CAR, loadImageIcon("car.png", 200, 200));
        transportImages.put(Transport.TransportType.BUS, loadImageIcon("bus.png", 200, 200));
        transportImages.put(Transport.TransportType.TRAIN, loadImageIcon("train.png", 200, 200));
    }

    private void loadTransports() {
        try {
            List<Map<String, Object>> transports = DatabaseManager.executeQuery(
                "SELECT * FROM transport WHERE available = true"
            );
            
            transportsPanel.removeAll();
            
            for (Map<String, Object> transport : transports) {
                transportsPanel.add(createTransportCard(transport));
            }
            
            transportsPanel.revalidate();
            transportsPanel.repaint();
            
        } catch (SQLException ex) {
            showError("Error loading transports: " + ex.getMessage());
        }
    }

    private JPanel createTransportCard(Map<String, Object> transport) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(new Color(255, 255, 255, 30));
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.WHITE, 1),
            BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));

        // Transport Image
        Transport.TransportType type = Transport.TransportType.valueOf(
            ((String) transport.get("type")).toUpperCase()
        );
        ImageIcon transportIcon = transportImages.get(type);
        JLabel imageLabel = new JLabel(transportIcon);
        imageLabel.setHorizontalAlignment(JLabel.CENTER);
        card.add(imageLabel, BorderLayout.NORTH);

        // Transport Details
        JPanel detailsPanel = new JPanel(new GridLayout(0, 1, 10, 10));
        detailsPanel.setOpaque(false);

        addDetail(detailsPanel, "Type", transport.get("type"));
        addDetail(detailsPanel, "Company", transport.get("company"));
        addDetail(detailsPanel, "Route", transport.get("pickup_location") + " → " + transport.get("dropoff_location"));
        
        LocalDateTime departure = (LocalDateTime) transport.get("departure");
        addDetail(detailsPanel, "Departure", formatDateTime(departure));
        
        BigDecimal price = (BigDecimal) transport.get("price");
        addDetail(detailsPanel, "Price", String.format("$%.2f", price.doubleValue()));
        addDetail(detailsPanel, "Capacity", transport.get("capacity"));

        // Book Button
        JButton bookButton = new JButton("Book Now");
        styleButton(bookButton);
        bookButton.addActionListener(e -> bookTransport(transport));

        card.add(detailsPanel, BorderLayout.CENTER);
        card.add(bookButton, BorderLayout.SOUTH);

        return card;
    }

    private String formatDateTime(LocalDateTime dateTime) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM dd, yyyy HH:mm");
        return dateTime.format(formatter);
    }

    private void addDetail(JPanel panel, String label, Object value) {
        JPanel detailPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        detailPanel.setOpaque(false);
        
        JLabel labelLbl = new JLabel(label + ":");
        labelLbl.setForeground(new Color(200, 200, 200));
        labelLbl.setFont(new Font("Segoe UI", Font.BOLD, 14));
        
        JLabel valueLbl = new JLabel(value.toString());
        valueLbl.setForeground(Color.WHITE);
        valueLbl.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        detailPanel.add(labelLbl);
        detailPanel.add(valueLbl);
        panel.add(detailPanel);
    }

    private void styleButton(JButton button) {
        button.setFont(new Font("Segoe UI", Font.BOLD, 16));
        button.setForeground(Color.WHITE);
        button.setBackground(new Color(62, 133, 185));
        button.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.WHITE, 1),
            BorderFactory.createEmptyBorder(10, 30, 10, 30)
        ));
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }
    
    private boolean checkAuthentication() {
        if (Session.getCurrentUserId() == 0) {
            JOptionPane.showMessageDialog(null, 
                "You need to login first!", 
                "Authentication Required", 
                JOptionPane.WARNING_MESSAGE);
            new UserLogin().setVisible(true);
            dispose();
            return false;
        }
        return true;
    }

    private void bookTransport(Map<String, Object> transport) {
        int userId = Session.getCurrentUserId();
        if (userId == 0) {
            showErrorAndRedirect("Session expired. Please login again");
            return;
        }

        int transportId = (int) transport.get("transport_id");
        int capacity = (int) transport.get("capacity");
        BigDecimal price = (BigDecimal) transport.get("price");

        if (capacity <= 0) {
            showError("No seats available for this transport!");
            return;
        }

        PaymentDialog paymentDialog = new PaymentDialog(this, price);
        paymentDialog.setVisible(true);
        
        if (!paymentDialog.isPaymentSuccessful()) {
            showError("Payment processing cancelled or failed");
            return;
        }
        
        String paymentMethod = paymentDialog.getDatabasePaymentMethod();

        Connection conn = null;
        try {
            conn = DatabaseManager.getConnection();
            conn.setAutoCommit(false);

            try (PreparedStatement stmt = conn.prepareStatement(
                    "UPDATE transport SET capacity = capacity - 1 WHERE transport_id = ?")) {
                stmt.setInt(1, transportId);
                stmt.executeUpdate();
            }

            int bookingId;
            try (PreparedStatement stmt = conn.prepareStatement(
                    "INSERT INTO bookings (user_id, service_type, service_id, total_price, booking_date) " +
                    "VALUES (?, 'transport', ?, ?, NOW())", Statement.RETURN_GENERATED_KEYS)) {
                
                stmt.setInt(1, userId);
                stmt.setInt(2, transportId);
                stmt.setBigDecimal(3, price);
                stmt.executeUpdate();

                try (ResultSet rs = stmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        bookingId = rs.getInt(1);
                    } else {
                        throw new SQLException("Failed to get booking ID");
                    }
                }
            }

            String transactionId = generateTransactionId();
            try (PreparedStatement stmt = conn.prepareStatement(
                "INSERT INTO payments (booking_id, amount, payment_method, transaction_id) " +
                "VALUES (?, ?, ?, ?)")) {

                stmt.setInt(1, bookingId);
                stmt.setBigDecimal(2, price);
                stmt.setString(3, paymentMethod);
                stmt.setString(4, transactionId);
                stmt.executeUpdate();
            }

            conn.commit();
            showPaymentSuccess(transactionId);
            loadTransports();

        } catch (SQLException ex) {
            // Error handling
        } finally {
            DatabaseManager.closeConnection(conn);
        }
    }

    // ... [Remaining methods identical to FlightBookingUI] ...
    
    private boolean processPayment(BigDecimal amount) {
    TransportBookingUI.PaymentDialog paymentDialog = new TransportBookingUI.PaymentDialog(this, amount);
    paymentDialog.setVisible(true);
    return paymentDialog.isPaymentSuccessful();
}
    
    private void showPaymentSuccess(String transactionId) {
    JOptionPane.showMessageDialog(this,
        "Payment processed successfully!\nTransaction ID: " + transactionId,
        "Payment Complete",
        JOptionPane.INFORMATION_MESSAGE);
}

    private void showErrorAndRedirect(String message) {
        showError(message);
        dispose();
        new UserLogin().setVisible(true);
    }
    
     private String generateTransactionId() {
        return "TX-" + System.currentTimeMillis() + "-" + (int)(Math.random() * 10000);
    }
     
     private class PaymentDialog extends JDialog {
    private boolean paymentSuccessful = false;
    private final JTextField txtCardNumber = new JTextField(20);
    private final JTextField txtExpiry = new JTextField(5);
    private final JTextField txtCVV = new JTextField(3);
    private final JTextField txtCardName = new JTextField(20);
    private final JComboBox<String> paymentMethodCombo = new JComboBox<>(
        new String[] {"Credit Card", "Debit Card", "PayPal", "Bank Transfer"}
    );

    public PaymentDialog(JFrame parent, BigDecimal amount) {
        super(parent, "Payment Processing", true);
        setSize(400, 300);
        setLocationRelativeTo(parent);
        
        JPanel mainPanel = new JPanel(new GridLayout(0, 1, 10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        mainPanel.add(new JLabel("Total Amount: $" + amount.setScale(2, BigDecimal.ROUND_HALF_UP)));
        mainPanel.add(createFormField("Card Number:", txtCardNumber));
        mainPanel.add(createFormField("Expiry (MM/YY):", txtExpiry));
        mainPanel.add(createFormField("CVV:", txtCVV));
        mainPanel.add(createFormField("Cardholder Name:", txtCardName));

        mainPanel.add(createFormField("Payment Method:", paymentMethodCombo));
        JButton btnPay = new JButton("Confirm Payment");
        btnPay.addActionListener(e -> validateAndProcessPayment());
        styleButton(btnPay);

        JButton btnCancel = new JButton("Cancel");
        btnCancel.addActionListener(e -> dispose());
        styleButton(btnCancel);

        JPanel buttonPanel = new JPanel(new GridLayout(1, 2, 10, 0));
        buttonPanel.add(btnPay);
        buttonPanel.add(btnCancel);

        add(mainPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
        
        
        
    }
    
    private String getDatabasePaymentMethod() {
        String selected = (String) paymentMethodCombo.getSelectedItem();
        return selected.replace(" ", "_").toLowerCase();
    }

    private JPanel createFormField(String label, JComponent component) {
        JPanel panel = new JPanel(new BorderLayout(10, 5));
        panel.add(new JLabel(label), BorderLayout.WEST);
        panel.add(component, BorderLayout.CENTER);
        return panel;
    }


    

    private void validateAndProcessPayment() {
        if (validatePaymentDetails()) {
            // Simulate payment processing
            paymentSuccessful = new Random().nextDouble() < 0.95; // 95% success rate
            dispose();
        }
    }

    private boolean validatePaymentDetails() {
        String cardNumber = txtCardNumber.getText().replaceAll("\\s+", "");
        String expiry = txtExpiry.getText();
        String cvv = txtCVV.getText();
        String name = txtCardName.getText().trim();

        if (cardNumber.length() != 10 || !cardNumber.matches("\\d+")) {
            showError("Invalid card number");
            return false;
        }

        if (!expiry.matches("(0[1-9]|1[0-2])/\\d{2}")) {
            showError("Invalid expiry date (MM/YY)");
            return false;
        }

        if (!cvv.matches("\\d{3,4}")) {
            showError("Invalid CVV");
            return false;
        }

        if (name.isEmpty()) {
            showError("Cardholder name required");
            return false;
        }

        return true;
    }

    public boolean isPaymentSuccessful() {
        return paymentSuccessful;
    }
}
     
      private String formatDateTime(Timestamp timestamp) {
        return new java.text.SimpleDateFormat("MMM dd, yyyy HH:mm").format(timestamp);
    }

    private ImageIcon loadImageIcon(String path, int width, int height) {
        try {
            java.io.InputStream imgStream = getClass().getResourceAsStream("/Travel_Booking/icons/" + path);
            if (imgStream != null) {
                ImageIcon originalIcon = new ImageIcon(ImageIO.read(imgStream));
                Image scaledImage = originalIcon.getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH);
                return new ImageIcon(scaledImage);
            }
        } catch (Exception e) {
            System.err.println("Error loading icon: " + e.getMessage());
        }
        return new ImageIcon();
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }
     
}