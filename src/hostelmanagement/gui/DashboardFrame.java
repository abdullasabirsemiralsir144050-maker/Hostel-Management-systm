package hostelmanagement.gui;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import hostelmanagement.db.DBConnection;

public class DashboardFrame extends JFrame {

    private static final Color BG = new Color(10, 16, 30);
    private static final Color SIDEBAR = new Color(13, 22, 40);
    private static final Color PANEL = new Color(20, 31, 52);
    private static final Color PANEL_LIGHT = new Color(25, 39, 63);
    private static final Color CYAN = new Color(0, 220, 235);
    private static final Color BLUE = new Color(65, 130, 255);
    private static final Color GREEN = new Color(30, 210, 150);
    private static final Color ORANGE = new Color(255, 170, 65);
    private static final Color RED = new Color(255, 95, 115);
    private static final Color WHITE = new Color(238, 244, 255);
    private static final Color MUTED = new Color(145, 162, 187);

    private JLabel lblStudentsValue, lblRoomsValue, lblAllocationsValue;
    private JLabel lblOccupiedValue, lblAvailableValue, lblPendingFeesValue;
    private JLabel lblPendingComplaintsValue, lblOverdueFeesValue;
    private JLabel lblOccupancyPercent;
    private JProgressBar occupancyBar;

    public DashboardFrame() {
        setTitle("Hostel Management System | Admin Dashboard");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(1100, 720));
        setSize(1440, 900);
        setLocationRelativeTo(null);
        getContentPane().setBackground(BG);
        setLayout(new BorderLayout());
        createDashboard();
        loadStatistics();
    }

    private void createDashboard() {
        add(createSidebar(), BorderLayout.WEST);

        JPanel main = new JPanel(new BorderLayout(0, 22));
        main.setBackground(BG);
        main.setBorder(new EmptyBorder(28, 32, 24, 32));

        main.add(createHeader(), BorderLayout.NORTH);
        main.add(createMainContent(), BorderLayout.CENTER);
        main.add(createFooter(), BorderLayout.SOUTH);

        // Keep the dashboard usable on smaller screens.
        // The header and footer remain visible while the centre content scrolls.
        JScrollPane contentScroll = new JScrollPane(main);
        contentScroll.setBorder(null);
        contentScroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        contentScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        contentScroll.getViewport().setBackground(BG);
        contentScroll.getVerticalScrollBar().setUnitIncrement(20);
        add(contentScroll, BorderLayout.CENTER);
    }

    private JPanel createSidebar() {
        JPanel side = new JPanel();
        side.setPreferredSize(new Dimension(245, 0));
        side.setBackground(SIDEBAR);
        side.setLayout(new BoxLayout(side, BoxLayout.Y_AXIS));
        side.setBorder(new EmptyBorder(28, 18, 24, 18));

        JLabel brandIcon = new JLabel("⌂");
        brandIcon.setFont(new Font("SansSerif", Font.BOLD, 34));
        brandIcon.setForeground(CYAN);
        brandIcon.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel brand = new JLabel("HOSTEL");
        brand.setFont(new Font("SansSerif", Font.BOLD, 23));
        brand.setForeground(WHITE);
        brand.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel brandSub = new JLabel("MANAGEMENT SYSTEM");
        brandSub.setFont(new Font("SansSerif", Font.BOLD, 9));
        brandSub.setForeground(MUTED);
        brandSub.setAlignmentX(Component.LEFT_ALIGNMENT);

        side.add(brandIcon);
        side.add(Box.createVerticalStrut(5));
        side.add(brand);
        side.add(brandSub);
        side.add(Box.createVerticalStrut(42));

        JLabel menuLabel = new JLabel("MAIN MENU");
        menuLabel.setFont(new Font("SansSerif", Font.BOLD, 10));
        menuLabel.setForeground(new Color(100, 122, 153));
        menuLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        side.add(menuLabel);
        side.add(Box.createVerticalStrut(14));

        addNavButton(side, "▦", "Dashboard", null, true);
        addNavButton(side, "♙", "Students", this::openStudents, false);
        addNavButton(side, "▤", "Rooms", this::openRooms, false);
        addNavButton(side, "⇄", "Room Allocation", this::openAllocation, false);
        addNavButton(side, "₹", "Fees", this::openFees, false);
        addNavButton(side, "▣", "Payments", this::openPayments, false);
        addNavButton(side, "✉", "Complaints", this::openComplaints, false);
        addNavButton(side, "▥", "Reports", this::openReports, false);

        side.add(Box.createVerticalGlue());

        JPanel status = new RoundedPanel(PANEL_LIGHT, 16);
        status.setLayout(new BorderLayout(8, 5));
        status.setBorder(new EmptyBorder(13, 13, 13, 13));
        JLabel statusTitle = new JLabel("SYSTEM STATUS");
        statusTitle.setForeground(MUTED);
        statusTitle.setFont(new Font("SansSerif", Font.BOLD, 9));
        JLabel statusText = new JLabel("●  Database connected");
        statusText.setForeground(GREEN);
        statusText.setFont(new Font("SansSerif", Font.PLAIN, 11));
        status.add(statusTitle, BorderLayout.NORTH);
        status.add(statusText, BorderLayout.CENTER);
        status.setMaximumSize(new Dimension(Integer.MAX_VALUE, 65));
        side.add(status);
        side.add(Box.createVerticalStrut(15));

        JButton logout = new JButton("↪   Logout");
        styleNavButton(logout, RED);
        logout.addActionListener(e -> logout());
        side.add(logout);

        return side;
    }

    private void addNavButton(JPanel side, String icon, String text,
                              Runnable action, boolean selected) {
        JButton button = new JButton(icon + "    " + text);
        button.setHorizontalAlignment(SwingConstants.LEFT);
        button.setFont(new Font("SansSerif", selected ? Font.BOLD : Font.PLAIN, 13));
        button.setForeground(selected ? CYAN : MUTED);
        button.setBackground(selected ? new Color(22, 54, 75) : SIDEBAR);
        button.setBorder(new EmptyBorder(13, 14, 13, 10));
        button.setFocusPainted(false);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));
        button.setAlignmentX(Component.LEFT_ALIGNMENT);
        button.setOpaque(true);
        if (action != null) button.addActionListener(e -> action.run());
        button.addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) {
                if (!selected) button.setBackground(new Color(24, 39, 62));
            }
            @Override public void mouseExited(MouseEvent e) {
                if (!selected) button.setBackground(SIDEBAR);
            }
        });
        side.add(button);
        side.add(Box.createVerticalStrut(5));
    }

    private void styleNavButton(JButton button, Color foreground) {
        button.setHorizontalAlignment(SwingConstants.LEFT);
        button.setFont(new Font("SansSerif", Font.BOLD, 12));
        button.setForeground(foreground);
        button.setBackground(SIDEBAR);
        button.setBorder(new EmptyBorder(12, 14, 12, 10));
        button.setFocusPainted(false);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.setAlignmentX(Component.LEFT_ALIGNMENT);
        button.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
    }

    private JPanel createHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);

        JPanel titles = new JPanel();
        titles.setOpaque(false);
        titles.setLayout(new BoxLayout(titles, BoxLayout.Y_AXIS));

        JLabel eyebrow = new JLabel("OVERVIEW  /  ADMINISTRATION");
        eyebrow.setForeground(CYAN);
        eyebrow.setFont(new Font("SansSerif", Font.BOLD, 10));

        JLabel title = new JLabel("Dashboard");
        title.setForeground(WHITE);
        title.setFont(new Font("SansSerif", Font.BOLD, 30));

        JLabel subtitle = new JLabel("Welcome back. Here's what's happening in your hostel today.");
        subtitle.setForeground(MUTED);
        subtitle.setFont(new Font("SansSerif", Font.PLAIN, 12));

        titles.add(eyebrow);
        titles.add(Box.createVerticalStrut(5));
        titles.add(title);
        titles.add(Box.createVerticalStrut(4));
        titles.add(subtitle);
        header.add(titles, BorderLayout.WEST);

        JButton refresh = new JButton("↻   Refresh Data");
        refresh.setFont(new Font("SansSerif", Font.BOLD, 12));
        refresh.setForeground(BG);
        refresh.setBackground(CYAN);
        refresh.setBorder(new EmptyBorder(12, 18, 12, 18));
        refresh.setFocusPainted(false);
        refresh.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        refresh.addActionListener(e -> loadStatistics());
        header.add(refresh, BorderLayout.EAST);
        return header;
    }

    private JPanel createMainContent() {
        JPanel content = new JPanel(new BorderLayout(0, 18));
        content.setOpaque(false);

        JPanel stats = new JPanel(new GridLayout(1, 4, 15, 0));
        stats.setOpaque(false);

        lblStudentsValue = valueLabel("0");
        lblRoomsValue = valueLabel("0");
        lblOccupiedValue = valueLabel("0");
        lblAvailableValue = valueLabel("0");

        stats.add(createStatCard("TOTAL STUDENTS", lblStudentsValue, "♙", CYAN, "Registered residents"));
        stats.add(createStatCard("TOTAL ROOMS", lblRoomsValue, "▤", BLUE, "Rooms in the hostel"));
        stats.add(createStatCard("OCCUPIED BEDS", lblOccupiedValue, "▣", GREEN, "Currently in use"));
        stats.add(createStatCard("AVAILABLE BEDS", lblAvailableValue, "⌂", ORANGE, "Ready for allocation"));

        JPanel lower = new JPanel(new GridLayout(1, 2, 15, 0));
        lower.setOpaque(false);
        lower.add(createOccupancyPanel());
        lower.add(createActivityPanel());

        JPanel bottom = new JPanel(new GridLayout(1, 3, 15, 0));
        bottom.setOpaque(false);
        lblAllocationsValue = valueLabel("0");
        lblPendingFeesValue = valueLabel("₹0.00");
        lblPendingComplaintsValue = valueLabel("0");
        lblOverdueFeesValue = valueLabel("0");

        bottom.add(createMiniCard("ACTIVE ALLOCATIONS", lblAllocationsValue, CYAN, "Current room assignments"));
        bottom.add(createMiniCard("PENDING FEE BALANCE", lblPendingFeesValue, ORANGE, "Outstanding amount"));
        bottom.add(createMiniCard("OPEN COMPLAINTS", lblPendingComplaintsValue, RED, "Awaiting resolution"));

        JPanel stack = new JPanel(new BorderLayout(0, 18));
        stack.setOpaque(false);
        stack.add(stats, BorderLayout.NORTH);
        stack.add(lower, BorderLayout.CENTER);
        stack.add(bottom, BorderLayout.SOUTH);
        content.add(stack, BorderLayout.CENTER);
        return content;
    }

    private JLabel valueLabel(String value) {
        JLabel label = new JLabel(value);
        label.setForeground(WHITE);
        label.setFont(new Font("SansSerif", Font.BOLD, 28));
        return label;
    }

    private JPanel createStatCard(String title, JLabel value, String icon, Color accent, String note) {
        RoundedPanel card = new RoundedPanel(PANEL, 18);
        card.setLayout(new BorderLayout(8, 9));
        card.setBorder(new EmptyBorder(18, 18, 16, 18));

        JPanel top = new JPanel(new BorderLayout());
        top.setOpaque(false);
        JLabel name = new JLabel(title);
        name.setForeground(MUTED);
        name.setFont(new Font("SansSerif", Font.BOLD, 10));
        JLabel symbol = new JLabel(icon);
        symbol.setForeground(accent);
        symbol.setFont(new Font("SansSerif", Font.BOLD, 22));
        top.add(name, BorderLayout.WEST);
        top.add(symbol, BorderLayout.EAST);

        JLabel description = new JLabel(note);
        description.setForeground(MUTED);
        description.setFont(new Font("SansSerif", Font.PLAIN, 10));

        card.add(top, BorderLayout.NORTH);
        card.add(value, BorderLayout.CENTER);
        card.add(description, BorderLayout.SOUTH);
        return card;
    }

    private JPanel createMiniCard(String title, JLabel value, Color accent, String note) {
        RoundedPanel card = new RoundedPanel(PANEL, 16);
        card.setLayout(new BorderLayout(5, 7));
        card.setBorder(new EmptyBorder(15, 17, 14, 17));
        JLabel heading = new JLabel(title);
        heading.setForeground(MUTED);
        heading.setFont(new Font("SansSerif", Font.BOLD, 10));
        value.setForeground(accent);
        value.setFont(new Font("SansSerif", Font.BOLD, 23));
        JLabel sub = new JLabel(note);
        sub.setForeground(MUTED);
        sub.setFont(new Font("SansSerif", Font.PLAIN, 10));
        card.add(heading, BorderLayout.NORTH);
        card.add(value, BorderLayout.CENTER);
        card.add(sub, BorderLayout.SOUTH);
        return card;
    }

    private JPanel createOccupancyPanel() {
        RoundedPanel panel = new RoundedPanel(PANEL, 18);
        panel.setLayout(new BorderLayout(10, 15));
        panel.setBorder(new EmptyBorder(20, 21, 20, 21));

        JLabel title = sectionTitle("HOSTEL OCCUPANCY");
        JLabel sub = new JLabel("Current bed utilization");
        sub.setForeground(MUTED);
        sub.setFont(new Font("SansSerif", Font.PLAIN, 11));

        JPanel heading = new JPanel(new BorderLayout());
        heading.setOpaque(false);
        heading.add(title, BorderLayout.NORTH);
        heading.add(sub, BorderLayout.SOUTH);

        JPanel center = new JPanel();
        center.setOpaque(false);
        center.setLayout(new BoxLayout(center, BoxLayout.Y_AXIS));

        lblOccupancyPercent = new JLabel("0% occupied");
        lblOccupancyPercent.setForeground(CYAN);
        lblOccupancyPercent.setFont(new Font("SansSerif", Font.BOLD, 25));
        lblOccupancyPercent.setAlignmentX(Component.LEFT_ALIGNMENT);

        occupancyBar = new JProgressBar(0, 100);
        occupancyBar.setValue(0);
        occupancyBar.setStringPainted(false);
        occupancyBar.setForeground(CYAN);
        occupancyBar.setBackground(new Color(39, 55, 78));
        occupancyBar.setBorderPainted(false);
        occupancyBar.setPreferredSize(new Dimension(100, 13));
        occupancyBar.setMaximumSize(new Dimension(Integer.MAX_VALUE, 13));

        JLabel detail = new JLabel("Occupied beds compared with total capacity");
        detail.setForeground(MUTED);
        detail.setFont(new Font("SansSerif", Font.PLAIN, 10));

        center.add(Box.createVerticalGlue());
        center.add(lblOccupancyPercent);
        center.add(Box.createVerticalStrut(15));
        center.add(occupancyBar);
        center.add(Box.createVerticalStrut(10));
        center.add(detail);
        center.add(Box.createVerticalGlue());

        JPanel legend = new JPanel(new FlowLayout(FlowLayout.LEFT, 16, 0));
        legend.setOpaque(false);
        legend.add(legendItem(GREEN, "Occupied"));
        legend.add(legendItem(new Color(55, 75, 102), "Available"));

        panel.add(heading, BorderLayout.NORTH);
        panel.add(center, BorderLayout.CENTER);
        panel.add(legend, BorderLayout.SOUTH);
        return panel;
    }

    private JLabel sectionTitle(String text) {
        JLabel label = new JLabel(text);
        label.setForeground(WHITE);
        label.setFont(new Font("SansSerif", Font.BOLD, 13));
        return label;
    }

    private JLabel legendItem(Color color, String text) {
        JLabel label = new JLabel("●  " + text);
        label.setForeground(MUTED);
        label.setFont(new Font("SansSerif", Font.PLAIN, 10));
        label.setIconTextGap(5);
        return label;
    }

    private JPanel createActivityPanel() {
        RoundedPanel panel = new RoundedPanel(PANEL, 18);
        panel.setLayout(new BorderLayout(8, 12));
        panel.setBorder(new EmptyBorder(20, 21, 20, 21));

        JLabel title = sectionTitle("HOSTEL AT A GLANCE");
        JLabel subtitle = new JLabel("Key operational indicators");
        subtitle.setForeground(MUTED);
        subtitle.setFont(new Font("SansSerif", Font.PLAIN, 11));

        JPanel heading = new JPanel(new BorderLayout());
        heading.setOpaque(false);
        heading.add(title, BorderLayout.NORTH);
        heading.add(subtitle, BorderLayout.SOUTH);

        JPanel rows = new JPanel(new GridLayout(3, 1, 0, 8));
        rows.setOpaque(false);
        rows.add(infoRow("Active room allocations", "Live records", CYAN));
        rows.add(infoRow("Fees past due date", "Requires attention", ORANGE));
        rows.add(infoRow("Complaints not resolved", "Pending follow-up", RED));

        panel.add(heading, BorderLayout.NORTH);
        panel.add(rows, BorderLayout.CENTER);
        return panel;
    }

    private JPanel infoRow(String title, String detail, Color accent) {
        JPanel row = new JPanel(new BorderLayout(10, 3));
        row.setOpaque(false);
        JPanel text = new JPanel();
        text.setOpaque(false);
        text.setLayout(new BoxLayout(text, BoxLayout.Y_AXIS));
        JLabel name = new JLabel(title);
        name.setForeground(WHITE);
        name.setFont(new Font("SansSerif", Font.BOLD, 11));
        JLabel desc = new JLabel(detail);
        desc.setForeground(MUTED);
        desc.setFont(new Font("SansSerif", Font.PLAIN, 10));
        text.add(name);
        text.add(Box.createVerticalStrut(3));
        text.add(desc);
        JLabel dot = new JLabel("●");
        dot.setForeground(accent);
        dot.setFont(new Font("SansSerif", Font.BOLD, 17));
        row.add(dot, BorderLayout.WEST);
        row.add(text, BorderLayout.CENTER);
        return row;
    }

    private JPanel createFooter() {
        JPanel footer = new JPanel(new BorderLayout());
        footer.setOpaque(false);
        JLabel left = new JLabel("HOSTEL MANAGEMENT SYSTEM  •  ADMIN PANEL");
        left.setForeground(new Color(100, 120, 148));
        left.setFont(new Font("SansSerif", Font.BOLD, 9));
        JLabel right = new JLabel("JAVA SWING  |  JDBC  |  MYSQL");
        right.setForeground(new Color(100, 120, 148));
        right.setFont(new Font("SansSerif", Font.BOLD, 9));
        footer.add(left, BorderLayout.WEST);
        footer.add(right, BorderLayout.EAST);
        return footer;
    }

    private void loadStatistics() {
        loadStudentCount();
        loadRoomStatistics();
        loadAllocationCount();
        loadFeeStatistics();
        loadComplaintStatistics();
    }

    private void loadStudentCount() {
        String sql = "SELECT COUNT(*) AS total FROM students";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement pst = con.prepareStatement(sql);
             ResultSet rs = pst.executeQuery()) {
            if (rs.next()) lblStudentsValue.setText(String.valueOf(rs.getInt("total")));
        } catch (Exception e) {
            lblStudentsValue.setText("ERR");
            showDatabaseError(e);
        }
    }

    private void loadRoomStatistics() {
        String sql = "SELECT COUNT(*) AS total_rooms, "
                + "COALESCE(SUM(occupied_beds),0) AS occupied, "
                + "COALESCE(SUM(capacity - occupied_beds),0) AS available "
                + "FROM rooms";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement pst = con.prepareStatement(sql);
             ResultSet rs = pst.executeQuery()) {
            if (rs.next()) {
                lblRoomsValue.setText(String.valueOf(rs.getInt("total_rooms")));
                int occupied = rs.getInt("occupied");
                int available = rs.getInt("available");
                lblOccupiedValue.setText(String.valueOf(occupied));
                lblAvailableValue.setText(String.valueOf(available));
                int totalBeds = occupied + available;
                int percent = totalBeds == 0 ? 0 : (int) Math.round(occupied * 100.0 / totalBeds);
                occupancyBar.setValue(percent);
                lblOccupancyPercent.setText(percent + "% occupied");
            }
        } catch (Exception e) {
            lblRoomsValue.setText("ERR");
            lblOccupiedValue.setText("ERR");
            lblAvailableValue.setText("ERR");
            showDatabaseError(e);
        }
    }

    private void loadAllocationCount() {
        String sql = "SELECT COUNT(*) AS total FROM room_allocations WHERE status='ACTIVE'";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement pst = con.prepareStatement(sql);
             ResultSet rs = pst.executeQuery()) {
            if (rs.next()) lblAllocationsValue.setText(String.valueOf(rs.getInt("total")));
        } catch (Exception e) {
            lblAllocationsValue.setText("ERR");
            showDatabaseError(e);
        }
    }

    private void loadFeeStatistics() {
        String sql = "SELECT COALESCE(SUM(amount - paid_amount),0) AS pending_balance, "
                + "COALESCE(SUM(CASE WHEN due_date IS NOT NULL AND due_date < CURDATE() "
                + "AND paid_amount < amount THEN 1 ELSE 0 END),0) AS overdue "
                + "FROM fees WHERE paid_amount < amount";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement pst = con.prepareStatement(sql);
             ResultSet rs = pst.executeQuery()) {
            if (rs.next()) {
                BigDecimal pending = rs.getBigDecimal("pending_balance");
                if (pending == null) pending = BigDecimal.ZERO;
                lblPendingFeesValue.setText("₹" + pending.setScale(2, java.math.RoundingMode.HALF_UP));
                lblOverdueFeesValue.setText(String.valueOf(rs.getInt("overdue")));
            }
        } catch (Exception e) {
            lblPendingFeesValue.setText("ERR");
            lblOverdueFeesValue.setText("ERR");
            showDatabaseError(e);
        }
    }

    private void loadComplaintStatistics() {
        String sql = "SELECT COUNT(*) AS total FROM complaints WHERE status <> 'RESOLVED'";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement pst = con.prepareStatement(sql);
             ResultSet rs = pst.executeQuery()) {
            if (rs.next()) lblPendingComplaintsValue.setText(String.valueOf(rs.getInt("total")));
        } catch (Exception e) {
            lblPendingComplaintsValue.setText("ERR");
            showDatabaseError(e);
        }
    }

    private void showDatabaseError(Exception e) {
        System.err.println("Dashboard data loading error: " + e.getMessage());
    }

    private void openStudents() { new StudentFrame().setVisible(true); }
    private void openRooms() { new RoomFrame().setVisible(true); }
    private void openAllocation() { new RoomAllocationFrame().setVisible(true); }
    private void openFees() { new FeeFrame().setVisible(true); }
    private void openPayments() { new PaymentFrame().setVisible(true); }
    private void openComplaints() { new ComplaintFrame().setVisible(true); }
    private void openReports() { new ReportFrame().setVisible(true); }

    private void logout() {
        int choice = JOptionPane.showConfirmDialog(
                this, "Are you sure you want to logout?",
                "Confirm Logout", JOptionPane.YES_NO_OPTION);
        if (choice == JOptionPane.YES_OPTION) {
            dispose();
            new LoginFrame().setVisible(true);
        }
    }

    private static class RoundedPanel extends JPanel {
        private final Color background;
        private final int radius;

        RoundedPanel(Color background, int radius) {
            this.background = background;
            this.radius = radius;
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(background);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), radius, radius);
            g2.dispose();
            super.paintComponent(g);
        }
    }

    public static void main(String[] args) {
        EventQueue.invokeLater(() -> new DashboardFrame().setVisible(true));
    }
}
