
package hostelmanagement.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
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
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;

import hostelmanagement.db.DBConnection;

public class ComplaintFrame extends JFrame {

    // ================= THEME COLORS =================

    private final Color BG = new Color(10, 16, 30);
    private final Color PANEL = new Color(20, 31, 52);
    private final Color FIELD = new Color(14, 24, 42);
    private final Color CYAN = new Color(0, 220, 235);
    private final Color WHITE = new Color(238, 244, 255);
    private final Color MUTED = new Color(145, 162, 187);
    private final Color GREEN = new Color(30, 210, 150);
    private final Color RED = new Color(235, 75, 90);

    private JComboBox<StudentItem> cmbStudent;
    private JComboBox<String> cmbCategory;
    private JComboBox<String> cmbStatus;
    private JComboBox<String> cmbSearchStatus;

    private JTextField txtTitle;
    private JTextField txtComplaintDate;
    private JTextField txtResolvedDate;
    private JTextField txtSearch;

    private JTextArea txtDescription;
    private JTextArea txtResolution;

    private JButton btnAdd;
    private JButton btnUpdate;
    private JButton btnDelete;
    private JButton btnClear;
    private JButton btnSearch;
    private JButton btnRefresh;

    private JTable complaintTable;
    private DefaultTableModel tableModel;

    private int selectedComplaintId = -1;

    // ================= CONSTRUCTOR =================

    public ComplaintFrame() {

        setTitle("Hostel Management System - Complaints");
        setSize(1300, 800);
        setMinimumSize(new Dimension(1100, 700));
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        getContentPane().setBackground(BG);

        createForm();

        loadStudents();
        loadComplaints();
    }

    // ================= MAIN FORM =================

    private void createForm() {

        setLayout(new BorderLayout(15, 15));

        JPanel mainPanel = new JPanel(new BorderLayout(15, 15));
        mainPanel.setBackground(BG);
        mainPanel.setBorder(
                BorderFactory.createEmptyBorder(18, 22, 18, 22)
        );

        // HEADER

        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(BG);

        JLabel title = new JLabel("COMPLAINT MANAGEMENT");
        title.setFont(new Font("SansSerif", Font.BOLD, 27));
        title.setForeground(CYAN);

        JLabel subtitle = new JLabel(
                "Manage student complaints and resolutions"
        );
        subtitle.setFont(new Font("SansSerif", Font.PLAIN, 13));
        subtitle.setForeground(MUTED);

        JPanel heading = new JPanel(new GridLayout(2, 1, 0, 5));
        heading.setBackground(BG);
        heading.add(title);
        heading.add(subtitle);

        headerPanel.add(heading, BorderLayout.WEST);

        mainPanel.add(headerPanel, BorderLayout.NORTH);

        // CENTER CONTENT

        JPanel centerPanel = new JPanel(new BorderLayout(15, 15));
        centerPanel.setBackground(BG);

        // FORM CARD

        JPanel formCard = createCard();
        formCard.setLayout(new BorderLayout(10, 10));

        JLabel formTitle = createSectionTitle("COMPLAINT DETAILS");
        formCard.add(formTitle, BorderLayout.NORTH);

        JPanel formPanel = new JPanel(new GridLayout(5, 2, 15, 12));
        formPanel.setBackground(PANEL);

        cmbStudent = new JComboBox<>();

        cmbCategory = new JComboBox<>(new String[]{
            "Room",
            "Mess",
            "Electricity",
            "Water",
            "Cleanliness",
            "Maintenance",
            "Other"
        });

        cmbStatus = new JComboBox<>(new String[]{
            "PENDING",
            "IN PROGRESS",
            "RESOLVED"
        });

        txtTitle = new JTextField();
        txtComplaintDate = new JTextField(LocalDate.now().toString());
        txtResolvedDate = new JTextField();
        txtResolvedDate.setEditable(false);

        txtDescription = new JTextArea(3, 20);
        txtDescription.setLineWrap(true);
        txtDescription.setWrapStyleWord(true);

        txtResolution = new JTextArea(3, 20);
        txtResolution.setLineWrap(true);
        txtResolution.setWrapStyleWord(true);

        formPanel.add(createFieldGroup("Student", cmbStudent));
        formPanel.add(createFieldGroup("Category", cmbCategory));

        formPanel.add(createFieldGroup("Complaint Title", txtTitle));
        formPanel.add(createFieldGroup(
                "Complaint Date (YYYY-MM-DD)", txtComplaintDate
        ));

        formPanel.add(createFieldGroup("Status", cmbStatus));
        formPanel.add(createFieldGroup("Resolved Date", txtResolvedDate));

        formPanel.add(createFieldGroup(
                "Description", new JScrollPane(txtDescription)
        ));

        formPanel.add(createFieldGroup(
                "Resolution", new JScrollPane(txtResolution)
        ));

        JLabel note = new JLabel(
                "Note: Resolution is required when status is RESOLVED."
        );
        note.setFont(new Font("SansSerif", Font.ITALIC, 12));
        note.setForeground(MUTED);

        JPanel notePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        notePanel.setBackground(PANEL);
        notePanel.add(note);

        formPanel.add(notePanel);
        formPanel.add(new JPanel() {{
            setBackground(PANEL);
        }});

        formCard.add(formPanel, BorderLayout.CENTER);

        centerPanel.add(formCard, BorderLayout.NORTH);

        // TABLE CARD

        JPanel tableCard = createCard();
        tableCard.setLayout(new BorderLayout(10, 12));

        JPanel tableHeader = new JPanel(new BorderLayout());
        tableHeader.setBackground(PANEL);

        JLabel tableTitle = createSectionTitle("COMPLAINT RECORDS");
        tableHeader.add(tableTitle, BorderLayout.WEST);

        // SEARCH CONTROLS

        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        searchPanel.setBackground(PANEL);

        JLabel searchLabel = createLabel("Search:");
        txtSearch = new JTextField(13);
        styleTextField(txtSearch);

        JLabel statusLabel = createLabel("Status:");

        cmbSearchStatus = new JComboBox<>(new String[]{
            "ALL",
            "PENDING",
            "IN PROGRESS",
            "RESOLVED"
        });
        styleComboBox(cmbSearchStatus);

        btnSearch = createButton("SEARCH", CYAN, BG);
        btnRefresh = createButton("REFRESH", new Color(65, 85, 115), WHITE);

        searchPanel.add(searchLabel);
        searchPanel.add(txtSearch);
        searchPanel.add(statusLabel);
        searchPanel.add(cmbSearchStatus);
        searchPanel.add(btnSearch);
        searchPanel.add(btnRefresh);

        tableHeader.add(searchPanel, BorderLayout.EAST);

        tableCard.add(tableHeader, BorderLayout.NORTH);

        // TABLE MODEL

        tableModel = new DefaultTableModel(
                new String[]{
                    "ID",
                    "Enrollment No",
                    "Student",
                    "Title",
                    "Category",
                    "Complaint Date",
                    "Status",
                    "Description",
                    "Resolution",
                    "Resolved Date"
                },
                0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        complaintTable = new JTable(tableModel);
        complaintTable.setSelectionMode(
                ListSelectionModel.SINGLE_SELECTION
        );
        complaintTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

        styleTable();

        JScrollPane scrollPane = new JScrollPane(complaintTable);
        scrollPane.getViewport().setBackground(FIELD);
        scrollPane.setBorder(
                BorderFactory.createLineBorder(new Color(42, 58, 82))
        );

        tableCard.add(scrollPane, BorderLayout.CENTER);

        centerPanel.add(tableCard, BorderLayout.CENTER);

        mainPanel.add(centerPanel, BorderLayout.CENTER);

        // BOTTOM BUTTONS

        JPanel buttonPanel = new JPanel(
                new FlowLayout(FlowLayout.CENTER, 15, 5)
        );
        buttonPanel.setBackground(BG);

        btnAdd = createButton("ADD COMPLAINT", GREEN, BG);
        btnUpdate = createButton("UPDATE", CYAN, BG);
        btnDelete = createButton("DELETE", RED, WHITE);
        btnClear = createButton("CLEAR", new Color(65, 85, 115), WHITE);

        buttonPanel.add(btnAdd);
        buttonPanel.add(btnUpdate);
        buttonPanel.add(btnDelete);
        buttonPanel.add(btnClear);

        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(mainPanel, BorderLayout.CENTER);

        // ACTION LISTENERS

        btnAdd.addActionListener(e -> addComplaint());
        btnUpdate.addActionListener(e -> updateComplaint());
        btnDelete.addActionListener(e -> deleteComplaint());
        btnClear.addActionListener(e -> clearFields());

        btnSearch.addActionListener(e -> loadComplaints());
        btnRefresh.addActionListener(e -> refreshData());

        cmbStatus.addActionListener(e -> updateResolutionField());

        cmbSearchStatus.addActionListener(e -> loadComplaints());

        complaintTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()
                    && complaintTable.getSelectedRow() != -1) {
                loadSelectedComplaint();
            }
        });
    }

    // ================= DESIGN HELPERS =================

    private JPanel createCard() {

        JPanel panel = new JPanel();
        panel.setBackground(PANEL);

        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(
                        new Color(42, 58, 82), 1
                ),
                BorderFactory.createEmptyBorder(15, 17, 15, 17)
        ));

        return panel;
    }

    private JLabel createSectionTitle(String text) {

        JLabel label = new JLabel(text);
        label.setFont(new Font("SansSerif", Font.BOLD, 16));
        label.setForeground(CYAN);

        return label;
    }

    private JLabel createLabel(String text) {

        JLabel label = new JLabel(text);
        label.setFont(new Font("SansSerif", Font.BOLD, 12));
        label.setForeground(WHITE);

        return label;
    }

    private JPanel createFieldGroup(String labelText,
                                    java.awt.Component component) {

        JPanel panel = new JPanel(new BorderLayout(0, 6));
        panel.setBackground(PANEL);

        JLabel label = createLabel(labelText);

        panel.add(label, BorderLayout.NORTH);
        panel.add(component, BorderLayout.CENTER);

        if (component instanceof JTextField) {
            styleTextField((JTextField) component);
        } else if (component instanceof JComboBox) {
            styleComboBox((JComboBox<?>) component);
        }

        return panel;
    }

    private void styleTextField(JTextField field) {

        field.setBackground(FIELD);
        field.setForeground(WHITE);
        field.setCaretColor(CYAN);
        field.setFont(new Font("SansSerif", Font.PLAIN, 13));

        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(
                        new Color(55, 75, 105)
                ),
                BorderFactory.createEmptyBorder(8, 10, 8, 10)
        ));
    }

    private void styleTextArea(JTextArea area) {

        area.setBackground(FIELD);
        area.setForeground(WHITE);
        area.setCaretColor(CYAN);
        area.setFont(new Font("SansSerif", Font.PLAIN, 13));

        area.setBorder(BorderFactory.createEmptyBorder(8, 10, 8, 10));
    }

    private void styleComboBox(JComboBox<?> combo) {

        combo.setBackground(FIELD);
        combo.setForeground(WHITE);
        combo.setFont(new Font("SansSerif", Font.PLAIN, 13));
    }

    private JButton createButton(String text, Color background,
                                 Color foreground) {

        JButton button = new JButton(text);

        button.setFont(new Font("SansSerif", Font.BOLD, 12));
        button.setBackground(background);
        button.setForeground(foreground);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(10, 18, 10, 18));
        button.setCursor(new java.awt.Cursor(
                java.awt.Cursor.HAND_CURSOR
        ));

        return button;
    }

    private void styleTable() {

        complaintTable.setBackground(FIELD);
        complaintTable.setForeground(WHITE);
        complaintTable.setFont(new Font("SansSerif", Font.PLAIN, 12));
        complaintTable.setRowHeight(30);
        complaintTable.setGridColor(new Color(42, 58, 82));
        complaintTable.setSelectionBackground(
                new Color(0, 105, 125)
        );
        complaintTable.setSelectionForeground(WHITE);
        complaintTable.setShowGrid(true);

        JTableHeader header = complaintTable.getTableHeader();

        header.setBackground(new Color(0, 125, 145));
        header.setForeground(Color.WHITE);
        header.setFont(new Font("SansSerif", Font.BOLD, 12));
        header.setPreferredSize(new Dimension(100, 35));

        int[] widths = {
            55, 120, 150, 180, 110,
            115, 110, 250, 250, 115
        };

        for (int i = 0; i < widths.length; i++) {
            complaintTable.getColumnModel()
                    .getColumn(i)
                    .setPreferredWidth(widths[i]);
        }

        styleTextArea(txtDescription);
        styleTextArea(txtResolution);
    }

    // ================= LOAD STUDENTS =================

    private void loadStudents() {

        cmbStudent.removeAllItems();

        String sql = "SELECT student_id, enrollment_no, full_name "
                + "FROM students ORDER BY full_name";

        try (
                Connection con = DBConnection.getConnection();
                PreparedStatement pst = con.prepareStatement(sql);
                ResultSet rs = pst.executeQuery()
        ) {

            while (rs.next()) {

                cmbStudent.addItem(new StudentItem(
                        rs.getInt("student_id"),
                        rs.getString("enrollment_no"),
                        rs.getString("full_name")
                ));
            }

        } catch (Exception e) {
            showError("Unable to load students:\n" + e.getMessage());
        }
    }

    // ================= LOAD COMPLAINTS =================

    private void loadComplaints() {

        tableModel.setRowCount(0);

        String search = txtSearch.getText().trim();

        String selectedStatus = cmbSearchStatus.getSelectedItem() == null
                ? "ALL"
                : cmbSearchStatus.getSelectedItem().toString();

        StringBuilder sql = new StringBuilder(
                "SELECT c.complaint_id, s.enrollment_no, s.full_name, "
                + "c.complaint_title, c.category, c.complaint_date, "
                + "c.status, c.description, c.resolution, c.resolved_date "
                + "FROM complaints c "
                + "INNER JOIN students s ON c.student_id = s.student_id "
                + "WHERE 1=1 "
        );

        if (!search.isEmpty()) {
            sql.append(
                    "AND (s.enrollment_no LIKE ? "
                    + "OR s.full_name LIKE ? "
                    + "OR c.complaint_title LIKE ? "
                    + "OR c.category LIKE ? "
                    + "OR c.status LIKE ?) "
            );
        }

        if (!selectedStatus.equals("ALL")) {
            sql.append("AND c.status = ? ");
        }

        sql.append(
                "ORDER BY CASE "
                + "WHEN c.status='PENDING' THEN 1 "
                + "WHEN c.status='IN PROGRESS' THEN 2 "
                + "ELSE 3 END, "
                + "c.complaint_date DESC, c.complaint_id DESC"
        );

        try (
                Connection con = DBConnection.getConnection();
                PreparedStatement pst = con.prepareStatement(sql.toString())
        ) {

            int parameter = 1;

            if (!search.isEmpty()) {

                String value = "%" + search + "%";

                for (int i = 0; i < 5; i++) {
                    pst.setString(parameter++, value);
                }
            }

            if (!selectedStatus.equals("ALL")) {
                pst.setString(parameter, selectedStatus);
            }

            try (ResultSet rs = pst.executeQuery()) {

                while (rs.next()) {

                    tableModel.addRow(new Object[]{
                        rs.getInt("complaint_id"),
                        rs.getString("enrollment_no"),
                        rs.getString("full_name"),
                        rs.getString("complaint_title"),
                        rs.getString("category"),
                        rs.getDate("complaint_date"),
                        rs.getString("status"),
                        rs.getString("description"),
                        rs.getString("resolution"),
                        rs.getDate("resolved_date")
                    });
                }
            }

        } catch (Exception e) {
            showError("Unable to load complaints:\n" + e.getMessage());
        }
    }

    // ================= LOAD SELECTED COMPLAINT =================

    private void loadSelectedComplaint() {

        int row = complaintTable.getSelectedRow();

        if (row == -1) {
            return;
        }

        selectedComplaintId = Integer.parseInt(
                tableModel.getValueAt(row, 0).toString()
        );

        String enrollmentNo = tableModel.getValueAt(row, 1).toString();

        for (int i = 0; i < cmbStudent.getItemCount(); i++) {

            StudentItem item = cmbStudent.getItemAt(i);

            if (item.enrollmentNo.equals(enrollmentNo)) {
                cmbStudent.setSelectedIndex(i);
                break;
            }
        }

        txtTitle.setText(valueAt(row, 3));

        cmbCategory.setSelectedItem(valueAt(row, 4));

        txtComplaintDate.setText(valueAt(row, 5));

        cmbStatus.setSelectedItem(valueAt(row, 6));

        txtDescription.setText(valueAt(row, 7));

        txtResolution.setText(valueAt(row, 8));

        txtResolvedDate.setText(valueAt(row, 9));

        updateResolutionField();
    }

    private String valueAt(int row, int column) {

        Object value = tableModel.getValueAt(row, column);

        return value == null ? "" : value.toString();
    }

    // ================= ADD COMPLAINT =================

    private void addComplaint() {

        StudentItem student = (StudentItem) cmbStudent.getSelectedItem();

        if (student == null) {
            showWarning("Please select a student.");
            return;
        }

        if (!validateFields()) {
            return;
        }

        String sql = "INSERT INTO complaints "
                + "(student_id, complaint_title, description, category, "
                + "complaint_date, status, resolution, resolved_date) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (
                Connection con = DBConnection.getConnection();
                PreparedStatement pst = con.prepareStatement(sql)
        ) {

            pst.setInt(1, student.studentId);
            pst.setString(2, txtTitle.getText().trim());
            pst.setString(3, txtDescription.getText().trim());
            pst.setString(4, cmbCategory.getSelectedItem().toString());

            pst.setDate(5, Date.valueOf(
                    LocalDate.parse(txtComplaintDate.getText().trim())
            ));

            String status = cmbStatus.getSelectedItem().toString();

            pst.setString(6, status);

            if (status.equals("RESOLVED")) {

                pst.setString(7, txtResolution.getText().trim());
                pst.setDate(8, Date.valueOf(LocalDate.now()));

            } else {

                pst.setNull(7, java.sql.Types.VARCHAR);
                pst.setNull(8, java.sql.Types.DATE);
            }

            pst.executeUpdate();

            JOptionPane.showMessageDialog(
                    this,
                    "Complaint added successfully."
            );

            clearFields();
            loadComplaints();

        } catch (Exception e) {
            showError("Unable to add complaint:\n" + e.getMessage());
        }
    }

    // ================= UPDATE COMPLAINT =================

    private void updateComplaint() {

        if (selectedComplaintId == -1) {
            showWarning("Please select a complaint first.");
            return;
        }

        StudentItem student = (StudentItem) cmbStudent.getSelectedItem();

        if (student == null) {
            showWarning("Please select a student.");
            return;
        }

        if (!validateFields()) {
            return;
        }

        String status = cmbStatus.getSelectedItem().toString();

        String sql;

        if (status.equals("RESOLVED")) {

            sql = "UPDATE complaints SET "
                    + "student_id=?, complaint_title=?, description=?, "
                    + "category=?, complaint_date=?, status=?, "
                    + "resolution=?, resolved_date=CURDATE() "
                    + "WHERE complaint_id=?";

        } else {

            sql = "UPDATE complaints SET "
                    + "student_id=?, complaint_title=?, description=?, "
                    + "category=?, complaint_date=?, status=?, "
                    + "resolution=NULL, resolved_date=NULL "
                    + "WHERE complaint_id=?";
        }

        try (
                Connection con = DBConnection.getConnection();
                PreparedStatement pst = con.prepareStatement(sql)
        ) {

            pst.setInt(1, student.studentId);
            pst.setString(2, txtTitle.getText().trim());
            pst.setString(3, txtDescription.getText().trim());
            pst.setString(4, cmbCategory.getSelectedItem().toString());

            pst.setDate(5, Date.valueOf(
                    LocalDate.parse(txtComplaintDate.getText().trim())
            ));

            pst.setString(6, status);

            if (status.equals("RESOLVED")) {

                pst.setString(7, txtResolution.getText().trim());
                pst.setInt(8, selectedComplaintId);

            } else {

                pst.setInt(7, selectedComplaintId);
            }

            pst.executeUpdate();

            JOptionPane.showMessageDialog(
                    this,
                    "Complaint updated successfully."
            );

            clearFields();
            loadComplaints();

        } catch (Exception e) {
            showError("Unable to update complaint:\n" + e.getMessage());
        }
    }

    // ================= DELETE COMPLAINT =================

    private void deleteComplaint() {

        if (selectedComplaintId == -1) {
            showWarning("Please select a complaint first.");
            return;
        }

        int choice = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to delete this complaint?",
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
        );

        if (choice != JOptionPane.YES_OPTION) {
            return;
        }

        String sql = "DELETE FROM complaints WHERE complaint_id=?";

        try (
                Connection con = DBConnection.getConnection();
                PreparedStatement pst = con.prepareStatement(sql)
        ) {

            pst.setInt(1, selectedComplaintId);
            pst.executeUpdate();

            JOptionPane.showMessageDialog(
                    this,
                    "Complaint deleted successfully."
            );

            clearFields();
            loadComplaints();

        } catch (Exception e) {
            showError("Unable to delete complaint:\n" + e.getMessage());
        }
    }

    // ================= VALIDATION =================

    private boolean validateFields() {

        String title = txtTitle.getText().trim();

        if (title.isEmpty()) {
            showWarning("Please enter complaint title.");
            return false;
        }

        if (title.length() < 3 || title.length() > 100) {
            showWarning("Complaint title must contain 3 to 100 characters.");
            return false;
        }

        if (!title.matches("[A-Za-z0-9 .,!?()'\"\\-/]{3,100}")) {
            showWarning("Complaint title contains invalid characters.");
            return false;
        }

        String description = txtDescription.getText().trim();

        if (description.isEmpty()) {
            showWarning("Please enter complaint description.");
            return false;
        }

        if (description.length() < 5 || description.length() > 2000) {
            showWarning("Description must contain 5 to 2000 characters.");
            return false;
        }

        String complaintDateText = txtComplaintDate.getText().trim();

        if (complaintDateText.isEmpty()) {
            showWarning("Please enter complaint date.");
            return false;
        }

        LocalDate complaintDate;

        try {

            complaintDate = LocalDate.parse(complaintDateText);

        } catch (DateTimeParseException e) {

            showWarning("Complaint date must use YYYY-MM-DD format.");
            return false;
        }

        if (complaintDate.isAfter(LocalDate.now())) {
            showWarning("Complaint date cannot be in the future.");
            return false;
        }

        if (cmbStatus.getSelectedItem() == null) {
            showWarning("Please select complaint status.");
            return false;
        }

        String status = cmbStatus.getSelectedItem().toString();

        if (status.equals("RESOLVED")) {

            String resolution = txtResolution.getText().trim();

            if (resolution.isEmpty()) {
                showWarning(
                        "Please enter the resolution before marking "
                        + "the complaint as RESOLVED."
                );
                return false;
            }

            if (resolution.length() > 2000) {
                showWarning("Resolution cannot exceed 2000 characters.");
                return false;
            }
        }

        return true;
    }

    // ================= RESOLUTION FIELD =================

    private void updateResolutionField() {

        if (cmbStatus == null || txtResolution == null) {
            return;
        }

        if (cmbStatus.getSelectedItem() == null) {
            return;
        }

        String status = cmbStatus.getSelectedItem().toString();

        txtResolution.setEditable(true);

        if (status.equals("RESOLVED")) {
            txtResolution.setToolTipText("Enter the complaint resolution.");
        } else {
            txtResolution.setToolTipText(
                    "Resolution will be cleared unless status is RESOLVED."
            );
        }
    }

    // ================= CLEAR =================

    private void clearFields() {

        selectedComplaintId = -1;

        if (cmbStudent.getItemCount() > 0) {
            cmbStudent.setSelectedIndex(0);
        }

        cmbCategory.setSelectedIndex(0);
        cmbStatus.setSelectedItem("PENDING");

        txtTitle.setText("");
        txtComplaintDate.setText(LocalDate.now().toString());
        txtResolvedDate.setText("");

        txtDescription.setText("");
        txtResolution.setText("");

        complaintTable.clearSelection();
    }

    // ================= REFRESH =================

    private void refreshData() {

        txtSearch.setText("");
        cmbSearchStatus.setSelectedItem("ALL");

        loadStudents();
        clearFields();
        loadComplaints();
    }

    // ================= MESSAGES =================

    private void showWarning(String message) {

        JOptionPane.showMessageDialog(
                this,
                message,
                "Validation",
                JOptionPane.WARNING_MESSAGE
        );
    }

    private void showError(String message) {

        JOptionPane.showMessageDialog(
                this,
                message,
                "Complaint Management Error",
                JOptionPane.ERROR_MESSAGE
        );
    }

    // ================= STUDENT ITEM =================

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

    // ================= MAIN =================

    public static void main(String[] args) {

        java.awt.EventQueue.invokeLater(() -> {
            new ComplaintFrame().setVisible(true);
        });
    }
}
