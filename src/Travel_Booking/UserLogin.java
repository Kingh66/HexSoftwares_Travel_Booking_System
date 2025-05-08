package Travel_Booking;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import javax.imageio.ImageIO;
import java.sql.*;
import java.util.List;
import java.util.Map;

public class UserLogin extends JFrame {

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

    private JTextField txtUsername;
    private JPasswordField txtPassword;

    public UserLogin() {
        initializeUI();
    }

    private void initializeUI() {
        setTitle("User Login");
        setSize(800, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel mainPanel = createMainPanel();
        add(mainPanel);
    }
    private JPanel createMainPanel() {
        JPanel mainPanel = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                paintGradientBackground(g);
            }
        };
        mainPanel.setBorder(BorderFactory.createEmptyBorder(40, 40, 40, 40));

        mainPanel.add(createTopPanel(), BorderLayout.NORTH);
        mainPanel.add(createLeftPanel(), BorderLayout.WEST);
        mainPanel.add(createLoginForm(), BorderLayout.CENTER);

        return mainPanel;
    }

    private void paintGradientBackground(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        Color startColor = new Color(12, 46, 97);
        Color endColor = new Color(62, 133, 185);
        GradientPaint gradient = new GradientPaint(0, 0, startColor, getWidth(), getHeight(), endColor);
        g2d.setPaint(gradient);
        g2d.fillRect(0, 0, getWidth(), getHeight());
    }

    private JPanel createTopPanel() {
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.setOpaque(false);
        topPanel.add(createBackButton());
        return topPanel;
    }

    private JPanel createLeftPanel() {
        JPanel leftPanel = new JPanel(new BorderLayout());
        leftPanel.setOpaque(false);
        ImageIcon icon = loadImageIcon("login_icon.png", 300, 300);
        leftPanel.add(new JLabel(icon), BorderLayout.CENTER);
        return leftPanel;
    }

    private JPanel createLoginForm() {
        JPanel rightPanel = new JPanel(new GridBagLayout());
        rightPanel.setOpaque(false);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.WEST;

        addFormComponents(rightPanel, gbc);
        return rightPanel;
    }

    private void addFormComponents(JPanel panel, GridBagConstraints gbc) {
        txtUsername = createStyledTextField(20);
        txtPassword = createStyledPasswordField(20);

        panel.add(createFormLabel("Username:"), gbc);
        gbc.gridx = 1;
        panel.add(txtUsername, gbc);

        gbc.gridy = 1;
        gbc.gridx = 0;
        panel.add(createFormLabel("Password:"), gbc);
        gbc.gridx = 1;
        panel.add(txtPassword, gbc);

        gbc.gridy = 2;
        gbc.gridx = 0;
        gbc.gridwidth = 2;
        panel.add(createButtonPanel(), gbc);

        gbc.gridy = 3;
        panel.add(createForgotPasswordLabel(), gbc);
    }

    private JLabel createFormLabel(String text) {
        JLabel label = new JLabel(text);
        label.setForeground(Color.WHITE);
        return label;
    }

    private JTextField createStyledTextField(int columns) {
        JTextField field = new JTextField(columns);
        styleTextField(field);
        return field;
    }

    private JPasswordField createStyledPasswordField(int columns) {
        JPasswordField field = new JPasswordField(columns);
        styleTextField(field);
        return field;
    }

    private JPanel createButtonPanel() {
        JPanel buttonPanel = new JPanel(new GridLayout(1, 2, 10, 0));
        buttonPanel.setOpaque(false);
        
        JButton btnLogin = createHoverButton("Login");
        JButton btnRegister = createHoverButton("Register");

        btnLogin.addActionListener(this::performLogin);
        btnRegister.addActionListener(e -> openRegistration());

        buttonPanel.add(btnLogin);
        buttonPanel.add(btnRegister);
        
        return buttonPanel;
    }

    private JLabel createForgotPasswordLabel() {
        JLabel lblForgot = new JLabel("<html><u>Forgot Password?</u></html>");
        lblForgot.setForeground(Color.WHITE);
        lblForgot.setCursor(new Cursor(Cursor.HAND_CURSOR));
        lblForgot.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                showError("Password reset feature coming soon!");
            }
        });
        return lblForgot;
    }

    private void performLogin(ActionEvent evt) {
        String username = txtUsername.getText().trim();
        String password = new String(txtPassword.getPassword());

        if (username.isEmpty() || password.isEmpty()) {
            showError("Please fill in all fields!");
            return;
        }

        try {
            processLogin(username, password);
        } catch (SQLException e) {
            showError(e.getErrorCode() == 0 ? 
                "Unable to connect to database" : 
                "Database error: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            showError("An error occurred: " + e.getMessage());
            e.printStackTrace();
        }
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

    private void processLogin(String username, String password) throws SQLException {
    List<Map<String, Object>> results = DatabaseManager.executeQuery(
        "SELECT * FROM users WHERE username = ?", // Get all user fields
        username
    );

    if (results.isEmpty()) {
        showError("Invalid username or password!");
        return;
    }

    Map<String, Object> userData = results.get(0);
    String storedHash = (String) userData.get("password");

    if (PasswordUtils.checkPassword(password, storedHash)) {
        // Create full User object
        User user = new User();
        user.setUserId((int) userData.get("user_id"));
        user.setUsername((String) userData.get("username"));
        user.setEmail((String) userData.get("email"));
        user.setFullName((String) userData.get("full_name"));
        user.setPhone((String) userData.get("phone"));
        user.setAddress((String) userData.get("address"));
        user.setRoleFromString((String) userData.get("role"));
        user.setProfilePicturePath((String) userData.get("profile_picture_path"));
        
        // Set complete user object in session
        Session.setCurrentUser(user);
        
        DatabaseManager.executeUpdate(
            "UPDATE users SET last_login = CURRENT_TIMESTAMP WHERE user_id = ?",
            user.getUserId()
        );
        openUserDashboard();
    } else {
        showError("Invalid username or password!");
    }
}


    private void openRegistration() {
        new UserRegistration().setVisible(true);
        dispose();
    }

    private void openUserDashboard() {
        new UserDashboard().setVisible(true);
        dispose();
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(
            this,
            message,
            "Error",
            JOptionPane.ERROR_MESSAGE
        );
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new UserLogin().setVisible(true));
    }
}