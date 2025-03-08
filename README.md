# ATM Management System  

## Overview  
The ATM Management System is a Java-based application designed to simulate real-world ATM functionalities. It allows users to create accounts, deposit and withdraw money, check balances, and view transaction histories. The system ensures secure authentication using a PIN and maintains data persistence with MySQL and file storage.  

## Features  
- **User Authentication**: Secure login with a 4-digit PIN  
- **Account Management**: Create, store, and retrieve user account details  
- **Deposit & Withdrawal**: Process secure transactions with real-time balance updates  
- **Transaction History**: Track and display past transactions  
- **Data Persistence**: Stores account details using MySQL and text files  
- **Error Handling**: Handles invalid inputs and ensures smooth user experience  

## Technologies Used  
- Java  
- JDBC (MySQL)  
- File Handling  
- Exception Handling  
- Object-Oriented Programming (OOP)  

## Setup Instructions  
1. Install Java Development Kit (JDK) and MySQL.  
2. Create a MySQL database named `ATMDB` and a table:  
   ```sql
   CREATE TABLE accounts (
       account_number VARCHAR(20) PRIMARY KEY,
       account_holder_name VARCHAR(100),
       pin VARCHAR(4),
       balance DOUBLE
   );
