# Legendary-Waffle - Blog Post REST API

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

## Endpoints

## Authentication Endpoints

Base URL: `/api/public/auth`

### Register User
- **Endpoint**: `POST /register`
- **Description**: Register a new user
- **Request Body**: RegisterUserDto (username, password)
- **Response**: Success message
- **Note**: GET method is not supported on this endpoint

### Login
- **Endpoint**: `POST /login`
- **Description**: Authenticate user and receive JWT token
- **Request Body**: LoginUserDto (username, password)
- **Response**: JWT token (in both cookie and response body)
- **Security Features**:
   - JWT token is set in HTTP-only cookie
   - Cookie is marked as secure
   - Authorization header includes Bearer token
- **Note**: GET method is not supported on this endpoint

### Logout
- **Endpoint**: `POST /logout`
- **Description**: Invalidate user session and blacklist JWT token
- **Security**: Requires authentication
- **Features**:
   - Blacklists the current JWT token
   - Removes JWT cookie
   - Returns success message

## Blog Post Management

Base URL: `/api/private/posts`

All endpoints require authentication (`@PreAuthorize("isAuthenticated()")`)

### Create Post
- **Endpoint**: `POST /`
- **Request Body**: PostInDTO (title, content)
- **Response**: Created blog post and success message
- **Features**:
   - Automatically sets creation timestamp
   - Associates post with authenticated user

### Get Post
- **Endpoint**: `GET /{id}`
- **Path Parameter**: post ID
- **Response**: BlogPost object
- **Security**:
   - Verifies user owns the requested post
   - Returns 404 if post not found
   - Returns 403 if unauthorized access

### Update Post
- **Endpoint**: `PUT /{id}`
- **Path Parameter**: post ID
- **Request Body**: Updated BlogPost
- **Response**: Updated post and success message
- **Security**:
   - Verifies user owns the post
   - Returns 404 if post not found
   - Returns 403 if unauthorized access

### Delete Post
- **Endpoint**: `DELETE /{id}`
- **Path Parameter**: post ID
- **Response**: Success message
- **Security**:
   - Verifies user owns the post
   - Returns 404 if post not found
   - Returns 403 if unauthorized access

## Error Handling

The API implements comprehensive error handling:
- Resource not found (404)
- Unauthorized access (401)
- Forbidden access (403)
- Method not allowed (405)
- Validation errors (400)

## Security Features

1. JWT-based authentication
2. Secure cookie handling
   - HTTP-only flag
   - Secure flag
   - Path restriction
3. Token blacklisting for logout
4. Pre-authorization checks
5. User-specific resource access control




