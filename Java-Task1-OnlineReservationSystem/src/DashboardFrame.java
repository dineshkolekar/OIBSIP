import javax.swing.*;
import java.awt.*;

public class DashboardFrame extends JFrame {
    public DashboardFrame(String username) {
        super("Online Reservation System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JLabel welcome = new JLabel("Logged in as: " + username);
        JButton logout = new JButton("Logout");
        logout.addActionListener(e -> {
            dispose();
            new LoginFrame().setVisible(true);
        });

        JPanel top = new JPanel(new BorderLayout());
        top.setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));
        top.add(welcome, BorderLayout.WEST);
        top.add(logout, BorderLayout.EAST);

        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Book Ticket", new BookingPanel(username));
        tabs.addTab("Cancel Ticket", new CancellationPanel());

        add(top, BorderLayout.NORTH);
        add(tabs, BorderLayout.CENTER);
        setSize(580, 520);
        setLocationRelativeTo(null);
    }
}