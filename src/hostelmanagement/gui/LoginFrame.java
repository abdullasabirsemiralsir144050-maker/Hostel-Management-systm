
package hostelmanagement.gui;

import java.awt.*;
import java.awt.event.*;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Base64;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

import javax.swing.*;
import javax.swing.border.*;

import hostelmanagement.db.DBConnection;

public class LoginFrame extends JFrame {

    // ================= UI COMPONENTS =================

    private JTextField txtUsername;
    private JPasswordField txtPassword;
    private JButton btnLogin;
    private JCheckBox chkShowPassword;

    // ================= SECURITY =================

    private int failedAttempts = 0;
    private long lockUntil = 0;

    private static final int MAX_ATTEMPTS = 5;
    private static final long LOCK_TIME = 60_000L;

    private static final int PBKDF2_ITERATIONS = 310_000;
    private static final int SALT_LENGTH = 16;
    private static final int HASH_LENGTH = 32;

    // ================= THEME =================

    private static final Color BG =
            new Color(10, 16, 30);

    private static final Color PANEL =
            new Color(20, 31, 52);

    private static final Color FIELD =
            new Color(14, 24, 42);

    private static final Color CYAN =
            new Color(0, 220, 235);

    private static final Color WHITE =
            new Color(238, 244, 255);

    private static final Color MUTED =
            new Color(145, 162, 187);

    private static final Color GREEN =
            new Color(30, 210, 150);

    // ================= CONSTRUCTOR =================

    public LoginFrame() {

        setTitle("Hostel Management System - Login");

        setSize(1000, 620);
        setMinimumSize(new Dimension(850, 550));

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        getContentPane().setBackground(BG);

        createLoginForm();
    }

    // ================= LOGIN DESIGN =================

    private void createLoginForm() {

        JPanel main = new JPanel(new GridLayout(1, 2));
        main.setBackground(BG);

        // LEFT BRANDING PANEL

        JPanel leftPanel = new JPanel();

        leftPanel.setLayout(
                new BoxLayout(leftPanel, BoxLayout.Y_AXIS)
        );

        leftPanel.setBackground(BG);

        leftPanel.setBorder(
                new EmptyBorder(70, 55, 50, 40)
        );

        JLabel brand = new JLabel("HOSTEL");
        brand.setForeground(CYAN);
        brand.setFont(
                new Font("SansSerif", Font.BOLD, 38)
        );

        JLabel brand2 = new JLabel("MANAGEMENT");
        brand2.setForeground(WHITE);
        brand2.setFont(
                new Font("SansSerif", Font.BOLD, 28)
        );

        JLabel brand3 = new JLabel("SYSTEM");
        brand3.setForeground(WHITE);
        brand3.setFont(
                new Font("SansSerif", Font.BOLD, 28)
        );

        JLabel description = new JLabel(
                "<html>Smart hostel management.<br>"
                + "Secure access to your administration<br>"
                + "dashboard and management tools.</html>"
        );

        description.setForeground(MUTED);
        description.setFont(
                new Font("SansSerif", Font.PLAIN, 14)
        );

        JLabel status = new JLabel("●  SYSTEM ONLINE");
        status.setForeground(GREEN);
        status.setFont(
                new Font("SansSerif", Font.BOLD, 12)
        );

        leftPanel.add(brand);
        leftPanel.add(Box.createVerticalStrut(5));
        leftPanel.add(brand2);
        leftPanel.add(brand3);
        leftPanel.add(Box.createVerticalStrut(25));
        leftPanel.add(description);
        leftPanel.add(Box.createVerticalStrut(45));
        leftPanel.add(status);

        // RIGHT LOGIN PANEL

        JPanel rightPanel = new JPanel(new GridBagLayout());

        rightPanel.setBackground(BG);

        rightPanel.setBorder(
                new EmptyBorder(35, 25, 35, 45)
        );

        JPanel card = new JPanel();

        card.setLayout(
                new BoxLayout(card, BoxLayout.Y_AXIS)
        );

        card.setBackground(PANEL);

        card.setBorder(
                new CompoundBorder(
                        new LineBorder(
                                new Color(40, 65, 90),
                                1,
                                true
                        ),
                        new EmptyBorder(35, 35, 35, 35)
                )
        );

        card.setPreferredSize(
                new Dimension(390, 440)
        );

        // HEADING

        JLabel heading = new JLabel("Welcome Back");

        heading.setForeground(WHITE);

        heading.setFont(
                new Font("SansSerif", Font.BOLD, 27)
        );

        heading.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel subtitle = new JLabel(
                "Sign in to continue to your dashboard"
        );

        subtitle.setForeground(MUTED);

        subtitle.setFont(
                new Font("SansSerif", Font.PLAIN, 12)
        );

        subtitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        // USERNAME

        JLabel usernameLabel = new JLabel("USERNAME");

        styleLabel(usernameLabel);

        txtUsername = new JTextField();

        styleInput(txtUsername);

        // PASSWORD

        JLabel passwordLabel = new JLabel("PASSWORD");

        styleLabel(passwordLabel);

        txtPassword = new JPasswordField();

        styleInput(txtPassword);

        // SHOW PASSWORD

        chkShowPassword = new JCheckBox("Show password");

        chkShowPassword.setForeground(MUTED);
        chkShowPassword.setBackground(PANEL);
        chkShowPassword.setFocusPainted(false);

        chkShowPassword.setFont(
                new Font("SansSerif", Font.PLAIN, 11)
        );

        chkShowPassword.setAlignmentX(
                Component.LEFT_ALIGNMENT
        );

        char defaultEcho = txtPassword.getEchoChar();

        chkShowPassword.addActionListener(e -> {

            if (chkShowPassword.isSelected()) {
                txtPassword.setEchoChar((char) 0);
            } else {
                txtPassword.setEchoChar(defaultEcho);
            }
        });

        // LOGIN BUTTON

        btnLogin = new JButton("SIGN IN");

        btnLogin.setFont(
                new Font("SansSerif", Font.BOLD, 14)
        );

        btnLogin.setForeground(BG);
        btnLogin.setBackground(CYAN);

        btnLogin.setFocusPainted(false);
        btnLogin.setBorderPainted(false);

        btnLogin.setCursor(
                new Cursor(Cursor.HAND_CURSOR)
        );

        btnLogin.setAlignmentX(
                Component.LEFT_ALIGNMENT
        );

        btnLogin.setMaximumSize(
                new Dimension(Integer.MAX_VALUE, 48)
        );

        btnLogin.addMouseListener(new MouseAdapter() {

            @Override
            public void mouseEntered(MouseEvent e) {
                btnLogin.setBackground(
                        new Color(80, 240, 245)
                );
            }

            @Override
            public void mouseExited(MouseEvent e) {
                btnLogin.setBackground(CYAN);
            }
        });

        btnLogin.addActionListener(e -> loginUser());

        // PRESS ENTER TO LOGIN

        txtPassword.addActionListener(e -> loginUser());

        // FOOTER

        JLabel footer = new JLabel(
                "SECURE ADMINISTRATOR ACCESS"
        );

        footer.setForeground(
                new Color(100, 120, 148)
        );

        footer.setFont(
                new Font("SansSerif", Font.BOLD, 9)
        );

        footer.setAlignmentX(Component.LEFT_ALIGNMENT);

        // ADD COMPONENTS

        card.add(heading);
        card.add(Box.createVerticalStrut(8));
        card.add(subtitle);

        card.add(Box.createVerticalStrut(35));

        card.add(usernameLabel);
        card.add(Box.createVerticalStrut(9));
        card.add(txtUsername);

        card.add(Box.createVerticalStrut(22));

        card.add(passwordLabel);
        card.add(Box.createVerticalStrut(9));
        card.add(txtPassword);

        card.add(Box.createVerticalStrut(10));
        card.add(chkShowPassword);

        card.add(Box.createVerticalStrut(28));
        card.add(btnLogin);

        card.add(Box.createVerticalStrut(30));
        card.add(footer);

        rightPanel.add(card);

        main.add(leftPanel);
        main.add(rightPanel);

        add(main);
    }

    // ================= LABEL STYLE =================

    private void styleLabel(JLabel label) {

        label.setForeground(CYAN);

        label.setFont(
                new Font("SansSerif", Font.BOLD, 11)
        );

        label.setAlignmentX(Component.LEFT_ALIGNMENT);
    }

    // ================= INPUT STYLE =================

    private void styleInput(JTextField field) {

        field.setFont(
                new Font("SansSerif", Font.PLAIN, 14)
        );

        field.setForeground(WHITE);
        field.setBackground(FIELD);
        field.setCaretColor(CYAN);

        field.setBorder(
                new CompoundBorder(
                        new LineBorder(
                                new Color(55, 80, 105),
                                1,
                                true
                        ),
                        new EmptyBorder(12, 12, 12, 12)
                )
        );

        field.setMaximumSize(
                new Dimension(Integer.MAX_VALUE, 45)
        );

        field.setAlignmentX(Component.LEFT_ALIGNMENT);
    }

    // ================= LOGIN AUTHENTICATION =================

    private void loginUser() {

        long currentTime = System.currentTimeMillis();

        // LOGIN LOCK

        if (currentTime < lockUntil) {

            long remaining =
                    (lockUntil - currentTime) / 1000;

            JOptionPane.showMessageDialog(
                    this,
                    "Too many failed login attempts.\n\n"
                    + "Please wait " + remaining + " seconds.",
                    "Login Locked",
                    JOptionPane.WARNING_MESSAGE
            );

            return;
        }

        String username =
                txtUsername.getText().trim();

        char[] password =
                txtPassword.getPassword();

        if (username.isEmpty() || password.length == 0) {

            JOptionPane.showMessageDialog(
                    this,
                    "Please enter username and password.",
                    "Login Error",
                    JOptionPane.WARNING_MESSAGE
            );

            clearPassword(password);

            return;
        }

        try {

            String sql =
                    "SELECT username, role, password "
                    + "FROM users "
                    + "WHERE username=?";

            try (
                    Connection con =
                            DBConnection.getConnection();

                    PreparedStatement pst =
                            con.prepareStatement(sql)
            ) {

                pst.setString(1, username);

                try (ResultSet rs = pst.executeQuery()) {

                    if (!rs.next()) {

                        loginFailed();
                        clearPassword(password);

                        return;
                    }

                    String storedPassword =
                            rs.getString("password");

                    boolean validPassword =
                            verifyPassword(
                                    password,
                                    storedPassword
                            );

                    if (!validPassword) {

                        loginFailed();
                        clearPassword(password);

                        return;
                    }

                    // AUTOMATIC PASSWORD UPGRADE

                    if (isLegacySha256(storedPassword)) {

                        String newHash =
                                hashPassword(password);

                        updatePassword(
                                con,
                                username,
                                newHash
                        );
                    }

                    failedAttempts = 0;
                    lockUntil = 0;

                    clearPassword(password);

                    JOptionPane.showMessageDialog(
                            this,
                            "Login successful!",
                            "Success",
                            JOptionPane.INFORMATION_MESSAGE
                    );

                    dispose();

                    new DashboardFrame().setVisible(true);
                }
            }

        } catch (Exception e) {

            clearPassword(password);

            JOptionPane.showMessageDialog(
                    this,
                    "Login error:\n" + e.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE
            );

            e.printStackTrace();
        }
    }

    // ================= VERIFY PASSWORD =================

    private boolean verifyPassword(
            char[] password,
            String storedPassword
    ) throws Exception {

        if (storedPassword == null
                || storedPassword.isEmpty()) {

            return false;
        }

        // PBKDF2 FORMAT

        if (storedPassword.startsWith("PBKDF2$")) {

            return verifyPbkdf2Password(
                    password,
                    storedPassword
            );
        }

        // LEGACY SHA-256

        if (isLegacySha256(storedPassword)) {

            String input = new String(password);

            String sha256 = sha256(input);

            return MessageDigest.isEqual(
                    sha256.getBytes(StandardCharsets.UTF_8),
                    storedPassword.getBytes(StandardCharsets.UTF_8)
            );
        }

        return false;
    }

    // ================= CHECK LEGACY SHA-256 =================

    private boolean isLegacySha256(String value) {

        return value != null
                && value.matches("[0-9a-fA-F]{64}");
    }

    // ================= SHA-256 =================

    private String sha256(String password)
            throws Exception {

        MessageDigest md =
                MessageDigest.getInstance("SHA-256");

        byte[] hash =
                md.digest(
                        password.getBytes(StandardCharsets.UTF_8)
                );

        StringBuilder result = new StringBuilder();

        for (byte b : hash) {

            result.append(
                    String.format("%02x", b)
            );
        }

        return result.toString();
    }

    // ================= PBKDF2 HASH =================

    private String hashPassword(char[] password)
            throws Exception {

        SecureRandom random = new SecureRandom();

        byte[] salt = new byte[SALT_LENGTH];

        random.nextBytes(salt);

        PBEKeySpec spec = new PBEKeySpec(
                password,
                salt,
                PBKDF2_ITERATIONS,
                HASH_LENGTH * 8
        );

        try {

            SecretKeyFactory factory =
                    SecretKeyFactory.getInstance(
                            "PBKDF2WithHmacSHA256"
                    );

            byte[] hash =
                    factory.generateSecret(spec).getEncoded();

            return "PBKDF2$"
                    + PBKDF2_ITERATIONS
                    + "$"
                    + Base64.getEncoder().encodeToString(salt)
                    + "$"
                    + Base64.getEncoder().encodeToString(hash);

        } finally {
            spec.clearPassword();
        }
    }

    // ================= VERIFY PBKDF2 =================

    private boolean verifyPbkdf2Password(
            char[] password,
            String storedPassword
    ) throws Exception {

        String[] parts = storedPassword.split("\\$");

        if (parts.length != 4) {
            return false;
        }

        if (!parts[0].equals("PBKDF2")) {
            return false;
        }

        int iterations = Integer.parseInt(parts[1]);

        byte[] salt =
                Base64.getDecoder().decode(parts[2]);

        byte[] storedHash =
                Base64.getDecoder().decode(parts[3]);

        PBEKeySpec spec = new PBEKeySpec(
                password,
                salt,
                iterations,
                storedHash.length * 8
        );

        try {

            SecretKeyFactory factory =
                    SecretKeyFactory.getInstance(
                            "PBKDF2WithHmacSHA256"
                    );

            byte[] calculatedHash =
                    factory.generateSecret(spec).getEncoded();

            return MessageDigest.isEqual(
                    calculatedHash,
                    storedHash
            );

        } finally {
            spec.clearPassword();
        }
    }

    // ================= UPDATE PASSWORD AFTER MIGRATION =================

    private void updatePassword(
            Connection con,
            String username,
            String newPasswordHash
    ) throws Exception {

        String sql =
                "UPDATE users "
                + "SET password=? "
                + "WHERE username=?";

        try (PreparedStatement pst =
                con.prepareStatement(sql)) {

            pst.setString(1, newPasswordHash);
            pst.setString(2, username);

            pst.executeUpdate();
        }
    }

    // ================= FAILED LOGIN =================

    private void loginFailed() {

        failedAttempts++;

        txtPassword.setText("");

        if (failedAttempts >= MAX_ATTEMPTS) {

            lockUntil =
                    System.currentTimeMillis() + LOCK_TIME;

            failedAttempts = 0;

            JOptionPane.showMessageDialog(
                    this,
                    "Too many failed attempts.\n\n"
                    + "Login is locked for 60 seconds.",
                    "Login Locked",
                    JOptionPane.ERROR_MESSAGE
            );

        } else {

            int remaining =
                    MAX_ATTEMPTS - failedAttempts;

            JOptionPane.showMessageDialog(
                    this,
                    "Invalid username or password.\n\n"
                    + "Remaining attempts: " + remaining,
                    "Login Failed",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    // ================= CLEAR PASSWORD =================

    private void clearPassword(char[] password) {

        if (password != null) {

            java.util.Arrays.fill(password, '\0');
        }
    }

    // ================= MAIN =================

    public static void main(String[] args) {

        java.awt.EventQueue.invokeLater(() -> {

            new LoginFrame().setVisible(true);

        });
    }
}
