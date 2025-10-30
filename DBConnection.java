import java.sql.Connection;
import java.sql.DriverManager;

public class DBConnection {
    public static Connection getConnection() {
        Connection conn = null;
        try {
            // Load SQL Server JDBC Driver
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");

            // Connection string for Windows Authentication
            String url = "jdbc:sqlserver://localhost:1433;"
                       + "databaseName=SmartAttendanceSystemDB;"
                       + "integratedSecurity=true;"
                       + "encrypt=false;";

            conn = DriverManager.getConnection(url);
            System.out.println(" Connected to SQL Server successfully!");
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println(" Database connection failed!");
        }
        return conn;
    }
}
