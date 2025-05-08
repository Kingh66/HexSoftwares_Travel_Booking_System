package Travel_Booking;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import javax.imageio.ImageIO;
import java.sql.*;
import java.util.List;
import java.util.Map;

public class UserRegistration extends JFrame {

    static {
        // Force consistent look and feel
        try {
            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
            UIManager.put("Button.background", new Color(62, 133, 185));
            UIManager.put("Button.foreground", Color.WHITE);
            UIManager.put("Button.border", BorderFactory.createLineBorder(Color.WHITE));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private JTextField txtFullName;
    private JTextField txtEmail;
    private JTextField txtUsername;
    private JPasswordField txtPassword;
    private JPasswordField txtConfirmPassword;

    public UserRegistration() {
        initializeUI();
    }

    private void initializeUI() {
        setTitle("User Registration");
        setSize(900, 550);
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
        ImageIcon originalIcon = loadImageIcon("register_icon.png", 300, 300);
        JLabel imageLabel = new JLabel(originalIcon);
        imageLabel.setHorizontalAlignment(JLabel.CENTER);
        leftPanel.add(imageLabel, BorderLayout.CENTER);

        // Right Panel with Registration Form
        JPanel rightPanel = createRegistrationForm();

        mainPanel.add(topPanel, BorderLayout.NORTH);
        mainPanel.add(leftPanel, BorderLayout.WEST);
        mainPanel.add(rightPanel, BorderLayout.CENTER);

        add(mainPanel);
    }

    private JPanel createRegistrationForm() {
        JPanel rightPanel = new JPanel(new GridBagLayout());
        rightPanel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.WEST;

        // Form Fields
        JLabel lblFullName = new JLabel("Full Name:");
        lblFullName.setForeground(Color.WHITE);
        txtFullName = new JTextField(20);
        styleTextField(txtFullName);

        JLabel lblEmail = new JLabel("Email:");
        lblEmail.setForeground(Color.WHITE);
        txtEmail = new JTextField(20);
        styleTextField(txtEmail);

        JLabel lblUsername = new JLabel("Username:");
        lblUsername.setForeground(Color.WHITE);
        txtUsername = new JTextField(20);
        styleTextField(txtUsername);

        JLabel lblPassword = new JLabel("Password:");
        lblPassword.setForeground(Color.WHITE);
        txtPassword = new JPasswordField(20);
        styleTextField(txtPassword);

        JLabel lblConfirmPassword = new JLabel("Confirm Password:");
        lblConfirmPassword.setForeground(Color.WHITE);
        txtConfirmPassword = new JPasswordField(20);
        styleTextField(txtConfirmPassword);

        // Buttons Panel
        JPanel buttonPanel = new JPanel(new GridLayout(1, 2, 10, 0));
        buttonPanel.setOpaque(false);
        
        JButton btnRegister = createHoverButton("Register");
        JButton btnLogin = createHoverButton("Existing User?");

        // Add components
        addFormRow(rightPanel, gbc, 0, lblFullName, txtFullName);
        addFormRow(rightPanel, gbc, 1, lblEmail, txtEmail);
        addFormRow(rightPanel, gbc, 2, lblUsername, txtUsername);
        addFormRow(rightPanel, gbc, 3, lblPassword, txtPassword);
        addFormRow(rightPanel, gbc, 4, lblConfirmPassword, txtConfirmPassword);

        gbc.gridy = 5;
        gbc.gridx = 0;
        gbc.gridwidth = 2;
        buttonPanel.add(btnRegister);
        buttonPanel.add(btnLogin);
        rightPanel.add(buttonPanel, gbc);

        // Action listeners
        btnRegister.addActionListener(this::performRegistration);
        btnLogin.addActionListener(e -> openLogin());

        return rightPanel;
    }

    private void addFormRow(JPanel panel, GridBagConstraints gbc, int row, JLabel label, JComponent field) {
        gbc.gridy = row;
        gbc.gridx = 0;
        panel.add(label, gbc);
        
        gbc.gridx = 1;
        panel.add(field, gbc);
    }

    private JButton createBackButton() {
        JButton btnBack = new JButton("← Back to Login");
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
            new UserLogin().setVisible(true);
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

    private void performRegistration(ActionEvent evt) {
        String fullName = txtFullName.getText().trim();
        String email = txtEmail.getText().trim();
        String username = txtUsername.getText().trim();
        String password = new String(txtPassword.getPassword());
        String confirmPassword = new String(txtConfirmPassword.getPassword());

        // Validation
        if (fullName.isEmpty() || email.isEmpty() || username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill in all fields!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (!password.equals(confirmPassword)) {
            JOptionPane.showMessageDialog(this, "Passwords do not match!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (!isValidEmail(email)) {
            JOptionPane.showMessageDialog(this, "Invalid email format!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            // Check if username already exists
            String checkUserSql = "SELECT username FROM users WHERE username = ?";
            List<Map<String, Object>> existingUser = DatabaseManager.executeQuery(checkUserSql, username);

            if (!existingUser.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Username already exists!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Hash password
            String hashedPassword = PasswordUtils.hashPassword(password);

            // Insert new user
            String insertSql = "INSERT INTO users (username, password, email, full_name, role) VALUES (?, ?, ?, ?, ?)";
            int affectedRows = DatabaseManager.executeUpdate(
                insertSql,
                username,
                hashedPassword,
                email,
                fullName,
                "user"  // Default role
            );

            if (affectedRows > 0) {
                JOptionPane.showMessageDialog(this, "Registration successful!", "Success", JOptionPane.INFORMATION_MESSAGE);
                new UserLogin().setVisible(true);
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Registration failed!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException e) {
            handleSQLException(e);
        }
    }

    private boolean isValidEmail(String email) {
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
        return email.matches(emailRegex);
    }

    private void handleSQLException(SQLException e) {
        if (e.getErrorCode() == 1062) { // MySQL error code for duplicate entry
            JOptionPane.showMessageDialog(this, "Username or email already exists!", "Error", JOptionPane.ERROR_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this, 
                "Database error: " + e.getMessage(), 
                "Error", 
                JOptionPane.ERROR_MESSAGE
            );
            e.printStackTrace();
        }
    }

    private void openLogin() {
        new UserLogin().setVisible(true);
        dispose();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new UserRegistration().setVisible(true));
    }
}