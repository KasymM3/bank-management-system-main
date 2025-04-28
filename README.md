

📂 Project Structure


src/main/java/com/bank
├── config              # Security and configuration files
├── controller          # Handles incoming HTTP requests
├── model               # Entity models (like User, Account, Transaction)
├── repository          # Spring Data JPA repositories (for DB interactions)
├── service             # Business logic and service layer
└── BankApplication.java # Main Spring Boot application entry point



⸻

🔍 Key Components


	1.	Config
	•	SecurityConfig – Configures Spring Security for authentication and authorization.
	•	PasswordGenerator – Utility class for password hashing.


	2.	Controller
	•	BankController – Manages user registration, login, and banking operations.
	•	AccountController – Handles account-related actions.
	•	TransactionController – Handles transactions (withdraw, deposit).


	3.	Model
	•	User – Represents a user with username, password, and roles.
	•	Account – Represents bank accounts.
	•	Transaction – Represents financial transactions.


	4.	Repository
	•	UserRepository – Fetches user details for authentication.
	•	AccountRepository – Fetches and manages account data.
	•	TransactionRepository – Handles transaction data.


	5.	Service
	•	UserService – Handles user registration and authentication.
	•	AccountService – Handles account-related business logic.
	•	TransactionService – Processes deposits and withdrawals.

⸻

🌐 Frontend (Thymeleaf Templates)
	•	login.html – User login page
	•	register.html – User registration page
	•	home.html – User dashboard after login
	•	customers.html – Displays customer list
	•	transactions.html – Displays transaction history

⸻

🚀 Functionality Overview

✅ Secure login with Spring Security
✅ Hashed passwords using BCrypt
✅ Role-based access (Admin/User)
✅ CSRF protection configured
✅ Transaction processing (deposit/withdrawal)
✅ Data persistence with Spring Data JPA

⸻

