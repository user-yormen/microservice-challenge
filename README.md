# Pesewa :: Event-Driven Inventory Management System

This project is a take-home assignment that demonstrates an event-driven inventory management system for a large retail store. The system is built using a microservices architecture with Java, Spring Boot, Apache Kafka, PostgreSQL, and MongoDB.

## Project Overview

The system is composed of three core microservices:

*   **Product Service**: Manages product information (CRUD operations) and publishes inventory change events to a Kafka topic.
*   **Inventory Service**: Subscribes to inventory events from Kafka, updates the main inventory records in PostgreSQL, and logs the events in MongoDB for historical tracking.
*   **Reporting Service**: Provides an API to query current inventory levels from PostgreSQL and historical inventory data from MongoDB.

## Technical Stack

*   **Languages**: Java 21
*   **Frameworks**: Spring Boot 3.2.0
*   **Databases**:
    *   PostgreSQL (for relational data)
    *   MongoDB (for event storage)
*   **Event Handling**: Apache Kafka
*   **Containerization**: Docker
*   **API Documentation**: Swagger (Springdoc)

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

    You can run each service in a separate terminal window.

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

http://localhost:8081/swagger-ui/index.html
*   **Product Service**: [http://localhost:8081/swagger-ui.html](http://localhost:8081/swagger-ui.html)
*   **Inventory Service**: This service is internal and does not expose any public APIs.
*   **Reporting Service**: [http://localhost:8083/swagger-ui.html](http://localhost:8083/swagger-ui.html)

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

## Next Steps

*   **Testing**: Add comprehensive unit and integration tests for all services.
*   **Containerization**: Add Dockerfiles for each service to run them in containers.
*   **CI/CD**: Implement a CI/CD pipeline using GitHub Actions or Jenkins.
*   **Kubernetes**: Add Kubernetes manifests for deploying the application to a cluster.
*   **Error Handling**: Implement more robust error handling and resilience patterns (e.g., dead-letter queues).
*   **Security**: Secure the APIs using Spring Security.
