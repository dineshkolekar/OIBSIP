import java.util.HashMap;
import java.util.Map;

public class Bank {
    private final Map<String, Account> accounts = new HashMap<>();

    public Bank() {
        // Demo accounts
        addAccount(new Account("1001", "1234", "Dinesh Kolekar", 10000));
        addAccount(new Account("1002", "5678", "Rahul Patil", 5000));
        addAccount(new Account("1003", "4321", "Sneha Joshi", 7500));
        addAccount(new Account("1004", "8765", "Amit Deshmukh", 12000));
    }

    public void addAccount(Account account) {
        accounts.put(account.getUserId(), account);
    }

    public Account getAccount(String userId) {
        return accounts.get(userId);
    }

    public Account authenticate(String userId, String pin) {
        Account acc = accounts.get(userId);
        if (acc != null && acc.checkPin(pin)) {
            return acc;
        }
        return null;
    }

    // returns: 0 = success, 1 = recipient not found, 2 = insufficient funds, 3 = same account
    public int transfer(Account from, String toId, double amount) {
        Account to = accounts.get(toId);
        if (to == null) return 1;
        if (to == from) return 3;
        if (amount > from.getBalance()) return 2;
        from.addTransferOut(amount, toId);
        to.addTransferIn(amount, from.getUserId());
        return 0;
    }
}