# LFRS Group 4 OOP Project

![Java](https://img.shields.io/badge/java-%23ED8B00.svg?style=for-the-badge&logo=openjdk&logoColor=white)
![JavaFX](https://img.shields.io/badge/JavaFX-%232C3E50.svg?style=for-the-badge&logo=java&logoColor=white)
![Maven](https://img.shields.io/badge/Apache%20Maven-C71A36?style=for-the-badge&logo=Apache%20Maven&logoColor=white)
![JUnit 5](https://img.shields.io/badge/JUnit5-25A162?style=for-the-badge&logo=junit5&logoColor=white)

## Project Overview

The LFRS Group 4 OOP Project is a modular JavaFX application designed to demonstrate advanced Object-Oriented Programming (OOP) concepts. 
Built with the Java Platform Module System (JPMS), this project serves as a centralized solution for **bridging the gap between scattered social media reports and unresolved lost item cases on campus.** It provides a reliable, desktop-based ecosystem for students and staff to record, search, and track the lifecycle of lost property.

## Key Features

### Search Engine
Comprehensive tools to locate lost property:
- Filter items by category (Electronics, Documents, Wallets, etc.).
- Search by keywords, date ranges, and location of loss.
- High-resolution image previews for quick identification.
- Real-time updates on item availability and status.

### Claim & Verification Workflow
A structured process for returning property:
- Digital claim submission with proof-of-ownership requirements.
- Secure communication channel between claimants and administrators.
- Automated status updates (Pending -> Verified -> Claimed).
- Identification logging to ensure secure property transfer.

### Admin Analytics & Management
Powerful oversight tools for campus staff:
- Data-driven insights into lost/found trends and recovery rates.
- Batch record validation and database maintenance tools.
- User management and role-based access control.
- Detailed audit logs for every claim and status change.

## Technical Specifications

| Component | Technology | Description |
| :--- | :--- | :--- |
| **Language** | **Java 24** | Utilizing modern LTS features, file handling, and robust exception management. |
| **Framework** | **JavaFX 21** | UI development using FXML, TableView for record display, and custom Dialogs. |
| **Build Tool** | **Maven** | Dependency management and streamlined build lifecycle. |
| **Database** | **JDBC** | Persistent storage for `items`, `users`, `categories`, and `claims` tables. |
| **UI Kits** | **ControlsFX / BootstrapFX** | Enhanced UI controls and professional CSS styling. |

## User Roles

- **Standard User**: Can register a profile, submit lost/found reports (including image paths and descriptions), and utilize the search engine to filter items by category (Electronics, Wallets, Documents).
- **Administrator**: Responsible for record validation and managing the claim verification process, including updating item statuses from "Lost" to "Claimed" with claimant identification.
- **System Lead**: Oversees the JDBC CRUD operations, database schema integrity, and the modular architecture of the Java API.

## Project Structure

This project follows a standard Maven and JPMS structure:

- **`/src/main/java`**: Contains the source code, including `module-info.java`, JDBC DAO patterns, and JavaFX controllers.
- **`/src/main/resources`**: Stores FXML layouts, CSS styles, and local assets for the LFRS interface.

## Getting Started

### Prerequisites
- JDK 24 or higher
- Git
- MySQL or compatible SQL environment (for JDBC connectivity)

### Installation & Execution
1.  **Clone the repository:**
    ```bash
    git clone <repository-url>
    ```
2.  **Run the application:**
    ```bash
    ./mvnw clean javafx:run
    ```

## Contribution Guidelines

We follow a structured workflow to ensure code quality and modular integrity.

### Branching Strategy
The `main` branch is for stable, production-ready code.
**All feature development (e.g., Search Engine, Claim Verification) should occur on feature branches.**

1.  **Pull the latest changes:**
    ```bash
    git pull origin main
    ```
2.  **Create a feature branch:**
    ```bash
    git checkout -b feature/your-feature-name
    ```

### Standards
All contributions must follow our official **Git and GitHub Guide**, which covers:
- Branch naming conventions.
- Commit message formatting.
- The Pull Request (PR) process.

[![Contribution Guide](https://img.shields.io/badge/View-Contribution_Guide-blue?style=for-the-badge)](./docs/CONTRIBUTION_GUIDE.md)
