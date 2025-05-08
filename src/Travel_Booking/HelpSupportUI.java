package Travel_Booking;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import javax.swing.border.EmptyBorder;

public class HelpSupportUI extends JFrame {
    
    public HelpSupportUI() {
        initializeUI();
    }

    private void initializeUI() {
        setTitle("Help & Support");
        setSize(1000, 600);
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
        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        // Header
        JPanel headerPanel = createHeaderPanel();
        mainPanel.add(headerPanel, BorderLayout.NORTH);

        // Content
        JTabbedPane tabbedPane = createContentTabs();
        mainPanel.add(tabbedPane, BorderLayout.CENTER);

        setContentPane(mainPanel);
    }

    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);
        headerPanel.setBorder(new EmptyBorder(0, 0, 20, 0));

        // Back button
        JButton btnBack = new JButton("← Back to Dashboard");
        styleButton(btnBack);
        btnBack.addActionListener(e -> {
            new UserDashboard().setVisible(true);
            dispose();
        });
        headerPanel.add(btnBack, BorderLayout.WEST);

        // Title
        JLabel titleLabel = new JLabel("Help & Support");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        headerPanel.add(titleLabel, BorderLayout.CENTER);

        return headerPanel;
    }

    private JTabbedPane createContentTabs() {
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setOpaque(false);
        tabbedPane.setBackground(new Color(255, 255, 255, 30));
        tabbedPane.setForeground(new Color(62, 133, 185));
        tabbedPane.setFont(new Font("Segoe UI", Font.BOLD, 14));

        // Contact Tab
        tabbedPane.addTab("Contact Support", createContactPanel());
        // FAQ Tab
        tabbedPane.addTab("FAQs", createFAQPanel());
        // Live Chat Tab
        tabbedPane.addTab("Live Chat", createLiveChatPanel());

        return tabbedPane;
    }

    private JPanel createContactPanel() {
        JPanel panel = new JPanel(new GridLayout(0, 1, 10, 10));
        panel.setOpaque(false);
        panel.setBorder(new EmptyBorder(20, 50, 20, 50));

        addSection(panel, "24/7 Support", 
            "Phone: +1 (800) 555-0199\n"
            + "Email: support@travelease.com\n"
            + "Emergency: +1 (800) 555-0175");

        addSection(panel, "Office Hours", 
            "Monday-Friday: 8:00 AM - 8:00 PM (GMT)\n"
            + "Saturday: 9:00 AM - 5:00 PM\n"
            + "Sunday: Closed");

        JButton btnSupportForm = new JButton("Open Support Form");
        styleButton(btnSupportForm);
        btnSupportForm.addActionListener(e -> showSupportForm());
        panel.add(btnSupportForm);

        return panel;
    }

    private JPanel createFAQPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);
        
        String[] faqs = {
            "Q: How do I cancel a booking?",
            "A: Go to Booking History and select the booking to cancel.",
            "",
            "Q: What payment methods are accepted?",
            "A: We accept credit cards, PayPal, and bank transfers.",
            "",
            "Q: How long does refund processing take?",
            "A: Refunds typically process within 5-7 business days."
        };

        JTextArea faqArea = new JTextArea(String.join("\n", faqs));
        faqArea.setEditable(false);
        faqArea.setForeground(new Color(62, 133, 185));
        faqArea.setBackground(new Color(0, 0, 0, 50));
        faqArea.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        faqArea.setBorder(new EmptyBorder(20, 20, 20, 20));

        panel.add(new JScrollPane(faqArea), BorderLayout.CENTER);
        return panel;
    }

    private JPanel createLiveChatPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));

        JTextArea chatArea = new JTextArea();
        chatArea.setEditable(false);
        chatArea.setForeground(Color.WHITE);
        chatArea.setBackground(new Color(0, 0, 0, 50));
        
        JTextField inputField = new JTextField();
        styleTextField(inputField);
        
        JButton btnSend = new JButton("Send");
        styleButton(btnSend);
        
        JPanel inputPanel = new JPanel(new BorderLayout());
        inputPanel.setOpaque(false);
        inputPanel.add(inputField, BorderLayout.CENTER);
        inputPanel.add(btnSend, BorderLayout.EAST);

        panel.add(new JScrollPane(chatArea), BorderLayout.CENTER);
        panel.add(inputPanel, BorderLayout.SOUTH);

        return panel;
    }

    private void addSection(JPanel parent, String title, String content) {
        JPanel section = new JPanel(new GridLayout(0, 1));
        section.setOpaque(false);
        
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        titleLabel.setForeground(new Color(255, 255, 255, 200));
        
        JTextArea contentArea = new JTextArea(content);
        contentArea.setEditable(false);
        contentArea.setLineWrap(true);
        contentArea.setWrapStyleWord(true);
        contentArea.setForeground(Color.WHITE);
        contentArea.setBackground(new Color(0, 0, 0, 50));
        contentArea.setBorder(new EmptyBorder(5, 10, 15, 10));

        section.add(titleLabel);
        section.add(contentArea);
        parent.add(section);
    }

    private void showSupportForm() {
        JDialog formDialog = new JDialog(this, "Support Request", true);
        formDialog.setSize(500, 400);
        formDialog.setLocationRelativeTo(this);

        JPanel mainPanel = new JPanel(new GridLayout(0, 1, 10, 10));
        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        mainPanel.setBackground(new Color(240, 240, 240));

        JComboBox<String> issueType = new JComboBox<>(new String[]{
            "Booking Issues", "Payment Problems", "Account Help", "Other"
        });

        JTextArea description = new JTextArea(5, 20);
        description.setLineWrap(true);
        JScrollPane scrollPane = new JScrollPane(description);

        JButton btnSubmit = new JButton("Submit Request");
        btnSubmit.addActionListener(e -> {
            if (description.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(formDialog, 
                    "Please enter a description", 
                    "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            JOptionPane.showMessageDialog(formDialog, 
                "Support request submitted successfully!", 
                "Success", JOptionPane.INFORMATION_MESSAGE);
            formDialog.dispose();
        });

        mainPanel.add(new JLabel("Issue Type:"));
        mainPanel.add(issueType);
        mainPanel.add(new JLabel("Description:"));
        mainPanel.add(scrollPane);
        mainPanel.add(btnSubmit);

        formDialog.add(mainPanel);
        formDialog.setVisible(true);
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

    private void styleTextField(JTextField field) {
        field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.WHITE, 1),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
    }

    public void setVisible(boolean b) {
        super.setVisible(b);
    }
}