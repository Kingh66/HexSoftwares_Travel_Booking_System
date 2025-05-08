package Travel_Booking;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import javax.imageio.ImageIO;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public class UserDashboard extends JFrame {

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

    public UserDashboard() {
        initializeUI();
    }

    private void initializeUI() {
    setTitle("User Dashboard");
    setSize(1000, 600);
    setLocationRelativeTo(null);
    setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

    // Main content panel - MUST BE TRANSPARENT
    JPanel mainPanel = new JPanel(new BorderLayout());
    mainPanel.setOpaque(false);  // Crucial transparency setting
    mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

    // Gradient background panel - Parent container
    JPanel backgroundPanel = new JPanel(new BorderLayout()) {
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

    // Add components to main panel
    mainPanel.add(createHeaderPanel(), BorderLayout.NORTH);
    mainPanel.add(createDashboardButtons(), BorderLayout.CENTER);
    mainPanel.add(createFooterPanel(), BorderLayout.SOUTH);

    // Add main panel to background
    backgroundPanel.add(mainPanel);
    
    // Set content pane properties
    setContentPane(backgroundPanel);
    setVisible(true);
}

    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        
        String welcomeMessage = "<html><div style='text-align: left;'>"
            + "Welcome, <b>" + getUsersName() + "</b>!"
            + "</div></html>";
        
        JLabel welcomeLabel = new JLabel(welcomeMessage);
        welcomeLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        welcomeLabel.setForeground(Color.WHITE);
        
        JButton btnLogout = createHoverButton("Logout");
        btnLogout.addActionListener(e -> logout());
        
        headerPanel.add(welcomeLabel, BorderLayout.WEST);
        headerPanel.add(btnLogout, BorderLayout.EAST);
        
        return headerPanel;
    }

    private JPanel createDashboardButtons() {
        JPanel dashboardPanel = new JPanel(new GridLayout(2, 3, 20, 20));
        dashboardPanel.setOpaque(false);
        dashboardPanel.setBorder(BorderFactory.createEmptyBorder(40, 40, 40, 40));

        String[][] buttonConfigs = {
            {"Book Flights", "flight_icon.png", "Search & book flights"},
            {"Book Hotels", "hotel_icon.png", "Find perfect accommodations"},
            {"Book Transport", "transport_icon.png", "Rent cars/book transfers"},
            {"Booking History", "history_icon.png", "View past bookings"},
            {"Profile Management", "profile_icon.png", "Update personal details"},
            {"Help & Support", "support_icon.png", "Get assistance"}
        };

        for (String[] config : buttonConfigs) {
            createDashboardButton(dashboardPanel, config[0], config[1], config[2]);
        }

        return dashboardPanel;
    }

    private JPanel createFooterPanel() {
        JPanel footerPanel = new JPanel();
        footerPanel.setOpaque(false);
        JLabel footerLabel = new JLabel("© 2025 TravelEase - Safe Travels!");
        footerLabel.setForeground(Color.WHITE);
        footerLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        footerPanel.add(footerLabel);
        return footerPanel;
    }

    private void createDashboardButton(JPanel panel, String title, String iconPath, String description) {
        JButton button = new JButton();
        button.setLayout(new BorderLayout());
        button.setPreferredSize(new Dimension(200, 200));
        
        // Content panel
        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.setOpaque(false);

        // Icon
        ImageIcon icon = loadImageIcon(iconPath, 80, 80);
        if (icon != null) {
            JLabel iconLabel = new JLabel(icon);
            iconLabel.setHorizontalAlignment(JLabel.CENTER);
            contentPanel.add(iconLabel, BorderLayout.CENTER);
        }

        // Text
        JPanel textPanel = new JPanel(new GridLayout(2, 1));
        textPanel.setOpaque(false);
        
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setHorizontalAlignment(JLabel.CENTER);
        
        JLabel descLabel = new JLabel("<html><center>" + description + "</center></html>");
        descLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        descLabel.setForeground(new Color(255, 255, 255, 200));
        descLabel.setHorizontalAlignment(JLabel.CENTER);
        
        textPanel.add(titleLabel);
        textPanel.add(descLabel);

        contentPanel.add(textPanel, BorderLayout.SOUTH);
        button.add(contentPanel);

        // Styling
        button.setBackground(new Color(62, 133, 185, 200));
        button.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.WHITE, 1),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        button.setOpaque(true);
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Hover effect
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(12, 46, 97, 200));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(62, 133, 185, 200));
            }
        });

        // Action
        button.addActionListener(e -> handleDashboardAction(title));
        panel.add(button);
    }

    private String getUsersName() {
        return Session.getCurrentFullName() != null ? 
               Session.getCurrentFullName() : 
               "User";
    }

    
    private void handleDashboardAction(String action) {
        switch (action) {
            case "Book Flights":
                new FlightBookingUI().setVisible(true);
                break;
            case "Book Hotels":
                new HotelBookingUI().setVisible(true);
                break;
            case "Book Transport":
                new TransportBookingUI().setVisible(true);
                break;
            case "Booking History":
                new BookingHistoryUI().setVisible(true);
                break;
            case "Profile Management":
                new ProfileManagementUI().setVisible(true);
                break;
            case "Help & Support":
                new HelpSupportUI().setVisible(true);
                break;
        }
    }

    private JButton createHoverButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setForeground(Color.WHITE);
        button.setBackground(new Color(62, 133, 185));
        button.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.WHITE, 1),
            BorderFactory.createEmptyBorder(5, 15, 5, 15)
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
    

    private void logout() {
        dispose();
        new UserLogin().setVisible(true);
    }


    private void showDatabaseError() {
        JOptionPane.showMessageDialog(this,
            "Error connecting to database. Please try again later.",
            "Database Error",
            JOptionPane.ERROR_MESSAGE
        );
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
           
            new UserDashboard().setVisible(true);
        });
    }
}

