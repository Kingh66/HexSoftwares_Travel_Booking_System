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
import com.toedter.calendar.JDateChooser;
import java.io.File;
import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.layout.element.Text;
import java.io.IOException;



public class GenerateReportsUI extends JFrame {
    
    private JTable reportsTable;
    private JComboBox<Report.ReportType> reportTypeSelector;
    private JDateChooser startDateChooser;
    private JDateChooser endDateChooser;

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

    public GenerateReportsUI() {
        initializeUI();
    }

    private void initializeUI() {
        setTitle("Generate Reports");
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
        refreshReportsTable();
    }

    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);
        
        JButton btnBack = createBackButton();
        headerPanel.add(btnBack, BorderLayout.WEST);

        JLabel titleLabel = new JLabel("Report Generation");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        headerPanel.add(titleLabel, BorderLayout.CENTER);

        return headerPanel;
    }

    private JPanel createMainContentPanel() {
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setOpaque(false);
        tabbedPane.setBackground(new Color(255, 255, 255, 50));
        tabbedPane.setForeground(Color.WHITE);
        
        tabbedPane.addTab("Generate Report", createGenerationPanel());
        tabbedPane.addTab("Report History", createHistoryPanel());

        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.setOpaque(false);
        contentPanel.add(tabbedPane, BorderLayout.CENTER);
        return contentPanel;
    }

    private JPanel createGenerationPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.WEST;

        reportTypeSelector = new JComboBox<>(Report.ReportType.values());
        startDateChooser = new JDateChooser();
        endDateChooser = new JDateChooser();
        
        styleDateChooser(startDateChooser);
        styleDateChooser(endDateChooser);
        styleComboBox(reportTypeSelector);

        addFormRow(panel, gbc, 0, "Report Type:", reportTypeSelector);
        addFormRow(panel, gbc, 1, "Start Date:", startDateChooser);
        addFormRow(panel, gbc, 2, "End Date:", endDateChooser);

        JButton btnGenerate = createHoverButton("Generate Report");
        btnGenerate.addActionListener(this::handleGenerateReport);
        
        gbc.gridx = 1;
        gbc.gridy = 3;
        panel.add(btnGenerate, gbc);

        return panel;
    }

    private JPanel createHistoryPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        String[] columns = {"Report ID", "Type", "Generated Date", "Parameters", "File Path"};
        DefaultTableModel model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        reportsTable = new JTable(model);
        reportsTable.setDefaultRenderer(Object.class, new ReportTableRenderer());
        reportsTable.setRowHeight(35);
        reportsTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        reportsTable.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        JScrollPane scrollPane = new JScrollPane(reportsTable);
        scrollPane.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(255, 255, 255, 80), 1),
            BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));

        JButton btnRefresh = createHoverButton("Refresh");
        btnRefresh.addActionListener(e -> refreshReportsTable());

        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        headerPanel.setOpaque(false);
        headerPanel.add(btnRefresh);

        panel.add(headerPanel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private void handleGenerateReport(ActionEvent e) {
        Report.ReportType type = (Report.ReportType) reportTypeSelector.getSelectedItem();
        java.util.Date startDate = startDateChooser.getDate();
        java.util.Date endDate = endDateChooser.getDate();

        if (!validateReportParameters(type, startDate, endDate)) {
            return;
        }

        Map<String, String> parameters = new HashMap<>();
        parameters.put("startDate", startDate != null ? new SimpleDateFormat("yyyy-MM-dd").format(startDate) : "");
        parameters.put("endDate", endDate != null ? new SimpleDateFormat("yyyy-MM-dd").format(endDate) : "");
        try {
            String filePath = generateReportFile(type, parameters);
            
            int result = DatabaseManager.executeUpdate(
                "INSERT INTO reports (admin_id, report_type, parameters, file_path) VALUES (?, ?, ?, ?)",
                3, // Replace with actual admin ID
                type.name(),
                mapToString(parameters),
                filePath
            );

            if (result > 0) {
                JOptionPane.showMessageDialog(this, "Report generated successfully!");
                refreshReportsTable();
            }
        } catch (SQLException ex) {
            showError("Error saving report: " + ex.getMessage());
        }
    }

    private boolean validateReportParameters(Report.ReportType type, java.util.Date startDate, java.util.Date endDate) {
        if (type == Report.ReportType.FINANCIAL && (startDate == null || endDate == null)) {
            showError("Date range is required for financial reports");
            return false;
        }
        
        if (startDate != null && endDate != null && startDate.after(endDate)) {
            showError("Start date cannot be after end date");
            return false;
        }
        return true;
    }

    private String generateReportFile(Report.ReportType type, Map<String, String> parameters) {
    String fileName = type + "_" + System.currentTimeMillis() + ".pdf";
    String userHome = System.getProperty("user.home");
    String dirPath = userHome + "/travel_booking/reports/";
    String filePath = dirPath + fileName;
    
    try {
        // Create reports directory if it doesn't exist
        File directory = new File(dirPath);
        if (!directory.exists()) {
            directory.mkdirs();
        }

        // Create PDF document
        PdfWriter writer = new PdfWriter(filePath);
        PdfDocument pdf = new PdfDocument(writer);
        Document document = new Document(pdf);
        
        // Add report header
        Paragraph header = new Paragraph("Travel Booking System Report")
                .setFont(PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD))
                .setFontSize(18)
                .setTextAlignment(TextAlignment.CENTER)
                .setMarginBottom(20);
        document.add(header);

        // Add report metadata
        Paragraph meta = new Paragraph()
                .add("Report Type: " + type + "\n")
                .add("Generated: " + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new java.util.Date()) + "\n")
                .add("Parameters: " + parameters.toString())
                .setFontSize(10)
                .setMarginBottom(15);
        document.add(meta);

        // Generate report content based on type
        switch (type) {
            case BOOKINGS:
                generateBookingsReport(document, parameters);
                break;
            case USERS:
                generateUsersReport(document, parameters);
                break;
            case FINANCIAL:
                generateFinancialReport(document, parameters);
                break;
            case SERVICES:
                generateServicesReport(document, parameters);
                break;
        }

        document.close();
        return filePath;
    } catch (Exception e) {
        e.printStackTrace();
        showError("Failed to generate report: " + e.getMessage());
        return null;
    }
}

private void generateBookingsReport(Document document, Map<String, String> parameters) throws SQLException, IOException {
    // Add booking report content
    Paragraph title = new Paragraph("Booking Details")
            .setFont(PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD))
            .setFontSize(14)
            .setMarginBottom(10);
    document.add(title);

    Table table = new Table(UnitValue.createPercentArray(new float[]{2, 3, 2, 2, 2}));
    table.setWidth(UnitValue.createPercentValue(100));
    
    // Table headers
    table.addHeaderCell(createCell("Booking ID", true));
    table.addHeaderCell(createCell("User", true));
    table.addHeaderCell(createCell("Service Type", true));
    table.addHeaderCell(createCell("Total Price", true));
    table.addHeaderCell(createCell("Status", true));

    // Query bookings data
    String query = "SELECT b.booking_id, u.username, b.service_type, b.total_price, b.status " +
                   "FROM bookings b JOIN users u ON b.user_id = u.user_id";
    
    List<Map<String, Object>> bookings = DatabaseManager.executeQuery(query);
    
    for (Map<String, Object> booking : bookings) {
        table.addCell(createCell(booking.get("booking_id").toString(), false));
        table.addCell(createCell(booking.get("username").toString(), false));
        table.addCell(createCell(booking.get("service_type").toString(), false));
        table.addCell(createCell(String.format("$%.2f", booking.get("total_price")), false));
        table.addCell(createCell(booking.get("status").toString(), false));
    }
    
    document.add(table);
}

private Cell createCell(String content, boolean isHeader) throws IOException {
    PdfFont font = isHeader ? 
        PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD) : 
        PdfFontFactory.createFont(StandardFonts.HELVETICA);
    
    return new Cell()
            .add(new Paragraph(content).setFont(font).setFontSize(isHeader ? 12 : 10))
            .setPadding(5)
            .setBackgroundColor(isHeader ? new DeviceRgb(230, 230, 230) : DeviceRgb.WHITE);
}

private void generateUsersReport(Document document, Map<String, String> parameters) throws SQLException, IOException {
    Paragraph title = new Paragraph("User Details")
        .setFont(PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD))
        .setFontSize(14)
        .setMarginBottom(10);
    document.add(title);

    Table table = new Table(UnitValue.createPercentArray(new float[]{1, 3, 4, 2, 3}));
    table.setWidth(UnitValue.createPercentValue(100));

    // Table headers
    table.addHeaderCell(createCell("User ID", true));
    table.addHeaderCell(createCell("Username", true));
    table.addHeaderCell(createCell("Email", true));
    table.addHeaderCell(createCell("Role", true));
    table.addHeaderCell(createCell("Registered", true));

    String query = "SELECT user_id, username, email, role, created_at FROM users";
    List<Map<String, Object>> users = DatabaseManager.executeQuery(query);

    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    for (Map<String, Object> user : users) {
        table.addCell(createCell(user.get("user_id").toString(), false));
        table.addCell(createCell(user.get("username").toString(), false));
        table.addCell(createCell(user.get("email").toString(), false));
        table.addCell(createCell(user.get("role").toString(), false));
        table.addCell(createCell(dateFormat.format(user.get("created_at")), false));
    }
    
    document.add(table);
}

private void generateFinancialReport(Document document, Map<String, String> parameters) throws SQLException, IOException {
    // Financial Summary
    Paragraph summaryTitle = new Paragraph("Financial Summary")
        .setFont(PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD))
        .setFontSize(14)
        .setMarginBottom(10);
    document.add(summaryTitle);

    // Total Revenue
    Map<String, Object> totalRevenue = DatabaseManager.executeQuery(
        "SELECT SUM(total_price) AS total FROM bookings WHERE payment_status = 'paid'"
    ).get(0);
    
    // Create formatted text for amount
    Text amountText = new Text(String.format("$%.2f", totalRevenue.get("total")))
        .setFont(PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD));
    
    Paragraph revenue = new Paragraph()
        .add("Total Revenue: ")
        .add(amountText)
        .setMarginBottom(15);
    document.add(revenue);

    // Payment Details Table
    Table table = new Table(UnitValue.createPercentArray(new float[]{2, 2, 3, 2, 3}));
    table.setWidth(UnitValue.createPercentValue(100));
    
    table.addHeaderCell(createCell("Payment ID", true));
    table.addHeaderCell(createCell("Amount", true));
    table.addHeaderCell(createCell("Method", true));
    table.addHeaderCell(createCell("Status", true));
    table.addHeaderCell(createCell("Date", true));

    String query = "SELECT p.payment_id, p.amount, p.payment_method, p.status, p.payment_date " +
                   "FROM payments p JOIN bookings b ON p.booking_id = b.booking_id " +
                   "WHERE b.payment_status = 'paid'";
    
    List<Map<String, Object>> payments = DatabaseManager.executeQuery(query);
    
    for (Map<String, Object> payment : payments) {
        table.addCell(createCell(payment.get("payment_id").toString(), false));
        table.addCell(createCell(String.format("$%.2f", payment.get("amount")), false));
        table.addCell(createCell(payment.get("payment_method").toString(), false));
        table.addCell(createCell(payment.get("status").toString(), false));
        table.addCell(createCell(new SimpleDateFormat("yyyy-MM-dd").format(payment.get("payment_date")), false));
    }
    
    document.add(table);
}

private void generateServicesReport(Document document, Map<String, String> parameters) throws SQLException, IOException {
    // Flights Section
    Paragraph flightsTitle = new Paragraph("Flight Services")
        .setFont(PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD))
        .setFontSize(14)
        .setMarginBottom(10);
    document.add(flightsTitle);

    Table flightsTable = new Table(UnitValue.createPercentArray(new float[]{3, 2, 2, 3, 3, 2}));
    flightsTable.setWidth(UnitValue.createPercentValue(100));
    
    flightsTable.addHeaderCell(createCell("Airline", true));
    flightsTable.addHeaderCell(createCell("Origin", true));
    flightsTable.addHeaderCell(createCell("Destination", true));
    flightsTable.addHeaderCell(createCell("Departure", true));
    flightsTable.addHeaderCell(createCell("Arrival", true));
    flightsTable.addHeaderCell(createCell("Price", true));

    List<Map<String, Object>> flights = DatabaseManager.executeQuery(
        "SELECT airline, origin, destination, departure, arrival, price FROM flights"
    );
    
    SimpleDateFormat datetimeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
    for (Map<String, Object> flight : flights) {
        flightsTable.addCell(createCell(flight.get("airline").toString(), false));
        flightsTable.addCell(createCell(flight.get("origin").toString(), false));
        flightsTable.addCell(createCell(flight.get("destination").toString(), false));
        flightsTable.addCell(createCell(datetimeFormat.format(flight.get("departure")), false));
        flightsTable.addCell(createCell(datetimeFormat.format(flight.get("arrival")), false));
        flightsTable.addCell(createCell(String.format("$%.2f", flight.get("price")), false));
    }
    document.add(flightsTable);

    // Hotels Section
    Paragraph hotelsTitle = new Paragraph("Hotel Services")
        .setFont(PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD))
        .setFontSize(14)
        .setMarginTop(20)
        .setMarginBottom(10);
    document.add(hotelsTitle);

    Table hotelsTable = new Table(UnitValue.createPercentArray(new float[]{3, 3, 2, 3, 2}));
    hotelsTable.setWidth(UnitValue.createPercentValue(100));
    
    hotelsTable.addHeaderCell(createCell("Hotel Name", true));
    hotelsTable.addHeaderCell(createCell("Location", true));
    hotelsTable.addHeaderCell(createCell("Price/Night", true));
    hotelsTable.addHeaderCell(createCell("Amenities", true));
    hotelsTable.addHeaderCell(createCell("Rooms", true));

    List<Map<String, Object>> hotels = DatabaseManager.executeQuery(
        "SELECT name, location, price_per_night, amenities, rooms_available FROM hotels"
    );
    
    for (Map<String, Object> hotel : hotels) {
        hotelsTable.addCell(createCell(hotel.get("name").toString(), false));
        hotelsTable.addCell(createCell(hotel.get("location").toString(), false));
        hotelsTable.addCell(createCell(String.format("$%.2f", hotel.get("price_per_night")), false));
        hotelsTable.addCell(createCell(hotel.get("amenities").toString(), false));
        hotelsTable.addCell(createCell(hotel.get("rooms_available").toString(), false));
    }
    document.add(hotelsTable);

    // Transport Section
    Paragraph transportTitle = new Paragraph("Transport Services")
        .setFont(PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD))
        .setFontSize(14)
        .setMarginTop(20)
        .setMarginBottom(10);
    document.add(transportTitle);

    Table transportTable = new Table(UnitValue.createPercentArray(new float[]{2, 3, 3, 3, 2}));
    transportTable.setWidth(UnitValue.createPercentValue(100));
    
    transportTable.addHeaderCell(createCell("Type", true));
    transportTable.addHeaderCell(createCell("Company", true));
    transportTable.addHeaderCell(createCell("Pickup", true));
    transportTable.addHeaderCell(createCell("Dropoff", true));
    transportTable.addHeaderCell(createCell("Price", true));

    List<Map<String, Object>> transports = DatabaseManager.executeQuery(
        "SELECT type, company, pickup_location, dropoff_location, price FROM transport"
    );
    
    for (Map<String, Object> transport : transports) {
        transportTable.addCell(createCell(transport.get("type").toString(), false));
        transportTable.addCell(createCell(transport.get("company").toString(), false));
        transportTable.addCell(createCell(transport.get("pickup_location").toString(), false));
        transportTable.addCell(createCell(transport.get("dropoff_location").toString(), false));
        transportTable.addCell(createCell(String.format("$%.2f", transport.get("price")), false));
    }
    document.add(transportTable);
}

    private void refreshReportsTable() {
        try {
            DefaultTableModel model = (DefaultTableModel) reportsTable.getModel();
            model.setRowCount(0);

            List<Map<String, Object>> reports = DatabaseManager.executeQuery(
                "SELECT report_id, report_type, generated_date, parameters, file_path FROM reports"
            );

            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            for (Map<String, Object> report : reports) {
                model.addRow(new Object[]{
                    report.get("report_id"),
                    report.get("report_type"),
                    dateFormat.format(report.get("generated_date")),
                    report.get("parameters"),
                    report.get("file_path")
                });
            }
        } catch (SQLException ex) {
            showError("Error loading reports: " + ex.getMessage());
        }
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

    private void styleDateChooser(JDateChooser dateChooser) {
        dateChooser.setPreferredSize(new Dimension(250, 35));
        dateChooser.getCalendarButton().setBackground(Color.WHITE);
        dateChooser.getCalendarButton().setForeground(Color.BLACK);
        dateChooser.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200)),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
    }

    private void styleComboBox(JComboBox<?> combo) {
        combo.setPreferredSize(new Dimension(250, 35));
        combo.setBackground(Color.WHITE);
        combo.setForeground(Color.BLACK);
        combo.setRenderer(new DefaultListCellRenderer() {
            public Component getListCellRendererComponent(JList<?> list, Object value,
                    int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                setBackground(Color.WHITE);
                setForeground(Color.BLACK);
                return this;
            }
        });
    }

    private void addFormRow(JPanel panel, GridBagConstraints gbc, int row, String label, JComponent component) {
        gbc.gridx = 0;
        gbc.gridy = row;
        JLabel lbl = new JLabel(label);
        lbl.setForeground(Color.WHITE);
        panel.add(lbl, gbc);

        gbc.gridx = 1;
        panel.add(component, gbc);
    }

    private JButton createHoverButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setForeground(Color.BLACK);
        button.setBackground(Color.WHITE);
        button.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200)),
            BorderFactory.createEmptyBorder(10, 20, 10, 20)
        ));
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(240, 240, 240));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(Color.WHITE);
            }
        });
        return button;
    }

    private String mapToString(Map<String, String> map) {
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, String> entry : map.entrySet()) {
            sb.append(entry.getKey()).append("=").append(entry.getValue()).append(";");
        }
        return sb.toString();
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    private static class ReportTableRenderer extends DefaultTableCellRenderer {
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
        SwingUtilities.invokeLater(() -> new GenerateReportsUI().setVisible(true));
    }
}