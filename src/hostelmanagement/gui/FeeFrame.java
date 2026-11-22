package hostelmanagement.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import hostelmanagement.db.DBConnection;

public class FeeFrame extends JFrame {

    private static final Color BG = new Color(10, 16, 30);
    private static final Color PANEL = new Color(20, 31, 52);
    private static final Color PANEL_LIGHT = new Color(27, 41, 65);
    private static final Color CYAN = new Color(0, 220, 235);
    private static final Color GREEN = new Color(30, 210, 150);
    private static final Color ORANGE = new Color(255, 170, 65);
    private static final Color RED = new Color(255, 95, 115);
    private static final Color WHITE = new Color(238, 244, 255);
    private static final Color MUTED = new Color(145, 162, 187);
    private static final Color FIELD = new Color(14, 24, 42);

    private JComboBox<StudentItem> cmbStudent;
    private JTextField txtAmount, txtPaidAmount, txtBalance;
    private JTextField txtDueDate, txtPaymentDate, txtReceiptNumber;
    private JComboBox<String> cmbFeeType, cmbPaymentMethod;
    private JLabel lblStatus;
    private JButton btnAdd, btnUpdate, btnDelete, btnClear;
    private JTextField txtSearch;
    private JButton btnSearch, btnAll, btnPending, btnPartial, btnPaid;
    private JTable feeTable;
    private DefaultTableModel tableModel;
    private int selectedFeeId = -1;

    public FeeFrame() {
        setTitle("Hostel Management System | Fee Management");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setMinimumSize(new Dimension(900, 650));
        setSize(1250, 850);
        setLocationRelativeTo(null);
        getContentPane().setBackground(BG);
        createForm();
        loadStudents();
        loadFees("");
    }

    private void createForm() {
        JPanel page = new JPanel();
        page.setLayout(new BoxLayout(page, BoxLayout.Y_AXIS));
        page.setBackground(BG);
        page.setBorder(BorderFactory.createEmptyBorder(26, 30, 28, 30));

        page.add(createHeader());
        page.add(Box.createVerticalStrut(22));
        page.add(createSearchPanel());
        page.add(Box.createVerticalStrut(18));
        page.add(createFormPanel());
        page.add(Box.createVerticalStrut(15));
        page.add(createActionPanel());
        page.add(Box.createVerticalStrut(20));
        page.add(createTablePanel());
        page.add(Box.createVerticalStrut(18));

        JLabel footer = new JLabel("HOSTEL MANAGEMENT SYSTEM  •  FEE MANAGEMENT");
        footer.setForeground(MUTED);
        footer.setFont(new Font("SansSerif", Font.BOLD, 10));
        footer.setAlignmentX(Component.LEFT_ALIGNMENT);
        page.add(footer);

        JScrollPane pageScroll = new JScrollPane(page);
        pageScroll.setBorder(null);
        pageScroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        pageScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        pageScroll.getViewport().setBackground(BG);
        pageScroll.getVerticalScrollBar().setUnitIncrement(20);
        add(pageScroll, BorderLayout.CENTER);
    }

    private JPanel createHeader() {
        JPanel header = new JPanel(new BorderLayout(10, 8));
        header.setOpaque(false);
        header.setAlignmentX(Component.LEFT_ALIGNMENT);
        header.setMaximumSize(new Dimension(Integer.MAX_VALUE, 80));

        JPanel titles = new JPanel();
        titles.setOpaque(false);
        titles.setLayout(new BoxLayout(titles, BoxLayout.Y_AXIS));

        JLabel eyebrow = new JLabel("HOSTEL FINANCE  /  ADMINISTRATION");
        eyebrow.setForeground(CYAN);
        eyebrow.setFont(new Font("SansSerif", Font.BOLD, 10));

        JLabel title = new JLabel("Fee Management");
        title.setForeground(WHITE);
        title.setFont(new Font("SansSerif", Font.BOLD, 30));

        JLabel subtitle = new JLabel("Create, track and manage student fee records.");
        subtitle.setForeground(MUTED);
        subtitle.setFont(new Font("SansSerif", Font.PLAIN, 12));

        titles.add(eyebrow);
        titles.add(Box.createVerticalStrut(4));
        titles.add(title);
        titles.add(Box.createVerticalStrut(3));
        titles.add(subtitle);
        header.add(titles, BorderLayout.WEST);

        JButton refresh = makeButton("↻  REFRESH", CYAN, BG);
        refresh.addActionListener(e -> {
            loadStudents();
            loadFees("");
        });
        header.add(refresh, BorderLayout.EAST);
        return header;
    }

    private JPanel createSearchPanel() {
        JPanel card = createCard();
        card.setLayout(new BorderLayout(12, 10));
        card.setBorder(BorderFactory.createEmptyBorder(15, 18, 15, 18));
        card.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel label = sectionLabel("SEARCH & FILTER FEES");
        card.add(label, BorderLayout.NORTH);

        JPanel controls = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 2));
        controls.setOpaque(false);
        txtSearch = new JTextField(19);
        styleField(txtSearch);
        btnSearch = makeButton("SEARCH", CYAN, BG);
        btnAll = makeButton("ALL FEES", PANEL_LIGHT, WHITE);
        btnPending = makeButton("PENDING", PANEL_LIGHT, ORANGE);
        btnPartial = makeButton("PARTIAL", PANEL_LIGHT, CYAN);
        btnPaid = makeButton("PAID", PANEL_LIGHT, GREEN);

        controls.add(txtSearch);
        controls.add(btnSearch);
        controls.add(btnAll);
        controls.add(btnPending);
        controls.add(btnPartial);
        controls.add(btnPaid);
        card.add(controls, BorderLayout.CENTER);

        btnSearch.addActionListener(e -> searchFees());
        txtSearch.addActionListener(e -> searchFees());
        btnAll.addActionListener(e -> loadFees(""));
        btnPending.addActionListener(e -> loadFees("PENDING"));
        btnPartial.addActionListener(e -> loadFees("PARTIAL"));
        btnPaid.addActionListener(e -> loadFees("PAID"));
        return card;
    }

    private JPanel createFormPanel() {
        JPanel card = createCard();
        card.setLayout(new BorderLayout(10, 15));
        card.setBorder(BorderFactory.createEmptyBorder(18, 20, 20, 20));
        card.setAlignmentX(Component.LEFT_ALIGNMENT);

        card.add(sectionLabel("FEE DETAILS"), BorderLayout.NORTH);

        JPanel fields = new JPanel(new GridLayout(0, 4, 14, 13));
        fields.setOpaque(false);

        cmbStudent = new JComboBox<>();
        txtAmount = new JTextField();
        txtPaidAmount = new JTextField("0");
        txtBalance = new JTextField("0");
        txtBalance.setEditable(false);
        txtDueDate = new JTextField();
        txtPaymentDate = new JTextField();
        txtReceiptNumber = new JTextField();

        cmbFeeType = new JComboBox<>(new String[]{
            "Hostel Fee", "Mess Fee", "Maintenance Fee", "Other"
        });
        cmbPaymentMethod = new JComboBox<>(new String[]{
            "Cash", "UPI", "Card", "Bank Transfer"
        });

        styleCombo(cmbStudent);
        styleCombo(cmbFeeType);
        styleCombo(cmbPaymentMethod);
        styleField(txtAmount);
        styleField(txtPaidAmount);
        styleField(txtBalance);
        styleField(txtDueDate);
        styleField(txtPaymentDate);
        styleField(txtReceiptNumber);

        lblStatus = new JLabel("PENDING");
        lblStatus.setForeground(ORANGE);
        lblStatus.setFont(new Font("SansSerif", Font.BOLD, 13));
        lblStatus.setHorizontalAlignment(SwingConstants.CENTER);
        lblStatus.setOpaque(true);
        lblStatus.setBackground(FIELD);
        lblStatus.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

        addField(fields, "STUDENT", cmbStudent);
        addField(fields, "TOTAL FEE (₹)", txtAmount);
        addField(fields, "PAID AMOUNT (₹)", txtPaidAmount);
        addField(fields, "BALANCE (₹)", txtBalance);
        addField(fields, "FEE TYPE", cmbFeeType);
        addField(fields, "DUE DATE (YYYY-MM-DD)", txtDueDate);
        addField(fields, "PAYMENT DATE (YYYY-MM-DD)", txtPaymentDate);
        addField(fields, "PAYMENT METHOD", cmbPaymentMethod);
        addField(fields, "PAYMENT STATUS", lblStatus);
        addField(fields, "RECEIPT NUMBER", txtReceiptNumber);

        card.add(fields, BorderLayout.CENTER);

        DocumentListener balanceListener = new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { calculateBalance(); }
            public void removeUpdate(DocumentEvent e) { calculateBalance(); }
            public void changedUpdate(DocumentEvent e) { calculateBalance(); }
        };
        txtAmount.getDocument().addDocumentListener(balanceListener);
        txtPaidAmount.getDocument().addDocumentListener(balanceListener);

        return card;
    }

    private void addField(JPanel panel, String labelText, Component field) {
        JPanel wrapper = new JPanel(new BorderLayout(0, 6));
        wrapper.setOpaque(false);
        JLabel label = new JLabel(labelText);
        label.setForeground(MUTED);
        label.setFont(new Font("SansSerif", Font.BOLD, 9));
        wrapper.add(label, BorderLayout.NORTH);
        wrapper.add(field, BorderLayout.CENTER);
        panel.add(wrapper);
    }

    private JPanel createActionPanel() {
        JPanel actions = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        actions.setOpaque(false);
        actions.setAlignmentX(Component.LEFT_ALIGNMENT);

        btnAdd = makeButton("+  ADD FEE", CYAN, BG);
        btnUpdate = makeButton("UPDATE", PANEL_LIGHT, WHITE);
        btnDelete = makeButton("DELETE", new Color(75, 35, 50), RED);
        btnClear = makeButton("CLEAR FORM", PANEL_LIGHT, MUTED);

        actions.add(btnAdd);
        actions.add(btnUpdate);
        actions.add(btnDelete);
        actions.add(btnClear);

        btnAdd.addActionListener(e -> addFee());
        btnUpdate.addActionListener(e -> updateFee());
        btnDelete.addActionListener(e -> deleteFee());
        btnClear.addActionListener(e -> clearFields());
        return actions;
    }

    private JPanel createTablePanel() {
        JPanel card = createCard();
        card.setLayout(new BorderLayout(10, 12));
        card.setBorder(BorderFactory.createEmptyBorder(18, 18, 18, 18));
        card.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel heading = new JPanel(new BorderLayout());
        heading.setOpaque(false);
        JLabel title = sectionLabel("FEE RECORDS");
        JLabel hint = new JLabel("Select a row to view or edit its details");
        hint.setForeground(MUTED);
        hint.setFont(new Font("SansSerif", Font.PLAIN, 10));
        heading.add(title, BorderLayout.NORTH);
        heading.add(hint, BorderLayout.SOUTH);
        card.add(heading, BorderLayout.NORTH);

        tableModel = new DefaultTableModel(new String[]{
            "ID", "Enrollment No", "Student Name", "Total Fee", "Paid",
            "Balance", "Fee Type", "Due Date", "Payment Date",
            "Method", "Status", "Receipt"
        }, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        feeTable = new JTable(tableModel);
        feeTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        feeTable.setRowHeight(29);
        feeTable.setFont(new Font("SansSerif", Font.PLAIN, 11));
        feeTable.setForeground(WHITE);
        feeTable.setBackground(FIELD);
        feeTable.setGridColor(new Color(39, 55, 78));
        feeTable.setSelectionBackground(new Color(22, 75, 91));
        feeTable.setSelectionForeground(WHITE);
        feeTable.getTableHeader().setBackground(PANEL_LIGHT);
        feeTable.getTableHeader().setForeground(CYAN);
        feeTable.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 10));
        feeTable.getTableHeader().setPreferredSize(new Dimension(0, 34));
        feeTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

        JScrollPane tableScroll = new JScrollPane(feeTable);
        tableScroll.setPreferredSize(new Dimension(1050, 330));
        tableScroll.getViewport().setBackground(FIELD);
        tableScroll.setBorder(BorderFactory.createLineBorder(new Color(39, 55, 78)));
        card.add(tableScroll, BorderLayout.CENTER);

        feeTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && feeTable.getSelectedRow() != -1) {
                fillFormFromSelectedRow();
            }
        });
        return card;
    }

    private JPanel createCard() {
        JPanel panel = new JPanel();
        panel.setBackground(PANEL);
        panel.setBorder(BorderFactory.createLineBorder(new Color(35, 51, 76)));
        return panel;
    }

    private JLabel sectionLabel(String text) {
        JLabel label = new JLabel(text);
        label.setForeground(WHITE);
        label.setFont(new Font("SansSerif", Font.BOLD, 12));
        return label;
    }

    private JButton makeButton(String text, Color background, Color foreground) {
        JButton button = new JButton(text);
        button.setFont(new Font("SansSerif", Font.BOLD, 10));
        button.setForeground(foreground);
        button.setBackground(background);
        button.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        button.setFocusPainted(false);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.setOpaque(true);
        return button;
    }

    private void styleField(JTextField field) {
        field.setBackground(FIELD);
        field.setForeground(WHITE);
        field.setCaretColor(CYAN);
        field.setFont(new Font("SansSerif", Font.PLAIN, 12));
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(53, 72, 101)),
                BorderFactory.createEmptyBorder(8, 9, 8, 9)));
    }

    private void styleCombo(JComboBox<?> combo) {
        combo.setBackground(FIELD);
        combo.setForeground(WHITE);
        combo.setFont(new Font("SansSerif", Font.PLAIN, 11));
        combo.setBorder(BorderFactory.createLineBorder(new Color(53, 72, 101)));
    }

    private void calculateBalance() {
        try {
            BigDecimal total = new BigDecimal(txtAmount.getText().trim().isEmpty()
                    ? "0" : txtAmount.getText().trim());
            BigDecimal paid = new BigDecimal(txtPaidAmount.getText().trim().isEmpty()
                    ? "0" : txtPaidAmount.getText().trim());
            BigDecimal balance = total.subtract(paid);
            if (balance.compareTo(BigDecimal.ZERO) < 0) balance = BigDecimal.ZERO;
            txtBalance.setText(balance.toPlainString());

            if (paid.compareTo(BigDecimal.ZERO) <= 0) {
                setStatus("PENDING", ORANGE);
            } else if (paid.compareTo(total) < 0) {
                setStatus("PARTIAL", CYAN);
            } else {
                setStatus("PAID", GREEN);
            }
        } catch (Exception ex) {
            txtBalance.setText("0");
            setStatus("PENDING", ORANGE);
        }
    }

    private void setStatus(String text, Color color) {
        lblStatus.setText(text);
        lblStatus.setForeground(color);
    }

    private void loadStudents() {
        cmbStudent.removeAllItems();
        String sql = "SELECT student_id, enrollment_no, full_name FROM students ORDER BY full_name";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement pst = con.prepareStatement(sql);
             ResultSet rs = pst.executeQuery()) {
            while (rs.next()) {
                cmbStudent.addItem(new StudentItem(
                        rs.getInt("student_id"),
                        rs.getString("enrollment_no"),
                        rs.getString("full_name")));
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Unable to load students:\n" + e.getMessage());
        }
    }

    private void selectStudent(String enrollmentNo) {
        for (int i = 0; i < cmbStudent.getItemCount(); i++) {
            StudentItem item = cmbStudent.getItemAt(i);
            if (item.enrollmentNo.equals(enrollmentNo)) {
                cmbStudent.setSelectedIndex(i);
                return;
            }
        }
    }

    private String feeSelectSql() {
        return "SELECT f.fee_id, s.enrollment_no, s.full_name, f.amount, "
                + "f.paid_amount, (f.amount - f.paid_amount) AS balance, "
                + "f.fee_type, f.due_date, f.payment_date, f.payment_method, "
                + "f.payment_status, f.receipt_number "
                + "FROM fees f INNER JOIN students s ON f.student_id = s.student_id ";
    }

    private void loadFees(String statusFilter) {
        tableModel.setRowCount(0);
        String sql = feeSelectSql();
        if (!statusFilter.isEmpty()) sql += "WHERE f.payment_status = ? ";
        sql += "ORDER BY f.fee_id DESC";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement pst = con.prepareStatement(sql)) {
            if (!statusFilter.isEmpty()) pst.setString(1, statusFilter);
            try (ResultSet rs = pst.executeQuery()) {
                while (rs.next()) addResultRow(rs);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Unable to load fees:\n" + e.getMessage());
        }
    }

    private void searchFees() {
        String search = txtSearch.getText().trim();
        if (search.isEmpty()) {
            loadFees("");
            return;
        }
        tableModel.setRowCount(0);
        String sql = feeSelectSql()
                + "WHERE s.full_name LIKE ? OR s.enrollment_no LIKE ? "
                + "OR f.receipt_number LIKE ? OR f.fee_type LIKE ? "
                + "ORDER BY f.fee_id DESC";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement pst = con.prepareStatement(sql)) {
            String pattern = "%" + search + "%";
            for (int i = 1; i <= 4; i++) pst.setString(i, pattern);
            try (ResultSet rs = pst.executeQuery()) {
                while (rs.next()) addResultRow(rs);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Search error:\n" + e.getMessage());
        }
    }

    private void addResultRow(ResultSet rs) throws Exception {
        tableModel.addRow(new Object[]{
            rs.getInt("fee_id"),
            rs.getString("enrollment_no"),
            rs.getString("full_name"),
            rs.getBigDecimal("amount"),
            rs.getBigDecimal("paid_amount"),
            rs.getBigDecimal("balance"),
            rs.getString("fee_type"),
            rs.getDate("due_date"),
            rs.getDate("payment_date"),
            rs.getString("payment_method"),
            rs.getString("payment_status"),
            rs.getString("receipt_number")
        });
    }

    private void fillFormFromSelectedRow() {
        int row = feeTable.getSelectedRow();
        if (row < 0) return;
        selectedFeeId = Integer.parseInt(tableModel.getValueAt(row, 0).toString());
        selectStudent(String.valueOf(tableModel.getValueAt(row, 1)));
        txtAmount.setText(String.valueOf(tableModel.getValueAt(row, 3)));
        txtPaidAmount.setText(String.valueOf(tableModel.getValueAt(row, 4)));
        txtBalance.setText(String.valueOf(tableModel.getValueAt(row, 5)));
        cmbFeeType.setSelectedItem(String.valueOf(tableModel.getValueAt(row, 6)));
        txtDueDate.setText(tableModel.getValueAt(row, 7) == null ? "" : tableModel.getValueAt(row, 7).toString());
        txtPaymentDate.setText(tableModel.getValueAt(row, 8) == null ? "" : tableModel.getValueAt(row, 8).toString());
        Object method = tableModel.getValueAt(row, 9);
        if (method != null) cmbPaymentMethod.setSelectedItem(method.toString());
        String status = String.valueOf(tableModel.getValueAt(row, 10));
        setStatus(status, "PAID".equals(status) ? GREEN : ("PARTIAL".equals(status) ? CYAN : ORANGE));
        Object receipt = tableModel.getValueAt(row, 11);
        txtReceiptNumber.setText(receipt == null ? "" : receipt.toString());
    }

    private boolean validateFields() {
        if (cmbStudent.getSelectedItem() == null) {
            showMessage("Please select a student.");
            return false;
        }
        if (txtAmount.getText().trim().isEmpty()) {
            showMessage("Please enter total fee.");
            return false;
        }
        if (txtPaidAmount.getText().trim().isEmpty()) {
            showMessage("Enter paid amount. Use 0 for pending.");
            return false;
        }
        if (txtDueDate.getText().trim().isEmpty()) {
            showMessage("Please enter due date.");
            return false;
        }
        return true;
    }

    private BigDecimal[] readAmounts() {
        BigDecimal total = new BigDecimal(txtAmount.getText().trim());
        BigDecimal paid = new BigDecimal(txtPaidAmount.getText().trim());
        if (total.compareTo(BigDecimal.ZERO) <= 0) throw new IllegalArgumentException("Total fee must be greater than zero.");
        if (paid.compareTo(BigDecimal.ZERO) < 0) throw new IllegalArgumentException("Paid amount cannot be negative.");
        if (paid.compareTo(total) > 0) throw new IllegalArgumentException("Paid amount cannot be greater than total fee.");
        return new BigDecimal[]{total, paid};
    }

    private Date readDueDate() {
        return Date.valueOf(txtDueDate.getText().trim());
    }

    private Date readPaymentDate(BigDecimal paid) {
        if (paid.compareTo(BigDecimal.ZERO) <= 0) return null;
        if (txtPaymentDate.getText().trim().isEmpty()) {
            throw new IllegalArgumentException("Enter payment date.");
        }
        return Date.valueOf(txtPaymentDate.getText().trim());
    }

    private String readPaymentMethod(BigDecimal paid) {
        return paid.compareTo(BigDecimal.ZERO) > 0
                ? String.valueOf(cmbPaymentMethod.getSelectedItem()) : null;
    }

    private String readReceipt() {
        String value = txtReceiptNumber.getText().trim();
        return value.isEmpty() ? null : value;
    }

    private String getStatus(BigDecimal total, BigDecimal paid) {
        if (paid.compareTo(BigDecimal.ZERO) <= 0) return "PENDING";
        if (paid.compareTo(total) < 0) return "PARTIAL";
        return "PAID";
    }

    private void addFee() {
        StudentItem student = (StudentItem) cmbStudent.getSelectedItem();
        if (student == null || !validateFields()) return;

        try {
            BigDecimal[] amounts = readAmounts();
            BigDecimal total = amounts[0], paid = amounts[1];
            Date dueDate = readDueDate();
            Date paymentDate = readPaymentDate(paid);
            String method = readPaymentMethod(paid);
            String status = getStatus(total, paid);

            String sql = "INSERT INTO fees (student_id, amount, paid_amount, fee_type, "
                    + "due_date, payment_date, payment_method, payment_status, receipt_number) "
                    + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
            try (Connection con = DBConnection.getConnection();
                 PreparedStatement pst = con.prepareStatement(sql)) {
                pst.setInt(1, student.studentId);
                pst.setBigDecimal(2, total);
                pst.setBigDecimal(3, paid);
                pst.setString(4, String.valueOf(cmbFeeType.getSelectedItem()));
                pst.setDate(5, dueDate);
                if (paymentDate == null) pst.setNull(6, java.sql.Types.DATE);
                else pst.setDate(6, paymentDate);
                if (method == null) pst.setNull(7, java.sql.Types.VARCHAR);
                else pst.setString(7, method);
                pst.setString(8, status);
                if (readReceipt() == null) pst.setNull(9, java.sql.Types.VARCHAR);
                else pst.setString(9, readReceipt());
                pst.executeUpdate();
            }
            JOptionPane.showMessageDialog(this, "Fee added successfully!\nStatus: "
                    + status + "\nBalance: ₹" + total.subtract(paid));
            clearFields();
            loadFees("");
        } catch (NumberFormatException e) {
            showMessage("Please enter valid amounts.");
        } catch (IllegalArgumentException e) {
            showMessage("Please check the amounts and dates.\n" + e.getMessage()
                    + "\nDates must use YYYY-MM-DD format.");
        } catch (Exception e) {
            showMessage("Error adding fee:\n" + e.getMessage());
        }
    }

    private void updateFee() {
        if (selectedFeeId == -1) {
            showMessage("Please select a fee from the table first.");
            return;
        }
        StudentItem student = (StudentItem) cmbStudent.getSelectedItem();
        if (student == null || !validateFields()) return;

        try {
            BigDecimal[] amounts = readAmounts();
            BigDecimal total = amounts[0], paid = amounts[1];
            Date dueDate = readDueDate();
            Date paymentDate = readPaymentDate(paid);
            String method = readPaymentMethod(paid);
            String status = getStatus(total, paid);

            String sql = "UPDATE fees SET student_id=?, amount=?, paid_amount=?, fee_type=?, "
                    + "due_date=?, payment_date=?, payment_method=?, payment_status=?, "
                    + "receipt_number=? WHERE fee_id=?";
            try (Connection con = DBConnection.getConnection();
                 PreparedStatement pst = con.prepareStatement(sql)) {
                pst.setInt(1, student.studentId);
                pst.setBigDecimal(2, total);
                pst.setBigDecimal(3, paid);
                pst.setString(4, String.valueOf(cmbFeeType.getSelectedItem()));
                pst.setDate(5, dueDate);
                if (paymentDate == null) pst.setNull(6, java.sql.Types.DATE);
                else pst.setDate(6, paymentDate);
                if (method == null) pst.setNull(7, java.sql.Types.VARCHAR);
                else pst.setString(7, method);
                pst.setString(8, status);
                if (readReceipt() == null) pst.setNull(9, java.sql.Types.VARCHAR);
                else pst.setString(9, readReceipt());
                pst.setInt(10, selectedFeeId);
                pst.executeUpdate();
            }
            JOptionPane.showMessageDialog(this, "Fee updated successfully!");
            clearFields();
            loadFees("");
        } catch (NumberFormatException e) {
            showMessage("Please enter valid amounts.");
        } catch (IllegalArgumentException e) {
            showMessage("Please check the amounts and dates.\n" + e.getMessage()
                    + "\nDates must use YYYY-MM-DD format.");
        } catch (Exception e) {
            showMessage("Error updating fee:\n" + e.getMessage());
        }
    }

    private void deleteFee() {
        if (selectedFeeId == -1) {
            showMessage("Please select a fee first.");
            return;
        }
        int choice = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete this fee?",
                "Confirm Delete", JOptionPane.YES_NO_OPTION);
        if (choice != JOptionPane.YES_OPTION) return;

        String sql = "DELETE FROM fees WHERE fee_id=?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement pst = con.prepareStatement(sql)) {
            pst.setInt(1, selectedFeeId);
            pst.executeUpdate();
            JOptionPane.showMessageDialog(this, "Fee deleted successfully!");
            clearFields();
            loadFees("");
        } catch (Exception e) {
            showMessage("Error deleting fee:\n" + e.getMessage());
        }
    }

    private void clearFields() {
        if (cmbStudent.getItemCount() > 0) cmbStudent.setSelectedIndex(0);
        txtAmount.setText("");
        txtPaidAmount.setText("0");
        txtBalance.setText("0");
        txtDueDate.setText("");
        txtPaymentDate.setText("");
        txtReceiptNumber.setText("");
        cmbFeeType.setSelectedIndex(0);
        cmbPaymentMethod.setSelectedIndex(0);
        setStatus("PENDING", ORANGE);
        selectedFeeId = -1;
        feeTable.clearSelection();
    }

    private void showMessage(String message) {
        JOptionPane.showMessageDialog(this, message);
    }

    private static class StudentItem {
        int studentId;
        String enrollmentNo;
        String fullName;

        StudentItem(int studentId, String enrollmentNo, String fullName) {
            this.studentId = studentId;
            this.enrollmentNo = enrollmentNo;
            this.fullName = fullName;
        }

        @Override
        public String toString() {
            return enrollmentNo + " - " + fullName;
        }
    }

    public static void main(String[] args) {
        java.awt.EventQueue.invokeLater(() -> new FeeFrame().setVisible(true));
    }
}
