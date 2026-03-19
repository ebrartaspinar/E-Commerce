# StarDrop Shop

A full-stack e-commerce platform inspired by Stardew Valley, built as a university project.

## Tech Stack

**Backend:** Java 17, Spring Boot 3.2, PostgreSQL, Redis, Apache Kafka
**Frontend:** Vue 3, TypeScript, Vite, Tailwind CSS, Pinia
**Infrastructure:** Docker Compose, Nginx

## Features

- User authentication with JWT
- Product catalog with search, filtering, pagination
- Shopping cart management
- Order processing with Kafka event streaming
- Redis caching for product queries
- Wishlist / favorites
- Pixel art UI theme with Stardew Valley items

## Getting Started

### Prerequisites
- Java 17
- Node.js 18+
- Docker Desktop

### Run Infrastructure
```bash
docker-compose up -d postgres redis zookeeper kafka
```

### Run Backend
```bash
cd backend
./gradlew bootRun
```

### Run Frontend
```bash
cd frontend
npm install
npm run dev
```

### Access
- Frontend: http://localhost:3000
- Backend API: http://localhost:8080
- H2 Console: http://localhost:8080/h2-console

### Test Accounts
| Email | Password | Role |
|-------|----------|------|
| buyer@stardrop.com | buyer123 | Buyer |
| seller@stardrop.com | seller123 | Seller |
| admin@stardrop.com | admin123 | Admin |

## Project Structure
```
├── backend/          # Spring Boot monolith
│   └── src/main/java/com/stardrop/
│       ├── config/       # Security, JWT, Cache, CORS
│       ├── controller/   # REST endpoints
│       ├── dto/          # Request/Response objects
│       ├── event/        # Kafka producer/consumer
│       ├── model/        # JPA entities
│       ├── repository/   # Data access layer
│       └── service/      # Business logic
├── frontend/         # Vue 3 SPA
│   └── src/
│       ├── components/   # Reusable UI components
│       ├── views/        # Page components
│       ├── stores/       # Pinia state management
│       ├── services/     # API client
│       └── types/        # TypeScript interfaces
└── docker-compose.yml
```
