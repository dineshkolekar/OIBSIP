import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.sql.*;

public class DBHelper {
    private static final String URL =
            "jdbc:mysql://localhost:3306/reservation_db?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
    private static final String DB_USER = "root";
    private static final String DB_PASS = "YOUR_MYSQL_PASSWORD";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, DB_USER, DB_PASS);
    }

    public static void init() throws SQLException {
        try (Connection c = getConnection(); Statement st = c.createStatement()) {
            st.execute("CREATE TABLE IF NOT EXISTS users ("
                    + "username VARCHAR(50) PRIMARY KEY, password_hash VARCHAR(64) NOT NULL)");
            st.execute("CREATE TABLE IF NOT EXISTS trains ("
                    + "train_no INT PRIMARY KEY, train_name VARCHAR(100) NOT NULL)");
            st.execute("CREATE TABLE IF NOT EXISTS reservations ("
                    + "pnr BIGINT PRIMARY KEY AUTO_INCREMENT, passenger_name VARCHAR(100) NOT NULL, "
                    + "train_no INT NOT NULL, train_name VARCHAR(100) NOT NULL, "
                    + "class_type VARCHAR(30) NOT NULL, journey_date VARCHAR(15) NOT NULL, "
                    + "source VARCHAR(100) NOT NULL, destination VARCHAR(100) NOT NULL, "
                    + "booked_by VARCHAR(50) NOT NULL) AUTO_INCREMENT=1000000001");
        }
        seedUser("admin", "admin123");
        seedUser("dinesh", "dinesh@123");
        seedTrain(12123, "Deccan Queen");
        seedTrain(12137, "Punjab Mail");
        seedTrain(12951, "Mumbai Rajdhani");
        seedTrain(11007, "Deccan Express");
        seedTrain(12627, "Karnataka Express");
    }

    private static void seedUser(String user, String pass) throws SQLException {
        try (Connection c = getConnection();
             PreparedStatement ps = c.prepareStatement(
                     "INSERT IGNORE INTO users(username, password_hash) VALUES (?, ?)")) {
            ps.setString(1, user);
            ps.setString(2, hash(pass));
            ps.executeUpdate();
        }
    }

    private static void seedTrain(int no, String name) throws SQLException {
        try (Connection c = getConnection();
             PreparedStatement ps = c.prepareStatement(
                     "INSERT IGNORE INTO trains(train_no, train_name) VALUES (?, ?)")) {
            ps.setInt(1, no);
            ps.setString(2, name);
            ps.executeUpdate();
        }
    }

    public static String hash(String text) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] bytes = md.digest(text.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (byte b : bytes) sb.append(String.format("%02x", b));
            return sb.toString();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static boolean authenticate(String username, String password) throws SQLException {
        try (Connection c = getConnection();
             PreparedStatement ps = c.prepareStatement(
                     "SELECT 1 FROM users WHERE username = ? AND password_hash = ?")) {
            ps.setString(1, username);
            ps.setString(2, hash(password));
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    // returns null if train not found
    public static String getTrainName(int trainNo) throws SQLException {
        try (Connection c = getConnection();
             PreparedStatement ps = c.prepareStatement(
                     "SELECT train_name FROM trains WHERE train_no = ?")) {
            ps.setInt(1, trainNo);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getString(1) : null;
            }
        }
    }

    // saves booking and returns the generated PNR
    public static long book(String passenger, int trainNo, String trainName, String classType,
                            String date, String source, String destination, String bookedBy)
            throws SQLException {
        try (Connection c = getConnection();
             PreparedStatement ps = c.prepareStatement(
                     "INSERT INTO reservations (passenger_name, train_no, train_name, class_type, "
                             + "journey_date, source, destination, booked_by) VALUES (?, ?, ?, ?, ?, ?, ?, ?)",
                     Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, passenger);
            ps.setInt(2, trainNo);
            ps.setString(3, trainName);
            ps.setString(4, classType);
            ps.setString(5, date);
            ps.setString(6, source);
            ps.setString(7, destination);
            ps.setString(8, bookedBy);
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                keys.next();
                return keys.getLong(1);
            }
        }
    }

    // returns null if PNR not found
    public static Reservation getReservation(long pnr) throws SQLException {
        try (Connection c = getConnection();
             PreparedStatement ps = c.prepareStatement(
                     "SELECT * FROM reservations WHERE pnr = ?")) {
            ps.setLong(1, pnr);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return null;
                return new Reservation(
                        rs.getLong("pnr"), rs.getString("passenger_name"),
                        rs.getInt("train_no"), rs.getString("train_name"),
                        rs.getString("class_type"), rs.getString("journey_date"),
                        rs.getString("source"), rs.getString("destination"));
            }
        }
    }

    public static boolean cancel(long pnr) throws SQLException {
        try (Connection c = getConnection();
             PreparedStatement ps = c.prepareStatement(
                     "DELETE FROM reservations WHERE pnr = ?")) {
            ps.setLong(1, pnr);
            return ps.executeUpdate() > 0;
        }
    }
}