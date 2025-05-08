package Travel_Booking;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import javax.imageio.ImageIO;
import java.sql.*;
import java.util.List;
import java.util.Map;

public class AdminLogin extends JFrame {

    static {
        // Force cross-platform look and feel
        try {
            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
            UIManager.put("Button.background", new Color(62, 133, 185));
            UIManager.put("Button.foreground", Color.WHITE);
            UIManager.put("Button.border", BorderFactory.createLineBorder(Color.WHITE));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private JTextField txtUsername;
    private JPasswordField txtPassword;

    public AdminLogin() {
        initializeUI();
    }

    private void initializeUI() {
        setTitle("Admin Login");
        setSize(800, 500);
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

        mainPanel.setBorder(BorderFactory.createEmptyBorder(40, 40, 40, 40));

        // Back button panel
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.setOpaque(false);
        topPanel.add(createBackButton());

        // Left Panel with Image
        JPanel leftPanel = new JPanel(new BorderLayout());
        leftPanel.setOpaque(false);
        ImageIcon originalIcon = loadImageIcon("admin_login_icon.png", 300, 300);
        JLabel imageLabel = new JLabel(originalIcon);
        imageLabel.setHorizontalAlignment(JLabel.CENTER);
        leftPanel.add(imageLabel, BorderLayout.CENTER);

        // Right Panel with Login Form
        JPanel rightPanel = createLoginForm();

        mainPanel.add(topPanel, BorderLayout.NORTH);
        mainPanel.add(leftPanel, BorderLayout.WEST);
        mainPanel.add(rightPanel, BorderLayout.CENTER);

        add(mainPanel);
    }

    private JPanel createLoginForm() {
        JPanel rightPanel = new JPanel(new GridBagLayout());
        rightPanel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.WEST;

        JLabel lblUsername = new JLabel("Username:");
        lblUsername.setForeground(Color.WHITE);
        txtUsername = new JTextField(20);
        styleTextField(txtUsername);

        JLabel lblPassword = new JLabel("Password:");
        lblPassword.setForeground(Color.WHITE);
        txtPassword = new JPasswordField(20);
        styleTextField(txtPassword);

        JButton btnLogin = createHoverButton("Login");

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;

        gbc.gridy++;
        gbc.gridwidth = 1;
        rightPanel.add(lblUsername, gbc);
        
        gbc.gridx = 1;
        rightPanel.add(txtUsername, gbc);

        gbc.gridy++;
        gbc.gridx = 0;
        rightPanel.add(lblPassword, gbc);
        
        gbc.gridx = 1;
        rightPanel.add(txtPassword, gbc);

        gbc.gridy++;
        gbc.gridx = 0;
        gbc.gridwidth = 2;
        rightPanel.add(btnLogin, gbc);

        btnLogin.addActionListener(this::performLogin);

        return rightPanel;
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
            new ChooseUser().setVisible(true);
            dispose();
        });

        return btnBack;
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

    private void performLogin(ActionEvent evt) {
        String username = txtUsername.getText().trim();
        String password = new String(txtPassword.getPassword());

        // Validate inputs
        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "Please fill in all fields!", 
                "Error", 
                JOptionPane.ERROR_MESSAGE
            );
            return;
        }

        try {
            // Get admin user from database
            String sql = "SELECT user_id, password, role FROM users WHERE username = ?";
            List<Map<String, Object>> results = DatabaseManager.executeQuery(sql, username);

            if (results.isEmpty()) {
                showAuthenticationError();
                return;
            }

            Map<String, Object> userData = results.get(0);
            String storedHash = (String) userData.get("password");
            String role = (String) userData.get("role");
            int userId = (int) userData.get("user_id");

            // Verify admin role and password
            if (!"admin".equalsIgnoreCase(role)) {
                JOptionPane.showMessageDialog(this, 
                    "Administrator access required!", 
                    "Access Denied", 
                    JOptionPane.WARNING_MESSAGE
                );
                return;
            }

            if (PasswordUtils.checkPassword(password, storedHash)) {
                new AdminDashboard().setVisible(true);
                dispose();
            } else {
                showAuthenticationError();
            }
        } catch (SQLException e) {
            handleDatabaseError(e);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, 
                "An error occurred: " + e.getMessage(), 
                "Error", 
                JOptionPane.ERROR_MESSAGE
            );
            e.printStackTrace();
        }
    }

    private void showAuthenticationError() {
        JOptionPane.showMessageDialog(this,
            "Invalid administrator credentials!",
            "Authentication Failed",
            JOptionPane.ERROR_MESSAGE
        );
    }

    private void handleDatabaseError(SQLException e) {
        String message = "Database error: ";
        if (e.getErrorCode() == 0) {
            message += "Unable to connect to database";
        } else {
            message += e.getMessage();
        }

        JOptionPane.showMessageDialog(this, 
            message, 
            "Database Error", 
            JOptionPane.ERROR_MESSAGE
        );
        e.printStackTrace();
    }
    
    

     public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new AdminLogin().setVisible(true);
        });
    }

    
}