package Travel_Booking;

import javax.swing.*;
import java.awt.*;
import javax.swing.border.Border;

public class ChooseUser extends JFrame {

    public ChooseUser() {
        initializeUI();
    }

    private void initializeUI() {
        setTitle("Travel Booking System");
        setSize(900, 550);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Main panel with gradient background
        JPanel mainPanel = new JPanel(new BorderLayout()) {
            // Add to all UI classes in their main panel
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

        // Header Panel
        JPanel headerPanel = new JPanel();
        headerPanel.setOpaque(false);
        JLabel titleLabel = new JLabel("Welcome to TravelEase");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 32));
        titleLabel.setForeground(Color.WHITE);
        headerPanel.add(titleLabel);
        mainPanel.add(headerPanel, BorderLayout.NORTH);

        // Role Selection Panel
        JPanel rolePanel = new JPanel(new GridLayout(1, 2, 50, 50));
        rolePanel.setBorder(BorderFactory.createEmptyBorder(80, 100, 80, 100));
        rolePanel.setOpaque(false);

        createRoleButton(rolePanel, "User", "user_icon.png", "Access travel bookings");
        createRoleButton(rolePanel, "Admin", "admin_icon.png", "Manage system content");

        mainPanel.add(rolePanel, BorderLayout.CENTER);

        // Footer Panel
        JPanel footerPanel = new JPanel();
        footerPanel.setOpaque(false);
        JLabel footerLabel = new JLabel("© 2025 TravelEase - Your Journey Starts Here");
        footerLabel.setForeground(Color.WHITE);
        footerLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        footerPanel.add(footerLabel);
        mainPanel.add(footerPanel, BorderLayout.SOUTH);

        add(mainPanel);
    }

    private void createRoleButton(JPanel panel, String role, String iconPath, String description) {
        JButton button = new JButton();
        button.setLayout(new BorderLayout());
        
        // Load and scale image
        ImageIcon originalIcon = loadImageIcon(iconPath, 80, 80);
        JLabel iconLabel = new JLabel(originalIcon);
        iconLabel.setHorizontalAlignment(JLabel.CENTER);
        
        // Text labels
        JLabel roleLabel = new JLabel(role);
        roleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        roleLabel.setForeground(Color.WHITE);
        roleLabel.setHorizontalAlignment(JLabel.CENTER);
        
        JLabel descLabel = new JLabel(description);
        descLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        descLabel.setForeground(new Color(255, 255, 255, 200));
        descLabel.setHorizontalAlignment(JLabel.CENTER);

        // Text container
        JPanel textPanel = new JPanel(new GridLayout(2, 1));
        textPanel.setOpaque(false);
        textPanel.add(roleLabel);
        textPanel.add(descLabel);
        textPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));

        // Button content
        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.setOpaque(false);
        contentPanel.add(iconLabel, BorderLayout.CENTER);
        contentPanel.add(textPanel, BorderLayout.SOUTH);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));

        button.add(contentPanel, BorderLayout.CENTER);
        
        // Styling
        button.setBackground(new Color(255, 255, 255, 30));
        button.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(255, 255, 255, 80), 1),
            BorderFactory.createEmptyBorder(20, 30, 20, 30)
        ));
        button.setOpaque(false);
        button.setContentAreaFilled(false);
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Hover effects
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(255, 255, 255, 60));
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(255, 255, 255, 30));
            }
        });

        button.addActionListener(e -> handleRoleSelection(role));
        panel.add(button);
    }

    private ImageIcon loadImageIcon(String path, int width, int height) {
        try {
            ImageIcon originalIcon = new ImageIcon(getClass().getResource("/Travel_Booking/icons/" + path));
            Image scaledImage = originalIcon.getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH);
            return new ImageIcon(scaledImage);
        } catch (Exception e) {
            System.err.println("Error loading icon: " + e.getMessage());
            return new ImageIcon(); // Return empty icon as fallback
        }
    }

    // Rest of the class remains the same...
    private void handleRoleSelection(String role) {
        dispose();
        switch (role) {
            case "User":
                new UserLogin().setVisible(true);
                break;
            case "Admin":
                new AdminLogin().setVisible(true);
                break;
        }
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        SwingUtilities.invokeLater(() -> {
            new ChooseUser().setVisible(true);
        });
    }
}
