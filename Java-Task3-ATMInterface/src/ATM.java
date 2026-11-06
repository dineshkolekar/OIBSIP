import java.util.ArrayList;
import java.util.Scanner;

public class ATM {
    private static final int MAX_ATTEMPTS = 3;
    private final Bank bank;
    private final Scanner scanner = new Scanner(System.in);

    public ATM(Bank bank) {
        this.bank = bank;
    }

    public void start() {
        System.out.println("=====================================");
        System.out.println("          WELCOME TO JAVA ATM        ");
        System.out.println("=====================================");

        Account account = login();
        if (account == null) {
            System.out.println("\nToo many incorrect attempts. Access denied!");
            return;
        }

        System.out.println("\nLogin successful. Welcome, " + account.getHolderName() + "!");
        showMenu(account);
        scanner.close();
    }

    private Account login() {
        for (int attempt = 1; attempt <= MAX_ATTEMPTS; attempt++) {
            System.out.print("\nEnter User ID: ");
            String id = scanner.nextLine().trim();
            System.out.print("Enter PIN: ");
            String pin = scanner.nextLine().trim();

            Account acc = bank.authenticate(id, pin);
            if (acc != null) {
                return acc;
            }
            System.out.println("Invalid User ID or PIN. Attempts left: " + (MAX_ATTEMPTS - attempt));
        }
        return null;
    }

    private void showMenu(Account account) {
        boolean running = true;
        while (running) {
            System.out.println("\n------------- MAIN MENU -------------");
            System.out.println("1. Transaction History");
            System.out.println("2. Withdraw");
            System.out.println("3. Deposit");
            System.out.println("4. Transfer");
            System.out.println("5. Quit");
            System.out.print("Choose an option (1-5): ");
            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1": showHistory(account); break;
                case "2": withdraw(account); break;
                case "3": deposit(account); break;
                case "4": transfer(account); break;
                case "5":
                    System.out.println("\nThank you for using Java ATM. Goodbye!");
                    running = false;
                    break;
                default:
                    System.out.println("Invalid option. Please choose 1 to 5.");
            }
        }
    }

    private void showHistory(Account account) {
        ArrayList<Transaction> history = account.getHistory();
        System.out.println("\n----- TRANSACTION HISTORY -----");
        if (history.isEmpty()) {
            System.out.println("No transactions in this session.");
        } else {
            for (int i = 0; i < history.size(); i++) {
                System.out.println((i + 1) + ". " + history.get(i));
            }
        }
        System.out.printf("Current Balance: Rs.%.2f%n", account.getBalance());
    }

    private void withdraw(Account account) {
        double amount = readAmount("Enter amount to withdraw: ");
        if (amount < 0) return;
        if (account.withdraw(amount)) {
            System.out.printf("Rs.%.2f withdrawn. New Balance: Rs.%.2f%n", amount, account.getBalance());
        } else {
            System.out.println("Insufficient Funds!");
        }
    }

    private void deposit(Account account) {
        double amount = readAmount("Enter amount to deposit: ");
        if (amount < 0) return;
        account.deposit(amount);
        System.out.printf("Rs.%.2f deposited. New Balance: Rs.%.2f%n", amount, account.getBalance());
    }

    private void transfer(Account account) {
        System.out.print("Enter recipient Account ID: ");
        String toId = scanner.nextLine().trim();
        double amount = readAmount("Enter amount to transfer: ");
        if (amount < 0) return;

        int result = bank.transfer(account, toId, amount);
        switch (result) {
            case 0:
                System.out.printf("Rs.%.2f transferred to %s. New Balance: Rs.%.2f%n",
                        amount, toId, account.getBalance());
                break;
            case 1: System.out.println("Recipient account not found."); break;
            case 2: System.out.println("Insufficient Funds!"); break;
            case 3: System.out.println("You cannot transfer to your own account."); break;
        }
    }

    // returns -1 if invalid
    private double readAmount(String prompt) {
        System.out.print(prompt);
        String input = scanner.nextLine().trim();
        try {
            double amount = Double.parseDouble(input);
            if (amount <= 0) {
                System.out.println("Amount must be greater than zero.");
                return -1;
            }
            return amount;
        } catch (NumberFormatException e) {
            System.out.println("Invalid amount. Please enter a number.");
            return -1;
        }
    }
}