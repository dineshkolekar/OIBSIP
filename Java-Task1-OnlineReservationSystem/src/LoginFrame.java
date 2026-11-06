import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;

public class LoginFrame extends JFrame {
    private final JTextField userField = new JTextField(16);
    private final JPasswordField passField = new JPasswordField(16);

    public LoginFrame() {
        super("Online Reservation System - Login");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JLabel title = new JLabel("Online Reservation System", SwingConstants.CENTER);
        title.setFont(new Font("SansSerif", Font.BOLD, 20));

        JPanel form = new JPanel(new GridBagLayout());
        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(8, 8, 8, 8);
        g.anchor = GridBagConstraints.WEST;
        g.gridx = 0; g.gridy = 0; form.add(new JLabel("Username:"), g);
        g.gridx = 1; form.add(userField, g);
        g.gridx = 0; g.gridy = 1; form.add(new JLabel("Password:"), g);
        g.gridx = 1; form.add(passField, g);

        JButton loginBtn = new JButton("Login");
        loginBtn.addActionListener(e -> doLogin());
        getRootPane().setDefaultButton(loginBtn);

        JPanel btnPanel = new JPanel();
        btnPanel.add(loginBtn);
        JLabel hint = new JLabel("Demo login: admin / admin123", SwingConstants.CENTER);

        JPanel south = new JPanel(new GridLayout(2, 1));
        south.add(btnPanel);
        south.add(hint);

        JPanel root = new JPanel(new BorderLayout(10, 10));
        root.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        root.add(title, BorderLayout.NORTH);
        root.add(form, BorderLayout.CENTER);
        root.add(south, BorderLayout.SOUTH);

        setContentPane(root);
        pack();
        setLocationRelativeTo(null);
    }

    private void doLogin() {
        String user = userField.getText().trim();
        String pass = new String(passField.getPassword());

        if (user.isEmpty() || pass.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter username and password.",
                    "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }
        try {
            if (DBHelper.authenticate(user, pass)) {
                new DashboardFrame(user).setVisible(true);
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Access Denied: invalid username or password.",
                        "Login Failed", JOptionPane.ERROR_MESSAGE);
                passField.setText("");
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Database error: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}