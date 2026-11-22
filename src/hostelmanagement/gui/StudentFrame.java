
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
import java.time.Period;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;

import hostelmanagement.db.DBConnection;

public class StudentFrame extends JFrame {

    private static final Color BG = new Color(10, 16, 30);
    private static final Color PANEL = new Color(20, 31, 52);
    private static final Color FIELD = new Color(14, 24, 42);
    private static final Color CYAN = new Color(0, 220, 235);
    private static final Color WHITE = new Color(238, 244, 255);
    private static final Color MUTED = new Color(145, 162, 187);
    private static final Color GREEN = new Color(30, 210, 150);
    private static final Color RED = new Color(235, 75, 90);

    private JTextField txtEnrollment;
    private JTextField txtFullName;
    private JTextField txtDOB;
    private JTextField txtPhone;
    private JTextField txtEmail;
    private JTextField txtDepartment;
    private JTextField txtSemester;
    private JTextField txtAddress;
    private JTextField txtAdmissionDate;
    private JTextField txtSearch;

    private JRadioButton rbMale;
    private JRadioButton rbFemale;
    private JRadioButton rbOther;
    private ButtonGroup genderGroup;

    private JButton btnAdd;
    private JButton btnUpdate;
    private JButton btnDelete;
    private JButton btnClear;
    private JButton btnSearch;
    private JButton btnShowAll;

    private JTable studentTable;
    private DefaultTableModel tableModel;

    private int selectedStudentId = -1;

    public StudentFrame() {

        setTitle("Hostel Management System - Students");
        setSize(1300, 800);
        setMinimumSize(new Dimension(1100, 700));
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        getContentPane().setBackground(BG);

        createForm();
        loadStudents();
    }

    // =====================================================
    // CREATE FORM
    // =====================================================

    private void createForm() {

        setLayout(new BorderLayout());

        JPanel mainPanel = new JPanel(new BorderLayout(15, 15));
        mainPanel.setBackground(BG);
        mainPanel.setBorder(
                BorderFactory.createEmptyBorder(20, 22, 20, 22)
        );

        // HEADER
        JPanel headerPanel = new JPanel(new BorderLayout(10, 12));
        headerPanel.setBackground(BG);

        JLabel title = new JLabel("STUDENT MANAGEMENT");
        title.setFont(new Font("SansSerif", Font.BOLD, 27));
        title.setForeground(CYAN);

        JLabel subtitle = new JLabel(
                "Manage student records, personal information and enrollment details"
        );
        subtitle.setFont(new Font("SansSerif", Font.PLAIN, 13));
        subtitle.setForeground(MUTED);

        headerPanel.add(title, BorderLayout.NORTH);
        headerPanel.add(subtitle, BorderLayout.CENTER);

        // SEARCH PANEL
        JPanel searchPanel = new JPanel(
                new FlowLayout(FlowLayout.LEFT, 10, 8)
        );
        searchPanel.setBackground(PANEL);
        searchPanel.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(new Color(40, 65, 95)),
                        BorderFactory.createEmptyBorder(5, 10, 5, 10)
                )
        );

        JLabel searchLabel = new JLabel("SEARCH STUDENT");
        searchLabel.setForeground(CYAN);
        searchLabel.setFont(new Font("SansSerif", Font.BOLD, 12));

        txtSearch = new JTextField(25);
        styleTextField(txtSearch);

        btnSearch = createButton("SEARCH", CYAN);
        btnShowAll = createButton("SHOW ALL", MUTED);

        searchPanel.add(searchLabel);
        searchPanel.add(txtSearch);
        searchPanel.add(btnSearch);
        searchPanel.add(btnShowAll);

        JPanel topPanel = new JPanel(new BorderLayout(10, 12));
        topPanel.setBackground(BG);
        topPanel.add(headerPanel, BorderLayout.NORTH);
        topPanel.add(searchPanel, BorderLayout.CENTER);

        mainPanel.add(topPanel, BorderLayout.NORTH);

        // CENTER
        JPanel centerPanel = new JPanel(new BorderLayout(15, 15));
        centerPanel.setBackground(BG);

        // STUDENT FORM CARD
        JPanel formCard = createCard();
        formCard.setLayout(new BorderLayout(10, 12));

        JLabel formTitle = new JLabel("STUDENT INFORMATION");
        formTitle.setFont(new Font("SansSerif", Font.BOLD, 17));
        formTitle.setForeground(CYAN);

        formCard.add(formTitle, BorderLayout.NORTH);

        JPanel formPanel = new JPanel(new GridLayout(5, 4, 12, 12));
        formPanel.setBackground(PANEL);

        txtEnrollment = new JTextField();
        txtFullName = new JTextField();
        txtDOB = new JTextField();
        txtPhone = new JTextField();
        txtEmail = new JTextField();
        txtDepartment = new JTextField();
        txtSemester = new JTextField();
        txtAddress = new JTextField();
        txtAdmissionDate = new JTextField();

        // GENDER
        rbMale = createRadioButton("Male");
        rbFemale = createRadioButton("Female");
        rbOther = createRadioButton("Other");

        genderGroup = new ButtonGroup();
        genderGroup.add(rbMale);
        genderGroup.add(rbFemale);
        genderGroup.add(rbOther);

        JPanel genderPanel = new JPanel(
                new FlowLayout(FlowLayout.LEFT, 8, 4)
        );
        genderPanel.setBackground(FIELD);
        genderPanel.setBorder(
                BorderFactory.createLineBorder(new Color(55, 80, 110))
        );

        genderPanel.add(rbMale);
        genderPanel.add(rbFemale);
        genderPanel.add(rbOther);

        // FORM FIELDS
        formPanel.add(createFieldGroup("Enrollment Number", txtEnrollment));
        formPanel.add(createFieldGroup("Full Name", txtFullName));

        formPanel.add(createFieldGroup("Gender", genderPanel));
        formPanel.add(createFieldGroup("Date of Birth (YYYY-MM-DD)", txtDOB));

        formPanel.add(createFieldGroup("Phone Number", txtPhone));
        formPanel.add(createFieldGroup("Email Address", txtEmail));

        formPanel.add(createFieldGroup("Department", txtDepartment));
        formPanel.add(createFieldGroup("Semester (1-8)", txtSemester));

        formPanel.add(createFieldGroup("Address", txtAddress));
        formPanel.add(createFieldGroup("Admission Date (YYYY-MM-DD)", txtAdmissionDate));

        formCard.add(formPanel, BorderLayout.CENTER);

        centerPanel.add(formCard, BorderLayout.NORTH);

        // STUDENT TABLE CARD
        JPanel tableCard = createCard();
        tableCard.setLayout(new BorderLayout(10, 12));

        JLabel tableTitle = new JLabel("REGISTERED STUDENTS");
        tableTitle.setFont(new Font("SansSerif", Font.BOLD, 17));
        tableTitle.setForeground(CYAN);

        tableCard.add(tableTitle, BorderLayout.NORTH);

        tableModel = new DefaultTableModel(
                new String[]{
                    "ID",
                    "Enrollment No",
                    "Full Name",
                    "Gender",
                    "DOB",
                    "Phone",
                    "Email",
                    "Department",
                    "Semester",
                    "Address",
                    "Admission Date"
                },
                0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        studentTable = new JTable(tableModel);
        studentTable.setSelectionMode(
                ListSelectionModel.SINGLE_SELECTION
        );

        styleTable(studentTable);

        JScrollPane scrollPane = new JScrollPane(studentTable);
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

        btnAdd = createButton("ADD STUDENT", GREEN);
        btnUpdate = createButton("UPDATE", CYAN);
        btnDelete = createButton("DELETE", RED);
        btnClear = createButton("CLEAR", MUTED);

        buttonPanel.add(btnAdd);
        buttonPanel.add(btnUpdate);
        buttonPanel.add(btnDelete);
        buttonPanel.add(btnClear);

        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(mainPanel);

        // ACTIONS
        btnAdd.addActionListener(e -> addStudent());
        btnUpdate.addActionListener(e -> updateStudent());
        btnDelete.addActionListener(e -> deleteStudent());
        btnClear.addActionListener(e -> clearFields());

        btnSearch.addActionListener(e -> searchStudents());

        btnShowAll.addActionListener(e -> {
            txtSearch.setText("");
            loadStudents();
        });

        txtSearch.addActionListener(e -> searchStudents());

        // TABLE SELECTION
        studentTable.getSelectionModel()
                .addListSelectionListener(e -> {

                    if (!e.getValueIsAdjusting()
                            && studentTable.getSelectedRow() != -1) {

                        int row = studentTable.getSelectedRow();

                        selectedStudentId = Integer.parseInt(
                                tableModel.getValueAt(row, 0).toString()
                        );

                        txtEnrollment.setText(
                                tableModel.getValueAt(row, 1).toString()
                        );

                        txtFullName.setText(
                                tableModel.getValueAt(row, 2).toString()
                        );

                        setGender(
                                tableModel.getValueAt(row, 3).toString()
                        );

                        txtDOB.setText(
                                tableModel.getValueAt(row, 4).toString()
                        );

                        txtPhone.setText(
                                tableModel.getValueAt(row, 5).toString()
                        );

                        txtEmail.setText(
                                tableModel.getValueAt(row, 6).toString()
                        );

                        txtDepartment.setText(
                                tableModel.getValueAt(row, 7).toString()
                        );

                        txtSemester.setText(
                                tableModel.getValueAt(row, 8).toString()
                        );

                        txtAddress.setText(
                                tableModel.getValueAt(row, 9).toString()
                        );

                        txtAdmissionDate.setText(
                                tableModel.getValueAt(row, 10).toString()
                        );
                    }
                });
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

    private JPanel createFieldGroup(
            String text,
            java.awt.Component field
    ) {

        JPanel panel = new JPanel(new BorderLayout(5, 7));
        panel.setBackground(PANEL);

        JLabel label = new JLabel(text);
        label.setForeground(MUTED);
        label.setFont(new Font("SansSerif", Font.BOLD, 12));

        panel.add(label, BorderLayout.NORTH);
        panel.add(field, BorderLayout.CENTER);

        if (field instanceof JTextField) {
            styleTextField((JTextField) field);
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
                        BorderFactory.createEmptyBorder(
                                7, 9, 7, 9
                        )
                )
        );
    }

    private JRadioButton createRadioButton(String text) {

        JRadioButton button = new JRadioButton(text);

        button.setBackground(FIELD);
        button.setForeground(WHITE);
        button.setFont(new Font("SansSerif", Font.PLAIN, 12));
        button.setFocusPainted(false);

        return button;
    }

    private JButton createButton(String text, Color color) {

        JButton button = new JButton(text);

        button.setFont(new Font("SansSerif", Font.BOLD, 12));
        button.setForeground(BG);
        button.setBackground(color);
        button.setFocusPainted(false);

        button.setBorder(
                BorderFactory.createEmptyBorder(
                        11, 18, 11, 18
                )
        );

        button.setCursor(
                new java.awt.Cursor(
                        java.awt.Cursor.HAND_CURSOR
                )
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
    // GET GENDER
    // =====================================================

    private String getSelectedGender() {

        if (rbMale.isSelected()) {
            return "Male";
        }

        if (rbFemale.isSelected()) {
            return "Female";
        }

        if (rbOther.isSelected()) {
            return "Other";
        }

        return "";
    }

    // =====================================================
    // SET GENDER
    // =====================================================

    private void setGender(String gender) {

        genderGroup.clearSelection();

        if (gender.equalsIgnoreCase("Male")) {
            rbMale.setSelected(true);
        } else if (gender.equalsIgnoreCase("Female")) {
            rbFemale.setSelected(true);
        } else if (gender.equalsIgnoreCase("Other")) {
            rbOther.setSelected(true);
        }
    }

    // =====================================================
    // VALIDATE
    // =====================================================

    private boolean validateFields() {

        if (txtEnrollment.getText().trim().isEmpty()) {
            showWarning("Please enter enrollment number.");
            return false;
        }

        if (!txtEnrollment.getText().trim().matches("[A-Za-z0-9]{5,20}")) {
            showWarning("Enrollment number must contain 5-20 letters or numbers.");
            return false;
        }

        if (txtFullName.getText().trim().isEmpty()) {
            showWarning("Please enter full name.");
            return false;
        }

        if (!txtFullName.getText().trim().matches("[A-Za-z .'-]{2,100}")) {
            showWarning("Full name contains invalid characters.");
            return false;
        }

        if (getSelectedGender().isEmpty()) {
            showWarning("Please select Male, Female, or Other.");
            return false;
        }

        if (txtDOB.getText().trim().isEmpty()) {
            showWarning("Please enter date of birth.");
            return false;
        }

        if (txtPhone.getText().trim().isEmpty()) {
            showWarning("Please enter phone number.");
            return false;
        }

        if (!txtPhone.getText().trim().matches("\\d{10,15}")) {
            showWarning("Phone number must contain 10-15 digits.");
            return false;
        }

        if (txtEmail.getText().trim().isEmpty()) {
            showWarning("Please enter email.");
            return false;
        }

        if (!txtEmail.getText().trim().matches(
                "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")) {

            showWarning("Please enter a valid email address.");
            return false;
        }

        if (txtDepartment.getText().trim().isEmpty()) {
            showWarning("Please enter department.");
            return false;
        }

        if (txtSemester.getText().trim().isEmpty()) {
            showWarning("Please enter semester.");
            return false;
        }

        try {

            int semester = Integer.parseInt(
                    txtSemester.getText().trim()
            );

            if (semester < 1 || semester > 8) {
                showWarning("Semester must be between 1 and 8.");
                return false;
            }

        } catch (NumberFormatException e) {

            showWarning("Semester must be a number.");
            return false;
        }

        if (txtAddress.getText().trim().isEmpty()) {
            showWarning("Please enter address.");
            return false;
        }

        if (txtAdmissionDate.getText().trim().isEmpty()) {
            showWarning("Please enter admission date.");
            return false;
        }

        try {

            LocalDate dob = LocalDate.parse(txtDOB.getText().trim());

            LocalDate admissionDate = LocalDate.parse(
                    txtAdmissionDate.getText().trim()
            );

            LocalDate today = LocalDate.now();

            if (dob.isAfter(today)) {
                showWarning("Date of birth cannot be in the future.");
                return false;
            }

            int age = Period.between(dob, today).getYears();

            if (age < 10 || age > 100) {
                showWarning("Please enter a valid date of birth.");
                return false;
            }

            if (admissionDate.isAfter(today)) {
                showWarning("Admission date cannot be in the future.");
                return false;
            }

            if (admissionDate.isBefore(dob)) {
                showWarning("Admission date cannot be before date of birth.");
                return false;
            }

        } catch (Exception e) {

            showWarning("Dates must be in YYYY-MM-DD format.");
            return false;
        }

        return true;
    }

    // =====================================================
    // CHECK DUPLICATE ENROLLMENT
    // =====================================================

    private boolean enrollmentExists(
            String enrollmentNo,
            int studentId
    ) {

        String sql =
                "SELECT student_id FROM students "
                + "WHERE enrollment_no=? AND student_id<>?";

        try (
                Connection con = DBConnection.getConnection();
                PreparedStatement pst = con.prepareStatement(sql)
        ) {

            pst.setString(1, enrollmentNo);
            pst.setInt(2, studentId);

            try (ResultSet rs = pst.executeQuery()) {
                return rs.next();
            }

        } catch (Exception e) {

            showWarning("Unable to check enrollment number.");
            return true;
        }
    }

    // =====================================================
    // ADD STUDENT
    // =====================================================

    private void addStudent() {

        if (!validateFields()) {
            return;
        }

        String enrollment = txtEnrollment.getText().trim();

        if (enrollmentExists(enrollment, -1)) {
            showWarning("This enrollment number already exists.");
            return;
        }

        String sql =
                "INSERT INTO students "
                + "(enrollment_no, full_name, gender, date_of_birth, "
                + "phone, email, department, semester, address, admission_date) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (
                Connection con = DBConnection.getConnection();
                PreparedStatement pst = con.prepareStatement(sql)
        ) {

            pst.setString(1, enrollment);
            pst.setString(2, txtFullName.getText().trim());
            pst.setString(3, getSelectedGender());
            pst.setDate(4, Date.valueOf(txtDOB.getText().trim()));
            pst.setString(5, txtPhone.getText().trim());
            pst.setString(6, txtEmail.getText().trim());
            pst.setString(7, txtDepartment.getText().trim());
            pst.setInt(8, Integer.parseInt(txtSemester.getText().trim()));
            pst.setString(9, txtAddress.getText().trim());
            pst.setDate(10, Date.valueOf(txtAdmissionDate.getText().trim()));

            pst.executeUpdate();

            JOptionPane.showMessageDialog(
                    this,
                    "Student added successfully!",
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE
            );

            clearFields();
            loadStudents();

        } catch (Exception e) {

            JOptionPane.showMessageDialog(
                    this,
                    "Unable to add student:\n" + e.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    // =====================================================
    // UPDATE STUDENT
    // =====================================================

    private void updateStudent() {

        if (selectedStudentId == -1) {
            showWarning("Please select a student first.");
            return;
        }

        if (!validateFields()) {
            return;
        }

        String enrollment = txtEnrollment.getText().trim();

        if (enrollmentExists(enrollment, selectedStudentId)) {
            showWarning("This enrollment number is already used by another student.");
            return;
        }

        String sql =
                "UPDATE students SET "
                + "enrollment_no=?, full_name=?, gender=?, date_of_birth=?, "
                + "phone=?, email=?, department=?, semester=?, address=?, "
                + "admission_date=? WHERE student_id=?";

        try (
                Connection con = DBConnection.getConnection();
                PreparedStatement pst = con.prepareStatement(sql)
        ) {

            pst.setString(1, enrollment);
            pst.setString(2, txtFullName.getText().trim());
            pst.setString(3, getSelectedGender());
            pst.setDate(4, Date.valueOf(txtDOB.getText().trim()));
            pst.setString(5, txtPhone.getText().trim());
            pst.setString(6, txtEmail.getText().trim());
            pst.setString(7, txtDepartment.getText().trim());
            pst.setInt(8, Integer.parseInt(txtSemester.getText().trim()));
            pst.setString(9, txtAddress.getText().trim());
            pst.setDate(10, Date.valueOf(txtAdmissionDate.getText().trim()));
            pst.setInt(11, selectedStudentId);

            pst.executeUpdate();

            JOptionPane.showMessageDialog(
                    this,
                    "Student updated successfully!",
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE
            );

            clearFields();
            loadStudents();

        } catch (Exception e) {

            JOptionPane.showMessageDialog(
                    this,
                    "Unable to update student:\n" + e.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    // =====================================================
    // DELETE STUDENT
    // =====================================================

    private void deleteStudent() {

        if (selectedStudentId == -1) {
            showWarning("Please select a student first.");
            return;
        }

        int choice = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to delete this student?",
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
        );

        if (choice != JOptionPane.YES_OPTION) {
            return;
        }

        String sql = "DELETE FROM students WHERE student_id=?";

        try (
                Connection con = DBConnection.getConnection();
                PreparedStatement pst = con.prepareStatement(sql)
        ) {

            pst.setInt(1, selectedStudentId);
            pst.executeUpdate();

            JOptionPane.showMessageDialog(
                    this,
                    "Student deleted successfully!",
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE
            );

            clearFields();
            loadStudents();

        } catch (Exception e) {

            JOptionPane.showMessageDialog(
                    this,
                    "Cannot delete this student.\n"
                    + "The student may be linked to room allocation, fees, or complaints.",
                    "Delete Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    // =====================================================
    // LOAD STUDENTS
    // =====================================================

    private void loadStudents() {

        tableModel.setRowCount(0);

        String sql =
                "SELECT * FROM students ORDER BY student_id";

        try (
                Connection con = DBConnection.getConnection();
                PreparedStatement pst = con.prepareStatement(sql);
                ResultSet rs = pst.executeQuery()
        ) {

            while (rs.next()) {

                tableModel.addRow(
                        new Object[]{
                            rs.getInt("student_id"),
                            rs.getString("enrollment_no"),
                            rs.getString("full_name"),
                            rs.getString("gender"),
                            rs.getDate("date_of_birth"),
                            rs.getString("phone"),
                            rs.getString("email"),
                            rs.getString("department"),
                            rs.getInt("semester"),
                            rs.getString("address"),
                            rs.getDate("admission_date")
                        }
                );
            }

        } catch (Exception e) {

            JOptionPane.showMessageDialog(
                    this,
                    "Unable to load students:\n" + e.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    // =====================================================
    // SEARCH
    // =====================================================

    private void searchStudents() {

        String search = txtSearch.getText().trim();

        if (search.isEmpty()) {
            loadStudents();
            return;
        }

        tableModel.setRowCount(0);

        String sql =
                "SELECT * FROM students "
                + "WHERE enrollment_no LIKE ? "
                + "OR full_name LIKE ? "
                + "OR phone LIKE ? "
                + "OR department LIKE ? "
                + "ORDER BY student_id";

        try (
                Connection con = DBConnection.getConnection();
                PreparedStatement pst = con.prepareStatement(sql)
        ) {

            String pattern = "%" + search + "%";

            pst.setString(1, pattern);
            pst.setString(2, pattern);
            pst.setString(3, pattern);
            pst.setString(4, pattern);

            try (ResultSet rs = pst.executeQuery()) {

                while (rs.next()) {

                    tableModel.addRow(
                            new Object[]{
                                rs.getInt("student_id"),
                                rs.getString("enrollment_no"),
                                rs.getString("full_name"),
                                rs.getString("gender"),
                                rs.getDate("date_of_birth"),
                                rs.getString("phone"),
                                rs.getString("email"),
                                rs.getString("department"),
                                rs.getInt("semester"),
                                rs.getString("address"),
                                rs.getDate("admission_date")
                            }
                    );
                }
            }

        } catch (Exception e) {

            JOptionPane.showMessageDialog(
                    this,
                    "Search error:\n" + e.getMessage(),
                    "Search Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    // =====================================================
    // CLEAR
    // =====================================================

    private void clearFields() {

        txtEnrollment.setText("");
        txtFullName.setText("");

        genderGroup.clearSelection();

        txtDOB.setText("");
        txtPhone.setText("");
        txtEmail.setText("");
        txtDepartment.setText("");
        txtSemester.setText("");
        txtAddress.setText("");
        txtAdmissionDate.setText("");

        selectedStudentId = -1;

        studentTable.clearSelection();
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
    // MAIN
    // =====================================================

    public static void main(String[] args) {

        java.awt.EventQueue.invokeLater(() -> {
            new StudentFrame().setVisible(true);
        });
    }
}
