<div align="center">

# Vert.x Backend Template

[![platform-java11](https://img.shields.io/badge/Java%2011-blue?style=for-the-badge&logo=java)](https://openjdk.java.net/projects/jdk/11/)
[![vertx-logo](https://img.shields.io/badge/Vert.x-purple?style=for-the-badge&logo=eclipsevertdotx)](https://vertx.io)

> This application was generated using http://start.vertx.io

</div>

## Build and Run

### Run the Tests

```bash
./mvnw clean test
```

### Package the Project

```bash
./mvnw clean package
```

### Run the Project

Run the source directly:

```bash
./mvnw clean compile exec:java
```

Run executable JAR file:

```bash
java -jar xxx-<version>-fat.jar
```

## Components and How to Extend

### Configuration

### Logger

### Web (HTTPS)

### JWT Authentication

### Database Connection

Currently supporting:

- [![database-mysql](https://img.shields.io/badge/MySQL-green?style=for-the-badge&logo=mysql)](https://www.mysql.com/)

More to be extended:

- [![database-postgresql](https://img.shields.io/badge/PostgreSQL-green?style=for-the-badge&logo=postgresql)](https://www.postgresql.org/)
- [![database-mongodb](https://img.shields.io/badge/MongoDB-green?style=for-the-badge&logo=mongodb)](https://www.mongodb.com/)
- [![database-redis](https://img.shields.io/badge/Redis-green?style=for-the-badge&logo=redis)](https://redis.io/)
- ...

## License

Copyright © 2021 Jingtang Zhang ([Apache License 2.0](LICENSE))

