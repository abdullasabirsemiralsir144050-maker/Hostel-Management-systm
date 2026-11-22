package hostelmanagement.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {

    private static final String URL =
            "jdbc:mysql://localhost:3306/hostel-management";

    private static final String USER = "hostel_app";

    private static final String PASSWORD =
            "HostelDB@2026#Secure";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}