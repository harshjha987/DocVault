# DocVault

A secure, cloud-based Document Management System built with Spring Boot. Upload, organize, and share your files with ease — backed by AWS S3 for storage and JWT for authentication.

**Live:** [docvault.site](https://docvault.site)

---

## Features

- **Authentication** — Register and login with JWT-based auth (24-hour tokens, BCrypt password hashing)
- **File Management** — Upload files to AWS S3, download, delete, and search by name
- **Folder Organization** — Create folders, move files into them, rename and delete folders
- **File Sharing** — Generate shareable public links via secure tokens; revoke anytime
- **Profile Management** — Update display name, change password, delete account
- **Storage Stats** — Track total files, total size, and total folders per user

---

## Tech Stack

| Layer | Technology |
|---|---|
| Backend Framework | Spring Boot (Java 21) |
| Security | Spring Security + JJWT 0.11.5 |
| Database | MySQL + Spring Data JPA (Hibernate) |
| File Storage | AWS S3 (ap-south-1) |
| Build Tool | Maven |
| Utilities | Lombok |

---

## Getting Started

### Prerequisites

- Java 21
- Maven
- MySQL running locally
- AWS account with an S3 bucket

### Setup

1. **Clone the repository**
   ```bash
   git clone https://github.com/harshjha987/docvault.git
   cd docvault
   ```

2. **Configure `application.properties`**

   ```properties
   spring.datasource.url=jdbc:mysql://localhost:3306/fileDb?createDatabaseIfNotExist=true
   spring.datasource.username=your_mysql_username
   spring.datasource.password=your_mysql_password

   jwt.secret=your_jwt_secret_key
   jwt.expiration=86400000

   aws.access-key=YOUR_AWS_ACCESS_KEY
   aws.secret-key=YOUR_AWS_SECRET_KEY
   aws.region=ap-south-1
   aws.s3.bucket=your-s3-bucket-name
   ```

3. **Build and run**
   ```bash
   mvn clean package
   java -jar target/File-Upload-System-*.jar
   ```

   The API will be available at `http://localhost:8080`.

---

## API Reference

All endpoints (except auth and shared file download) require a JWT token:
```
Authorization: Bearer <token>
```

### Auth — `/api/v1/auth`

| Method | Endpoint | Description | Auth Required |
|---|---|---|---|
| POST | `/register` | Create a new account | No |
| POST | `/login` | Login and receive JWT token | No |

### Files — `/api/v1/files`

| Method | Endpoint | Description |
|---|---|---|
| POST | `/upload` | Upload a file (optionally to a folder) |
| GET | `/allFiles` | List all your files |
| GET | `/{id}` | Get file metadata |
| GET | `/{id}/download` | Download a file |
| DELETE | `/{id}` | Delete a file |
| GET | `/search?name={name}` | Search files by name |
| GET | `/folders/{folderId}/files` | List files in a folder |
| GET | `/stats` | Get storage stats (files, size, folders) |
| POST | `/{id}/share` | Generate a shareable link |
| DELETE | `/{id}/share` | Revoke a shareable link |
| GET | `/shared/{token}` | Download a shared file (no auth required) |

### Folders — `/api/v1/folders`

| Method | Endpoint | Description |
|---|---|---|
| POST | `/create` | Create a new folder |
| GET | `/allFolders` | List all your folders |
| PUT | `/{id}` | Rename a folder |
| DELETE | `/{id}` | Delete a folder |

### Profile — `/api/v1/user`

| Method | Endpoint | Description |
|---|---|---|
| GET | `/profile` | Get your profile |
| PUT | `/profile` | Update display name |
| PUT | `/change-password` | Change password |
| DELETE | `/account` | Delete account |

---

## Project Structure

```
src/main/java/com/harsh/project/
├── Controller/          # REST controllers
├── Service/             # Business logic
├── Repository/          # Data access layer
├── Entity/              # JPA entities (User, File, Folder)
├── Dto/                 # Request/response DTOs
├── Security/            # JWT filter, Spring Security config, S3 config
├── Exception/           # Custom exceptions + global handler
└── Mapper/              # Entity ↔ DTO mapping
```

---

## Error Responses

All errors follow a consistent format:

```json
{
  "status": 404,
  "message": "File not found with id: abc123"
}
```

| Status | Meaning |
|---|---|
| 400 | Bad request / validation error |
| 401 | Missing or invalid JWT token |
| 403 | Access denied (not your resource) |
| 404 | Resource not found |
| 409 | Duplicate resource (e.g. email already registered) |
| 413 | File too large |
| 500 | Internal server error |

---

## Deployment

The application is deployed on **AWS EC2** with **Nginx** as a reverse proxy and **Let's Encrypt** for SSL.

- EC2: Ubuntu, `t2.micro`
- Domain: `docvault.site` (DNS A record → EC2 Elastic IP)
- Nginx: Handles HTTPS termination, proxies to `localhost:8080`
- CI/CD: GitHub Actions (`.github/workflows/deploy.yml`) — builds JAR and deploys on push to `main`

---

## Security Notes

- Never commit `application.properties` with real credentials to version control
- Use environment variables or AWS Secrets Manager for production secrets
- The `shared/{token}` endpoint is intentionally public — tokens can be revoked via the API
