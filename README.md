ATM Management System
This is a Java-based ATM management system that allows users to create accounts, deposit, withdraw, and view transaction history. The project uses JDBC to connect to a MySQL database and store account details and transaction history.

Features
Account Creation: Create new accounts with a unique account number, name, PIN, and initial deposit.
Account Login: Secure login using a 4-digit PIN.
Deposit & Withdrawal: Deposit money or withdraw funds from your account.
Balance Check: Check the current balance in the account.
Transaction History: View the complete history of deposits and withdrawals.
Database Integration: Account details are stored and managed using MySQL.
File Backup: Account details are also saved to a local text file for redundancy.
Technology Stack
Java: Core language for the application.
MySQL: Database used to store account details.
JDBC: Java Database Connectivity for connecting to MySQL.
File I/O: For saving account data to a local file (accounts.txt).
How to Run
Install MySQL and create a database named ATMDB. Run the following SQL query to create the accounts table:

sql
CREATE DATABASE ATMDB;
USE ATMDB;
CREATE TABLE accounts (
  account_number VARCHAR(20) PRIMARY KEY,
  account_holder_name VARCHAR(100),
  pin VARCHAR(10),
  balance DOUBLE
);
Clone this repository or download the project files.

Modify Database Credentials:

Update your MySQL username and password in the saveAccountToDatabase() method in the Account class.
Compile and Run the project in your IDE or terminal:

bash
Copy code
javac Main.java
java Main
Folder Structure
Main.java: Entry point of the program.
Account.java: Defines the Account class for handling accounts.
ATM.java: Manages the main ATM operations.
Transaction.java: Defines the Transaction class to track deposits and withdrawals.
License
This project is open-source and available under the MIT License.
