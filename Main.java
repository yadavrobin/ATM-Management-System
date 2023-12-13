import java.io.*;
import java.sql.*;
import java.util.*;

class Transaction {
    private String type;
    private double amount;

    public Transaction(String type, double amount) {
        this.type = type;
        this.amount = amount;
    }

    public String getType() {
        return type;
    }

    public double getAmount() {
        return amount;
    }
}

class Account {
    private String accountNumber;
    private String accountHolderName;
    private String pin;
    private double balance;
    private ArrayList<Transaction> transactionHistory;

    public Account(String accountNumber, String accountHolderName, String pin, double balance) {
        this.accountNumber = accountNumber;
        this.accountHolderName = accountHolderName;
        this.pin = pin;
        this.balance = balance;
        this.transactionHistory = new ArrayList<>();
        saveAccountToDatabase(); // Save account details after creation
        saveAccountToFile();
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public String getAccountHolderName() {
        return accountHolderName;
    }

    public String getPin() {
        return pin;
    }

    public double getBalance() {
        return balance;
    }

    public ArrayList<Transaction> getTransactionHistory() {
        return transactionHistory;
    }

    public void deposit(double amount) {
        balance += amount;
        addTransaction(new Transaction("Deposit", amount));
        saveAccountToDatabase(); // Save account details after each deposit
        saveAccountToFile();
    }

    public boolean withdraw(double amount) {
        if (amount <= balance) {
            balance -= amount;
            addTransaction(new Transaction("Withdrawal", amount));
            saveAccountToDatabase(); // Save account details after each withdrawal
            saveAccountToFile();
            return true;
        } else {
            System.out.println("Insufficient funds.");
            return false;
        }
    }

    public void addTransaction(Transaction transaction) {
        transactionHistory.add(transaction);
    }

    public boolean validatePin(String enteredPin) {
        return pin.equals(enteredPin);
    }

    private void saveAccountToDatabase() {
        String url = "jdbc:mysql://localhost:3306/ATMDB";
        String username = "root";
        String password = "root1234";

        try (Connection connection = DriverManager.getConnection(url, username, password);
                PreparedStatement statement = connection.prepareStatement(
                        "INSERT INTO accounts (account_number, account_holder_name, pin, balance) VALUES (?, ?, ?, ?)")) {
            statement.setString(1, accountNumber);
            statement.setString(2, accountHolderName);
            statement.setString(3, pin);
            statement.setDouble(4, balance);

            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void saveAccountToFile() {
        String filename = "accounts.txt";

        try (PrintWriter writer = new PrintWriter(new FileWriter(filename, true))) {
            writer.println(accountNumber + "," + accountHolderName + "," + pin + "," + balance);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

class ATM {
    private Scanner scanner;

    public ATM() {
        scanner = new Scanner(System.in);
    }

    private ArrayList<Account> loadAccountsFromDatabase() {
        String url = "jdbc:mysql://localhost:3306/ATMDB";
        String username = "root";
        String password = "root1234";
        ArrayList<Account> loadedAccounts = new ArrayList<>();

        try (Connection connection = DriverManager.getConnection(url, username, password);
                Statement statement = connection.createStatement();
                ResultSet resultSet = statement.executeQuery("SELECT * FROM accounts")) {

            while (resultSet.next()) {
                String accountNumber = resultSet.getString("account_number");
                String accountHolderName = resultSet.getString("account_holder_name");
                String pin = resultSet.getString("pin");
                double balance = resultSet.getDouble("balance");

                Account account = new Account(accountNumber, accountHolderName, pin, balance);
                loadedAccounts.add(account);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return loadedAccounts;
    }

    private void saveAccountsToDatabase(ArrayList<Account> accounts) {
        String url = "jdbc:mysql://localhost:3306/ATMDB";
        String username = "root";
        String password = "root1234";

        for (Account account : accounts) {
            try (Connection connection = DriverManager.getConnection(url, username, password);
                    PreparedStatement statement = connection.prepareStatement(
                            "UPDATE accounts SET balance = ? WHERE account_number = ?")) {
                statement.setDouble(1, account.getBalance());
                statement.setString(2, account.getAccountNumber());

                statement.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public void start() {
        System.out.println("Welcome to the ATM!");
        ArrayList<Account> accounts = loadAccountsFromDatabase();

        while (true) {
            try {
                System.out.println("\nMain Menu:");
                System.out.println("1. Create an Account");
                System.out.println("2. Login");
                System.out.println("3. Exit");

                System.out.print("Enter your choice: ");
                int choice = scanner.nextInt();
                scanner.nextLine();

                switch (choice) {
                    case 1:
                        createAccount(accounts);
                        break;
                    case 2:
                        login(accounts);
                        break;
                    case 3:
                        saveAccountsToDatabase(accounts);
                        System.out.println("Thank you for using the ATM. Goodbye!");
                        return;
                    default:
                        System.out.println("Invalid choice. Please try again.");
                }
            } catch (InputMismatchException e) {
                System.out.println("Invalid input. Please enter a number.");
                scanner.nextLine();
            }
        }
    }

    private void login(ArrayList<Account> accounts) {
        System.out.print("Enter your account number: ");
        String accountNumber = scanner.nextLine();

        Account account = findAccount(accounts, accountNumber);

        if (account != null) {
            try {
                System.out.print("Enter your PIN (4 digits): ");
                String enteredPin = scanner.nextLine();

                if (!enteredPin.matches("\\d{4}")) {
                    System.out.println("Invalid PIN. Please enter a 4-digit numeric PIN.");
                    return;
                }

                if (account.validatePin(enteredPin)) {
                    showMenu(account, accounts);
                } else {
                    System.out.println("Incorrect PIN. Please try again.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid PIN format. Please enter a numeric PIN.");
            }
        } else {
            System.out.println("Please enter a valid account number.");
        }
    }

    private Account findAccount(ArrayList<Account> accounts, String accountNumber) {
        for (Account account : accounts) {
            if (account.getAccountNumber().equals(accountNumber)) {
                return account;
            }
        }
        return null;
    }

    private void createAccount(ArrayList<Account> accounts) {
        try {
            String accountNumber = generateRandomAccountNumber();

            System.out.print("Enter your name: ");
            String accountHolderName = scanner.nextLine();

            if (accountHolderName.isEmpty() || !accountHolderName.matches("^[a-zA-Z\\s]+$")) {
                System.out.println("Invalid name. Please enter a valid name.");
                return;
            }

            System.out.print("Enter your desired PIN (4 digits): ");
            String pin = scanner.nextLine();

            if (!pin.matches("\\d{4}")) {
                System.out.println("Invalid PIN. Please enter a 4-digit numeric PIN.");
                return;
            }

            System.out.print("Enter the amount you want to deposit: Rs.");
            double initialBalance = scanner.nextDouble();
            scanner.nextLine();
            if (initialBalance < 0) {
                System.out.println("Invalid input for balance. Please enter a positive numeric value.");
                return;
            }

            Account account = new Account(accountNumber, accountHolderName, pin, initialBalance);
            accounts.add(account);

            System.out.println("Account created successfully! Your account number is: " + account.getAccountNumber());

            showMenu(account, accounts);
        } catch (InputMismatchException e) {
            System.out.println("Invalid input. Please enter a valid numeric value.");
            scanner.nextLine();
        }
    }

    private String generateRandomAccountNumber() {
        Random random = new Random();
        int randomNumber = 100000000 + random.nextInt(900000000);
        return String.valueOf(randomNumber);
    }

    private void showMenu(Account account, ArrayList<Account> accounts) {
        while (true) {
            try {
                System.out.println("\nWelcome, " + account.getAccountHolderName() + "!");
                System.out.println("Main Menu:");
                System.out.println("1. Check Balance");
                System.out.println("2. Deposit");
                System.out.println("3. Withdraw");
                System.out.println("4. View Transaction History");
                System.out.println("5. Logout");

                System.out.print("Enter your choice: ");
                int choice = scanner.nextInt();
                scanner.nextLine();

                switch (choice) {
                    case 1:
                        System.out.println("Your balance: Rs." + account.getBalance());
                        break;
                    case 2:
                        System.out.print("Enter the deposit amount: Rs.");
                        double depositAmount = scanner.nextDouble();
                        if (depositAmount < 0) {
                            System.out.println("Invalid deposit amount. Please enter a positive value.");
                            break;
                        }
                        account.deposit(depositAmount);
                        System.out.println("Deposit successful. New balance: Rs." + account.getBalance());
                        break;
                    case 3:
                        System.out.print("Enter the withdrawal amount: Rs.");
                        double withdrawalAmount = scanner.nextDouble();
                        if (withdrawalAmount < 0) {
                            System.out.println("Invalid withdrawal amount. Please enter a positive value.");
                            break;
                        }
                        if (account.withdraw(withdrawalAmount)) {
                            System.out.println("Withdrawal successful. New balance: Rs." + account.getBalance());
                        }
                        break;
                    case 4:
                        viewTransactionHistory(account);
                        break;
                    case 5:
                        saveAccountsToDatabase(accounts);
                        System.out.println("Logout successful. Returning to the main menu.");
                        return;
                    default:
                        System.out.println("Invalid choice. Please try again.");
                }
            } catch (InputMismatchException e) {
                System.out.println("Invalid input. Please enter a number.");
                scanner.nextLine();
            }
        }
    }

    private void viewTransactionHistory(Account account) {
        ArrayList<Transaction> transactions = account.getTransactionHistory();
        if (transactions.isEmpty()) {
            System.out.println("No transactions found.");
        } else {
            System.out.println("Transaction History:");
            for (Transaction transaction : transactions) {
                System.out.println(transaction.getType() + ": Rs." + transaction.getAmount());
            }
        }
    }
}

public class Main {
    public static void main(String[] args) {
        ATM atm = new ATM();
        atm.start();
    }
}
