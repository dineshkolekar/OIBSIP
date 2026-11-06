import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Transaction {
    private final String type;
    private final double amount;
    private final double balanceAfter;
    private final String details;
    private final LocalDateTime time;

    public Transaction(String type, double amount, double balanceAfter, String details) {
        this.type = type;
        this.amount = amount;
        this.balanceAfter = balanceAfter;
        this.details = details;
        this.time = LocalDateTime.now();
    }

    @Override
    public String toString() {
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
        return String.format("%s | %-9s | Rs.%10.2f | Balance: Rs.%10.2f | %s",
                time.format(fmt), type, amount, balanceAfter, details);
    }
}