# 💰 SpendWise AI

**SpendWise AI** is a full-stack personal expense management application designed to help users track their daily expenses, manage income and budgets, and simplify group expense splitting.

The application provides a modern web interface with a Spring Boot backend and React frontend, with secure user authentication and MySQL database integration.

## 🚀 Features

* 🔐 User Registration & Login
* 🔑 JWT-based Authentication
* 💰 Add and manage expenses
* 📊 Track total and recent expenses
* 💵 Income management
* 🎯 Budget management
* 👥 Group expense splitting
* 💳 Split expenses among multiple members
* 📋 Split expense records
* 💸 Payment status tracking — Paid / Pending
* ⚖️ Balance calculation for group expenses
* 📧 Email notifications for split expenses
* 🔒 User-specific expense data
* 📱 Responsive and user-friendly interface

## 🛠️ Tech Stack

### Backend

* Java
* Spring Boot
* Spring Security
* JWT Authentication
* Spring Data JPA / Hibernate
* REST APIs
* Maven

### Frontend

* React.js
* Vite
* Tailwind CSS
* JavaScript
* Framer Motion

### Database

* MySQL

### Development Tools

* Git
* GitHub
* VS Code
* Postman
* MySQL Workbench

## 📂 Project Structure

```text
Spendwise-AI-System/
│
├── spendwise-ai/
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/
│   │   │   └── resources/
│   │   └── test/
│   ├── pom.xml
│   └── mvnw
│
├── spendwise-ai-frontend/
│   ├── public/
│   ├── src/
│   ├── package.json
│   ├── vite.config.js
│   └── tailwind.config.js
│
└── README.md
```

## ⚙️ Getting Started

### 1. Clone the Repository

```bash
git clone https://github.com/riyasonar25/Spendwise-AI-System.git
```

```bash
cd Spendwise-AI-System
```

## 🔧 Backend Setup

Navigate to the backend directory:

```bash
cd spendwise-ai
```

Create the MySQL database:

```sql
CREATE DATABASE spendwise_db;
```

Configure the database credentials in:

```text
src/main/resources/application.properties
```

Example:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/spendwise_db
spring.datasource.username=YOUR_USERNAME
spring.datasource.password=YOUR_PASSWORD
```

Start the Spring Boot application:

```bash
./mvnw spring-boot:run
```

On Windows:

```bash
mvnw.cmd spring-boot:run
```

The backend runs on:

```text
http://localhost:8083
```

## 🎨 Frontend Setup

Open a new terminal and navigate to the frontend:

```bash
cd spendwise-ai-frontend
```

Install dependencies:

```bash
npm install
```

Start the development server:

```bash
npm run dev
```

The frontend will normally run on:

```text
http://localhost:5173
```

## 🔐 Authentication

SpendWise AI uses **JWT-based authentication** to secure application APIs.

After login, the authentication token is used to authorize protected API requests.

## 👥 Split Expense Management

The Split Expense feature allows users to:

1. Create a group.
2. Add an expense.
3. Add group members.
4. Split the expense equally or using custom amounts.
5. Track each member's payment status.
6. Calculate balances between members.
7. Send split-expense email notifications.
8. Delete a split record after all members have completed payment.

## 📊 Expense Management

Users can add and manage expenses by providing information such as:

* Expense title
* Amount
* Category
* Date

The Balance section displays recent expenses and the total expense amount for the logged-in user.

## 🔒 Security

The backend uses:

* Spring Security
* JWT authentication
* Protected REST endpoints
* User-specific data access

Sensitive configuration such as database passwords should be kept outside the public repository.

## 🧪 API Testing

Backend REST APIs can be tested using **Postman** or **Swagger UI** where configured.

Example API categories include:

```text
Authentication
Expenses
Income
Budget
Split Groups
Split Expenses
Split Records
Balance
```

## 📸 Screenshots

Add screenshots of the application here to showcase the UI.

Recommended screenshots:

* Login / Registration
* Dashboard
* Add Expense
* Balance
* Split Expense
* Split Record

Example:

```markdown
![Dashboard](screenshots/dashboard.png)
```

## 🔮 Future Enhancements

* 🤖 AI-based expense insights
* 📈 Advanced spending analytics
* 📊 Interactive charts and reports
* 🔔 Smart spending notifications
* 💡 Personalized saving recommendations
* ☁️ Cloud deployment
* 📱 Mobile application

## 👩‍💻 Author

**Riya Ravindra Sonar**

GitHub: [riyasonar25](https://github.com/riyasonar25)

## ⭐ Project

If you find this project useful, consider giving it a ⭐ on GitHub.
