import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class SmartAttendanceSystem extends JFrame {
    private JTextField rollNoField, emailField;
    private JPasswordField passwordField;
    private JButton loginButton, markButton, viewButton;
    private JTable attendanceTable;
    private int loggedRollNo = -1;

    public SmartAttendanceSystem() {
        setTitle("Smart Attendance System");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new FlowLayout());
        setSize(550, 450);

        JLabel title = new JLabel("SMART ATTENDANCE SYSTEM");
        title.setFont(new Font("Arial", Font.BOLD, 16));
        add(title);

        add(new JLabel("Roll No:"));
        rollNoField = new JTextField(5);
        add(rollNoField);

        add(new JLabel("Email:"));
        emailField = new JTextField(20);
        add(emailField);

        add(new JLabel("Password:"));
        passwordField = new JPasswordField(20);
        add(passwordField);

        loginButton = new JButton("Login");
        markButton = new JButton("Mark Attendance");
        viewButton = new JButton("View Attendance");

        add(loginButton);
        add(markButton);
        add(viewButton);

        markButton.setEnabled(false);
        viewButton.setEnabled(false);

        loginButton.addActionListener(e -> login());
        markButton.addActionListener(e -> markAttendance());
        viewButton.addActionListener(e -> viewAttendance());

        attendanceTable = new JTable();
        add(new JScrollPane(attendanceTable));

        setVisible(true);
    }

    // LOGIN 
    private void login() {
        String gmail = emailField.getText();
        String pass = new String(passwordField.getPassword());
        String rollText = rollNoField.getText();

        if (rollText.isEmpty() || gmail.isEmpty() || pass.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill Roll No, Email, and Password!");
            return;
        }

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pst = conn.prepareStatement("SELECT * FROM Student WHERE Roll_No=? AND Gmail=? AND Password=?")) {

            pst.setInt(1, Integer.parseInt(rollText));
            pst.setString(2, gmail);
            pst.setString(3, pass);
            ResultSet rs = pst.executeQuery();

            if (rs.next()) {
                loggedRollNo = rs.getInt("Roll_No");
                JOptionPane.showMessageDialog(this, "Login successful!");
                markButton.setEnabled(true);
                viewButton.setEnabled(true);
            } else {
                JOptionPane.showMessageDialog(this, "Invalid Roll No, Email, or Password!");
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error during login: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    //  MARK ATTENDANCE
    private void markAttendance() {
        if (loggedRollNo == -1) {
            JOptionPane.showMessageDialog(this, "Please login first!");
            return;
        }

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pst = conn.prepareStatement("INSERT INTO Attendance (Roll_No, Status) VALUES (?, 'Present')")) {
            pst.setInt(1, loggedRollNo);
            pst.executeUpdate();
            JOptionPane.showMessageDialog(this, "Attendance marked successfully!");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error marking attendance: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    //  VIEW ATTENDANCE 
    private void viewAttendance() {
        if (loggedRollNo == -1) {
            JOptionPane.showMessageDialog(this, "Please login first!");
            return;
        }

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pst = conn.prepareStatement("SELECT * FROM Attendance WHERE Roll_No=?")) {
            pst.setInt(1, loggedRollNo);
            ResultSet rs = pst.executeQuery();

            String[] columns = {"Attendance_ID", "Roll_No", "Date", "Status"};
            DefaultTableModel model = new DefaultTableModel(columns, 0);

            while (rs.next()) {
                Object[] row = {
                    rs.getInt("Attendance_ID"),
                    rs.getInt("Roll_No"),
                    rs.getDate("Date"),
                    rs.getString("Status")
                };
                model.addRow(row);
            }

            attendanceTable.setModel(model);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error loading attendance: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    public static void main(String[] args) {
        new SmartAttendanceSystem();
    }
}
