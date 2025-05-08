package Travel_Booking;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import com.toedter.calendar.JDateChooser;
import java.math.BigDecimal;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;

public class ManageTransportUI extends JFrame {
    
    private JScrollPane scrollPane;
    private JTable transportTable;
    private JComboBox<String> typeSelector;

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

    public ManageTransportUI() {
        initializeUI();
    }

    private void initializeUI() {
        setTitle("Manage Transportation");
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

        JLabel titleLabel = new JLabel("Transport Management");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        headerPanel.add(titleLabel, BorderLayout.CENTER);

        mainPanel.add(headerPanel, BorderLayout.NORTH);

        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setOpaque(false);
        tabbedPane.setBackground(new Color(255, 255, 255, 50));
        tabbedPane.setForeground(Color.WHITE);
        
        tabbedPane.addTab("Transport List", createTransportListPanel());
        tabbedPane.addTab("Add New Transport", createAddTransportPanel());
        tabbedPane.addTab("Edit Transport", createEditTransportPanel());

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

    private JPanel createTransportListPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        String[] columns = {"ID", "Type", "Company", "Pickup", "Dropoff", "Departure", "Price", "Capacity"};
        DefaultTableModel model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        transportTable = new JTable(model);
        transportTable.setDefaultRenderer(Object.class, new TransportTableRenderer());
        transportTable.setRowHeight(30);
        transportTable.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        
        JTableHeader header = transportTable.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 14));
        header.setBackground(new Color(62, 133, 185));
        header.setForeground(Color.WHITE);
        
        scrollPane = new JScrollPane(transportTable);
        scrollPane.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(255, 255, 255, 80), 1),
            BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));

        JButton btnRefresh = createHoverButton("Refresh List");
        btnRefresh.addActionListener(e -> refreshTransportTable());

        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        controlPanel.setOpaque(false);
        controlPanel.add(btnRefresh);

        panel.add(controlPanel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);

        refreshTransportTable();
        return panel;
    }

    private JPanel createAddTransportPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.WEST;

        typeSelector = new JComboBox<>(new String[]{"Car", "Bus", "Train"});
        JTextField txtCompany = new JTextField(25);
        JTextField txtPickup = new JTextField(25);
        JTextField txtDropoff = new JTextField(25);
        JDateChooser dateDeparture = new JDateChooser();
        JTextField txtPrice = new JTextField(25);
        JTextField txtCapacity = new JTextField(25);

        styleDateChooser(dateDeparture);
        styleTextField(typeSelector);
        styleTextField(txtCompany);
        styleTextField(txtPickup);
        styleTextField(txtDropoff);
        styleTextField(txtPrice);
        styleTextField(txtCapacity);

        addFormRow(panel, gbc, 0, "Type*:", typeSelector);
        addFormRow(panel, gbc, 1, "Company*:", txtCompany);
        addFormRow(panel, gbc, 2, "Pickup Location*:", txtPickup);
        addFormRow(panel, gbc, 3, "Dropoff Location*:", txtDropoff);
        addFormRow(panel, gbc, 4, "Departure Time*:", dateDeparture);
        addFormRow(panel, gbc, 5, "Price*:", txtPrice);
        addFormRow(panel, gbc, 6, "Capacity*:", txtCapacity);

        JButton btnSubmit = createHoverButton("Add Transport");
        gbc.gridx = 1;
        gbc.gridy = 7;
        panel.add(btnSubmit, gbc);

        btnSubmit.addActionListener(e -> validateAndAddTransport(
            (String) typeSelector.getSelectedItem(),
            txtCompany,
            txtPickup,
            txtDropoff,
            dateDeparture,
            txtPrice,
            txtCapacity
        ));

        return panel;
    }

    private JPanel createEditTransportPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JComboBox<String> transportSelector = new JComboBox<>();
        transportSelector.setPreferredSize(new Dimension(250, 35));
        refreshTransportSelector(transportSelector);
        styleTextField(transportSelector);

        JPanel formPanel = createEditFormPanel(transportSelector);
        panel.add(formPanel, BorderLayout.CENTER);
        return panel;
    }

    // Helper Methods for ManageTransportUI
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

private void refreshTransportTable() {
    try {
        DefaultTableModel model = (DefaultTableModel) transportTable.getModel();
        model.setRowCount(0);
        
        List<Map<String, Object>> results = DatabaseManager.executeQuery(
            "SELECT * FROM transport ORDER BY departure DESC"
        );
        
        for (Map<String, Object> row : results) {
            Object departureObj = row.get("departure");
            String formattedDate = formatDepartureDate(departureObj);
            
            model.addRow(new Object[]{
                row.get("transport_id"),
                row.get("type"),
                row.get("company"),
                row.get("pickup_location"),
                row.get("dropoff_location"),
                formattedDate,
                String.format("$%.2f", row.get("price")),
                row.get("capacity")
            });
        }
    } catch (SQLException e) {
        showError("Error refreshing table: " + e.getMessage());
    }
}

private String formatDepartureDate(Object departureObj) {
    if (departureObj == null) {
        return "N/A";
    }
    
    try {
        if (departureObj instanceof java.sql.Timestamp) {
            return new SimpleDateFormat("yyyy-MM-dd HH:mm").format((Timestamp) departureObj);
        } else if (departureObj instanceof java.time.LocalDateTime) {
            return ((java.time.LocalDateTime) departureObj)
                    .format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
        } else if (departureObj instanceof java.util.Date) {
            return new SimpleDateFormat("yyyy-MM-dd HH:mm").format((java.util.Date) departureObj);
        }
        return departureObj.toString();
    } catch (Exception e) {
        return "Invalid date";
    }
}


private void validateAndAddTransport(String type, JTextField txtCompany,
                                    JTextField txtPickup, JTextField txtDropoff,
                                    JDateChooser dateDeparture, JTextField txtPrice,
                                    JTextField txtCapacity) {
    try {
        if (validateTransportFields(txtCompany, txtPickup, txtDropoff, dateDeparture, txtPrice, txtCapacity)) {
            Timestamp departure = new Timestamp(dateDeparture.getDate().getTime());
            
            int result = DatabaseManager.executeUpdate(
                "INSERT INTO transport (type, company, pickup_location, dropoff_location, "
                + "departure, price, capacity) VALUES (?, ?, ?, ?, ?, ?, ?)",
                type.toUpperCase(),
                txtCompany.getText().trim(),
                txtPickup.getText().trim(),
                txtDropoff.getText().trim(),
                departure,
                Double.parseDouble(txtPrice.getText()),
                Integer.parseInt(txtCapacity.getText())
            );

            if (result > 0) {
                JOptionPane.showMessageDialog(this, "Transport added successfully!");
                refreshTransportTable();
            }
        }
    } catch (SQLException | NumberFormatException e) {
        handleDatabaseError(e);
    }
}

private void handleDatabaseError(Exception e) {
    if (e instanceof SQLException) {
        SQLException sqlEx = (SQLException) e;
        if (sqlEx.getErrorCode() == 1062) {
            showError("Transport with these details already exists!");
        } else {
            showError("Database error: " + sqlEx.getMessage());
        }
    } else {
        showError("Input error: " + e.getMessage());
    }
}

private boolean validateTransportFields(JTextField txtCompany, JTextField txtPickup,
                                      JTextField txtDropoff, JDateChooser dateDeparture,
                                      JTextField txtPrice, JTextField txtCapacity) {
    // Check empty required fields
    if (txtCompany.getText().trim().isEmpty() ||
        txtPickup.getText().trim().isEmpty() ||
        txtDropoff.getText().trim().isEmpty() ||
        dateDeparture.getDate() == null ||
        txtPrice.getText().trim().isEmpty() ||
        txtCapacity.getText().trim().isEmpty()) {
        showError("All fields marked with * are required!");
        return false;
    }

    // Validate numerical values
    try {
        double price = Double.parseDouble(txtPrice.getText());
        int capacity = Integer.parseInt(txtCapacity.getText());
        
        if (price <= 0) {
            showError("Price must be positive!");
            return false;
        }
        
        if (capacity <= 0) {
            showError("Capacity must be positive!");
            return false;
        }
        
    } catch (NumberFormatException ex) {
        showError("Invalid number format in price or capacity!");
        return false;
    }

    return true;
}

private void refreshTransportSelector(JComboBox<String> selector) {
    try {
        List<Map<String, Object>> results = DatabaseManager.executeQuery(
            "SELECT transport_id, company FROM transport ORDER BY company ASC"
        );
        
        selector.removeAllItems();
        if (!results.isEmpty()) {
            for (Map<String, Object> row : results) {
                selector.addItem(row.get("transport_id") + " - " + row.get("company"));
            }
            selector.setEnabled(true);
        } else {
            selector.addItem("No transports available");
            selector.setEnabled(false);
        }
    } catch (SQLException e) {
        selector.addItem("Error loading transports");
        selector.setEnabled(false);
    }
}

private JPanel createEditFormPanel(JComboBox<String> transportSelector) {
    JPanel panel = new JPanel(new GridBagLayout());
    panel.setOpaque(false);
    GridBagConstraints gbc = new GridBagConstraints();
    gbc.insets = new Insets(10, 10, 10, 10);
    gbc.anchor = GridBagConstraints.WEST;

    JComboBox<String> typeSelector = new JComboBox<>(new String[]{"Car", "Bus", "Train"});
    JTextField txtCompany = new JTextField(25);
    JTextField txtPickup = new JTextField(25);
    JTextField txtDropoff = new JTextField(25);
    JDateChooser dateDeparture = new JDateChooser();
    JTextField txtPrice = new JTextField(25);
    JTextField txtCapacity = new JTextField(25);

    styleDateChooser(dateDeparture);
    styleTextField(typeSelector);
    styleTextField(txtCompany);
    styleTextField(txtPickup);
    styleTextField(txtDropoff);
    styleTextField(txtPrice);
    styleTextField(txtCapacity);

    addFormRow(panel, gbc, 0, "Transport:", transportSelector);
    addFormRow(panel, gbc, 1, "Type*:", typeSelector);
    addFormRow(panel, gbc, 2, "Company*:", txtCompany);
    addFormRow(panel, gbc, 3, "Pickup*:", txtPickup);
    addFormRow(panel, gbc, 4, "Dropoff*:", txtDropoff);
    addFormRow(panel, gbc, 5, "Departure*:", dateDeparture);
    addFormRow(panel, gbc, 6, "Price*:", txtPrice);
    addFormRow(panel, gbc, 7, "Capacity*:", txtCapacity);

    JButton btnUpdate = createHoverButton("Update Transport");
    gbc.gridx = 1;
    gbc.gridy = 8;
    panel.add(btnUpdate, gbc);

    btnUpdate.addActionListener(e -> {
        Object selected = transportSelector.getSelectedItem();
        if (selected != null && !selected.toString().startsWith("No transports")) {
            updateTransport(
                selected.toString(),
                (String) typeSelector.getSelectedItem(),
                txtCompany, txtPickup, txtDropoff,
                dateDeparture, txtPrice, txtCapacity,
                transportSelector
            );
        }
    });

    transportSelector.addActionListener(e -> {
        Object selected = transportSelector.getSelectedItem();
        if (selected != null && !selected.toString().startsWith("No transports")) {
            loadTransportDetails(
                selected.toString(),
                typeSelector, txtCompany, txtPickup,
                txtDropoff, dateDeparture, txtPrice, txtCapacity
            );
        }
    });

    return panel;
}

private void loadTransportDetails(String selectedTransport, JComboBox<String> typeSelector,
                                 JTextField txtCompany, JTextField txtPickup,
                                 JTextField txtDropoff, JDateChooser dateDeparture,
                                 JTextField txtPrice, JTextField txtCapacity) {
    try {
        int transportId = Integer.parseInt(selectedTransport.split(" - ")[0]);
        List<Map<String, Object>> results = DatabaseManager.executeQuery(
            "SELECT * FROM transport WHERE transport_id = ?", transportId
        );

        if (!results.isEmpty()) {
            Map<String, Object> transportData = results.get(0);
            
            // Handle different date types
            Object departureObj = transportData.get("departure");
            java.util.Date departureDate = null;
            
            if (departureObj != null) {
                if (departureObj instanceof java.time.LocalDateTime) {
                    // Convert LocalDateTime to java.util.Date
                    java.time.LocalDateTime ldt = (java.time.LocalDateTime) departureObj;
                    departureDate = java.util.Date.from(ldt.atZone(java.time.ZoneId.systemDefault()).toInstant());
                } else if (departureObj instanceof java.sql.Timestamp) {
                    departureDate = new java.util.Date(((java.sql.Timestamp) departureObj).getTime());
                } else if (departureObj instanceof java.util.Date) {
                    departureDate = (java.util.Date) departureObj;
                }
            }

            typeSelector.setSelectedItem(transportData.get("type").toString().toLowerCase());
            txtCompany.setText(transportData.get("company").toString());
            txtPickup.setText(transportData.get("pickup_location").toString());
            txtDropoff.setText(transportData.get("dropoff_location").toString());
            dateDeparture.setDate(departureDate);
            txtPrice.setText(String.format("%.2f", transportData.get("price")));
            txtCapacity.setText(transportData.get("capacity").toString());
        }
    } catch (SQLException | NumberFormatException ex) {
        showError("Error loading transport details: " + ex.getMessage());
    }
}

private void updateTransport(String selectedTransport, String type,
                            JTextField txtCompany, JTextField txtPickup,
                            JTextField txtDropoff, JDateChooser dateDeparture,
                            JTextField txtPrice, JTextField txtCapacity,
                            JComboBox<String> transportSelector) {
    try {
        if (!validateTransportFields(txtCompany, txtPickup, txtDropoff, dateDeparture, txtPrice, txtCapacity)) {
            return;
        }

        int transportId = Integer.parseInt(selectedTransport.split(" - ")[0]);
        Timestamp departure = new Timestamp(dateDeparture.getDate().getTime());
        
        int result = DatabaseManager.executeUpdate(
            "UPDATE transport SET type = ?, company = ?, pickup_location = ?, "
            + "dropoff_location = ?, departure = ?, price = ?, capacity = ? "
            + "WHERE transport_id = ?",
            type.toUpperCase(),
            txtCompany.getText().trim(),
            txtPickup.getText().trim(),
            txtDropoff.getText().trim(),
            departure,
            Double.parseDouble(txtPrice.getText()),
            Integer.parseInt(txtCapacity.getText()),
            transportId
        );

        if (result > 0) {
            JOptionPane.showMessageDialog(this, "Transport updated successfully!");
            refreshTransportTable();
            refreshTransportSelector(transportSelector);
        }
    } catch (SQLException | NumberFormatException ex) {
        handleDatabaseError(ex);
    }
}

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new ManageTransportUI().setVisible(true));
    }

    private static class TransportTableRenderer extends DefaultTableCellRenderer {
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
}