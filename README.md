# Sport Event API

## Overview

The Sport Event API provides CRUD (Create, Read, Update, Delete) operations for managing sport events. An event consists of an ID, name, sport type (e.g., football, hockey, etc.), status (inactive, active, finished), and a start time. This application was developed as a test task to showcase CRUD operations with optional filters, handling different event statuses with specific restrictions, and implementing basic security.

## Features

- Create a sport event.
- Retrieve a list of sport events with optional filters by status and sport type.
- Get a sport event by its ID.
- Update the status of a sport event with certain restrictions.

### Event Status Change Restrictions:

- Can be changed from `inactive` to `active`.
- Can be changed from `active` to `finished`.
- An event cannot be activated if its `start_time` is in the past.
- A `finished` event cannot be changed to any other status.
- An `inactive` event cannot be changed to `finished`.

## Tech Stack

- **Language**: Java 17
- **Framework**: Spring Boot
- **ORM**: Hibernate
- **Database**: H2
- **API Documentation**: SpringDoc
- **Object Mapping**: MapStruct
- **Code Simplification**: Lombok
- **Security**: Spring Security

## Setup & Installation

1. Clone the repository to your local machine.
2. Navigate to the project directory.
3. To run the application without security, use the `dev` profile:
   ```bash
   mvn spring-boot:run -Dspring-boot.run.profiles=dev
  
Otherwise, just run:
  ```bash
  mvn spring-boot:run
```
4. Access the Swagger UI for API documentation and testing at http://localhost:8012/swagger-ui.html.

## API Endpoints & Security

- GET /api/events & /api/events/{id}: Fetches events or a single event by ID. Requires ROLE_USER or ROLE_ADMIN.
- POST /api/events: Creates a new event. Requires ROLE_ADMIN.
- PATCH /api/events/{id}/status: Changes the status of a specific event. Requires ROLE_ADMIN.
