package Travel_Booking;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.math.BigDecimal;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;

public class BookingHistoryUI extends JFrame {
    
    private JTable bookingsTable;
    private JScrollPane scrollPane;

    public BookingHistoryUI() {
        if (!checkAuthentication()) {
            showErrorAndRedirect("Please login to view bookings");
            return;
        }
        initializeUI();
        loadBookings();
    }

    private void initializeUI() {
        setTitle("Booking History");
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

        JLabel titleLabel = new JLabel("Booking History");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        headerPanel.add(titleLabel, BorderLayout.CENTER);

        mainPanel.add(headerPanel, BorderLayout.NORTH);

        // Bookings Table
        String[] columns = {"Booking ID", "Service Type", "Details", "Booking Date", 
                          "Total Price", "Status", "Payment Status", "Actions"};
        DefaultTableModel model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 7; // Only action column is editable
            }
        };
        
        bookingsTable = new JTable(model);
        styleTable();
        
        scrollPane = new JScrollPane(bookingsTable);
        scrollPane.getViewport().setBackground(new Color(255, 255, 255, 30));
        scrollPane.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(255, 255, 255, 80), 1),
            BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));

        mainPanel.add(scrollPane, BorderLayout.CENTER);

        // Background Panel
        JPanel backgroundPanel = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                GradientPaint gradient = new GradientPaint(
                    0, 0, new Color(12, 46, 97), 
                    getWidth(), getHeight(), new Color(62, 133, 185)
                );
                g2d.setPaint(gradient);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        backgroundPanel.add(mainPanel);
        setContentPane(backgroundPanel);
    }

    private JButton createBackButton() {
        JButton btnBack = new JButton("← Back to Main");
        btnBack.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        btnBack.setForeground(Color.WHITE);
        btnBack.setBackground(new Color(62, 133, 185));
        btnBack.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.WHITE, 1),
            BorderFactory.createEmptyBorder(5, 15, 5, 15)
        ));
        btnBack.setFocusPainted(false);
        btnBack.setOpaque(true);
        btnBack.setContentAreaFilled(true);

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

    private void styleTable() {
        bookingsTable.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, 
                    isSelected, hasFocus, row, column);
                
                c.setForeground(Color.BLACK);
                c.setBackground(new Color(255, 255, 255, 30));
                c.setFont(new Font("Segoe UI", Font.PLAIN, 14));
                setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
                
                return c;
            }
        });
        
        JTableHeader header = bookingsTable.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 14));
        header.setBackground(new Color(62, 133, 185));
        header.setForeground(Color.WHITE);
        
        // Add cancel buttons
        bookingsTable.getColumnModel().getColumn(7).setCellRenderer(new ButtonRenderer());
        bookingsTable.getColumnModel().getColumn(7).setCellEditor(new ButtonEditor());
    }

    private void loadBookings() {
    try {
        DefaultTableModel model = (DefaultTableModel) bookingsTable.getModel();
        model.setRowCount(0);

        int userId = Session.getCurrentUserId();
        List<Map<String, Object>> bookings = DatabaseManager.executeQuery(
            "SELECT b.*, p.status AS payment_status " +
            "FROM bookings b " +
            "LEFT JOIN payments p ON b.booking_id = p.booking_id " +
            "WHERE b.user_id = ? " +
            "ORDER BY b.booking_date DESC", 
            userId
        );

        for (Map<String, Object> booking : bookings) {
            Object[] row = new Object[8];
            row[0] = booking.get("booking_id");
            row[1] = booking.get("service_type");
            row[2] = getServiceDetails(booking);
            
            // Handle date conversion
            LocalDateTime bookingDate = (LocalDateTime) booking.get("booking_date");
            row[3] = formatDate(bookingDate);
            
            row[4] = String.format("$%.2f", ((BigDecimal) booking.get("total_price")).doubleValue());
            row[5] = booking.get("status");
            row[6] = booking.get("payment_status");
            row[7] = "Cancel";
            
            if (!"confirmed".equalsIgnoreCase(booking.get("status").toString())) {
                row[7] = "";
            }
            
            model.addRow(row);
        }
    } catch (SQLException ex) {
        showError("Error loading bookings: " + ex.getMessage());
    }
}

    private String getServiceDetails(Map<String, Object> booking) {
        try {
            String serviceType = (String) booking.get("service_type");
            int serviceId = (int) booking.get("service_id");
            
            switch (serviceType.toLowerCase()) {
                case "flight":
                    return getFlightDetails(serviceId);
                case "hotel":
                    return getHotelDetails(serviceId);
                case "transport":
                    return getTransportDetails(serviceId);
                default:
                    return "Service Details";
            }
        } catch (SQLException e) {
            return "Details unavailable";
        }
    }

    private String getFlightDetails(int serviceId) throws SQLException {
        List<Map<String, Object>> results = DatabaseManager.executeQuery(
            "SELECT origin, destination FROM flights WHERE flight_id = ?",
            serviceId
        );
        if (!results.isEmpty()) {
            Map<String, Object> flight = results.get(0);
            return flight.get("origin") + " → " + flight.get("destination");
        }
        return "Flight details";
    }

    private String getHotelDetails(int serviceId) throws SQLException {
        List<Map<String, Object>> results = DatabaseManager.executeQuery(
            "SELECT name, location FROM hotels WHERE hotel_id = ?",
            serviceId
        );
        if (!results.isEmpty()) {
            Map<String, Object> hotel = results.get(0);
            return hotel.get("name") + ", " + hotel.get("location");
        }
        return "Hotel details";
    }

    private String getTransportDetails(int serviceId) throws SQLException {
        List<Map<String, Object>> results = DatabaseManager.executeQuery(
            "SELECT pickup_location, dropoff_location FROM transport WHERE transport_id = ?",
            serviceId
        );
        if (!results.isEmpty()) {
            Map<String, Object> transport = results.get(0);
            return transport.get("pickup_location") + " → " + transport.get("dropoff_location");
        }
        return "Transport details";
    }

    private String formatDate(LocalDateTime dateTime) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM dd, yyyy HH:mm");
        return dateTime.format(formatter);
    }

    private class ButtonRenderer extends JButton implements TableCellRenderer {
        public ButtonRenderer() {
            setOpaque(true);
            styleButton(this);
        }

        public Component getTableCellRendererComponent(JTable table, Object value,
            boolean isSelected, boolean hasFocus, int row, int column) {
            setText((value == null) ? "" : value.toString());
            return this;
        }
    }

    private class ButtonEditor extends DefaultCellEditor {
        private JButton button;
        private String label;
        private boolean isPushed;

        public ButtonEditor() {
            super(new JTextField());
            button = new JButton();
            styleButton(button);
            button.addActionListener(e -> fireEditingStopped());
        }

        public Component getTableCellEditorComponent(JTable table, Object value,
            boolean isSelected, int row, int column) {
            label = (value == null) ? "" : value.toString();
            button.setText(label);
            isPushed = true;
            return button;
        }

        public Object getCellEditorValue() {
            if (isPushed) {
                int bookingId = (int) bookingsTable.getValueAt(bookingsTable.getSelectedRow(), 0);
                cancelBooking(bookingId);
            }
            isPushed = false;
            return label;
        }
    }

    private void cancelBooking(int bookingId) {
        int confirm = JOptionPane.showConfirmDialog(this,
            "Are you sure you want to cancel this booking?",
            "Confirm Cancellation",
            JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                DatabaseManager.executeUpdate(
                    "UPDATE bookings SET status = 'cancelled' WHERE booking_id = ?",
                    bookingId
                );
                
                // Update availability in respective service table
                updateServiceAvailability(bookingId);
                
                JOptionPane.showMessageDialog(this, "Booking cancelled successfully!");
                loadBookings();
            } catch (SQLException ex) {
                showError("Cancellation failed: " + ex.getMessage());
            }
        }
    }

    private void updateServiceAvailability(int bookingId) throws SQLException {
        List<Map<String, Object>> booking = DatabaseManager.executeQuery(
            "SELECT service_type, service_id FROM bookings WHERE booking_id = ?",
            bookingId
        );
        
        if (!booking.isEmpty()) {
            String serviceType = (String) booking.get(0).get("service_type");
            int serviceId = (int) booking.get(0).get("service_id");

            switch (serviceType.toLowerCase()) {
                case "flight":
                    DatabaseManager.executeUpdate(
                        "UPDATE flights SET seats_available = seats_available + 1 WHERE flight_id = ?",
                        serviceId
                    );
                    break;
                case "hotel":
                    DatabaseManager.executeUpdate(
                        "UPDATE hotels SET rooms_available = rooms_available + 1 WHERE hotel_id = ?",
                        serviceId
                    );
                    break;
                case "transport":
                    DatabaseManager.executeUpdate(
                        "UPDATE transport SET capacity = capacity + 1 WHERE transport_id = ?",
                        serviceId
                    );
                    break;
            }
        }
    }

    private void styleButton(JButton button) {
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setForeground(Color.WHITE);
        button.setBackground(new Color(62, 133, 185));
        button.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.WHITE, 1),
            BorderFactory.createEmptyBorder(5, 15, 5, 15)
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

    private void showErrorAndRedirect(String message) {
        showError(message);
        dispose();
        new UserLogin().setVisible(true);
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new BookingHistoryUI().setVisible(true));
    }
}