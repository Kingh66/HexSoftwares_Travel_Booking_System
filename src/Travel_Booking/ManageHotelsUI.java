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
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;

public class ManageHotelsUI extends JFrame {
    
    private JScrollPane scrollPane;
    private JTable hotelTable;
    private JList<String> amenitiesList;

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

    public ManageHotelsUI() {
        initializeUI();
    }

    private void initializeUI() {
        setTitle("Manage Hotels");
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

        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);
        
        JButton btnBack = createBackButton();
        headerPanel.add(btnBack, BorderLayout.WEST);

        JLabel titleLabel = new JLabel("Hotel Management");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        headerPanel.add(titleLabel, BorderLayout.CENTER);

        mainPanel.add(headerPanel, BorderLayout.NORTH);

        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setOpaque(false);
        tabbedPane.setBackground(new Color(255, 255, 255, 50));
        tabbedPane.setForeground(Color.WHITE);
        
        tabbedPane.addTab("Hotel List", createHotelListPanel());
        tabbedPane.addTab("Add New Hotel", createAddHotelPanel());
        tabbedPane.addTab("Edit Hotel", createEditHotelPanel());

        mainPanel.add(tabbedPane, BorderLayout.CENTER);
        add(mainPanel);
    }
    
    private JButton createBackButton() {
        JButton btnBack = new JButton("← Back to Dashboard");
        btnBack.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        btnBack.setForeground(Color.WHITE);
        btnBack.setBackground(new Color(62, 133, 185));
        btnBack.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.WHITE, 1),
            BorderFactory.createEmptyBorder(5, 15, 5, 15)
        ));
        btnBack.setOpaque(true);
        btnBack.setContentAreaFilled(true);
        btnBack.setFocusPainted(false);

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
    

    private JPanel createHotelListPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        String[] columns = {"ID", "Name", "Location", "Price/Night", "Rooms", "Rating", "Amenities"};
        DefaultTableModel model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        hotelTable = new JTable(model);
        hotelTable.setDefaultRenderer(Object.class, new StyledTableRenderer());
        hotelTable.setRowHeight(30);
        hotelTable.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        
        JTableHeader header = hotelTable.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 14));
        header.setBackground(new Color(62, 133, 185));
        header.setForeground(Color.WHITE);
        
        scrollPane = new JScrollPane(hotelTable);
        scrollPane.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(255, 255, 255, 80), 1),
            BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));

        JButton btnRefresh = createHoverButton("Refresh List");
        btnRefresh.addActionListener(e -> refreshHotelTable());

        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        controlPanel.setOpaque(false);
        controlPanel.add(btnRefresh);

        panel.add(controlPanel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);

        refreshHotelTable();
        return panel;
    }

    private JPanel createAddHotelPanel() {
    JPanel panel = new JPanel(new GridBagLayout());
    panel.setOpaque(false);
    panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
    GridBagConstraints gbc = new GridBagConstraints();
    gbc.insets = new Insets(10, 10, 10, 10);
    gbc.anchor = GridBagConstraints.WEST;

    // Text fields with consistent sizing
    JTextField txtName = new JTextField(25);
    JTextField txtLocation = new JTextField(25);
    JTextField txtPrice = new JTextField(25);
    JTextField txtRooms = new JTextField(25);
    JTextField txtRating = new JTextField(25);
    
    // Enhanced description text area
    JTextArea txtDescription = new JTextArea(5, 30);
    txtDescription.setLineWrap(true);
    txtDescription.setWrapStyleWord(true);
    JScrollPane descriptionScroll = new JScrollPane(txtDescription);
    descriptionScroll.setPreferredSize(new Dimension(300, 120));

    // Improved amenities panel
    JPanel amenitiesPanel = new JPanel(new GridLayout(0, 2, 10, 10));
    amenitiesPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
    amenitiesPanel.setPreferredSize(new Dimension(300, 120));
    amenitiesPanel.setBackground(new Color(255, 255, 255, 150));
    amenitiesPanel.setOpaque(true);
    
    String[] amenities = {"wifi", "pool", "spa", "restaurant", "parking"};
    List<JCheckBox> amenityCheckboxes = new ArrayList<>();
    
    for (String amenity : amenities) {
        JCheckBox cb = new JCheckBox(amenity.substring(0, 1).toUpperCase() + amenity.substring(1));
        cb.setOpaque(false);
        cb.setForeground(Color.BLACK);
        cb.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        amenityCheckboxes.add(cb);
        amenitiesPanel.add(cb);
    }

    // Add components with improved layout
    addFormRow(panel, gbc, 0, "Name*:", txtName);
    addFormRow(panel, gbc, 1, "Location*:", txtLocation);
    addFormRow(panel, gbc, 2, "Price/Night*:", txtPrice);
    addFormRow(panel, gbc, 3, "Rooms Available*:", txtRooms);
    addFormRow(panel, gbc, 4, "Rating (0-5):", txtRating);
    
    // Amenities row with better spacing
    gbc.gridx = 0;
    gbc.gridy = 5;
    JLabel amenitiesLabel = new JLabel("Amenities:");
    amenitiesLabel.setForeground(new Color(62, 133, 185));
    panel.add(amenitiesLabel, gbc);
    
    gbc.gridx = 1;
    gbc.fill = GridBagConstraints.HORIZONTAL;
    panel.add(amenitiesPanel, gbc);
    
    // Description row
    gbc.gridx = 0;
    gbc.gridy = 6;
    gbc.fill = GridBagConstraints.NONE;
    JLabel descLabel = new JLabel("Description:");
    descLabel.setForeground(new Color(62, 133, 185));
    panel.add(descLabel, gbc);
    
    gbc.gridx = 1;
    panel.add(descriptionScroll, gbc);

    // Submit button
    JButton btnSubmit = createHoverButton("Add Hotel");
    gbc.gridx = 1;
    gbc.gridy = 7;
    gbc.anchor = GridBagConstraints.EAST;
    panel.add(btnSubmit, gbc);

    btnSubmit.addActionListener(e -> validateAndAddHotel(
        txtName, txtLocation, txtPrice, 
        txtRooms, txtRating, txtDescription,
        amenityCheckboxes
    ));

    return panel;
}
    

    private JPanel createEditHotelPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JComboBox<String> hotelSelector = new JComboBox<>();
        hotelSelector.setPreferredSize(new Dimension(250, 35));
        refreshHotelSelector(hotelSelector);
        styleTextField(hotelSelector);

        JPanel formPanel = createEditFormPanel(hotelSelector);
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
    
    private void refreshHotelTable() {
        try {
            DefaultTableModel model = (DefaultTableModel) hotelTable.getModel();
            model.setRowCount(0);
            
            List<Map<String, Object>> results = DatabaseManager.executeQuery(
                "SELECT * FROM hotels ORDER BY name ASC"
            );
            
            for (Map<String, Object> row : results) {
                model.addRow(new Object[]{
                    row.get("hotel_id"),
                    row.get("name"),
                    row.get("location"),
                    formatPrice((BigDecimal) row.get("price_per_night")),
                    row.get("rooms_available"),
                    row.get("rating"),
                    row.get("amenities")
                });
            }
        } catch (SQLException e) {
            showError("Error refreshing table: " + e.getMessage());
        }
    }
    
    private String formatPrice(BigDecimal price) {
    if (price == null) return "$0.00";
    return String.format("$%.2f", price.doubleValue());
}

    private void validateAndAddHotel(JTextField txtName, JTextField txtLocation,
                                JTextField txtPrice, JTextField txtRooms,
                                JTextField txtRating, JTextArea txtDescription,
                                List<JCheckBox> amenityCheckboxes) {
    try {
        if (validateHotelFields(txtName, txtLocation, txtPrice, txtRooms, txtRating)) {
            Set<String> amenities = new HashSet<>();
            for (JCheckBox cb : amenityCheckboxes) {
                if (cb.isSelected()) {
                    // Convert to lowercase before adding
                    amenities.add(cb.getText().toLowerCase());
                }
            }
            
            int result = DatabaseManager.executeUpdate(
                "INSERT INTO hotels (name, location, price_per_night, rooms_available, "
                + "rating, description, amenities) VALUES (?, ?, ?, ?, ?, ?, ?)",
                txtName.getText().trim(),
                txtLocation.getText().trim(),
                Double.parseDouble(txtPrice.getText()),
                Integer.parseInt(txtRooms.getText()),
                Double.parseDouble(txtRating.getText()),
                txtDescription.getText(),
                String.join(",", amenities)
            );

            if (result > 0) {
                JOptionPane.showMessageDialog(this, "Hotel added successfully!");
                refreshHotelTable();
            }
        }
    } catch (SQLException | NumberFormatException e) {
        handleDatabaseError((SQLException) e);
    }
}
    
    private void handleDatabaseError(SQLException e) {
    if (e.getErrorCode() == 1062) { // Duplicate entry
        showError("Hotel with these details already exists!");
    } else {
        showError("Database error: " + e.getMessage());
    }
}
    
    private boolean validateHotelFields(JTextField txtName, JTextField txtLocation, 
                                   JTextField txtPrice, JTextField txtRooms,
                                   JTextField txtRating) {
    // Check empty required fields
    if (txtName.getText().trim().isEmpty() ||
        txtLocation.getText().trim().isEmpty() ||
        txtPrice.getText().trim().isEmpty() ||
        txtRooms.getText().trim().isEmpty()) {
        showError("Fields marked with * are required!");
        return false;
    }

    // Validate numerical values
    try {
        double price = Double.parseDouble(txtPrice.getText());
        int rooms = Integer.parseInt(txtRooms.getText());
        double rating = txtRating.getText().isEmpty() ? 0 : Double.parseDouble(txtRating.getText());
        
        if (price <= 0) {
            showError("Price per night must be positive!");
            return false;
        }
        
        if (rooms < 0) {
            showError("Rooms available cannot be negative!");
            return false;
        }
        
        if (rating < 0 || rating > 5) {
            showError("Rating must be between 0 and 5!");
            return false;
        }
        
    } catch (NumberFormatException ex) {
        showError("Invalid number format in price, rooms, or rating!");
        return false;
    }

    return true;
}

private void refreshHotelSelector(JComboBox<String> selector) {
    try {
        List<Map<String, Object>> results = DatabaseManager.executeQuery(
            "SELECT hotel_id, name FROM hotels ORDER BY name ASC"
        );
        
        if (!results.isEmpty()) {
            String[] hotels = results.stream()
                .map(row -> row.get("hotel_id") + " - " + row.get("name"))
                .toArray(String[]::new);
            
            selector.setModel(new DefaultComboBoxModel<>(hotels));
            selector.setSelectedIndex(0);
            selector.setEnabled(true);
        } else {
            selector.setModel(new DefaultComboBoxModel<>(new String[]{"No hotels available"}));
            selector.setEnabled(false);
        }
    } catch (SQLException e) {
        selector.setModel(new DefaultComboBoxModel<>(new String[]{"Error loading hotels"}));
        selector.setEnabled(false);
    }
}

private JPanel createEditFormPanel(JComboBox<String> hotelSelector) {
    JPanel panel = new JPanel(new GridBagLayout());
    panel.setOpaque(false);
    GridBagConstraints gbc = new GridBagConstraints();
    gbc.insets = new Insets(12, 12, 12, 12);  // Increased spacing
    gbc.anchor = GridBagConstraints.WEST;

    // Form components with larger sizing
    JTextField txtName = new JTextField(30);  // Increased column count
    JTextField txtLocation = new JTextField(30);
    JTextField txtPrice = new JTextField(30);
    JTextField txtRooms = new JTextField(30);
    JTextField txtRating = new JTextField(30);
    
    // Enhanced description text area
    JTextArea txtDescription = new JTextArea(6, 40);  // Increased rows/columns
    txtDescription.setLineWrap(true);
    txtDescription.setWrapStyleWord(true);
    JScrollPane descriptionScroll = new JScrollPane(txtDescription);
    descriptionScroll.setPreferredSize(new Dimension(400, 150));  // Larger size

    // Improved amenities panel
    JPanel amenitiesPanel = new JPanel(new GridLayout(0, 2, 12, 12));  // Increased spacing
    amenitiesPanel.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
    amenitiesPanel.setPreferredSize(new Dimension(400, 150));  // Larger size
    amenitiesPanel.setBackground(new Color(255, 255, 255, 150));
    amenitiesPanel.setOpaque(true);

    // Common field styling
    Font fieldFont = new Font("Segoe UI", Font.PLAIN, 14);
    Dimension fieldSize = new Dimension(350, 35);  // Consistent field size
    
    styleTextField(txtName, fieldFont, fieldSize);
    styleTextField(txtLocation, fieldFont, fieldSize);
    styleTextField(txtPrice, fieldFont, fieldSize);
    styleTextField(txtRooms, fieldFont, fieldSize);
    styleTextField(txtRating, fieldFont, fieldSize);
    
    String[] amenities = {"WiFi", "Pool", "Spa", "Restaurant", "Parking"};
    List<JCheckBox> amenityCheckboxes = new ArrayList<>();
    
    for (String amenity : amenities) {
        JCheckBox cb = new JCheckBox(amenity);
        cb.setOpaque(false);
        cb.setForeground(Color.BLACK);
        cb.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        amenityCheckboxes.add(cb);
        amenitiesPanel.add(cb);
    }

    // Add components with improved layout
     addFormRow(panel, gbc, 0, "Hotel:", hotelSelector);
    addFormRow(panel, gbc, 1, "Name*:", txtName);
    addFormRow(panel, gbc, 2, "Location*:", txtLocation);
    addFormRow(panel, gbc, 3, "Price/Night*:", txtPrice);
    addFormRow(panel, gbc, 4, "Rooms*:", txtRooms);
    addFormRow(panel, gbc, 5, "Rating:", txtRating);
    
    // Amenities row
    gbc.gridx = 0;
    gbc.gridy = 6;
    gbc.fill = GridBagConstraints.NONE;
    panel.add(createFormLabel("Amenities:"), gbc);
    
    gbc.gridx = 1;
    gbc.fill = GridBagConstraints.HORIZONTAL;
    panel.add(amenitiesPanel, gbc);
    
    // Description row
    gbc.gridx = 0;
    gbc.gridy = 7;
    gbc.fill = GridBagConstraints.NONE;
    panel.add(createFormLabel("Description:"), gbc);
    
    gbc.gridx = 1;
    gbc.fill = GridBagConstraints.HORIZONTAL;
    panel.add(descriptionScroll, gbc);

    // Update button
    JButton btnUpdate = createHoverButton("Update Hotel");
    gbc.gridx = 1;
    gbc.gridy = 8;
    gbc.anchor = GridBagConstraints.EAST;
    panel.add(btnUpdate, gbc);

    // Add action listener to call updateHotel
    btnUpdate.addActionListener(e -> {
        Object selected = hotelSelector.getSelectedItem();
        if (selected != null && !selected.toString().startsWith("No hotels")) {
            updateHotel(
                selected.toString(),
                txtName, txtLocation, txtPrice,
                txtRooms, txtRating, txtDescription,
                amenityCheckboxes,
                hotelSelector  // Pass the selector for refresh
            );
        }
    });
    
    hotelSelector.addActionListener(e -> {
        Object selected = hotelSelector.getSelectedItem();
        if (selected != null && !selected.toString().startsWith("No hotels")) {
            loadHotelDetails(selected.toString(), txtName, txtLocation, txtPrice,
                txtRooms, txtRating, txtDescription, amenityCheckboxes);
        }
    });


    return panel;
}

private void styleTextField(JComponent field, Font font, Dimension size) {
    field.setFont(font);
    field.setPreferredSize(size);
    field.setMinimumSize(size);
    field.setBorder(BorderFactory.createCompoundBorder(
    BorderFactory.createLineBorder(new Color(200, 200, 200), 1),  // outer border
    BorderFactory.createEmptyBorder(8, 10, 8, 10)                 // inner padding
));

}

private JLabel createFormLabel(String text) {
    JLabel label = new JLabel(text);
    label.setFont(new Font("Segoe UI", Font.BOLD, 14));
    label.setForeground(new Color(62, 133, 185));
    return label;
}

// Helper method to load hotel details
private void loadHotelDetails(String selectedHotel, JTextField txtName,
                             JTextField txtLocation, JTextField txtPrice,
                             JTextField txtRooms, JTextField txtRating,
                             JTextArea txtDescription, List<JCheckBox> amenityCheckboxes) {
    try {
        int hotelId = Integer.parseInt(selectedHotel.split(" - ")[0]);
        List<Map<String, Object>> results = DatabaseManager.executeQuery(
            "SELECT * FROM hotels WHERE hotel_id = ?", hotelId
        );

        if (!results.isEmpty()) {
            Map<String, Object> hotelData = results.get(0);
            txtName.setText((String) hotelData.get("name"));
            txtLocation.setText((String) hotelData.get("location"));
            txtPrice.setText(String.valueOf(hotelData.get("price_per_night")));
            txtRooms.setText(String.valueOf(hotelData.get("rooms_available")));
            txtRating.setText(String.valueOf(hotelData.get("rating")));
            txtDescription.setText((String) hotelData.get("description"));
            
            // Set amenities
            String amenities = (String) hotelData.get("amenities");
            Set<String> selectedAmenities = new HashSet<>();
            if (amenities != null && !amenities.isEmpty()) {
                selectedAmenities = new HashSet<>(Arrays.asList(amenities.split(",")));
            }
            
            for (JCheckBox cb : amenityCheckboxes) {
                cb.setSelected(selectedAmenities.contains(cb.getText()));
            }
        }
    } catch (SQLException | NumberFormatException ex) {
        showError("Error loading hotel details: " + ex.getMessage());
    }
}

// Update hotel method
private void updateHotel(String selectedHotel, JTextField txtName,
                        JTextField txtLocation, JTextField txtPrice,
                        JTextField txtRooms, JTextField txtRating,
                        JTextArea txtDescription, List<JCheckBox> amenityCheckboxes,
                        JComboBox<String> hotelSelector) {
    try {
        if (!validateHotelFields(txtName, txtLocation, txtPrice, txtRooms, txtRating)) {
            return;
        }

        if (!selectedHotel.contains(" - ")) {
            showError("Invalid hotel selection");
            return;
        }
        
        int hotelId = Integer.parseInt(selectedHotel.split(" - ")[0].trim());
        
        Set<String> amenities = new HashSet<>();
        for (JCheckBox cb : amenityCheckboxes) {
            if (cb.isSelected()) {
                amenities.add(cb.getText());
            }
        }

        int result = DatabaseManager.executeUpdate(
            "UPDATE hotels SET name = ?, location = ?, price_per_night = ?, "
            + "rooms_available = ?, rating = ?, description = ?, amenities = ? "
            + "WHERE hotel_id = ?",
            txtName.getText().trim(),
            txtLocation.getText().trim(),
            Double.parseDouble(txtPrice.getText()),
            Integer.parseInt(txtRooms.getText()),
            Double.parseDouble(txtRating.getText()),
            txtDescription.getText(),
            amenities.isEmpty() ? "" : String.join(",", amenities), // Handle empty amenities
            hotelId
        );

        if (result > 0) {
            JOptionPane.showMessageDialog(this, 
                "Hotel updated successfully!",
                "Success",
                JOptionPane.INFORMATION_MESSAGE);
            refreshHotelTable();
            refreshHotelSelector(hotelSelector);
        } else {
            showError("No changes made or hotel not found");
        }
        
    } catch (NumberFormatException ex) {
        showError("Invalid number format: " + ex.getMessage());
    } catch (SQLException ex) {
        handleDatabaseError(ex);
    }
}



// Inner class for table rendering
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
        SwingUtilities.invokeLater(() -> new ManageHotelsUI().setVisible(true));
    }


    
}