
package hostelmanagement.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

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
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableModel;

import hostelmanagement.db.DBConnection;

public class ReportFrame extends JFrame {

    // ================= THEME =================

    private final Color BG = new Color(5, 15, 32);
    private final Color PANEL = new Color(12, 27, 50);
    private final Color FIELD = new Color(9, 22, 42);
    private final Color CYAN = new Color(0, 205, 240);
    private final Color BLUE = new Color(30, 130, 255);
    private final Color GREEN = new Color(0, 210, 145);
    private final Color ORANGE = new Color(255, 140, 40);
    private final Color PURPLE = new Color(150, 90, 255);
    private final Color PINK = new Color(235, 65, 155);
    private final Color WHITE = new Color(240, 247, 255);
    private final Color MUTED = new Color(150, 170, 195);

    // ================= COMPONENTS =================

    private JComboBox<String> cmbReportType;
    private JTextField txtSearch;

    private JButton btnGenerate;
    private JButton btnRefresh;
    private JButton btnClear;

    private JTable reportTable;
    private DefaultTableModel tableModel;

    private JLabel lblStudents;
    private JLabel lblRooms;
    private JLabel lblOccupied;
    private JLabel lblAvailable;
    private JLabel lblCollected;
    private JLabel lblPending;

    private JLabel lblOccupancy;
    private JLabel lblPaid;
    private JLabel lblPendingPayments;
    private JLabel lblBoys;
    private JLabel lblGirls;

    private DonutPanel occupancyChart;
    private DonutPanel paymentChart;
    private DonutPanel studentChart;
    private LineChartPanel monthlyChart;

    public ReportFrame() {

        setTitle("Hostel Management System - Reports & Analytics");
        setSize(1450, 900);
        setMinimumSize(new Dimension(1150, 750));
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        getContentPane().setBackground(BG);

        createDashboard();
        refreshDashboard();
        generateReport();
    }

    // ================= MAIN DASHBOARD =================

    private void createDashboard() {

        setLayout(new BorderLayout());

        add(createSidebar(), BorderLayout.WEST);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(BG);

        mainPanel.add(createHeader(), BorderLayout.NORTH);

        JPanel content = new JPanel();
        content.setBackground(BG);
        content.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));

        content.add(createMetricPanel());
        content.add(Box.createVerticalStrut(15));

        content.add(createChartPanel());
        content.add(Box.createVerticalStrut(15));

        content.add(createBottomPanel());
        content.add(Box.createVerticalStrut(15));

        content.add(createReportPanel());

        JScrollPane scroll = new JScrollPane(content);
        scroll.setBorder(null);
        scroll.getViewport().setBackground(BG);
        scroll.getVerticalScrollBar().setUnitIncrement(18);

        mainPanel.add(scroll, BorderLayout.CENTER);

        add(mainPanel, BorderLayout.CENTER);
    }

    // ================= SIDEBAR =================

    private JPanel createSidebar() {

        JPanel sidebar = new JPanel();
        sidebar.setPreferredSize(new Dimension(145, 0));
        sidebar.setBackground(new Color(4, 17, 37));
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBorder(BorderFactory.createMatteBorder(
                0, 0, 0, 1, CYAN));

        JLabel logo = new JLabel("HOSTEL");
        logo.setForeground(CYAN);
        logo.setFont(new Font("SansSerif", Font.BOLD, 22));
        logo.setAlignmentX(CENTER_ALIGNMENT);

        JLabel subtitle = new JLabel("MANAGEMENT");
        subtitle.setForeground(WHITE);
        subtitle.setFont(new Font("SansSerif", Font.BOLD, 11));
        subtitle.setAlignmentX(CENTER_ALIGNMENT);

        sidebar.add(Box.createVerticalStrut(25));
        sidebar.add(logo);
        sidebar.add(subtitle);
        sidebar.add(Box.createVerticalStrut(45));

        String[] menu = {
            "Dashboard",
            "Students",
            "Rooms",
            "Fees",
            "Reports",
            "Settings",
            "Logout"
        };

        for (String item : menu) {

            JButton button = new JButton(item);
            button.setMaximumSize(new Dimension(125, 48));
            button.setPreferredSize(new Dimension(125, 48));
            button.setAlignmentX(CENTER_ALIGNMENT);
            button.setFocusPainted(false);
            button.setForeground(WHITE);
            button.setBackground(
                    item.equals("Reports")
                    ? new Color(8, 55, 95)
                    : new Color(4, 17, 37));
            button.setFont(new Font("SansSerif", Font.BOLD, 13));
            button.setBorder(BorderFactory.createEmptyBorder(8, 5, 8, 5));

            if (item.equals("Reports")) {
                button.setBorder(BorderFactory.createMatteBorder(
                        0, 3, 0, 0, CYAN));
            }

            button.addActionListener(e -> handleMenu(item));

            sidebar.add(button);
            sidebar.add(Box.createVerticalStrut(10));
        }

        sidebar.add(Box.createVerticalGlue());

        JLabel footer = new JLabel("HOSTEL SYSTEM");
        footer.setForeground(MUTED);
        footer.setFont(new Font("SansSerif", Font.PLAIN, 10));
        footer.setAlignmentX(CENTER_ALIGNMENT);

        sidebar.add(footer);
        sidebar.add(Box.createVerticalStrut(15));

        return sidebar;
    }

    private void handleMenu(String item) {

        if (item.equals("Dashboard")) {
            openFrame("DashboardFrame");
        } else if (item.equals("Students")) {
            openFrame("StudentFrame");
        } else if (item.equals("Rooms")) {
            openFrame("RoomFrame");
        } else if (item.equals("Fees")) {
            openFrame("FeeFrame");
        } else if (item.equals("Logout")) {

            int choice = JOptionPane.showConfirmDialog(
                    this,
                    "Are you sure you want to logout?",
                    "Logout",
                    JOptionPane.YES_NO_OPTION
            );

            if (choice == JOptionPane.YES_OPTION) {
                dispose();
                new LoginFrame().setVisible(true);
            }
        }
    }

    private void openFrame(String className) {

        try {
            Class<?> frameClass = Class.forName(
                    "hostelmanagement.gui." + className);

            JFrame frame = (JFrame) frameClass
                    .getDeclaredConstructor()
                    .newInstance();

            frame.setVisible(true);

        } catch (Exception e) {
            JOptionPane.showMessageDialog(
                    this,
                    "Unable to open " + className
                    + ". Check whether that class exists.",
                    "Navigation",
                    JOptionPane.WARNING_MESSAGE
            );
        }
    }

    // ================= HEADER =================

    private JPanel createHeader() {

        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(PANEL);
        header.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(0, 110, 170)),
                BorderFactory.createEmptyBorder(15, 22, 15, 22)
        ));

        JPanel heading = new JPanel();
        heading.setOpaque(false);
        heading.setLayout(new BoxLayout(heading, BoxLayout.Y_AXIS));

        JLabel title = new JLabel("Reports & Analytics");
        title.setForeground(WHITE);
        title.setFont(new Font("SansSerif", Font.BOLD, 28));

        JLabel sub = new JLabel("Insights at a glance");
        sub.setForeground(MUTED);
        sub.setFont(new Font("SansSerif", Font.PLAIN, 14));

        heading.add(title);
        heading.add(sub);

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 5));
        actions.setOpaque(false);

        JLabel date = new JLabel("Hostel Management System");
        date.setForeground(MUTED);

        JButton refresh = createButton("Refresh", BLUE);
        refresh.addActionListener(e -> refreshDashboard());

        actions.add(date);
        actions.add(refresh);

        header.add(heading, BorderLayout.WEST);
        header.add(actions, BorderLayout.EAST);

        return header;
    }

    // ================= SUMMARY CARDS =================

    private JPanel createMetricPanel() {

        JPanel panel = new JPanel(new GridLayout(1, 6, 12, 12));
        panel.setBackground(BG);
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 135));

        lblStudents = new JLabel("0");
        lblRooms = new JLabel("0");
        lblOccupied = new JLabel("0");
        lblAvailable = new JLabel("0");
        lblCollected = new JLabel("₹0");
        lblPending = new JLabel("₹0");

        panel.add(createMetricCard("Total Students", lblStudents, BLUE));
        panel.add(createMetricCard("Total Rooms", lblRooms, PURPLE));
        panel.add(createMetricCard("Occupied Beds", lblOccupied, GREEN));
        panel.add(createMetricCard("Available Beds", lblAvailable, ORANGE));
        panel.add(createMetricCard("Fees Collected", lblCollected, CYAN));
        panel.add(createMetricCard("Pending Payments", lblPending, PINK));

        return panel;
    }

    private JPanel createMetricCard(
            String title,
            JLabel value,
            Color accent) {

        JPanel card = new JPanel(new BorderLayout(5, 10));
        card.setBackground(PANEL);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(accent, 1),
                BorderFactory.createEmptyBorder(15, 10, 15, 10)
        ));

        JLabel heading = new JLabel(title, SwingConstants.CENTER);
        heading.setForeground(MUTED);
        heading.setFont(new Font("SansSerif", Font.BOLD, 12));

        value.setForeground(WHITE);
        value.setFont(new Font("SansSerif", Font.BOLD, 23));
        value.setHorizontalAlignment(SwingConstants.CENTER);

        card.add(heading, BorderLayout.NORTH);
        card.add(value, BorderLayout.CENTER);

        return card;
    }

    // ================= CHARTS =================

    private JPanel createChartPanel() {

        JPanel panel = new JPanel(new GridLayout(1, 3, 12, 12));
        panel.setBackground(BG);
        panel.setPreferredSize(new Dimension(1000, 270));
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 270));

        occupancyChart = new DonutPanel("Room Occupancy", CYAN, BLUE);
        monthlyChart = new LineChartPanel();
        paymentChart = new DonutPanel("Payment Status", GREEN, ORANGE);

        panel.add(occupancyChart);
        panel.add(monthlyChart);
        panel.add(paymentChart);

        return panel;
    }

    // ================= BOTTOM PANEL =================

    private JPanel createBottomPanel() {

        JPanel panel = new JPanel(new GridLayout(1, 2, 12, 12));
        panel.setBackground(BG);
        panel.setPreferredSize(new Dimension(1000, 220));
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 220));

        JPanel distribution = createCardPanel("Student Distribution");

        studentChart = new DonutPanel("Gender Distribution", BLUE, PINK);
        studentChart.setPreferredSize(new Dimension(250, 150));

        lblBoys = new JLabel("Male: 0");
        lblGirls = new JLabel("Female: 0");

        lblBoys.setForeground(WHITE);
        lblGirls.setForeground(WHITE);

        JPanel genderInfo = new JPanel(new GridLayout(2, 1, 5, 5));
        genderInfo.setOpaque(false);
        genderInfo.add(lblBoys);
        genderInfo.add(lblGirls);

        JPanel distributionContent = new JPanel(new BorderLayout());
        distributionContent.setOpaque(false);
        distributionContent.add(studentChart, BorderLayout.CENTER);
        distributionContent.add(genderInfo, BorderLayout.EAST);

        distribution.add(distributionContent, BorderLayout.CENTER);

        JPanel quick = createCardPanel("Quick Reports");

        JPanel quickButtons = new JPanel(new GridLayout(2, 3, 10, 10));
        quickButtons.setOpaque(false);

        String[] types = {
            "Students",
            "Rooms",
            "Fees",
            "Payments",
            "Complaints"
        };

        Color[] colors = {
            BLUE, PURPLE, GREEN, ORANGE, CYAN
        };

        for (int i = 0; i < types.length; i++) {

            final String type = types[i];

            JButton button = createButton(type + " Report", colors[i]);

            button.addActionListener(e -> {
                cmbReportType.setSelectedItem(type);
                txtSearch.setText("");
                generateReport();
            });

            quickButtons.add(button);
        }

        quick.add(quickButtons, BorderLayout.CENTER);

        panel.add(distribution);
        panel.add(quick);

        return panel;
    }

    // ================= REPORT TABLE =================

    private JPanel createReportPanel() {

        JPanel panel = createCardPanel("Detailed Reports");

        JPanel controls = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        controls.setOpaque(false);

        cmbReportType = new JComboBox<>(new String[]{
            "Students",
            "Rooms",
            "Fees",
            "Payments",
            "Complaints"
        });

        styleCombo(cmbReportType);

        txtSearch = new JTextField(20);
        styleTextField(txtSearch);

        btnGenerate = createButton("Generate", BLUE);
        btnRefresh = createButton("Refresh", CYAN);
        btnClear = createButton("Clear", new Color(90, 105, 125));

        controls.add(makeLabel("Report Type:"));
        controls.add(cmbReportType);
        controls.add(makeLabel("Search:"));
        controls.add(txtSearch);
        controls.add(btnGenerate);
        controls.add(btnClear);
        controls.add(btnRefresh);

        tableModel = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        reportTable = new JTable(tableModel);
        styleTable(reportTable);

        JScrollPane tableScroll = new JScrollPane(reportTable);
        tableScroll.getViewport().setBackground(FIELD);
        tableScroll.setBorder(BorderFactory.createLineBorder(
                new Color(0, 100, 150)));

        panel.add(controls, BorderLayout.NORTH);
        panel.add(tableScroll, BorderLayout.CENTER);

        btnGenerate.addActionListener(e -> generateReport());
        btnRefresh.addActionListener(e -> {
            refreshDashboard();
            generateReport();
        });

        btnClear.addActionListener(e -> {
            txtSearch.setText("");
            generateReport();
        });

        cmbReportType.addActionListener(e -> generateReport());
        txtSearch.addActionListener(e -> generateReport());

        return panel;
    }

    // ================= DASHBOARD DATA =================

    private void refreshDashboard() {

        loadSummaryCards();
        loadGenderData();
        loadMonthlyData();
    }

    private void loadSummaryCards() {

        String sqlStudents = "SELECT COUNT(*) FROM students";
        String sqlRooms = "SELECT COUNT(*) FROM rooms";
        String sqlRoomBeds =
                "SELECT COALESCE(SUM(capacity),0), "
                + "COALESCE(SUM(occupied_beds),0) FROM rooms";

        String sqlFees =
                "SELECT COALESCE(SUM(amount),0), "
                + "COALESCE(SUM(paid_amount),0) FROM fees";

        String sqlPayments =
                "SELECT "
                + "SUM(CASE WHEN payment_status='PAID' THEN 1 ELSE 0 END), "
                + "SUM(CASE WHEN payment_status<>'PAID' THEN 1 ELSE 0 END) "
                + "FROM fees";

        try (Connection con = DBConnection.getConnection()) {

            lblStudents.setText(String.valueOf(getCount(con, sqlStudents)));
            lblRooms.setText(String.valueOf(getCount(con, sqlRooms)));

            try (PreparedStatement pst = con.prepareStatement(sqlRoomBeds);
                 ResultSet rs = pst.executeQuery()) {

                if (rs.next()) {

                    int capacity = rs.getInt(1);
                    int occupied = rs.getInt(2);

                    lblOccupied.setText(String.valueOf(occupied));
                    lblAvailable.setText(String.valueOf(
                            Math.max(0, capacity - occupied)));

                    int percent = capacity == 0
                            ? 0
                            : (occupied * 100 / capacity);

                    lblOccupancy = new JLabel(percent + "%");
                    occupancyChart.setValues(occupied,
                            Math.max(0, capacity - occupied),
                            percent + "%");
                }
            }

            try (PreparedStatement pst = con.prepareStatement(sqlFees);
                 ResultSet rs = pst.executeQuery()) {

                if (rs.next()) {

                    BigDecimal total = rs.getBigDecimal(1);
                    BigDecimal paid = rs.getBigDecimal(2);

                    if (total == null) total = BigDecimal.ZERO;
                    if (paid == null) paid = BigDecimal.ZERO;

                    BigDecimal balance = total.subtract(paid);

                    lblCollected.setText("₹" + paid.toPlainString());
                    lblPending.setText("₹" + balance.toPlainString());
                }
            }

            try (PreparedStatement pst = con.prepareStatement(sqlPayments);
                 ResultSet rs = pst.executeQuery()) {

                if (rs.next()) {

                    int paid = rs.getInt(1);
                    int pending = rs.getInt(2);
                    int total = paid + pending;

                    int percent = total == 0
                            ? 0
                            : paid * 100 / total;

                    lblPaid = new JLabel(String.valueOf(paid));
                    lblPendingPayments = new JLabel(String.valueOf(pending));

                    paymentChart.setValues(paid, pending, percent + "%");
                }
            }

        } catch (Exception e) {
            showError("Unable to load dashboard summary:\n" + e.getMessage());
        }
    }

    private int getCount(Connection con, String sql) throws Exception {

        try (PreparedStatement pst = con.prepareStatement(sql);
             ResultSet rs = pst.executeQuery()) {

            if (rs.next()) {
                return rs.getInt(1);
            }
        }

        return 0;
    }

    private void loadGenderData() {

        int male = 0;
        int female = 0;

        String sql =
                "SELECT gender, COUNT(*) AS total "
                + "FROM students GROUP BY gender";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement pst = con.prepareStatement(sql);
             ResultSet rs = pst.executeQuery()) {

            while (rs.next()) {

                String gender = rs.getString("gender");
                int total = rs.getInt("total");

                if (gender != null && gender.equalsIgnoreCase("Male")) {
                    male += total;
                } else if (gender != null
                        && gender.equalsIgnoreCase("Female")) {
                    female += total;
                }
            }

            lblBoys.setText("Male: " + male);
            lblGirls.setText("Female: " + female);

            studentChart.setValues(male, female, "");

        } catch (Exception e) {
            showError("Unable to load student distribution:\n" + e.getMessage());
        }
    }

    private void loadMonthlyData() {

        List<String> months = new ArrayList<>();
        List<Double> values = new ArrayList<>();

        String sql =
                "SELECT DATE_FORMAT(payment_date,'%b') AS month_name, "
                + "SUM(payment_amount) AS total "
                + "FROM payment_history "
                + "WHERE payment_date >= DATE_SUB(CURDATE(), INTERVAL 5 MONTH) "
                + "GROUP BY YEAR(payment_date), MONTH(payment_date), "
                + "DATE_FORMAT(payment_date,'%b') "
                + "ORDER BY YEAR(payment_date), MONTH(payment_date)";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement pst = con.prepareStatement(sql);
             ResultSet rs = pst.executeQuery()) {

            while (rs.next()) {

                months.add(rs.getString("month_name"));

                BigDecimal amount = rs.getBigDecimal("total");
                values.add(amount == null ? 0.0 : amount.doubleValue());
            }

            monthlyChart.setData(months, values);

        } catch (Exception e) {
            monthlyChart.setData(months, values);
        }
    }

    // ================= GENERATE REPORT =================

    private void generateReport() {

        if (cmbReportType == null || tableModel == null) {
            return;
        }

        String type = cmbReportType.getSelectedItem().toString();
        String search = txtSearch.getText().trim();

        String sql;
        String[] columns;
        int parameters = 0;

        switch (type) {

            case "Students":

                columns = new String[]{
                    "ID", "Enrollment No", "Full Name", "Gender",
                    "Date of Birth", "Phone", "Email", "Department",
                    "Semester", "Address", "Admission Date"
                };

                sql = "SELECT student_id, enrollment_no, full_name, gender, "
                        + "date_of_birth, phone, email, department, semester, "
                        + "address, admission_date FROM students";

                if (!search.isEmpty()) {
                    sql += " WHERE enrollment_no LIKE ? OR full_name LIKE ? "
                            + "OR phone LIKE ? OR department LIKE ?";
                    parameters = 4;
                }

                sql += " ORDER BY full_name";
                break;

            case "Rooms":

                columns = new String[]{
                    "ID", "Room Number", "Room Type", "Capacity",
                    "Occupied Beds", "Available Beds", "Floor", "Status"
                };

                sql = "SELECT room_id, room_number, room_type, capacity, "
                        + "occupied_beds, (capacity - occupied_beds) "
                        + "AS available_beds, floor_number, status FROM rooms";

                if (!search.isEmpty()) {
                    sql += " WHERE room_number LIKE ? OR room_type LIKE ? "
                            + "OR status LIKE ?";
                    parameters = 3;
                }

                sql += " ORDER BY room_number";
                break;

            case "Fees":

                columns = new String[]{
                    "Fee ID", "Enrollment No", "Student", "Fee Type",
                    "Amount", "Paid", "Balance", "Due Date",
                    "Payment Date", "Method", "Status", "Receipt"
                };

                sql = "SELECT f.fee_id, s.enrollment_no, s.full_name, "
                        + "f.fee_type, f.amount, f.paid_amount, "
                        + "(f.amount-f.paid_amount) AS balance, "
                        + "f.due_date, f.payment_date, f.payment_method, "
                        + "f.payment_status, f.receipt_number "
                        + "FROM fees f INNER JOIN students s "
                        + "ON f.student_id=s.student_id";

                if (!search.isEmpty()) {
                    sql += " WHERE s.enrollment_no LIKE ? OR s.full_name LIKE ? "
                            + "OR f.fee_type LIKE ? OR f.payment_status LIKE ?";
                    parameters = 4;
                }

                sql += " ORDER BY f.due_date ASC";
                break;

            case "Payments":

                columns = new String[]{
                    "Payment ID", "Enrollment No", "Student", "Fee Type",
                    "Payment Amount", "Payment Date", "Payment Method",
                    "Receipt Number"
                };

                sql = "SELECT p.payment_id, s.enrollment_no, s.full_name, "
                        + "f.fee_type, p.payment_amount, p.payment_date, "
                        + "p.payment_method, p.receipt_number "
                        + "FROM payment_history p "
                        + "INNER JOIN fees f ON p.fee_id=f.fee_id "
                        + "INNER JOIN students s ON f.student_id=s.student_id";

                if (!search.isEmpty()) {
                    sql += " WHERE s.enrollment_no LIKE ? OR s.full_name LIKE ? "
                            + "OR f.fee_type LIKE ? OR p.receipt_number LIKE ? "
                            + "OR p.payment_method LIKE ?";
                    parameters = 5;
                }

                sql += " ORDER BY p.payment_id DESC";
                break;

            case "Complaints":

                columns = new String[]{
                    "ID", "Enrollment No", "Student", "Title", "Category",
                    "Complaint Date", "Status", "Description",
                    "Resolution", "Resolved Date"
                };

                sql = "SELECT c.complaint_id, s.enrollment_no, s.full_name, "
                        + "c.complaint_title, c.category, c.complaint_date, "
                        + "c.status, c.description, c.resolution, c.resolved_date "
                        + "FROM complaints c INNER JOIN students s "
                        + "ON c.student_id=s.student_id";

                if (!search.isEmpty()) {
                    sql += " WHERE s.enrollment_no LIKE ? OR s.full_name LIKE ? "
                            + "OR c.complaint_title LIKE ? OR c.category LIKE ? "
                            + "OR c.status LIKE ?";
                    parameters = 5;
                }

                sql += " ORDER BY c.complaint_date DESC";
                break;

            default:
                return;
        }

        tableModel.setColumnIdentifiers(columns);
        tableModel.setRowCount(0);

        try (Connection con = DBConnection.getConnection();
             PreparedStatement pst = con.prepareStatement(sql)) {

            if (!search.isEmpty()) {

                String value = "%" + search + "%";

                for (int i = 1; i <= parameters; i++) {
                    pst.setString(i, value);
                }
            }

            try (ResultSet rs = pst.executeQuery()) {

                int count = 0;
                int columnCount = columns.length;

                while (rs.next()) {

                    Object[] row = new Object[columnCount];

                    for (int i = 0; i < columnCount; i++) {
                        row[i] = rs.getObject(i + 1);
                    }

                    tableModel.addRow(row);
                    count++;
                }

                setTitle("Hostel Management System - "
                        + type + " Report (" + count + " records)");
            }

        } catch (Exception e) {
            showError("Unable to generate " + type
                    + " report:\n" + e.getMessage());
        }
    }

    // ================= PANEL HELPERS =================

    private JPanel createCardPanel(String title) {

        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(PANEL);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(0, 125, 185)),
                BorderFactory.createEmptyBorder(12, 14, 12, 14)
        ));

        JLabel heading = new JLabel(title);
        heading.setForeground(WHITE);
        heading.setFont(new Font("SansSerif", Font.BOLD, 17));

        panel.add(heading, BorderLayout.NORTH);

        return panel;
    }

    private JButton createButton(String text, Color color) {

        JButton button = new JButton(text);
        button.setForeground(WHITE);
        button.setBackground(color);
        button.setFont(new Font("SansSerif", Font.BOLD, 12));
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(9, 14, 9, 14));
        button.setCursor(new java.awt.Cursor(
                java.awt.Cursor.HAND_CURSOR));

        return button;
    }

    private JLabel makeLabel(String text) {

        JLabel label = new JLabel(text);
        label.setForeground(WHITE);
        label.setFont(new Font("SansSerif", Font.BOLD, 12));

        return label;
    }

    private void styleCombo(JComboBox<String> combo) {

        combo.setBackground(FIELD);
        combo.setForeground(WHITE);
        combo.setFont(new Font("SansSerif", Font.PLAIN, 13));
    }

    private void styleTextField(JTextField field) {

        field.setBackground(FIELD);
        field.setForeground(WHITE);
        field.setCaretColor(CYAN);
        field.setFont(new Font("SansSerif", Font.PLAIN, 13));
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(0, 120, 170)),
                BorderFactory.createEmptyBorder(7, 8, 7, 8)
        ));
    }

    private void styleTable(JTable table) {

        table.setBackground(FIELD);
        table.setForeground(WHITE);
        table.setGridColor(new Color(35, 65, 95));
        table.setRowHeight(27);
        table.setFont(new Font("SansSerif", Font.PLAIN, 12));
        table.setSelectionBackground(new Color(0, 90, 140));
        table.setSelectionForeground(WHITE);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

        table.getTableHeader().setBackground(new Color(0, 65, 105));
        table.getTableHeader().setForeground(WHITE);
        table.getTableHeader().setFont(
                new Font("SansSerif", Font.BOLD, 12));
        table.getTableHeader().setPreferredSize(new Dimension(100, 34));
    }

    private void showError(String message) {

        JOptionPane.showMessageDialog(
                this,
                message,
                "Report Dashboard Error",
                JOptionPane.ERROR_MESSAGE
        );
    }

    // ================= DONUT CHART =================

    private class DonutPanel extends JPanel {

        private String heading;
        private Color firstColor;
        private Color secondColor;
        private int firstValue;
        private int secondValue;
        private String centerText = "0%";

        DonutPanel(String heading, Color firstColor, Color secondColor) {

            this.heading = heading;
            this.firstColor = firstColor;
            this.secondColor = secondColor;

            setBackground(PANEL);
            setBorder(BorderFactory.createLineBorder(
                    new Color(0, 125, 185)));
        }

        void setValues(int first, int second, String text) {

            firstValue = Math.max(0, first);
            secondValue = Math.max(0, second);
            centerText = text == null ? "" : text;

            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {

            super.paintComponent(g);

            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(
                    RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);

            g2.setColor(WHITE);
            g2.setFont(new Font("SansSerif", Font.BOLD, 16));
            g2.drawString(heading, 15, 27);

            int total = firstValue + secondValue;

            int size = Math.min(155, Math.max(90, getHeight() - 75));
            int x = 25;
            int y = 48;

            if (total > 0) {

                int angle = (int) Math.round(
                        360.0 * firstValue / total);

                g2.setColor(firstColor);
                g2.fillArc(x, y, size, size, 90, angle);

                g2.setColor(secondColor);
                g2.fillArc(x, y, size, size, 90 + angle, 360 - angle);

            } else {

                g2.setColor(new Color(50, 65, 85));
                g2.fillOval(x, y, size, size);
            }

            g2.setColor(PANEL);
            int hole = size / 2;
            g2.fillOval(x + hole / 2, y + hole / 2, size - hole, size - hole);

            g2.setColor(WHITE);
            g2.setFont(new Font("SansSerif", Font.BOLD, 18));

            String center = centerText.isEmpty()
                    ? String.valueOf(total)
                    : centerText;

            int textWidth = g2.getFontMetrics().stringWidth(center);
            g2.drawString(center,
                    x + (size - textWidth) / 2,
                    y + size / 2 + 6);

            int legendX = x + size + 18;
            int legendY = y + 40;

            g2.setColor(firstColor);
            g2.fillOval(legendX, legendY - 10, 12, 12);

            g2.setColor(WHITE);
            g2.setFont(new Font("SansSerif", Font.PLAIN, 12));
            g2.drawString(" " + firstValue, legendX + 18, legendY);

            g2.setColor(secondColor);
            g2.fillOval(legendX, legendY + 28, 12, 12);

            g2.setColor(WHITE);
            g2.drawString(" " + secondValue, legendX + 18, legendY + 38);

            g2.dispose();
        }
    }

    // ================= MONTHLY LINE CHART =================

    private class LineChartPanel extends JPanel {

        private List<String> months = new ArrayList<>();
        private List<Double> values = new ArrayList<>();

        LineChartPanel() {

            setBackground(PANEL);
            setBorder(BorderFactory.createLineBorder(
                    new Color(0, 125, 185)));
        }

        void setData(List<String> months, List<Double> values) {

            this.months = new ArrayList<>(months);
            this.values = new ArrayList<>(values);

            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {

            super.paintComponent(g);

            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(
                    RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);

            g2.setColor(WHITE);
            g2.setFont(new Font("SansSerif", Font.BOLD, 16));
            g2.drawString("Monthly Fee Collection", 15, 27);

            int left = 48;
            int top = 55;
            int right = getWidth() - 20;
            int bottom = getHeight() - 40;

            g2.setColor(new Color(40, 70, 100));

            for (int i = 0; i < 5; i++) {

                int y = top + (bottom - top) * i / 4;
                g2.drawLine(left, y, right, y);
            }

            if (values.isEmpty()) {

                g2.setColor(MUTED);
                g2.drawString("No payment history available",
                        left + 20, top + 55);

                g2.dispose();
                return;
            }

            double max = 1;

            for (double value : values) {
                if (value > max) max = value;
            }

            int count = values.size();
            int previousX = -1;
            int previousY = -1;

            g2.setColor(CYAN);
            g2.setStroke(new java.awt.BasicStroke(3));

            for (int i = 0; i < count; i++) {

                int x = count == 1
                        ? (left + right) / 2
                        : left + (right - left) * i / (count - 1);

                int y = bottom - (int) (
                        (values.get(i) / max) * (bottom - top));

                if (previousX != -1) {
                    g2.drawLine(previousX, previousY, x, y);
                }

                g2.fillOval(x - 5, y - 5, 10, 10);

                g2.setColor(WHITE);
                g2.setFont(new Font("SansSerif", Font.PLAIN, 11));

                String month = months.get(i);
                int width = g2.getFontMetrics().stringWidth(month);
                g2.drawString(month, x - width / 2, bottom + 22);

                g2.setColor(CYAN);

                previousX = x;
                previousY = y;
            }

            g2.dispose();
        }
    }

    // ================= MAIN =================

    public static void main(String[] args) {

        java.awt.EventQueue.invokeLater(() -> {
            new ReportFrame().setVisible(true);
        });
    }
}
