
package hostelmanagement.gui;

import java.awt.*;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDate;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;

import hostelmanagement.db.DBConnection;

public class RoomAllocationFrame extends JFrame {

    private JComboBox<StudentItem> cmbStudent;
    private JComboBox<RoomItem> cmbRoom;

    private JTextField txtBedNumber;
    private JTextField txtAllocationDate;

    private JButton btnAllocate;
    private JButton btnVacate;
    private JButton btnClear;
    private JButton btnRefresh;

    private JTable allocationTable;
    private DefaultTableModel tableModel;

    private int selectedAllocationId = -1;

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

    private static final Color RED =
            new Color(235, 75, 90);

    // ================= CONSTRUCTOR =================

    public RoomAllocationFrame() {

        setTitle("Hostel Management System - Room Allocation");
        setSize(1150, 750);
        setMinimumSize(new Dimension(950, 650));
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        getContentPane().setBackground(BG);

        createForm();

        loadStudents();
        loadRooms();
        loadAllocations();
    }

    // ================= MAIN DESIGN =================

    private void createForm() {

        setLayout(new BorderLayout());

        JPanel mainPanel = new JPanel(new BorderLayout(20, 20));
        mainPanel.setBackground(BG);
        mainPanel.setBorder(
                new EmptyBorder(25, 30, 25, 30)
        );

        // ================= HEADER =================

        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(BG);

        JPanel headingPanel = new JPanel();
        headingPanel.setLayout(
                new BoxLayout(headingPanel, BoxLayout.Y_AXIS)
        );
        headingPanel.setBackground(BG);

        JLabel title = new JLabel("ROOM ALLOCATION");

        title.setFont(
                new Font("SansSerif", Font.BOLD, 27)
        );
        title.setForeground(CYAN);

        JLabel subtitle = new JLabel(
                "Allocate hostel rooms and manage student occupancy"
        );

        subtitle.setFont(
                new Font("SansSerif", Font.PLAIN, 13)
        );
        subtitle.setForeground(MUTED);

        headingPanel.add(title);
        headingPanel.add(Box.createVerticalStrut(6));
        headingPanel.add(subtitle);

        JLabel systemStatus = new JLabel("● SYSTEM ONLINE");

        systemStatus.setFont(
                new Font("SansSerif", Font.BOLD, 11)
        );
        systemStatus.setForeground(GREEN);

        header.add(headingPanel, BorderLayout.WEST);
        header.add(systemStatus, BorderLayout.EAST);

        mainPanel.add(header, BorderLayout.NORTH);

        // ================= CENTER =================

        JPanel centerPanel = new JPanel(new BorderLayout(15, 15));
        centerPanel.setBackground(BG);

        // ================= ALLOCATION FORM CARD =================

        JPanel formCard = createCard();

        formCard.setLayout(new BorderLayout(10, 15));

        formCard.setBorder(
                new CompoundBorder(
                        new LineBorder(
                                new Color(40, 65, 90), 1, true
                        ),
                        new EmptyBorder(20, 22, 20, 22)
                )
        );

        JLabel formTitle = new JLabel("ALLOCATION DETAILS");

        formTitle.setForeground(CYAN);
        formTitle.setFont(
                new Font("SansSerif", Font.BOLD, 16)
        );

        formCard.add(formTitle, BorderLayout.NORTH);

        JPanel formPanel = new JPanel(
                new GridLayout(1, 4, 15, 15)
        );

        formPanel.setBackground(PANEL);

        cmbStudent = new JComboBox<>();
        cmbRoom = new JComboBox<>();

        txtBedNumber = new JTextField();
        txtAllocationDate = new JTextField();

        txtAllocationDate.setText(
                LocalDate.now().toString()
        );

        formPanel.add(
                createField("SELECT STUDENT", cmbStudent)
        );

        formPanel.add(
                createField("SELECT AVAILABLE ROOM", cmbRoom)
        );

        formPanel.add(
                createField("BED NUMBER", txtBedNumber)
        );

        formPanel.add(
                createField("ALLOCATION DATE (YYYY-MM-DD)", txtAllocationDate)
        );

        formCard.add(formPanel, BorderLayout.CENTER);

        centerPanel.add(formCard, BorderLayout.NORTH);

        // ================= TABLE CARD =================

        JPanel tableCard = createCard();

        tableCard.setLayout(new BorderLayout(10, 15));

        tableCard.setBorder(
                new CompoundBorder(
                        new LineBorder(
                                new Color(40, 65, 90), 1, true
                        ),
                        new EmptyBorder(18, 20, 18, 20)
                )
        );

        JLabel tableTitle = new JLabel("ALLOCATION RECORDS");

        tableTitle.setForeground(CYAN);
        tableTitle.setFont(
                new Font("SansSerif", Font.BOLD, 16)
        );

        tableCard.add(tableTitle, BorderLayout.NORTH);

        tableModel = new DefaultTableModel(
                new String[]{
                    "ID",
                    "Enrollment No",
                    "Student",
                    "Room",
                    "Bed",
                    "Allocation Date",
                    "Vacating Date",
                    "Status"
                },
                0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        allocationTable = new JTable(tableModel);

        allocationTable.setSelectionMode(
                ListSelectionModel.SINGLE_SELECTION
        );

        allocationTable.setRowHeight(32);

        allocationTable.setFont(
                new Font("SansSerif", Font.PLAIN, 12)
        );

        allocationTable.setBackground(FIELD);
        allocationTable.setForeground(WHITE);

        allocationTable.setGridColor(
                new Color(40, 60, 85)
        );

        allocationTable.setSelectionBackground(
                new Color(0, 105, 125)
        );

        allocationTable.setSelectionForeground(WHITE);

        allocationTable.setShowVerticalLines(false);

        allocationTable.setAutoResizeMode(
                JTable.AUTO_RESIZE_OFF
        );

        JTableHeader tableHeader =
                allocationTable.getTableHeader();

        tableHeader.setBackground(
                new Color(25, 48, 70)
        );

        tableHeader.setForeground(CYAN);

        tableHeader.setFont(
                new Font("SansSerif", Font.BOLD, 12)
        );

        tableHeader.setPreferredSize(
                new Dimension(100, 38)
        );

        tableHeader.setReorderingAllowed(false);

        int[] widths = {
            55, 125, 150, 90, 65, 125, 125, 100
        };

        for (int i = 0; i < widths.length; i++) {

            allocationTable.getColumnModel()
                    .getColumn(i)
                    .setPreferredWidth(widths[i]);
        }

        JScrollPane scrollPane =
                new JScrollPane(allocationTable);

        scrollPane.getViewport().setBackground(FIELD);

        scrollPane.setBorder(
                new LineBorder(
                        new Color(40, 65, 90), 1, true
                )
        );

        tableCard.add(scrollPane, BorderLayout.CENTER);

        centerPanel.add(tableCard, BorderLayout.CENTER);

        mainPanel.add(centerPanel, BorderLayout.CENTER);

        // ================= BUTTONS =================

        JPanel buttonPanel = new JPanel(
                new FlowLayout(FlowLayout.RIGHT, 12, 5)
        );

        buttonPanel.setBackground(BG);

        btnAllocate = createButton("ALLOCATE", GREEN);
        btnVacate = createButton("VACATE", RED);
        btnRefresh = createButton("REFRESH", CYAN);
        btnClear = createButton("CLEAR", MUTED);

        buttonPanel.add(btnAllocate);
        buttonPanel.add(btnVacate);
        buttonPanel.add(btnRefresh);
        buttonPanel.add(btnClear);

        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(mainPanel, BorderLayout.CENTER);

        // ================= ACTIONS =================

        btnAllocate.addActionListener(e -> allocateRoom());
        btnVacate.addActionListener(e -> vacateRoom());
        btnClear.addActionListener(e -> clearFields());
        btnRefresh.addActionListener(e -> refreshData());

        // ================= TABLE SELECTION =================

        allocationTable.getSelectionModel()
                .addListSelectionListener(e -> {

                    if (!e.getValueIsAdjusting()
                            && allocationTable.getSelectedRow() != -1) {

                        int row = allocationTable.getSelectedRow();

                        selectedAllocationId = Integer.parseInt(
                                tableModel.getValueAt(row, 0).toString()
                        );
                    }
                });
    }

    // ================= CREATE CARD =================

    private JPanel createCard() {

        JPanel panel = new JPanel();
        panel.setBackground(PANEL);

        return panel;
    }

    // ================= CREATE FIELD =================

    private JPanel createField(
            String name,
            JComponent component) {

        JPanel panel = new JPanel();

        panel.setLayout(
                new BoxLayout(panel, BoxLayout.Y_AXIS)
        );

        panel.setBackground(PANEL);

        JLabel label = new JLabel(name);

        label.setForeground(CYAN);
        label.setFont(
                new Font("SansSerif", Font.BOLD, 10)
        );

        component.setFont(
                new Font("SansSerif", Font.PLAIN, 12)
        );

        component.setForeground(WHITE);
        component.setBackground(FIELD);

        component.setBorder(
                new CompoundBorder(
                        new LineBorder(
                                new Color(55, 80, 105), 1, true
                        ),
                        new EmptyBorder(8, 10, 8, 10)
                )
        );

        component.setMaximumSize(
                new Dimension(Integer.MAX_VALUE, 40)
        );

        panel.add(label);
        panel.add(Box.createVerticalStrut(7));
        panel.add(component);

        return panel;
    }

    // ================= CREATE BUTTON =================

    private JButton createButton(
            String text,
            Color color) {

        JButton button = new JButton(text);

        button.setFont(
                new Font("SansSerif", Font.BOLD, 11)
        );

        button.setForeground(BG);
        button.setBackground(color);

        button.setFocusPainted(false);
        button.setBorderPainted(false);

        button.setCursor(
                new Cursor(Cursor.HAND_CURSOR)
        );

        button.setPreferredSize(
                new Dimension(125, 42)
        );

        button.addMouseListener(
                new java.awt.event.MouseAdapter() {

            @Override
            public void mouseEntered(
                    java.awt.event.MouseEvent e) {

                button.setBackground(color.brighter());
            }

            @Override
            public void mouseExited(
                    java.awt.event.MouseEvent e) {

                button.setBackground(color);
            }
        });

        return button;
    }

    // ================= LOAD STUDENTS =================

    private void loadStudents() {

        cmbStudent.removeAllItems();

        String sql =
                "SELECT student_id, enrollment_no, full_name "
                + "FROM students ORDER BY full_name";

        try (
                Connection con = DBConnection.getConnection();
                PreparedStatement pst = con.prepareStatement(sql);
                ResultSet rs = pst.executeQuery()
        ) {

            while (rs.next()) {

                cmbStudent.addItem(
                        new StudentItem(
                                rs.getInt("student_id"),
                                rs.getString("enrollment_no"),
                                rs.getString("full_name")
                        )
                );
            }

        } catch (Exception e) {

            showError(
                    "Unable to load students:\n" + e.getMessage()
            );
        }
    }

    // ================= LOAD AVAILABLE ROOMS =================

    private void loadRooms() {

        cmbRoom.removeAllItems();

        String sql =
                "SELECT room_id, room_number, capacity, "
                + "occupied_beds, status "
                + "FROM rooms "
                + "WHERE occupied_beds < capacity "
                + "AND status <> 'MAINTENANCE' "
                + "ORDER BY room_number";

        try (
                Connection con = DBConnection.getConnection();
                PreparedStatement pst = con.prepareStatement(sql);
                ResultSet rs = pst.executeQuery()
        ) {

            while (rs.next()) {

                cmbRoom.addItem(
                        new RoomItem(
                                rs.getInt("room_id"),
                                rs.getString("room_number"),
                                rs.getInt("capacity"),
                                rs.getInt("occupied_beds")
                        )
                );
            }

        } catch (Exception e) {

            showError(
                    "Unable to load rooms:\n" + e.getMessage()
            );
        }
    }

    // ================= LOAD ALLOCATIONS =================

    private void loadAllocations() {

        tableModel.setRowCount(0);

        String sql =
                "SELECT ra.allocation_id, s.enrollment_no, "
                + "s.full_name, r.room_number, ra.bed_number, "
                + "ra.allocation_date, ra.vacating_date, ra.status "
                + "FROM room_allocations ra "
                + "INNER JOIN students s "
                + "ON ra.student_id = s.student_id "
                + "INNER JOIN rooms r "
                + "ON ra.room_id = r.room_id "
                + "ORDER BY ra.allocation_id DESC";

        try (
                Connection con = DBConnection.getConnection();
                PreparedStatement pst = con.prepareStatement(sql);
                ResultSet rs = pst.executeQuery()
        ) {

            while (rs.next()) {

                tableModel.addRow(
                        new Object[]{
                            rs.getInt("allocation_id"),
                            rs.getString("enrollment_no"),
                            rs.getString("full_name"),
                            rs.getString("room_number"),
                            rs.getInt("bed_number"),
                            rs.getDate("allocation_date"),
                            rs.getDate("vacating_date"),
                            rs.getString("status")
                        }
                );
            }

        } catch (Exception e) {

            showError(
                    "Unable to load allocations:\n" + e.getMessage()
            );
        }
    }

    // ================= ALLOCATE ROOM =================

    private void allocateRoom() {

        StudentItem student =
                (StudentItem) cmbStudent.getSelectedItem();

        RoomItem room =
                (RoomItem) cmbRoom.getSelectedItem();

        if (student == null) {
            showWarning("Please select a student.");
            return;
        }

        if (room == null) {
            showWarning("Please select an available room.");
            return;
        }

        String bedText = txtBedNumber.getText().trim();
        String dateText = txtAllocationDate.getText().trim();

        if (bedText.isEmpty() || dateText.isEmpty()) {

            showWarning(
                    "Please enter bed number and allocation date."
            );

            return;
        }

        int bedNumber;

        try {

            bedNumber = Integer.parseInt(bedText);

        } catch (NumberFormatException e) {

            showWarning("Bed number must be a number.");
            return;
        }

        if (bedNumber < 1 || bedNumber > room.capacity) {

            showWarning(
                    "Bed number must be between 1 and "
                    + room.capacity + "."
            );

            return;
        }

        LocalDate allocationLocalDate;

        try {

            allocationLocalDate = LocalDate.parse(dateText);

        } catch (Exception e) {

            showWarning(
                    "Allocation date must be YYYY-MM-DD."
            );

            return;
        }

        if (allocationLocalDate.isAfter(LocalDate.now())) {

            showWarning(
                    "Allocation date cannot be in the future."
            );

            return;
        }

        Date allocationDate = Date.valueOf(allocationLocalDate);

        try (Connection con = DBConnection.getConnection()) {

            con.setAutoCommit(false);

            try {

                // CHECK STUDENT ACTIVE ALLOCATION

                String checkStudent =
                        "SELECT allocation_id FROM room_allocations "
                        + "WHERE student_id=? AND status='ACTIVE' "
                        + "FOR UPDATE";

                try (PreparedStatement pst =
                        con.prepareStatement(checkStudent)) {

                    pst.setInt(1, student.studentId);

                    try (ResultSet rs = pst.executeQuery()) {

                        if (rs.next()) {

                            con.rollback();

                            showWarning(
                                    "This student already has an active room."
                            );

                            return;
                        }
                    }
                }

                // CHECK ROOM

                String checkRoom =
                        "SELECT capacity, occupied_beds, status "
                        + "FROM rooms WHERE room_id=? FOR UPDATE";

                int capacity;
                int occupiedBeds;
                String status;

                try (PreparedStatement pst =
                        con.prepareStatement(checkRoom)) {

                    pst.setInt(1, room.roomId);

                    try (ResultSet rs = pst.executeQuery()) {

                        if (!rs.next()) {

                            con.rollback();

                            showWarning(
                                    "Selected room does not exist."
                            );

                            return;
                        }

                        capacity = rs.getInt("capacity");
                        occupiedBeds = rs.getInt("occupied_beds");
                        status = rs.getString("status");
                    }
                }

                if (status.equalsIgnoreCase("MAINTENANCE")) {

                    con.rollback();

                    showWarning("This room is under maintenance.");

                    return;
                }

                if (occupiedBeds >= capacity) {

                    con.rollback();

                    showWarning("This room is full.");

                    return;
                }

                if (bedNumber > capacity) {

                    con.rollback();

                    showWarning("Invalid bed number.");

                    return;
                }

                // CHECK BED

                String checkBed =
                        "SELECT allocation_id FROM room_allocations "
                        + "WHERE room_id=? AND bed_number=? "
                        + "AND status='ACTIVE' FOR UPDATE";

                try (PreparedStatement pst =
                        con.prepareStatement(checkBed)) {

                    pst.setInt(1, room.roomId);
                    pst.setInt(2, bedNumber);

                    try (ResultSet rs = pst.executeQuery()) {

                        if (rs.next()) {

                            con.rollback();

                            showWarning(
                                    "This bed is already occupied."
                            );

                            return;
                        }
                    }
                }

                // INSERT ALLOCATION

                String insert =
                        "INSERT INTO room_allocations "
                        + "(student_id, room_id, allocation_date, "
                        + "bed_number, status) "
                        + "VALUES (?, ?, ?, ?, 'ACTIVE')";

                try (PreparedStatement pst =
                        con.prepareStatement(insert)) {

                    pst.setInt(1, student.studentId);
                    pst.setInt(2, room.roomId);
                    pst.setDate(3, allocationDate);
                    pst.setInt(4, bedNumber);

                    pst.executeUpdate();
                }

                // UPDATE ROOM OCCUPANCY

                int newOccupied = occupiedBeds + 1;

                String newStatus =
                        newOccupied >= capacity
                        ? "FULL"
                        : "AVAILABLE";

                String updateRoom =
                        "UPDATE rooms SET occupied_beds=?, "
                        + "status=? WHERE room_id=?";

                try (PreparedStatement pst =
                        con.prepareStatement(updateRoom)) {

                    pst.setInt(1, newOccupied);
                    pst.setString(2, newStatus);
                    pst.setInt(3, room.roomId);

                    pst.executeUpdate();
                }

                con.commit();

                JOptionPane.showMessageDialog(
                        this,
                        "Room allocated successfully!",
                        "Success",
                        JOptionPane.INFORMATION_MESSAGE
                );

                clearFields();

                loadRooms();
                loadAllocations();

            } catch (Exception e) {

                con.rollback();

                showError(
                        "Allocation failed:\n" + e.getMessage()
                );
            }

        } catch (Exception e) {

            showError(
                    "Database error:\n" + e.getMessage()
            );
        }
    }

    // ================= VACATE ROOM =================

    private void vacateRoom() {

        if (selectedAllocationId == -1) {

            showWarning(
                    "Please select an allocation first."
            );

            return;
        }

        int choice = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to vacate this room allocation?",
                "Confirm Vacate",
                JOptionPane.YES_NO_OPTION
        );

        if (choice != JOptionPane.YES_OPTION) {
            return;
        }

        try (Connection con = DBConnection.getConnection()) {

            con.setAutoCommit(false);

            try {

                // GET ACTIVE ALLOCATION

                String findSql =
                        "SELECT room_id FROM room_allocations "
                        + "WHERE allocation_id=? "
                        + "AND status='ACTIVE' FOR UPDATE";

                int roomId;

                try (PreparedStatement pst =
                        con.prepareStatement(findSql)) {

                    pst.setInt(1, selectedAllocationId);

                    try (ResultSet rs = pst.executeQuery()) {

                        if (!rs.next()) {

                            con.rollback();

                            showWarning(
                                    "This allocation is already vacated "
                                    + "or does not exist."
                            );

                            return;
                        }

                        roomId = rs.getInt("room_id");
                    }
                }

                // UPDATE ALLOCATION

                String updateAllocation =
                        "UPDATE room_allocations "
                        + "SET vacating_date=CURDATE(), "
                        + "status='VACATED' "
                        + "WHERE allocation_id=? "
                        + "AND status='ACTIVE'";

                try (PreparedStatement pst =
                        con.prepareStatement(updateAllocation)) {

                    pst.setInt(1, selectedAllocationId);

                    pst.executeUpdate();
                }

                // GET ROOM DETAILS

                String roomSql =
                        "SELECT capacity, occupied_beds, status "
                        + "FROM rooms WHERE room_id=? FOR UPDATE";

                int capacity;
                int occupiedBeds;
                String roomStatus;

                try (PreparedStatement pst =
                        con.prepareStatement(roomSql)) {

                    pst.setInt(1, roomId);

                    try (ResultSet rs = pst.executeQuery()) {

                        if (!rs.next()) {

                            con.rollback();

                            showWarning(
                                    "The related room no longer exists."
                            );

                            return;
                        }

                        capacity = rs.getInt("capacity");
                        occupiedBeds = rs.getInt("occupied_beds");
                        roomStatus = rs.getString("status");
                    }
                }

                int newOccupied = Math.max(0, occupiedBeds - 1);

                String newStatus;

                if (roomStatus.equalsIgnoreCase("MAINTENANCE")) {

                    newStatus = "MAINTENANCE";

                } else if (newOccupied >= capacity) {

                    newStatus = "FULL";

                } else {

                    newStatus = "AVAILABLE";
                }

                // UPDATE ROOM

                String updateRoom =
                        "UPDATE rooms SET occupied_beds=?, "
                        + "status=? WHERE room_id=?";

                try (PreparedStatement pst =
                        con.prepareStatement(updateRoom)) {

                    pst.setInt(1, newOccupied);
                    pst.setString(2, newStatus);
                    pst.setInt(3, roomId);

                    pst.executeUpdate();
                }

                con.commit();

                JOptionPane.showMessageDialog(
                        this,
                        "Room vacated successfully!",
                        "Success",
                        JOptionPane.INFORMATION_MESSAGE
                );

                clearFields();

                loadRooms();
                loadAllocations();

            } catch (Exception e) {

                con.rollback();

                showError(
                        "Vacating failed:\n" + e.getMessage()
                );
            }

        } catch (Exception e) {

            showError(
                    "Database error:\n" + e.getMessage()
            );
        }
    }

    // ================= CLEAR =================

    private void clearFields() {

        txtBedNumber.setText("");

        txtAllocationDate.setText(
                LocalDate.now().toString()
        );

        selectedAllocationId = -1;

        allocationTable.clearSelection();

        if (cmbStudent.getItemCount() > 0) {
            cmbStudent.setSelectedIndex(0);
        }

        if (cmbRoom.getItemCount() > 0) {
            cmbRoom.setSelectedIndex(0);
        }
    }

    // ================= REFRESH =================

    private void refreshData() {

        loadStudents();
        loadRooms();
        loadAllocations();

        selectedAllocationId = -1;

        allocationTable.clearSelection();
    }

    // ================= WARNING =================

    private void showWarning(String message) {

        JOptionPane.showMessageDialog(
                this,
                message,
                "Validation",
                JOptionPane.WARNING_MESSAGE
        );
    }

    // ================= ERROR =================

    private void showError(String message) {

        JOptionPane.showMessageDialog(
                this,
                message,
                "Room Allocation Error",
                JOptionPane.ERROR_MESSAGE
        );
    }

    // ================= STUDENT ITEM =================

    private static class StudentItem {

        int studentId;
        String enrollmentNo;
        String fullName;

        StudentItem(
                int studentId,
                String enrollmentNo,
                String fullName) {

            this.studentId = studentId;
            this.enrollmentNo = enrollmentNo;
            this.fullName = fullName;
        }

        @Override
        public String toString() {

            return enrollmentNo + " - " + fullName;
        }
    }

    // ================= ROOM ITEM =================

    private static class RoomItem {

        int roomId;
        String roomNumber;
        int capacity;
        int occupiedBeds;

        RoomItem(
                int roomId,
                String roomNumber,
                int capacity,
                int occupiedBeds) {

            this.roomId = roomId;
            this.roomNumber = roomNumber;
            this.capacity = capacity;
            this.occupiedBeds = occupiedBeds;
        }

        @Override
        public String toString() {

            return roomNumber
                    + " ("
                    + occupiedBeds
                    + "/"
                    + capacity
                    + " occupied)";
        }
    }

    // ================= MAIN =================

    public static void main(String[] args) {

        java.awt.EventQueue.invokeLater(() -> {
            new RoomAllocationFrame().setVisible(true);
        });
    }
}
