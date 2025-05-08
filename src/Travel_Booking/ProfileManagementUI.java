package Travel_Booking;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import java.io.*;
import java.nio.file.*;
import javax.imageio.ImageIO;
import java.sql.SQLException;

public class ProfileManagementUI extends JFrame {
    private JLabel profileImageLabel;
    private JTextField txtFullName, txtEmail, txtPhone, txtAddress;
    private String profileImagePath;
    private static final int IMAGE_SIZE = 150;
    private static final Path PROFILE_IMAGES_DIR = Paths.get(
        System.getProperty("user.home"), ".travel_booking", "profiles");

    public ProfileManagementUI() {
        if (!checkAuthentication()) {
            showErrorAndRedirect("Please login to manage profile");
            return;
        }
        initializeUI();
        loadUserData();
    }

    private void initializeUI() {
        setTitle("Profile Management");
        setSize(800, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        // Main content panel with gradient background
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

        // Profile image section
        JPanel imagePanel = createProfileImagePanel();
        mainPanel.add(imagePanel, BorderLayout.NORTH);

        // Form fields
        JPanel formPanel = createFormPanel();
        mainPanel.add(new JScrollPane(formPanel), BorderLayout.CENTER);

        // Buttons panel
        JPanel buttonPanel = createButtonPanel();
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        setContentPane(mainPanel);
    }

    private JPanel createProfileImagePanel() {
        JPanel panel = new JPanel();
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));

        User user = Session.getCurrentUser();
        boolean isAdmin = user.hasAdminAccess();

        profileImageLabel = new JLabel() {
            @Override
            protected void paintComponent(Graphics g) {
                if (getIcon() != null && !isAdmin) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2.setClip(new java.awt.geom.Ellipse2D.Float(0, 0, IMAGE_SIZE, IMAGE_SIZE));
                    super.paintComponent(g2);
                    g2.dispose();
                }
            }
        };
        profileImageLabel.setPreferredSize(new Dimension(IMAGE_SIZE, IMAGE_SIZE));

        if (!isAdmin) {
            loadProfileImage(user.getProfilePicturePath());
            JButton btnChangeImage = new JButton("Change Profile Picture");
            styleButton(btnChangeImage);
            btnChangeImage.addActionListener(e -> selectProfileImage());
            panel.add(profileImageLabel);
            panel.add(btnChangeImage);
        } else {
            JLabel adminLabel = new JLabel("Admin Profile - No Image Available");
            adminLabel.setForeground(Color.WHITE);
            panel.add(adminLabel);
        }

        return panel;
    }

    private JPanel createFormPanel() {
        JPanel panel = new JPanel(new GridLayout(4, 2, 10, 10));
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 50, 20, 50));

        txtFullName = createFormField(panel, "Full Name:");
        txtEmail = createFormField(panel, "Email:");
        txtPhone = createFormField(panel, "Phone:");
        txtAddress = createFormField(panel, "Address:");

        return panel;
    }

    private JTextField createFormField(JPanel panel, String label) {
        JLabel lbl = new JLabel(label);
        lbl.setForeground(Color.BLACK);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 14));
        
        JTextField txtField = new JTextField();
        txtField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.WHITE),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        
        panel.add(lbl);
        panel.add(txtField);
        return txtField;
    }

    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        panel.setOpaque(false);

        JButton btnSave = new JButton("Save Changes");
        styleButton(btnSave);
        btnSave.addActionListener(e -> saveProfile());

        JButton btnCancel = new JButton("Cancel");
        styleButton(btnCancel);
        btnCancel.addActionListener(e -> dispose());

        panel.add(btnSave);
        panel.add(btnCancel);
        return panel;
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

    private void styleButton(JButton button) {
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setForeground(Color.WHITE);
        button.setBackground(new Color(62, 133, 185));
        button.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.WHITE, 1),
            BorderFactory.createEmptyBorder(8, 20, 8, 20)
        ));
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }
    
    private boolean checkAuthentication() {
        if (Session.getCurrentUserId() == 0) {
            JOptionPane.showMessageDialog(null, 
                "You need to login first!", 
                "Authentication Required", 
                JOptionPane.WARNING_MESSAGE);
            new UserLogin().setVisible(true);
            dispose();
            return false;
        }
        return true;
    }

    private void showErrorAndRedirect(String message) {
        showError(message);
        dispose();
        new UserLogin().setVisible(true);
    }

    private void loadUserData() {
        User user = Session.getCurrentUser();
        txtFullName.setText(user.getFullName());
        txtEmail.setText(user.getEmail());
        txtPhone.setText(user.getPhone());
        txtAddress.setText(user.getAddress());
    }

    private void selectProfileImage() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter(
            "Image files", "jpg", "jpeg", "png", "gif"));
        
        if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            saveProfileImage(selectedFile);
            loadProfileImage(profileImagePath);
        }
    }

    private void saveProfile() {
        try {
            User user = Session.getCurrentUser();
            user.setFullName(txtFullName.getText());
            user.setEmail(txtEmail.getText());
            user.setPhone(txtPhone.getText());
            user.setAddress(txtAddress.getText());
            
            if (!user.hasAdminAccess() && profileImagePath != null) {
                user.setProfilePicturePath(profileImagePath);
            }

            DatabaseManager.updateUser(user);
            JOptionPane.showMessageDialog(this, "Profile updated successfully!");
            dispose();
        } catch (IllegalArgumentException ex) {
            showError(ex.getMessage());
        } catch (SQLException ex) {
            showError("Database error: " + ex.getMessage());
        }
    }


    private BufferedImage resizeImage(BufferedImage original, int width, int height) {
        BufferedImage resized = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = resized.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g.drawImage(original, 0, 0, width, height, null);
        g.dispose();
        return resized;
    }

    private void loadProfileImage(String imagePath) {
        try {
            ImageIcon icon;
            if (imagePath != null && Files.exists(Paths.get(imagePath))) {
                icon = new ImageIcon(imagePath);
            } else {
                icon = new ImageIcon(getClass().getResource("/Travel_Booking/icons/default_profile.png"));
            }
            
            profileImageLabel.setIcon(new ImageIcon(
                icon.getImage().getScaledInstance(IMAGE_SIZE, IMAGE_SIZE, Image.SCALE_SMOOTH)));
            profileImageLabel.repaint();
        } catch (Exception e) {
            showError("Error loading profile image");
        }
    }

    private void saveProfileImage(File selectedFile) {
        try {
            User user = Session.getCurrentUser();
            user.setFullName(txtFullName.getText());
            user.setEmail(txtEmail.getText());
            user.setPhone(txtPhone.getText());
            user.setAddress(txtAddress.getText());
            
            if (profileImagePath != null) {
                user.setProfilePicturePath(profileImagePath);
            }

            // Update user in database
            DatabaseManager.updateUser(user);
            JOptionPane.showMessageDialog(this, "Profile updated successfully!");
            dispose();
        } catch (IllegalArgumentException ex) {
            showError(ex.getMessage());
        } catch (SQLException ex) {
            showError("Database error: " + ex.getMessage());
        }
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    public void setVisible(boolean b) {
        super.setVisible(b);
    }

}