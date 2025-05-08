package Travel_Booking;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;

public class ManageUsersUI extends JFrame {
    
    private JScrollPane scrollPane;
    private JTable userTable;
    private JComboBox<String> roleSelector;

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

    public ManageUsersUI() {
        initializeUI();
    }

    private void initializeUI() {
        setTitle("Manage Users");
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

        JLabel titleLabel = new JLabel("User Management");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        headerPanel.add(titleLabel, BorderLayout.CENTER);

        mainPanel.add(headerPanel, BorderLayout.NORTH);

        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setOpaque(false);
        tabbedPane.setBackground(new Color(255, 255, 255, 50));
        tabbedPane.setForeground(Color.WHITE);
        
        tabbedPane.addTab("User List", createUserListPanel());
        tabbedPane.addTab("Add New User", createAddUserPanel());
        tabbedPane.addTab("Edit User", createEditUserPanel());

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

    private JPanel createUserListPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        String[] columns = {"ID", "Username", "Email", "Full Name", "Role", "Created At", "Last Login"};
        DefaultTableModel model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        userTable = new JTable(model);
        userTable.setDefaultRenderer(Object.class, new UserTableRenderer());
        userTable.setRowHeight(30);
        userTable.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        
        JTableHeader header = userTable.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 14));
        header.setBackground(new Color(62, 133, 185));
        header.setForeground(Color.WHITE);
        
        scrollPane = new JScrollPane(userTable);
        scrollPane.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(255, 255, 255, 80), 1),
            BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));

        JButton btnRefresh = createHoverButton("Refresh List");
        btnRefresh.addActionListener(e -> refreshUserTable());

        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        controlPanel.setOpaque(false);
        controlPanel.add(btnRefresh);

        panel.add(controlPanel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);

        refreshUserTable();
        return panel;
    }

    private JPanel createAddUserPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.WEST;

        roleSelector = new JComboBox<>(new String[]{"user", "admin"});
        JTextField txtUsername = new JTextField(25);
        JPasswordField txtPassword = new JPasswordField(25);
        JTextField txtEmail = new JTextField(25);
        JTextField txtFullName = new JTextField(25);
        JTextField txtPhone = new JTextField(25);
        JTextField txtAddress = new JTextField(25);

        styleTextField(roleSelector);
        styleTextField(txtUsername);
        styleTextField(txtPassword);
        styleTextField(txtEmail);
        styleTextField(txtFullName);
        styleTextField(txtPhone);
        styleTextField(txtAddress);

        addFormRow(panel, gbc, 0, "Role*:", roleSelector);
        addFormRow(panel, gbc, 1, "Username*:", txtUsername);
        addFormRow(panel, gbc, 2, "Password*:", txtPassword);
        addFormRow(panel, gbc, 3, "Email*:", txtEmail);
        addFormRow(panel, gbc, 4, "Full Name:", txtFullName);
        addFormRow(panel, gbc, 5, "Phone:", txtPhone);
        addFormRow(panel, gbc, 6, "Address:", txtAddress);

        JButton btnSubmit = createHoverButton("Add User");
        gbc.gridx = 1;
        gbc.gridy = 7;
        panel.add(btnSubmit, gbc);

        btnSubmit.addActionListener(e -> validateAndAddUser(
            (String) roleSelector.getSelectedItem(),
            txtUsername,
            txtPassword,
            txtEmail,
            txtFullName,
            txtPhone,
            txtAddress
        ));

        return panel;
    }

    private JPanel createEditUserPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JComboBox<String> userSelector = new JComboBox<>();
        userSelector.setPreferredSize(new Dimension(250, 35));
        refreshUserSelector(userSelector);
        styleTextField(userSelector);

        JPanel formPanel = createEditFormPanel(userSelector);
        panel.add(formPanel, BorderLayout.CENTER);
        return panel;
    }

    // Helper methods similar to ManageTransportUI but adapted for users
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

    private void refreshUserTable() {
        try {
            DefaultTableModel model = (DefaultTableModel) userTable.getModel();
            model.setRowCount(0);
            
            List<Map<String, Object>> results = DatabaseManager.executeQuery(
                "SELECT user_id, username, email, full_name, role, created_at, last_login FROM users"
            );
            
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            for (Map<String, Object> row : results) {
                model.addRow(new Object[]{
                    row.get("user_id"),
                    row.get("username"),
                    row.get("email"),
                    row.get("full_name"),
                    row.get("role"),
                    dateFormat.format(row.get("created_at")),
                    row.get("last_login") != null ? dateFormat.format(row.get("last_login")) : "Never"
                });
            }
        } catch (SQLException e) {
            showError("Error refreshing table: " + e.getMessage());
        }
    }

    private void validateAndAddUser(String role, JTextField txtUsername,
                                   JPasswordField txtPassword, JTextField txtEmail,
                                   JTextField txtFullName, JTextField txtPhone,
                                   JTextField txtAddress) {
        try {
            if (validateUserFields(txtUsername, txtPassword, txtEmail)) {
                String hashedPassword = PasswordUtils.hashPassword(new String(txtPassword.getPassword()));
                
                int result = DatabaseManager.executeUpdate(
                    "INSERT INTO users (username, password, email, full_name, phone, address, role) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?)",
                    txtUsername.getText().trim(),
                    hashedPassword,
                    txtEmail.getText().trim(),
                    txtFullName.getText().trim(),
                    txtPhone.getText().trim(),
                    txtAddress.getText().trim(),
                    role
                );

                if (result > 0) {
                    JOptionPane.showMessageDialog(this, "User added successfully!");
                    refreshUserTable();
                }
            }
        } catch (SQLException e) {
            handleDatabaseError(e);
        }
    }

    private boolean validateUserFields(JTextField txtUsername, JPasswordField txtPassword,
                                      JTextField txtEmail) {
        String username = txtUsername.getText().trim();
        String password = new String(txtPassword.getPassword());
        String email = txtEmail.getText().trim();

        if (username.isEmpty() || password.isEmpty() || email.isEmpty()) {
            showError("Username, Password, and Email are required fields!");
            return false;
        }

        if (!email.matches("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$")) {
            showError("Invalid email format!");
            return false;
        }

        try {
            List<Map<String, Object>> existingUsers = DatabaseManager.executeQuery(
                "SELECT user_id FROM users WHERE username = ? OR email = ?",
                username, email
            );
            if (!existingUsers.isEmpty()) {
                showError("Username or email already exists!");
                return false;
            }
        } catch (SQLException e) {
            showError("Error checking existing users: " + e.getMessage());
            return false;
        }

        return true;
    }

    private void handleDatabaseError(SQLException e) {
        if (e.getErrorCode() == 1062) {
            showError("Username or email already exists!");
        } else {
            showError("Database error: " + e.getMessage());
        }
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    private void refreshUserSelector(JComboBox<String> selector) {
        try {
            List<Map<String, Object>> results = DatabaseManager.executeQuery(
                "SELECT user_id, username FROM users ORDER BY username ASC"
            );
            
            selector.removeAllItems();
            if (!results.isEmpty()) {
                for (Map<String, Object> row : results) {
                    selector.addItem(row.get("user_id") + " - " + row.get("username"));
                }
                selector.setEnabled(true);
            } else {
                selector.addItem("No users available");
                selector.setEnabled(false);
            }
        } catch (SQLException e) {
            selector.addItem("Error loading users");
            selector.setEnabled(false);
        }
    }

    private JPanel createEditFormPanel(JComboBox<String> userSelector) {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.WEST;

        JComboBox<String> roleSelector = new JComboBox<>(new String[]{"user", "admin"});
        JTextField txtUsername = new JTextField(25);
        JTextField txtEmail = new JTextField(25);
        JTextField txtFullName = new JTextField(25);
        JTextField txtPhone = new JTextField(25);
        JTextField txtAddress = new JTextField(25);

        styleTextField(roleSelector);
        styleTextField(txtUsername);
        styleTextField(txtEmail);
        styleTextField(txtFullName);
        styleTextField(txtPhone);
        styleTextField(txtAddress);

        addFormRow(panel, gbc, 0, "User:", userSelector);
        addFormRow(panel, gbc, 1, "Role*:", roleSelector);
        addFormRow(panel, gbc, 2, "Username*:", txtUsername);
        addFormRow(panel, gbc, 3, "Email*:", txtEmail);
        addFormRow(panel, gbc, 4, "Full Name:", txtFullName);
        addFormRow(panel, gbc, 5, "Phone:", txtPhone);
        addFormRow(panel, gbc, 6, "Address:", txtAddress);

        JButton btnUpdate = createHoverButton("Update User");
        gbc.gridx = 1;
        gbc.gridy = 7;
        panel.add(btnUpdate, gbc);

        btnUpdate.addActionListener(e -> {
            Object selected = userSelector.getSelectedItem();
            if (selected != null && !selected.toString().startsWith("No users")) {
                updateUser(
                    selected.toString(),
                    (String) roleSelector.getSelectedItem(),
                    txtUsername, txtEmail,
                    txtFullName, txtPhone, txtAddress,
                    userSelector
                );
            }
        });

        userSelector.addActionListener(e -> {
            Object selected = userSelector.getSelectedItem();
            if (selected != null && !selected.toString().startsWith("No users")) {
                loadUserDetails(
                    selected.toString(),
                    roleSelector, txtUsername, txtEmail,
                    txtFullName, txtPhone, txtAddress
                );
            }
        });

        return panel;
    }

    private void loadUserDetails(String selectedUser, JComboBox<String> roleSelector,
                                JTextField txtUsername, JTextField txtEmail,
                                JTextField txtFullName, JTextField txtPhone,
                                JTextField txtAddress) {
        try {
            int userId = Integer.parseInt(selectedUser.split(" - ")[0]);
            List<Map<String, Object>> results = DatabaseManager.executeQuery(
                "SELECT * FROM users WHERE user_id = ?", userId
            );

            if (!results.isEmpty()) {
                Map<String, Object> userData = results.get(0);
                roleSelector.setSelectedItem(userData.get("role"));
                txtUsername.setText(userData.get("username").toString());
                txtEmail.setText(userData.get("email").toString());
                txtFullName.setText(userData.get("full_name") != null ? userData.get("full_name").toString() : "");
                txtPhone.setText(userData.get("phone") != null ? userData.get("phone").toString() : "");
                txtAddress.setText(userData.get("address") != null ? userData.get("address").toString() : "");
            }
        } catch (SQLException | NumberFormatException ex) {
            showError("Error loading user details: " + ex.getMessage());
        }
    }

    private void updateUser(String selectedUser, String role,
                           JTextField txtUsername, JTextField txtEmail,
                           JTextField txtFullName, JTextField txtPhone,
                           JTextField txtAddress, JComboBox<String> userSelector) {
        try {
            int userId = Integer.parseInt(selectedUser.split(" - ")[0]);
            
            int result = DatabaseManager.executeUpdate(
                "UPDATE users SET role = ?, username = ?, email = ?, " +
                "full_name = ?, phone = ?, address = ? WHERE user_id = ?",
                role,
                txtUsername.getText().trim(),
                txtEmail.getText().trim(),
                txtFullName.getText().trim(),
                txtPhone.getText().trim(),
                txtAddress.getText().trim(),
                userId
            );

            if (result > 0) {
                JOptionPane.showMessageDialog(this, "User updated successfully!");
                refreshUserTable();
                refreshUserSelector(userSelector);
            }
        } catch (SQLException | NumberFormatException ex) {
            handleDatabaseError((SQLException) ex);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new ManageUsersUI().setVisible(true));
    }

    private static class UserTableRenderer extends DefaultTableCellRenderer {
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