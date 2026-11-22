
package hostelmanagement.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;

import hostelmanagement.db.DBConnection;

public class PaymentFrame extends JFrame {

    private static final Color BG = new Color(10, 16, 30);
    private static final Color PANEL = new Color(20, 31, 52);
    private static final Color FIELD = new Color(14, 24, 42);
    private static final Color CYAN = new Color(0, 220, 235);
    private static final Color WHITE = new Color(238, 244, 255);
    private static final Color MUTED = new Color(145, 162, 187);
    private static final Color GREEN = new Color(30, 210, 150);
    private static final Color RED = new Color(235, 75, 90);

    private JComboBox<FeeItem> cmbFee;
    private JComboBox<String> cmbPaymentMethod;

    private JTextField txtAmount;
    private JTextField txtPaymentDate;
    private JTextField txtReceiptNumber;

    private JLabel lblTotalAmount;
    private JLabel lblPaidAmount;
    private JLabel lblBalance;

    private JButton btnPay;
    private JButton btnClear;
    private JButton btnRefresh;

    private JTable paymentTable;
    private DefaultTableModel tableModel;

    public PaymentFrame() {

        setTitle("Hostel Management System - Payments");
        setSize(1200, 750);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        getContentPane().setBackground(BG);

        createForm();
        refreshData();
    }

    // =====================================================
    // CREATE FORM
    // =====================================================

    private void createForm() {

        setLayout(new BorderLayout(15, 15));

        JPanel mainPanel = new JPanel(new BorderLayout(15, 15));
        mainPanel.setBackground(BG);
        mainPanel.setBorder(
                BorderFactory.createEmptyBorder(20, 22, 20, 22)
        );

        // TITLE
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(BG);

        JLabel title = new JLabel(
                "PAYMENT MANAGEMENT",
                SwingConstants.LEFT
        );

        title.setFont(new Font("SansSerif", Font.BOLD, 27));
        title.setForeground(CYAN);

        JLabel subtitle = new JLabel(
                "Record student payments and manage fee transactions"
        );

        subtitle.setFont(new Font("SansSerif", Font.PLAIN, 13));
        subtitle.setForeground(MUTED);

        headerPanel.add(title, BorderLayout.NORTH);
        headerPanel.add(subtitle, BorderLayout.CENTER);

        mainPanel.add(headerPanel, BorderLayout.NORTH);

        // CENTER
        JPanel centerPanel = new JPanel(new BorderLayout(15, 15));
        centerPanel.setBackground(BG);

        // FORM CARD
        JPanel formCard = createCard();
        formCard.setLayout(new BorderLayout(10, 12));

        JLabel formTitle = new JLabel("RECORD NEW PAYMENT");
        formTitle.setFont(new Font("SansSerif", Font.BOLD, 17));
        formTitle.setForeground(CYAN);

        formCard.add(formTitle, BorderLayout.NORTH);

        JPanel formPanel = new JPanel(new GridLayout(3, 4, 12, 12));
        formPanel.setBackground(PANEL);

        cmbFee = new JComboBox<>();
        cmbPaymentMethod = new JComboBox<>(
                new String[]{
                    "Cash",
                    "UPI",
                    "Card",
                    "Bank Transfer"
                }
        );

        txtAmount = new JTextField();

        txtPaymentDate = new JTextField(
                LocalDate.now().toString()
        );

        txtReceiptNumber = new JTextField();

        formPanel.add(createFieldGroup("Fee / Student", cmbFee));
        formPanel.add(createFieldGroup("Payment Amount", txtAmount));
        formPanel.add(createFieldGroup("Payment Date (YYYY-MM-DD)", txtPaymentDate));
        formPanel.add(createFieldGroup("Payment Method", cmbPaymentMethod));
        formPanel.add(createFieldGroup("Receipt Number", txtReceiptNumber));

        // FEE INFORMATION
        JPanel infoPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 8));
        infoPanel.setBackground(FIELD);
        infoPanel.setBorder(
                BorderFactory.createLineBorder(new Color(45, 65, 90))
        );

        lblTotalAmount = createInfoLabel("0.00");
        lblPaidAmount = createInfoLabel("0.00");
        lblBalance = createInfoLabel("0.00");

        infoPanel.add(createInfoGroup("TOTAL FEE", lblTotalAmount));
        infoPanel.add(createInfoGroup("PAID AMOUNT", lblPaidAmount));
        infoPanel.add(createInfoGroup("REMAINING BALANCE", lblBalance));

        JPanel formContent = new JPanel(new BorderLayout(10, 10));
        formContent.setBackground(PANEL);
        formContent.add(formPanel, BorderLayout.CENTER);
        formContent.add(infoPanel, BorderLayout.SOUTH);

        formCard.add(formContent, BorderLayout.CENTER);

        centerPanel.add(formCard, BorderLayout.NORTH);

        // PAYMENT HISTORY CARD
        JPanel tableCard = createCard();
        tableCard.setLayout(new BorderLayout(10, 12));

        JLabel tableTitle = new JLabel("PAYMENT HISTORY");
        tableTitle.setFont(new Font("SansSerif", Font.BOLD, 17));
        tableTitle.setForeground(CYAN);

        tableCard.add(tableTitle, BorderLayout.NORTH);

        tableModel = new DefaultTableModel(
                new String[]{
                    "Payment ID",
                    "Enrollment No",
                    "Student",
                    "Fee Type",
                    "Payment Amount",
                    "Payment Date",
                    "Payment Method",
                    "Receipt Number"
                },
                0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        paymentTable = new JTable(tableModel);
        styleTable(paymentTable);

        JScrollPane scrollPane = new JScrollPane(paymentTable);
        scrollPane.getViewport().setBackground(FIELD);
        scrollPane.setBorder(
                BorderFactory.createLineBorder(new Color(45, 65, 90))
        );

        tableCard.add(scrollPane, BorderLayout.CENTER);

        centerPanel.add(tableCard, BorderLayout.CENTER);

        mainPanel.add(centerPanel, BorderLayout.CENTER);

        // BUTTONS
        JPanel buttonPanel = new JPanel(
                new FlowLayout(FlowLayout.RIGHT, 12, 5)
        );
        buttonPanel.setBackground(BG);

        btnPay = createButton("RECORD PAYMENT", GREEN);
        btnClear = createButton("CLEAR", MUTED);
        btnRefresh = createButton("REFRESH", CYAN);

        buttonPanel.add(btnPay);
        buttonPanel.add(btnClear);
        buttonPanel.add(btnRefresh);

        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(mainPanel);

        // ACTIONS
        cmbFee.addActionListener(e -> updateFeeInformation());

        btnPay.addActionListener(e -> recordPayment());

        btnClear.addActionListener(e -> clearFields());

        btnRefresh.addActionListener(e -> refreshData());
    }

    // =====================================================
    // DESIGN HELPERS
    // =====================================================

    private JPanel createCard() {

        JPanel panel = new JPanel();
        panel.setBackground(PANEL);

        panel.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(
                                new Color(40, 65, 95)
                        ),
                        BorderFactory.createEmptyBorder(
                                15, 18, 15, 18
                        )
                )
        );

        return panel;
    }

    private JPanel createFieldGroup(String text, java.awt.Component field) {

        JPanel panel = new JPanel(new BorderLayout(5, 7));
        panel.setBackground(PANEL);

        JLabel label = new JLabel(text);
        label.setForeground(MUTED);
        label.setFont(new Font("SansSerif", Font.BOLD, 12));

        panel.add(label, BorderLayout.NORTH);
        panel.add(field, BorderLayout.CENTER);

        if (field instanceof JTextField) {
            JTextField input = (JTextField) field;
            styleTextField(input);
        }

        if (field instanceof JComboBox) {
            @SuppressWarnings("rawtypes")
            JComboBox combo = (JComboBox) field;
            combo.setBackground(FIELD);
            combo.setForeground(WHITE);
            combo.setFont(new Font("SansSerif", Font.PLAIN, 13));
            combo.setPreferredSize(new Dimension(180, 36));
        }

        return panel;
    }

    private void styleTextField(JTextField field) {

        field.setBackground(FIELD);
        field.setForeground(WHITE);
        field.setCaretColor(CYAN);
        field.setFont(new Font("SansSerif", Font.PLAIN, 13));
        field.setPreferredSize(new Dimension(180, 36));

        field.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(
                                new Color(55, 80, 110)
                        ),
                        BorderFactory.createEmptyBorder(7, 9, 7, 9)
                )
        );
    }

    private JLabel createInfoLabel(String value) {

        JLabel label = new JLabel(value);
        label.setForeground(CYAN);
        label.setFont(new Font("SansSerif", Font.BOLD, 15));

        return label;
    }

    private JPanel createInfoGroup(String heading, JLabel value) {

        JPanel panel = new JPanel(new GridLayout(2, 1, 3, 3));
        panel.setBackground(FIELD);

        JLabel title = new JLabel(heading);
        title.setForeground(MUTED);
        title.setFont(new Font("SansSerif", Font.BOLD, 10));

        panel.add(title);
        panel.add(value);

        return panel;
    }

    private JButton createButton(String text, Color color) {

        JButton button = new JButton(text);

        button.setFont(new Font("SansSerif", Font.BOLD, 12));
        button.setForeground(BG);
        button.setBackground(color);
        button.setFocusPainted(false);
        button.setBorder(
                BorderFactory.createEmptyBorder(11, 18, 11, 18)
        );
        button.setCursor(
                new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR)
        );

        return button;
    }

    private void styleTable(JTable table) {

        table.setBackground(FIELD);
        table.setForeground(WHITE);
        table.setGridColor(new Color(40, 55, 78));
        table.setSelectionBackground(new Color(0, 100, 125));
        table.setSelectionForeground(WHITE);
        table.setFont(new Font("SansSerif", Font.PLAIN, 12));
        table.setRowHeight(30);
        table.setShowVerticalLines(false);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

        JTableHeader header = table.getTableHeader();
        header.setBackground(new Color(24, 43, 68));
        header.setForeground(CYAN);
        header.setFont(new Font("SansSerif", Font.BOLD, 12));
        header.setPreferredSize(new Dimension(100, 36));
        header.setReorderingAllowed(false);
    }

    // =====================================================
    // LOAD FEES
    // =====================================================

    private void loadFees() {

        cmbFee.removeAllItems();

        String sql =
                "SELECT "
                + "f.fee_id, "
                + "s.enrollment_no, "
                + "s.full_name, "
                + "f.fee_type, "
                + "f.amount, "
                + "f.paid_amount, "
                + "(f.amount - f.paid_amount) AS balance, "
                + "f.due_date "
                + "FROM fees f "
                + "INNER JOIN students s "
                + "ON f.student_id = s.student_id "
                + "WHERE f.amount > f.paid_amount "
                + "AND f.payment_status IN "
                + "('PENDING', 'PARTIAL') "
                + "ORDER BY "
                + "f.due_date ASC, "
                + "f.fee_id ASC";

        try (
                Connection con = DBConnection.getConnection();
                PreparedStatement pst = con.prepareStatement(sql);
                ResultSet rs = pst.executeQuery()
        ) {

            while (rs.next()) {

                cmbFee.addItem(
                        new FeeItem(
                                rs.getInt("fee_id"),
                                rs.getString("enrollment_no"),
                                rs.getString("full_name"),
                                rs.getString("fee_type"),
                                rs.getBigDecimal("amount"),
                                rs.getBigDecimal("paid_amount"),
                                rs.getBigDecimal("balance")
                        )
                );
            }

        } catch (Exception e) {

            showError(
                    "Unable to load pending fees:\n" + e.getMessage()
            );
        }

        updateFeeInformation();
    }

    // =====================================================
    // LOAD PAYMENT HISTORY
    // =====================================================

    private void loadPaymentHistory() {

        tableModel.setRowCount(0);

        String sql =
                "SELECT "
                + "p.payment_id, "
                + "s.enrollment_no, "
                + "s.full_name, "
                + "f.fee_type, "
                + "p.payment_amount, "
                + "p.payment_date, "
                + "p.payment_method, "
                + "p.receipt_number "
                + "FROM payment_history p "
                + "INNER JOIN fees f "
                + "ON p.fee_id = f.fee_id "
                + "INNER JOIN students s "
                + "ON f.student_id = s.student_id "
                + "ORDER BY "
                + "p.payment_id DESC";

        try (
                Connection con = DBConnection.getConnection();
                PreparedStatement pst = con.prepareStatement(sql);
                ResultSet rs = pst.executeQuery()
        ) {

            while (rs.next()) {

                tableModel.addRow(
                        new Object[]{
                            rs.getInt("payment_id"),
                            rs.getString("enrollment_no"),
                            rs.getString("full_name"),
                            rs.getString("fee_type"),
                            rs.getBigDecimal("payment_amount"),
                            rs.getDate("payment_date"),
                            rs.getString("payment_method"),
                            rs.getString("receipt_number")
                        }
                );
            }

        } catch (Exception e) {

            showError(
                    "Unable to load payment history:\n" + e.getMessage()
            );
        }
    }

    // =====================================================
    // UPDATE FEE INFORMATION
    // =====================================================

    private void updateFeeInformation() {

        FeeItem fee = (FeeItem) cmbFee.getSelectedItem();

        if (fee == null) {

            lblTotalAmount.setText("0.00");
            lblPaidAmount.setText("0.00");
            lblBalance.setText("0.00");

            return;
        }

        lblTotalAmount.setText(fee.amount.toString());
        lblPaidAmount.setText(fee.paidAmount.toString());
        lblBalance.setText(fee.balance.toString());
    }

    // =====================================================
    // RECORD PAYMENT
    // =====================================================

    private void recordPayment() {

        FeeItem selectedFee =
                (FeeItem) cmbFee.getSelectedItem();

        if (selectedFee == null) {

            showWarning(
                    "There are no pending or partial fees available for payment."
            );

            return;
        }

        String amountText = txtAmount.getText().trim();

        if (amountText.isEmpty()) {

            showWarning("Please enter payment amount.");
            return;
        }

        BigDecimal paymentAmount;

        try {

            paymentAmount = new BigDecimal(amountText);

        } catch (NumberFormatException e) {

            showWarning("Payment amount must be a valid number.");
            return;
        }

        if (paymentAmount.compareTo(BigDecimal.ZERO) <= 0) {

            showWarning("Payment amount must be greater than zero.");
            return;
        }

        if (paymentAmount.scale() > 2) {

            showWarning(
                    "Payment amount can have maximum two decimal places."
            );

            return;
        }

        if (txtPaymentDate.getText().trim().isEmpty()) {

            showWarning("Please enter payment date.");
            return;
        }

        LocalDate paymentLocalDate;

        try {

            paymentLocalDate = LocalDate.parse(
                    txtPaymentDate.getText().trim()
            );

        } catch (DateTimeParseException e) {

            showWarning("Payment date must use YYYY-MM-DD format.");
            return;
        }

        if (paymentLocalDate.isAfter(LocalDate.now())) {

            showWarning("Payment date cannot be in the future.");
            return;
        }

        String receiptNumber = txtReceiptNumber.getText().trim();

        if (receiptNumber.isEmpty()) {

            receiptNumber = generateReceiptNumber();
            txtReceiptNumber.setText(receiptNumber);
        }

        if (!receiptNumber.matches("[A-Za-z0-9-]{3,50}")) {

            showWarning(
                    "Receipt number may contain only letters, numbers and '-'."
            );

            return;
        }

        if (receiptExists(receiptNumber)) {

            showWarning("This receipt number already exists.");
            return;
        }

        String paymentMethod =
                cmbPaymentMethod.getSelectedItem().toString();

        Date paymentDate = Date.valueOf(paymentLocalDate);

        // TRANSACTION

        try (Connection con = DBConnection.getConnection()) {

            con.setAutoCommit(false);

            try {

                String feeSql =
                        "SELECT amount, paid_amount, payment_status "
                        + "FROM fees WHERE fee_id=? FOR UPDATE";

                BigDecimal totalAmount;
                BigDecimal currentPaid;
                String currentStatus;

                try (
                        PreparedStatement pst = con.prepareStatement(feeSql)
                ) {

                    pst.setInt(1, selectedFee.feeId);

                    try (ResultSet rs = pst.executeQuery()) {

                        if (!rs.next()) {

                            con.rollback();
                            showWarning("Selected fee does not exist.");
                            return;
                        }

                        totalAmount = rs.getBigDecimal("amount");
                        currentPaid = rs.getBigDecimal("paid_amount");
                        currentStatus = rs.getString("payment_status");
                    }
                }

                if (currentPaid.compareTo(totalAmount) >= 0) {

                    con.rollback();
                    showWarning("This fee is already fully paid.");
                    return;
                }

                if (currentStatus.equalsIgnoreCase("PAID")) {

                    con.rollback();
                    showWarning("This fee is already marked PAID.");
                    return;
                }

                BigDecimal balance =
                        totalAmount.subtract(currentPaid);

                if (paymentAmount.compareTo(balance) > 0) {

                    con.rollback();

                    showWarning(
                            "Payment exceeds the remaining balance.\n\n"
                            + "Remaining balance: " + balance
                    );

                    return;
                }

                BigDecimal newPaid = currentPaid.add(paymentAmount);

                BigDecimal newBalance =
                        totalAmount.subtract(newPaid);

                String newStatus;

                if (newBalance.compareTo(BigDecimal.ZERO) == 0) {
                    newStatus = "PAID";
                } else {
                    newStatus = "PARTIAL";
                }

                // INSERT PAYMENT HISTORY

                String insertPayment =
                        "INSERT INTO payment_history "
                        + "(fee_id, payment_amount, payment_date, "
                        + "payment_method, receipt_number) "
                        + "VALUES (?, ?, ?, ?, ?)";

                try (
                        PreparedStatement pst =
                                con.prepareStatement(insertPayment)
                ) {

                    pst.setInt(1, selectedFee.feeId);
                    pst.setBigDecimal(2, paymentAmount);
                    pst.setDate(3, paymentDate);
                    pst.setString(4, paymentMethod);
                    pst.setString(5, receiptNumber);

                    pst.executeUpdate();
                }

                // UPDATE FEE

                String updateFee =
                        "UPDATE fees SET "
                        + "paid_amount=?, "
                        + "payment_date=?, "
                        + "payment_method=?, "
                        + "payment_status=?, "
                        + "receipt_number=? "
                        + "WHERE fee_id=?";

                try (
                        PreparedStatement pst =
                                con.prepareStatement(updateFee)
                ) {

                    pst.setBigDecimal(1, newPaid);
                    pst.setDate(2, paymentDate);
                    pst.setString(3, paymentMethod);
                    pst.setString(4, newStatus);
                    pst.setString(5, receiptNumber);
                    pst.setInt(6, selectedFee.feeId);

                    pst.executeUpdate();
                }

                con.commit();

                String message;

                if (newStatus.equals("PAID")) {

                    message =
                            "Payment recorded successfully!\n\n"
                            + "Fee Status: PAID\n"
                            + "Balance: 0.00";

                } else {

                    message =
                            "Payment recorded successfully!\n\n"
                            + "Fee Status: PARTIAL\n"
                            + "Remaining Balance: " + newBalance;
                }

                JOptionPane.showMessageDialog(
                        this,
                        message,
                        "Payment Successful",
                        JOptionPane.INFORMATION_MESSAGE
                );

                clearFields();
                refreshData();

            } catch (Exception e) {

                con.rollback();

                showError(
                        "Payment transaction failed:\n" + e.getMessage()
                );
            }

        } catch (Exception e) {

            showError("Database error:\n" + e.getMessage());
        }
    }

    // =====================================================
    // RECEIPT DUPLICATE CHECK
    // =====================================================

    private boolean receiptExists(String receiptNumber) {

        String sql =
                "SELECT payment_id FROM payment_history "
                + "WHERE receipt_number=? LIMIT 1";

        try (
                Connection con = DBConnection.getConnection();
                PreparedStatement pst = con.prepareStatement(sql)
        ) {

            pst.setString(1, receiptNumber);

            try (ResultSet rs = pst.executeQuery()) {
                return rs.next();
            }

        } catch (Exception e) {

            showError(
                    "Unable to check receipt number:\n" + e.getMessage()
            );

            return true;
        }
    }

    // =====================================================
    // GENERATE RECEIPT NUMBER
    // =====================================================

    private String generateReceiptNumber() {

        return "RCP-" + System.currentTimeMillis();
    }

    // =====================================================
    // CLEAR
    // =====================================================

    private void clearFields() {

        txtAmount.setText("");
        txtPaymentDate.setText(LocalDate.now().toString());
        txtReceiptNumber.setText("");

        cmbPaymentMethod.setSelectedIndex(0);

        updateFeeInformation();
    }

    // =====================================================
    // REFRESH
    // =====================================================

    private void refreshData() {

        loadFees();
        loadPaymentHistory();
        clearFields();
    }

    // =====================================================
    // WARNING
    // =====================================================

    private void showWarning(String message) {

        JOptionPane.showMessageDialog(
                this,
                message,
                "Validation",
                JOptionPane.WARNING_MESSAGE
        );
    }

    // =====================================================
    // ERROR
    // =====================================================

    private void showError(String message) {

        JOptionPane.showMessageDialog(
                this,
                message,
                "Payment Management Error",
                JOptionPane.ERROR_MESSAGE
        );
    }

    // =====================================================
    // FEE ITEM
    // =====================================================

    private static class FeeItem {

        int feeId;
        String enrollmentNo;
        String studentName;
        String feeType;
        BigDecimal amount;
        BigDecimal paidAmount;
        BigDecimal balance;

        FeeItem(
                int feeId,
                String enrollmentNo,
                String studentName,
                String feeType,
                BigDecimal amount,
                BigDecimal paidAmount,
                BigDecimal balance
        ) {

            this.feeId = feeId;
            this.enrollmentNo = enrollmentNo;
            this.studentName = studentName;
            this.feeType = feeType;
            this.amount = amount;
            this.paidAmount = paidAmount;
            this.balance = balance;
        }

        @Override
        public String toString() {

            return enrollmentNo
                    + " - "
                    + studentName
                    + " - "
                    + feeType
                    + " - Balance: "
                    + balance;
        }
    }

    // =====================================================
    // MAIN
    // =====================================================

    public static void main(String[] args) {

        java.awt.EventQueue.invokeLater(() -> {

            new PaymentFrame().setVisible(true);
        });
    }
}
