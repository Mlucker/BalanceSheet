# BalanceSheet 📊

A modern, full-stack **double-entry accounting system** built with Spring Boot and React. This portfolio project demonstrates enterprise-level software development with real-world business logic, professional UI/UX, and production-ready architecture.

![Java](https://img.shields.io/badge/Java-17-orange?logo=java)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-4.0.2-brightgreen?logo=spring)
![React](https://img.shields.io/badge/React-19.2-blue?logo=react)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-15-blue?logo=postgresql)
![Docker](https://img.shields.io/badge/Docker-Ready-2496ED?logo=docker)

---

## 🎯 Project Overview

BalanceSheet is a comprehensive accounting application that implements **proper double-entry bookkeeping** principles. Unlike simple CRUD applications, this project tackles complex business domain logic including:

- **General Ledger** with chart of accounts
- **Accounts Receivable** with invoice management
- **Payment Processing** with automatic GL posting
- **Inventory Management** with stock tracking
- **Financial Reporting** (P&L, Balance Sheet, Cash Flow)
- **Multi-Company Support** with data isolation

**Perfect for demonstrating:**
- Full-stack development skills
- Complex business logic implementation
- RESTful API design
- Modern React patterns
- Database design and JPA/Hibernate
- Docker containerization

---

## ✨ Key Features

### 📈 Financial Management
- **Double-Entry Accounting**: Every transaction maintains balanced debits and credits
- **Chart of Accounts**: Organized by type (Asset, Liability, Equity, Revenue, Expense)
- **General Ledger**: Complete audit trail of all financial transactions
- **Financial Reports**: 
  - Profit & Loss Statement
  - Balance Sheet
  - Cash Flow Statement
  - Detailed Position Reports

### 🧾 Invoice & AR Management
- **Invoice Creation**: Multi-line invoices with product integration
- **Invoice Lifecycle**: Draft → Posted → Paid workflow
- **Payment Recording**: Automatic GL entries and status updates
- **Customer Management**: Track customer details and transaction history

### 📦 Inventory & Products
- **Product Catalog**: SKU, pricing, and stock management
- **Automatic Stock Updates**: Inventory decrements on invoice approval
- **Product Integration**: Auto-fill invoice items from product catalog

### 🔄 Automation Features
- **Recurring Transactions**: Auto-generate monthly expenses (rent, salaries, etc.)
- **Transaction Templates**: Reusable transaction patterns
- **Scheduled Processing**: Background job for recurring transactions

### 🏢 Multi-Company Support
- **Company Switching**: Manage multiple businesses in one system
- **Data Isolation**: Complete separation between companies
- **Demo Data**: Pre-populated "Demo Brewery" with realistic transactions

### 🎨 Modern UI/UX
- **Glassmorphism Design**: Modern, professional interface
- **Interactive Dashboard**: Key metrics, charts, and recent activity
- **Responsive Layout**: Works on desktop and tablet
- **Dark Theme**: Easy on the eyes for extended use

---

## 🛠️ Technology Stack

### Backend
- **Java 17** - Modern Java features
- **Spring Boot 4.0.2** - Latest Spring framework
- **Spring Data JPA** - ORM with Hibernate
- **PostgreSQL 15** - Robust relational database
- **Lombok** - Reduce boilerplate code
- **Maven** - Dependency management

### Frontend
- **React 19.2** - Latest React with hooks
- **React Router 7** - Client-side routing
- **Axios** - HTTP client
- **Recharts 3** - Data visualization
- **Vite 7** - Fast build tool
- **CSS Variables** - Theming system

### DevOps
- **Docker & Docker Compose** - Containerization
- **PostgreSQL Container** - Database
- **Multi-stage Builds** - Optimized images
- **Nginx** - Production frontend server

---

## 🚀 Quick Start

### Prerequisites
- Docker & Docker Compose
- (Optional) Java 17 & Maven for local development
- (Optional) Node.js 18+ for frontend development

### Option 1: Docker Compose (Recommended)

```bash
# Clone the repository
git clone https://github.com/yourusername/BalanceSheet.git
cd BalanceSheet

# Start all services
docker-compose up -d

# Wait for services to start (~30 seconds)
# Access the application at http://localhost:5173
```

That's it! The application will:
- Start PostgreSQL database
- Build and run Spring Boot backend
- Build and serve React frontend
- Initialize demo data automatically

### Option 2: Local Development

#### Backend
```bash
cd backend

# Start PostgreSQL (or use Docker)
docker run -d -p 5432:5432 \
  -e POSTGRES_DB=balancesheet \
  -e POSTGRES_USER=postgres \
  -e POSTGRES_PASSWORD=postgres \
  postgres:15-alpine

# Run Spring Boot
./mvnw spring-boot:run
# Backend runs on http://localhost:8080
```

#### Frontend
```bash
cd frontend

# Install dependencies
npm install

# Start development server
npm run dev
# Frontend runs on http://localhost:5173
```

---

## 📸 Screenshots

### Dashboard
![Dashboard](docs/screenshots/dashboard.png)
*Modern dashboard with key metrics, revenue trends, and recent activity*

### Invoice Management
![Invoices](docs/screenshots/invoices.png)
*Complete invoice lifecycle from draft to payment*

### Financial Reports
![Reports](docs/screenshots/reports.png)
*Professional financial statements with drill-down capability*

### Product Catalog
![Products](docs/screenshots/products.png)
*Inventory management with stock tracking*

---

## 🎮 Demo Credentials

The application includes two pre-configured companies:

### Demo Brewery (Company ID: 1)
**Pre-populated with:**
- 3 Customers (distributors and pub chains)
- 4 Beer products with inventory
- 3 Invoices (Draft, Posted, Paid)
- 1 Payment record
- 2 years of historical transactions
- 5 Recurring transactions
- 3 Transaction templates

### My Company (Company ID: 2)
**Clean slate for testing:**
- Default chart of accounts
- No transactions
- Ready for your data

**Switch companies** using the dropdown in the navigation bar.

---

## 📁 Project Structure

```
BalanceSheet/
├── backend/                    # Spring Boot application
│   ├── src/main/java/
│   │   └── com/balancesheet/backend/
│   │       ├── model/         # JPA entities
│   │       ├── repository/    # Data access layer
│   │       ├── service/       # Business logic
│   │       ├── controller/    # REST endpoints
│   │       └── bootstrap/     # Data initialization
│   ├── Dockerfile
│   └── pom.xml
│
├── frontend/                   # React application
│   ├── src/
│   │   ├── components/        # React components
│   │   ├── api/              # API client
│   │   └── index.css         # Global styles
│   ├── Dockerfile
│   └── package.json
│
└── docker-compose.yml         # Multi-container setup
```

---

## 🔌 API Endpoints

### Companies
- `GET /api/companies` - List all companies
- `GET /api/companies/{id}` - Get company details
- `POST /api/companies` - Create new company

### Accounts
- `GET /api/accounts` - List chart of accounts
- `POST /api/accounts` - Create new account

### Transactions
- `GET /api/transactions` - List all transactions
- `POST /api/transactions` - Record new transaction
- `POST /api/transactions/from-template/{id}` - Create from template

### Invoices
- `GET /api/invoices` - List all invoices
- `POST /api/invoices` - Create new invoice
- `POST /api/invoices/{id}/approve` - Post invoice to GL

### Payments
- `POST /api/payments` - Record payment
- `GET /api/payments/invoice/{id}` - Get invoice payments

### Reports
- `GET /api/financial-position/detailed` - Balance sheet data
- `GET /api/profit-loss` - P&L statement
- `GET /api/cash-flow` - Cash flow statement

**Note:** All endpoints require `X-Company-ID` header for multi-company support.

---

## 🏗️ Architecture Highlights

### Backend Design Patterns
- **Repository Pattern**: Clean data access abstraction
- **Service Layer**: Business logic separation
- **DTO Pattern**: API response optimization
- **Transaction Management**: ACID compliance with `@Transactional`
- **Dependency Injection**: Loose coupling via Spring IoC

### Database Schema
- **Normalized Design**: 3NF compliance
- **Referential Integrity**: Foreign key constraints
- **Audit Trail**: Complete transaction history
- **Multi-tenancy**: Company-based data isolation

### Frontend Architecture
- **Component-Based**: Reusable UI components
- **Custom Hooks**: Shared logic extraction
- **API Abstraction**: Centralized HTTP client
- **CSS Variables**: Consistent theming
- **Responsive Design**: Mobile-friendly layouts

---

## 🧪 Testing

```bash
# Backend tests
cd backend
./mvnw test

# Frontend tests
cd frontend
npm test
```

---

## 📦 Deployment

### Production Build

```bash
# Build backend JAR
cd backend
./mvnw clean package

# Build frontend static files
cd frontend
npm run build
```

### Environment Variables

**Backend (`application.properties`):**
```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/balancesheet
spring.datasource.username=postgres
spring.datasource.password=your_password
spring.jpa.hibernate.ddl-auto=update
```

**Frontend (`.env`):**
```env
VITE_API_URL=http://localhost:8080/api
```

---

## 🎓 What I Learned

Building this project taught me:

1. **Domain-Driven Design**: Modeling complex business rules in code
2. **Double-Entry Accounting**: Understanding financial systems
3. **JPA Relationships**: Managing bidirectional associations
4. **Transaction Management**: Ensuring data consistency
5. **RESTful API Design**: Proper HTTP semantics and status codes
6. **React State Management**: Complex form handling
7. **Docker Multi-Container**: Orchestrating microservices
8. **Production Deployment**: Environment configuration

---

## 🚧 Future Enhancements

- [ ] **Authentication & Authorization** - JWT-based security
- [ ] **Expense Management** - Bill tracking and AP
- [ ] **Bank Reconciliation** - Match transactions with statements
- [ ] **Multi-Currency** - Foreign exchange support
- [ ] **Tax Calculations** - VAT/Sales tax automation
- [ ] **Export Features** - PDF reports, CSV exports
- [ ] **API Documentation** - Swagger/OpenAPI integration
- [ ] **Unit Tests** - Comprehensive test coverage
- [ ] **Performance Optimization** - Caching, pagination

---

## 📄 License

This project is open source and available under the [MIT License](LICENSE).

---

## 👤 Author

- GitHub: [@Mlucker](https://github.com/Mlucker)
---

## 🙏 Acknowledgments

- Spring Boot team for excellent documentation
- React community for best practices
- PostgreSQL for robust database
- Docker for containerization simplicity

---

**⭐ If you found this project helpful, please give it a star!**