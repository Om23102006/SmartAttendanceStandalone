import java.sql.*;

public class DBConnection {
    public static Connection getConnection() {
        Connection conn = null;
        try {
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
            String url = "jdbc:sqlserver://localhost:1433;databaseName=master;encrypt=false;integratedSecurity=true;";
            conn = DriverManager.getConnection(url);
        } catch (Exception e) {
            System.out.println("? Database connection failed!");
            e.printStackTrace();
        }
        return conn;
    }
}

