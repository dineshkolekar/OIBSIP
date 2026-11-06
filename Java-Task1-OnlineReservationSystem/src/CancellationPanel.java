import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;

public class CancellationPanel extends JPanel {
    private final JTextField pnrField = new JTextField(15);
    private final JTextArea details = new JTextArea(10, 35);
    private final JButton cancelBtn = new JButton("Cancel Ticket");
    private Reservation current;

    public CancellationPanel() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JButton fetchBtn = new JButton("Fetch");
        fetchBtn.addActionListener(e -> fetch());

        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT));
        top.add(new JLabel("PNR Number:"));
        top.add(pnrField);
        top.add(fetchBtn);

        details.setEditable(false);
        details.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 13));
        details.setBorder(BorderFactory.createTitledBorder("Booking Details"));

        cancelBtn.setEnabled(false);
        cancelBtn.addActionListener(e -> cancel());
        JPanel bottom = new JPanel();
        bottom.add(cancelBtn);

        add(top, BorderLayout.NORTH);
        add(new JScrollPane(details), BorderLayout.CENTER);
        add(bottom, BorderLayout.SOUTH);
    }

    private void fetch() {
        current = null;
        cancelBtn.setEnabled(false);
        details.setText("");

        String text = pnrField.getText().trim();
        if (text.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter a PNR number.",
                    "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (!text.matches("\\d{1,18}")) {
            JOptionPane.showMessageDialog(this, "PNR must be numeric.",
                    "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }
        try {
            Reservation r = DBHelper.getReservation(Long.parseLong(text));
            if (r == null) {
                details.setText("No booking found for PNR " + text);
            } else {
                current = r;
                details.setText(r.toDisplay());
                cancelBtn.setEnabled(true);
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Database error: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void cancel() {
        if (current == null) return;
        int choice = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to cancel PNR " + current.pnr + "?",
                "Confirm Cancellation", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
        if (choice != JOptionPane.YES_OPTION) return;
        try {
            if (DBHelper.cancel(current.pnr)) {
                JOptionPane.showMessageDialog(this,
                        "Ticket with PNR " + current.pnr + " cancelled successfully.",
                        "Cancelled", JOptionPane.INFORMATION_MESSAGE);
                current = null;
                details.setText("");
                pnrField.setText("");
                cancelBtn.setEnabled(false);
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Database error: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}