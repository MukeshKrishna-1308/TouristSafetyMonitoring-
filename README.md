# Campus Lost and Found API

A Spring Boot REST API for campus accounts, lost reports, found-item intake, possible matches, and admin statistics.

## Requirements

- Java 17 or later
- Maven 3.9+
- PostgreSQL 14 or later

Create a database and user named `lost_found`, or set `DATABASE_URL`, `DATABASE_USERNAME`, and `DATABASE_PASSWORD` to your own PostgreSQL connection. The schema is created/updated by Hibernate on startup.

For a local database with Docker Compose, run `docker compose up -d`; its credentials match the development defaults in `application.properties`.

PowerShell example for local development:

```powershell
$env:DATABASE_URL = "jdbc:postgresql://localhost:5432/lost_found"
$env:DATABASE_USERNAME = "lost_found"
$env:DATABASE_PASSWORD = "your-local-db-password"
$env:CAMPUS_ADMIN_EMAIL = "admin@campus.edu"
$env:CAMPUS_ADMIN_PASSWORD = "set-a-long-unique-password"
$env:ADMIN_REGISTRATION_KEY = "set-a-separate-random-registration-key"
$env:JWT_SECRET = [Convert]::ToBase64String([Security.Cryptography.RandomNumberGenerator]::GetBytes(48))
mvn spring-boot:run
```

The built-in JWT key in `application.properties` is for local development only. Set a strong, randomly generated base64 `JWT_SECRET` outside development. The bootstrap admin is created only when both `CAMPUS_ADMIN_EMAIL` and `CAMPUS_ADMIN_PASSWORD` are set. Admin registration is also supported when `ADMIN_REGISTRATION_KEY` is configured; send it in the `X-Admin-Registration-Key` header. This prevents an unauthenticated caller from granting themselves admin access. Passwords are stored with BCrypt. Tokens expire after 12 hours.

Categories are seeded at startup: Electronics, Documents, Keys, Clothing, Bags, Accessories, and Other.

## API

All paths are under `/api`. Send `Authorization: Bearer <token>` except for register and login. JSON dates use ISO-8601 timestamps.

| Method | Path | Access | Purpose |
| --- | --- | --- | --- |
| POST | `/auth/register` | Public | Create a STUDENT or STAFF account; ADMIN requires `X-Admin-Registration-Key` |
| POST | `/auth/login` | Public | Authenticate and receive a JWT |
| GET | `/categories` | Authenticated | List seeded categories |
| POST | `/lost-reports` | Student | Submit a lost report |
| GET | `/lost-reports` | Student sees own; staff/admin see open reports | List reports |
| GET | `/lost-reports/{id}/matches` | Report owner, staff, or admin | Rank available same-category items by shared description/location keywords |
| POST | `/found-items` | Staff/admin | Log a found item (starts AVAILABLE) |
| GET | `/found-items` | Authenticated | List found items |
| PATCH | `/found-items/{id}/status` | Logging staff member or admin | Change item status |
| GET | `/admin/dashboard` | Admin | Counts of open reports, possible matched pairs, and returned items |

Status payload: `{"status":"CLAIMED"}`. A transition to `RETURNED` is rejected with HTTP 400 unless the current status is `CLAIMED`. A different staff member receives HTTP 403. Invalid request fields include per-field details in the error response.

## Verification

Run unit tests with `mvn test`. Import `postman/Campus-Lost-and-Found.postman_collection.json` into Postman and set `baseUrl`, `adminEmail`, and `adminPassword` in a Postman environment. Run the collection in order; it registers test accounts, creates matching records, checks the 400/403 rules, claims and returns an item, then queries the admin dashboard.