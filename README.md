# Pesewa :: Event-Driven Inventory Management System

This project is a take-home assignment that demonstrates an event-driven inventory management system for a large retail store. The system is built using a microservices architecture with Java, Spring Boot, Apache Kafka, PostgreSQL, and MongoDB.

## Project Overview

The system is composed of three core microservices that communicate via event-driven architecture:

Product Service: Manages product information (CRUD operations) and publishes product events to Kafka.

Inventory Service: Subscribes to product events, updates current inventory in PostgreSQL, and publishes inventory events to Kafka.

Reporting Service: Subscribes to inventory events, stores historical data in MongoDB, and provides reporting APIs.

┌─────────────────┐     ┌─────────────┐     ┌──────────────────────┐     ┌─────────────┐     ┌──────────────────────┐
│  Product Service│────▶│    Kafka    │────▶│  Inventory Service   │────▶│    Kafka    │────▶│  Reporting Service   │
│  (Port: 8081)   │     │  product-   │     │  (Port: 8082)        │     │  inventory- │     │  (Port: 8083)        │
│                 │     │   events    │     │                      │     │   events    │     │                      │
└─────────────────┘     └─────────────┘     └──────────────────────┘     └─────────────┘     └──────────────────────┘
        │                       │                       │                       │                       │
        │                       │                       │                       │                       │
        ▼                       ▼                       ▼                       ▼                       ▼
┌─────────────┐         ┌─────────────┐         ┌─────────────┐         ┌─────────────┐         ┌─────────────┐
│ PostgreSQL  │         │    Kafka    │         │ PostgreSQL  │         │    Kafka    │         │  MongoDB    │
│  product_db │         │   Broker    │         │ inventory_db│         │   Broker    │         │  events_db  │
└─────────────┘         └─────────────┘         └─────────────┘         └─────────────┘         └─────────────┘

## Technical Stack

*   **Languages**: Java
*   **Frameworks**: Spring Boot 
*   **Databases**:
    *   PostgreSQL (for relational data)
    *   MongoDB 5.0 (for event storage - historical data)
*   **Event Handling**: Apache Kafka (with Spring Kafka)
*   **Containerization**: Docker & Docker Compose
*   **API Documentation**: Swagger (Springdoc)
*   **Build Tool**: Maven**
*   **Testing**: JUnit 5, Mockito, Spring Test

## Project Structure

The project is a multi-module Maven project with the following structure:

```
inventory-management-system/
├── product-service/
├── inventory-service/
├── reporting-service/
├── docker-compose.yml
├── init-db.sql
└── pom.xml
```

## Getting Started

### Prerequisites

*   Java 21 or later
*   Maven 3.6 or later
*   Docker and Docker Compose

### Setup and Run

1.  **Clone the repository:**

    ```bash
    git clone https://github.com/user-yormen/microservice-challenge.git
    cd inventory-management-system
    ```

2.  **Start the infrastructure:**

    This command will start PostgreSQL, MongoDB, Zookeeper, and Kafka in Docker containers.

    ```bash
    docker-compose up -d
    ```

3.  **Build the applications:**

    Navigate to the root of the project and run the following Maven command to build all the services:

    ```bash
    mvn clean install
    ```

4.  **Run the services:**
    **Build and run services individually**:
    
    # In separate terminals
    bash
    ```
        cd product-service && mvn spring-boot:run
        cd inventory-service && mvn spring-boot:run
        cd reporting-service && mvn spring-boot:run
    ```

    # OR You can run each service in a separate terminal window.

    *   **Product Service:**

        ```bash
        cd product-service
        mvn spring-boot:run
        ```

    *   **Inventory Service:**

        ```bash
        cd inventory-service
        mvn spring-boot:run
        ```

    *   **Reporting Service:**

        ```bash
        cd reporting-service
        mvn spring-boot:run
        ```

## API Documentation

Each service includes its own Swagger UI for API documentation.
http://localhost:8082/swagger-ui/index.html

Service Endpoints
Service	Port	Swagger UI	Health Check
*   **Product Service**	8081	http://localhost:8081/swagger-ui.html	http://localhost:8081/actuator/health
*   **Inventory Service**	8082	http://localhost:8082/swagger-ui.html	http://localhost:8082/actuator/health
*   **Reporting Service**	8083	http://localhost:8083/swagger-ui.html	http://localhost:8083/actuator/health

*   **Product Service**: [http://localhost:8081/swagger-ui.html](http://localhost:8081/swagger-ui.html)
*   **Reporting Service**: [http://localhost:8083/swagger-ui.html](http://localhost:8083/swagger-ui.html)

## VariousAPI Documentation
#### Product Service (/api/products)
bash```
POST /api/products - Create a new product
GET /api/products - Get all products
GET /api/products/{id} - Get product by ID
PUT /api/products/{id} - Update product
DELETE /api/products/{id} - Delete product```

#### Inventory Service (/api/inventory)
bash```
GET /api/inventory - Get all inventory items
GET /api/inventory/{productId} - Get inventory by product ID
POST /api/inventory/{productId}/add?quantity={qty} - Add stock
POST /api/inventory/{productId}/deduct?quantity={qty} - Deduct stock```

#### Reporting Service (/api/reports)
bash```
GET /api/reports/inventory - Get current inventory for all products
GET /api/reports/inventory/{productId} - Get current inventory for specific product
GET /api/reports/history/{productId} - Get inventory history for specific product```

## How to Use

1.  **Create a new product:**

    Use the Product Service's Swagger UI to send a `POST` request to `/api/products` with the following body:

    ```json
    {
      "name": "Laptop",
      "description": "A powerful laptop",
      "price": 1200.00,
      "quantity": 50
    }
    ```

    This will create a new product and publish an event to the `inventory-events` Kafka topic.

2.  **Verify the inventory update:**

    The Inventory Service will consume the event and update its own database. You can verify this by checking the logs of the `inventory-service`.

3.  **Check the reports:**

    Use the Reporting Service's Swagger UI to:

    *   Get the current inventory for all products: `GET /api/reports/inventory`
    *   Get the inventory for the new product: `GET /api/reports/inventory/1`
    *   Get the inventory history for the new product: `GET /api/reports/history/1`

### Testing the System
After running the service and knowing all are up, you can try parsing these 
*   **Test Event Flow**
bash```
# 1. Create a product (triggers full event chain)
curl -X POST http://localhost:8081/api/products \
  -H "Content-Type: application/json" \
  -d '{"name":"Laptop","price":999.99,"quantity":10}'

# 2. Check current inventory
curl http://localhost:8082/api/inventory

# 3. Add more stock
curl -X POST "http://localhost:8082/api/inventory/1/add?quantity=5"

# 4. Check inventory history
curl http://localhost:8083/api/reports/history/1```

### Verify Infrastructure
bash```
# Check Kafka topics
docker exec -it kafka kafka-topics --list --bootstrap-server localhost:9092

# Monitor Kafka messages
docker exec -it kafka kafka-console-consumer \
  --bootstrap-server localhost:9092 \
  --topic inventory-events \
  --from-beginning

# Check MongoDB data
docker exec -it mongo mongo inventory_events_db --eval "db.inventory_events.find().pretty()"```

## Common Issues 
*   **Database connection errors**:
    * Check if databases are running
    docker-compose ps

    * Recreate databases
    docker-compose down -v
    docker-compose up --build
*   **Kafka connection issues**:
    * Check Kafka health
    docker-compose logs kafka

    * List topics to verify Kafka is working
    docker exec -it kafka kafka-topics --list --bootstrap-server localhost:9092

*   **Event not flowing**:
    * Check service logs for errors
    docker-compose logs product-service | grep -i error
    docker-compose logs inventory-service | grep -i error
    docker-compose logs reporting-service | grep -i error

    * Send test message manually
    docker exec -it kafka kafka-console-producer \
    --broker-list localhost:9092 \
    --topic product-events