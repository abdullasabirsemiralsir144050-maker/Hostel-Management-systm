package hostelmanagement.db;

import java.sql.Connection;

public class DBTest {

    public static void main(String[] args) {

        try (Connection con = DBConnection.getConnection()) {

            if (con != null && !con.isClosed()) {
                System.out.println("SUCCESS!");
                System.out.println("Connected to Hostel Management Database.");
            }

        } catch (Exception e) {
            System.out.println("DATABASE CONNECTION FAILED!");
            e.printStackTrace();
        }
    }
}