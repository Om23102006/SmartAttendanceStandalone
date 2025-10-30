import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class SmartAttendanceStandalone extends JFrame {
    private JTable table;
    private DefaultTableModel model;

    public SmartAttendanceStandalone() {
        setTitle("Smart Attendance System - Student Info");
        setSize(700, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Layout
        setLayout(new BorderLayout());

        // Buttons
        JPanel buttonPanel = new JPanel();
        JButton showButton = new JButton("Show All Students");
        JButton addButton = new JButton("Add Student");
        JButton deleteButton = new JButton("Delete Student");

        buttonPanel.add(showButton);
        buttonPanel.add(addButton);
        buttonPanel.add(deleteButton);

        add(buttonPanel, BorderLayout.NORTH);

        // Table
        model = new DefaultTableModel(new String[]{"Roll_No", "Student_Name", "Gmail", "Password"}, 0);
        table = new JTable(model);
        add(new JScrollPane(table), BorderLayout.CENTER);

        // Button Actions
        showButton.addActionListener(e -> refreshReport());
        addButton.addActionListener(e -> addStudent());
        deleteButton.addActionListener(e -> deleteStudent());
    }

    private void refreshReport() {
        try {
            Connection conn = DBConnection.getConnection();
            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery("SELECT Roll_No, Student_Name, Gmail, Password FROM Student");

            // Clear table
            model.setRowCount(0);

            while (rs.next()) {
                int roll = rs.getInt("Roll_No");
                String name = rs.getString("Student_Name");
                String gmail = rs.getString("Gmail");
                String password = rs.getString("Password");

                model.addRow(new Object[]{roll, name, gmail, password});
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error fetching data: " + ex.getMessage());
        }
    }

    private void addStudent() {
        try {
            String roll = JOptionPane.showInputDialog(this, "Enter Roll No:");
            String name = JOptionPane.showInputDialog(this, "Enter Student Name:");
            String gmail = JOptionPane.showInputDialog(this, "Enter Gmail:");
            String password = JOptionPane.showInputDialog(this, "Enter Password:");

            if (roll == null || name == null || gmail == null || password == null ||
                roll.isEmpty() || name.isEmpty() || gmail.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(this, "All fields are required!");
                return;
            }

            Connection conn = DBConnection.getConnection();
            String query = "INSERT INTO Student (Roll_No, Student_Name, Gmail, Password) VALUES (?, ?, ?, ?)";
            PreparedStatement pst = conn.prepareStatement(query);
            pst.setInt(1, Integer.parseInt(roll));
            pst.setString(2, name);
            pst.setString(3, gmail);
            pst.setString(4, password);
            pst.executeUpdate();

            JOptionPane.showMessageDialog(this, " Student added successfully!");
            refreshReport();
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error adding student: " + ex.getMessage());
        }
    }

    private void deleteStudent() {
        try {
            String roll = JOptionPane.showInputDialog(this, "Enter Roll No to Delete:");
            if (roll == null || roll.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Roll No required!");
                return;
            }

            Connection conn = DBConnection.getConnection();
            String query = "DELETE FROM Student WHERE Roll_No = ?";
            PreparedStatement pst = conn.prepareStatement(query);
            pst.setInt(1, Integer.parseInt(roll));

            int rows = pst.executeUpdate();
            if (rows > 0)
                JOptionPane.showMessageDialog(this, " Student deleted successfully!");
            else
                JOptionPane.showMessageDialog(this, "No student found with Roll No: " + roll);

            refreshReport();
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error deleting student: " + ex.getMessage());
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new SmartAttendanceStandalone().setVisible(true));
    }
}
