# 🛡️ ShadowGuard

## AI-Powered Shadow API Risk Analyzer

ShadowGuard is a security-focused web application that discovers API endpoints, analyzes their security risk, and provides a centralized dashboard for monitoring potentially risky or undocumented APIs.

🔗 Live Demo: https://shadowguard-1.onrender.com

## 🚀 Features

- Automatic API endpoint discovery
- Shadow API identification
- Automated API risk scoring
- Risk level classification
- Interactive security dashboard
- API scanning and dashboard refresh
- MySQL-based persistent storage
- React-based frontend
- Spring Boot REST backend
- Gemini API integration for AI-assisted security analysis
- Production frontend-backend communication

## 🧠 How It Works

ShadowGuard discovers registered API endpoints from the Spring Boot application using RequestMappingHandlerMapping.

The discovered endpoints are analyzed by the risk analyzer, stored in MySQL, and displayed through the React dashboard.

The basic workflow is:

User → React Dashboard → Spring Boot REST API → API Discovery → Risk Analysis → MySQL → Dashboard

For individual APIs, Gemini API is used to provide AI-assisted security analysis and recommendations.

## 📊 Dashboard

The ShadowGuard dashboard provides a centralized view of API security information.

It displays:

- Total APIs
- Shadow APIs
- High Risk APIs
- Critical APIs
- API endpoints
- Risk levels
- API source information
- AI security analysis

## 🔍 API Discovery

ShadowGuard uses Spring Boot's RequestMappingHandlerMapping to inspect registered controller mappings and discover API endpoints.

The discovery process:

1. Reads registered API mappings.
2. Extracts HTTP methods.
3. Extracts endpoint paths.
4. Filters API endpoints.
5. Ignores ShadowGuard's own discovery endpoint.
6. Checks for existing APIs to prevent duplicates.
7. Calculates the API risk.
8. Stores API information in MySQL.

## ⚠️ Risk Analysis

Each discovered API is analyzed using the project's RiskAnalyzer.

The analyzer calculates:

- Risk Score
- Risk Level
- Risk Reasons

Supported risk levels include:

- LOW
- MEDIUM
- HIGH
- CRITICAL

The calculated risk information is stored with the API and displayed on the dashboard.

## 🤖 AI Security Analysis

ShadowGuard integrates the Google Gemini API for AI-assisted security analysis.

When an API is selected from the dashboard, the application can generate:

- Security summary
- Potential impact
- Security recommendations

The Gemini API key is stored as an environment variable and is not hardcoded in the source code.

## 🛠️ Tech Stack

### Backend
- Java 17
- Spring Boot
- Spring Data JPA
- Hibernate
- REST APIs
- Maven
- Lombok

### Frontend
- React
- JavaScript
- Vite
- HTML
- CSS

### Database
- MySQL

### AI
- Google Gemini API

### Tools
- IntelliJ IDEA
- Visual Studio Code
- MySQL Workbench
- Postman
- Git
- GitHub

### Deployment
- Render

## 🏗️ Project Structure

ShadowAPI/

├── shadowguard/  
│   ├── src/  
│   │   └── main/  
│   │       ├── java/  
│   │       │   └── com/shadowguard/  
│   │       │       ├── ai/  
│   │       │       ├── analyzer/  
│   │       │       ├── controller/  
│   │       │       ├── dashboard/  
│   │       │       ├── discovery/  
│   │       │       ├── entity/  
│   │       │       ├── parser/  
│   │       │       ├── repository/  
│   │       │       └── security/  
│   │       └── resources/  
│   └── pom.xml  
│  
├── shadowguard-frontend/  
│   ├── public/  
│   ├── src/  
│   │   ├── App.jsx  
│   │   ├── App.css  
│   │   └── main.jsx  
│   ├── index.html  
│   ├── package.json  
│   └── vite.config.js  
│  
└── README.md

## 🌐 REST API

Main backend endpoints include:

GET /api/apis

Returns all stored APIs.

GET /api/apis/shadow

Returns detected Shadow APIs.

GET /api/apis/high-risk

Returns high-risk APIs.

GET /api/discovery/scan

Scans the application for registered API endpoints.

GET /api/dashboard/stats

Returns dashboard statistics.

GET /api/ai/analyze/{id}

Generates AI-assisted security analysis for a selected API.

## 💻 Run Locally

### Prerequisites

- Java 17
- Node.js
- MySQL
- Maven
- Git

### Clone the Repository

git clone https://github.com/shivamsinha02/ShadowAPI.git

cd ShadowAPI

### Configure MySQL

Create the database:

CREATE DATABASE shadowguard;

Configure your local database using environment variables:

DB_HOST=localhost  
DB_PORT=3306  
DB_NAME=shadowguard  
DB_USERNAME=root  
DB_PASSWORD=your_password

For Gemini AI functionality:

GEMINI_API_KEY=your_gemini_api_key

Never commit API keys, passwords, or production credentials to GitHub.

### Run Backend

cd shadowguard

mvn spring-boot:run

Backend runs on:

http://localhost:8080

### Run Frontend

Open another terminal:

cd shadowguard-frontend

npm install

npm run dev

Frontend runs on:

http://localhost:5173

## 🚀 Deployment

ShadowGuard is deployed using Render.

### Frontend

https://shadowguard-1.onrender.com

### Backend

https://shadowguard-sbsv.onrender.com

The React frontend communicates with the deployed Spring Boot backend through REST APIs.

## 🔐 Security

Sensitive configuration is managed through environment variables.

The following should never be committed to GitHub:

- Database passwords
- Gemini API keys
- .env files
- Production credentials

## 🔮 Future Improvements

- Advanced authentication and authorization analysis
- Deeper OpenAPI/Swagger specification analysis
- Behavior-based Shadow API detection
- Advanced security analytics
- Real-time security alerts
- User authentication and role-based access
- API security trends and historical analysis
- Improved cloud scalability
- More advanced AI-assisted vulnerability analysis

## 🎯 Project Objective

The goal of ShadowGuard is to provide a practical platform for discovering and monitoring APIs that may be undocumented, overlooked, or potentially risky.

By combining API discovery, automated risk analysis, persistent storage, a web dashboard, and AI-assisted security analysis, ShadowGuard provides a centralized approach to API security monitoring.

## 👨‍💻 Author

Shivam Sinha

B.Tech – Information Technology

GitHub: https://github.com/shivamsinha02

LinkedIn: https://www.linkedin.com/in/shivam-sinha23/

## ⭐ Support

If you find this project useful, consider giving the repository a ⭐ on GitHub.
