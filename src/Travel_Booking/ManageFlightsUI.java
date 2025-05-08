package Travel_Booking;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import com.toedter.calendar.JDateChooser;
import java.math.BigDecimal;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;

public class ManageFlightsUI extends JFrame {
    
    private JScrollPane scrollPane;
    private JTable flightTable;

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

    public ManageFlightsUI() {
        initializeUI();
    }

    private void initializeUI() {
        setTitle("Manage Flights");
        setSize(1200, 800);
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

        // Header Panel
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);
        
        JButton btnBack = createBackButton();
        headerPanel.add(btnBack, BorderLayout.WEST);

        JLabel titleLabel = new JLabel("Flight Management");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        headerPanel.add(titleLabel, BorderLayout.CENTER);

        mainPanel.add(headerPanel, BorderLayout.NORTH);

        // Content Panel
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setOpaque(false);
        tabbedPane.setBackground(new Color(255, 255, 255, 50));
        tabbedPane.setForeground(Color.WHITE);
        
        tabbedPane.addTab("Flight List", createFlightListPanel());
        tabbedPane.addTab("Add New Flight", createAddFlightPanel());
        tabbedPane.addTab("Edit Flight", createEditFlightPanel());

        mainPanel.add(tabbedPane, BorderLayout.CENTER);
        add(mainPanel);
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
            new AdminDashboard().setVisible(true);
            dispose();
        });

        return btnBack;
    }

    private JPanel createFlightListPanel() {
    JPanel panel = new JPanel(new BorderLayout());
    panel.setOpaque(false);
    panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

    // Table setup with proper DefaultTableModel
    String[] columns = {"Flight ID", "Airline", "Origin", "Destination", "Departure", "Arrival", "Price", "Seats"};
    DefaultTableModel model = new DefaultTableModel(columns, 0) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false; // Make table non-editable
        }
    };
    
    flightTable = new JTable(model);
    flightTable.setDefaultRenderer(Object.class, new StyledTableRenderer());
    flightTable.setFillsViewportHeight(true);
    flightTable.setRowHeight(30);
    flightTable.setFont(new Font("Segoe UI", Font.PLAIN, 14));
    flightTable.setForeground(Color.BLACK);
    flightTable.setBackground(new Color(255, 255, 255, 30));
    
    JTableHeader header = flightTable.getTableHeader();
    header.setFont(new Font("Segoe UI", Font.BOLD, 14));
    header.setBackground(new Color(62, 133, 185));
    header.setForeground(Color.WHITE);
    
    scrollPane = new JScrollPane(flightTable);
    scrollPane.getViewport().setBackground(new Color(255, 255, 255, 30));
    
    // Add consistent border
    scrollPane.setBorder(BorderFactory.createCompoundBorder(
        BorderFactory.createLineBorder(new Color(255, 255, 255, 80), 1),
        BorderFactory.createEmptyBorder(5, 5, 5, 5)
    ));

    JButton btnRefresh = createHoverButton("Refresh List");
    btnRefresh.addActionListener(e -> refreshFlightTable());

    JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
    controlPanel.setOpaque(false);
    controlPanel.add(btnRefresh);

    panel.add(controlPanel, BorderLayout.NORTH);
    panel.add(scrollPane, BorderLayout.CENTER);

    // Load initial data
    refreshFlightTable();
    return panel;
}

    private JPanel createAddFlightPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.WEST;

        JTextField txtAirline = new JTextField(20);
        JTextField txtOrigin = new JTextField(20);
        JTextField txtDestination = new JTextField(20);
        JDateChooser dateDeparture = new JDateChooser();
        JDateChooser dateArrival = new JDateChooser();
        JTextField txtPrice = new JTextField(20);
        JTextField txtSeats = new JTextField(20);

        styleDateChooser(dateDeparture);
        styleDateChooser(dateArrival);

        addFormRow(panel, gbc, 0, "Airline*:", txtAirline);
        addFormRow(panel, gbc, 1, "Origin*:", txtOrigin);
        addFormRow(panel, gbc, 2, "Destination*:", txtDestination);
        addFormRow(panel, gbc, 3, "Departure*:", dateDeparture);
        addFormRow(panel, gbc, 4, "Arrival*:", dateArrival);
        addFormRow(panel, gbc, 5, "Price (USD)*:", txtPrice);
        addFormRow(panel, gbc, 6, "Seats Available*:", txtSeats);

        JButton btnSubmit = createHoverButton("Add Flight");
        gbc.gridx = 1;
        gbc.gridy = 7;
        panel.add(btnSubmit, gbc);

        btnSubmit.addActionListener(e -> validateAndAddFlight(
            txtAirline, txtOrigin, txtDestination, 
            dateDeparture, dateArrival, txtPrice, txtSeats
        ));

        return panel;
    }

    private void styleDateChooser(JDateChooser dateChooser) {
        dateChooser.setPreferredSize(new Dimension(250, 35));
        dateChooser.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        dateChooser.getCalendarButton().setBackground(new Color(62, 133, 185));
        dateChooser.getCalendarButton().setForeground(Color.WHITE);
        dateChooser.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(255, 255, 255, 80), 1),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
    }
    
    private JPanel createEditFlightPanel() {
    JPanel panel = new JPanel(new BorderLayout());
    panel.setOpaque(false);
    panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

    // Create single combo box instance
    JComboBox<String> flightSelector = new JComboBox<>();
    flightSelector.setPreferredSize(new Dimension(250, 35));
    refreshFlightSelector(flightSelector);
    styleTextField(flightSelector);

    // Create form panel with the shared combo box
    JPanel formPanel = createEditFormPanel(flightSelector);

    panel.add(formPanel, BorderLayout.CENTER);
    return panel;
}

    private void addFormRow(JPanel panel, GridBagConstraints gbc, int row, String label, JComponent field) {
        gbc.gridx = 0;
        gbc.gridy = row;
        JLabel lbl = new JLabel(label);
        lbl.setForeground(new Color(62, 133, 185));
        panel.add(lbl, gbc);

        gbc.gridx = 1;
        styleTextField(field);
        panel.add(field, gbc);
    }

    private void styleTextField(JComponent field) {
        field.setPreferredSize(new Dimension(250, 35));
        field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(255, 255, 255, 80), 1),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
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
        button.setFocusPainted(false);
        button.setOpaque(true);
        button.setContentAreaFilled(true);

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

    

    private void showError(String message) {
        JOptionPane.showMessageDialog(this, 
            message, 
            "Validation Error", 
            JOptionPane.ERROR_MESSAGE);
    }

    
    
    private JPanel createEditFormPanel(JComboBox<String> flightSelector) {
    JPanel panel = new JPanel(new GridBagLayout());
    panel.setOpaque(false);
    GridBagConstraints gbc = new GridBagConstraints();
    gbc.insets = new Insets(10, 10, 10, 10);
    gbc.anchor = GridBagConstraints.WEST;

    // Add components using the shared flightSelector
    gbc.gridx = 0;
    gbc.gridy = 0;
    panel.add(new JLabel("Select Flight:"), gbc);
    
    gbc.gridx = 1;
    panel.add(flightSelector, gbc);

    JTextField txtAirline = new JTextField(20);
    JTextField txtOrigin = new JTextField(20);
    JTextField txtDestination = new JTextField(20);
    JDateChooser dateDeparture = new JDateChooser();
    JDateChooser dateArrival = new JDateChooser();
    JTextField txtPrice = new JTextField(20);
    JTextField txtSeats = new JTextField(20);
    JButton btnUpdate = createHoverButton("Update Flight");

    // Style components
    styleTextField(flightSelector);
    styleTextField(txtAirline);
    styleTextField(txtOrigin);
    styleTextField(txtDestination);
    styleDateChooser(dateDeparture);
    styleDateChooser(dateArrival);
    styleTextField(txtPrice);
    styleTextField(txtSeats);

    // Add components to panel
    gbc.gridx = 0;
    gbc.gridy = 0;
    panel.add(new JLabel("Select Flight:"), gbc);
    
    gbc.gridx = 1;
    panel.add(flightSelector, gbc);

    addFormRow(panel, gbc, 1, "Airline*:", txtAirline);
    addFormRow(panel, gbc, 2, "Origin*:", txtOrigin);
    addFormRow(panel, gbc, 3, "Destination*:", txtDestination);
    addFormRow(panel, gbc, 4, "Departure*:", dateDeparture);
    addFormRow(panel, gbc, 5, "Arrival*:", dateArrival);
    addFormRow(panel, gbc, 6, "Price (USD)*:", txtPrice);
    addFormRow(panel, gbc, 7, "Seats Available*:", txtSeats);

    gbc.gridx = 1;
    gbc.gridy = 8;
    panel.add(btnUpdate, gbc);

    // Add event listeners
    flightSelector.addActionListener(e -> loadFlightDetails(
        flightSelector.getSelectedItem().toString(),
        txtAirline, txtOrigin, txtDestination,
        dateDeparture, dateArrival, txtPrice, txtSeats
    ));

    btnUpdate.addActionListener(e -> {
        Object selected = flightSelector.getSelectedItem();
        if (selected != null && !selected.toString().contains("No flights")) {
            updateFlight(
                selected.toString(),
                txtAirline, txtOrigin, txtDestination,
                dateDeparture, dateArrival, txtPrice, txtSeats
            );
        } else {
            showError("Please select a valid flight!");
        }
    });

    return panel;
}

// Updated method with correct parameters and field usage
private void loadFlightDetails(String selectedFlight, 
                              JTextField txtAirline,
                              JTextField txtOrigin, 
                              JTextField txtDestination,
                              JDateChooser dateDeparture, 
                              JDateChooser dateArrival,
                              JTextField txtPrice, 
                              JTextField txtSeats) {
    
    try {
        int flightId = Integer.parseInt(selectedFlight.split(" - ")[0]);
        
        List<Map<String, Object>> results = DatabaseManager.executeQuery(
            "SELECT * FROM flights WHERE flight_id = ?",
            flightId
        );

        if (!results.isEmpty()) {
            Map<String, Object> flightData = results.get(0);
            
            // Handle date conversion properly
            Timestamp departureTs = convertToTimestamp(flightData.get("departure"));
            Timestamp arrivalTs = convertToTimestamp(flightData.get("arrival"));
            
            // Rest of the method remains the same
            txtAirline.setText((String) flightData.get("airline"));
            txtOrigin.setText((String) flightData.get("origin"));
            txtDestination.setText((String) flightData.get("destination"));
            dateDeparture.setDate(departureTs != null ? new Date(departureTs.getTime()) : null);
            dateArrival.setDate(arrivalTs != null ? new Date(arrivalTs.getTime()) : null);
            
            BigDecimal price = (BigDecimal) flightData.get("price");
            txtPrice.setText(String.format("%.2f", price));
            
            txtSeats.setText(flightData.get("seats_available").toString());
        }
    } catch (SQLException | NumberFormatException ex) {
        showError("Error loading flight details: " + ex.getMessage());
    }
}

private void updateFlight(String selectedFlight, 
                         JTextField txtAirline,
                         JTextField txtOrigin, 
                         JTextField txtDestination,
                         JDateChooser dateDeparture, 
                         JDateChooser dateArrival,
                         JTextField txtPrice, 
                         JTextField txtSeats) {
    try {
        // Add validation for placeholder text
        if (selectedFlight.startsWith("No flights") || selectedFlight.startsWith("Error")) {
            showError("Invalid flight selection");
            return;
        }
        
        int flightId = Integer.parseInt(selectedFlight.split(" - ")[0]);
        
        if (!validateFlightFields(txtAirline, txtOrigin, txtDestination,
            dateDeparture, dateArrival, txtPrice, txtSeats)) {
            return;
        }

        int result = DatabaseManager.executeUpdate(
            "UPDATE flights SET " +
            "airline = ?, origin = ?, destination = ?, " +
            "departure = ?, arrival = ?, price = ?, seats_available = ? " +
            "WHERE flight_id = ?",
            txtAirline.getText().trim(),
            txtOrigin.getText().trim(),
            txtDestination.getText().trim(),
            new Timestamp(dateDeparture.getDate().getTime()),
            new Timestamp(dateArrival.getDate().getTime()),
            Double.parseDouble(txtPrice.getText()),
            Integer.parseInt(txtSeats.getText()),
            flightId
        );

        if (result > 0) {
            JOptionPane.showMessageDialog(this, "Flight updated successfully!");
            refreshFlightTable(); // Call the parameter-less refresh
        }
    } catch (SQLException ex) {
        handleDatabaseError(ex);
    } catch (NumberFormatException ex) {
        showError("Invalid number format in price or seats");
    }
}



    private void validateAndAddFlight(JTextField txtAirline, 
                                     JTextField txtOrigin,
                                     JTextField txtDestination,
                                     JDateChooser dateDeparture,
                                     JDateChooser dateArrival,
                                     JTextField txtPrice,
                                     JTextField txtSeats) {
        
        try {
            // Validate input
            if (!validateFlightFields(txtAirline, txtOrigin, txtDestination, 
                dateDeparture, dateArrival, txtPrice, txtSeats)) {
                return;
            }

            // Insert into database
            int result = DatabaseManager.executeUpdate(
                "INSERT INTO flights (airline, origin, destination, departure, arrival, price, seats_available) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)",
                txtAirline.getText().trim(),
                txtOrigin.getText().trim(),
                txtDestination.getText().trim(),
                new Timestamp(dateDeparture.getDate().getTime()),
                new Timestamp(dateArrival.getDate().getTime()),
                Double.parseDouble(txtPrice.getText()),
                Integer.parseInt(txtSeats.getText())
            );

            if (result > 0) {
                JOptionPane.showMessageDialog(this, "Flight added successfully!");
                clearFormFields(txtAirline, txtOrigin, txtDestination, 
                    dateDeparture, dateArrival, txtPrice, txtSeats);
            }
        } catch (SQLException e) {
            handleDatabaseError(e);
        } catch (NumberFormatException e) {
            showError("Invalid number format in price or seats");
        }
    }

    private boolean validateFlightFields(JTextField txtAirline, 
                                        JTextField txtOrigin,
                                        JTextField txtDestination,
                                        JDateChooser dateDeparture,
                                        JDateChooser dateArrival,
                                        JTextField txtPrice,
                                        JTextField txtSeats) {
        // Check empty text fields
        if (txtAirline.getText().trim().isEmpty() ||
            txtOrigin.getText().trim().isEmpty() ||
            txtDestination.getText().trim().isEmpty() ||
            txtPrice.getText().trim().isEmpty() ||
            txtSeats.getText().trim().isEmpty()) {
            showError("All fields marked with * are required!");
            return false;
        }

        // Check dates
        if (dateDeparture.getDate() == null || dateArrival.getDate() == null) {
            showError("Departure and Arrival dates are required!");
            return false;
        }

        // Validate numerical values
        try {
            Double.parseDouble(txtPrice.getText());
            Integer.parseInt(txtSeats.getText());
        } catch (NumberFormatException ex) {
            showError("Price must be a number and Seats must be an integer!");
            return false;
        }

        return true;
    }
    
    private void refreshFlightSelector(JComboBox<String> selector) {
    try {
        String[] flights = getFlightNumbersFromDB();
        if (flights.length > 0) {
            selector.setModel(new DefaultComboBoxModel<>(flights));
            selector.setSelectedIndex(0);
            selector.setEnabled(true);
        } else {
            selector.setModel(new DefaultComboBoxModel<>(new String[]{"No flights available"}));
            selector.setEnabled(false);
        }
    } catch (Exception e) {
        selector.setModel(new DefaultComboBoxModel<>(new String[]{"Error loading flights"}));
        selector.setEnabled(false);
    }
}

    

    private void refreshFlightTable() {
    try {
        DefaultTableModel model = (DefaultTableModel) flightTable.getModel();
        int firstVisibleRow = -1;

        // Only try to get scroll position if scrollPane exists
        if (scrollPane != null) {
            // Get visible rectangle and convert coordinates
            Point viewPoint = scrollPane.getViewport().getViewPosition();
            Point tablePoint = SwingUtilities.convertPoint(
                scrollPane.getViewport(), 
                viewPoint, 
                flightTable
            );
            firstVisibleRow = flightTable.rowAtPoint(tablePoint);
        }

        model.setRowCount(0);  // Clear existing data
        
        List<Map<String, Object>> results = DatabaseManager.executeQuery(
            "SELECT * FROM flights ORDER BY departure DESC"
        );
        
        // Convert results to array
        Object[][] data = convertResultsToArray(results);
        for (Object[] row : data) {
            model.addRow(row);
        }
        
        // Restore scroll position if valid
        if (firstVisibleRow >= 0 && firstVisibleRow < flightTable.getRowCount()) {
            Rectangle rect = flightTable.getCellRect(firstVisibleRow, 0, true);
            flightTable.scrollRectToVisible(rect);
        }
    } catch (SQLException e) {
        showError("Error refreshing table: " + e.getMessage());
    }
}

// Add this conversion method
private Object[][] convertResultsToArray(List<Map<String, Object>> results) {
    return results.stream()
        .map(row -> new Object[]{
            row.get("flight_id"),
            row.get("airline"),
            row.get("origin"),
            row.get("destination"),
            formatTimestamp(convertToTimestamp(row.get("departure"))),
            formatTimestamp(convertToTimestamp(row.get("arrival"))),
            formatPrice((BigDecimal) row.get("price")),
            row.get("seats_available")
        })
        .toArray(Object[][]::new);
}

// Add this helper method
private Timestamp convertToTimestamp(Object dateObj) {
    if (dateObj == null) return null;
    if (dateObj instanceof LocalDateTime) {
        return Timestamp.valueOf((LocalDateTime) dateObj);
    }
    return (Timestamp) dateObj;
}

// Add these helper methods
private String formatTimestamp(Timestamp ts) {
    return ts != null ? new SimpleDateFormat("yyyy-MM-dd HH:mm").format(ts) : "";
}

private String formatPrice(BigDecimal price) {
    return price != null ? String.format("$%.2f", price.doubleValue()) : "";
}

    private void handleDatabaseError(SQLException e) {
        if (e.getErrorCode() == 1062) { // Duplicate entry
            showError("Flight with these details already exists!");
        } else {
            showError("Database error: " + e.getMessage());
        }
    }

    private void clearFormFields(JTextField txtAirline, 
                                JTextField txtOrigin,
                                JTextField txtDestination,
                                JDateChooser dateDeparture,
                                JDateChooser dateArrival,
                                JTextField txtPrice,
                                JTextField txtSeats) {
        txtAirline.setText("");
        txtOrigin.setText("");
        txtDestination.setText("");
        dateDeparture.setDate(null);
        dateArrival.setDate(null);
        txtPrice.setText("");
        txtSeats.setText("");
    }
    
    // Add to your DateUtils class or directly in the ManageFlightsUI
    public static Date toDate(Timestamp timestamp) {
        return timestamp != null ? new Date(timestamp.getTime()) : null;
    }
    
    private static class StyledTableRenderer extends DefaultTableCellRenderer {
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
        SwingUtilities.invokeLater(() -> new ManageFlightsUI().setVisible(true));
    }

  

    private String[] getFlightNumbersFromDB() {
        try {
            List<Map<String, Object>> results = DatabaseManager.executeQuery(
                "SELECT flight_id, CONCAT(airline, ' - ', origin, ' to ', destination) AS flight_info " +
                "FROM flights ORDER BY departure DESC"
            );

            return results.stream()
                .map(row -> row.get("flight_id") + " - " + row.get("flight_info"))
                .toArray(String[]::new);
        } catch (SQLException e) {
            showError("Database error: " + e.getMessage());
            return new String[]{"Error loading flights"};
        }
    }

    
}