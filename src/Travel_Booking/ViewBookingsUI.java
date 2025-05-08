package Travel_Booking;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;
import java.util.Map;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

public class ViewBookingsUI extends JFrame {
    
    private JTable bookingsTable;
    private JComboBox<String> filterServiceType;
    private JComboBox<String> filterStatus;
    private JComboBox<String> filterPaymentStatus;

    static {
        try {
            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
            UIManager.put("Button.background", new Color(62, 133, 185));
            UIManager.put("Button.foreground", Color.WHITE);
            UIManager.put("Button.border", BorderFactory.createLineBorder(Color.WHITE));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public ViewBookingsUI() {
        initializeUI();
    }

    private void initializeUI() {
        setTitle("Manage Bookings");
        setSize(1400, 800);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel mainPanel = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                Color startColor = new Color(12, 46, 97);
                Color endColor = new Color(62, 133, 185);
                GradientPaint gradient = new GradientPaint(0, 0, startColor, getWidth(), getHeight(), endColor);
                g2d.setPaint(gradient);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        mainPanel.add(createHeaderPanel(), BorderLayout.NORTH);
        mainPanel.add(createMainContentPanel(), BorderLayout.CENTER);

        add(mainPanel);
        refreshBookingsTable();
    }

    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);
        
        JButton btnBack = createBackButton();
        headerPanel.add(btnBack, BorderLayout.WEST);

        JLabel titleLabel = new JLabel("Booking Management");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        headerPanel.add(titleLabel, BorderLayout.CENTER);

        return headerPanel;
    }

    private JPanel createMainContentPanel() {
        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.setOpaque(false);

        contentPanel.add(createFilterPanel(), BorderLayout.NORTH);
        contentPanel.add(createTablePanel(), BorderLayout.CENTER);
        contentPanel.add(createActionPanel(), BorderLayout.SOUTH);

        return contentPanel;
    }

    private JPanel createFilterPanel() {
    JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
    filterPanel.setOpaque(false);

    // Create and style labels
    JLabel serviceTypeLabel = new JLabel("Service Type:");
    JLabel statusLabel = new JLabel("Status:");
    JLabel paymentStatusLabel = new JLabel("Payment Status:");
    
    serviceTypeLabel.setForeground(Color.WHITE);
    statusLabel.setForeground(Color.WHITE);
    paymentStatusLabel.setForeground(Color.WHITE);

    // Create combo boxes
    filterServiceType = new JComboBox<>(new String[]{"All", "Flight", "Hotel", "Transport"});
    filterStatus = new JComboBox<>(new String[]{"All", "Confirmed", "Cancelled", "Completed"});
    filterPaymentStatus = new JComboBox<>(new String[]{"All", "Paid", "Pending", "Refunded"});

    // Style combo boxes
    styleFilterComboBox(filterServiceType);
    styleFilterComboBox(filterStatus);
    styleFilterComboBox(filterPaymentStatus);

    // Create and style apply button
    JButton btnApplyFilters = new JButton("Apply Filters");
    btnApplyFilters.setForeground(Color.BLACK);
    btnApplyFilters.setBackground(Color.WHITE);
    btnApplyFilters.setFont(new Font("Segoe UI", Font.BOLD, 14));
    btnApplyFilters.setBorder(BorderFactory.createCompoundBorder(
        BorderFactory.createLineBorder(new Color(200, 200, 200)),
        BorderFactory.createEmptyBorder(5, 15, 5, 15)
    ));
    btnApplyFilters.addActionListener(e -> refreshBookingsTable());

    // Add hover effects
    btnApplyFilters.addMouseListener(new java.awt.event.MouseAdapter() {
        public void mouseEntered(java.awt.event.MouseEvent evt) {
            btnApplyFilters.setBackground(new Color(240, 240, 240));
        }
        public void mouseExited(java.awt.event.MouseEvent evt) {
            btnApplyFilters.setBackground(Color.WHITE);
        }
    });

    // Add components to panel
    filterPanel.add(serviceTypeLabel);
    filterPanel.add(filterServiceType);
    filterPanel.add(statusLabel);
    filterPanel.add(filterStatus);
    filterPanel.add(paymentStatusLabel);
    filterPanel.add(filterPaymentStatus);
    filterPanel.add(btnApplyFilters);

    return filterPanel;
}
    
    private JPanel createTablePanel() {
        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setOpaque(false);
        tablePanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));

        String[] columns = {"Booking ID", "User", "Service Type", "Service Details", 
                           "Booking Date", "Status", "Payment Status", "Total Price"};
        DefaultTableModel model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        bookingsTable = new JTable(model);
        bookingsTable.setDefaultRenderer(Object.class, new BookingTableRenderer());
        bookingsTable.setRowHeight(35);
        bookingsTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        bookingsTable.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        JScrollPane scrollPane = new JScrollPane(bookingsTable);
        scrollPane.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(255, 255, 255, 80), 1),
            BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));

        tablePanel.add(scrollPane, BorderLayout.CENTER);
        return tablePanel;
    }

    private JPanel createActionPanel() {
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        actionPanel.setOpaque(false);

        JButton btnRefresh = createHoverButton("Refresh");
        

        btnRefresh.addActionListener(e -> refreshBookingsTable());
        

        actionPanel.add(btnRefresh);
        

        return actionPanel;
    }

    private void refreshBookingsTable() {
    try {
        DefaultTableModel model = (DefaultTableModel) bookingsTable.getModel();
        model.setRowCount(0);

        List<Map<String, Object>> bookings = fetchFilteredBookings();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");

        for (Map<String, Object> booking : bookings) {
            String serviceDetails = getServiceDetails(
                booking.get("service_type").toString(),
                ((Number) booking.get("service_id")).intValue()
            );

            // Handle date formatting properly
            Object dateObj = booking.get("booking_date");
            String formattedDate = "N/A";
            
            if (dateObj instanceof Timestamp) {
                formattedDate = dateFormat.format((Timestamp) dateObj);
            } else if (dateObj instanceof java.sql.Date) {
                
            }

            model.addRow(new Object[]{
                booking.get("booking_id"),
                booking.get("username"),
                booking.get("service_type"),
                serviceDetails,
                formattedDate,  // Use the properly formatted date
                booking.get("status"),
                booking.get("payment_status"),
                String.format("$%.2f", booking.get("total_price"))
            });
        }
    } catch (SQLException ex) {
        showError("Error loading bookings: " + ex.getMessage());
    }
}

    private List<Map<String, Object>> fetchFilteredBookings() throws SQLException {
        String query = "SELECT b.booking_id, u.username, b.service_type, b.service_id, " +
                      "b.booking_date, b.status, b.payment_status, b.total_price " +
                      "FROM bookings b JOIN users u ON b.user_id = u.user_id WHERE 1=1";

        List<Object> params = new ArrayList<>();

        if (!filterServiceType.getSelectedItem().equals("All")) {
            query += " AND b.service_type = ?";
            params.add(filterServiceType.getSelectedItem().toString().toLowerCase());
        }

        if (!filterStatus.getSelectedItem().equals("All")) {
            query += " AND b.status = ?";
            params.add(filterStatus.getSelectedItem().toString().toLowerCase());
        }

        if (!filterPaymentStatus.getSelectedItem().equals("All")) {
            query += " AND b.payment_status = ?";
            params.add(filterPaymentStatus.getSelectedItem().toString().toLowerCase());
        }

        return DatabaseManager.executeQuery(query, params.toArray());
    }

    private String getServiceDetails(String serviceType, int serviceId) throws SQLException {
        String query = "";
        switch (serviceType.toLowerCase()) {
            case "flight":
                query = "SELECT CONCAT(airline, ' (', origin, ' to ', destination, ')') AS details " +
                       "FROM flights WHERE flight_id = ?";
                break;
            case "hotel":
                query = "SELECT CONCAT(name, ' (', location, ')') AS details " +
                       "FROM hotels WHERE hotel_id = ?";
                break;
            case "transport":
                query = "SELECT CONCAT(type, ' by ', company, ' (', pickup_location, " +
                       "' to ', dropoff_location, ')') AS details FROM transport WHERE transport_id = ?";
                break;
            default:
                return "N/A";
        }

        List<Map<String, Object>> results = DatabaseManager.executeQuery(query, serviceId);
        return results.isEmpty() ? "N/A" : results.get(0).get("details").toString();
    }

    private void handleCancelBooking(ActionEvent e) {
        int selectedRow = bookingsTable.getSelectedRow();
        if (selectedRow == -1) {
            showError("Please select a booking to cancel");
            return;
        }

        int bookingId = (int) bookingsTable.getValueAt(selectedRow, 0);
        String currentStatus = bookingsTable.getValueAt(selectedRow, 5).toString();

        if (!currentStatus.equalsIgnoreCase("confirmed")) {
            showError("Only confirmed bookings can be cancelled");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
            "Are you sure you want to cancel this booking?",
            "Confirm Cancellation",
            JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                int result = DatabaseManager.executeUpdate(
                    "UPDATE bookings SET status = 'cancelled' WHERE booking_id = ?",
                    bookingId
                );

                if (result > 0) {
                    refreshBookingsTable();
                    JOptionPane.showMessageDialog(this, "Booking cancelled successfully");
                }
            } catch (SQLException ex) {
                showError("Error cancelling booking: " + ex.getMessage());
            }
        }
    }

    private void handleViewDetails(ActionEvent e) {
        int selectedRow = bookingsTable.getSelectedRow();
        if (selectedRow == -1) {
            showError("Please select a booking to view details");
            return;
        }

        String serviceType = bookingsTable.getValueAt(selectedRow, 2).toString();
        int serviceId = Integer.parseInt(bookingsTable.getValueAt(selectedRow, 3).toString());
        
        try {
            String details = getFullServiceDetails(serviceType, serviceId);
            JTextArea textArea = new JTextArea(details);
            textArea.setEditable(false);
            JOptionPane.showMessageDialog(this, new JScrollPane(textArea),
                "Service Details", JOptionPane.INFORMATION_MESSAGE);
        } catch (SQLException ex) {
            showError("Error loading service details: " + ex.getMessage());
        }
    }

    private String getFullServiceDetails(String serviceType, int serviceId) throws SQLException {
        String query = "";
        switch (serviceType.toLowerCase()) {
            case "flight":
                query = "SELECT * FROM flights WHERE flight_id = ?";
                break;
            case "hotel":
                query = "SELECT * FROM hotels WHERE hotel_id = ?";
                break;
            case "transport":
                query = "SELECT * FROM transport WHERE transport_id = ?";
                break;
            default:
                return "No details available";
        }

        List<Map<String, Object>> results = DatabaseManager.executeQuery(query, serviceId);
        if (results.isEmpty()) return "Details not found";

        Map<String, Object> service = results.get(0);
        StringBuilder details = new StringBuilder();
        
        switch (serviceType.toLowerCase()) {
            case "flight":
                details.append("Airline: ").append(service.get("airline")).append("\n")
                      .append("Route: ").append(service.get("origin")).append(" to ")
                      .append(service.get("destination")).append("\n")
                      .append("Departure: ").append(service.get("departure")).append("\n")
                      .append("Arrival: ").append(service.get("arrival")).append("\n")
                      .append("Price: $").append(service.get("price"));
                break;
            case "hotel":
                details.append("Hotel: ").append(service.get("name")).append("\n")
                      .append("Location: ").append(service.get("location")).append("\n")
                      .append("Price/Night: $").append(service.get("price_per_night")).append("\n")
                      .append("Amenities: ").append(service.get("amenities"));
                break;
            case "transport":
                details.append("Type: ").append(service.get("type")).append("\n")
                      .append("Company: ").append(service.get("company")).append("\n")
                      .append("Route: ").append(service.get("pickup_location")).append(" to ")
                      .append(service.get("dropoff_location")).append("\n")
                      .append("Departure: ").append(service.get("departure")).append("\n")
                      .append("Price: $").append(service.get("price"));
                break;
        }
        return details.toString();
    }

    // Helper methods
    private JButton createBackButton() {
        JButton btnBack = new JButton("← Back to Dashboard");
        btnBack.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        btnBack.setForeground(Color.WHITE);
        btnBack.setBackground(new Color(62, 133, 185));
        btnBack.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.WHITE, 1),
            BorderFactory.createEmptyBorder(5, 15, 5, 15)
        ));
        btnBack.addActionListener(e -> {
            new AdminDashboard().setVisible(true);
            dispose();
        });
        return btnBack;
    }

    private JButton createHoverButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setForeground(Color.WHITE);
        button.setBackground(new Color(62, 133, 185));
        button.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.WHITE, 1),
            BorderFactory.createEmptyBorder(10, 20, 10, 20)
        ));
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(12, 46, 97));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(62, 133, 185));
            }
        });
        return button;
    }

    private void styleFilterComboBox(JComboBox<String> combo) {
    combo.setPreferredSize(new Dimension(150, 30));
    combo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
    
    // Set white background and black text
    combo.setBackground(Color.WHITE);
    combo.setForeground(Color.BLACK);
    
    // Custom renderer for dropdown items
    combo.setRenderer(new DefaultListCellRenderer() {
        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value,
                int index, boolean isSelected, boolean cellHasFocus) {
            super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            setBackground(Color.WHITE);
            setForeground(Color.BLACK);
            return this;
        }
    });
    
    // Optional: Add border styling
    combo.setBorder(BorderFactory.createCompoundBorder(
        BorderFactory.createLineBorder(new Color(200, 200, 200)),
        BorderFactory.createEmptyBorder(5, 5, 5, 5)
    ));
}

    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    private static class BookingTableRenderer extends DefaultTableCellRenderer {
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
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new ViewBookingsUI().setVisible(true));
    }
}