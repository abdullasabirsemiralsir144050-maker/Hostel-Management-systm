
package hostelmanagement.gui;

import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;

import hostelmanagement.db.DBConnection;

public class RoomFrame extends JFrame {

    private JTextField txtRoomNumber;
    private JTextField txtCapacity;
    private JTextField txtOccupiedBeds;
    private JTextField txtFloorNumber;

    private JComboBox<String> cmbRoomType;
    private JComboBox<String> cmbStatus;

    private JButton btnAdd;
    private JButton btnUpdate;
    private JButton btnDelete;
    private JButton btnClear;

    private JTable roomTable;
    private DefaultTableModel tableModel;

    private int selectedRoomId = -1;

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

    public RoomFrame() {

        setTitle("Hostel Management System - Rooms");
        setSize(1150, 750);
        setMinimumSize(new Dimension(950, 650));
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        getContentPane().setBackground(BG);

        createForm();
        loadRooms();
    }

    // ================= MAIN DESIGN =================

    private void createForm() {

        setLayout(new BorderLayout(20, 20));

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

        JLabel title = new JLabel("ROOM MANAGEMENT");
        title.setFont(new Font("SansSerif", Font.BOLD, 27));
        title.setForeground(CYAN);

        JLabel subtitle = new JLabel(
                "Manage hostel rooms, capacity and availability"
        );
        subtitle.setFont(new Font("SansSerif", Font.PLAIN, 13));
        subtitle.setForeground(MUTED);

        headingPanel.add(title);
        headingPanel.add(Box.createVerticalStrut(6));
        headingPanel.add(subtitle);

        JLabel statusLabel = new JLabel("● SYSTEM ONLINE");
        statusLabel.setFont(
                new Font("SansSerif", Font.BOLD, 11)
        );
        statusLabel.setForeground(GREEN);

        header.add(headingPanel, BorderLayout.WEST);
        header.add(statusLabel, BorderLayout.EAST);

        mainPanel.add(header, BorderLayout.NORTH);

        // ================= CENTER =================

        JPanel centerPanel = new JPanel(new BorderLayout(15, 15));
        centerPanel.setBackground(BG);

        // ================= FORM CARD =================

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

        JLabel formTitle = new JLabel("ROOM DETAILS");
        formTitle.setForeground(CYAN);
        formTitle.setFont(
                new Font("SansSerif", Font.BOLD, 16)
        );

        formCard.add(formTitle, BorderLayout.NORTH);

        JPanel formPanel = new JPanel(
                new GridLayout(2, 4, 15, 15)
        );
        formPanel.setBackground(PANEL);

        txtRoomNumber = new JTextField();
        txtCapacity = new JTextField();
        txtOccupiedBeds = new JTextField("0");
        txtFloorNumber = new JTextField();

        cmbRoomType = new JComboBox<>(
                new String[]{
                    "Single",
                    "Double",
                    "Triple",
                    "Dormitory"
                }
        );

        cmbStatus = new JComboBox<>(
                new String[]{
                    "AVAILABLE",
                    "MAINTENANCE"
                }
        );

        formPanel.add(createField("ROOM NUMBER", txtRoomNumber));
        formPanel.add(createField("ROOM TYPE", cmbRoomType));
        formPanel.add(createField("CAPACITY", txtCapacity));
        formPanel.add(createField("OCCUPIED BEDS", txtOccupiedBeds));
        formPanel.add(createField("FLOOR NUMBER", txtFloorNumber));
        formPanel.add(createField("ROOM STATUS", cmbStatus));

        JPanel formWrapper = new JPanel(new BorderLayout());
        formWrapper.setBackground(PANEL);
        formWrapper.add(formPanel, BorderLayout.CENTER);

        formCard.add(formWrapper, BorderLayout.CENTER);

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

        JLabel tableTitle = new JLabel("ROOM RECORDS");
        tableTitle.setForeground(CYAN);
        tableTitle.setFont(
                new Font("SansSerif", Font.BOLD, 16)
        );

        tableCard.add(tableTitle, BorderLayout.NORTH);

        tableModel = new DefaultTableModel(
                new String[]{
                    "ID",
                    "Room Number",
                    "Room Type",
                    "Capacity",
                    "Occupied Beds",
                    "Available Beds",
                    "Floor",
                    "Status"
                },
                0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        roomTable = new JTable(tableModel);

        roomTable.setSelectionMode(
                ListSelectionModel.SINGLE_SELECTION
        );

        roomTable.setRowHeight(32);
        roomTable.setFont(
                new Font("SansSerif", Font.PLAIN, 12)
        );

        roomTable.setBackground(FIELD);
        roomTable.setForeground(WHITE);
        roomTable.setGridColor(
                new Color(40, 60, 85)
        );

        roomTable.setSelectionBackground(
                new Color(0, 105, 125)
        );

        roomTable.setSelectionForeground(WHITE);
        roomTable.setShowVerticalLines(false);
        roomTable.setAutoResizeMode(
                JTable.AUTO_RESIZE_OFF
        );

        JTableHeader tableHeader = roomTable.getTableHeader();

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

        int[] columnWidths = {
            55, 120, 120, 90, 120, 120, 70, 130
        };

        for (int i = 0; i < columnWidths.length; i++) {
            roomTable.getColumnModel()
                    .getColumn(i)
                    .setPreferredWidth(columnWidths[i]);
        }

        JScrollPane scrollPane = new JScrollPane(roomTable);

        scrollPane.getViewport().setBackground(FIELD);
        scrollPane.setBorder(
                new LineBorder(
                        new Color(40, 65, 90), 1, true
                )
        );

        scrollPane.getVerticalScrollBar().setBackground(PANEL);
        scrollPane.getHorizontalScrollBar().setBackground(PANEL);

        tableCard.add(scrollPane, BorderLayout.CENTER);

        centerPanel.add(tableCard, BorderLayout.CENTER);

        mainPanel.add(centerPanel, BorderLayout.CENTER);

        // ================= BUTTON PANEL =================

        JPanel bottomPanel = new JPanel(
                new FlowLayout(FlowLayout.RIGHT, 12, 5)
        );
        bottomPanel.setBackground(BG);

        btnAdd = createButton("ADD ROOM", GREEN);
        btnUpdate = createButton("UPDATE", CYAN);
        btnDelete = createButton("DELETE", RED);
        btnClear = createButton("CLEAR", MUTED);

        bottomPanel.add(btnAdd);
        bottomPanel.add(btnUpdate);
        bottomPanel.add(btnDelete);
        bottomPanel.add(btnClear);

        mainPanel.add(bottomPanel, BorderLayout.SOUTH);

        add(mainPanel, BorderLayout.CENTER);

        // ================= BUTTON ACTIONS =================

        btnAdd.addActionListener(e -> addRoom());
        btnUpdate.addActionListener(e -> updateRoom());
        btnDelete.addActionListener(e -> deleteRoom());
        btnClear.addActionListener(e -> clearFields());

        // ================= TABLE SELECTION =================

        roomTable.getSelectionModel().addListSelectionListener(e -> {

            if (!e.getValueIsAdjusting()
                    && roomTable.getSelectedRow() != -1) {

                int row = roomTable.getSelectedRow();

                selectedRoomId = Integer.parseInt(
                        tableModel.getValueAt(row, 0).toString()
                );

                txtRoomNumber.setText(
                        tableModel.getValueAt(row, 1).toString()
                );

                cmbRoomType.setSelectedItem(
                        tableModel.getValueAt(row, 2).toString()
                );

                txtCapacity.setText(
                        tableModel.getValueAt(row, 3).toString()
                );

                txtOccupiedBeds.setText(
                        tableModel.getValueAt(row, 4).toString()
                );

                txtFloorNumber.setText(
                        tableModel.getValueAt(row, 6).toString()
                );

                cmbStatus.setSelectedItem(
                        tableModel.getValueAt(row, 7).toString()
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

    private JPanel createField(String name, JComponent component) {

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
                new Font("SansSerif", Font.PLAIN, 13)
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

    private JButton createButton(String text, Color color) {

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

        button.addMouseListener(new java.awt.event.MouseAdapter() {

            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                button.setBackground(color.brighter());
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                button.setBackground(color);
            }
        });

        return button;
    }

    // ================= CALCULATE STATUS =================

    private String calculateStatus(
            int capacity,
            int occupiedBeds,
            String selectedStatus) {

        if (selectedStatus.equals("MAINTENANCE")) {
            return "MAINTENANCE";
        }

        if (occupiedBeds >= capacity) {
            return "FULL";
        }

        return "AVAILABLE";
    }

    // ================= ADD ROOM =================

    private void addRoom() {

        if (!validateFields()) {
            return;
        }

        String roomNumber = txtRoomNumber.getText().trim();

        if (roomExists(roomNumber, -1)) {

            JOptionPane.showMessageDialog(
                    this,
                    "This room number already exists.",
                    "Validation",
                    JOptionPane.WARNING_MESSAGE
            );

            return;
        }

        try {

            int capacity = Integer.parseInt(txtCapacity.getText().trim());
            int occupiedBeds = Integer.parseInt(txtOccupiedBeds.getText().trim());
            int floor = Integer.parseInt(txtFloorNumber.getText().trim());

            String status = calculateStatus(
                    capacity,
                    occupiedBeds,
                    cmbStatus.getSelectedItem().toString()
            );

            String sql =
                    "INSERT INTO rooms "
                    + "(room_number, room_type, capacity, "
                    + "occupied_beds, floor_number, status) "
                    + "VALUES (?, ?, ?, ?, ?, ?)";

            try (
                    Connection con = DBConnection.getConnection();
                    PreparedStatement pst = con.prepareStatement(sql)
            ) {

                pst.setString(1, roomNumber);
                pst.setString(2, cmbRoomType.getSelectedItem().toString());
                pst.setInt(3, capacity);
                pst.setInt(4, occupiedBeds);
                pst.setInt(5, floor);
                pst.setString(6, status);

                pst.executeUpdate();

                JOptionPane.showMessageDialog(
                        this,
                        "Room added successfully!"
                );

                clearFields();
                loadRooms();
            }

        } catch (Exception e) {

            JOptionPane.showMessageDialog(
                    this,
                    "Unable to add room:\n" + e.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    // ================= UPDATE ROOM =================

    private void updateRoom() {

        if (selectedRoomId == -1) {

            JOptionPane.showMessageDialog(
                    this,
                    "Please select a room first."
            );

            return;
        }

        if (!validateFields()) {
            return;
        }

        String roomNumber = txtRoomNumber.getText().trim();

        if (roomExists(roomNumber, selectedRoomId)) {

            JOptionPane.showMessageDialog(
                    this,
                    "Another room already uses this room number."
            );

            return;
        }

        try {

            int capacity = Integer.parseInt(txtCapacity.getText().trim());
            int occupiedBeds = Integer.parseInt(txtOccupiedBeds.getText().trim());
            int floor = Integer.parseInt(txtFloorNumber.getText().trim());

            String status = calculateStatus(
                    capacity,
                    occupiedBeds,
                    cmbStatus.getSelectedItem().toString()
            );

            String sql =
                    "UPDATE rooms SET "
                    + "room_number=?, "
                    + "room_type=?, "
                    + "capacity=?, "
                    + "occupied_beds=?, "
                    + "floor_number=?, "
                    + "status=? "
                    + "WHERE room_id=?";

            try (
                    Connection con = DBConnection.getConnection();
                    PreparedStatement pst = con.prepareStatement(sql)
            ) {

                pst.setString(1, roomNumber);
                pst.setString(2, cmbRoomType.getSelectedItem().toString());
                pst.setInt(3, capacity);
                pst.setInt(4, occupiedBeds);
                pst.setInt(5, floor);
                pst.setString(6, status);
                pst.setInt(7, selectedRoomId);

                pst.executeUpdate();

                JOptionPane.showMessageDialog(
                        this,
                        "Room updated successfully!"
                );

                clearFields();
                loadRooms();
            }

        } catch (Exception e) {

            JOptionPane.showMessageDialog(
                    this,
                    "Unable to update room:\n" + e.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    // ================= DELETE ROOM =================

    private void deleteRoom() {

        if (selectedRoomId == -1) {

            JOptionPane.showMessageDialog(
                    this,
                    "Please select a room first."
            );

            return;
        }

        int choice = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to delete this room?",
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION
        );

        if (choice != JOptionPane.YES_OPTION) {
            return;
        }

        String sql = "DELETE FROM rooms WHERE room_id=?";

        try (
                Connection con = DBConnection.getConnection();
                PreparedStatement pst = con.prepareStatement(sql)
        ) {

            pst.setInt(1, selectedRoomId);
            pst.executeUpdate();

            JOptionPane.showMessageDialog(
                    this,
                    "Room deleted successfully!"
            );

            clearFields();
            loadRooms();

        } catch (Exception e) {

            JOptionPane.showMessageDialog(
                    this,
                    "This room cannot be deleted because "
                    + "it may be linked to a room allocation.\n"
                    + e.getMessage(),
                    "Delete Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    // ================= CHECK DUPLICATE ROOM =================

    private boolean roomExists(String roomNumber, int roomId) {

        String sql =
                "SELECT room_id FROM rooms "
                + "WHERE room_number=? AND room_id<>?";

        try (
                Connection con = DBConnection.getConnection();
                PreparedStatement pst = con.prepareStatement(sql)
        ) {

            pst.setString(1, roomNumber);
            pst.setInt(2, roomId);

            try (ResultSet rs = pst.executeQuery()) {
                return rs.next();
            }

        } catch (Exception e) {

            JOptionPane.showMessageDialog(
                    this,
                    "Unable to check room number:\n" + e.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE
            );

            return true;
        }
    }

    // ================= LOAD ROOMS =================

    private void loadRooms() {

        tableModel.setRowCount(0);

        String sql =
                "SELECT room_id, room_number, room_type, "
                + "capacity, occupied_beds, "
                + "(capacity - occupied_beds) AS available_beds, "
                + "floor_number, status "
                + "FROM rooms ORDER BY room_number";

        try (
                Connection con = DBConnection.getConnection();
                PreparedStatement pst = con.prepareStatement(sql);
                ResultSet rs = pst.executeQuery()
        ) {

            while (rs.next()) {

                tableModel.addRow(
                        new Object[]{
                            rs.getInt("room_id"),
                            rs.getString("room_number"),
                            rs.getString("room_type"),
                            rs.getInt("capacity"),
                            rs.getInt("occupied_beds"),
                            rs.getInt("available_beds"),
                            rs.getInt("floor_number"),
                            rs.getString("status")
                        }
                );
            }

        } catch (Exception e) {

            JOptionPane.showMessageDialog(
                    this,
                    "Unable to load rooms:\n" + e.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    // ================= VALIDATION =================

    private boolean validateFields() {

        String roomNumber = txtRoomNumber.getText().trim();

        if (roomNumber.isEmpty()) {
            showWarning("Please enter room number.");
            return false;
        }

        if (!roomNumber.matches("[A-Za-z0-9-]{1,20}")) {
            showWarning(
                    "Room number may contain only letters, numbers and '-'."
            );
            return false;
        }

        if (txtCapacity.getText().trim().isEmpty()) {
            showWarning("Please enter room capacity.");
            return false;
        }

        if (txtOccupiedBeds.getText().trim().isEmpty()) {
            showWarning("Please enter occupied beds.");
            return false;
        }

        if (txtFloorNumber.getText().trim().isEmpty()) {
            showWarning("Please enter floor number.");
            return false;
        }

        try {

            int capacity = Integer.parseInt(txtCapacity.getText().trim());
            int occupied = Integer.parseInt(txtOccupiedBeds.getText().trim());
            int floor = Integer.parseInt(txtFloorNumber.getText().trim());

            if (capacity < 1) {
                showWarning("Capacity must be at least 1.");
                return false;
            }

            if (occupied < 0) {
                showWarning("Occupied beds cannot be negative.");
                return false;
            }

            if (occupied > capacity) {
                showWarning(
                        "Occupied beds cannot be greater than capacity."
                );
                return false;
            }

            if (floor < 0 || floor > 100) {
                showWarning("Please enter a valid floor number.");
                return false;
            }

        } catch (NumberFormatException e) {

            showWarning(
                    "Capacity, occupied beds and floor number must be numbers."
            );

            return false;
        }

        return true;
    }

    // ================= CLEAR FIELDS =================

    private void clearFields() {

        txtRoomNumber.setText("");
        txtCapacity.setText("");
        txtOccupiedBeds.setText("0");
        txtFloorNumber.setText("");

        cmbRoomType.setSelectedIndex(0);
        cmbStatus.setSelectedIndex(0);

        selectedRoomId = -1;

        roomTable.clearSelection();
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

    // ================= MAIN =================

    public static void main(String[] args) {

        java.awt.EventQueue.invokeLater(() -> {
            new RoomFrame().setVisible(true);
        });
    }
}
