# Legendary-Waffle

This is a Spring Boot application built for UEHS Security class.

## Features
- Spring Web
- Spring Security
- Spring Data JPA
- Flyway for database migrations
- SQLite as the database

## Prerequisites
- **Java 23** installed
- **Gradle** installed (or use the Gradle wrapper included in the project)

## Getting Started

1. **Clone the repository:**
   ```bash
   git clone <repository-url>
   cd <repository-name>
   ```

2. Set up environment variables: Create a .env file in the root directory with the following:
```bash
DB_URL=jdbc:sqlite:database.db
```

3. Create Your Database file at root of project.

4. Run the application: Use the Gradle wrapper to build and run the application:

```bash
./gradlew bootRun
```

