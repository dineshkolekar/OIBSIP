import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;

public class BookingPanel extends JPanel {
    private final String username;
    private final JTextField nameField = new JTextField(20);
    private final JTextField trainNoField = new JTextField(20);
    private final JTextField trainNameField = new JTextField(20);
    private final JComboBox<String> classBox = new JComboBox<>(
            new String[]{"Sleeper (SL)", "AC 3 Tier (3A)", "AC 2 Tier (2A)", "AC First Class (1A)"});
    private final JTextField dateField = new JTextField(20);
    private final JTextField sourceField = new JTextField(20);
    private final JTextField destField = new JTextField(20);

    public BookingPanel(String username) {
        this.username = username;
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        trainNameField.setEditable(false);
        dateField.setText(LocalDate.now().plusDays(1).toString());

        JPanel form = new JPanel(new GridBagLayout());
        addRow(form, 0, "Passenger Name:", nameField);
        addRow(form, 1, "Train Number:", trainNoField);
        addRow(form, 2, "Train Name (auto):", trainNameField);
        addRow(form, 3, "Class Type:", classBox);
        addRow(form, 4, "Date (yyyy-MM-dd):", dateField);
        addRow(form, 5, "Source Station:", sourceField);
        addRow(form, 6, "Destination Station:", destField);

        JLabel hint = new JLabel("<html>Available trains: 12123 Deccan Queen, 12137 Punjab Mail, "
                + "12951 Mumbai Rajdhani, 11007 Deccan Express, 12627 Karnataka Express</html>");

        JButton bookBtn = new JButton("Book Ticket");
        JButton clearBtn = new JButton("Clear");
        bookBtn.addActionListener(e -> book());
        clearBtn.addActionListener(e -> clearForm());
        JPanel buttons = new JPanel();
        buttons.add(bookBtn);
        buttons.add(clearBtn);

        JPanel south = new JPanel(new BorderLayout());
        south.add(hint, BorderLayout.NORTH);
        south.add(buttons, BorderLayout.SOUTH);

        add(form, BorderLayout.CENTER);
        add(south, BorderLayout.SOUTH);

        trainNoField.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { updateTrainName(); }
            public void removeUpdate(DocumentEvent e) { updateTrainName(); }
            public void changedUpdate(DocumentEvent e) { updateTrainName(); }
        });
    }

    private void addRow(JPanel p, int row, String label, JComponent field) {
        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(6, 6, 6, 6);
        g.anchor = GridBagConstraints.WEST;
        g.gridx = 0; g.gridy = row;
        p.add(new JLabel(label), g);
        g.gridx = 1;
        p.add(field, g);
    }

    private void updateTrainName() {
        String text = trainNoField.getText().trim();
        trainNameField.setText("");
        if (!text.matches("\\d+")) return;
        try {
            String name = DBHelper.getTrainName(Integer.parseInt(text));
            if (name != null) trainNameField.setText(name);
        } catch (NumberFormatException | SQLException ignored) {
        }
    }

    private void error(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Validation Error", JOptionPane.WARNING_MESSAGE);
    }

    private void book() {
        String name = nameField.getText().trim();
        String trainNoText = trainNoField.getText().trim();
        String trainName = trainNameField.getText().trim();
        String classType = (String) classBox.getSelectedItem();
        String dateText = dateField.getText().trim();
        String source = sourceField.getText().trim();
        String dest = destField.getText().trim();

        if (name.isEmpty() || trainNoText.isEmpty() || dateText.isEmpty()
                || source.isEmpty() || dest.isEmpty()) {
            error("All fields are required.");
            return;
        }
        if (!name.matches("[A-Za-z .]+")) {
            error("Passenger name should contain only letters.");
            return;
        }
        if (!trainNoText.matches("\\d+")) {
            error("Train number must be numeric.");
            return;
        }
        if (trainName.isEmpty()) {
            error("Train number not found. Please enter a valid train number.");
            return;
        }
        LocalDate date;
        try {
            date = LocalDate.parse(dateText);
        } catch (DateTimeParseException ex) {
            error("Invalid date format. Use yyyy-MM-dd (e.g. 2026-11-20).");
            return;
        }
        if (date.isBefore(LocalDate.now())) {
            error("Date of journey cannot be in the past.");
            return;
        }
        if (source.equalsIgnoreCase(dest)) {
            error("Source and destination cannot be the same.");
            return;
        }

        try {
            long pnr = DBHelper.book(name, Integer.parseInt(trainNoText), trainName,
                    classType, date.toString(), source, dest, username);
            Reservation r = DBHelper.getReservation(pnr);
            JOptionPane.showMessageDialog(this,
                    "Booking Confirmed!\n\n" + r.toDisplay(),
                    "Booking Confirmation", JOptionPane.INFORMATION_MESSAGE);
            clearForm();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Database error: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void clearForm() {
        nameField.setText("");
        trainNoField.setText("");
        trainNameField.setText("");
        classBox.setSelectedIndex(0);
        dateField.setText(LocalDate.now().plusDays(1).toString());
        sourceField.setText("");
        destField.setText("");
    }
}