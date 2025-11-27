# FX Deals Clustered Data Warehouse

A Spring Boot application for importing and managing Foreign Exchange (FX) deals for Bloomberg's data warehouse analysis.

## Overview

This system accepts FX deal details and persists them into a PostgreSQL database. It supports both single and batch imports with comprehensive validation, duplicate detection, and partial success handling (no rollback on batch imports).

## Features

-  Import single FX deals
-  Batch import multiple deals with partial success support
-  Duplicate deal detection (no duplicate imports)
-  ISO currency code validation
-  Comprehensive error handling and logging
-  No rollback policy - successfully imported deals are always saved
-  RESTful API with proper HTTP status codes
-  Dockerized deployment with PostgreSQL
-  80%+ unit test coverage

## Technologies Used

- **Java 17**
- **Spring Boot 3.2**
- **PostgreSQL 15**
- **Docker & Docker Compose**
- **Maven**
- **JUnit 5 & Mockito**

## Prerequisites

- Docker Desktop installed and running
- Java 17 or higher (for local development)
- Maven 3.8+ (for local development)

## Quick Start

### Using Docker Compose (Recommended)
```bash
# Clone the repository
git clone https://github.com/dohafettach/fx-deals-warehouse.git
cd fx-deals-warehouse

# Build and run everything
make run

# Or manually
docker-compose up --build
```

The application will be available at `http://localhost:8080`

### Using Makefile
```bash
# Build the project
make build

# Run tests
make test

# Run application with Docker
make run

# Stop application
make stop

# Clean everything
make clean
```

## API Documentation

### Base URL
```
http://localhost:8080/api/deals
```

### Endpoints

#### 1. Import Single Deal
**POST** `/api/deals`

**Request Body:**
```json
{
  "dealId": "DEAL001",
  "fromCurrency": "USD",
  "toCurrency": "EUR",
  "dealTimestamp": "2025-11-26T10:30:00",
  "dealAmount": 1000.50
}
```

**Success Response (201 Created):**
```json
{
  "dealId": "DEAL001",
  "fromCurrency": "USD",
  "toCurrency": "EUR",
  "dealTimestamp": "2025-11-26T10:30:00",
  "dealAmount": 1000.50,
  "createdAt": "2025-11-26T19:27:12.125510",
  "message": "Deal imported successfully"
}
```

**Error Response (409 Conflict):**
```json
{
  "timestamp": "2025-11-26T19:30:00",
  "status": 409,
  "error": "Duplicate Deal",
  "message": "Deal DEAL001 already exists"
}
```

#### 2. Import Batch Deals
**POST** `/api/deals/batch`

**Request Body:**
```json
{
  "deals": [
    {
      "dealId": "DEAL001",
      "fromCurrency": "USD",
      "toCurrency": "MAD",
      "dealTimestamp": "2025-11-26T10:30:00",
      "dealAmount": 1000.50
    },
    {
      "dealId": "DEAL002",
      "fromCurrency": "MAD",
      "toCurrency": "JPY",
      "dealTimestamp": "2025-11-26T11:00:00",
      "dealAmount": 5000.00
    }
  ]
}
```

**Success Response (201 Created):**
```json
{
  "totalRequested": 2,
  "successCount": 2,
  "failureCount": 0,
  "successfulDeals": [],
  "failedDeals": []
}
```

**Partial Success Response (207 Multi-Status):**
```json
{
  "totalRequested": 3,
  "successCount": 2,
  "failureCount": 1,
  "successfulDeals": [],
  "failedDeals": [
    {
      "dealId": "DEAL002",
      "errorMessage": "Deal DEAL002 already exists",
      "rowNumber": 2
    }
  ]
}
```

#### 3. Get All Deals
**GET** `/api/deals`

**Response (200 OK):**
```json
[
  {
    "dealId": "DEAL001",
    "fromCurrency": "USD",
    "toCurrency": "EUR",
    "dealTimestamp": "2025-11-26T10:30:00",
    "dealAmount": 1000.50,
    "createdAt": "2025-11-26T19:27:12.125510"
  }
]
```

## Testing with Sample Data

A sample data file is provided in `sample-deals.json`. Test the batch import:

### Using PowerShell:
```powershell
# Load the JSON file
$body = Get-Content -Raw -Path "sample-deals.json"

# Send the batch import request
Invoke-RestMethod `
  -Uri "http://localhost:8080/api/deals/batch" `
  -Method POST `
  -Body $body `
  -ContentType "application/json"

### Using curl (bash):
```bash
curl -X POST "http://localhost:8080/api/deals/batch" \
  -H "Content-Type: application/json" \
  --data "@sample-deals.json"

```

## Validation Rules

### Request Validation
- **dealId**: Required, cannot be blank
- **fromCurrency**: Required, must be valid 3-letter ISO code (e.g., USD, EUR, MAD)
- **toCurrency**: Required, must be valid 3-letter ISO code
- **dealTimestamp**: Required, must be valid date-time
- **dealAmount**: Required, must be positive (minimum 0.01)

### Business Validation
- From and To currencies must be different
- Deal ID must be unique (no duplicates allowed)
- Currency codes must exist in ISO 4217 standard

## Running Tests
```bash
# Run all tests
mvn test

# Run with coverage report
mvn test jacoco:report

# View coverage report
open target/site/jacoco/index.html
```

**Test Coverage: 80%+**

## Project Structure
```
fx-deals-warehouse/
├── src/
│   ├── main/
│   │   ├── java/bloomberg/fxdealswarehouse/
│   │   │   ├── controller/       # REST endpoints
            ├── dto/              # Request/response objects
            ├── entity/           # JPA entities
            ├── exception/        # Custom exceptions
│   │   │   ├── repository/       # Database access
│   │   │   └── service/          # Business logic
│   │   └── resources/
│   │       └── application.properties
│   └── test/
│       └── java/bloomberg/fxdealswarehouse/
│           ├── controller/
│           ├── service/
│           └── repository/
├── docker-compose.yml
├── Dockerfile
├── Makefile
├── pom.xml
├── readme.md
└── sample-deals.json
```

## Database Schema

**Table: fx_deals**

| Column         | Type         | Constraints       |
|----------------|--------------|-------------------|
| deal_id        | VARCHAR(255) | PRIMARY KEY       |
| from_currency  | VARCHAR(3)   | NOT NULL          |
| to_currency    | VARCHAR(3)   | NOT NULL          |
| deal_timestamp | TIMESTAMP    | NOT NULL          |
| deal_amount    | DECIMAL      | NOT NULL          |
| created_at     | TIMESTAMP    | AUTO-GENERATED    |

## Error Handling

The application provides comprehensive error handling:

- **400 Bad Request**: Validation errors, invalid data format
- **409 Conflict**: Duplicate deal ID
- **500 Internal Server Error**: Unexpected errors

All errors return a consistent JSON structure:
```json
{
  "timestamp": "2025-11-26T19:30:00",
  "status": 400,
  "error": "Error Type",
  "message": "Detailed error message"
}
```

## Logging

The application uses SLF4J for logging:
- **INFO**: Deal imports, batch operations
- **WARN**: Validation failures, duplicate deals
- **ERROR**: Unexpected errors
- **DEBUG**: SQL queries, detailed application flow

## No Rollback Policy

As per requirements, the system implements a "no rollback" policy for batch imports:
- Each deal in a batch is processed in its own transaction (`REQUIRES_NEW`)
- Successfully imported deals are always saved
- Failed deals don't affect successfully imported ones
- Batch response includes details of both successes and failures

## Docker Configuration

### Environment Variables

The application uses the following environment variables in Docker:
```yaml
SPRING_DATASOURCE_URL: jdbc:postgresql://postgres:5432/fx_deals_warehouse
SPRING_DATASOURCE_USERNAME: bloomberg
SPRING_DATASOURCE_PASSWORD: bloomberg123
```

### Useful Docker Commands
```bash
# View application logs
docker-compose logs -f app

# View database logs
docker-compose logs -f postgres

# Stop containers
docker-compose down

# Remove volumes (clean slate)
docker-compose down -v
```

## Development

### Local Development (without Docker)

1. Start PostgreSQL:
```bash
docker-compose up postgres -d
```

2. Run the application:
```bash
mvn spring-boot:run
```

### Building
```bash
# Clean and build
mvn clean package

# Skip tests
mvn clean package -DskipTests
```

## Troubleshooting

### Application won't start
- Ensure Docker is running
- Check if port 8080 is available: `netstat -an | findstr 8080`
- Check logs: `docker-compose logs app`

### Database connection errors
- Verify PostgreSQL is running: `docker-compose ps`
- Check database credentials in docker-compose.yml
- Wait for health check to pass before app starts

### Tests failing
- Ensure you're using Java 17+
- Run `mvn clean test` to rebuild
- Check test logs for specific failures

## Security Notes

**For Production:**
- Change default database credentials
- Use environment variables for sensitive data
- Enable HTTPS/TLS
- Implement authentication & authorization
- Enable CORS selectively

## Future Enhancements

- Add pagination for GET endpoints
- Implement deal update/delete operations
- Add deal search and filtering
- Implement comprehensive security measures

## GitHub Repository

https://github.com/dohafettach/fx-deals-warehouse

---

**Developed by:** Doha Fettach  
**Assignment for:** ProgressSoft Corporation  
**Date:** November 27th 2025