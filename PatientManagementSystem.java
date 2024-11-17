import java.sql.*;
import java.util.Scanner;
public class PatientManagementSystem {
    private static final String URL = "jdbc:mysql://localhost:3306/HospitalDB";
    private static final String USER = "root"; 
    private static final String PASSWORD = "1234";
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        while (true) {
            System.out.println("1. Add Patient");
            System.out.println("2. Add Visit");
            System.out.println("3. View Visits by Patient ID");
            System.out.println("4. Update Visit Details");
            System.out.println("5. Delete Visit");
            System.out.println("6. Exit");
            System.out.print("Enter choice: ");
            int choice = sc.nextInt();
            sc.nextLine(); 
            try {
                switch (choice) {
                    case 1 -> addPatient(sc);
                    case 2 -> addVisit(sc);
                    case 3 -> viewVisitsByPatientId(sc);
                    case 4 -> updateVisitDetails(sc);
                    case 5 -> deleteVisit(sc);
                    case 6 -> {
                        System.out.println("Exiting...");
                        sc.close();
                        return;
                    }
                    default -> System.out.println("Invalid choice.");
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private static void addPatient(Scanner sc) throws SQLException
 {
    System.out.print("Enter patient name: ");
    String name = sc.nextLine();
    System.out.print("Enter age: ");
    int age = sc.nextInt();
    sc.nextLine();
    System.out.print("Enter gender: ");
    String gender = sc.nextLine();

    String query = "INSERT INTO patients (name, age, gender) VALUES (?, ?, ?)";

    try (Connection conn = getConnection();
         PreparedStatement stmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
        stmt.setString(1, name);
        stmt.setInt(2, age);
        stmt.setString(3, gender);
        stmt.executeUpdate();
        ResultSet rs = stmt.getGeneratedKeys();
        if (rs.next()) {
            int patientId = rs.getInt(1); 
            System.out.println("Patient added successfully with Patient ID: " + patientId);
        } else {
            System.out.println("Patient added, but unable to retrieve Patient ID.");
        }
    }
}
    private static void addVisit(Scanner sc) throws SQLException {
        System.out.print("Enter patient ID: ");
        int patientId = sc.nextInt();
        sc.nextLine(); 
        System.out.print("Enter visit date (YYYY-MM-DD): ");
        String date = sc.nextLine();
        System.out.print("Enter reason for visit: ");
        String reason = sc.nextLine();
        System.out.print("Enter doctor name: ");
        String doctor = sc.nextLine();

        String query = "INSERT INTO visits (patient_id, visit_date, reason, doctor) VALUES (?, ?, ?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, patientId);
            stmt.setDate(2,Date.valueOf(date));
            stmt.setString(3, reason);
            stmt.setString(4, doctor);
            stmt.executeUpdate();
            System.out.println("Visit added successfully.");
        }
    }

    private static void viewVisitsByPatientId(Scanner sc) throws SQLException {
        System.out.print("Enter patient ID: ");
        int patientId = sc.nextInt();

        String query = "SELECT * FROM visits WHERE patient_id = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, patientId);
            ResultSet rs = stmt.executeQuery();

            System.out.println("Visits for Patient ID " + patientId + ":");
            while (rs.next()) {
                System.out.println("Visit ID: " + rs.getInt("visit_id"));
                System.out.println("Date: " + rs.getDate("visit_date"));
                System.out.println("Reason: " + rs.getString("reason"));
                System.out.println("Doctor: " + rs.getString("doctor"));
                System.out.println("--------------------");
            }
        }
    }

    private static void updateVisitDetails(Scanner sc) throws SQLException {
        System.out.print("Enter visit ID to update: ");
        int visitId = sc.nextInt();
        sc.nextLine(); // consume newline
        System.out.print("Enter new reason for visit: ");
        String reason = sc.nextLine();
        System.out.print("Enter new doctor name: ");
        String doctor = sc.nextLine();

        String query = "UPDATE visits SET reason = ?, doctor = ? WHERE visit_id = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, reason);
            stmt.setString(2, doctor);
            stmt.setInt(3, visitId);
            stmt.executeUpdate();
            System.out.println("Visit updated successfully.");
        }
    }

    private static void deleteVisit(Scanner sc) throws SQLException {
        System.out.print("Enter visit ID to delete: ");
        int visitId = sc.nextInt();

        String query = "DELETE FROM visits WHERE visit_id = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, visitId);
            stmt.executeUpdate();
            System.out.println("Visit deleted successfully.");
        }
    }
}
